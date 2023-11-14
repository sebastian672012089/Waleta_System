package waleta_system.BahanBaku;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;

public class JDialog_total_cucian_hari_ini extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    java.util.Date date = new java.util.Date();//date berisikan tanggal hari ini
    DecimalFormat decimalFormat = new DecimalFormat();
    String myText = "";

    public JDialog_total_cucian_hari_ini(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        Load_Data();
    }

    public void Load_Data() {
        try {
            
            String tanggal_cuci = new SimpleDateFormat("EEEEE, dd/MM/yyyy").format(date);
            String tanggal_cabut = new SimpleDateFormat("EEEEE, dd/MM/yyyy").format(Utility.addDaysSkippingFreeDays(date, 1));
            label_tgl_cuci.setText("Tgl Cuci : " + tanggal_cuci);
            label_tgl_cabut.setText("Tgl Cabut : " + tanggal_cabut);
            myText = myText + "*Total Cucian Hari ini*\n";
            myText = myText + label_tgl_cuci.getText() + "\n";
            myText = myText + label_tgl_cabut.getText() + "\n";
            myText = myText + "\n";
            int total_kpg_waleta = 0, total_gram_waleta = 0;
            int total_kpg_sub = 0, total_gram_sub = 0;
            DefaultTableModel model = (DefaultTableModel) tabel_cucian_hari_ini.getModel();
            model.setRowCount(0);
            sql = "SELECT `ruangan`, SUM(`jumlah_keping`) AS 'kpg', SUM(`berat_basah`) AS 'gram'\n"
                    + "FROM `tb_laporan_produksi` WHERE `tanggal_lp` = CURRENT_DATE GROUP BY `ruangan`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString("ruangan"), rs.getFloat("kpg"), rs.getFloat("gram")});
                myText = myText + rs.getString("ruangan") + " : " + decimalFormat.format(rs.getFloat("kpg")) + " - " + decimalFormat.format(rs.getFloat("gram")) + "\n";
                if (rs.getString("ruangan").length() == 1) {
                    total_kpg_waleta = total_kpg_waleta + rs.getInt("kpg");
                    total_gram_waleta = total_gram_waleta + rs.getInt("gram");
                } else if (rs.getString("ruangan").length() > 1) {
                    total_kpg_sub = total_kpg_sub + rs.getInt("kpg");
                    total_gram_sub = total_gram_sub + rs.getInt("gram");
                }
            }
            myText = myText + "\n";
            ColumnsAutoSizer.sizeColumnsToFit(tabel_cucian_hari_ini);
            decimalFormat.setGroupingUsed(true);
            decimalFormat.setMaximumFractionDigits(1);
            label_total_waleta.setText("WALETA : " + decimalFormat.format(total_kpg_waleta) + " - " + decimalFormat.format(total_gram_waleta));
            label_total_sub.setText("SUB : " + decimalFormat.format(total_kpg_sub) + " - " + decimalFormat.format(total_gram_sub));
            label_total_waleta_sub.setText("WALETA + SUB : " + decimalFormat.format(total_kpg_waleta + total_kpg_sub) + " - " + decimalFormat.format(total_gram_waleta + total_gram_sub));

            myText = myText + "*TOTAL (KPG - GRAM)*\n";
            myText = myText + label_total_waleta.getText() + "\n";
            myText = myText + label_total_sub.getText() + "\n";
            myText = myText + label_total_waleta_sub.getText() + "\n";
            myText = myText + "\n";
            
            //Berdasarkan jenis bulu upah
            float total_gram_br = 0, total_gram_bs = 0, total_gram_bb = 0, total_gram_br_bs_bb = 0;
            sql = "SELECT `jenis_bulu_lp`, SUM(`berat_basah`) AS 'gram'\n"
                    + "FROM `tb_laporan_produksi` WHERE `tanggal_lp` = CURRENT_DATE GROUP BY `jenis_bulu_lp`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                if (rs.getString("jenis_bulu_lp").contains("BR")) {
                    total_gram_br = total_gram_br + rs.getInt("gram");
                } else if (rs.getString("jenis_bulu_lp").contains("BS")) {
                    total_gram_bs = total_gram_bs + rs.getInt("gram");
                } else if (rs.getString("jenis_bulu_lp").contains("BB")) {
                    total_gram_bb = total_gram_bb + rs.getInt("gram");
                }
            }
            total_gram_br_bs_bb = total_gram_br + total_gram_bs + total_gram_bb;
            float persen_br = (total_gram_br / total_gram_br_bs_bb) * 100f;
            float persen_bs = (total_gram_bs / total_gram_br_bs_bb) * 100f;
            float persen_bb = (total_gram_bb / total_gram_br_bs_bb) * 100f;
            label_total_br.setText("BR : " + decimalFormat.format(total_gram_br) + " (" + decimalFormat.format(persen_br) + "%)");
            label_total_bs.setText("BS : " + decimalFormat.format(total_gram_bs) + " (" + decimalFormat.format(persen_bs) + "%)");
            label_total_bb.setText("BB : " + decimalFormat.format(total_gram_bb) + " (" + decimalFormat.format(persen_bb) + "%)");
            myText = myText + "*Berdasarkan Jenis Bulu Upah (Gram)*\n";
            myText = myText + label_total_br.getText() + "\n";
            myText = myText + label_total_bs.getText() + "\n";
            myText = myText + label_total_bb.getText() + "\n";
            myText = myText + "\n";

            //Berdasarkan tujuan
            float total_gram_utuh = 0, total_gram_flat = 0, total_gram_jidun = 0, total_gram_lain2 = 0, total_gram_kategori = 0;
            sql = "SELECT `tb_grade_bahan_baku`.`kategori`, SUM(`berat_basah`) AS 'gram'\n"
                    + "FROM `tb_laporan_produksi` \n"
                    + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`\n"
                    + "WHERE `tanggal_lp` = CURRENT_DATE \n"
                    + "GROUP BY `tb_grade_bahan_baku`.`kategori`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                if (rs.getString("kategori").contains("MK A") || rs.getString("kategori").contains("MK B") || rs.getString("kategori").contains("OVL") || rs.getString("kategori").contains("SGTG")) {
                    total_gram_utuh = total_gram_utuh + rs.getInt("gram");
                } else if (rs.getString("kategori").contains("FLAT")) {
                    total_gram_flat = total_gram_flat + rs.getInt("gram");
                } else if (rs.getString("kategori").contains("JDN")) {
                    total_gram_jidun = total_gram_jidun + rs.getInt("gram");
                } else {
                    total_gram_lain2 = total_gram_lain2 + rs.getInt("gram");
                }
            }
            total_gram_kategori = total_gram_utuh + total_gram_flat + total_gram_jidun + total_gram_lain2;
            float persen_utuh = (total_gram_utuh / total_gram_kategori) * 100f;
            float persen_flat = (total_gram_flat / total_gram_kategori) * 100f;
            float persen_jidun = (total_gram_jidun / total_gram_kategori) * 100f;
            float persen_lain = (total_gram_lain2 / total_gram_kategori) * 100f;
            label_persen_utuh.setText("UTUH : " + decimalFormat.format(persen_utuh) + "% (MK, OVL, SGTG)");
            label_persen_flat.setText("FLAT : " + decimalFormat.format(persen_flat) + "% (FLAT)");
            label_persen_jdn.setText("JDN : " + decimalFormat.format(persen_jidun) + "% (JDN)");
            label_persen_lain2.setText("LAIN2 : " + decimalFormat.format(persen_lain) + "%");
            myText = myText + "*Berdasarkan Tujuan (Gram) Kategori grade baku*\n";
            myText = myText + label_persen_utuh.getText() + "\n";
            myText = myText + label_persen_flat.getText() + "\n";
            myText = myText + label_persen_jdn.getText() + "\n";
            myText = myText + label_persen_lain2.getText() + "\n";
            myText = myText + "\n";

            //Berdasarkan MEMO JD
            float total_jdn_waleta = 0, total_jdn_sub = 0;
            sql = "SELECT `no_laporan_produksi`, `berat_basah` FROM `tb_laporan_produksi` WHERE `tanggal_lp` = CURRENT_DATE AND `memo_lp` LIKE '%JD%'";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                if (rs.getString("no_laporan_produksi").contains("WL-")) {
                    total_jdn_waleta = total_jdn_waleta + rs.getInt("berat_basah");
                } else if (rs.getString("no_laporan_produksi").contains("WL.")) {
                    total_jdn_sub = total_jdn_sub + rs.getInt("berat_basah");
                } else {
                }
            }
            label_total_jdn_waleta.setText("WALETA : " + decimalFormat.format(total_jdn_waleta) + "");
            label_total_jdn_sub.setText("SUB : " + decimalFormat.format(total_jdn_sub) + "");
            myText = myText + "*Total Bahan Jidun (Gram) memo LP mengandung JD*\n";
            myText = myText + label_total_jdn_waleta.getText() + "\n";
            myText = myText + label_total_jdn_sub.getText() + "\n";
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_total_cucian_hari_ini.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        label_tgl_cuci = new javax.swing.JLabel();
        label_tgl_cabut = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_cucian_hari_ini = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        label_total_waleta = new javax.swing.JLabel();
        label_total_sub = new javax.swing.JLabel();
        label_total_waleta_sub = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        label_total_br = new javax.swing.JLabel();
        label_total_bs = new javax.swing.JLabel();
        label_total_bb = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        label_persen_utuh = new javax.swing.JLabel();
        label_persen_flat = new javax.swing.JLabel();
        label_persen_jdn = new javax.swing.JLabel();
        label_persen_lain2 = new javax.swing.JLabel();
        label_total_jdn_waleta = new javax.swing.JLabel();
        label_total_jdn_sub = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        button_copy_text = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel1.setText("Total Cucian Hari ini");

        label_tgl_cuci.setBackground(new java.awt.Color(255, 255, 255));
        label_tgl_cuci.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_tgl_cuci.setText("Tgl Cuci : Hari, dd/mm/yyyy (tgl lp)");

        label_tgl_cabut.setBackground(new java.awt.Color(255, 255, 255));
        label_tgl_cabut.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_tgl_cabut.setText("Tgl Cabut :  Hari, dd/mm/yyyy");

        tabel_cucian_hari_ini.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        tabel_cucian_hari_ini.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Ruang", "KPG", "GRAM"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class
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
        jScrollPane1.setViewportView(tabel_cucian_hari_ini);

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel4.setText("TOTAL (KPG - GRAM)");

        label_total_waleta.setBackground(new java.awt.Color(255, 255, 255));
        label_total_waleta.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_waleta.setText("WALETA : KPG - GRAM");

        label_total_sub.setBackground(new java.awt.Color(255, 255, 255));
        label_total_sub.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_sub.setText("SUB : KPG - GRAM");

        label_total_waleta_sub.setBackground(new java.awt.Color(255, 255, 255));
        label_total_waleta_sub.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_waleta_sub.setText("WALETA + SUB = KPG - GRAM");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel8.setText("Berdasarkan Jenis Bulu Upah (Gram)");

        label_total_br.setBackground(new java.awt.Color(255, 255, 255));
        label_total_br.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_br.setText("BR gram (%)");

        label_total_bs.setBackground(new java.awt.Color(255, 255, 255));
        label_total_bs.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_bs.setText("BS gram (%)");

        label_total_bb.setBackground(new java.awt.Color(255, 255, 255));
        label_total_bb.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_bb.setText("BB gram (%)");

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel12.setText("Berdasarkan Tujuan (Gram) Kategori grade baku");

        label_persen_utuh.setBackground(new java.awt.Color(255, 255, 255));
        label_persen_utuh.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_persen_utuh.setText("Utuh : % (MK, OVL, SGTG)");

        label_persen_flat.setBackground(new java.awt.Color(255, 255, 255));
        label_persen_flat.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_persen_flat.setText("Flat : %");

        label_persen_jdn.setBackground(new java.awt.Color(255, 255, 255));
        label_persen_jdn.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_persen_jdn.setText("JDN : %");

        label_persen_lain2.setBackground(new java.awt.Color(255, 255, 255));
        label_persen_lain2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_persen_lain2.setText("Lain : %");

        label_total_jdn_waleta.setBackground(new java.awt.Color(255, 255, 255));
        label_total_jdn_waleta.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_jdn_waleta.setText("WALETA : gram");

        label_total_jdn_sub.setBackground(new java.awt.Color(255, 255, 255));
        label_total_jdn_sub.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_jdn_sub.setText("SUB : gram");

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel19.setText("Total Bahan Jidun (Gram) memo LP mengandung JD");

        button_copy_text.setBackground(new java.awt.Color(255, 255, 255));
        button_copy_text.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_copy_text.setText("Copy text");
        button_copy_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_copy_textActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_copy_text))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(label_total_waleta)
                                    .addComponent(label_total_sub)
                                    .addComponent(label_total_waleta_sub)
                                    .addComponent(jLabel8)
                                    .addComponent(label_total_br)
                                    .addComponent(label_total_bs)
                                    .addComponent(label_total_bb)
                                    .addComponent(jLabel12)
                                    .addComponent(label_persen_utuh)
                                    .addComponent(label_persen_flat)
                                    .addComponent(label_persen_jdn)
                                    .addComponent(label_persen_lain2)
                                    .addComponent(jLabel19)
                                    .addComponent(label_total_jdn_waleta)
                                    .addComponent(label_total_jdn_sub)))
                            .addComponent(label_tgl_cuci)
                            .addComponent(label_tgl_cabut))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(button_copy_text))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_tgl_cuci)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_tgl_cabut)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_waleta)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_sub)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_waleta_sub)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_br)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_bs)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_bb)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_persen_utuh)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_persen_flat)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_persen_jdn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_persen_lain2)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_jdn_waleta)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_jdn_sub))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
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

    private void button_copy_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_copy_textActionPerformed
        // TODO add your handling code here:
        StringSelection stringSelection = new StringSelection(myText);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
        JOptionPane.showMessageDialog(this, "Text copied to clipboard!");
    }//GEN-LAST:event_button_copy_textActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JDialog_total_cucian_hari_ini.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JDialog_total_cucian_hari_ini.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JDialog_total_cucian_hari_ini.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JDialog_total_cucian_hari_ini.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JDialog_total_cucian_hari_ini dialog = new JDialog_total_cucian_hari_ini(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton button_copy_text;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_persen_flat;
    private javax.swing.JLabel label_persen_jdn;
    private javax.swing.JLabel label_persen_lain2;
    private javax.swing.JLabel label_persen_utuh;
    private javax.swing.JLabel label_tgl_cabut;
    private javax.swing.JLabel label_tgl_cuci;
    private javax.swing.JLabel label_total_bb;
    private javax.swing.JLabel label_total_br;
    private javax.swing.JLabel label_total_bs;
    private javax.swing.JLabel label_total_jdn_sub;
    private javax.swing.JLabel label_total_jdn_waleta;
    private javax.swing.JLabel label_total_sub;
    private javax.swing.JLabel label_total_waleta;
    private javax.swing.JLabel label_total_waleta_sub;
    private javax.swing.JTable tabel_cucian_hari_ini;
    // End of variables declaration//GEN-END:variables
}
