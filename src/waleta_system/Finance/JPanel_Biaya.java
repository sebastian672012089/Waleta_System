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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;

import waleta_system.Class.ExportToExcel;

public class JPanel_Biaya extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date today = new Date();
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    DefaultTableModel dm = new DefaultTableModel();
    JFileChooser chooser = new JFileChooser();
    DecimalFormat decimalFormat = new DecimalFormat();

    public JPanel_Biaya() {
        initComponents();
    }

    public void init() {
        try {
            ComboBox_Kategori.removeAllItems();
            ComboBox_Kategori.addItem("All");
            sql = "SELECT DISTINCT(`Kategori1`) AS 'kategori' FROM `tb_biaya_pabrik` WHERE 1";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_Kategori.addItem(rs.getString("kategori"));
            }
            refreshTable();
        } catch (Exception ex) {
            Logger.getLogger(JPanel_Biaya.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable() {
        try {
            long total_pengeluaran = 0;
            long total_pengeluaran_laporan_keuangan = 0;
            int num1 = 0;
            int num2 = 0;
            DefaultTableModel model = (DefaultTableModel) tabel_Biaya.getModel();
            model.setRowCount(0);
            DefaultTableModel model_tabel_Laporan_Keuangan = (DefaultTableModel) tabel_Laporan_Keuangan.getModel();
            model_tabel_Laporan_Keuangan.setRowCount(0);

            String tipe_akun = "AND `tb_biaya_pabrik`.`Kategori1` = '" + ComboBox_Kategori.getSelectedItem().toString() + "' ";
            String jumlah_pengeluaran = "";
            String tanggal = "";

            if ("All".equals(ComboBox_Kategori.getSelectedItem().toString())) {
                tipe_akun = "";
            }
            try {
                num1 = Integer.valueOf(txt_jumlah1.getText());
                num2 = Integer.valueOf(txt_jumlah2.getText());
                jumlah_pengeluaran = "AND " + num1 + " < `nilai` AND `nilai` < " + num2 + " ";
            } catch (NumberFormatException e) {
                num1 = 0;
                num2 = 0;
                jumlah_pengeluaran = "";
            }
            if (Date1.getDate() != null && Date2.getDate() != null) {
                tanggal = "AND `bulan` BETWEEN '" + dateFormat.format(Date1.getDate()) + "' AND '" + dateFormat.format(Date2.getDate()) + "' ";
            }
            sql = "SELECT `no`, `bulan`, `Kategori1`, `jenis_pengeluaran`, `nilai` FROM `tb_biaya_pabrik`\n"
                    + "WHERE `jenis_pengeluaran` LIKE '%" + txt_keywords.getText() + "%' "
                    + tipe_akun
                    + jumlah_pengeluaran
                    + tanggal;
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[5];
            while (rs.next()) {
                row[0] = rs.getString("no");
                row[1] = rs.getDate("bulan");
                row[2] = rs.getString("Kategori1");
                row[3] = rs.getString("jenis_pengeluaran");
                row[4] = decimalFormat.format(rs.getLong("nilai"));
                if (rs.getString("Kategori1").equals("Laporan Keuangan")) {
                    model_tabel_Laporan_Keuangan.addRow(row);
                    total_pengeluaran_laporan_keuangan = total_pengeluaran_laporan_keuangan + rs.getLong("nilai");
                } else {
                    model.addRow(row);
                    total_pengeluaran = total_pengeluaran + rs.getLong("nilai");
                }
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_Laporan_Keuangan);
            ColumnsAutoSizer.sizeColumnsToFit(tabel_Biaya);

            label_jumlah_data_laporan_keuangan.setText(Integer.toString(tabel_Laporan_Keuangan.getRowCount()));
            label_jumlah_pengeluaran_laporan_keuangan.setText(decimalFormat.format(total_pengeluaran_laporan_keuangan));
            label_jumlah_data.setText(Integer.toString(tabel_Biaya.getRowCount()));
            label_jumlah_pengeluaran.setText(decimalFormat.format(total_pengeluaran));
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Biaya.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_DaftarAkun() {
        try {
            DefaultTableModel model = (DefaultTableModel) Tabel_Akun.getModel();
            model.setRowCount(0);
            String query = "SELECT `nama_akun`, `induk_akun`, `tipe_akun` FROM `tb_biaya_pabrik_kategori` WHERE 1";
            PreparedStatement pst1 = Utility.db.getConnection().prepareStatement(query);
            ResultSet result = pst1.executeQuery();
            Object[] row = new Object[3];
            while (result.next()) {
                row[0] = result.getString("tipe_akun");
                row[1] = result.getString("induk_akun");
                row[2] = result.getString("nama_akun");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Tabel_Akun);
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Biaya.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void ImportFromCSV() throws ParseException {
        try {
            int n = 0;
            JOptionPane.showMessageDialog(this,
                    "Pastikan \n"
                    + "- tanggal dalam format 'yyyy-MM-dd'\n"
                    + "- 6 Kategori Biaya (Bahan Baku, Tenaga Kerja Langsung, Overhead, Umum, Payroll Staff, Ekspor)",
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
                            if (line.length() > 10) {
                                String[] value = line.split(";");
                                Query = "INSERT INTO `tb_biaya_pabrik`(`bulan`, `Kategori1`, `jenis_pengeluaran`, `nilai`) "
                                        + "VALUES ('" + value[0] + "','" + value[1] + "','" + value[2] + "','" + value[3] + "')";
                                Utility.db.getConnection().prepareStatement(Query);
                                if ((Utility.db.getStatement().executeUpdate(Query)) > 0) {
//                                System.out.println(value[0] + "','" + value[1] + "','" + value[2] + "','" + value[3]);
                                    n++;
                                } else {
                                    JOptionPane.showMessageDialog(this, "Failed insert : " + value[3]);
                                }
                            }
                        }
                        Utility.db.getConnection().commit();
                        JOptionPane.showMessageDialog(this, "Data Berhasil Masuk : " + n);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, ex);
                        Logger.getLogger(JPanel_Biaya.class.getName()).log(Level.SEVERE, null, ex);
                        try {
                            Utility.db.getConnection().rollback();
                        } catch (SQLException x) {
                            Logger.getLogger(JPanel_Biaya.class.getName()).log(Level.SEVERE, null, x);
                        }
                    } finally {
                        try {
                            Utility.db.getConnection().setAutoCommit(true);
                        } catch (SQLException x) {
                            Logger.getLogger(JPanel_Biaya.class.getName()).log(Level.SEVERE, null, "BBB : " + x);
                        }
                    }
                }
            }
        } catch (HeadlessException | IOException ex) {
            JOptionPane.showMessageDialog(this, ex);
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
        tabel_Biaya = new javax.swing.JTable();
        jPanel_search_karyawan = new javax.swing.JPanel();
        txt_keywords = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txt_jumlah1 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        ComboBox_Kategori = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        txt_jumlah2 = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        Date1 = new com.toedter.calendar.JDateChooser();
        jLabel34 = new javax.swing.JLabel();
        Date2 = new com.toedter.calendar.JDateChooser();
        button_refresh = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        label_jumlah_data = new javax.swing.JLabel();
        label_jumlah_pengeluaran = new javax.swing.JLabel();
        button_edit_pengeluaran = new javax.swing.JButton();
        button_export_data_pengeluaran = new javax.swing.JButton();
        button_new_pengeluaran = new javax.swing.JButton();
        button_delete_pengeluaran = new javax.swing.JButton();
        button_import = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabel_Laporan_Keuangan = new javax.swing.JTable();
        jLabel12 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        label_jumlah_pengeluaran_laporan_keuangan = new javax.swing.JLabel();
        label_jumlah_data_laporan_keuangan = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        button_export_data_akun = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        Tabel_Akun = new javax.swing.JTable();
        jLabel14 = new javax.swing.JLabel();
        button_edit_akun = new javax.swing.JButton();
        label_jumlah_akun = new javax.swing.JLabel();
        button_new_akun = new javax.swing.JButton();
        button_delete_akun = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel1.setText("DATA BIAYA PABRIK");

        tabel_Biaya.setAutoCreateRowSorter(true);
        tabel_Biaya.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        tabel_Biaya.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "Tanggal", "Kategori", "Jenis Biaya", "Biaya"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel_Biaya.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tabel_Biaya);

        jPanel_search_karyawan.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_search_karyawan.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Filter", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N

        txt_keywords.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_keywords.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_keywordsKeyPressed(evt);
            }
        });

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Keywords :");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Jumlah Pengeluaran :");

        txt_jumlah1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_jumlah1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_jumlah1KeyPressed(evt);
            }
        });

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("Kategori :");

        ComboBox_Kategori.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_Kategori.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("-");

        txt_jumlah2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_jumlah2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_jumlah2KeyPressed(evt);
            }
        });

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Bulan :");

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

        javax.swing.GroupLayout jPanel_search_karyawanLayout = new javax.swing.GroupLayout(jPanel_search_karyawan);
        jPanel_search_karyawan.setLayout(jPanel_search_karyawanLayout);
        jPanel_search_karyawanLayout.setHorizontalGroup(
            jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_karyawanLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_keywords, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date2, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ComboBox_Kategori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_jumlah1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_jumlah2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_refresh)
                .addContainerGap(246, Short.MAX_VALUE))
        );
        jPanel_search_karyawanLayout.setVerticalGroup(
            jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_karyawanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txt_keywords, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(Date2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_jumlah1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_jumlah2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(ComboBox_Kategori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(button_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel15.setText("Jumlah Pengeluaran :");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel11.setText("Jumlah Data :");

        label_jumlah_data.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_data.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_jumlah_data.setText("0");

        label_jumlah_pengeluaran.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_pengeluaran.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_jumlah_pengeluaran.setText("0");

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

        button_new_pengeluaran.setBackground(new java.awt.Color(255, 255, 255));
        button_new_pengeluaran.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_new_pengeluaran.setText("+ New");
        button_new_pengeluaran.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_new_pengeluaranActionPerformed(evt);
            }
        });

        button_delete_pengeluaran.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_pengeluaran.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete_pengeluaran.setText("Delete");
        button_delete_pengeluaran.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_pengeluaranActionPerformed(evt);
            }
        });

        button_import.setBackground(new java.awt.Color(255, 255, 255));
        button_import.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_import.setText("Import from CSV");
        button_import.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_importActionPerformed(evt);
            }
        });

        tabel_Laporan_Keuangan.setAutoCreateRowSorter(true);
        tabel_Laporan_Keuangan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        tabel_Laporan_Keuangan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Kode", "Tanggal", "Kategori", "Jenis Biaya", "Biaya"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel_Laporan_Keuangan.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tabel_Laporan_Keuangan);

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel12.setText("Jumlah Data :");

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel16.setText("Jumlah :");

        label_jumlah_pengeluaran_laporan_keuangan.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_pengeluaran_laporan_keuangan.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_jumlah_pengeluaran_laporan_keuangan.setText("0");

        label_jumlah_data_laporan_keuangan.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_data_laporan_keuangan.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_jumlah_data_laporan_keuangan.setText("0");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        button_export_data_akun.setBackground(new java.awt.Color(255, 255, 255));
        button_export_data_akun.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        button_export_data_akun.setText("Export to Excel");
        button_export_data_akun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_data_akunActionPerformed(evt);
            }
        });

        Tabel_Akun.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Tabel_Akun.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tipe Akun", "Induk Akun", "Nama Akun"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        Tabel_Akun.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(Tabel_Akun);

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel14.setText("Jumlah Akun :");

        button_edit_akun.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_akun.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_edit_akun.setText("Edit");
        button_edit_akun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_akunActionPerformed(evt);
            }
        });

        label_jumlah_akun.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_akun.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_jumlah_akun.setText("0");

        button_new_akun.setBackground(new java.awt.Color(255, 255, 255));
        button_new_akun.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_new_akun.setText("+ New");
        button_new_akun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_new_akunActionPerformed(evt);
            }
        });

        button_delete_akun.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_akun.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete_akun.setText("Delete");
        button_delete_akun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_akunActionPerformed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel2.setText("Daftar Akun");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_jumlah_akun)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 83, Short.MAX_VALUE)
                        .addComponent(button_new_akun)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_edit_akun)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_delete_akun)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export_data_akun))
                    .addComponent(jScrollPane3))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_jumlah_akun, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(button_export_data_akun, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_delete_akun, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(button_new_akun, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_edit_akun, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel_search_karyawan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_jumlah_data_laporan_keuangan)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_jumlah_pengeluaran_laporan_keuangan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_import)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_new_pengeluaran)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_edit_pengeluaran)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_delete_pengeluaran)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export_data_pengeluaran))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_jumlah_data)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_jumlah_pengeluaran)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel_search_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_export_data_pengeluaran, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete_pengeluaran, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_jumlah_data_laporan_keuangan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_jumlah_pengeluaran_laporan_keuangan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(button_new_pengeluaran, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_import, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(button_edit_pengeluaran, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_jumlah_data, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_jumlah_pengeluaran, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
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

    private void txt_keywordsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_keywordsKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_keywordsKeyPressed

    private void button_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refreshActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_button_refreshActionPerformed

    private void txt_jumlah1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_jumlah1KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_jumlah1KeyPressed

    private void txt_jumlah2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_jumlah2KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_jumlah2KeyPressed

    private void button_edit_pengeluaranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_pengeluaranActionPerformed
        // TODO add your handling code here:
        int x = tabel_Biaya.getSelectedRow();
        if (x > -1) {
            String kode = tabel_Biaya.getValueAt(x, 0).toString();
            JDialog_NewEdit_Biaya dialog = new JDialog_NewEdit_Biaya(new javax.swing.JFrame(), true, "edit", kode);
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
        DefaultTableModel model = (DefaultTableModel) tabel_Biaya.getModel();
        ExportToExcel.writeToExcel(model, jPanel2);
    }//GEN-LAST:event_button_export_data_pengeluaranActionPerformed

    private void button_new_pengeluaranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_new_pengeluaranActionPerformed
        // TODO add your handling code here:
        JDialog_NewEdit_Biaya dialog = new JDialog_NewEdit_Biaya(new javax.swing.JFrame(), true, "new", null);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        refreshTable();
    }//GEN-LAST:event_button_new_pengeluaranActionPerformed

    private void button_delete_pengeluaranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_pengeluaranActionPerformed
        // TODO add your handling code here:
        JDialog_Delete_Biaya dialog = new JDialog_Delete_Biaya(new javax.swing.JFrame(), true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        refreshTable();
    }//GEN-LAST:event_button_delete_pengeluaranActionPerformed

    private void button_importActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_importActionPerformed
        // TODO add your handling code here:
        try {
            // TODO add your handling code here:
            ImportFromCSV();
        } catch (ParseException ex) {
            Logger.getLogger(JPanel_Biaya.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_importActionPerformed

    private void button_delete_akunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_akunActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_button_delete_akunActionPerformed

    private void button_new_akunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_new_akunActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_button_new_akunActionPerformed

    private void button_export_data_akunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_data_akunActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Tabel_Akun.getModel();
        ExportToExcel.writeToExcel(model, jPanel2);
    }//GEN-LAST:event_button_export_data_akunActionPerformed

    private void button_edit_akunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_akunActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_button_edit_akunActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_Kategori;
    private com.toedter.calendar.JDateChooser Date1;
    private com.toedter.calendar.JDateChooser Date2;
    private javax.swing.JTable Tabel_Akun;
    public static javax.swing.JButton button_delete_akun;
    public static javax.swing.JButton button_delete_pengeluaran;
    public static javax.swing.JButton button_edit_akun;
    public static javax.swing.JButton button_edit_pengeluaran;
    private javax.swing.JButton button_export_data_akun;
    private javax.swing.JButton button_export_data_pengeluaran;
    public static javax.swing.JButton button_import;
    public static javax.swing.JButton button_new_akun;
    public static javax.swing.JButton button_new_pengeluaran;
    public static javax.swing.JButton button_refresh;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel_search_karyawan;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel label_jumlah_akun;
    private javax.swing.JLabel label_jumlah_data;
    private javax.swing.JLabel label_jumlah_data_laporan_keuangan;
    private javax.swing.JLabel label_jumlah_pengeluaran;
    private javax.swing.JLabel label_jumlah_pengeluaran_laporan_keuangan;
    private javax.swing.JTable tabel_Biaya;
    private javax.swing.JTable tabel_Laporan_Keuangan;
    private javax.swing.JTextField txt_jumlah1;
    private javax.swing.JTextField txt_jumlah2;
    private javax.swing.JTextField txt_keywords;
    // End of variables declaration//GEN-END:variables
}
