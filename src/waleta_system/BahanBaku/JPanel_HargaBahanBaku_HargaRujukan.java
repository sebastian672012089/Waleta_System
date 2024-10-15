package waleta_system.BahanBaku;

import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;
import waleta_system.Interface.InterfacePanel;

public class JPanel_HargaBahanBaku_HargaRujukan extends javax.swing.JPanel implements InterfacePanel {

    String sql = null;
    ResultSet rs;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    java.util.Date date = new java.util.Date();//date berisikan tanggal hari ini
    DecimalFormat decimalFormat = new DecimalFormat();
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_HargaBahanBaku_HargaRujukan() {
        initComponents();
    }

    @Override
    public void init() {
        load_ComboBox_tanggal();
        refreshTableKartuBaku();
    }

    private void load_ComboBox_tanggal() {
        try {
            ComboBox_tanggal.removeAllItems();
            String query = "SELECT DISTINCT(`tanggal`) AS 'tanggal' FROM `tb_harga_baku_esta` WHERE 1 ORDER BY `tanggal` DESC";
            ResultSet rs1 = Utility.db.getStatement().executeQuery(query);
            while (rs1.next()) {
                ComboBox_tanggal.addItem(rs1.getString("tanggal"));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_HargaBahanBaku_HargaRujukan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void refreshTableKartuBaku() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_KartuBaku.getModel();
            model.setRowCount(0);
            sql = "SELECT `no_kartu_waleta`, `tgl_masuk` "
                    + "FROM `tb_bahan_baku_masuk` "
                    + "WHERE `no_kartu_waleta` LIKE '%" + txt_cari_kartu.getText() + "%' "
                    + "ORDER BY `tanggal` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[2];
            while (rs.next()) {
                row[0] = rs.getString("no_kartu_waleta");
                row[1] = new SimpleDateFormat("dd MMMM yyyy").format(rs.getDate("tgl_masuk"));
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_KartuBaku);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_HargaBahanBaku_HargaRujukan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void refreshTable_perhitungan() {
        try {
            int total_berat_grading = 0, total_keping_grading = 0;
            double total_harga_waleta = 0, total_harga_esta = 0, total_selisih = 0;
            DefaultTableModel model = (DefaultTableModel) Table_Grading.getModel();
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
            label_total_grading.setText(Integer.toString(Table_Grading.getRowCount()) + " Grade");
            label_total_kpg.setText(decimalFormat.format(total_keping_grading) + " Keping");
            label_total_berat.setText(decimalFormat.format(total_berat_grading) + " Gram");
            label_total_harga_waleta.setText("Rp. " + decimalFormat.format(total_harga_waleta));
            label_total_harga_esta.setText("Rp. " + decimalFormat.format(total_harga_esta));
            label_total_selisih.setText("Rp. " + decimalFormat.format(total_selisih));
            ColumnsAutoSizer.sizeColumnsToFit(Table_Grading);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_HargaBahanBaku_HargaRujukan.class.getName()).log(Level.SEVERE, null, ex);
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
        jLabel12 = new javax.swing.JLabel();
        txt_cari_kartu = new javax.swing.JTextField();
        Button_refresh = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        ComboBox_tanggal = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        label_total_berat = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Table_Grading = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        label_total_selisih = new javax.swing.JLabel();
        label_total_harga_esta = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        label_total_kpg = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        label_total_grading = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        label_total_harga_waleta = new javax.swing.JLabel();
        label_NoKartu = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_KartuBaku = new javax.swing.JTable();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(1366, 700));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Daftar Harga Bahan Baku Rujukan Esta", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(1366, 652));

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

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel14.setText("Cari No Kartu :");

        ComboBox_tanggal.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel13.setText("HARGA PERGRADE BAHAN BAKU");

        label_total_berat.setBackground(new java.awt.Color(255, 255, 255));
        label_total_berat.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_berat.setText("0 Gram");

        Table_Grading.setAutoCreateRowSorter(true);
        Table_Grading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_Grading.setModel(new javax.swing.table.DefaultTableModel(
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
        Table_Grading.setMaximumSize(new java.awt.Dimension(420, 0));
        Table_Grading.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(Table_Grading);

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("TOTAL HARGA WALETA :");

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText("TOTAL GRAM :");

        label_total_selisih.setBackground(new java.awt.Color(255, 255, 255));
        label_total_selisih.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_selisih.setText("Rp. 0");

        label_total_harga_esta.setBackground(new java.awt.Color(255, 255, 255));
        label_total_harga_esta.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_harga_esta.setText("Rp. 0");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("TOTAL KEPING :");

        label_total_kpg.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_kpg.setText("0 Keping");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("SELISIH HARGA :");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("TOTAL HARGA ESTA :");

        label_total_grading.setBackground(new java.awt.Color(255, 255, 255));
        label_total_grading.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_grading.setText("0 Grade");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("TOTAL GRADE WALETA :");

        label_total_harga_waleta.setBackground(new java.awt.Color(255, 255, 255));
        label_total_harga_waleta.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_harga_waleta.setText("Rp. 0");

        label_NoKartu.setBackground(new java.awt.Color(255, 255, 255));
        label_NoKartu.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_NoKartu.setForeground(new java.awt.Color(255, 0, 0));
        label_NoKartu.setText("NO KARTU");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(label_total_berat, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_total_kpg, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_total_harga_waleta, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_total_harga_esta, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_total_selisih, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_NoKartu)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(label_NoKartu))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_grading)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_kpg)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_berat)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_harga_waleta)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_harga_esta)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_selisih)
                            .addComponent(jLabel6))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel9.setText("DATA NO KARTU WALETA");

        Table_KartuBaku.setAutoCreateRowSorter(true);
        Table_KartuBaku.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Table_KartuBaku.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Kartu", "Tanggal Masuk"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
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
        Table_KartuBaku.setMaximumSize(new java.awt.Dimension(420, 0));
        Table_KartuBaku.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Table_KartuBaku);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 565, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_cari_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(ComboBox_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Button_refresh)
                        .addContainerGap(858, Short.MAX_VALUE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_cari_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Button_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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
    private javax.swing.JTable Table_Grading;
    private javax.swing.JTable Table_KartuBaku;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel label_NoKartu;
    private javax.swing.JLabel label_total_berat;
    private javax.swing.JLabel label_total_grading;
    private javax.swing.JLabel label_total_harga_esta;
    private javax.swing.JLabel label_total_harga_waleta;
    private javax.swing.JLabel label_total_kpg;
    private javax.swing.JLabel label_total_selisih;
    private javax.swing.JTextField txt_cari_kartu;
    // End of variables declaration//GEN-END:variables

}
