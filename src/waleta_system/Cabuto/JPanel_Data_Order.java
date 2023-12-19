package waleta_system.Cabuto;

import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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

public class JPanel_Data_Order extends javax.swing.JPanel {

    String sql = null;
    String sql2 = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    float kadar_air_bahan_baku = 0;

    public JPanel_Data_Order() {
        initComponents();
        Table_data_order.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && Table_data_order.getSelectedRow() != -1) {
                    int i = Table_data_order.getSelectedRow();
                    Utility.db_cabuto.connect();
                    refresh_list_lp(Table_data_order.getValueAt(i, 0).toString());
                    refresh_evaluasi(Table_data_order.getValueAt(i, 0).toString());
                }
            }
        });
    }

    public void init() {
        refresh_data_order();
    }

    private void refresh_data_order() {
        try {
            Utility.db_cabuto.connect();
            float total_order_lp = 0, total_berat_lp = 0, total_nilai_lp = 0;
            DefaultTableModel model = (DefaultTableModel) Table_data_order.getModel();
            model.setRowCount(0);

            String filter_tanggal_order = "";
            if (Date_Search_1.getDate() != null && Date_Search_2.getDate() != null) {
                if (ComboBox_filter_tanggal.getSelectedIndex() == 0) {//Belum diambil
                    filter_tanggal_order = "AND DATE(`waktu_order`) BETWEEN '" + dateFormat.format(Date_Search_1.getDate()) + "' AND '" + dateFormat.format(Date_Search_2.getDate()) + "' ";
                } else if (ComboBox_filter_tanggal.getSelectedIndex() == 1) {//Belum diambil
                    filter_tanggal_order = "AND DATE(`waktu_acc`) BETWEEN '" + dateFormat.format(Date_Search_1.getDate()) + "' AND '" + dateFormat.format(Date_Search_2.getDate()) + "' ";
                } else if (ComboBox_filter_tanggal.getSelectedIndex() == 2) {//Belum diambil
                    filter_tanggal_order = "AND DATE(`waktu_setor`) BETWEEN '" + dateFormat.format(Date_Search_1.getDate()) + "' AND '" + dateFormat.format(Date_Search_2.getDate()) + "' ";
                }
            }

            String status = "";
            if (ComboBox_status.getSelectedIndex() == 1) {//Belum diambil
                status = " AND `waktu_acc` IS NULL \n";
            } else if (ComboBox_status.getSelectedIndex() == 2) {//sedang dikerjakan
                status = " AND `waktu_acc` IS NOT NULL AND `waktu_setor` IS NULL \n";
            } else if (ComboBox_status.getSelectedIndex() == 3) {//sudah selesai
                status = " AND `waktu_setor` IS NOT NULL \n";
            }

            String query = "SELECT `tb_order`.`id_order`, `tb_order`.`id_user`, `tb_user`.`nama_user`, `jumlah_order`, `waktu_order`, `waktu_acc`, `waktu_setor`,\n"
                    + "(SELECT SUM(`tb_lp`.`jumlah_keping`) FROM `tb_lp` WHERE `tb_lp`.`id_order` = `tb_order`.`id_order`) AS 'kpg_lp', \n"
                    + "(SELECT SUM(`tb_lp`.`berat_basah`) FROM `tb_lp` WHERE `tb_lp`.`id_order` = `tb_order`.`id_order`) AS 'berat_lp', \n"
                    + "(SELECT SUM(`tb_lp`.`harga_baku`) FROM `tb_lp` WHERE `tb_lp`.`id_order` = `tb_order`.`id_order`) AS 'nilai_lp', \n"
                    + "(SELECT SUM(`keping`) FROM `tb_evaluasi` WHERE `tb_evaluasi`.`id_order` = `tb_order`.`id_order`) AS 'kpg_evaluasi', \n"
                    + "(SELECT SUM(`gram`) FROM `tb_evaluasi` WHERE `tb_evaluasi`.`id_order` = `tb_order`.`id_order`) AS 'berat_evaluasi', \n"
                    + "(SELECT SUM(`gram` * `harga`) FROM `tb_evaluasi` WHERE `tb_evaluasi`.`id_order` = `tb_order`.`id_order`) AS 'nilai_evaluasi' \n"
                    + "FROM `tb_order` \n"
                    + "LEFT JOIN `tb_user` ON `tb_order`.`id_user` = `tb_user`.`id_user`\n"
                    + "WHERE "
                    + "`tb_order`.`id_order` LIKE '%" + txt_search_id_order.getText() + "%' \n"
                    + "AND `tb_user`.`nama_user` LIKE '%" + txt_search_nama.getText() + "%' \n"
                    + filter_tanggal_order
                    + status
                    + "ORDER BY `waktu_order` DESC";
            ResultSet result = Utility.db_cabuto.getStatement().executeQuery(query);
            Object[] row = new Object[20];
            while (result.next()) {
                row[0] = result.getString("id_order");
                row[1] = result.getString("id_user");
                row[2] = result.getString("nama_user");
                row[3] = result.getFloat("jumlah_order");
                row[4] = result.getTimestamp("waktu_order");
                row[5] = result.getTimestamp("waktu_acc");
                row[6] = result.getTimestamp("waktu_setor");
                row[7] = result.getInt("kpg_lp");
                row[8] = result.getInt("berat_lp");
                row[9] = result.getInt("nilai_lp");
                row[10] = result.getInt("kpg_evaluasi");
                row[11] = result.getFloat("berat_evaluasi");
                row[12] = Math.round(result.getFloat("nilai_evaluasi"));
                row[13] = result.getFloat("berat_lp") - result.getFloat("berat_evaluasi");
                row[14] = Math.round(result.getFloat("nilai_evaluasi") - result.getFloat("nilai_lp"));
                
                model.addRow(row);
                total_order_lp = total_order_lp + result.getFloat("jumlah_order");
                total_berat_lp = total_berat_lp + result.getFloat("berat_lp");
                total_nilai_lp = total_nilai_lp + result.getFloat("nilai_lp");
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_data_order);
            label_total_data_order.setText(Integer.toString(Table_data_order.getRowCount()));
            label_total_order_lp.setText(decimalFormat.format(total_order_lp));
            label_total_berat_lp.setText(decimalFormat.format(total_berat_lp));
            label_total_nilai_lp.setText(decimalFormat.format(total_nilai_lp));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Data_Order.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void refresh_list_lp(String id_order) {
        try {
            DefaultTableModel model = (DefaultTableModel) table_list_lp.getModel();
            model.setRowCount(0);

            String query = "SELECT `no_laporan_produksi`, `tanggal_lp`, `kode_grade`, `berat_basah`, `jumlah_keping`, `harga_baku` "
                    + "FROM `tb_lp` "
                    + "WHERE `id_order` = '" + id_order + "' \n";
            ResultSet result = Utility.db_cabuto.getStatement().executeQuery(query);
            Object[] row = new Object[15];
            while (result.next()) {
                row[0] = result.getString("no_laporan_produksi");
                row[1] = result.getDate("tanggal_lp");
                row[2] = result.getString("kode_grade");
                row[3] = result.getInt("jumlah_keping");
                row[4] = result.getInt("berat_basah");
                row[5] = result.getInt("harga_baku");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_list_lp);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Data_Order.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void refresh_evaluasi(String id_order) {
        try {
            DefaultTableModel model = (DefaultTableModel) table_evaluasi.getModel();
            model.setRowCount(0);

            String query = "SELECT `grade`, `keping`, `gram`, `harga` FROM `tb_evaluasi` WHERE `id_order` = '" + id_order + "' \n";
            ResultSet result = Utility.db_cabuto.getStatement().executeQuery(query);
            Object[] row = new Object[15];
            while (result.next()) {
                row[0] = result.getString("grade");
                row[1] = result.getInt("keping");
                row[2] = result.getFloat("gram");
                row[3] = result.getInt("harga");
                row[4] = Math.round(result.getFloat("gram") * result.getFloat("harga"));
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_evaluasi);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Data_Order.class.getName()).log(Level.SEVERE, null, e);
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

        jPanel_laporan_produksi = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        label_total_data_order = new javax.swing.JLabel();
        label_total_order_lp = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        button_export = new javax.swing.JButton();
        jScrollPane9 = new javax.swing.JScrollPane();
        Table_data_order = new javax.swing.JTable();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txt_search_nama = new javax.swing.JTextField();
        txt_search_id_order = new javax.swing.JTextField();
        Date_Search_1 = new com.toedter.calendar.JDateChooser();
        Date_Search_2 = new com.toedter.calendar.JDateChooser();
        button_cari = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        ComboBox_status = new javax.swing.JComboBox<>();
        jLabel36 = new javax.swing.JLabel();
        ComboBox_filter_tanggal = new javax.swing.JComboBox<>();
        jLabel28 = new javax.swing.JLabel();
        label_total_berat_lp = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        label_total_nilai_lp = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_list_lp = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        table_evaluasi = new javax.swing.JTable();

        jPanel_laporan_produksi.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_laporan_produksi.setPreferredSize(new java.awt.Dimension(1366, 700));

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel15.setText("Total Data :");

        label_total_data_order.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_order.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_data_order.setText("TOTAL");

        label_total_order_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_total_order_lp.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_order_lp.setText("TOTAL");

        jLabel27.setBackground(new java.awt.Color(255, 255, 255));
        jLabel27.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel27.setText("Total Order LP :");

        button_export.setBackground(new java.awt.Color(255, 255, 255));
        button_export.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_export.setText("Export Excel");
        button_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportActionPerformed(evt);
            }
        });

        Table_data_order.setAutoCreateRowSorter(true);
        Table_data_order.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_data_order.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Order", "ID User", "Nama User", "Jumlah Order", "Waktu Order", "Waktu Ambil", "Waktu Setor", "Kpg LP", "Gram LP", "Nilai LP", "Kpg Hasil", "Gram Hasil", "Nilai Hasil", "SH (Gr)", "Selisih Nilai"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        Table_data_order.getTableHeader().setReorderingAllowed(false);
        jScrollPane9.setViewportView(Table_data_order);

        jLabel13.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel13.setText("ID Order :");

        jLabel14.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel14.setText("Nama :");

        txt_search_nama.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_nama.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_namaKeyPressed(evt);
            }
        });

        txt_search_id_order.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_id_order.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_id_orderKeyPressed(evt);
            }
        });

        Date_Search_1.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search_1.setToolTipText("");
        Date_Search_1.setDate(new Date());
        Date_Search_1.setDateFormatString("dd MMM yyyy");
        Date_Search_1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Date_Search_1.setMinSelectableDate(new java.util.Date(1420048915000L));

        Date_Search_2.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search_2.setDate(new Date());
        Date_Search_2.setDateFormatString("dd MMM yyyy");
        Date_Search_2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        button_cari.setBackground(new java.awt.Color(255, 255, 255));
        button_cari.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_cari.setText("Cari");
        button_cari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_cariActionPerformed(evt);
            }
        });

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel18.setText("DATA ORDER CABUTO");

        ComboBox_status.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        ComboBox_status.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Belum Diambil", "Sedang Dikerjakan", "Sudah Selesai" }));

        jLabel36.setBackground(new java.awt.Color(255, 255, 255));
        jLabel36.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel36.setText("Status :");

        ComboBox_filter_tanggal.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        ComboBox_filter_tanggal.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tanggal Order", "Tanggal Ambil", "Tanggal Setor" }));

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel28.setText("Total Berat LP :");

        label_total_berat_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_total_berat_lp.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_berat_lp.setText("TOTAL");

        jLabel29.setBackground(new java.awt.Color(255, 255, 255));
        jLabel29.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel29.setText("Total Nilai LP :");

        label_total_nilai_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_total_nilai_lp.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_nilai_lp.setText("TOTAL");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Data LP", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N

        table_list_lp.setAutoCreateRowSorter(true);
        table_list_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_list_lp.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP", "Tanggal LP", "Grade", "Kpg", "Berat", "Nilai LP"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(table_list_lp);
        if (table_list_lp.getColumnModel().getColumnCount() > 0) {
            table_list_lp.getColumnModel().getColumn(0).setHeaderValue("Grade");
            table_list_lp.getColumnModel().getColumn(1).setHeaderValue("Tanggal LP");
        }

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Data Evaluasi", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N

        table_evaluasi.setAutoCreateRowSorter(true);
        table_evaluasi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_evaluasi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade", "Kpg", "Berat", "Nilai/gr", "Subtotal"
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
        jScrollPane2.setViewportView(table_evaluasi);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel_laporan_produksiLayout = new javax.swing.GroupLayout(jPanel_laporan_produksi);
        jPanel_laporan_produksi.setLayout(jPanel_laporan_produksiLayout);
        jPanel_laporan_produksiLayout.setHorizontalGroup(
            jPanel_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_laporan_produksiLayout.createSequentialGroup()
                .addGroup(jPanel_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_laporan_produksiLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel18))
                    .addGroup(jPanel_laporan_produksiLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data_order)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel27)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_order_lp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_berat_lp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_nilai_lp))
                    .addGroup(jPanel_laporan_produksiLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_laporan_produksiLayout.createSequentialGroup()
                                .addComponent(jScrollPane9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel_laporan_produksiLayout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_id_order, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_filter_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_Search_1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_Search_2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel36)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_status, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_cari)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export)
                                .addGap(0, 231, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        jPanel_laporan_produksiLayout.setVerticalGroup(
            jPanel_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_laporan_produksiLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(Date_Search_2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Search_1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_id_order, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_filter_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_status, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_cari, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_total_order_lp)
                    .addComponent(jLabel27)
                    .addComponent(jLabel15)
                    .addComponent(label_total_data_order)
                    .addComponent(label_total_berat_lp)
                    .addComponent(jLabel28)
                    .addComponent(label_total_nilai_lp)
                    .addComponent(jLabel29))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 630, Short.MAX_VALUE)
                    .addGroup(jPanel_laporan_produksiLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jPanel_laporan_produksi, javax.swing.GroupLayout.DEFAULT_SIZE, 1364, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_laporan_produksi, javax.swing.GroupLayout.DEFAULT_SIZE, 728, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void button_cariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_cariActionPerformed
        // TODO add your handling code here:
        refresh_data_order();
    }//GEN-LAST:event_button_cariActionPerformed

    private void txt_search_id_orderKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_id_orderKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refresh_data_order();
        }
    }//GEN-LAST:event_txt_search_id_orderKeyPressed

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_data_order.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_exportActionPerformed

    private void txt_search_namaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_namaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refresh_data_order();
        }
    }//GEN-LAST:event_txt_search_namaKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_filter_tanggal;
    private javax.swing.JComboBox<String> ComboBox_status;
    private com.toedter.calendar.JDateChooser Date_Search_1;
    private com.toedter.calendar.JDateChooser Date_Search_2;
    public static javax.swing.JTable Table_data_order;
    private javax.swing.JButton button_cari;
    private javax.swing.JButton button_export;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel_laporan_produksi;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JLabel label_total_berat_lp;
    private javax.swing.JLabel label_total_data_order;
    private javax.swing.JLabel label_total_nilai_lp;
    private javax.swing.JLabel label_total_order_lp;
    private javax.swing.JTable table_evaluasi;
    private javax.swing.JTable table_list_lp;
    private javax.swing.JTextField txt_search_id_order;
    private javax.swing.JTextField txt_search_nama;
    // End of variables declaration//GEN-END:variables
}
