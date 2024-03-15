package waleta_system.Finance;

import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
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
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;
import waleta_system.Class.Utility;

public class JPanel_GajiCABUTO extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DecimalFormat decimalFormat = new DecimalFormat();
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_GajiCABUTO() {
        initComponents();
    }

    public void init() {
        try {
            decimalFormat = Utility.DecimalFormatUS(decimalFormat);
            table_penggajian_cabuto_rekap_harian.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && table_penggajian_cabuto_rekap_harian.getSelectedRow() != -1) {
                        int i = table_penggajian_cabuto_rekap_harian.getSelectedRow();
                        String id_order = table_penggajian_cabuto_rekap_harian.getValueAt(i, 1).toString();
                        label_order_id1.setText(id_order);
                        label_order_id2.setText(id_order);
                        Utility.db_cabuto.connect();
                        refreshTable_asal_lp(id_order);
                        refreshTable_evaluasi_hasil(id_order);
                    }
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(JPanel_GajiCABUTO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_penggajian_cabuto() {
        try {
            Utility.db_cabuto.connect();
            decimalFormat.setMaximumFractionDigits(2);
            double total_nilai_baku = 0, total_nilai_hasil = 0, total_selisih = 0;

            Map<String, String> ListID_karyawan = new HashMap<>();
            Map<String, Float> ListJumlahLP_karyawan = new HashMap<>();
            Map<String, Float> ListGramLP_karyawan = new HashMap<>();
            Map<String, Float> ListUpah_karyawan = new HashMap<>();

            DefaultTableModel model = (DefaultTableModel) table_penggajian_cabuto_rekap_harian.getModel();
            model.setRowCount(0);

            String filter_tanggal = "";
            if (DateFilter_Setor1.getDate() != null && DateFilter_Setor2.getDate() != null) {
                filter_tanggal = "AND DATE(`waktu_setor`) BETWEEN '" + dateFormat.format(DateFilter_Setor1.getDate()) + "' AND '" + dateFormat.format(DateFilter_Setor2.getDate()) + "'";
            }

            sql = "SELECT `waktu_setor`, `tb_order`.`id_order`, `tb_order`.`id_user`, `tb_user`.`nama_user`,\n"
                    + "data_lp.`jumlah_lp`, data_lp.`gram_lp`, data_lp.`nilai_lp`,\n"
                    + "data_hasil.`gram_hasil`, data_hasil.`nilai_hasil`\n"
                    + "FROM `tb_order` \n"
                    + "LEFT JOIN `tb_user` ON `tb_order`.`id_user` = `tb_user`.`id_user` \n"
                    + "LEFT JOIN (\n"
                    + "SELECT `id_order`, COUNT(`no_laporan_produksi`) AS 'jumlah_lp', SUM(`berat_basah`) AS 'gram_lp', SUM(`harga_baku`) AS 'nilai_lp' \n"
                    + "FROM `tb_lp` WHERE 1 GROUP BY `id_order`\n"
                    + ") data_lp ON `tb_order`.`id_order` = data_lp.`id_order`\n"
                    + "LEFT JOIN (\n"
                    + "SELECT `id_order`, SUM(`gram`) AS 'gram_hasil', SUM(`gram`*`harga`) AS 'nilai_hasil' FROM `tb_evaluasi` WHERE 1 GROUP BY `id_order`\n"
                    + ") data_hasil ON `tb_order`.`id_order` = data_hasil.`id_order`\n"
                    + "WHERE \n"
                    + "`tb_user`.`nama_user` LIKE '%" + txt_search_nama.getText() + "%'\n"
                    + filter_tanggal;
//                System.out.println(sql);
            PreparedStatement pst = Utility.db_cabuto.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            Object[] row = new Object[14];
            while (rs.next()) {
                row[0] = rs.getDate("waktu_setor");
                row[1] = rs.getString("id_order");
                row[2] = null;
                String qry = "SELECT `id_pegawai` FROM `tb_karyawan` WHERE `nama_pegawai` = '" + rs.getString("nama_user") + "' ORDER BY `tanggal_masuk` DESC LIMIT 1 \n";
                ResultSet result = Utility.db.getStatement().executeQuery(qry);
                if (result.next()) {
                    row[2] = result.getString("id_pegawai");
                    ListID_karyawan.put(rs.getString("nama_user"), result.getString("id_pegawai"));
                }
                row[3] = rs.getString("nama_user");
                row[4] = rs.getFloat("jumlah_lp");
                row[5] = rs.getFloat("gram_lp");
                row[6] = rs.getFloat("nilai_lp");
                row[7] = rs.getFloat("gram_hasil");
                row[8] = rs.getFloat("nilai_hasil");
                row[9] = rs.getFloat("gram_lp") - rs.getFloat("gram_hasil");
                row[10] = rs.getFloat("nilai_hasil") - rs.getFloat("nilai_lp");
                model.addRow(row);
                total_nilai_baku = total_nilai_baku + rs.getFloat("nilai_lp");
                total_nilai_hasil = total_nilai_hasil + rs.getFloat("nilai_hasil");
                total_selisih = total_selisih + (rs.getFloat("nilai_hasil") - rs.getFloat("nilai_lp"));

                ListJumlahLP_karyawan.put(rs.getString("nama_user"), ListJumlahLP_karyawan.getOrDefault(rs.getString("nama_user"), 0f) + rs.getFloat("jumlah_lp"));
                ListGramLP_karyawan.put(rs.getString("nama_user"), ListGramLP_karyawan.getOrDefault(rs.getString("nama_user"), 0f) + rs.getFloat("gram_lp"));
                ListUpah_karyawan.put(rs.getString("nama_user"), ListUpah_karyawan.getOrDefault(rs.getString("nama_user"), 0f) + (rs.getFloat("nilai_hasil") - rs.getFloat("nilai_lp")));
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_penggajian_cabuto_rekap_harian);

            decimalFormat.setMaximumFractionDigits(1);
            label_total_data.setText(decimalFormat.format(table_penggajian_cabuto_rekap_harian.getRowCount()));
            label_avg_persen_sh.setText("Rp. " + decimalFormat.format(0));
            label_total_nilai_baku.setText("Rp. " + decimalFormat.format(total_nilai_baku));
            label_total_nilai_hasil.setText("Rp. " + decimalFormat.format(total_nilai_hasil));
            label_total_selisih.setText("Rp. " + decimalFormat.format(total_selisih));

            DefaultTableModel model2 = (DefaultTableModel) table_penggajian_cabuto_rekap_karyawan.getModel();
            model2.setRowCount(0);
            Object[] row_tb2 = new Object[10];
            for (String key : ListID_karyawan.keySet()) {
                row_tb2[0] = ListID_karyawan.get(key);
                row_tb2[1] = key;
                row_tb2[2] = ListJumlahLP_karyawan.get(key);
                row_tb2[3] = ListGramLP_karyawan.get(key);
                row_tb2[4] = ListUpah_karyawan.get(key);
                model2.addRow(row_tb2);
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_penggajian_cabuto_rekap_karyawan);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_GajiCABUTO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_asal_lp(String id_order) {
        try {
            int total_gram_lp = 0;
            int total_harga_baku = 0;
            decimalFormat.setMaximumFractionDigits(0);
            DefaultTableModel model = (DefaultTableModel) Tabel_detail_LP_Asal.getModel();
            model.setRowCount(0);

            sql = "SELECT `tanggal_lp`, `no_laporan_produksi`, `no_kartu_waleta`, `kode_grade`, `jenis_bulu_lp`, `berat_basah`, `berat_kering`, `jumlah_keping`, `keping_upah`, `harga_baku` \n"
                    + "FROM `tb_lp` "
                    + "WHERE `id_order` = '" + id_order + "' \n";

//            System.out.println(sql);
            rs = Utility.db_cabuto.getStatement().executeQuery(sql);
            Object[] row = new Object[13];
            while (rs.next()) {
                row[0] = rs.getDate("tanggal_lp");
                row[1] = rs.getString("no_laporan_produksi");
                row[2] = rs.getString("kode_grade");
                row[3] = rs.getString("jenis_bulu_lp");
                row[4] = rs.getInt("jumlah_keping");
                row[5] = rs.getInt("berat_basah");
                row[6] = rs.getInt("harga_baku");
                model.addRow(row);
                total_gram_lp += rs.getInt("berat_basah");
                total_harga_baku += rs.getInt("harga_baku");
            }
            label_total_gram_lp_asal.setText(decimalFormat.format(total_gram_lp));
            label_total_nilai_lp_asal.setText("Rp. " + decimalFormat.format(total_harga_baku));

            ColumnsAutoSizer.sizeColumnsToFit(Tabel_detail_LP_Asal);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_GajiCABUTO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_evaluasi_hasil(String id_order) {
        try {
            int total_gram_hasil = 0;
            float total_harga_hasil = 0;
            decimalFormat.setMaximumFractionDigits(0);
            DefaultTableModel model = (DefaultTableModel) Tabel_detail_Evaluasi.getModel();
            model.setRowCount(0);

            sql = "SELECT `grade`, `keping`, `gram`, `harga`\n"
                    + "FROM `tb_evaluasi` WHERE `id_order` = '" + id_order + "' \n";

//            System.out.println(sql);
            rs = Utility.db_cabuto.getStatement().executeQuery(sql);
            Object[] row = new Object[13];
            while (rs.next()) {
                row[0] = rs.getString("grade");
                row[1] = rs.getInt("keping");
                row[2] = rs.getFloat("gram");
                row[3] = rs.getFloat("harga");
                row[4] = rs.getFloat("gram") * rs.getFloat("harga");
                model.addRow(row);
                total_gram_hasil += rs.getInt("gram");
                total_harga_hasil += (rs.getFloat("gram") * rs.getFloat("harga"));
            }
            label_total_gram_evaluasi.setText(decimalFormat.format(total_gram_hasil));
            label_total_nilai_evaluasi.setText("Rp. " + decimalFormat.format(total_harga_hasil));

            ColumnsAutoSizer.sizeColumnsToFit(Tabel_detail_Evaluasi);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_GajiCABUTO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        txt_search_nama = new javax.swing.JTextField();
        button_search = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        DateFilter_Setor1 = new com.toedter.calendar.JDateChooser();
        DateFilter_Setor2 = new com.toedter.calendar.JDateChooser();
        jLabel6 = new javax.swing.JLabel();
        label_total_data = new javax.swing.JLabel();
        button_export = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        label_avg_persen_sh = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        label_total_nilai_baku = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        label_total_nilai_hasil = new javax.swing.JLabel();
        label_total_selisih = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jPanel10 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        label_order_id1 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        Tabel_detail_LP_Asal = new javax.swing.JTable();
        label_total_nilai_lp_asal = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        label_total_gram_lp_asal = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jLabel61 = new javax.swing.JLabel();
        label_order_id2 = new javax.swing.JLabel();
        jScrollPane11 = new javax.swing.JScrollPane();
        Tabel_detail_Evaluasi = new javax.swing.JTable();
        label_total_gram_evaluasi = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        label_total_nilai_evaluasi = new javax.swing.JLabel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        table_penggajian_cabuto_rekap_harian = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        table_penggajian_cabuto_rekap_karyawan = new javax.swing.JTable();

        setBackground(new java.awt.Color(255, 255, 255));

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        txt_search_nama.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_nama.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_namaKeyPressed(evt);
            }
        });

        button_search.setBackground(new java.awt.Color(255, 255, 255));
        button_search.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search.setText("Refresh");
        button_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_searchActionPerformed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Nama :");

        DateFilter_Setor1.setBackground(new java.awt.Color(255, 255, 255));
        DateFilter_Setor1.setDateFormatString("dd MMMM yyyy");
        DateFilter_Setor1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        DateFilter_Setor2.setBackground(new java.awt.Color(255, 255, 255));
        DateFilter_Setor2.setDateFormatString("dd MMMM yyyy");
        DateFilter_Setor2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Total Data :");

        label_total_data.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_data.setText("TOTAL");

        button_export.setBackground(new java.awt.Color(255, 255, 255));
        button_export.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export.setText("Export to Excel");
        button_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportActionPerformed(evt);
            }
        });

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("AVG % SH :");

        label_avg_persen_sh.setBackground(new java.awt.Color(255, 255, 255));
        label_avg_persen_sh.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_avg_persen_sh.setText("TOTAL");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Tgl Setor :");

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel23.setText("Total Nilai Baku :");

        label_total_nilai_baku.setBackground(new java.awt.Color(255, 255, 255));
        label_total_nilai_baku.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_nilai_baku.setText("TOTAL");

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel24.setText("Total Nilai Hasil :");

        label_total_nilai_hasil.setBackground(new java.awt.Color(255, 255, 255));
        label_total_nilai_hasil.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_nilai_hasil.setText("TOTAL");

        label_total_selisih.setBackground(new java.awt.Color(255, 255, 255));
        label_total_selisih.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_selisih.setText("TOTAL");

        jLabel27.setBackground(new java.awt.Color(255, 255, 255));
        jLabel27.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel27.setText("Total Selisih :");

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("Notes : ");
        jScrollPane1.setViewportView(jTextArea1);

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));
        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Asal LP", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("Total Gram :");

        label_order_id1.setBackground(new java.awt.Color(255, 255, 255));
        label_order_id1.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_order_id1.setText("-");

        Tabel_detail_LP_Asal.setAutoCreateRowSorter(true);
        Tabel_detail_LP_Asal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Tabel_detail_LP_Asal.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Tgl LP", "No LP", "Grade", "Bulu", "Kpg", "Gram", "Nilai Baku"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        Tabel_detail_LP_Asal.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(Tabel_detail_LP_Asal);

        label_total_nilai_lp_asal.setBackground(new java.awt.Color(255, 255, 255));
        label_total_nilai_lp_asal.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_nilai_lp_asal.setText("0");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Total Nilai :");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Order ID :");

        label_total_gram_lp_asal.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_lp_asal.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gram_lp_asal.setText("0");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_order_id1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_lp_asal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_nilai_lp_asal))
                    .addComponent(jScrollPane3))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_order_id1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram_lp_asal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_nilai_lp_asal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel12.setBackground(new java.awt.Color(255, 255, 255));
        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Evaluasi Hasil", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N

        jLabel61.setBackground(new java.awt.Color(255, 255, 255));
        jLabel61.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel61.setText("Total Nilai :");

        label_order_id2.setBackground(new java.awt.Color(255, 255, 255));
        label_order_id2.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_order_id2.setText("-");

        Tabel_detail_Evaluasi.setAutoCreateRowSorter(true);
        Tabel_detail_Evaluasi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Tabel_detail_Evaluasi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Grade", "Kpg", "Gram", "Rp/gr", "Nilai BJD"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        Tabel_detail_Evaluasi.getTableHeader().setReorderingAllowed(false);
        jScrollPane11.setViewportView(Tabel_detail_Evaluasi);

        label_total_gram_evaluasi.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_evaluasi.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gram_evaluasi.setText("0");

        jLabel62.setBackground(new java.awt.Color(255, 255, 255));
        jLabel62.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel62.setText("Total Gram :");

        jLabel63.setBackground(new java.awt.Color(255, 255, 255));
        jLabel63.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel63.setText("Order ID :");

        label_total_nilai_evaluasi.setBackground(new java.awt.Color(255, 255, 255));
        label_total_nilai_evaluasi.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_nilai_evaluasi.setText("0");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel63)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_order_id2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel62)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_evaluasi)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel61)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_nilai_evaluasi))
                    .addComponent(jScrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 489, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel63, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_order_id2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel61, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_nilai_evaluasi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel62, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram_evaluasi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane2.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N

        table_penggajian_cabuto_rekap_harian.setAutoCreateRowSorter(true);
        table_penggajian_cabuto_rekap_harian.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tgl Setor", "ID Order", "ID Pegawai", "Nama", "Jumlah LP", "Gram LP", "Nilai LP", "Gram Hasil", "Nilai Hasil", "% SH", "Selisih"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_penggajian_cabuto_rekap_harian.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(table_penggajian_cabuto_rekap_harian);
        if (table_penggajian_cabuto_rekap_harian.getColumnModel().getColumnCount() > 0) {
            table_penggajian_cabuto_rekap_harian.getColumnModel().getColumn(0).setHeaderValue("Tgl Setor");
            table_penggajian_cabuto_rekap_harian.getColumnModel().getColumn(1).setHeaderValue("ID Order");
            table_penggajian_cabuto_rekap_harian.getColumnModel().getColumn(6).setHeaderValue("Nilai LP");
            table_penggajian_cabuto_rekap_harian.getColumnModel().getColumn(7).setHeaderValue("Gram Hasil");
            table_penggajian_cabuto_rekap_harian.getColumnModel().getColumn(8).setHeaderValue("Nilai Hasil");
            table_penggajian_cabuto_rekap_harian.getColumnModel().getColumn(9).setHeaderValue("% SH");
        }

        jTabbedPane2.addTab("Rekap Harian", jScrollPane2);

        table_penggajian_cabuto_rekap_karyawan.setAutoCreateRowSorter(true);
        table_penggajian_cabuto_rekap_karyawan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Pegawai", "Nama", "Jumlah LP", "Gram LP", "Selisih (Rp.)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        table_penggajian_cabuto_rekap_karyawan.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(table_penggajian_cabuto_rekap_karyawan);

        jTabbedPane2.addTab("Rekap Karyawan", jScrollPane4);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DateFilter_Setor1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DateFilter_Setor2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_total_nilai_baku, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_selisih, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_nilai_hasil, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_data, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_avg_persen_sh, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 73, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 475, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jTabbedPane2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DateFilter_Setor1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DateFilter_Setor2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_data, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_avg_persen_sh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_nilai_baku, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_nilai_hasil, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_selisih, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane1))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("PERHITUNGAN PENDAPATAN MITRA CABUTO", jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txt_search_namaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_namaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (DateFilter_Setor1.getDate() != null && DateFilter_Setor2.getDate() != null) {
                refreshTable_penggajian_cabuto();
            } else {
                JOptionPane.showMessageDialog(this, "Harap masukkan Range Tanggal setor");
            }
        }
    }//GEN-LAST:event_txt_search_namaKeyPressed

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        if (DateFilter_Setor1.getDate() != null && DateFilter_Setor2.getDate() != null) {
            refreshTable_penggajian_cabuto();
        } else {
            JOptionPane.showMessageDialog(this, "Harap masukkan Range Tanggal setor");
        }
    }//GEN-LAST:event_button_searchActionPerformed

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_penggajian_cabuto_rekap_harian.getModel();
        ExportToExcel.writeToExcel(model, jPanel2);
    }//GEN-LAST:event_button_exportActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser DateFilter_Setor1;
    private com.toedter.calendar.JDateChooser DateFilter_Setor2;
    private javax.swing.JTable Tabel_detail_Evaluasi;
    private javax.swing.JTable Tabel_detail_LP_Asal;
    private javax.swing.JButton button_export;
    public static javax.swing.JButton button_search;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel label_avg_persen_sh;
    private javax.swing.JLabel label_order_id1;
    private javax.swing.JLabel label_order_id2;
    private javax.swing.JLabel label_total_data;
    private javax.swing.JLabel label_total_gram_evaluasi;
    private javax.swing.JLabel label_total_gram_lp_asal;
    private javax.swing.JLabel label_total_nilai_baku;
    private javax.swing.JLabel label_total_nilai_evaluasi;
    private javax.swing.JLabel label_total_nilai_hasil;
    private javax.swing.JLabel label_total_nilai_lp_asal;
    private javax.swing.JLabel label_total_selisih;
    private javax.swing.JTable table_penggajian_cabuto_rekap_harian;
    private javax.swing.JTable table_penggajian_cabuto_rekap_karyawan;
    private javax.swing.JTextField txt_search_nama;
    // End of variables declaration//GEN-END:variables
}
