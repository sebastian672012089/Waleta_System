package waleta_system.Finance;

import waleta_system.Class.Utility;

import waleta_system.BahanBaku.*;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
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
import waleta_system.Class.ExportToExcel;
import waleta_system.MainForm;

public class JPanel_Harga_Kartu_Campuran extends javax.swing.JPanel {

    
    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_Harga_Kartu_Campuran() {
        initComponents();
    }

    public void init() {
        try {
            
            
            refreshTable();
        } catch (Exception ex) {
            Logger.getLogger(JPanel_GradeBahanBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
        Table_kartu_campuran.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && Table_kartu_campuran.getSelectedRow() != -1) {
                    int i = Table_kartu_campuran.getSelectedRow();
                    String kode = Table_kartu_campuran.getValueAt(i, 0).toString();
                    refresh_TabelDetail(kode);
                    refresh_TabelHasil(kode);
                }
            }
        });
    }

    public void refreshTable() {
        try {
            decimalFormat.setMaximumFractionDigits(5);
            DefaultTableModel model = (DefaultTableModel) Table_kartu_campuran.getModel();
            model.setRowCount(0);
            if (Date_filter1.getDate() != null && Date_filter2.getDate() != null) {
                sql = "SELECT CMP.`kode_kartu_cmp`, `tanggal`, `catatan`, SUM(DETAIL.`keping`) AS 'kpg_asal', SUM(DETAIL.`gram`) AS 'gram_asal', "
                        + "ROUND(SUM(DETAIL.`gram` * A.`harga_bahanbaku`) / SUM(DETAIL.`gram`), 5) AS 'harga_awal' "
                        + "FROM `tb_kartu_cmp` AS CMP\n"
                        + "LEFT JOIN `tb_kartu_cmp_detail` AS DETAIL ON CMP.`kode_kartu_cmp` = DETAIL.`kode_kartu_cmp`\n"
                        + "LEFT JOIN `tb_grading_bahan_baku` AS A ON DETAIL.`no_grading` = A.`no_grading`\n"
                        + "WHERE CMP.`kode_kartu_cmp` LIKE '%" + txt_search_kode_kartu.getText() + "%' "
                        + "AND (`tanggal` BETWEEN '" + dateFormat.format(Date_filter1.getDate()) + "' AND '" + dateFormat.format(Date_filter2.getDate()) + "')"
                        + "GROUP BY CMP.`kode_kartu_cmp`";
            } else {
                sql = "SELECT CMP.`kode_kartu_cmp`, `tanggal`, `catatan`, SUM(DETAIL.`keping`) AS 'kpg_asal', SUM(DETAIL.`gram`) AS 'gram_asal', "
                        + "ROUND(SUM(DETAIL.`gram` * A.`harga_bahanbaku`) / SUM(DETAIL.`gram`), 5) AS 'harga_awal' "
                        + "FROM `tb_kartu_cmp` AS CMP\n"
                        + "LEFT JOIN `tb_kartu_cmp_detail` AS DETAIL ON CMP.`kode_kartu_cmp` = DETAIL.`kode_kartu_cmp`\n"
                        + "LEFT JOIN `tb_grading_bahan_baku` AS A ON DETAIL.`no_grading` = A.`no_grading`\n"
                        + "WHERE CMP.`kode_kartu_cmp` LIKE '%" + txt_search_kode_kartu.getText() + "%'"
                        + "GROUP BY CMP.`kode_kartu_cmp`";
            }
            PreparedStatement pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            Object[] row = new Object[12];
            while (rs.next()) {
                row[0] = rs.getString("kode_kartu_cmp");
                row[1] = rs.getDate("tanggal");
                row[2] = rs.getString("catatan");
                String query = "SELECT `kode_kartu_cmp`, SUM(`tb_grading_bahan_baku`.`jumlah_keping`) AS 'kpg_akhir', SUM(`tb_grading_bahan_baku`.`total_berat`) AS 'gram_akhir', "
                        + "ROUND(SUM(`tb_grading_bahan_baku`.`total_berat` * `tb_grading_bahan_baku`.`harga_bahanbaku`) / SUM(`tb_grading_bahan_baku`.`total_berat`), 5) AS 'total_harga'\n"
                        + "FROM `tb_kartu_cmp` \n"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_kartu_cmp`.`kode_kartu_cmp` = `tb_grading_bahan_baku`.`no_kartu_waleta`\n"
                        + "WHERE `kode_kartu_cmp` = '" + rs.getString("kode_kartu_cmp") + "'"
                        + "GROUP BY `kode_kartu_cmp`";
                ResultSet result = Utility.db.getStatement().executeQuery(query);
                if (result.next()) {
                    row[3] = rs.getDouble("harga_awal");
                    row[4] = result.getDouble("total_harga");
                    row[5] = decimalFormat.format(rs.getDouble("harga_awal") - result.getDouble("total_harga"));
                    row[6] = rs.getInt("kpg_asal");
                    row[7] = result.getInt("kpg_akhir");
                    row[8] = decimalFormat.format(rs.getInt("kpg_asal") - result.getDouble("kpg_akhir"));
                    row[9] = rs.getInt("gram_asal");
                    row[10] = result.getInt("gram_akhir");
                    row[11] = decimalFormat.format(rs.getInt("gram_asal") - result.getDouble("gram_akhir"));
                }
                model.addRow(row);
            }
            int rowData = Table_kartu_campuran.getRowCount();
            label_total_pengeluaran.setText(Integer.toString(rowData));
            ColumnsAutoSizer.sizeColumnsToFit(Table_kartu_campuran);

            TableAlignment.setHorizontalAlignment(JLabel.CENTER);
            for (int i = 0; i < Table_kartu_campuran.getColumnCount(); i++) {
                Table_kartu_campuran.getColumnModel().getColumn(i).setCellRenderer(TableAlignment);
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Harga_Kartu_Campuran.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refresh_TabelDetail(String kode) {
        try {
            decimalFormat.setMaximumFractionDigits(5);
            DefaultTableModel model = (DefaultTableModel) Tabel_detail_asal.getModel();
            model.setRowCount(0);
            int total_kpg = 0, total_gram = 0;
            double total_nilai = 0;
            sql = "SELECT `kode_kartu_cmp`, `tb_grading_bahan_baku`.`no_kartu_waleta`, `tb_grading_bahan_baku`.`kode_grade`, `keping`, `gram`, `tb_grading_bahan_baku`.`harga_bahanbaku`  \n"
                    + "FROM `tb_kartu_cmp_detail` JOIN `tb_grading_bahan_baku` ON `tb_kartu_cmp_detail`.`no_grading` =  `tb_grading_bahan_baku`.`no_grading`\n"
                    + "WHERE `kode_kartu_cmp` = '" + kode + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[7];
            while (rs.next()) {
                row[0] = rs.getString("kode_kartu_cmp");
                row[1] = rs.getString("no_kartu_waleta");
                row[2] = rs.getString("kode_grade");
                row[3] = rs.getInt("keping");
                row[4] = rs.getInt("gram");
                row[5] = decimalFormat.format(rs.getDouble("harga_bahanbaku"));
                row[6] = decimalFormat.format(rs.getDouble("gram") * rs.getDouble("harga_bahanbaku"));
                total_kpg = total_kpg + rs.getInt("keping");
                total_gram = total_gram + rs.getInt("gram");
                total_nilai = total_nilai + (rs.getDouble("gram") * rs.getDouble("harga_bahanbaku"));
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Tabel_detail_asal);

            TableAlignment.setHorizontalAlignment(JLabel.CENTER);
            for (int i = 0; i < Tabel_detail_asal.getColumnCount(); i++) {
                Tabel_detail_asal.getColumnModel().getColumn(i).setCellRenderer(TableAlignment);
            }

            int rowData = Tabel_detail_asal.getRowCount();
            label_total_detail.setText(Integer.toString(rowData));
            label_total_keping.setText(Integer.toString(total_kpg));
            label_total_gram.setText(Integer.toString(total_gram));
            label_total_harga.setText(decimalFormat.format(total_nilai));
            label_HargaPerGr.setText(decimalFormat.format(total_nilai / total_gram));
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Harga_Kartu_Campuran.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refresh_TabelHasil(String kode) {
        try {
            decimalFormat.setMaximumFractionDigits(5);
            DefaultTableModel model = (DefaultTableModel) Tabel_hasil.getModel();
            model.setRowCount(0);
            sql = "SELECT `no_grading`, `no_kartu_waleta`, `kode_grade`, `jumlah_keping`, `total_berat`, `harga_bahanbaku` "
                    + "FROM `tb_grading_bahan_baku`\n"
                    + "WHERE `no_kartu_waleta` = '" + kode + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[7];
            while (rs.next()) {
                row[0] = rs.getString("no_grading");
                row[1] = rs.getString("no_kartu_waleta");
                row[2] = rs.getString("kode_grade");
                row[3] = rs.getInt("jumlah_keping");
                row[4] = rs.getInt("total_berat");
                row[5] = decimalFormat.format(rs.getDouble("harga_bahanbaku"));
                row[6] = decimalFormat.format(rs.getDouble("total_berat") * rs.getDouble("harga_bahanbaku"));
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Tabel_hasil);

            TableAlignment.setHorizontalAlignment(JLabel.CENTER);
            for (int i = 0; i < Tabel_hasil.getColumnCount(); i++) {
                Tabel_hasil.getColumnModel().getColumn(i).setCellRenderer(TableAlignment);
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Harga_Kartu_Campuran.class.getName()).log(Level.SEVERE, null, ex);
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
        txt_search_kode_kartu = new javax.swing.JTextField();
        button_search_baku_keluar = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        button_export_BahanBakuKeluar = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        Date_filter1 = new com.toedter.calendar.JDateChooser();
        Date_filter2 = new com.toedter.calendar.JDateChooser();
        jScrollPane7 = new javax.swing.JScrollPane();
        Table_kartu_campuran = new javax.swing.JTable();
        jLabel20 = new javax.swing.JLabel();
        label_total_pengeluaran = new javax.swing.JLabel();
        button_set_harga = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        label_HargaPerGr = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Tabel_detail_asal = new javax.swing.JTable();
        jLabel27 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        label_total_detail = new javax.swing.JLabel();
        label_total_gram = new javax.swing.JLabel();
        label_total_harga = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Tabel_hasil = new javax.swing.JTable();
        jLabel25 = new javax.swing.JLabel();
        label_total_keping = new javax.swing.JLabel();
        button_export_asalKartuCMP = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel_Bahan_Baku_Keluar.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_Bahan_Baku_Keluar.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Kartu Campuran", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N
        jPanel_Bahan_Baku_Keluar.setPreferredSize(new java.awt.Dimension(1366, 700));

        jPanel_search_baku_keluar.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_search_baku_keluar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        txt_search_kode_kartu.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_kode_kartu.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_kode_kartuKeyPressed(evt);
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
        jLabel16.setText("Kode Kartu Campuran :");

        button_export_BahanBakuKeluar.setBackground(new java.awt.Color(255, 255, 255));
        button_export_BahanBakuKeluar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_BahanBakuKeluar.setText("Export To Excel");
        button_export_BahanBakuKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_BahanBakuKeluarActionPerformed(evt);
            }
        });

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel19.setText("Tanggal Kartu CMP :");

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
                .addComponent(txt_search_kode_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date_filter1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date_filter2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_search_baku_keluar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 485, Short.MAX_VALUE)
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
                    .addComponent(txt_search_kode_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        Table_kartu_campuran.setAutoCreateRowSorter(true);
        Table_kartu_campuran.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_kartu_campuran.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Kartu Campuran", "Tanggal Kartu", "Catatan Kartu", "Harga asal", "Harga Grading", "Cek", "Kpg Asal", "Kpg Hasil", "Cek Kpg", "Gr Asal", "Gr Hasil", "Cek Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        Table_kartu_campuran.getTableHeader().setReorderingAllowed(false);
        jScrollPane7.setViewportView(Table_kartu_campuran);

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel20.setText("Total Kartu Campuran :");

        label_total_pengeluaran.setBackground(new java.awt.Color(255, 255, 255));
        label_total_pengeluaran.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        label_total_pengeluaran.setText("TOTAL");

        button_set_harga.setBackground(new java.awt.Color(255, 255, 255));
        button_set_harga.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_set_harga.setText("Set Harga");
        button_set_harga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_set_hargaActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel26.setText("Hasil Grade CMP");

        label_HargaPerGr.setBackground(new java.awt.Color(255, 255, 255));
        label_HargaPerGr.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        label_HargaPerGr.setText("TOTAL");

        Tabel_detail_asal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Tabel_detail_asal.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Kartu CMP", "No Kartu Waleta", "Grade", "Keping", "Gram", "Harga (Rp.)", "Total (Rp.)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Tabel_detail_asal.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Tabel_detail_asal);

        jLabel27.setBackground(new java.awt.Color(255, 255, 255));
        jLabel27.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel27.setText("Asal Kartu CMP");

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel23.setText("Total Gram :");

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel21.setText("Total Data :");

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel24.setText("Total Harga :");

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel22.setText("Total Keping :");

        label_total_detail.setBackground(new java.awt.Color(255, 255, 255));
        label_total_detail.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        label_total_detail.setText("TOTAL");

        label_total_gram.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        label_total_gram.setText("TOTAL");

        label_total_harga.setBackground(new java.awt.Color(255, 255, 255));
        label_total_harga.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        label_total_harga.setText("TOTAL");

        Tabel_hasil.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Tabel_hasil.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "No Kartu", "Grade", "Kpg", "Gram", "Harga", "Total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(Tabel_hasil);

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel25.setText("Total Harga /Gr Keseluruhan :");

        label_total_keping.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        label_total_keping.setText("TOTAL");

        button_export_asalKartuCMP.setBackground(new java.awt.Color(255, 255, 255));
        button_export_asalKartuCMP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_asalKartuCMP.setText("Export To Excel");
        button_export_asalKartuCMP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_asalKartuCMPActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 602, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel22)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_keping))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel23)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_detail))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel24)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_harga))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel25)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_HargaPerGr))
                            .addComponent(jLabel26))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel27)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_export_asalKartuCMP)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel26)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(button_export_asalKartuCMP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_total_detail)
                    .addComponent(jLabel21))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_total_keping)
                    .addComponent(jLabel22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_total_gram)
                    .addComponent(jLabel23))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_total_harga)
                    .addComponent(jLabel24))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_HargaPerGr)
                    .addComponent(jLabel25))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel_Bahan_Baku_KeluarLayout = new javax.swing.GroupLayout(jPanel_Bahan_Baku_Keluar);
        jPanel_Bahan_Baku_Keluar.setLayout(jPanel_Bahan_Baku_KeluarLayout);
        jPanel_Bahan_Baku_KeluarLayout.setHorizontalGroup(
            jPanel_Bahan_Baku_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Bahan_Baku_KeluarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Bahan_Baku_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel_search_baku_keluar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel_Bahan_Baku_KeluarLayout.createSequentialGroup()
                        .addGroup(jPanel_Bahan_Baku_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_Bahan_Baku_KeluarLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_pengeluaran)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_set_harga))
                            .addComponent(jScrollPane7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel_Bahan_Baku_KeluarLayout.setVerticalGroup(
            jPanel_Bahan_Baku_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Bahan_Baku_KeluarLayout.createSequentialGroup()
                .addComponent(jPanel_search_baku_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Bahan_Baku_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_Bahan_Baku_KeluarLayout.createSequentialGroup()
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 587, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_Bahan_Baku_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_pengeluaran)
                            .addComponent(jLabel20)
                            .addComponent(button_set_harga))
                        .addGap(11, 11, 11))
                    .addGroup(jPanel_Bahan_Baku_KeluarLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
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
            .addComponent(jPanel_Bahan_Baku_Keluar, javax.swing.GroupLayout.DEFAULT_SIZE, 701, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txt_search_kode_kartuKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_kode_kartuKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_kode_kartuKeyPressed

    private void button_search_baku_keluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_baku_keluarActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_button_search_baku_keluarActionPerformed

    private void button_export_BahanBakuKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_BahanBakuKeluarActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_kartu_campuran.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_export_BahanBakuKeluarActionPerformed

    private void button_set_hargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_set_hargaActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_kartu_campuran.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Pilih Kartu terlebih dahulu");
            } else {
                double harga = (double) Table_kartu_campuran.getValueAt(j, 3);
                String no_cmp = Table_kartu_campuran.getValueAt(j, 0).toString();
                int dialogResult = JOptionPane.showConfirmDialog(this, "Set harga kartu " + no_cmp + " menjadi " + harga + "?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    String Query = "UPDATE `tb_grading_bahan_baku` SET "
                            + "`harga_bahanbaku` = " + harga
                            + " WHERE `no_kartu_waleta` = '" + no_cmp + "'";
                    System.out.println(Query);
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(Query)) > 0) {
                        JOptionPane.showMessageDialog(this, "Data Saved !");
                        refreshTable();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed !");
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JPanel_Harga_Kartu_Campuran.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_set_hargaActionPerformed

    private void button_export_asalKartuCMPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_asalKartuCMPActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Tabel_detail_asal.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_export_asalKartuCMPActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser Date_filter1;
    private com.toedter.calendar.JDateChooser Date_filter2;
    private javax.swing.JTable Tabel_detail_asal;
    private javax.swing.JTable Tabel_hasil;
    public static javax.swing.JTable Table_kartu_campuran;
    public static javax.swing.JButton button_export_BahanBakuKeluar;
    public static javax.swing.JButton button_export_asalKartuCMP;
    private javax.swing.JButton button_search_baku_keluar;
    private javax.swing.JButton button_set_harga;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel_Bahan_Baku_Keluar;
    private javax.swing.JPanel jPanel_search_baku_keluar;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JLabel label_HargaPerGr;
    private javax.swing.JLabel label_total_detail;
    private javax.swing.JLabel label_total_gram;
    private javax.swing.JLabel label_total_harga;
    private javax.swing.JLabel label_total_keping;
    private javax.swing.JLabel label_total_pengeluaran;
    private javax.swing.JTextField txt_search_kode_kartu;
    // End of variables declaration//GEN-END:variables
}
