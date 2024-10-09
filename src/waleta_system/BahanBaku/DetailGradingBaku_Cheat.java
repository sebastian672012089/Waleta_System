package waleta_system.BahanBaku;


import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;
import waleta_system.Class.TableColumnHider;
import waleta_system.Class.Utility;
import waleta_system.MainForm;

public class DetailGradingBaku_Cheat extends javax.swing.JFrame {

     
    String sql = null;
    ResultSet rs;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    java.util.Date date = new java.util.Date();//date berisikan tanggal hari ini
    DecimalFormat decimalFormat = new DecimalFormat();
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public DetailGradingBaku_Cheat() {
        initComponents();
    }

    public void init() {
        this.setResizable(false);
        try {
            
        } catch (Exception ex) {
            Logger.getLogger(DetailGradingBaku_Cheat.class.getName()).log(Level.SEVERE, null, ex);
        }

        int j = JPanel_BahanBakuMasuk_Cheat.Table_Bahan_Baku_Masuk_ct.getSelectedRow();
        label_NO_KARTU.setText(JPanel_BahanBakuMasuk_Cheat.Table_Bahan_Baku_Masuk_ct.getValueAt(j, 0).toString());
        label_berat_awal.setText(JPanel_BahanBakuMasuk_Cheat.Table_Bahan_Baku_Masuk_ct.getValueAt(j, 8).toString());
        refreshTable_grading();
        try {
            sql = "SELECT `kode_grade` FROM `tb_grade_bahan_baku` ORDER BY `kode_grade` ASC";
            ResultSet rs1 = Utility.db.getStatement().executeQuery(sql);
            while (rs1.next()) {
                ComboBox_kode_grading.addItem(rs1.getString("kode_grade"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, e);
        }
        AutoCompleteDecorator.decorate(ComboBox_kode_grading);
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
    }

    public void refreshTable_grading() {
        try {
            int total_kpg = 0;
            int total_gram = 0;
            decimalFormat.setMaximumFractionDigits(2);
            DefaultTableModel model = (DefaultTableModel) Table_Grading_Bahan_Baku.getModel();
            model.setRowCount(0);
            sql = "SELECT `kode_grade`, `jumlah_keping`, `total_berat`, `harga_bahanbaku` FROM `tb_grading_bahan_baku_cheat` WHERE `no_kartu_waleta`=\"" + label_NO_KARTU.getText() + "\"";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[4];
            while (rs.next()) {
                row[0] = rs.getString("kode_grade");
                row[1] = rs.getInt("jumlah_keping");
                row[2] = rs.getInt("total_berat");
                float kpg = rs.getInt("jumlah_keping");
                float gr = rs.getInt("total_berat");
                float rerata = gr / kpg;
                if (kpg > 0) {
                    row[3] = Math.round(rerata * 100.0) / 100.0;
                } else {
                    row[3] = rerata;
                }
                model.addRow(row);
                total_kpg = total_kpg + rs.getInt("jumlah_keping");
                total_gram = total_gram + rs.getInt("total_berat");
            }
            int rowData = Table_Grading_Bahan_Baku.getRowCount();
            label_total_data_grading.setText(Integer.toString(rowData));
            label_total_keping.setText(Integer.toString(total_kpg));
            label_total_gram.setText(Integer.toString(total_gram));

            //menghitung sisa berat yang belum di grading
            int j = JPanel_BahanBakuMasuk_Cheat.Table_Bahan_Baku_Masuk_ct.getSelectedRow();
            int berat = (int) JPanel_BahanBakuMasuk_Cheat.Table_Bahan_Baku_Masuk_ct.getValueAt(j, 8);
            int sisa = berat - total_gram;
            label_belum_grading.setText(Integer.toString(sisa));
        } catch (SQLException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
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
            JOptionPane.showMessageDialog(this, e.getMessage());
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
        ComboBox_kode_grading = new javax.swing.JComboBox<>();
        txt_Jumlah_keping = new javax.swing.JTextField();
        Button_Edit_data_grading = new javax.swing.JButton();
        Button_hapus_data_grading = new javax.swing.JButton();
        button_grading_selesai = new javax.swing.JButton();
        txt_jumlah_berat = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        button_print = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        label_total_keping = new javax.swing.JLabel();
        label_total_gram = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
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
                "Grade", "Jumlah Kpg", "Gram", "Rerata per Kpg"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
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

        ComboBox_kode_grading.setEditable(true);
        ComboBox_kode_grading.setFont(new java.awt.Font("Segoe UI Light", 0, 11)); // NOI18N

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
                        .addGap(0, 0, Short.MAX_VALUE)
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
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txt_Jumlah_keping, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_jumlah_berat, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(ComboBox_kode_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Button_Tambah_data_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Button_Edit_data_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Button_hapus_data_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_grading_selesai, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

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
        jLabel25.setText("Grading Baku Waleta Cheat");

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
                            .addComponent(button_print))
                        .addGap(0, 128, Short.MAX_VALUE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel25)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_print, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
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
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_NO_KARTU))
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(label_NO_KARTU))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
            String grade = ComboBox_kode_grading.getSelectedItem().toString();
            String qry_grade = "SELECT `jenis_bentuk` FROM `tb_grade_bahan_baku` WHERE `kode_grade` = '" + grade + "'";
            ResultSet rst_grade = Utility.db.getStatement().executeQuery(qry_grade);
            if (rst_grade.next() && rst_grade.getString("jenis_bentuk").length() > 6) {
                grade = rst_grade.getString("jenis_bentuk");
            }

//            String cek_dobel_input = "SELECT * FROM `tb_grading_bahan_baku_cheat` WHERE `no_kartu_waleta` = '" + label_NO_KARTU.getText() + "' AND `kode_grade` = '" + ComboBox_kode_grading.getSelectedItem() + "'";
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

            if (Check && Integer.parseInt(txt_Jumlah_keping.getText()) < 5 && (grade.contains("Mangkok") || grade.contains("Oval") || grade.contains("Segitiga")) && !grade.equals("Mangkok Antik")) {
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
                String Query = "INSERT INTO `tb_grading_bahan_baku_cheat`(`no_kartu_waleta`, `kode_grade`, `jumlah_keping`, `total_berat`) "
                        + "VALUES ('" + label_NO_KARTU.getText() + "','" + ComboBox_kode_grading.getSelectedItem() + "','" + txt_Jumlah_keping.getText() + "','" + txt_jumlah_berat.getText() + "')";
                executeSQLQuery(Query, "inserted !");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_Button_Tambah_data_gradingActionPerformed

    private void Button_Edit_data_gradingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_Edit_data_gradingActionPerformed
        try {
            int i = Table_Grading_Bahan_Baku.getSelectedRow();
            if (i == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih salah satu data pada tabel !");
            } else {
                boolean check = true;
                String no_kartu = label_NO_KARTU.getText();
                String grade = Table_Grading_Bahan_Baku.getValueAt(i, 0).toString();
                String qry = "SELECT SUM(`berat_basah`) AS 'berat', SUM(`jumlah_keping`) AS 'keping' "
                        + "FROM `tb_laporan_produksi` WHERE `kode_grade` = '" + grade + "' AND `no_kartu_waleta` = '" + no_kartu + "'";
                ResultSet rst = Utility.db.getStatement().executeQuery(qry);
                if (rst.next()) {
                    if (rst.getInt("berat") > Integer.valueOf(txt_jumlah_berat.getText())) {
                        check = false;
                        JOptionPane.showMessageDialog(this, "Maaf Grade tsb sudah dikeluarkan menjadi LP sebanyak " + rst.getInt("berat") + "Gram");
                    } else 
                    if (rst.getInt("keping") > Integer.valueOf(txt_Jumlah_keping.getText())) {
                        check = false;
                        JOptionPane.showMessageDialog(this, "Maaf Grade tsb sudah dikeluarkan menjadi LP sebanyak " + rst.getInt("keping") + "Kpg");
                    }
                }

                if (check) {
                    if (ComboBox_kode_grading.getSelectedItem().equals(Table_Grading_Bahan_Baku.getValueAt(i, 0))) {
                        String Query = "UPDATE `tb_grading_bahan_baku_cheat` SET `jumlah_keping` = '" + txt_Jumlah_keping.getText() + "', `total_berat` = '" + txt_jumlah_berat.getText() + "' "
                                + "WHERE `tb_grading_bahan_baku_cheat`.`no_kartu_waleta` = '" + label_NO_KARTU.getText() + "' AND `tb_grading_bahan_baku_cheat`.`kode_grade` = '" + ComboBox_kode_grading.getSelectedItem() + "'";
                        executeSQLQuery(Query, "updated !");
                    } else {
                        JOptionPane.showMessageDialog(this, "Tidak bisa edit Kode Grade, hanya bisa edit jumlah jika salah memasukkan");
                    }
                }
            }
        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, e);
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
                        String Query = "DELETE FROM `tb_grading_bahan_baku_cheat` WHERE `tb_grading_bahan_baku_cheat`.`no_kartu_waleta` = \'" + label_NO_KARTU.getText() + "\' AND `tb_grading_bahan_baku_cheat`.`kode_grade` = \'" + ComboBox_kode_grading.getSelectedItem() + "\'";
                        executeSQLQuery(Query, "deleted !");
                    }
                }
            }
        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_Button_hapus_data_gradingActionPerformed

    private void button_grading_selesaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_grading_selesaiActionPerformed
        try {
            int j = JPanel_BahanBakuMasuk_Cheat.Table_Bahan_Baku_Masuk_ct.getSelectedRow();
            decimalFormat = Utility.DecimalFormatUS(decimalFormat);
            decimalFormat.setMaximumFractionDigits(2);

            int a = Integer.valueOf(label_berat_awal.getText());
            int b = Integer.valueOf(label_total_gram.getText());
            float y = Float.valueOf(JPanel_BahanBakuMasuk_Cheat.Table_Bahan_Baku_Masuk_ct.getValueAt(j, 9).toString());
            float KA_akhir = (b - (((100 - y) / 100) * a)) / b;

            Utility.db.getConnection().createStatement();
            int x = JOptionPane.showConfirmDialog(null, "Apakah Sudah Selesai Grading ?", "Grading Selesai", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if (x == JOptionPane.YES_OPTION) {
                if (b == 0) {
                    JOptionPane.showMessageDialog(this, "Belum ada Data Grading");
                } else {
                    String Query = "UPDATE `tb_bahan_baku_masuk_cheat` SET `tgl_timbang` = '" + dateFormat.format(date) + "', `berat_real` = '" + label_total_gram.getText() + "', `keping_real` = '" + label_total_keping.getText() + "', `kadar_air_bahan_baku` = '" + decimalFormat.format(KA_akhir * 100) + "' "
                            + "WHERE `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta` = '" + label_NO_KARTU.getText() + "'";
                    if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                        JPanel_BahanBakuMasuk_Cheat.button_search.doClick();
                        this.dispose();
                    }
                }
            }
        } catch (SQLException | HeadlessException e) {
//            JOptionPane.showMessageDialog(this, "there's must be a mistake please do tripple check");
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_grading_selesaiActionPerformed

    private void button_printActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_printActionPerformed
        try {
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Catatan_Penerimaan_dan_Grading_Sarang_Burung_Mentah_cheat.jrxml");
            Map<String, Object> map = new HashMap<>();
            map.put("no_kartu_waleta", label_NO_KARTU.getText());//parameter name should be like it was named inside your report.
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
        } catch (JRException ex) {
            Logger.getLogger(DetailGradingBaku_Cheat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_printActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DetailGradingBaku_Cheat.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new DetailGradingBaku_Cheat().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton Button_Edit_data_grading;
    public javax.swing.JButton Button_Tambah_data_grading;
    public javax.swing.JButton Button_hapus_data_grading;
    private javax.swing.JComboBox<String> ComboBox_kode_grading;
    private javax.swing.JTable Table_Grading_Bahan_Baku;
    public javax.swing.JButton button_grading_selesai;
    public javax.swing.JButton button_print;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
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
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JLabel label_NO_KARTU;
    private javax.swing.JLabel label_belum_grading;
    private javax.swing.JLabel label_berat_awal;
    private javax.swing.JLabel label_total_data_grading;
    private javax.swing.JLabel label_total_gram;
    private javax.swing.JLabel label_total_keping;
    private javax.swing.JTextField txt_Jumlah_keping;
    private javax.swing.JTextField txt_jumlah_berat;
    // End of variables declaration//GEN-END:variables
}
