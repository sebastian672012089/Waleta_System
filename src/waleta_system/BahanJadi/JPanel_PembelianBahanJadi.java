package waleta_system.BahanJadi;

import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import waleta_system.BahanBaku.JPanel_GradeBahanBaku;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;

public class JPanel_PembelianBahanJadi extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_PembelianBahanJadi() {
        initComponents();
    }

    public void init() {
        try {
            String suplier = "SELECT `nama_supplier` FROM `tb_supplier`";
            ComboBox_Supplier.removeAllItems();
            ResultSet suplier_result = Utility.db.getStatement().executeQuery(suplier);
            while (suplier_result.next()) {
                ComboBox_Supplier.addItem(suplier_result.getString("nama_supplier"));
            }
            AutoCompleteDecorator.decorate(ComboBox_Supplier);
            refreshTable();
            refreshTable_detail();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JPanel_GradeBahanBaku.class.getName()).log(Level.SEVERE, null, e);
        }
        Table_pembelian_bahan_jadi.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && Table_pembelian_bahan_jadi.getSelectedRow() != -1) {
                    try {
                        int i = Table_pembelian_bahan_jadi.getSelectedRow();
                        txt_kode_pembelian.setText(Table_pembelian_bahan_jadi.getValueAt(i, 0).toString());

                        String tgl_pembelian = Table_pembelian_bahan_jadi.getValueAt(i, 1).toString();
                        Date_pembelian.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(tgl_pembelian));

                        txt_keping.setText(Table_pembelian_bahan_jadi.getValueAt(i, 2).toString());
                        txt_berat.setText(Table_pembelian_bahan_jadi.getValueAt(i, 3).toString());
                        ComboBox_Supplier.setSelectedItem(Table_pembelian_bahan_jadi.getValueAt(i, 4).toString());
                        txt_keterangan.setText(Table_pembelian_bahan_jadi.getValueAt(i, 5).toString());
                    } catch (ParseException | NullPointerException ex) {
                        Logger.getLogger(JPanel_PembelianBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

        Table_hasil_lab.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && Table_hasil_lab.getSelectedRow() != -1) {
                    try {
                        int i = Table_hasil_lab.getSelectedRow();
                        txt_kode_pembelian_hasil_lab.setText(Table_hasil_lab.getValueAt(i, 0).toString());
                        txt_grade_hasil_lab.setText(Table_hasil_lab.getValueAt(i, 1).toString());
                        txt_PH.setText(Table_hasil_lab.getValueAt(i, 4).toString());
                        txt_H2O2.setText(Table_hasil_lab.getValueAt(i, 5).toString());
                        txt_NO2.setText(Table_hasil_lab.getValueAt(i, 6).toString());
                        txt_NO3.setText(Table_hasil_lab.getValueAt(i, 7).toString());
                        txt_Pb.setText(Table_hasil_lab.getValueAt(i, 8).toString());
                        txt_Cu.setText(Table_hasil_lab.getValueAt(i, 9).toString());
                        txt_As.setText(Table_hasil_lab.getValueAt(i, 10).toString());
                        txt_DETERGEN.setText(Table_hasil_lab.getValueAt(i, 11).toString());
                    } catch (NullPointerException ex) {
                        Logger.getLogger(JPanel_PembelianBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

        Table_hasil_masak.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && Table_hasil_masak.getSelectedRow() != -1) {
                    try {
                        int i = Table_hasil_masak.getSelectedRow();
                        txt_kode_pembelian_hasil_masak.setText(Table_hasil_masak.getValueAt(i, 0).toString());
                        txt_grade_hasil_masak.setText(Table_hasil_masak.getValueAt(i, 1).toString());
                        txt_expansion_rate.setText(Table_hasil_masak.getValueAt(i, 2).toString());
                        txt_rasa.setText(Table_hasil_masak.getValueAt(i, 3).toString());
                        txt_aroma.setText(Table_hasil_masak.getValueAt(i, 4).toString());
                        txt_kebersihan.setText(Table_hasil_masak.getValueAt(i, 5).toString());
                        txt_penilaian.setText(Table_hasil_masak.getValueAt(i, 6).toString());
                    } catch (NullPointerException ex) {
//                        Logger.getLogger(JPanel_PembelianBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
    }

    public void refreshTable() {
        try {
            decimalFormat.setGroupingUsed(false);
            DefaultTableModel model1 = (DefaultTableModel) Table_pembelian_bahan_jadi.getModel();
            model1.setRowCount(0);

            String SearchBy = "";
            if (ComboBox_SearchBy.getSelectedIndex() == 0) {
                SearchBy = "`kode_pembelian_bahan_jadi` LIKE '%" + txt_keyword.getText() + "%'";
            } else {
                SearchBy = "`nama_supplier` LIKE '%" + txt_keyword.getText() + "%'";
            }

            String filter_tanggal = "";
            if (Date_pembelian1.getDate() != null && Date_pembelian2.getDate() != null) {
                filter_tanggal = " AND (`tanggal_pembelian` BETWEEN '" + dateFormat.format(Date_pembelian1.getDate()) + "' AND '" + dateFormat.format(Date_pembelian2.getDate()) + "')";
            }

            sql = "SELECT `kode_pembelian_bahan_jadi`, `kode_tutupan`, `tanggal_pembelian`, `tb_pembelian_barang_jadi`.`jumlah_keping`, `tb_pembelian_barang_jadi`.`berat`, `harga`, `tb_supplier`.`nama_supplier`, `keterangan` \n"
                    + "FROM `tb_pembelian_barang_jadi` "
                    + "LEFT JOIN `tb_supplier` ON `tb_pembelian_barang_jadi`.`kode_supplier` = `tb_supplier`.`kode_supplier`\n"
                    + "LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_pembelian_barang_jadi`.`kode_pembelian_bahan_jadi` = `tb_bahan_jadi_masuk`.`kode_asal`\n"
                    + "WHERE " + SearchBy + filter_tanggal;
//            System.out.println(sql);
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[9];
            while (rs.next()) {
                row[0] = rs.getString("kode_pembelian_bahan_jadi");
                row[1] = rs.getDate("tanggal_pembelian");
                row[2] = rs.getInt("jumlah_keping");
                row[3] = rs.getFloat("berat");
                row[4] = rs.getString("nama_supplier");
                row[5] = rs.getString("keterangan");
                row[6] = rs.getString("kode_tutupan");
                model1.addRow(row);
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_PembelianBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }

        int rowData = Table_pembelian_bahan_jadi.getRowCount();
        label_total_data.setText(Integer.toString(rowData));

        ColumnsAutoSizer.sizeColumnsToFit(Table_pembelian_bahan_jadi);
    }

    public void refreshTable_detail() {
        try {
            decimalFormat.setGroupingUsed(false);
            DefaultTableModel model2 = (DefaultTableModel) Table_hasil_lab.getModel();
            model2.setRowCount(0);
            DefaultTableModel model3 = (DefaultTableModel) Table_hasil_masak.getModel();
            model3.setRowCount(0);

            String SearchBy = "";
            switch (ComboBox_SearchBy.getSelectedIndex()) {
                case 0: //kode pembalian
                    SearchBy = "`tb_pembelian_barang_jadi_grading`.`kode_pembelian_bahan_jadi` LIKE '%" + txt_keyword.getText() + "%'";
                    break;
                case 1: // supplier
                    SearchBy = "`nama_supplier` LIKE '%" + txt_keyword.getText() + "%'";
                    break;
                default:
                    SearchBy = "";
                    break;
            }

            sql = "SELECT `tb_pembelian_barang_jadi_grading`.`kode_pembelian_bahan_jadi`, `grade`, `tb_pembelian_barang_jadi_grading`.`keping`, `tb_pembelian_barang_jadi_grading`.`berat`, \n"
                    + "`lab_ph`, `lab_h2o2`, `lab_no2`, `lab_no3`, `lab_pb`, `lab_cu`, `lab_as`, `lab_detergen`, \n"
                    + "`expansion_rate`, `rasa`, `aroma`, `kebersihan`, `penilaian` \n"
                    + "FROM `tb_pembelian_barang_jadi_grading` "
                    + "LEFT JOIN `tb_pembelian_barang_jadi` ON `tb_pembelian_barang_jadi`.`kode_pembelian_bahan_jadi` = `tb_pembelian_barang_jadi_grading`.`kode_pembelian_bahan_jadi`\n"
                    + "LEFT JOIN `tb_supplier` ON `tb_pembelian_barang_jadi`.`kode_supplier` = `tb_supplier`.`kode_supplier`\n"
                    + "LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_pembelian_barang_jadi`.`kode_pembelian_bahan_jadi` = `tb_bahan_jadi_masuk`.`kode_asal`\n"
                    + "WHERE " + SearchBy + "";
            if (Date_pembelian1.getDate() != null && Date_pembelian2.getDate() != null) {
                sql = "SELECT `tb_pembelian_barang_jadi_grading`.`kode_pembelian_bahan_jadi`, `grade`, `tb_pembelian_barang_jadi_grading`.`keping`, `tb_pembelian_barang_jadi_grading`.`berat`, \n"
                        + "`lab_ph`, `lab_h2o2`, `lab_no2`, `lab_no3`, `lab_pb`, `lab_cu`, `lab_as`, `lab_detergen`, \n"
                        + "`expansion_rate`, `rasa`, `aroma`, `kebersihan`, `penilaian` \n"
                        + "FROM `tb_pembelian_barang_jadi_grading` "
                        + "LEFT JOIN `tb_pembelian_barang_jadi` ON `tb_pembelian_barang_jadi`.`kode_pembelian_bahan_jadi` = `tb_pembelian_barang_jadi_grading`.`kode_pembelian_bahan_jadi`\n"
                        + "LEFT JOIN `tb_supplier` ON `tb_pembelian_barang_jadi`.`kode_supplier` = `tb_supplier`.`kode_supplier`\n"
                        + "LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_pembelian_barang_jadi`.`kode_pembelian_bahan_jadi` = `tb_bahan_jadi_masuk`.`kode_asal`\n"
                        + "WHERE " + SearchBy + " AND (`tanggal_pembelian` BETWEEN " + dateFormat.format(Date_pembelian1.getDate()) + " AND " + dateFormat.format(Date_pembelian2.getDate()) + ")";
            }
//            System.out.println(sql);
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[12];
            while (rs.next()) {
                row[0] = rs.getString("kode_pembelian_bahan_jadi");
                row[1] = rs.getString("grade");
                row[2] = rs.getInt("keping");
                row[3] = rs.getFloat("berat");
                row[4] = rs.getFloat("lab_ph");
                row[5] = rs.getFloat("lab_h2o2");
                row[6] = rs.getFloat("lab_no2");
                row[7] = rs.getFloat("lab_no3");
                row[8] = rs.getFloat("lab_pb");
                row[9] = rs.getFloat("lab_cu");
                row[10] = rs.getFloat("lab_as");
                row[11] = rs.getFloat("lab_detergen");
                model2.addRow(row);

                row[0] = rs.getString("kode_pembelian_bahan_jadi");
                row[1] = rs.getString("grade");
                row[2] = rs.getInt("expansion_rate");
                row[3] = rs.getString("rasa");
                row[4] = rs.getString("aroma");
                row[5] = rs.getString("kebersihan");
                row[6] = rs.getString("penilaian");
                model3.addRow(row);
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_PembelianBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }

        int rowData = Table_pembelian_bahan_jadi.getRowCount();
        label_total_data.setText(Integer.toString(rowData));

        ColumnsAutoSizer.sizeColumnsToFit(Table_hasil_lab);
        ColumnsAutoSizer.sizeColumnsToFit(Table_hasil_masak);
    }

    public void executeSQLQuery(String query, String message) {
        Utility.db.getConnection();
        try {
            Utility.db.getConnection().createStatement();
            if ((Utility.db.getStatement().executeUpdate(query)) == 1) {
                JOptionPane.showMessageDialog(this, "data " + message + " Successfully");
                refreshTable();
                button_clear.doClick();
            } else {
                JOptionPane.showMessageDialog(this, "data not " + message);
            }
        } catch (SQLException | HeadlessException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JPanel_PembelianBahanJadi.class.getName()).log(Level.SEVERE, null, e);
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
        txt_keyword = new javax.swing.JTextField();
        button_search = new javax.swing.JButton();
        Date_pembelian1 = new com.toedter.calendar.JDateChooser();
        label_tgl_pembelian = new javax.swing.JLabel();
        Date_pembelian2 = new com.toedter.calendar.JDateChooser();
        ComboBox_SearchBy = new javax.swing.JComboBox<>();
        jLabel18 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        label_total_data = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        Table_pembelian_bahan_jadi = new javax.swing.JTable();
        jPanel_operation_bahan_baku1 = new javax.swing.JPanel();
        label1_1 = new javax.swing.JLabel();
        label1_3 = new javax.swing.JLabel();
        label1_7 = new javax.swing.JLabel();
        button_insert = new javax.swing.JButton();
        button_update = new javax.swing.JButton();
        button_clear = new javax.swing.JButton();
        button_delete = new javax.swing.JButton();
        txt_berat = new javax.swing.JTextField();
        Date_pembelian = new com.toedter.calendar.JDateChooser();
        txt_kode_pembelian = new javax.swing.JTextField();
        label_kode_pembelian1 = new javax.swing.JLabel();
        label1_2 = new javax.swing.JLabel();
        txt_keping = new javax.swing.JTextField();
        label1_4 = new javax.swing.JLabel();
        ComboBox_Supplier = new javax.swing.JComboBox<>();
        label1_6 = new javax.swing.JLabel();
        label1_5 = new javax.swing.JLabel();
        txt_keterangan = new javax.swing.JTextField();
        button_input_grading = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        Table_hasil_lab = new javax.swing.JTable();
        jPanel_operation_bahan_baku2 = new javax.swing.JPanel();
        txt_kode_pembelian_hasil_lab = new javax.swing.JTextField();
        label_kode_pembelian2 = new javax.swing.JLabel();
        label2_1 = new javax.swing.JLabel();
        txt_PH = new javax.swing.JTextField();
        label2_2 = new javax.swing.JLabel();
        txt_H2O2 = new javax.swing.JTextField();
        txt_NO2 = new javax.swing.JTextField();
        label2_3 = new javax.swing.JLabel();
        label2_4 = new javax.swing.JLabel();
        txt_NO3 = new javax.swing.JTextField();
        txt_Pb = new javax.swing.JTextField();
        label2_5 = new javax.swing.JLabel();
        label2_6 = new javax.swing.JLabel();
        txt_Cu = new javax.swing.JTextField();
        txt_As = new javax.swing.JTextField();
        label2_7 = new javax.swing.JLabel();
        txt_DETERGEN = new javax.swing.JTextField();
        label2_8 = new javax.swing.JLabel();
        button_save_hasil_lab = new javax.swing.JButton();
        label_kode_pembelian4 = new javax.swing.JLabel();
        txt_grade_hasil_lab = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        Table_hasil_masak = new javax.swing.JTable();
        jPanel_operation_bahan_baku3 = new javax.swing.JPanel();
        button_save_hasil_masak = new javax.swing.JButton();
        txt_kode_pembelian_hasil_masak = new javax.swing.JTextField();
        label_kode_pembelian3 = new javax.swing.JLabel();
        label3_1 = new javax.swing.JLabel();
        txt_expansion_rate = new javax.swing.JTextField();
        label3_2 = new javax.swing.JLabel();
        txt_rasa = new javax.swing.JTextField();
        txt_aroma = new javax.swing.JTextField();
        label3_3 = new javax.swing.JLabel();
        txt_kebersihan = new javax.swing.JTextField();
        label3_4 = new javax.swing.JLabel();
        label3_5 = new javax.swing.JLabel();
        txt_penilaian = new javax.swing.JTextField();
        label_kode_pembelian5 = new javax.swing.JLabel();
        txt_grade_hasil_masak = new javax.swing.JTextField();

        jPanel_Bahan_Baku_Keluar.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_Bahan_Baku_Keluar.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Pembelian Barang Jadi", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N
        jPanel_Bahan_Baku_Keluar.setPreferredSize(new java.awt.Dimension(1366, 700));

        jPanel_search_baku_keluar.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_search_baku_keluar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        txt_keyword.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_keyword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_keywordKeyPressed(evt);
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

        Date_pembelian1.setBackground(new java.awt.Color(255, 255, 255));
        Date_pembelian1.setDateFormatString("dd MMMM yyyy");
        Date_pembelian1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        label_tgl_pembelian.setBackground(new java.awt.Color(255, 255, 255));
        label_tgl_pembelian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_tgl_pembelian.setText("Tanggal Pembelian :");

        Date_pembelian2.setBackground(new java.awt.Color(255, 255, 255));
        Date_pembelian2.setDateFormatString("dd MMMM yyyy");
        Date_pembelian2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        ComboBox_SearchBy.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_SearchBy.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Kode Pembelian", "Supplier" }));

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel18.setText("Search By ");

        javax.swing.GroupLayout jPanel_search_baku_keluarLayout = new javax.swing.GroupLayout(jPanel_search_baku_keluar);
        jPanel_search_baku_keluar.setLayout(jPanel_search_baku_keluarLayout);
        jPanel_search_baku_keluarLayout.setHorizontalGroup(
            jPanel_search_baku_keluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_baku_keluarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ComboBox_SearchBy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_keyword, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_tgl_pembelian)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date_pembelian1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date_pembelian2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_search)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_search_baku_keluarLayout.setVerticalGroup(
            jPanel_search_baku_keluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_baku_keluarLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel_search_baku_keluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_keyword, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_pembelian1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_tgl_pembelian, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_pembelian2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_SearchBy, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jLabel20.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jLabel20.setText("Total Data :");

        label_total_data.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        label_total_data.setText("TOTAL");

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        Table_pembelian_bahan_jadi.setAutoCreateRowSorter(true);
        Table_pembelian_bahan_jadi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Pembelian", "Tgl Masuk", "Keping", "Gram", "Supplier", "Keterangan", "Tutupan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        Table_pembelian_bahan_jadi.getTableHeader().setReorderingAllowed(false);
        jScrollPane7.setViewportView(Table_pembelian_bahan_jadi);

        jPanel_operation_bahan_baku1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_operation_bahan_baku1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Main Operation", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Yu Gothic", 0, 12))); // NOI18N
        jPanel_operation_bahan_baku1.setName("aah"); // NOI18N

        label1_1.setBackground(new java.awt.Color(255, 255, 255));
        label1_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label1_1.setText("Tanggal Pembelian :");

        label1_3.setBackground(new java.awt.Color(255, 255, 255));
        label1_3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label1_3.setText("Berat :");

        label1_7.setBackground(new java.awt.Color(255, 255, 255));
        label1_7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label1_7.setText("Gram");

        button_insert.setBackground(new java.awt.Color(255, 255, 255));
        button_insert.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_insert.setText("insert");
        button_insert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_insertActionPerformed(evt);
            }
        });

        button_update.setBackground(new java.awt.Color(255, 255, 255));
        button_update.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_update.setText("Update");
        button_update.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_updateActionPerformed(evt);
            }
        });

        button_clear.setBackground(new java.awt.Color(255, 255, 255));
        button_clear.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_clear.setText("Clear Text");
        button_clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_clearActionPerformed(evt);
            }
        });

        button_delete.setBackground(new java.awt.Color(255, 255, 255));
        button_delete.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete.setText("Delete");
        button_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_deleteActionPerformed(evt);
            }
        });

        txt_berat.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_berat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_beratKeyTyped(evt);
            }
        });

        Date_pembelian.setBackground(new java.awt.Color(255, 255, 255));
        Date_pembelian.setDateFormatString("dd MMMM yyyy");
        Date_pembelian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_pembelian.setMaxSelectableDate(new Date());

        txt_kode_pembelian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        label_kode_pembelian1.setBackground(new java.awt.Color(255, 255, 255));
        label_kode_pembelian1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_kode_pembelian1.setText("Kode Pembelian :");

        label1_2.setBackground(new java.awt.Color(255, 255, 255));
        label1_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label1_2.setText("Jumlah Keping :");

        txt_keping.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_keping.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_kepingKeyTyped(evt);
            }
        });

        label1_4.setBackground(new java.awt.Color(255, 255, 255));
        label1_4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label1_4.setText("Supplier :");

        ComboBox_Supplier.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        label1_6.setBackground(new java.awt.Color(255, 255, 255));
        label1_6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label1_6.setText("Kpg");

        label1_5.setBackground(new java.awt.Color(255, 255, 255));
        label1_5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label1_5.setText("Keterangan :");

        txt_keterangan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_input_grading.setBackground(new java.awt.Color(255, 255, 255));
        button_input_grading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_input_grading.setText("Input Grade Pembelian");
        button_input_grading.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_gradingActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_operation_bahan_baku1Layout = new javax.swing.GroupLayout(jPanel_operation_bahan_baku1);
        jPanel_operation_bahan_baku1.setLayout(jPanel_operation_bahan_baku1Layout);
        jPanel_operation_bahan_baku1Layout.setHorizontalGroup(
            jPanel_operation_bahan_baku1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_operation_bahan_baku1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_operation_bahan_baku1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_operation_bahan_baku1Layout.createSequentialGroup()
                        .addGroup(jPanel_operation_bahan_baku1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label1_1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_kode_pembelian1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_operation_bahan_baku1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_kode_pembelian, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date_pembelian, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_operation_bahan_baku1Layout.createSequentialGroup()
                        .addGroup(jPanel_operation_bahan_baku1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label1_3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label1_2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_operation_bahan_baku1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txt_keping, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_berat))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_operation_bahan_baku1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label1_7)
                            .addComponent(label1_6)))
                    .addGroup(jPanel_operation_bahan_baku1Layout.createSequentialGroup()
                        .addComponent(label1_4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_Supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_operation_bahan_baku1Layout.createSequentialGroup()
                        .addComponent(label1_5, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_keterangan))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_operation_bahan_baku1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel_operation_bahan_baku1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_operation_bahan_baku1Layout.createSequentialGroup()
                                .addComponent(button_update)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_insert)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_delete)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_clear))
                            .addComponent(button_input_grading, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );
        jPanel_operation_bahan_baku1Layout.setVerticalGroup(
            jPanel_operation_bahan_baku1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_operation_bahan_baku1Layout.createSequentialGroup()
                .addGroup(jPanel_operation_bahan_baku1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_kode_pembelian, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kode_pembelian1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_baku1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(Date_pembelian, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label1_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_baku1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_keping, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label1_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label1_6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_baku1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_berat, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label1_7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label1_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_baku1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label1_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_Supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_baku1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label1_5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_keterangan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_baku1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_update, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_insert, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_clear, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_input_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 965, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel_operation_bahan_baku1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 533, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel_operation_bahan_baku1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Data Pembelian Barang jadi", jPanel1);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        Table_hasil_lab.setAutoCreateRowSorter(true);
        Table_hasil_lab.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Pembelian", "Grade", "Keping", "Gram", "PH", "H2O2", "NO2", "NO3", "Pb", "Cu", "As", "DETERGEN"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        jScrollPane8.setViewportView(Table_hasil_lab);
        if (Table_hasil_lab.getColumnModel().getColumnCount() > 0) {
            Table_hasil_lab.getColumnModel().getColumn(2).setHeaderValue("Keping");
            Table_hasil_lab.getColumnModel().getColumn(3).setHeaderValue("Gram");
        }

        jPanel_operation_bahan_baku2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_operation_bahan_baku2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Main Operation", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Yu Gothic", 0, 12))); // NOI18N
        jPanel_operation_bahan_baku2.setName("aah"); // NOI18N

        txt_kode_pembelian_hasil_lab.setEditable(false);
        txt_kode_pembelian_hasil_lab.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        label_kode_pembelian2.setBackground(new java.awt.Color(255, 255, 255));
        label_kode_pembelian2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_kode_pembelian2.setText("Kode Pembelian :");

        label2_1.setBackground(new java.awt.Color(255, 255, 255));
        label2_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label2_1.setText("PH :");

        txt_PH.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_PH.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_PHKeyTyped(evt);
            }
        });

        label2_2.setBackground(new java.awt.Color(255, 255, 255));
        label2_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label2_2.setText("H2O2 :");

        txt_H2O2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_H2O2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_H2O2KeyTyped(evt);
            }
        });

        txt_NO2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_NO2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_NO2KeyTyped(evt);
            }
        });

        label2_3.setBackground(new java.awt.Color(255, 255, 255));
        label2_3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label2_3.setText("NO2 :");

        label2_4.setBackground(new java.awt.Color(255, 255, 255));
        label2_4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label2_4.setText("NO3 :");

        txt_NO3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_NO3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_NO3KeyTyped(evt);
            }
        });

        txt_Pb.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_Pb.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_PbKeyTyped(evt);
            }
        });

        label2_5.setBackground(new java.awt.Color(255, 255, 255));
        label2_5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label2_5.setText("Pb :");

        label2_6.setBackground(new java.awt.Color(255, 255, 255));
        label2_6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label2_6.setText("Cu :");

        txt_Cu.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_Cu.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_CuKeyTyped(evt);
            }
        });

        txt_As.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_As.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_AsKeyTyped(evt);
            }
        });

        label2_7.setBackground(new java.awt.Color(255, 255, 255));
        label2_7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label2_7.setText("As :");

        txt_DETERGEN.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_DETERGEN.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_DETERGENKeyTyped(evt);
            }
        });

        label2_8.setBackground(new java.awt.Color(255, 255, 255));
        label2_8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label2_8.setText("DETERGEN :");

        button_save_hasil_lab.setBackground(new java.awt.Color(255, 255, 255));
        button_save_hasil_lab.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_save_hasil_lab.setText("Save");
        button_save_hasil_lab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_save_hasil_labActionPerformed(evt);
            }
        });

        label_kode_pembelian4.setBackground(new java.awt.Color(255, 255, 255));
        label_kode_pembelian4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_kode_pembelian4.setText("Grade :");

        txt_grade_hasil_lab.setEditable(false);
        txt_grade_hasil_lab.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        javax.swing.GroupLayout jPanel_operation_bahan_baku2Layout = new javax.swing.GroupLayout(jPanel_operation_bahan_baku2);
        jPanel_operation_bahan_baku2.setLayout(jPanel_operation_bahan_baku2Layout);
        jPanel_operation_bahan_baku2Layout.setHorizontalGroup(
            jPanel_operation_bahan_baku2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_operation_bahan_baku2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_operation_bahan_baku2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_operation_bahan_baku2Layout.createSequentialGroup()
                        .addComponent(label2_1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_PH))
                    .addGroup(jPanel_operation_bahan_baku2Layout.createSequentialGroup()
                        .addComponent(label2_2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_H2O2))
                    .addGroup(jPanel_operation_bahan_baku2Layout.createSequentialGroup()
                        .addComponent(label2_3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_NO2))
                    .addGroup(jPanel_operation_bahan_baku2Layout.createSequentialGroup()
                        .addComponent(label2_4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_NO3))
                    .addGroup(jPanel_operation_bahan_baku2Layout.createSequentialGroup()
                        .addComponent(label2_5, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_Pb))
                    .addGroup(jPanel_operation_bahan_baku2Layout.createSequentialGroup()
                        .addComponent(label2_6, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_Cu))
                    .addGroup(jPanel_operation_bahan_baku2Layout.createSequentialGroup()
                        .addComponent(label2_7, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_As))
                    .addGroup(jPanel_operation_bahan_baku2Layout.createSequentialGroup()
                        .addComponent(label2_8, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_DETERGEN))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_operation_bahan_baku2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(button_save_hasil_lab))
                    .addGroup(jPanel_operation_bahan_baku2Layout.createSequentialGroup()
                        .addGroup(jPanel_operation_bahan_baku2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_operation_bahan_baku2Layout.createSequentialGroup()
                                .addComponent(label_kode_pembelian2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_kode_pembelian_hasil_lab, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel_operation_bahan_baku2Layout.createSequentialGroup()
                                .addComponent(label_kode_pembelian4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_grade_hasil_lab, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel_operation_bahan_baku2Layout.setVerticalGroup(
            jPanel_operation_bahan_baku2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_operation_bahan_baku2Layout.createSequentialGroup()
                .addGroup(jPanel_operation_bahan_baku2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_kode_pembelian_hasil_lab, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kode_pembelian2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_baku2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_grade_hasil_lab, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kode_pembelian4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_baku2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_PH, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label2_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_baku2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_H2O2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label2_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_baku2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_NO2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label2_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_baku2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_NO3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label2_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_baku2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_Pb, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label2_5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_baku2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_Cu, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label2_6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_baku2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_As, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label2_7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_baku2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_DETERGEN, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label2_8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(button_save_hasil_lab, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 965, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel_operation_bahan_baku2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 533, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel_operation_bahan_baku2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Detail Grade & Uji Lab", jPanel2);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        Table_hasil_masak.setAutoCreateRowSorter(true);
        Table_hasil_masak.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Pembelian", "Grade", "Expansion Rate", "Rasa", "Aroma", "Kebersihan", "Penilaian"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        Table_hasil_masak.getTableHeader().setReorderingAllowed(false);
        jScrollPane9.setViewportView(Table_hasil_masak);

        jPanel_operation_bahan_baku3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_operation_bahan_baku3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Main Operation", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Yu Gothic", 0, 12))); // NOI18N
        jPanel_operation_bahan_baku3.setName("aah"); // NOI18N

        button_save_hasil_masak.setBackground(new java.awt.Color(255, 255, 255));
        button_save_hasil_masak.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_save_hasil_masak.setText("Save");
        button_save_hasil_masak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_save_hasil_masakActionPerformed(evt);
            }
        });

        txt_kode_pembelian_hasil_masak.setEditable(false);
        txt_kode_pembelian_hasil_masak.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        label_kode_pembelian3.setBackground(new java.awt.Color(255, 255, 255));
        label_kode_pembelian3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_kode_pembelian3.setText("Kode Pembelian :");

        label3_1.setBackground(new java.awt.Color(255, 255, 255));
        label3_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label3_1.setText("Expansion Rate :");

        txt_expansion_rate.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_expansion_rate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_expansion_rateKeyTyped(evt);
            }
        });

        label3_2.setBackground(new java.awt.Color(255, 255, 255));
        label3_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label3_2.setText("Rasa :");

        txt_rasa.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        txt_aroma.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        label3_3.setBackground(new java.awt.Color(255, 255, 255));
        label3_3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label3_3.setText("Aroma :");

        txt_kebersihan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        label3_4.setBackground(new java.awt.Color(255, 255, 255));
        label3_4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label3_4.setText("Kebersihan :");

        label3_5.setBackground(new java.awt.Color(255, 255, 255));
        label3_5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label3_5.setText("Penilaian :");

        txt_penilaian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        label_kode_pembelian5.setBackground(new java.awt.Color(255, 255, 255));
        label_kode_pembelian5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_kode_pembelian5.setText("Grade :");

        txt_grade_hasil_masak.setEditable(false);
        txt_grade_hasil_masak.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        javax.swing.GroupLayout jPanel_operation_bahan_baku3Layout = new javax.swing.GroupLayout(jPanel_operation_bahan_baku3);
        jPanel_operation_bahan_baku3.setLayout(jPanel_operation_bahan_baku3Layout);
        jPanel_operation_bahan_baku3Layout.setHorizontalGroup(
            jPanel_operation_bahan_baku3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_operation_bahan_baku3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_operation_bahan_baku3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_operation_bahan_baku3Layout.createSequentialGroup()
                        .addGroup(jPanel_operation_bahan_baku3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_kode_pembelian3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label3_1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label3_2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label3_3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label3_4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label3_5, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_kode_pembelian5, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_operation_bahan_baku3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_expansion_rate)
                            .addComponent(txt_rasa)
                            .addComponent(txt_aroma)
                            .addComponent(txt_kebersihan)
                            .addComponent(txt_penilaian)
                            .addGroup(jPanel_operation_bahan_baku3Layout.createSequentialGroup()
                                .addGroup(jPanel_operation_bahan_baku3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txt_kode_pembelian_hasil_masak, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_grade_hasil_masak, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_operation_bahan_baku3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(button_save_hasil_masak)))
                .addContainerGap())
        );
        jPanel_operation_bahan_baku3Layout.setVerticalGroup(
            jPanel_operation_bahan_baku3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_operation_bahan_baku3Layout.createSequentialGroup()
                .addGroup(jPanel_operation_bahan_baku3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_kode_pembelian_hasil_masak, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kode_pembelian3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_baku3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_grade_hasil_masak, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kode_pembelian5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_baku3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_expansion_rate, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label3_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_baku3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label3_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_rasa, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_baku3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label3_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_aroma, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_baku3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label3_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_kebersihan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_baku3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label3_5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_penilaian, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(button_save_hasil_masak, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 965, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel_operation_bahan_baku3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 533, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jPanel_operation_bahan_baku3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Hasil Masak", jPanel3);

        javax.swing.GroupLayout jPanel_Bahan_Baku_KeluarLayout = new javax.swing.GroupLayout(jPanel_Bahan_Baku_Keluar);
        jPanel_Bahan_Baku_Keluar.setLayout(jPanel_Bahan_Baku_KeluarLayout);
        jPanel_Bahan_Baku_KeluarLayout.setHorizontalGroup(
            jPanel_Bahan_Baku_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Bahan_Baku_KeluarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Bahan_Baku_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel_search_baku_keluar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel_Bahan_Baku_KeluarLayout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jTabbedPane1))
                .addContainerGap())
        );
        jPanel_Bahan_Baku_KeluarLayout.setVerticalGroup(
            jPanel_Bahan_Baku_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Bahan_Baku_KeluarLayout.createSequentialGroup()
                .addComponent(jPanel_search_baku_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Bahan_Baku_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_total_data)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1)
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

    private void txt_keywordKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_keywordKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_keywordKeyPressed

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        refreshTable();
        refreshTable_detail();
    }//GEN-LAST:event_button_searchActionPerformed

    private void txt_expansion_rateKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_expansion_rateKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_expansion_rateKeyTyped

    private void button_save_hasil_masakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_save_hasil_masakActionPerformed
        // TODO add your handling code here:
        int j = Table_hasil_masak.getSelectedRow();
        try {
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Klik data pembelian di tabel !");
            } else {
                Boolean Check = true;
                int expansion_rate = 0;
                try {
                    expansion_rate = Integer.parseInt(txt_expansion_rate.getText());
                } catch (NumberFormatException | NullPointerException e) {
                    JOptionPane.showMessageDialog(this, e.getMessage());
                    Logger.getLogger(JPanel_PembelianBahanJadi.class.getName()).log(Level.SEVERE, null, e);
                    Check = false;
                }

                if (Check) {
                    String Query = "UPDATE `tb_pembelian_barang_jadi` SET "
                            + "`expansion_rate`='" + expansion_rate + "',"
                            + "`rasa`='" + txt_rasa.getText() + "',"
                            + "`aroma`=" + txt_aroma.getText() + ","
                            + "`kebersihan`=" + txt_kebersihan.getText() + ","
                            + "`penilaian`='" + txt_penilaian.getText() + "' "
                            + "WHERE `kode_pembelian_bahan_jadi` = '" + txt_kode_pembelian_hasil_masak.getText() + "'"
                            + "AND `grade` = '" + txt_grade_hasil_lab.getText() + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                        refreshTable();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to save data!");
                    }
                }
            }
        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JPanel_PembelianBahanJadi.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_save_hasil_masakActionPerformed

    private void button_save_hasil_labActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_save_hasil_labActionPerformed
        // TODO add your handling code here:
        int j = Table_hasil_lab.getSelectedRow();
        try {
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Klik data pembelian di tabel !");
            } else {
                Boolean Check = true;
                float ph = 0;
                float h2o2 = 0;
                float no2 = 0;
                float no3 = 0;
                float pb = 0;
                float cu = 0;
                float as = 0;
                float detergen = 0;
                try {
                    ph = Float.parseFloat(txt_PH.getText());
                    h2o2 = Float.parseFloat(txt_H2O2.getText());
                    no2 = Float.parseFloat(txt_NO2.getText());
                    no3 = Float.parseFloat(txt_NO3.getText());
                    pb = Float.parseFloat(txt_Pb.getText());
                    cu = Float.parseFloat(txt_Cu.getText());
                    as = Float.parseFloat(txt_As.getText());
                    detergen = Float.parseFloat(txt_DETERGEN.getText());
                } catch (NumberFormatException | NullPointerException e) {
                    JOptionPane.showMessageDialog(this, e.getMessage());
                    Logger.getLogger(JPanel_PembelianBahanJadi.class.getName()).log(Level.SEVERE, null, e);
                    Check = false;
                }
                if (Check) {
                    String Query = "UPDATE `tb_pembelian_barang_jadi_grading` SET "
                            + "`lab_ph`='" + ph + "',"
                            + "`lab_h2o2`='" + h2o2 + "',"
                            + "`lab_no2`=" + no2 + ","
                            + "`lab_no3`=" + no3 + ","
                            + "`lab_pb`=" + pb + ", "
                            + "`lab_cu`='" + cu + "', "
                            + "`lab_as`='" + as + "', "
                            + "`lab_detergen`='" + detergen + "' "
                            + "WHERE `kode_pembelian_bahan_jadi` = '" + txt_kode_pembelian_hasil_lab.getText() + "'"
                            + "AND `grade` = '" + txt_grade_hasil_lab.getText() + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                        JOptionPane.showMessageDialog(this, "Data Saved!");
                        refreshTable();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to save data!");
                    }
                }
            }
        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JPanel_PembelianBahanJadi.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_save_hasil_labActionPerformed

    private void txt_DETERGENKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_DETERGENKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_DETERGENKeyTyped

    private void txt_AsKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_AsKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_AsKeyTyped

    private void txt_CuKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_CuKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_CuKeyTyped

    private void txt_PbKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_PbKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_PbKeyTyped

    private void txt_NO3KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_NO3KeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_NO3KeyTyped

    private void txt_NO2KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_NO2KeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_NO2KeyTyped

    private void txt_H2O2KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_H2O2KeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_H2O2KeyTyped

    private void txt_PHKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_PHKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_PHKeyTyped

    private void txt_kepingKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_kepingKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_kepingKeyTyped

    private void txt_beratKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_beratKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_beratKeyTyped

    private void button_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_deleteActionPerformed
        // TODO add your handling code here:
        try {
            boolean Check = true;
            int j = Table_pembelian_bahan_jadi.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang ingin di hapus !");
                Check = false;
            } else {
                sql = "SELECT `kode_asal` FROM `tb_bahan_jadi_masuk` WHERE `kode_asal` = '" + Table_pembelian_bahan_jadi.getValueAt(j, 0) + "'";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Pembelian sudah diterima di Bahan Jadi masuk, data tidak dapat di hapus!");
                    Check = false;
                }
            }
            if (Check) {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    String Query = "DELETE FROM `tb_pembelian_barang_jadi` WHERE `kode_pembelian_bahan_jadi` = '" + Table_pembelian_bahan_jadi.getValueAt(j, 0) + "'";
                    executeSQLQuery(Query, "deleted !");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JPanel_PembelianBahanJadi.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_deleteActionPerformed

    private void button_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_clearActionPerformed
        // TODO add your handling code here:
        txt_kode_pembelian.setText(null);
        Date_pembelian.setDate(null);
        txt_keping.setText(null);
        txt_berat.setText(null);
        ComboBox_Supplier.setSelectedIndex(0);
        txt_keterangan.setText(null);
    }//GEN-LAST:event_button_clearActionPerformed

    private void button_updateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_updateActionPerformed
        // TODO add your handling code here:
        DefaultTableModel Table = (DefaultTableModel) Table_pembelian_bahan_jadi.getModel();
        int j = Table_pembelian_bahan_jadi.getSelectedRow();
        try {
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih salah satu data pada tabel !");
            } else {
                Boolean Check = true;
                String tgl_pembelian = null;
                int kpg = 0;
                float gram = 0;
                double harga = 0;
                try {
                    tgl_pembelian = dateFormat.format(Date_pembelian.getDate());
                    kpg = Integer.parseInt(txt_keping.getText());
                    gram = Float.parseFloat(txt_berat.getText());

                    sql = "SELECT `kode_asal` FROM `tb_bahan_jadi_masuk` WHERE `kode_asal` = '" + Table_pembelian_bahan_jadi.getValueAt(j, 0) + "'";
                    rs = Utility.db.getStatement().executeQuery(sql);
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(this, "Pembelian sudah diterima di Bahan Jadi masuk, data tidak dapat di rubah!");
                        Check = false;
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, e.getMessage());
                    Logger.getLogger(JPanel_PembelianBahanJadi.class.getName()).log(Level.SEVERE, null, e);
                    Check = false;
                }
                if (Check) {
                    String insert_suplier = "(SELECT `kode_supplier` FROM `tb_supplier` WHERE `nama_supplier` = '" + ComboBox_Supplier.getSelectedItem().toString() + "')";
                    String Query = "UPDATE `tb_pembelian_barang_jadi` SET "
                            + "`kode_pembelian_bahan_jadi`='" + txt_kode_pembelian.getText() + "',"
                            + "`tanggal_pembelian`='" + tgl_pembelian + "',"
                            + "`jumlah_keping`=" + kpg + ","
                            + "`berat`=" + gram + ","
                            + "`kode_supplier`=" + insert_suplier + ", "
                            + "`keterangan`='" + txt_keterangan.getText() + "' "
                            + "WHERE `kode_pembelian_bahan_jadi` = '" + Table_pembelian_bahan_jadi.getValueAt(j, 0) + "'";
                    executeSQLQuery(Query, "updated !");
                }
            }
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JPanel_PembelianBahanJadi.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_updateActionPerformed

    private void button_insertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_insertActionPerformed
        // TODO add your handling code here:
        Boolean Check = true;
        String tgl_pembelian = null;
        int kpg = 0;
        float gram = 0;
        double harga = 0;
        try {
            tgl_pembelian = dateFormat.format(Date_pembelian.getDate());
            kpg = Integer.parseInt(txt_keping.getText());
            gram = Float.parseFloat(txt_berat.getText());
        } catch (NumberFormatException | NullPointerException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JPanel_PembelianBahanJadi.class.getName()).log(Level.SEVERE, null, e);
            Check = false;
        }
        if (Check) {
            String insert_suplier = "(SELECT `kode_supplier` FROM `tb_supplier` WHERE `nama_supplier` = '" + ComboBox_Supplier.getSelectedItem().toString() + "')";
            String Query = "INSERT INTO `tb_pembelian_barang_jadi`(`kode_pembelian_bahan_jadi`, `tanggal_pembelian`, `jumlah_keping`, `berat`, `kode_supplier`, `keterangan`) "
                    + "VALUES ('" + txt_kode_pembelian.getText() + "','" + tgl_pembelian + "','" + kpg + "','" + gram + "'," + insert_suplier + ", '" + txt_keterangan.getText() + "')";
            executeSQLQuery(Query, "inserted !");
        }
    }//GEN-LAST:event_button_insertActionPerformed

    private void button_input_gradingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_gradingActionPerformed
        // TODO add your handling code here:
        int j = Table_pembelian_bahan_jadi.getSelectedRow();
        try {
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih salah satu data pada tabel !");
            } else {
                String kode = Table_pembelian_bahan_jadi.getValueAt(j, 0).toString();
                int berat = Math.round((float) Table_pembelian_bahan_jadi.getValueAt(j, 3));
                JDialog_Grading_PembelianBJ dialog = new JDialog_Grading_PembelianBJ();
                dialog.pack();
                dialog.setResizable(false);
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
                dialog.setEnabled(true);
                dialog.init(kode, berat);
            }
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JPanel_PembelianBahanJadi.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_input_gradingActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_SearchBy;
    private javax.swing.JComboBox<String> ComboBox_Supplier;
    private com.toedter.calendar.JDateChooser Date_pembelian;
    private com.toedter.calendar.JDateChooser Date_pembelian1;
    private com.toedter.calendar.JDateChooser Date_pembelian2;
    public static javax.swing.JTable Table_hasil_lab;
    public static javax.swing.JTable Table_hasil_masak;
    public static javax.swing.JTable Table_pembelian_bahan_jadi;
    private javax.swing.JButton button_clear;
    public javax.swing.JButton button_delete;
    public javax.swing.JButton button_input_grading;
    public javax.swing.JButton button_insert;
    public javax.swing.JButton button_save_hasil_lab;
    public javax.swing.JButton button_save_hasil_masak;
    private javax.swing.JButton button_search;
    public javax.swing.JButton button_update;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel_Bahan_Baku_Keluar;
    private javax.swing.JPanel jPanel_operation_bahan_baku1;
    private javax.swing.JPanel jPanel_operation_bahan_baku2;
    private javax.swing.JPanel jPanel_operation_bahan_baku3;
    private javax.swing.JPanel jPanel_search_baku_keluar;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label1_1;
    private javax.swing.JLabel label1_2;
    private javax.swing.JLabel label1_3;
    private javax.swing.JLabel label1_4;
    private javax.swing.JLabel label1_5;
    private javax.swing.JLabel label1_6;
    private javax.swing.JLabel label1_7;
    private javax.swing.JLabel label2_1;
    private javax.swing.JLabel label2_2;
    private javax.swing.JLabel label2_3;
    private javax.swing.JLabel label2_4;
    private javax.swing.JLabel label2_5;
    private javax.swing.JLabel label2_6;
    private javax.swing.JLabel label2_7;
    private javax.swing.JLabel label2_8;
    private javax.swing.JLabel label3_1;
    private javax.swing.JLabel label3_2;
    private javax.swing.JLabel label3_3;
    private javax.swing.JLabel label3_4;
    private javax.swing.JLabel label3_5;
    private javax.swing.JLabel label_kode_pembelian1;
    private javax.swing.JLabel label_kode_pembelian2;
    private javax.swing.JLabel label_kode_pembelian3;
    private javax.swing.JLabel label_kode_pembelian4;
    private javax.swing.JLabel label_kode_pembelian5;
    private javax.swing.JLabel label_tgl_pembelian;
    private javax.swing.JLabel label_total_data;
    public static javax.swing.JTextField txt_As;
    public static javax.swing.JTextField txt_Cu;
    public static javax.swing.JTextField txt_DETERGEN;
    public static javax.swing.JTextField txt_H2O2;
    public static javax.swing.JTextField txt_NO2;
    public static javax.swing.JTextField txt_NO3;
    public static javax.swing.JTextField txt_PH;
    public static javax.swing.JTextField txt_Pb;
    public static javax.swing.JTextField txt_aroma;
    public static javax.swing.JTextField txt_berat;
    public static javax.swing.JTextField txt_expansion_rate;
    private javax.swing.JTextField txt_grade_hasil_lab;
    private javax.swing.JTextField txt_grade_hasil_masak;
    public static javax.swing.JTextField txt_kebersihan;
    public static javax.swing.JTextField txt_keping;
    public static javax.swing.JTextField txt_keterangan;
    private javax.swing.JTextField txt_keyword;
    private javax.swing.JTextField txt_kode_pembelian;
    private javax.swing.JTextField txt_kode_pembelian_hasil_lab;
    private javax.swing.JTextField txt_kode_pembelian_hasil_masak;
    public static javax.swing.JTextField txt_penilaian;
    public static javax.swing.JTextField txt_rasa;
    // End of variables declaration//GEN-END:variables
}
