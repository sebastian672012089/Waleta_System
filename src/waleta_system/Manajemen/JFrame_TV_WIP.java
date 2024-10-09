package waleta_system.Manajemen;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Paint;
import java.awt.SystemColor;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.LegendItemEntity;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import waleta_system.Class.Utility;
import waleta_system.MainForm;

public class JFrame_TV_WIP extends javax.swing.JFrame {

    Date today = new Date();
    GraphicsDevice device = GraphicsEnvironment
            .getLocalGraphicsEnvironment().getScreenDevices()[0];
    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    Date date = new Date();//date berisikan tanggal hari ini
    JFileChooser chooser = new JFileChooser();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    public DefaultCategoryDataset dataset1;
    JFreeChart chart1;
    int TABEL_KPI = 1;
    static Timer timer;
    TimerTask timerTask;

    public JFrame_TV_WIP() {
        initComponents();
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                e.getWindow().dispose();
                timer.cancel();
                timerTask.cancel();
//                int x = JOptionPane.showConfirmDialog(null, "do you want really close app?", "exit on close", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
//                if (x == JOptionPane.YES_OPTION) {
//                    e.getWindow().dispose();
//                    timer.cancel();
//                    timerTask.cancel();
//                }
            }
        });
    }

    public void init(final int TABEL_KPI) {
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        chart();
        this.TABEL_KPI = TABEL_KPI;
        timerTask = new TimerTask() {
            @Override
            public void run() {
                refresh_chart2();
                refresh_JAM();
                if (TABEL_KPI == 0) {
                    Table_kpi.setEnabled(false);
                    Table_kpi.setVisible(false);
                } else if (TABEL_KPI == 1) {
                    Table_kpi.setEnabled(true);
                    Table_kpi.setVisible(true);
                    refreshTable_kpi();
                    Table_kpi.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18));
                    Table_kpi.repaint();
                    refresh_table_jumlah_karyawan();
                    Table_jumlah_anak.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18));
                    Table_jumlah_anak.repaint();
                }
//        refreshTable_StockBJ();
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 1000, 60000);
    }

    public void chart() {
        dataset1 = new DefaultCategoryDataset();
        chart1 = ChartFactory.createBarChart("Jumlah PRODUKSI setiap Bagian", "", "", dataset1, PlotOrientation.VERTICAL, true, true, false);
        chart1.setBackgroundPaint(Color.WHITE);
        chart1.getTitle().setPaint(Color.RED);
        CategoryPlot cp4 = (CategoryPlot) chart1.getPlot();
        cp4.setBackgroundPaint(SystemColor.inactiveCaption);
        cp4.setRenderer(new BarRenderer() {
            @Override
            public Paint getItemPaint(final int row, final int column) {
                float max = 0;
                Color color = new Color(0, 0, 0);
                for (int i = 0; i < dataset1.getRowCount(); i++) {
                    for (int j = 0; j < dataset1.getColumnCount(); j++) {
                        if (dataset1.getValue(i, j).floatValue() > max) {
                            max = dataset1.getValue(i, j).floatValue();
                        }
                    }
                }
                switch (column) {
                    case 0: //CUCI
                        color = new Color(220, 220, 220);
                        break;
                    case 1://CABUT
                        color = new Color(251, 236, 93);
                        break;
                    case 2://CETAK
                        color = new Color(251, 236, 93);
                        break;
                    case 3://KOREKSI KERING
                        color = new Color(255, 255, 255);
                        break;
                    case 4://F1
                        color = new Color(255, 255, 255);
                        break;
                    case 5://F2
                        color = new Color(255, 255, 255);
                        break;
                    case 6://Final Check
                        color = new Color(255, 255, 255);
                        break;
                    case 7://QC SAMPLING
                        color = new Color(254, 127, 156);
                        break;
                    case 8://QC HOLD
                        color = new Color(254, 127, 156);
                        break;
                    case 9://GBJ
                        color = new Color(254, 230, 168);
                        break;
                    case 10://BLM TUTUP
                        color = new Color(254, 230, 168);
                        break;
                    default:
                        break;
                }
//                float valueColor = 255 * dataset1.getValue(row, column).floatValue() / max;
//                if (valueColor >= 255) {
//                    valueColor = 255;
//                } else {
//                    valueColor = valueColor % 256;
//                }
//                Color a = new Color(0, (int) valueColor, 0);

                return color;
            }
        });
        ((BarRenderer) cp4.getRenderer()).setBarPainter(new StandardBarPainter());
        BarRenderer renderer = (BarRenderer) chart1.getCategoryPlot().getRenderer();
        renderer.setDrawBarOutline(true);
        renderer.setShadowVisible(false);
//        renderer.setSeriesPaint(0, Color.lightGray);
//        renderer.setSeriesPaint(1, Color.white);
//        renderer.setSeriesPaint(2, Color.white);
//        renderer.setSeriesPaint(3, Color.white);
//        renderer.setSeriesPaint(4, Color.pink);
//        renderer.setSeriesPaint(5, new Color(255, 251, 231));
        ChartPanel panelChartProgressLP = new ChartPanel(chart1);
        panelChartProgressLP.setLocation(0, 0);
        panelChartProgressLP.setSize(Panel_chart1.getSize());
        Panel_chart1.add(panelChartProgressLP);
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
    }

    public void refresh_chart2() {
        try {

            int gbm_lp = 0, cuci_lp = 0, cabut_lp = 0, cetak_lp = 0, koreksi_lp = 0, f1_lp = 0, f2_lp = 0, final_lp = 0, qc_sampling_lp = 0, qc_hold_lp = 0, gbj_grading_lp = 0, gbj_tutupan_lp = 0;
            int gbm_gram = 0, cuci_gram = 0, cabut_gram = 0, cetak_gram = 0, koreksi_gram = 0, f1_gram = 0, f2_gram = 0, final_gram = 0, qc_sampling_gram = 0, qc_hold_gram = 0, gbj_grading_gram = 0, gbj_tutupan_gram = 0;

            String Filter_jenis_LP = "";
            switch (ComboBox_jenis_lp.getSelectedIndex()) {
                case 1:
                    Filter_jenis_LP = "AND `tb_laporan_produksi`.`ruangan` IN ('A', 'B', 'C', 'D', 'E', 'CABUTO') ";
                    break;
                case 2:
                    Filter_jenis_LP = "AND `tb_laporan_produksi`.`ruangan` NOT IN ('A', 'B', 'C', 'D', 'E', 'CABUTO') ";
                    break;
                default:
                    break;
            }
            HashMap<String, Date[]> List_tanggal = new HashMap<>();
            Utility.db_sub.connect();
            String query = "SELECT `tb_cabut`.`no_laporan_produksi`, `tgl_mulai_cabut`, `tgl_cabut`, `tgl_setor_cabut`, `tb_cetak`.`tgl_mulai_cetak`, `tgl_selesai_cetak` "
                    + "FROM `tb_cabut` "
                    + "LEFT JOIN `tb_cetak` ON `tb_cabut`.`no_laporan_produksi` = `tb_cetak`.`no_laporan_produksi` "
                    + "WHERE 1";
            ResultSet result = Utility.db_sub.getStatement().executeQuery(query);
            while (result.next()) {
                Date[] Array_tanggal = new Date[]{
                    result.getDate("tgl_mulai_cabut"),
                    result.getDate("tgl_cabut"),
                    result.getDate("tgl_setor_cabut"),
                    result.getDate("tgl_mulai_cetak"),
                    result.getDate("tgl_selesai_cetak")
                };
                List_tanggal.put(result.getString("no_laporan_produksi"), Array_tanggal);
            }
            
            sql = "SELECT `tb_laporan_produksi`.`no_laporan_produksi`, `tanggal_lp`, `berat_basah`, "
                    + "`tb_cuci`.`no_laporan_produksi` AS 'lp_cuci', `tb_cuci`.`cuci_diserahkan`, "
                    + "`tb_cabut`.`no_laporan_produksi` AS 'lp_cabut', `tb_cabut`.`tgl_mulai_cabut`, `tb_cabut`.`tgl_setor_cabut`, "
                    + "`tb_cetak`.`no_laporan_produksi` AS 'lp_cetak', `tb_cetak`.`tgl_mulai_cetak`, `tb_cetak`.`tgl_selesai_cetak`, "
                    + "`tb_finishing_2`.`no_laporan_produksi` AS 'lp_f2', `tb_finishing_2`.`tgl_dikerjakan_f2`, `tgl_masuk_f2`, `tb_finishing_2`.`tgl_f1`, `tb_finishing_2`.`tgl_f2`, `tb_finishing_2`.`tgl_setor_f2`,\n"
                    + "`tb_lab_laporan_produksi`.`no_laporan_produksi` AS 'lp_qc', `tb_lab_laporan_produksi`.`tgl_uji`, `tb_lab_laporan_produksi`.`tgl_selesai` AS 'tgl_selesai_qc', `tb_lab_laporan_produksi`.`status`,\n"
                    + "`tb_bahan_jadi_masuk`.`kode_asal`, `tb_bahan_jadi_masuk`.`tanggal_grading`, `tb_bahan_jadi_masuk`.`kode_tutupan`, `tb_bahan_jadi_masuk`.`keping`, `tb_bahan_jadi_masuk`.`berat`\n"
                    + "FROM `tb_laporan_produksi` "
                    + "LEFT JOIN `tb_cuci` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cuci`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_cabut` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cabut`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_cetak` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cetak`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_finishing_2` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_finishing_2`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_lab_laporan_produksi` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_lab_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_bahan_jadi_masuk`.`kode_asal`\n"
                    + "WHERE "
                    + "`tb_bahan_jadi_masuk`.`kode_tutupan` IS NULL \n"
                    + Filter_jenis_LP;
            rs = Utility.db.getStatement().executeQuery(sql);
            dataset1.clear();
            while (rs.next()) {
                Date tgl_setor_cabut = rs.getDate("tgl_setor_cabut") != null ? rs.getDate("tgl_setor_cabut") : List_tanggal.get(rs.getString("no_laporan_produksi")) != null ? List_tanggal.get(rs.getString("no_laporan_produksi"))[2] : null;
                Date tgl_selesai_cetak = rs.getDate("tgl_selesai_cetak") != null ? rs.getDate("tgl_selesai_cetak") : List_tanggal.get(rs.getString("no_laporan_produksi")) != null ? List_tanggal.get(rs.getString("no_laporan_produksi"))[4] : null;
                if (rs.getDate("tanggal_grading") != null) {
                    gbj_tutupan_lp++;
                    gbj_tutupan_gram = gbj_tutupan_gram + rs.getInt("berat");
                } else if (rs.getDate("tgl_selesai_qc") != null) {
                    gbj_grading_lp++;
                    if (rs.getString("kode_asal") != null) {
                        gbj_grading_gram = gbj_grading_gram + rs.getInt("berat");
                    } else {
                        gbj_grading_gram = gbj_grading_gram + rs.getInt("berat_basah");
                    }
                } else if (rs.getDate("tgl_uji") != null && rs.getString("status").equals("HOLD/NON GNS")) {
                    qc_hold_lp++;
                    qc_hold_gram = qc_hold_gram + rs.getInt("berat_basah");
                } else if (rs.getDate("tgl_setor_f2") != null) {
                    qc_sampling_lp++;
                    qc_sampling_gram = qc_sampling_gram + rs.getInt("berat_basah");
                } else if (rs.getDate("tgl_f2") != null) {
                    final_lp++;
                    final_gram = final_gram + rs.getInt("berat_basah");
                } else if (rs.getDate("tgl_f1") != null) {
                    f2_lp++;
                    f2_gram = f2_gram + rs.getInt("berat_basah");
                } else if (rs.getDate("tgl_dikerjakan_f2") != null) {
                    f1_lp++;
                    f1_gram = f1_gram + rs.getInt("berat_basah");
                } else if (tgl_selesai_cetak != null) {
                    koreksi_lp++;
                    koreksi_gram = koreksi_gram + rs.getInt("berat_basah");
                } else if (tgl_setor_cabut != null) {
                    cetak_lp++;
                    cetak_gram = cetak_gram + rs.getInt("berat_basah");
                } else if (rs.getString("cuci_diserahkan") != null && !rs.getString("cuci_diserahkan").equals("-")) {
                    cabut_lp++;
                    cabut_gram = cabut_gram + rs.getInt("berat_basah");
                } else if (rs.getString("cuci_diserahkan") != null && rs.getString("cuci_diserahkan").equals("-")) {
                    cuci_lp++;
                    cuci_gram = cuci_gram + rs.getInt("berat_basah");
                } else if (rs.getDate("tanggal_lp") != null) {
                    gbm_lp++;
                    gbm_gram = gbm_gram + rs.getInt("berat_basah");
                } else {
                }
                /*
                //GBJ
                if (rs.getString("tanggal_lp").equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))) {
                    gbm_lp++;
                    gbm_gram = gbm_gram + rs.getInt("berat_basah");
                }
                //Cuci
                if ("-".equals(rs.getString("cuci_diserahkan"))) {
                    cuci_lp++;
                    cuci_gram = cuci_gram + rs.getInt("berat_basah");
                }
                //Cabut
                if (!"-".equals(rs.getString("cuci_diserahkan")) && rs.getString("cuci_diserahkan") != null && rs.getDate("tgl_setor_cabut") == null && rs.getDate("tgl_masuk_f2") == null) {
                    cabut_lp++;
                    cabut_gram = cabut_gram + rs.getInt("berat_basah");
                }
                //Cetak
                if (rs.getDate("tgl_setor_cabut") != null && rs.getDate("tgl_selesai_cetak") == null) {
                    cetak_lp++;
                    cetak_gram = cetak_gram + rs.getInt("berat_basah");
                }
                //Koreksi
                if (rs.getDate("tgl_selesai_cetak") != null && rs.getDate("tgl_dikerjakan_f2") == null) {
                    koreksi_lp++;
                    koreksi_gram = koreksi_gram + rs.getInt("berat_basah");
                }
                //F1
                if (rs.getDate("tgl_dikerjakan_f2") != null && rs.getDate("tgl_f1") == null && rs.getDate("tgl_setor_f2") == null) {
                    f1_lp++;
                    f1_gram = f1_gram + rs.getInt("berat_basah");
                }
                //F2
                if (rs.getDate("tgl_f1") != null && rs.getDate("tgl_f2") == null && rs.getDate("tgl_setor_f2") == null) {
                    f2_lp++;
                    f2_gram = f2_gram + rs.getInt("berat_basah");
                }
                //Final Check
                if (rs.getDate("tgl_f2") != null && rs.getDate("tgl_setor_f2") == null) {
                    final_lp++;
                    final_gram = final_gram + rs.getInt("berat_basah");
                }
                //QC Sampling
                if (rs.getString("lp_qc") != null && rs.getDate("tgl_uji") == null && rs.getDate("tgl_selesai_qc") == null) {
                    qc_sampling_lp++;
                    qc_sampling_gram = qc_sampling_gram + rs.getInt("berat_basah");
                }
                //QC Hold
                if (rs.getDate("tgl_uji") != null && rs.getDate("tgl_selesai_qc") == null && rs.getString("status").equals("HOLD/NON GNS")) {
                    qc_hold_lp++;
                    qc_hold_gram = qc_hold_gram + rs.getInt("berat_basah");
                }
                //GBJ grading
                if (rs.getDate("tanggal_grading") == null && rs.getDate("tgl_selesai_qc") != null) {
                    gbj_grading_lp++;
                    if (rs.getString("kode_asal") != null) {
                        gbj_grading_gram = gbj_grading_gram + rs.getInt("berat");
                    } else {
                        gbj_grading_gram = gbj_grading_gram + rs.getInt("berat_basah");
                    }
                }

                //GBJ tutupan
                if (rs.getDate("tanggal_grading") != null && rs.getString("kode_tutupan") == null) {
                    gbj_tutupan_lp++;
                    gbj_tutupan_gram = gbj_tutupan_gram + rs.getInt("berat");
                }
                */
            }

            dataset1.setValue(cuci_gram, "Berat", "Cuci");
            dataset1.setValue(cabut_gram, "Berat", "CBT");
            dataset1.setValue(cetak_gram, "Berat", "CTK");
            dataset1.setValue(koreksi_gram, "Berat", "Koreksi");
            dataset1.setValue(f1_gram, "Berat", "F1");
            dataset1.setValue(f2_gram, "Berat", "F2");
            dataset1.setValue(final_gram, "Berat", "Final");
            dataset1.setValue(qc_sampling_gram, "Berat", "QC S");
            dataset1.setValue(qc_hold_gram, "Berat", "QC H");
            dataset1.setValue(gbj_grading_gram, "Berat", "GBJ");
            dataset1.setValue(gbj_tutupan_gram, "Berat", "Tutupan");

            DefaultTableModel model = (DefaultTableModel) Table_detail_tandon.getModel();
            model.setRowCount(0);
            model.addRow(new Object[]{"GBM", gbm_lp, gbm_gram});
            model.addRow(new Object[]{"Cuci", cuci_lp, cuci_gram});
            model.addRow(new Object[]{"Cabut", cabut_lp, cabut_gram});
            model.addRow(new Object[]{"Cetak", cetak_lp, cetak_gram});
            model.addRow(new Object[]{"Koreksi", koreksi_lp, koreksi_gram});
            model.addRow(new Object[]{"F1", f1_lp, f1_gram});
            model.addRow(new Object[]{"F2", f2_lp, f2_gram});
            model.addRow(new Object[]{"Final Check", final_lp, final_gram});
            model.addRow(new Object[]{"QC Sampling", qc_sampling_lp, qc_sampling_gram});
            model.addRow(new Object[]{"QC Hold", qc_hold_lp, qc_hold_gram});
            model.addRow(new Object[]{"GBJ", gbj_grading_lp, gbj_grading_gram});
            model.addRow(new Object[]{"LP Belum Tutupan", gbj_tutupan_lp, gbj_tutupan_gram});
            int total = cuci_gram + cabut_gram + cetak_gram + koreksi_gram + f1_gram + f2_gram
                    + final_gram + qc_sampling_gram + qc_hold_gram + gbj_grading_gram + gbj_tutupan_gram;
            model.addRow(new Object[]{"TOTAL", null, total});

            Table_detail_tandon.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (row == Table_detail_tandon.getRowCount() - 1) {
                        comp.setFont(this.getFont().deriveFont(Font.BOLD));
                    }
                    return comp;
                }
            });

        } catch (Exception ex) {
            Logger.getLogger(JFrame_TV_WIP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refresh_JAM() {
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        String formattedDate = myDateObj.format(myFormatObj);
        label_jam.setText(formattedDate);
    }

    public void refresh_table_jumlah_karyawan() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_jumlah_anak.getModel();
            model.setRowCount(0);
            int gbm = 0, cuci = 0, cabut = 0, cetak = 0, f2 = 0, qc = 0, gbj = 0, packing = 0, support = 0, umum = 0, staff = 0, TOTAL = 0;
            sql = "SELECT DISTINCT(`pin`), `tb_karyawan`.`posisi`, `tb_karyawan`.`kode_bagian`, `nama_bagian`, `kode_departemen` "
                    + "FROM `att_log` "
                    + "LEFT JOIN `tb_karyawan` ON `att_log`.`pin` = `tb_karyawan`.`pin_finger` "
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`"
                    + "WHERE `status` = 'IN' "
                    + "AND `posisi` IN ('PEJUANG', 'STAFF 5', 'STAFF 6') "
                    + "AND DATE(`scan_date`) = CURRENT_DATE "
                    + "AND `tb_karyawan`.`kode_bagian` IS NOT NULL";
            rs = Utility.db.getStatement().executeQuery(sql);

            while (rs.next()) {
                String posisi = rs.getString("posisi").toUpperCase();
                if (posisi.equals("PEJUANG")) {
                    String nama_bagian = rs.getString("nama_bagian").toUpperCase();
                    String kode_departemen = rs.getString("kode_departemen").toUpperCase();
                    if (kode_departemen.equals("PRODUKSI")) {
                        if (nama_bagian.contains("CUCI")) {
                            cuci++;
                        } else if (nama_bagian.contains("CABUT-BORONG") || nama_bagian.contains("CABUT-TRAINING")) {
                            cabut++;
                        } else if (nama_bagian.contains("CETAK-BORONG") || nama_bagian.contains("CETAK-MANDIRI")) {
                            cetak++;
                        } else if (nama_bagian.contains("FINISHING")) {
                            f2++;
                        } else {
                            support++;
                        }
                    } else if (kode_departemen.equals("EKSPOR")) {
                        if (nama_bagian.contains("GRADING")) {
                            gbj++;
                        } else if (nama_bagian.contains("PACKING")) {
                            packing++;
                        } else {
                            support++;
                        }
                    } else if (kode_departemen.equals("BAHAN MENTAH")) {
                        gbm++;
                    } else if (kode_departemen.equals("QCQA")) {
                        qc++;
                    } else if (kode_departemen.equals("HRGA")) {
                        umum++;
                    } else {
                        support++;
                    }

                } else if (posisi.contains("STAFF")) {
                    staff++;
                }

                TOTAL = gbm + cuci + cabut + cetak + f2 + qc + gbj + packing + support + umum + staff;
            }
            model.addRow(new Object[]{gbm, cuci, cabut, cetak, f2, qc, gbj, packing, support, umum, staff, TOTAL});

            Table_jumlah_anak.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (column == Table_jumlah_anak.getColumnCount() - 1) {
                        comp.setFont(this.getFont().deriveFont(Font.BOLD));
                        comp.setForeground(Color.red);
                    }
                    return comp;
                }
            });
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JFrame_TV_WIP.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void refreshTable_kpi() {
        DefaultTableModel model_Table_kpi = (DefaultTableModel) Table_kpi.getModel();
        model_Table_kpi.setRowCount(0);
        int tahun = Integer.valueOf(new SimpleDateFormat("yyyy").format(new Date()));
        for (int i = 0; i < 4; i++) {
            model_Table_kpi.addRow(new Object[]{(tahun - i)});
//            Table_kpi.setValueAt((tahun - i), i, 0);
        }
        Total_Proses_baku();
        AVG_cabutan();
        Rendemen();
        AVG_ProductionPerWeek();
        Total_Production();
        AVG_Lama_proses();
        Persentase_QC();
        Penjualan();
//        ColumnsAutoSizer.sizeColumnsToFit(jTable_kpi);
    }

    public void Rendemen() {
        try {
            int tahun = Integer.valueOf(new SimpleDateFormat("yyyy").format(new Date()));
            for (int i = 0; i < 4; i++) {
                sql = "SELECT"
                        + "(SELECT ROUND((SUM(IF((`berat_fbonus`+`berat_fnol`)>0,(`berat_fbonus`+`berat_fnol`) - (`tambahan_kaki1`+`tambahan_kaki2`),0)) / SUM(`tb_laporan_produksi`.`berat_kering`)) * 100, 1) AS 'rata2'\n"
                        + "FROM `tb_finishing_2` LEFT JOIN `tb_laporan_produksi` ON `tb_finishing_2`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "WHERE YEAR(`tgl_setor_f2`) = " + (tahun - i) + ") AS `utuh`,"
                        + "(SELECT ROUND((SUM(IF((`berat_jidun`)>0,`berat_jidun` - (`tambahan_kaki1`+`tambahan_kaki2`),0)) / SUM(`tb_laporan_produksi`.`berat_kering`)) * 100, 1) AS 'rata2' "
                        + "FROM `tb_finishing_2` LEFT JOIN `tb_laporan_produksi` ON `tb_finishing_2`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi` "
                        + "WHERE YEAR(`tgl_setor_f2`) = " + (tahun - i) + ") AS `jidun`,"
                        + "(SELECT ROUND((SUM(`berat_pecah`+`berat_flat`) / SUM(`tb_laporan_produksi`.`berat_kering`)) * 100, 1) AS 'rata2' "
                        + "FROM `tb_finishing_2` LEFT JOIN `tb_laporan_produksi` ON `tb_finishing_2`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi` "
                        + "WHERE YEAR(`tgl_setor_f2`) = " + (tahun - i) + ") AS `PecahFlat`,"
                        + "(SELECT ROUND((SUM(`sesekan` + `hancuran` + `rontokan` + `bonggol` + `serabut`) / SUM(`tb_laporan_produksi`.`berat_kering`)) * 100, 1) AS 'rata2' "
                        + "FROM `tb_finishing_2` LEFT JOIN `tb_laporan_produksi` ON `tb_finishing_2`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi` "
                        + "WHERE YEAR(`tgl_setor_f2`) = " + (tahun - i) + ") AS `ByProd`"
                        + "FROM DUAL";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    Table_kpi.setValueAt(rs.getFloat("utuh"), i, 3);
                    Table_kpi.setValueAt(rs.getFloat("jidun"), i, 4);
                    Table_kpi.setValueAt(rs.getFloat("PecahFlat"), i, 5);
                    Table_kpi.setValueAt(rs.getFloat("ByProd"), i, 6);
                    Table_kpi.setValueAt((100 - (rs.getFloat("utuh") + rs.getFloat("jidun") + rs.getFloat("PecahFlat") + rs.getFloat("ByProd"))), i, 7);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JFrame_TV_WIP.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void AVG_ProductionPerWeek() {
        try {
            int tahun = Integer.valueOf(new SimpleDateFormat("yyyy").format(new Date()));
            sql = "SELECT "
                    + "(SELECT (SUM(`berat`) / ((DATEDIFF(CURRENT_DATE(), MAKEDATE(year(now()),1))) / 7)) AS 'AVG' FROM `tb_bahan_jadi_masuk` WHERE YEAR(`tanggal_grading`) = " + tahun + " AND `kode_asal` LIKE 'WL-%') AS `rata2_tahun_ini`,"
                    + "(SELECT (SUM(`berat`) / 52) AS 'AVG' FROM `tb_bahan_jadi_masuk` WHERE YEAR(`tanggal_grading`) = " + (tahun - 1) + " AND `kode_asal` LIKE 'WL-%') AS `rata2_tahun_lalu1`,"
                    + "(SELECT (SUM(`berat`) / 52) AS 'AVG' FROM `tb_bahan_jadi_masuk` WHERE YEAR(`tanggal_grading`) = " + (tahun - 2) + " AND `kode_asal` LIKE 'WL-%') AS `rata2_tahun_lalu2`,"
                    + "(SELECT (SUM(`berat`) / 52) AS 'AVG' FROM `tb_bahan_jadi_masuk` WHERE YEAR(`tanggal_grading`) = " + (tahun - 3) + " AND `kode_asal` LIKE 'WL-%') AS `rata2_tahun_lalu3`"
                    + "FROM DUAL";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                Table_kpi.setValueAt(rs.getInt("rata2_tahun_ini") / 1000, 0, 8);
                Table_kpi.setValueAt(rs.getInt("rata2_tahun_lalu1") / 1000, 1, 8);
                Table_kpi.setValueAt(rs.getInt("rata2_tahun_lalu2") / 1000, 2, 8);
                Table_kpi.setValueAt(rs.getInt("rata2_tahun_lalu2") / 1000, 3, 8);
            }
        } catch (SQLException ex) {
            Logger.getLogger(JFrame_TV_WIP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Total_Production() {
        try {
            int tahun = Integer.valueOf(new SimpleDateFormat("yyyy").format(new Date()));
            sql = "SELECT "
                    + "(SELECT SUM(`berat`) FROM `tb_bahan_jadi_masuk` WHERE YEAR(`tanggal_grading`) = " + tahun + " AND `kode_asal` LIKE 'WL-%') AS `produksi_tahun_ini`,"
                    + "(SELECT SUM(`berat`) FROM `tb_bahan_jadi_masuk` WHERE YEAR(`tanggal_grading`) = " + (tahun - 1) + " AND `kode_asal` LIKE 'WL-%') AS `produksi_tahun_lalu1`,"
                    + "(SELECT SUM(`berat`) FROM `tb_bahan_jadi_masuk` WHERE YEAR(`tanggal_grading`) = " + (tahun - 2) + " AND `kode_asal` LIKE 'WL-%') AS `produksi_tahun_lalu2`,"
                    + "(SELECT SUM(`berat`) FROM `tb_bahan_jadi_masuk` WHERE YEAR(`tanggal_grading`) = " + (tahun - 3) + " AND `kode_asal` LIKE 'WL-%') AS `produksi_tahun_lalu3`"
                    + "FROM DUAL";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                Table_kpi.setValueAt(rs.getInt("produksi_tahun_ini") / 1000, 0, 9);
                Table_kpi.setValueAt(rs.getInt("produksi_tahun_lalu1") / 1000, 1, 9);
                Table_kpi.setValueAt(rs.getInt("produksi_tahun_lalu2") / 1000, 2, 9);
                Table_kpi.setValueAt(rs.getInt("produksi_tahun_lalu3") / 1000, 3, 9);
            }
        } catch (SQLException ex) {
            Logger.getLogger(JFrame_TV_WIP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Total_Proses_baku() {
        try {
            int tahun = Integer.valueOf(new SimpleDateFormat("yyyy").format(new Date()));
            sql = "SELECT "
                    + "(SELECT SUM(`berat_basah`) FROM `tb_laporan_produksi` WHERE YEAR(`tanggal_lp`) = " + tahun + ") AS `pengeluaran_baku_tahun_ini`,"
                    + "(SELECT SUM(`berat_basah`) FROM `tb_laporan_produksi` WHERE YEAR(`tanggal_lp`) = " + (tahun - 1) + ") AS `pengeluaran_baku_tahun_lalu1`,"
                    + "(SELECT SUM(`berat_basah`) FROM `tb_laporan_produksi` WHERE YEAR(`tanggal_lp`) = " + (tahun - 2) + ") AS `pengeluaran_baku_tahun_lalu2`,"
                    + "(SELECT SUM(`berat_basah`) FROM `tb_laporan_produksi` WHERE YEAR(`tanggal_lp`) = " + (tahun - 3) + ") AS `pengeluaran_baku_tahun_lalu3`"
                    + "FROM DUAL";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                Table_kpi.setValueAt(rs.getInt("pengeluaran_baku_tahun_ini") / 1000, 0, 1);
                Table_kpi.setValueAt(rs.getInt("pengeluaran_baku_tahun_lalu1") / 1000, 1, 1);
                Table_kpi.setValueAt(rs.getInt("pengeluaran_baku_tahun_lalu2") / 1000, 2, 1);
                Table_kpi.setValueAt(rs.getInt("pengeluaran_baku_tahun_lalu3") / 1000, 3, 1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(JFrame_TV_WIP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void AVG_Lama_proses() {
        try {
            int tahun = Integer.valueOf(new SimpleDateFormat("yyyy").format(new Date()));
            for (int i = 0; i < 4; i++) {
                sql = "SELECT AVG(DATEDIFF(`tb_bahan_jadi_masuk`.`tanggal_grading`, `tanggal_lp`)) AS 'lama_proses' "
                        + "FROM `tb_laporan_produksi` LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_bahan_jadi_masuk`.`kode_asal` "
                        + "WHERE YEAR(`tb_laporan_produksi`.`tanggal_lp`) = '" + (tahun - i) + "' AND `tb_bahan_jadi_masuk`.`tanggal_grading` IS NOT NULL";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    Table_kpi.setValueAt(rs.getInt("lama_proses"), i, 10);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(JFrame_TV_WIP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void AVG_cabutan() {
        try {
            int tahun = Integer.valueOf(new SimpleDateFormat("yyyy").format(new Date()));
            for (int i = 0; i < 4; i++) {
                sql = "SELECT AVG(cabutan) AS 'avg' FROM ("
                        + "SELECT SUM(IF(`jumlah_cabut`>0, `jumlah_cabut`, `jumlah_gram`/8)) AS 'cabutan' "
                        + "FROM `tb_detail_pencabut` \n"
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_detail_pencabut`.`no_laporan_produksi`\n"
                        + "WHERE "
                        + "LENGTH(`ruangan`) <> 5 "
                        + "AND YEAR(`tanggal_cabut`) = '" + (tahun - i) + "'"
                        + "GROUP BY `id_pegawai`, `tanggal_cabut`"
                        + ") x";
                rs = Utility.db.getStatement().executeQuery(sql);
                while (rs.next()) {
                    float avg = Math.round(rs.getFloat("avg") * 100) / 100.f;
                    Table_kpi.setValueAt(avg, i, 2);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(JFrame_TV_WIP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Persentase_QC() {
        try {
            int tahun = Integer.valueOf(new SimpleDateFormat("yyyy").format(new Date()));
            float pass = 0, hold = 0;
            float total = 0, persen = 0;
            for (int i = 0; i < 4; i++) {
                sql = "SELECT"
                        + "(SELECT COUNT(`status_akhir`) FROM `tb_lab_laporan_produksi` WHERE YEAR(`tgl_selesai`) = '" + (tahun - i) + "' AND `status_akhir` = 'PASSED') AS `pass`,"
                        + "(SELECT COUNT(`status_akhir`) FROM `tb_lab_laporan_produksi` WHERE YEAR(`tgl_selesai`) = '" + (tahun - i) + "' AND `status_akhir` = 'HOLD/NON GNS') AS `hold`"
                        + "FROM DUAL";
                rs = Utility.db.getStatement().executeQuery(sql);
                while (rs.next()) {
                    pass = rs.getInt("pass");
                    hold = rs.getInt("hold");
                    total = pass + hold;
                }
                persen = Math.round((pass / total) * 10000) / 100.f;
                Table_kpi.setValueAt(persen, i, 11);
            }
        } catch (SQLException ex) {
            Logger.getLogger(JFrame_TV_WIP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Penjualan() {
        try {
            int tahun = Integer.valueOf(new SimpleDateFormat("yyyy").format(new Date()));
            for (int i = 0; i < 4; i++) {
                sql = "SELECT SUM(`tb_box_bahan_jadi`.`berat`) AS 'penjualan' \n"
                        + "FROM `tb_box_packing` LEFT JOIN `tb_box_bahan_jadi` ON `tb_box_packing`.`no_box` = `tb_box_bahan_jadi`.`no_box`\n"
                        + "LEFT JOIN `tb_pengiriman` ON `tb_box_packing`.`invoice_pengiriman` = `tb_pengiriman`.`invoice_no`\n"
                        + "WHERE `tb_pengiriman`.`jenis_pengiriman` IN ('Ekspor', 'Ekspor Esta', 'Lokal') AND YEAR(`tb_pengiriman`.`tanggal_invoice` ) = " + (tahun - i);
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    Table_kpi.setValueAt(rs.getInt("penjualan") / 1000, i, 12);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(JFrame_TV_WIP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        Panel_chart1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_detail_tandon = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        Table_kpi = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        Table_jumlah_anak = new javax.swing.JTable();
        button_close = new javax.swing.JButton();
        button_refresh = new javax.swing.JButton();
        label_jam = new javax.swing.JLabel();
        label_jam1 = new javax.swing.JLabel();
        ComboBox_jenis_lp = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        Panel_chart1.setBackground(new java.awt.Color(255, 255, 255));
        Panel_chart1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel_chart1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        javax.swing.GroupLayout Panel_chart1Layout = new javax.swing.GroupLayout(Panel_chart1);
        Panel_chart1.setLayout(Panel_chart1Layout);
        Panel_chart1Layout.setHorizontalGroup(
            Panel_chart1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 716, Short.MAX_VALUE)
        );
        Panel_chart1Layout.setVerticalGroup(
            Panel_chart1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 498, Short.MAX_VALUE)
        );

        Table_detail_tandon.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        Table_detail_tandon.setModel(new javax.swing.table.DefaultTableModel(
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
                {null, null, null}
            },
            new String [] {
                "Dept.", "Tot LP", "Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Float.class
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
        Table_detail_tandon.setRowHeight(33);
        Table_detail_tandon.setRowSelectionAllowed(false);
        Table_detail_tandon.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Table_detail_tandon);
        if (Table_detail_tandon.getColumnModel().getColumnCount() > 0) {
            Table_detail_tandon.getColumnModel().getColumn(0).setMinWidth(180);
        }

        Table_kpi.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 24)); // NOI18N
        Table_kpi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"2021", null, null, null, null, null, null, null, null, null, null, null, null},
                {"2020", null, null, null, null, null, null, null, null, null, null, null, null},
                {"2019", null, null, null, null, null, null, null, null, null, null, null, null},
                {"2018", null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Tahun", "Tot Baku", "Kpg ank/Hr", "Utuh 0%", "JDN 0%", "Flat 0%", "BP 0%", "SH 0%", "Kg/Mg", "BJD", "Hari", "GNS %", "Sales"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        Table_kpi.setFocusable(false);
        Table_kpi.setRowHeight(30);
        Table_kpi.setRowSelectionAllowed(false);
        Table_kpi.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(Table_kpi);

        Table_jumlah_anak.setFont(new java.awt.Font("Arial Unicode MS", 0, 24)); // NOI18N
        Table_jumlah_anak.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "GBM", "Cuci", "Cabut", "Cetak", "F2", "QC", "GBJ", "Packing", "Support", "Umum", "Staff", "Total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_jumlah_anak.setRowHeight(30);
        Table_jumlah_anak.setRowSelectionAllowed(false);
        Table_jumlah_anak.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(Table_jumlah_anak);

        button_close.setBackground(new java.awt.Color(255, 255, 255));
        button_close.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_close.setText("Close");
        button_close.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_closeActionPerformed(evt);
            }
        });

        button_refresh.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh.setText("Refresh");
        button_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refreshActionPerformed(evt);
            }
        });

        label_jam.setBackground(new java.awt.Color(255, 255, 255));
        label_jam.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_jam.setText("Jam :");

        label_jam1.setBackground(new java.awt.Color(255, 255, 255));
        label_jam1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_jam1.setText("Automatic refresh every 60 second > ");

        ComboBox_jenis_lp.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        ComboBox_jenis_lp.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ALL", "WALETA", "SUB+PLASMA" }));

        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText("Jenis LP :");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(Panel_chart1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 622, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_jenis_lp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(label_jam1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_jam)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_refresh)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_close)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(label_jam, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_refresh)
                            .addComponent(button_close)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_jenis_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_jam1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 470, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(Panel_chart1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                .addContainerGap())
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

    private void button_closeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_closeActionPerformed
        // TODO add your handling code here:
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }//GEN-LAST:event_button_closeActionPerformed

    private void button_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refreshActionPerformed
        // TODO add your handling code here:
        refresh_chart2();
        refresh_JAM();

        if (TABEL_KPI == 1) {
            refreshTable_kpi();
            refresh_table_jumlah_karyawan();
        }
    }//GEN-LAST:event_button_refreshActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JFrame_TV_WIP.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFrame_TV_WIP.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFrame_TV_WIP.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrame_TV_WIP.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame_TV_WIP chart = new JFrame_TV_WIP();
                chart.pack();
                chart.setResizable(true);
                chart.setLocationRelativeTo(null);
                chart.setVisible(true);
                chart.setEnabled(true);
                chart.setExtendedState(JFrame.MAXIMIZED_BOTH);
                chart.init(0);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_jenis_lp;
    private javax.swing.JPanel Panel_chart1;
    private javax.swing.JTable Table_detail_tandon;
    private javax.swing.JTable Table_jumlah_anak;
    private javax.swing.JTable Table_kpi;
    private javax.swing.JButton button_close;
    private javax.swing.JButton button_refresh;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel label_jam;
    private javax.swing.JLabel label_jam1;
    // End of variables declaration//GEN-END:variables
}
