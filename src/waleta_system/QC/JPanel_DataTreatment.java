package waleta_system.QC;

import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
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
import waleta_system.Class.Utility;
import waleta_system.Class.ExportToExcel;

public class JPanel_DataTreatment extends javax.swing.JPanel {

    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    String sql = null;
    ResultSet rs;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_DataTreatment() {
        initComponents();
    }

    public void init() {
        refreshTable_Treatment();
    }

    public void refreshTable_Treatment() {
        try {
            int total_passed = 0, total_hold = 0;
            double total_perubahan = 0, total_perubahan_persen = 0;
            double rata2_perubahan = 0, rata2_perubahan_persen = 0;
            double total_nitrit_awal = 0, total_nitrit_akhir = 0;
            double rata2_nitrit_awal = 0, rata2_nitrit_akhir = 0;
            decimalFormat.setMaximumFractionDigits(2);
            DefaultTableModel model = (DefaultTableModel) Table_data_treatment.getModel();
            model.setRowCount(0);
            String status = ComboBox_status_uji.getSelectedItem().toString();
            if (ComboBox_status_uji.getSelectedItem() == "All") {
                status = "";
            }
            String status_print = "";
            if (ComboBox_status_print.getSelectedIndex() == 1) {
                status_print = " AND `print_label` = 1 ";
            } else if (ComboBox_status_print.getSelectedIndex() == 2) {
                status_print = " AND `print_label` = 0 ";
            }

            String filter_tanggal = "";
            if (Date_1.getDate() != null && Date_2.getDate() != null) {
                filter_tanggal = "AND (`tgl_treatment` BETWEEN '" + dateFormat.format(Date_1.getDate()) + "' AND '" + dateFormat.format(Date_2.getDate()) + "')";
            }

            sql = "SELECT * FROM `tb_lab_treatment_lp` "
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_lab_treatment_lp`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`"
                    + "WHERE `tb_lab_treatment_lp`.`no_laporan_produksi` LIKE '%" + txt_search_Lab_LP.getText() + "%' "
                    + "AND `status` LIKE '%" + status + "%' " + filter_tanggal + status_print;
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[15];
            while (rs.next()) {
                row[0] = rs.getString("kode_treatment");
                row[1] = rs.getString("no_laporan_produksi");
                row[2] = rs.getString("kode_grade");
                row[3] = rs.getString("jenis_barang");
                row[4] = rs.getDate("tgl_treatment");
                row[5] = rs.getInt("waktu_treatment");
                row[6] = rs.getDouble("nitrit_awal");
                total_nitrit_awal = total_nitrit_awal + rs.getDouble("nitrit_awal");
                row[7] = rs.getDouble("nitrit_akhir");
                total_nitrit_akhir = total_nitrit_akhir + rs.getDouble("nitrit_akhir");
                row[8] = rs.getString("status");
                if (rs.getString("status").equals("PASSED")) {
                    total_passed++;
                } else if (rs.getString("status").equals("HOLD/NON GNS")) {
                    total_hold++;
                }
                double perubahan_nitrit = rs.getDouble("nitrit_awal") - rs.getDouble("nitrit_akhir");
                double perubahan_nitrit_persen = (perubahan_nitrit / rs.getDouble("nitrit_awal")) * 100;
                total_perubahan = total_perubahan + perubahan_nitrit;
                total_perubahan_persen = total_perubahan_persen + perubahan_nitrit_persen;
                row[9] = perubahan_nitrit;
                row[10] = Math.round(perubahan_nitrit_persen);
                row[11] = rs.getBoolean("print_label");

                model.addRow(row);
            }
            rata2_perubahan = total_perubahan / Table_data_treatment.getRowCount();
            rata2_perubahan_persen = total_perubahan_persen / Table_data_treatment.getRowCount();
            rata2_nitrit_awal = total_nitrit_awal / Table_data_treatment.getRowCount();
            rata2_nitrit_akhir = total_nitrit_akhir / Table_data_treatment.getRowCount();

            label_total_data.setText(Integer.toString(Table_data_treatment.getRowCount()));
            label_total_passed.setText(Integer.toString(total_passed));
            label_total_hold.setText(Integer.toString(total_hold));
            label_rata2_nitritAwal.setText(decimalFormat.format(rata2_nitrit_awal));
            label_rata2_nitritAkhir.setText(decimalFormat.format(rata2_nitrit_akhir));
            label_rata2_perubahanNitrit.setText(decimalFormat.format(rata2_perubahan));
            label_rata2_perubahanNitrit_Persen.setText(decimalFormat.format(rata2_perubahan_persen));
            ColumnsAutoSizer.sizeColumnsToFit(Table_data_treatment);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataTreatment.class.getName()).log(Level.SEVERE, null, ex);
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
        Table_data_treatment = new javax.swing.JTable();
        button_export_dataTreatment = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        label_total_data = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txt_search_Lab_LP = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        Date_1 = new com.toedter.calendar.JDateChooser();
        Date_2 = new com.toedter.calendar.JDateChooser();
        button_Refresh = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        label_total_passed = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        label_total_hold = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        ComboBox_status_uji = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        label_rata2_nitritAwal = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        label_rata2_nitritAkhir = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        label_rata2_perubahanNitrit = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        label_rata2_perubahanNitrit_Persen = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        button_print_catatan_abnormal_produk = new javax.swing.JButton();
        button_print_labelQC = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        ComboBox_status_print = new javax.swing.JComboBox<>();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Treatment", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        Table_data_treatment.setAutoCreateRowSorter(true);
        Table_data_treatment.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_data_treatment.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "No. LP", "Grade", "Jenis Barang", "Tanggal Treatment", "Waktu (Jam)", "Nitrit Awal", "Nitrit Akhir", "Status", "Perubahan Nitrit", "Perubahan (%)", "Printed"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.Float.class, java.lang.Double.class, java.lang.Boolean.class
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
        Table_data_treatment.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Table_data_treatment);

        button_export_dataTreatment.setBackground(new java.awt.Color(255, 255, 255));
        button_export_dataTreatment.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_dataTreatment.setText("Export to Excel");
        button_export_dataTreatment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_dataTreatmentActionPerformed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("Total Data :");

        label_total_data.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_data.setText("0");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("No. LP :");

        txt_search_Lab_LP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_Lab_LP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_Lab_LPKeyPressed(evt);
            }
        });

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Tanggal Treatment :");

        Date_1.setBackground(new java.awt.Color(255, 255, 255));
        Date_1.setDate(new Date());
        Date_1.setDateFormatString("dd MMM yyyy");
        Date_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_2.setBackground(new java.awt.Color(255, 255, 255));
        Date_2.setDate(new Date());
        Date_2.setDateFormatString("dd MMM yyyy");
        Date_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_Refresh.setBackground(new java.awt.Color(255, 255, 255));
        button_Refresh.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_Refresh.setText("Refresh");
        button_Refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_RefreshActionPerformed(evt);
            }
        });

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("Total Passed :");

        label_total_passed.setBackground(new java.awt.Color(255, 255, 255));
        label_total_passed.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_passed.setText("0");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("Total Hold :");

        label_total_hold.setBackground(new java.awt.Color(255, 255, 255));
        label_total_hold.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_hold.setText("0");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel15.setText("Status :");

        ComboBox_status_uji.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_status_uji.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "PASSED", "HOLD/NON GNS" }));

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("Rata-rata Nitrit Awal :");

        label_rata2_nitritAwal.setBackground(new java.awt.Color(255, 255, 255));
        label_rata2_nitritAwal.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_rata2_nitritAwal.setText("0");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Rata-rata Nitrit Akhir :");

        label_rata2_nitritAkhir.setBackground(new java.awt.Color(255, 255, 255));
        label_rata2_nitritAkhir.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_rata2_nitritAkhir.setText("0");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Rata-rata Perubahan Nitrit :");

        label_rata2_perubahanNitrit.setBackground(new java.awt.Color(255, 255, 255));
        label_rata2_perubahanNitrit.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_rata2_perubahanNitrit.setText("0");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("ppm");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel10.setText("ppm");

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel12.setText("ppm");

        label_rata2_perubahanNitrit_Persen.setBackground(new java.awt.Color(255, 255, 255));
        label_rata2_perubahanNitrit_Persen.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_rata2_perubahanNitrit_Persen.setText("0");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel13.setText("%");

        button_print_catatan_abnormal_produk.setBackground(new java.awt.Color(255, 255, 255));
        button_print_catatan_abnormal_produk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_print_catatan_abnormal_produk.setText("Catatan Abnormal Produk");
        button_print_catatan_abnormal_produk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_print_catatan_abnormal_produkActionPerformed(evt);
            }
        });

        button_print_labelQC.setBackground(new java.awt.Color(255, 255, 255));
        button_print_labelQC.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_print_labelQC.setText("Print Label QC");
        button_print_labelQC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_print_labelQCActionPerformed(evt);
            }
        });

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel21.setText("Print :");

        ComboBox_status_print.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_status_print.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Sudah Print", "Belum Print" }));
        ComboBox_status_print.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ComboBox_status_printItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_Lab_LP, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_status_uji, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_status_print, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_Refresh)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 103, Short.MAX_VALUE)
                        .addComponent(button_print_labelQC)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_print_catatan_abnormal_produk)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export_dataTreatment))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_data, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_passed, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_rata2_nitritAkhir, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_rata2_nitritAwal, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel10)
                                    .addComponent(jLabel9)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_hold, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_rata2_perubahanNitrit, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_rata2_perubahanNitrit_Persen, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel13)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_export_dataTreatment, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ComboBox_status_uji, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_print_catatan_abnormal_produk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_print_labelQC, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_Lab_LP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Date_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Date_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ComboBox_status_print, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_Refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 565, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(label_total_data))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(label_total_passed)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(label_rata2_nitritAwal))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(label_rata2_nitritAkhir)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_rata2_perubahanNitrit_Persen)
                        .addComponent(jLabel13))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(label_total_hold)
                        .addComponent(jLabel7)
                        .addComponent(label_rata2_perubahanNitrit)
                        .addComponent(jLabel12)))
                .addContainerGap())
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

    private void button_export_dataTreatmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_dataTreatmentActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_data_treatment.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_export_dataTreatmentActionPerformed

    private void txt_search_Lab_LPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_Lab_LPKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_Treatment();
        }
    }//GEN-LAST:event_txt_search_Lab_LPKeyPressed

    private void button_RefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_RefreshActionPerformed
        // TODO add your handling code here:
        refreshTable_Treatment();
    }//GEN-LAST:event_button_RefreshActionPerformed

//    public Model_Data_Treatment produce(java.sql.Date tglTreatment, String noLaporanProduksi, String jenisBarang, int waktuTreatment, float nitritAwal, float nitritAkhir) {
//        Model_Data_Treatment model = new Model_Data_Treatment();
//        model.setTglTreatment(tglTreatment);
//        model.setNoLaporanProduksi(noLaporanProduksi);
//        model.setJenisBarang(jenisBarang);
//        model.setWaktuTreatment(waktuTreatment);
//        model.setNitritAwal(nitritAwal);
//        model.setNitritAkhir(nitritAkhir);
//        return model;
//    }

    private void button_print_catatan_abnormal_produkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_print_catatan_abnormal_produkActionPerformed
        try {
            if (Date_1.getDate() == null || Date_2.getDate() == null) {
                /*sql = "SELECT `tgl_treatment`, `tb_lab_treatment_lp`.`no_laporan_produksi`, `jenis_barang`, `waktu_treatment`, `nitrit_awal`, MIN(`nitrit_akhir`) AS `nitrit_akhir`, `status` "
                        + "FROM `tb_lab_treatment_lp` LEFT JOIN `tb_laporan_produksi` ON `tb_lab_treatment_lp`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi` "
                        + "WHERE NOT (`status`='HOLD/NON GNS' AND COUNT(`nitrit_akhir`)<3) "
                        + "GROUP BY `tb_lab_treatment_lp`.`no_laporan_produksi`, `jenis_barang` "
                        + "ORDER BY `tb_lab_treatment_lp`.`no_laporan_produksi` ASC";*/
                sql = "SELECT `tgl_treatment`, `tb_lab_treatment_lp`.`no_laporan_produksi`, `cheat_no_kartu`, `cheat_rsb`, `tb_lab_treatment_lp`.`jenis_barang`, `waktu_treatment`, `nitrit_awal`, `tb_lab_treatment_lp`.`nitrit_akhir`, `status` "
                        + "FROM `tb_lab_treatment_lp` "
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_lab_treatment_lp`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi` "
                        + "LEFT JOIN (SELECT `no_laporan_produksi`, MIN(`nitrit_akhir`) AS `nitrit_akhir`, COUNT(`no_laporan_produksi`) AS `jumlah_laporan`, `jenis_barang` "
                        + "FROM `tb_lab_treatment_lp` GROUP BY `no_laporan_produksi`, `jenis_barang`) AS `a` ON `tb_lab_treatment_lp`.`no_laporan_produksi` = `a`.`no_laporan_produksi` AND `tb_lab_treatment_lp`.`jenis_barang` = `a`.`jenis_barang` AND `tb_lab_treatment_lp`.`nitrit_akhir` = `a`.`nitrit_akhir` "
                        + "WHERE NOT (`status`='HOLD/NON GNS' AND `a`.`jumlah_laporan`<3) "
                        + "ORDER BY `tb_lab_treatment_lp`.`no_laporan_produksi` ASC";
            } else {
                /*sql = "SELECT `tgl_treatment`, `tb_lab_treatment_lp`.`no_laporan_produksi`, `jenis_barang`, `waktu_treatment`, `nitrit_awal`, MIN(`nitrit_akhir`) AS `nitrit_akhir`, `status` "
                        + "FROM `tb_lab_treatment_lp` LEFT JOIN `tb_laporan_produksi` ON `tb_lab_treatment_lp`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi` "
                        + "WHERE NOT (`status`='HOLD/NON GNS' AND COUNT(`nitrit_akhir`)<3) "
                        + "AND `tgl_treatment` BETWEEN '" + dateFormat.format(Date_1.getDate()) + "' AND '" + dateFormat.format(Date_2.getDate()) + "' "
                        + "GROUP BY `tb_lab_treatment_lp`.`no_laporan_produksi`, `jenis_barang` "
                        + "ORDER BY `tb_lab_treatment_lp`.`no_laporan_produksi` ASC";*/
                sql = "SELECT `tgl_treatment`, `tb_lab_treatment_lp`.`no_laporan_produksi`, `cheat_no_kartu`, `cheat_rsb`, `tb_lab_treatment_lp`.`jenis_barang`, `waktu_treatment`, `nitrit_awal`, `tb_lab_treatment_lp`.`nitrit_akhir`, `status` "
                        + "FROM `tb_lab_treatment_lp` "
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_lab_treatment_lp`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi` "
                        + "LEFT JOIN (SELECT `no_laporan_produksi`, MIN(`nitrit_akhir`) AS `nitrit_akhir`, COUNT(`no_laporan_produksi`) AS `jumlah_laporan`, `jenis_barang` "
                        + "FROM `tb_lab_treatment_lp` GROUP BY `no_laporan_produksi`, `jenis_barang`) AS `a` ON `tb_lab_treatment_lp`.`no_laporan_produksi` = `a`.`no_laporan_produksi` AND `tb_lab_treatment_lp`.`jenis_barang` = `a`.`jenis_barang` AND `tb_lab_treatment_lp`.`nitrit_akhir` = `a`.`nitrit_akhir` "
                        + "WHERE NOT (`status`='HOLD/NON GNS' AND `a`.`jumlah_laporan`<3) "
                        + "AND `tgl_treatment` BETWEEN '" + dateFormat.format(Date_1.getDate()) + "' AND '" + dateFormat.format(Date_2.getDate()) + "' "
                        + "ORDER BY `tb_lab_treatment_lp`.`no_laporan_produksi` ASC";
            }
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(sql);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Catatan_Abnormal_Produk.jrxml");
            JASP_DESIGN.setQuery(newQuery);
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, null, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);
        } catch (JRException ex) {
            Logger.getLogger(JPanel_DataTreatment.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_print_catatan_abnormal_produkActionPerformed

    private void button_print_labelQCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_print_labelQCActionPerformed
        // TODO add your handling code here:
        try {
            String kode_treatment = "";
            for (int i = 0; i < Table_data_treatment.getRowCount(); i++) {
                if (i != 0) {
                    kode_treatment = kode_treatment + ", ";
                }
                kode_treatment = kode_treatment + "'" + Table_data_treatment.getValueAt(i, 0).toString() + "'";
            }
            String Query = "SELECT CONCAT(A.`no_laporan_produksi`, IF(`cheat_rsb` IS NULL, CONCAT('-', `no_registrasi`), CONCAT('-', `cheat_rsb`))) AS 'no_lp_rsb', A.`no_laporan_produksi`, `tgl_treatment` AS 'tanggal', A.`status`, "
                    + "CONCAT(' ', `jenis_barang`, '(T):', `nitrit_akhir`) AS 'nitrit', `tb_lab_laporan_produksi`.`kadar_aluminium` \n"
                    + "FROM `tb_lab_treatment_lp` A "
                    + "LEFT JOIN `tb_laporan_produksi` ON A.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi` "
                    + "LEFT JOIN `tb_lab_laporan_produksi` ON A.`no_laporan_produksi` = `tb_lab_laporan_produksi`.`no_laporan_produksi` "
                    + "LEFT JOIN `tb_bahan_baku_masuk_cheat` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta`"
                    + "WHERE A.`kode_treatment` IN (" + kode_treatment + ") ";
//                    + "AND `print_label` = 0";
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(Query);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Label_QC_Passed.jrxml");
            JASP_DESIGN.setQuery(newQuery);
            Map<String, Object> map = new HashMap<>();
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)

            String Query_update = "UPDATE `tb_lab_treatment_lp` SET `print_label`=1 WHERE `print_label` = 0 AND `kode_treatment` IN (" + kode_treatment + ")";
            Utility.db.getStatement().executeUpdate(Query_update);
            refreshTable_Treatment();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataTreatment.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_print_labelQCActionPerformed

    private void ComboBox_status_printItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ComboBox_status_printItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_ComboBox_status_printItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_status_print;
    private javax.swing.JComboBox<String> ComboBox_status_uji;
    private com.toedter.calendar.JDateChooser Date_1;
    private com.toedter.calendar.JDateChooser Date_2;
    private javax.swing.JTable Table_data_treatment;
    public static javax.swing.JButton button_Refresh;
    public static javax.swing.JButton button_export_dataTreatment;
    public static javax.swing.JButton button_print_catatan_abnormal_produk;
    public static javax.swing.JButton button_print_labelQC;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_rata2_nitritAkhir;
    private javax.swing.JLabel label_rata2_nitritAwal;
    private javax.swing.JLabel label_rata2_perubahanNitrit;
    private javax.swing.JLabel label_rata2_perubahanNitrit_Persen;
    private javax.swing.JLabel label_total_data;
    private javax.swing.JLabel label_total_hold;
    private javax.swing.JLabel label_total_passed;
    private javax.swing.JTextField txt_search_Lab_LP;
    // End of variables declaration//GEN-END:variables
}
