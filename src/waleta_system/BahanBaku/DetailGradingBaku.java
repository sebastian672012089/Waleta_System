package waleta_system.BahanBaku;

import java.awt.Color;
import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import static waleta_system.BahanBaku.JPanel_BahanBakuMasuk.Table_Bahan_Baku_Masuk;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;
import waleta_system.Class.TableColumnHider;
import waleta_system.Class.Utility;
import waleta_system.MainForm;

public class DetailGradingBaku extends javax.swing.JFrame {

    String sql = null;
    ResultSet rs;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    java.util.Date date = new java.util.Date();//date berisikan tanggal hari ini
    DecimalFormat decimalFormat = new DecimalFormat();
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public DetailGradingBaku() {
        initComponents();
    }

    public void init() {
        this.setResizable(false);
        //coding untuk menambahkan confirm dialog ketika default close operation di klik
//        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
//        this.addWindowListener(new WindowAdapter() {
//            @Override
//            public void windowClosing(WindowEvent e) {
//                int x = JOptionPane.showConfirmDialog(null, "do you want really close app?", "exit on close", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
//                if (x == JOptionPane.YES_OPTION) {
//                    e.getWindow().dispose();
//                }
//            } 
//        });

        int j = JPanel_BahanBakuMasuk.Table_Bahan_Baku_Masuk.getSelectedRow();
        label_NO_KARTU.setText(Table_Bahan_Baku_Masuk.getValueAt(j, 0).toString());
        label_berat_awal.setText(Table_Bahan_Baku_Masuk.getValueAt(j, 9).toString());
        label_berat_awal1.setText(Table_Bahan_Baku_Masuk.getValueAt(j, 9).toString());
        refreshTable_grading();
        refreshTable_gradeSupplier();
        if (label_NO_KARTU.getText().contains("CMP")) {//kartu CMP tidak bisa di DONE, supaya tidak merubah kadar air kartu
            button_grading_selesai.setEnabled(false);
        } else {
            button_grading_selesai.setEnabled(true);
        }
        try {
            ComboBox_kode_grading.removeAllItems();
            sql = "SELECT `kode_grade` FROM `tb_grade_bahan_baku` "
                    + "WHERE `status_grade_baku` = 1 ORDER BY `kode_grade` ASC";
            ResultSet rs1 = Utility.db.getStatement().executeQuery(sql);
            while (rs1.next()) {
                ComboBox_kode_grading.addItem(rs1.getString("kode_grade"));
            }
            AutoCompleteDecorator.decorate(ComboBox_kode_grading);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, e);
        }
        Table_Grading_Bahan_Baku.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && Table_Grading_Bahan_Baku.getSelectedRow() != -1) {
                    int i = Table_Grading_Bahan_Baku.getSelectedRow();
                    ComboBox_kode_grading.setSelectedItem(Table_Grading_Bahan_Baku.getValueAt(i, 0).toString());
                    txt_Jumlah_keping.setText(Table_Grading_Bahan_Baku.getValueAt(i, 1).toString());
                    txt_jumlah_berat.setText(Table_Grading_Bahan_Baku.getValueAt(i, 2).toString());
                }
            }
        });
        Table_Grade_Supplier.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && Table_Grade_Supplier.getSelectedRow() != -1) {
                    int i = Table_Grade_Supplier.getSelectedRow();
                    txt_grade_supplier.setText(Table_Grade_Supplier.getValueAt(i, 1).toString());
                    txt_berat_grade_supplier.setText(Table_Grade_Supplier.getValueAt(i, 2).toString());
                    txt_harga_grade_supplier.setText(Table_Grade_Supplier.getValueAt(i, 3).toString());
                }
            }
        });
    }

    public void HidePriceColumn() {
        TableColumnHider hider1 = new TableColumnHider(Table_Grade_Supplier);
        hider1.hide("Harga / Kg");
        hider1.hide("Total");
        jLabel17.setVisible(false);
        label_total_harga_grade_supplier.setVisible(false);
    }

    public void ShowPriceColumn() {
        TableColumnHider hider1 = new TableColumnHider(Table_Grade_Supplier);
        hider1.show("Harga / Kg");
        hider1.show("Total");
        jLabel17.setVisible(true);
        label_total_harga_grade_supplier.setVisible(true);
    }

    public void refreshTable_grading() {
        try {
            int total_kpg = 0;
            int total_gram = 0;
            decimalFormat.setMaximumFractionDigits(2);
            DefaultTableModel model = (DefaultTableModel) Table_Grading_Bahan_Baku.getModel();
            model.setRowCount(0);
            sql = "SELECT `tb_grading_bahan_baku`.`kode_grade`, `jumlah_keping`, `total_berat`, `harga_bahanbaku`, `pekerja_grading`, "
                    + "`kpg_lp`, `gram_lp`, `kpg_jual`, `gram_jual`, `kpg_cmp`, `gram_cmp`\n"
                    + "FROM `tb_grading_bahan_baku` "
                    + "LEFT JOIN (SELECT `no_kartu_waleta`, `kode_grade`, SUM(`tb_laporan_produksi`.`jumlah_keping`) AS 'kpg_lp', SUM(`tb_laporan_produksi`.`berat_basah`) AS 'gram_lp' FROM `tb_laporan_produksi` GROUP BY `no_kartu_waleta`, `kode_grade`) LP "
                    + "ON `tb_grading_bahan_baku`.`no_kartu_waleta` = LP.`no_kartu_waleta` AND `tb_grading_bahan_baku`.`kode_grade` = LP.`kode_grade`\n"
                    + "LEFT JOIN (SELECT `no_kartu_waleta`, `kode_grade`, SUM(`total_keping_keluar`) AS 'kpg_jual', SUM(`total_berat_keluar`) AS 'gram_jual' FROM `tb_bahan_baku_keluar` GROUP BY `no_kartu_waleta`, `kode_grade`) JUAL "
                    + "ON `tb_grading_bahan_baku`.`no_kartu_waleta` = JUAL.`no_kartu_waleta` AND `tb_grading_bahan_baku`.`kode_grade` = JUAL.`kode_grade`\n"
                    + "LEFT JOIN (SELECT `no_grading`, SUM(`tb_kartu_cmp_detail`.`keping`) AS 'kpg_cmp', SUM(`tb_kartu_cmp_detail`.`gram`) AS 'gram_cmp' FROM `tb_kartu_cmp_detail` GROUP BY `no_grading`) CMP "
                    + "ON `tb_grading_bahan_baku`.`no_grading` = CMP.`no_grading`\n"
                    + "WHERE `tb_grading_bahan_baku`.`no_kartu_waleta`='" + label_NO_KARTU.getText() + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getString("kode_grade");
                row[1] = rs.getInt("jumlah_keping");
                row[2] = rs.getInt("total_berat");
                float kpg = rs.getInt("jumlah_keping");
                float gr = rs.getInt("total_berat");
                if (kpg > 0) {
                    float rerata = gr / kpg;
                    row[3] = Math.round(rerata * 100.0) / 100.0;
                } else {
                    row[3] = 0;
                }
                row[4] = (rs.getInt("kpg_lp") + rs.getInt("kpg_jual") + rs.getInt("kpg_cmp"));
                row[5] = (rs.getInt("gram_lp") + rs.getInt("gram_jual") + rs.getInt("gram_cmp"));
                row[6] = rs.getString("pekerja_grading");
                model.addRow(row);
                total_kpg = total_kpg + rs.getInt("jumlah_keping");
                total_gram = total_gram + rs.getInt("total_berat");
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_Grading_Bahan_Baku);

            Table_Grading_Bahan_Baku.setDefaultRenderer(Float.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (Float.valueOf(Table_Grading_Bahan_Baku.getValueAt(row, 3).toString()) > 0 && Float.valueOf(Table_Grading_Bahan_Baku.getValueAt(row, 3).toString()) <= 3) {
                        if (isSelected) {
                            comp.setBackground(Table_Grading_Bahan_Baku.getSelectionBackground());
                            comp.setForeground(Table_Grading_Bahan_Baku.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.red);
                            comp.setForeground(Color.WHITE);
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(Table_Grading_Bahan_Baku.getSelectionBackground());
                            comp.setForeground(Table_Grading_Bahan_Baku.getSelectionForeground());
                        } else {
                            comp.setBackground(Table_Grading_Bahan_Baku.getBackground());
                            comp.setForeground(Table_Grading_Bahan_Baku.getForeground());
                        }
                    }
                    return comp;
                }
            });
            Table_Grading_Bahan_Baku.repaint();
            int rowData = Table_Grading_Bahan_Baku.getRowCount();
            label_total_data_grading.setText(Integer.toString(rowData));
            label_total_keping.setText(Integer.toString(total_kpg));
            label_total_gram.setText(Integer.toString(total_gram));

            //menghitung sisa berat yang belum di grading
            int j = JPanel_BahanBakuMasuk.Table_Bahan_Baku_Masuk.getSelectedRow();
            int berat = (int) Table_Bahan_Baku_Masuk.getValueAt(j, 9);
            int sisa = berat - total_gram;
            label_belum_grading.setText(Integer.toString(sisa));
        } catch (SQLException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_gradeSupplier() {
        try {
            long total_gram_grade_supplier = 0, total_harga_grade_supplier = 0;
            DefaultTableModel model = (DefaultTableModel) Table_Grade_Supplier.getModel();
            model.setRowCount(0);
            sql = "SELECT `no_grading`, `no_kartu_waleta`, `grade_supplier`, `gram`, `harga_kg`, (`gram` * `harga_kg` / 1000) AS 'total' FROM `tb_grading_baku_supplier` "
                    + "WHERE `no_kartu_waleta`='" + label_NO_KARTU.getText() + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[5];
            int no_urut = 1;
            while (rs.next()) {
                row[0] = no_urut;
                row[1] = rs.getString("grade_supplier");
                row[2] = rs.getInt("gram");
                row[3] = rs.getInt("harga_kg");
                row[4] = rs.getLong("total");
                total_gram_grade_supplier = total_gram_grade_supplier + rs.getLong("gram");
                total_harga_grade_supplier = total_harga_grade_supplier + rs.getLong("total");
                no_urut++;
                model.addRow(row);
            }
//            decimalFormat.setGroupingUsed(true);
            label_total_berat_grade_supplier.setText(decimalFormat.format(total_gram_grade_supplier));
            label_total_harga_grade_supplier.setText(decimalFormat.format(total_harga_grade_supplier));
            label_total_data_grade_supplier.setText(decimalFormat.format(Table_Grade_Supplier.getRowCount()));
        } catch (SQLException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        ColumnsAutoSizer.sizeColumnsToFit(Table_Grade_Supplier);
    }

    public void executeSQLQuery(String query, String message) {
        Utility.db.getConnection();
        try {
            Utility.db.getConnection().createStatement();
            if ((Utility.db.getStatement().executeUpdate(query)) == 1) {
                refreshTable_grading();
                clear();
                JOptionPane.showMessageDialog(this, "data " + message + " Successfully");
            } else {
                JOptionPane.showMessageDialog(this, "data not " + message);
            }
        } catch (SQLException | HeadlessException e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void clear() {
        ComboBox_kode_grading.setSelectedItem(null);
        txt_Jumlah_keping.setText(null);
        txt_jumlah_berat.setText(null);
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
        jPanel6 = new javax.swing.JPanel();
        label_NO_KARTU = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        label_berat_awal1 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        label_total_data_grade_supplier = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Table_Grade_Supplier = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        Button_Tambah_data_grade_supplier = new javax.swing.JButton();
        Button_hapus_grade_supplier = new javax.swing.JButton();
        txt_berat_grade_supplier = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        txt_grade_supplier = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        txt_harga_grade_supplier = new javax.swing.JTextField();
        Button_Edit_data_grade_supplier = new javax.swing.JButton();
        button_export_grade_supplier = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        label_total_berat_grade_supplier = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        label_total_harga_grade_supplier = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        label_berat_awal = new javax.swing.JLabel();
        label_belum_grading = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        label_total_data_grading = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_Grading_Bahan_Baku = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        Button_Tambah_data_grading = new javax.swing.JButton();
        txt_Jumlah_keping = new javax.swing.JTextField();
        Button_Edit_data_grading = new javax.swing.JButton();
        Button_hapus_data_grading = new javax.swing.JButton();
        button_grading_selesai = new javax.swing.JButton();
        txt_jumlah_berat = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        ComboBox_kode_grading = new javax.swing.JComboBox<>();
        button_export = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        button_print = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        button_printLabel = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        label_total_keping = new javax.swing.JLabel();
        label_total_gram = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        button_PrintSatuLabel = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Detail Grading");
        setIconImages(null);

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));

        label_NO_KARTU.setBackground(new java.awt.Color(255, 255, 255));
        label_NO_KARTU.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        label_NO_KARTU.setText("NO KARTU");

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_berat_awal1.setBackground(new java.awt.Color(255, 255, 255));
        label_berat_awal1.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); // NOI18N
        label_berat_awal1.setText("Berat");

        jLabel13.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); // NOI18N
        jLabel13.setText("Total Data            :");

        label_total_data_grade_supplier.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_grade_supplier.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); // NOI18N
        label_total_data_grade_supplier.setText("total");

        jLabel14.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); // NOI18N
        jLabel14.setText("Total Berat           :");

        Table_Grade_Supplier.setAutoCreateRowSorter(true);
        Table_Grade_Supplier.setFont(new java.awt.Font("Segoe UI Light", 0, 12)); // NOI18N
        Table_Grade_Supplier.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Grade", "Gram", "Harga / Kg", "Total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Double.class
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
        Table_Grade_Supplier.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(Table_Grade_Supplier);

        jPanel5.setBackground(new java.awt.Color(204, 255, 204));
        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        Button_Tambah_data_grade_supplier.setBackground(new java.awt.Color(255, 255, 255));
        Button_Tambah_data_grade_supplier.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); // NOI18N
        Button_Tambah_data_grade_supplier.setText("+ Add");
        Button_Tambah_data_grade_supplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_Tambah_data_grade_supplierActionPerformed(evt);
            }
        });

        Button_hapus_grade_supplier.setBackground(new java.awt.Color(255, 255, 255));
        Button_hapus_grade_supplier.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); // NOI18N
        Button_hapus_grade_supplier.setText("Delete");
        Button_hapus_grade_supplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_hapus_grade_supplierActionPerformed(evt);
            }
        });

        txt_berat_grade_supplier.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); // NOI18N
        txt_berat_grade_supplier.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_berat_grade_supplierKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_berat_grade_supplierKeyTyped(evt);
            }
        });

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel16.setText("Grade :");

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel18.setText("Berat :");

        txt_grade_supplier.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); // NOI18N

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel21.setText("Harga / Kg :");

        txt_harga_grade_supplier.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); // NOI18N
        txt_harga_grade_supplier.setText("0");
        txt_harga_grade_supplier.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_harga_grade_supplierKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_harga_grade_supplierKeyTyped(evt);
            }
        });

        Button_Edit_data_grade_supplier.setBackground(new java.awt.Color(255, 255, 255));
        Button_Edit_data_grade_supplier.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); // NOI18N
        Button_Edit_data_grade_supplier.setText("Edit");
        Button_Edit_data_grade_supplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_Edit_data_grade_supplierActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel16)
                                .addGap(0, 163, Short.MAX_VALUE))
                            .addComponent(txt_grade_supplier))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_berat_grade_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_harga_grade_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel21)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(Button_Edit_data_grade_supplier)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Button_Tambah_data_grade_supplier)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Button_hapus_grade_supplier)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addComponent(jLabel16)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txt_grade_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addComponent(jLabel21)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txt_harga_grade_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_berat_grade_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Button_Tambah_data_grade_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Button_hapus_grade_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Button_Edit_data_grade_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        button_export_grade_supplier.setBackground(new java.awt.Color(255, 255, 255));
        button_export_grade_supplier.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); // NOI18N
        button_export_grade_supplier.setText("Export");
        button_export_grade_supplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_grade_supplierActionPerformed(evt);
            }
        });

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); // NOI18N
        jLabel19.setText("Grams");

        jLabel20.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); // NOI18N
        jLabel20.setText("Total Berat Awal   :");

        label_total_berat_grade_supplier.setBackground(new java.awt.Color(255, 255, 255));
        label_total_berat_grade_supplier.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); // NOI18N
        label_total_berat_grade_supplier.setText("Berat");

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); // NOI18N
        jLabel24.setText("Grams");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Segoe UI Light", 0, 14)); // NOI18N
        jLabel15.setText("Grade Supplier");

        jLabel17.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); // NOI18N
        jLabel17.setText("Total Harga          :");

        label_total_harga_grade_supplier.setBackground(new java.awt.Color(255, 255, 255));
        label_total_harga_grade_supplier.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); // NOI18N
        label_total_harga_grade_supplier.setText("Harga");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(button_export_grade_supplier))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_berat_awal1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel24))
                            .addComponent(jLabel15)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_berat_grade_supplier)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel19))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data_grade_supplier))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_harga_grade_supplier)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_export_grade_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(label_berat_awal1)
                    .addComponent(jLabel24))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(label_total_berat_grade_supplier)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(label_total_harga_grade_supplier))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_total_data_grade_supplier)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_berat_awal.setBackground(new java.awt.Color(255, 255, 255));
        label_berat_awal.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); // NOI18N
        label_berat_awal.setText("Berat");

        label_belum_grading.setBackground(new java.awt.Color(255, 255, 255));
        label_belum_grading.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); // NOI18N
        label_belum_grading.setText("Berat");

        jLabel3.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); // NOI18N
        jLabel3.setText("Total Data                                            :");

        label_total_data_grading.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_grading.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); // NOI18N
        label_total_data_grading.setText("total");

        jLabel2.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); // NOI18N
        jLabel2.setText("Total Berat Yang Sudah Grading           :");

        Table_Grading_Bahan_Baku.setAutoCreateRowSorter(true);
        Table_Grading_Bahan_Baku.setFont(new java.awt.Font("Segoe UI Light", 0, 12)); // NOI18N
        Table_Grading_Bahan_Baku.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade", "Jumlah Kpg", "Gram", "Rerata per Kpg", "Kpg Keluar", "Grm Keluar", "Pekerja"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class
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
        Table_Grading_Bahan_Baku.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Table_Grading_Bahan_Baku);

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        Button_Tambah_data_grading.setBackground(new java.awt.Color(255, 255, 255));
        Button_Tambah_data_grading.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); // NOI18N
        Button_Tambah_data_grading.setText("+ Add Data");
        Button_Tambah_data_grading.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_Tambah_data_gradingActionPerformed(evt);
            }
        });

        txt_Jumlah_keping.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); // NOI18N

        Button_Edit_data_grading.setBackground(new java.awt.Color(255, 255, 255));
        Button_Edit_data_grading.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); // NOI18N
        Button_Edit_data_grading.setText("Edit");
        Button_Edit_data_grading.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_Edit_data_gradingActionPerformed(evt);
            }
        });

        Button_hapus_data_grading.setBackground(new java.awt.Color(255, 255, 255));
        Button_hapus_data_grading.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); // NOI18N
        Button_hapus_data_grading.setText("Delete");
        Button_hapus_data_grading.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_hapus_data_gradingActionPerformed(evt);
            }
        });

        button_grading_selesai.setBackground(new java.awt.Color(255, 255, 255));
        button_grading_selesai.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); // NOI18N
        button_grading_selesai.setText("DONE");
        button_grading_selesai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_grading_selesaiActionPerformed(evt);
            }
        });

        txt_jumlah_berat.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); // NOI18N

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("Grade Bulu :");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Keping :");

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("Berat :");

        ComboBox_kode_grading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_kode_grading.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(ComboBox_kode_grading, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_Jumlah_keping, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(txt_jumlah_berat, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 239, Short.MAX_VALUE)
                        .addComponent(Button_Tambah_data_grading)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Button_Edit_data_grading)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Button_hapus_data_grading)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_grading_selesai)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_Jumlah_keping, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_jumlah_berat, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_kode_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Button_Tambah_data_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Button_Edit_data_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Button_hapus_data_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_grading_selesai, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        button_export.setBackground(new java.awt.Color(255, 255, 255));
        button_export.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); // NOI18N
        button_export.setText("Export");
        button_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportActionPerformed(evt);
            }
        });

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); // NOI18N
        jLabel9.setText("Grams");

        button_print.setBackground(new java.awt.Color(255, 255, 255));
        button_print.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); // NOI18N
        button_print.setText("Laporan");
        button_print.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_printActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); // NOI18N
        jLabel5.setText("Total Berat Awal                                   :");

        jLabel6.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); // NOI18N
        jLabel6.setText("Total Keping                                        :");

        jLabel4.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); // NOI18N
        jLabel4.setText("Total Sisa Berat yang belum di Grading :");

        button_printLabel.setBackground(new java.awt.Color(255, 255, 255));
        button_printLabel.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); // NOI18N
        button_printLabel.setText("Semua Label");
        button_printLabel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_printLabelActionPerformed(evt);
            }
        });

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); // NOI18N
        jLabel8.setText("Grams");

        label_total_keping.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); // NOI18N
        label_total_keping.setText("keping");

        label_total_gram.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); // NOI18N
        label_total_gram.setText("Berat");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); // NOI18N
        jLabel7.setText("Grams");

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(new java.awt.Font("Segoe UI Light", 0, 14)); // NOI18N
        jLabel25.setText("Grading Baku Waleta");

        button_PrintSatuLabel.setBackground(new java.awt.Color(255, 255, 255));
        button_PrintSatuLabel.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); // NOI18N
        button_PrintSatuLabel.setText("1 Label");
        button_PrintSatuLabel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_PrintSatuLabelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(label_total_data_grading)
                                    .addComponent(label_total_keping)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(label_berat_awal, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(label_belum_grading, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(label_total_gram, javax.swing.GroupLayout.Alignment.LEADING))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)))))
                            .addComponent(jLabel25)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(button_print)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_printLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_PrintSatuLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export)))
                        .addGap(0, 233, Short.MAX_VALUE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel25)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_print, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_printLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_PrintSatuLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(label_berat_awal)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(label_belum_grading)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(label_total_gram)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(label_total_keping))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_total_data_grading)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Segoe UI Light", 0, 14)); // NOI18N
        jLabel1.setText("NO KARTU WALETA :");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_NO_KARTU)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(label_NO_KARTU))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void Button_Tambah_data_gradingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_Tambah_data_gradingActionPerformed
        // TODO add your handling code here:
        try {
            DefaultTableModel Table = (DefaultTableModel) Table_Grading_Bahan_Baku.getModel();
            int total_baris = Table.getRowCount();
            Boolean Check = true;
            String grade = ComboBox_kode_grading.getSelectedItem().toString().trim();
            String jenis_bentuk = "";
            String qry_grade = "SELECT `jenis_bentuk` FROM `tb_grade_bahan_baku` WHERE `kode_grade` = '" + grade + "'";
            ResultSet rst_grade = Utility.db.getStatement().executeQuery(qry_grade);
            if (rst_grade.next()) {
                jenis_bentuk = rst_grade.getString("jenis_bentuk");
            }

//            String cek_dobel_input = "SELECT * FROM `tb_grading_bahan_baku` WHERE `no_kartu_waleta` = '" + label_NO_KARTU.getText() + "' AND `kode_grade` = '" + ComboBox_kode_grading.getSelectedItem() + "'";
//            ResultSet result_cek = Utility.db.getStatement().executeQuery(cek_dobel_input);
//            if (result_cek.next()) {
//                JOptionPane.showMessageDialog(this, "Maaf untuk GRADE (" + ComboBox_kode_grading.getSelectedItem() + ") pada No Kartu " + label_NO_KARTU.getText() + " Sudah terinput");
//                Check = false;
//            }
            for (int i = 0; i < total_baris; i++) {
                if (ComboBox_kode_grading.getSelectedItem().equals(Table_Grading_Bahan_Baku.getValueAt(i, 0))) {
                    JOptionPane.showMessageDialog(this, "Grade (" + ComboBox_kode_grading.getSelectedItem() + ") sudah ada");
                    Check = false;
                }
            }

            float kpg = Float.valueOf(txt_Jumlah_keping.getText());
            float berat = Float.valueOf(txt_jumlah_berat.getText());
            if (kpg > 0) {
                float berat_kpg = berat / kpg;
                if (berat_kpg > 13.5 || berat_kpg < 3) {
                    JOptionPane.showMessageDialog(this, "Berat / Kpg tidak wajar silahkan cek kembali !", "Warning !", JOptionPane.ERROR_MESSAGE);
//                    Check = false;
                }
            }

            if (Check 
                    && Integer.parseInt(txt_Jumlah_keping.getText()) < 5 
                    && (jenis_bentuk.equalsIgnoreCase("Mangkok") || jenis_bentuk.equalsIgnoreCase("Oval") || jenis_bentuk.equalsIgnoreCase("Segitiga")) 
                    && !grade.equals("Mangkok Antik")) {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Jumlah Keping kurang dari 5 \nAre Sure You Want to Continue?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    JDialog_otorisasi_grading dialog = new JDialog_otorisasi_grading(new javax.swing.JFrame(), true);
                    dialog.pack();
                    dialog.setLocationRelativeTo(this);
                    dialog.setVisible(true);
                    dialog.setEnabled(true);
                    Check = dialog.OK();
                } else {
                    Check = false;                     
                }
            }

            if (Check) {
                String Query = "INSERT INTO `tb_grading_bahan_baku`(`no_kartu_waleta`, `kode_grade`, `jumlah_keping`, `total_berat`) "
                        + "VALUES ('" + label_NO_KARTU.getText() + "',TRIM('" + ComboBox_kode_grading.getSelectedItem().toString().trim() + "'),'" + txt_Jumlah_keping.getText() + "','" + txt_jumlah_berat.getText() + "')";
                executeSQLQuery(Query, "inserted !");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_Button_Tambah_data_gradingActionPerformed

    private void Button_Edit_data_gradingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_Edit_data_gradingActionPerformed
        int i = Table_Grading_Bahan_Baku.getSelectedRow();
        if (i == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih salah satu data pada tabel !");
        } else {
            boolean check = true;
            float kpg_keluar = Float.valueOf(Table_Grading_Bahan_Baku.getValueAt(i, 4).toString());
            float berat_keluar = Float.valueOf(Table_Grading_Bahan_Baku.getValueAt(i, 5).toString());
            if (kpg_keluar > Integer.valueOf(txt_Jumlah_keping.getText())) {
                check = false;
                JOptionPane.showMessageDialog(this, "Maaf kpg melebihi dari yang sudah dikeluarkan !");
            } else if (berat_keluar > Integer.valueOf(txt_jumlah_berat.getText())) {
                check = false;
                JOptionPane.showMessageDialog(this, "Maaf gram melebihi dari yang sudah dikeluarkan !");
            }

            if (check) {
                if (ComboBox_kode_grading.getSelectedItem().equals(Table_Grading_Bahan_Baku.getValueAt(i, 0))) {
                    String Query = "UPDATE `tb_grading_bahan_baku` SET `jumlah_keping` = '" + txt_Jumlah_keping.getText() + "', `total_berat` = '" + txt_jumlah_berat.getText() + "' "
                            + "WHERE `tb_grading_bahan_baku`.`no_kartu_waleta` = '" + label_NO_KARTU.getText() + "' AND `tb_grading_bahan_baku`.`kode_grade` = '" + ComboBox_kode_grading.getSelectedItem().toString().trim() + "'";
                    executeSQLQuery(Query, "updated !");
                } else {
                    JOptionPane.showMessageDialog(this, "Tidak bisa edit Kode Grade, hanya bisa edit jumlah jika salah memasukkan");
                }
            }
        }
    }//GEN-LAST:event_Button_Edit_data_gradingActionPerformed

    private void Button_hapus_data_gradingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_hapus_data_gradingActionPerformed
        try {
            int i = Table_Grading_Bahan_Baku.getSelectedRow();
            if (i == -1) {
                JOptionPane.showMessageDialog(this, "Klik dulu data di tabel yang akan di hapus");
            } else {
                boolean check = true;
                String no_kartu = label_NO_KARTU.getText();
                String grade = Table_Grading_Bahan_Baku.getValueAt(i, 0).toString();
                String qry = "SELECT SUM(`berat_basah`) AS 'berat', SUM(`jumlah_keping`) AS 'keping' "
                        + "FROM `tb_laporan_produksi` WHERE `kode_grade` = '" + grade + "' AND `no_kartu_waleta` = '" + no_kartu + "'";
                ResultSet rst = Utility.db.getStatement().executeQuery(qry);
                if (rst.next()) {
                    if (rst.getInt("berat") > 0) {
                        check = false;
                        JOptionPane.showMessageDialog(this, "Maaf grade ini sudah di keluar ke produksi, tidak di perbolehkan untuk di hapus !");
                    }
                }

                if (check) {
                    int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Warning", 0);
                    if (dialogResult == JOptionPane.YES_OPTION) {
                        // delete code here
                        String Query = "DELETE FROM `tb_grading_bahan_baku` WHERE `tb_grading_bahan_baku`.`no_kartu_waleta` = \'" + label_NO_KARTU.getText() + "\' AND `tb_grading_bahan_baku`.`kode_grade` = \'" + ComboBox_kode_grading.getSelectedItem() + "\'";
                        executeSQLQuery(Query, "deleted !");
                    }
                }
            }
        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_Button_hapus_data_gradingActionPerformed

    private void button_grading_selesaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_grading_selesaiActionPerformed
        try {
            int j = JPanel_BahanBakuMasuk.Table_Bahan_Baku_Masuk.getSelectedRow();
            decimalFormat = Utility.DecimalFormatUS(decimalFormat);
            decimalFormat.setMaximumFractionDigits(2);

            int a = Integer.valueOf(label_berat_awal.getText());
            int b = Integer.valueOf(label_total_gram.getText());
            float y = Float.valueOf(Table_Bahan_Baku_Masuk.getValueAt(j, 10).toString());
            float KA_akhir = (b - (((100 - y) / 100) * a)) / b;

            Utility.db.getConnection().createStatement();
            int x = JOptionPane.showConfirmDialog(null, "Apakah Sudah Selesai Grading ?", "Grading Selesai", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if (x == JOptionPane.YES_OPTION) {
                if (b == 0) {
                    JOptionPane.showMessageDialog(this, "Belum ada Data Grading");
                } else {
                    if (label_NO_KARTU.getText().contains("CMP")) {
                        sql = "UPDATE `tb_bahan_baku_masuk` SET `tgl_timbang` = '" + dateFormat.format(date) + "', `berat_real` = '" + label_total_gram.getText() + "', `keping_real` = '" + label_total_keping.getText() + "' "
                                + "WHERE `tb_bahan_baku_masuk`.`no_kartu_waleta` = '" + label_NO_KARTU.getText() + "'";
                    } else {
                        sql = "UPDATE `tb_bahan_baku_masuk` SET `tgl_timbang` = '" + dateFormat.format(date) + "', `berat_real` = '" + label_total_gram.getText() + "', `keping_real` = '" + label_total_keping.getText() + "', `kadar_air_bahan_baku` = '" + decimalFormat.format(KA_akhir * 100) + "' "
                                + "WHERE `tb_bahan_baku_masuk`.`no_kartu_waleta` = '" + label_NO_KARTU.getText() + "'";
                    }
                    if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                        JPanel_BahanBakuMasuk.button_search.doClick();
                        this.dispose();
                    }
                }
            }
        } catch (SQLException | HeadlessException e) {
//            JOptionPane.showMessageDialog(this, "there's must be a mistake please do tripple check");
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_grading_selesaiActionPerformed

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        DefaultTableModel model = (DefaultTableModel) Table_Grading_Bahan_Baku.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_exportActionPerformed

    private void button_printActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_printActionPerformed
        try {
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Catatan_Penerimaan_dan_Grading_Sarang_Burung_Mentah.jrxml");
            Map<String, Object> map = new HashMap<>();
            map.put("no_kartu_waleta", label_NO_KARTU.getText());//parameter name should be like it was named inside your report.
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(DetailGradingBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_printActionPerformed

    private void button_printLabelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_printLabelActionPerformed
        try {
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Label_baku.jrxml");
            Map<String, Object> map = new HashMap<>();
            map.put("no_kartu_waleta", label_NO_KARTU.getText());//parameter name should be like it was named inside your report.
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)

        } catch (JRException ex) {
            Logger.getLogger(DetailGradingBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_printLabelActionPerformed

    private void Button_Tambah_data_grade_supplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_Tambah_data_grade_supplierActionPerformed
        // TODO add your handling code here:
        try {
            boolean check = true;
            int berat = 0, harga = 0;
            if (txt_grade_supplier.getText().equals("") || txt_grade_supplier.getText() == null) {
                check = false;
                JOptionPane.showMessageDialog(this, "maaf, nama grade supplier tidak boleh kosong");
            }
            try {
                berat = Integer.valueOf(txt_berat_grade_supplier.getText());
                harga = Integer.valueOf(txt_harga_grade_supplier.getText());
            } catch (NumberFormatException e) {
                check = false;
                JOptionPane.showMessageDialog(this, "maaf format angka salah");
            }
            if (check) {
                String Query = "INSERT INTO `tb_grading_baku_supplier`(`no_kartu_waleta`, `grade_supplier`, `gram`, `harga_kg`) "
                        + "VALUES ('" + label_NO_KARTU.getText() + "','" + txt_grade_supplier.getText() + "','" + berat + "','" + harga + "')";
                Utility.db.getConnection().createStatement();
                if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                    refreshTable_gradeSupplier();
                    txt_grade_supplier.setText("");
                    txt_berat_grade_supplier.setText("");
                    txt_harga_grade_supplier.setText("");
                    txt_grade_supplier.requestFocus();
                }
            }
        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_Button_Tambah_data_grade_supplierActionPerformed

    private void Button_hapus_grade_supplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_hapus_grade_supplierActionPerformed
        // TODO add your handling code here:
        try {
            int i = Table_Grade_Supplier.getSelectedRow();
            if (i == -1) {
                JOptionPane.showMessageDialog(this, "Klik dulu data di tabel yang akan di hapus");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    String Query = "DELETE FROM `tb_grading_baku_supplier` "
                            + "WHERE `no_kartu_waleta` = '" + label_NO_KARTU.getText() + "' AND `grade_supplier` = '" + Table_Grade_Supplier.getValueAt(i, 1) + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(Query)) > 0) {
                        refreshTable_gradeSupplier();
                    }
                }
            }
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, e);
        } catch (SQLException ex) {
            Logger.getLogger(DetailGradingBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_Button_hapus_grade_supplierActionPerformed

    private void button_export_grade_supplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_grade_supplierActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_Grade_Supplier.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_export_grade_supplierActionPerformed

    private void button_PrintSatuLabelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_PrintSatuLabelActionPerformed
        // TODO add your handling code here:
        try {
            int x = Table_Grading_Bahan_Baku.getSelectedRow();
            if (x > -1) {
                String kode_grade = Table_Grading_Bahan_Baku.getValueAt(x, 0).toString();
                JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Label_grading_baku.jrxml");
                Map<String, Object> map = new HashMap<>();
                map.put("no_kartu_waleta", label_NO_KARTU.getText());//parameter name should be like it was named inside your report.
                map.put("kode_grade", kode_grade);//parameter name should be like it was named inside your report.
                JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
                JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
            }
        } catch (JRException ex) {
            Logger.getLogger(DetailGradingBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_PrintSatuLabelActionPerformed

    private void txt_berat_grade_supplierKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_berat_grade_supplierKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            Button_Tambah_data_grade_supplier.doClick();
        }
    }//GEN-LAST:event_txt_berat_grade_supplierKeyPressed

    private void txt_harga_grade_supplierKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_harga_grade_supplierKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            Button_Tambah_data_grade_supplier.doClick();
        }
    }//GEN-LAST:event_txt_harga_grade_supplierKeyPressed

    private void txt_berat_grade_supplierKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_berat_grade_supplierKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_berat_grade_supplierKeyTyped

    private void txt_harga_grade_supplierKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_harga_grade_supplierKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_harga_grade_supplierKeyTyped

    private void Button_Edit_data_grade_supplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_Edit_data_grade_supplierActionPerformed
        // TODO add your handling code here:
        try {
            int i = Table_Grade_Supplier.getSelectedRow();
            if (i == -1) {
                JOptionPane.showMessageDialog(this, "Klik dulu data di tabel yang akan di edit");
            } else {

                boolean check = true;
                int berat = 0, harga = 0;
                if (txt_grade_supplier.getText().equals("") || txt_grade_supplier.getText() == null) {
                    check = false;
                    JOptionPane.showMessageDialog(this, "maaf, nama grade supplier tidak boleh kosong");
                }
                try {
                    berat = Integer.valueOf(txt_berat_grade_supplier.getText());
                    harga = Integer.valueOf(txt_harga_grade_supplier.getText());
                } catch (NumberFormatException e) {
                    check = false;
                    JOptionPane.showMessageDialog(this, "maaf format angka salah");
                }
                if (check) {
                    String Query = "UPDATE `tb_grading_baku_supplier` SET "
                            + "`grade_supplier` = '" + txt_grade_supplier.getText() + "', "
                            + "`gram` = '" + berat + "', "
                            + "`harga_kg` = '" + harga + "' "
                            + "WHERE `no_kartu_waleta` = '" + label_NO_KARTU.getText() + "' AND `grade_supplier` = '" + Table_Grade_Supplier.getValueAt(i, 1) + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(Query)) > 0) {
                        refreshTable_gradeSupplier();
                        txt_grade_supplier.setText("");
                        txt_berat_grade_supplier.setText("");
                        txt_harga_grade_supplier.setText("");
                        txt_grade_supplier.requestFocus();
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(DetailGradingBaku.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_Button_Edit_data_grade_supplierActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DetailGradingBaku.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new DetailGradingBaku().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton Button_Edit_data_grade_supplier;
    public javax.swing.JButton Button_Edit_data_grading;
    public javax.swing.JButton Button_Tambah_data_grade_supplier;
    public javax.swing.JButton Button_Tambah_data_grading;
    public javax.swing.JButton Button_hapus_data_grading;
    public javax.swing.JButton Button_hapus_grade_supplier;
    private javax.swing.JComboBox<String> ComboBox_kode_grading;
    private javax.swing.JTable Table_Grade_Supplier;
    private javax.swing.JTable Table_Grading_Bahan_Baku;
    public javax.swing.JButton button_PrintSatuLabel;
    public javax.swing.JButton button_export;
    public javax.swing.JButton button_export_grade_supplier;
    public javax.swing.JButton button_grading_selesai;
    public javax.swing.JButton button_print;
    public javax.swing.JButton button_printLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    public static javax.swing.JLabel label_NO_KARTU;
    private javax.swing.JLabel label_belum_grading;
    private javax.swing.JLabel label_berat_awal;
    private javax.swing.JLabel label_berat_awal1;
    private javax.swing.JLabel label_total_berat_grade_supplier;
    private javax.swing.JLabel label_total_data_grade_supplier;
    private javax.swing.JLabel label_total_data_grading;
    private javax.swing.JLabel label_total_gram;
    private javax.swing.JLabel label_total_harga_grade_supplier;
    private javax.swing.JLabel label_total_keping;
    private javax.swing.JTextField txt_Jumlah_keping;
    private javax.swing.JTextField txt_berat_grade_supplier;
    private javax.swing.JTextField txt_grade_supplier;
    private javax.swing.JTextField txt_harga_grade_supplier;
    private javax.swing.JTextField txt_jumlah_berat;
    // End of variables declaration//GEN-END:variables
}
