package waleta_system.Finance;

import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
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

public class JPanel_LevelGaji extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_LevelGaji() {
        initComponents();
    }

    public void init() {
        refreshTable();
        table_level_gaji.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && table_level_gaji.getSelectedRow() != -1) {
                    int i = table_level_gaji.getSelectedRow();
                    int upah = Math.round(Float.valueOf(table_level_gaji.getValueAt(i, 1).toString()));
                    float lembur_per_jam = Float.valueOf(table_level_gaji.getValueAt(i, 2).toString());
                    float lembur_x_hari_kerja = Float.valueOf(table_level_gaji.getValueAt(i, 3).toString());
                    float lembur_x_hari_libur = Float.valueOf(table_level_gaji.getValueAt(i, 4).toString());
                    int premi_hadir = Math.round(Float.valueOf(table_level_gaji.getValueAt(i, 5).toString()));
                    int denda_terlambat = Math.round(Float.valueOf(table_level_gaji.getValueAt(i, 6).toString()));
                    txt_level_gaji.setText(table_level_gaji.getValueAt(i, 0).toString());
                    txt_upah.setText(Integer.toString(upah));
                    txt_lembur_per_jam.setText(Float.toString(lembur_per_jam));
                    txt_lembur_x_hari_kerja.setText(Float.toString(lembur_x_hari_kerja));
                    txt_lembur_x_hari_libur.setText(Float.toString(lembur_x_hari_libur));
                    txt_premi_hadir.setText(Integer.toString(premi_hadir));
                    txt_denda_terlambat.setText(Integer.toString(denda_terlambat));
                    txt_keterangan.setText(table_level_gaji.getValueAt(i, 7).toString());
                    ComboBox_bagian.setSelectedItem(table_level_gaji.getValueAt(i, 8).toString());
                }
            }
        });
    }

    public void refreshTable() {
        try {
            DefaultTableModel model = (DefaultTableModel) table_level_gaji.getModel();
            model.setRowCount(0);
            sql = "SELECT COUNT(`id_pegawai`) AS 'total' FROM `tb_karyawan` WHERE `status` = 'IN'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                label_total_karyawan_in.setText("Total Karyawan IN : " + Float.toString(rs.getFloat("total")));
            }
            float total_karyawan_IN_levelGaji = 0;
            sql = "SELECT `tb_level_gaji`.`level_gaji`, `upah_per_hari`, `lembur_per_jam`, `lembur_x_hari_kerja`, `lembur_x_hari_libur`, `premi_hadir`, `denda_terlambat`, `tb_level_gaji`.`keterangan`, `bagian`,\n"
                    + "SUM(IF(`status` = 'IN', 1, 0)) AS 'jumlah'\n"
                    + "FROM `tb_level_gaji` \n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_level_gaji`.`level_gaji` = `tb_karyawan`.`level_gaji`\n"
                    + "WHERE 1 "
                    + "GROUP BY `tb_level_gaji`.`level_gaji`  \n"
                    + "ORDER BY `tb_level_gaji`.`level_gaji` ";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[20];
            while (rs.next()) {
                row[0] = rs.getString("level_gaji");
                row[1] = rs.getFloat("upah_per_hari");
                row[2] = rs.getFloat("lembur_per_jam");
                row[3] = rs.getFloat("lembur_x_hari_kerja");
                row[4] = rs.getFloat("lembur_x_hari_libur");
                row[5] = rs.getFloat("premi_hadir");
                row[6] = rs.getFloat("denda_terlambat");
                row[7] = rs.getString("keterangan");
                row[8] = rs.getString("bagian");
                row[9] = rs.getInt("jumlah");
                row[10] = 0; //percent
                total_karyawan_IN_levelGaji = total_karyawan_IN_levelGaji + rs.getInt("jumlah");
                model.addRow(row);
            }
            label_total_karyawan_level_gaji.setText("Total Karyawan IN dengan level gaji : " + Float.toString(total_karyawan_IN_levelGaji));
            for (int i = 0; i < table_level_gaji.getRowCount(); i++) {
                float jumlah = (int) table_level_gaji.getValueAt(i, 9);
                float percent = jumlah / total_karyawan_IN_levelGaji * 100f;
                table_level_gaji.setValueAt(Math.round(percent * 10f) / 10f, i, 10);
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_level_gaji);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_LevelGaji.class.getName()).log(Level.SEVERE, null, ex);
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
        jScrollPane1 = new javax.swing.JScrollPane();
        table_level_gaji = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        txt_level_gaji = new javax.swing.JTextField();
        txt_upah = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txt_premi_hadir = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txt_keterangan = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        button_new = new javax.swing.JButton();
        button_edit = new javax.swing.JButton();
        button_delete = new javax.swing.JButton();
        label_total_karyawan_in = new javax.swing.JLabel();
        label_total_karyawan_level_gaji = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        ComboBox_bagian = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        txt_cari_level_gaji = new javax.swing.JTextField();
        button_search = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        txt_lembur_per_jam = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txt_lembur_x_hari_kerja = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txt_lembur_x_hari_libur = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        button_export = new javax.swing.JButton();
        txt_denda_terlambat = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "LEVEL GAJI", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        table_level_gaji.setAutoCreateRowSorter(true);
        table_level_gaji.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_level_gaji.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Level Gaji", "Upah / Hari", "Lembur/Jam", "Lembur x Hari Kerja", "Lembur x Hari Libur", "Premi Hadir", "Denda Terlambat", "Keterangan", "Bagian", "Jumlah", "%"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class
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
        table_level_gaji.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table_level_gaji);

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel2.setText("Level Gaji :");

        txt_level_gaji.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        txt_upah.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txt_upah.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_upahKeyTyped(evt);
            }
        });

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel3.setText("Upah / Hari :");

        txt_premi_hadir.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txt_premi_hadir.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_premi_hadirKeyTyped(evt);
            }
        });

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel4.setText("Premi Hadir /Hari :");

        txt_keterangan.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel5.setText("Keterangan :");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel6.setText("Rupiah");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel7.setText("Rupiah");

        button_new.setBackground(new java.awt.Color(255, 255, 255));
        button_new.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        button_new.setText("New");
        button_new.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_newActionPerformed(evt);
            }
        });

        button_edit.setBackground(new java.awt.Color(255, 255, 255));
        button_edit.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        button_edit.setText("Edit");
        button_edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_editActionPerformed(evt);
            }
        });

        button_delete.setBackground(new java.awt.Color(255, 255, 255));
        button_delete.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        button_delete.setText("Delete");
        button_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_deleteActionPerformed(evt);
            }
        });

        label_total_karyawan_in.setBackground(new java.awt.Color(255, 255, 255));
        label_total_karyawan_in.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_karyawan_in.setText("Total Karyawan IN :");

        label_total_karyawan_level_gaji.setBackground(new java.awt.Color(255, 255, 255));
        label_total_karyawan_level_gaji.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_karyawan_level_gaji.setText("Total Karyawan IN dengan level gaji :");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel8.setText("Bagian :");

        ComboBox_bagian.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        ComboBox_bagian.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "HARIAN", "CABUT" }));

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("Cari Level Gaji :");

        txt_cari_level_gaji.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_search.setBackground(new java.awt.Color(255, 255, 255));
        button_search.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search.setText("Cari");
        button_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_searchActionPerformed(evt);
            }
        });

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel10.setText("Lembur /Jam :");

        txt_lembur_per_jam.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txt_lembur_per_jam.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_lembur_per_jamKeyTyped(evt);
            }
        });

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel11.setText("Rupiah");

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel12.setText("Lembur Hari Kerja :");

        txt_lembur_x_hari_kerja.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txt_lembur_x_hari_kerja.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_lembur_x_hari_kerjaKeyTyped(evt);
            }
        });

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel13.setText("Lembur Hari Libur :");

        txt_lembur_x_hari_libur.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txt_lembur_x_hari_libur.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_lembur_x_hari_liburKeyTyped(evt);
            }
        });

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel14.setText("x");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel15.setText("x");

        button_export.setBackground(new java.awt.Color(255, 255, 255));
        button_export.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export.setText("Export To Excel");
        button_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportActionPerformed(evt);
            }
        });

        txt_denda_terlambat.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txt_denda_terlambat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_denda_terlambatKeyTyped(evt);
            }
        });

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel16.setText("Denda Terlambat /Hari :");

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel17.setText("Rupiah");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(label_total_karyawan_in, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label_total_karyawan_level_gaji, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_cari_level_gaji, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txt_premi_hadir, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txt_lembur_per_jam, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txt_upah, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txt_level_gaji, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(txt_lembur_x_hari_kerja, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(txt_lembur_x_hari_libur, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addComponent(txt_denda_terlambat, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(ComboBox_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button_new)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button_edit)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button_delete))
                                    .addComponent(txt_keterangan, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 782, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(label_total_karyawan_in, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_total_karyawan_level_gaji, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_cari_level_gaji, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_level_gaji, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_upah, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_lembur_per_jam, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_lembur_x_hari_kerja, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_lembur_x_hari_libur, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_premi_hadir, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_denda_terlambat, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_keterangan, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_new, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_edit, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
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
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void button_newActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_newActionPerformed
        // TODO add your handling code here:
        Boolean Check = true;
        int upah_per_hari = 0;
        float lembur_per_jam = 0;
        float lembur_x_hari_kerja = 0;
        float lembur_x_hari_libur = 0;
        int premi_hadir = 0;
        int denda_terlambat = 0;
        try {
            try {
                upah_per_hari = Integer.valueOf(txt_upah.getText());
                lembur_per_jam = Float.valueOf(txt_lembur_per_jam.getText());
                lembur_x_hari_kerja = Float.valueOf(txt_lembur_x_hari_kerja.getText());
                lembur_x_hari_libur = Float.valueOf(txt_lembur_x_hari_libur.getText());
                premi_hadir = Integer.valueOf(txt_premi_hadir.getText());
                denda_terlambat = Integer.valueOf(txt_denda_terlambat.getText());
            } catch (NumberFormatException e) {
                Check = false;
                JOptionPane.showMessageDialog(this, "Maaf format angka salah");
            }
            if (Check) {
                String Query = "INSERT INTO `tb_level_gaji` ("
                        + "`level_gaji`, "
                        + "`upah_per_hari`, "
                        + "`lembur_per_jam`, "
                        + "`lembur_x_hari_kerja`, "
                        + "`lembur_x_hari_libur`, "
                        + "`premi_hadir`, "
                        + "`denda_terlambat`, "
                        + "`keterangan`, "
                        + "`bagian`) "
                        + "VALUES ("
                        + "'" + txt_level_gaji.getText() + "', "
                        + "'" + upah_per_hari + "', "
                        + "'" + lembur_per_jam + "', "
                        + "'" + lembur_x_hari_kerja + "', "
                        + "'" + lembur_x_hari_libur + "', "
                        + "'" + premi_hadir + "', "
                        + "'" + denda_terlambat + "', "
                        + "'" + txt_keterangan.getText() + "', "
                        + "'" + ComboBox_bagian.getSelectedItem().toString() + "'"
                        + ")";
                Utility.db.getConnection().createStatement();
                if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                    JOptionPane.showMessageDialog(this, "Data berhasil di tambahkan");
                    refreshTable();
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_LevelGaji.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_newActionPerformed

    private void button_editActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_editActionPerformed
        // TODO add your handling code here:
        int j = table_level_gaji.getSelectedRow();
        Boolean Check = true;
        int upah_per_hari = 0;
        float lembur_per_jam = 0;
        float lembur_x_hari_kerja = 0;
        float lembur_x_hari_libur = 0;
        int premi = 0;
        int denda_terlambat = 0;
        try {
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan Pilih data yang mau di edit !");
            } else {
                try {
                    upah_per_hari = Integer.valueOf(txt_upah.getText());
                    lembur_per_jam = Float.valueOf(txt_lembur_per_jam.getText());
                    lembur_x_hari_kerja = Float.valueOf(txt_lembur_x_hari_kerja.getText());
                    lembur_x_hari_libur = Float.valueOf(txt_lembur_x_hari_libur.getText());
                    premi = Integer.valueOf(txt_premi_hadir.getText());
                    denda_terlambat = Integer.valueOf(txt_denda_terlambat.getText());
                } catch (NumberFormatException e) {
                    Check = false;
                    JOptionPane.showMessageDialog(this, "Maaf format angka salah");
                }
                if (Check) {
                    String Query = "UPDATE `tb_level_gaji` SET "
                            + "`level_gaji` = '" + txt_level_gaji.getText() + "', "
                            + "`upah_per_hari` = '" + upah_per_hari + "', "
                            + "`lembur_per_jam` = '" + lembur_per_jam + "', "
                            + "`lembur_x_hari_kerja` = '" + lembur_x_hari_kerja + "', "
                            + "`lembur_x_hari_libur` = '" + lembur_x_hari_libur + "', "
                            + "`premi_hadir` = '" + premi + "', "
                            + "`denda_terlambat` = '" + denda_terlambat + "', "
                            + "`keterangan` = '" + txt_keterangan.getText() + "', "
                            + "`bagian` = '" + ComboBox_bagian.getSelectedItem().toString() + "' "
                            + "WHERE `level_gaji` = '" + table_level_gaji.getValueAt(j, 0).toString() + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                        JOptionPane.showMessageDialog(this, "Data berhasil di ubah");
                        refreshTable();
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_LevelGaji.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_editActionPerformed

    private void button_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_deleteActionPerformed
        // TODO add your handling code here:
        try {
            int j = table_level_gaji.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang mau di hapus !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Apakah kamu yakin ingin menghapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    String Query = "DELETE FROM `tb_level_gaji` WHERE `level_gaji` = '" + table_level_gaji.getValueAt(j, 0).toString() + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                        JOptionPane.showMessageDialog(this, "Data berhasil di ubah");
                        refreshTable();
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_LevelGaji.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_deleteActionPerformed

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_button_searchActionPerformed

    private void txt_upahKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_upahKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_upahKeyTyped

    private void txt_lembur_per_jamKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_lembur_per_jamKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_lembur_per_jamKeyTyped

    private void txt_lembur_x_hari_kerjaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_lembur_x_hari_kerjaKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_lembur_x_hari_kerjaKeyTyped

    private void txt_lembur_x_hari_liburKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_lembur_x_hari_liburKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_lembur_x_hari_liburKeyTyped

    private void txt_premi_hadirKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_premi_hadirKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_premi_hadirKeyTyped

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_level_gaji.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_exportActionPerformed

    private void txt_denda_terlambatKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_denda_terlambatKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_denda_terlambatKeyTyped


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_bagian;
    private javax.swing.JButton button_delete;
    private javax.swing.JButton button_edit;
    private javax.swing.JButton button_export;
    private javax.swing.JButton button_new;
    private javax.swing.JButton button_search;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_total_karyawan_in;
    private javax.swing.JLabel label_total_karyawan_level_gaji;
    public static javax.swing.JTable table_level_gaji;
    private javax.swing.JTextField txt_cari_level_gaji;
    private javax.swing.JTextField txt_denda_terlambat;
    private javax.swing.JTextField txt_keterangan;
    private javax.swing.JTextField txt_lembur_per_jam;
    private javax.swing.JTextField txt_lembur_x_hari_kerja;
    private javax.swing.JTextField txt_lembur_x_hari_libur;
    private javax.swing.JTextField txt_level_gaji;
    private javax.swing.JTextField txt_premi_hadir;
    private javax.swing.JTextField txt_upah;
    // End of variables declaration//GEN-END:variables
}
