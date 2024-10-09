package waleta_system.RND;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
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
import waleta_system.Browse_Karyawan;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;
import waleta_system.Class.Utility;
import waleta_system.MainForm;

public class JPanel_ProduksiATB_PenilaianLP extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DecimalFormat decimalFormat = new DecimalFormat();
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_ProduksiATB_PenilaianLP() {
        initComponents();
    }

    public void init() {
        try {
            decimalFormat = Utility.DecimalFormatUS(decimalFormat);
            ComboBox_jenis_bulu_hasil.removeAllItems();
            sql = "SELECT `bulu_upah` FROM `tb_tarif_cabut` WHERE 1";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_jenis_bulu_hasil.addItem(rs.getString("bulu_upah"));
            }
            refreshTable_penilaian_lp();
            table_data_lp.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && table_data_lp.getSelectedRow() != -1) {
                        int i = table_data_lp.getSelectedRow();
                        refreshTable_penilaian_lp_atb(table_data_lp.getValueAt(i, 0).toString());
                        txt_no_lp_input.setText(table_data_lp.getValueAt(i, 0).toString());
                        ComboBox_jenis_bulu_hasil.setSelectedItem(table_data_lp.getValueAt(i, 2).toString());
                        if (table_data_lp.getValueAt(i, 7) != null) {
                            txt_penilai_id.setText(table_data_lp.getValueAt(i, 7).toString());
                            txt_penilai_nama.setText(table_data_lp.getValueAt(i, 8).toString());
                        } else {
                            txt_penilai_id.setText("");
                            txt_penilai_nama.setText("");
                        }
                    }
                }
            });
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_ProduksiATB_PenilaianLP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_penilaian_lp() {
        try {
            DefaultTableModel model = (DefaultTableModel) table_data_lp.getModel();
            model.setRowCount(0);
            float total_kpg = 0, total_gram = 0;

            String filter_tanggal = "";
            if (Datefilter1.getDate() != null && Datefilter2.getDate() != null) {
                filter_tanggal = "AND (`tanggal` BETWEEN '" + dateFormat.format(Datefilter1.getDate()) + "' AND '" + dateFormat.format(Datefilter2.getDate()) + "') \n";
            }
            String filter_grade = "";
            if (txt_search_grade.getText() != null && !txt_search_grade.getText().equals("")) {
                filter_grade = "AND `tb_laporan_produksi`.`kode_grade` LIKE '%" + txt_search_grade.getText() + "%' \n";
            }

            sql = "SELECT `tb_atb_produksi`.`no_laporan_produksi`, `tb_laporan_produksi`.`kode_grade`, `tb_laporan_produksi`.`jumlah_keping`, `tb_laporan_produksi`.`berat_basah`,\n"
                    + "`jenis_bulu_lp`, `jenis_bulu_asal`, `jenis_bulu_hasil`, `id_penilai`, `tb_karyawan`.`nama_pegawai`, `tanggal` AS 'tgl_evaluasi', `disetujui`, `waktu_disetujui`\n"
                    + "FROM `tb_atb_produksi` \n"
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_atb_produksi`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_laporan_produksi_penilaian_bulu` ON `tb_atb_produksi`.`no_laporan_produksi` = `tb_laporan_produksi_penilaian_bulu`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_laporan_produksi_penilaian_bulu`.`id_penilai` = `tb_karyawan`.`id_pegawai`\n"
                    + "WHERE \n"
                    + "`tb_atb_produksi`.`no_laporan_produksi` LIKE '%" + txt_search_no_lp.getText() + "%' \n"
                    + filter_tanggal
                    + filter_grade
                    + "GROUP BY `tb_atb_produksi`.`no_laporan_produksi` \n"
                    + "ORDER BY `tanggal` IS NULL DESC, `tanggal` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[15];
            while (rs.next()) {
                row[0] = rs.getString("no_laporan_produksi");
                row[1] = rs.getString("kode_grade");
                if (rs.getString("jenis_bulu_asal") == null) {
                    row[2] = rs.getString("jenis_bulu_lp");
                } else {
                    row[2] = rs.getString("jenis_bulu_asal");
                }
                row[3] = rs.getInt("jumlah_keping");
                row[4] = rs.getInt("berat_basah");
                row[5] = rs.getDate("tgl_evaluasi");
                row[6] = rs.getString("jenis_bulu_hasil");
                row[7] = rs.getString("id_penilai");
                row[8] = rs.getString("nama_pegawai");
                row[9] = rs.getString("disetujui");
                row[10] = rs.getTimestamp("waktu_disetujui");
                model.addRow(row);
                total_kpg += rs.getFloat("jumlah_keping");
                total_gram += rs.getFloat("berat_basah");
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_data_lp);
            table_data_lp.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                    if (table_data_lp.getValueAt(row, 6) != null && !table_data_lp.getValueAt(row, 2).toString().equals(table_data_lp.getValueAt(row, 6).toString())) {
                        if (isSelected) {
                            comp.setBackground(Color.gray);
                            comp.setForeground(Color.red);
                        } else {
                            comp.setBackground(Color.PINK);
                            comp.setForeground(Color.red);
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(table_data_lp.getSelectionBackground());
                            comp.setForeground(table_data_lp.getSelectionForeground());
                        } else {
                            comp.setBackground(table_data_lp.getBackground());
                            comp.setForeground(table_data_lp.getForeground());
                        }
                    }
                    return comp;
                }
            });
            table_data_lp.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                    if (table_data_lp.getValueAt(row, 6) != null && !table_data_lp.getValueAt(row, 2).toString().equals(table_data_lp.getValueAt(row, 6).toString())) {
                        if (isSelected) {
                            comp.setBackground(Color.gray);
                            comp.setForeground(Color.red);
                        } else {
                            comp.setBackground(Color.PINK);
                            comp.setForeground(Color.red);
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(table_data_lp.getSelectionBackground());
                            comp.setForeground(table_data_lp.getSelectionForeground());
                        } else {
                            comp.setBackground(table_data_lp.getBackground());
                            comp.setForeground(table_data_lp.getForeground());
                        }
                    }
                    return comp;
                }
            });
            table_data_lp.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                    if (table_data_lp.getValueAt(row, 6) != null && !table_data_lp.getValueAt(row, 2).toString().equals(table_data_lp.getValueAt(row, 6).toString())) {
                        if (isSelected) {
                            comp.setBackground(Color.gray);
                            comp.setForeground(Color.red);
                        } else {
                            comp.setBackground(Color.PINK);
                            comp.setForeground(Color.red);
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(table_data_lp.getSelectionBackground());
                            comp.setForeground(table_data_lp.getSelectionForeground());
                        } else {
                            comp.setBackground(table_data_lp.getBackground());
                            comp.setForeground(table_data_lp.getForeground());
                        }
                    }
                    return comp;
                }
            });
            table_data_lp.repaint();
            decimalFormat.setMaximumFractionDigits(1);
            int total_data = table_data_lp.getRowCount();
            label_total_data.setText(decimalFormat.format(total_data));
            label_total_kpg.setText(decimalFormat.format(total_kpg));
            label_total_gram.setText(decimalFormat.format(total_gram));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JPanel_ProduksiATB_PenilaianLP.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void refreshTable_penilaian_lp_atb(String no_lp) {
        try {
            DefaultTableModel model = (DefaultTableModel) table_data_produksi_atb.getModel();
            model.setRowCount(0);
            float total_kpg = 0, total_gram = 0;

            sql = "SELECT `no`, `tb_atb_produksi`.`no_laporan_produksi`, `tb_laporan_produksi`.`kode_grade`, `id_pegawai_atb`, `keping`, `gram`, `waktu_mulai`, `waktu_selesai`, `nama_pegawai`, `layer_selesai`, "
                    + "`jenis_bulu_lp`, `tarif_sub`"
                    + "FROM `tb_atb_produksi` \n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_atb_produksi`.`operator` = `tb_karyawan`.`id_pegawai`\n"
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_atb_produksi`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah`\n"
                    + "WHERE `tb_atb_produksi`.`no_laporan_produksi` = '" + no_lp + "' ";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[20];
            while (rs.next()) {
                row[0] = rs.getString("id_pegawai_atb");
                row[1] = rs.getFloat("keping");
                row[2] = rs.getFloat("gram");
                row[3] = rs.getDate("waktu_mulai");
                row[4] = rs.getTime("waktu_mulai");
                row[5] = rs.getDate("waktu_selesai");
                row[6] = rs.getTime("waktu_selesai");
                row[7] = rs.getString("nama_pegawai");
                row[8] = rs.getString("layer_selesai");
                model.addRow(row);
                total_kpg += rs.getFloat("keping");
                total_gram += rs.getFloat("gram");
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_data_produksi_atb);
            decimalFormat.setMaximumFractionDigits(1);
            int total_data = table_data_produksi_atb.getRowCount();
            label_total_data_produksi.setText(decimalFormat.format(total_data));
            label_total_kpg_produksi.setText(decimalFormat.format(total_kpg));
            label_total_gram_produksi.setText(decimalFormat.format(total_gram));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JPanel_ProduksiATB_PenilaianLP.class.getName()).log(Level.SEVERE, null, e);
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
        txt_search_no_lp = new javax.swing.JTextField();
        button_search = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        Datefilter1 = new com.toedter.calendar.JDateChooser();
        Datefilter2 = new com.toedter.calendar.JDateChooser();
        jLabel6 = new javax.swing.JLabel();
        label_total_data = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_data_lp = new javax.swing.JTable();
        button_export = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        label_total_gram = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        label_total_kpg = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        button_simpan = new javax.swing.JButton();
        ComboBox_jenis_bulu_hasil = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        txt_no_lp_input = new javax.swing.JTextField();
        txt_penilai_nama = new javax.swing.JTextField();
        button_pick_pekerja = new javax.swing.JButton();
        txt_penilai_id = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txt_search_grade = new javax.swing.JTextField();
        button_setujui = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        label_total_data_produksi = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        Button_export_data_produksi_atb = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        label_total_kpg_produksi = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        table_data_produksi_atb = new javax.swing.JTable();
        jLabel19 = new javax.swing.JLabel();
        label_total_gram_produksi = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Penilaian LP ATB", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        txt_search_no_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_no_lp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_no_lpKeyPressed(evt);
            }
        });

        button_search.setBackground(new java.awt.Color(255, 255, 255));
        button_search.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_search.setText("Search");
        button_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_searchActionPerformed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("No LP :");

        Datefilter1.setBackground(new java.awt.Color(255, 255, 255));
        Datefilter1.setDateFormatString("dd MMM yyyy");
        Datefilter1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Datefilter2.setBackground(new java.awt.Color(255, 255, 255));
        Datefilter2.setDateFormatString("dd MMM yyyy");
        Datefilter2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Total Data :");

        label_total_data.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_data.setText("0");

        table_data_lp.setAutoCreateRowSorter(true);
        table_data_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_lp.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP", "Grade", "Jenis Bulu LP", "Kpg LP", "Gram LP", "Tgl Evaluasi", "Jenis Bulu Hasil", "ID Penilai", "Nama Penilai", "Disetujui", "Waktu Disetujui"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
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
        jScrollPane1.setViewportView(table_data_lp);

        button_export.setBackground(new java.awt.Color(255, 255, 255));
        button_export.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_export.setText("Export to Excel");
        button_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportActionPerformed(evt);
            }
        });

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel12.setText("Total Gram LP :");

        label_total_gram.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_gram.setText("0");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel15.setText("Total Kpg LP :");

        label_total_kpg.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_kpg.setText("0");

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel16.setText("Tgl Evaluasi :");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Jenis Bulu Hasil :");

        button_simpan.setBackground(new java.awt.Color(255, 255, 255));
        button_simpan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_simpan.setText("Simpan");
        button_simpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_simpanActionPerformed(evt);
            }
        });

        ComboBox_jenis_bulu_hasil.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("No LP :");

        txt_no_lp_input.setEditable(false);
        txt_no_lp_input.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        txt_penilai_nama.setEditable(false);
        txt_penilai_nama.setBackground(new java.awt.Color(255, 255, 255));
        txt_penilai_nama.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_pick_pekerja.setBackground(new java.awt.Color(255, 255, 255));
        button_pick_pekerja.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_pick_pekerja.setText("...");
        button_pick_pekerja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_pick_pekerjaActionPerformed(evt);
            }
        });

        txt_penilai_id.setEditable(false);
        txt_penilai_id.setBackground(new java.awt.Color(255, 255, 255));
        txt_penilai_id.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel32.setBackground(new java.awt.Color(255, 255, 255));
        jLabel32.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel32.setText("Penilai :");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ComboBox_jenis_bulu_hasil, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txt_no_lp_input)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txt_penilai_id, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_pick_pekerja, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txt_penilai_nama)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(button_simpan)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_no_lp_input, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_jenis_bulu_hasil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txt_penilai_id, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_pick_pekerja, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_penilai_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(button_simpan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("Grade :");

        txt_search_grade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_grade.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_gradeKeyPressed(evt);
            }
        });

        button_setujui.setBackground(new java.awt.Color(255, 255, 255));
        button_setujui.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_setujui.setText("Disetujui");
        button_setujui.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_setujuiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_kpg)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_setujui)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_no_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Datefilter1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Datefilter2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_search_no_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Datefilter1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Datefilter2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_total_data, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_kpg, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_setujui, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 457, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Data Produksi ATB", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12))); // NOI18N

        label_total_data_produksi.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_produksi.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_data_produksi.setText("0");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("Total Data :");

        Button_export_data_produksi_atb.setText("Export");
        Button_export_data_produksi_atb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_export_data_produksi_atbActionPerformed(evt);
            }
        });

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel18.setText("Total Kpg :");

        label_total_kpg_produksi.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg_produksi.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_kpg_produksi.setText("0");

        table_data_produksi_atb.setAutoCreateRowSorter(true);
        table_data_produksi_atb.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_produksi_atb.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Pegawai ATB", "Kpg", "Gram", "Tgl Mulai", "Waktu Mulai", "Tgl Selesai", "Waktu Selesai", "Operator", "Layer Selesai"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(table_data_produksi_atb);

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel19.setText("Total Gram :");

        label_total_gram_produksi.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_produksi.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_gram_produksi.setText("0");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data_produksi)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_kpg_produksi)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_produksi)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Button_export_data_produksi_atb))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 562, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_gram_produksi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_kpg_produksi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_data_produksi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(Button_export_data_produksi_atb, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txt_search_no_lpKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_no_lpKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_penilaian_lp();
        }
    }//GEN-LAST:event_txt_search_no_lpKeyPressed

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        refreshTable_penilaian_lp();
    }//GEN-LAST:event_button_searchActionPerformed

    private void button_simpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_simpanActionPerformed
        try {
            boolean check = true;
            int selectedRow = table_data_lp.getSelectedRow();
            if (selectedRow > -1) {
                sql = "SELECT `no_laporan_produksi`, `keping_upah`, `berat_basah` FROM `tb_laporan_produksi` WHERE `no_laporan_produksi` = '" + txt_no_lp_input.getText() + "'";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(this, "No LP " + txt_no_lp_input.getText() + " tidak ditemukan!");
                    check = false;
                } else if (table_data_lp.getValueAt(selectedRow, 9) != null) {
                    JOptionPane.showMessageDialog(this, "Jenis bulu LP sudah disetujui");
                    check = false;
                } else if (txt_penilai_id.getText() == null || txt_penilai_id.getText().equals("")) {
                    JOptionPane.showMessageDialog(this, "Penilai belum dipilih!");
                    check = false;
                }
            } else {
                JOptionPane.showMessageDialog(this, "Silahkan pilih salah satu no LP pada tabel!");
                check = false;
            }

            if (check) {
                try {
                    String Query = "INSERT INTO `tb_laporan_produksi_penilaian_bulu`(`no_laporan_produksi`, `jenis_bulu_asal`, `jenis_bulu_hasil`, `id_penilai`, `tanggal`) "
                            + "VALUES ("
                            + "'" + txt_no_lp_input.getText() + "',"
                            + "(SELECT `jenis_bulu_lp` FROM `tb_laporan_produksi` WHERE `tb_laporan_produksi`.`no_laporan_produksi` = '" + txt_no_lp_input.getText() + "'),"
                            + "'" + ComboBox_jenis_bulu_hasil.getSelectedItem().toString() + "',"
                            + "'" + txt_penilai_id.getText() + "',"
                            + "CURRENT_DATE()"
                            + ")";
                    Utility.db.getStatement().executeUpdate(Query);
                    JOptionPane.showMessageDialog(this, "data SAVED!");
                    refreshTable_penilaian_lp();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, e.getMessage());
                    Logger.getLogger(JPanel_ProduksiATB_PenilaianLP.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_ProduksiATB_PenilaianLP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_simpanActionPerformed

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_data_lp.getModel();
        ExportToExcel.writeToExcel(model, jPanel2);
    }//GEN-LAST:event_button_exportActionPerformed

    private void txt_search_gradeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_gradeKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_penilaian_lp();
        }
    }//GEN-LAST:event_txt_search_gradeKeyPressed

    private void Button_export_data_produksi_atbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_export_data_produksi_atbActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_data_produksi_atb.getModel();
        ExportToExcel.writeToExcel(model, jPanel2);
    }//GEN-LAST:event_Button_export_data_produksi_atbActionPerformed

    private void button_pick_pekerjaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_pick_pekerjaActionPerformed
        // TODO add your handling code here:
        String filter_tgl = "";
        Browse_Karyawan dialog = new Browse_Karyawan(new javax.swing.JFrame(), true, filter_tgl);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);

        int x = Browse_Karyawan.table_list_karyawan.getSelectedRow();
        if (x != -1) {
            txt_penilai_id.setText(Browse_Karyawan.table_list_karyawan.getValueAt(x, 0).toString());
            txt_penilai_nama.setText(Browse_Karyawan.table_list_karyawan.getValueAt(x, 1).toString());
        }
    }//GEN-LAST:event_button_pick_pekerjaActionPerformed

    private void button_setujuiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_setujuiActionPerformed
        // TODO add your handling code here:
        try {
            boolean check = true;
            int selectedRow = table_data_lp.getSelectedRow();
            String no_lp = null;
            if (selectedRow > -1) {
                no_lp = table_data_lp.getValueAt(selectedRow, 0).toString();
                sql = "SELECT `no_laporan_produksi`, `keping_upah`, `berat_basah` FROM `tb_laporan_produksi` WHERE `no_laporan_produksi` = '" + no_lp + "'";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(this, "No LP " + no_lp + " tidak ditemukan!");
                    check = false;
                } else if (table_data_lp.getValueAt(selectedRow, 8) == null) {
                    JOptionPane.showMessageDialog(this, "Jenis bulu LP belum dinilai!");
                    check = false;
                } else if (table_data_lp.getValueAt(selectedRow, 9) != null) {
                    JOptionPane.showMessageDialog(this, "Jenis bulu LP sudah disetujui");
                    check = false;
                } else if (MainForm.Login_kodeBagian != 244 && !MainForm.Login_namaBagian.contains("KADEP") && !(MainForm.Login_Posisi.equals("STAFF 5") && MainForm.Login_Departemen.equals("R&D"))) {
                    JOptionPane.showMessageDialog(this, "hanya OM/KADEP & STAFF 5 RND yang bisa menyetujui penilaian bulu upah");
                    check = false;
                }
            } else {
                JOptionPane.showMessageDialog(this, "Silahkan pilih salah satu no LP pada tabel!");
                check = false;
            }

            if (check) {
                try {
                    String jenis_bulu_asal = table_data_lp.getValueAt(selectedRow, 2).toString();
                    String jenis_bulu_hasil = table_data_lp.getValueAt(selectedRow, 6).toString();
                    String jenis_bulu_maklun = jenis_bulu_hasil;
                    switch (jenis_bulu_hasil.charAt(1)) {
                        case 'R':
                            jenis_bulu_maklun = "Ringan";
                            break;
                        case 'S':
                            jenis_bulu_maklun = "Sedang";
                            break;
                        case 'B':
                            jenis_bulu_maklun = "Berat";
                            break;
                        default:
                            break;
                    }
                    Utility.db_sub.connect();
                    Utility.db_maklun.connect();
                    Utility.db.getConnection().setAutoCommit(false);
                    Utility.db_sub.getConnection().setAutoCommit(false);
                    Utility.db_maklun.getConnection().setAutoCommit(false);
                    String Query = "UPDATE `tb_laporan_produksi_penilaian_bulu` SET "
                            + "`disetujui`='" + MainForm.Login_NamaPegawai + "',"
                            + "`waktu_disetujui`=NOW() "
                            + "WHERE `no_laporan_produksi`='" + no_lp + "'";
                    Utility.db.getStatement().executeUpdate(Query);
                    if (!jenis_bulu_asal.equals(jenis_bulu_hasil)) {
                        int tarif_sub = 0;
                        sql = "SELECT `tarif_sub` FROM `tb_tarif_cabut` WHERE `bulu_upah` = '" + jenis_bulu_hasil + "'";
                        rs = Utility.db.getStatement().executeQuery(sql);
                        if (rs.next()) {
                            tarif_sub = rs.getInt("tarif_sub");
                        }
                        String Query1 = "UPDATE `tb_laporan_produksi` SET `jenis_bulu_lp`='" + jenis_bulu_hasil + "' \n"
                                + "WHERE `no_laporan_produksi` = '" + no_lp + "'";
                        Utility.db.getStatement().executeUpdate(Query1);
                        String Query2 = "UPDATE `tb_laporan_produksi` SET `jenis_bulu_lp`='" + jenis_bulu_hasil + "', `upah_per_gram` = " + tarif_sub + " \n"
                                + "WHERE `no_laporan_produksi` = '" + no_lp + "'";
                        Utility.db_sub.getStatement().executeUpdate(Query2);
                        String Query3 = "UPDATE `tb_lp` SET `jenis_bulu`='" + jenis_bulu_maklun + "', `upah_basah` = " + tarif_sub + " \n"
                                + "WHERE `no_lp` = '" + no_lp + "'";
                        Utility.db_maklun.getStatement().executeUpdate(Query3);
                    }

                    Utility.db.getConnection().commit();
                    Utility.db_sub.getConnection().commit();
                    Utility.db_maklun.getConnection().commit();
                    JOptionPane.showMessageDialog(this, "data SAVED!");
                    refreshTable_penilaian_lp();
                } catch (Exception e) {
                    Utility.db.getConnection().rollback();
                    Utility.db_sub.getConnection().rollback();
                    Utility.db_maklun.getConnection().rollback();
                    JOptionPane.showMessageDialog(this, e.getMessage());
                    Logger.getLogger(JPanel_ProduksiATB_PenilaianLP.class.getName()).log(Level.SEVERE, null, e);
                } finally {
                    Utility.db.getConnection().setAutoCommit(true);
                    Utility.db_sub.getConnection().setAutoCommit(true);
                    Utility.db_maklun.getConnection().setAutoCommit(true);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_ProduksiATB_PenilaianLP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_setujuiActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Button_export_data_produksi_atb;
    private javax.swing.JComboBox<String> ComboBox_jenis_bulu_hasil;
    private com.toedter.calendar.JDateChooser Datefilter1;
    private com.toedter.calendar.JDateChooser Datefilter2;
    private javax.swing.JButton button_export;
    private javax.swing.JButton button_pick_pekerja;
    public static javax.swing.JButton button_search;
    private javax.swing.JButton button_setujui;
    public javax.swing.JButton button_simpan;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel label_total_data;
    private javax.swing.JLabel label_total_data_produksi;
    private javax.swing.JLabel label_total_gram;
    private javax.swing.JLabel label_total_gram_produksi;
    private javax.swing.JLabel label_total_kpg;
    private javax.swing.JLabel label_total_kpg_produksi;
    private javax.swing.JTable table_data_lp;
    private javax.swing.JTable table_data_produksi_atb;
    private javax.swing.JTextField txt_no_lp_input;
    private javax.swing.JTextField txt_penilai_id;
    private javax.swing.JTextField txt_penilai_nama;
    private javax.swing.JTextField txt_search_grade;
    private javax.swing.JTextField txt_search_no_lp;
    // End of variables declaration//GEN-END:variables

}
