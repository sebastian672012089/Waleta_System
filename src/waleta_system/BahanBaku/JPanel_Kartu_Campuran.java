package waleta_system.BahanBaku;

import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;
import waleta_system.Class.ExportToExcel;

public class JPanel_Kartu_Campuran extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_Kartu_Campuran() {
        initComponents();
    }

    public void init() {
        refreshTable();
        Table_kartu_campuran.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && Table_kartu_campuran.getSelectedRow() != -1) {
                    int i = Table_kartu_campuran.getSelectedRow();
                    String kode = Table_kartu_campuran.getValueAt(i, 0).toString();
                    refresh_TabelDetail(kode);
                }
            }
        });
    }

    public void refreshTable() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_kartu_campuran.getModel();
            model.setRowCount(0);
            if (Date_filter1.getDate() != null && Date_filter2.getDate() != null) {
                sql = "SELECT `kode_kartu_cmp`, `tanggal`, `catatan` FROM `tb_kartu_cmp` "
                        + "WHERE `kode_kartu_cmp` LIKE '%" + txt_search_kode_kartu.getText() + "%' AND (`tanggal` BETWEEN '" + dateFormat.format(Date_filter1.getDate()) + "' AND '" + dateFormat.format(Date_filter2.getDate()) + "')"
                        + "ORDER BY `kode_kartu_cmp` DESC";
            } else {
                sql = "SELECT `kode_kartu_cmp`, `tanggal`, `catatan` FROM `tb_kartu_cmp` "
                        + "WHERE `kode_kartu_cmp` LIKE '%" + txt_search_kode_kartu.getText() + "%'"
                        + "ORDER BY `kode_kartu_cmp` DESC";
            }
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[3];
            while (rs.next()) {
                row[0] = rs.getString("kode_kartu_cmp");
                row[1] = rs.getDate("tanggal");
                row[2] = rs.getString("catatan");
                model.addRow(row);
            }
            int rowData = Table_kartu_campuran.getRowCount();
            label_total_pengeluaran.setText(Integer.toString(rowData));
            ColumnsAutoSizer.sizeColumnsToFit(Table_kartu_campuran);

            TableAlignment.setHorizontalAlignment(JLabel.CENTER);
            for (int i = 0; i < Table_kartu_campuran.getColumnCount(); i++) {
                Table_kartu_campuran.getColumnModel().getColumn(i).setCellRenderer(TableAlignment);
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Kartu_Campuran.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refresh_TabelDetail(String kode) {
        try {
            DefaultTableModel model = (DefaultTableModel) Tabel_detail.getModel();
            model.setRowCount(0);
            int total_kpg = 0, total_gram = 0;
            sql = "SELECT `kode_kartu_cmp`, `tb_grading_bahan_baku`.`no_kartu_waleta`, `tb_grading_bahan_baku`.`kode_grade`, `keping`, `gram`, `tb_kartu_cmp_detail`.`no_grading`  \n"
                    + "FROM `tb_kartu_cmp_detail` JOIN `tb_grading_bahan_baku` ON `tb_kartu_cmp_detail`.`no_grading` =  `tb_grading_bahan_baku`.`no_grading`\n"
                    + "WHERE `kode_kartu_cmp` = '" + kode + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getString("kode_kartu_cmp");
                row[1] = rs.getString("no_kartu_waleta");
                row[2] = rs.getString("kode_grade");
                row[3] = rs.getInt("keping");
                row[4] = rs.getInt("gram");
                row[5] = rs.getInt("no_grading");
                total_kpg = total_kpg + rs.getInt("keping");
                total_gram = total_gram + rs.getInt("gram");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Tabel_detail);

            TableAlignment.setHorizontalAlignment(JLabel.CENTER);
            for (int i = 0; i < Tabel_detail.getColumnCount(); i++) {
                Tabel_detail.getColumnModel().getColumn(i).setCellRenderer(TableAlignment);
            }

            int rowData = Tabel_detail.getRowCount();
            label_total_detail.setText(Integer.toString(rowData));
            label_total_keping.setText(Integer.toString(total_kpg));
            label_total_gram.setText(Integer.toString(total_gram));
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Kartu_Campuran.class.getName()).log(Level.SEVERE, null, ex);
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
        txt_search_kode_kartu = new javax.swing.JTextField();
        button_search_baku_keluar = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        button_export_BahanBakuKeluar = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        Date_filter1 = new com.toedter.calendar.JDateChooser();
        Date_filter2 = new com.toedter.calendar.JDateChooser();
        jScrollPane7 = new javax.swing.JScrollPane();
        Table_kartu_campuran = new javax.swing.JTable();
        jLabel20 = new javax.swing.JLabel();
        label_total_pengeluaran = new javax.swing.JLabel();
        button_Edit = new javax.swing.JButton();
        button_insert = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        Tabel_detail = new javax.swing.JTable();
        jLabel21 = new javax.swing.JLabel();
        label_total_detail = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        label_total_keping = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        label_total_gram = new javax.swing.JLabel();
        button_delete = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel_Bahan_Baku_Keluar.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_Bahan_Baku_Keluar.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Kartu Campuran", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N
        jPanel_Bahan_Baku_Keluar.setPreferredSize(new java.awt.Dimension(1366, 700));

        jPanel_search_baku_keluar.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_search_baku_keluar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        txt_search_kode_kartu.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_kode_kartu.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_kode_kartuKeyPressed(evt);
            }
        });

        button_search_baku_keluar.setBackground(new java.awt.Color(255, 255, 255));
        button_search_baku_keluar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_baku_keluar.setText("Search");
        button_search_baku_keluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_baku_keluarActionPerformed(evt);
            }
        });

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel16.setText("Kode Kartu Campuran :");

        button_export_BahanBakuKeluar.setBackground(new java.awt.Color(255, 255, 255));
        button_export_BahanBakuKeluar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_BahanBakuKeluar.setText("Export To Excel");
        button_export_BahanBakuKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_BahanBakuKeluarActionPerformed(evt);
            }
        });

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel19.setText("Tanggal Kartu CMP :");

        Date_filter1.setBackground(new java.awt.Color(255, 255, 255));
        Date_filter1.setDateFormatString("dd MMMM yyyy");
        Date_filter1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_filter2.setBackground(new java.awt.Color(255, 255, 255));
        Date_filter2.setDateFormatString("dd MMMM yyyy");
        Date_filter2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        javax.swing.GroupLayout jPanel_search_baku_keluarLayout = new javax.swing.GroupLayout(jPanel_search_baku_keluar);
        jPanel_search_baku_keluar.setLayout(jPanel_search_baku_keluarLayout);
        jPanel_search_baku_keluarLayout.setHorizontalGroup(
            jPanel_search_baku_keluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_baku_keluarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_search_kode_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date_filter1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date_filter2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_search_baku_keluar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(button_export_BahanBakuKeluar)
                .addContainerGap())
        );
        jPanel_search_baku_keluarLayout.setVerticalGroup(
            jPanel_search_baku_keluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_search_baku_keluarLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel_search_baku_keluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_search_baku_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_BahanBakuKeluar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_filter2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_filter1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_kode_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        Table_kartu_campuran.setAutoCreateRowSorter(true);
        Table_kartu_campuran.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_kartu_campuran.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Kartu Campuran", "Tanggal Kartu", "Catatan Kartu"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class
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
        Table_kartu_campuran.getTableHeader().setReorderingAllowed(false);
        jScrollPane7.setViewportView(Table_kartu_campuran);

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel20.setText("Total Kartu Campuran :");

        label_total_pengeluaran.setBackground(new java.awt.Color(255, 255, 255));
        label_total_pengeluaran.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        label_total_pengeluaran.setText("TOTAL");

        button_Edit.setBackground(new java.awt.Color(255, 255, 255));
        button_Edit.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_Edit.setText("Edit");
        button_Edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_EditActionPerformed(evt);
            }
        });

        button_insert.setBackground(new java.awt.Color(255, 255, 255));
        button_insert.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_insert.setText("insert");
        button_insert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_insertActionPerformed(evt);
            }
        });

        Tabel_detail.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Tabel_detail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Kartu CMP", "No Kartu Waleta", "Grade", "Keping", "Gram", "Kode Grading"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class
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
        Tabel_detail.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Tabel_detail);

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel21.setText("Total Data :");

        label_total_detail.setBackground(new java.awt.Color(255, 255, 255));
        label_total_detail.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        label_total_detail.setText("TOTAL");

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel22.setText("Total Keping :");

        label_total_keping.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        label_total_keping.setText("TOTAL");

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel23.setText("Total Gram :");

        label_total_gram.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        label_total_gram.setText("TOTAL");

        button_delete.setBackground(new java.awt.Color(255, 255, 255));
        button_delete.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete.setText("Delete");
        button_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_deleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_Bahan_Baku_KeluarLayout = new javax.swing.GroupLayout(jPanel_Bahan_Baku_Keluar);
        jPanel_Bahan_Baku_Keluar.setLayout(jPanel_Bahan_Baku_KeluarLayout);
        jPanel_Bahan_Baku_KeluarLayout.setHorizontalGroup(
            jPanel_Bahan_Baku_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Bahan_Baku_KeluarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Bahan_Baku_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel_search_baku_keluar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel_Bahan_Baku_KeluarLayout.createSequentialGroup()
                        .addGroup(jPanel_Bahan_Baku_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel_Bahan_Baku_KeluarLayout.createSequentialGroup()
                                .addComponent(button_insert)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_Edit)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_delete)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_pengeluaran))
                            .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 565, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_Bahan_Baku_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addGroup(jPanel_Bahan_Baku_KeluarLayout.createSequentialGroup()
                                .addGroup(jPanel_Bahan_Baku_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel_Bahan_Baku_KeluarLayout.createSequentialGroup()
                                        .addComponent(jLabel22)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_keping))
                                    .addGroup(jPanel_Bahan_Baku_KeluarLayout.createSequentialGroup()
                                        .addComponent(jLabel23)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_gram))
                                    .addGroup(jPanel_Bahan_Baku_KeluarLayout.createSequentialGroup()
                                        .addComponent(jLabel21)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_detail)))
                                .addGap(0, 643, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        jPanel_Bahan_Baku_KeluarLayout.setVerticalGroup(
            jPanel_Bahan_Baku_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Bahan_Baku_KeluarLayout.createSequentialGroup()
                .addComponent(jPanel_search_baku_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Bahan_Baku_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 589, Short.MAX_VALUE)
                    .addGroup(jPanel_Bahan_Baku_KeluarLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_Bahan_Baku_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_detail)
                            .addComponent(jLabel21))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_Bahan_Baku_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_keping)
                            .addComponent(jLabel22))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_Bahan_Baku_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_gram)
                            .addComponent(jLabel23))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Bahan_Baku_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_Bahan_Baku_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_pengeluaran)
                        .addComponent(jLabel20))
                    .addGroup(jPanel_Bahan_Baku_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_Edit, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_insert, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
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

    private void txt_search_kode_kartuKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_kode_kartuKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_kode_kartuKeyPressed

    private void button_search_baku_keluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_baku_keluarActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_button_search_baku_keluarActionPerformed

    private void button_export_BahanBakuKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_BahanBakuKeluarActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_kartu_campuran.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_export_BahanBakuKeluarActionPerformed

    private void button_insertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_insertActionPerformed
        // TODO add your handling code here:
        JDialog_CreateKartuCampuran dialog = new JDialog_CreateKartuCampuran(new javax.swing.JFrame(), true, "new");
        dialog.setEnabled(true);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        refreshTable();
    }//GEN-LAST:event_button_insertActionPerformed

    private void button_EditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_EditActionPerformed
        // TODO add your handling code here:
        int j = Table_kartu_campuran.getSelectedRow();
        if (j == -1) {
            JOptionPane.showMessageDialog(this, "Klik Data yang mau di edit");
        } else {
            try {
                String catatan = JOptionPane.showInputDialog("Catatan : ");
                if (catatan != null) {
                    sql = "UPDATE `tb_kartu_cmp` SET `catatan`='" + catatan + "' WHERE `kode_kartu_cmp`='" + Table_kartu_campuran.getValueAt(j, 0).toString() + "'";
                    if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                        refreshTable();
                        JOptionPane.showMessageDialog(this, "Update success!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Update failed!");
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(JPanel_Kartu_Campuran.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_EditActionPerformed

    private void button_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_deleteActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_kartu_campuran.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang ingin di hapus !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Menghapus kartu CMP di sini tidak otomatis menghapus kartu masuk waleta, lanjutkan?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    String no_kartu = Table_kartu_campuran.getValueAt(j, 0).toString();
                    String Query = "DELETE FROM `tb_kartu_cmp` WHERE `kode_kartu_cmp` = '" + no_kartu + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                        JOptionPane.showMessageDialog(this, "delete kartu CMP sukses");
                        refreshTable();
                        refresh_TabelDetail("");
                    } else {
                        JOptionPane.showMessageDialog(this, "delete gagal");
                    }
                    button_search_baku_keluar.doClick();
                }
            }
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, e);
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Kartu_Campuran.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_deleteActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser Date_filter1;
    private com.toedter.calendar.JDateChooser Date_filter2;
    private javax.swing.JTable Tabel_detail;
    public static javax.swing.JTable Table_kartu_campuran;
    public javax.swing.JButton button_Edit;
    public javax.swing.JButton button_delete;
    public static javax.swing.JButton button_export_BahanBakuKeluar;
    public javax.swing.JButton button_insert;
    private javax.swing.JButton button_search_baku_keluar;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JPanel jPanel_Bahan_Baku_Keluar;
    private javax.swing.JPanel jPanel_search_baku_keluar;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JLabel label_total_detail;
    private javax.swing.JLabel label_total_gram;
    private javax.swing.JLabel label_total_keping;
    private javax.swing.JLabel label_total_pengeluaran;
    private javax.swing.JTextField txt_search_kode_kartu;
    // End of variables declaration//GEN-END:variables
}
