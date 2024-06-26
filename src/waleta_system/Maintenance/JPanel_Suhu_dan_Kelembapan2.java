package waleta_system.Maintenance;

import java.awt.BasicStroke;
import java.net.InetAddress;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.msg.ReadInputRegistersRequest;
import net.wimpi.modbus.msg.ReadInputRegistersResponse;
import net.wimpi.modbus.net.TCPMasterConnection;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;

public class JPanel_Suhu_dan_Kelembapan2 extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    DecimalFormat decimalFormat = new DecimalFormat();
    int detik = 0;

    private Timer timer;

    private static final int UNIT_ID = 1; // Sesuaikan dengan ID unit Modbus Anda
    private static final int NUMBER_OF_REGISTERS = 1; // Jumlah register yang akan dibaca
    private static final int PORT = 9090; // Port ModBus
    private static final String IP_ADDRESS = "192.168.8.8"; // Alamat IP HMI

    public JPanel_Suhu_dan_Kelembapan2() {
        initComponents();
    }

    public void init() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                decimalFormat.setMaximumFractionDigits(2);
                final int REGISTER_ADDRESS_TEMPERATURE = 300; // Alamat register untuk data suhu
                final int REGISTER_ADDRESS_HUMIDITY = 301; // Alamat register untuk data suhu

                try {
                    float suhu = getModBusValue(REGISTER_ADDRESS_TEMPERATURE);
                    float kelembapan = getModBusValue(REGISTER_ADDRESS_HUMIDITY);
                    label_suhu.setText(decimalFormat.format(suhu));
                    label_kelembapan.setText(decimalFormat.format(kelembapan));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                LocalTime currentTime = LocalTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                String formattedTime = currentTime.format(formatter);
                label_time.setText(formattedTime);

                if (detik % 60 == 0) {
                    refreshChart();
                }
                detik++;
            }
        };

        if (timer != null) {
            timer.cancel();
            detik = 0;
        }
        timer = new Timer();
        timer.schedule(timerTask, 100, 1000);
        refreshTable();
    }

    private float getModBusValue(int REGISTER_ADDRESS) throws Exception {
        float Value = 0;
        InetAddress addr = InetAddress.getByName(IP_ADDRESS);
        TCPMasterConnection connection = new TCPMasterConnection(addr);
        connection.setPort(PORT);
        connection.connect();

        ReadInputRegistersRequest request = new ReadInputRegistersRequest(REGISTER_ADDRESS, NUMBER_OF_REGISTERS);
        request.setUnitID(UNIT_ID);

        ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);
        transaction.setRequest(request);
        transaction.execute();

        ReadInputRegistersResponse response = (ReadInputRegistersResponse) transaction.getResponse();
        if (response != null) {
            Value = response.getRegisterValue(0) / 100.0f; // Konversi dari register ke suhu (misal: register value 300 = 30.0 derajat Celsius)
        }

        connection.close();
        
        return Value;
    }

    public void refreshChart() {
        try {
            System.out.println("chart refreshed!");
            // Create TimeSeries objects for suhu and kelembapan
            TimeSeries suhuSeries = new TimeSeries("Suhu", Second.class);
            TimeSeries kelembapanSeries = new TimeSeries("Kelembapan", Second.class);

            sql = "SELECT `suhu`, `kelembapan`, `created_at` "
                    + "FROM `tb_suhu_kelembapan_hmi` \n"
                    + "WHERE 1 "
                    + "ORDER BY `created_at` DESC LIMIT 40";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                double suhu = rs.getDouble("suhu");
                double kelembapan = rs.getDouble("kelembapan");
                Timestamp timestamp = rs.getTimestamp("created_at");
                Second second = new Second(timestamp);

                // Add data points to the series
                suhuSeries.add(second, suhu);
                kelembapanSeries.add(second, kelembapan);
            }

            // Create a dataset and add the series to it
            TimeSeriesCollection dataset = new TimeSeriesCollection();
            dataset.addSeries(suhuSeries);
            dataset.addSeries(kelembapanSeries);

            // Create the chart
            JFreeChart lineChart = ChartFactory.createTimeSeriesChart(
                    "Suhu dan Kelembapan",
                    "Timestamp",
                    "Value",
                    dataset,
                    true,
                    true,
                    false
            );

            // Set maximum y-axis value to 100
            XYPlot plot = lineChart.getXYPlot();
            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setRange(-10.0, 100.0);

            // Customize the renderer to make the stroke more bold
            XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
            renderer.setSeriesStroke(0, new BasicStroke(2.0f)); // Bold stroke for the first series
            renderer.setSeriesStroke(1, new BasicStroke(2.0f)); // Bold stroke for the second series
            XYToolTipGenerator tooltipGenerator = new StandardXYToolTipGenerator("{0}: {2} ({1})", new SimpleDateFormat("HH:mm"), NumberFormat.getInstance());
            renderer.setBaseToolTipGenerator(tooltipGenerator);
            plot.setRenderer(renderer);

            // Create a panel for the chart
            ChartPanel chartPanel = new ChartPanel(lineChart);
            chartPanel.setPreferredSize(new java.awt.Dimension(jPanel_chart.getWidth(), jPanel_chart.getHeight()));

            // Remove all existing components and add the new chart panel
            jPanel_chart.removeAll();
            jPanel_chart.setLayout(new java.awt.BorderLayout());
            jPanel_chart.add(chartPanel, java.awt.BorderLayout.CENTER);
            jPanel_chart.validate();
            jPanel_chart.repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Suhu_dan_Kelembapan2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_suhu_dan_kelembapan.getModel();
            model.setRowCount(0);
            String filter_tanggal = "";
            if (Date1.getDate() != null && Date2.getDate() != null) {
                filter_tanggal = "AND DATE(`created_at`) BETWEEN '" + dateFormat.format(Date1.getDate()) + "' AND '" + dateFormat.format(Date2.getDate()) + "' \n";
            }

            String jam1 = String.format("%02d", Spinner_jam1.getValue()) + ":" + String.format("%02d", Spinner_menit1.getValue()) + ":00";
            String jam2 = String.format("%02d", Spinner_jam2.getValue()) + ":" + String.format("%02d", Spinner_menit2.getValue()) + ":00";
            sql = "SELECT `no_input`, `ruang`, `suhu`, `kelembapan`, `created_at` "
                    + "FROM `tb_suhu_kelembapan_hmi` \n"
                    + "WHERE \n"
                    + "TIME(`created_at`) BETWEEN '" + jam1 + "' AND '" + jam2 + "' \n"
                    + filter_tanggal
                    + "ORDER BY `created_at` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getTimestamp("created_at");
                row[1] = rs.getFloat("suhu");
                row[2] = rs.getFloat("kelembapan");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_suhu_dan_kelembapan);
            int rowData = Table_suhu_dan_kelembapan.getRowCount();
            label_total_data.setText(Integer.toString(rowData));

            TableAlignment.setHorizontalAlignment(JLabel.CENTER);
            for (int i = 0; i < Table_suhu_dan_kelembapan.getColumnCount(); i++) {
                Table_suhu_dan_kelembapan.getColumnModel().getColumn(i).setCellRenderer(TableAlignment);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Suhu_dan_Kelembapan2.class.getName()).log(Level.SEVERE, null, ex);
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

        jPanel_SuhuKelembapan = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        Table_suhu_dan_kelembapan = new javax.swing.JTable();
        jLabel12 = new javax.swing.JLabel();
        label_total_data = new javax.swing.JLabel();
        button_search = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        Date1 = new com.toedter.calendar.JDateChooser();
        Date2 = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        Spinner_jam2 = new javax.swing.JSpinner();
        Spinner_menit2 = new javax.swing.JSpinner();
        Spinner_jam1 = new javax.swing.JSpinner();
        Spinner_menit1 = new javax.swing.JSpinner();
        jLabel5 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        label_suhu = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        label_kelembapan = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        label_time = new javax.swing.JLabel();
        jPanel_chart = new javax.swing.JPanel();

        jPanel_SuhuKelembapan.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_SuhuKelembapan.setPreferredSize(new java.awt.Dimension(1366, 701));

        Table_suhu_dan_kelembapan.setAutoCreateRowSorter(true);
        Table_suhu_dan_kelembapan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Waktu", "Suhu", "Kelembapan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Float.class, java.lang.Float.class
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
        Table_suhu_dan_kelembapan.getTableHeader().setReorderingAllowed(false);
        jScrollPane8.setViewportView(Table_suhu_dan_kelembapan);

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel12.setText("Total Data :");

        label_total_data.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_data.setText("TOTAL");

        button_search.setBackground(new java.awt.Color(255, 255, 255));
        button_search.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search.setText("Search");
        button_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_searchActionPerformed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Tanggal : ");

        Date1.setBackground(new java.awt.Color(255, 255, 255));
        Date1.setDate(new Date());
        Date1.setDateFormatString("dd MMM yyyy");
        Date1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date2.setBackground(new java.awt.Color(255, 255, 255));
        Date2.setDate(new Date());
        Date2.setDateFormatString("dd MMM yyyy");
        Date2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel4.setText("Data Suhu dan Kelembapan HMI Coolroom Ruang A");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Jam :");

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel19.setText(":");

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel20.setText(":");

        Spinner_jam2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Spinner_jam2.setModel(new javax.swing.SpinnerNumberModel(23, 0, 23, 1));
        Spinner_jam2.setEditor(new javax.swing.JSpinner.NumberEditor(Spinner_jam2, ""));

        Spinner_menit2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Spinner_menit2.setModel(new javax.swing.SpinnerNumberModel(59, 0, 59, 1));
        Spinner_menit2.setEditor(new javax.swing.JSpinner.NumberEditor(Spinner_menit2, ""));

        Spinner_jam1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Spinner_jam1.setModel(new javax.swing.SpinnerNumberModel(0, 0, 23, 1));
        Spinner_jam1.setEditor(new javax.swing.JSpinner.NumberEditor(Spinner_jam1, ""));

        Spinner_menit1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Spinner_menit1.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));
        Spinner_menit1.setEditor(new javax.swing.JSpinner.NumberEditor(Spinner_menit1, ""));

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Sampai");

        jPanel1.setBackground(new java.awt.Color(245, 245, 245));

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel13.setText("Temperature");

        label_suhu.setBackground(new java.awt.Color(255, 255, 255));
        label_suhu.setFont(new java.awt.Font("Arial", 1, 48)); // NOI18N
        label_suhu.setForeground(new java.awt.Color(0, 153, 0));
        label_suhu.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_suhu.setText("000.00");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel15.setText("°C");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(label_suhu, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel15)
                        .addGap(0, 13, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(label_suhu)
                    .addComponent(jLabel15))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(245, 245, 245));

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel14.setText("Humidity");

        label_kelembapan.setBackground(new java.awt.Color(255, 255, 255));
        label_kelembapan.setFont(new java.awt.Font("Arial", 1, 48)); // NOI18N
        label_kelembapan.setForeground(new java.awt.Color(0, 153, 0));
        label_kelembapan.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_kelembapan.setText("000.00");

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel16.setText("%");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(label_kelembapan, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel16)
                        .addGap(0, 22, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_kelembapan)
                    .addComponent(jLabel16))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBackground(new java.awt.Color(245, 245, 245));

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel23.setText("Time");

        label_time.setBackground(new java.awt.Color(255, 255, 255));
        label_time.setFont(new java.awt.Font("Arial", 1, 48)); // NOI18N
        label_time.setForeground(new java.awt.Color(0, 153, 0));
        label_time.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_time.setText("00:00:00");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label_time, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_time)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel_chart.setBackground(new java.awt.Color(245, 245, 245));

        javax.swing.GroupLayout jPanel_chartLayout = new javax.swing.GroupLayout(jPanel_chart);
        jPanel_chart.setLayout(jPanel_chartLayout);
        jPanel_chartLayout.setHorizontalGroup(
            jPanel_chartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 656, Short.MAX_VALUE)
        );
        jPanel_chartLayout.setVerticalGroup(
            jPanel_chartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel_SuhuKelembapanLayout = new javax.swing.GroupLayout(jPanel_SuhuKelembapan);
        jPanel_SuhuKelembapan.setLayout(jPanel_SuhuKelembapanLayout);
        jPanel_SuhuKelembapanLayout.setHorizontalGroup(
            jPanel_SuhuKelembapanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_SuhuKelembapanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_SuhuKelembapanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addGroup(jPanel_SuhuKelembapanLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_SuhuKelembapanLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Spinner_jam1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Spinner_menit1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Spinner_jam2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Spinner_menit2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search))
                    .addGroup(jPanel_SuhuKelembapanLayout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data))
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 684, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel_chart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel_SuhuKelembapanLayout.setVerticalGroup(
            jPanel_SuhuKelembapanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_SuhuKelembapanLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_SuhuKelembapanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_SuhuKelembapanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_SuhuKelembapanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Date2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Date1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_SuhuKelembapanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(Spinner_jam1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Spinner_menit1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Spinner_jam2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Spinner_menit2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_SuhuKelembapanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_total_data, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_SuhuKelembapanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel_chart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel_SuhuKelembapan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel_SuhuKelembapan, javax.swing.GroupLayout.DEFAULT_SIZE, 700, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_button_searchActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser Date1;
    private com.toedter.calendar.JDateChooser Date2;
    private javax.swing.JSpinner Spinner_jam1;
    private javax.swing.JSpinner Spinner_jam2;
    private javax.swing.JSpinner Spinner_menit1;
    private javax.swing.JSpinner Spinner_menit2;
    private javax.swing.JTable Table_suhu_dan_kelembapan;
    private javax.swing.JButton button_search;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel_SuhuKelembapan;
    private javax.swing.JPanel jPanel_chart;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JLabel label_kelembapan;
    private javax.swing.JLabel label_suhu;
    private javax.swing.JLabel label_time;
    private javax.swing.JLabel label_total_data;
    // End of variables declaration//GEN-END:variables
}
