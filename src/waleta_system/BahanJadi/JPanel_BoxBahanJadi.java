package waleta_system.BahanJadi;

import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import waleta_system.Packing.JDialog_new_SPK_SE_Lokal;

public class JPanel_BoxBahanJadi extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_BoxBahanJadi() {
        initComponents();
        table_data_LPsuwir.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && table_data_LPsuwir.getSelectedRow() != -1) {
                    int i = table_data_LPsuwir.getSelectedRow();
                    label_asalBox_lpSuwir.setText(table_data_LPsuwir.getValueAt(i, 0).toString());
                    label_LaporanProduksi_lpSuwir.setText(table_data_LPsuwir.getValueAt(i, 0).toString());
                    label_BoxReproses_lpSuwir1.setText(table_data_LPsuwir.getValueAt(i, 0).toString());
                    refreshTable_LaporanProduksi_LPsuwir(table_data_LPsuwir.getValueAt(i, 0).toString());
                    refreshTable_BoxReproses_LPsuwir(table_data_LPsuwir.getValueAt(i, 0).toString());
                    refreshTable_asalBox_LPSuwir(table_data_LPsuwir.getValueAt(i, 0).toString());
                }
            }
        });
        table_data_repacking.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && table_data_repacking.getSelectedRow() != -1) {
                    int i = table_data_repacking.getSelectedRow();
                    label_kode_repacking1.setText(table_data_repacking.getValueAt(table_data_repacking.getSelectedRow(), 0).toString());
                    label_kode_repacking2.setText(table_data_repacking.getValueAt(table_data_repacking.getSelectedRow(), 0).toString());
                    refreshTable_detailRepacking();

                    if (table_data_repacking.getValueAt(i, 2).toString().equals("PROSES")) {
                        button_edit_repacking.setEnabled(true);
                        button_selesai_repacking.setEnabled(true);
                    } else {
                        button_edit_repacking.setEnabled(false);
                        button_selesai_repacking.setEnabled(false);
                    }
                }
            }
        });
    }

    public void init() {
        try {
            String this_year = new SimpleDateFormat("yyyy").format(new Date());
            Calendar calendar = Calendar.getInstance();
            calendar.set(Integer.valueOf(this_year), new Date().getMonth(), 1);
            Date first_date = calendar.getTime();
            Date_Search_LPsuwir_1.setDate(first_date);
            Date_Search_LPsuwir_2.setDate(new Date());

            ComboBox_Search_Grade.removeAllItems();
            ComboBox_Search_Grade.addItem("All");
            String grade = "SELECT `kode_grade` FROM `tb_grade_bahan_jadi` WHERE `status_grade` = 'AKTIF'";
            ResultSet rs_grade = Utility.db.getStatement().executeQuery(grade);
            while (rs_grade.next()) {
                ComboBox_Search_Grade.addItem(rs_grade.getString("kode_grade"));
            }

            ComboBox_Search_BentukGrade.removeAllItems();
            ComboBox_Search_BentukGrade.addItem("All");
            String bentuk_grade = "SELECT DISTINCT(`bentuk_grade`) FROM `tb_grade_bahan_jadi`";
            ResultSet rs_bentuk = Utility.db.getStatement().executeQuery(bentuk_grade);
            while (rs_bentuk.next()) {
                ComboBox_Search_BentukGrade.addItem(rs_bentuk.getString("bentuk_grade"));
            }

            ComboBox_lokasi.removeAllItems();
            ComboBox_lokasi.addItem("All");
            String lokasi = "SELECT DISTINCT(`lokasi_terakhir`) FROM `tb_box_bahan_jadi`";
            ResultSet rs_lokasi = Utility.db.getStatement().executeQuery(lokasi);
            while (rs_lokasi.next()) {
                ComboBox_lokasi.addItem(rs_lokasi.getString("lokasi_terakhir"));
            }
            ComboBox_lokasi.setSelectedItem("GRADING");
            refreshTable_DataBox();
            refreshTable_PinjamBarangJadi();
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_DataBox() {
        try {
            float total_kpg = 0, total_gram = 0;
            decimalFormat.setGroupingUsed(true);
            String lokasi = " AND `lokasi_terakhir` = '" + ComboBox_lokasi.getSelectedItem().toString() + "' ";
            if (ComboBox_lokasi.getSelectedItem() == "All") {
                lokasi = "";
            }
            String grade = " AND `kode_grade` = '" + ComboBox_Search_Grade.getSelectedItem() + "' ";
            if (ComboBox_Search_Grade.getSelectedItem() == "All") {
                grade = "";
            }
            String bentuk_grade = " AND `bentuk_grade` = '" + ComboBox_Search_BentukGrade.getSelectedItem() + "' ";
            if (ComboBox_Search_BentukGrade.getSelectedItem() == "All") {
                bentuk_grade = "";
            }

            String tanggal = "";
            if (Date_box1.getDate() != null && Date_box2.getDate() != null) {
                tanggal = " AND (`tanggal_box` BETWEEN '" + dateFormat.format(Date_box1.getDate()) + "' AND '" + dateFormat.format(Date_box2.getDate()) + "')";
            }

            String tanggal_proses = "";
            if (Date_proses_terakhir1.getDate() != null && Date_proses_terakhir2.getDate() != null) {
                tanggal_proses = " AND (`tgl_proses_terakhir` BETWEEN '" + dateFormat.format(Date_proses_terakhir1.getDate()) + "' AND '" + dateFormat.format(Date_proses_terakhir2.getDate()) + "')";
            }
            String search_spk = " AND `tb_spk_detail`.`kode_spk` LIKE '%" + txt_search_spk.getText() + "%' ";
            if ("".equals(txt_search_spk.getText()) || txt_search_spk.getText() == null) {
                search_spk = "";
            }
            String search_kode_grade_spk = " AND `tb_spk_detail`.`no` = '" + txt_search_kode_grade_spk.getText() + "' ";
            if ("".equals(txt_search_kode_grade_spk.getText()) || txt_search_kode_grade_spk.getText() == null) {
                search_kode_grade_spk = "";
            }
            String search_invoice = " AND `tb_pengiriman`.`invoice_no` LIKE '%" + txt_search_no_invoice.getText() + "%' ";
            if ("".equals(txt_search_no_invoice.getText()) || txt_search_no_invoice.getText() == null) {
                search_invoice = "";
            }
            String search_rsb = " AND `tb_box_bahan_jadi`.`kode_rsb` LIKE '%" + txt_search_rsb.getText() + "%' ";
            if ("".equals(txt_search_rsb.getText()) || txt_search_rsb.getText() == null) {
                search_rsb = "";
            }
            String search_kh = " AND `tb_box_bahan_jadi`.`kode_kh` LIKE '%" + txt_search_kh.getText() + "%' ";
            if ("".equals(txt_search_kh.getText()) || txt_search_kh.getText() == null) {
                search_kh = "";
            }

            sql = "SELECT `tb_box_bahan_jadi`.`no_box`, `tanggal_box`, `tb_grade_bahan_jadi`.`kode_grade`, `tb_box_bahan_jadi`.`keping`, `tb_box_bahan_jadi`.`berat`, `no_tutupan`, `status_terakhir`, `lokasi_terakhir`, `tgl_proses_terakhir`, `tb_spk_detail`.`no`, `tb_spk_detail`.`kode_spk`, `tb_spk_detail`.`grade_buyer`, `tb_box_bahan_jadi`.`kode_rsb`, `tb_rumah_burung`.`nama_rumah_burung`, `tb_box_bahan_jadi`.`kode_kh`, `tb_dokumen_kh`.`no_registrasi_rsb`, `no_box_ct1`, `tb_pengiriman`.`invoice_no`, `memo_box_bj` "
                    + "FROM `tb_box_bahan_jadi` "
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`"
                    + "LEFT JOIN `tb_box_packing` ON `tb_box_bahan_jadi`.`no_box` = `tb_box_packing`.`no_box`"
                    + "LEFT JOIN `tb_spk_detail` ON `tb_box_packing`.`no_grade_spk` = `tb_spk_detail`.`no` "
                    + "LEFT JOIN `tb_pengiriman` ON `tb_spk_detail`.`kode_spk` = `tb_pengiriman`.`kode_spk` "
                    + "LEFT JOIN `tb_rumah_burung` ON `tb_box_bahan_jadi`.`kode_rsb` = `tb_rumah_burung`.`no_registrasi`"
                    + "LEFT JOIN `tb_dokumen_kh` ON `tb_box_bahan_jadi`.`kode_kh` = `tb_dokumen_kh`.`kode_kh`"
                    + "WHERE `tb_box_bahan_jadi`.`no_box` LIKE '%" + txt_search_no_box.getText() + "%' "
                    + "AND `no_tutupan` LIKE '%" + txt_search_no_tutupan.getText() + "%' "
                    + search_rsb
                    + search_spk
                    + search_kode_grade_spk
                    + search_invoice
                    + search_kh
                    + tanggal + tanggal_proses + grade + lokasi + bentuk_grade
                    + " ORDER BY `tgl_proses_terakhir` DESC";
//            System.out.println(sql);
            rs = Utility.db.getStatement().executeQuery(sql);
            DefaultTableModel model = (DefaultTableModel) table_dataBox.getModel();
            model.setRowCount(0);
            Object[] baris = new Object[20];
            while (rs.next()) {
                baris[0] = rs.getString("no_box");
                baris[1] = rs.getDate("tanggal_box");
                baris[2] = rs.getString("kode_grade");
                baris[3] = rs.getFloat("keping");
                baris[4] = rs.getFloat("berat");
                baris[5] = rs.getString("no_tutupan");
                baris[6] = rs.getString("status_terakhir");
                baris[7] = rs.getString("lokasi_terakhir");
                baris[8] = rs.getDate("tgl_proses_terakhir");
                baris[9] = rs.getString("kode_spk");
                baris[10] = rs.getString("grade_buyer");
                baris[11] = rs.getString("kode_rsb");
                baris[12] = rs.getString("kode_kh");
                baris[13] = rs.getString("no_registrasi_rsb");
                baris[14] = rs.getString("no_box_ct1");
                baris[15] = rs.getString("invoice_no");
                baris[16] = rs.getString("memo_box_bj");
                model.addRow(baris);

                total_kpg += rs.getFloat("keping");
                total_gram += rs.getFloat("berat");
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_dataBox);
            int total_data = table_dataBox.getRowCount();
            label_total_data_box.setText(Integer.toString(total_data));
            label_total_kpg_data_box.setText(decimalFormat.format(total_kpg));
            label_total_gram_data_box.setText(decimalFormat.format(total_gram));
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_Repacking() {
        try {
            double total_sh = 0;
            DefaultTableModel model = (DefaultTableModel) table_data_repacking.getModel();
            model.setRowCount(0);
            if (Date_Search_Repacking1.getDate() != null && Date_Search_Repacking2.getDate() != null) {
                sql = "SELECT `kode_repacking`, `tanggal_repacking`, `status_repacking`, `keterangan_repacking`, `pekerja_repacking`, `nama_pegawai`, SUM(IF(`tb_repacking`.`status` = 'ASAL', `gram`, 0)) AS 'gram_asal', SUM(IF(`tb_repacking`.`status` = 'HASIL', `gram`, 0)) AS 'gram_hasil', `kode_rsb` "
                        + "FROM `tb_repacking` "
                        + "LEFT JOIN `tb_karyawan` ON `tb_repacking`.`pekerja_repacking` = `tb_karyawan`.`id_pegawai` "
                        + "WHERE `kode_repacking` LIKE '%" + txt_search_kode_repacking.getText() + "%' AND `tanggal_repacking` BETWEEN '" + dateFormat.format(Date_Search_Repacking1.getDate()) + "' AND '" + dateFormat.format(Date_Search_Repacking2.getDate()) + "' "
                        + "GROUP BY `kode_repacking` ORDER BY `tanggal_repacking` DESC";
            } else {
                sql = "SELECT `kode_repacking`, `tanggal_repacking`, `status_repacking`, `keterangan_repacking`, `pekerja_repacking`, `nama_pegawai`, SUM(IF(`tb_repacking`.`status` = 'ASAL', `gram`, 0)) AS 'gram_asal', SUM(IF(`tb_repacking`.`status` = 'HASIL', `gram`, 0)) AS 'gram_hasil', `kode_rsb` "
                        + "FROM `tb_repacking` "
                        + "LEFT JOIN `tb_karyawan` ON `tb_repacking`.`pekerja_repacking` = `tb_karyawan`.`id_pegawai` "
                        + "WHERE `kode_repacking` LIKE '%" + txt_search_kode_repacking.getText() + "%'  "
                        + "GROUP BY `kode_repacking` ORDER BY `tanggal_repacking` DESC";
            }
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[7];
            while (rs.next()) {
                row[0] = rs.getString("kode_repacking");
                row[1] = rs.getString("tanggal_repacking");
                row[2] = rs.getString("status_repacking");
                row[3] = rs.getString("keterangan_repacking");
                row[4] = rs.getString("nama_pegawai");
                double susut_hilang = Math.round((rs.getFloat("gram_asal") - rs.getFloat("gram_hasil")) / rs.getFloat("gram_asal") * 10000.f) / 100.f;
                total_sh = total_sh + susut_hilang;
                row[5] = susut_hilang;
                row[6] = rs.getString("kode_rsb");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_data_repacking);
            int total_data_repacking = table_data_repacking.getRowCount();
            label_avg_sh_repacking.setText(decimalFormat.format(total_sh / total_data_repacking));
            label_total_repacking.setText(Integer.toString(total_data_repacking));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_detailRepacking() {
        try {
            DefaultTableModel model_asal = (DefaultTableModel) table_asal_repacking.getModel();
            DefaultTableModel model_hasil = (DefaultTableModel) table_hasil_repacking.getModel();
            model_asal.setRowCount(0);
            model_hasil.setRowCount(0);
            int i = table_data_repacking.getSelectedRow();
            int total_kpg_asal = 0, total_kpg_hasil = 0;
            float total_gram_asal = 0, total_gram_hasil = 0;
            if (i > -1) {
                sql = "SELECT `kode_repacking`, `tb_repacking`.`no_box`, `kode_grade`, `tb_repacking`.`keping`, `tb_repacking`.`gram`, `tb_repacking`.`status`, `tb_box_bahan_jadi`.`status_terakhir`, `tb_box_bahan_jadi`.`lokasi_terakhir`, `no_tutupan`, `nama_pegawai` AS 'pekerja_repacking', `tb_box_bahan_jadi`.`tanggal_repacking`"
                        + "FROM `tb_repacking` "
                        + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_repacking`.`no_box` = `tb_box_bahan_jadi`.`no_box`"
                        + "LEFT JOIN `tb_karyawan` ON `tb_box_bahan_jadi`.`pekerja_repacking` = `tb_karyawan`.`id_pegawai` "
                        + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`"
                        + "WHERE `kode_repacking` LIKE '%" + table_data_repacking.getValueAt(i, 0) + "%'";
                rs = Utility.db.getStatement().executeQuery(sql);
                Object[] row = new Object[10];
                while (rs.next()) {
                    row[0] = rs.getString("kode_repacking");
                    row[1] = rs.getString("no_box");
                    row[2] = rs.getString("kode_grade");
                    row[3] = rs.getInt("keping");
                    row[4] = rs.getFloat("gram");
                    row[5] = rs.getString("status_terakhir");
                    row[6] = rs.getString("lokasi_terakhir");
                    row[7] = rs.getString("no_tutupan");
                    row[8] = rs.getString("pekerja_repacking");
                    row[9] = rs.getDate("tanggal_repacking");
                    if (null == rs.getString("status")) {
                    } else {
                        switch (rs.getString("status")) {
                            case "ASAL":
                                total_kpg_asal = total_kpg_asal + rs.getInt("keping");
                                total_gram_asal = total_gram_asal + rs.getFloat("gram");
                                model_asal.addRow(row);
                                break;
                            case "HASIL":
                                total_kpg_hasil = total_kpg_hasil + rs.getInt("keping");
                                total_gram_hasil = total_gram_hasil + rs.getFloat("gram");
                                model_hasil.addRow(row);
                                break;
                            default:
                                break;
                        }
                    }

                }
                label_total_asal_repacking.setText(Integer.toString(table_asal_repacking.getRowCount()));
                label_total_keping_asal_repacking.setText(Integer.toString(total_kpg_asal));
                label_total_gram_asal_repacking.setText(Float.toString(total_gram_asal));
                label_total_hasil_repacking.setText(Integer.toString(table_hasil_repacking.getRowCount()));
                label_total_keping_hasil_repacking.setText(Integer.toString(total_kpg_hasil));
                label_total_gram_hasil_repacking.setText(Float.toString(total_gram_hasil));
                ColumnsAutoSizer.sizeColumnsToFit(table_asal_repacking);
                ColumnsAutoSizer.sizeColumnsToFit(table_hasil_repacking);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_Keluar() {
        try {
            float gram_keluar = 0;
            int keping_keluar = 0;
            DefaultTableModel model = (DefaultTableModel) table_data_keluar.getModel();
            model.setRowCount(0);
            if (Date_Search_keluar1.getDate() != null && Date_Search_keluar2.getDate() != null) {
                sql = "SELECT * FROM `tb_box_keluar` WHERE `no_box` LIKE '%" + txt_search_box_keluar.getText() + "%' AND `tanggal_keluar` BETWEEN '" + dateFormat.format(Date_Search_keluar1.getDate()) + "' AND '" + dateFormat.format(Date_Search_keluar2.getDate()) + "'";
            } else {
                sql = "SELECT * FROM `tb_box_keluar` WHERE `no_box` LIKE '%" + txt_search_box_keluar.getText() + "%'";
            }
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[5];
            while (rs.next()) {
                row[0] = rs.getString("no_box");
                row[1] = rs.getDate("tanggal_keluar");
                row[2] = rs.getInt("keping");
                keping_keluar = keping_keluar + rs.getInt("keping");
                row[3] = rs.getInt("gram");
                gram_keluar = gram_keluar + rs.getFloat("gram");
                row[4] = rs.getString("keterangan");
                model.addRow(row);
            }
            label_total_keluar.setText(Integer.toString(table_data_keluar.getRowCount()));
            label_total_keping_keluar.setText(Integer.toString(keping_keluar));
            label_total_gram_keluar.setText(Float.toString(gram_keluar));
            ColumnsAutoSizer.sizeColumnsToFit(table_data_keluar);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_PinjamBarangJadi() {
        try {
            float total_gram = 0;
            int total_kpg = 0;
            DefaultTableModel model = (DefaultTableModel) table_data_pinjam_barang_jadi.getModel();
            model.setRowCount(0);
            if (Date_barang_dipinjam1.getDate() != null && Date_barang_dipinjam2.getDate() != null) {
                sql = "SELECT `no`, `tb_pinjam_barang_jadi`.`no_box`, `tb_box_bahan_jadi`.`keping`, `tb_box_bahan_jadi`.`berat`, `waktu_pinjam`, `waktu_kembali`, `keterangan`, `departemen`, `pic` \n"
                        + "FROM `tb_pinjam_barang_jadi` LEFT JOIN `tb_box_bahan_jadi` ON `tb_pinjam_barang_jadi`.`no_box` = `tb_box_bahan_jadi`.`no_box`\n"
                        + "WHERE `tb_pinjam_barang_jadi`.`no_box` LIKE '%" + txt_search_box_dipinjam.getText() + "%' AND DATE(`waktu_pinjam`) BETWEEN '" + dateFormat.format(Date_barang_dipinjam1.getDate()) + "' AND '" + dateFormat.format(Date_barang_dipinjam2.getDate()) + "'";
            } else {
                sql = "SELECT `no`, `tb_pinjam_barang_jadi`.`no_box`, `tb_box_bahan_jadi`.`keping`, `tb_box_bahan_jadi`.`berat`, `waktu_pinjam`, `waktu_kembali`, `keterangan`, `departemen`, `pic` \n"
                        + "FROM `tb_pinjam_barang_jadi` LEFT JOIN `tb_box_bahan_jadi` ON `tb_pinjam_barang_jadi`.`no_box` = `tb_box_bahan_jadi`.`no_box`\n"
                        + "WHERE `tb_pinjam_barang_jadi`.`no_box` LIKE '%" + txt_search_box_dipinjam.getText() + "%'";
            }
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getInt("no");
                row[1] = rs.getString("no_box");
                row[2] = rs.getInt("keping");
                row[3] = rs.getInt("berat");
                row[4] = rs.getTimestamp("waktu_pinjam");
                row[5] = rs.getTimestamp("waktu_kembali");
                row[6] = rs.getString("keterangan");
                row[7] = rs.getString("departemen");
                row[8] = rs.getString("pic");
                model.addRow(row);
                total_kpg = total_kpg + rs.getInt("keping");
                total_gram = total_gram + rs.getFloat("berat");
            }
            label_total_box_dipinjam.setText(decimalFormat.format(table_data_pinjam_barang_jadi.getRowCount()));
            label_total_keping_dipinjam.setText(decimalFormat.format(total_kpg));
            label_total_gram_dipinjam.setText(decimalFormat.format(total_gram));
            ColumnsAutoSizer.sizeColumnsToFit(table_data_pinjam_barang_jadi);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_KinerjaGBJ() {
        try {
            float total_gram_lp = 0, total_kpg_lp = 0, total_gram_dikerjakan = 0, total_kpg_dikerjakan = 0;
            DefaultTableModel model = (DefaultTableModel) table_data_kinerja_gbj.getModel();
            model.setRowCount(0);
            String filer_tanggal = "";
            if (Date_kinerja1.getDate() != null && Date_kinerja2.getDate() != null) {
                filer_tanggal = "AND `tanggal_pengerjaan` BETWEEN '" + dateFormat.format(Date_kinerja1.getDate()) + "' AND '" + dateFormat.format(Date_kinerja2.getDate()) + "'";
            }
            sql = "SELECT `no`, `tanggal_pengerjaan`, `pekerja_gbj`, `tb_karyawan`.`nama_pegawai`, `tb_kinerja_gbj`.`no_box`, `tb_grade_bahan_jadi`.`kode_grade`, `tb_box_bahan_jadi`.`keping`, `tb_box_bahan_jadi`.`berat`, `keping_dikerjakan`, `gram_dikerjakan`, `catatan` \n"
                    + "FROM `tb_kinerja_gbj` \n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_kinerja_gbj`.`pekerja_gbj` = `tb_karyawan`.`id_pegawai`\n"
                    + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_kinerja_gbj`.`no_box` = `tb_box_bahan_jadi`.`no_box`\n"
                    + "lEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`\n"
                    + "WHERE `tb_kinerja_gbj`.`no_box` LIKE '%" + txt_search_box_kinerja.getText() + "%' "
                    + "AND `pekerja_gbj` LIKE '%" + txt_search_id_kinerja.getText() + "%' "
                    + "AND `tb_karyawan`.`nama_pegawai` LIKE '%" + txt_search_nama_kinerja.getText() + "%' "
                    + filer_tanggal;
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[11];
            while (rs.next()) {
                row[0] = rs.getInt("no");
                row[1] = rs.getDate("tanggal_pengerjaan");
                row[2] = rs.getString("pekerja_gbj");
                row[3] = rs.getString("nama_pegawai");
                row[4] = rs.getString("no_box");
                row[5] = rs.getString("kode_grade");
                row[6] = rs.getFloat("keping");
                row[7] = rs.getFloat("berat");
                row[8] = rs.getFloat("keping_dikerjakan");
                row[9] = rs.getFloat("gram_dikerjakan");
                row[10] = rs.getString("catatan");
                model.addRow(row);
                total_kpg_lp = total_kpg_lp + rs.getInt("keping");
                total_gram_lp = total_gram_lp + rs.getFloat("berat");
                total_kpg_dikerjakan = total_kpg_dikerjakan + rs.getInt("keping_dikerjakan");
                total_gram_dikerjakan = total_gram_dikerjakan + rs.getFloat("gram_dikerjakan");
            }
            label_total_box_dipinjam.setText(decimalFormat.format(table_data_pinjam_barang_jadi.getRowCount()));
            label_total_keping_lp_kinerjaGBJ.setText(decimalFormat.format(total_kpg_lp));
            label_total_gram_lp_kinerjaGBJ.setText(decimalFormat.format(total_gram_lp));
            label_total_keping_dikerjakan_kinerjaGBJ.setText(decimalFormat.format(total_kpg_dikerjakan));
            label_total_gram_dikerjakan_kinerjaGBJ.setText(decimalFormat.format(total_gram_dikerjakan));
            ColumnsAutoSizer.sizeColumnsToFit(table_data_pinjam_barang_jadi);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel61 = new javax.swing.JLabel();
        label_total_stok1 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel_Data_Box = new javax.swing.JPanel();
        button_search_Box = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        table_dataBox = new javax.swing.JTable();
        jLabel38 = new javax.swing.JLabel();
        txt_search_no_tutupan = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        txt_search_no_box = new javax.swing.JTextField();
        jLabel40 = new javax.swing.JLabel();
        ComboBox_Search_Grade = new javax.swing.JComboBox<>();
        button_export_data_box = new javax.swing.JButton();
        jLabel41 = new javax.swing.JLabel();
        label_total_data_box = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        label_total_kpg_data_box = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        label_total_gram_data_box = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        ComboBox_lokasi = new javax.swing.JComboBox<>();
        button_rePacking = new javax.swing.JButton();
        button_reProcess = new javax.swing.JButton();
        button_setor_Packing = new javax.swing.JButton();
        button_out = new javax.swing.JButton();
        button_lp_suwir = new javax.swing.JButton();
        jLabel53 = new javax.swing.JLabel();
        Date_box1 = new com.toedter.calendar.JDateChooser();
        Date_box2 = new com.toedter.calendar.JDateChooser();
        jLabel57 = new javax.swing.JLabel();
        ComboBox_Search_BentukGrade = new javax.swing.JComboBox<>();
        button_terima_retur = new javax.swing.JButton();
        button_Belum_Repack = new javax.swing.JButton();
        button_treatment = new javax.swing.JButton();
        button_print_label1 = new javax.swing.JButton();
        button_new_spk_se = new javax.swing.JButton();
        button_set_rsb = new javax.swing.JButton();
        jLabel58 = new javax.swing.JLabel();
        txt_search_rsb = new javax.swing.JTextField();
        jLabel59 = new javax.swing.JLabel();
        txt_search_spk = new javax.swing.JTextField();
        button_print_label_packing = new javax.swing.JButton();
        button_set_cheat_no_box = new javax.swing.JButton();
        jLabel63 = new javax.swing.JLabel();
        txt_search_no_invoice = new javax.swing.JTextField();
        jLabel64 = new javax.swing.JLabel();
        txt_search_kh = new javax.swing.JTextField();
        jLabel65 = new javax.swing.JLabel();
        txt_search_kode_grade_spk = new javax.swing.JTextField();
        jLabel74 = new javax.swing.JLabel();
        Date_proses_terakhir1 = new com.toedter.calendar.JDateChooser();
        Date_proses_terakhir2 = new com.toedter.calendar.JDateChooser();
        button_print_label2 = new javax.swing.JButton();
        button_edit_memo_box = new javax.swing.JButton();
        jPanel_data_rePacking = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_data_repacking = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txt_search_kode_repacking = new javax.swing.JTextField();
        Date_Search_Repacking1 = new com.toedter.calendar.JDateChooser();
        Date_Search_Repacking2 = new com.toedter.calendar.JDateChooser();
        button_search_repacking = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        label_total_repacking = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        table_asal_repacking = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        table_hasil_repacking = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        label_total_hasil_repacking = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        label_total_keping_hasil_repacking = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        label_total_gram_hasil_repacking = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        label_total_asal_repacking = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        label_total_keping_asal_repacking = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        label_total_gram_asal_repacking = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        label_kode_repacking1 = new javax.swing.JLabel();
        label_kode_repacking2 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        button_edit_repacking = new javax.swing.JButton();
        button_print_repacking = new javax.swing.JButton();
        button_selesai_repacking = new javax.swing.JButton();
        button_export_asalRepacking = new javax.swing.JButton();
        button_export_hasilRepacking = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        label_avg_sh_repacking = new javax.swing.JLabel();
        jPanel_data_keluar = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        table_data_keluar = new javax.swing.JTable();
        jLabel23 = new javax.swing.JLabel();
        txt_search_box_keluar = new javax.swing.JTextField();
        Date_Search_keluar1 = new com.toedter.calendar.JDateChooser();
        Date_Search_keluar2 = new com.toedter.calendar.JDateChooser();
        button_search_keluar = new javax.swing.JButton();
        jLabel24 = new javax.swing.JLabel();
        label_total_keluar = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        label_total_keping_keluar = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        label_total_gram_keluar = new javax.swing.JLabel();
        jPanel_data_LP_suwir = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        txt_search_lpsuwir = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        Date_Search_LPsuwir_1 = new com.toedter.calendar.JDateChooser();
        jLabel36 = new javax.swing.JLabel();
        Date_Search_LPsuwir_2 = new com.toedter.calendar.JDateChooser();
        button_search_lp = new javax.swing.JButton();
        jScrollPane10 = new javax.swing.JScrollPane();
        table_data_LPsuwir = new javax.swing.JTable();
        jLabel32 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jScrollPane11 = new javax.swing.JScrollPane();
        table_asalBox_lpSuwir = new javax.swing.JTable();
        jLabel44 = new javax.swing.JLabel();
        label_total_asalBox_lpSuwir = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        label_total_keping_asalBox_lpsuwir = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        label_total_gram_asalBox_lpsuwir = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        label_total_lpsuwir = new javax.swing.JLabel();
        label_asalBox_lpSuwir = new javax.swing.JLabel();
        button_export_LPSuwir = new javax.swing.JButton();
        button_export_AsalBox = new javax.swing.JButton();
        button_editgram = new javax.swing.JButton();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        label_LaporanProduksi_lpSuwir = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        label_total_LaporanProduksi_lpsuwir = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        button_export_LP_F2 = new javax.swing.JButton();
        jScrollPane12 = new javax.swing.JScrollPane();
        table_LaporanProduksi_LPSuwir = new javax.swing.JTable();
        label_total_gram_LaporanProduksi_lpsuwir = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel54 = new javax.swing.JLabel();
        jScrollPane13 = new javax.swing.JScrollPane();
        table_BoxReprosesi_LPSuwir = new javax.swing.JTable();
        label_total_gram_BoxRepracking_lpsuwir = new javax.swing.JLabel();
        label_BoxReproses_lpSuwir1 = new javax.swing.JLabel();
        button_export_BoxReproses = new javax.swing.JButton();
        jLabel55 = new javax.swing.JLabel();
        label_total_BoxRepacking_lpsuwir = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        label_total_stok = new javax.swing.JLabel();
        button_edit_lp_suwir = new javax.swing.JButton();
        button_edit_lp_suwir1 = new javax.swing.JButton();
        label_total_keluar_f2 = new javax.swing.JLabel();
        jLabel75 = new javax.swing.JLabel();
        button_Print_LP_SWR = new javax.swing.JButton();
        jPanel_data_pinjam_barangJadi = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        table_data_pinjam_barang_jadi = new javax.swing.JTable();
        jLabel27 = new javax.swing.JLabel();
        txt_search_box_dipinjam = new javax.swing.JTextField();
        Date_barang_dipinjam1 = new com.toedter.calendar.JDateChooser();
        Date_barang_dipinjam2 = new com.toedter.calendar.JDateChooser();
        button_search_pinjam_barang_jadi = new javax.swing.JButton();
        jLabel28 = new javax.swing.JLabel();
        label_total_box_dipinjam = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        label_total_keping_dipinjam = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        label_total_gram_dipinjam = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        jPanel_data_kinerja_gbj = new javax.swing.JPanel();
        jScrollPane14 = new javax.swing.JScrollPane();
        table_data_kinerja_gbj = new javax.swing.JTable();
        jLabel34 = new javax.swing.JLabel();
        txt_search_box_kinerja = new javax.swing.JTextField();
        Date_kinerja1 = new com.toedter.calendar.JDateChooser();
        Date_kinerja2 = new com.toedter.calendar.JDateChooser();
        button_search_kinerja = new javax.swing.JButton();
        jLabel66 = new javax.swing.JLabel();
        label_total_data_kinerja = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        label_total_keping_lp_kinerjaGBJ = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        label_total_gram_lp_kinerjaGBJ = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        jLabel70 = new javax.swing.JLabel();
        txt_search_id_kinerja = new javax.swing.JTextField();
        jLabel71 = new javax.swing.JLabel();
        txt_search_nama_kinerja = new javax.swing.JTextField();
        button_delete_kinerja = new javax.swing.JButton();
        button_insert_kinerja = new javax.swing.JButton();
        button_edit_kinerja = new javax.swing.JButton();
        label_total_keping_dikerjakan_kinerjaGBJ = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        label_total_gram_dikerjakan_kinerjaGBJ = new javax.swing.JLabel();
        jLabel73 = new javax.swing.JLabel();

        jLabel61.setBackground(new java.awt.Color(255, 255, 255));
        jLabel61.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel61.setText("Total Stok :");

        label_total_stok1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_stok1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_stok1.setText("0");

        setBackground(new java.awt.Color(255, 255, 255));

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel_Data_Box.setBackground(new java.awt.Color(255, 255, 255));

        button_search_Box.setBackground(new java.awt.Color(255, 255, 255));
        button_search_Box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_Box.setText("Search");
        button_search_Box.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_BoxActionPerformed(evt);
            }
        });

        table_dataBox.setAutoCreateRowSorter(true);
        table_dataBox.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_dataBox.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No. Box", "Tgl Box", "Grade", "Keping", "Berat", "No Tutupan", "Status terakhir", "Lokasi", "Tgl Proses terakhir", "Kode SPK", "Grade Buyer", "RSB CT1", "Kode KH", "RSB KH", "No Box Cheat", "invoice", "Memo"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane6.setViewportView(table_dataBox);

        jLabel38.setBackground(new java.awt.Color(255, 255, 255));
        jLabel38.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel38.setText("No. Tutupan :");

        txt_search_no_tutupan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_no_tutupan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_no_tutupanKeyPressed(evt);
            }
        });

        jLabel39.setBackground(new java.awt.Color(255, 255, 255));
        jLabel39.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel39.setText("No. Box :");

        txt_search_no_box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_no_box.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_no_boxKeyPressed(evt);
            }
        });

        jLabel40.setBackground(new java.awt.Color(255, 255, 255));
        jLabel40.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel40.setText("Grade Barang Jadi :");

        ComboBox_Search_Grade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_Search_Grade.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        button_export_data_box.setBackground(new java.awt.Color(255, 255, 255));
        button_export_data_box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_data_box.setText("Export to Excel");
        button_export_data_box.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_data_boxActionPerformed(evt);
            }
        });

        jLabel41.setBackground(new java.awt.Color(255, 255, 255));
        jLabel41.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel41.setText("Total Box :");

        label_total_data_box.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_data_box.setText("0");

        jLabel43.setBackground(new java.awt.Color(255, 255, 255));
        jLabel43.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel43.setText("Keping :");

        label_total_kpg_data_box.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg_data_box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_kpg_data_box.setText("0");

        jLabel45.setBackground(new java.awt.Color(255, 255, 255));
        jLabel45.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel45.setText("Gram :");

        label_total_gram_data_box.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_data_box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_gram_data_box.setText("0");

        jLabel47.setBackground(new java.awt.Color(255, 255, 255));
        jLabel47.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel47.setText("Lokasi :");

        ComboBox_lokasi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_lokasi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        button_rePacking.setBackground(new java.awt.Color(255, 255, 255));
        button_rePacking.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_rePacking.setText("Re-Packing");
        button_rePacking.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_rePackingActionPerformed(evt);
            }
        });

        button_reProcess.setBackground(new java.awt.Color(255, 255, 255));
        button_reProcess.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_reProcess.setText("Re-Processing");
        button_reProcess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_reProcessActionPerformed(evt);
            }
        });

        button_setor_Packing.setBackground(new java.awt.Color(255, 255, 255));
        button_setor_Packing.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_setor_Packing.setText("Setor ke Packing");
        button_setor_Packing.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_setor_PackingActionPerformed(evt);
            }
        });

        button_out.setBackground(new java.awt.Color(255, 255, 255));
        button_out.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_out.setText("Product Out");
        button_out.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_outActionPerformed(evt);
            }
        });

        button_lp_suwir.setBackground(new java.awt.Color(255, 255, 255));
        button_lp_suwir.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_lp_suwir.setText("Membuat LP Suwir");
        button_lp_suwir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_lp_suwirActionPerformed(evt);
            }
        });

        jLabel53.setBackground(new java.awt.Color(255, 255, 255));
        jLabel53.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel53.setText("Tanggal Box :");

        Date_box1.setBackground(new java.awt.Color(255, 255, 255));
        Date_box1.setDateFormatString("dd MMMM yyyy");
        Date_box1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_box2.setBackground(new java.awt.Color(255, 255, 255));
        Date_box2.setDateFormatString("dd MMMM yyyy");
        Date_box2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel57.setBackground(new java.awt.Color(255, 255, 255));
        jLabel57.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel57.setText("Bentuk Grade :");

        ComboBox_Search_BentukGrade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_Search_BentukGrade.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", " " }));

        button_terima_retur.setBackground(new java.awt.Color(255, 255, 255));
        button_terima_retur.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_terima_retur.setText("Terima Retur");
        button_terima_retur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_terima_returActionPerformed(evt);
            }
        });

        button_Belum_Repack.setBackground(new java.awt.Color(255, 255, 255));
        button_Belum_Repack.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_Belum_Repack.setText("Box Belum Repack");
        button_Belum_Repack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_Belum_RepackActionPerformed(evt);
            }
        });

        button_treatment.setBackground(new java.awt.Color(255, 255, 255));
        button_treatment.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_treatment.setText("Treatment QC");
        button_treatment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_treatmentActionPerformed(evt);
            }
        });

        button_print_label1.setBackground(new java.awt.Color(255, 255, 255));
        button_print_label1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_print_label1.setText("Print Label Box");
        button_print_label1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_print_label1ActionPerformed(evt);
            }
        });

        button_new_spk_se.setBackground(new java.awt.Color(255, 255, 255));
        button_new_spk_se.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_new_spk_se.setText("New SPK SE");
        button_new_spk_se.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_new_spk_seActionPerformed(evt);
            }
        });

        button_set_rsb.setBackground(new java.awt.Color(255, 255, 255));
        button_set_rsb.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_set_rsb.setText("Set KH to All");
        button_set_rsb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_set_rsbActionPerformed(evt);
            }
        });

        jLabel58.setBackground(new java.awt.Color(255, 255, 255));
        jLabel58.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel58.setText("RSB :");

        txt_search_rsb.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_rsb.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_rsbKeyPressed(evt);
            }
        });

        jLabel59.setBackground(new java.awt.Color(255, 255, 255));
        jLabel59.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel59.setText("SPK :");

        txt_search_spk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_spk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_spkKeyPressed(evt);
            }
        });

        button_print_label_packing.setBackground(new java.awt.Color(255, 255, 255));
        button_print_label_packing.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_print_label_packing.setText("Print Label Packing");
        button_print_label_packing.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_print_label_packingActionPerformed(evt);
            }
        });

        button_set_cheat_no_box.setBackground(new java.awt.Color(255, 255, 255));
        button_set_cheat_no_box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_set_cheat_no_box.setText("Set Cheat No Box");
        button_set_cheat_no_box.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_set_cheat_no_boxActionPerformed(evt);
            }
        });

        jLabel63.setBackground(new java.awt.Color(255, 255, 255));
        jLabel63.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel63.setText("No Invoice :");

        txt_search_no_invoice.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_no_invoice.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_no_invoiceKeyPressed(evt);
            }
        });

        jLabel64.setBackground(new java.awt.Color(255, 255, 255));
        jLabel64.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel64.setText("KH :");

        txt_search_kh.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_kh.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_khKeyPressed(evt);
            }
        });

        jLabel65.setBackground(new java.awt.Color(255, 255, 255));
        jLabel65.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel65.setText("Kode Grade SPK :");

        txt_search_kode_grade_spk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_kode_grade_spk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_kode_grade_spkKeyPressed(evt);
            }
        });

        jLabel74.setBackground(new java.awt.Color(255, 255, 255));
        jLabel74.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel74.setText("Tgl Proses Terakhir :");

        Date_proses_terakhir1.setBackground(new java.awt.Color(255, 255, 255));
        Date_proses_terakhir1.setDateFormatString("dd MMMM yyyy");
        Date_proses_terakhir1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_proses_terakhir2.setBackground(new java.awt.Color(255, 255, 255));
        Date_proses_terakhir2.setDateFormatString("dd MMMM yyyy");
        Date_proses_terakhir2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_print_label2.setBackground(new java.awt.Color(255, 255, 255));
        button_print_label2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_print_label2.setText("Print Label Box CT");
        button_print_label2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_print_label2ActionPerformed(evt);
            }
        });

        button_edit_memo_box.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_memo_box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_edit_memo_box.setText("Edit Memo BOX");
        button_edit_memo_box.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_memo_boxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_Data_BoxLayout = new javax.swing.GroupLayout(jPanel_Data_Box);
        jPanel_Data_Box.setLayout(jPanel_Data_BoxLayout);
        jPanel_Data_BoxLayout.setHorizontalGroup(
            jPanel_Data_BoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Data_BoxLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Data_BoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_Data_BoxLayout.createSequentialGroup()
                        .addGroup(jPanel_Data_BoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane6)
                            .addGroup(jPanel_Data_BoxLayout.createSequentialGroup()
                                .addGroup(jPanel_Data_BoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel_Data_BoxLayout.createSequentialGroup()
                                        .addComponent(jLabel41)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_data_box)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel43)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_kpg_data_box)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel45)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_gram_data_box))
                                    .addGroup(jPanel_Data_BoxLayout.createSequentialGroup()
                                        .addComponent(jLabel47)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(ComboBox_lokasi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel40)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(ComboBox_Search_Grade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel57)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(ComboBox_Search_BentukGrade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel53)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(Date_box1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(Date_box2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel74)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(Date_proses_terakhir1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(Date_proses_terakhir2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_Data_BoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_Data_BoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(button_set_cheat_no_box, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_Data_BoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(button_reProcess, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(button_setor_Packing, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(button_out, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(button_export_data_box, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(button_lp_suwir, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(button_terima_retur, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(button_Belum_Repack, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(button_treatment, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(button_print_label1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(button_new_spk_se, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(button_set_rsb, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(button_print_label_packing, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(button_rePacking, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(button_print_label2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_edit_memo_box, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel_Data_BoxLayout.createSequentialGroup()
                        .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_no_tutupan, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel39)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_no_box, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel58)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_rsb, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel59)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel65)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_kode_grade_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel63)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_no_invoice, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel64)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_kh, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search_Box)
                        .addGap(0, 121, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel_Data_BoxLayout.setVerticalGroup(
            jPanel_Data_BoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Data_BoxLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Data_BoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_Data_BoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel_Data_BoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_search_no_tutupan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel_Data_BoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_Data_BoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel65, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txt_search_kode_grade_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel_Data_BoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txt_search_no_box, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel58, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txt_search_rsb, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel59, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txt_search_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel_Data_BoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel64, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_kh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_search_Box, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_Data_BoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel63, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_no_invoice, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Data_BoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel_Data_BoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel_Data_BoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_Search_BentukGrade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel_Data_BoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_lokasi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_Search_Grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel_Data_BoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date_box1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date_box2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel74, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_proses_terakhir1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_proses_terakhir2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Data_BoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_Data_BoxLayout.createSequentialGroup()
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 567, Short.MAX_VALUE)
                        .addGap(11, 11, 11))
                    .addGroup(jPanel_Data_BoxLayout.createSequentialGroup()
                        .addComponent(button_rePacking, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_reProcess, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_treatment, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_Belum_Repack, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_setor_Packing, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addComponent(button_terima_retur, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_out, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_lp_suwir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_print_label1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_print_label_packing, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export_data_box, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_new_spk_se, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_set_rsb, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_set_cheat_no_box, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_print_label2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_edit_memo_box, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(jPanel_Data_BoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_data_box, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_kpg_data_box, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram_data_box, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Data Box Barang Jadi", jPanel_Data_Box);

        jPanel_data_rePacking.setBackground(new java.awt.Color(255, 255, 255));

        table_data_repacking.setAutoCreateRowSorter(true);
        table_data_repacking.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_repacking.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Repacking", "Tanggal Repacking", "Status", "Keterangan", "Pekerja", "% SH", "Kode RSB"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.String.class
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
        table_data_repacking.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table_data_repacking);

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Kode Repacking :");

        txt_search_kode_repacking.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_kode_repacking.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_kode_repackingKeyPressed(evt);
            }
        });

        Date_Search_Repacking1.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search_Repacking1.setDateFormatString("dd MMMM yyyy");
        Date_Search_Repacking1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_Search_Repacking2.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search_Repacking2.setDateFormatString("dd MMMM yyyy");
        Date_Search_Repacking2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_search_repacking.setBackground(new java.awt.Color(255, 255, 255));
        button_search_repacking.setText("Search");
        button_search_repacking.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_repackingActionPerformed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Total Data :");

        label_total_repacking.setBackground(new java.awt.Color(255, 255, 255));
        label_total_repacking.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_repacking.setText("0");

        table_asal_repacking.setAutoCreateRowSorter(true);
        table_asal_repacking.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_asal_repacking.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Repacking", "No Box", "Grade", "Keping", "Gram", "Status Box", "Lokasi Box", "Tutupan", "Pekerja Repacking", "Tgl Repacking"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_asal_repacking.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(table_asal_repacking);

        table_hasil_repacking.setAutoCreateRowSorter(true);
        table_hasil_repacking.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_hasil_repacking.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Repacking", "No Box", "Grade", "Keping", "Gram", "Status Box", "Lokasi Box"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_hasil_repacking.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(table_hasil_repacking);

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel3.setText("Tabel Data Hasil Re-Packing");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel4.setText("Tabel Data Asal Re-Packing");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Total Data :");

        label_total_hasil_repacking.setBackground(new java.awt.Color(255, 255, 255));
        label_total_hasil_repacking.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_hasil_repacking.setText("0");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Keping :");

        label_total_keping_hasil_repacking.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_hasil_repacking.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_keping_hasil_repacking.setText("0");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Gram :");

        label_total_gram_hasil_repacking.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_hasil_repacking.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_gram_hasil_repacking.setText("0");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Total Data :");

        label_total_asal_repacking.setBackground(new java.awt.Color(255, 255, 255));
        label_total_asal_repacking.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_asal_repacking.setText("0");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("Keping :");

        label_total_keping_asal_repacking.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_asal_repacking.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_keping_asal_repacking.setText("0");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("Gram :");

        label_total_gram_asal_repacking.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_asal_repacking.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_gram_asal_repacking.setText("0");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel11.setText("Data Re-Packing");

        label_kode_repacking1.setBackground(new java.awt.Color(255, 255, 255));
        label_kode_repacking1.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_kode_repacking1.setText("KODE");

        label_kode_repacking2.setBackground(new java.awt.Color(255, 255, 255));
        label_kode_repacking2.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_kode_repacking2.setText("KODE");

        jLabel50.setBackground(new java.awt.Color(255, 255, 255));
        jLabel50.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel50.setText("Tanggal Repacking :");

        button_edit_repacking.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_repacking.setText("EDIT");
        button_edit_repacking.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_repackingActionPerformed(evt);
            }
        });

        button_print_repacking.setBackground(new java.awt.Color(255, 255, 255));
        button_print_repacking.setText("PRINT");
        button_print_repacking.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_print_repackingActionPerformed(evt);
            }
        });

        button_selesai_repacking.setBackground(new java.awt.Color(255, 255, 255));
        button_selesai_repacking.setText("SELESAI");
        button_selesai_repacking.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_selesai_repackingActionPerformed(evt);
            }
        });

        button_export_asalRepacking.setBackground(new java.awt.Color(255, 255, 255));
        button_export_asalRepacking.setText("Export");
        button_export_asalRepacking.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_asalRepackingActionPerformed(evt);
            }
        });

        button_export_hasilRepacking.setBackground(new java.awt.Color(255, 255, 255));
        button_export_hasilRepacking.setText("Export");
        button_export_hasilRepacking.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_hasilRepackingActionPerformed(evt);
            }
        });

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("AVG SH :");

        label_avg_sh_repacking.setBackground(new java.awt.Color(255, 255, 255));
        label_avg_sh_repacking.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_avg_sh_repacking.setText("0");

        javax.swing.GroupLayout jPanel_data_rePackingLayout = new javax.swing.GroupLayout(jPanel_data_rePacking);
        jPanel_data_rePacking.setLayout(jPanel_data_rePackingLayout);
        jPanel_data_rePackingLayout.setHorizontalGroup(
            jPanel_data_rePackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_data_rePackingLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_data_rePackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_data_rePackingLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_kode_repacking, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel50)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Search_Repacking1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Search_Repacking2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search_repacking)
                        .addGap(0, 607, Short.MAX_VALUE))
                    .addGroup(jPanel_data_rePackingLayout.createSequentialGroup()
                        .addGroup(jPanel_data_rePackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addGroup(jPanel_data_rePackingLayout.createSequentialGroup()
                                .addGroup(jPanel_data_rePackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel_data_rePackingLayout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_repacking)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel12)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_avg_sh_repacking))
                                    .addGroup(jPanel_data_rePackingLayout.createSequentialGroup()
                                        .addComponent(jLabel11)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button_selesai_repacking)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button_print_repacking)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button_edit_repacking)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_data_rePackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel_data_rePackingLayout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_kode_repacking1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(button_export_asalRepacking))
                            .addGroup(jPanel_data_rePackingLayout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_asal_repacking)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_keping_asal_repacking)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_asal_repacking))
                            .addGroup(jPanel_data_rePackingLayout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_kode_repacking2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(button_export_hasilRepacking))
                            .addGroup(jPanel_data_rePackingLayout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_hasil_repacking)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_keping_hasil_repacking)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_hasil_repacking))
                            .addComponent(jScrollPane2)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 701, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel_data_rePackingLayout.setVerticalGroup(
            jPanel_data_rePackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_data_rePackingLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_data_rePackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_data_rePackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_kode_repacking, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_data_rePackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel_data_rePackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date_Search_Repacking1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date_Search_Repacking2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(button_search_repacking, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_data_rePackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_data_rePackingLayout.createSequentialGroup()
                        .addGroup(jPanel_data_rePackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_data_rePackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(button_edit_repacking, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(button_print_repacking, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel_data_rePackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(button_selesai_repacking, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_data_rePackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_data_rePackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(label_avg_sh_repacking, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel_data_rePackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(label_total_repacking, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel_data_rePackingLayout.createSequentialGroup()
                        .addGroup(jPanel_data_rePackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_kode_repacking1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_export_asalRepacking, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_data_rePackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_asal_repacking, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_keping_asal_repacking, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_gram_asal_repacking, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_data_rePackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_kode_repacking2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_export_hasilRepacking, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_data_rePackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_hasil_repacking, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_keping_hasil_repacking, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_gram_hasil_repacking, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Data Re-Packing", jPanel_data_rePacking);

        jPanel_data_keluar.setBackground(new java.awt.Color(255, 255, 255));

        table_data_keluar.setAutoCreateRowSorter(true);
        table_data_keluar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_keluar.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Box", "Tanggal Keluar", "Keping", "Gram", "Keterangan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Float.class, java.lang.String.class
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
        table_data_keluar.getTableHeader().setReorderingAllowed(false);
        jScrollPane8.setViewportView(table_data_keluar);

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel23.setText("No Box :");

        txt_search_box_keluar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_box_keluar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_box_keluarKeyPressed(evt);
            }
        });

        Date_Search_keluar1.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search_keluar1.setDateFormatString("dd MMMM yyyy");
        Date_Search_keluar1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_Search_keluar2.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search_keluar2.setDateFormatString("dd MMMM yyyy");
        Date_Search_keluar2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_search_keluar.setBackground(new java.awt.Color(255, 255, 255));
        button_search_keluar.setText("Search");
        button_search_keluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_keluarActionPerformed(evt);
            }
        });

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel24.setText("Total Data :");

        label_total_keluar.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keluar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_keluar.setText("0");

        jLabel33.setBackground(new java.awt.Color(255, 255, 255));
        jLabel33.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel33.setText("Data Keluar");

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel25.setText("Keping :");

        label_total_keping_keluar.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_keluar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_keping_keluar.setText("0");

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel26.setText("Gram :");

        label_total_gram_keluar.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_keluar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_gram_keluar.setText("0");

        javax.swing.GroupLayout jPanel_data_keluarLayout = new javax.swing.GroupLayout(jPanel_data_keluar);
        jPanel_data_keluar.setLayout(jPanel_data_keluarLayout);
        jPanel_data_keluarLayout.setHorizontalGroup(
            jPanel_data_keluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_data_keluarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_data_keluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel_data_keluarLayout.createSequentialGroup()
                        .addComponent(jLabel24)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_keluar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel25)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_keping_keluar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_keluar))
                    .addComponent(jLabel33)
                    .addGroup(jPanel_data_keluarLayout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_box_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Search_keluar1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Search_keluar2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search_keluar))
                    .addComponent(jScrollPane8))
                .addContainerGap(773, Short.MAX_VALUE))
        );
        jPanel_data_keluarLayout.setVerticalGroup(
            jPanel_data_keluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_data_keluarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_data_keluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel_data_keluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel_data_keluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_search_box_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(Date_Search_keluar1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Date_Search_keluar2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(button_search_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_data_keluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_data_keluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_keping_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_gram_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_data_keluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Data Keluar", jPanel_data_keluar);

        jPanel_data_LP_suwir.setBackground(new java.awt.Color(255, 255, 255));

        jLabel31.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel31.setText("No LP Suwir :");

        txt_search_lpsuwir.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_lpsuwir.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_lpsuwirKeyPressed(evt);
            }
        });

        jLabel35.setBackground(new java.awt.Color(255, 255, 255));
        jLabel35.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel35.setText("Date Filter :");

        Date_Search_LPsuwir_1.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search_LPsuwir_1.setToolTipText("");
        Date_Search_LPsuwir_1.setDate(new Date(new Date().getTime()-(14 * 24 * 60 * 60 * 1000)));
        Date_Search_LPsuwir_1.setDateFormatString("dd MMMM yyyy");
        Date_Search_LPsuwir_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_Search_LPsuwir_1.setMinSelectableDate(new java.util.Date(1420048915000L));

        jLabel36.setBackground(new java.awt.Color(255, 255, 255));
        jLabel36.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel36.setText("Sampai");

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

        jLabel32.setBackground(new java.awt.Color(255, 255, 255));
        jLabel32.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel32.setText("Data LP Suwir");

        jLabel37.setBackground(new java.awt.Color(255, 255, 255));
        jLabel37.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel37.setText("Asal Box LP Suwir");

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

        jLabel44.setBackground(new java.awt.Color(255, 255, 255));
        jLabel44.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel44.setText("Total Data :");

        label_total_asalBox_lpSuwir.setBackground(new java.awt.Color(255, 255, 255));
        label_total_asalBox_lpSuwir.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_asalBox_lpSuwir.setText("0");

        jLabel46.setBackground(new java.awt.Color(255, 255, 255));
        jLabel46.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel46.setText("Keping :");

        label_total_keping_asalBox_lpsuwir.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_asalBox_lpsuwir.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_keping_asalBox_lpsuwir.setText("0");

        jLabel48.setBackground(new java.awt.Color(255, 255, 255));
        jLabel48.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel48.setText("Gram :");

        label_total_gram_asalBox_lpsuwir.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_asalBox_lpsuwir.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_gram_asalBox_lpsuwir.setText("0");

        jLabel52.setBackground(new java.awt.Color(255, 255, 255));
        jLabel52.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel52.setText("Total Data :");

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

        button_editgram.setBackground(new java.awt.Color(255, 255, 255));
        button_editgram.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_editgram.setText("Edit Gram Akhir");
        button_editgram.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_editgramActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        label_LaporanProduksi_lpSuwir.setBackground(new java.awt.Color(255, 255, 255));
        label_LaporanProduksi_lpSuwir.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_LaporanProduksi_lpSuwir.setText("(NO LP SUWIR)");

        jLabel51.setBackground(new java.awt.Color(255, 255, 255));
        jLabel51.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel51.setText("Total Gram kaki :");

        jLabel49.setBackground(new java.awt.Color(255, 255, 255));
        jLabel49.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel49.setText("Total Data :");

        label_total_LaporanProduksi_lpsuwir.setBackground(new java.awt.Color(255, 255, 255));
        label_total_LaporanProduksi_lpsuwir.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_LaporanProduksi_lpsuwir.setText("0");

        jLabel42.setBackground(new java.awt.Color(255, 255, 255));
        jLabel42.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel42.setText("Laporan Produksi yang menggunakan LP Suwir");

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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel42)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_LaporanProduksi_lpSuwir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export_LP_F2))
                    .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel49)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_LaporanProduksi_lpsuwir)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel51)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_LaporanProduksi_lpsuwir)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_LaporanProduksi_lpSuwir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_LP_F2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_LaporanProduksi_lpsuwir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram_LaporanProduksi_lpsuwir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane2.addTab("F2", jPanel1);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel54.setBackground(new java.awt.Color(255, 255, 255));
        jLabel54.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel54.setText("Total Gram kaki :");

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

        jLabel55.setBackground(new java.awt.Color(255, 255, 255));
        jLabel55.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel55.setText("Total Data :");

        label_total_BoxRepacking_lpsuwir.setBackground(new java.awt.Color(255, 255, 255));
        label_total_BoxRepacking_lpsuwir.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_BoxRepacking_lpsuwir.setText("0");

        jLabel56.setBackground(new java.awt.Color(255, 255, 255));
        jLabel56.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel56.setText("Box Re-Proses menggunakan LP susur perut");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel56)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_BoxReproses_lpSuwir1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export_BoxReproses))
                    .addComponent(jScrollPane13, javax.swing.GroupLayout.DEFAULT_SIZE, 435, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel55)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_BoxRepacking_lpsuwir)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel54)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_BoxRepracking_lpsuwir)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_BoxReproses_lpSuwir1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_BoxReproses, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane13, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_BoxRepacking_lpsuwir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram_BoxRepracking_lpsuwir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane2.addTab("Box Reproses", jPanel2);

        jLabel60.setBackground(new java.awt.Color(255, 255, 255));
        jLabel60.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel60.setText("Total Stok :");

        label_total_stok.setBackground(new java.awt.Color(255, 255, 255));
        label_total_stok.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_stok.setText("0");

        button_edit_lp_suwir.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_lp_suwir.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_edit_lp_suwir.setText("EDIT");
        button_edit_lp_suwir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_lp_suwirActionPerformed(evt);
            }
        });

        button_edit_lp_suwir1.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_lp_suwir1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_edit_lp_suwir1.setText("DELETE");
        button_edit_lp_suwir1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_lp_suwir1ActionPerformed(evt);
            }
        });

        label_total_keluar_f2.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keluar_f2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_keluar_f2.setText("0");

        jLabel75.setBackground(new java.awt.Color(255, 255, 255));
        jLabel75.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel75.setText("Total Keluar F2 :");

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
                        .addComponent(jLabel31)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_lpsuwir, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel35)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Search_LPsuwir_1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel36)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Search_LPsuwir_2, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search_lp))
                    .addGroup(jPanel_data_LP_suwirLayout.createSequentialGroup()
                        .addGroup(jPanel_data_LP_suwirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel_data_LP_suwirLayout.createSequentialGroup()
                                .addComponent(jLabel52)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_lpsuwir)
                                .addGap(21, 21, 21)
                                .addComponent(jLabel60)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_stok)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel75)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_keluar_f2))
                            .addGroup(jPanel_data_LP_suwirLayout.createSequentialGroup()
                                .addComponent(jLabel32)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(button_edit_lp_suwir1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_edit_lp_suwir)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_editgram)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_Print_LP_SWR)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_LPSuwir))
                            .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 871, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel_data_LP_suwirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane11)
                            .addGroup(jPanel_data_LP_suwirLayout.createSequentialGroup()
                                .addComponent(jLabel37)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_asalBox_lpSuwir)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(button_export_AsalBox))
                            .addGroup(jPanel_data_LP_suwirLayout.createSequentialGroup()
                                .addComponent(jLabel44)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_asalBox_lpSuwir)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel46)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_keping_asalBox_lpsuwir)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel48)
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
                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Search_LPsuwir_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Search_LPsuwir_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_lpsuwir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_data_LP_suwirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_data_LP_suwirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_asalBox_lpSuwir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_export_LPSuwir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_export_AsalBox, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_editgram, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_edit_lp_suwir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_edit_lp_suwir1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_Print_LP_SWR, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_data_LP_suwirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_data_LP_suwirLayout.createSequentialGroup()
                        .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_data_LP_suwirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_data_LP_suwirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel75, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(label_total_keluar_f2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel_data_LP_suwirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel60, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(label_total_stok, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel_data_LP_suwirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(label_total_lpsuwir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel_data_LP_suwirLayout.createSequentialGroup()
                        .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_data_LP_suwirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_asalBox_lpSuwir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_keping_asalBox_lpsuwir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_gram_asalBox_lpsuwir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Data LP Suwir", jPanel_data_LP_suwir);

        jPanel_data_pinjam_barangJadi.setBackground(new java.awt.Color(255, 255, 255));

        table_data_pinjam_barang_jadi.setAutoCreateRowSorter(true);
        table_data_pinjam_barang_jadi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_pinjam_barang_jadi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Box", "Tanggal Keluar", "Keping", "Gram", "Waktu Pinjam", "Waktu Kembali", "Keterangan", "Departemen", "PIC"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_data_pinjam_barang_jadi.getTableHeader().setReorderingAllowed(false);
        jScrollPane9.setViewportView(table_data_pinjam_barang_jadi);

        jLabel27.setBackground(new java.awt.Color(255, 255, 255));
        jLabel27.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel27.setText("No Box :");

        txt_search_box_dipinjam.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_box_dipinjam.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_box_dipinjamKeyPressed(evt);
            }
        });

        Date_barang_dipinjam1.setBackground(new java.awt.Color(255, 255, 255));
        Date_barang_dipinjam1.setDateFormatString("dd MMMM yyyy");
        Date_barang_dipinjam1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_barang_dipinjam2.setBackground(new java.awt.Color(255, 255, 255));
        Date_barang_dipinjam2.setDateFormatString("dd MMMM yyyy");
        Date_barang_dipinjam2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_search_pinjam_barang_jadi.setBackground(new java.awt.Color(255, 255, 255));
        button_search_pinjam_barang_jadi.setText("Search");
        button_search_pinjam_barang_jadi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_pinjam_barang_jadiActionPerformed(evt);
            }
        });

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel28.setText("Total Data :");

        label_total_box_dipinjam.setBackground(new java.awt.Color(255, 255, 255));
        label_total_box_dipinjam.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_box_dipinjam.setText("0");

        jLabel29.setBackground(new java.awt.Color(255, 255, 255));
        jLabel29.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel29.setText("Keping :");

        label_total_keping_dipinjam.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_dipinjam.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_keping_dipinjam.setText("0");

        jLabel30.setBackground(new java.awt.Color(255, 255, 255));
        jLabel30.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel30.setText("Gram :");

        label_total_gram_dipinjam.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_dipinjam.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_gram_dipinjam.setText("0");

        jLabel62.setBackground(new java.awt.Color(255, 255, 255));
        jLabel62.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel62.setText("Tanggal :");

        javax.swing.GroupLayout jPanel_data_pinjam_barangJadiLayout = new javax.swing.GroupLayout(jPanel_data_pinjam_barangJadi);
        jPanel_data_pinjam_barangJadi.setLayout(jPanel_data_pinjam_barangJadiLayout);
        jPanel_data_pinjam_barangJadiLayout.setHorizontalGroup(
            jPanel_data_pinjam_barangJadiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_data_pinjam_barangJadiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_data_pinjam_barangJadiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane9)
                    .addGroup(jPanel_data_pinjam_barangJadiLayout.createSequentialGroup()
                        .addGroup(jPanel_data_pinjam_barangJadiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_data_pinjam_barangJadiLayout.createSequentialGroup()
                                .addComponent(jLabel28)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_box_dipinjam)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel29)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_keping_dipinjam)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel30)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_dipinjam))
                            .addGroup(jPanel_data_pinjam_barangJadiLayout.createSequentialGroup()
                                .addComponent(jLabel27)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_box_dipinjam, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel62)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_barang_dipinjam1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_barang_dipinjam2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search_pinjam_barang_jadi)))
                        .addGap(0, 717, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel_data_pinjam_barangJadiLayout.setVerticalGroup(
            jPanel_data_pinjam_barangJadiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_data_pinjam_barangJadiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_data_pinjam_barangJadiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel_data_pinjam_barangJadiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel_data_pinjam_barangJadiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_search_box_dipinjam, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel62, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(Date_barang_dipinjam1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Date_barang_dipinjam2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(button_search_pinjam_barang_jadi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 598, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_data_pinjam_barangJadiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_data_pinjam_barangJadiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_keping_dipinjam, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_gram_dipinjam, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_data_pinjam_barangJadiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_box_dipinjam, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Data Pinjam Barang Jadi", jPanel_data_pinjam_barangJadi);

        jPanel_data_kinerja_gbj.setBackground(new java.awt.Color(255, 255, 255));

        table_data_kinerja_gbj.setAutoCreateRowSorter(true);
        table_data_kinerja_gbj.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_kinerja_gbj.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Tanggal", "ID", "Nama", "No Box", "Grade", "Kpg Box", "Berat Box", "Kpg", "Gram", "Catatan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class
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
        table_data_kinerja_gbj.getTableHeader().setReorderingAllowed(false);
        jScrollPane14.setViewportView(table_data_kinerja_gbj);

        jLabel34.setBackground(new java.awt.Color(255, 255, 255));
        jLabel34.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel34.setText("No Box :");

        txt_search_box_kinerja.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_box_kinerja.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_box_kinerjaKeyPressed(evt);
            }
        });

        Date_kinerja1.setBackground(new java.awt.Color(255, 255, 255));
        Date_kinerja1.setDate(new Date());
        Date_kinerja1.setDateFormatString("dd MMM yyyy");
        Date_kinerja1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_kinerja2.setBackground(new java.awt.Color(255, 255, 255));
        Date_kinerja2.setDate(new Date());
        Date_kinerja2.setDateFormatString("dd MMM yyyy");
        Date_kinerja2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_search_kinerja.setBackground(new java.awt.Color(255, 255, 255));
        button_search_kinerja.setText("Search");
        button_search_kinerja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_kinerjaActionPerformed(evt);
            }
        });

        jLabel66.setBackground(new java.awt.Color(255, 255, 255));
        jLabel66.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel66.setText("Total Data :");

        label_total_data_kinerja.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_kinerja.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_data_kinerja.setText("0");

        jLabel67.setBackground(new java.awt.Color(255, 255, 255));
        jLabel67.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel67.setText("Keping Box :");

        label_total_keping_lp_kinerjaGBJ.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_lp_kinerjaGBJ.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_keping_lp_kinerjaGBJ.setText("0");

        jLabel68.setBackground(new java.awt.Color(255, 255, 255));
        jLabel68.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel68.setText("Gram Box :");

        label_total_gram_lp_kinerjaGBJ.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_lp_kinerjaGBJ.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gram_lp_kinerjaGBJ.setText("0");

        jLabel69.setBackground(new java.awt.Color(255, 255, 255));
        jLabel69.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel69.setText("Tanggal :");

        jLabel70.setBackground(new java.awt.Color(255, 255, 255));
        jLabel70.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel70.setText("ID :");

        txt_search_id_kinerja.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_id_kinerja.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_id_kinerjaKeyPressed(evt);
            }
        });

        jLabel71.setBackground(new java.awt.Color(255, 255, 255));
        jLabel71.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel71.setText("Nama :");

        txt_search_nama_kinerja.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_nama_kinerja.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_nama_kinerjaKeyPressed(evt);
            }
        });

        button_delete_kinerja.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_kinerja.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete_kinerja.setText("Delete");
        button_delete_kinerja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_kinerjaActionPerformed(evt);
            }
        });

        button_insert_kinerja.setBackground(new java.awt.Color(255, 255, 255));
        button_insert_kinerja.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_insert_kinerja.setText("Insert");
        button_insert_kinerja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_insert_kinerjaActionPerformed(evt);
            }
        });

        button_edit_kinerja.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_kinerja.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_edit_kinerja.setText("Edit");
        button_edit_kinerja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_kinerjaActionPerformed(evt);
            }
        });

        label_total_keping_dikerjakan_kinerjaGBJ.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_dikerjakan_kinerjaGBJ.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_keping_dikerjakan_kinerjaGBJ.setText("0");

        jLabel72.setBackground(new java.awt.Color(255, 255, 255));
        jLabel72.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel72.setText("Total Kpg dikerjakan :");

        label_total_gram_dikerjakan_kinerjaGBJ.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_dikerjakan_kinerjaGBJ.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gram_dikerjakan_kinerjaGBJ.setText("0");

        jLabel73.setBackground(new java.awt.Color(255, 255, 255));
        jLabel73.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel73.setText("Total Gram dikerjakan :");

        javax.swing.GroupLayout jPanel_data_kinerja_gbjLayout = new javax.swing.GroupLayout(jPanel_data_kinerja_gbj);
        jPanel_data_kinerja_gbj.setLayout(jPanel_data_kinerja_gbjLayout);
        jPanel_data_kinerja_gbjLayout.setHorizontalGroup(
            jPanel_data_kinerja_gbjLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_data_kinerja_gbjLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_data_kinerja_gbjLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane14)
                    .addGroup(jPanel_data_kinerja_gbjLayout.createSequentialGroup()
                        .addGroup(jPanel_data_kinerja_gbjLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_data_kinerja_gbjLayout.createSequentialGroup()
                                .addComponent(button_insert_kinerja)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_edit_kinerja)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_delete_kinerja)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel66)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data_kinerja)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel67)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_keping_lp_kinerjaGBJ)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel68)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_lp_kinerjaGBJ)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel72)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_keping_dikerjakan_kinerjaGBJ)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel73)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_dikerjakan_kinerjaGBJ))
                            .addGroup(jPanel_data_kinerja_gbjLayout.createSequentialGroup()
                                .addComponent(jLabel70)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_id_kinerja, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel71)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_nama_kinerja, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel34)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_box_kinerja, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel69)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_kinerja1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_kinerja2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search_kinerja)))
                        .addGap(0, 353, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel_data_kinerja_gbjLayout.setVerticalGroup(
            jPanel_data_kinerja_gbjLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_data_kinerja_gbjLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_data_kinerja_gbjLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel_data_kinerja_gbjLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel70, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_id_kinerja, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel71, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_nama_kinerja, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_data_kinerja_gbjLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel_data_kinerja_gbjLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_search_box_kinerja, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel69, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(Date_kinerja1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Date_kinerja2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(button_search_kinerja, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_data_kinerja_gbjLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_data_kinerja_gbjLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_insert_kinerja, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_edit_kinerja, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_delete_kinerja, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_data_kinerja_gbjLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel67, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_keping_lp_kinerjaGBJ, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel68, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_gram_lp_kinerjaGBJ, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel72, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_keping_dikerjakan_kinerjaGBJ, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel73, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_gram_dikerjakan_kinerjaGBJ, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_data_kinerja_gbjLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel66, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_data_kinerja, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane14, javax.swing.GroupLayout.DEFAULT_SIZE, 598, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Data kinerja GBJ", jPanel_data_kinerja_gbj);

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

    private void button_search_BoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_BoxActionPerformed
        // TODO add your handling code here:
        refreshTable_DataBox();
    }//GEN-LAST:event_button_search_BoxActionPerformed

    private void txt_search_no_tutupanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_no_tutupanKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_DataBox();
        }
    }//GEN-LAST:event_txt_search_no_tutupanKeyPressed

    private void txt_search_no_boxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_no_boxKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_DataBox();
        }
    }//GEN-LAST:event_txt_search_no_boxKeyPressed

    private void button_export_data_boxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_data_boxActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_dataBox.getModel();
        ExportToExcel.writeToExcel(model, jPanel_data_rePacking);
    }//GEN-LAST:event_button_export_data_boxActionPerformed

    private void button_rePackingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_rePackingActionPerformed
        JDialog_rePacking dialog = new JDialog_rePacking(new javax.swing.JFrame(), true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        refreshTable_DataBox();
    }//GEN-LAST:event_button_rePackingActionPerformed

    private void button_reProcessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_reProcessActionPerformed
        JDialog_reProcess dialog = new JDialog_reProcess(new javax.swing.JFrame(), true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        refreshTable_DataBox();
    }//GEN-LAST:event_button_reProcessActionPerformed

    private void button_setor_PackingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_setor_PackingActionPerformed
        JDialog_Setor_Packing dialog = new JDialog_Setor_Packing(new javax.swing.JFrame(), true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        refreshTable_DataBox();
    }//GEN-LAST:event_button_setor_PackingActionPerformed

    private void button_outActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_outActionPerformed
        JDialog_Out dialog = new JDialog_Out(new javax.swing.JFrame(), true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        refreshTable_DataBox();
    }//GEN-LAST:event_button_outActionPerformed

    private void txt_search_kode_repackingKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_kode_repackingKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_Repacking();
        }
    }//GEN-LAST:event_txt_search_kode_repackingKeyPressed

    private void button_search_repackingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_repackingActionPerformed
        // TODO add your handling code here:
        refreshTable_Repacking();
        DefaultTableModel model1 = (DefaultTableModel) table_asal_repacking.getModel();
        model1.setRowCount(0);
        DefaultTableModel model2 = (DefaultTableModel) table_hasil_repacking.getModel();
        model2.setRowCount(0);
    }//GEN-LAST:event_button_search_repackingActionPerformed

    private void txt_search_box_keluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_box_keluarKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_Keluar();
        }
    }//GEN-LAST:event_txt_search_box_keluarKeyPressed

    private void button_search_keluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_keluarActionPerformed
        // TODO add your handling code here:
        refreshTable_Keluar();
    }//GEN-LAST:event_button_search_keluarActionPerformed

    private void button_lp_suwirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_lp_suwirActionPerformed
        // TODO add your handling code here:
        JDialog_Create_LPSuwir dialog = new JDialog_Create_LPSuwir(new javax.swing.JFrame(), true, null, null, null);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        refreshTable_DataBox();
    }//GEN-LAST:event_button_lp_suwirActionPerformed

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

    private void button_edit_repackingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_repackingActionPerformed
        // TODO add your handling code here:
        boolean check = true;
        int x = table_data_repacking.getSelectedRow();
        if (x > -1) {
            try {
                for (int i = 0; i < table_hasil_repacking.getRowCount(); i++) {
                    String query = "SELECT `status_terakhir`, `lokasi_terakhir` FROM `tb_box_bahan_jadi` "
                            + "WHERE `no_box` = '" + table_hasil_repacking.getValueAt(i, 1) + "'";
                    rs = Utility.db.getStatement().executeQuery(query);
                    if (rs.next()) {
                        if (!"NEW BOX".equals(rs.getString("status_terakhir")) || !"GRADING".equals(rs.getString("lokasi_terakhir"))) {
                            JOptionPane.showMessageDialog(this, "maaf tidak bisa edit repacking, Box hasil repacking sudah terproses di proses yang lain");
                            check = false;
                            break;
                        }
                    }
                }
            } catch (SQLException ex) {
                check = false;
                JOptionPane.showMessageDialog(this, ex);
                Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            check = false;
        }

        if (check) {
            String kode_repacking = table_data_repacking.getValueAt(x, 0).toString();
            String keterangan = table_data_repacking.getValueAt(x, 3).toString();
            String pekerja = table_data_repacking.getValueAt(x, 4).toString();
            String kode_rsb = "";
            if (table_data_repacking.getValueAt(x, 6) != null) {
                kode_rsb = table_data_repacking.getValueAt(x, 6).toString();
            }
            Date tgl_repacking = null;
            try {
                tgl_repacking = dateFormat.parse(table_data_repacking.getValueAt(x, 1).toString());
            } catch (ParseException ex) {
                Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
            }
            JDialog_Edit_rePacking dialog = new JDialog_Edit_rePacking(new javax.swing.JFrame(), true, kode_repacking, tgl_repacking, keterangan, pekerja, kode_rsb);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            dialog.setEnabled(true);
            refreshTable_Repacking();
            refreshTable_detailRepacking();
        }
    }//GEN-LAST:event_button_edit_repackingActionPerformed

    private void button_print_repackingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_print_repackingActionPerformed
        try {
            // TODO add your handling code here:
            //            DefaultTableModel Table = (DefaultTableModel)Table_Bahan_Baku_Masuk.getModel();
            int j = table_data_repacking.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Please select data from the table first", "warning!", 1);
            } else {

                JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Data_Repacking.jrxml");

                JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);

                Map<String, Object> params = new HashMap<String, Object>();
                params.put("PARAM_KODE_REPACKING", table_data_repacking.getValueAt(j, 0));
                params.put("SUBREPORT_DIR", "Report\\");
                JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, params, Utility.db.getConnection());
                JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
            }

        } catch (JRException ex) {
            JOptionPane.showMessageDialog(this, ex.getLocalizedMessage());
            Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_print_repackingActionPerformed

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

    private void button_editgramActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_editgramActionPerformed
        // TODO add your handling code here:
        int i = table_data_LPsuwir.getSelectedRow();
        if (i > -1) {
            try {
                String harga = JOptionPane.showInputDialog("Masukkan Gram Akhir : ");
                double HARGA_F = Double.valueOf(harga);
                decimalFormat = Utility.DecimalFormatUS(decimalFormat);
                sql = "UPDATE `tb_lp_suwir` SET `gram_akhir`='" + harga + "' WHERE `no_lp_suwir`='" + table_data_LPsuwir.getValueAt(i, 0) + "'";
                if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                    refreshTable_LP_suwir();
                    JOptionPane.showMessageDialog(this, "Update success!");
                } else {
                    JOptionPane.showMessageDialog(this, "Update failed!");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error Connection!");
                Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Input must be number!");
                Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih No LP yang akan di edit");
        }

    }//GEN-LAST:event_button_editgramActionPerformed

    private void button_selesai_repackingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_selesai_repackingActionPerformed
        // TODO add your handling code here:
        try {
            int j = table_data_repacking.getSelectedRow();
            String kode_repacking = table_data_repacking.getValueAt(j, 0).toString();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih repacking yang sudah selesai !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Apakah yakin repacking " + kode_repacking + " sudah selesai ??", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    String Query = "UPDATE `tb_repacking` SET `status_repacking`='SELESAI' WHERE `kode_repacking` = '" + kode_repacking + "'";
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(Query);
                    button_search_repacking.doClick();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_selesai_repackingActionPerformed

    private void button_export_asalRepackingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_asalRepackingActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_asal_repacking.getModel();
        ExportToExcel.writeToExcel(model, jPanel_data_rePacking);
    }//GEN-LAST:event_button_export_asalRepackingActionPerformed

    private void button_export_hasilRepackingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_hasilRepackingActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_hasil_repacking.getModel();
        ExportToExcel.writeToExcel(model, jPanel_data_rePacking);
    }//GEN-LAST:event_button_export_hasilRepackingActionPerformed

    private void button_export_BoxReprosesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_BoxReprosesActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_BoxReprosesi_LPSuwir.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_export_BoxReprosesActionPerformed

    private void button_terima_returActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_terima_returActionPerformed
        // TODO add your handling code here:
        JDialog_terima_retur dialog = new JDialog_terima_retur(new javax.swing.JFrame(), true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        dialog.setResizable(false);
    }//GEN-LAST:event_button_terima_returActionPerformed

    private void button_Belum_RepackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_Belum_RepackActionPerformed
        // TODO add your handling code here:
        JFrame_Reproses_Belum_Repack frame = new JFrame_Reproses_Belum_Repack();
        frame.pack();
        frame.setVisible(true);
        frame.setEnabled(true);
        frame.setLocationRelativeTo(this);
    }//GEN-LAST:event_button_Belum_RepackActionPerformed

    private void button_treatmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_treatmentActionPerformed
        // TODO add your handling code here:
        JDialog_treatment_box dialog = new JDialog_treatment_box(new javax.swing.JFrame(), true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        dialog.setResizable(false);
    }//GEN-LAST:event_button_treatmentActionPerformed

    private void button_print_label1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_print_label1ActionPerformed
        // TODO add your handling code here:
        try {
            String no_box = "";
            for (int i = 0; i < table_dataBox.getRowCount(); i++) {
                if (i != 0) {
                    no_box = no_box + ", ";
                }
                no_box = no_box + "'" + table_dataBox.getValueAt(i, 0).toString() + "'";
            }
            String query = "SELECT `no_box`, `tanggal_box`, `keping`, `berat`, `kode_grade`, `tb_box_bahan_jadi`.`kode_rsb`, \n"
                    + "(SELECT `no_kartu_waleta` FROM `tb_bahan_baku_masuk_cheat` WHERE `kode_kh` = `tb_dokumen_kh`.`kode_kh` AND `no_kartu_waleta` NOT LIKE '%CMP%' ORDER BY `no_kartu_waleta` LIMIT 1) AS 'no_kartu_waleta'\n"
                    + "FROM `tb_box_bahan_jadi`\n"
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`\n"
                    + "LEFT JOIN `tb_dokumen_kh` ON `tb_box_bahan_jadi`.`kode_kh` = `tb_dokumen_kh`.`kode_kh`\n"
                    + "WHERE `no_box` IN (" + no_box + ")";
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
        } catch (JRException ex) {
            Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_print_label1ActionPerformed

    private void button_new_spk_seActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_new_spk_seActionPerformed
        // TODO add your handling code here:
        JDialog_new_SPK_SE_Lokal dialog = new JDialog_new_SPK_SE_Lokal(new javax.swing.JFrame(), true, "SE");
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        dialog.setResizable(false);
    }//GEN-LAST:event_button_new_spk_seActionPerformed

    private void button_set_rsbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_set_rsbActionPerformed
        // TODO add your handling code here:
        try {
            int jumlah_berhasil = 0, jumlah_gagal = 0;
            String kode_kh = JOptionPane.showInputDialog("Silahkan masukkan no kode KH yang sudah terdaftar : ");
            if (kode_kh == null || kode_kh.equals("")) {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Hapus kode KH dan RSB dari BOX, lanjutkan ??", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    for (int i = 0; i < table_dataBox.getRowCount(); i++) {
                        String Query = "UPDATE `tb_box_bahan_jadi` SET `kode_rsb`=NULL, `kode_kh`=NULL "
                                + "WHERE `no_box` = '" + table_dataBox.getValueAt(i, 0).toString() + "'";
                        //jika sudah di turunkan ke packing memakai rsb dari spk, edit rsb dari spk
                        if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                            jumlah_berhasil++;
                        } else {
                            jumlah_gagal++;
                        }
                    }
                    JOptionPane.showMessageDialog(this, "Data berhasil terupdate = " + jumlah_berhasil);
                    button_search_Box.doClick();
                }
            } else {
                String query = "SELECT `kode_kh` FROM `tb_dokumen_kh` WHERE `kode_kh` = '" + kode_kh + "' ";
                rs = Utility.db.getStatement().executeQuery(query);
                if (rs.next()) {
                    int dialogResult = JOptionPane.showConfirmDialog(this, "Edit data kode KH dan RSB semua BOX menjadi " + kode_kh + ", lanjutkan ??", "Warning", 0);
                    if (dialogResult == JOptionPane.YES_OPTION) {
                        String[] kode_rsb = kode_kh.split("-");
                        for (int i = 0; i < table_dataBox.getRowCount(); i++) {
                            String Query = "UPDATE `tb_box_bahan_jadi` SET `kode_rsb`='" + kode_rsb[0] + "', `kode_kh`='" + kode_kh + "' "
                                    + "WHERE `no_box` = '" + table_dataBox.getValueAt(i, 0).toString() + "'";
                            //jika sudah di turunkan ke packing memakai rsb dari spk, edit rsb dari spk
                            if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                                jumlah_berhasil++;
                            } else {
                                jumlah_gagal++;
                            }
                        }
                        JOptionPane.showMessageDialog(this, "Data berhasil terupdate = " + jumlah_berhasil);
                        button_search_Box.doClick();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Maaf Rumah burung belum terdaftar / teregistrasi !");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_set_rsbActionPerformed

    private void txt_search_rsbKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_rsbKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_DataBox();
        }
    }//GEN-LAST:event_txt_search_rsbKeyPressed

    private void txt_search_spkKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_spkKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_DataBox();
        }
    }//GEN-LAST:event_txt_search_spkKeyPressed

    private void button_print_label_packingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_print_label_packingActionPerformed
        // TODO add your handling code here:
        refreshTable_DataBox();
        try {
            String no_box = "";
            for (int i = 0; i < table_dataBox.getRowCount(); i++) {
                if (i != 0) {
                    no_box = no_box + ", ";
                }
                no_box = no_box + "'" + table_dataBox.getValueAt(i, 0).toString() + "'";
            }
            String query = "SELECT `tb_box_bahan_jadi`.`no_box`, `tanggal_box`, `keping`, `berat`, `kode_grade`, `tb_box_bahan_jadi`.`kode_rsb`, `batch_number`  "
                    + "FROM `tb_box_bahan_jadi`\n"
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode` "
                    + "LEFT JOIN `tb_dokumen_kh` ON `tb_box_bahan_jadi`.`kode_kh` = `tb_dokumen_kh`.`kode_kh` \n"
                    + "LEFT JOIN `tb_box_packing` ON `tb_box_bahan_jadi`.`no_box` = `tb_box_packing`.`no_box` \n"
                    + "WHERE `tb_box_bahan_jadi`.`no_box` IN (" + no_box + ")";
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(query);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Label_Box_QR_Packing.jrxml");
            JASP_DESIGN.setQuery(newQuery);
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            Map<String, Object> params = new HashMap<>();
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, params, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);
        } catch (JRException ex) {
            Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_print_label_packingActionPerformed

    private void button_set_cheat_no_boxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_set_cheat_no_boxActionPerformed
        // TODO add your handling code here:
        try {
            int jumlah_berhasil = 0, jumlah_gagal = 0, jumlah_data = table_dataBox.getRowCount();
            String no_box1 = JOptionPane.showInputDialog("Silahkan masukkan no box pertama (contoh 210801234) : ");
            if (no_box1 != null && !no_box1.equals("")) {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Cheat No Box dimulai dari no " + no_box1 + ", lanjutkan ??", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    for (int i = 0; i < jumlah_data; i++) {
                        String tahun_bulan = no_box1.substring(0, 4);
                        int nomor = Integer.valueOf(no_box1.substring(4));
                        String formatted_number = table_dataBox.getValueAt(i, 0).toString().substring(0, 7) + tahun_bulan + String.format("%05d", nomor + i);
                        String Query = "UPDATE `tb_box_bahan_jadi` SET `no_box_ct1`='" + formatted_number + "' WHERE `no_box` = '" + table_dataBox.getValueAt(i, 0).toString() + "'";
                        if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                            jumlah_berhasil++;
                        } else {
                            jumlah_gagal++;
                        }
                    }
                    JOptionPane.showMessageDialog(this, "Data berhasil terupdate = " + jumlah_berhasil);
                    button_search_Box.doClick();
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_set_cheat_no_boxActionPerformed

    private void txt_search_box_dipinjamKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_box_dipinjamKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_PinjamBarangJadi();
        }
    }//GEN-LAST:event_txt_search_box_dipinjamKeyPressed

    private void button_search_pinjam_barang_jadiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_pinjam_barang_jadiActionPerformed
        // TODO add your handling code here:
        refreshTable_PinjamBarangJadi();
    }//GEN-LAST:event_button_search_pinjam_barang_jadiActionPerformed

    private void txt_search_no_invoiceKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_no_invoiceKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_DataBox();
        }
    }//GEN-LAST:event_txt_search_no_invoiceKeyPressed

    private void txt_search_khKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_khKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_DataBox();
        }
    }//GEN-LAST:event_txt_search_khKeyPressed

    private void txt_search_kode_grade_spkKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_kode_grade_spkKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_DataBox();
        }
    }//GEN-LAST:event_txt_search_kode_grade_spkKeyPressed

    private void txt_search_box_kinerjaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_box_kinerjaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_KinerjaGBJ();
        }
    }//GEN-LAST:event_txt_search_box_kinerjaKeyPressed

    private void button_search_kinerjaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_kinerjaActionPerformed
        // TODO add your handling code here:
        refreshTable_KinerjaGBJ();
    }//GEN-LAST:event_button_search_kinerjaActionPerformed

    private void txt_search_id_kinerjaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_id_kinerjaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_KinerjaGBJ();
        }
    }//GEN-LAST:event_txt_search_id_kinerjaKeyPressed

    private void txt_search_nama_kinerjaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_nama_kinerjaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_KinerjaGBJ();
        }
    }//GEN-LAST:event_txt_search_nama_kinerjaKeyPressed

    private void button_delete_kinerjaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_kinerjaActionPerformed
        // TODO add your handling code here:
        try {
            int j = table_data_kinerja_gbj.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang akan dihapus pada tabel. !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "yakin akan hapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    String no = table_data_kinerja_gbj.getValueAt(j, 0).toString();
                    String Query = "DELETE FROM `tb_kinerja_gbj` WHERE `no` = '" + no + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                        JOptionPane.showMessageDialog(this, "data berhasil di hapus");
                        refreshTable_KinerjaGBJ();
                    } else {
                        JOptionPane.showMessageDialog(this, "delete Failed!");
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_delete_kinerjaActionPerformed

    private void button_insert_kinerjaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_insert_kinerjaActionPerformed
        // TODO add your handling code here:
        JDialog_Insert_Edit_kinerjaGBJ dialog = new JDialog_Insert_Edit_kinerjaGBJ(new javax.swing.JFrame(), true, null);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        dialog.setResizable(false);
        refreshTable_KinerjaGBJ();
    }//GEN-LAST:event_button_insert_kinerjaActionPerformed

    private void button_edit_kinerjaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_kinerjaActionPerformed
        // TODO add your handling code here:
        int x = table_data_kinerja_gbj.getSelectedRow();
        if (x > -1) {
            String no = table_data_kinerja_gbj.getValueAt(x, 0).toString();
            JDialog_Insert_Edit_kinerjaGBJ dialog = new JDialog_Insert_Edit_kinerjaGBJ(new javax.swing.JFrame(), true, no);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            dialog.setEnabled(true);
            refreshTable_KinerjaGBJ();
        }
    }//GEN-LAST:event_button_edit_kinerjaActionPerformed

    private void button_edit_lp_suwirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_lp_suwirActionPerformed
        // TODO add your handling code here:
        int i = table_data_LPsuwir.getSelectedRow();
        if (i > -1) {
            try {
                boolean cek_f2 = true, cek_reproses = true;
                String no_lp_kaki = table_data_LPsuwir.getValueAt(i, 0).toString();
                String tgl_lp = table_data_LPsuwir.getValueAt(i, 1).toString();
                String gram_akhir = table_data_LPsuwir.getValueAt(i, 4).toString();
                sql = "SELECT `no_laporan_produksi`, `tambahan_kaki1`, `lp_kaki1`, `tambahan_kaki2`, `lp_kaki2` FROM `tb_finishing_2` "
                        + "WHERE `lp_kaki1` = '" + no_lp_kaki + "' OR `lp_kaki2` = '" + no_lp_kaki + "'";
                rs = Utility.db.getStatement().executeQuery(sql);
                cek_f2 = rs.next();

                sql = "SELECT `no_reproses`, `no_box`, `tanggal_proses`, `no_lp_suwir`, `no_lp_suwir2`, `gram_kaki`, `gram_kaki2` FROM `tb_reproses` WHERE `no_lp_suwir` = '" + no_lp_kaki + "' OR `no_lp_suwir2` = '" + no_lp_kaki + "'";
                rs = Utility.db.getStatement().executeQuery(sql);
                cek_reproses = rs.next();

                if (cek_reproses || cek_f2) {
                    JOptionPane.showMessageDialog(this, "Maaf " + no_lp_kaki + " sudah terpakai, tidak dapat melakukan edit");
                } else {
                    JDialog_Create_LPSuwir dialog = new JDialog_Create_LPSuwir(new javax.swing.JFrame(), true, no_lp_kaki, tgl_lp, gram_akhir);
                    dialog.pack();
                    dialog.setLocationRelativeTo(this);
                    dialog.setVisible(true);
                    dialog.setEnabled(true);
                    refreshTable_DataBox();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e);
                Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, e);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih No LP yang akan di edit");
        }
    }//GEN-LAST:event_button_edit_lp_suwirActionPerformed

    private void button_edit_lp_suwir1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_lp_suwir1ActionPerformed
        // TODO add your handling code here:
        int i = table_data_LPsuwir.getSelectedRow();
        if (i > -1) {
            try {
                boolean cek_f2 = true, cek_reproses = true;
                String no_lp_kaki = table_data_LPsuwir.getValueAt(i, 0).toString();
                sql = "SELECT `no_laporan_produksi`, `tambahan_kaki1`, `lp_kaki1`, `tambahan_kaki2`, `lp_kaki2` FROM `tb_finishing_2` "
                        + "WHERE `lp_kaki1` = '" + no_lp_kaki + "' OR `lp_kaki2` = '" + no_lp_kaki + "'";
                rs = Utility.db.getStatement().executeQuery(sql);
                cek_f2 = rs.next();

                sql = "SELECT `no_reproses`, `no_box`, `tanggal_proses`, `no_lp_suwir`, `no_lp_suwir2`, `gram_kaki`, `gram_kaki2` FROM `tb_reproses` WHERE `no_lp_suwir` = '" + no_lp_kaki + "' OR `no_lp_suwir2` = '" + no_lp_kaki + "'";
                rs = Utility.db.getStatement().executeQuery(sql);
                cek_reproses = rs.next();

                if (cek_reproses || cek_f2) {
                    JOptionPane.showMessageDialog(this, "Maaf " + no_lp_kaki + " sudah terpakai, data LP tidak bisa di hapus");
                } else {
                    int dialogResult = JOptionPane.showConfirmDialog(this, "yakin akan hapus data ini?", "Warning", 0);
                    if (dialogResult == JOptionPane.YES_OPTION) {
                        Utility.db.getConnection().setAutoCommit(false);
                        //balikin no box yang terpakai di lp suwir
                        String update_box = "UPDATE `tb_box_bahan_jadi` SET `status_terakhir`='delete lp suwir',"
                                + "`lokasi_terakhir`='GRADING',`tgl_proses_terakhir`=CURRENT_DATE WHERE `no_box` IN (SELECT `no_box` FROM `tb_lp_suwir_detail` WHERE `no_lp_suwir` = '" + no_lp_kaki + "')";
                        Utility.db.getConnection().createStatement();
                        Utility.db.getStatement().executeUpdate(update_box);
                        //delete detail LP suwir
                        String delete_detail = "DELETE FROM `tb_lp_suwir_detail` WHERE `no_lp_suwir` =  '" + no_lp_kaki + "'";
                        Utility.db.getConnection().createStatement();
                        Utility.db.getStatement().executeUpdate(delete_detail);
                        //delete LP suwir
                        String delete_lp = "DELETE FROM `tb_lp_suwir` WHERE `no_lp_suwir` = '" + no_lp_kaki + "'";
                        Utility.db.getConnection().createStatement();
                        Utility.db.getStatement().executeUpdate(delete_lp);

                        Utility.db.getConnection().commit();
                        JOptionPane.showMessageDialog(this, "Berhasil hapus data !");
                        refreshTable_LP_suwir();
                    }
                }
            } catch (Exception e) {
                try {
                    Utility.db.getConnection().rollback();
                } catch (SQLException ex) {
                    Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
                }
                JOptionPane.showMessageDialog(this, e);
                Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, e);
            } finally {
                try {
                    Utility.db.getConnection().setAutoCommit(true);
                } catch (SQLException ex) {
                    Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih No LP yang akan di hapus");
        }
    }//GEN-LAST:event_button_edit_lp_suwir1ActionPerformed

    private void button_print_label2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_print_label2ActionPerformed
        // TODO add your handling code here:
        refreshTable_DataBox();
        try {
            String no_box = "";
            for (int i = 0; i < table_dataBox.getRowCount(); i++) {
                if (i != 0) {
                    no_box = no_box + ", ";
                }
                no_box = no_box + "'" + table_dataBox.getValueAt(i, 0).toString() + "'";
            }
            String query = "SELECT `no_box_ct1` AS 'no_box', `tanggal_box`, `keping`, `berat`, `kode_grade`, `tb_box_bahan_jadi`.`kode_rsb`, `batch_number`  "
                    + "FROM `tb_box_bahan_jadi`\n"
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode` "
                    + "LEFT JOIN `tb_dokumen_kh` ON `tb_box_bahan_jadi`.`kode_kh` = `tb_dokumen_kh`.`kode_kh` \n"
                    + "LEFT JOIN `tb_box_packing` ON `tb_box_bahan_jadi`.`no_box` = `tb_box_packing`.`no_box` \n"
                    + "WHERE `tb_box_bahan_jadi`.`no_box` IN (" + no_box + ")";
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(query);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Label_Box_QR_Packing.jrxml");
            JASP_DESIGN.setQuery(newQuery);
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            Map<String, Object> params = new HashMap<>();
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, params, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);
        } catch (JRException ex) {
            Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_print_label2ActionPerformed

    private void button_edit_memo_boxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_memo_boxActionPerformed
        // TODO add your handling code here:
        try {
            int j = table_dataBox.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih no box yang akan di edit !");
            } else {
                String no_box = table_dataBox.getValueAt(j, 0).toString();
                String memo_lama = table_dataBox.getValueAt(j, 16) != null ? table_dataBox.getValueAt(j, 16).toString() : "";
                String memo_baru = JOptionPane.showInputDialog("Memo : ", memo_lama);
                if (memo_baru != null) {
                    String Query = "UPDATE `tb_box_bahan_jadi` SET `memo_box_bj`='" + memo_baru + "' WHERE `no_box` = '" + no_box + "'";
                    if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                        JOptionPane.showMessageDialog(this, "Edit memo berhasil!");
                        refreshTable_DataBox();
                    } else {
                        JOptionPane.showMessageDialog(this, "Edit memo GAGAL!");
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_edit_memo_boxActionPerformed

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
            Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_Print_LP_SWRActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_Search_BentukGrade;
    private javax.swing.JComboBox<String> ComboBox_Search_Grade;
    private javax.swing.JComboBox<String> ComboBox_lokasi;
    private com.toedter.calendar.JDateChooser Date_Search_LPsuwir_1;
    private com.toedter.calendar.JDateChooser Date_Search_LPsuwir_2;
    private com.toedter.calendar.JDateChooser Date_Search_Repacking1;
    private com.toedter.calendar.JDateChooser Date_Search_Repacking2;
    private com.toedter.calendar.JDateChooser Date_Search_keluar1;
    private com.toedter.calendar.JDateChooser Date_Search_keluar2;
    private com.toedter.calendar.JDateChooser Date_barang_dipinjam1;
    private com.toedter.calendar.JDateChooser Date_barang_dipinjam2;
    private com.toedter.calendar.JDateChooser Date_box1;
    private com.toedter.calendar.JDateChooser Date_box2;
    private com.toedter.calendar.JDateChooser Date_kinerja1;
    private com.toedter.calendar.JDateChooser Date_kinerja2;
    private com.toedter.calendar.JDateChooser Date_proses_terakhir1;
    private com.toedter.calendar.JDateChooser Date_proses_terakhir2;
    public javax.swing.JButton button_Belum_Repack;
    private javax.swing.JButton button_Print_LP_SWR;
    private javax.swing.JButton button_delete_kinerja;
    private javax.swing.JButton button_edit_kinerja;
    public javax.swing.JButton button_edit_lp_suwir;
    public javax.swing.JButton button_edit_lp_suwir1;
    private javax.swing.JButton button_edit_memo_box;
    public javax.swing.JButton button_edit_repacking;
    public javax.swing.JButton button_editgram;
    private javax.swing.JButton button_export_AsalBox;
    private javax.swing.JButton button_export_BoxReproses;
    private javax.swing.JButton button_export_LPSuwir;
    private javax.swing.JButton button_export_LP_F2;
    private javax.swing.JButton button_export_asalRepacking;
    private javax.swing.JButton button_export_data_box;
    private javax.swing.JButton button_export_hasilRepacking;
    private javax.swing.JButton button_insert_kinerja;
    public javax.swing.JButton button_lp_suwir;
    private javax.swing.JButton button_new_spk_se;
    public javax.swing.JButton button_out;
    private javax.swing.JButton button_print_label1;
    private javax.swing.JButton button_print_label2;
    private javax.swing.JButton button_print_label_packing;
    private javax.swing.JButton button_print_repacking;
    public javax.swing.JButton button_rePacking;
    public javax.swing.JButton button_reProcess;
    public static javax.swing.JButton button_search_Box;
    private javax.swing.JButton button_search_keluar;
    private javax.swing.JButton button_search_kinerja;
    private javax.swing.JButton button_search_lp;
    private javax.swing.JButton button_search_pinjam_barang_jadi;
    private javax.swing.JButton button_search_repacking;
    public javax.swing.JButton button_selesai_repacking;
    private javax.swing.JButton button_set_cheat_no_box;
    private javax.swing.JButton button_set_rsb;
    public javax.swing.JButton button_setor_Packing;
    public javax.swing.JButton button_terima_retur;
    public javax.swing.JButton button_treatment;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
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
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel_Data_Box;
    private javax.swing.JPanel jPanel_data_LP_suwir;
    private javax.swing.JPanel jPanel_data_keluar;
    private javax.swing.JPanel jPanel_data_kinerja_gbj;
    private javax.swing.JPanel jPanel_data_pinjam_barangJadi;
    private javax.swing.JPanel jPanel_data_rePacking;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JLabel label_BoxReproses_lpSuwir1;
    private javax.swing.JLabel label_LaporanProduksi_lpSuwir;
    private javax.swing.JLabel label_asalBox_lpSuwir;
    private javax.swing.JLabel label_avg_sh_repacking;
    private javax.swing.JLabel label_kode_repacking1;
    private javax.swing.JLabel label_kode_repacking2;
    private javax.swing.JLabel label_total_BoxRepacking_lpsuwir;
    private javax.swing.JLabel label_total_LaporanProduksi_lpsuwir;
    private javax.swing.JLabel label_total_asalBox_lpSuwir;
    private javax.swing.JLabel label_total_asal_repacking;
    private javax.swing.JLabel label_total_box_dipinjam;
    private javax.swing.JLabel label_total_data_box;
    private javax.swing.JLabel label_total_data_kinerja;
    private javax.swing.JLabel label_total_gram_BoxRepracking_lpsuwir;
    private javax.swing.JLabel label_total_gram_LaporanProduksi_lpsuwir;
    private javax.swing.JLabel label_total_gram_asalBox_lpsuwir;
    private javax.swing.JLabel label_total_gram_asal_repacking;
    private javax.swing.JLabel label_total_gram_data_box;
    private javax.swing.JLabel label_total_gram_dikerjakan_kinerjaGBJ;
    private javax.swing.JLabel label_total_gram_dipinjam;
    private javax.swing.JLabel label_total_gram_hasil_repacking;
    private javax.swing.JLabel label_total_gram_keluar;
    private javax.swing.JLabel label_total_gram_lp_kinerjaGBJ;
    private javax.swing.JLabel label_total_hasil_repacking;
    private javax.swing.JLabel label_total_keluar;
    private javax.swing.JLabel label_total_keluar_f2;
    private javax.swing.JLabel label_total_keping_asalBox_lpsuwir;
    private javax.swing.JLabel label_total_keping_asal_repacking;
    private javax.swing.JLabel label_total_keping_dikerjakan_kinerjaGBJ;
    private javax.swing.JLabel label_total_keping_dipinjam;
    private javax.swing.JLabel label_total_keping_hasil_repacking;
    private javax.swing.JLabel label_total_keping_keluar;
    private javax.swing.JLabel label_total_keping_lp_kinerjaGBJ;
    private javax.swing.JLabel label_total_kpg_data_box;
    private javax.swing.JLabel label_total_lpsuwir;
    private javax.swing.JLabel label_total_repacking;
    private javax.swing.JLabel label_total_stok;
    private javax.swing.JLabel label_total_stok1;
    private javax.swing.JTable table_BoxReprosesi_LPSuwir;
    private javax.swing.JTable table_LaporanProduksi_LPSuwir;
    private javax.swing.JTable table_asalBox_lpSuwir;
    private javax.swing.JTable table_asal_repacking;
    public static javax.swing.JTable table_dataBox;
    private javax.swing.JTable table_data_LPsuwir;
    private javax.swing.JTable table_data_keluar;
    private javax.swing.JTable table_data_kinerja_gbj;
    private javax.swing.JTable table_data_pinjam_barang_jadi;
    private javax.swing.JTable table_data_repacking;
    private javax.swing.JTable table_hasil_repacking;
    private javax.swing.JTextField txt_search_box_dipinjam;
    private javax.swing.JTextField txt_search_box_keluar;
    private javax.swing.JTextField txt_search_box_kinerja;
    private javax.swing.JTextField txt_search_id_kinerja;
    private javax.swing.JTextField txt_search_kh;
    private javax.swing.JTextField txt_search_kode_grade_spk;
    private javax.swing.JTextField txt_search_kode_repacking;
    private javax.swing.JTextField txt_search_lpsuwir;
    private javax.swing.JTextField txt_search_nama_kinerja;
    private javax.swing.JTextField txt_search_no_box;
    private javax.swing.JTextField txt_search_no_invoice;
    public static javax.swing.JTextField txt_search_no_tutupan;
    private javax.swing.JTextField txt_search_rsb;
    private javax.swing.JTextField txt_search_spk;
    // End of variables declaration//GEN-END:variables
}
