package waleta_system.BahanBaku;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;

public class JDialog_PecahLP_GabungPecah extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public JDialog_PecahLP_GabungPecah(java.awt.Frame parent, boolean modal, String no_grading) {
        super(parent, modal);
        initComponents();
        refreshTable_pecah_lp(no_grading);
    }

    private void refreshTable_pecah_lp(String no_grading) {
        try {
            decimalFormat.setMaximumFractionDigits(0);
            decimalFormat.setGroupingUsed(true);
            DefaultTableModel model = (DefaultTableModel) Table_pecah_lp.getModel();
            model.setRowCount(0);

            sql = "SELECT PCH.`kode_pecah_kartu`, PCH.`no_grading`, `tb_grading_bahan_baku`.`no_kartu_waleta`, `tb_grading_bahan_baku`.`kode_grade`, PCH.`jumlah_keping`, PCH.`keping_upah`, PCH.`berat_basah`, PCH.`berat_riil`, PCH.`jenis_bulu_lp`, PCH.`memo_lp`, \n"
                    + "PCH.`kaki_besar_lp`, PCH.`kaki_kecil_lp`, PCH.`hilang_kaki_lp`, PCH.`ada_susur_lp`, PCH.`ada_susur_besar_lp`, PCH.`tanpa_susur_lp`, PCH.`utuh_lp`, PCH.`hilang_ujung_lp`, PCH.`pecah_1_lp`, PCH.`pecah_2`, PCH.`jumlah_sobek`, PCH.`sobek_lepas`, PCH.`jumlah_gumpil`, "
                    + "`tb_laporan_produksi`.`no_laporan_produksi` \n"
                    + "FROM `tb_bahan_baku_pecah_kartu` PCH \n"
                    + "LEFT JOIN `tb_grading_bahan_baku` ON PCH.`no_grading` = `tb_grading_bahan_baku`.`no_grading`\n"
                    + "LEFT JOIN `tb_laporan_produksi` ON PCH.`kode_pecah_kartu` = `tb_laporan_produksi`.`kode_pecah_lp`\n"
                    + "WHERE \n"
                    + "PCH.`no_grading` = '" + no_grading + "' \n"
                    + "AND `tb_laporan_produksi`.`no_laporan_produksi` IS NULL ";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[25];
            while (rs.next()) {
                label_no_kartu.setText(rs.getString("no_kartu_waleta"));
                label_kode_grade.setText(rs.getString("kode_grade"));
                row[0] = false;
                row[1] = rs.getInt("kode_pecah_kartu");
                row[2] = rs.getInt("no_grading");
                row[3] = rs.getString("no_kartu_waleta");
                row[4] = rs.getString("kode_grade");
                row[5] = rs.getFloat("jumlah_keping");
                row[6] = rs.getFloat("keping_upah");
                row[7] = rs.getFloat("berat_basah");
                row[8] = rs.getFloat("berat_riil");
                row[9] = rs.getString("jenis_bulu_lp");
                row[10] = rs.getString("memo_lp");
                row[11] = rs.getInt("kaki_besar_lp");
                row[12] = rs.getInt("kaki_kecil_lp");
                row[13] = rs.getInt("hilang_kaki_lp");
                row[14] = rs.getInt("ada_susur_besar_lp");
                row[15] = rs.getInt("ada_susur_lp");
                row[16] = rs.getInt("tanpa_susur_lp");
                row[17] = rs.getInt("utuh_lp");
                row[18] = rs.getInt("hilang_ujung_lp");
                row[19] = rs.getInt("pecah_1_lp");
                row[20] = rs.getInt("pecah_2");
                row[21] = rs.getInt("jumlah_sobek");
                row[22] = rs.getInt("sobek_lepas");
                row[23] = rs.getInt("jumlah_gumpil");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_pecah_lp);
            int rowData = Table_pecah_lp.getRowCount();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JDialog_PecahLP_GabungPecah.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void proses() {
        try {
            boolean check = true;
            String no_grading = null, no_kartu = null, kode_grade = null, jenisBulu = null, memo_lp = null;
            float kpg = 0, kpg_upah = 0, gram = 0, gram_riil = 0;
            int kk_besar = 0, kk_kecil = 0, tanpa_kk = 0, susur_besar = 0, susur_kecil = 0, tanpa_susur = 0, utuh = 0, hilang_ujung = 0, pch_1 = 0, pch_2 = 0, sobek = 0, sobek_lepas = 0, gumpil = 0;
            for (int i = 0; i < Table_pecah_lp.getRowCount(); i++) {
                if ((boolean) Table_pecah_lp.getValueAt(i, 0)) {
                    String currentJenisBulu = Table_pecah_lp.getValueAt(i, 9).toString();
                    if (jenisBulu == null) {
                        jenisBulu = currentJenisBulu;
                        no_grading = Table_pecah_lp.getValueAt(i, 2).toString();
                        no_kartu = Table_pecah_lp.getValueAt(i, 3).toString();
                        kode_grade = Table_pecah_lp.getValueAt(i, 4).toString();
                        memo_lp = Table_pecah_lp.getValueAt(i, 10) == null ? "" : Table_pecah_lp.getValueAt(i, 10).toString();
                    } else if (!jenisBulu.equals(currentJenisBulu)) {
                        JOptionPane.showMessageDialog(this, "Maaf Jenis bulu yang terpilih harus sama semua!");
                        check = false;
                        break;
                    }

                    kpg = kpg + (float) Table_pecah_lp.getValueAt(i, 5);
                    kpg_upah = kpg_upah + (float) Table_pecah_lp.getValueAt(i, 6);
                    gram = gram + (float) Table_pecah_lp.getValueAt(i, 7);
                    gram_riil = gram_riil + (float) Table_pecah_lp.getValueAt(i, 8);

                    kk_besar = kk_besar + (int) Table_pecah_lp.getValueAt(i, 11);
                    kk_kecil = kk_kecil + (int) Table_pecah_lp.getValueAt(i, 12);
                    tanpa_kk = tanpa_kk + (int) Table_pecah_lp.getValueAt(i, 13);
                    susur_besar = susur_besar + (int) Table_pecah_lp.getValueAt(i, 14);
                    susur_kecil = susur_kecil + (int) Table_pecah_lp.getValueAt(i, 15);
                    tanpa_susur = tanpa_susur + (int) Table_pecah_lp.getValueAt(i, 16);
                    utuh = utuh + (int) Table_pecah_lp.getValueAt(i, 17);
                    hilang_ujung = hilang_ujung + (int) Table_pecah_lp.getValueAt(i, 18);
                    pch_1 = pch_1 + (int) Table_pecah_lp.getValueAt(i, 19);
                    pch_2 = pch_2 + (int) Table_pecah_lp.getValueAt(i, 20);
                    sobek = sobek + (int) Table_pecah_lp.getValueAt(i, 21);
                    sobek_lepas = sobek_lepas + (int) Table_pecah_lp.getValueAt(i, 22);
                    gumpil = gumpil + (int) Table_pecah_lp.getValueAt(i, 23);
                }
            }
            Table_pecah_lp_baru.setValueAt(no_grading, 0, 0);
            Table_pecah_lp_baru.setValueAt(no_kartu, 0, 1);
            Table_pecah_lp_baru.setValueAt(kode_grade, 0, 2);
            Table_pecah_lp_baru.setValueAt(kpg, 0, 3);
            Table_pecah_lp_baru.setValueAt(kpg_upah, 0, 4);
            Table_pecah_lp_baru.setValueAt(gram, 0, 5);
            Table_pecah_lp_baru.setValueAt(gram_riil, 0, 6);
            Table_pecah_lp_baru.setValueAt(jenisBulu, 0, 7);
            Table_pecah_lp_baru.setValueAt(memo_lp, 0, 8);
            Table_pecah_lp_baru.setValueAt(kk_besar, 0, 9);
            Table_pecah_lp_baru.setValueAt(kk_kecil, 0, 10);
            Table_pecah_lp_baru.setValueAt(tanpa_kk, 0, 11);
            Table_pecah_lp_baru.setValueAt(susur_besar, 0, 12);
            Table_pecah_lp_baru.setValueAt(susur_kecil, 0, 13);
            Table_pecah_lp_baru.setValueAt(tanpa_susur, 0, 14);
            Table_pecah_lp_baru.setValueAt(utuh, 0, 15);
            Table_pecah_lp_baru.setValueAt(hilang_ujung, 0, 16);
            Table_pecah_lp_baru.setValueAt(pch_1, 0, 17);
            Table_pecah_lp_baru.setValueAt(pch_2, 0, 18);
            Table_pecah_lp_baru.setValueAt(sobek, 0, 19);
            Table_pecah_lp_baru.setValueAt(sobek_lepas, 0, 20);
            Table_pecah_lp_baru.setValueAt(gumpil, 0, 21);
            ColumnsAutoSizer.sizeColumnsToFit(Table_pecah_lp_baru);
            button_gabung_pecah.setEnabled(check);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JDialog_PecahLP_GabungPecah.class.getName()).log(Level.SEVERE, null, ex);
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
        jLabel18 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Table_pecah_lp = new javax.swing.JTable();
        button_gabung_pecah = new javax.swing.JButton();
        label_no_kartu = new javax.swing.JLabel();
        button_batal = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        Table_pecah_lp_baru = new javax.swing.JTable();
        jLabel21 = new javax.swing.JLabel();
        label_kode_grade = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel18.setText("Hasil Pecah yang baru");

        Table_pecah_lp.setAutoCreateRowSorter(true);
        Table_pecah_lp.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Table_pecah_lp.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "Kode", "No Grading", "No Kartu", "Grade", "Kpg", "Kpg Upah", "Gram", "Gram Riil", "Jenis Bulu", "Memo LP", "kk BESAR", "kk KECIL", "Tanpa kk", "Susur BESAR", "Susur KECIL", "Tanpa Susur", "Utuh", "Hlg Ujung", "Pch 1", "Pch 2", "Sobek", "Sobek Lepas", "Gumpil"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_pecah_lp.getTableHeader().setReorderingAllowed(false);
        Table_pecah_lp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Table_pecah_lpMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(Table_pecah_lp);

        button_gabung_pecah.setBackground(new java.awt.Color(255, 255, 255));
        button_gabung_pecah.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_gabung_pecah.setText("Gabung Pecah");
        button_gabung_pecah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_gabung_pecahActionPerformed(evt);
            }
        });

        label_no_kartu.setBackground(new java.awt.Color(255, 255, 255));
        label_no_kartu.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_no_kartu.setForeground(new java.awt.Color(255, 0, 0));
        label_no_kartu.setText("No Kartu");

        button_batal.setBackground(new java.awt.Color(255, 255, 255));
        button_batal.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_batal.setText("Batal");
        button_batal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_batalActionPerformed(evt);
            }
        });

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel20.setText("Data pecah LP : ");

        Table_pecah_lp_baru.setAutoCreateRowSorter(true);
        Table_pecah_lp_baru.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Table_pecah_lp_baru.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "No Grading", "No Kartu", "Grade", "Kpg", "Kpg Upah", "Gram", "Gram Riil", "Jenis Bulu", "Memo LP", "kk BESAR", "kk KECIL", "Tanpa kk", "Susur BESAR", "Susur KECIL", "Tanpa Susur", "Utuh", "Hlg Ujung", "Pch 1", "Pch 2", "Sobek", "Sobek Lepas", "Gumpil"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_pecah_lp_baru.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(Table_pecah_lp_baru);

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 0, 0));
        jLabel21.setText("-");

        label_kode_grade.setBackground(new java.awt.Color(255, 255, 255));
        label_kode_grade.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_kode_grade.setForeground(new java.awt.Color(255, 0, 0));
        label_kode_grade.setText("Grade");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 971, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 971, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(button_batal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_gabung_pecah))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel18)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_no_kartu)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_kode_grade)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_no_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kode_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_gabung_pecah)
                    .addComponent(button_batal))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void button_gabung_pecahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_gabung_pecahActionPerformed
        // TODO add your handling code here:
        try {
            Utility.db.getConnection().setAutoCommit(false);
            String DEL_Query = "DELETE FROM `tb_bahan_baku_pecah_kartu` WHERE `kode_pecah_kartu` = ?";
            PreparedStatement pst = Utility.db.getConnection().prepareStatement(DEL_Query);

            for (int i = 0; i < Table_pecah_lp.getRowCount(); i++) {
                if ((boolean) Table_pecah_lp.getValueAt(i, 0)) {
                    String kode_pecah_kartu = Table_pecah_lp.getValueAt(i, 1).toString();
                    pst.setString(1, kode_pecah_kartu);
                    pst.executeUpdate();
                }
            }

            String INSERT_Query = "INSERT INTO `tb_bahan_baku_pecah_kartu`(`no_grading`, `jumlah_keping`, `keping_upah`, `berat_basah`, `berat_riil`, `jenis_bulu_lp`, `memo_lp`, `kaki_besar_lp`, `kaki_kecil_lp`, `hilang_kaki_lp`, `ada_susur_lp`, `ada_susur_besar_lp`, `tanpa_susur_lp`, `utuh_lp`, `hilang_ujung_lp`, `pecah_1_lp`, `pecah_2`, `jumlah_sobek`, `sobek_lepas`, `jumlah_gumpil`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst2 = Utility.db.getConnection().prepareStatement(INSERT_Query);
            // Set the parameters of the prepared statement.
            pst2.setString(1, Table_pecah_lp_baru.getValueAt(0, 0).toString());
            pst2.setString(2, Table_pecah_lp_baru.getValueAt(0, 3).toString());
            pst2.setString(3, Table_pecah_lp_baru.getValueAt(0, 4).toString());
            pst2.setString(4, Table_pecah_lp_baru.getValueAt(0, 5).toString());
            pst2.setString(5, Table_pecah_lp_baru.getValueAt(0, 6).toString());
            pst2.setString(6, Table_pecah_lp_baru.getValueAt(0, 7).toString());
            pst2.setString(7, Table_pecah_lp_baru.getValueAt(0, 8).toString());
            pst2.setString(8, Table_pecah_lp_baru.getValueAt(0, 9).toString());
            pst2.setString(9, Table_pecah_lp_baru.getValueAt(0, 10).toString());
            pst2.setString(10, Table_pecah_lp_baru.getValueAt(0, 11).toString());
            pst2.setString(11, Table_pecah_lp_baru.getValueAt(0, 12).toString());
            pst2.setString(12, Table_pecah_lp_baru.getValueAt(0, 13).toString());
            pst2.setString(13, Table_pecah_lp_baru.getValueAt(0, 14).toString());
            pst2.setString(14, Table_pecah_lp_baru.getValueAt(0, 15).toString());
            pst2.setString(15, Table_pecah_lp_baru.getValueAt(0, 16).toString());
            pst2.setString(16, Table_pecah_lp_baru.getValueAt(0, 17).toString());
            pst2.setString(17, Table_pecah_lp_baru.getValueAt(0, 18).toString());
            pst2.setString(18, Table_pecah_lp_baru.getValueAt(0, 19).toString());
            pst2.setString(19, Table_pecah_lp_baru.getValueAt(0, 20).toString());
            pst2.setString(20, Table_pecah_lp_baru.getValueAt(0, 21).toString());
            pst2.executeUpdate();

            Utility.db.getConnection().commit();
            JOptionPane.showMessageDialog(this, "Sukses menggabungkan pecah LP");
            this.dispose();
        } catch (Exception ex) {
            try {
                Utility.db.getConnection().rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(JDialog_PecahLP_GabungPecah.class.getName()).log(Level.SEVERE, null, ex1);
            }
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JDialog_PecahLP_GabungPecah.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                Utility.db.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_PecahLP_GabungPecah.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_gabung_pecahActionPerformed

    private void button_batalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_batalActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_button_batalActionPerformed

    private void Table_pecah_lpMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Table_pecah_lpMouseClicked
        // TODO add your handling code here:
        proses();
    }//GEN-LAST:event_Table_pecah_lpMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable Table_pecah_lp;
    private javax.swing.JTable Table_pecah_lp_baru;
    public javax.swing.JButton button_batal;
    public javax.swing.JButton button_gabung_pecah;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel label_kode_grade;
    private javax.swing.JLabel label_no_kartu;
    // End of variables declaration//GEN-END:variables
}
