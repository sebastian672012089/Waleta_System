package waleta_system;

import java.awt.BasicStroke;
import java.awt.Color;
import waleta_system.Class.Utility;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;

public class JFrame_Tampilan_TidakAbsen extends javax.swing.JFrame {

    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date today = new Date();
    static Timer t;
    public DefaultCategoryDataset chart_dataset;
    JFreeChart chart_jumlahTerlambat;

    public JFrame_Tampilan_TidakAbsen() {
        initComponents();
    }

    public void init() {
        try {
            init_chart();
            tabel_data_TidakMasuk1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && tabel_data_TidakMasuk1.getSelectedRow() != -1) {
                        int i = tabel_data_TidakMasuk1.getSelectedRow();
                    }
                }
            });
            ComboBox_grup.removeAllItems();
            ComboBox_grup.addItem("All");
            ComboBox_grup1.removeAllItems();
            ComboBox_grup1.addItem("All");
            sql = "SELECT DISTINCT(`ruang_bagian`) AS 'ruang_bagian' "
                    + "FROM `tb_bagian` "
                    + "WHERE `ruang_bagian` IS NOT NULL "
                    + "ORDER BY `kode_departemen`, ruang_bagian";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_grup.addItem(rs.getString("ruang_bagian"));
                ComboBox_grup1.addItem(rs.getString("ruang_bagian"));
            }
            refreshTable_TidakMasuk();

            TimerTask timerTask;
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    switch (jTabbedPane1.getSelectedIndex()) {
                        case 0:
                            jTabbedPane1.setSelectedIndex(1);
                            refreshTable_terlambat();
                            break;
                        case 1:
                            jTabbedPane1.setSelectedIndex(2);
                            refreshTable_jumlah_terlambat_dalam_1_periode();
                            break;
                        default:
                            jTabbedPane1.setSelectedIndex(0);
                            refreshTable_TidakMasuk();
                            break;
                    }
                }
            };
            t = new Timer();
            t.schedule(timerTask, 120000, 120000); //120 detik = 2 menit
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JFrame_Tampilan_TidakAbsen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void init_chart() {
        // Create the chart dataset
        chart_dataset = new DefaultCategoryDataset();

        // Create the bar chart
        chart_jumlahTerlambat = ChartFactory.createBarChart(
                "Jumlah Terlambat", "", "Jumlah", chart_dataset,
                PlotOrientation.VERTICAL, false, true, false);

        // Set background color for the chart
        chart_jumlahTerlambat.setBackgroundPaint(Color.WHITE);
        chart_jumlahTerlambat.getTitle().setPaint(Color.RED);

        // Get the CategoryPlot of the chart
        CategoryPlot cp = (CategoryPlot) chart_jumlahTerlambat.getPlot();

        // Get the range (y-axis) of the plot
        ValueAxis rangeAxis = cp.getRangeAxis();

        // Set the range of the y-axis
        rangeAxis.setRange(0, 100);

        // Create a dashed stroke with dot pattern
        float[] dashPattern = {10f, 5f}; // Customize the pattern as needed
        BasicStroke dashedStroke = new BasicStroke(5.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, dashPattern, 0.0f);

        // Create a ValueMarker with the dashed stroke
        ValueMarker boundaryMarker = new ValueMarker(30, Color.RED, dashedStroke);

        // Add the boundary marker to the plot
        cp.addRangeMarker(boundaryMarker);

        // Get the BarRenderer of the chart's CategoryPlot
        BarRenderer barRenderer = (BarRenderer) cp.getRenderer();

        // Set the bar painter to a StandardBarPainter for 2D bars
        barRenderer.setBarPainter(new StandardBarPainter());

        // Create the ChartPanel
        ChartPanel panelChartJumlahTerlambat = new ChartPanel(chart_jumlahTerlambat);
        panelChartJumlahTerlambat.setLocation(0, 0);
        panelChartJumlahTerlambat.setSize(Panel_chart.getSize());

        // Add the ChartPanel to the Panel_chart
        Panel_chart.add(panelChartJumlahTerlambat);
    }

    private void refreshTable_TidakMasuk() {
        try {
            Connection con = Utility.db.getConnection();
            DefaultTableModel model1 = (DefaultTableModel) tabel_data_TidakMasuk1.getModel();
            model1.setRowCount(0);
            DefaultTableModel model2 = (DefaultTableModel) tabel_data_TidakMasuk2.getModel();
            model2.setRowCount(0);
            label_tanggal.setText(new SimpleDateFormat("dd MMMM yyyy").format(Date_TidakMasuk.getDate()));
            String jam1 = txt_jam1.getText();
            String jam2 = txt_jam2.getText();

            sql = "SELECT `id_pegawai`, `pin_finger`, `nik_ktp`, `nama_pegawai`, `tb_bagian`.`nama_bagian`, `tb_bagian`.`kode_departemen`, `tb_bagian`.`ruang_bagian`, "
                    + "absen.`tanggal`, absen.`jam` "
                    + "FROM `tb_karyawan` "
                    + "LEFT JOIN `tb_bagian` ON `tb_bagian`.`kode_bagian` = `tb_karyawan`.`kode_bagian` "
                    + "LEFT JOIN (SELECT `pin`, DATE(`scan_date`) AS 'tanggal', TIME(`scan_date`) AS 'jam' "
                    + "FROM `att_log` "
                    + "WHERE DATE(`scan_date`) = '" + dateFormat.format(Date_TidakMasuk.getDate()) + "' "
                    + "AND HOUR(`scan_date`) BETWEEN '" + jam1 + "' AND '" + jam2 + "') absen "
                    + "ON `tb_karyawan`.`pin_finger` = absen.`pin` "
                    + "WHERE `status` = 'IN' \n"
                    + "AND absen.`tanggal` IS NULL \n"
                    + "AND `pin_finger` LIKE '%" + txt_search_pin2.getText() + "%'\n"
                    + "AND `nama_pegawai` LIKE '%" + txt_search_NamaKaryawan2.getText() + "%'\n"
                    + "ORDER BY `nama_pegawai`";
//                    System.out.println(sql);
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            Object[] row = new Object[7];
            int nomor = 1;
            while (rs.next()) {
//                row[0] = dateFormat.format(Date_TidakMasuk2.getDate());
//                row[1] = rs.getString("pin_finger");
//                row[1] = rs.getString("id_pegawai");
                row[0] = nomor;
                row[1] = rs.getString("nama_pegawai");
                String[] bagian = rs.getString("nama_bagian").toUpperCase().split("-");
                if (bagian[4].equals("A") || bagian[4].equals("B") || bagian[4].equals("C") || bagian[4].equals("D") || bagian[4].equals("E")) {
                    row[2] = bagian[2] + "-" + bagian[3] + "-" + bagian[4];
                } else {
                    row[2] = bagian[3];
                }

                if ("All".equals(ComboBox_grup.getSelectedItem().toString())) {
                    if (nomor >= 16) {
                        model2.addRow(row);
                    } else {
                        model1.addRow(row);
                    }
                    nomor++;
                } else if ("ATB".equals(ComboBox_grup.getSelectedItem().toString())) {
                    if (rs.getString("nama_bagian").contains("SECURITY") || rs.getString("ruang_bagian").equals("ATB")) {
                        if (nomor >= 16) {
                            model2.addRow(row);
                        } else {
                            model1.addRow(row);
                        }
                        nomor++;
                    }
                } else if (rs.getString("ruang_bagian").equals(ComboBox_grup.getSelectedItem().toString())) {
                    if (nomor >= 16) {
                        model2.addRow(row);
                    } else {
                        model1.addRow(row);
                    }
                    nomor++;
                }

//                String b = "SELECT DATE(`scan_date`) AS 'tanggal', TIME(`scan_date`) AS 'jam' FROM `att_log` "
//                        + "WHERE `pin` = '" + rs.getString("pin_finger") + "' "
//                        + "AND DATE(`scan_date`) = '" + dateFormat.format(Date_TidakMasuk.getDate()) + "' "
//                        + "AND HOUR(`scan_date`) BETWEEN '" + jam1 + "' AND '" + jam2 + "' "
//                        + "ORDER BY `scan_date`";
////                        System.out.println(b);
//                PreparedStatement pst1 = con.prepareStatement(b);
//                ResultSet rs1 = pst1.executeQuery();
//                if (rs1.next()) {
//                    row[4] = rs1.getDate("tanggal");
//                    row[5] = rs1.getTime("jam");
//                    if (ComboBox_tampilan.getSelectedIndex() == 0) {
//                        if (nomor >= 16) {
//                            model2.addRow(row);
//                        } else {
//                            model1.addRow(row);
//                        }
//                        nomor++;
//                    }
//                } else {
//                    row[4] = "-";
//                    row[5] = "-";
//                    if (nomor >= 16) {
//                        model2.addRow(row);
//                    } else {
//                        model1.addRow(row);
//                    }
//                    nomor++;
//                }
            }
            label_total_data_TidakMasuk.setText(Integer.toString(nomor - 1));
            ColumnsAutoSizer.sizeColumnsToFit(tabel_data_TidakMasuk1);
            ColumnsAutoSizer.sizeColumnsToFit(tabel_data_TidakMasuk2);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JFrame_Tampilan_TidakAbsen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void refreshTable_terlambat() {
        try {
            label_tanggal_terlambat.setText("Tanggal : " + new SimpleDateFormat("dd MMMM yyyy").format(new Date()));
            String ruang_bagian = "AND `tb_bagian`.`ruang_bagian` = '" + ComboBox_grup1.getSelectedItem().toString() + "' ";
            if ("All".equals(ComboBox_grup1.getSelectedItem().toString())) {
                ruang_bagian = "";
            }
            DefaultTableModel model = (DefaultTableModel) tabel_data_Terlambat.getModel();
            model.setRowCount(0);

            sql = "SELECT `pin`, `nama_pegawai`, `tb_bagian`.`nama_bagian`, `tb_bagian`.`kode_departemen`, `tb_karyawan`.`jam_kerja`, DATE(`scan_date`) AS 'tanggal', DAYNAME(`scan_date`) AS 'day_name', \n"
                    + "(CASE  \n"
                    + "WHEN DAYNAME(`scan_date`) = 'Monday' THEN `masuk1` \n"
                    + "WHEN DAYNAME(`scan_date`) = 'Tuesday' THEN `masuk2` \n"
                    + "WHEN DAYNAME(`scan_date`) = 'Wednesday' THEN `masuk3` \n"
                    + "WHEN DAYNAME(`scan_date`) = 'Thursday' THEN `masuk4` \n"
                    + "WHEN DAYNAME(`scan_date`) = 'Friday' THEN `masuk5` \n"
                    + "WHEN DAYNAME(`scan_date`) = 'Saturday' THEN `masuk6` \n"
                    + "WHEN DAYNAME(`scan_date`) = 'Sunday' THEN `masuk7` \n"
                    + "END) AS 'jadwal_masuk',\n"
                    + "TIME(MIN(`scan_date`)) AS 'absen',\n"
                    + "TIME_FORMAT(DATE_FORMAT(MIN(`scan_date`), '%H:%i:00'), '%H:%i:%s') AS 'rounded_time'\n"
                    + "FROM `att_log` \n"
                    + "LEFT JOIN `tb_karyawan` ON `att_log`.`pin` = `tb_karyawan`.`pin_finger`\n"
                    + "LEFT JOIN `tb_bagian` ON `tb_bagian`.`kode_bagian` = `tb_karyawan`.`kode_bagian` \n"
                    + "LEFT JOIN `tb_jam_kerja` ON `tb_karyawan`.`jam_kerja` = `tb_jam_kerja`.`jam_kerja` \n"
                    + "WHERE `status` = 'IN' \n"
                    + "AND `tb_karyawan`.`posisi` = 'PEJUANG' \n"
                    + "AND `tb_karyawan`.`jam_kerja` <> 'SHIFT_MALAM' \n"
                    + "AND `tb_karyawan`.`level_gaji` IS NOT NULL \n"
                    + "AND `tb_karyawan`.`kode_bagian` IS NOT NULL \n"
                    + "AND DATE(`scan_date`) = CURRENT_DATE()\n"
                    + ruang_bagian
                    + "GROUP BY `pin`\n"
                    + "HAVING TIME(`absen`) > ADDTIME(TIME(`jadwal_masuk`), '00:02:00')\n"
                    + "ORDER BY `scan_date` DESC \n";
            Connection con = Utility.db.getConnection();
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            Object[] row = new Object[7];
            while (rs.next()) {
                row[0] = rs.getString("absen");
                row[1] = rs.getString("nama_pegawai");
                row[2] = rs.getString("kode_departemen");
                String[] bagian = rs.getString("nama_bagian").toUpperCase().split("-");
                if (bagian[4].equals("A") || bagian[4].equals("B") || bagian[4].equals("C") || bagian[4].equals("D") || bagian[4].equals("E")) {
                    row[3] = bagian[2] + "-" + bagian[3] + "-" + bagian[4];
                } else {
                    row[3] = bagian[3];
                }
                model.addRow(row);
            }
            label_jumlah_terlambat.setText(Integer.toString(tabel_data_Terlambat.getRowCount()));
            ColumnsAutoSizer.sizeColumnsToFit(tabel_data_Terlambat);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JFrame_Tampilan_TidakAbsen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void refreshTable_jumlah_terlambat_dalam_1_periode() {
        try {
            DefaultTableModel model = (DefaultTableModel) tabel_data_jumlah_Terlambat.getModel();
            model.setRowCount(0);
            chart_dataset.clear();

            sql = "SELECT COUNT(`pin`) AS 'jumlah', `pin`, `nama_pegawai`, `nama_bagian`, `kode_departemen` "
                    + "FROM (\n"
                    + "SELECT `pin`, `nama_pegawai`, `tb_bagian`.`nama_bagian`, `tb_bagian`.`kode_departemen`, `tb_karyawan`.`jam_kerja`, DATE(`scan_date`) AS 'tanggal', DAYNAME(`scan_date`) AS 'day_name', \n"
                    + "(CASE  \n"
                    + "WHEN DAYNAME(`scan_date`) = 'Monday' THEN `masuk1` \n"
                    + "WHEN DAYNAME(`scan_date`) = 'Tuesday' THEN `masuk2` \n"
                    + "WHEN DAYNAME(`scan_date`) = 'Wednesday' THEN `masuk3` \n"
                    + "WHEN DAYNAME(`scan_date`) = 'Thursday' THEN `masuk4` \n"
                    + "WHEN DAYNAME(`scan_date`) = 'Friday' THEN `masuk5` \n"
                    + "WHEN DAYNAME(`scan_date`) = 'Saturday' THEN `masuk6` \n"
                    + "WHEN DAYNAME(`scan_date`) = 'Sunday' THEN `masuk7` \n"
                    + "END) AS 'jadwal_masuk',\n"
                    + "TIME(MIN(`scan_date`)) AS 'absen',\n"
                    + "TIME_FORMAT(DATE_FORMAT(MIN(`scan_date`), '%H:%i:00'), '%H:%i:%s') AS 'rounded_time'\n"
                    + "FROM `att_log` \n"
                    + "LEFT JOIN `tb_karyawan` ON `att_log`.`pin` = `tb_karyawan`.`pin_finger`\n"
                    + "LEFT JOIN `tb_bagian` ON `tb_bagian`.`kode_bagian` = `tb_karyawan`.`kode_bagian` \n"
                    + "LEFT JOIN `tb_jam_kerja` ON `tb_karyawan`.`jam_kerja` = `tb_jam_kerja`.`jam_kerja` \n"
                    + "WHERE `status` = 'IN' \n"
                    + "AND `tb_karyawan`.`posisi` = 'PEJUANG' \n"
                    + "AND `tb_karyawan`.`jam_kerja` <> 'SHIFT_MALAM' \n"
                    + "AND `tb_karyawan`.`level_gaji` IS NOT NULL \n"
                    + "AND `tb_karyawan`.`kode_bagian` IS NOT NULL \n"
                    + "AND `tb_bagian`.`nama_bagian` NOT LIKE '%BORONG%' \n"
                    + "AND DATE(`scan_date`) BETWEEN '" + dateFormat.format(Date_terlamabat1.getDate()) + "' AND '" + dateFormat.format(Date_terlamabat2.getDate()) + "'\n"
                    + "GROUP BY `pin`, DATE(`scan_date`)\n"
                    + "HAVING TIME(`absen`) > ADDTIME(TIME(`jadwal_masuk`), '00:02:00')\n"
                    + "ORDER BY `scan_date` DESC) TABEL\n"
                    + "WHERE 1\n"
                    + "GROUP BY `pin` \n"
                    + "ORDER BY `jumlah` DESC";
            Connection con = Utility.db.getConnection();
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            Object[] row = new Object[7];
            while (rs.next()) {
                row[0] = rs.getInt("jumlah");
                row[1] = rs.getString("nama_pegawai");
                row[2] = rs.getString("kode_departemen");
                String[] bagian = rs.getString("nama_bagian").toUpperCase().split("-");
                if (bagian[4].equals("A") || bagian[4].equals("B") || bagian[4].equals("C") || bagian[4].equals("D") || bagian[4].equals("E")) {
                    row[3] = bagian[2] + "-" + bagian[3] + "-" + bagian[4];
                } else {
                    row[3] = bagian[3];
                }
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_data_jumlah_Terlambat);
            int total_data = tabel_data_jumlah_Terlambat.getRowCount();
            label_jumlah_terlambat_1_periode.setText(Integer.toString(total_data));

            // Get the CategoryPlot of the chart
            CategoryPlot cp = (CategoryPlot) chart_jumlahTerlambat.getPlot();

            // Get the BarRenderer of the chart's CategoryPlot
            BarRenderer barRenderer = (BarRenderer) cp.getRenderer();

            if (total_data > 30) {
                label_jumlah_terlambat_1_periode.setForeground(Color.red);
                // Set the bar colors for each series
                barRenderer.setSeriesPaint(0, Color.ORANGE);
            } else {
                label_jumlah_terlambat_1_periode.setForeground(new Color(0, 153, 76));
                // Set the bar colors for each series
                barRenderer.setSeriesPaint(0, new Color(0, 153, 76));
            }

            // Update values in the dataset
            chart_dataset.setValue(total_data, "", "Karyawan Terlambat");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JFrame_Tampilan_TidakAbsen.class.getName()).log(Level.SEVERE, null, ex);
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

        jPanel2 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txt_search_NamaKaryawan2 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txt_search_pin2 = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        Date_TidakMasuk = new com.toedter.calendar.JDateChooser();
        button_refresh_data_TidakMasuk = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabel_data_TidakMasuk1 = new javax.swing.JTable();
        jLabel15 = new javax.swing.JLabel();
        label_total_data_TidakMasuk = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        ComboBox_grup = new javax.swing.JComboBox<>();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        button_export_tdkMasuk = new javax.swing.JButton();
        txt_jam1 = new javax.swing.JTextField();
        txt_jam2 = new javax.swing.JTextField();
        label_tanggal = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tabel_data_TidakMasuk2 = new javax.swing.JTable();
        label_tanggal1 = new javax.swing.JLabel();
        label_tanggal2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tabel_data_Terlambat = new javax.swing.JTable();
        label_tanggal_terlambat = new javax.swing.JLabel();
        ComboBox_grup1 = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        label_jumlah_terlambat = new javax.swing.JLabel();
        button_refresh_data_TidakMasuk1 = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tabel_data_jumlah_Terlambat = new javax.swing.JTable();
        label_tanggal_jumlah_terlambat = new javax.swing.JLabel();
        label_jumlah_terlambat_1_periode = new javax.swing.JLabel();
        button_refresh_jumlah_terlambat = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        Date_terlamabat1 = new com.toedter.calendar.JDateChooser();
        Date_terlamabat2 = new com.toedter.calendar.JDateChooser();
        Panel_chart = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        label_jumlah_terlambat_1_periode1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Nama :");

        txt_search_NamaKaryawan2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_NamaKaryawan2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_NamaKaryawan2KeyPressed(evt);
            }
        });

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("PIN :");

        txt_search_pin2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_pin2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_pin2KeyPressed(evt);
            }
        });

        jLabel37.setBackground(new java.awt.Color(255, 255, 255));
        jLabel37.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        jLabel37.setText("Tanggal :");

        Date_TidakMasuk.setBackground(new java.awt.Color(255, 255, 255));
        Date_TidakMasuk.setDate(new Date());
        Date_TidakMasuk.setDateFormatString("dd MMMM yyyy");
        Date_TidakMasuk.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N

        button_refresh_data_TidakMasuk.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh_data_TidakMasuk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh_data_TidakMasuk.setText("Refresh");
        button_refresh_data_TidakMasuk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh_data_TidakMasukActionPerformed(evt);
            }
        });

        tabel_data_TidakMasuk1.setAutoCreateRowSorter(true);
        tabel_data_TidakMasuk1.setFont(new java.awt.Font("Arial Narrow", 1, 36)); // NOI18N
        tabel_data_TidakMasuk1.setModel(new javax.swing.table.DefaultTableModel(
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
                {null, null, null}
            },
            new String [] {
                "No", "Nama", "Bagian"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class
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
        tabel_data_TidakMasuk1.setRowHeight(32);
        jScrollPane3.setViewportView(tabel_data_TidakMasuk1);

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel15.setText("Total Data :");

        label_total_data_TidakMasuk.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_TidakMasuk.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        label_total_data_TidakMasuk.setText("0");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Grup Ruangan :");

        ComboBox_grup.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_grup.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel17.setText("Jam");

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel18.setText(" - ");

        button_export_tdkMasuk.setBackground(new java.awt.Color(255, 255, 255));
        button_export_tdkMasuk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_tdkMasuk.setText("Export");
        button_export_tdkMasuk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_tdkMasukActionPerformed(evt);
            }
        });

        txt_jam1.setEditable(false);
        txt_jam1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_jam1.setText("5:00");

        txt_jam2.setEditable(false);
        txt_jam2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_jam2.setText("11:00");

        label_tanggal.setBackground(new java.awt.Color(255, 255, 255));
        label_tanggal.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        label_tanggal.setText("Tanggal");

        tabel_data_TidakMasuk2.setAutoCreateRowSorter(true);
        tabel_data_TidakMasuk2.setFont(new java.awt.Font("Arial Narrow", 1, 36)); // NOI18N
        tabel_data_TidakMasuk2.setModel(new javax.swing.table.DefaultTableModel(
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
                {null, null, null}
            },
            new String [] {
                "No", "Nama", "Bagian"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class
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
        tabel_data_TidakMasuk2.setRowHeight(32);
        jScrollPane5.setViewportView(tabel_data_TidakMasuk2);

        label_tanggal1.setBackground(new java.awt.Color(255, 255, 255));
        label_tanggal1.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        label_tanggal1.setForeground(new java.awt.Color(255, 0, 0));
        label_tanggal1.setText("UNTUK NAMA-NAMA DIBAWAH INI,");

        label_tanggal2.setBackground(new java.awt.Color(255, 255, 255));
        label_tanggal2.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        label_tanggal2.setForeground(new java.awt.Color(255, 0, 0));
        label_tanggal2.setText("HARAP SEGERA MELAKUKAN ABSEN DI MESIN ABSENSI !!!");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_tanggal1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane5))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_tanggal)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_NamaKaryawan2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_pin2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel37)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_TidakMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_jam1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_jam2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_grup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_refresh_data_TidakMasuk)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export_tdkMasuk)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data_TidakMasuk)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(label_tanggal2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1339, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(txt_jam2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_jam1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date_TidakMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_search_pin2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_search_NamaKaryawan2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_grup, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_tanggal))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(button_refresh_data_TidakMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_export_tdkMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_data_TidakMasuk)
                        .addComponent(jLabel15)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_tanggal1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_tanggal2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("DATA BELUM ABSEN", jPanel3);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        tabel_data_Terlambat.setAutoCreateRowSorter(true);
        tabel_data_Terlambat.setFont(new java.awt.Font("Arial Narrow", 1, 36)); // NOI18N
        tabel_data_Terlambat.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Jam Absen", "Nama", "Departemen", "Bagian"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        tabel_data_Terlambat.setRowHeight(35);
        tabel_data_Terlambat.setRowSelectionAllowed(false);
        tabel_data_Terlambat.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(tabel_data_Terlambat);

        label_tanggal_terlambat.setBackground(new java.awt.Color(255, 255, 255));
        label_tanggal_terlambat.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        label_tanggal_terlambat.setText("Tanggal :");

        ComboBox_grup1.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        ComboBox_grup1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel12.setText("Grup Ruangan :");

        label_jumlah_terlambat.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_terlambat.setFont(new java.awt.Font("Arial", 1, 48)); // NOI18N
        label_jumlah_terlambat.setForeground(new java.awt.Color(255, 0, 0));
        label_jumlah_terlambat.setText("888");

        button_refresh_data_TidakMasuk1.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh_data_TidakMasuk1.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        button_refresh_data_TidakMasuk1.setText("Refresh");
        button_refresh_data_TidakMasuk1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh_data_TidakMasuk1ActionPerformed(evt);
            }
        });

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel13.setText("Jumlah Terlambat :");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(label_tanggal_terlambat)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_grup1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_refresh_data_TidakMasuk1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_jumlah_terlambat))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 1341, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel12)
                    .addComponent(ComboBox_grup1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_tanggal_terlambat)
                    .addComponent(label_jumlah_terlambat)
                    .addComponent(button_refresh_data_TidakMasuk1)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 613, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("DATA TERLAMBAT HARI INI", jPanel1);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        tabel_data_jumlah_Terlambat.setAutoCreateRowSorter(true);
        tabel_data_jumlah_Terlambat.setFont(new java.awt.Font("Arial Narrow", 1, 36)); // NOI18N
        tabel_data_jumlah_Terlambat.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Jumlah", "Nama", "Departemen", "Bagian"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        tabel_data_jumlah_Terlambat.setRowHeight(35);
        tabel_data_jumlah_Terlambat.getTableHeader().setReorderingAllowed(false);
        jScrollPane6.setViewportView(tabel_data_jumlah_Terlambat);

        label_tanggal_jumlah_terlambat.setBackground(new java.awt.Color(255, 255, 255));
        label_tanggal_jumlah_terlambat.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        label_tanggal_jumlah_terlambat.setText("Tanggal :");

        label_jumlah_terlambat_1_periode.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_terlambat_1_periode.setFont(new java.awt.Font("Arial", 1, 48)); // NOI18N
        label_jumlah_terlambat_1_periode.setForeground(new java.awt.Color(255, 0, 0));
        label_jumlah_terlambat_1_periode.setText("888");

        button_refresh_jumlah_terlambat.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh_jumlah_terlambat.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        button_refresh_jumlah_terlambat.setText("Refresh");
        button_refresh_jumlah_terlambat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh_jumlah_terlambatActionPerformed(evt);
            }
        });

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel16.setText("Jumlah Terlambat :");

        Date_terlamabat1.setBackground(new java.awt.Color(255, 255, 255));
        Date_terlamabat1.setDate(new Date());
        Date_terlamabat1.setDateFormatString("dd MMM yyyy");
        Date_terlamabat1.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N

        Date_terlamabat2.setBackground(new java.awt.Color(255, 255, 255));
        Date_terlamabat2.setDate(new Date());
        Date_terlamabat2.setDateFormatString("dd MMM yyyy");
        Date_terlamabat2.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N

        Panel_chart.setBackground(new java.awt.Color(255, 255, 255));
        Panel_chart.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        javax.swing.GroupLayout Panel_chartLayout = new javax.swing.GroupLayout(Panel_chart);
        Panel_chart.setLayout(Panel_chartLayout);
        Panel_chartLayout.setHorizontalGroup(
            Panel_chartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 460, Short.MAX_VALUE)
        );
        Panel_chartLayout.setVerticalGroup(
            Panel_chartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 613, Short.MAX_VALUE)
        );

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel19.setText("MAX :");

        label_jumlah_terlambat_1_periode1.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_terlambat_1_periode1.setFont(new java.awt.Font("Arial", 1, 48)); // NOI18N
        label_jumlah_terlambat_1_periode1.setForeground(new java.awt.Color(255, 0, 0));
        label_jumlah_terlambat_1_periode1.setText("30");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jScrollPane6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Panel_chart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(label_tanggal_jumlah_terlambat)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_terlamabat1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_terlamabat2, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_refresh_jumlah_terlambat)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 261, Short.MAX_VALUE)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_jumlah_terlambat_1_periode)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_jumlah_terlambat_1_periode1)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_tanggal_jumlah_terlambat, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh_jumlah_terlambat, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_terlamabat1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_terlamabat2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_jumlah_terlambat_1_periode)
                    .addComponent(jLabel16)
                    .addComponent(jLabel19)
                    .addComponent(label_jumlah_terlambat_1_periode1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6)
                    .addComponent(Panel_chart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("JUMLAH TERLAMBAT 1 PERIODE", jPanel4);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txt_search_NamaKaryawan2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_NamaKaryawan2KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_TidakMasuk();
        }
    }//GEN-LAST:event_txt_search_NamaKaryawan2KeyPressed

    private void txt_search_pin2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_pin2KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_TidakMasuk();
        }
    }//GEN-LAST:event_txt_search_pin2KeyPressed

    private void button_refresh_data_TidakMasukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh_data_TidakMasukActionPerformed
        // TODO add your handling code here:
        refreshTable_TidakMasuk();
    }//GEN-LAST:event_button_refresh_data_TidakMasukActionPerformed

    private void button_export_tdkMasukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_tdkMasukActionPerformed
        // TODO add your handling code here:
        DefaultTableModel table = (DefaultTableModel) tabel_data_TidakMasuk1.getModel();
        ExportToExcel.writeToExcel(table, jPanel3);
    }//GEN-LAST:event_button_export_tdkMasukActionPerformed

    private void button_refresh_data_TidakMasuk1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh_data_TidakMasuk1ActionPerformed
        // TODO add your handling code here:
        refreshTable_terlambat();
    }//GEN-LAST:event_button_refresh_data_TidakMasuk1ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        t.cancel();
    }//GEN-LAST:event_formWindowClosing

    private void button_refresh_jumlah_terlambatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh_jumlah_terlambatActionPerformed
        // TODO add your handling code here:
        refreshTable_jumlah_terlambat_dalam_1_periode();
    }//GEN-LAST:event_button_refresh_jumlah_terlambatActionPerformed

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
            java.util.logging.Logger.getLogger(JFrame_Tampilan_TidakAbsen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFrame_Tampilan_TidakAbsen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFrame_Tampilan_TidakAbsen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrame_Tampilan_TidakAbsen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFrame_Tampilan_TidakAbsen frame = new JFrame_Tampilan_TidakAbsen();
                frame.setVisible(true);
                frame.setLocationRelativeTo(null);
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                frame.init();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_grup;
    private javax.swing.JComboBox<String> ComboBox_grup1;
    private com.toedter.calendar.JDateChooser Date_TidakMasuk;
    private com.toedter.calendar.JDateChooser Date_terlamabat1;
    private com.toedter.calendar.JDateChooser Date_terlamabat2;
    private javax.swing.JPanel Panel_chart;
    private javax.swing.JButton button_export_tdkMasuk;
    private javax.swing.JButton button_refresh_data_TidakMasuk;
    private javax.swing.JButton button_refresh_data_TidakMasuk1;
    private javax.swing.JButton button_refresh_jumlah_terlambat;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label_jumlah_terlambat;
    private javax.swing.JLabel label_jumlah_terlambat_1_periode;
    private javax.swing.JLabel label_jumlah_terlambat_1_periode1;
    private javax.swing.JLabel label_tanggal;
    private javax.swing.JLabel label_tanggal1;
    private javax.swing.JLabel label_tanggal2;
    private javax.swing.JLabel label_tanggal_jumlah_terlambat;
    private javax.swing.JLabel label_tanggal_terlambat;
    private javax.swing.JLabel label_total_data_TidakMasuk;
    private javax.swing.JTable tabel_data_Terlambat;
    private javax.swing.JTable tabel_data_TidakMasuk1;
    private javax.swing.JTable tabel_data_TidakMasuk2;
    private javax.swing.JTable tabel_data_jumlah_Terlambat;
    private javax.swing.JTextField txt_jam1;
    private javax.swing.JTextField txt_jam2;
    private javax.swing.JTextField txt_search_NamaKaryawan2;
    private javax.swing.JTextField txt_search_pin2;
    // End of variables declaration//GEN-END:variables
}
