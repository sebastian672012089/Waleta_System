package waleta_system.Finance;

import waleta_system.Class.Utility;

import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

public class JPanel_Neraca extends javax.swing.JPanel {

    
    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date today = new Date();
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    DefaultTableModel dm = new DefaultTableModel();
    JFileChooser chooser = new JFileChooser();
    DecimalFormat decimalFormat = new DecimalFormat();

    public JPanel_Neraca() {
        initComponents();
    }

    public void init() {
        try {
            
            

            ComboBox_Kategori.removeAllItems();
            ComboBox_Kategori.addItem("All");
            sql = "SELECT DISTINCT(`kategori`) AS 'kategori' FROM `tb_neraca` WHERE 1";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_Kategori.addItem(rs.getString("kategori"));
            }
            refreshTable_bulan();
            int x = tabel_bulan.getSelectedRow();
            if (x > -1) {
                String bulan = tabel_bulan.getValueAt(x, 0).toString();
                int tahun = Integer.valueOf(tabel_bulan.getValueAt(x, 1).toString());
                refreshTable(bulan, tahun);
            } else {
                if (tabel_bulan.getRowCount() > 0) {
                    String bulan = tabel_bulan.getValueAt(0, 0).toString();
                    int tahun = Integer.valueOf(tabel_bulan.getValueAt(0, 1).toString());
                    refreshTable(bulan, tahun);
                }
            }

            tabel_bulan.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && tabel_bulan.getSelectedRow() != -1) {
                        int x = tabel_bulan.getSelectedRow();
                        if (x > -1) {
                            String bulan = tabel_bulan.getValueAt(x, 0).toString();
                            int tahun = Integer.valueOf(tabel_bulan.getValueAt(x, 1).toString());
                            refreshTable(bulan, tahun);
                        }
                    }
                }
            });

        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Neraca.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(JPanel_Neraca.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable(String bulan, int tahun) {
        try {
            double total_aktiva = 0, total_kewajiban = 0;
            DefaultTableModel model = (DefaultTableModel) tabel_Neraca.getModel();
            model.setRowCount(0);

            String kategori = "AND `tb_neraca`.`kategori` = '" + ComboBox_Kategori.getSelectedItem().toString() + "' ";
            if ("All".equals(ComboBox_Kategori.getSelectedItem().toString())) {
                kategori = "";
            }

            sql = "SELECT `no`, `bulan`, `kategori`, `deskripsi`, `nilai` FROM `tb_neraca`\n"
                    + "WHERE `deskripsi` LIKE '%" + txt_keywords.getText() + "%' AND MONTHNAME(`bulan`) = '" + bulan + "' AND YEAR(`bulan`) = '" + tahun + "'"
                    + "ORDER BY `kategori`, `deskripsi`"
                    + kategori;
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[5];
            while (rs.next()) {
                row[0] = rs.getString("no");
                row[1] = rs.getDate("bulan");
                row[2] = rs.getString("kategori");
                row[3] = rs.getString("deskripsi");
                row[4] = rs.getDouble("nilai");
                if (rs.getString("kategori").contains("Aktiva")) {
                    total_aktiva = total_aktiva + rs.getDouble("nilai");
                } else {
                    total_kewajiban = total_kewajiban + rs.getDouble("nilai");
                }
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_Neraca);
            label_total_aktiva.setText(decimalFormat.format(total_aktiva));
            label_total_kewajiban.setText(decimalFormat.format(total_kewajiban));
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Neraca.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_bulan() {
        try {
            DefaultTableModel model = (DefaultTableModel) tabel_bulan.getModel();
            model.setRowCount(0);
            sql = "SELECT MONTHNAME(`bulan`) AS 'bulan', YEAR(`bulan`) AS 'tahun' "
                    + "FROM `tb_neraca` WHERE 1 GROUP BY `bulan`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString("bulan"), rs.getString("tahun")});
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Neraca.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void ImportFromCSV() throws ParseException {
        try {
            int n = 0;
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
                            Query = "INSERT INTO `tb_neraca`(`bulan`, `kategori`, `deskripsi`, `nilai`) "
                                    + "VALUES ('" + value[0] + "','" + value[1] + "','" + value[2] + "','" + value[3] + "')";
                            Utility.db.getConnection().prepareStatement(Query);
                            if ((Utility.db.getStatement().executeUpdate(Query)) > 0) {
//                                System.out.println(value[0] + "','" + value[1] + "','" + value[2] + "','" + value[3]);
                                n++;
                            } else {
                                JOptionPane.showMessageDialog(this, "Failed insert : " + value[3]);
                            }
                        }
                        Utility.db.getConnection().commit();
                    } catch (SQLException ex) {
                        try {
                            Utility.db.getConnection().rollback();
                        } catch (SQLException x) {
                            Logger.getLogger(JPanel_Neraca.class.getName()).log(Level.SEVERE, null, "BBB : " + x);
                        }
                        JOptionPane.showMessageDialog(this, ex);
                        Logger.getLogger(JPanel_Neraca.class.getName()).log(Level.SEVERE, null, "AAA : " + ex);
                    } finally {
                        JOptionPane.showMessageDialog(this, "Data Berhasil Masuk : " + n);
                    }
                }
            }
        } catch (HeadlessException | IOException ex) {
            JOptionPane.showMessageDialog(this, ex);
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

        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_Neraca = new javax.swing.JTable();
        jPanel_search_karyawan = new javax.swing.JPanel();
        txt_keywords = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        ComboBox_Kategori = new javax.swing.JComboBox<>();
        button_refresh = new javax.swing.JButton();
        button_import = new javax.swing.JButton();
        button_new_pengeluaran = new javax.swing.JButton();
        button_edit_pengeluaran = new javax.swing.JButton();
        button_delete_pengeluaran = new javax.swing.JButton();
        button_export_data_pengeluaran = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        label_total_aktiva = new javax.swing.JLabel();
        label_total_kewajiban = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabel_bulan = new javax.swing.JTable();

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel1.setText("DATA NECARA KEUANGAN WALETA");

        tabel_Neraca.setAutoCreateRowSorter(true);
        tabel_Neraca.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        tabel_Neraca.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "Tanggal", "Kategori", "Deskripsi", "Biaya"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class
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
        tabel_Neraca.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tabel_Neraca);
        if (tabel_Neraca.getColumnModel().getColumnCount() > 0) {
            tabel_Neraca.getColumnModel().getColumn(1).setResizable(false);
        }

        jPanel_search_karyawan.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_search_karyawan.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Filter", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N

        txt_keywords.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_keywords.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_keywordsKeyPressed(evt);
            }
        });

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Deskripsi :");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("Kategori :");

        ComboBox_Kategori.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_Kategori.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        button_refresh.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh.setText("Refresh");
        button_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refreshActionPerformed(evt);
            }
        });

        button_import.setBackground(new java.awt.Color(255, 255, 255));
        button_import.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_import.setText("Import from CSV");
        button_import.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_importActionPerformed(evt);
            }
        });

        button_new_pengeluaran.setBackground(new java.awt.Color(255, 255, 255));
        button_new_pengeluaran.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_new_pengeluaran.setText("+ New");
        button_new_pengeluaran.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_new_pengeluaranActionPerformed(evt);
            }
        });

        button_edit_pengeluaran.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_pengeluaran.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_edit_pengeluaran.setText("Edit");
        button_edit_pengeluaran.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_pengeluaranActionPerformed(evt);
            }
        });

        button_delete_pengeluaran.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_pengeluaran.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete_pengeluaran.setText("Delete");
        button_delete_pengeluaran.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_pengeluaranActionPerformed(evt);
            }
        });

        button_export_data_pengeluaran.setBackground(new java.awt.Color(255, 255, 255));
        button_export_data_pengeluaran.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        button_export_data_pengeluaran.setText("Export to Excel");
        button_export_data_pengeluaran.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_data_pengeluaranActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_search_karyawanLayout = new javax.swing.GroupLayout(jPanel_search_karyawan);
        jPanel_search_karyawan.setLayout(jPanel_search_karyawanLayout);
        jPanel_search_karyawanLayout.setHorizontalGroup(
            jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_karyawanLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_keywords, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ComboBox_Kategori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_refresh)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 545, Short.MAX_VALUE)
                .addComponent(button_import)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_new_pengeluaran)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_edit_pengeluaran)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_delete_pengeluaran)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_export_data_pengeluaran)
                .addContainerGap())
        );
        jPanel_search_karyawanLayout.setVerticalGroup(
            jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_karyawanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_Kategori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_keywords, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(button_export_data_pengeluaran, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete_pengeluaran, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(button_new_pengeluaran, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_import, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(button_edit_pengeluaran, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel15.setText("Total Aktiva :");

        label_total_aktiva.setBackground(new java.awt.Color(255, 255, 255));
        label_total_aktiva.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_aktiva.setText("0");

        label_total_kewajiban.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kewajiban.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_kewajiban.setText("0");

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel16.setText("Total Kewajiban & Ekuitas :");

        tabel_bulan.setAutoCreateRowSorter(true);
        tabel_bulan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        tabel_bulan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Bulan", "Tahun"
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
        tabel_bulan.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tabel_bulan);
        if (tabel_bulan.getColumnModel().getColumnCount() > 0) {
            tabel_bulan.getColumnModel().getColumn(0).setResizable(false);
            tabel_bulan.getColumnModel().getColumn(1).setResizable(false);
        }

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel_search_karyawan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_aktiva)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_kewajiban))
                            .addComponent(jLabel1))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel_search_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 583, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_aktiva, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_kewajiban, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txt_keywordsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_keywordsKeyPressed
        // TODO add your handling code here:
        refreshTable_bulan();
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            int x = tabel_bulan.getSelectedRow();
            if (x > -1) {
                String bulan = tabel_bulan.getValueAt(x, 0).toString();
                int tahun = Integer.valueOf(tabel_bulan.getValueAt(x, 1).toString());
                refreshTable(bulan, tahun);
            } else {
                if (tabel_bulan.getRowCount() > 0) {
                    String bulan = tabel_bulan.getValueAt(0, 0).toString();
                    int tahun = Integer.valueOf(tabel_bulan.getValueAt(0, 1).toString());
                    refreshTable(bulan, tahun);
                }
            }
        }
    }//GEN-LAST:event_txt_keywordsKeyPressed

    private void button_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refreshActionPerformed
        // TODO add your handling code here:
        refreshTable_bulan();
        int x = tabel_bulan.getSelectedRow();
        if (x > -1) {
            String bulan = tabel_bulan.getValueAt(x, 0).toString();
            int tahun = Integer.valueOf(tabel_bulan.getValueAt(x, 1).toString());
            refreshTable(bulan, tahun);
        } else {
            if (tabel_bulan.getRowCount() > 0) {
                String bulan = tabel_bulan.getValueAt(0, 0).toString();
                int tahun = Integer.valueOf(tabel_bulan.getValueAt(0, 1).toString());
                refreshTable(bulan, tahun);
            }
        }
    }//GEN-LAST:event_button_refreshActionPerformed

    private void button_edit_pengeluaranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_pengeluaranActionPerformed
        // TODO add your handling code here:
        int x = tabel_bulan.getSelectedRow();
        if (x > -1) {
            String bulan = tabel_bulan.getValueAt(x, 0).toString();
            int tahun = Integer.valueOf(tabel_bulan.getValueAt(x, 1).toString());
            JDialog_NewEdit_Neraca dialog = new JDialog_NewEdit_Neraca(new javax.swing.JFrame(), true, "edit", bulan, tahun);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            dialog.setEnabled(true);
            refreshTable_bulan();
            if (x > -1) {
                refreshTable(bulan, tahun);
            } else {
                if (tabel_bulan.getRowCount() > 0) {
                    String a = tabel_bulan.getValueAt(0, 0).toString();
                    int b = Integer.valueOf(tabel_bulan.getValueAt(0, 1).toString());
                    refreshTable(a, b);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Silahkan klik pada data yang akan di Edit");
        }
    }//GEN-LAST:event_button_edit_pengeluaranActionPerformed

    private void button_export_data_pengeluaranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_data_pengeluaranActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_Neraca.getModel();
        ExportToExcel.writeToExcel(model, jPanel2);
    }//GEN-LAST:event_button_export_data_pengeluaranActionPerformed

    private void button_new_pengeluaranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_new_pengeluaranActionPerformed
        // TODO add your handling code here:
        JDialog_NewEdit_Neraca dialog = new JDialog_NewEdit_Neraca(new javax.swing.JFrame(), true, "new", null, 0);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        refreshTable_bulan();
        int x = tabel_bulan.getSelectedRow();
        if (x > -1) {
            String bulan = tabel_bulan.getValueAt(x, 0).toString();
            int tahun = Integer.valueOf(tabel_bulan.getValueAt(x, 1).toString());
            refreshTable(bulan, tahun);
        } else {
            if (tabel_bulan.getRowCount() > 0) {
                String bulan = tabel_bulan.getValueAt(0, 0).toString();
                int tahun = Integer.valueOf(tabel_bulan.getValueAt(0, 1).toString());
                refreshTable(bulan, tahun);
            }
        }
    }//GEN-LAST:event_button_new_pengeluaranActionPerformed

    private void button_delete_pengeluaranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_pengeluaranActionPerformed
        // TODO add your handling code here:
        try {
            int j = tabel_bulan.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan Pilih data pada bulan yang akan di hapus");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    String bulan = tabel_bulan.getValueAt(j, 0).toString();
                    int tahun = Integer.valueOf(tabel_bulan.getValueAt(j, 1).toString());
                    String Query = "DELETE FROM `tb_neraca` WHERE MONTHNAME(`bulan`) = '" + bulan + "' AND YEAR(`bulan`) = '" + tahun + "'";
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(Query);
                    refreshTable_bulan();
                }
            }
        } catch (HeadlessException | SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Neraca.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_delete_pengeluaranActionPerformed

    private void button_importActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_importActionPerformed
        // TODO add your handling code here:
        try {
            // TODO add your handling code here:
            ImportFromCSV();
        } catch (ParseException ex) {
            Logger.getLogger(JPanel_Neraca.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_importActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_Kategori;
    public static javax.swing.JButton button_delete_pengeluaran;
    public static javax.swing.JButton button_edit_pengeluaran;
    private javax.swing.JButton button_export_data_pengeluaran;
    public static javax.swing.JButton button_import;
    public static javax.swing.JButton button_new_pengeluaran;
    public static javax.swing.JButton button_refresh;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel_search_karyawan;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel label_total_aktiva;
    private javax.swing.JLabel label_total_kewajiban;
    private javax.swing.JTable tabel_Neraca;
    private javax.swing.JTable tabel_bulan;
    private javax.swing.JTextField txt_keywords;
    // End of variables declaration//GEN-END:variables
}
