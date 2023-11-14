package waleta_system.HRD;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.table.DefaultTableModel;
import static org.apache.xmlbeans.impl.util.HexBin.bytesToString;
import waleta_system.Class.ACR122U_ReaderHelper;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;

public class JFrame_ScanIjinToilet extends javax.swing.JFrame {

    String sql;
    ResultSet rs;
    ACR122U_ReaderHelper reader;

    public JFrame_ScanIjinToilet() {
        initComponents();
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        try {
            
        } catch (Exception e) {
        }
        refreshTable();

        Thread thread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        reader = ACR122U_ReaderHelper.getInstance();
                        if (reader.connectReader()) {
                            label_reader_status.setText("NFC Reader Ready !");
                            label_reader_status.setForeground(new Color(0, 220, 0));//dark green
                            reader.waitForCardPresent(0);
                            reader.connectCard(null);
                            byte[] byteUID = reader.getUID();
                            PasswordField.setText(bytesToString(reader.getUID()).substring(0, 8) + "\n");
                            ENTER();
                            reader.waitForCardAbsent(0);
                        } else {
                            reader.clearReaderList();
                            label_reader_status.setText("NFC Reader is not Ready !");
                            label_reader_status.setForeground(Color.red);//dark blue
                            Thread.sleep(2000);
                        }
                    } catch (Exception e) {
//                        Logger.getLogger(JFrame_ScanIjinToilet.class.getName()).log(Level.SEVERE, null, e);
                    }
                }
            }
        };
        thread.start();
    }

    public void refreshTable() {
        try {
            DefaultTableModel model_kiri = (DefaultTableModel) Table_ijin_toilet.getModel();
            model_kiri.setRowCount(0);
            DefaultTableModel model_kanan = (DefaultTableModel) Table_Belum_Kembali.getModel();
            model_kanan.setRowCount(0);
            sql = "SELECT `no`, `tb_ijin_keluar_ruangan`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `nama_bagian`, `waktu_ijin`, `waktu_kembali` \n"
                    + "FROM `tb_ijin_keluar_ruangan` \n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_ijin_keluar_ruangan`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                    + "WHERE DATE(`waktu_ijin`) = CURRENT_DATE ORDER BY `waktu_ijin` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getString("nama_pegawai");
                row[1] = rs.getString("nama_bagian");
                row[2] = rs.getTimestamp("waktu_ijin");
                row[3] = rs.getTimestamp("waktu_kembali");
                model_kiri.addRow(row);
                if (rs.getTimestamp("waktu_kembali") == null) {
                    model_kanan.addRow(row);
                }
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_ijin_toilet);
            label_total_data_ijin.setText(Integer.toString(Table_ijin_toilet.getRowCount()));
            ColumnsAutoSizer.sizeColumnsToFit(Table_Belum_Kembali);
            label_total_data_belum_kembali.setText(Integer.toString(Table_Belum_Kembali.getRowCount()));
        } catch (SQLException ex) {
            Logger.getLogger(JFrame_ScanIjinToilet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void getFoto(String id) {
        try {
            sql = "SELECT `id_pegawai`, `nama_pegawai`, `tb_bagian`.`nama_bagian` "
                    + "FROM `tb_karyawan` LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`"
                    + "WHERE `id_pegawai`='" + id + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                label_id.setText(id);
                label_nama.setText(rs.getString("nama_pegawai"));
                label_bagian.setText(rs.getString("nama_bagian"));
                lbl_Image.setIcon(Utility.ResizeImage("\\\\192.168.10.2\\Shared Folder\\DOKUMEN SISTEM\\FOTO_ID_CARD\\" + id + ".JPG", null, lbl_Image.getWidth(), lbl_Image.getHeight()));
            }
        } catch (SQLException ex) {
            Logger.getLogger(JFrame_ScanIjinToilet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Insert(String id) {
        try {
            sql = "SELECT * FROM `tb_ijin_keluar_ruangan` WHERE `id_pegawai`='" + id + "' AND DATE(`waktu_ijin`) = CURRENT_DATE AND `waktu_kembali` IS NULL";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                String Query = "UPDATE `tb_ijin_keluar_ruangan` SET `waktu_kembali`=NOW() WHERE `no`='" + rs.getString("no") + "' ";
                if (Utility.db.getStatement().executeUpdate(Query) == 1) {
                    getFoto(id);
                    PasswordField.setText(null);
                    PasswordField.requestFocus();
                    label_status.setText("Selesai dari Toilet");
                    label_status.setForeground(new Color(0, 0, 220));//dark blue
                }
            } else {
                String Query = "INSERT INTO `tb_ijin_keluar_ruangan` (`id_pegawai`, `waktu_ijin`, `keterangan_ijin`) VALUES ('" + id + "', NOW(), 'Toilet')";
                if (Utility.db.getStatement().executeUpdate(Query) == 1) {
                    getFoto(id);
                    PasswordField.setText(null);
                    PasswordField.requestFocus();
                    label_status.setText("Masuk Toilet");
                    label_status.setForeground(new Color(0, 220, 0));//dark green
                }
            }
            refreshTable();
        } catch (SQLException ex) {
            Logger.getLogger(JFrame_ScanIjinToilet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void ENTER() {
        try {
            sql = "SELECT `id_pegawai` FROM `tb_karyawan` WHERE `uid_card` = '" + PasswordField.getText() + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                Insert(rs.getString("id_pegawai"));
            } else {
                PasswordField.setText(null);
                PasswordField.requestFocus();
                label_status.setText("ID card belum terdaftar!!");
                label_status.setForeground(Color.red);
            }
        } catch (SQLException ex) {
            Logger.getLogger(JFrame_ScanIjinToilet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        PasswordField = new javax.swing.JPasswordField();
        lbl_Image = new javax.swing.JLabel();
        label_id = new javax.swing.JLabel();
        label_nama = new javax.swing.JLabel();
        Jlabel3 = new javax.swing.JLabel();
        Jlabel2 = new javax.swing.JLabel();
        Jlabel4 = new javax.swing.JLabel();
        label_bagian = new javax.swing.JLabel();
        label_status = new javax.swing.JLabel();
        label_reader_status = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_ijin_toilet = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        Table_Belum_Kembali = new javax.swing.JTable();
        Jlabel5 = new javax.swing.JLabel();
        label_total_data_ijin = new javax.swing.JLabel();
        Jlabel6 = new javax.swing.JLabel();
        label_total_data_belum_kembali = new javax.swing.JLabel();
        Jlabel7 = new javax.swing.JLabel();
        Jlabel8 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Scan Ijin ke Toilet");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Scan Your ID Card", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 18))); // NOI18N

        PasswordField.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        PasswordField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PasswordFieldKeyPressed(evt);
            }
        });

        lbl_Image.setBackground(new java.awt.Color(255, 255, 255));
        lbl_Image.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_Image.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_id.setBackground(new java.awt.Color(255, 255, 255));
        label_id.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_id.setText("ID");
        label_id.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        label_id.setMaximumSize(new java.awt.Dimension(280, 15));

        label_nama.setBackground(new java.awt.Color(255, 255, 255));
        label_nama.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_nama.setText("Nama");
        label_nama.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        label_nama.setMaximumSize(new java.awt.Dimension(280, 15));

        Jlabel3.setBackground(new java.awt.Color(255, 255, 255));
        Jlabel3.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        Jlabel3.setText("ID Pegawai :");
        Jlabel3.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        Jlabel3.setMaximumSize(new java.awt.Dimension(280, 15));

        Jlabel2.setBackground(new java.awt.Color(255, 255, 255));
        Jlabel2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        Jlabel2.setText("Nama : ");
        Jlabel2.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        Jlabel2.setMaximumSize(new java.awt.Dimension(280, 15));

        Jlabel4.setBackground(new java.awt.Color(255, 255, 255));
        Jlabel4.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        Jlabel4.setText("Bagian :");
        Jlabel4.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        Jlabel4.setMaximumSize(new java.awt.Dimension(280, 15));

        label_bagian.setBackground(new java.awt.Color(255, 255, 255));
        label_bagian.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_bagian.setText("Bagian");
        label_bagian.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        label_bagian.setMaximumSize(new java.awt.Dimension(280, 15));

        label_status.setBackground(new java.awt.Color(255, 255, 255));
        label_status.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_status.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_status.setText("--");
        label_status.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        label_status.setMaximumSize(new java.awt.Dimension(280, 15));

        label_reader_status.setBackground(new java.awt.Color(255, 255, 255));
        label_reader_status.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_reader_status.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_reader_status.setText("--");
        label_reader_status.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        label_reader_status.setMaximumSize(new java.awt.Dimension(280, 15));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PasswordField)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lbl_Image, javax.swing.GroupLayout.PREFERRED_SIZE, 323, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(Jlabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(Jlabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_nama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(Jlabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(label_status, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(label_reader_status, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(label_reader_status, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Jlabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Jlabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_nama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Jlabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_status, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_Image, javax.swing.GroupLayout.PREFERRED_SIZE, 534, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        Table_ijin_toilet.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        Table_ijin_toilet.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nama", "Bagian", "Waktu ke Toilet", "Waktu Kembali"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_ijin_toilet.setRowHeight(25);
        jScrollPane1.setViewportView(Table_ijin_toilet);

        Table_Belum_Kembali.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        Table_Belum_Kembali.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nama", "Bagian", "Jam Scan", "Jam Kembali"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_Belum_Kembali.setRowHeight(25);
        jScrollPane2.setViewportView(Table_Belum_Kembali);

        Jlabel5.setBackground(new java.awt.Color(255, 255, 255));
        Jlabel5.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        Jlabel5.setText("Total Data :");
        Jlabel5.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        Jlabel5.setMaximumSize(new java.awt.Dimension(280, 15));

        label_total_data_ijin.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_ijin.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_total_data_ijin.setText("0");
        label_total_data_ijin.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        label_total_data_ijin.setMaximumSize(new java.awt.Dimension(280, 15));

        Jlabel6.setBackground(new java.awt.Color(255, 255, 255));
        Jlabel6.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        Jlabel6.setText("Data Belum Kembali");
        Jlabel6.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        Jlabel6.setMaximumSize(new java.awt.Dimension(280, 15));

        label_total_data_belum_kembali.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_belum_kembali.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_total_data_belum_kembali.setText("0");
        label_total_data_belum_kembali.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        label_total_data_belum_kembali.setMaximumSize(new java.awt.Dimension(280, 15));

        Jlabel7.setBackground(new java.awt.Color(255, 255, 255));
        Jlabel7.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        Jlabel7.setText("Total Data :");
        Jlabel7.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        Jlabel7.setMaximumSize(new java.awt.Dimension(280, 15));

        Jlabel8.setBackground(new java.awt.Color(255, 255, 255));
        Jlabel8.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        Jlabel8.setText("Data hari ini");
        Jlabel8.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        Jlabel8.setMaximumSize(new java.awt.Dimension(280, 15));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 497, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(Jlabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Jlabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data_ijin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(Jlabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Jlabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data_belum_kembali, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Jlabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_data_ijin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Jlabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 690, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(Jlabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(label_total_data_belum_kembali, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(Jlabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2))
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
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

    private void PasswordFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PasswordFieldKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            ENTER();
        }
    }//GEN-LAST:event_PasswordFieldKeyPressed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JFrame_ScanIjinToilet.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFrame_ScanIjinToilet.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFrame_ScanIjinToilet.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrame_ScanIjinToilet.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JFrame_ScanIjinToilet().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Jlabel2;
    private javax.swing.JLabel Jlabel3;
    private javax.swing.JLabel Jlabel4;
    private javax.swing.JLabel Jlabel5;
    private javax.swing.JLabel Jlabel6;
    private javax.swing.JLabel Jlabel7;
    private javax.swing.JLabel Jlabel8;
    private javax.swing.JPasswordField PasswordField;
    private javax.swing.JTable Table_Belum_Kembali;
    private javax.swing.JTable Table_ijin_toilet;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel label_bagian;
    private javax.swing.JLabel label_id;
    private javax.swing.JLabel label_nama;
    private javax.swing.JLabel label_reader_status;
    private javax.swing.JLabel label_status;
    private javax.swing.JLabel label_total_data_belum_kembali;
    private javax.swing.JLabel label_total_data_ijin;
    private javax.swing.JLabel lbl_Image;
    // End of variables declaration//GEN-END:variables
}
