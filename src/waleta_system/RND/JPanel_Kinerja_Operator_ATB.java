package waleta_system.RND;

import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.entity.LegendItemEntity;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;
import waleta_system.Class.Utility;

public class JPanel_Kinerja_Operator_ATB extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DecimalFormat decimalFormat = new DecimalFormat();
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    DefaultCategoryDataset dataset;

    public JPanel_Kinerja_Operator_ATB() {
        initComponents();
    }

    public void init(String hak_akses) {
        if (hak_akses.equals("keuangan")) {
            button_save_data_bonus.setVisible(true);
        } else {
            button_save_data_bonus.setVisible(false);
        }
        decimalFormat = Utility.DecimalFormatUS(decimalFormat);
        refreshTable_operator();
        table_data_kinerja_operator.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && table_data_kinerja_operator.getSelectedRow() != -1) {
                    int i = table_data_kinerja_operator.getSelectedRow();
                    refreshTable_lp_dikerjakan(table_data_kinerja_operator.getValueAt(i, 0).toString());
                }
            }
        });
    }

    public void initChart() {
        dataset = new DefaultCategoryDataset();
        JFreeChart chart = ChartFactory.createBarChart("", "Nama Operator", "Total LP", dataset, PlotOrientation.HORIZONTAL, true, true, false);
        chart.setBackgroundPaint(Color.WHITE);
        chart.getTitle().setPaint(Color.red);
        CategoryPlot categoryPlot = (CategoryPlot) chart.getPlot();
        CategoryAxis axis = categoryPlot.getDomainAxis();
//        ValueAxis axis2 = categoryPlot.getRangeAxis();

        axis.setTickLabelFont(new Font("Calibri", Font.PLAIN, 12));
//        axis2.setTickLabelFont(new Font("Verdana", Font.BOLD, 12));

        categoryPlot.setBackgroundPaint(SystemColor.inactiveCaption);
        ((BarRenderer) categoryPlot.getRenderer()).setBarPainter(new StandardBarPainter());
        BarRenderer barRenderer = (BarRenderer) chart.getCategoryPlot().getRenderer();
        barRenderer.setSeriesPaint(0, new Color(0, 255, 128));
        barRenderer.setBaseLegendTextFont(new Font("Calibri", Font.PLAIN, 12));

        // Disable shadow for the bar chart
        barRenderer.setShadowVisible(false);

        ChartPanel panelChart = new ChartPanel(chart);
        panelChart.setLocation(0, 0);
        panelChart.setSize(jPanel_chart.getSize());
        jPanel_chart.add(panelChart);
        panelChart.addChartMouseListener(new ChartMouseListener() {
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

    public void refreshTable_operator() {
        initChart();
        try {
            dataset.clear();
            DefaultTableModel model = (DefaultTableModel) table_data_kinerja_operator.getModel();
            model.setRowCount(0);
            float total_kpg = 0, total_gram = 0, total_bobot_lp = 0, total_bonus_operator = 0;

            String filter_tanggal = "";
            if (Date_penilaian1.getDate() != null && Date_penilaian2.getDate() != null) {
                filter_tanggal = "AND (`tanggal` BETWEEN '" + dateFormat.format(Date_penilaian1.getDate()) + "' AND '" + dateFormat.format(Date_penilaian2.getDate()) + "') \n";
            }

            sql = "SELECT `operator`, `tb_karyawan`.`nama_pegawai`, `tb_atb_produksi`.`no_laporan_produksi`, \n"
                    + "SUM(`tb_atb_produksi`.`keping`) AS 'keping', \n"
                    + "ROUND(SUM(`tb_atb_produksi`.`gram`), 2) AS 'gram', \n"
                    + "SUM(`tb_atb_produksi`.`keping`/`tb_tarif_cabut`.`kpg_lp`) AS 'bobot_lp', \n"
                    + "DAYNAME(MIN(`waktu_mulai`)) AS 'hari', \n"
                    + "DATE(MIN(`waktu_mulai`)) AS 'tgl_mulai_lp', \n"
                    + "TIME(MIN(`waktu_mulai`)) AS 'waktu_mulai_lp'\n"
                    + "FROM `tb_atb_produksi` \n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_atb_produksi`.`operator` = `tb_karyawan`.`id_pegawai`\n"
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_atb_produksi`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah`\n"
                    + "LEFT JOIN `tb_laporan_produksi_penilaian_bulu` ON `tb_atb_produksi`.`no_laporan_produksi` = `tb_laporan_produksi_penilaian_bulu`.`no_laporan_produksi`\n"
                    + "WHERE  \n"
                    + "`id_pegawai_atb` <> 'Bayangan' \n"
                    + "AND `jenis_bulu_asal` = `jenis_bulu_hasil` \n"
                    + "AND `tanggal` IS NOT NULL \n"
                    + filter_tanggal
                    + "GROUP BY `operator`, `tb_atb_produksi`.`no_laporan_produksi` \n"
                    + "ORDER BY `tb_karyawan`.`nama_pegawai` ";
            rs = Utility.db.getStatement().executeQuery(sql);
            HashMap<String, String> NamaPegawai = new HashMap<>();
            HashMap<String, Double> Keping = new HashMap<>();
            HashMap<String, Double> Gram = new HashMap<>();
            HashMap<String, Double> Bobot_LP_Shift_12 = new HashMap<>();
            HashMap<String, Double> Bobot_LP_Shift_3 = new HashMap<>();
            while (rs.next()) {
                NamaPegawai.put(rs.getString("operator"), rs.getString("nama_pegawai"));
                Keping.put(rs.getString("operator"), Keping.getOrDefault(rs.getString("operator"), 0d) + rs.getFloat("keping"));
                Gram.put(rs.getString("operator"), Gram.getOrDefault(rs.getString("operator"), 0d) + rs.getFloat("gram"));

                String indonesianDay = Utility.ubah_ke_INDONESIA(rs.getString("hari"));
                if (rs.getTime("waktu_mulai_lp").before(java.sql.Time.valueOf("06:00:01"))) {//shift 3
                    Bobot_LP_Shift_3.put(rs.getString("operator"), Bobot_LP_Shift_3.getOrDefault(rs.getString("operator"), 0d) + rs.getFloat("bobot_lp"));
                } else if (rs.getTime("waktu_mulai_lp").before(java.sql.Time.valueOf("22:00:01"))) {//shift 1/2
                    Bobot_LP_Shift_12.put(rs.getString("operator"), Bobot_LP_Shift_12.getOrDefault(rs.getString("operator"), 0d) + rs.getFloat("bobot_lp"));
                } else {//shift 3
                    Bobot_LP_Shift_3.put(rs.getString("operator"), Bobot_LP_Shift_3.getOrDefault(rs.getString("operator"), 0d) + rs.getFloat("bobot_lp"));
                }
//                if (indonesianDay.equals("Kamis")) {
//                    if (rs.getTime("waktu_mulai_lp").before(java.sql.Time.valueOf("06:00:01"))) {//shift 3
//                        Bobot_LP_Shift_3.put(rs.getString("operator"), Bobot_LP_Shift_3.getOrDefault(rs.getString("operator"), 0d) + rs.getFloat("bobot_lp"));
//                    } else if (rs.getTime("waktu_mulai_lp").before(java.sql.Time.valueOf("16:00:01"))) {//shift 1/2
//                        Bobot_LP_Shift_12.put(rs.getString("operator"), Bobot_LP_Shift_12.getOrDefault(rs.getString("operator"), 0d) + rs.getFloat("bobot_lp"));
//                    } else if (rs.getTime("waktu_mulai_lp").before(java.sql.Time.valueOf("21:00:01"))) {//shift 3
//                        Bobot_LP_Shift_3.put(rs.getString("operator"), Bobot_LP_Shift_3.getOrDefault(rs.getString("operator"), 0d) + rs.getFloat("bobot_lp"));
//                    }
//                } else {
//                    if (rs.getTime("waktu_mulai_lp").before(java.sql.Time.valueOf("06:00:01"))) {//shift 3
//                        Bobot_LP_Shift_3.put(rs.getString("operator"), Bobot_LP_Shift_3.getOrDefault(rs.getString("operator"), 0d) + rs.getFloat("bobot_lp"));
//                    } else if (rs.getTime("waktu_mulai_lp").before(java.sql.Time.valueOf("22:00:01"))) {//shift 1/2
//                        Bobot_LP_Shift_12.put(rs.getString("operator"), Bobot_LP_Shift_12.getOrDefault(rs.getString("operator"), 0d) + rs.getFloat("bobot_lp"));
//                    } else {//shift 3
//                        Bobot_LP_Shift_3.put(rs.getString("operator"), Bobot_LP_Shift_3.getOrDefault(rs.getString("operator"), 0d) + rs.getFloat("bobot_lp"));
//                    }
//                }

                total_bobot_lp += rs.getFloat("bobot_lp");
                total_kpg += rs.getFloat("keping");
                total_gram += rs.getFloat("gram");
            }

            for (String key : NamaPegawai.keySet()) {
                Object[] row = new Object[10];
                row[0] = key;
                row[1] = NamaPegawai.get(key);
                row[2] = Keping.get(key);
                row[3] = Gram.get(key);
                row[4] = Math.round(Bobot_LP_Shift_12.getOrDefault(key, 0d) * 100d) / 100d;
                row[5] = Math.round(Bobot_LP_Shift_3.getOrDefault(key, 0d) * 100d) / 100d;
                double bobot_lp_3_shift = Math.round((Bobot_LP_Shift_12.getOrDefault(key, 0d) + Bobot_LP_Shift_3.getOrDefault(key, 0d)) * 100d) / 100d;
                row[6] = bobot_lp_3_shift;
                row[7] = 0d;
                row[8] = 0d;
                row[9] = 0d;
                if (bobot_lp_3_shift >= 11d) {
                    double bonus_per_lp_shift12 = txt_bonus_per_lp_shift12.getText() == null || txt_bonus_per_lp_shift12.getText().equals("")
                            ? 0d : Integer.valueOf(txt_bonus_per_lp_shift12.getText());
                    double bonus_per_lp_shift3 = txt_bonus_per_lp_shift3.getText() == null || txt_bonus_per_lp_shift3.getText().equals("")
                            ? 0d : Integer.valueOf(txt_bonus_per_lp_shift3.getText());
                    double bonus_shift_12 = Bobot_LP_Shift_12.getOrDefault(key, 0d) * bonus_per_lp_shift12;
                    double bonus_shift_3 = Bobot_LP_Shift_3.getOrDefault(key, 0d) * bonus_per_lp_shift3;
                    double bonus_operator = bonus_shift_12 + bonus_shift_3;
                    total_bonus_operator += bonus_operator;
                    row[7] = bonus_shift_12;
                    row[8] = bonus_shift_3;
                    row[9] = bonus_operator;
                }
                model.addRow(row);
                dataset.setValue(bobot_lp_3_shift, "Jumlah LP", NamaPegawai.get(key));
                dataset.getColumnKey(0);
            }

            ColumnsAutoSizer.sizeColumnsToFit(table_data_kinerja_operator);
            decimalFormat.setMaximumFractionDigits(2);
            int total_data = table_data_kinerja_operator.getRowCount();
            label_total_data_operator.setText(decimalFormat.format(total_data));
            label_total_kpg_operator.setText(decimalFormat.format(total_kpg));
            label_total_gram_operator.setText(decimalFormat.format(total_gram));
            label_total_bobotLP_operator.setText(decimalFormat.format(total_bobot_lp));
            label_total_bonus_operator.setText(decimalFormat.format(total_bonus_operator));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JPanel_Kinerja_Operator_ATB.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void refreshTable_lp_dikerjakan(String id_operator) {
        try {
            double total_bobot_lp = 0;
            DefaultTableModel model = (DefaultTableModel) table_data_dikerjakan.getModel();
            model.setRowCount(0);

            String filter_tanggal = "";
            if (Date_penilaian1.getDate() != null && Date_penilaian2.getDate() != null) {
                filter_tanggal = "AND `tanggal` BETWEEN '" + dateFormat.format(Date_penilaian1.getDate()) + "' AND '" + dateFormat.format(Date_penilaian2.getDate()) + "' \n";
            }

            sql = "SELECT `tb_atb_produksi`.`no_laporan_produksi`, `kode_grade`, "
                    + "SUM(`tb_atb_produksi`.`keping`/`tb_tarif_cabut`.`kpg_lp`) AS 'bobot_lp', \n"
                    + "`jenis_bulu_lp`, `jenis_bulu_asal`, `jenis_bulu_hasil`, `tanggal` AS 'tgl_evaluasi', \n"
                    + "DATE(MIN(`waktu_mulai`)) AS 'tgl_mulai_lp', "
                    + "TIME(MIN(`waktu_mulai`)) AS 'waktu_mulai_lp', "
                    + "DAYNAME(MIN(`waktu_mulai`)) AS 'hari'\n"
                    + "FROM `tb_atb_produksi` \n"
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_atb_produksi`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah`\n"
                    + "LEFT JOIN `tb_laporan_produksi_penilaian_bulu` ON `tb_atb_produksi`.`no_laporan_produksi` = `tb_laporan_produksi_penilaian_bulu`.`no_laporan_produksi`\n"
                    + "WHERE  \n"
                    + "`tb_atb_produksi`.`operator` = '" + id_operator + "' \n"
                    + "AND `id_pegawai_atb` <> 'Bayangan' \n"
                    + "AND `jenis_bulu_asal` = `jenis_bulu_hasil` \n"
                    + "AND `tanggal` IS NOT NULL \n"
                    + filter_tanggal
                    + "GROUP BY `operator`, `tb_atb_produksi`.`no_laporan_produksi` \n";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[15];
            while (rs.next()) {
                row[0] = rs.getString("no_laporan_produksi");
                row[1] = rs.getString("kode_grade");
                row[2] = Math.round(rs.getDouble("bobot_lp") * 100d) / 100d;
                if (rs.getString("jenis_bulu_asal") == null) {
                    row[3] = rs.getString("jenis_bulu_lp");
                } else {
                    row[3] = rs.getString("jenis_bulu_asal");
                }
                row[4] = rs.getDate("tgl_evaluasi");
                row[5] = rs.getString("jenis_bulu_hasil");
                String indonesianDay = Utility.ubah_ke_INDONESIA(rs.getString("hari"));
                row[6] = indonesianDay;
                row[7] = rs.getDate("tgl_mulai_lp");
                row[8] = rs.getTime("waktu_mulai_lp");
                String shift = "-";
                if (rs.getTime("waktu_mulai_lp").before(java.sql.Time.valueOf("06:00:01"))) {
                    shift = "shift 3";
                } else if (rs.getTime("waktu_mulai_lp").before(java.sql.Time.valueOf("22:00:01"))) {
                    shift = "shift 1/2";
                } else {
                    shift = "shift 3";
                }
//                if (indonesianDay.equals("Kamis")) {
//                    if (rs.getTime("waktu_mulai_lp").before(java.sql.Time.valueOf("06:00:01"))) {
//                        shift = "shift 3";
//                    } else if (rs.getTime("waktu_mulai_lp").before(java.sql.Time.valueOf("16:00:01"))) {
//                        shift = "shift 1/2";
//                    } else if (rs.getTime("waktu_mulai_lp").before(java.sql.Time.valueOf("21:00:01"))) {
//                        shift = "shift 3";
//                    }
//                } else {
//                    if (rs.getTime("waktu_mulai_lp").before(java.sql.Time.valueOf("06:00:01"))) {
//                        shift = "shift 3";
//                    } else if (rs.getTime("waktu_mulai_lp").before(java.sql.Time.valueOf("22:00:01"))) {
//                        shift = "shift 1/2";
//                    } else {
//                        shift = "shift 3";
//                    }
//                }
                row[9] = shift;
                model.addRow(row);
                total_bobot_lp += Math.round(rs.getDouble("bobot_lp") * 100d) / 100d;
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_data_dikerjakan);
            decimalFormat.setMaximumFractionDigits(2);
            int total_data = table_data_dikerjakan.getRowCount();
            label_total_data_dikerjakan.setText(decimalFormat.format(total_data));
            label_total_bobot_dikerjakan.setText(decimalFormat.format(total_bobot_lp));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JPanel_Kinerja_Operator_ATB.class.getName()).log(Level.SEVERE, null, e);
        }
    }

//    public void refreshTable_lp_dikerjakan(String id_operator) {
//        try {
//            DefaultTableModel model = (DefaultTableModel) table_data_dikerjakan.getModel();
//            model.setRowCount(0);
//            float total_kpg = 0, total_gram = 0;
//
//            String filter_tanggal = "";
//            if (Datefilter1.getDate() != null && Datefilter2.getDate() != null) {
//                filter_tanggal = "AND `tanggal` BETWEEN '" + dateFormat.format(Datefilter1.getDate()) + "' AND '" + dateFormat.format(Datefilter2.getDate()) + "'";
//            }
//
//            sql = "SELECT `no`, `tb_atb_produksi`.`no_laporan_produksi`, `tb_laporan_produksi`.`kode_grade`, `id_pegawai_atb`, `keping`, `gram`, `waktu_mulai`, `waktu_selesai`, `nama_pegawai`, `layer_selesai`, "
//                    + "`jenis_bulu_lp`, `tarif_sub`,"
//                    + "(SELECT MIN(`waktu_mulai`) FROM `tb_atb_produksi` A WHERE A.`no_laporan_produksi` = `tb_atb_produksi`.`no_laporan_produksi`) AS 'waktu_mulai_lp',\n"
//                    + "IF((SELECT TIME(MIN(`waktu_mulai`)) FROM `tb_atb_produksi` A WHERE A.`no_laporan_produksi` = `tb_atb_produksi`.`no_laporan_produksi`) <= '15:00:00', 'shift12', 'shift3') AS 'shift'"
//                    + "FROM `tb_atb_produksi` \n"
//                    + "LEFT JOIN `tb_karyawan` ON `tb_atb_produksi`.`operator` = `tb_karyawan`.`id_pegawai`\n"
//                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_atb_produksi`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
//                    + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah`\n"
//                    + "LEFT JOIN `tb_laporan_produksi_penilaian_bulu` ON `tb_atb_produksi`.`no_laporan_produksi` = `tb_laporan_produksi_penilaian_bulu`.`no_laporan_produksi`\n"
//                    + "WHERE "
//                    + "`operator` = '" + id_operator + "'  "
//                    + "AND `id_pegawai_atb` <> 'Bayangan' \n"
//                    + "AND `tanggal` IS NOT NULL \n"
//                    + filter_tanggal;
//            rs = Utility.db.getStatement().executeQuery(sql);
//            Object[] row = new Object[20];
//            while (rs.next()) {
//                row[0] = rs.getString("no_laporan_produksi");
//                row[1] = rs.getString("kode_grade");
//                row[2] = rs.getString("jenis_bulu_lp");
//                row[3] = rs.getString("id_pegawai_atb");
//                row[4] = rs.getFloat("keping");
//                row[5] = rs.getFloat("gram");
//                row[6] = rs.getDate("waktu_mulai");
//                row[7] = rs.getTime("waktu_mulai");
//                row[8] = rs.getDate("waktu_selesai");
//                row[9] = rs.getTime("waktu_selesai");
//                row[10] = rs.getTimestamp("waktu_mulai_lp");
//                row[11] = rs.getString("shift");
//                model.addRow(row);
//                total_kpg += rs.getFloat("keping");
//                total_gram += rs.getFloat("gram");
//            }
//            ColumnsAutoSizer.sizeColumnsToFit(table_data_dikerjakan);
//            decimalFormat.setMaximumFractionDigits(1);
//            int total_data = table_data_dikerjakan.getRowCount();
//            label_total_data_dikerjakan.setText(decimalFormat.format(total_data));
//            label_total_kpg_dikerjakan.setText(decimalFormat.format(total_kpg));
//            label_total_gram_dikerjakan.setText(decimalFormat.format(total_gram));
//        } catch (Exception e) {
//            JOptionPane.showMessageDialog(this, e.getMessage());
//            Logger.getLogger(JPanel_Kinerja_Operator_ATB.class.getName()).log(Level.SEVERE, null, e);
//        }
//    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        txt_search_no_lp = new javax.swing.JTextField();
        button_search = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        Date_penilaian1 = new com.toedter.calendar.JDateChooser();
        Date_penilaian2 = new com.toedter.calendar.JDateChooser();
        jLabel6 = new javax.swing.JLabel();
        label_total_data_operator = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_data_kinerja_operator = new javax.swing.JTable();
        button_export = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        label_total_gram_operator = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        label_total_kpg_operator = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        label_total_bobotLP_operator = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        label_total_bonus_operator = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txt_bonus_per_lp_shift12 = new javax.swing.JTextField();
        jPanel_chart = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txt_bonus_per_lp_shift3 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txt_target_min_LP = new javax.swing.JTextField();
        button_save_data_bonus = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        label_total_data_dikerjakan = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        Button_export_data_dikerjakan = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        table_data_dikerjakan = new javax.swing.JTable();
        jLabel10 = new javax.swing.JLabel();
        label_total_bobot_dikerjakan = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Kinerja ATB", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        txt_search_no_lp.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_no_lp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_no_lpKeyPressed(evt);
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

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("Nama Operator :");

        Date_penilaian1.setBackground(new java.awt.Color(255, 255, 255));
        Date_penilaian1.setDate(new Date(new Date().getTime()- (7 * 24 * 60 * 60 * 1000)));
        Date_penilaian1.setDateFormatString("dd MMM yyyy");
        Date_penilaian1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_penilaian2.setBackground(new java.awt.Color(255, 255, 255));
        Date_penilaian2.setDate(new Date());
        Date_penilaian2.setDateFormatString("dd MMM yyyy");
        Date_penilaian2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Total Data :");

        label_total_data_operator.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_operator.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_data_operator.setText("TOTAL");

        table_data_kinerja_operator.setAutoCreateRowSorter(true);
        table_data_kinerja_operator.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_kinerja_operator.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Operator", "Nama Operator", "Kpg", "Gram", "Bobot Shift 1&2", "Bobot Shift 3", "Total Bobot LP", "Bonus Shift 1&2", "Bonus Shift 3", "Total Bonus"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        jScrollPane1.setViewportView(table_data_kinerja_operator);

        button_export.setBackground(new java.awt.Color(255, 255, 255));
        button_export.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export.setText("Export");
        button_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportActionPerformed(evt);
            }
        });

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel12.setText("Total Gram :");

        label_total_gram_operator.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_operator.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_gram_operator.setText("TOTAL");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel15.setText("Total Keping :");

        label_total_kpg_operator.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg_operator.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_kpg_operator.setText("TOTAL");

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel16.setText("Tgl Evaluasi :");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel13.setText("Total Bobot LP :");

        label_total_bobotLP_operator.setBackground(new java.awt.Color(255, 255, 255));
        label_total_bobotLP_operator.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_bobotLP_operator.setText("TOTAL");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel14.setText("Total Bonus :");

        label_total_bonus_operator.setBackground(new java.awt.Color(255, 255, 255));
        label_total_bonus_operator.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_bonus_operator.setText("TOTAL");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("Bonus Shift 1&2 / LP :");

        txt_bonus_per_lp_shift12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_bonus_per_lp_shift12.setText("2500");
        txt_bonus_per_lp_shift12.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_bonus_per_lp_shift12KeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_bonus_per_lp_shift12KeyTyped(evt);
            }
        });

        jPanel_chart.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_chart.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel_chartLayout = new javax.swing.GroupLayout(jPanel_chart);
        jPanel_chart.setLayout(jPanel_chartLayout);
        jPanel_chartLayout.setHorizontalGroup(
            jPanel_chartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel_chartLayout.setVerticalGroup(
            jPanel_chartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 268, Short.MAX_VALUE)
        );

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("Bonus Shift 3 / LP :");

        txt_bonus_per_lp_shift3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_bonus_per_lp_shift3.setText("4000");
        txt_bonus_per_lp_shift3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_bonus_per_lp_shift3KeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_bonus_per_lp_shift3KeyTyped(evt);
            }
        });

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("Min LP :");

        txt_target_min_LP.setEditable(false);
        txt_target_min_LP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_target_min_LP.setText("11");
        txt_target_min_LP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_target_min_LPKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_target_min_LPKeyTyped(evt);
            }
        });

        button_save_data_bonus.setBackground(new java.awt.Color(255, 255, 255));
        button_save_data_bonus.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_save_data_bonus.setText("Save Data");
        button_save_data_bonus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_save_data_bonusActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data_operator)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_kpg_operator)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_operator)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_bobotLP_operator)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_bonus_operator))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_no_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_penilaian1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_penilaian2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_bonus_per_lp_shift12, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_bonus_per_lp_shift3, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_target_min_LP, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_save_data_bonus)))
                        .addGap(0, 69, Short.MAX_VALUE))
                    .addComponent(jPanel_chart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_search_no_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_penilaian1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_penilaian2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_bonus_per_lp_shift12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_bonus_per_lp_shift3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_target_min_LP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_save_data_bonus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_data_operator, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_kpg_operator, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_gram_operator, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_bobotLP_operator, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_bonus_operator, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel_chart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Data LP Dikerjakan", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12))); // NOI18N

        label_total_data_dikerjakan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_dikerjakan.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_data_dikerjakan.setText("TOTAL");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("Total Data :");

        Button_export_data_dikerjakan.setBackground(new java.awt.Color(255, 255, 255));
        Button_export_data_dikerjakan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Button_export_data_dikerjakan.setText("Export");
        Button_export_data_dikerjakan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_export_data_dikerjakanActionPerformed(evt);
            }
        });

        table_data_dikerjakan.setAutoCreateRowSorter(true);
        table_data_dikerjakan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_dikerjakan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP", "Grade", "Bobot LP", "Bulu Asal", "Tgl Penilaian", "Bulu Akhir", "Hari", "Tgl Mulai", "Waktu Mulai", "Shift"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class
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
        jScrollPane3.setViewportView(table_data_dikerjakan);

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel10.setText("Total Bobot :");

        label_total_bobot_dikerjakan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_bobot_dikerjakan.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_bobot_dikerjakan.setText("TOTAL");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data_dikerjakan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_bobot_dikerjakan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Button_export_data_dikerjakan))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 585, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_bobot_dikerjakan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_data_dikerjakan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(Button_export_data_dikerjakan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jTextArea1.setRows(5);
        jTextArea1.setText("Aturan Bonus :\n1. Minimal 11 LP dikerjakan.\n2. LP yang sudah dinilai, tanggal bonus mengikuti tanggal penilaian LP.\n3. Tidak ada adjustment bulu.\n4. Nominal Bonus / LP mengikuti shift kerja.\n\tHari kamis : \n\t\tshift 3: 00:00-06:00\n\t\tshift 1: 06:00-11:00\n\t\tshift 2: 11:00-16:00\n\t\tshift 3: 16:00-21:00\n\tSelain kamis :\n\t\tshift 1: 06:00-14:00\n\t\tshift 2: 14:00-22:00\n\t\tshift 3: 22:00-06:00\n5. Level Gaji harus SHIFT MALAM.");
        jScrollPane2.setViewportView(jTextArea1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txt_search_no_lpKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_no_lpKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_operator();
        }
    }//GEN-LAST:event_txt_search_no_lpKeyPressed

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        refreshTable_operator();
    }//GEN-LAST:event_button_searchActionPerformed

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_data_kinerja_operator.getModel();
        ExportToExcel.writeToExcel(model, jPanel2);
    }//GEN-LAST:event_button_exportActionPerformed

    private void Button_export_data_dikerjakanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_export_data_dikerjakanActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_data_dikerjakan.getModel();
        ExportToExcel.writeToExcel(model, jPanel2);
    }//GEN-LAST:event_Button_export_data_dikerjakanActionPerformed

    private void txt_bonus_per_lp_shift12KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_bonus_per_lp_shift12KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_operator();
        }
    }//GEN-LAST:event_txt_bonus_per_lp_shift12KeyPressed

    private void txt_bonus_per_lp_shift12KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_bonus_per_lp_shift12KeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_bonus_per_lp_shift12KeyTyped

    private void txt_bonus_per_lp_shift3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_bonus_per_lp_shift3KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_operator();
        }
    }//GEN-LAST:event_txt_bonus_per_lp_shift3KeyPressed

    private void txt_bonus_per_lp_shift3KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_bonus_per_lp_shift3KeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_bonus_per_lp_shift3KeyTyped

    private void txt_target_min_LPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_target_min_LPKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_operator();
        }
    }//GEN-LAST:event_txt_target_min_LPKeyPressed

    private void txt_target_min_LPKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_target_min_LPKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_target_min_LPKeyTyped

    private void button_save_data_bonusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_save_data_bonusActionPerformed
        // TODO add your handling code here:
        boolean check = true;
        String hari1 = Date_penilaian1.getDate() != null ? new SimpleDateFormat("EEE").format(Date_penilaian1.getDate()) : "";
        String hari2 = Date_penilaian2.getDate() != null ? new SimpleDateFormat("EEE").format(Date_penilaian2.getDate()) : "";
        if (table_data_kinerja_operator.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tidak ada data pada tabel!");
            check = false;
        } else if (Date_penilaian1.getDate() == null || Date_penilaian2.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih tanggal sesuai periode penggajian!");
            check = false;
        } else if (!hari1.equals("Thu")) {
            JOptionPane.showMessageDialog(this, "periode penggajian dimulai pada hari kamis!");
            check = false;
        } else if (!hari2.equals("Wed")) {
            JOptionPane.showMessageDialog(this, "periode penggajian selesai pada hari rabu!");
            check = false;
        }

        if (check) {
            int dialogResult = JOptionPane.showConfirmDialog(this, "Simpan data bonus?", "Warning", 0);
            if (dialogResult == JOptionPane.YES_OPTION) {
                try {
                    Utility.db.getConnection().setAutoCommit(false);
                    for (int i = 0; i < table_data_kinerja_operator.getRowCount(); i++) {
                        String Query = "INSERT INTO `tb_bonus_operator_atb`(`id_pegawai`, `tanggal`, `bonus_operator_atb`) "
                                + "VALUES ("
                                + "'" + table_data_kinerja_operator.getValueAt(i, 0).toString() + "',"
                                + "'" + dateFormat.format(Date_penilaian2.getDate()) + "',"
                                + table_data_kinerja_operator.getValueAt(i, 9).toString() + ") "
                                + "ON DUPLICATE KEY UPDATE "
                                + "`bonus_operator_atb`=" + table_data_kinerja_operator.getValueAt(i, 9).toString();
                        Utility.db.getConnection().createStatement();
                        Utility.db.getStatement().executeUpdate(Query);
                    }
                    Utility.db.getConnection().commit();
                    JOptionPane.showMessageDialog(this, "Data telah tersimpan\nData bonus ada diperhitungan SHIFT MALAM");
                } catch (Exception e) {
                    try {
                        Utility.db.getConnection().rollback();
                    } catch (SQLException ex) {
                        Logger.getLogger(JPanel_Kinerja_Operator_ATB.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    JOptionPane.showMessageDialog(this, "Save Failed !" + e);
                    Logger.getLogger(JPanel_Kinerja_Operator_ATB.class.getName()).log(Level.SEVERE, null, e);
                } finally {
                    try {
                        Utility.db.getConnection().setAutoCommit(true);
                    } catch (SQLException ex) {
                        Logger.getLogger(JPanel_Kinerja_Operator_ATB.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }//GEN-LAST:event_button_save_data_bonusActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Button_export_data_dikerjakan;
    private com.toedter.calendar.JDateChooser Date_penilaian1;
    private com.toedter.calendar.JDateChooser Date_penilaian2;
    private javax.swing.JButton button_export;
    private javax.swing.JButton button_save_data_bonus;
    public static javax.swing.JButton button_search;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel_chart;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel label_total_bobotLP_operator;
    private javax.swing.JLabel label_total_bobot_dikerjakan;
    private javax.swing.JLabel label_total_bonus_operator;
    private javax.swing.JLabel label_total_data_dikerjakan;
    private javax.swing.JLabel label_total_data_operator;
    private javax.swing.JLabel label_total_gram_operator;
    private javax.swing.JLabel label_total_kpg_operator;
    private javax.swing.JTable table_data_dikerjakan;
    private javax.swing.JTable table_data_kinerja_operator;
    private javax.swing.JTextField txt_bonus_per_lp_shift12;
    private javax.swing.JTextField txt_bonus_per_lp_shift3;
    private javax.swing.JTextField txt_search_no_lp;
    private javax.swing.JTextField txt_target_min_LP;
    // End of variables declaration//GEN-END:variables

}
