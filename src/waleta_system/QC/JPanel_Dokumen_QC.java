package waleta_system.QC;

import java.awt.Color;
import java.awt.Component;
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
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;
import waleta_system.Class.ExportToExcel;

public class JPanel_Dokumen_QC extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    DecimalFormat decimalFormat = new DecimalFormat();

    public JPanel_Dokumen_QC() {
        initComponents();
        Table_pembaruan_dokumen.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && Table_pembaruan_dokumen.getSelectedRow() != -1) {
                    int i = Table_pembaruan_dokumen.getSelectedRow();
                    ComboBox_kode_dokumen.setSelectedItem(Table_pembaruan_dokumen.getValueAt(i, 0).toString() + "-" + Table_pembaruan_dokumen.getValueAt(i, 2).toString());
                    try {
                        Date_Dokumen.setDate(dateFormat.parse(Table_pembaruan_dokumen.getValueAt(i, 3).toString()));
                    } catch (ParseException ex) {
                        Logger.getLogger(JPanel_Dokumen_QC.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        table_master_dokumen.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && table_master_dokumen.getSelectedRow() != -1) {
                    int i = table_master_dokumen.getSelectedRow();
                    txt_kode_dokumen.setText(table_master_dokumen.getValueAt(i, 0).toString());
                    txt_nama_dokumen.setText(table_master_dokumen.getValueAt(i, 1).toString());
                    txt_tempat_pengujian.setText(table_master_dokumen.getValueAt(i, 2).toString());
                    ComboBox_jenis_dokumen.setSelectedItem(table_master_dokumen.getValueAt(i, 3).toString());
                    txt_keterangan_dokumen.setText(table_master_dokumen.getValueAt(i, 4).toString());
                    txt_masa_berlaku.setText(table_master_dokumen.getValueAt(i, 5).toString());
                }
            }
        });
    }

    public void init() {
        refreshComboBox();
        refreshTable_data_pembaruan_dokumen();
        refreshTable_data_master_dokumen();
    }

    public void refreshComboBox() {
        try {
            ComboBox_kode_dokumen.removeAllItems();
            sql = "SELECT `kode_dokumen`, `nama_dokumen` FROM `tb_dokumen_qc` WHERE 1";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_kode_dokumen.addItem(rs.getString("kode_dokumen") + "-" + rs.getString("nama_dokumen"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Dokumen_QC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_data_pembaruan_dokumen() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_pembaruan_dokumen.getModel();
            model.setRowCount(0);
            String filter_tanggal_dokumen = "";
            if (DateFilter_tgl_dokumen1.getDate() != null && DateFilter_tgl_dokumen2.getDate() != null) {
                filter_tanggal_dokumen = " AND (`tanggal_dokumen` BETWEEN '" + dateFormat.format(DateFilter_tgl_dokumen1.getDate()) + "' AND '" + dateFormat.format(DateFilter_tgl_dokumen2.getDate()) + "') ";
            }
            String filter_tanggal_kadaluarsa = "";
            if (DateFilter_tgl_kadaluarsa1.getDate() != null && DateFilter_tgl_kadaluarsa2.getDate() != null) {
                filter_tanggal_kadaluarsa = " AND `tanggal_kadaluarsa` BETWEEN '" + dateFormat.format(DateFilter_tgl_kadaluarsa1.getDate()) + "' AND '" + dateFormat.format(DateFilter_tgl_kadaluarsa2.getDate()) + "') ";
            }
            sql = "SELECT `no_dokumen`, `tb_dokumen_qc_update`.`kode_dokumen`, `tb_dokumen_qc`.`nama_dokumen`, `tanggal_dokumen`, `tanggal_kadaluarsa`, DATEDIFF(`tanggal_kadaluarsa`, CURRENT_DATE()) AS 'hari_jatuh_tempo' \n"
                    + "FROM `tb_dokumen_qc_update` \n"
                    + "LEFT JOIN `tb_dokumen_qc` ON `tb_dokumen_qc`.`kode_dokumen` = `tb_dokumen_qc_update`.`kode_dokumen`\n"
                    + "WHERE (`no_dokumen` LIKE '%" + txt_search_dokumen.getText() + "%' OR `tb_dokumen_qc_update`.`kode_dokumen` LIKE '%" + txt_search_dokumen.getText() + "%' OR `tb_dokumen_qc`.`nama_dokumen` LIKE '%" + txt_search_dokumen.getText() + "%') "
                    + filter_tanggal_dokumen + filter_tanggal_kadaluarsa;
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getString("kode_dokumen");
                row[1] = rs.getString("no_dokumen");
                row[2] = rs.getString("nama_dokumen");
                row[3] = rs.getDate("tanggal_dokumen");
                row[4] = rs.getDate("tanggal_kadaluarsa");
                row[5] = rs.getInt("hari_jatuh_tempo");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_pembaruan_dokumen);
            int rowData = Table_pembaruan_dokumen.getRowCount();
            label_total_data.setText(Integer.toString(rowData));

            Table_pembaruan_dokumen.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (isSelected) {
                        comp.setBackground(Table_pembaruan_dokumen.getSelectionBackground());
                        comp.setForeground(Table_pembaruan_dokumen.getSelectionForeground());
                    } else {
                        if ((int) Table_pembaruan_dokumen.getValueAt(row, 5) < 0) {
                            comp.setBackground(Color.RED);
                            comp.setForeground(Color.WHITE);
                        } else if ((int) Table_pembaruan_dokumen.getValueAt(row, 5) < 30) {
                            comp.setBackground(Color.ORANGE);
                            comp.setForeground(Color.BLACK);
                        } else {
                            comp.setBackground(Table_pembaruan_dokumen.getBackground());
                            comp.setForeground(Table_pembaruan_dokumen.getForeground());
                        }
                    }
                    return comp;
                }
            });
            Table_pembaruan_dokumen.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (isSelected) {
                        comp.setBackground(Table_pembaruan_dokumen.getSelectionBackground());
                        comp.setForeground(Table_pembaruan_dokumen.getSelectionForeground());
                    } else {
                        if ((int) Table_pembaruan_dokumen.getValueAt(row, 5) < 0) {
                            comp.setBackground(Color.RED);
                            comp.setForeground(Color.WHITE);
                        } else if ((int) Table_pembaruan_dokumen.getValueAt(row, 5) < 30) {
                            comp.setBackground(Color.ORANGE);
                            comp.setForeground(Color.BLACK);
                        } else {
                            comp.setBackground(Table_pembaruan_dokumen.getBackground());
                            comp.setForeground(Table_pembaruan_dokumen.getForeground());
                        }
                    }
                    return comp;
                }
            });
            Table_pembaruan_dokumen.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (isSelected) {
                        comp.setBackground(Table_pembaruan_dokumen.getSelectionBackground());
                        comp.setForeground(Table_pembaruan_dokumen.getSelectionForeground());
                    } else {
                        if ((int) Table_pembaruan_dokumen.getValueAt(row, 5) < 0) {
                            comp.setBackground(Color.RED);
                            comp.setForeground(Color.WHITE);
                        } else if ((int) Table_pembaruan_dokumen.getValueAt(row, 5) < 30) {
                            comp.setBackground(Color.ORANGE);
                            comp.setForeground(Color.BLACK);
                        } else {
                            comp.setBackground(Table_pembaruan_dokumen.getBackground());
                            comp.setForeground(Table_pembaruan_dokumen.getForeground());
                        }
                    }
                    return comp;
                }
            });
            Table_pembaruan_dokumen.repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Dokumen_QC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_data_master_dokumen() {
        try {
            DefaultTableModel model = (DefaultTableModel) table_master_dokumen.getModel();
            model.setRowCount(0);
            sql = "SELECT `kode_dokumen`, `nama_dokumen`, `tempat_pengujian`, `jenis_dokumen`, `keterangan`, `masa_berlaku` \n"
                    + "FROM `tb_dokumen_qc` \n"
                    + "WHERE (`kode_dokumen` LIKE '%" + txt_search_master_dokumen.getText() + "%' OR `nama_dokumen` LIKE '%" + txt_search_master_dokumen.getText() + "%') ";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getString("kode_dokumen");
                row[1] = rs.getString("nama_dokumen");
                row[2] = rs.getString("tempat_pengujian");
                row[3] = rs.getString("jenis_dokumen");
                row[4] = rs.getString("keterangan");
                row[5] = rs.getInt("masa_berlaku");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_master_dokumen);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Dokumen_QC.class.getName()).log(Level.SEVERE, null, ex);
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

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel_Pembaruan_Dokumen_QC = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        Table_pembaruan_dokumen = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        button_insert_pembaruan_dokumen = new javax.swing.JButton();
        button_update_pembaruan_dokumen = new javax.swing.JButton();
        button_clear_pembaruan_dokumen = new javax.swing.JButton();
        button_delete_pembaruan_dokumen = new javax.swing.JButton();
        label_alamat_customer_baku = new javax.swing.JLabel();
        label_nama_customer_baku1 = new javax.swing.JLabel();
        ComboBox_kode_dokumen = new javax.swing.JComboBox<>();
        Date_Dokumen = new com.toedter.calendar.JDateChooser();
        jLabel12 = new javax.swing.JLabel();
        label_total_data = new javax.swing.JLabel();
        txt_search_dokumen = new javax.swing.JTextField();
        button_search_dokumen = new javax.swing.JButton();
        button_export_pembaruan_dokumen = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        DateFilter_tgl_dokumen1 = new com.toedter.calendar.JDateChooser();
        DateFilter_tgl_dokumen2 = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        DateFilter_tgl_kadaluarsa1 = new com.toedter.calendar.JDateChooser();
        DateFilter_tgl_kadaluarsa2 = new com.toedter.calendar.JDateChooser();
        jPanel_master_dokumen = new javax.swing.JPanel();
        jPanel_operation_master_dokumen = new javax.swing.JPanel();
        button_insert_master_dokumen = new javax.swing.JButton();
        button_update_master_dokumen = new javax.swing.JButton();
        button_clear_master_dokumen = new javax.swing.JButton();
        button_delete_master_dokumen = new javax.swing.JButton();
        txt_nama_dokumen = new javax.swing.JTextField();
        txt_masa_berlaku = new javax.swing.JTextField();
        label_nama_customer_baku2 = new javax.swing.JLabel();
        label_noTelp_customer_baku3 = new javax.swing.JLabel();
        txt_kode_dokumen = new javax.swing.JTextField();
        label_noTelp_customer_baku4 = new javax.swing.JLabel();
        label_noTelp_customer_baku5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txt_keterangan_dokumen = new javax.swing.JTextArea();
        label_noTelp_customer_baku6 = new javax.swing.JLabel();
        txt_tempat_pengujian = new javax.swing.JTextField();
        label_noTelp_customer_baku7 = new javax.swing.JLabel();
        ComboBox_jenis_dokumen = new javax.swing.JComboBox<>();
        jScrollPane11 = new javax.swing.JScrollPane();
        table_master_dokumen = new javax.swing.JTable();
        button_search_master_dokumen = new javax.swing.JButton();
        txt_search_master_dokumen = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N

        jPanel_Pembaruan_Dokumen_QC.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_Pembaruan_Dokumen_QC.setPreferredSize(new java.awt.Dimension(1366, 701));

        Table_pembaruan_dokumen.setAutoCreateRowSorter(true);
        Table_pembaruan_dokumen.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Dokumen", "No Dokumen", "Nama Dokumen", "Tgl Dokumen", "Tgl kadaluarsa", "Hari Jatuh Tempo"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class
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
        Table_pembaruan_dokumen.getTableHeader().setReorderingAllowed(false);
        jScrollPane8.setViewportView(Table_pembaruan_dokumen);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.setName("aah"); // NOI18N

        button_insert_pembaruan_dokumen.setBackground(new java.awt.Color(255, 255, 255));
        button_insert_pembaruan_dokumen.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_insert_pembaruan_dokumen.setText("insert");
        button_insert_pembaruan_dokumen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_insert_pembaruan_dokumenActionPerformed(evt);
            }
        });

        button_update_pembaruan_dokumen.setBackground(new java.awt.Color(255, 255, 255));
        button_update_pembaruan_dokumen.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_update_pembaruan_dokumen.setText("Edit");
        button_update_pembaruan_dokumen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_update_pembaruan_dokumenActionPerformed(evt);
            }
        });

        button_clear_pembaruan_dokumen.setBackground(new java.awt.Color(255, 255, 255));
        button_clear_pembaruan_dokumen.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_clear_pembaruan_dokumen.setText("Clear Text");
        button_clear_pembaruan_dokumen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_clear_pembaruan_dokumenActionPerformed(evt);
            }
        });

        button_delete_pembaruan_dokumen.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_pembaruan_dokumen.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete_pembaruan_dokumen.setText("Delete");
        button_delete_pembaruan_dokumen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_pembaruan_dokumenActionPerformed(evt);
            }
        });

        label_alamat_customer_baku.setBackground(new java.awt.Color(255, 255, 255));
        label_alamat_customer_baku.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_alamat_customer_baku.setText("Tanggal Dokumen :");

        label_nama_customer_baku1.setBackground(new java.awt.Color(255, 255, 255));
        label_nama_customer_baku1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_nama_customer_baku1.setText("Dokumen :");

        Date_Dokumen.setBackground(new java.awt.Color(255, 255, 255));
        Date_Dokumen.setDateFormatString("dd MMM yyyy");
        Date_Dokumen.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_nama_customer_baku1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ComboBox_kode_dokumen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_alamat_customer_baku)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date_Dokumen, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_insert_pembaruan_dokumen)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_update_pembaruan_dokumen)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_delete_pembaruan_dokumen)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_clear_pembaruan_dokumen)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_update_pembaruan_dokumen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_insert_pembaruan_dokumen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_delete_pembaruan_dokumen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_clear_pembaruan_dokumen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_nama_customer_baku1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_kode_dokumen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(label_alamat_customer_baku, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date_Dokumen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel12.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel12.setText("Total Data :");

        label_total_data.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_total_data.setText("TOTAL");

        txt_search_dokumen.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_dokumen.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_dokumenKeyPressed(evt);
            }
        });

        button_search_dokumen.setBackground(new java.awt.Color(255, 255, 255));
        button_search_dokumen.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_dokumen.setText("Search");
        button_search_dokumen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_dokumenActionPerformed(evt);
            }
        });

        button_export_pembaruan_dokumen.setBackground(new java.awt.Color(255, 255, 255));
        button_export_pembaruan_dokumen.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_pembaruan_dokumen.setText("Export To Excel");
        button_export_pembaruan_dokumen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_pembaruan_dokumenActionPerformed(evt);
            }
        });

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Search :");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Tanggal Dokumen :");

        DateFilter_tgl_dokumen1.setBackground(new java.awt.Color(255, 255, 255));
        DateFilter_tgl_dokumen1.setDateFormatString("dd MMM yyyy");
        DateFilter_tgl_dokumen1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        DateFilter_tgl_dokumen2.setBackground(new java.awt.Color(255, 255, 255));
        DateFilter_tgl_dokumen2.setDateFormatString("dd MMM yyyy");
        DateFilter_tgl_dokumen2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Tanggal Kadaluarsa :");

        DateFilter_tgl_kadaluarsa1.setBackground(new java.awt.Color(255, 255, 255));
        DateFilter_tgl_kadaluarsa1.setDateFormatString("dd MMM yyyy");
        DateFilter_tgl_kadaluarsa1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        DateFilter_tgl_kadaluarsa2.setBackground(new java.awt.Color(255, 255, 255));
        DateFilter_tgl_kadaluarsa2.setDateFormatString("dd MMM yyyy");
        DateFilter_tgl_kadaluarsa2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        javax.swing.GroupLayout jPanel_Pembaruan_Dokumen_QCLayout = new javax.swing.GroupLayout(jPanel_Pembaruan_Dokumen_QC);
        jPanel_Pembaruan_Dokumen_QC.setLayout(jPanel_Pembaruan_Dokumen_QCLayout);
        jPanel_Pembaruan_Dokumen_QCLayout.setHorizontalGroup(
            jPanel_Pembaruan_Dokumen_QCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Pembaruan_Dokumen_QCLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Pembaruan_Dokumen_QCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_Pembaruan_Dokumen_QCLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_dokumen, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DateFilter_tgl_dokumen1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DateFilter_tgl_dokumen2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DateFilter_tgl_kadaluarsa1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DateFilter_tgl_kadaluarsa2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search_dokumen)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export_pembaruan_dokumen)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data)
                        .addGap(0, 159, Short.MAX_VALUE))
                    .addComponent(jScrollPane8)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel_Pembaruan_Dokumen_QCLayout.setVerticalGroup(
            jPanel_Pembaruan_Dokumen_QCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Pembaruan_Dokumen_QCLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Pembaruan_Dokumen_QCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DateFilter_tgl_dokumen1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_dokumen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DateFilter_tgl_dokumen2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DateFilter_tgl_kadaluarsa1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DateFilter_tgl_kadaluarsa2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search_dokumen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_pembaruan_dokumen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(label_total_data))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 562, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Data Pembaruan Dokumen", jPanel_Pembaruan_Dokumen_QC);

        jPanel_master_dokumen.setBackground(new java.awt.Color(255, 255, 255));

        jPanel_operation_master_dokumen.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_operation_master_dokumen.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel_operation_master_dokumen.setName("aah"); // NOI18N

        button_insert_master_dokumen.setBackground(new java.awt.Color(255, 255, 255));
        button_insert_master_dokumen.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_insert_master_dokumen.setText("insert");
        button_insert_master_dokumen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_insert_master_dokumenActionPerformed(evt);
            }
        });

        button_update_master_dokumen.setBackground(new java.awt.Color(255, 255, 255));
        button_update_master_dokumen.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_update_master_dokumen.setText("Update");
        button_update_master_dokumen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_update_master_dokumenActionPerformed(evt);
            }
        });

        button_clear_master_dokumen.setBackground(new java.awt.Color(255, 255, 255));
        button_clear_master_dokumen.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_clear_master_dokumen.setText("Clear Text");
        button_clear_master_dokumen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_clear_master_dokumenActionPerformed(evt);
            }
        });

        button_delete_master_dokumen.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_master_dokumen.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete_master_dokumen.setText("Delete");
        button_delete_master_dokumen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_master_dokumenActionPerformed(evt);
            }
        });

        txt_nama_dokumen.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        txt_masa_berlaku.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        label_nama_customer_baku2.setBackground(new java.awt.Color(255, 255, 255));
        label_nama_customer_baku2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_nama_customer_baku2.setText("Kode Dokumen :");

        label_noTelp_customer_baku3.setBackground(new java.awt.Color(255, 255, 255));
        label_noTelp_customer_baku3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_noTelp_customer_baku3.setText("Masa Berlaku (Hari) :");

        txt_kode_dokumen.setEditable(false);
        txt_kode_dokumen.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        label_noTelp_customer_baku4.setBackground(new java.awt.Color(255, 255, 255));
        label_noTelp_customer_baku4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_noTelp_customer_baku4.setText("Nama Dokumen :");

        label_noTelp_customer_baku5.setBackground(new java.awt.Color(255, 255, 255));
        label_noTelp_customer_baku5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_noTelp_customer_baku5.setText("Keterangan :");

        txt_keterangan_dokumen.setColumns(20);
        txt_keterangan_dokumen.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_keterangan_dokumen.setLineWrap(true);
        txt_keterangan_dokumen.setRows(3);
        jScrollPane1.setViewportView(txt_keterangan_dokumen);

        label_noTelp_customer_baku6.setBackground(new java.awt.Color(255, 255, 255));
        label_noTelp_customer_baku6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_noTelp_customer_baku6.setText("Tempat Pengujian :");

        txt_tempat_pengujian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        label_noTelp_customer_baku7.setBackground(new java.awt.Color(255, 255, 255));
        label_noTelp_customer_baku7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_noTelp_customer_baku7.setText("Jenis Dokumen :");

        ComboBox_jenis_dokumen.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_jenis_dokumen.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Eksternal", "Internal" }));

        javax.swing.GroupLayout jPanel_operation_master_dokumenLayout = new javax.swing.GroupLayout(jPanel_operation_master_dokumen);
        jPanel_operation_master_dokumen.setLayout(jPanel_operation_master_dokumenLayout);
        jPanel_operation_master_dokumenLayout.setHorizontalGroup(
            jPanel_operation_master_dokumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_operation_master_dokumenLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_operation_master_dokumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_noTelp_customer_baku3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_noTelp_customer_baku5, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_noTelp_customer_baku7, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_noTelp_customer_baku6, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_noTelp_customer_baku4, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_nama_customer_baku2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_master_dokumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_masa_berlaku, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1)
                    .addComponent(ComboBox_jenis_dokumen, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txt_tempat_pengujian, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_nama_dokumen, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_kode_dokumen, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel_operation_master_dokumenLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(button_update_master_dokumen)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_insert_master_dokumen)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_delete_master_dokumen)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_clear_master_dokumen)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_operation_master_dokumenLayout.setVerticalGroup(
            jPanel_operation_master_dokumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_operation_master_dokumenLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_operation_master_dokumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_nama_customer_baku2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_kode_dokumen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_master_dokumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_noTelp_customer_baku4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_nama_dokumen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_master_dokumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_noTelp_customer_baku6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_tempat_pengujian, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_master_dokumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_noTelp_customer_baku7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_jenis_dokumen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_master_dokumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_noTelp_customer_baku5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_master_dokumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_noTelp_customer_baku3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_masa_berlaku, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_master_dokumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_update_master_dokumen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_insert_master_dokumen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete_master_dokumen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_clear_master_dokumen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        table_master_dokumen.setAutoCreateRowSorter(true);
        table_master_dokumen.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_master_dokumen.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Dokumen", "Nama Dokumen", "Tempat Pengujian", "Jenis Dokumen", "Keterangan", "Masa Berlaku (Hari)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class
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
        table_master_dokumen.getTableHeader().setReorderingAllowed(false);
        jScrollPane11.setViewportView(table_master_dokumen);

        button_search_master_dokumen.setBackground(new java.awt.Color(255, 255, 255));
        button_search_master_dokumen.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_master_dokumen.setText("Refresh");
        button_search_master_dokumen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_master_dokumenActionPerformed(evt);
            }
        });

        txt_search_master_dokumen.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_master_dokumen.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_master_dokumenKeyPressed(evt);
            }
        });

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel23.setText("Search :");

        javax.swing.GroupLayout jPanel_master_dokumenLayout = new javax.swing.GroupLayout(jPanel_master_dokumen);
        jPanel_master_dokumen.setLayout(jPanel_master_dokumenLayout);
        jPanel_master_dokumenLayout.setHorizontalGroup(
            jPanel_master_dokumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_master_dokumenLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_master_dokumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_master_dokumenLayout.createSequentialGroup()
                        .addComponent(jScrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 939, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel_operation_master_dokumen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_master_dokumenLayout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_master_dokumen, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search_master_dokumen)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel_master_dokumenLayout.setVerticalGroup(
            jPanel_master_dokumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_master_dokumenLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_master_dokumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_master_dokumen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search_master_dokumen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_master_dokumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 601, Short.MAX_VALUE)
                    .addGroup(jPanel_master_dokumenLayout.createSequentialGroup()
                        .addComponent(jPanel_operation_master_dokumen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Master Dokumen", jPanel_master_dokumen);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1366, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 680, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void button_insert_pembaruan_dokumenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_insert_pembaruan_dokumenActionPerformed
        // TODO add your handling code here:
        try {
            if (Date_Dokumen.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Tanggal Dokumen tidak boleh kosong");
            } else {
                String no_dokumen = ComboBox_kode_dokumen.getSelectedItem().toString().split("-")[0] + "-" + new SimpleDateFormat("dd/MM/yyyy").format(Date_Dokumen.getDate());
                sql = "INSERT INTO `tb_dokumen_qc_update`(`no_dokumen`, `kode_dokumen`, `tanggal_dokumen`, `tanggal_kadaluarsa`) "
                        + "VALUES ('" + no_dokumen + "','" + ComboBox_kode_dokumen.getSelectedItem().toString().split("-")[0] + "','" + dateFormat.format(Date_Dokumen.getDate()) + "', DATE_ADD('" + dateFormat.format(Date_Dokumen.getDate()) + "', INTERVAL (SELECT `masa_berlaku` FROM `tb_dokumen_qc` WHERE `kode_dokumen`='" + ComboBox_kode_dokumen.getSelectedItem().toString().split("-")[0] + "') DAY))";
                Utility.db.getConnection().createStatement();
                if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                    JOptionPane.showMessageDialog(this, "data Saved !");
                    refreshTable_data_pembaruan_dokumen();
                    button_clear_pembaruan_dokumen.doClick();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed!");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Dokumen_QC.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_insert_pembaruan_dokumenActionPerformed

    private void button_update_pembaruan_dokumenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_update_pembaruan_dokumenActionPerformed
        // TODO add your handling code here:
        int j = Table_pembaruan_dokumen.getSelectedRow();
        try {
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan klik data pada tabel !");
            } else {
                if (Date_Dokumen.getDate() == null) {
                    JOptionPane.showMessageDialog(this, "Tanggal Dokumen tidak boleh kosong");
                } else {
                    String no_dokumen = ComboBox_kode_dokumen.getSelectedItem().toString().split("-")[0] + "-" + new SimpleDateFormat("dd/MM/yyyy").format(Date_Dokumen.getDate());
                    sql = "UPDATE `tb_dokumen_qc_update` SET "
                            + "`no_dokumen` = '" + no_dokumen + "', "
                            + "`kode_dokumen` = '" + ComboBox_kode_dokumen.getSelectedItem().toString().split("-")[0] + "', "
                            + "`tanggal_dokumen` = '" + dateFormat.format(Date_Dokumen.getDate()) + "', "
                            + "`tanggal_kadaluarsa` = DATE_ADD('" + dateFormat.format(Date_Dokumen.getDate()) + "', INTERVAL (SELECT `masa_berlaku` FROM `tb_dokumen_qc` WHERE `kode_dokumen`='" + ComboBox_kode_dokumen.getSelectedItem().toString().split("-")[0] + "') DAY) "
                            + "WHERE `no_dokumen` = '" + Table_pembaruan_dokumen.getValueAt(j, 1).toString() + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                        JOptionPane.showMessageDialog(this, "data Saved !");
                        refreshTable_data_pembaruan_dokumen();
                        button_clear_pembaruan_dokumen.doClick();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed!");
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Dokumen_QC.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_update_pembaruan_dokumenActionPerformed

    private void button_clear_pembaruan_dokumenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_clear_pembaruan_dokumenActionPerformed
        // TODO add your handling code here:
        ComboBox_kode_dokumen.setSelectedIndex(0);
        Date_Dokumen.setDate(date);
    }//GEN-LAST:event_button_clear_pembaruan_dokumenActionPerformed

    private void button_delete_pembaruan_dokumenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_pembaruan_dokumenActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_pembaruan_dokumen.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan klik data pada tabel !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin akan hapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    sql = "DELETE FROM `tb_dokumen_qc_update` WHERE `no_dokumen` = '" + Table_pembaruan_dokumen.getValueAt(j, 1).toString() + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                        JOptionPane.showMessageDialog(this, "data Deleted !");
                        refreshTable_data_pembaruan_dokumen();
                        button_clear_pembaruan_dokumen.doClick();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed!");
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
        }
    }//GEN-LAST:event_button_delete_pembaruan_dokumenActionPerformed

    private void txt_search_dokumenKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_dokumenKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_data_pembaruan_dokumen();
        }
    }//GEN-LAST:event_txt_search_dokumenKeyPressed

    private void button_search_dokumenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_dokumenActionPerformed
        // TODO add your handling code here:
        refreshTable_data_pembaruan_dokumen();
    }//GEN-LAST:event_button_search_dokumenActionPerformed

    private void button_export_pembaruan_dokumenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_pembaruan_dokumenActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_pembaruan_dokumen.getModel();
        ExportToExcel.writeToExcel(model, jPanel_Pembaruan_Dokumen_QC);
    }//GEN-LAST:event_button_export_pembaruan_dokumenActionPerformed

    private void button_search_master_dokumenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_master_dokumenActionPerformed
        // TODO add your handling code here:
        refreshTable_data_master_dokumen();
    }//GEN-LAST:event_button_search_master_dokumenActionPerformed

    private void txt_search_master_dokumenKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_master_dokumenKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_data_master_dokumen();
        }
    }//GEN-LAST:event_txt_search_master_dokumenKeyPressed

    private void button_insert_master_dokumenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_insert_master_dokumenActionPerformed
        // TODO add your handling code here:
        try {
            if (txt_nama_dokumen.getText() == null || txt_nama_dokumen.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "No Dokumen tidak boleh kosong");
            } else {
                sql = "INSERT INTO `tb_dokumen_qc`(`nama_dokumen`, `tempat_pengujian`, `jenis_dokumen`, `keterangan`, `masa_berlaku`) "
                        + "VALUES ('" + txt_nama_dokumen.getText() + "','" + txt_tempat_pengujian.getText() + "','" + ComboBox_jenis_dokumen.getSelectedItem().toString() + "','" + txt_keterangan_dokumen.getText() + "','" + txt_masa_berlaku.getText() + "')";
                Utility.db.getConnection().createStatement();
                if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                    JOptionPane.showMessageDialog(this, "data Saved !");
                    refreshTable_data_master_dokumen();
                    refreshComboBox();
                    button_clear_master_dokumen.doClick();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed!");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Dokumen_QC.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_insert_master_dokumenActionPerformed

    private void button_update_master_dokumenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_update_master_dokumenActionPerformed
        // TODO add your handling code here:
        int j = table_master_dokumen.getSelectedRow();
        try {
            int masa_berlaku = Integer.valueOf(txt_masa_berlaku.getText());
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan klik data pada tabel !");
            } else {
                if (txt_nama_dokumen.getText() == null || txt_nama_dokumen.getText().equals("")) {
                    JOptionPane.showMessageDialog(this, "No Dokumen tidak boleh kosong");
                } else {
                    sql = "UPDATE `tb_dokumen_qc` SET "
                            + "`nama_dokumen` = '" + txt_nama_dokumen.getText() + "', "
                            + "`tempat_pengujian` = '" + txt_tempat_pengujian.getText() + "', "
                            + "`jenis_dokumen` = '" + ComboBox_jenis_dokumen.getSelectedItem().toString() + "', "
                            + "`keterangan` = '" + txt_keterangan_dokumen.getText() + "', "
                            + "`masa_berlaku` = '" + masa_berlaku + "' "
                            + "WHERE `kode_dokumen` = '" + table_master_dokumen.getValueAt(j, 0).toString() + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                        JOptionPane.showMessageDialog(this, "data Saved !");
                        refreshTable_data_master_dokumen();
                        refreshComboBox();
                        button_clear_master_dokumen.doClick();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed!");
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Dokumen_QC.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_update_master_dokumenActionPerformed

    private void button_clear_master_dokumenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_clear_master_dokumenActionPerformed
        // TODO add your handling code here:
        txt_kode_dokumen.setText("");
        txt_nama_dokumen.setText("");
        txt_tempat_pengujian.setText("");
        txt_keterangan_dokumen.setText("");
        txt_masa_berlaku.setText("");
        ComboBox_jenis_dokumen.setSelectedIndex(0);
    }//GEN-LAST:event_button_clear_master_dokumenActionPerformed

    private void button_delete_master_dokumenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_master_dokumenActionPerformed
        // TODO add your handling code here:
        try {
            int j = table_master_dokumen.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan klik data pada tabel !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin akan hapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    sql = "DELETE FROM `tb_dokumen_qc` WHERE `kode_dokumen` = '" + table_master_dokumen.getValueAt(j, 0).toString() + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                        JOptionPane.showMessageDialog(this, "data Deleted !");
                        refreshTable_data_master_dokumen();
                        refreshComboBox();
                        button_clear_master_dokumen.doClick();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed!");
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
        }
    }//GEN-LAST:event_button_delete_master_dokumenActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_jenis_dokumen;
    private javax.swing.JComboBox<String> ComboBox_kode_dokumen;
    private com.toedter.calendar.JDateChooser DateFilter_tgl_dokumen1;
    private com.toedter.calendar.JDateChooser DateFilter_tgl_dokumen2;
    private com.toedter.calendar.JDateChooser DateFilter_tgl_kadaluarsa1;
    private com.toedter.calendar.JDateChooser DateFilter_tgl_kadaluarsa2;
    private com.toedter.calendar.JDateChooser Date_Dokumen;
    private javax.swing.JTable Table_pembaruan_dokumen;
    private javax.swing.JButton button_clear_master_dokumen;
    private javax.swing.JButton button_clear_pembaruan_dokumen;
    public javax.swing.JButton button_delete_master_dokumen;
    public javax.swing.JButton button_delete_pembaruan_dokumen;
    private javax.swing.JButton button_export_pembaruan_dokumen;
    public javax.swing.JButton button_insert_master_dokumen;
    public javax.swing.JButton button_insert_pembaruan_dokumen;
    private javax.swing.JButton button_search_dokumen;
    private javax.swing.JButton button_search_master_dokumen;
    public javax.swing.JButton button_update_master_dokumen;
    public javax.swing.JButton button_update_pembaruan_dokumen;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel_Pembaruan_Dokumen_QC;
    private javax.swing.JPanel jPanel_master_dokumen;
    private javax.swing.JPanel jPanel_operation_master_dokumen;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label_alamat_customer_baku;
    private javax.swing.JLabel label_nama_customer_baku1;
    private javax.swing.JLabel label_nama_customer_baku2;
    private javax.swing.JLabel label_noTelp_customer_baku3;
    private javax.swing.JLabel label_noTelp_customer_baku4;
    private javax.swing.JLabel label_noTelp_customer_baku5;
    private javax.swing.JLabel label_noTelp_customer_baku6;
    private javax.swing.JLabel label_noTelp_customer_baku7;
    private javax.swing.JLabel label_total_data;
    private javax.swing.JTable table_master_dokumen;
    private javax.swing.JTextArea txt_keterangan_dokumen;
    private javax.swing.JTextField txt_kode_dokumen;
    private javax.swing.JTextField txt_masa_berlaku;
    private javax.swing.JTextField txt_nama_dokumen;
    private javax.swing.JTextField txt_search_dokumen;
    private javax.swing.JTextField txt_search_master_dokumen;
    private javax.swing.JTextField txt_tempat_pengujian;
    // End of variables declaration//GEN-END:variables
}
