package waleta_system.Finance;

import waleta_system.Class.Utility;

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
import waleta_system.Class.ColumnsAutoSizer;

import waleta_system.Class.ExportToExcel;
import waleta_system.Class.Utility;
import waleta_system.Interface.InterfacePanel;

public class JPanel_Harga_BahanBaku_Esta extends javax.swing.JPanel implements InterfacePanel {

    
    String sql = null;
    ResultSet rs;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    java.util.Date date = new java.util.Date();//date berisikan tanggal hari ini
    DecimalFormat decimalFormat = new DecimalFormat();
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_Harga_BahanBaku_Esta() {
        initComponents();
    }

    @Override
    public void init() {
        load_ComboBox_tanggal();
        refreshTable_tanggal();
        refreshTable_GradeEsta();
        Table_Tanggal.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                int selectedRow = Table_Tanggal.getSelectedRow();
                if (!event.getValueIsAdjusting() && selectedRow != -1) {
                    label_tanggal.setText(Table_Tanggal.getValueAt(selectedRow, 1).toString());
                    refreshTable_harga();
                }
            }
        });
    }

    public void load_ComboBox_tanggal() {
        try {
            
            
            ComboBox_tanggal.removeAllItems();
            String query = "SELECT DISTINCT(`tanggal`) AS 'tanggal' FROM `tb_harga_baku_esta` WHERE 1 ORDER BY `tanggal` DESC";
            ResultSet rs1 = Utility.db.getStatement().executeQuery(query);
            while (rs1.next()) {
                ComboBox_tanggal.addItem(rs1.getString("tanggal"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Harga_BahanBaku_Esta.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(JPanel_Harga_BahanBaku_Esta.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_tanggal() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_Tanggal.getModel();
            model.setRowCount(0);
            int month = jMonthChooser1.getMonth() + 1;
            int year = jYearChooser1.getYear();
            sql = "SELECT DISTINCT(`tanggal`) AS 'tanggal' FROM `tb_harga_baku_esta` WHERE MONTH(`tanggal`) = " + month + " AND YEAR(`tanggal`) = " + year + " ORDER BY `tanggal` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[2];
            int no = 1;
            while (rs.next()) {
                row[0] = no;
                row[1] = new SimpleDateFormat("dd MMMM yyyy").format(rs.getDate("tanggal"));
                model.addRow(row);
                no++;
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_Tanggal);
            load_ComboBox_tanggal();
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Harga_BahanBaku_Esta.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(JPanel_Harga_BahanBaku_Esta.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_harga() {
        try {
            Date tanggal = null;
            try {
                tanggal = new SimpleDateFormat("dd MMMM yyyy").parse(Table_Tanggal.getValueAt(Table_Tanggal.getSelectedRow(), 1).toString());
            } catch (ParseException ex) {
                Logger.getLogger(JPanel_Harga_BahanBaku_Esta.class.getName()).log(Level.SEVERE, null, ex);
            }
            DefaultTableModel model = (DefaultTableModel) Table_Harga.getModel();
            model.setRowCount(0);
            sql = "SELECT `grade_esta`, `harga` FROM `tb_harga_baku_esta` WHERE `tanggal` = '" + dateFormat.format(tanggal) + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[3];
            int no = 1;
            while (rs.next()) {
                row[0] = no;
                row[1] = rs.getString("grade_esta");
                row[2] = rs.getFloat("harga");
                model.addRow(row);
                no++;
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_Harga);
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Harga_BahanBaku_Esta.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_GradeEsta() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_grade_esta.getModel();
            model.setRowCount(0);
            sql = "SELECT `grade_esta` FROM `tb_grade_baku_esta` WHERE 1";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[3];
            int no = 1;
            while (rs.next()) {
                row[0] = no;
                row[1] = rs.getString("grade_esta");
                model.addRow(row);
                no++;
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Harga_BahanBaku_Esta.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_perhitungan() {
        try {
            int total_berat_grading = 0, total_keping_grading = 0;
            double total_harga_waleta = 0, total_harga_esta = 0, total_selisih = 0;
            DefaultTableModel model = (DefaultTableModel) Table_PerhitunganEstaWaleta.getModel();
            model.setRowCount(0);
            sql = "SELECT `no_kartu_waleta`, `tgl_masuk`, `tb_grading_bahan_baku`.`kode_grade`, `tb_grade_bahan_baku`.`kategori_esta`, `jumlah_keping`, `total_berat`, `harga_bahanbaku`, `tb_harga_baku_esta`.`harga` AS 'harga_esta' \n"
                    + "FROM `tb_grading_bahan_baku` "
                    + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_grading_bahan_baku`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`\n"
                    + "LEFT JOIN `tb_bahan_baku_masuk` ON `tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`\n"
                    + "LEFT JOIN `tb_harga_baku_esta` ON `tb_grade_bahan_baku`.`kategori_esta` = `tb_harga_baku_esta`.`grade_esta`\n"
                    + "WHERE "
                    + "`no_kartu_waleta` LIKE '%" + txt_cari_kartu.getText() + "%' "
                    + "AND `tb_harga_baku_esta`.`tanggal` = '" + ComboBox_tanggal.getSelectedItem().toString() + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[15];
            while (rs.next()) {
                row[0] = rs.getString("no_kartu_waleta");
                row[1] = rs.getDate("tgl_masuk");
                row[2] = rs.getString("kode_grade");
                row[3] = rs.getString("kategori_esta");
                row[4] = rs.getFloat("jumlah_keping");
                row[5] = rs.getFloat("total_berat");
                row[6] = rs.getFloat("harga_bahanbaku");
                double harga_waleta = rs.getDouble("total_berat") * rs.getDouble("harga_bahanbaku");
                row[7] = harga_waleta;
                row[8] = rs.getFloat("harga_esta");
                double harga_esta = rs.getDouble("total_berat") * rs.getDouble("harga_esta");
                row[9] = harga_esta;
                row[10] = harga_esta - harga_waleta;
                model.addRow(row);

                total_keping_grading = total_keping_grading + rs.getInt("jumlah_keping");
                total_berat_grading = total_berat_grading + rs.getInt("total_berat");
                total_harga_waleta = total_harga_waleta + harga_waleta;
                total_harga_esta = total_harga_esta + harga_esta;
                total_selisih = total_selisih + (harga_esta - harga_waleta);
            }
            decimalFormat.setGroupingUsed(true);
            label_total_grading.setText(Integer.toString(Table_PerhitunganEstaWaleta.getRowCount()) + " Grade");
            label_total_kpg.setText(decimalFormat.format(total_keping_grading) + " Keping");
            label_total_berat.setText(decimalFormat.format(total_berat_grading) + " Gram");
            label_total_harga_waleta.setText("Rp. " + decimalFormat.format(total_harga_waleta));
            label_total_harga_esta.setText("Rp. " + decimalFormat.format(total_harga_esta));
            label_total_selisih.setText("Rp. " + decimalFormat.format(total_selisih));
            ColumnsAutoSizer.sizeColumnsToFit(Table_PerhitunganEstaWaleta);
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Harga_BahanBaku_Esta.class.getName()).log(Level.SEVERE, null, ex);
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
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel_Perbandingan = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        txt_cari_kartu = new javax.swing.JTextField();
        Button_refresh = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Table_PerhitunganEstaWaleta = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        label_total_berat = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        label_total_kpg = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        label_total_harga_waleta = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        label_total_harga_esta = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        label_total_grading = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        ComboBox_tanggal = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        label_total_selisih = new javax.swing.JLabel();
        jPanel_Grade_Harga_Esta = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        Table_Harga = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_Tanggal = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        button_export = new javax.swing.JButton();
        jMonthChooser1 = new com.toedter.calendar.JMonthChooser();
        jYearChooser1 = new com.toedter.calendar.JYearChooser();
        button_refresh = new javax.swing.JButton();
        button_input = new javax.swing.JButton();
        button_delete = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        Table_grade_esta = new javax.swing.JTable();
        button_new_grade_esta = new javax.swing.JButton();
        button_delete_grade_esta = new javax.swing.JButton();
        label_tanggal = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(1366, 700));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Daftar Harga Bahan Baku Rujukan Esta", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(1366, 652));

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jPanel_Perbandingan.setBackground(new java.awt.Color(255, 255, 255));

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel12.setText("Tanggal Harga Rujukan :");

        txt_cari_kartu.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_cari_kartu.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_cari_kartuKeyPressed(evt);
            }
        });

        Button_refresh.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Button_refresh.setText("Refresh");
        Button_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_refreshActionPerformed(evt);
            }
        });

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel13.setText("HARGA PERGRADE BAHAN BAKU");

        Table_PerhitunganEstaWaleta.setAutoCreateRowSorter(true);
        Table_PerhitunganEstaWaleta.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_PerhitunganEstaWaleta.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Kartu", "Tgl Masuk", "Grade Waleta", "Grade Esta", "Kpg", "Berat", "Harga Waleta/Gr", "Harga Waleta", "Harga Esta/Gr", "Harga Esta", "Esta - Waleta"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
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
        Table_PerhitunganEstaWaleta.setMaximumSize(new java.awt.Dimension(420, 0));
        Table_PerhitunganEstaWaleta.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(Table_PerhitunganEstaWaleta);

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText("TOTAL GRAM :");

        label_total_berat.setBackground(new java.awt.Color(255, 255, 255));
        label_total_berat.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_berat.setText("0 Gram");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("TOTAL KEPING :");

        label_total_kpg.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_kpg.setText("0 Keping");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("TOTAL HARGA WALETA :");

        label_total_harga_waleta.setBackground(new java.awt.Color(255, 255, 255));
        label_total_harga_waleta.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_harga_waleta.setText("Rp. 0");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("TOTAL HARGA ESTA :");

        label_total_harga_esta.setBackground(new java.awt.Color(255, 255, 255));
        label_total_harga_esta.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_harga_esta.setText("Rp. 0");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("TOTAL GRADE WALETA :");

        label_total_grading.setBackground(new java.awt.Color(255, 255, 255));
        label_total_grading.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_grading.setText("0 Grade");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel14.setText("Cari No Kartu :");

        ComboBox_tanggal.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("SELISIH HARGA :");

        label_total_selisih.setBackground(new java.awt.Color(255, 255, 255));
        label_total_selisih.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_selisih.setText("Rp. 0");

        javax.swing.GroupLayout jPanel_PerbandinganLayout = new javax.swing.GroupLayout(jPanel_Perbandingan);
        jPanel_Perbandingan.setLayout(jPanel_PerbandinganLayout);
        jPanel_PerbandinganLayout.setHorizontalGroup(
            jPanel_PerbandinganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_PerbandinganLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_PerbandinganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_PerbandinganLayout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1021, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_PerbandinganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_PerbandinganLayout.createSequentialGroup()
                                .addGroup(jPanel_PerbandinganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel_PerbandinganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(label_total_berat, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_total_kpg, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_total_harga_waleta, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_total_harga_esta, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_total_selisih, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel_PerbandinganLayout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel_PerbandinganLayout.createSequentialGroup()
                        .addGroup(jPanel_PerbandinganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13)
                            .addGroup(jPanel_PerbandinganLayout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_cari_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Button_refresh)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel_PerbandinganLayout.setVerticalGroup(
            jPanel_PerbandinganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_PerbandinganLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_PerbandinganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_cari_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Button_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_PerbandinganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 571, Short.MAX_VALUE)
                    .addGroup(jPanel_PerbandinganLayout.createSequentialGroup()
                        .addGroup(jPanel_PerbandinganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_grading)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_PerbandinganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_kpg)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_PerbandinganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_berat)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_PerbandinganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_harga_waleta)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_PerbandinganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_harga_esta)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_PerbandinganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_selisih)
                            .addComponent(jLabel6))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Perbandingan harga baku Esta Waleta", jPanel_Perbandingan);

        jPanel_Grade_Harga_Esta.setBackground(new java.awt.Color(255, 255, 255));

        Table_Harga.setAutoCreateRowSorter(true);
        Table_Harga.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Table_Harga.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Kategori", "Harga"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Float.class
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
        Table_Harga.setMaximumSize(new java.awt.Dimension(920, 0));
        Table_Harga.getTableHeader().setReorderingAllowed(false);
        Table_Harga.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Table_HargaMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(Table_Harga);
        if (Table_Harga.getColumnModel().getColumnCount() > 0) {
            Table_Harga.getColumnModel().getColumn(0).setMaxWidth(50);
        }

        Table_Tanggal.setAutoCreateRowSorter(true);
        Table_Tanggal.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Table_Tanggal.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Tanggal"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_Tanggal.setMaximumSize(new java.awt.Dimension(420, 0));
        Table_Tanggal.getTableHeader().setReorderingAllowed(false);
        Table_Tanggal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Table_TanggalMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(Table_Tanggal);
        if (Table_Tanggal.getColumnModel().getColumnCount() > 0) {
            Table_Tanggal.getColumnModel().getColumn(0).setMaxWidth(100);
        }

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel9.setText("DATA HARGA PER TANGGAL");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel10.setText("HARGA GRADE BAKU ESTA");

        button_export.setBackground(new java.awt.Color(255, 255, 255));
        button_export.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export.setText("Export Harga");
        button_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportActionPerformed(evt);
            }
        });

        button_refresh.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh.setText("Refresh");
        button_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refreshActionPerformed(evt);
            }
        });

        button_input.setBackground(new java.awt.Color(255, 255, 255));
        button_input.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_input.setText("Input Harga Baru");
        button_input.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_inputActionPerformed(evt);
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

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel11.setText("MASTER GRADE BAKU ESTA");

        Table_grade_esta.setAutoCreateRowSorter(true);
        Table_grade_esta.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Table_grade_esta.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Grade"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_grade_esta.setMaximumSize(new java.awt.Dimension(920, 0));
        Table_grade_esta.getTableHeader().setReorderingAllowed(false);
        jScrollPane6.setViewportView(Table_grade_esta);
        if (Table_grade_esta.getColumnModel().getColumnCount() > 0) {
            Table_grade_esta.getColumnModel().getColumn(0).setMaxWidth(50);
        }

        button_new_grade_esta.setBackground(new java.awt.Color(255, 255, 255));
        button_new_grade_esta.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_new_grade_esta.setText("New");
        button_new_grade_esta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_new_grade_estaActionPerformed(evt);
            }
        });

        button_delete_grade_esta.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_grade_esta.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete_grade_esta.setText("Delete");
        button_delete_grade_esta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_grade_estaActionPerformed(evt);
            }
        });

        label_tanggal.setBackground(new java.awt.Color(255, 255, 255));
        label_tanggal.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_tanggal.setForeground(new java.awt.Color(0, 142, 0));
        label_tanggal.setText("TANGGAL");

        javax.swing.GroupLayout jPanel_Grade_Harga_EstaLayout = new javax.swing.GroupLayout(jPanel_Grade_Harga_Esta);
        jPanel_Grade_Harga_Esta.setLayout(jPanel_Grade_Harga_EstaLayout);
        jPanel_Grade_Harga_EstaLayout.setHorizontalGroup(
            jPanel_Grade_Harga_EstaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Grade_Harga_EstaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Grade_Harga_EstaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addGroup(jPanel_Grade_Harga_EstaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGroup(jPanel_Grade_Harga_EstaLayout.createSequentialGroup()
                            .addComponent(jMonthChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jYearChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(button_refresh))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Grade_Harga_EstaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_Grade_Harga_EstaLayout.createSequentialGroup()
                        .addComponent(button_input)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_delete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export))
                    .addGroup(jPanel_Grade_Harga_EstaLayout.createSequentialGroup()
                        .addGroup(jPanel_Grade_Harga_EstaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_Grade_Harga_EstaLayout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_tanggal))
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_Grade_Harga_EstaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel_Grade_Harga_EstaLayout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_new_grade_esta)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_delete_grade_esta))
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel_Grade_Harga_EstaLayout.setVerticalGroup(
            jPanel_Grade_Harga_EstaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Grade_Harga_EstaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Grade_Harga_EstaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jMonthChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jYearChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_input, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Grade_Harga_EstaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_Grade_Harga_EstaLayout.createSequentialGroup()
                        .addGroup(jPanel_Grade_Harga_EstaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 571, Short.MAX_VALUE))
                    .addGroup(jPanel_Grade_Harga_EstaLayout.createSequentialGroup()
                        .addGroup(jPanel_Grade_Harga_EstaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel_Grade_Harga_EstaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                .addComponent(button_new_grade_esta, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(button_delete_grade_esta, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 571, Short.MAX_VALUE))
                    .addGroup(jPanel_Grade_Harga_EstaLayout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Master Grade Esta & Harga Esta", jPanel_Grade_Harga_Esta);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 700, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void Table_HargaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Table_HargaMouseClicked
        decimalFormat.setMaximumFractionDigits(5);
        decimalFormat.setGroupingUsed(false);
        int i = Table_Tanggal.getSelectedRow();
        int j = Table_Harga.getSelectedRow();
        if (evt.getClickCount() == 2) {
            try {
                Date tanggal = null;
                tanggal = new SimpleDateFormat("dd MMMM yyyy").parse(label_tanggal.getText());
                String old_price = Table_Harga.getValueAt(j, 2).toString();
                String price1 = "";
                price1 = old_price.replace(",", "");
                String harga = JOptionPane.showInputDialog("Masukkan Harga Baru : ", price1);
                double HARGA_F = Double.valueOf(harga);
                decimalFormat = Utility.DecimalFormatUS(decimalFormat);
                System.out.println(HARGA_F);
                sql = "UPDATE `tb_harga_baku_esta` SET `harga`='" + decimalFormat.format(HARGA_F) + "' WHERE `tanggal`='" + dateFormat.format(tanggal) + "' AND `grade_esta`='" + Table_Harga.getValueAt(j, 1).toString() + "'";
                System.out.println(sql);
                if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                    refreshTable_harga();
                    JOptionPane.showMessageDialog(this, "Update success!");
                } else {
                    JOptionPane.showMessageDialog(this, "Update failed!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex);
                Logger.getLogger(JPanel_Harga_BahanBaku_Esta.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_Table_HargaMouseClicked

    private void Table_TanggalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Table_TanggalMouseClicked
        // TODO add your handling code here:

    }//GEN-LAST:event_Table_TanggalMouseClicked

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_Harga.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_exportActionPerformed

    private void button_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refreshActionPerformed
        // TODO add your handling code here:
        refreshTable_tanggal();
    }//GEN-LAST:event_button_refreshActionPerformed

    private void button_inputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_inputActionPerformed
        // TODO add your handling code here:
        JDialog_InputHargaEsta dialog = new JDialog_InputHargaEsta(new javax.swing.JFrame(), true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        refreshTable_tanggal();
        DefaultTableModel model = (DefaultTableModel) Table_Harga.getModel();
        model.setRowCount(0);
    }//GEN-LAST:event_button_inputActionPerformed

    private void button_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_deleteActionPerformed
        // TODO add your handling code here:
        int x = Table_Tanggal.getSelectedRow();
        if (x > -1) {
            int dialogResult = JOptionPane.showConfirmDialog(this, "Delete this record?", "Warning", 0);
            if (dialogResult == JOptionPane.YES_OPTION) {
                Date tanggal = null;
                try {
                    tanggal = new SimpleDateFormat("dd MMMM yyyy").parse(Table_Tanggal.getValueAt(x, 1).toString());
                } catch (ParseException ex) {
                    Logger.getLogger(JPanel_Harga_BahanBaku_Esta.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    sql = "DELETE FROM `tb_harga_baku_esta` WHERE `tanggal` = '" + dateFormat.format(tanggal) + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(sql)) > 0) {
                        refreshTable_tanggal();
                        DefaultTableModel model = (DefaultTableModel) Table_Harga.getModel();
                        model.setRowCount(0);
                        JOptionPane.showMessageDialog(this, "Data berhasil Di hapus");
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(JPanel_Biaya_Ekspor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Silahkan klik pada data yang akan di Hapus");
        }
    }//GEN-LAST:event_button_deleteActionPerformed

    private void button_new_grade_estaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_new_grade_estaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_button_new_grade_estaActionPerformed

    private void button_delete_grade_estaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_grade_estaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_button_delete_grade_estaActionPerformed

    private void txt_cari_kartuKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_cari_kartuKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_perhitungan();
        }
    }//GEN-LAST:event_txt_cari_kartuKeyPressed

    private void Button_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_refreshActionPerformed
        // TODO add your handling code here:
        refreshTable_perhitungan();
    }//GEN-LAST:event_Button_refreshActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Button_refresh;
    private javax.swing.JComboBox<String> ComboBox_tanggal;
    public static javax.swing.JTable Table_Harga;
    private javax.swing.JTable Table_PerhitunganEstaWaleta;
    private javax.swing.JTable Table_Tanggal;
    public static javax.swing.JTable Table_grade_esta;
    private javax.swing.JButton button_delete;
    private javax.swing.JButton button_delete_grade_esta;
    private javax.swing.JButton button_export;
    private javax.swing.JButton button_input;
    private javax.swing.JButton button_new_grade_esta;
    public static javax.swing.JButton button_refresh;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private com.toedter.calendar.JMonthChooser jMonthChooser1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel_Grade_Harga_Esta;
    private javax.swing.JPanel jPanel_Perbandingan;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedPane1;
    private com.toedter.calendar.JYearChooser jYearChooser1;
    private javax.swing.JLabel label_tanggal;
    private javax.swing.JLabel label_total_berat;
    private javax.swing.JLabel label_total_grading;
    private javax.swing.JLabel label_total_harga_esta;
    private javax.swing.JLabel label_total_harga_waleta;
    private javax.swing.JLabel label_total_kpg;
    private javax.swing.JLabel label_total_selisih;
    private javax.swing.JTextField txt_cari_kartu;
    // End of variables declaration//GEN-END:variables

}
