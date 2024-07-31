package waleta_system.QC;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
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

public class JPanel_Lab_Heat_Treatment extends javax.swing.JPanel {

    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    String sql = null;
    ResultSet rs;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_Lab_Heat_Treatment() {
        initComponents();
    }

    public void init() {
        refreshTable();
        Table_data.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && Table_data.getSelectedRow() != -1) {
                    int i = Table_data.getSelectedRow();
                }
            }
        });
    }

    public void refreshTable() {
        try {
            decimalFormat.setMaximumFractionDigits(2);
            DefaultTableModel model = (DefaultTableModel) Table_data.getModel();
            model.setRowCount(0);

            String filter_tanggal = "";
            if (Date_Heat_Treatment1.getDate() != null && Date_Heat_Treatment2.getDate() != null) {
                filter_tanggal = " AND (`tgl_heat_treatment` BETWEEN '" + dateFormat.format(Date_Heat_Treatment1.getDate()) + "' AND '" + dateFormat.format(Date_Heat_Treatment2.getDate()) + "')";
            }

            String search_invoice = " AND `tb_pengiriman`.`invoice_no` LIKE '%" + txt_search_no_invoice.getText() + "%' ";
            if (txt_search_no_invoice.getText() == null || txt_search_no_invoice.getText().equals("")) {
                search_invoice = "";
            }

            String search_spk = " AND `tb_spk_detail`.`kode_spk` LIKE '%" + txt_search_no_spk.getText() + "%' ";
            if (txt_search_no_spk.getText() == null || txt_search_no_spk.getText().equals("")) {
                search_spk = "";
            }

            String search_grade = " AND `tb_heat_treatment_pengiriman`.`kode_grade` LIKE '%" + txt_search_grade.getText() + "%' ";
            if (txt_search_grade.getText() == null || txt_search_grade.getText().equals("")) {
                search_grade = "";
            }

            sql = "SELECT `tb_pengiriman`.`invoice_no`, `tb_spk_detail`.`kode_spk`, `tb_spk`.`tanggal_awb`, "
                    + "`operator_heat_treatment`, `nama_pegawai`, `suhu_ruang`, `suhu_sarang_awal`, "
                    + "`tb_heat_treatment_pengiriman`.`no`, `tgl_heat_treatment`, `no_tray`, "
                    + "`tb_heat_treatment_pengiriman`.`no_box`, "
                    + "`tb_heat_treatment_pengiriman`.`kode_rsb`, `tb_heat_treatment_pengiriman`.`kode_grade`,\n"
                    + "`tb_box_bahan_jadi`.`kode_rsb` AS 'kode_rsb2', `tb_grade_bahan_jadi`.`kode_grade` AS 'kode_grade2',\n"
                    + "`tb_heat_treatment_pengiriman`.`keping`, `tb_heat_treatment_pengiriman`.`gram`, `waktu_preheat`, `suhu_preheat`, `suhu_akhir`, `waktu_heat_treatment`, "
                    + "`tb_heat_treatment_pengiriman`.`keterangan`, `tb_heat_treatment_pengiriman`.`tanggal_pengiriman` \n"
                    + "FROM `tb_heat_treatment_pengiriman` \n"
                    + "LEFT JOIN `tb_box_packing` ON `tb_heat_treatment_pengiriman`.`no_box` = `tb_box_packing`.`no_box`\n"
                    + "LEFT JOIN `tb_spk_detail` ON `tb_box_packing`.`no_grade_spk` = `tb_spk_detail`.`no`\n"
                    + "LEFT JOIN `tb_spk` ON `tb_spk_detail`.`kode_spk` = `tb_spk`.`kode_spk`\n"
                    + "LEFT JOIN `tb_pengiriman` ON `tb_spk`.`kode_spk` = `tb_pengiriman`.`kode_spk`\n"
                    + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_heat_treatment_pengiriman`.`no_box` = `tb_box_bahan_jadi`.`no_box`\n"
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`\n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_heat_treatment_pengiriman`.`operator_heat_treatment` = `tb_karyawan`.`id_pegawai`\n"
                    + "WHERE "
                    + "`tb_heat_treatment_pengiriman`.`no_box` LIKE '%" + txt_search_no_box.getText() + "%' "
                    + search_spk
                    + search_invoice
                    + search_grade
                    + filter_tanggal
                    + "ORDER BY `tb_spk_detail`.`kode_spk` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[20];
            while (rs.next()) {
                row[0] = rs.getInt("no");
                row[1] = rs.getString("invoice_no");
                row[2] = rs.getString("kode_spk");
                row[3] = rs.getDate("tanggal_pengiriman");
                row[4] = rs.getString("nama_pegawai");
                row[5] = rs.getFloat("suhu_ruang");
                row[6] = rs.getFloat("suhu_sarang_awal");
                row[7] = rs.getDate("tgl_heat_treatment");
                row[8] = rs.getInt("no_tray");
                row[9] = rs.getString("no_box");
                row[10] = rs.getString("kode_rsb");
                row[11] = rs.getString("kode_grade");
                row[12] = rs.getFloat("keping");
                row[13] = rs.getFloat("gram");
                row[14] = rs.getTime("waktu_preheat");
                row[15] = rs.getFloat("suhu_preheat");
                row[16] = rs.getFloat("suhu_akhir");
                row[17] = rs.getTime("waktu_heat_treatment");
                row[18] = rs.getString("keterangan");
                String batch = new SimpleDateFormat("YYMMDD").format(rs.getDate("tgl_heat_treatment"));
                model.addRow(row);
            }

            label_total_data.setText(Integer.toString(Table_data.getRowCount()));
            ColumnsAutoSizer.sizeColumnsToFit(Table_data);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Lab_Heat_Treatment.class.getName()).log(Level.SEVERE, null, ex);
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
        Table_data = new javax.swing.JTable();
        button_export_dataTreatment = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        label_total_data = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txt_search_no_box = new javax.swing.JTextField();
        button_Refresh = new javax.swing.JButton();
        txt_search_grade = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txt_search_no_invoice = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txt_search_no_spk = new javax.swing.JTextField();
        button_edit = new javax.swing.JButton();
        button_delete = new javax.swing.JButton();
        button_catatan_pemanasan_barang_jadi = new javax.swing.JButton();
        button_input_data_csv = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        Date_Heat_Treatment1 = new com.toedter.calendar.JDateChooser();
        Date_Heat_Treatment2 = new com.toedter.calendar.JDateChooser();
        button_delete_all = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Heat Treatment Pengiriman", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        Table_data.setAutoCreateRowSorter(true);
        Table_data.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_data.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Invoice", "kode SPK", "Tgl Pengiriman", "Operator", "Suhu Ruang", "Suhu Sarang awal", "Tgl Heat Treatment", "No Tray", "No Box", "Kode RSB", "Grade", "Biji (pcs)", "Berat (gr)", "Waktu Preheat", "Suhu Preheat", "Suhu Akhir", "Menit Treatment", "Keterangan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Object.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Object.class, java.lang.Float.class, java.lang.Float.class, java.lang.Object.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_data.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Table_data);

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

        button_Refresh.setBackground(new java.awt.Color(255, 255, 255));
        button_Refresh.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_Refresh.setText("Refresh");
        button_Refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_RefreshActionPerformed(evt);
            }
        });

        txt_search_grade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_grade.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_gradeKeyPressed(evt);
            }
        });

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Grade :");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("No Invoice :");

        txt_search_no_invoice.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_no_invoice.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_no_invoiceKeyPressed(evt);
            }
        });

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel15.setText("SPK :");

        txt_search_no_spk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_no_spk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_no_spkKeyPressed(evt);
            }
        });

        button_edit.setBackground(new java.awt.Color(255, 255, 255));
        button_edit.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_edit.setText("Edit");
        button_edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_editActionPerformed(evt);
            }
        });

        button_delete.setBackground(new java.awt.Color(255, 255, 255));
        button_delete.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_delete.setText("Delete");
        button_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_deleteActionPerformed(evt);
            }
        });

        button_catatan_pemanasan_barang_jadi.setBackground(new java.awt.Color(255, 255, 255));
        button_catatan_pemanasan_barang_jadi.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_catatan_pemanasan_barang_jadi.setText("Catatan Pemanasan Barang Jadi (CCP2)");
        button_catatan_pemanasan_barang_jadi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_catatan_pemanasan_barang_jadiActionPerformed(evt);
            }
        });

        button_input_data_csv.setBackground(new java.awt.Color(255, 255, 255));
        button_input_data_csv.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_input_data_csv.setText("Input Data CSV");
        button_input_data_csv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_data_csvActionPerformed(evt);
            }
        });

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel16.setText("Tgl Heat Treatment :");

        Date_Heat_Treatment1.setBackground(new java.awt.Color(255, 255, 255));
        Date_Heat_Treatment1.setDateFormatString("dd MMM yyyy");
        Date_Heat_Treatment1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_Heat_Treatment2.setBackground(new java.awt.Color(255, 255, 255));
        Date_Heat_Treatment2.setDateFormatString("dd MMM yyyy");
        Date_Heat_Treatment2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_delete_all.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_all.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_delete_all.setText("Delete All");
        button_delete_all.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_allActionPerformed(evt);
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
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(button_edit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_delete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_delete_all)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_input_data_csv)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_catatan_pemanasan_barang_jadi)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_no_box, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_no_invoice, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_no_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Heat_Treatment1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Heat_Treatment2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_Refresh)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 137, Short.MAX_VALUE)
                        .addComponent(button_export_dataTreatment)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_export_dataTreatment, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_Refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_no_box, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_no_invoice, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_no_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(Date_Heat_Treatment1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Date_Heat_Treatment2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_edit)
                    .addComponent(button_delete)
                    .addComponent(jLabel2)
                    .addComponent(label_total_data)
                    .addComponent(button_input_data_csv)
                    .addComponent(button_catatan_pemanasan_barang_jadi)
                    .addComponent(button_delete_all))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE)
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
        DefaultTableModel model = (DefaultTableModel) Table_data.getModel();
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

    private void txt_search_gradeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_gradeKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_gradeKeyPressed

    private void txt_search_no_spkKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_no_spkKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_no_spkKeyPressed

    private void button_editActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_editActionPerformed
        // TODO add your handling code here:
        int j = Table_data.getSelectedRow();
        if (j == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih data yang akan di edit !");
        } else {
            String no = Table_data.getValueAt(j, 0).toString();
            JDialog_Input_BoxHeatTreatment dialog = new JDialog_Input_BoxHeatTreatment(new javax.swing.JFrame(), true, no);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            dialog.setEnabled(true);
            dialog.setResizable(false);
            refreshTable();
        }
    }//GEN-LAST:event_button_editActionPerformed

    private void button_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_deleteActionPerformed
        // TODO add your handling code here:
        int j = Table_data.getSelectedRow();
        if (j == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih data yang akan di hapus !");
        } else {
            int dialogResult = JOptionPane.showConfirmDialog(this, "Hapus data", "Warning", 0);
            if (dialogResult == JOptionPane.YES_OPTION) {
                // delete code here
                try {
                    String Query = "DELETE FROM `tb_heat_treatment_pengiriman` WHERE `no` = '" + Table_data.getValueAt(j, 0).toString() + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                        JOptionPane.showMessageDialog(this, "deleted !");
                        refreshTable();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed !");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, e);
                }
            }
        }
    }//GEN-LAST:event_button_deleteActionPerformed

    private void button_catatan_pemanasan_barang_jadiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_catatan_pemanasan_barang_jadiActionPerformed
        // TODO add your handling code here:
        try {
            String no = "";
            for (int i = 0; i < Table_data.getRowCount(); i++) {
                if (i != 0) {
                    no = no + ", ";
                }
                no = no + "'" + Table_data.getValueAt(i, 0).toString() + "'";
            }
            String Query = "SELECT `tb_pengiriman`.`invoice_no`, `tb_spk_detail`.`kode_spk`, `tb_spk`.`tanggal_awb`, `tb_heat_treatment_pengiriman`.`no`, `tgl_heat_treatment`, `no_tray`, "
                    + "`tb_heat_treatment_pengiriman`.`no_box`,\n"
                    + "`tb_heat_treatment_pengiriman`.`kode_rsb`, `tb_heat_treatment_pengiriman`.`kode_grade`,\n"
                    + "`tb_box_bahan_jadi`.`kode_rsb` AS 'kode_rsb2', `tb_grade_bahan_jadi`.`kode_grade` AS 'kode_grade2',\n"
                    + "`tb_heat_treatment_pengiriman`.`keping`, `tb_heat_treatment_pengiriman`.`gram`,\n"
                    + "`operator_heat_treatment`, `nama_pegawai`, `suhu_ruang`, `suhu_sarang_awal`, `waktu_preheat`, `suhu_preheat`, `suhu_akhir`, `waktu_heat_treatment`, "
                    + "`tb_heat_treatment_pengiriman`.`keterangan`, `tb_heat_treatment_pengiriman`.`tanggal_pengiriman`\n"
                    + "FROM `tb_heat_treatment_pengiriman`\n"
                    + "LEFT JOIN `tb_box_packing` ON `tb_heat_treatment_pengiriman`.`no_box` = `tb_box_packing`.`no_box`\n"
                    + "LEFT JOIN `tb_spk_detail` ON `tb_box_packing`.`no_grade_spk` = `tb_spk_detail`.`no`\n"
                    + "LEFT JOIN `tb_spk` ON `tb_spk_detail`.`kode_spk` = `tb_spk`.`kode_spk`\n"
                    + "LEFT JOIN `tb_pengiriman` ON `tb_spk`.`kode_spk` = `tb_pengiriman`.`kode_spk`\n"
                    + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_heat_treatment_pengiriman`.`no_box` = `tb_box_bahan_jadi`.`no_box`\n"
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`\n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_heat_treatment_pengiriman`.`operator_heat_treatment` = `tb_karyawan`.`id_pegawai`\n"
                    + "WHERE "
                    + "`tb_heat_treatment_pengiriman`.`no` IN (" + no + ") "
                    + "ORDER BY "
                    + "`tb_heat_treatment_pengiriman`.`tanggal_pengiriman`, `tgl_heat_treatment`, `suhu_ruang`, `suhu_sarang_awal`, `waktu_preheat`, `suhu_preheat`, `suhu_akhir`, `waktu_heat_treatment`, `no_tray`";
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(Query);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Catatan_Pemanasan_Barang_Jadi_CCP2.jrxml");
            JASP_DESIGN.setQuery(newQuery);
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, null, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Lab_Heat_Treatment.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_catatan_pemanasan_barang_jadiActionPerformed

    private void txt_search_no_invoiceKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_no_invoiceKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_no_invoiceKeyPressed

    private void button_input_data_csvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_data_csvActionPerformed
        // TODO add your handling code here:
        try {
            JOptionPane.showMessageDialog(this, "Format csv sesuai tabel di sistem, kecuali kolom invoice, kode spk\n"
                    + "Nama Operator di ganti ID pegawai!");
            int n = 0;
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select CSV file to import!");
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                Utility.db.getConnection();
                File file = chooser.getSelectedFile();
                String filename1 = file.getAbsolutePath();
                try (BufferedReader br = new BufferedReader(new FileReader(filename1))) {
                    String line;
                    try {
                        Utility.db.getConnection().setAutoCommit(false);
                        while ((line = br.readLine()) != null) {
                            String[] value = line.split(";");
                            String Query = "INSERT INTO `tb_heat_treatment_pengiriman`("
                                    + "`tanggal_pengiriman`, "
                                    + "`operator_heat_treatment`, "
                                    + "`suhu_ruang`, "
                                    + "`suhu_sarang_awal`, "
                                    + "`tgl_heat_treatment`, "
                                    + "`no_tray`, "
                                    + "`no_box`, "
                                    + "`kode_rsb`, "
                                    + "`kode_grade`, "
                                    + "`keping`, "
                                    + "`gram`, "
                                    + "`waktu_preheat`, "
                                    + "`suhu_preheat`, "
                                    + "`suhu_akhir`, "
                                    + "`waktu_heat_treatment`, "
                                    + "`keterangan`"
                                    + ") "
                                    + "VALUES ("
                                    + "'" + value[0] + "',"//tanggal_pengiriman
                                    + "'" + value[1] + "',"//operator_heat_treatment
                                    + "'" + value[2] + "',"//suhu_ruang
                                    + "'" + value[3] + "',"//suhu_sarang_awal
                                    + "'" + value[4] + "',"//tgl_heat_treatment
                                    + "'" + value[5] + "',"//no_tray
                                    + "'" + value[6] + "',"//no_box
                                    + "'" + value[7] + "',"//kode_rsb
                                    + "'" + value[8] + "',"//kode_grade
                                    + "'" + value[9] + "',"//keping
                                    + "'" + value[10] + "',"//gram
                                    + "'" + value[11] + "',"//waktu_preheat
                                    + "'" + value[12] + "',"//suhu_preheat
                                    + "'" + value[13] + "',"//suhu_akhir
                                    + "'" + value[14] + "',"//waktu_heat_treatment
                                    + "'" + value[15] + "'"//keterangan
                                    + ") ";
                            Utility.db.getConnection().prepareStatement(Query);
                            if ((Utility.db.getStatement().executeUpdate(Query)) > 0) {
                                n++;
                            }
                        }
                        Utility.db.getConnection().commit();
                    } catch (Exception ex) {
                        Utility.db.getConnection().rollback();
                        JOptionPane.showMessageDialog(this, ex);
                        Logger.getLogger(JPanel_Lab_Heat_Treatment.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        Utility.db.getConnection().setAutoCommit(true);
                        JOptionPane.showMessageDialog(this, "Data Berhasil Masuk : " + n);
                        refreshTable();
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Lab_Heat_Treatment.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_input_data_csvActionPerformed

    private void button_delete_allActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_allActionPerformed
        // TODO add your handling code here:
        int j = Table_data.getSelectedRow();
        if (j == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih data yang akan di hapus !");
        } else {
            int dialogResult = JOptionPane.showConfirmDialog(this, "Hapus data?", "Warning", 0);
            if (dialogResult == JOptionPane.YES_OPTION) {
                // delete code here
                try {
                    String no = "";
                    for (int i = 0; i < Table_data.getRowCount(); i++) {
                        if (i != 0) {
                            no = no + ", ";
                        }
                        no = no + "'" + Table_data.getValueAt(i, 0).toString() + "'";
                    }

                    String Query = "DELETE FROM `tb_heat_treatment_pengiriman` WHERE `no` IN (" + no + ")";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                        JOptionPane.showMessageDialog(this, "deleted !");
                        refreshTable();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed !");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, e);
                }
            }
        }
    }//GEN-LAST:event_button_delete_allActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser Date_Heat_Treatment1;
    private com.toedter.calendar.JDateChooser Date_Heat_Treatment2;
    private javax.swing.JTable Table_data;
    public static javax.swing.JButton button_Refresh;
    private javax.swing.JButton button_catatan_pemanasan_barang_jadi;
    public static javax.swing.JButton button_delete;
    public static javax.swing.JButton button_delete_all;
    public static javax.swing.JButton button_edit;
    public static javax.swing.JButton button_export_dataTreatment;
    public static javax.swing.JButton button_input_data_csv;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_total_data;
    private javax.swing.JTextField txt_search_grade;
    private javax.swing.JTextField txt_search_no_box;
    private javax.swing.JTextField txt_search_no_invoice;
    private javax.swing.JTextField txt_search_no_spk;
    // End of variables declaration//GEN-END:variables
}
