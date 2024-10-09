package waleta_system.Finance;

import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;
import waleta_system.Class.Utility;
import waleta_system.Interface.InterfacePanel;
import waleta_system.MainForm;

public class JPanel_Harga_BahanBaku extends javax.swing.JPanel implements InterfacePanel {

    String sql = null;
    ResultSet rs;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    java.util.Date date = new java.util.Date();//date berisikan tanggal hari ini
    DecimalFormat decimalFormat = new DecimalFormat();
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_Harga_BahanBaku() {
        initComponents();
    }

    @Override
    public void init() {
        decimalFormat = Utility.DecimalFormatUS(decimalFormat);
        refreshTable_BahanBaku();
        Table_Bahan_Baku.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && Table_Bahan_Baku.getSelectedRow() != -1) {
                    refreshTable_hasilGrading(Table_Bahan_Baku.getSelectedRow());
                }
            }
        });
    }

    public void refreshTable_BahanBaku() {
        try {
            decimalFormat.setGroupingUsed(true);
            decimalFormat.setMaximumFractionDigits(5);
            DefaultTableModel model = (DefaultTableModel) Table_Bahan_Baku.getModel();
            model.setRowCount(0);
            double total_harga_kartu = 0;
            float tot_kpg = 0, tot_gram = 0;

            String filter_tanggal = "";
            if (Date_search1.getDate() != null && Date_search2.getDate() != null) {
                filter_tanggal = "AND `tgl_masuk` BETWEEN '" + dateFormat.format(Date_search1.getDate()) + "' AND '" + dateFormat.format(Date_search2.getDate()) + "' ";
            }
            
            sql = "SELECT `tb_bahan_baku_masuk`.`no_kartu_waleta`, `tb_kartu_cmp`.`kode_kartu_cmp`, `no_kartu_pengirim`, `tb_supplier`.`nama_supplier`, `tb_rumah_burung`.`nama_rumah_burung`, `tgl_kh`, `tgl_masuk`, `jumlah_koli`, `berat_awal`, `kadar_air_waleta`, `uji_kerapatan`, `uji_kerusakan`, `uji_basah`, `tgl_grading`, `tgl_timbang`, `berat_real`, `keping_real`, `kadar_air_bahan_baku`, `tgl_panen`, SUM(`tb_grading_bahan_baku`.`harga_bahanbaku` * `tb_grading_bahan_baku`.`total_berat`) AS 'total_harga', `status_kartu_baku` \n"
                    + "FROM `tb_bahan_baku_masuk` \n"
                    + "LEFT JOIN `tb_rumah_burung` ON `tb_bahan_baku_masuk`.`no_registrasi`=`tb_rumah_burung`.`no_registrasi`\n"
                    + "LEFT JOIN `tb_supplier` ON `tb_bahan_baku_masuk`.`kode_supplier`=`tb_supplier`.`kode_supplier`"
                    + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_bahan_baku_masuk`.`no_kartu_waleta`=`tb_grading_bahan_baku`.`no_kartu_waleta`"
                    + "LEFT JOIN `tb_kartu_cmp` ON `tb_bahan_baku_masuk`.`no_kartu_waleta`=`tb_kartu_cmp`.`kode_kartu_cmp`"
                    + "WHERE `tb_bahan_baku_masuk`.`no_kartu_waleta` LIKE '%" + txt_search_bahan_baku.getText() + "%' "
                    + filter_tanggal
                    + "GROUP BY `tb_bahan_baku_masuk`.`no_kartu_waleta` "
                    + "ORDER BY `tgl_masuk` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[15];
            while (rs.next()) {
                row[0] = rs.getString("no_kartu_waleta");
                row[1] = rs.getString("nama_supplier");
                row[2] = rs.getString("nama_rumah_burung");
                row[3] = rs.getDate("tgl_masuk");
                row[5] = rs.getFloat("berat_awal");
                row[7] = rs.getFloat("kadar_air_waleta");
                row[8] = rs.getFloat("kadar_air_bahan_baku");
                if (rs.getString("no_kartu_waleta").contains("CMP") && rs.getString("kode_kartu_cmp") != null) {
                    row[4] = 0;
                    row[6] = 0;
                    row[9] = "0";
                } else {
                    row[4] = rs.getFloat("keping_real");
                    row[6] = rs.getFloat("berat_real");
                    row[9] = decimalFormat.format(rs.getDouble("total_harga"));
                    total_harga_kartu = total_harga_kartu + rs.getDouble("total_harga");
                    tot_kpg = tot_kpg + rs.getFloat("keping_real");
                    tot_gram = tot_gram + rs.getFloat("berat_real");
                }
                if (rs.getInt("status_kartu_baku") == 0) {
                    row[10] = "Proses";
                } else {
                    row[10] = "Selesai";
                }
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_Bahan_Baku);
            label_total_kpg1.setText(decimalFormat.format(tot_kpg));
            label_total_berat1.setText(decimalFormat.format(tot_gram));
            label_total_harga1.setText(decimalFormat.format(total_harga_kartu));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_Harga_BahanBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_hasilGrading(int SelectedRow) {
        try {
            double berat, keping, harga, sub_tot_harga, tot_harga = 0, total_gram = 0, total_keping = 0;
            decimalFormat.setMaximumFractionDigits(5);
            decimalFormat.setGroupingUsed(true);
            DefaultTableModel model = (DefaultTableModel) Table_Grading_Bahan_Baku.getModel();
            model.setRowCount(0);
            sql = "SELECT `kode_grade`, `jumlah_keping`, `total_berat`, `harga_bahanbaku` "
                    + "FROM `tb_grading_bahan_baku` WHERE `no_kartu_waleta`='" + Table_Bahan_Baku.getValueAt(SelectedRow, 0).toString() + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[5];
            while (rs.next()) {
                keping = rs.getInt("jumlah_keping");
                berat = rs.getInt("total_berat");
                harga = rs.getDouble("harga_bahanbaku");

                row[0] = rs.getString("kode_grade");
                row[1] = rs.getInt("jumlah_keping");
                row[2] = rs.getInt("total_berat");
                row[3] = decimalFormat.format(harga);
                sub_tot_harga = berat * harga;
                row[4] = decimalFormat.format(sub_tot_harga);
                total_keping = total_keping + keping;
                total_gram = total_gram + berat;
                model.addRow(row);

                tot_harga = tot_harga + sub_tot_harga;
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_Grading_Bahan_Baku);

            label_total_berat.setText(Double.toString(total_gram) + " Grams");
            label_total_kpg.setText(Double.toString(total_keping) + " Keping");
            label_total_harga.setText("Rp. " + decimalFormat.format(tot_harga));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
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
        jScrollPane5 = new javax.swing.JScrollPane();
        Table_Bahan_Baku = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_Grading_Bahan_Baku = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        label_total_harga = new javax.swing.JLabel();
        label_total_berat = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        button_export = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        txt_search_bahan_baku = new javax.swing.JTextField();
        button_search_bahan_baku = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        Date_search1 = new com.toedter.calendar.JDateChooser();
        Date_search2 = new com.toedter.calendar.JDateChooser();
        button_edit_csv = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        label_total_kpg = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        label_total_harga1 = new javax.swing.JLabel();
        label_total_berat1 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        label_total_kpg1 = new javax.swing.JLabel();
        button_export1 = new javax.swing.JButton();
        button_edit_harga_cmp = new javax.swing.JButton();
        button_selesai = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(1366, 700));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Harga Bahan Baku Waleta", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(1366, 652));

        Table_Bahan_Baku.setAutoCreateRowSorter(true);
        Table_Bahan_Baku.setFont(new java.awt.Font("SansSerif", 0, 11)); // NOI18N
        Table_Bahan_Baku.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Kartu", "Supplier", "R. Burung", "Tgl Masuk", "Kpg", "Berat Awal / Nota", "Berat Real Grading", "KA WLT (%)", "KA (%)", "Total Harga", "Status Kartu"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class
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
        Table_Bahan_Baku.setMaximumSize(new java.awt.Dimension(920, 0));
        Table_Bahan_Baku.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(Table_Bahan_Baku);

        Table_Grading_Bahan_Baku.setAutoCreateRowSorter(true);
        Table_Grading_Bahan_Baku.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_Grading_Bahan_Baku.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade", "Kpg", "Berat", "Harga Satuan", "Total (Rp.)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class
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
        Table_Grading_Bahan_Baku.setMaximumSize(new java.awt.Dimension(420, 0));
        Table_Grading_Bahan_Baku.getTableHeader().setReorderingAllowed(false);
        Table_Grading_Bahan_Baku.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Table_Grading_Bahan_BakuMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(Table_Grading_Bahan_Baku);

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText("TOTAL BERAT :");

        label_total_harga.setBackground(new java.awt.Color(255, 255, 255));
        label_total_harga.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_harga.setText("Rp. 0");

        label_total_berat.setBackground(new java.awt.Color(255, 255, 255));
        label_total_berat.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_berat.setText("0 Gram");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("TOTAL HARGA :");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel9.setText("HARGA PERGRADE BAHAN BAKU");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel10.setText("DATA KARTU BAHAN BAKU WALETA");

        button_export.setBackground(new java.awt.Color(255, 255, 255));
        button_export.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export.setText("Export to Excel");
        button_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel11.setText("By No Kartu Waleta :");

        txt_search_bahan_baku.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_bahan_baku.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_bahan_bakuKeyPressed(evt);
            }
        });

        button_search_bahan_baku.setBackground(new java.awt.Color(255, 255, 255));
        button_search_bahan_baku.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_bahan_baku.setText("Search");
        button_search_bahan_baku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_bahan_bakuActionPerformed(evt);
            }
        });

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("Tanggal Masuk :");

        Date_search1.setBackground(new java.awt.Color(255, 255, 255));
        Date_search1.setDateFormatString("dd MMMM yyyy");
        Date_search1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_search2.setBackground(new java.awt.Color(255, 255, 255));
        Date_search2.setDateFormatString("dd MMMM yyyy");
        Date_search2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_edit_csv.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_csv.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_edit_csv.setText("Edit Data (csv.)");
        button_edit_csv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_csvActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_search_bahan_baku, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date_search1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date_search2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_search_bahan_baku)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_edit_csv)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_bahan_baku, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search_bahan_baku, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_search1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_search2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_edit_csv, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("TOTAL KEPING :");

        label_total_kpg.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_kpg.setText("0 Keping");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("TOTAL BERAT :");

        label_total_harga1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_harga1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_harga1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_total_harga1.setText("Rp. 0");

        label_total_berat1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_berat1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_berat1.setText("0 Gram");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("TOTAL HARGA :");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("TOTAL KEPING :");

        label_total_kpg1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_kpg1.setText("0 Keping");

        button_export1.setBackground(new java.awt.Color(255, 255, 255));
        button_export1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export1.setText("Export to Excel");
        button_export1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export1ActionPerformed(evt);
            }
        });

        button_edit_harga_cmp.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_harga_cmp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_edit_harga_cmp.setText("Edit Harga CMP");
        button_edit_harga_cmp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_harga_cmpActionPerformed(evt);
            }
        });

        button_selesai.setBackground(new java.awt.Color(255, 255, 255));
        button_selesai.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_selesai.setText("Status kartu SELESAI");
        button_selesai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_selesaiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(label_total_harga, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_berat, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_kpg, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_total_harga1)
                            .addComponent(label_total_berat1)
                            .addComponent(label_total_kpg1))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(button_export))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 420, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(button_edit_harga_cmp)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_selesai)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(button_export1))
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 910, Short.MAX_VALUE))
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(10, 10, 10))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel9)
                    .addComponent(button_export)
                    .addComponent(jLabel10)
                    .addComponent(button_export1)
                    .addComponent(button_edit_harga_cmp)
                    .addComponent(button_selesai))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 512, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_kpg)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_berat)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel6)
                            .addComponent(label_total_harga)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_kpg1)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_berat1)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel7)
                            .addComponent(label_total_harga1))))
                .addContainerGap())
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

    private void txt_search_bahan_bakuKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_bahan_bakuKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_BahanBaku();
        }
    }//GEN-LAST:event_txt_search_bahan_bakuKeyPressed

    private void button_search_bahan_bakuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_bahan_bakuActionPerformed
        // TODO add your handling code here:
        refreshTable_BahanBaku();
    }//GEN-LAST:event_button_search_bahan_bakuActionPerformed

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_Grading_Bahan_Baku.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_exportActionPerformed

    private void button_export1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export1ActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_Bahan_Baku.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_export1ActionPerformed

    private void button_edit_csvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_csvActionPerformed
        // TODO add your handling code here:
        try {
            int n = 0;
            JFileChooser chooser = new JFileChooser();
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
                            String[] value = line.split(";");
//                            System.out.println(ID);
                            sql = "UPDATE `tb_grading_bahan_baku` \n"
                                    + "SET `harga_bahanbaku`='" + value[0] + "'"
                                    + "WHERE `no_kartu_waleta` = '" + value[1] + "' AND `kode_grade` = '" + value[2] + "'";
//                            System.out.println(sql);
                            Utility.db.getConnection().prepareStatement(sql);
                            if ((Utility.db.getStatement().executeUpdate(sql)) > 0) {
                                System.out.println(value[0]);
                                n++;
                                System.out.println(n);
                            } else {
                                JOptionPane.showMessageDialog(this, "Failed insert : " + value[1] + " Grade " + value[2]);
                            }
                        }
                        Utility.db.getConnection().commit();
                    } catch (SQLException ex) {
                        try {
                            Utility.db.getConnection().rollback();
                        } catch (SQLException e) {
                            Logger.getLogger(JPanel_Harga_BahanBaku.class.getName()).log(Level.SEVERE, null, e);
                        }
                        JOptionPane.showMessageDialog(this, ex.getMessage());
                        Logger.getLogger(JPanel_Harga_BahanBaku.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        try {
                            Utility.db.getConnection().setAutoCommit(true);
                        } catch (SQLException ex) {
                            Logger.getLogger(JPanel_Harga_BahanBaku.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        JOptionPane.showMessageDialog(this, "Data Berhasil Masuk : " + n);
                        refreshTable_BahanBaku();
                    }
                }
            }
        } catch (HeadlessException | IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_Harga_BahanBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_edit_csvActionPerformed

    private void button_edit_harga_cmpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_harga_cmpActionPerformed
        // TODO add your handling code here:
        int i = Table_Bahan_Baku.getSelectedRow();
        if (i > -1) {
            String no_kartu = Table_Bahan_Baku.getValueAt(i, 0).toString();
            JDialog_Hitung_harga_CMP dialog = new JDialog_Hitung_harga_CMP(new javax.swing.JFrame(), true, no_kartu);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            dialog.setEnabled(true);
        } else {
            JOptionPane.showMessageDialog(this, "Silahkan pilih data yang akan diubah!");
        }
    }//GEN-LAST:event_button_edit_harga_cmpActionPerformed

    private void button_selesaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_selesaiActionPerformed
        // TODO add your handling code here:
        int i = Table_Bahan_Baku.getSelectedRow();
        if (i == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih salah satu NO KARTU BAKU !");
        } else if (Table_Bahan_Baku.getValueAt(i, 10).equals("SELESAI")) {
            JOptionPane.showMessageDialog(this, "Kartu baku tsb sudah SELESAI !");
        } else {
            try {
                Utility.db.getConnection().setAutoCommit(false);
                String no_kartu = Table_Bahan_Baku.getValueAt(i, 0).toString();
                String Query = "UPDATE `tb_bahan_baku_masuk` SET `status_kartu_baku`=1 WHERE `no_kartu_waleta` = '" + no_kartu + "'";
                Utility.db.getConnection().createStatement();
                if ((Utility.db.getStatement().executeUpdate(Query)) > 0) {
                    String Query2 = "SELECT `tgl_grading`, `tgl_timbang`, `berat_real`, `keping_real`, `kadar_air_bahan_baku` FROM `tb_bahan_baku_masuk` WHERE `no_kartu_waleta` = '" + no_kartu + "'";
                    ResultSet result = Utility.db.getStatement().executeQuery(Query2);
                    if (result.next()) {
                        sql = "UPDATE `tb_bahan_baku_masuk_cheat` SET \n"
                                + "`uji_kerapatan`='Passed', \n"
                                + "`uji_kerusakan`='Passed', \n"
                                + "`uji_basah`='Passed', \n"
                                + "`tgl_grading`='" + result.getString("tgl_grading") + "', \n"
                                + "`tgl_timbang`='" + result.getString("tgl_timbang") + "', \n"
                                + "`berat_real`=" + result.getString("berat_real") + ", \n"
                                + "`keping_real`=" + result.getString("keping_real") + ", \n"
                                + "`kadar_air_bahan_baku`=" + result.getString("kadar_air_bahan_baku") + " \n"
                                + "WHERE `no_kartu_waleta` = '" + no_kartu + "'";
                        Utility.db.getConnection().createStatement();
                        Utility.db.getStatement().executeUpdate(sql);
                    }
                    sql = "INSERT IGNORE INTO `tb_grading_bahan_baku_cheat`(`no_grading`, `no_kartu_waleta`, `kode_grade`, `jumlah_keping`, `total_berat`, `harga_bahanbaku`) \n"
                            + "SELECT `no_grading`, `no_kartu_waleta`, `kode_grade`, `jumlah_keping`, `total_berat`, `harga_bahanbaku`s FROM `tb_grading_bahan_baku` WHERE `no_kartu_waleta` = '" + no_kartu + "'";
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(sql);
                    JOptionPane.showMessageDialog(this, no_kartu + " Sudah diLAPORKAN !");
                    refreshTable_BahanBaku();
                }
                Utility.db.getConnection().commit();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
                try {
                    Utility.db.getConnection().rollback();
                } catch (SQLException e) {
                    Logger.getLogger(JPanel_Harga_BahanBaku.class.getName()).log(Level.SEVERE, null, e);
                }
                Logger.getLogger(JPanel_Harga_BahanBaku.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    Utility.db.getConnection().setAutoCommit(true);
                } catch (SQLException ex) {
                    Logger.getLogger(JPanel_Harga_BahanBaku.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_button_selesaiActionPerformed

    private void Table_Grading_Bahan_BakuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Table_Grading_Bahan_BakuMouseClicked
        // TODO add your handling code here:
        decimalFormat.setMaximumFractionDigits(5);
        decimalFormat.setGroupingUsed(false);
        int i = Table_Grading_Bahan_Baku.getSelectedRow();
        int j = Table_Bahan_Baku.getSelectedRow();
        if (evt.getClickCount() == 2) {
            try {
                String old_price = Table_Grading_Bahan_Baku.getValueAt(i, 3).toString();
                String price1 = "";
                price1 = old_price.replace(",", "");
                //                try {
                //                    double price_2 = (double) decimalFormat.parse(price1);
                //                } catch (ParseException e) {
                //                    JOptionPane.showMessageDialog(this, "error");
                //                    price1 = old_price.replace(".","");
                //                    price1 = price1.replace(",", ".");
                //                }

                String harga = JOptionPane.showInputDialog("Masukkan Harga : ", price1);
                double HARGA_F = Double.valueOf(harga);
                decimalFormat = Utility.DecimalFormatUS(decimalFormat);
                sql = "UPDATE `tb_grading_bahan_baku` SET `harga_bahanbaku`='" + decimalFormat.format(HARGA_F) + "' WHERE `no_kartu_waleta`='" + Table_Bahan_Baku.getValueAt(j, 0).toString() + "' AND `kode_grade`='" + Table_Grading_Bahan_Baku.getValueAt(i, 0).toString() + "'";
                if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                    refreshTable_hasilGrading(j);
                    JOptionPane.showMessageDialog(this, "Update success!");
                } else {
                    JOptionPane.showMessageDialog(this, "Update failed!");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
                Logger.getLogger(JPanel_Harga_BahanBaku.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Input must be number!");
                Logger.getLogger(JPanel_Harga_BahanBaku.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_Table_Grading_Bahan_BakuMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser Date_search1;
    private com.toedter.calendar.JDateChooser Date_search2;
    public static javax.swing.JTable Table_Bahan_Baku;
    private javax.swing.JTable Table_Grading_Bahan_Baku;
    public static javax.swing.JButton button_edit_csv;
    private javax.swing.JButton button_edit_harga_cmp;
    private javax.swing.JButton button_export;
    private javax.swing.JButton button_export1;
    public static javax.swing.JButton button_search_bahan_baku;
    private javax.swing.JButton button_selesai;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JLabel label_total_berat;
    private javax.swing.JLabel label_total_berat1;
    private javax.swing.JLabel label_total_harga;
    private javax.swing.JLabel label_total_harga1;
    private javax.swing.JLabel label_total_kpg;
    private javax.swing.JLabel label_total_kpg1;
    private javax.swing.JTextField txt_search_bahan_baku;
    // End of variables declaration//GEN-END:variables

}
