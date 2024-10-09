package waleta_system.Finance;

import waleta_system.Class.Utility;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
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
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;

public class JPanel_Biaya_Ekspor extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date today = new Date();
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    DefaultTableModel dm = new DefaultTableModel();
    JFileChooser chooser = new JFileChooser();
    DecimalFormat decimalFormat = new DecimalFormat();

    public JPanel_Biaya_Ekspor() {
        initComponents();
    }

    public void init() {
        refreshTable();
    }

    public void refreshTable() {
        try {
            double total_kg = 0, total_biaya_kg = 0;
            long total_biaya_invoice = 0;
            int kurs = Integer.valueOf(txt_kurs.getText());
            DefaultTableModel model = (DefaultTableModel) tabel_Biaya_invoice.getModel();
            model.setRowCount(0);
            
            String Buyer = "";
            if (txt_buyer.getText() != null && !txt_buyer.getText().equals("")) {
                Buyer = "AND `tb_payment_report`.`Buyer` LIKE '%" + txt_buyer.getText() + "%' ";
            }
            
            String tanggal = "";
            if (Date1.getDate() != null && Date2.getDate() != null) {
                tanggal = "AND `bulan` BETWEEN '" + dateFormat.format(Date1.getDate()) + "' AND '" + dateFormat.format(Date2.getDate()) + "' ";
            }
            
            sql = "SELECT `invoice_no`, `tb_payment_report`.`tgl_invoice`, `tb_payment_report`.`Buyer`, `tb_buyer`.`nama`, (`berat_waleta` + `berat_esta` + `berat_jastip`) AS 'net_weight', `biaya_coo`, `biaya_admin`, `biaya_sertif`, `biaya_fumigasi`, `biaya_asuransi`, `biaya_cargo`, (`value_waleta` + `value_esta` + `value_from_jtp` + `value_to_jtp`) AS 'cny' "
                    + "FROM `tb_biaya_expor` "
                    + "LEFT JOIN `tb_payment_report` ON `tb_biaya_expor`.`invoice_no` = `tb_payment_report`.`invoice`\n"
                    + "LEFT JOIN `tb_buyer` ON `tb_payment_report`.`Buyer` = `tb_buyer`.`kode_buyer`\n"
                    + "WHERE `invoice_no` LIKE '%" + txt_invoice.getText() + "%' "
                    + Buyer
                    + tanggal;
            System.out.println(sql);
            pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            Object[] row = new Object[14];
            while (rs.next()) {
                long rp = rs.getLong("cny") * Long.valueOf(txt_kurs.getText());
                long total_biaya = rs.getInt("biaya_coo") + rs.getInt("biaya_admin") + rs.getInt("biaya_sertif") + rs.getInt("biaya_fumigasi") + rs.getInt("biaya_asuransi") + rs.getInt("biaya_cargo");
                double net_weight = Math.round(rs.getFloat("net_weight") / 100.0) / 10.0;

                total_kg = total_kg + net_weight;
                total_biaya_invoice = total_biaya_invoice + total_biaya;
                row[0] = rs.getString("invoice_no");
                row[1] = rs.getDate("tgl_invoice");
                row[2] = rs.getString("nama");
                row[3] = net_weight;
                row[4] = rs.getDouble("cny");
                row[5] = rp;
                row[6] = Math.round(total_biaya / net_weight);
                row[7] = total_biaya;
                row[8] = rs.getFloat("biaya_admin");
                row[9] = rs.getFloat("biaya_coo");
                row[10] = rs.getFloat("biaya_sertif");
                row[11] = rs.getFloat("biaya_fumigasi");
                row[12] = rs.getFloat("biaya_asuransi");
                row[13] = rs.getFloat("biaya_cargo");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_Biaya_invoice);

            label_jumlah_data.setText(Integer.toString(tabel_Biaya_invoice.getRowCount()));
            label_total_kg.setText(decimalFormat.format(total_kg));
            label_total_biaya.setText(decimalFormat.format(total_biaya_invoice));
            label_total_biaya_kg.setText(decimalFormat.format(Math.round(total_biaya_invoice / total_kg)));
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Biaya_Ekspor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void ImportFromCSV() throws ParseException {
        try {
            int n = 0;
            JOptionPane.showMessageDialog(this,
                    "Pastikan \n"
                    + "- tanggal dalam format 'yyyy-MM-dd'\n"
                    + "- Urutan Kolom CSV :\n"
                    + "no invoice, tgl, buyer, gram, cny, ADMIN, COO, SERTIF, FUMIGASI, ASURANSI, CARGO",
                    "Warning Message !", JOptionPane.INFORMATION_MESSAGE);
            chooser.setDialogTitle("Select CSV file to import!");
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                Utility.db.getConnection();
                File file = chooser.getSelectedFile();
                String filename1 = file.getAbsolutePath();
                try (BufferedReader br = new BufferedReader(new FileReader(filename1))) {
                    String line;
                    String Query = null;
                    try {
                        Utility.db.getConnection().setAutoCommit(false);
                        while ((line = br.readLine()) != null) {
                            String[] value = line.split(";");
                            Query = "INSERT INTO `tb_biaya_expor`(`invoice_no`, `tanggal_invoice`, `Buyer`, `net_weight`, `cny`, `biaya_admin`, `biaya_coo`, `biaya_sertif`, `biaya_fumigasi`, `biaya_asuransi`, `biaya_cargo`) "
                                    + "VALUES ('" + value[0] + "','" + value[1] + "','" + value[2] + "','" + value[3] + "','" + value[4] + "','" + value[5] + "','" + value[6] + "','" + value[7] + "','" + value[8] + "','" + value[9] + "','" + value[10] + "','" + value[11] + "')";
                            Utility.db.getConnection().prepareStatement(Query);
                            if ((Utility.db.getStatement().executeUpdate(Query)) > 0) {
                                System.out.println(value[0]);
                                n++;
                            } else {
                                JOptionPane.showMessageDialog(this, "Failed insert : " + value[3]);
                            }
                        }
                        Utility.db.getConnection().commit();
                    } catch (SQLException ex) {
                        try {
                            Utility.db.getConnection().rollback();
                        } catch (SQLException x) {
                            Logger.getLogger(JPanel_Biaya_Ekspor.class.getName()).log(Level.SEVERE, null, x);
                        }
                        JOptionPane.showMessageDialog(this, ex.getMessage());
                        Logger.getLogger(JPanel_Biaya_Ekspor.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        JOptionPane.showMessageDialog(this, "Data Berhasil Masuk : " + n);
                    }
                }
            }
        } catch (HeadlessException | IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
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
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_Biaya_invoice = new javax.swing.JTable();
        jPanel_search_karyawan = new javax.swing.JPanel();
        txt_invoice = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        Date1 = new com.toedter.calendar.JDateChooser();
        jLabel34 = new javax.swing.JLabel();
        Date2 = new com.toedter.calendar.JDateChooser();
        button_refresh = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        txt_buyer = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        txt_kurs = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        label_jumlah_data = new javax.swing.JLabel();
        label_total_kg = new javax.swing.JLabel();
        button_edit_pengeluaran = new javax.swing.JButton();
        button_export_data_pengeluaran = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        label_total_biaya = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        label_total_biaya_kg = new javax.swing.JLabel();

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel1.setText("DATA BIAYA EKSPOR");

        tabel_Biaya_invoice.setAutoCreateRowSorter(true);
        tabel_Biaya_invoice.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        tabel_Biaya_invoice.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Invoice No", "Tanggal", "Buyer", "Qty (kg)", "Value (CNY)", "Value (Rp)", "Biaya / Kg", "Total Biaya", "Admin (Rp)", "COO (Rp)", "Sertifikat (Rp)", "Fumigasi (Rp)", "Asuransi (Rp)", "Cargo (Rp)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel_Biaya_invoice.setRowHeight(18);
        tabel_Biaya_invoice.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tabel_Biaya_invoice);

        jPanel_search_karyawan.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_search_karyawan.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Search", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N

        txt_invoice.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_invoice.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_invoiceKeyPressed(evt);
            }
        });

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Invoice No :");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Tanggal Invoice :");

        Date1.setBackground(new java.awt.Color(255, 255, 255));
        Date1.setToolTipText("");
        Date1.setDateFormatString("dd MMMM yyyy");
        Date1.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        Date1.setMinSelectableDate(new java.util.Date(1483207315000L));

        jLabel34.setBackground(new java.awt.Color(255, 255, 255));
        jLabel34.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setText("-");

        Date2.setBackground(new java.awt.Color(255, 255, 255));
        Date2.setDateFormatString("dd MMMM yyyy");
        Date2.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N

        button_refresh.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh.setText("Refresh");
        button_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refreshActionPerformed(evt);
            }
        });

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("Buyer :");

        txt_buyer.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_buyer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_buyerKeyPressed(evt);
            }
        });

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Kurs CNY to IDR :");

        txt_kurs.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_kurs.setText("2100");
        txt_kurs.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_kursKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_search_karyawanLayout = new javax.swing.GroupLayout(jPanel_search_karyawan);
        jPanel_search_karyawan.setLayout(jPanel_search_karyawanLayout);
        jPanel_search_karyawanLayout.setHorizontalGroup(
            jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_karyawanLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_invoice, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_buyer, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date2, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_kurs, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(button_refresh)
                .addContainerGap(337, Short.MAX_VALUE))
        );
        jPanel_search_karyawanLayout.setVerticalGroup(
            jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_karyawanLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_buyer, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_invoice, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(Date2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_kurs, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(5, 5, 5))
        );

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel15.setText("Total kg :");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel11.setText("Jumlah Pengiriman :");

        label_jumlah_data.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_data.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_jumlah_data.setText("0");

        label_total_kg.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kg.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_total_kg.setText("0");

        button_edit_pengeluaran.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_pengeluaran.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_edit_pengeluaran.setText("Edit");
        button_edit_pengeluaran.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_pengeluaranActionPerformed(evt);
            }
        });

        button_export_data_pengeluaran.setBackground(new java.awt.Color(255, 255, 255));
        button_export_data_pengeluaran.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        button_export_data_pengeluaran.setText("Export to Excel");
        button_export_data_pengeluaran.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_data_pengeluaranActionPerformed(evt);
            }
        });

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel16.setText("Total Biaya :");

        label_total_biaya.setBackground(new java.awt.Color(255, 255, 255));
        label_total_biaya.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_total_biaya.setText("0");

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel17.setText("Rata2 Biaya / Kg :");

        label_total_biaya_kg.setBackground(new java.awt.Color(255, 255, 255));
        label_total_biaya_kg.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_total_biaya_kg.setText("0");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel_search_karyawan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_jumlah_data)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_kg)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_biaya)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_biaya_kg)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_edit_pengeluaran)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export_data_pengeluaran)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel_search_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 555, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_export_data_pengeluaran, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_edit_pengeluaran, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_jumlah_data, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_kg, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_biaya, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_biaya_kg, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txt_invoiceKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_invoiceKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_invoiceKeyPressed

    private void button_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refreshActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_button_refreshActionPerformed

    private void button_edit_pengeluaranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_pengeluaranActionPerformed
        // TODO add your handling code here:
        int x = tabel_Biaya_invoice.getSelectedRow();
        if (x > -1) {
            String invoice = tabel_Biaya_invoice.getValueAt(x, 0).toString();
            JDialog_NewEdit_BiayaEkspor dialog = new JDialog_NewEdit_BiayaEkspor(new javax.swing.JFrame(), true, invoice);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            dialog.setEnabled(true);
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Silahkan klik pada data yang akan di Edit");
        }
    }//GEN-LAST:event_button_edit_pengeluaranActionPerformed

    private void button_export_data_pengeluaranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_data_pengeluaranActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_Biaya_invoice.getModel();
        ExportToExcel.writeToExcel(model, jPanel2);
    }//GEN-LAST:event_button_export_data_pengeluaranActionPerformed

    private void txt_buyerKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_buyerKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_buyerKeyPressed

    private void txt_kursKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_kursKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_kursKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser Date1;
    private com.toedter.calendar.JDateChooser Date2;
    public static javax.swing.JButton button_edit_pengeluaran;
    private javax.swing.JButton button_export_data_pengeluaran;
    public static javax.swing.JButton button_refresh;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel_search_karyawan;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_jumlah_data;
    private javax.swing.JLabel label_total_biaya;
    private javax.swing.JLabel label_total_biaya_kg;
    private javax.swing.JLabel label_total_kg;
    private javax.swing.JTable tabel_Biaya_invoice;
    private javax.swing.JTextField txt_buyer;
    private javax.swing.JTextField txt_invoice;
    private javax.swing.JTextField txt_kurs;
    // End of variables declaration//GEN-END:variables
}
