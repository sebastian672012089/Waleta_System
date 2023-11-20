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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
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

public class JPanel_Lab_BarangJadi extends javax.swing.JPanel {

    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    String sql = null;
    ResultSet rs;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_Lab_BarangJadi() {
        initComponents();
    }

    public void init() {
        refreshTable();
        Table_data_treatment.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && Table_data_treatment.getSelectedRow() != -1) {
                    int i = Table_data_treatment.getSelectedRow();
                    if (Table_data_treatment.getValueAt(i, 7) == null) {
                        button_setor.setEnabled(true);
                        button_edit.setEnabled(false);
                    } else {
                        button_setor.setEnabled(false);
                        button_edit.setEnabled(true);
                    }
                }
            }
        });
    }

    public void refreshTable() {
        try {
            float total_nitrit_awal = 0, total_nitrit_akhir = 0;
            float rata2_nitrit_awal = 0, rata2_nitrit_akhir = 0;
            float jumlah_data_nitrit_akhir = 0;
            decimalFormat.setMaximumFractionDigits(2);
            DefaultTableModel model = (DefaultTableModel) Table_data_treatment.getModel();
            model.setRowCount(0);

            String filter_tanggal = "";
            if (Date_1.getDate() != null && Date_2.getDate() != null) {
                if (ComboBox_filter_tanggal.getSelectedIndex() == 0) {
                    filter_tanggal = "AND (`tgl_masuk` BETWEEN '" + dateFormat.format(Date_1.getDate()) + "' AND '" + dateFormat.format(Date_2.getDate()) + "')";
                } else if (ComboBox_filter_tanggal.getSelectedIndex() == 1) {
                    filter_tanggal = "AND (`tgl_selesai` BETWEEN '" + dateFormat.format(Date_1.getDate()) + "' AND '" + dateFormat.format(Date_2.getDate()) + "')";
                }
            }

            String status_print = "";
            if (ComboBox_status_print.getSelectedIndex() == 1) {
                status_print = " AND `print_label` = 1 ";
            } else if (ComboBox_status_print.getSelectedIndex() == 2) {
                status_print = " AND `print_label` = 0 ";
            }

            sql = "SELECT `tb_lab_barang_jadi`.`kode`, `tb_lab_barang_jadi`.`no_box`, `tb_grade_bahan_jadi`.`kode_grade`, `tb_box_bahan_jadi`.`keping`, `tb_box_bahan_jadi`.`berat`, `tgl_masuk`, `nitrit_awal`, `tgl_selesai`, `nitrit_akhir`, `kpg_akhir`, `gram_akhir`, `print_label` \n"
                    + "FROM `tb_lab_barang_jadi` "
                    + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_lab_barang_jadi`.`no_box` = `tb_box_bahan_jadi`.`no_box`\n"
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`"
                    + "WHERE `tb_lab_barang_jadi`.`no_box` LIKE '%" + txt_search_no_box.getText() + "%' "
                    + "AND `tb_grade_bahan_jadi`.`kode_grade` LIKE '%" + txt_search_grade.getText() + "%' "
                    + filter_tanggal + status_print;
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[15];
            while (rs.next()) {
                row[0] = rs.getInt("kode");
                row[1] = rs.getString("no_box");
                row[2] = rs.getString("kode_grade");
                row[3] = rs.getDate("tgl_masuk");
                row[4] = rs.getInt("keping");
                row[5] = rs.getFloat("berat");
                row[6] = rs.getFloat("nitrit_awal");
                row[7] = rs.getDate("tgl_selesai");
                row[8] = rs.getFloat("nitrit_akhir");
                row[9] = rs.getInt("kpg_akhir");
                row[10] = rs.getFloat("gram_akhir");
                total_nitrit_awal = total_nitrit_awal + rs.getFloat("nitrit_awal");
                if (rs.getFloat("nitrit_akhir") > 0) {
                    total_nitrit_akhir = total_nitrit_akhir + rs.getFloat("nitrit_akhir");
                    jumlah_data_nitrit_akhir++;
                }
                if (rs.getFloat("nitrit_akhir") > Float.valueOf(txt_max_nitrit.getText())) {
                    row[11] = "NON NS";
                } else {
                    row[11] = "PASSED";
                }
                row[12] = rs.getBoolean("print_label");
                model.addRow(row);
            }
            rata2_nitrit_awal = total_nitrit_awal / Table_data_treatment.getRowCount();
            rata2_nitrit_akhir = total_nitrit_akhir / jumlah_data_nitrit_akhir;

            label_total_data.setText(Integer.toString(Table_data_treatment.getRowCount()));
            label_rata2_nitritAwal.setText(decimalFormat.format(rata2_nitrit_awal));
            label_rata2_nitritAkhir.setText(decimalFormat.format(rata2_nitrit_akhir));
            ColumnsAutoSizer.sizeColumnsToFit(Table_data_treatment);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Lab_BarangJadi.class.getName()).log(Level.SEVERE, null, ex);
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
        txt_search_no_box = new javax.swing.JTextField();
        Date_1 = new com.toedter.calendar.JDateChooser();
        Date_2 = new com.toedter.calendar.JDateChooser();
        button_Refresh = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        label_rata2_nitritAwal = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        label_rata2_nitritAkhir = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        ComboBox_filter_tanggal = new javax.swing.JComboBox<>();
        button_setor = new javax.swing.JButton();
        button_input_nitrit = new javax.swing.JButton();
        button_edit = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        txt_search_grade = new javax.swing.JTextField();
        button_print_label_passed = new javax.swing.JButton();
        button_print_label_passed1 = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        txt_max_nitrit = new javax.swing.JTextField();
        button_input_nitrit_akhir = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        ComboBox_status_print = new javax.swing.JComboBox<>();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data QC Barang Jadi", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        Table_data_treatment.setAutoCreateRowSorter(true);
        Table_data_treatment.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_data_treatment.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "No Box", "Grade", "Tgl Masuk", "Kpg Awal", "Gram Awal", "Nitrit Awal", "Tgl Selesai", "Nitrit Akhir", "Keping Akhir", "Gram Akhir", "Status", "Print Label"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class, java.lang.Object.class, java.lang.Float.class, java.lang.Integer.class, java.lang.Float.class, java.lang.String.class, java.lang.Boolean.class
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
        jLabel11.setText("No Box :");

        txt_search_no_box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_no_box.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_no_boxKeyPressed(evt);
            }
        });

        Date_1.setBackground(new java.awt.Color(255, 255, 255));
        Date_1.setDateFormatString("dd MMMM yyyy");
        Date_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_2.setBackground(new java.awt.Color(255, 255, 255));
        Date_2.setDateFormatString("dd MMMM yyyy");
        Date_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_Refresh.setBackground(new java.awt.Color(255, 255, 255));
        button_Refresh.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_Refresh.setText("Refresh");
        button_Refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_RefreshActionPerformed(evt);
            }
        });

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

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("ppm");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel10.setText("ppm");

        ComboBox_filter_tanggal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_filter_tanggal.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tanggal Masuk", "Tanggal Selesai" }));

        button_setor.setBackground(new java.awt.Color(255, 255, 255));
        button_setor.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_setor.setText("Setor ke GBJ");
        button_setor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_setorActionPerformed(evt);
            }
        });

        button_input_nitrit.setBackground(new java.awt.Color(255, 255, 255));
        button_input_nitrit.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_input_nitrit.setText("Input Nitrit Awal");
        button_input_nitrit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_nitritActionPerformed(evt);
            }
        });

        button_edit.setBackground(new java.awt.Color(255, 255, 255));
        button_edit.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_edit.setText("Edit");
        button_edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_editActionPerformed(evt);
            }
        });

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("Grade :");

        txt_search_grade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_grade.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_gradeKeyPressed(evt);
            }
        });

        button_print_label_passed.setBackground(new java.awt.Color(255, 255, 255));
        button_print_label_passed.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_print_label_passed.setText("Print Semua Label QC");
        button_print_label_passed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_print_label_passedActionPerformed(evt);
            }
        });

        button_print_label_passed1.setBackground(new java.awt.Color(255, 255, 255));
        button_print_label_passed1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_print_label_passed1.setText("Print 1 Label QC");
        button_print_label_passed1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_print_label_passed1ActionPerformed(evt);
            }
        });

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Max Nitrit :");

        txt_max_nitrit.setEditable(false);
        txt_max_nitrit.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_max_nitrit.setText("21");

        button_input_nitrit_akhir.setBackground(new java.awt.Color(255, 255, 255));
        button_input_nitrit_akhir.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_input_nitrit_akhir.setText("Input Nitrit Akhir");
        button_input_nitrit_akhir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_nitrit_akhirActionPerformed(evt);
            }
        });

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel21.setText("Status print :");

        ComboBox_status_print.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_status_print.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Sudah Print", "Belum Print" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1336, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_no_box, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_filter_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_status_print, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_Refresh)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_export_dataTreatment))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel6)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_rata2_nitritAkhir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_rata2_nitritAwal)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel10)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(button_setor)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_input_nitrit)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_input_nitrit_akhir)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_edit)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_print_label_passed)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_print_label_passed1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_max_nitrit, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(button_export_dataTreatment, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_no_box, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Date_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Date_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ComboBox_filter_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ComboBox_status_print, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(button_Refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_setor, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_input_nitrit, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_edit, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_print_label_passed, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_print_label_passed1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_input_nitrit_akhir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_max_nitrit, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 539, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(label_total_data))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(label_rata2_nitritAwal)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(label_rata2_nitritAkhir)
                    .addComponent(jLabel10))
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

    private void txt_search_no_boxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_no_boxKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_no_boxKeyPressed

    private void button_RefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_RefreshActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_button_RefreshActionPerformed

    private void button_setorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_setorActionPerformed
        // TODO add your handling code here:
        int i = Table_data_treatment.getSelectedRow();
        if (i > -1) {
            String kode = Table_data_treatment.getValueAt(i, 0).toString();
            String no_box = Table_data_treatment.getValueAt(i, 1).toString();
            String kpg1 = Table_data_treatment.getValueAt(i, 4).toString();
            String gram1 = Table_data_treatment.getValueAt(i, 5).toString();
            String nitrit1 = Table_data_treatment.getValueAt(i, 6).toString();
            JDialog_EditSetor_boxTreatment dialog = new JDialog_EditSetor_boxTreatment(new javax.swing.JFrame(), true, kode, no_box, "", nitrit1, "", kpg1, gram1, "setor");
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            dialog.setEnabled(true);
            dialog.setResizable(false);
            refreshTable();
        }
    }//GEN-LAST:event_button_setorActionPerformed

    private void button_input_nitritActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_nitritActionPerformed
        // TODO add your handling code here:
        decimalFormat.setMaximumFractionDigits(2);
        decimalFormat.setGroupingUsed(false);
        int i = Table_data_treatment.getSelectedRow();
        if (i == -1) {
            JOptionPane.showMessageDialog(this, "Belum memilih data");
        } else {
            String kode = Table_data_treatment.getValueAt(i, 0).toString();
            if ((float) Table_data_treatment.getValueAt(i, 6) > 0) {
                JOptionPane.showMessageDialog(this, "nitrit awal sudah di input, input ulang untuk melakukan edit data");
            }
            try {
                String input = JOptionPane.showInputDialog("Masukkan Nitrit Awal : ");
                if (input != null && !input.equals("")) {
                    float NITRIT_A = Float.valueOf(input);
                    decimalFormat = Utility.DecimalFormatUS(decimalFormat);
                    sql = "UPDATE `tb_lab_barang_jadi` SET `nitrit_awal`='" + decimalFormat.format(NITRIT_A) + "' WHERE `kode`='" + kode + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                        refreshTable();
                    } else {
                        JOptionPane.showMessageDialog(this, "Input failed!");
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex);
                Logger.getLogger(JPanel_Lab_BarangJadi.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Input must be number!");
                Logger.getLogger(JPanel_Lab_BarangJadi.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_input_nitritActionPerformed

    private void button_editActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_editActionPerformed
        // TODO add your handling code here:
        int i = Table_data_treatment.getSelectedRow();
        if (i > -1) {
            String kode = Table_data_treatment.getValueAt(i, 0).toString();
            String no_box = Table_data_treatment.getValueAt(i, 1).toString();
            String nitrit1 = Table_data_treatment.getValueAt(i, 6).toString();
            String tgl_selesai = Table_data_treatment.getValueAt(i, 7).toString();
            String nitrit2 = Table_data_treatment.getValueAt(i, 8).toString();
            String kpg2 = Table_data_treatment.getValueAt(i, 9).toString();
            String gram2 = Table_data_treatment.getValueAt(i, 10).toString();
            try {
                sql = "SELECT `no_box`, `lokasi_terakhir` FROM `tb_box_bahan_jadi` WHERE `no_box` = '" + no_box + "' ";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    if (rs.getString("lokasi_terakhir").equals("GRADING")) {
                        JDialog_EditSetor_boxTreatment dialog = new JDialog_EditSetor_boxTreatment(new javax.swing.JFrame(), true, kode, no_box, tgl_selesai, nitrit1, nitrit2, kpg2, gram2, "edit");
                        dialog.pack();
                        dialog.setLocationRelativeTo(this);
                        dialog.setVisible(true);
                        dialog.setEnabled(true);
                        dialog.setResizable(false);
                        refreshTable();
                    } else {
                        JOptionPane.showMessageDialog(this, "Maaf lokasi Box di " + rs.getString("lokasi_terakhir") + ", Tidak bisa melakukan edit");
                    }
                }
            } catch (SQLException e) {
                Logger.getLogger(JPanel_Lab_BarangJadi.class.getName()).log(Level.SEVERE, null, e);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih salah satu data dulu");
        }
    }//GEN-LAST:event_button_editActionPerformed

    private void txt_search_gradeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_gradeKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_gradeKeyPressed

    private void button_print_label_passedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_print_label_passedActionPerformed
        // TODO add your handling code here:
        try {
            String kode = "";
            for (int i = 0; i < Table_data_treatment.getRowCount(); i++) {
                if (i != 0) {
                    kode = kode + ", ";
                }
                kode = kode + "'" + Table_data_treatment.getValueAt(i, 0).toString() + "'";
            }
            String Query = "SELECT IF(`kode_rsb` IS NULL, `tb_lab_barang_jadi`.`no_box`, CONCAT(`tb_lab_barang_jadi`.`no_box`, '-', `kode_rsb`)) AS 'no_box_rsb', `tb_lab_barang_jadi`.`no_box`, `tb_grade_bahan_jadi`.`kode_grade`, `tgl_selesai`, IF(`tgl_selesai` IS NULL, `nitrit_awal`, `nitrit_akhir`) AS 'nitrit_akhir', "
                    + "IF(IF(`tgl_selesai` IS NULL, `nitrit_awal`, `nitrit_akhir`) > " + txt_max_nitrit.getText() + ", 'HOLD/NON GNS', 'PASSED') AS `status` \n"
                    + "FROM `tb_lab_barang_jadi` \n"
                    + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_lab_barang_jadi`.`no_box` = `tb_box_bahan_jadi`.`no_box` "
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode` "
                    + "WHERE `tb_lab_barang_jadi`.`kode` IN (" + kode + ")";
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(Query);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Label_QC_Passed_BoxBJ.jrxml");
            JASP_DESIGN.setQuery(newQuery);
            Map<String, Object> map = new HashMap<>();
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)

            String Query_update = "UPDATE `tb_lab_barang_jadi` SET `print_label`=1 WHERE `print_label` = 0 AND `tb_lab_barang_jadi`.`kode` IN (" + kode + ")";
            Utility.db.getStatement().executeUpdate(Query_update);
            refreshTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Lab_LaporanProduksi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_print_label_passedActionPerformed

    private void button_print_label_passed1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_print_label_passed1ActionPerformed
        // TODO add your handling code here:
        int i = Table_data_treatment.getSelectedRow();
        if (i > -1) {
            try {
                String kode = "'" + Table_data_treatment.getValueAt(i, 0).toString() + "'";
                String Query = "SELECT IF(`kode_rsb` IS NULL, `tb_lab_barang_jadi`.`no_box`, CONCAT(`tb_lab_barang_jadi`.`no_box`, '-', `kode_rsb`)) AS 'no_box_rsb', `tb_lab_barang_jadi`.`no_box`, `tb_grade_bahan_jadi`.`kode_grade`, `tgl_selesai`, IF(`tgl_selesai` IS NULL, `nitrit_awal`, `nitrit_akhir`) AS 'nitrit_akhir', "
                        + "IF(IF(`tgl_selesai` IS NULL, `nitrit_awal`, `nitrit_akhir`) > " + txt_max_nitrit.getText() + ", 'HOLD/NON GNS', 'PASSED') AS `status` \n"
                        + "FROM `tb_lab_barang_jadi` \n"
                        + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_lab_barang_jadi`.`no_box` = `tb_box_bahan_jadi`.`no_box` "
                        + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode` "
                        + "WHERE `tb_lab_barang_jadi`.`kode` IN (" + kode + ")";
                JRDesignQuery newQuery = new JRDesignQuery();
                newQuery.setText(Query);
                JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Label_QC_Passed_BoxBJ.jrxml");
                JASP_DESIGN.setQuery(newQuery);
                Map<String, Object> map = new HashMap<>();
                JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
                JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)

                String Query_update = "UPDATE `tb_lab_barang_jadi` SET `print_label`=1 WHERE `print_label` = 0 AND `tb_lab_barang_jadi`.`kode` IN (" + kode + ")";
                Utility.db.getStatement().executeUpdate(Query_update);
                refreshTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex);
                Logger.getLogger(JPanel_Lab_LaporanProduksi.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih salah satu yang mau di print");
        }
    }//GEN-LAST:event_button_print_label_passed1ActionPerformed

    private void button_input_nitrit_akhirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_nitrit_akhirActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_button_input_nitrit_akhirActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_filter_tanggal;
    private javax.swing.JComboBox<String> ComboBox_status_print;
    private com.toedter.calendar.JDateChooser Date_1;
    private com.toedter.calendar.JDateChooser Date_2;
    private javax.swing.JTable Table_data_treatment;
    public static javax.swing.JButton button_Refresh;
    public static javax.swing.JButton button_edit;
    public static javax.swing.JButton button_export_dataTreatment;
    public static javax.swing.JButton button_input_nitrit;
    public static javax.swing.JButton button_input_nitrit_akhir;
    public static javax.swing.JButton button_print_label_passed;
    public static javax.swing.JButton button_print_label_passed1;
    public static javax.swing.JButton button_setor;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_rata2_nitritAkhir;
    private javax.swing.JLabel label_rata2_nitritAwal;
    private javax.swing.JLabel label_total_data;
    private javax.swing.JTextField txt_max_nitrit;
    private javax.swing.JTextField txt_search_grade;
    private javax.swing.JTextField txt_search_no_box;
    // End of variables declaration//GEN-END:variables
}
