package waleta_system.Panel_produksi;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import waleta_system.Class.Utility;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;

public class JDialog_Rekap_TargetLPCetakdiSetor extends javax.swing.JDialog {

    ResultSet rs;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public JDialog_Rekap_TargetLPCetakdiSetor(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setResizable(false);
    }

    public void loaddata() {
        try {
            DefaultTableModel model = (DefaultTableModel) tabel_mandiri.getModel();
            model.setRowCount(0);
            String query = "SELECT `cetak_dikerjakan`, `tb_karyawan`.`nama_pegawai`, `tb_bagian`.`nama_bagian`, `level_gaji`,\n"
                    + "SUM(IF(DATEDIFF(`tgl_selesai_cetak`, `tgl_mulai_cetak`) < 4 AND ((`cetak_mangkok`/`keping_upah`)*100)>=`target_ctk_mku`, (IF((`cetak_mangkok`+`cetak_pecah`+`cetak_flat`)=0,`cetak_jidun_real` / `kpg_lp_jidun`, `keping_upah` / IF(`tb_grade_bahan_baku`.`kategori`='FLAT',`kpg_lp_flat`, `kpg_lp`))), 0)) AS 'lp_bonus'\n"
                    + "FROM `tb_cetak` \n"
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`\n"
                    + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah`\n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_cetak`.`cetak_dikerjakan` = `tb_karyawan`.`id_pegawai`\n"
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                    + "WHERE `tgl_selesai_cetak` BETWEEN '" + dateFormat.format(Date_setor_cetak1.getDate()) + "' AND '" + dateFormat.format(Date_setor_cetak2.getDate()) + "'\n"
                    + "AND `cetak_dikerjakan` IS NOT NULL \n"
                    + "GROUP BY `cetak_dikerjakan`\n"
                    + "ORDER BY `tb_bagian`.`nama_bagian`";
            rs = Utility.db.getStatement().executeQuery(query);
            Object[] row = new Object[5];
            while (rs.next()) {
                String ruangan = "";
                if (rs.getString("nama_bagian").split("-")[4] != null) {
                    ruangan = rs.getString("nama_bagian").split("-")[4];
                }
                row[0] = ruangan;
                row[1] = rs.getString("nama_pegawai");
                row[2] = Math.round(rs.getFloat("lp_bonus") * 100.f) / 100.f;
                if (rs.getFloat("lp_bonus") > 0) {
                    model.addRow(row);
                }
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_mandiri);

            DefaultTableModel model2 = (DefaultTableModel) tabel_koreksi.getModel();
            model2.setRowCount(0);
            String query2 = "SELECT `cetak_dikoreksi`, `cetak_dikoreksi_id`, `tb_karyawan`.`nama_pegawai`, `tb_bagian`.`nama_bagian`,  \n"
                    + "SUM(IF(DATEDIFF(`tgl_mulai_cetak`, `tb_cabut`.`tgl_setor_cabut`) < 3, (IF(`tb_laporan_produksi`.`jumlah_keping`>0, `tb_laporan_produksi`.`jumlah_keping`, `tb_laporan_produksi`.`berat_basah`/8) / `kpg_lp`), 0)) AS 'lp_bonus'\n"
                    + "FROM `tb_cetak` \n"
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_cabut` ON `tb_cetak`.`no_laporan_produksi` = `tb_cabut`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah`\n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_cetak`.`cetak_dikoreksi_id` = `tb_karyawan`.`id_pegawai`\n"
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                    + "WHERE `tgl_mulai_cetak` BETWEEN '" + dateFormat.format(Date_setor_cetak1.getDate()) + "' AND '" + dateFormat.format(Date_setor_cetak2.getDate()) + "'\n"
                    + "AND `cetak_dikoreksi` IS NOT NULL AND `tb_cetak`.`no_laporan_produksi` LIKE 'WL-%'\n"
                    + "GROUP BY `cetak_dikoreksi`"
                    + "ORDER BY `tb_bagian`.`nama_bagian`";
            rs = Utility.db.getStatement().executeQuery(query2);
            Object[] row2 = new Object[5];
            while (rs.next()) {
                String ruangan = "";
                if (rs.getString("nama_bagian").split("-")[4] != null) {
                    ruangan = rs.getString("nama_bagian").split("-")[4];
                }
                row2[0] = ruangan;
                row2[1] = rs.getString("nama_pegawai");
                row2[2] = Math.round(rs.getFloat("lp_bonus") * 100.f) / 100.f;
                if (rs.getFloat("lp_bonus") > 0) {
                    model2.addRow(row2);
                }
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_koreksi);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_Rekap_TargetLPCetakdiSetor.class.getName()).log(Level.SEVERE, null, ex);
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
        button_refresh = new javax.swing.JButton();
        label_hari_tanggal = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        button_copy_text1 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabel_mandiri = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        button_copy_text2 = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        tabel_koreksi = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        Date_setor_cetak2 = new com.toedter.calendar.JDateChooser();
        Date_setor_cetak1 = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        button_refresh.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh.setText("Refresh");
        button_refresh.setToolTipText("Copy text into clipboard");
        button_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refreshActionPerformed(evt);
            }
        });

        label_hari_tanggal.setBackground(new java.awt.Color(255, 255, 255));
        label_hari_tanggal.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_hari_tanggal.setText("Tanggal Selesai Cetak :");

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel2.setText("Jumlah LP Target disetorkan Anak Cetak Mandiri");

        button_copy_text1.setBackground(new java.awt.Color(255, 255, 255));
        button_copy_text1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_copy_text1.setText("Copy Text");
        button_copy_text1.setToolTipText("Copy text into clipboard");
        button_copy_text1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_copy_text1ActionPerformed(evt);
            }
        });

        tabel_mandiri.setAutoCreateRowSorter(true);
        tabel_mandiri.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        tabel_mandiri.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Ruangan", "Nama", "LP"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Float.class
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
        tabel_mandiri.setRowHeight(20);
        tabel_mandiri.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tabel_mandiri);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_copy_text1)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(button_copy_text1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 589, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        button_copy_text2.setBackground(new java.awt.Color(255, 255, 255));
        button_copy_text2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_copy_text2.setText("Copy Text");
        button_copy_text2.setToolTipText("Copy text into clipboard");
        button_copy_text2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_copy_text2ActionPerformed(evt);
            }
        });

        tabel_koreksi.setAutoCreateRowSorter(true);
        tabel_koreksi.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        tabel_koreksi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Ruangan", "Nama", "LP"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Float.class
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
        tabel_koreksi.setRowHeight(20);
        tabel_koreksi.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(tabel_koreksi);

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel5.setText("Jumlah LP Target Tim Koreksi");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_copy_text2)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_copy_text2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 589, Short.MAX_VALUE)
                .addContainerGap())
        );

        Date_setor_cetak2.setBackground(new java.awt.Color(255, 255, 255));
        Date_setor_cetak2.setDate(new Date());
        Date_setor_cetak2.setDateFormatString("dd MMMM yyyy");
        Date_setor_cetak2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_setor_cetak1.setBackground(new java.awt.Color(255, 255, 255));
        Date_setor_cetak1.setDate(new Date(new Date().getTime()- (7 * 24 * 60 * 60 * 1000)));
        Date_setor_cetak1.setDateFormatString("dd MMMM yyyy");
        Date_setor_cetak1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 2, 12)); // NOI18N
        jLabel4.setText("Note : HANYA AMBIL LP YG ADA PEGAWAI CETAKNYA");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_hari_tanggal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date_setor_cetak1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date_setor_cetak2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_refresh)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(Date_setor_cetak1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_setor_cetak2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_hari_tanggal)
                    .addComponent(button_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void button_copy_text1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_copy_text1ActionPerformed
        // TODO add your handling code here:
        String myString = "*Jumlah LP Target disetorkan Anak Cetak Mandiri*";
        myString += "\nPeriode : " + new SimpleDateFormat("dd-MMM-yy").format(Date_setor_cetak1.getDate()) + " - " + new SimpleDateFormat("dd-MMM-yy").format(Date_setor_cetak2.getDate());
        myString += "\n\n*Ruang A*";
        for (int i = 0; i < tabel_mandiri.getRowCount(); i++) {
            if (tabel_mandiri.getValueAt(i, 0).toString().equals("A")) {
                myString += "\n" + tabel_mandiri.getValueAt(i, 1).toString() + "   :   " + tabel_mandiri.getValueAt(i, 2).toString();
            }
        }
        myString += "\n\n*Ruang B*";
        for (int i = 0; i < tabel_mandiri.getRowCount(); i++) {
            if (tabel_mandiri.getValueAt(i, 0).toString().equals("B")) {
                myString += "\n" + tabel_mandiri.getValueAt(i, 1).toString() + "   :   " + tabel_mandiri.getValueAt(i, 2).toString();
            }
        }
        myString += "\n\n*Ruang C*";
        for (int i = 0; i < tabel_mandiri.getRowCount(); i++) {
            if (tabel_mandiri.getValueAt(i, 0).toString().equals("C")) {
                myString += "\n" + tabel_mandiri.getValueAt(i, 1).toString() + "   :   " + tabel_mandiri.getValueAt(i, 2).toString();
            }
        }
        myString += "\n\n*Ruang D*";
        for (int i = 0; i < tabel_mandiri.getRowCount(); i++) {
            if (tabel_mandiri.getValueAt(i, 0).toString().equals("D")) {
                myString += "\n" + tabel_mandiri.getValueAt(i, 1).toString() + "   :   " + tabel_mandiri.getValueAt(i, 2).toString();
            }
        }
        myString += "\n\n*Ruang E*";
        for (int i = 0; i < tabel_mandiri.getRowCount(); i++) {
            if (tabel_mandiri.getValueAt(i, 0).toString().equals("E")) {
                myString += "\n" + tabel_mandiri.getValueAt(i, 1).toString() + "   :   " + tabel_mandiri.getValueAt(i, 2).toString();
            }
        }
        
        StringSelection stringSelection = new StringSelection(myString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
        JOptionPane.showMessageDialog(this, "Text copied to clipboard");
    }//GEN-LAST:event_button_copy_text1ActionPerformed

    private void button_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refreshActionPerformed
        // TODO add your handling code here:
        if (Date_setor_cetak1.getDate() != null && Date_setor_cetak2.getDate() != null) {
            loaddata();
        } else {
            JOptionPane.showMessageDialog(this, "Silahkan pilih range tanggal");
        }
    }//GEN-LAST:event_button_refreshActionPerformed

    private void button_copy_text2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_copy_text2ActionPerformed
        // TODO add your handling code here:
        String myString = "*Jumlah LP Target Tim Koreksi*";
        myString += "\nPeriode : " + new SimpleDateFormat("dd MMM yy").format(Date_setor_cetak1.getDate()) + " - " + new SimpleDateFormat("dd MMM yy").format(Date_setor_cetak2.getDate());
        myString += "\n\n*Ruang A*";
        for (int i = 0; i < tabel_koreksi.getRowCount(); i++) {
            if (tabel_koreksi.getValueAt(i, 0).toString().equals("A")) {
                myString += "\n" + tabel_koreksi.getValueAt(i, 1).toString() + "   :   " + tabel_koreksi.getValueAt(i, 2).toString();
            }
        }
        myString += "\n\n*Ruang B*";
        for (int i = 0; i < tabel_koreksi.getRowCount(); i++) {
            if (tabel_koreksi.getValueAt(i, 0).toString().equals("B")) {
                myString += "\n" + tabel_koreksi.getValueAt(i, 1).toString() + "   :   " + tabel_koreksi.getValueAt(i, 2).toString();
            }
        }
        myString += "\n\n*Ruang C*";
        for (int i = 0; i < tabel_koreksi.getRowCount(); i++) {
            if (tabel_koreksi.getValueAt(i, 0).toString().equals("C")) {
                myString += "\n" + tabel_koreksi.getValueAt(i, 1).toString() + "   :   " + tabel_koreksi.getValueAt(i, 2).toString();
            }
        }
        myString += "\n\n*Ruang D*";
        for (int i = 0; i < tabel_koreksi.getRowCount(); i++) {
            if (tabel_koreksi.getValueAt(i, 0).toString().equals("D")) {
                myString += "\n" + tabel_koreksi.getValueAt(i, 1).toString() + "   :   " + tabel_koreksi.getValueAt(i, 2).toString();
            }
        }
        myString += "\n\n*Ruang E*";
        for (int i = 0; i < tabel_koreksi.getRowCount(); i++) {
            if (tabel_koreksi.getValueAt(i, 0).toString().equals("E")) {
                myString += "\n" + tabel_koreksi.getValueAt(i, 1).toString() + "   :   " + tabel_koreksi.getValueAt(i, 2).toString();
            }
        }
        
        StringSelection stringSelection = new StringSelection(myString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
        JOptionPane.showMessageDialog(this, "Text copied to clipboard");
    }//GEN-LAST:event_button_copy_text2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JDialog_Rekap_TargetLPCetakdiSetor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JDialog_Rekap_TargetLPCetakdiSetor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JDialog_Rekap_TargetLPCetakdiSetor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JDialog_Rekap_TargetLPCetakdiSetor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JDialog_Rekap_TargetLPCetakdiSetor dialog = new JDialog_Rekap_TargetLPCetakdiSetor(new javax.swing.JFrame(), true);
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
    private com.toedter.calendar.JDateChooser Date_setor_cetak1;
    private com.toedter.calendar.JDateChooser Date_setor_cetak2;
    private javax.swing.JButton button_copy_text1;
    private javax.swing.JButton button_copy_text2;
    private javax.swing.JButton button_refresh;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel label_hari_tanggal;
    private javax.swing.JTable tabel_koreksi;
    private javax.swing.JTable tabel_mandiri;
    // End of variables declaration//GEN-END:variables
}
