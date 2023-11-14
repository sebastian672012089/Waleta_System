package waleta_system.BahanBaku;

import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;
import waleta_system.Class.ExportToExcel;

public class JPanel_BahanBakuKeluar extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_BahanBakuKeluar() {
        initComponents();
        Table_bahan_baku_keluar.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && Table_bahan_baku_keluar.getSelectedRow() != -1) {
                    int i = Table_bahan_baku_keluar.getSelectedRow();
                    String kode = Table_bahan_baku_keluar.getValueAt(i, 0).toString();
                    refresh_TabelDetail(kode);
                }
            }
        });
    }

    public void init() {
        refreshTable();
    }

    public void refreshTable() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_bahan_baku_keluar.getModel();
            model.setRowCount(0);
            
            String jenis_pengeluaran = ComboBox_jenis.getSelectedItem().toString();
            if ("All".equals(jenis_pengeluaran)) {
                jenis_pengeluaran = "";
            }
            String filter_tanggal = "";
            if (Date_filter1.getDate() != null && Date_filter2.getDate() != null) {
                filter_tanggal = " AND `tgl_keluar` BETWEEN '" + dateFormat.format(Date_filter1.getDate()) + "' AND '" + dateFormat.format(Date_filter2.getDate()) + "'";

            }
            sql = "SELECT `tb_bahan_baku_keluar1`.`kode_pengeluaran`, `jenis_pengeluaran`, `tgl_keluar`, `customer_baku`, `tb_customer_baku`.`nama_customer`, `keterangan`, SUM(`total_keping_keluar`) AS 'tot_kpg', SUM(`total_berat_keluar`) AS 'tot_gram' "
                    + "FROM `tb_bahan_baku_keluar1` "
                    + "LEFT JOIN `tb_customer_baku` ON `tb_bahan_baku_keluar1`.`customer_baku` = `tb_customer_baku`.`kode_cust`"
                    + "LEFT JOIN `tb_bahan_baku_keluar` ON `tb_bahan_baku_keluar1`.`kode_pengeluaran` = `tb_bahan_baku_keluar`.`kode_pengeluaran`"
                    + "WHERE  `tb_bahan_baku_keluar1`.`kode_pengeluaran` LIKE '%" + txt_search_kode.getText() + "%' "
                    + "AND `nama_customer` LIKE '%" + txt_search_customer.getText() + "%' "
                    + "AND `jenis_pengeluaran` LIKE '%" + jenis_pengeluaran + "%' "
                    + filter_tanggal
                    + "GROUP BY `kode_pengeluaran` ORDER BY `tgl_keluar` DESC";

            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[8];
            while (rs.next()) {
                row[0] = rs.getString("kode_pengeluaran");
                row[1] = rs.getString("jenis_pengeluaran");
                row[2] = rs.getDate("tgl_keluar");
                row[3] = rs.getString("customer_baku");
                row[4] = rs.getString("nama_customer");
                row[5] = rs.getString("keterangan");
                row[6] = rs.getInt("tot_kpg");
                row[7] = rs.getInt("tot_gram");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_bahan_baku_keluar);
            int rowData = Table_bahan_baku_keluar.getRowCount();
            label_total_pengeluaran.setText(Integer.toString(rowData));
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_BahanBakuKeluar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refresh_TabelDetail(String kode) {
        try {
            DefaultTableModel model = (DefaultTableModel) Tabel_detail.getModel();
            model.setRowCount(0);
            int total_kpg = 0, total_gram = 0;
            sql = "SELECT `kode`, `no_kartu_waleta`, `kode_grade`, `total_keping_keluar`, `total_berat_keluar`, `kode_pengeluaran` "
                    + "FROM `tb_bahan_baku_keluar` WHERE `kode_pengeluaran` = '" + kode + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[5];
            while (rs.next()) {
                row[0] = rs.getString("kode_pengeluaran");
                row[1] = rs.getString("no_kartu_waleta");
                row[2] = rs.getString("kode_grade");
                row[3] = rs.getInt("total_keping_keluar");
                row[4] = rs.getInt("total_berat_keluar");
                total_kpg = total_kpg + rs.getInt("total_keping_keluar");
                total_gram = total_gram + rs.getInt("total_berat_keluar");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Tabel_detail);

            TableAlignment.setHorizontalAlignment(JLabel.CENTER);
            for (int i = 0; i < Tabel_detail.getColumnCount(); i++) {
                Tabel_detail.getColumnModel().getColumn(i).setCellRenderer(TableAlignment);
            }

            int rowData = Tabel_detail.getRowCount();
            label_total_detail.setText(Integer.toString(rowData));
            label_total_keping.setText(Integer.toString(total_kpg));
            label_total_gram.setText(Integer.toString(total_gram));
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_BahanBakuKeluar.class.getName()).log(Level.SEVERE, null, ex);
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

        jPanel_Bahan_Baku_Keluar = new javax.swing.JPanel();
        jPanel_search_baku_keluar = new javax.swing.JPanel();
        txt_search_kode = new javax.swing.JTextField();
        button_search_baku_keluar = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        button_export_BahanBakuKeluar = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        txt_search_customer = new javax.swing.JTextField();
        ComboBox_jenis = new javax.swing.JComboBox<>();
        jLabel19 = new javax.swing.JLabel();
        Date_filter1 = new com.toedter.calendar.JDateChooser();
        Date_filter2 = new com.toedter.calendar.JDateChooser();
        jScrollPane7 = new javax.swing.JScrollPane();
        Table_bahan_baku_keluar = new javax.swing.JTable();
        jLabel20 = new javax.swing.JLabel();
        label_total_pengeluaran = new javax.swing.JLabel();
        button_insert_bahan_baku_keluar = new javax.swing.JButton();
        button_delete_bahan_baku_keluar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        Tabel_detail = new javax.swing.JTable();
        jLabel21 = new javax.swing.JLabel();
        label_total_detail = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        label_total_keping = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        label_total_gram = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel_Bahan_Baku_Keluar.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_Bahan_Baku_Keluar.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Bahan Baku Keluar", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N
        jPanel_Bahan_Baku_Keluar.setPreferredSize(new java.awt.Dimension(1366, 700));

        jPanel_search_baku_keluar.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_search_baku_keluar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        txt_search_kode.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_kode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_kodeKeyPressed(evt);
            }
        });

        button_search_baku_keluar.setBackground(new java.awt.Color(255, 255, 255));
        button_search_baku_keluar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_baku_keluar.setText("Search");
        button_search_baku_keluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_baku_keluarActionPerformed(evt);
            }
        });

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel16.setText("Search Kode :");

        button_export_BahanBakuKeluar.setBackground(new java.awt.Color(255, 255, 255));
        button_export_BahanBakuKeluar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_BahanBakuKeluar.setText("Export To Excel");
        button_export_BahanBakuKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_BahanBakuKeluarActionPerformed(evt);
            }
        });

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel17.setText("Search Customer :");

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel18.setText("Jenis Pengeluaran :");

        txt_search_customer.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_customer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_customerKeyPressed(evt);
            }
        });

        ComboBox_jenis.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_jenis.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Penjualan", "Sampel" }));

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel19.setText("Tanggal Keluar :");

        Date_filter1.setBackground(new java.awt.Color(255, 255, 255));
        Date_filter1.setDateFormatString("dd MMMM yyyy");
        Date_filter1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_filter2.setBackground(new java.awt.Color(255, 255, 255));
        Date_filter2.setDateFormatString("dd MMMM yyyy");
        Date_filter2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        javax.swing.GroupLayout jPanel_search_baku_keluarLayout = new javax.swing.GroupLayout(jPanel_search_baku_keluar);
        jPanel_search_baku_keluar.setLayout(jPanel_search_baku_keluarLayout);
        jPanel_search_baku_keluarLayout.setHorizontalGroup(
            jPanel_search_baku_keluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_baku_keluarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_search_kode, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_search_customer, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ComboBox_jenis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date_filter1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date_filter2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_search_baku_keluar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 156, Short.MAX_VALUE)
                .addComponent(button_export_BahanBakuKeluar)
                .addContainerGap())
        );
        jPanel_search_baku_keluarLayout.setVerticalGroup(
            jPanel_search_baku_keluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_search_baku_keluarLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel_search_baku_keluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_search_baku_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_BahanBakuKeluar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_filter2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_filter1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_jenis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_customer, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_kode, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        Table_bahan_baku_keluar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_bahan_baku_keluar.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Keluar", "Jenis Pengeluaran", "Tanggal Keluar", "Kode Customer", "Customer", "Keterangan", "Total Keping", "Total Berat (Gram)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_bahan_baku_keluar.getTableHeader().setReorderingAllowed(false);
        jScrollPane7.setViewportView(Table_bahan_baku_keluar);

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel20.setText("Total Pengeluaran :");

        label_total_pengeluaran.setBackground(new java.awt.Color(255, 255, 255));
        label_total_pengeluaran.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        label_total_pengeluaran.setText("TOTAL");

        button_insert_bahan_baku_keluar.setBackground(new java.awt.Color(255, 255, 255));
        button_insert_bahan_baku_keluar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_insert_bahan_baku_keluar.setText("insert");
        button_insert_bahan_baku_keluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_insert_bahan_baku_keluarActionPerformed(evt);
            }
        });

        button_delete_bahan_baku_keluar.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_bahan_baku_keluar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete_bahan_baku_keluar.setText("Delete");
        button_delete_bahan_baku_keluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_bahan_baku_keluarActionPerformed(evt);
            }
        });

        Tabel_detail.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Tabel_detail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Keluar", "No Kartu", "Grade", "Keping", "Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Tabel_detail.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Tabel_detail);

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel21.setText("Total Data :");

        label_total_detail.setBackground(new java.awt.Color(255, 255, 255));
        label_total_detail.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        label_total_detail.setText("TOTAL");

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel22.setText("Total Keping :");

        label_total_keping.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        label_total_keping.setText("TOTAL");

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel23.setText("Total Gram :");

        label_total_gram.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        label_total_gram.setText("TOTAL");

        javax.swing.GroupLayout jPanel_Bahan_Baku_KeluarLayout = new javax.swing.GroupLayout(jPanel_Bahan_Baku_Keluar);
        jPanel_Bahan_Baku_Keluar.setLayout(jPanel_Bahan_Baku_KeluarLayout);
        jPanel_Bahan_Baku_KeluarLayout.setHorizontalGroup(
            jPanel_Bahan_Baku_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Bahan_Baku_KeluarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Bahan_Baku_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel_search_baku_keluar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel_Bahan_Baku_KeluarLayout.createSequentialGroup()
                        .addGroup(jPanel_Bahan_Baku_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_Bahan_Baku_KeluarLayout.createSequentialGroup()
                                .addComponent(button_insert_bahan_baku_keluar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_delete_bahan_baku_keluar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_pengeluaran))
                            .addComponent(jScrollPane7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_Bahan_Baku_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 473, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel_Bahan_Baku_KeluarLayout.createSequentialGroup()
                                .addComponent(jLabel22)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_keping))
                            .addGroup(jPanel_Bahan_Baku_KeluarLayout.createSequentialGroup()
                                .addComponent(jLabel23)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram))
                            .addGroup(jPanel_Bahan_Baku_KeluarLayout.createSequentialGroup()
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_detail)))))
                .addContainerGap())
        );
        jPanel_Bahan_Baku_KeluarLayout.setVerticalGroup(
            jPanel_Bahan_Baku_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Bahan_Baku_KeluarLayout.createSequentialGroup()
                .addComponent(jPanel_search_baku_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Bahan_Baku_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 589, Short.MAX_VALUE)
                    .addGroup(jPanel_Bahan_Baku_KeluarLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_Bahan_Baku_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_detail)
                            .addComponent(jLabel21))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_Bahan_Baku_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_keping)
                            .addComponent(jLabel22))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_Bahan_Baku_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_gram)
                            .addComponent(jLabel23))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Bahan_Baku_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_total_pengeluaran)
                    .addComponent(jLabel20)
                    .addComponent(button_insert_bahan_baku_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete_bahan_baku_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel_Bahan_Baku_Keluar, javax.swing.GroupLayout.DEFAULT_SIZE, 1364, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_Bahan_Baku_Keluar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txt_search_kodeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_kodeKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_kodeKeyPressed

    private void button_search_baku_keluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_baku_keluarActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_button_search_baku_keluarActionPerformed

    private void button_export_BahanBakuKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_BahanBakuKeluarActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_bahan_baku_keluar.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_export_BahanBakuKeluarActionPerformed

    private void button_insert_bahan_baku_keluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_insert_bahan_baku_keluarActionPerformed
        // TODO add your handling code here:
        JDialog_PengeluaranBahanBaku dialog = new JDialog_PengeluaranBahanBaku(new javax.swing.JFrame(), true, "new", null);
        dialog.setEnabled(true);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        refreshTable();
    }//GEN-LAST:event_button_insert_bahan_baku_keluarActionPerformed

    private void button_delete_bahan_baku_keluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_bahan_baku_keluarActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_bahan_baku_keluar.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Please Select Row Data that you want to Delete !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    String Query = "DELETE FROM `tb_bahan_baku_keluar1` WHERE `tb_bahan_baku_keluar1`.`kode_pengeluaran` = '" + Table_bahan_baku_keluar.getValueAt(j, 0).toString() + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                        JOptionPane.showMessageDialog(this, "data deleted Successfully");
                        refreshTable();
                    } else {
                        JOptionPane.showMessageDialog(this, "data not DELETED");
                    }
                }
            }
        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_BahanBakuKeluar.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_delete_bahan_baku_keluarActionPerformed

    private void txt_search_customerKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_customerKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_search_customerKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_jenis;
    private com.toedter.calendar.JDateChooser Date_filter1;
    private com.toedter.calendar.JDateChooser Date_filter2;
    private javax.swing.JTable Tabel_detail;
    public static javax.swing.JTable Table_bahan_baku_keluar;
    public javax.swing.JButton button_delete_bahan_baku_keluar;
    public static javax.swing.JButton button_export_BahanBakuKeluar;
    public javax.swing.JButton button_insert_bahan_baku_keluar;
    private javax.swing.JButton button_search_baku_keluar;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JPanel jPanel_Bahan_Baku_Keluar;
    private javax.swing.JPanel jPanel_search_baku_keluar;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JLabel label_total_detail;
    private javax.swing.JLabel label_total_gram;
    private javax.swing.JLabel label_total_keping;
    private javax.swing.JLabel label_total_pengeluaran;
    private javax.swing.JTextField txt_search_customer;
    private javax.swing.JTextField txt_search_kode;
    // End of variables declaration//GEN-END:variables
}
