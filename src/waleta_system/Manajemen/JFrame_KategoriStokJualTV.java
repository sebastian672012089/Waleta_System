package waleta_system.Manajemen;

import java.awt.Color;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.LegendItemEntity;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import waleta_system.Class.Utility;

public class JFrame_KategoriStokJualTV extends javax.swing.JFrame {

    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public DefaultPieDataset PieChart_dataset;
    float total_jual_mk = 0, total_jual_flat = 0, total_jual_bp = 0, total_jual_pch = 0, total_jual_jdn = 0,
            total_serabut = 0, total_susah_jual = 0, total_residu = 0, total_non_ns = 0, total_non_aktif = 0, total_no_kategori = 0;
    float total_reproses_bp = 0, total_reproses = 0, total_reproses_swr = 0, total_gram_StokRepacking = 0;
//    JFreeChart chart1;

    public JFrame_KategoriStokJualTV() {
        initComponents();
    }

    public void init(int show_value) {
        PieChart();
        refreshTable_DataStok();
        refreshTable_StockRepacking();
        refreshTable_MLEM();
        refreshTable_WIP_Reproses();
        refresh_JAM();
        if (show_value == 0) {
            jLabel4.setVisible(false);
            label_total_harga_cny.setVisible(false);
            jLabel5.setVisible(false);
            label_kurs.setVisible(false);
            jLabel18.setVisible(false);
            label_total_harga_idr.setVisible(false);
        } else if (show_value == 1) {
            jLabel4.setVisible(true);
            label_total_harga_cny.setVisible(true);
            jLabel5.setVisible(true);
            label_kurs.setVisible(true);
            jLabel18.setVisible(true);
            label_total_harga_idr.setVisible(true);
        }
    }

    public void refresh_JAM() {
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        String formattedDate = myDateObj.format(myFormatObj);
        label_jam.setText(formattedDate);
    }

    public void refreshTable_DataStok() {
        try {
            int total_gram_NonBox = 0;
            float total_gram_StokBox = 0, total_nilai_bjd = 0;
            total_reproses_bp = 0;
            total_reproses = 0;
            total_reproses_swr = 0;
            total_gram_StokRepacking = 0;
            DefaultTableModel MODEL_JUAL_MK = (DefaultTableModel) tabel_JUAL_MK.getModel();
            DefaultTableModel MODEL_JUAL_FLAT = (DefaultTableModel) tabel_JUAL_FLAT.getModel();
            DefaultTableModel MODEL_JUAL_BP = (DefaultTableModel) tabel_JUAL_BP.getModel();
            DefaultTableModel MODEL_JUAL_PCH = (DefaultTableModel) tabel_JUAL_PCH.getModel();
            DefaultTableModel MODEL_JUAL_JDN = (DefaultTableModel) tabel_JUAL_JDN.getModel();
            DefaultTableModel MODEL_REPROSES_BP = (DefaultTableModel) tabel_REPROSES_BP.getModel();
            DefaultTableModel MODEL_REPROSES = (DefaultTableModel) tabel_REPROSES.getModel();
            DefaultTableModel MODEL_REPROSES_SWR = (DefaultTableModel) tabel_REPROSES_SWR.getModel();
            DefaultTableModel MODEL_SERABUT = (DefaultTableModel) tabel_SERABUT.getModel();
            DefaultTableModel MODEL_SUSAH_JUAL = (DefaultTableModel) tabel_SUSAH_JUAL.getModel();
            DefaultTableModel MODEL_RESIDU = (DefaultTableModel) tabel_RESIDU.getModel();
            MODEL_JUAL_MK.setRowCount(0);
            MODEL_JUAL_FLAT.setRowCount(0);
            MODEL_JUAL_BP.setRowCount(0);
            MODEL_JUAL_PCH.setRowCount(0);
            MODEL_JUAL_JDN.setRowCount(0);
            MODEL_REPROSES_BP.setRowCount(0);
            MODEL_REPROSES.setRowCount(0);
            MODEL_REPROSES_SWR.setRowCount(0);
            MODEL_SERABUT.setRowCount(0);
            MODEL_SUSAH_JUAL.setRowCount(0);
            MODEL_RESIDU.setRowCount(0);

            sql = "SELECT `tb_grade_bahan_jadi`.`kode_grade`, SUM(`tb_grading_bahan_jadi`.`gram`) AS 'gram'  "
                    + "FROM `tb_grading_bahan_jadi` "
                    + "LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_grading_bahan_jadi`.`kode_asal_bahan_jadi` = `tb_bahan_jadi_masuk`.`kode_asal` "
                    + "LEFT JOIN `tb_tutupan_grading` ON `tb_bahan_jadi_masuk`.`kode_tutupan` = `tb_tutupan_grading`.`kode_tutupan` "
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_grading_bahan_jadi`.`grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode` "
                    + "WHERE (`tb_tutupan_grading`.`status_box` = 'PROSES' OR `tb_tutupan_grading`.`status_box` IS NULL) AND `tanggal_grading` IS NOT NULL "
                    + "GROUP BY `tb_grade_bahan_jadi`.`kode_grade`";
            ArrayList<String> grade = new ArrayList<>();
            ArrayList<Float> gram = new ArrayList<>();
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                grade.add(rs.getString("kode_grade"));
                gram.add(rs.getFloat("gram"));
                total_gram_NonBox = total_gram_NonBox + rs.getInt("gram");
            }

            Object[] row = new Object[6];
            sql = "SELECT `tb_grade_bahan_jadi`.`kode`, `tb_grade_bahan_jadi`.`kode_grade`, SUM(`berat`) AS 'berat', `harga`, `kategori_jual` "
                    + "FROM `tb_grade_bahan_jadi` "
                    + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_grade_bahan_jadi`.`kode` = `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` "
                    + "LEFT JOIN `tb_tutupan_grading` ON `tb_box_bahan_jadi`.`no_tutupan` = `tb_tutupan_grading`.`kode_tutupan`"
                    + "WHERE `lokasi_terakhir` = 'GRADING' "
                    + "AND (`tb_tutupan_grading`.`status_box` = 'SELESAI' OR `tb_tutupan_grading`.`status_box` IS NULL)"
                    + "GROUP BY `tb_grade_bahan_jadi`.`kode` ORDER BY `kode_grade` ASC";
            pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            int i = 0;
            while (rs.next()) {
                i++;
                row[0] = rs.getString("kode_grade").substring(4);
                int index = grade.indexOf(rs.getString("kode_grade"));
                float gram_StokNonBox = 0;
                if (index > -1) {
                    gram_StokNonBox = gram.get(index);
                    row[1] = gram_StokNonBox;
                } else {
                    row[1] = 0;
                }

                float gram_StokBox = rs.getInt("berat");
                row[2] = gram_StokBox;
                total_gram_StokBox = total_gram_StokBox + gram_StokBox + gram_StokNonBox;
                total_nilai_bjd = total_nilai_bjd + ((gram_StokBox + gram_StokNonBox) * (rs.getFloat("harga") * 0.98f / 1000.f));

                if (rs.getString("kategori_jual").equals("JUAL-MK")) {
                    total_jual_mk = total_jual_mk + gram_StokBox + gram_StokNonBox;
                    MODEL_JUAL_MK.addRow(row);
                } else if (rs.getString("kategori_jual").equals("JUAL-FLAT")) {
                    total_jual_flat = total_jual_flat + gram_StokBox + gram_StokNonBox;
                    MODEL_JUAL_FLAT.addRow(row);
                } else if (rs.getString("kategori_jual").equals("JUAL-BP")) {
                    total_jual_bp = total_jual_bp + gram_StokBox + gram_StokNonBox;
                    MODEL_JUAL_BP.addRow(row);
                } else if (rs.getString("kategori_jual").equals("JUAL-PECAH")) {
                    total_jual_pch = total_jual_pch + gram_StokBox + gram_StokNonBox;
                    MODEL_JUAL_PCH.addRow(row);
                } else if (rs.getString("kategori_jual").equals("JUAL-JDN")) {
                    total_jual_jdn = total_jual_jdn + gram_StokBox + gram_StokNonBox;
                    MODEL_JUAL_JDN.addRow(row);
                } else if (rs.getString("kategori_jual").equals("REPROSES BP")) {
                    total_reproses_bp = total_reproses_bp + gram_StokBox + gram_StokNonBox;
                    MODEL_REPROSES_BP.addRow(row);
                } else if (rs.getString("kategori_jual").equals("REPROSES")) {
                    total_reproses = total_reproses + gram_StokBox + gram_StokNonBox;
                    MODEL_REPROSES.addRow(row);
                } else if (rs.getString("kategori_jual").equals("REPROSES SWR")) {
                    total_reproses_swr = total_reproses_swr + gram_StokBox + gram_StokNonBox;
                    MODEL_REPROSES_SWR.addRow(row);
                } else if (rs.getString("kategori_jual").equals("RESIDU")) {
                    total_residu = total_residu + gram_StokBox + gram_StokNonBox;
                    MODEL_RESIDU.addRow(row);
                } else if (rs.getString("kategori_jual").equals("SUSAH JUAL")) {
                    total_susah_jual = total_susah_jual + gram_StokBox + gram_StokNonBox;
                    MODEL_SUSAH_JUAL.addRow(row);
                } else if (rs.getString("kategori_jual").equals("SERABUT")) {
                    total_serabut = total_serabut + gram_StokBox + gram_StokNonBox;
                    MODEL_SERABUT.addRow(row);
                } else if (rs.getString("kategori_jual").equals("NON NS")) {
                    total_non_ns = total_non_ns + gram_StokBox + gram_StokNonBox;
                } else if (rs.getString("kategori_jual").equals("NON-AKTIF")) {
                    total_non_aktif = total_non_aktif + gram_StokBox + gram_StokNonBox;
                } else {
                    total_no_kategori = total_no_kategori + gram_StokBox + gram_StokNonBox;
                }
            }

            //GET KURS TERAKHIR
            float kurs = 0;
            sql = "SELECT `tanggal`, `nilai` FROM `tb_kurs` WHERE 1 ORDER BY `tb_kurs`.`tanggal` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                kurs = rs.getFloat("nilai");
            }

            decimalFormat.setMaximumFractionDigits(0);
            label_total_harga_cny.setText("¥" + decimalFormat.format(total_nilai_bjd));
            label_kurs.setText(decimalFormat.format(kurs));
            label_total_harga_idr.setText("Rp. " + decimalFormat.format(total_nilai_bjd * kurs));
            label_total_gram_StokBox.setText(decimalFormat.format((total_gram_StokBox - total_residu) / 1000.f) + " Kg");
            label_total_jual_mk.setText(decimalFormat.format(total_jual_mk / 1000.f) + " Kg");
            label_total_jual_flat.setText(decimalFormat.format(total_jual_flat / 1000.f) + " Kg");
            label_total_jual_bp.setText(decimalFormat.format(total_jual_bp / 1000.f) + " Kg");
            label_total_jual_pch.setText(decimalFormat.format(total_jual_pch / 1000.f) + " Kg");
            label_total_jual_jdn.setText(decimalFormat.format(total_jual_jdn / 1000.f) + " Kg");
            label_total_reproses_bp.setText(decimalFormat.format(total_reproses_bp / 1000.f) + " Kg");
            label_total_reproses.setText(decimalFormat.format(total_reproses / 1000.f) + " Kg");
            label_total_reproses_swr.setText(decimalFormat.format(total_reproses_swr / 1000.f) + " Kg");
            label_total_residu.setText(decimalFormat.format(total_residu / 1000.f) + " Kg");
            label_total_susah_jual.setText(decimalFormat.format(total_susah_jual / 1000.f) + " Kg");
            label_total_serabut.setText(decimalFormat.format(total_serabut / 1000.f) + " Kg");
            label_total_non_ns.setText(decimalFormat.format(total_non_ns / 1000.f) + " Kg");
            label_total_non_aktif.setText(decimalFormat.format(total_non_aktif / 1000.f) + " Kg");
//            label_total_no_kategori.setText(decimalFormat.format(total_no_kategori / 1000.f) + " Kg");

            PieChart_dataset.setValue("JUAL", (total_jual_mk + total_jual_flat + total_jual_bp + total_jual_pch + total_jual_jdn));
            PieChart_dataset.setValue("REPROSES", (total_reproses_bp + total_reproses + total_reproses_swr));
//            PieChart_dataset.setValue("RESIDU", total_residu);
            PieChart_dataset.setValue("SUSAH JUAL", total_susah_jual);
            PieChart_dataset.setValue("SERABUT", total_serabut);
            PieChart_dataset.setValue("NON NS & NON AKTIF", total_non_ns + total_non_aktif);
//            PieChart_dataset.setValue("NON NS", total_non_ns);
//            PieChart_dataset.setValue("NON AKTIF", total_non_aktif);
//            PieChart_dataset.setValue("NO KATEGORI", total_no_kategori);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JFrame_KategoriStokJualTV.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_StockRepacking() {
        try {
            DefaultTableModel model = (DefaultTableModel) tabel_REPACKING.getModel();

            model.setRowCount(0);
            Object[] row = new Object[3];
            sql = "SELECT `kode_grade`, `kategori_jual`, SUM(`tb_box_bahan_jadi`.`berat`) AS 'berat' "
                    + "FROM `tb_box_bahan_jadi` "
                    + "LEFT JOIN `tb_reproses` ON `tb_reproses`.`no_box` = `tb_box_bahan_jadi`.`no_box`\n"
                    + "LEFT JOIN `tb_lab_barang_jadi` ON `tb_lab_barang_jadi`.`no_box` = `tb_box_bahan_jadi`.`no_box`\n"
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`"
                    + "WHERE `tb_reproses`.`status` = 'FINISHED' AND `lokasi_terakhir` = 'GRADING' "
                    + "GROUP BY `kode_grade` ORDER BY `kategori_jual` DESC";
//            System.out.println(sql);
            pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                row[0] = rs.getString("kode_grade").substring(4);
                row[1] = rs.getString("kategori_jual");
                row[2] = rs.getFloat("berat");
                total_gram_StokRepacking = total_gram_StokRepacking + rs.getInt("berat");
                model.addRow(row);

                switch (rs.getString("kategori_jual")) {
                    case "JUAL-MK":
                        for (int i = 0; i < tabel_JUAL_MK.getRowCount(); i++) {
                            if (tabel_JUAL_MK.getValueAt(i, 0).toString().equals(rs.getString("kode_grade").substring(4))) {
                                float sisa = (float) tabel_JUAL_MK.getValueAt(i, 2) - rs.getFloat("berat");
                                tabel_JUAL_MK.setValueAt(sisa, i, 2);
                                total_jual_mk = total_jual_mk - rs.getFloat("berat");
                            }
                        }
                        break;
                    case "JUAL-FLAT":
                        for (int i = 0; i < tabel_JUAL_FLAT.getRowCount(); i++) {
                            if (tabel_JUAL_FLAT.getValueAt(i, 0).toString().equals(rs.getString("kode_grade").substring(4))) {
                                float sisa = (float) tabel_JUAL_FLAT.getValueAt(i, 2) - rs.getFloat("berat");
                                tabel_JUAL_FLAT.setValueAt(sisa, i, 2);
                                total_jual_flat = total_jual_flat - rs.getFloat("berat");
                            }
                        }
                        break;
                    case "JUAL-BP":
                        for (int i = 0; i < tabel_JUAL_BP.getRowCount(); i++) {
                            if (tabel_JUAL_BP.getValueAt(i, 0).toString().equals(rs.getString("kode_grade").substring(4))) {
                                float sisa = (float) tabel_JUAL_BP.getValueAt(i, 2) - rs.getFloat("berat");
                                tabel_JUAL_BP.setValueAt(sisa, i, 2);
                                total_jual_bp = total_jual_bp - rs.getFloat("berat");
                            }
                        }
                        break;
                    case "JUAL-PECAH":
                        for (int i = 0; i < tabel_JUAL_PCH.getRowCount(); i++) {
                            if (tabel_JUAL_PCH.getValueAt(i, 0).toString().equals(rs.getString("kode_grade").substring(4))) {
                                float sisa = (float) tabel_JUAL_PCH.getValueAt(i, 2) - rs.getFloat("berat");
                                tabel_JUAL_PCH.setValueAt(sisa, i, 2);
                                total_jual_pch = total_jual_pch - rs.getFloat("berat");
                            }
                        }
                        break;
                    case "JUAL-JDN":
                        for (int i = 0; i < tabel_JUAL_JDN.getRowCount(); i++) {
                            if (tabel_JUAL_JDN.getValueAt(i, 0).toString().equals(rs.getString("kode_grade").substring(4))) {
                                float sisa = (float) tabel_JUAL_JDN.getValueAt(i, 2) - rs.getFloat("berat");
                                tabel_JUAL_JDN.setValueAt(sisa, i, 2);
                                total_jual_jdn = total_jual_jdn - rs.getFloat("berat");
                            }
                        }
                        break;
                    case "REPROSES BP":
                        for (int i = 0; i < tabel_REPROSES_BP.getRowCount(); i++) {
                            if (tabel_REPROSES_BP.getValueAt(i, 0).toString().equals(rs.getString("kode_grade").substring(4))) {
                                float sisa = (float) tabel_REPROSES_BP.getValueAt(i, 2) - rs.getFloat("berat");
                                tabel_REPROSES_BP.setValueAt(sisa, i, 2);
                                total_reproses_bp = total_reproses_bp - rs.getFloat("berat");
                            }
                        }
                        break;
                    case "REPROSES":
                        for (int i = 0; i < tabel_REPROSES.getRowCount(); i++) {
                            if (tabel_REPROSES.getValueAt(i, 0).toString().equals(rs.getString("kode_grade").substring(4))) {
                                float sisa = (float) tabel_REPROSES.getValueAt(i, 2) - rs.getFloat("berat");
                                tabel_REPROSES.setValueAt(sisa, i, 2);
                                total_reproses = total_reproses - rs.getFloat("berat");
                            }
                        }
                        break;
                    case "REPROSES SWR":
                        for (int i = 0; i < tabel_REPROSES_SWR.getRowCount(); i++) {
                            if (tabel_REPROSES_SWR.getValueAt(i, 0).toString().equals(rs.getString("kode_grade").substring(4))) {
                                float sisa = (float) tabel_REPROSES_SWR.getValueAt(i, 2) - rs.getFloat("berat");
                                tabel_REPROSES_SWR.setValueAt(sisa, i, 2);
                                total_reproses_swr = total_reproses_swr - rs.getFloat("berat");
                            }
                        }
                        break;
                    case "RESIDU":
                        for (int i = 0; i < tabel_RESIDU.getRowCount(); i++) {
                            if (tabel_RESIDU.getValueAt(i, 0).toString().equals(rs.getString("kode_grade").substring(4))) {
                                float sisa = (float) tabel_RESIDU.getValueAt(i, 2) - rs.getFloat("berat");
                                tabel_RESIDU.setValueAt(sisa, i, 2);
                                total_residu = total_residu - rs.getFloat("berat");
                            }
                        }
                        break;
                    case "SUSAH JUAL":
                        for (int i = 0; i < tabel_SUSAH_JUAL.getRowCount(); i++) {
                            if (tabel_SUSAH_JUAL.getValueAt(i, 0).toString().equals(rs.getString("kode_grade").substring(4))) {
                                float sisa = (float) tabel_SUSAH_JUAL.getValueAt(i, 2) - rs.getFloat("berat");
                                tabel_SUSAH_JUAL.setValueAt(sisa, i, 2);
                                total_susah_jual = total_susah_jual - rs.getFloat("berat");
                            }
                        }
                        break;
                    case "SERABUT":
                        for (int i = 0; i < tabel_SERABUT.getRowCount(); i++) {
                            if (tabel_SERABUT.getValueAt(i, 0).toString().equals(rs.getString("kode_grade").substring(4))) {
                                float sisa = (float) tabel_SERABUT.getValueAt(i, 2) - rs.getFloat("berat");
                                tabel_SERABUT.setValueAt(sisa, i, 2);
                                total_serabut = total_serabut - rs.getFloat("berat");
                            }
                        }
                        break;
                    default:
                        break;
                }
            }

            label_total_jual_mk.setText(decimalFormat.format(total_jual_mk / 1000.f) + " Kg");
            label_total_jual_flat.setText(decimalFormat.format(total_jual_flat / 1000.f) + " Kg");
            label_total_jual_bp.setText(decimalFormat.format(total_jual_bp / 1000.f) + " Kg");
            label_total_jual_pch.setText(decimalFormat.format(total_jual_pch / 1000.f) + " Kg");
            label_total_jual_jdn.setText(decimalFormat.format(total_jual_jdn / 1000.f) + " Kg");
            label_total_reproses_bp.setText(decimalFormat.format(total_reproses_bp / 1000.f) + " Kg");
            label_total_reproses.setText(decimalFormat.format(total_reproses / 1000.f) + " Kg");
            label_total_reproses_swr.setText(decimalFormat.format(total_reproses_swr / 1000.f) + " Kg");
            label_total_residu.setText(decimalFormat.format(total_residu / 1000.f) + " Kg");
            label_total_susah_jual.setText(decimalFormat.format(total_susah_jual / 1000.f) + " Kg");
            label_total_serabut.setText(decimalFormat.format(total_serabut / 1000.f) + " Kg");
            label_total_non_ns.setText(decimalFormat.format(total_non_ns / 1000.f) + " Kg");
            label_total_non_aktif.setText(decimalFormat.format(total_non_aktif / 1000.f) + " Kg");
            label_total_repacking.setText(decimalFormat.format(total_gram_StokRepacking / 1000.f) + " Kg");
            PieChart_dataset.setValue("REPROSES", (total_reproses_bp + total_reproses + total_reproses_swr));

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JFrame_KategoriStokJualTV.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_MLEM() {
        try {
            int total_gram_mlem = 0;
            DefaultTableModel MODEL_TABEL_MLEM = (DefaultTableModel) tabel_MLEM.getModel();
            MODEL_TABEL_MLEM.setRowCount(0);
            sql = "SELECT `lokasi_terakhir`,  SUM(IF(`no_tutupan` LIKE 'GRD%', `berat`, 0)) AS 'GRD', SUM(IF(`no_tutupan` LIKE 'RPK%', `berat`, 0)) AS 'RPK'\n"
                    + "FROM `tb_box_bahan_jadi` \n"
                    + "LEFT JOIN `tb_tutupan_grading` ON `tb_box_bahan_jadi`.`no_tutupan` = `tb_tutupan_grading`.`kode_tutupan`\n"
                    + "LEFT JOIN `tb_box_packing` ON `tb_box_bahan_jadi`.`no_box` = `tb_box_packing`.`no_box`\n"
                    + "WHERE `kode_grade_bahan_jadi` = '033'\n"
                    + " AND `lokasi_terakhir` IN ('GRADING', 'PACKING', 'RE-PROSES', 'TREATMENT', 'DIPINJAM') AND (`tb_box_packing`.`status` IN ('PENDING', 'STOK', 'RETUR') OR `tb_box_packing`.`status` IS NULL)"
                    + " AND (`tb_tutupan_grading`.`status_box` = 'SELESAI' OR `tb_tutupan_grading`.`status_box` IS NULL) "
                    + " GROUP BY `lokasi_terakhir`";
//            System.out.println(sql);
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[3];
            while (rs.next()) {
                row[0] = rs.getString("lokasi_terakhir");
                row[1] = rs.getInt("GRD");
                row[2] = rs.getInt("RPK");
                MODEL_TABEL_MLEM.addRow(row);
                total_gram_mlem = total_gram_mlem + rs.getInt("GRD") + rs.getInt("RPK");
            }
            decimalFormat.setMaximumFractionDigits(0);
            label_total_mlem.setText(decimalFormat.format(total_gram_mlem / 1000.f) + " Kg");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JFrame_KategoriStokJualTV.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_WIP_Reproses() {
        try {
            double total_gram_wip_reproses = 0;
            DefaultTableModel model = (DefaultTableModel) tabel_WIP_REPROSES.getModel();
            model.setRowCount(0);
            Object[] row = new Object[3];
            sql = "SELECT `kode_grade`, `kategori_jual`, SUM(`tb_box_bahan_jadi`.`berat`) AS 'berat' "
                    + "FROM `tb_box_bahan_jadi` "
                    + "LEFT JOIN `tb_reproses` ON `tb_reproses`.`no_box` = `tb_box_bahan_jadi`.`no_box`\n"
                    + "LEFT JOIN `tb_lab_barang_jadi` ON `tb_lab_barang_jadi`.`no_box` = `tb_box_bahan_jadi`.`no_box`\n"
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`"
                    + "WHERE `lokasi_terakhir` = 'RE-PROSES' "
                    + "GROUP BY `kode_grade` "
                    + "ORDER BY `kategori_jual` DESC";
            pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                row[0] = rs.getString("kode_grade").substring(4);
                row[1] = rs.getString("kategori_jual");
                row[2] = rs.getFloat("berat");
                total_gram_wip_reproses = total_gram_wip_reproses + rs.getInt("berat");
                model.addRow(row);
            }
            label_total_wip_reproses.setText(decimalFormat.format(total_gram_wip_reproses / 1000.f) + " Kg");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JFrame_KategoriStokJualTV.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void PieChart() {
        PieChart_dataset = new DefaultPieDataset();
        JFreeChart chart7 = ChartFactory.createPieChart("Stok Barang jadi", PieChart_dataset, true, true, false);
        chart7.setBackgroundPaint(Color.WHITE);
        chart7.getTitle().setPaint(Color.red);
        PiePlot pp2 = (PiePlot) chart7.getPlot();
        pp2.setSectionPaint("JUAL", Color.cyan);
        pp2.setSectionPaint("REPROSES", Color.orange);
        pp2.setSectionPaint("RESIDU", Color.GREEN);
        pp2.setSectionPaint("SUSAH JUAL", Color.red);
        pp2.setSectionPaint("SERABUT", new Color(44, 199, 204));
        pp2.setSectionPaint("NON NS & NON AKTIF", Color.black);
        ChartPanel panelChartStokBahanBaku = new ChartPanel(chart7);
        panelChartStokBahanBaku.setLocation(0, 0);
        panelChartStokBahanBaku.setSize(Panel_Pie_Chart.getWidth(), Panel_Pie_Chart.getHeight());
        Panel_Pie_Chart.add(panelChartStokBahanBaku);
        panelChartStokBahanBaku.addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseClicked(ChartMouseEvent cme) {
                if (cme.getEntity() != null) {
                    if (!(cme.getEntity() instanceof LegendItemEntity)) {
                        PieSectionEntity entity = (PieSectionEntity) cme.getEntity();
                        label_detail_chart.setText(entity.getToolTipText());
//                        float total = 0;
//                        for (int i = 0; i < PieChart_dataset.getItemCount(); i++) {
//                            total = total + entity.getDataset().getValue(i).intValue();
//                        }
//                        System.out.println(total);
                    }
                }
            }

            @Override
            public void chartMouseMoved(ChartMouseEvent cme) {

            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        label_total_gram_StokBox = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        Button_Refresh = new javax.swing.JButton();
        label_jam = new javax.swing.JLabel();
        Panel_Pie_Chart = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_JUAL_MK = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabel_JUAL_FLAT = new javax.swing.JTable();
        jLabel6 = new javax.swing.JLabel();
        label_total_jual_mk = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        label_total_jual_flat = new javax.swing.JLabel();
        label_total_jual_bp = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabel_JUAL_BP = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        label_total_jual_pch = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tabel_JUAL_PCH = new javax.swing.JTable();
        jScrollPane6 = new javax.swing.JScrollPane();
        tabel_JUAL_JDN = new javax.swing.JTable();
        jLabel10 = new javax.swing.JLabel();
        label_total_jual_jdn = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        tabel_SUSAH_JUAL = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        label_total_susah_jual = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        tabel_REPROSES_SWR = new javax.swing.JTable();
        jLabel12 = new javax.swing.JLabel();
        label_total_reproses_bp = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        tabel_SERABUT = new javax.swing.JTable();
        jLabel13 = new javax.swing.JLabel();
        label_total_serabut = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        label_total_residu = new javax.swing.JLabel();
        jScrollPane10 = new javax.swing.JScrollPane();
        tabel_RESIDU = new javax.swing.JTable();
        jLabel15 = new javax.swing.JLabel();
        label_total_non_ns = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        label_total_non_aktif = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        label_total_harga_cny = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        label_kurs = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        label_total_harga_idr = new javax.swing.JLabel();
        label_detail_chart = new javax.swing.JLabel();
        jScrollPane11 = new javax.swing.JScrollPane();
        tabel_REPACKING = new javax.swing.JTable();
        jLabel16 = new javax.swing.JLabel();
        label_total_repacking = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        label_total_mlem = new javax.swing.JLabel();
        jScrollPane12 = new javax.swing.JScrollPane();
        tabel_MLEM = new javax.swing.JTable();
        jScrollPane13 = new javax.swing.JScrollPane();
        tabel_REPROSES_BP = new javax.swing.JTable();
        jScrollPane14 = new javax.swing.JScrollPane();
        tabel_REPROSES = new javax.swing.JTable();
        jLabel20 = new javax.swing.JLabel();
        label_total_reproses = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        label_total_reproses_swr = new javax.swing.JLabel();
        label_total_wip_reproses = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jScrollPane15 = new javax.swing.JScrollPane();
        tabel_WIP_REPROSES = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        label_total_gram_StokBox.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_StokBox.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        label_total_gram_StokBox.setForeground(new java.awt.Color(255, 0, 0));
        label_total_gram_StokBox.setText("0");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel3.setText("Finished Goods Inventory :");

        Button_Refresh.setBackground(new java.awt.Color(255, 255, 255));
        Button_Refresh.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        Button_Refresh.setText("REFRESH");
        Button_Refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_RefreshActionPerformed(evt);
            }
        });

        label_jam.setBackground(new java.awt.Color(255, 255, 255));
        label_jam.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_jam.setText("Jam :");

        Panel_Pie_Chart.setBackground(new java.awt.Color(255, 255, 255));
        Panel_Pie_Chart.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        javax.swing.GroupLayout Panel_Pie_ChartLayout = new javax.swing.GroupLayout(Panel_Pie_Chart);
        Panel_Pie_Chart.setLayout(Panel_Pie_ChartLayout);
        Panel_Pie_ChartLayout.setHorizontalGroup(
            Panel_Pie_ChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        Panel_Pie_ChartLayout.setVerticalGroup(
            Panel_Pie_ChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        tabel_JUAL_MK.setAutoCreateRowSorter(true);
        tabel_JUAL_MK.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        tabel_JUAL_MK.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "JUAL MK", "w/o Box", "Box"
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
        tabel_JUAL_MK.setRequestFocusEnabled(false);
        tabel_JUAL_MK.setRowSelectionAllowed(false);
        tabel_JUAL_MK.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tabel_JUAL_MK);
        if (tabel_JUAL_MK.getColumnModel().getColumnCount() > 0) {
            tabel_JUAL_MK.getColumnModel().getColumn(0).setMinWidth(80);
        }

        tabel_JUAL_FLAT.setAutoCreateRowSorter(true);
        tabel_JUAL_FLAT.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        tabel_JUAL_FLAT.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "JUAL FLAT", "w/o Box", "Box"
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
        tabel_JUAL_FLAT.setRequestFocusEnabled(false);
        tabel_JUAL_FLAT.setRowSelectionAllowed(false);
        tabel_JUAL_FLAT.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tabel_JUAL_FLAT);
        if (tabel_JUAL_FLAT.getColumnModel().getColumnCount() > 0) {
            tabel_JUAL_FLAT.getColumnModel().getColumn(0).setMinWidth(80);
        }

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel6.setText("Total JUAL MK :");

        label_total_jual_mk.setBackground(new java.awt.Color(255, 255, 255));
        label_total_jual_mk.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_total_jual_mk.setForeground(new java.awt.Color(255, 0, 0));
        label_total_jual_mk.setText("0");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel7.setText("Total JUAL FLAT :");

        label_total_jual_flat.setBackground(new java.awt.Color(255, 255, 255));
        label_total_jual_flat.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_total_jual_flat.setForeground(new java.awt.Color(255, 0, 0));
        label_total_jual_flat.setText("0");

        label_total_jual_bp.setBackground(new java.awt.Color(255, 255, 255));
        label_total_jual_bp.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_total_jual_bp.setForeground(new java.awt.Color(255, 0, 0));
        label_total_jual_bp.setText("0");

        tabel_JUAL_BP.setAutoCreateRowSorter(true);
        tabel_JUAL_BP.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        tabel_JUAL_BP.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "JUAL BP", "w/o Box", "Box"
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
        tabel_JUAL_BP.setRequestFocusEnabled(false);
        tabel_JUAL_BP.setRowSelectionAllowed(false);
        tabel_JUAL_BP.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(tabel_JUAL_BP);
        if (tabel_JUAL_BP.getColumnModel().getColumnCount() > 0) {
            tabel_JUAL_BP.getColumnModel().getColumn(0).setMinWidth(80);
        }

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel8.setText("Total JUAL BP :");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel9.setText("Total JUAL PECAH :");

        label_total_jual_pch.setBackground(new java.awt.Color(255, 255, 255));
        label_total_jual_pch.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_total_jual_pch.setForeground(new java.awt.Color(255, 0, 0));
        label_total_jual_pch.setText("0");

        tabel_JUAL_PCH.setAutoCreateRowSorter(true);
        tabel_JUAL_PCH.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        tabel_JUAL_PCH.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "JUAL BP", "w/o Box", "Box"
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
        tabel_JUAL_PCH.setRequestFocusEnabled(false);
        tabel_JUAL_PCH.setRowSelectionAllowed(false);
        tabel_JUAL_PCH.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(tabel_JUAL_PCH);
        if (tabel_JUAL_PCH.getColumnModel().getColumnCount() > 0) {
            tabel_JUAL_PCH.getColumnModel().getColumn(0).setMinWidth(80);
        }

        tabel_JUAL_JDN.setAutoCreateRowSorter(true);
        tabel_JUAL_JDN.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        tabel_JUAL_JDN.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "JUAL JDN", "w/o Box", "Box"
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
        tabel_JUAL_JDN.setRequestFocusEnabled(false);
        tabel_JUAL_JDN.setRowSelectionAllowed(false);
        tabel_JUAL_JDN.getTableHeader().setReorderingAllowed(false);
        jScrollPane6.setViewportView(tabel_JUAL_JDN);
        if (tabel_JUAL_JDN.getColumnModel().getColumnCount() > 0) {
            tabel_JUAL_JDN.getColumnModel().getColumn(0).setMinWidth(80);
        }

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel10.setText("Total JUAL JIDUN :");

        label_total_jual_jdn.setBackground(new java.awt.Color(255, 255, 255));
        label_total_jual_jdn.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_total_jual_jdn.setForeground(new java.awt.Color(255, 0, 0));
        label_total_jual_jdn.setText("0");

        tabel_SUSAH_JUAL.setAutoCreateRowSorter(true);
        tabel_SUSAH_JUAL.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        tabel_SUSAH_JUAL.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "SUSAH JUAL", "w/o Box", "Box"
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
        tabel_SUSAH_JUAL.setRequestFocusEnabled(false);
        tabel_SUSAH_JUAL.setRowSelectionAllowed(false);
        tabel_SUSAH_JUAL.getTableHeader().setReorderingAllowed(false);
        jScrollPane7.setViewportView(tabel_SUSAH_JUAL);
        if (tabel_SUSAH_JUAL.getColumnModel().getColumnCount() > 0) {
            tabel_SUSAH_JUAL.getColumnModel().getColumn(0).setMinWidth(80);
        }

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel11.setText("Total SUSAH JUAL :");

        label_total_susah_jual.setBackground(new java.awt.Color(255, 255, 255));
        label_total_susah_jual.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_total_susah_jual.setForeground(new java.awt.Color(255, 0, 0));
        label_total_susah_jual.setText("0");

        tabel_REPROSES_SWR.setAutoCreateRowSorter(true);
        tabel_REPROSES_SWR.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        tabel_REPROSES_SWR.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "REPROSES SWR", "w/o Box", "Box"
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
        tabel_REPROSES_SWR.setRequestFocusEnabled(false);
        tabel_REPROSES_SWR.setRowSelectionAllowed(false);
        tabel_REPROSES_SWR.getTableHeader().setReorderingAllowed(false);
        jScrollPane8.setViewportView(tabel_REPROSES_SWR);
        if (tabel_REPROSES_SWR.getColumnModel().getColumnCount() > 0) {
            tabel_REPROSES_SWR.getColumnModel().getColumn(0).setMinWidth(80);
        }

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel12.setText("Total REPROSES BP :");

        label_total_reproses_bp.setBackground(new java.awt.Color(255, 255, 255));
        label_total_reproses_bp.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_total_reproses_bp.setForeground(new java.awt.Color(255, 0, 0));
        label_total_reproses_bp.setText("0");

        tabel_SERABUT.setAutoCreateRowSorter(true);
        tabel_SERABUT.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        tabel_SERABUT.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null}
            },
            new String [] {
                "SERABUT", "w/o Box", "Box"
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
        tabel_SERABUT.setRequestFocusEnabled(false);
        tabel_SERABUT.setRowSelectionAllowed(false);
        tabel_SERABUT.getTableHeader().setReorderingAllowed(false);
        jScrollPane9.setViewportView(tabel_SERABUT);
        if (tabel_SERABUT.getColumnModel().getColumnCount() > 0) {
            tabel_SERABUT.getColumnModel().getColumn(0).setMinWidth(80);
        }

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel13.setText("Total SERABUT :");

        label_total_serabut.setBackground(new java.awt.Color(255, 255, 255));
        label_total_serabut.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_total_serabut.setForeground(new java.awt.Color(255, 0, 0));
        label_total_serabut.setText("0");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel14.setText("Total RESIDU :");

        label_total_residu.setBackground(new java.awt.Color(255, 255, 255));
        label_total_residu.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_total_residu.setForeground(new java.awt.Color(255, 0, 0));
        label_total_residu.setText("0");

        tabel_RESIDU.setAutoCreateRowSorter(true);
        tabel_RESIDU.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        tabel_RESIDU.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "NON NS", "w/o Box", "Box"
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
        tabel_RESIDU.setRequestFocusEnabled(false);
        tabel_RESIDU.setRowSelectionAllowed(false);
        tabel_RESIDU.getTableHeader().setReorderingAllowed(false);
        jScrollPane10.setViewportView(tabel_RESIDU);
        if (tabel_RESIDU.getColumnModel().getColumnCount() > 0) {
            tabel_RESIDU.getColumnModel().getColumn(0).setMinWidth(80);
        }

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel15.setText("Total NON NS :");

        label_total_non_ns.setBackground(new java.awt.Color(255, 255, 255));
        label_total_non_ns.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_non_ns.setForeground(new java.awt.Color(255, 0, 0));
        label_total_non_ns.setText("0");

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel17.setText("Total NON AKTIF :");

        label_total_non_aktif.setBackground(new java.awt.Color(255, 255, 255));
        label_total_non_aktif.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_non_aktif.setForeground(new java.awt.Color(255, 0, 0));
        label_total_non_aktif.setText("0");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel4.setText("Stock Value :");

        label_total_harga_cny.setBackground(new java.awt.Color(255, 255, 255));
        label_total_harga_cny.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        label_total_harga_cny.setForeground(new java.awt.Color(255, 0, 0));
        label_total_harga_cny.setText("0");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel5.setText("x");

        label_kurs.setBackground(new java.awt.Color(255, 255, 255));
        label_kurs.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        label_kurs.setForeground(new java.awt.Color(255, 0, 0));
        label_kurs.setText("0");

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel18.setText("=");

        label_total_harga_idr.setBackground(new java.awt.Color(255, 255, 255));
        label_total_harga_idr.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        label_total_harga_idr.setForeground(new java.awt.Color(255, 0, 0));
        label_total_harga_idr.setText("0");

        label_detail_chart.setBackground(new java.awt.Color(255, 255, 255));
        label_detail_chart.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_detail_chart.setText("DETAIL");

        tabel_REPACKING.setAutoCreateRowSorter(true);
        tabel_REPACKING.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        tabel_REPACKING.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "REPACKING", "Kategori", "Box"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Float.class
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
        tabel_REPACKING.setRequestFocusEnabled(false);
        tabel_REPACKING.setRowSelectionAllowed(false);
        tabel_REPACKING.getTableHeader().setReorderingAllowed(false);
        jScrollPane11.setViewportView(tabel_REPACKING);
        if (tabel_REPACKING.getColumnModel().getColumnCount() > 0) {
            tabel_REPACKING.getColumnModel().getColumn(0).setMinWidth(80);
        }

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel16.setText("Total REPACKING :");

        label_total_repacking.setBackground(new java.awt.Color(255, 255, 255));
        label_total_repacking.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_total_repacking.setForeground(new java.awt.Color(255, 0, 0));
        label_total_repacking.setText("0");

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel19.setText("Total MLEM :");

        label_total_mlem.setBackground(new java.awt.Color(255, 255, 255));
        label_total_mlem.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_total_mlem.setForeground(new java.awt.Color(255, 0, 0));
        label_total_mlem.setText("0");

        tabel_MLEM.setAutoCreateRowSorter(true);
        tabel_MLEM.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        tabel_MLEM.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Loc MLEM", "GRD", "RPK"
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
        tabel_MLEM.setRequestFocusEnabled(false);
        tabel_MLEM.setRowSelectionAllowed(false);
        tabel_MLEM.getTableHeader().setReorderingAllowed(false);
        jScrollPane12.setViewportView(tabel_MLEM);
        if (tabel_MLEM.getColumnModel().getColumnCount() > 0) {
            tabel_MLEM.getColumnModel().getColumn(0).setMinWidth(80);
        }

        tabel_REPROSES_BP.setAutoCreateRowSorter(true);
        tabel_REPROSES_BP.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        tabel_REPROSES_BP.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "REPROSES BP", "w/o Box", "Box"
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
        tabel_REPROSES_BP.setRequestFocusEnabled(false);
        tabel_REPROSES_BP.setRowSelectionAllowed(false);
        tabel_REPROSES_BP.getTableHeader().setReorderingAllowed(false);
        jScrollPane13.setViewportView(tabel_REPROSES_BP);
        if (tabel_REPROSES_BP.getColumnModel().getColumnCount() > 0) {
            tabel_REPROSES_BP.getColumnModel().getColumn(0).setMinWidth(80);
        }

        tabel_REPROSES.setAutoCreateRowSorter(true);
        tabel_REPROSES.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        tabel_REPROSES.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "REPROSES", "w/o Box", "Box"
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
        tabel_REPROSES.setRequestFocusEnabled(false);
        tabel_REPROSES.setRowSelectionAllowed(false);
        tabel_REPROSES.getTableHeader().setReorderingAllowed(false);
        jScrollPane14.setViewportView(tabel_REPROSES);
        if (tabel_REPROSES.getColumnModel().getColumnCount() > 0) {
            tabel_REPROSES.getColumnModel().getColumn(0).setMinWidth(80);
        }

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel20.setText("Total REPROSES :");

        label_total_reproses.setBackground(new java.awt.Color(255, 255, 255));
        label_total_reproses.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_total_reproses.setForeground(new java.awt.Color(255, 0, 0));
        label_total_reproses.setText("0");

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel21.setText("Total REPROSES SWR :");

        label_total_reproses_swr.setBackground(new java.awt.Color(255, 255, 255));
        label_total_reproses_swr.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_total_reproses_swr.setForeground(new java.awt.Color(255, 0, 0));
        label_total_reproses_swr.setText("0");

        label_total_wip_reproses.setBackground(new java.awt.Color(255, 255, 255));
        label_total_wip_reproses.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_total_wip_reproses.setForeground(new java.awt.Color(255, 0, 0));
        label_total_wip_reproses.setText("0");

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel22.setText("Total WIP REPROSES :");

        tabel_WIP_REPROSES.setAutoCreateRowSorter(true);
        tabel_WIP_REPROSES.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        tabel_WIP_REPROSES.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "REPACKING", "Kategori", "Box"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Float.class
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
        tabel_WIP_REPROSES.setRequestFocusEnabled(false);
        tabel_WIP_REPROSES.setRowSelectionAllowed(false);
        tabel_WIP_REPROSES.getTableHeader().setReorderingAllowed(false);
        jScrollPane15.setViewportView(tabel_WIP_REPROSES);
        if (tabel_WIP_REPROSES.getColumnModel().getColumnCount() > 0) {
            tabel_WIP_REPROSES.getColumnModel().getColumn(0).setMinWidth(80);
        }

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_jual_mk))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_jual_flat))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel8)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(label_total_jual_bp))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel9)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(label_total_jual_pch))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel10)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(label_total_jual_jdn))
                                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel13)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_serabut))
                                    .addComponent(jScrollPane9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel19)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(label_total_mlem)))
                            .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel14)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_residu))
                                    .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel11)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_susah_jual))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel15)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_non_ns))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel17)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_non_aktif))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel16)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_repacking))
                                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel12)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(label_total_reproses_bp))
                                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                                .addComponent(jScrollPane13, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addComponent(jScrollPane14, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_reproses))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_reproses_swr))
                            .addComponent(jScrollPane15, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel22)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_wip_reproses)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Panel_Pie_Chart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_StokBox)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_harga_cny)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(label_kurs)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(label_total_harga_idr)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 692, Short.MAX_VALUE)
                        .addComponent(label_detail_chart)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_jam)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Button_Refresh)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(label_jam, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(label_detail_chart))
                            .addComponent(Button_Refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Panel_Pie_Chart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel3)
                            .addComponent(label_total_gram_StokBox)
                            .addComponent(jLabel4)
                            .addComponent(label_total_harga_cny)
                            .addComponent(jLabel5)
                            .addComponent(label_kurs)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel18)
                                .addComponent(label_total_harga_idr)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                    .addComponent(label_total_reproses_bp)
                                    .addComponent(jLabel12)
                                    .addComponent(label_total_residu)
                                    .addComponent(jLabel14))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jScrollPane13, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                            .addComponent(label_total_reproses)
                                            .addComponent(jLabel20))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jScrollPane14, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                            .addComponent(label_total_non_ns)
                                            .addComponent(jLabel15))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                            .addComponent(label_total_non_aktif)
                                            .addComponent(jLabel17))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                            .addComponent(label_total_susah_jual)
                                            .addComponent(jLabel11))))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                        .addComponent(label_total_jual_mk)
                                        .addComponent(jLabel6))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(2, 2, 2)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(label_total_jual_bp)
                                            .addComponent(jLabel8))))
                                .addGap(4, 4, 4)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jScrollPane1)
                                        .addGap(7, 7, 7)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                            .addComponent(label_total_jual_flat)
                                            .addComponent(jLabel7))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(192, 192, 192)
                                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                            .addComponent(label_total_repacking)
                                            .addComponent(jLabel16))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jScrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                            .addComponent(label_total_jual_pch)
                                            .addComponent(jLabel9))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(13, 13, 13)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                            .addComponent(label_total_jual_jdn)
                                            .addComponent(jLabel10))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                            .addComponent(label_total_serabut)
                                            .addComponent(jLabel13))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                            .addComponent(label_total_mlem)
                                            .addComponent(jLabel19))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(290, 290, 290)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                            .addComponent(label_total_reproses_swr)
                                            .addComponent(jLabel21))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                            .addComponent(label_total_wip_reproses)
                                            .addComponent(jLabel22))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jScrollPane15, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                                .addContainerGap())))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
//        t.cancel();
//        System.out.println("STOP");
    }//GEN-LAST:event_formWindowClosing

    private void Button_RefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_RefreshActionPerformed
        // TODO add your handling code here:
        total_jual_mk = 0;
        total_jual_flat = 0;
        total_jual_bp = 0;
        total_jual_pch = 0;
        total_jual_jdn = 0;
        total_serabut = 0;
        total_susah_jual = 0;
        total_residu = 0;
        total_non_ns = 0;
        total_non_aktif = 0;
        total_no_kategori = 0;
        total_reproses_bp = 0;
        total_reproses = 0;
        total_reproses_swr = 0;
        total_gram_StokRepacking = 0;
        refreshTable_DataStok();
        refreshTable_StockRepacking();
        refreshTable_MLEM();
        refresh_JAM();
    }//GEN-LAST:event_Button_RefreshActionPerformed

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrame_KategoriStokJualTV.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame_KategoriStokJualTV Stok = new JFrame_KategoriStokJualTV();
                Stok.setVisible(true);
                Stok.setLocationRelativeTo(null);
                Stok.setExtendedState(MAXIMIZED_BOTH);
                Stok.init(0);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Button_Refresh;
    private javax.swing.JPanel Panel_Pie_Chart;
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
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JLabel label_detail_chart;
    private javax.swing.JLabel label_jam;
    private javax.swing.JLabel label_kurs;
    private javax.swing.JLabel label_total_gram_StokBox;
    private javax.swing.JLabel label_total_harga_cny;
    private javax.swing.JLabel label_total_harga_idr;
    private javax.swing.JLabel label_total_jual_bp;
    private javax.swing.JLabel label_total_jual_flat;
    private javax.swing.JLabel label_total_jual_jdn;
    private javax.swing.JLabel label_total_jual_mk;
    private javax.swing.JLabel label_total_jual_pch;
    private javax.swing.JLabel label_total_mlem;
    private javax.swing.JLabel label_total_non_aktif;
    private javax.swing.JLabel label_total_non_ns;
    private javax.swing.JLabel label_total_repacking;
    private javax.swing.JLabel label_total_reproses;
    private javax.swing.JLabel label_total_reproses_bp;
    private javax.swing.JLabel label_total_reproses_swr;
    private javax.swing.JLabel label_total_residu;
    private javax.swing.JLabel label_total_serabut;
    private javax.swing.JLabel label_total_susah_jual;
    private javax.swing.JLabel label_total_wip_reproses;
    private javax.swing.JTable tabel_JUAL_BP;
    private javax.swing.JTable tabel_JUAL_FLAT;
    private javax.swing.JTable tabel_JUAL_JDN;
    private javax.swing.JTable tabel_JUAL_MK;
    private javax.swing.JTable tabel_JUAL_PCH;
    private javax.swing.JTable tabel_MLEM;
    private javax.swing.JTable tabel_REPACKING;
    private javax.swing.JTable tabel_REPROSES;
    private javax.swing.JTable tabel_REPROSES_BP;
    private javax.swing.JTable tabel_REPROSES_SWR;
    private javax.swing.JTable tabel_RESIDU;
    private javax.swing.JTable tabel_SERABUT;
    private javax.swing.JTable tabel_SUSAH_JUAL;
    private javax.swing.JTable tabel_WIP_REPROSES;
    // End of variables declaration//GEN-END:variables
}
