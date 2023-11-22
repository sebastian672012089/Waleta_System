package waleta_system.Packing;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;

import waleta_system.Class.Utility;

public class JDialog_ReturPacking extends javax.swing.JDialog {

     
    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    String grade, kode_repacking;
    float total_keping_asal = 0, tot_keping = 0;
    float total_gram_asal = 0, tot_gram = 0;

    public JDialog_ReturPacking(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        button_hapus.setIcon(Utility.ResizeImageIcon(new javax.swing.ImageIcon(getClass().getResource("/waleta_system/Images/delete-icon.png")), button_hapus.getWidth(), button_hapus.getHeight()));
        try {
            this.setResizable(false);
            
            refresh_TabelDaftarBoxPacking();
        } catch (Exception ex) {
            Logger.getLogger(JDialog_ReturPacking.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refresh_TabelDaftarBoxPacking() {
        try {
            tot_keping = 0;
            tot_gram = 0;
            DefaultTableModel model = (DefaultTableModel) Table_daftarBoxPacking.getModel();
            model.setRowCount(0);
            sql = "SELECT `tb_box_packing`.`no_box`, `tanggal_masuk`, `tb_grade_bahan_jadi`.`kode_grade`, `tb_box_bahan_jadi`.`keping`, `tb_box_bahan_jadi`.`berat` "
                    + "FROM `tb_box_packing` LEFT JOIN `tb_box_bahan_jadi` ON `tb_box_packing`.`no_box` = `tb_box_bahan_jadi`.`no_box`"
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`"
                    + "WHERE `invoice_pengiriman` IS NULL AND `status` = 'STOK' AND `tb_box_packing`.`no_box` LIKE '%" + txt_search_box.getText() + "%'";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("no_box"),
                    rs.getString("kode_grade"),
                    new SimpleDateFormat("dd MMMM yyyy").format(rs.getDate("tanggal_masuk")),
                    rs.getInt("keping"),
                    rs.getFloat("berat")});
                tot_keping = tot_keping + rs.getInt("keping");
                tot_gram = tot_gram + rs.getFloat("berat");
            }
            label_total_daftarBox.setText(Integer.toString(Table_daftarBoxPacking.getRowCount()));
            label_total_keping_daftarBox.setText(decimalFormat.format(tot_keping));
            label_total_gram_daftarBox.setText(decimalFormat.format(tot_gram));
            ColumnsAutoSizer.sizeColumnsToFit(Table_daftarBoxPacking);
        } catch (SQLException ex) {
            Logger.getLogger(JDialog_ReturPacking.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void count_asal() {
        tot_keping = 0;
        tot_gram = 0;
        int x = Table_BoxRetur.getRowCount();
        for (int i = 0; i < x; i++) {
            try {
                tot_keping = tot_keping + Integer.valueOf(Table_BoxRetur.getValueAt(i, 3).toString());
                tot_gram = tot_gram + Float.valueOf(Table_BoxRetur.getValueAt(i, 4).toString());
            } catch (NullPointerException e) {
                JOptionPane.showMessageDialog(this, "Tidak Boleh ada data yang kosong !");
            }
        }
        label_total_boxSetor.setText(Integer.toString(x));
        label_total_keping_setor.setText(decimalFormat.format(tot_keping));
        label_total_gram_setor.setText(decimalFormat.format(tot_gram));
        ColumnsAutoSizer.sizeColumnsToFit(Table_BoxRetur);
    }

    public boolean CheckDuplicateBoxAsal(String no_box) {
        int i = Table_BoxRetur.getRowCount();
        for (int j = 0; j < i; j++) {
            if (no_box.equals(Table_BoxRetur.getValueAt(j, 0).toString())) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        label_total_keping_daftarBox = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        label_total_gram_setor = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_BoxRetur = new javax.swing.JTable();
        label_keterangan = new javax.swing.JLabel();
        label_total_keping_setor = new javax.swing.JLabel();
        button_RETUR = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        Table_daftarBoxPacking = new javax.swing.JTable();
        label_total_daftarBox = new javax.swing.JLabel();
        label_total_gram_daftarBox = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        button_hapus = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        Date_retur = new com.toedter.calendar.JDateChooser();
        jLabel11 = new javax.swing.JLabel();
        txt_search_box = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        label_total_boxSetor = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Bagian Grading Bahan Jadi");
        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        label_total_keping_daftarBox.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_daftarBox.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_keping_daftarBox.setText("0");

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel18.setText("No Box :");

        label_total_gram_setor.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_setor.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_gram_setor.setText("0");

        Table_BoxRetur.setAutoCreateRowSorter(true);
        Table_BoxRetur.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_BoxRetur.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Box", "Grade", "Tgl Masuk", "Kpg", "Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Float.class
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
        Table_BoxRetur.setFocusable(false);
        Table_BoxRetur.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Table_BoxRetur);

        label_keterangan.setBackground(new java.awt.Color(255, 255, 255));
        label_keterangan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_keterangan.setForeground(new java.awt.Color(102, 102, 102));
        label_keterangan.setText("*Press ENTER to insert");

        label_total_keping_setor.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_setor.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_keping_setor.setText("0");

        button_RETUR.setBackground(new java.awt.Color(255, 255, 255));
        button_RETUR.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_RETUR.setText("Retur ke Grading");
        button_RETUR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_RETURActionPerformed(evt);
            }
        });

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Total :");

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel20.setText("Tanggal Retur :");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Daftar Box yang akan di retur");

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel19.setText("Retur Box ke Grading BJ");

        Table_daftarBoxPacking.setAutoCreateRowSorter(true);
        Table_daftarBoxPacking.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_daftarBoxPacking.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Box", "Grade", "Tgl Masuk", "Kpg", "Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Float.class
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
        Table_daftarBoxPacking.setFocusable(false);
        Table_daftarBoxPacking.getTableHeader().setReorderingAllowed(false);
        Table_daftarBoxPacking.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Table_daftarBoxPackingMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(Table_daftarBoxPacking);

        label_total_daftarBox.setBackground(new java.awt.Color(255, 255, 255));
        label_total_daftarBox.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_daftarBox.setText("0");

        label_total_gram_daftarBox.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_daftarBox.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_gram_daftarBox.setText("0");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Keping :");

        button_hapus.setBackground(new java.awt.Color(255, 255, 255));
        button_hapus.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_hapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_hapusActionPerformed(evt);
            }
        });

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Gram :");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Total :");

        Date_retur.setBackground(new java.awt.Color(255, 255, 255));
        Date_retur.setDate(new Date());
        Date_retur.setDateFormatString("dd MMMM yyyy");
        Date_retur.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_retur.setMaxSelectableDate(new Date());

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Gram :");

        txt_search_box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_box.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_boxKeyPressed(evt);
            }
        });

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("Keping :");

        label_total_boxSetor.setBackground(new java.awt.Color(255, 255, 255));
        label_total_boxSetor.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_boxSetor.setText("0");

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
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_box, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_keterangan, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_daftarBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_keping_daftarBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_daftarBox))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE))
                        .addGap(10, 10, 10)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(button_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_boxSetor)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_keping_setor)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_setor)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(button_RETUR))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE)))
                    .addComponent(jLabel19)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_retur, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel20)
                    .addComponent(Date_retur, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txt_search_box, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_keterangan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(button_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_total_keping_daftarBox, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_keping_setor, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram_daftarBox, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_RETUR, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram_setor, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_daftarBox, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_boxSetor, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
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

    private void Table_daftarBoxPackingMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Table_daftarBoxPackingMouseClicked
        // TODO add your handling code here:
        int x = Table_daftarBoxPacking.getSelectedRow();
        if (evt.getClickCount() == 2) {
            if (CheckDuplicateBoxAsal(Table_daftarBoxPacking.getValueAt(x, 0).toString())) {
                JOptionPane.showMessageDialog(this, "Box " + Table_daftarBoxPacking.getValueAt(x, 0).toString() + " sudah masuk");
            } else {
                DefaultTableModel model = (DefaultTableModel) Table_BoxRetur.getModel();
                model.addRow(new Object[]{Table_daftarBoxPacking.getValueAt(x, 0),
                    Table_daftarBoxPacking.getValueAt(x, 1),
                    Table_daftarBoxPacking.getValueAt(x, 2),
                    Table_daftarBoxPacking.getValueAt(x, 3),
                    Table_daftarBoxPacking.getValueAt(x, 4)});
                count_asal();
            }
        }
    }//GEN-LAST:event_Table_daftarBoxPackingMouseClicked

    private void txt_search_boxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_boxKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refresh_TabelDaftarBoxPacking();
            if (Table_daftarBoxPacking.getRowCount() == 1) {
                if (CheckDuplicateBoxAsal(Table_daftarBoxPacking.getValueAt(0, 0).toString())) {
                    JOptionPane.showMessageDialog(this, "Box " + Table_daftarBoxPacking.getValueAt(0, 0).toString() + " sudah masuk");
                } else {
                    DefaultTableModel model = (DefaultTableModel) Table_BoxRetur.getModel();
                    model.addRow(new Object[]{Table_daftarBoxPacking.getValueAt(0, 0),
                        Table_daftarBoxPacking.getValueAt(0, 1),
                        Table_daftarBoxPacking.getValueAt(0, 2),
                        Table_daftarBoxPacking.getValueAt(0, 3),
                        Table_daftarBoxPacking.getValueAt(0, 4)});
                    ColumnsAutoSizer.sizeColumnsToFit(Table_BoxRetur);
                    label_keterangan.setVisible(true);
                    label_keterangan.setForeground(Color.green);
                    label_keterangan.setText("Inserted !");
                    txt_search_box.setText("");
                    txt_search_box.requestFocus();
                    count_asal();
                }
            } else if (Table_daftarBoxPacking.getRowCount() > 0) {
                label_keterangan.setVisible(true);
                label_keterangan.setForeground(Color.red);
                label_keterangan.setText("Multiple data selected !");
            } else {
                label_keterangan.setVisible(true);
                label_keterangan.setForeground(Color.red);
                label_keterangan.setText("No Data !");
            }
        }
    }//GEN-LAST:event_txt_search_boxKeyPressed

    private void button_hapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_hapusActionPerformed
        // TODO add your handling code here:
        int i = Table_BoxRetur.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) Table_BoxRetur.getModel();
        if (i != -1) {
            model.removeRow(Table_BoxRetur.getRowSorter().convertRowIndexToModel(i));
        }
        ColumnsAutoSizer.sizeColumnsToFit(Table_BoxRetur);
        count_asal();
    }//GEN-LAST:event_button_hapusActionPerformed

    private void button_RETURActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_RETURActionPerformed
        // TODO add your handling code here:
        try {
            Utility.db.getConnection().setAutoCommit(false);
            for (int i = 0; i < Table_BoxRetur.getRowCount(); i++) {
                String update = "UPDATE `tb_box_bahan_jadi` SET "
                        + "`status_terakhir` = 'Retur dari Packing', "
                        + "`lokasi_terakhir`='PACKING', "
                        + "`tgl_proses_terakhir` = '" + dateFormat.format(Date_retur.getDate()) + "' "
                        + "WHERE `no_box` = '" + Table_BoxRetur.getValueAt(i, 0).toString() + "'";
                Utility.db.getConnection().createStatement();
                Utility.db.getStatement().executeUpdate(update);
                
                String delete = "UPDATE `tb_box_packing` SET "
                        + "`status` = 'RETUR' "
                        + "WHERE `no_box` = '" + Table_BoxRetur.getValueAt(i, 0).toString() + "'";
                Utility.db.getConnection().createStatement();
                Utility.db.getStatement().executeUpdate(delete);
            }
            Utility.db.getConnection().commit();
            JOptionPane.showMessageDialog(this, "Retur ke Bagian Packing telah berhasil !");
            this.dispose();
        } catch (SQLException | NullPointerException e) {
            try {
                Utility.db.getConnection().rollback();
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_ReturPacking.class.getName()).log(Level.SEVERE, null, ex);
            }
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JDialog_ReturPacking.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                Utility.db.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_ReturPacking.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_RETURActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JDialog_ReturPacking.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JDialog_ReturPacking dialog = new JDialog_ReturPacking(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser Date_retur;
    private javax.swing.JTable Table_BoxRetur;
    private javax.swing.JTable Table_daftarBoxPacking;
    private javax.swing.JButton button_RETUR;
    private javax.swing.JButton button_hapus;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel label_keterangan;
    private javax.swing.JLabel label_total_boxSetor;
    private javax.swing.JLabel label_total_daftarBox;
    private javax.swing.JLabel label_total_gram_daftarBox;
    private javax.swing.JLabel label_total_gram_setor;
    private javax.swing.JLabel label_total_keping_daftarBox;
    private javax.swing.JLabel label_total_keping_setor;
    private javax.swing.JTextField txt_search_box;
    // End of variables declaration//GEN-END:variables
}
