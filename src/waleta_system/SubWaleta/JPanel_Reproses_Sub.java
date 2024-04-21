package waleta_system.SubWaleta;

import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;

public class JPanel_Reproses_Sub extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_Reproses_Sub() {
        initComponents();
    }

    public void init() {
        try {
            table_data_reproses_cabut.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && table_data_reproses_cabut.getSelectedRow() != -1) {
                        int i = table_data_reproses_cabut.getSelectedRow();
                        label_no_reproses.setText(table_data_reproses_cabut.getValueAt(i, 0).toString());
                        refreshTable_reProses_pencabut();
                        if (table_data_reproses_cabut.getValueAt(i, 6) == null) {
                            button_selesai_reproses.setEnabled(true);
                            button_input_pekerja_cabut.setEnabled(true);
                            button_edit_pekerja_cabut.setEnabled(true);
                            button_delete_pekerja_cabut.setEnabled(true);
                        } else {
                            button_selesai_reproses.setEnabled(false);
                            button_input_pekerja_cabut.setEnabled(false);
                            button_edit_pekerja_cabut.setEnabled(false);
                            button_delete_pekerja_cabut.setEnabled(false);
                        }
                    }
                }
            });
            refreshTable_reProses();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Reproses_Sub.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_reProses() {
        try {
            Utility.db_sub.connect();
            DefaultTableModel model_cabut = (DefaultTableModel) table_data_reproses_cabut.getModel();
            model_cabut.setRowCount(0);

            String status = "";
            switch (ComboBox_status_reproses.getSelectedIndex()) {
                case 0:
                    status = "";
                    break;
                case 1:
                    status = "AND `tanggal_selesai` IS NULL\n";
                    break;
                case 2:
                    status = "AND `tanggal_selesai` IS NOT NULL\n";
                    break;
                default:
                    break;
            }
            String filter_tanggal = "";
            if (Date_Search_reproses1.getDate() != null && Date_Search_reproses2.getDate() != null) {
                if (ComboBox_Filter_Tgl_reproses.getSelectedIndex() == 0) {
                    filter_tanggal = "AND `tanggal_terima` BETWEEN '" + dateFormat.format(Date_Search_reproses1.getDate()) + "' AND '" + dateFormat.format(Date_Search_reproses2.getDate()) + "'";
                } else if (ComboBox_Filter_Tgl_reproses.getSelectedIndex() == 1) {
                    filter_tanggal = "AND `tanggal_selesai` BETWEEN '" + dateFormat.format(Date_Search_reproses1.getDate()) + "' AND '" + dateFormat.format(Date_Search_reproses2.getDate()) + "'";
                }
            }
            sql = "SELECT `no_reproses`, `no_box`, `kode_grade`, `keping`, `gram`, `tanggal_terima`, `tanggal_selesai` "
                    + "FROM `tb_reproses_sub` "
                    + "WHERE "
                    + "`tb_reproses_sub`.`no_box` LIKE '%" + txt_search_box_reproses.getText() + "%' "
                    + filter_tanggal
                    + status;
            rs = Utility.db_sub.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getString("no_reproses");
                row[1] = rs.getString("no_box");
                row[2] = rs.getString("kode_grade");
                row[3] = rs.getInt("keping");
                row[4] = rs.getFloat("gram");
                row[5] = rs.getTimestamp("tanggal_terima");
                row[6] = rs.getTimestamp("tanggal_selesai");
                model_cabut.addRow(row);
            }

            decimalFormat.setMaximumFractionDigits(2);
            ColumnsAutoSizer.sizeColumnsToFit(table_data_reproses_cabut);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Reproses_Sub.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_reProses_pencabut() {
        try {
            Utility.db_sub.connect();
            float total_gram = 0;
            String no_reproses = label_no_reproses.getText();
            DefaultTableModel model = (DefaultTableModel) table_data_pencabut_reproses.getModel();
            model.setRowCount(0);
            sql = "SELECT `no`, `no_reproses`, `tb_reproses_sub_pencabut`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `tb_karyawan`.`bagian`, `tanggal_cabut`, `jumlah_cabut`, `jumlah_gram` \n"
                    + "FROM `tb_reproses_sub_pencabut` \n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_reproses_sub_pencabut`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                    + "WHERE `no_reproses` = '" + no_reproses + "'";
            rs = Utility.db_sub.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getInt("no");
                row[1] = rs.getInt("no_reproses");
                row[2] = rs.getDate("tanggal_cabut");
                row[3] = rs.getString("id_pegawai");
                row[4] = rs.getString("nama_pegawai");
                row[5] = rs.getString("bagian");
                row[6] = rs.getInt("jumlah_cabut");
                row[7] = rs.getFloat("jumlah_gram");
                model.addRow(row);
                total_gram = total_gram + rs.getFloat("jumlah_gram");
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_data_pencabut_reproses);
            int total_data = table_data_pencabut_reproses.getRowCount();
            label_total_data_pencabut.setText(Integer.toString(total_data));
            label_total_gram_cabutan.setText(decimalFormat.format(total_gram));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Reproses_Sub.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel61 = new javax.swing.JLabel();
        label_total_stok1 = new javax.swing.JLabel();
        jPanel_data_reproses = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        txt_search_box_reproses = new javax.swing.JTextField();
        Date_Search_reproses1 = new com.toedter.calendar.JDateChooser();
        Date_Search_reproses2 = new com.toedter.calendar.JDateChooser();
        button_search_reproses = new javax.swing.JButton();
        jLabel34 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        ComboBox_status_reproses = new javax.swing.JComboBox<>();
        ComboBox_Filter_Tgl_reproses = new javax.swing.JComboBox<>();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane11 = new javax.swing.JScrollPane();
        table_data_reproses_cabut = new javax.swing.JTable();
        button_selesai_reproses = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_data_pencabut_reproses = new javax.swing.JTable();
        button_input_pekerja_cabut = new javax.swing.JButton();
        button_delete_pekerja_cabut = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        label_total_data_pencabut = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        label_total_gram_cabutan = new javax.swing.JLabel();
        button_edit_pekerja_cabut = new javax.swing.JButton();
        label_no_reproses = new javax.swing.JLabel();
        button_terima_reproses = new javax.swing.JButton();
        button_terima_reproses_scan_qr = new javax.swing.JButton();

        jLabel61.setBackground(new java.awt.Color(255, 255, 255));
        jLabel61.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel61.setText("Total Stok :");

        label_total_stok1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_stok1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_stok1.setText("0");

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel_data_reproses.setBackground(new java.awt.Color(255, 255, 255));

        jLabel27.setBackground(new java.awt.Color(255, 255, 255));
        jLabel27.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel27.setText("No Box :");

        txt_search_box_reproses.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_box_reproses.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_box_reprosesKeyPressed(evt);
            }
        });

        Date_Search_reproses1.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search_reproses1.setDateFormatString("dd MMMM yyyy");
        Date_Search_reproses1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_Search_reproses2.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search_reproses2.setDateFormatString("dd MMMM yyyy");
        Date_Search_reproses2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_search_reproses.setBackground(new java.awt.Color(255, 255, 255));
        button_search_reproses.setText("Search");
        button_search_reproses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_reprosesActionPerformed(evt);
            }
        });

        jLabel34.setBackground(new java.awt.Color(255, 255, 255));
        jLabel34.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel34.setText("Data re-proses Eksternal");

        jLabel59.setBackground(new java.awt.Color(255, 255, 255));
        jLabel59.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel59.setText("Status :");

        ComboBox_status_reproses.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_status_reproses.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "IN PROSES", "FINISHED" }));

        ComboBox_Filter_Tgl_reproses.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_Filter_Tgl_reproses.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tanggal Terima", "Tanggal Selesai" }));

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        table_data_reproses_cabut.setAutoCreateRowSorter(true);
        table_data_reproses_cabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_reproses_cabut.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Reproses", "No Box", "Kode Grade", "Kpg", "Gram", "Tgl Terima", "Tgl Selesai"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Object.class, java.lang.Object.class
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
        table_data_reproses_cabut.getTableHeader().setReorderingAllowed(false);
        jScrollPane11.setViewportView(table_data_reproses_cabut);

        button_selesai_reproses.setBackground(new java.awt.Color(255, 255, 255));
        button_selesai_reproses.setText("Selesai Reproses");
        button_selesai_reproses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_selesai_reprosesActionPerformed(evt);
            }
        });

        table_data_pencabut_reproses.setAutoCreateRowSorter(true);
        table_data_pencabut_reproses.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_pencabut_reproses.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "No Reproses", "Tanggal", "ID Pegawai", "Nama", "Bagian", "Kpg", "Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class
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
        jScrollPane1.setViewportView(table_data_pencabut_reproses);

        button_input_pekerja_cabut.setBackground(new java.awt.Color(255, 255, 255));
        button_input_pekerja_cabut.setText("Input Pekerja Cabut");
        button_input_pekerja_cabut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_pekerja_cabutActionPerformed(evt);
            }
        });

        button_delete_pekerja_cabut.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_pekerja_cabut.setText("Delete Pekerja Cabut");
        button_delete_pekerja_cabut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_pekerja_cabutActionPerformed(evt);
            }
        });

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText("Total Data Pencabut :");

        label_total_data_pencabut.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_pencabut.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_data_pencabut.setText("0");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("Total Gram :");

        label_total_gram_cabutan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_cabutan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_gram_cabutan.setText("0");

        button_edit_pekerja_cabut.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_pekerja_cabut.setText("Edit Pekerja Cabut");
        button_edit_pekerja_cabut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_pekerja_cabutActionPerformed(evt);
            }
        });

        label_no_reproses.setBackground(new java.awt.Color(255, 255, 255));
        label_no_reproses.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_no_reproses.setText("NO REPROSES");

        button_terima_reproses.setBackground(new java.awt.Color(255, 255, 255));
        button_terima_reproses.setText("Terima Reproses");
        button_terima_reproses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_terima_reprosesActionPerformed(evt);
            }
        });

        button_terima_reproses_scan_qr.setBackground(new java.awt.Color(255, 255, 255));
        button_terima_reproses_scan_qr.setText("Scan QR Terima Reproses");
        button_terima_reproses_scan_qr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_terima_reproses_scan_qrActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(button_terima_reproses_scan_qr)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_terima_reproses)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_selesai_reproses)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 775, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(button_input_pekerja_cabut)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_edit_pekerja_cabut)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_delete_pekerja_cabut)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_no_reproses)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data_pencabut)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_cabutan)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_input_pekerja_cabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete_pekerja_cabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(label_total_data_pencabut)
                    .addComponent(jLabel3)
                    .addComponent(label_total_gram_cabutan)
                    .addComponent(button_selesai_reproses, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_edit_pekerja_cabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_no_reproses)
                    .addComponent(button_terima_reproses, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_terima_reproses_scan_qr, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("CABUT", jPanel2);

        javax.swing.GroupLayout jPanel_data_reprosesLayout = new javax.swing.GroupLayout(jPanel_data_reproses);
        jPanel_data_reproses.setLayout(jPanel_data_reprosesLayout);
        jPanel_data_reprosesLayout.setHorizontalGroup(
            jPanel_data_reprosesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_data_reprosesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_data_reprosesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel34)
                    .addGroup(jPanel_data_reprosesLayout.createSequentialGroup()
                        .addComponent(jLabel27)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_box_reproses, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_Filter_Tgl_reproses, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Search_reproses1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Search_reproses2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel59)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_status_reproses, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search_reproses)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel_data_reprosesLayout.setVerticalGroup(
            jPanel_data_reprosesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_data_reprosesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_data_reprosesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel_data_reprosesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel_data_reprosesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_search_box_reproses, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_Filter_Tgl_reproses, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(Date_Search_reproses1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Date_Search_reproses2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_data_reprosesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel59, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ComboBox_status_reproses, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_search_reproses, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_data_reproses, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_data_reproses, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txt_search_box_reprosesKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_box_reprosesKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_reProses();
        }
    }//GEN-LAST:event_txt_search_box_reprosesKeyPressed

    private void button_search_reprosesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_reprosesActionPerformed
        // TODO add your handling code here:
        refreshTable_reProses();
    }//GEN-LAST:event_button_search_reprosesActionPerformed

    private void button_selesai_reprosesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_selesai_reprosesActionPerformed
        // TODO add your handling code here:
        try {
            int j = table_data_reproses_cabut.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih salah satu data reproses !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Input Selesai Reproses?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    String no_reproses = table_data_reproses_cabut.getValueAt(j, 0).toString();
                    double total_gram_cabutan = 0;
                    double gram_box = Double.valueOf(table_data_reproses_cabut.getValueAt(j, 4).toString());
                    sql = "SELECT ROUND(SUM(`jumlah_gram`), 1) AS 'total_gram_cabutan' "
                            + "FROM `tb_reproses_sub_pencabut` WHERE `no_reproses` = '" + no_reproses + "'";
                    rs = Utility.db_sub.getStatement().executeQuery(sql);
                    if (rs.next()) {
                        total_gram_cabutan = rs.getDouble("total_gram_cabutan");
                    }
                    if (total_gram_cabutan == gram_box) {
                        sql = "UPDATE `tb_reproses_sub` SET `tanggal_selesai`=NOW() WHERE `no_reproses` = '" + no_reproses + "'";
                        Utility.db_sub.getConnection().createStatement();
                        if ((Utility.db_sub.getStatement().executeUpdate(sql)) == 1) {
                            JOptionPane.showMessageDialog(this, "data Tersimpan !");
                        } else {
                            JOptionPane.showMessageDialog(this, "Gagal / Tidak ada perubahan data !");
                        }
                        refreshTable_reProses();
                    } else {
                        JOptionPane.showMessageDialog(this, "Maaf total gram cabutan dan gram box tidak sama !");
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Reproses_Sub.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_selesai_reprosesActionPerformed

    private void button_input_pekerja_cabutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_pekerja_cabutActionPerformed
        // TODO add your handling code here:
        int x = table_data_reproses_cabut.getSelectedRow();
        if (x < 0) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih data !");
        } else {
            String no_reproses = label_no_reproses.getText();
            JDialog_Reproses_Sub_pencabut dialog = new JDialog_Reproses_Sub_pencabut(new javax.swing.JFrame(), true, no_reproses, null);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            dialog.setEnabled(true);
            refreshTable_reProses_pencabut();
        }
    }//GEN-LAST:event_button_input_pekerja_cabutActionPerformed

    private void button_delete_pekerja_cabutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_pekerja_cabutActionPerformed
        // TODO add your handling code here:
        try {
            int j = table_data_pencabut_reproses.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang ingin di hapus !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    String no = table_data_pencabut_reproses.getValueAt(j, 0).toString();
                    sql = "DELETE FROM `tb_reproses_sub_pencabut` WHERE "
                            + "`no`='" + no + "'";
                    Utility.db_sub.getConnection().createStatement();
                    if ((Utility.db_sub.getStatement().executeUpdate(sql)) == 1) {
                        JOptionPane.showMessageDialog(this, "data Saved !");
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed !");
                    }
                    refreshTable_reProses_pencabut();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Reproses_Sub.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_delete_pekerja_cabutActionPerformed

    private void button_edit_pekerja_cabutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_pekerja_cabutActionPerformed
        // TODO add your handling code here:
        int x = table_data_pencabut_reproses.getSelectedRow();
        if (x < 0) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih data !");
        } else {
            String no = table_data_pencabut_reproses.getValueAt(x, 0).toString();
            String no_reproses = table_data_pencabut_reproses.getValueAt(x, 1).toString();
            JDialog_Reproses_Sub_pencabut dialog = new JDialog_Reproses_Sub_pencabut(new javax.swing.JFrame(), true, no_reproses, no);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            dialog.setEnabled(true);
            refreshTable_reProses_pencabut();
        }
    }//GEN-LAST:event_button_edit_pekerja_cabutActionPerformed

    private void button_terima_reprosesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_terima_reprosesActionPerformed
        // TODO add your handling code here:
        JDialog_Reproses_Sub_Terima dialog = new JDialog_Reproses_Sub_Terima(new javax.swing.JFrame(), true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        refreshTable_reProses_pencabut();
    }//GEN-LAST:event_button_terima_reprosesActionPerformed

    private void button_terima_reproses_scan_qrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_terima_reproses_scan_qrActionPerformed
        // TODO add your handling code here:
        try {
            String QR = JOptionPane.showInputDialog(this, "Silahkan Scan QR :");
            if (QR != null && !QR.equals("")) {
                String[] data = QR.split(";");
                Utility.db_sub.connect();
                String Query = "INSERT INTO `tb_reproses_sub`(`no_reproses`, `no_box`, `kode_grade`, `keping`, `gram`, `tanggal_terima`) "
                        + "VALUES ("
                        + "'" + data[0] + "',"
                        + "'" + data[1] + "',"
                        + "'" + data[2] + "',"
                        + "'" + data[3] + "',"
                        + "'" + data[4] + "',"
                        + "NOW()"
                        + ")";
                Utility.db_sub.getConnection().createStatement();
                if ((Utility.db_sub.getStatement().executeUpdate(Query)) == 1) {
                    JOptionPane.showMessageDialog(this, "sukses input reproses sub");
                    refreshTable_reProses();
                } else {
                    JOptionPane.showMessageDialog(this, "input data gagal");
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Reproses_Sub.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_terima_reproses_scan_qrActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_Filter_Tgl_reproses;
    private javax.swing.JComboBox<String> ComboBox_status_reproses;
    private com.toedter.calendar.JDateChooser Date_Search_reproses1;
    private com.toedter.calendar.JDateChooser Date_Search_reproses2;
    public javax.swing.JButton button_delete_pekerja_cabut;
    public javax.swing.JButton button_edit_pekerja_cabut;
    public javax.swing.JButton button_input_pekerja_cabut;
    private javax.swing.JButton button_search_reproses;
    public javax.swing.JButton button_selesai_reproses;
    public javax.swing.JButton button_terima_reproses;
    public javax.swing.JButton button_terima_reproses_scan_qr;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel_data_reproses;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label_no_reproses;
    private javax.swing.JLabel label_total_data_pencabut;
    private javax.swing.JLabel label_total_gram_cabutan;
    private javax.swing.JLabel label_total_stok1;
    private javax.swing.JTable table_data_pencabut_reproses;
    private javax.swing.JTable table_data_reproses_cabut;
    private javax.swing.JTextField txt_search_box_reproses;
    // End of variables declaration//GEN-END:variables
}
