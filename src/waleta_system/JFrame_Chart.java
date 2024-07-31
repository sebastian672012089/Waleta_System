package waleta_system;

import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Paint;
import java.awt.SystemColor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.LegendItemEntity;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;
import waleta_system.Class.AksesMenu;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.DataF2;
import waleta_system.Class.DataKaryawan;
import waleta_system.Class.GradeBahanBaku;
import waleta_system.Class.GradeBahanJadi;
import waleta_system.Class.Utility;
import static waleta_system.Panel_produksi.JPanel_Finishing2.Table_Setoran_harian_f2;

public class JFrame_Chart extends javax.swing.JFrame {

    Date today = new Date();
    GraphicsDevice device = GraphicsEnvironment
            .getLocalGraphicsEnvironment().getScreenDevices()[0];
    String sql = null;
    ResultSet rs;
    Date date = new Date();//date berisikan tanggal hari ini
    JFileChooser chooser = new JFileChooser();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    public static String posisi, user;
    public static List<AksesMenu.Akses> dataMenu;
    public TimeSeriesCollection dataset2;
    public DefaultPieDataset dataset3;
    public DefaultPieDataset dataset4;
    public DefaultPieDataset dataset5;
    public DefaultCategoryDataset dataset6;
    public DefaultPieDataset dataset7;
    public DefaultPieDataset dataset8;
    public DefaultPieDataset dataset9;
    JFreeChart chart2;
    JFreeChart chart4;
    JFreeChart chart6;
    JFreeChart chart7;
    JFreeChart chart8;
    JFreeChart chart9;
    ArrayList<DataF2> list;
    ArrayList<DataKaryawan> list2;
    ArrayList<GradeBahanBaku> list3;
    ArrayList<GradeBahanBaku> list4;
    ArrayList<GradeBahanJadi> list5;

    public JFrame_Chart() {
        initComponents();
    }

    public void init() {
        try {
            ComboBox_grade.removeAllItems();
            ComboBox_grade.addItem("All");
            String query = "SELECT `kode_grade` FROM `tb_grade_bahan_baku`";
            ResultSet rs1 = Utility.db.getStatement().executeQuery(query);
            while (rs1.next()) {
                ComboBox_grade.addItem(rs1.getString("kode_grade"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        Table_hasil_produksi_baku.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        Table_hasil_produksi_jadi.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        chart();
//        refreshChart_Rendemen();
//        show_data_masukKaryawan();
//        show_data_hasilProduksi();
//        show_data_stokBahanBaku();
    }

    public ArrayList<DataF2> SetoranHarianList() {
        ArrayList<DataF2> F2List = new ArrayList<>();
        try {

            String ruang = "";
            String kodeGrade = "";

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
            if (null != ComboBox_grade.getSelectedItem()) {
                switch (ComboBox_grade.getSelectedItem().toString()) {
                    case "All": {
                        kodeGrade = "";
                        break;
                    }
                    default:
                        kodeGrade = ComboBox_grade.getSelectedItem().toString();
                        break;
                }
            }
            if (Date_Setoran1.getDate() == null || Date_Setoran2.getDate() == null) {
                sql = "SELECT * \n"
                        + "FROM `tb_finishing_2` "
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_finishing_2`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_grade_bahan_baku`.`kode_grade` = `tb_laporan_produksi`.`kode_grade`\n"
                        + "WHERE `tb_laporan_produksi`.`ruangan` LIKE '%" + ruang + "%' AND `tb_grade_bahan_baku`.`kode_grade` LIKE '%" + kodeGrade + "%'\n"
                        + "ORDER BY `tb_finishing_2`.`tgl_setor_f2` DESC";
            } else {
                sql = "SELECT * \n"
                        + "FROM `tb_finishing_2` "
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_finishing_2`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_grade_bahan_baku`.`kode_grade` = `tb_laporan_produksi`.`kode_grade`\n"
                        + "WHERE `tb_laporan_produksi`.`ruangan` LIKE '%" + ruang + "%' AND `tb_grade_bahan_baku`.`kode_grade` LIKE '%" + kodeGrade + "%' AND `tb_finishing_2`.`tgl_setor_f2` BETWEEN '" + dateFormat.format(Date_Setoran1.getDate()) + "' AND '" + dateFormat.format(Date_Setoran2.getDate()) + "'\n"
                        + "ORDER BY `tb_finishing_2`.`tgl_setor_f2` DESC";
            }

            rs = Utility.db.getStatement().executeQuery(sql);
            DataF2 f2;
            while (rs.next()) {
                f2 = new DataF2(rs.getString("no_kartu_waleta"),
                        rs.getString("no_laporan_produksi"),
                        0,
                        rs.getInt("berat_kering"),
                        rs.getString("memo_lp"),
                        rs.getString("kode_grade"),
                        null,
                        null,
                        rs.getString("ruangan"),
                        null,
                        null,
                        null,
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
                        rs.getInt("tambahan_kaki1"),
                        rs.getString("lp_kaki1"),
                        rs.getInt("tambahan_kaki2"),
                        rs.getString("lp_kaki2"),
                        rs.getString("admin_f2"),
                        rs.getString("otorisasi"),
                        rs.getString("keterangan"));
                F2List.add(f2);
            }
        } catch (Exception ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        return F2List;
    }

    public ArrayList<DataKaryawan> KaryawanList() {
        ArrayList<DataKaryawan> KaryawanList = new ArrayList<>();
        try {
            String filter_tanggal = "1";
            if (Date_DataMasuk.getDate() != null && Date_DataMasuk2.getDate() != null) {
                filter_tanggal = "`tanggal_masuk` BETWEEN '" + dateFormat.format(Date_DataMasuk.getDate()) + "' AND '" + dateFormat.format(Date_DataMasuk2.getDate()) + "'\n";
            }
            
            sql = "SELECT `id_pegawai`, `pin_finger`, `nik_ktp`, `nama_pegawai`, `jenis_kelamin`, `tempat_lahir`, `tanggal_lahir`, `agama`, `alamat`, `desa`, `kecamatan`, `golongan_darah`, `no_telp`, `status_kawin`, `nama_ibu`, `tb_bagian`.`nama_bagian`,`tb_bagian`.`kode_departemen`, `posisi`, `pendidikan`, `tanggal_interview`, `tanggal_masuk`, `tanggal_keluar`, `kategori_keluar`, `keterangan`, `status`, `level_gaji`, `jam_kerja`, `fc_ktp`, `sertifikat_vaksin1`, `sertifikat_vaksin2`, `berkas_surat_pernyataan`, `tanggal_surat`, `email`, `potongan_bpjs`, `status_pajak`, `no_npwp` "
                    + "FROM `tb_karyawan` "
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian` \n"
                    + filter_tanggal
                    + "ORDER BY `id_pegawai` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            DataKaryawan Karyawan;
            while (rs.next()) {
                Karyawan = new DataKaryawan(rs.getString("id_pegawai"), rs.getString("pin_finger"), rs.getString("nik_ktp"), rs.getString("nama_pegawai"), rs.getString("jenis_kelamin"), rs.getString("tempat_lahir"), rs.getDate("tanggal_lahir"), rs.getString("alamat"), rs.getString("desa"), rs.getString("kecamatan"), rs.getString("kota_kabupaten"), rs.getString("provinsi"), rs.getString("golongan_darah"), rs.getString("status_kawin"), rs.getString("nama_ibu"), rs.getString("no_telp"), rs.getString("email"), rs.getString("kategori_keluar"), rs.getString("keterangan"), rs.getBoolean("uid_card"),
                        rs.getString("nama_bagian"), rs.getString("posisi"), rs.getString("kode_departemen"), rs.getString("pendidikan"), rs.getDate("tanggal_interview"), rs.getDate("tanggal_masuk"), rs.getDate("tanggal_keluar"), rs.getString("status"), rs.getString("jam_kerja"), rs.getString("jalur_jemputan"), rs.getInt("potongan_bpjs"),
                        rs.getInt("fc_ktp"), rs.getInt("sertifikat_vaksin1"), rs.getInt("sertifikat_vaksin2"), rs.getInt("berkas_surat_pernyataan"), rs.getDate("tanggal_surat"), rs.getDate("tgl_surat_berakhir"),
                        rs.getString("status_pajak"), rs.getString("no_npwp")
                );
                KaryawanList.add(Karyawan);
            }
        } catch (Exception ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        return KaryawanList;
    }

    public ArrayList<GradeBahanBaku> GradeBakuList() {
        ArrayList<GradeBahanBaku> gradeList = new ArrayList<>();
        try {
            sql = "SELECT * FROM `tb_grade_bahan_baku` GROUP BY kategori";
            rs = Utility.db.getStatement().executeQuery(sql);
            GradeBahanBaku gradeBaku;
            while (rs.next()) {
                gradeBaku = new GradeBahanBaku(rs.getString("kode_grade"), rs.getString("jenis_bentuk"), rs.getString("jenis_bulu"), rs.getString("jenis_warna"), rs.getString("kategori"));
                gradeList.add(gradeBaku);
            }
        } catch (Exception ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, ex);
        }
        return gradeList;
    }

    public ArrayList<GradeBahanJadi> GradeBJList() {
        ArrayList<GradeBahanJadi> GradeBJList = new ArrayList<>();
        try {

            sql = "SELECT * FROM `tb_grade_bahan_jadi`";
            rs = Utility.db.getStatement().executeQuery(sql);
            GradeBahanJadi GradeBJ;
            while (rs.next()) {
                GradeBJ = new GradeBahanJadi(rs.getString("kode"), rs.getString("kode_grade"), rs.getString("nama_grade"), rs.getString("bentuk_grade"));
                GradeBJList.add(GradeBJ);
            }
        } catch (Exception ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        return GradeBJList;
    }

    public void show_data_setoran() {
        decimalFormat.setMaximumFractionDigits(2);
        decimalFormat.setGroupingUsed(true);
        list = SetoranHarianList();
        int TotalData;
        int TotalData1;
        float bk, utuh, fbonus, fnol, flat, sh, sp, kaki, netto;
        float rend_utuh, rend_flat, rend_sh, rend_sp;
        float tot_bk = 0, tot_kpg_utuh = 0, tot_brt_utuh = 0, tot_kpg_flat = 0, tot_brt_flat = 0;
        float tot_bk1 = 0, tot_kpg_utuh1 = 0, tot_brt_utuh1 = 0, tot_kpg_flat1 = 0, tot_brt_flat1 = 0;
        float avg_rend_sh, tot_rend_sh = 0;
        float avg_rend_sh1, tot_rend_sh1 = 0;
        float avg_rend_sp, tot_rend_sp = 0;
        float avg_rend_sp1, tot_rend_sp1 = 0;
        float avg_rend_utuh, tot_rend_utuh = 0;
        float avg_rend_utuh1, tot_rend_utuh1 = 0;
        float avg_rend_flat, tot_rend_flat = 0;
        float avg_rend_flat1, tot_rend_flat1 = 0;
        TotalData = 0;
        TotalData1 = list.size();
        Date tglTerakhir = null;

        dataset2.removeAllSeries();
        dataset3.clear();
        final TimeSeries chart_rend_sh = new TimeSeries("Susut Hilang");
        final TimeSeries chart_rend_sp = new TimeSeries("Susut Proses");
        final TimeSeries chart_rend_utuh = new TimeSeries("Utuh");
        final TimeSeries chart_rend_flat = new TimeSeries("Flat");
        for (int i = 0; i < list.size(); i++) {
            bk = list.get(i).getBerat_Kering();
            fbonus = list.get(i).getFbonus_berat();
            fnol = list.get(i).getFnol_berat();
            utuh = fbonus + fnol;
            kaki = list.get(i).getTambah_kaki1() + list.get(i).getTambah_kaki2();
            netto = utuh - kaki;
            flat = list.get(i).getFlat_berat() + list.get(i).getPecah_berat();
            sp = list.get(i).getSesekan() + list.get(i).getHancuran() + list.get(i).getRontokan() + list.get(i).getBonggol() + list.get(i).getSerabut();
            sh = bk - (netto + flat + sp);

            rend_utuh = (netto / bk) * 100;
            rend_flat = (flat / bk) * 100;
            rend_sh = (sh / bk) * 100;
            rend_sp = (sp / bk) * 100;
            if (tglTerakhir == null) {
                tglTerakhir = list.get(i).getTgl_setor_f2();
            } else if (list.get(i).getTgl_setor_f2().getTime() != tglTerakhir.getTime()) {
                avg_rend_sh = tot_rend_sh / TotalData;
                avg_rend_sp = tot_rend_sp / TotalData;
                avg_rend_utuh = tot_rend_utuh / TotalData;
                avg_rend_flat = tot_rend_flat / TotalData;
                chart_rend_sh.add(new Day(tglTerakhir), avg_rend_sh);
                chart_rend_sp.add(new Day(tglTerakhir), avg_rend_sp);
                chart_rend_utuh.add(new Day(tglTerakhir), avg_rend_utuh);
                chart_rend_flat.add(new Day(tglTerakhir), avg_rend_flat);

                tot_bk = 0;
                tot_kpg_utuh = 0;
                tot_brt_utuh = 0;
                tot_kpg_flat = 0;
                tot_brt_flat = 0;
                tot_rend_sh = 0;
                tot_rend_sp = 0;
                tot_rend_utuh = 0;
                tot_rend_flat = 0;
                TotalData = 0;
                tglTerakhir = list.get(i).getTgl_setor_f2();
            }

            //total Berat Kering
            tot_bk = tot_bk + list.get(i).getBerat_Kering();
            tot_bk1 = tot_bk1 + list.get(i).getBerat_Kering();
            //total keping utuh
            tot_kpg_utuh = tot_kpg_utuh + list.get(i).getFbonus_keping() + list.get(i).getFnol_keping();
            tot_kpg_utuh1 = tot_kpg_utuh1 + list.get(i).getFbonus_keping() + list.get(i).getFnol_keping();
            //total berat utuh
            tot_brt_utuh = tot_brt_utuh + list.get(i).getFbonus_berat() + list.get(i).getFnol_berat();
            tot_brt_utuh1 = tot_brt_utuh1 + list.get(i).getFbonus_berat() + list.get(i).getFnol_berat();
            //total keping flat
            tot_kpg_flat = tot_kpg_flat + list.get(i).getFlat_keping() + list.get(i).getPecah_keping();
            tot_kpg_flat1 = tot_kpg_flat1 + list.get(i).getFlat_keping() + list.get(i).getPecah_keping();
            //total berat flat
            tot_brt_flat = tot_brt_flat + list.get(i).getFlat_berat() + list.get(i).getPecah_berat();
            tot_brt_flat1 = tot_brt_flat1 + list.get(i).getFlat_berat() + list.get(i).getPecah_berat();
            //total rendemen susut hilang
            tot_rend_sh = tot_rend_sh + rend_sh;
            tot_rend_sh1 = tot_rend_sh1 + rend_sh;
            //total rendemen susut proses
            tot_rend_sp = tot_rend_sp + rend_sp;
            tot_rend_sp1 = tot_rend_sp1 + rend_sp;
            //total rendemen mangkok
            tot_rend_utuh = tot_rend_utuh + rend_utuh;
            tot_rend_utuh1 = tot_rend_utuh1 + rend_utuh;
            //total rendemen flat
            tot_rend_flat = tot_rend_flat + rend_flat;
            tot_rend_flat1 = tot_rend_flat1 + rend_flat;
            TotalData++;
        }

        if (TotalData > 0) {
            avg_rend_sh = tot_rend_sh / TotalData;
            avg_rend_sp = tot_rend_sp / TotalData;
            avg_rend_utuh = tot_rend_utuh / TotalData;
            avg_rend_flat = tot_rend_flat / TotalData;
            chart_rend_sh.add(new Day(tglTerakhir), avg_rend_sh);
            chart_rend_sp.add(new Day(tglTerakhir), avg_rend_sp);
            chart_rend_utuh.add(new Day(tglTerakhir), avg_rend_utuh);
            chart_rend_flat.add(new Day(tglTerakhir), avg_rend_flat);
        }
        dataset2.addSeries(chart_rend_sh);
        dataset2.addSeries(chart_rend_sp);
        dataset2.addSeries(chart_rend_utuh);
        dataset2.addSeries(chart_rend_flat);
        showTickMarksForSingleElements(dataset2, chart2);

        avg_rend_sh1 = tot_rend_sh1 / TotalData1;
        avg_rend_sp1 = tot_rend_sp1 / TotalData1;
        avg_rend_utuh1 = tot_rend_utuh1 / TotalData1;
        avg_rend_flat1 = tot_rend_flat1 / TotalData1;
        dataset3.setValue("Susut Hilang", avg_rend_sh1);
        dataset3.setValue("Susut Proses", avg_rend_sp1);
        dataset3.setValue("Utuh", avg_rend_utuh1);
        dataset3.setValue("Flat", avg_rend_flat1);

        label_total_data_lineChart.setText(Integer.toString(TotalData1));
        label_total_data_pieCHart.setText(Integer.toString(TotalData1));
    }

    public void show_data_qclp() {
        try {
            int total_passed = 0, total_hold = 0;

            if (Date_QCLP1.getDate() != null && Date_QCLP2.getDate() != null) {
                sql = "SELECT * FROM `tb_lab_treatment_lp` LEFT JOIN `tb_laporan_produksi` ON `tb_lab_treatment_lp`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`"
                        + "WHERE (`tgl_treatment` BETWEEN '" + dateFormat.format(Date_QCLP1.getDate()) + "' AND '" + dateFormat.format(Date_QCLP2.getDate()) + "') ";
            } else {
                sql = "SELECT * FROM `tb_lab_treatment_lp` LEFT JOIN `tb_laporan_produksi` ON `tb_lab_treatment_lp`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`";
            }

            rs = Utility.db.getStatement().executeQuery(sql);
            DataF2 f2;
            while (rs.next()) {
                if (rs.getString("status").equals("PASSED")) {
                    total_passed++;
                } else if (rs.getString("status").equals("HOLD/NON GNS")) {
                    total_hold++;
                }
            }
            dataset5.setValue("Passed", total_passed);
            dataset5.setValue("Hold", total_hold);
            label_qclp_passed.setText("" + total_passed);
            label_qclp_persentase_passed.setText("" + Math.round((float) total_passed * (float) 100 / (float) (total_passed + total_hold)));
            label_qclp_hold.setText("" + total_hold);
            label_qclp_persentase_hold.setText("" + Math.round((float) total_hold * (float) 100 / (float) (total_passed + total_hold)));
            label_qclp_total.setText("" + (total_passed + total_hold));
        } catch (Exception ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void show_data_masukKaryawan() {
        decimalFormat.setMaximumFractionDigits(2);
        decimalFormat.setGroupingUsed(true);
        list2 = KaryawanList();
        String namaBagian[] = new String[]{
            "LABORATORIUM",
            "SANITASI",
            "MAINTENANCE",
            "TRAINING DEVELOPMENT",
            "RECRUITMENT",
            "CUCI BM",
            "GRADING BM",
            "ACCOUNTING",
            "RECRUITMENT",
            "PURCHASING",
            "CUCI ALAT",
            "CABUT A",
            "CABUT B",
            "CABUT C",
            "KOREKSI CABUT A",
            "KOREKSI CABUT B",
            "KOREKSI CABUT C",
            "PENGAWAS A",
            "PENGAWAS B",
            "PENGAWAS C",
            "ASISTEN PENGAWAS A",
            "ASISTEN PENGAWAS B",
            "ASISTEN PENGAWAS C",
            "CETAK A",
            "FINISHING 2",
            "GRADING BJ"
        };
        int TotalData;
        int totalAll = 0;
        int totalCewek = 0;
        int totalCowok = 0;
        float tot[] = new float[namaBagian.length];
        float avg[] = new float[namaBagian.length];
        TotalData = list2.size();
        dataset4.clear();
        for (int i = 0; i < list2.size(); i++) {
            for (int j = 0; j < namaBagian.length; j++) {
                if (list2.get(i).getKode_bagian().equalsIgnoreCase(namaBagian[j])) {
                    tot[j]++;
                    totalAll++;
                    if (list2.get(i).getJenis_kelamin().equalsIgnoreCase("Perempuan")) {
                        totalCewek++;
                    } else {
                        totalCowok++;
                    }
                }
            }
        }
        for (int i = 0; i < namaBagian.length; i++) {
            avg[i] = tot[i] / TotalData;
            dataset4.setValue(namaBagian[i], avg[i]);
        }
        LegendItemCollection legendItemsOld = chart4.getPlot().getLegendItems();
        final LegendItemCollection legendItemsNew = new LegendItemCollection();
        for (int i = 0; i < namaBagian.length; i++) {
            avg[i] = tot[i] / TotalData;
            if (avg[i] != 0) {
                Iterator iterator = legendItemsOld.iterator();
                while (iterator.hasNext()) {
                    org.jfree.chart.LegendItem item = (org.jfree.chart.LegendItem) iterator.next();
                    if (item.getLabel().equals(namaBagian[i])) {
                        iterator.remove();
                        legendItemsNew.add(item);
                    }
                }
            }
        }

        LegendItemSource source = new LegendItemSource() {
            LegendItemCollection lic = new LegendItemCollection();

            {
                lic.addAll(legendItemsNew);
            }

            @Override
            public LegendItemCollection getLegendItems() {
                return lic;
            }
        };
        chart4.removeLegend();
        LegendTitle legend = new LegendTitle(source);
        legend.setPosition(RectangleEdge.BOTTOM);
        legend.setBorder(new BlockBorder());
        legend.setMargin(0, 5, 3, 5);
        chart4.addLegend(legend);

        label_bagian_pieChart1.setText("-");
        label_persentase_pieChart1.setText("-");
        label_laki_pieChart1.setText("-");
        label_perempuan_pieChart1.setText("-");
        label_total_pieChart1.setText("-");
        label_total_laki_pieChart1.setText("" + totalCowok);
        label_total_perempuan_pieChart1.setText("" + totalCewek);
        label_total_data_pieChart1.setText("" + totalAll);
    }

    public void show_data_stokBahanBaku() {
        decimalFormat.setMaximumFractionDigits(2);
        decimalFormat.setGroupingUsed(true);
        DefaultTableModel model = (DefaultTableModel) Table_stok_bahan_baku.getModel();
        int kpg_masuk = 0, gram_masuk = 0, kpg_lp = 0, gram_lp = 0, kpg_jual = 0, gram_keluar = 0, sisa_keping = 0, sisa_gram = 0;
        float tot_gram = 0;
        ResultSet rs_grading, rs_jual, rs_lp;
        list3 = GradeBakuList();
        dataset7.clear();
        Object[][] row = new Object[list3.size()][4];
        for (int i = 0; i < list3.size(); i++) {
            try {
                String sql_grading = "SELECT SUM(`tb_grading_bahan_baku`.`jumlah_keping`) AS 'jumlah_keping', SUM(`tb_grading_bahan_baku`.`total_berat`) AS 'total_berat' "
                        + "FROM `tb_grading_bahan_baku` LEFT JOIN `tb_bahan_baku_masuk` ON `tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta` LEFT JOIN `tb_grade_bahan_baku` ON `tb_grading_bahan_baku`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`"
                        + "WHERE `kategori` = '" + list3.get(i).getKategori() + "' AND `tgl_masuk`<='" + dateFormat.format(Date_filter1.getDate()) + "'";
                rs_grading = Utility.db.getStatement().executeQuery(sql_grading);
                if (rs_grading.next()) {
                    kpg_masuk = rs_grading.getInt("jumlah_keping");
                    gram_masuk = rs_grading.getInt("total_berat");
                }
                String sql_lp = "SELECT SUM(`jumlah_keping`) AS 'keping', SUM(`berat_basah`) AS 'berat' FROM `tb_laporan_produksi` LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade` WHERE `kategori` = '" + list3.get(i).getKategori() + "'  AND `tanggal_lp`<='" + dateFormat.format(Date_filter1.getDate()) + "'";
                rs_lp = Utility.db.getStatement().executeQuery(sql_lp);
                if (rs_lp.next()) {
                    kpg_lp = rs_lp.getInt("keping");
                    gram_lp = rs_lp.getInt("berat");
                }
                String sql_jual = "SELECT SUM(`total_keping_keluar`) AS 'keping', SUM(`total_berat_keluar`) AS 'berat' "
                        + "FROM `tb_bahan_baku_keluar` LEFT JOIN `tb_bahan_baku_keluar1` ON `tb_bahan_baku_keluar`.`kode_pengeluaran` = `tb_bahan_baku_keluar1`.`kode_pengeluaran` LEFT JOIN `tb_grade_bahan_baku` ON `tb_bahan_baku_keluar`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade` "
                        + "WHERE `kategori` = '" + list3.get(i).getKategori() + "'  AND `tgl_keluar`<='" + dateFormat.format(Date_filter1.getDate()) + "'";
                rs_jual = Utility.db.getStatement().executeQuery(sql_jual);
                if (rs_jual.next()) {
                    kpg_jual = rs_jual.getInt("keping");
                    gram_keluar = rs_jual.getInt("berat");
                }
                sisa_keping = kpg_masuk - (kpg_lp + kpg_jual);
                sisa_gram = gram_masuk - (gram_lp + gram_keluar);
            } catch (SQLException ex) {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
            }
            dataset7.setValue(list3.get(i).getKategori(), sisa_gram);
            row[i][0] = list3.get(i).getKategori();
            row[i][1] = sisa_keping;
            row[i][2] = sisa_gram;
            tot_gram += sisa_gram;
        }
        for (int i = 0; i < row.length; i++) {
            row[i][3] = ((float) (int) row[i][2] / tot_gram) * (float) 100;
            model.addRow(row[i]);
        }
        final LegendItemCollection legendItemsNew = new LegendItemCollection();
    }

    public void show_data_hasilProduksi() {
        decimalFormat.setMaximumFractionDigits(2);
        decimalFormat.setGroupingUsed(true);
        DefaultTableModel model = (DefaultTableModel) Table_hasil_produksi_baku.getModel();
        DefaultTableModel model2 = (DefaultTableModel) Table_hasil_produksi_jadi.getModel();
        int berat_baku = 0, berat_jadi = 0;
        float tot_berat_baku = 0, tot_berat_jadi = 0;
        ResultSet rs_baku, rs_jadi;
        list4 = GradeBakuList();
        list5 = GradeBJList();
        dataset8.clear();
        dataset9.clear();
        Object[][] row = new Object[list4.size()][3];
        for (int i = 0; i < list4.size(); i++) {
            try {
                String sql_baku = "SELECT SUM(`tb_laporan_produksi`.`berat_basah`) AS `total_berat`, `tb_grade_bahan_baku`.`kategori` "
                        + "FROM `tb_bahan_jadi_masuk` JOIN `tb_laporan_produksi` ON `tb_bahan_jadi_masuk`.`kode_asal` = `tb_laporan_produksi`.`no_laporan_produksi` "
                        + "JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade` "
                        + "WHERE `tb_grade_bahan_baku`.`kategori` = '" + list4.get(i).getKategori() + "' AND `tanggal_grading` BETWEEN '" + dateFormat.format(Date_filter2.getDate()) + "' AND '" + dateFormat.format(Date_filter3.getDate()) + "'";
                rs_baku = Utility.db.getStatement().executeQuery(sql_baku);
                if (rs_baku.next()) {
                    berat_baku = rs_baku.getInt("total_berat");
                }
            } catch (SQLException ex) {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
            }
            dataset8.setValue(list4.get(i).getKategori(), berat_baku);
            row[i][0] = list4.get(i).getKategori();
            row[i][1] = berat_baku;
            tot_berat_baku += berat_baku;
        }
        for (int i = 0; i < model.getRowCount(); i++) {
            model.removeRow(i);
            i--;
        }
        for (int i = 0; i < model2.getRowCount(); i++) {
            model2.removeRow(i);
            i--;
        }

        DecimalFormat df = new DecimalFormat();
        df.setMinimumFractionDigits(2);
        df.setMaximumFractionDigits(2);
        for (int i = 0; i < row.length; i++) {
            if (tot_berat_baku == 0) {
                row[i][2] = "0.00 %";
            } else {
                row[i][2] = df.format(((float) (int) row[i][1] / (float) tot_berat_baku) * (float) 100) + " %";
            }
            model.addRow(row[i]);
        }
        for (int i = 0; i < list5.size(); i++) {
            try {
                String sql_jadi = "SELECT SUM(`tb_laporan_produksi`.`berat_basah`) AS `total_berat` "
                        + "FROM `tb_grade_bahan_jadi` "
                        + "JOIN `tb_grading_bahan_jadi` ON `tb_grade_bahan_jadi`.`kode` = `tb_grading_bahan_jadi`.`grade_bahan_jadi` "
                        + "JOIN `tb_laporan_produksi` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_grading_bahan_jadi`.`kode_asal_bahan_jadi` "
                        + "JOIN `tb_bahan_jadi_masuk` ON `tb_bahan_jadi_masuk`.`kode_asal` = `tb_laporan_produksi`.`no_laporan_produksi` "
                        + "WHERE `tb_grade_bahan_jadi`.`kode_grade` = '" + list5.get(i).getKode_grade() + "' AND `tanggal_grading` BETWEEN '" + dateFormat.format(Date_filter2.getDate()) + "' AND '" + dateFormat.format(Date_filter3.getDate()) + "'";
                rs_jadi = Utility.db.getStatement().executeQuery(sql_jadi);
                if (rs_jadi.next()) {
                    berat_jadi = rs_jadi.getInt("total_berat");
                }
            } catch (SQLException ex) {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
            }
            String kode = "";
            switch (list5.get(i).getKode_grade()) {
                case "GNS JMB":
                case "GNS JMB R":
                    kode = "JUMBO";
                    break;
                case "GNS SPR":
                case "GNS SPR R":
                    kode = "SUPER";
                    break;
                case "GNS PREM":
                case "GNS PREM R":
                    kode = "PREMIUM";
                    break;
                case "GNS STD":
                    kode = "STANDART";
                    break;
                case "GNS MK ABN":
                case "GNS SGTG":
                case "GNS SGTG 1":
                case "GNS SGTG 2":
                case "GNS SGTG BSR":
                case "GNS GRADE 3":
                case "GNS GRADE 2":
                    kode = "MK LAIN";
                case "GNS OVAL 1":
                case "GNS OVAL 1 R":
                case "GNS OVAL 2":
                case "GNS OVAL 2 R":
                case "GNS OVAL":
                case "GNS OVAL SGTG":
                    //case "GNS OVAL KK KNG":
                    kode = "OVAL";
                    break;
                case "GNS SRBT":
                case "GNS BGL":
                case "GNS MESS 1":
                case "GNS F1":
                case "GNS F2":
                case "GNS F3":
                case "GNS HC":
                case "GNS MESS 2":
                case "GNS SSK":
                case "GNS SSK BRS":
                case "GNS HCRN KTR":
                case "GNS RONT LP":
                case "GNS B2":
                case "GNS B3":
                    kode = "SUSUT PROSES";
                    break;
                case "GNS SRT KCL":
                case "GNS SWR":
                case "GNS SRT PDK":
                case "GNS PATAHAN":
                    kode = "FLAT LAIN";
                    break;
                case "GNS MK LEM":
                case "GNS SAMPLE":
                case "GNS MK KCL":
                case "GNS LAB":
                    kode = "LAIN-LAIN";
                    break;
                case "GNS MK PCH 1":
                case "GNS MK PCH 2":
                case "GNS MK PCH":
                case "GNS SGTG PCH":
                    kode = "PCH";
                    break;
                case "GNS ZT":
                    kode = "ZT";
                    break;
                case "GNS YT KCL":
                case "GNS YT SDG":
                case "GNS YT BSR":
                case "GNS YT KOTAK BESAR":
                    kode = "YT";
                    break;
                case "GNS RONT GRD":
                    kode = "RONTOKAN";
                    break;
                case "GNS JMB B":
                case "GNS SPR G":
                case "GNS PREM G":
                case "GNS STD G":
                case "GNS ZT B":
                case "GNS MK CUP B":
                case "GNS MK CUP B 1":
                case "GNS MK CUP B 2":
                case "GNS SGTG B":
                case "GNS STR JMR":
                case "GNS STR KNG":
                case "GNS DWI WRN SGTG":
                case "GNS KK KNG":
                case "GNS JMB KK KNG":
                case "GNS SPR KK KNG":
                case "GNS PREM KK KNG":
                case "GNS STD KK KNG":
                case "GNS OVAL 1 KK KNG":
                case "GNS OVAL 2 KK KNG":
                case "GNS SGTG KK KNG":
                case "GNS OVAL KK KNG":
                    kode = "KALAH WARNA";
                    break;
                default:
                    //System.out.println(list5.get(i).getKode_grade());                
                    kode = "-";
            }
            int nilaiSekarang = 0;
            try {
                nilaiSekarang = (int) (double) dataset9.getValue(kode) + berat_jadi;
            } catch (org.jfree.data.UnknownKeyException ex) {
                nilaiSekarang = berat_jadi;
            }
            dataset9.setValue(kode, nilaiSekarang);
            //row2[i][0] = list5.get(i).getKode_grade();
            //row2[i][1] = berat_jadi;

            tot_berat_jadi += berat_jadi;
        }
        Object[][] row2 = new Object[dataset9.getKeys().size()][3];
        for (int i = 0; i < row2.length; i++) {
            row2[i][0] = dataset9.getKeys().get(i);
            row2[i][1] = dataset9.getValue((String) dataset9.getKeys().get(i));
        }

        /*for (int i = 0; i < row2.length; i++) {
            row2[i][2] = ((float) (int) row2[i][1] / tot_berat_jadi) * (float) 100;
            model2.addRow(row2[i]);
        }*/
        for (int i = 0; i < row2.length; i++) {
            if (tot_berat_jadi == 0) {
                row2[i][2] = "0.00 %";
            } else {
                row2[i][2] = df.format(((float) (double) row2[i][1] / (float) tot_berat_jadi) * (float) 100) + " %";
            }
            model2.addRow(row2[i]);
        }
    }

    public void chart() {
        /*ChartFrame frame = new ChartFrame("Bar Chart For Laporan Produksi", chart);
        frame.setVisible(true);
        frame.setLocationRelativeTo(this);
        frame.setSize(450, 350);*/

        dataset2 = new TimeSeriesCollection();
        chart2 = ChartFactory.createTimeSeriesChart("Rata-Rata Rendemen", "Tanggal", "Persentase", dataset2, true, true, false);

        showTickMarksForSingleElements(dataset2, chart2);
        chart2.setBackgroundPaint(Color.WHITE);
        chart2.getTitle().setPaint(Color.red);
        ChartPanel panelChartRendemen = new ChartPanel(chart2);
        panelChartRendemen.setLocation(0, 0);
        panelChartRendemen.setSize(Panel_Line_Chart.getSize());
        Panel_Line_Chart.add(panelChartRendemen);

        final TimeSeries chart_rend_sh = new TimeSeries("Susut Hilang");
        final TimeSeries chart_rend_sp = new TimeSeries("Susut Proses");
        final TimeSeries chart_rend_utuh = new TimeSeries("Utuh");
        final TimeSeries chart_rend_flat = new TimeSeries("Flat");
        dataset2.addSeries(chart_rend_sh);
        dataset2.addSeries(chart_rend_sp);
        dataset2.addSeries(chart_rend_utuh);
        dataset2.addSeries(chart_rend_flat);
        XYPlot plot2 = chart2.getXYPlot();
        LegendItemCollection legendItems2 = plot2.getLegendItems();
        plot2.setFixedLegendItems(legendItems2);
        panelChartRendemen.addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseClicked(ChartMouseEvent cme) {
                if (cme.getEntity() != null) {
                    if (!(cme.getEntity() instanceof LegendItemEntity)) {
                        XYItemEntity entity = (XYItemEntity) cme.getEntity();
                        /*System.out.println("Entity: " + cme.getEntity());
                        System.out.println(cme.getEntity().getToolTipText());
                        System.out.println("Entity serieskey: " + entity.getItem());
                        System.out.println("Entity seriesindex: " + entity.getSeriesIndex());
                        System.out.println("Entity dataset: " + entity.getDataset());
                        System.out.println("Entity dataset: " + entity.getDataset().getSeriesKey(entity.getSeriesIndex()));
                        System.out.println("Entity dataset: " + new Date((long) entity.getDataset().getXValue(entity.getSeriesIndex(), entity.getItem())).toString());
                        System.out.println("Entity dataset: " + entity.getDataset().getYValue(entity.getSeriesIndex(), entity.getItem()));*/
                        float bk, fbonus, fnol, utuh, kaki, netto, flat, sp, sh;

                        label_tipe_lineChart.setText((String) entity.getDataset().getSeriesKey(entity.getSeriesIndex()));
                        label_tanggal.setText(new SimpleDateFormat("dd MMMM yyyy").format(new Date((long) entity.getDataset().getXValue(entity.getSeriesIndex(), entity.getItem()))));
                        label_persentase_lineChart.setText(decimalFormat.format(entity.getDataset().getYValue(entity.getSeriesIndex(), entity.getItem())));
                        switch (entity.getSeriesIndex()) {
                            case 0: //Susut Hilang
                                bk = list.get(entity.getItem()).getBerat_Kering();
                                fbonus = list.get(entity.getItem()).getFbonus_berat();
                                fnol = list.get(entity.getItem()).getFnol_berat();
                                utuh = fbonus + fnol;
                                kaki = list.get(entity.getItem()).getTambah_kaki1() + list.get(entity.getItem()).getTambah_kaki2();
                                netto = utuh - kaki;
                                flat = list.get(entity.getItem()).getFlat_berat() + list.get(entity.getItem()).getPecah_berat();
                                sp = list.get(entity.getItem()).getSesekan() + list.get(entity.getItem()).getHancuran() + list.get(entity.getItem()).getRontokan() + list.get(entity.getItem()).getBonggol() + list.get(entity.getItem()).getSerabut();
                                sh = bk - (netto + flat + sp);
                                label_berat_lineChart.setText(decimalFormat.format(sh));
                                break;
                            case 1: //Susut Proses
                                sp = list.get(entity.getItem()).getSesekan() + list.get(entity.getItem()).getHancuran() + list.get(entity.getItem()).getRontokan() + list.get(entity.getItem()).getBonggol() + list.get(entity.getItem()).getSerabut();
                                label_berat_lineChart.setText(decimalFormat.format(sp));
                                break;
                            case 2: //Utuh
                                fbonus = list.get(entity.getItem()).getFbonus_berat();
                                fnol = list.get(entity.getItem()).getFnol_berat();
                                utuh = fbonus + fnol;
                                label_berat_lineChart.setText(decimalFormat.format(utuh));
                                break;
                            case 3: //Flat
                                flat = list.get(entity.getItem()).getFlat_berat() + list.get(entity.getItem()).getPecah_berat();
                                label_berat_lineChart.setText(decimalFormat.format(flat));
                                break;
                        }

                    } else {
                        ChartEntity entity = cme.getEntity();
                        LegendItemEntity itemEntity = (LegendItemEntity) entity;
                        XYDataset dataset = (XYDataset) itemEntity.getDataset();
                        int index = dataset.indexOf(itemEntity.getSeriesKey());
                        XYPlot plot = (XYPlot) cme.getChart().getPlot();

                        //set the renderer to hide the series
                        XYItemRenderer renderer = plot.getRenderer();
                        renderer.setSeriesVisible(index, !renderer.isSeriesVisible(index), true);
                    }
                }
            }

            @Override

            public void chartMouseMoved(ChartMouseEvent cme) {

            }
        }
        );
        dataset3 = new DefaultPieDataset();
        JFreeChart chart3 = ChartFactory.createPieChart("Rata-Rata Rendemen Total", dataset3, true, true, false);
        chart3.setBackgroundPaint(Color.WHITE);
        chart3.getTitle().setPaint(Color.red);
        PiePlot pp = (PiePlot) chart3.getPlot();
        pp.setSectionPaint("Susut Hilang", Color.red);
        pp.setSectionPaint("Susut Proses", Color.green);
        pp.setSectionPaint("Utuh", Color.blue);
        pp.setSectionPaint("Flat", Color.yellow);
        ChartPanel panelChartRendemenTotal = new ChartPanel(chart3);
        panelChartRendemenTotal.setLocation(0, 0);
        panelChartRendemenTotal.setSize(Panel_Pie_Chart.getSize());
        Panel_Pie_Chart.add(panelChartRendemenTotal);
        panelChartRendemenTotal.addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseClicked(ChartMouseEvent cme) {
                if (cme.getEntity() != null) {
                    if (!(cme.getEntity() instanceof LegendItemEntity)) {

                    }
                }
            }

            @Override
            public void chartMouseMoved(ChartMouseEvent cme) {

            }
        });
        dataset4 = new DefaultPieDataset();
        chart4 = ChartFactory.createPieChart("Data Masuk Karyawan Total", dataset4, true, true, false);
        chart4.setBackgroundPaint(Color.WHITE);
        chart4.getTitle().setPaint(Color.red);
        PiePlot pp2 = (PiePlot) chart4.getPlot();
        pp2.setLabelGenerator(null);
        pp2.setSectionPaint("LABORATORIUM", Color.black);
        ChartPanel panelChartMasukKaryawanTotal = new ChartPanel(chart4);
        panelChartMasukKaryawanTotal.setLocation(0, 0);
        panelChartMasukKaryawanTotal.setSize(Panel_chart2.getSize());
        Panel_chart2.add(panelChartMasukKaryawanTotal);

        panelChartMasukKaryawanTotal.addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseClicked(ChartMouseEvent cme) {
                if (cme.getEntity() != null) {
                    if (!(cme.getEntity() instanceof LegendItemEntity)) {
                        PieSectionEntity entity = (PieSectionEntity) cme.getEntity();
                        /*System.out.println("Entity: " + cme.getEntity());
                        System.out.println(cme.getEntity().getToolTipText());
                        System.out.println("Entity sectionindex: " + entity.getSectionIndex());
                        System.out.println("Entity pieindex: " + entity.getPieIndex());
                        System.out.println("Entity dataset: " + entity.getDataset());
                        System.out.println("Entity data: " + entity.getDataset().getKey(entity.getSectionIndex()));
                        System.out.println("Entity data: " + entity.getDataset().getValue(entity.getSectionIndex()));*/
                        String namaBagian[] = new String[]{
                            "LABORATORIUM",
                            "SANITASI",
                            "MAINTENANCE",
                            "TRAINING DEVELOPMENT",
                            "RECRUITMENT",
                            "CUCI BM",
                            "GRADING BM",
                            "ACCOUNTING",
                            "RECRUITMENT",
                            "PURCHASING",
                            "CUCI ALAT",
                            "CABUT A",
                            "CABUT B",
                            "CABUT C",
                            "KOREKSI CABUT A",
                            "KOREKSI CABUT B",
                            "KOREKSI CABUT C",
                            "PENGAWAS A",
                            "PENGAWAS B",
                            "PENGAWAS C",
                            "ASISTEN PENGAWAS A",
                            "ASISTEN PENGAWAS B",
                            "ASISTEN PENGAWAS C",
                            "CETAK A",
                            "FINISHING 2",
                            "GRADING BJ"
                        };
                        int cewek = 0;
                        int cowok = 0;

                        for (int i = 0; i < list2.size(); i++) {
                            if (list2.get(i).getKode_bagian().equalsIgnoreCase(namaBagian[entity.getSectionIndex() + 1])) {
                                if (list2.get(i).getJenis_kelamin().equalsIgnoreCase("Perempuan")) {
                                    cewek++;
                                } else {
                                    cowok++;
                                }
                            }
                        }
                        label_bagian_pieChart1.setText((String) entity.getDataset().getKey(entity.getSectionIndex()));
                        label_persentase_pieChart1.setText(decimalFormat.format(entity.getDataset().getValue(entity.getSectionIndex())));
                        label_laki_pieChart1.setText("" + cowok);
                        label_perempuan_pieChart1.setText("" + cewek);
                        label_total_pieChart1.setText("" + (cowok + cewek));
                    }
                }
            }

            @Override
            public void chartMouseMoved(ChartMouseEvent cme) {

            }
        });
        dataset5 = new DefaultPieDataset();
        JFreeChart chart5 = ChartFactory.createPieChart("Uji QC Laporan Produksi", dataset5, true, true, false);
        chart5.setBackgroundPaint(Color.WHITE);
        chart5.getTitle().setPaint(Color.red);
        PiePlot pp3 = (PiePlot) chart5.getPlot();
        pp3.setSectionPaint("Passed", Color.red);
        pp3.setSectionPaint("Hold", Color.green);
        ChartPanel panelChartUjiQCLaporanProduksi = new ChartPanel(chart5);
        panelChartUjiQCLaporanProduksi.setLocation(0, 0);
        panelChartUjiQCLaporanProduksi.setSize(Panel_chart3.getSize());
        Panel_chart3.add(panelChartUjiQCLaporanProduksi);
        panelChartUjiQCLaporanProduksi.addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseClicked(ChartMouseEvent cme) {
                if (cme.getEntity() != null) {
                    if (!(cme.getEntity() instanceof LegendItemEntity)) {

                    }
                }
            }

            @Override
            public void chartMouseMoved(ChartMouseEvent cme) {
                //System.out.println("a");
            }
        });
        dataset6 = new DefaultCategoryDataset();
        chart6 = ChartFactory.createBarChart("Progress LP", "", "Berat", dataset6, PlotOrientation.VERTICAL, true, true, false);
        chart6.setBackgroundPaint(Color.WHITE);
        chart6.getTitle().setPaint(Color.red);
        CategoryPlot cp4 = (CategoryPlot) chart6.getPlot();
        cp4.setBackgroundPaint(SystemColor.inactiveCaption);
        cp4.setRenderer(new BarRenderer() {
            @Override
            public Paint getItemPaint(final int row, final int column) {
                float max = 0;
                for (int i = 0; i < dataset6.getRowCount(); i++) {
                    for (int j = 0; j < dataset6.getColumnCount(); j++) {
                        if (dataset6.getValue(i, j).floatValue() > max) {
                            max = dataset6.getValue(i, j).floatValue();
                        }
                    }
                }
                float valueColor = 255 * dataset6.getValue(row, column).floatValue() / max;
                if (valueColor >= 255) {
                    valueColor = 255;
                } else {
                    valueColor = valueColor % 256;
                }
//                Color a = new Color((int) 255 - (int) valueColor, (int) valueColor, 0);
                Color a = new Color(0, (int) valueColor, 0);
                return a;
            }
        });
        ((BarRenderer) cp4.getRenderer()).setBarPainter(new StandardBarPainter());
        /*BarRenderer r4 = (BarRenderer) chart6.getCategoryPlot().getRenderer();
        r4.setSeriesPaint(0, Color.cyan);
        r4.setSeriesPaint(1, Color.magenta);
        r4.setSeriesPaint(2, Color.green);
        r4.setSeriesPaint(3, Color.yellow);
        r4.setSeriesPaint(4, Color.red);
        r4.setSeriesPaint(5, Color.blue);*/
        ChartPanel panelChartProgressLP = new ChartPanel(chart6);
        panelChartProgressLP.setLocation(0, 0);
        panelChartProgressLP.setSize(Panel_chart4.getSize());
        Panel_chart4.add(panelChartProgressLP);
        panelChartProgressLP.addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseClicked(ChartMouseEvent cme) {
                if (cme.getEntity() != null) {
                    if (!(cme.getEntity() instanceof LegendItemEntity)) {

                    }
                }
            }

            @Override
            public void chartMouseMoved(ChartMouseEvent cme) {

            }
        });

        dataset7 = new DefaultPieDataset();
        JFreeChart chart7 = ChartFactory.createPieChart("Stok Bahan Baku", dataset7, true, true, false);
        chart7.setBackgroundPaint(Color.WHITE);
        chart7.getTitle().setPaint(Color.red);
        /*PiePlot pp2 = (PiePlot) chart7.getPlot();
        pp2.setSectionPaint("Susut Hilang", Color.red);
        pp2.setSectionPaint("Susut Proses", Color.green);
        pp2.setSectionPaint("Utuh", Color.blue);
        pp2.setSectionPaint("Flat", Color.yellow);*/
        ChartPanel panelChartStokBahanBaku = new ChartPanel(chart7);
        panelChartStokBahanBaku.setLocation(0, 0);
        panelChartStokBahanBaku.setSize(Panel_Pie_Chart1.getSize());
        Panel_Pie_Chart1.add(panelChartStokBahanBaku);
        panelChartStokBahanBaku.addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseClicked(ChartMouseEvent cme) {
                if (cme.getEntity() != null) {
                    if (!(cme.getEntity() instanceof LegendItemEntity)) {

                    }
                }
            }

            @Override
            public void chartMouseMoved(ChartMouseEvent cme) {

            }
        });
        dataset8 = new DefaultPieDataset();
        JFreeChart chart8 = ChartFactory.createPieChart("Hasil Produksi Baku", dataset8, true, true, false);
        chart8.setBackgroundPaint(Color.WHITE);
        chart8.getTitle().setPaint(Color.red);
        ChartPanel panelChartHasilProduksiBaku = new ChartPanel(chart8);
        panelChartHasilProduksiBaku.setLocation(0, 0);
        panelChartHasilProduksiBaku.setSize(Panel_Pie_Chart2.getSize());
        Panel_Pie_Chart2.add(panelChartHasilProduksiBaku);
        panelChartHasilProduksiBaku.addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseClicked(ChartMouseEvent cme) {
                if (cme.getEntity() != null) {
                    if (!(cme.getEntity() instanceof LegendItemEntity)) {

                    }
                }
            }

            @Override
            public void chartMouseMoved(ChartMouseEvent cme) {

            }
        });
        dataset9 = new DefaultPieDataset();
        JFreeChart chart9 = ChartFactory.createPieChart("Hasil Produksi Jadi", dataset9, true, true, false);
        chart9.setBackgroundPaint(Color.WHITE);
        chart9.getTitle().setPaint(Color.red);
        ChartPanel panelChartHasilProduksiJadi = new ChartPanel(chart9);
        panelChartHasilProduksiJadi.setLocation(0, 0);
        panelChartHasilProduksiJadi.setSize(Panel_Pie_Chart3.getSize());
        Panel_Pie_Chart3.add(panelChartHasilProduksiJadi);
        panelChartHasilProduksiJadi.addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseClicked(ChartMouseEvent cme) {
                if (cme.getEntity() != null) {
                    if (!(cme.getEntity() instanceof LegendItemEntity)) {

                    }
                }
            }

            @Override
            public void chartMouseMoved(ChartMouseEvent cme) {

            }
        });
    }

    private static void showTickMarksForSingleElements(XYDataset dataset, JFreeChart chart) {
        try {
            TimeSeriesCollection timeSeriesCollection
                    = (TimeSeriesCollection) dataset;
            List<?> series = timeSeriesCollection.getSeries();
            for (int i = 0; i < series.size(); i++) {
                //TimeSeries timeSeries = (TimeSeries) series.get(i);
                //if (timeSeries.getItemCount() == 1) {
                XYPlot plot = (XYPlot) chart.getPlot();
                XYLineAndShapeRenderer renderer
                        = (XYLineAndShapeRenderer) plot.getRenderer();
                renderer.setSeriesShapesVisible(i, true);
                //}
            }
        } catch (Exception ex) {

        }
    }

    public void refreshChart_Rendemen() {
        DefaultTableModel model = (DefaultTableModel) Table_Setoran_harian_f2.getModel();
        model.setRowCount(0);
        show_data_setoran();
        ColumnsAutoSizer.sizeColumnsToFit(Table_Setoran_harian_f2);

        TableAlignment.setHorizontalAlignment(JLabel.CENTER);
        //tabel Data Bahan Baku
        for (int i = 0; i < Table_Setoran_harian_f2.getColumnCount(); i++) {
            Table_Setoran_harian_f2.getColumnModel().getColumn(i).setCellRenderer(TableAlignment);
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
        jPanel2 = new javax.swing.JPanel();
        button_search_dataMasuk = new javax.swing.JButton();
        Date_DataMasuk2 = new com.toedter.calendar.JDateChooser();
        Date_DataMasuk = new com.toedter.calendar.JDateChooser();
        jLabel14 = new javax.swing.JLabel();
        Panel_chart2 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        label_bagian_pieChart1 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        label_laki_pieChart1 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        label_perempuan_pieChart1 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        label_persentase_pieChart1 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        label_total_data_pieChart1 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        label_total_pieChart1 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        label_total_laki_pieChart1 = new javax.swing.JLabel();
        label_total_perempuan_pieChart1 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        Panel_chart3 = new javax.swing.JPanel();
        button_search_ujiQCLP = new javax.swing.JButton();
        jLabel28 = new javax.swing.JLabel();
        Date_QCLP1 = new com.toedter.calendar.JDateChooser();
        Date_QCLP2 = new com.toedter.calendar.JDateChooser();
        jLabel31 = new javax.swing.JLabel();
        label_qclp_passed = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        label_qclp_persentase_passed = new javax.swing.JLabel();
        label_qclp_persentase_hold = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        label_qclp_hold = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        label_qclp_total = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        Date_Setoran = new com.toedter.calendar.JDateChooser();
        jLabel29 = new javax.swing.JLabel();
        button_search_LPSetoran = new javax.swing.JButton();
        button_search_LPTandon = new javax.swing.JButton();
        Date_Tandon = new com.toedter.calendar.JDateChooser();
        jLabel30 = new javax.swing.JLabel();
        Panel_chart4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_detail_tandon = new javax.swing.JTable();
        jLabel43 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        ComboBox_ruangan = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        Date_Setoran1 = new com.toedter.calendar.JDateChooser();
        Date_Setoran2 = new com.toedter.calendar.JDateChooser();
        button_search_setoran = new javax.swing.JButton();
        ComboBox_grade = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        Panel_Pie_Chart = new javax.swing.JPanel();
        Panel_Line_Chart = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        label_tipe_lineChart = new javax.swing.JLabel();
        label_tanggal = new javax.swing.JLabel();
        label_berat_lineChart = new javax.swing.JLabel();
        label_persentase_lineChart = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        label_total_data_lineChart = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        label_tipe_pieChart = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        label_berat_pieChart = new javax.swing.JLabel();
        label_persentase_pieChart = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        label_total_data_pieCHart = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel36 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        Date_filter1 = new com.toedter.calendar.JDateChooser();
        button_search = new javax.swing.JButton();
        Panel_Pie_Chart1 = new javax.swing.JPanel();
        jLabel39 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        Table_stok_bahan_baku = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        jLabel37 = new javax.swing.JLabel();
        Date_filter2 = new com.toedter.calendar.JDateChooser();
        button_search1 = new javax.swing.JButton();
        jLabel40 = new javax.swing.JLabel();
        Panel_Pie_Chart2 = new javax.swing.JPanel();
        jLabel41 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        Table_hasil_produksi_baku = new javax.swing.JTable();
        Panel_Pie_Chart3 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        Table_hasil_produksi_jadi = new javax.swing.JTable();
        jLabel42 = new javax.swing.JLabel();
        Date_filter3 = new com.toedter.calendar.JDateChooser();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jTabbedPane1.setBackground(new java.awt.Color(162, 155, 254));
        jTabbedPane1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jPanel2.setBackground(new java.awt.Color(162, 155, 254));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        button_search_dataMasuk.setBackground(new java.awt.Color(255, 255, 255));
        button_search_dataMasuk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_dataMasuk.setText("Search");
        button_search_dataMasuk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_dataMasukActionPerformed(evt);
            }
        });

        Date_DataMasuk2.setBackground(new java.awt.Color(255, 255, 255));
        Date_DataMasuk2.setDate(new Date());
        Date_DataMasuk2.setDateFormatString("dd MMMM yyyy");
        Date_DataMasuk2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_DataMasuk.setBackground(new java.awt.Color(255, 255, 255));
        Date_DataMasuk.setDate(new Date(new Date().getTime()- (7 * 24 * 60 * 60 * 1000)));
        Date_DataMasuk.setDateFormatString("dd MMMM yyyy");
        Date_DataMasuk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Tanggal :");

        Panel_chart2.setBackground(new java.awt.Color(255, 255, 255));
        Panel_chart2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        javax.swing.GroupLayout Panel_chart2Layout = new javax.swing.GroupLayout(Panel_chart2);
        Panel_chart2.setLayout(Panel_chart2Layout);
        Panel_chart2Layout.setHorizontalGroup(
            Panel_chart2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 460, Short.MAX_VALUE)
        );
        Panel_chart2Layout.setVerticalGroup(
            Panel_chart2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 456, Short.MAX_VALUE)
        );

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel15.setText("Data Masuk Karyawan Baru");

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel16.setText("Bagian :");

        label_bagian_pieChart1.setBackground(new java.awt.Color(255, 255, 255));
        label_bagian_pieChart1.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_bagian_pieChart1.setText("-");

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel17.setText("Laki-laki");

        label_laki_pieChart1.setBackground(new java.awt.Color(255, 255, 255));
        label_laki_pieChart1.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_laki_pieChart1.setText("-");

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel18.setText("Perempuan");

        label_perempuan_pieChart1.setBackground(new java.awt.Color(255, 255, 255));
        label_perempuan_pieChart1.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_perempuan_pieChart1.setText("-");

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel20.setText("Persentase (%) :");

        label_persentase_pieChart1.setBackground(new java.awt.Color(255, 255, 255));
        label_persentase_pieChart1.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_persentase_pieChart1.setText("-");

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel22.setText("Total Data :");

        label_total_data_pieChart1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_pieChart1.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_data_pieChart1.setText("-");

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel23.setText("Total");

        label_total_pieChart1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_pieChart1.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_pieChart1.setText("-");

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel24.setText("Total Laki :");

        label_total_laki_pieChart1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_laki_pieChart1.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_laki_pieChart1.setText("-");

        label_total_perempuan_pieChart1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_perempuan_pieChart1.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_perempuan_pieChart1.setText("-");

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel25.setText("Total Perempuan :");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(Panel_chart2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_bagian_pieChart1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_laki_pieChart1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_perempuan_pieChart1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_persentase_pieChart1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel22)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data_pieChart1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel23)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_pieChart1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel24)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_laki_pieChart1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel25)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_perempuan_pieChart1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_DataMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_DataMasuk2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search_dataMasuk)))
                .addContainerGap(705, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(Date_DataMasuk2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search_dataMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_DataMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Panel_chart2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_bagian_pieChart1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_persentase_pieChart1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_laki_pieChart1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_perempuan_pieChart1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_pieChart1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_laki_pieChart1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_perempuan_pieChart1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_data_pieChart1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Data Masuk Karyawan", jPanel2);

        jPanel3.setBackground(new java.awt.Color(162, 155, 254));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel26.setText("Uji QC Laporan Produksi");

        Panel_chart3.setBackground(new java.awt.Color(255, 255, 255));
        Panel_chart3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        javax.swing.GroupLayout Panel_chart3Layout = new javax.swing.GroupLayout(Panel_chart3);
        Panel_chart3.setLayout(Panel_chart3Layout);
        Panel_chart3Layout.setHorizontalGroup(
            Panel_chart3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 460, Short.MAX_VALUE)
        );
        Panel_chart3Layout.setVerticalGroup(
            Panel_chart3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 436, Short.MAX_VALUE)
        );

        button_search_ujiQCLP.setBackground(new java.awt.Color(255, 255, 255));
        button_search_ujiQCLP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_ujiQCLP.setText("Search");
        button_search_ujiQCLP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_ujiQCLPActionPerformed(evt);
            }
        });

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel28.setText("Tanggal :");

        Date_QCLP1.setBackground(new java.awt.Color(255, 255, 255));
        Date_QCLP1.setDate(new Date(new Date().getTime()- (7 * 24 * 60 * 60 * 1000)));
        Date_QCLP1.setDateFormatString("dd MMMM yyyy");
        Date_QCLP1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_QCLP2.setBackground(new java.awt.Color(255, 255, 255));
        Date_QCLP2.setDate(new Date());
        Date_QCLP2.setDateFormatString("dd MMMM yyyy");
        Date_QCLP2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel31.setBackground(new java.awt.Color(255, 255, 255));
        jLabel31.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel31.setText("Passed");

        label_qclp_passed.setBackground(new java.awt.Color(255, 255, 255));
        label_qclp_passed.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_qclp_passed.setText("-");

        jLabel32.setBackground(new java.awt.Color(255, 255, 255));
        jLabel32.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel32.setText("Persentase Passed (%)");

        label_qclp_persentase_passed.setBackground(new java.awt.Color(255, 255, 255));
        label_qclp_persentase_passed.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_qclp_persentase_passed.setText("-");

        label_qclp_persentase_hold.setBackground(new java.awt.Color(255, 255, 255));
        label_qclp_persentase_hold.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_qclp_persentase_hold.setText("-");

        jLabel33.setBackground(new java.awt.Color(255, 255, 255));
        jLabel33.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel33.setText("Persentase Hold (%)");

        label_qclp_hold.setBackground(new java.awt.Color(255, 255, 255));
        label_qclp_hold.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_qclp_hold.setText("-");

        jLabel34.setBackground(new java.awt.Color(255, 255, 255));
        jLabel34.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel34.setText("Hold");

        jLabel35.setBackground(new java.awt.Color(255, 255, 255));
        jLabel35.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel35.setText("Total");

        label_qclp_total.setBackground(new java.awt.Color(255, 255, 255));
        label_qclp_total.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_qclp_total.setText("-");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel26)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(Panel_chart3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel31)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_qclp_passed, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel32)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_qclp_persentase_passed, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel34)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_qclp_hold, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel33)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_qclp_persentase_hold, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel35)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_qclp_total, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_QCLP1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_QCLP2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search_ujiQCLP)))
                .addContainerGap(682, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel26)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(Date_QCLP2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_QCLP1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search_ujiQCLP))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Panel_chart3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_qclp_passed, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_qclp_persentase_passed, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_qclp_hold, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_qclp_persentase_hold, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_qclp_total, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(163, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Uji QC Laporan PRoduksi", jPanel3);

        jPanel4.setBackground(new java.awt.Color(162, 155, 254));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        jLabel27.setBackground(new java.awt.Color(255, 255, 255));
        jLabel27.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel27.setText("Progress LP");

        Date_Setoran.setBackground(new java.awt.Color(255, 255, 255));
        Date_Setoran.setDate(new Date(new Date().getTime()- (7 * 24 * 60 * 60 * 1000)));
        Date_Setoran.setDateFormatString("dd MMMM yyyy");
        Date_Setoran.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel29.setBackground(new java.awt.Color(255, 255, 255));
        jLabel29.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel29.setText("Tanggal setoran:");

        button_search_LPSetoran.setBackground(new java.awt.Color(255, 255, 255));
        button_search_LPSetoran.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_LPSetoran.setText("Refresh");
        button_search_LPSetoran.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_LPSetoranActionPerformed(evt);
            }
        });

        button_search_LPTandon.setBackground(new java.awt.Color(255, 255, 255));
        button_search_LPTandon.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_LPTandon.setText("Refresh");
        button_search_LPTandon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_LPTandonActionPerformed(evt);
            }
        });

        Date_Tandon.setBackground(new java.awt.Color(255, 255, 255));
        Date_Tandon.setDate(new Date(new Date().getTime()- (7 * 24 * 60 * 60 * 1000)));
        Date_Tandon.setDateFormatString("dd MMMM yyyy");
        Date_Tandon.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel30.setBackground(new java.awt.Color(255, 255, 255));
        jLabel30.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel30.setText("Tanggal tandon:");

        Panel_chart4.setBackground(new java.awt.Color(255, 255, 255));
        Panel_chart4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        javax.swing.GroupLayout Panel_chart4Layout = new javax.swing.GroupLayout(Panel_chart4);
        Panel_chart4.setLayout(Panel_chart4Layout);
        Panel_chart4Layout.setHorizontalGroup(
            Panel_chart4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 875, Short.MAX_VALUE)
        );
        Panel_chart4Layout.setVerticalGroup(
            Panel_chart4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 554, Short.MAX_VALUE)
        );

        Table_detail_tandon.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_detail_tandon.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Departemen", "Jumlah LP", "Total Keping", "Total Gram Basah"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Float.class
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
        jScrollPane1.setViewportView(Table_detail_tandon);

        jLabel43.setBackground(new java.awt.Color(255, 255, 255));
        jLabel43.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel43.setText("Keterangan :");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Panel_chart4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel27)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel29)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_Setoran, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search_LPSetoran))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel30)
                                .addGap(8, 8, 8)
                                .addComponent(Date_Tandon, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search_LPTandon)))
                        .addGap(0, 563, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel43))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel27)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(Date_Setoran, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search_LPSetoran))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(Date_Tandon, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search_LPTandon))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Panel_chart4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel43)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Progress LP", jPanel4);

        jPanel6.setBackground(new java.awt.Color(162, 155, 254));
        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102), 2));
        jPanel6.setPreferredSize(new java.awt.Dimension(500, 150));

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

        Date_Setoran2.setBackground(new java.awt.Color(255, 255, 255));
        Date_Setoran2.setDate(new Date());
        Date_Setoran2.setDateFormatString("dd MMMM yyyy");
        Date_Setoran2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_search_setoran.setBackground(new java.awt.Color(255, 255, 255));
        button_search_setoran.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_setoran.setText("Search");
        button_search_setoran.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_setoranActionPerformed(evt);
            }
        });

        ComboBox_grade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_grade.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Grade :");

        Panel_Pie_Chart.setBackground(new java.awt.Color(255, 255, 255));
        Panel_Pie_Chart.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        javax.swing.GroupLayout Panel_Pie_ChartLayout = new javax.swing.GroupLayout(Panel_Pie_Chart);
        Panel_Pie_Chart.setLayout(Panel_Pie_ChartLayout);
        Panel_Pie_ChartLayout.setHorizontalGroup(
            Panel_Pie_ChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 417, Short.MAX_VALUE)
        );
        Panel_Pie_ChartLayout.setVerticalGroup(
            Panel_Pie_ChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        Panel_Line_Chart.setBackground(new java.awt.Color(255, 255, 255));
        Panel_Line_Chart.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        javax.swing.GroupLayout Panel_Line_ChartLayout = new javax.swing.GroupLayout(Panel_Line_Chart);
        Panel_Line_Chart.setLayout(Panel_Line_ChartLayout);
        Panel_Line_ChartLayout.setHorizontalGroup(
            Panel_Line_ChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 415, Short.MAX_VALUE)
        );
        Panel_Line_ChartLayout.setVerticalGroup(
            Panel_Line_ChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Tipe :");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Tanggal :");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Berat (Gram) :");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Persentase (%) :");

        label_tipe_lineChart.setBackground(new java.awt.Color(255, 255, 255));
        label_tipe_lineChart.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_tipe_lineChart.setText("-");

        label_tanggal.setBackground(new java.awt.Color(255, 255, 255));
        label_tanggal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_tanggal.setText("-");

        label_berat_lineChart.setBackground(new java.awt.Color(255, 255, 255));
        label_berat_lineChart.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_berat_lineChart.setText("-");

        label_persentase_lineChart.setBackground(new java.awt.Color(255, 255, 255));
        label_persentase_lineChart.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_persentase_lineChart.setText("-");

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("Total Data :");

        label_total_data_lineChart.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_lineChart.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_data_lineChart.setText("-");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Tipe :");

        label_tipe_pieChart.setBackground(new java.awt.Color(255, 255, 255));
        label_tipe_pieChart.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_tipe_pieChart.setText("-");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("Berat (Gram) :");

        label_berat_pieChart.setBackground(new java.awt.Color(255, 255, 255));
        label_berat_pieChart.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_berat_pieChart.setText("-");

        label_persentase_pieChart.setBackground(new java.awt.Color(255, 255, 255));
        label_persentase_pieChart.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_persentase_pieChart.setText("-");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Persentase (%) :");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Total Data :");

        label_total_data_pieCHart.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_pieCHart.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_data_pieCHart.setText("-");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel9.setText("Data Setoran Diterima Grading Barang Jadi");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(Panel_Line_Chart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_tipe_lineChart))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_tanggal))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_berat_lineChart))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_persentase_lineChart))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data_lineChart)))
                        .addGap(18, 18, 18)
                        .addComponent(Panel_Pie_Chart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_tipe_pieChart))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_berat_pieChart))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_persentase_pieChart))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data_pieCHart))))
                    .addComponent(jLabel9)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Setoran1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Setoran2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search_setoran))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_grade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(303, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Setoran1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search_setoran, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Setoran2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Panel_Line_Chart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Panel_Pie_Chart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_tipe_lineChart, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_berat_lineChart, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_persentase_lineChart, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_total_data_lineChart, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_tipe_pieChart, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_berat_pieChart, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_persentase_pieChart, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_total_data_pieCHart, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 437, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Data Setoran Diterima Grading Barang Jadi", jPanel6);

        jPanel5.setBackground(new java.awt.Color(162, 155, 254));
        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        jLabel36.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel36.setText("Stok Bahan Baku");

        jLabel38.setBackground(new java.awt.Color(255, 255, 255));
        jLabel38.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel38.setText("Data Pada Tanggal :");

        Date_filter1.setBackground(new java.awt.Color(255, 255, 255));
        Date_filter1.setDate(today);
        Date_filter1.setDateFormatString("dd MMMM yyyy");
        Date_filter1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_search.setBackground(new java.awt.Color(255, 255, 255));
        button_search.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search.setText("Search");
        button_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_searchActionPerformed(evt);
            }
        });

        Panel_Pie_Chart1.setBackground(new java.awt.Color(255, 255, 255));
        Panel_Pie_Chart1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        javax.swing.GroupLayout Panel_Pie_Chart1Layout = new javax.swing.GroupLayout(Panel_Pie_Chart1);
        Panel_Pie_Chart1.setLayout(Panel_Pie_Chart1Layout);
        Panel_Pie_Chart1Layout.setHorizontalGroup(
            Panel_Pie_Chart1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 417, Short.MAX_VALUE)
        );
        Panel_Pie_Chart1Layout.setVerticalGroup(
            Panel_Pie_Chart1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel39.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel39.setText("Keterangan :");

        Table_stok_bahan_baku.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_stok_bahan_baku.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kategori", "Sisa Kpg", "Sisa Gram", "Persentase"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Float.class
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
        jScrollPane3.setViewportView(Table_stok_bahan_baku);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel36)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel38)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_filter1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(Panel_Pie_Chart1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel39)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(468, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel36)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(Date_filter1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Panel_Pie_Chart1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel39)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Stok Bahan Baku", jPanel5);

        jPanel7.setBackground(new java.awt.Color(162, 155, 254));
        jPanel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        jLabel37.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel37.setText("Hasil Produksi");

        Date_filter2.setBackground(new java.awt.Color(255, 255, 255));
        Date_filter2.setDate(today);
        Date_filter2.setDateFormatString("dd MMMM yyyy");
        Date_filter2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_search1.setBackground(new java.awt.Color(255, 255, 255));
        button_search1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search1.setText("Search");
        button_search1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search1ActionPerformed(evt);
            }
        });

        jLabel40.setBackground(new java.awt.Color(255, 255, 255));
        jLabel40.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel40.setText("Data Pada Tanggal :");

        Panel_Pie_Chart2.setBackground(new java.awt.Color(255, 255, 255));
        Panel_Pie_Chart2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        javax.swing.GroupLayout Panel_Pie_Chart2Layout = new javax.swing.GroupLayout(Panel_Pie_Chart2);
        Panel_Pie_Chart2.setLayout(Panel_Pie_Chart2Layout);
        Panel_Pie_Chart2Layout.setHorizontalGroup(
            Panel_Pie_Chart2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 417, Short.MAX_VALUE)
        );
        Panel_Pie_Chart2Layout.setVerticalGroup(
            Panel_Pie_Chart2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel41.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel41.setText("Keterangan :");

        Table_hasil_produksi_baku.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_hasil_produksi_baku.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kategori", "Gram", "Persentase"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.String.class
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
        jScrollPane4.setViewportView(Table_hasil_produksi_baku);

        Panel_Pie_Chart3.setBackground(new java.awt.Color(255, 255, 255));
        Panel_Pie_Chart3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        javax.swing.GroupLayout Panel_Pie_Chart3Layout = new javax.swing.GroupLayout(Panel_Pie_Chart3);
        Panel_Pie_Chart3.setLayout(Panel_Pie_Chart3Layout);
        Panel_Pie_Chart3Layout.setHorizontalGroup(
            Panel_Pie_Chart3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 405, Short.MAX_VALUE)
        );
        Panel_Pie_Chart3Layout.setVerticalGroup(
            Panel_Pie_Chart3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        Table_hasil_produksi_jadi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_hasil_produksi_jadi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade", "Gram", "Persentase"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.String.class
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
        jScrollPane5.setViewportView(Table_hasil_produksi_jadi);

        jLabel42.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel42.setText("Keterangan :");

        Date_filter3.setBackground(new java.awt.Color(255, 255, 255));
        Date_filter3.setDate(today);
        Date_filter3.setDateFormatString("dd MMMM yyyy");
        Date_filter3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel37)
                    .addComponent(Panel_Pie_Chart2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel40)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_filter2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_filter3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(button_search1)
                                .addGap(115, 115, 115))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel41)
                                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(10, 10, 10)
                                .addComponent(Panel_Pie_Chart3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addComponent(jLabel42)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel37)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(Date_filter2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_search1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(Date_filter3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Panel_Pie_Chart2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel42)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane5))
                            .addComponent(Panel_Pie_Chart3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel41)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE)))))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Hasil Produksi", jPanel7);

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

    private void button_search_dataMasukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_dataMasukActionPerformed
        show_data_masukKaryawan();
    }//GEN-LAST:event_button_search_dataMasukActionPerformed

    private void button_search_ujiQCLPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_ujiQCLPActionPerformed
        show_data_qclp();
    }//GEN-LAST:event_button_search_ujiQCLPActionPerformed

    private void button_search_LPSetoranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_LPSetoranActionPerformed
        try {

            if (Date_Setoran.getDate() != null) {
                sql = "SELECT "
                        + "(SELECT COUNT(tgl_masuk_cuci) FROM `tb_laporan_produksi` LEFT JOIN `tb_cuci` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cuci`.`no_laporan_produksi` WHERE tgl_masuk_cuci='" + dateFormat.format(Date_Setoran.getDate()) + "') AS `cuci_lp`, "
                        + "(SELECT SUM(berat_basah) FROM `tb_laporan_produksi` LEFT JOIN `tb_cuci` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cuci`.`no_laporan_produksi` WHERE tgl_masuk_cuci='" + dateFormat.format(Date_Setoran.getDate()) + "') AS `cuci_basah`, "
                        + "(SELECT SUM(jumlah_keping) FROM `tb_laporan_produksi` LEFT JOIN `tb_cuci` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cuci`.`no_laporan_produksi` WHERE tgl_masuk_cuci='" + dateFormat.format(Date_Setoran.getDate()) + "') AS `cuci_keping`, "
                        + "(SELECT COUNT(tgl_mulai_cabut) FROM `tb_laporan_produksi` LEFT JOIN `tb_cabut` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cabut`.`no_laporan_produksi` WHERE tgl_setor_cabut='" + dateFormat.format(Date_Setoran.getDate()) + "') AS `cabut_lp`, "
                        + "(SELECT SUM(berat_basah) FROM `tb_laporan_produksi` LEFT JOIN `tb_cabut` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cabut`.`no_laporan_produksi` WHERE tgl_setor_cabut='" + dateFormat.format(Date_Setoran.getDate()) + "') AS `cabut_basah`, "
                        + "(SELECT SUM(jumlah_keping) FROM `tb_laporan_produksi` LEFT JOIN `tb_cabut` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cabut`.`no_laporan_produksi` WHERE tgl_setor_cabut='" + dateFormat.format(Date_Setoran.getDate()) + "') AS `cabut_keping`, "
                        + "(SELECT COUNT(tgl_mulai_cetak) FROM `tb_laporan_produksi` LEFT JOIN `tb_cetak` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cetak`.`no_laporan_produksi` WHERE tgl_selesai_cetak='" + dateFormat.format(Date_Setoran.getDate()) + "') AS `cetak_lp`, "
                        + "(SELECT SUM(berat_basah) FROM `tb_laporan_produksi` LEFT JOIN `tb_cetak` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cetak`.`no_laporan_produksi` WHERE tgl_selesai_cetak='" + dateFormat.format(Date_Setoran.getDate()) + "') AS `cetak_basah`, "
                        + "(SELECT SUM(jumlah_keping) FROM `tb_laporan_produksi` LEFT JOIN `tb_cetak` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cetak`.`no_laporan_produksi` WHERE tgl_selesai_cetak='" + dateFormat.format(Date_Setoran.getDate()) + "') AS `cetak_keping`, "
                        + "(SELECT COUNT(tgl_masuk_f2) FROM `tb_laporan_produksi` LEFT JOIN `tb_finishing_2` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_finishing_2`.`no_laporan_produksi` WHERE tgl_setor_f2='" + dateFormat.format(Date_Setoran.getDate()) + "') AS `f2_lp`, "
                        + "(SELECT SUM(berat_basah) FROM `tb_laporan_produksi` LEFT JOIN `tb_finishing_2` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_finishing_2`.`no_laporan_produksi` WHERE tgl_setor_f2='" + dateFormat.format(Date_Setoran.getDate()) + "') AS `f2_basah`, "
                        + "(SELECT SUM(jumlah_keping) FROM `tb_laporan_produksi` LEFT JOIN `tb_finishing_2` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_finishing_2`.`no_laporan_produksi` WHERE tgl_setor_f2='" + dateFormat.format(Date_Setoran.getDate()) + "') AS `f2_keping`, "
                        + "(SELECT COUNT(`tb_lab_laporan_produksi`.`tgl_masuk`) FROM `tb_laporan_produksi` LEFT JOIN `tb_lab_laporan_produksi` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_lab_laporan_produksi`.`no_laporan_produksi` WHERE `tb_lab_laporan_produksi`.`tgl_selesai`='" + dateFormat.format(Date_Setoran.getDate()) + "') AS `qc_lp`, "
                        + "(SELECT SUM(berat_basah) FROM `tb_laporan_produksi` LEFT JOIN `tb_lab_laporan_produksi` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_lab_laporan_produksi`.`no_laporan_produksi` WHERE `tb_lab_laporan_produksi`.`tgl_selesai`='" + dateFormat.format(Date_Setoran.getDate()) + "') AS `qc_basah`, "
                        + "(SELECT SUM(jumlah_keping) FROM `tb_laporan_produksi` LEFT JOIN `tb_lab_laporan_produksi` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_lab_laporan_produksi`.`no_laporan_produksi` WHERE `tb_lab_laporan_produksi`.`tgl_selesai`='" + dateFormat.format(Date_Setoran.getDate()) + "') AS `qc_keping`, "
                        + "(SELECT COUNT(tanggal_grading) FROM `tb_laporan_produksi` LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_bahan_jadi_masuk`.`kode_asal` WHERE tanggal_grading='" + dateFormat.format(Date_Setoran.getDate()) + "') AS `gbj_lp`, "
                        + "(SELECT SUM(berat_basah) FROM `tb_laporan_produksi` LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_bahan_jadi_masuk`.`kode_asal` WHERE tanggal_grading='" + dateFormat.format(Date_Setoran.getDate()) + "') AS `gbj_basah`, "
                        + "(SELECT SUM(jumlah_keping) FROM `tb_laporan_produksi` LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_bahan_jadi_masuk`.`kode_asal` WHERE tanggal_grading='" + dateFormat.format(Date_Setoran.getDate()) + "') AS `gbj_keping` FROM DUAL";

                rs = Utility.db.getStatement().executeQuery(sql);
                dataset6.clear();
                if (rs.next()) {
                    dataset6.setValue(rs.getInt("cuci_basah"), "Berat", "Cuci");
                    dataset6.setValue(rs.getInt("cabut_basah"), "Berat", "Cabut");
                    dataset6.setValue(rs.getInt("cetak_basah"), "Berat", "Cetak");
                    dataset6.setValue(rs.getInt("f2_basah"), "Berat", "F2");
                    dataset6.setValue(rs.getInt("qc_basah"), "Berat", "QC");
                    dataset6.setValue(rs.getInt("gbj_basah"), "Berat", "Grading BJ");

                    DefaultTableModel model = (DefaultTableModel) Table_detail_tandon.getModel();
                    model.setRowCount(0);
                    model.addRow(new Object[]{"Cuci", rs.getInt("cuci_lp"), rs.getInt("cuci_keping"), rs.getInt("cuci_basah")});
                    model.addRow(new Object[]{"Cabut", rs.getInt("cabut_lp"), rs.getInt("cabut_keping"), rs.getInt("cabut_basah")});
                    model.addRow(new Object[]{"Cetak", rs.getInt("cetak_lp"), rs.getInt("cetak_keping"), rs.getInt("cetak_basah")});
                    model.addRow(new Object[]{"Finishing 2", rs.getInt("f2_lp"), rs.getInt("f2_keping"), rs.getInt("f2_basah")});
                    model.addRow(new Object[]{"QC", rs.getInt("qc_lp"), rs.getInt("qc_keping"), rs.getInt("qc_basah")});
                    model.addRow(new Object[]{"Grading BJ", rs.getInt("gbj_lp"), rs.getInt("gbj_keping"), rs.getInt("gbj_basah")});
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_search_LPSetoranActionPerformed

    private void button_search_LPTandonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_LPTandonActionPerformed
        try {

            if (Date_Tandon.getDate() != null) {
                sql = "SELECT "
                        + "(SELECT COUNT(`tb_laporan_produksi`.`no_laporan_produksi`) FROM `tb_laporan_produksi` LEFT JOIN `tb_cuci` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cuci`.`no_laporan_produksi` WHERE cuci_diserahkan='-' OR cuci_diserahkan IS NULL) AS `cuci_lp`, "
                        + "(SELECT SUM(berat_basah) FROM `tb_laporan_produksi` LEFT JOIN `tb_cuci` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cuci`.`no_laporan_produksi` WHERE cuci_diserahkan='-' OR cuci_diserahkan IS NULL) AS `cuci_basah`, "
                        + "(SELECT SUM(jumlah_keping) FROM `tb_laporan_produksi` LEFT JOIN `tb_cuci` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cuci`.`no_laporan_produksi` WHERE cuci_diserahkan='-' OR cuci_diserahkan IS NULL) AS `cuci_keping`, "
                        + "(SELECT COUNT(`tb_laporan_produksi`.`no_laporan_produksi`) FROM `tb_laporan_produksi` LEFT JOIN `tb_cabut` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cabut`.`no_laporan_produksi` LEFT JOIN `tb_cuci` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cuci`.`no_laporan_produksi` WHERE `tgl_setor_cabut` IS NULL AND `tb_cuci`.`id_pegawai` IS NOT NULL) AS `cabut_lp`, "
                        + "(SELECT SUM(berat_basah) FROM `tb_laporan_produksi` LEFT JOIN `tb_cabut` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cabut`.`no_laporan_produksi` LEFT JOIN `tb_cuci` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cuci`.`no_laporan_produksi` WHERE `tgl_setor_cabut` IS NULL AND `tb_cuci`.`id_pegawai` IS NOT NULL) AS `cabut_basah`, "
                        + "(SELECT SUM(jumlah_keping) FROM `tb_laporan_produksi` LEFT JOIN `tb_cabut` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cabut`.`no_laporan_produksi` LEFT JOIN `tb_cuci` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cuci`.`no_laporan_produksi` WHERE `tgl_setor_cabut` IS NULL AND `tb_cuci`.`id_pegawai` IS NOT NULL) AS `cabut_keping`, "
                        + "(SELECT COUNT(`tb_laporan_produksi`.`no_laporan_produksi`) FROM `tb_laporan_produksi` LEFT JOIN `tb_cetak` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cetak`.`no_laporan_produksi` WHERE tgl_mulai_cetak='" + dateFormat.format(Date_Tandon.getDate()) + "') AS `cetak_lp`, "
                        + "(SELECT SUM(berat_basah) FROM `tb_laporan_produksi` LEFT JOIN `tb_cetak` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cetak`.`no_laporan_produksi` WHERE tgl_mulai_cetak='" + dateFormat.format(Date_Tandon.getDate()) + "') AS `cetak_basah`, "
                        + "(SELECT SUM(jumlah_keping) FROM `tb_laporan_produksi` LEFT JOIN `tb_cetak` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cetak`.`no_laporan_produksi` WHERE tgl_mulai_cetak='" + dateFormat.format(Date_Tandon.getDate()) + "') AS `cetak_keping`, "
                        + "(SELECT COUNT(`tb_laporan_produksi`.`no_laporan_produksi`) FROM `tb_laporan_produksi` LEFT JOIN `tb_finishing_2` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_finishing_2`.`no_laporan_produksi` WHERE tgl_masuk_f2='" + dateFormat.format(Date_Tandon.getDate()) + "') AS `f2_lp`, "
                        + "(SELECT SUM(berat_basah) FROM `tb_laporan_produksi` LEFT JOIN `tb_finishing_2` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_finishing_2`.`no_laporan_produksi` WHERE tgl_masuk_f2='" + dateFormat.format(Date_Tandon.getDate()) + "') AS `f2_basah`, "
                        + "(SELECT SUM(jumlah_keping) FROM `tb_laporan_produksi` LEFT JOIN `tb_finishing_2` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_finishing_2`.`no_laporan_produksi` WHERE tgl_masuk_f2='" + dateFormat.format(Date_Tandon.getDate()) + "') AS `f2_keping`, "
                        + "(SELECT COUNT(`tb_laporan_produksi`.`no_laporan_produksi`) FROM `tb_laporan_produksi` LEFT JOIN `tb_lab_laporan_produksi` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_lab_laporan_produksi`.`no_laporan_produksi` WHERE `tb_lab_laporan_produksi`.`no_laporan_produksi` IS NOT NULL AND `tb_lab_laporan_produksi`.`tgl_uji` IS NULL AND `tb_lab_laporan_produksi`.`tgl_selesai` IS NULL) AS `qc_sampling_lp`, "
                        + "(SELECT SUM(berat_basah) FROM `tb_laporan_produksi` LEFT JOIN `tb_lab_laporan_produksi` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_lab_laporan_produksi`.`no_laporan_produksi` WHERE `tb_lab_laporan_produksi`.`no_laporan_produksi` IS NOT NULL AND `tb_lab_laporan_produksi`.`tgl_uji` IS NULL AND `tb_lab_laporan_produksi`.`tgl_selesai` IS NULL) AS `qc_sampling_basah`, "
                        + "(SELECT SUM(jumlah_keping) FROM `tb_laporan_produksi` LEFT JOIN `tb_lab_laporan_produksi` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_lab_laporan_produksi`.`no_laporan_produksi` WHERE `tb_lab_laporan_produksi`.`no_laporan_produksi` IS NOT NULL AND `tb_lab_laporan_produksi`.`tgl_uji` IS NULL AND `tb_lab_laporan_produksi`.`tgl_selesai` IS NULL) AS `qc_sampling_keping`, "
                        + "(SELECT COUNT(`tb_laporan_produksi`.`no_laporan_produksi`) FROM `tb_laporan_produksi` LEFT JOIN `tb_lab_laporan_produksi` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_lab_laporan_produksi`.`no_laporan_produksi` WHERE `tb_lab_laporan_produksi`.`no_laporan_produksi` IS NOT NULL AND `tb_lab_laporan_produksi`.`tgl_selesai` IS NULL AND `status` = 'HOLD/NON GNS') AS `qc_hold_lp`, "
                        + "(SELECT SUM(berat_basah) FROM `tb_laporan_produksi` LEFT JOIN `tb_lab_laporan_produksi` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_lab_laporan_produksi`.`no_laporan_produksi` WHERE `tb_lab_laporan_produksi`.`no_laporan_produksi` IS NOT NULL AND `tb_lab_laporan_produksi`.`tgl_selesai` IS NULL AND `status` = 'HOLD/NON GNS') AS `qc_hold_basah`, "
                        + "(SELECT SUM(jumlah_keping) FROM `tb_laporan_produksi` LEFT JOIN `tb_lab_laporan_produksi` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_lab_laporan_produksi`.`no_laporan_produksi` WHERE `tb_lab_laporan_produksi`.`no_laporan_produksi` IS NOT NULL AND `tb_lab_laporan_produksi`.`tgl_selesai` IS NULL AND `status` = 'HOLD/NON GNS') AS `qc_hold_keping`, "
                        + "(SELECT COUNT(`kode_asal`) FROM `tb_bahan_jadi_masuk` WHERE `tanggal_grading` IS NULL) AS `gbj_belumgrading_lp`, "
                        + "(SELECT SUM(`berat`) FROM `tb_bahan_jadi_masuk` WHERE `tanggal_grading` IS NULL) AS `gbj_belumgrading_basah`, "
                        + "(SELECT SUM(`keping`) FROM `tb_bahan_jadi_masuk` WHERE `tanggal_grading` IS NULL) AS `gbj_belumgrading_keping`, "
                        + "(SELECT COUNT(`kode_asal`) FROM `tb_bahan_jadi_masuk` WHERE `kode_tutupan` IS NULL AND `tanggal_grading` IS NOT NULL) AS `gbj_belumtutupan_lp`, "
                        + "(SELECT SUM(`berat`) FROM `tb_bahan_jadi_masuk` WHERE `kode_tutupan` IS NULL AND `tanggal_grading` IS NOT NULL) AS `gbj_belumtutupan_basah`, "
                        + "(SELECT SUM(`keping`) FROM `tb_bahan_jadi_masuk` WHERE `kode_tutupan` IS NULL AND `tanggal_grading` IS NOT NULL) AS `gbj_belumtutupan_keping` "
                        + "FROM DUAL";
                rs = Utility.db.getStatement().executeQuery(sql);
                dataset6.clear();
                while (rs.next()) {
                    dataset6.setValue(rs.getInt("cuci_basah"), "Berat", "Cuci");
                    dataset6.setValue(rs.getInt("cabut_basah"), "Berat", "Cabut");
                    dataset6.setValue(rs.getInt("cetak_basah"), "Berat", "Cetak");
                    dataset6.setValue(rs.getInt("f2_basah"), "Berat", "F2");
                    dataset6.setValue(rs.getInt("qc_sampling_basah"), "Berat", "QC Sampling");
                    dataset6.setValue(rs.getInt("qc_hold_basah"), "Berat", "QC Hold");
                    dataset6.setValue(rs.getInt("gbj_belumgrading_basah"), "Berat", "Belum Grading BJ");
                    dataset6.setValue(rs.getInt("gbj_belumtutupan_basah"), "Berat", "LP belum Tutupan");

                    DefaultTableModel model = (DefaultTableModel) Table_detail_tandon.getModel();
                    model.setRowCount(0);
                    model.addRow(new Object[]{"Cuci", rs.getInt("cuci_lp"), rs.getInt("cuci_keping"), rs.getInt("cuci_basah")});
                    model.addRow(new Object[]{"Cabut", rs.getInt("cabut_lp"), rs.getInt("cabut_keping"), rs.getInt("cabut_basah")});
                    model.addRow(new Object[]{"Cetak", rs.getInt("cetak_lp"), rs.getInt("cetak_keping"), rs.getInt("cetak_basah")});
                    model.addRow(new Object[]{"Finishing 2", rs.getInt("f2_lp"), rs.getInt("f2_keping"), rs.getInt("f2_basah")});
                    model.addRow(new Object[]{"QC Sampling", rs.getInt("qc_sampling_lp"), rs.getInt("qc_sampling_keping"), rs.getInt("qc_sampling_basah")});
                    model.addRow(new Object[]{"QC Hold", rs.getInt("qc_hold_lp"), rs.getInt("qc_hold_keping"), rs.getInt("qc_hold_basah")});
                    model.addRow(new Object[]{"Belum Grading BJ", rs.getInt("gbj_belumgrading_lp"), rs.getInt("gbj_belumgrading_keping"), rs.getInt("gbj_belumgrading_basah")});
                    model.addRow(new Object[]{"LP Belum Tutupan", rs.getInt("gbj_belumtutupan_lp"), rs.getInt("gbj_belumtutupan_keping"), rs.getInt("gbj_belumtutupan_basah")});
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_search_LPTandonActionPerformed

    private void button_search_setoranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_setoranActionPerformed
        // TODO add your handling code here:
        refreshChart_Rendemen();
    }//GEN-LAST:event_button_search_setoranActionPerformed

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        show_data_stokBahanBaku();
    }//GEN-LAST:event_button_searchActionPerformed

    private void button_search1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search1ActionPerformed
        // TODO add your handling code here:
        show_data_hasilProduksi();
    }//GEN-LAST:event_button_search1ActionPerformed

    /**
     * @param args the command line arguments
     */
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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JFrame_Chart.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFrame_Chart.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFrame_Chart.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrame_Chart.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFrame_Chart chart = new JFrame_Chart();
                chart.pack();
                chart.setResizable(true);
                chart.setLocationRelativeTo(null);
                chart.setVisible(true);
                chart.setEnabled(true);
                chart.init();
                chart.setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_grade;
    private javax.swing.JComboBox<String> ComboBox_ruangan;
    private com.toedter.calendar.JDateChooser Date_DataMasuk;
    private com.toedter.calendar.JDateChooser Date_DataMasuk2;
    private com.toedter.calendar.JDateChooser Date_QCLP1;
    private com.toedter.calendar.JDateChooser Date_QCLP2;
    private com.toedter.calendar.JDateChooser Date_Setoran;
    private com.toedter.calendar.JDateChooser Date_Setoran1;
    private com.toedter.calendar.JDateChooser Date_Setoran2;
    private com.toedter.calendar.JDateChooser Date_Tandon;
    private com.toedter.calendar.JDateChooser Date_filter1;
    private com.toedter.calendar.JDateChooser Date_filter2;
    private com.toedter.calendar.JDateChooser Date_filter3;
    private javax.swing.JPanel Panel_Line_Chart;
    private javax.swing.JPanel Panel_Pie_Chart;
    private javax.swing.JPanel Panel_Pie_Chart1;
    private javax.swing.JPanel Panel_Pie_Chart2;
    private javax.swing.JPanel Panel_Pie_Chart3;
    private javax.swing.JPanel Panel_chart2;
    private javax.swing.JPanel Panel_chart3;
    private javax.swing.JPanel Panel_chart4;
    private javax.swing.JTable Table_detail_tandon;
    private javax.swing.JTable Table_hasil_produksi_baku;
    private javax.swing.JTable Table_hasil_produksi_jadi;
    private javax.swing.JTable Table_stok_bahan_baku;
    private javax.swing.JButton button_search;
    private javax.swing.JButton button_search1;
    public static javax.swing.JButton button_search_LPSetoran;
    public static javax.swing.JButton button_search_LPTandon;
    public static javax.swing.JButton button_search_dataMasuk;
    public static javax.swing.JButton button_search_setoran;
    public static javax.swing.JButton button_search_ujiQCLP;
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
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
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
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label_bagian_pieChart1;
    private javax.swing.JLabel label_berat_lineChart;
    private javax.swing.JLabel label_berat_pieChart;
    private javax.swing.JLabel label_laki_pieChart1;
    private javax.swing.JLabel label_perempuan_pieChart1;
    private javax.swing.JLabel label_persentase_lineChart;
    private javax.swing.JLabel label_persentase_pieChart;
    private javax.swing.JLabel label_persentase_pieChart1;
    private javax.swing.JLabel label_qclp_hold;
    private javax.swing.JLabel label_qclp_passed;
    private javax.swing.JLabel label_qclp_persentase_hold;
    private javax.swing.JLabel label_qclp_persentase_passed;
    private javax.swing.JLabel label_qclp_total;
    private javax.swing.JLabel label_tanggal;
    private javax.swing.JLabel label_tipe_lineChart;
    private javax.swing.JLabel label_tipe_pieChart;
    private javax.swing.JLabel label_total_data_lineChart;
    private javax.swing.JLabel label_total_data_pieCHart;
    private javax.swing.JLabel label_total_data_pieChart1;
    private javax.swing.JLabel label_total_laki_pieChart1;
    private javax.swing.JLabel label_total_perempuan_pieChart1;
    private javax.swing.JLabel label_total_pieChart1;
    // End of variables declaration//GEN-END:variables
}
