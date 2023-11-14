package waleta_system.HRD;

import waleta_system.Class.Utility;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import waleta_system.Class.ColumnsAutoSizer;

public class JPanel_Data_KaryawanLama_Masuk extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public JPanel_Data_KaryawanLama_Masuk() {
        initComponents();
    }

    public void init() {
        refreshTabel_karyawan();
        String a = new SimpleDateFormat("dd MMMM yyyy").format(new Date());
    }

    public void refreshTabel_karyawan() {
        try {
            if (Date1.getDate() != null && Date2.getDate() != null) {
//                HashMap<Integer, String> absen_map = new HashMap<>();
//                sql = "SELECT `kode_bagian`, `nama_bagian`, `kode_departemen`, `grup`, `status_bagian` FROM `tb_bagian` WHERE 1";
//                rs = Utility.db.getStatement().executeQuery(sql);
//                while (rs.next()) {
//                    absen_map.put(rs.getInt("kode_bagian"), rs.getString("nama_bagian"));
//                }

                DefaultTableModel model = (DefaultTableModel) table_karyawan.getModel();
                model.setRowCount(0);
                sql = "SELECT `id_pegawai`, `nik_ktp`, `nama_pegawai`, A.`kode_bagian`, `status`, `tanggal_masuk`, `level_gaji`, `tb_bagian`.`nama_bagian`, \n"
                        + "(SELECT `id_pegawai` FROM `tb_karyawan` WHERE `nik_ktp` = A.`nik_ktp` AND `tanggal_keluar` IS NOT NULL\n"
                        + "ORDER BY `tanggal_keluar` DESC LIMIT 1) AS 'id_lama', \n"
                        + "(SELECT `tanggal_masuk` FROM `tb_karyawan` WHERE `nik_ktp` = A.`nik_ktp` AND `tanggal_keluar` IS NOT NULL\n"
                        + "ORDER BY `tanggal_keluar` DESC LIMIT 1) AS 'tanggal_masuk_lama', \n"
                        + "(SELECT `tanggal_keluar` FROM `tb_karyawan` WHERE `nik_ktp` = A.`nik_ktp` AND `tanggal_keluar` IS NOT NULL\n"
                        + "ORDER BY `tanggal_keluar` DESC LIMIT 1) AS 'tanggal_keluar_lama', \n"
                        + "(SELECT `level_gaji` FROM `tb_karyawan` WHERE `nik_ktp` = A.`nik_ktp` AND `tanggal_keluar` IS NOT NULL\n"
                        + "ORDER BY `tanggal_keluar` DESC LIMIT 1) AS 'level_gaji_lama',\n"
                        + "(SELECT `nama_bagian` FROM `tb_karyawan` LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian` WHERE `nik_ktp` = A.`nik_ktp` AND `tanggal_keluar` IS NOT NULL\n"
                        + "ORDER BY `tanggal_keluar` DESC LIMIT 1) AS 'bagian_lama'\n"
                        + "FROM `tb_karyawan` A "
                        + "LEFT JOIN `tb_bagian` ON A.`kode_bagian` = `tb_bagian`.`kode_bagian`"
                        + "WHERE `id_pegawai` LIKE '%" + txt_search_id.getText() + "%' AND `nama_pegawai` LIKE '%" + txt_search_nama.getText() + "%' "
                        + "AND (`tanggal_masuk` BETWEEN '" + dateFormat.format(Date1.getDate()) + "' AND '" + dateFormat.format(Date2.getDate()) + "') \n"
                        + "HAVING `id_lama` IS NOT NULL\n"
                        + "ORDER BY A.`nama_pegawai`";
                PreparedStatement pst = Utility.db.getConnection().prepareStatement(sql);
                rs = pst.executeQuery();
                Object[] row = new Object[15];
                int no = 0;
                while (rs.next()) {
                    Date masuk = rs.getDate("tanggal_masuk_lama");
                    Date keluar = rs.getDate("tanggal_keluar_lama");
                    Date masuk_baru = rs.getDate("tanggal_masuk");
                    long lama_bekerja = Math.abs(keluar.getTime() - masuk.getTime());
                    long jeda_waktu = Math.abs(masuk_baru.getTime() - keluar.getTime());
                    no++;
                    row[0] = no;
                    row[1] = rs.getString("id_pegawai");
                    row[2] = rs.getString("nik_ktp");
                    row[3] = rs.getString("nama_pegawai");
                    row[4] = rs.getString("nama_bagian");
                    row[5] = rs.getString("status");
                    row[6] = rs.getString("tanggal_masuk");
                    row[7] = rs.getString("level_gaji");
                    row[8] = rs.getString("id_lama");
                    row[9] = TimeUnit.MILLISECONDS.toDays(lama_bekerja) / 365 + "Th " + (TimeUnit.MILLISECONDS.toDays(lama_bekerja) % 365) / 30 + "Bln " + (TimeUnit.MILLISECONDS.toDays(lama_bekerja) % 365) % 30 + "Hr";
                    row[10] = rs.getString("tanggal_keluar_lama");
                    row[11] = rs.getString("bagian_lama");
                    row[12] = rs.getString("level_gaji_lama");
                    row[13] = TimeUnit.MILLISECONDS.toDays(jeda_waktu) / 365 + "Th " + (TimeUnit.MILLISECONDS.toDays(jeda_waktu) % 365) / 30 + "Bln " + (TimeUnit.MILLISECONDS.toDays(jeda_waktu) % 365) % 30 + "Hr";
                    model.addRow(row);
                }
                ColumnsAutoSizer.sizeColumnsToFit(table_karyawan);
                label_total.setText(Integer.toString(table_karyawan.getRowCount()));
            } else {
                JOptionPane.showMessageDialog(this, "Harap pilih tanggal masuk yang di inginkan !");
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Data_KaryawanLama_Masuk.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jPanel_search_karyawan = new javax.swing.JPanel();
        txt_search_nama = new javax.swing.JTextField();
        button_search = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        txt_search_id = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        label_total = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        Date1 = new com.toedter.calendar.JDateChooser();
        Date2 = new com.toedter.calendar.JDateChooser();
        button_form = new javax.swing.JButton();
        button_input_pindah_bagian = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        table_karyawan = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jPanel_search_karyawan.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_search_karyawan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        txt_search_nama.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_nama.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_namaKeyPressed(evt);
            }
        });

        button_search.setBackground(new java.awt.Color(255, 255, 255));
        button_search.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search.setText("Search");
        button_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_searchActionPerformed(evt);
            }
        });

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Nama :");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel15.setText("ID :");

        txt_search_id.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_id.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_idKeyPressed(evt);
            }
        });

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel20.setText("Total :");

        label_total.setBackground(new java.awt.Color(255, 255, 255));
        label_total.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total.setText("0");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Tanggal Masuk :");

        Date1.setBackground(new java.awt.Color(255, 255, 255));
        Date1.setDate(new Date());
        Date1.setDateFormatString("dd MMM yyyy");
        Date1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date2.setBackground(new java.awt.Color(255, 255, 255));
        Date2.setDate(new Date());
        Date2.setDateFormatString("dd MMM yyyy");
        Date2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_form.setBackground(new java.awt.Color(255, 255, 255));
        button_form.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_form.setText("Print Form");
        button_form.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_formActionPerformed(evt);
            }
        });

        button_input_pindah_bagian.setBackground(new java.awt.Color(255, 255, 255));
        button_input_pindah_bagian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_input_pindah_bagian.setText("Input pindah bagian");
        button_input_pindah_bagian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_pindah_bagianActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_search_karyawanLayout = new javax.swing.GroupLayout(jPanel_search_karyawan);
        jPanel_search_karyawan.setLayout(jPanel_search_karyawanLayout);
        jPanel_search_karyawanLayout.setHorizontalGroup(
            jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_karyawanLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_search_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_search_id, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_search)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_form)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_total)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(button_input_pindah_bagian)
                .addContainerGap())
        );
        jPanel_search_karyawanLayout.setVerticalGroup(
            jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_karyawanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_search_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_id, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_form, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_input_pindah_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        table_karyawan.setAutoCreateRowSorter(true);
        table_karyawan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        table_karyawan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "ID Baru", "NIK", "Nama", "Bagian Baru", "Status Baru", "Tgl Masuk", "Level Gaji Baru", "ID Lama", "Masa Kerja Lama", "Tanggal Keluar", "Bagian Lama", "Level Gaji Lama", "Jeda waktu"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_karyawan.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(table_karyawan);

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel2.setText("Data Karyawan Lama Masuk Kembali");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1343, Short.MAX_VALUE)
                    .addComponent(jPanel_search_karyawan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel_search_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 632, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(2, 2, 2))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txt_search_namaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_namaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTabel_karyawan();
        }
    }//GEN-LAST:event_txt_search_namaKeyPressed

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        refreshTabel_karyawan();
    }//GEN-LAST:event_button_searchActionPerformed

    private void txt_search_idKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_idKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTabel_karyawan();
        }
    }//GEN-LAST:event_txt_search_idKeyPressed

    private void button_formActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_formActionPerformed
        // TODO add your handling code here:
        if (Date1.getDate() != null && Date2.getDate() != null) {
            try {
                JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Form_anak_lama_masuk_lagi.jrxml");
                JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("SUBREPORT_DIR", "Report\\");
                params.put("ID", "%" + txt_search_id.getText() + "%");
                params.put("NAMA", "%" + txt_search_nama.getText() + "%");
                params.put("TGL1", dateFormat.format(Date1.getDate()));
                params.put("TGL2", dateFormat.format(Date2.getDate()));
                JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, params, Utility.db.getConnection());
                JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
            } catch (JRException ex) {
                Logger.getLogger(JPanel_Data_Karyawan.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Harap pilih tanggal masuk yang di inginkan !");
        }
    }//GEN-LAST:event_button_formActionPerformed

    private void button_input_pindah_bagianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_pindah_bagianActionPerformed
        // TODO add your handling code here:
        int i = table_karyawan.getSelectedRow();
        if (i >= 0) {
            try {
                String grup_lama = "NULL", bagian_lama = "NULL", jam_kerja_lama = "NULL", level_gaji_lama = "NULL";
                String query = "SELECT `level_gaji`, `jam_kerja`, `tb_bagian`.`nama_bagian`, `kode_grup`\n"
                        + "FROM `tb_karyawan` "
                        + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                        + "LEFT JOIN `tb_grup_cabut` ON `tb_karyawan`.`id_pegawai` = `tb_grup_cabut`.`id_pegawai`\n"
                        + "WHERE `tb_karyawan`.`id_pegawai` = '" + table_karyawan.getValueAt(i, 1).toString() + "'";
                rs = Utility.db.getStatement().executeQuery(query);
                if (rs.next()) {
                    if (rs.getString("kode_grup") != null) {
                        grup_lama = "'" + rs.getString("kode_grup") + "'";
                    }

                    if (rs.getString("nama_bagian") != null) {
                        bagian_lama = "'" + rs.getString("nama_bagian") + "'";
                    }

                    if (rs.getString("jam_kerja") != null) {
                        jam_kerja_lama = "'" + rs.getString("jam_kerja") + "'";
                    }

                    if (rs.getString("level_gaji") != null) {
                        level_gaji_lama = "'" + rs.getString("level_gaji") + "'";
                    }
                }
                String Query = "INSERT INTO `tb_form_pindah_grup`(`tanggal_input`, `id_pegawai`, `grup_lama`, `bagian_lama`, `jamkerja_lama`, `levelgaji_lama`, `keterangan`) \n"
                        + "VALUES (NOW(),"
                        + "'" + table_karyawan.getValueAt(i, 1).toString() + "',\n" //id pegawai
                        + "" + grup_lama + ", \n" //grup lama
                        + "" + bagian_lama + ", "//bagian lama
                        + "" + jam_kerja_lama + ",\n" //jam kerja lama
                        + "" + level_gaji_lama + ",\n" //level gaji lama
                        + "'Karyawan lama masuk lagi')"; //keterangan
                Utility.db.getConnection().createStatement();
                if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                    JOptionPane.showMessageDialog(this, "Berhasil input pindah bagian!");
                } else {
                    JOptionPane.showMessageDialog(this, "GAGAL, silahkan lapor ke bagian IT");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex);
                Logger.getLogger(JPanel_Data_KaryawanLama_Masuk.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih salah satu data!");
        }
    }//GEN-LAST:event_button_input_pindah_bagianActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser Date1;
    private com.toedter.calendar.JDateChooser Date2;
    public static javax.swing.JButton button_form;
    public static javax.swing.JButton button_input_pindah_bagian;
    public static javax.swing.JButton button_search;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel_search_karyawan;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel label_total;
    public static javax.swing.JTable table_karyawan;
    private javax.swing.JTextField txt_search_id;
    private javax.swing.JTextField txt_search_nama;
    // End of variables declaration//GEN-END:variables
}
