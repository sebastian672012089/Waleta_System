package waleta_system.Packing;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;

public class JFrame_TV_SPK extends javax.swing.JFrame {

    
    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    ArrayList<String> KodeSPK, NamaBuyer, Catatan;
    ArrayList<Date> Date_TK, Date_AWB;
    static Timer t;
    TimerTask timer;

    public JFrame_TV_SPK() {
        initComponents();
    }

    public void init() {
        try {
            KodeSPK = new ArrayList<>();
            NamaBuyer = new ArrayList<>();
            Catatan = new ArrayList<>();
            Date_TK = new ArrayList<>();
            Date_AWB = new ArrayList<>();
            
            
            timer = new TimerTask() {
                @Override
                public void run() {
                    int i = Tabel_SPK_All.getSelectedRow();
                    if (i > -1) {
                        refreshTable_detailSPK(i);
                    }
                }
            };
            refreshTable_SPK();
            refreshTable_Box_Belum_Retur();
            
            Tabel_SPK_All.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && Tabel_SPK_All.getSelectedRow() != -1) {
                        int i = Tabel_SPK_All.getSelectedRow();
                        refreshTable_detailSPK(i);
                        label_kode_spk.setText(KodeSPK.get(i));
                        label_buyer.setText(NamaBuyer.get(i));
                        if (Date_TK.get(i) != null) {
                            label_tanggal_tk.setText(new SimpleDateFormat("dd MMM YYYY").format(Date_TK.get(i)));
                        }
                        if (Date_AWB.get(i) != null) {
                            label_tanggal_awb.setText(new SimpleDateFormat("dd MMM YYYY").format(Date_AWB.get(i)));
                        }
                        label_catatan.setText(Catatan.get(i));
                    }
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(JFrame_TV_SPK.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refresh_JAM() {
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");

        String formattedDate = myDateObj.format(myFormatObj);
        label_jam.setText(formattedDate);
    }

    public void refreshTable_SPK() {
        try {
            DefaultTableModel model = (DefaultTableModel) Tabel_SPK_All.getModel();
            model.setRowCount(0);
            Object[] row = new Object[4];
            sql = "SELECT `tb_spk`.`kode_spk`, (SELECT `nama` FROM `tb_buyer` WHERE `kode_buyer` = `tb_spk`.`buyer`) AS 'nama_buyer', `tanggal_spk`, `tanggal_tk`, `tanggal_keluar`, `detail`, "
                    + "(SELECT SUM(`tb_spk_detail`.`berat`) FROM `tb_spk_detail` WHERE `kode_spk` = `tb_spk`.`kode_spk`) AS 'berat', SUM(`tb_box_bahan_jadi`.`berat`) AS 'progress'"
                    + "FROM `tb_spk` LEFT JOIN `tb_spk_detail` ON `tb_spk`.`kode_spk` = `tb_spk_detail`.`kode_spk`"
                    + "LEFT JOIN `tb_box_packing` ON `tb_spk_detail`.`no` = `tb_box_packing`.`no_grade_spk` "
                    + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_box_bahan_jadi`.`no_box` = `tb_box_packing`.`no_box` "
                    + "WHERE `tb_spk`.`status` = 'PROSES' GROUP BY `kode_spk` ORDER BY `tanggal_tk` DESC";
            pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                row[0] = rs.getString("kode_spk");
                row[1] = rs.getDate("tanggal_tk");
                row[2] = rs.getFloat("berat");
                row[3] = Math.round((rs.getFloat("progress") / rs.getFloat("berat")) * 1000.f) / 10.f;
                model.addRow(row);
                KodeSPK.add(rs.getString("kode_spk"));
                NamaBuyer.add(rs.getString("nama_buyer"));
                Catatan.add(rs.getString("detail"));
                Date_TK.add(rs.getDate("tanggal_tk"));
                Date_AWB.add(rs.getDate("tanggal_keluar"));
            }
            ColumnsAutoSizer.sizeColumnsToFit(Tabel_SPK_All);
            refresh_JAM();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JFrame_TV_SPK.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_detailSPK(int rowSelected) {
        try {
            DefaultTableModel model = (DefaultTableModel) Tabel_Detail_SPK.getModel();
            model.setRowCount(0);
            Object[] row = new Object[9];
            sql = "SELECT `no`, `grade_waleta`, `grade_buyer`, `berat_kemasan`, `jumlah_kemasan`, `tb_spk_detail`.`berat`, `harga_cny`, `keterangan`, `kode_spk`, SUM(`tb_box_bahan_jadi`.`berat`) AS 'progress'"
                    + "FROM `tb_spk_detail` LEFT JOIN `tb_box_packing` ON `tb_spk_detail`.`no` = `tb_box_packing`.`no_grade_spk` "
                    + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_box_bahan_jadi`.`no_box` = `tb_box_packing`.`no_box` "
                    + "WHERE `kode_spk` = '" + Tabel_SPK_All.getValueAt(rowSelected, 0).toString() + "' GROUP BY `no`";
            pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                row[0] = rs.getString("grade_waleta");
                row[1] = rs.getString("grade_buyer");
                row[2] = rs.getFloat("berat_kemasan");
                row[3] = rs.getFloat("jumlah_kemasan");
                row[4] = rs.getFloat("berat");
                row[5] = rs.getString("keterangan");
                row[6] = rs.getFloat("progress");
                row[7] = Math.round((rs.getFloat("progress") / rs.getFloat("berat")) * 1000.f) / 10.f;
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Tabel_Detail_SPK);
            refresh_JAM();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JFrame_TV_SPK.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_Box_Belum_Retur() {
        try {
            DefaultTableModel model = (DefaultTableModel) Tabel_box_belum_retur.getModel();
            model.setRowCount(0);
            sql = "SELECT `tb_box_packing`.`no_box`, `tb_grade_bahan_jadi`.`kode_grade`, `tb_box_bahan_jadi`.`berat`, `tb_spk_detail`.`kode_spk`, `tb_spk`.`status`, `tb_box_packing`.`status`"
                    + ", DATEDIFF(CURRENT_DATE, `tb_spk`.`tanggal_keluar`) AS 'hari'\n"
                    + "FROM `tb_box_packing` LEFT JOIN `tb_box_bahan_jadi` ON `tb_box_packing`.`no_box` = `tb_box_bahan_jadi`.`no_box`\n"
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`\n"
                    + "LEFT JOIN `tb_spk_detail` ON `tb_box_packing`.`no_grade_spk` = `tb_spk_detail`.`no`\n"
                    + "LEFT JOIN `tb_spk` ON `tb_spk_detail`.`kode_spk` = `tb_spk`.`kode_spk`\n"
                    + "WHERE `tb_spk`.`status` = 'SEND OUT' AND `tb_box_packing`.`status` <> 'OUT'";
            pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            Object[] row = new Object[5];
            while (rs.next()) {
                row[0] = rs.getString("no_box");
                row[1] = rs.getString("kode_spk");
                row[2] = rs.getString("kode_grade");
                row[3] = rs.getFloat("berat");
                row[4] = rs.getInt("hari");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Tabel_box_belum_retur);
            label_total_belum_retur.setText(Integer.toString(Tabel_box_belum_retur.getRowCount()));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JFrame_TV_SPK.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Tabel_Detail_SPK = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        Tabel_SPK_All = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        label_tanggal_tk = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        label_tanggal_awb = new javax.swing.JLabel();
        label_kode_spk = new javax.swing.JLabel();
        label_buyer = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        label_catatan = new javax.swing.JLabel();
        ToggleButton_start = new javax.swing.JToggleButton();
        label_jam = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        Tabel_box_belum_retur = new javax.swing.JTable();
        jLabel6 = new javax.swing.JLabel();
        label_total_belum_retur = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Surat Perintah Kerja");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel1.setText("SPK PENGIRIMAN");

        Tabel_Detail_SPK.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        Tabel_Detail_SPK.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade WLT", "Grade Buyer", "Berat / Kemasan", "Jumlah Kemasan", "Total Net Weight", "Keterangan", "Turun", "%"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class
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
        Tabel_Detail_SPK.setRowHeight(30);
        Tabel_Detail_SPK.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Tabel_Detail_SPK);

        Tabel_SPK_All.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        Tabel_SPK_All.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SPK", "TK", "Net Weight", "Progress (%)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.Float.class, java.lang.Float.class
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
        Tabel_SPK_All.setRowHeight(30);
        Tabel_SPK_All.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(Tabel_SPK_All);

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel2.setText("-");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel3.setText("Tanggal TK :");

        label_tanggal_tk.setBackground(new java.awt.Color(255, 255, 255));
        label_tanggal_tk.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        label_tanggal_tk.setForeground(new java.awt.Color(255, 0, 0));
        label_tanggal_tk.setText("-");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel4.setText("Tanggal Kirim / AWB :");

        label_tanggal_awb.setBackground(new java.awt.Color(255, 255, 255));
        label_tanggal_awb.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        label_tanggal_awb.setForeground(new java.awt.Color(255, 0, 0));
        label_tanggal_awb.setText("-");

        label_kode_spk.setBackground(new java.awt.Color(255, 255, 255));
        label_kode_spk.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        label_kode_spk.setForeground(new java.awt.Color(255, 0, 0));
        label_kode_spk.setText("KODE SPK");

        label_buyer.setBackground(new java.awt.Color(255, 255, 255));
        label_buyer.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        label_buyer.setForeground(new java.awt.Color(255, 0, 0));
        label_buyer.setText("BUYER");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel5.setText("Catatan :");

        label_catatan.setBackground(new java.awt.Color(255, 255, 255));
        label_catatan.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        label_catatan.setText("Catatan");

        ToggleButton_start.setBackground(new java.awt.Color(255, 255, 255));
        ToggleButton_start.setText("START");
        ToggleButton_start.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ToggleButton_startActionPerformed(evt);
            }
        });

        label_jam.setBackground(new java.awt.Color(255, 255, 255));
        label_jam.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_jam.setText("Jam :");

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        Tabel_box_belum_retur.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        Tabel_box_belum_retur.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Box", "SPK", "Grade", "Gram", "Hari"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Integer.class
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
        Tabel_box_belum_retur.setRowHeight(30);
        Tabel_box_belum_retur.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(Tabel_box_belum_retur);

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel6.setText("Box Belum Retur");

        label_total_belum_retur.setBackground(new java.awt.Color(255, 255, 255));
        label_total_belum_retur.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        label_total_belum_retur.setText("TOTAL");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 759, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(label_total_belum_retur)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(label_total_belum_retur))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_catatan))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_kode_spk)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_buyer))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_tanggal_tk)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel4)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_tanggal_awb)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jScrollPane1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 480, Short.MAX_VALUE)
                        .addComponent(label_jam)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ToggleButton_start))
                    .addComponent(jScrollPane3))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(label_kode_spk)
                    .addComponent(label_buyer))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(label_tanggal_tk)
                    .addComponent(jLabel4)
                    .addComponent(label_tanggal_awb))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(label_catatan)
                    .addComponent(label_jam, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ToggleButton_start, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 623, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ToggleButton_startActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ToggleButton_startActionPerformed
        // TODO add your handling code here:
        if (ToggleButton_start.isSelected()) {
            ToggleButton_start.setText("STOP");
            t = new Timer();
            t.schedule(timer, 1000, 60000);
        } else {
            ToggleButton_start.setText("START");
            t.cancel();
        }
    }//GEN-LAST:event_ToggleButton_startActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrame_TV_SPK.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame_TV_SPK chart = new JFrame_TV_SPK();
                chart.pack();
                chart.setResizable(true);
                chart.setLocationRelativeTo(null);
                chart.setVisible(true);
                chart.setEnabled(true);
                chart.setExtendedState(JFrame.MAXIMIZED_BOTH);
                chart.init();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable Tabel_Detail_SPK;
    private javax.swing.JTable Tabel_SPK_All;
    private javax.swing.JTable Tabel_box_belum_retur;
    private javax.swing.JToggleButton ToggleButton_start;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel label_buyer;
    private javax.swing.JLabel label_catatan;
    private javax.swing.JLabel label_jam;
    private javax.swing.JLabel label_kode_spk;
    private javax.swing.JLabel label_tanggal_awb;
    private javax.swing.JLabel label_tanggal_tk;
    private javax.swing.JLabel label_total_belum_retur;
    // End of variables declaration//GEN-END:variables
}
