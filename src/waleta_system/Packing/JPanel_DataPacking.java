package waleta_system.Packing;

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

public class JPanel_DataPacking extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_DataPacking() {
        initComponents();
    }

    public void init() {
        refreshTable_DataBox();
    }

    public void refreshTable_DataBox() {
        try {
            int total_kpg = 0;
            float total_gram = 0;
            DefaultTableModel model = (DefaultTableModel) table_box_packing.getModel();
            model.setRowCount(0);

            String status = " AND `tb_box_packing`.`status` = '" + ComboBox_search_status.getSelectedItem().toString() + "'";
            if (ComboBox_search_status.getSelectedItem() == "All") {
                status = "";
            }
            String search_invoice = "AND `tb_box_packing`.`invoice_pengiriman` LIKE '%" + txt_no_invoice.getText() + "%' ";
            if ("".equals(txt_no_invoice.getText()) || txt_no_invoice.getText() == null) {
                search_invoice = "";
            }
            String search_spk = "AND `tb_spk_detail`.`kode_spk` LIKE '%" + txt_no_spk.getText() + "%' ";
            if ("".equals(txt_no_spk.getText()) || txt_no_spk.getText() == null) {
                search_spk = "";
            }

            String filter_tanggal = "";
            if (Date_filter1.getDate() != null && Date_filter2.getDate() != null) {
                if (ComboBox_filter_tanggal.getSelectedIndex() == 0) {
                    filter_tanggal = "AND (`tanggal_masuk` BETWEEN '" + dateFormat.format(Date_filter1.getDate()) + "' AND '" + dateFormat.format(Date_filter2.getDate()) + "')";
                } else if (ComboBox_filter_tanggal.getSelectedIndex() == 1) {
                    filter_tanggal = "AND (`tb_spk`.`tanggal_awb` BETWEEN '" + dateFormat.format(Date_filter1.getDate()) + "' AND '" + dateFormat.format(Date_filter2.getDate()) + "')";
                }
            }

            String search_grade = " AND `tb_grade_bahan_jadi`.`kode_grade` LIKE '%" + txt_search_grade.getText() + "%'";
            if ("".equals(txt_search_grade.getText()) || txt_search_grade.getText() == null) {
                search_grade = "";
            }

            sql = "SELECT `tb_box_packing`.`no_box`, `tb_box_bahan_jadi`.`tanggal_box`, `tb_grade_bahan_jadi`.`kode_grade`,`tb_box_bahan_jadi`.`keping`, `tb_box_bahan_jadi`.`berat`, `tb_box_bahan_jadi`.`kode_rsb` AS 'rsb_box', "
                    + "`tanggal_masuk`, `tb_box_packing`.`status`, `tb_box_packing`.`batch_number`, `tb_box_packing`.`invoice_pengiriman`, `tb_spk_detail`.`kode_spk`, `tb_spk_detail`.`grade_buyer`, `tb_spk_detail`.`kode_kh`, "
                    + "`tb_spk`.`tanggal_awb`, `tb_buyer`.`nama`, `jenis_pengiriman`  \n"
                    + "FROM `tb_box_packing` "
                    + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_box_packing`.`no_box` = `tb_box_bahan_jadi`.`no_box`\n"
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`"
                    + "LEFT JOIN `tb_spk_detail` ON `tb_box_packing`.`no_grade_spk` = `tb_spk_detail`.`no`"
                    + "LEFT JOIN `tb_spk` ON `tb_spk_detail`.`kode_spk` = `tb_spk`.`kode_spk`"
                    + "LEFT JOIN `tb_pengiriman` ON `tb_spk`.`kode_spk` = `tb_pengiriman`.`kode_spk`"
                    + "LEFT JOIN `tb_buyer` ON `tb_spk`.`buyer` = `tb_buyer`.`kode_buyer`"
                    + "WHERE `tb_box_packing`.`no_box` LIKE '%" + txt_no_box.getText() + "%' "
                    + search_invoice
                    + search_spk
                    + search_grade
                    + status
                    + filter_tanggal
                    + "ORDER BY `tanggal_masuk` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] baris = new Object[20];
            while (rs.next()) {
                baris[0] = rs.getString("no_box");
                baris[1] = rs.getString("kode_grade");
                baris[2] = rs.getInt("keping");
                baris[3] = rs.getFloat("berat");
                baris[4] = rs.getDate("tanggal_masuk");
                baris[5] = rs.getString("status");
                baris[6] = rs.getString("batch_number");
                baris[7] = rs.getString("invoice_pengiriman");
                baris[8] = rs.getString("jenis_pengiriman");
                baris[9] = rs.getString("kode_spk");
                baris[10] = rs.getString("grade_buyer");
                baris[11] = rs.getString("tanggal_awb");
                baris[12] = rs.getString("nama");
                baris[13] = rs.getString("kode_kh");
                baris[14] = rs.getString("rsb_box");
                model.addRow(baris);
                total_kpg += rs.getInt("keping");
                total_gram += rs.getFloat("berat");
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_box_packing);

            int total_data = table_box_packing.getRowCount();
            label_total_data.setText(decimalFormat.format(total_data));
            label_total_keping.setText(decimalFormat.format(total_kpg));
            label_total_gram.setText(decimalFormat.format(total_gram));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataPacking.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        table_box_packing = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txt_no_box = new javax.swing.JTextField();
        button_search = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        label_total_data = new javax.swing.JLabel();
        label_total_keping = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        label_total_gram = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        button_terima = new javax.swing.JButton();
        button_retur = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        ComboBox_search_status = new javax.swing.JComboBox<>();
        button_export = new javax.swing.JButton();
        txt_no_invoice = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        Date_filter1 = new com.toedter.calendar.JDateChooser();
        Date_filter2 = new com.toedter.calendar.JDateChooser();
        button_pindah_SPK = new javax.swing.JButton();
        button_print_label_packing = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        txt_no_spk = new javax.swing.JTextField();
        txt_search_grade = new javax.swing.JTextField();
        ComboBox_filter_tanggal = new javax.swing.JComboBox<>();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Box Packing", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        table_box_packing.setAutoCreateRowSorter(true);
        table_box_packing.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_box_packing.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Box", "Grade", "Keping", "Gram", "Tanggal Masuk", "Status", "Batch Numb", "Invoice Pengiriman", "Jenis Pengiriman", "SPK", "Grade SPK", "Tanggal AWB", "Nama Buyer", "RSB SPK", "RSB BOX"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_box_packing.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table_box_packing);

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("No Box :");

        txt_no_box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_no_box.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_no_boxKeyPressed(evt);
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
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Total Data :");

        label_total_data.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_data.setText("0");

        label_total_keping.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_keping.setText("0");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Keping :");

        label_total_gram.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_gram.setText("0");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Gram :");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Grade :");

        button_terima.setBackground(new java.awt.Color(255, 255, 255));
        button_terima.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_terima.setText("Terima Box");
        button_terima.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_terimaActionPerformed(evt);
            }
        });

        button_retur.setBackground(new java.awt.Color(255, 255, 255));
        button_retur.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_retur.setText("Retur ke Grading");
        button_retur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_returActionPerformed(evt);
            }
        });

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Status :");

        ComboBox_search_status.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_search_status.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "STOK", "PROSES", "OUT", "PENDING" }));

        button_export.setBackground(new java.awt.Color(255, 255, 255));
        button_export.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export.setText("Export to Excel");
        button_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportActionPerformed(evt);
            }
        });

        txt_no_invoice.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_no_invoice.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_no_invoiceKeyPressed(evt);
            }
        });

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("No Invoice :");

        Date_filter1.setDate(new Date());
        Date_filter1.setDateFormatString("dd MMM yyyy");
        Date_filter1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_filter2.setDate(new Date());
        Date_filter2.setDateFormatString("dd MMM yyyy");
        Date_filter2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_pindah_SPK.setBackground(new java.awt.Color(255, 255, 255));
        button_pindah_SPK.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_pindah_SPK.setText("Pindah SPK");
        button_pindah_SPK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_pindah_SPKActionPerformed(evt);
            }
        });

        button_print_label_packing.setBackground(new java.awt.Color(255, 255, 255));
        button_print_label_packing.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_print_label_packing.setText("Print Label Box");
        button_print_label_packing.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_print_label_packingActionPerformed(evt);
            }
        });

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("SPK :");

        txt_no_spk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_no_spk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_no_spkKeyPressed(evt);
            }
        });

        txt_search_grade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_grade.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_gradeKeyPressed(evt);
            }
        });

        ComboBox_filter_tanggal.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tgl Masuk", "Tgl AWB" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1210, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(button_terima, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(button_retur, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_pindah_SPK, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_print_label_packing, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_keping)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_no_box, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_no_invoice, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_no_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_search_status, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_filter_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_filter1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_filter2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ComboBox_search_status, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ComboBox_filter_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_no_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_no_invoice, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_no_box, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(Date_filter1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_filter2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 605, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(button_terima, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_retur, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_pindah_SPK, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_print_label_packing, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(label_total_data)
                    .addComponent(jLabel3)
                    .addComponent(label_total_keping)
                    .addComponent(jLabel4)
                    .addComponent(label_total_gram))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        refreshTable_DataBox();
    }//GEN-LAST:event_button_searchActionPerformed

    private void txt_no_boxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_no_boxKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_DataBox();
        }
    }//GEN-LAST:event_txt_no_boxKeyPressed

    private void button_terimaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_terimaActionPerformed
        // TODO add your handling code here:
        JDialog_terima_box dialog = new JDialog_terima_box(new javax.swing.JFrame(), true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        dialog.setResizable(false);
        refreshTable_DataBox();
    }//GEN-LAST:event_button_terimaActionPerformed

    private void button_returActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_returActionPerformed
        JDialog_ReturPacking dialog = new JDialog_ReturPacking(new javax.swing.JFrame(), true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        dialog.setResizable(false);
        refreshTable_DataBox();
    }//GEN-LAST:event_button_returActionPerformed

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_box_packing.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_exportActionPerformed

    private void txt_no_invoiceKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_no_invoiceKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_DataBox();
        }
    }//GEN-LAST:event_txt_no_invoiceKeyPressed

    private void button_pindah_SPKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_pindah_SPKActionPerformed
        // TODO add your handling code here:
        JDialog_PindahSPK dialog = new JDialog_PindahSPK(new javax.swing.JFrame(), true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        dialog.setResizable(false);
        refreshTable_DataBox();
    }//GEN-LAST:event_button_pindah_SPKActionPerformed

    private void button_print_label_packingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_print_label_packingActionPerformed
        // TODO add your handling code here:
        refreshTable_DataBox();
        try {
            String no_box = "";
            for (int i = 0; i < table_box_packing.getRowCount(); i++) {
                if (table_box_packing.getValueAt(i, 0) != null && !table_box_packing.getValueAt(i, 0).toString().equals("")) {
                    if (!no_box.equals("")) {
                        no_box = no_box + ", ";
                    }
                    no_box = no_box + "'" + table_box_packing.getValueAt(i, 0).toString() + "'";
                }
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
            Map<String, Object> params = new HashMap<String, Object>();
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, params, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);
        } catch (JRException ex) {
            Logger.getLogger(JPanel_DataPacking.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_print_label_packingActionPerformed

    private void txt_no_spkKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_no_spkKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_DataBox();
        }
    }//GEN-LAST:event_txt_no_spkKeyPressed

    private void txt_search_gradeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_gradeKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_DataBox();
        }
    }//GEN-LAST:event_txt_search_gradeKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_filter_tanggal;
    private javax.swing.JComboBox<String> ComboBox_search_status;
    private com.toedter.calendar.JDateChooser Date_filter1;
    private com.toedter.calendar.JDateChooser Date_filter2;
    private javax.swing.JButton button_export;
    public javax.swing.JButton button_pindah_SPK;
    private javax.swing.JButton button_print_label_packing;
    public javax.swing.JButton button_retur;
    public static javax.swing.JButton button_search;
    public javax.swing.JButton button_terima;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_total_data;
    private javax.swing.JLabel label_total_gram;
    private javax.swing.JLabel label_total_keping;
    public static javax.swing.JTable table_box_packing;
    private javax.swing.JTextField txt_no_box;
    private javax.swing.JTextField txt_no_invoice;
    private javax.swing.JTextField txt_no_spk;
    private javax.swing.JTextField txt_search_grade;
    // End of variables declaration//GEN-END:variables
}
