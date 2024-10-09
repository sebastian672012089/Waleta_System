package waleta_system.SubWaleta;

import waleta_system.Class.Utility;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;
import waleta_system.Interface.InterfacePanel;

public class JPanel_DataCetakSub extends javax.swing.JPanel implements InterfacePanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_DataCetakSub() {
        initComponents();
    }

    @Override
    public void init() {
        try {
            Utility.db_sub.connect();
            ComboBox_ruang.removeAllItems();
            ComboBox_ruang.addItem("All");
            sql = "SELECT `kode_sub` FROM `tb_sub_waleta` WHERE 1";
            rs = Utility.db_sub.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_ruang.addItem(rs.getString("kode_sub"));
            }
        } catch (Exception ex) {
            Logger.getLogger(JPanel_DataCetakSub.class.getName()).log(Level.SEVERE, null, ex);
        }
        refresh_cetak_sub_online();
    }

    public void refresh_cetak_sub_online() {
        try {
            Utility.db_sub.connect();
            decimalFormat.setMaximumFractionDigits(2);
            float tot_mk = 0, tot_pch = 0, tot_flat = 0, total_kpg = 0, tot_jidun = 0, total_gram = 0;
            DefaultTableModel model = (DefaultTableModel) Table_Data_Cetak.getModel();
            model.setRowCount(0);
            String ruang = "AND `tb_laporan_produksi`.`ruangan` = '" + ComboBox_ruang.getSelectedItem().toString() + "'";
            if (ComboBox_ruang.getSelectedItem().toString().equals("All")) {
                ruang = "";
            }
            String filter_tanggal = "";
            if (ComboBox_filterTgl.getSelectedIndex() == 0 && Date1_cetak.getDate() != null && Date2_cetak.getDate() != null) {
                filter_tanggal = " AND (`tb_cetak`.`tgl_mulai_cetak` BETWEEN '" + dateFormat.format(Date1_cetak.getDate()) + "' and '" + dateFormat.format(Date2_cetak.getDate()) + "')";
            } else if (ComboBox_filterTgl.getSelectedIndex() == 1 && Date1_cetak.getDate() != null && Date2_cetak.getDate() != null) {
                filter_tanggal = " AND (`tb_cetak`.`tgl_selesai_cetak` BETWEEN '" + dateFormat.format(Date1_cetak.getDate()) + "' and '" + dateFormat.format(Date2_cetak.getDate()) + "')";
            }
            sql = "SELECT `tb_cetak`.`no_laporan_produksi`, `kode_grade`, `ruangan`, `jumlah_keping`, `berat_basah`, "
                    + "`tgl_mulai_cetak`, `cetak_diterima`, `tgl_selesai_cetak`, `cetak_diserahkan`, "
                    + "`cetak_dikerjakan`, `tb_karyawan`.`nama_pegawai` AS 'pekerja_cetak', `cetak_dikerjakan_grup`, `cetak_dikerjakan_level`, "
                    + "`cetak_dikoreksi`, `cetak_dikoreksi_id`, `cetak_dikoreksi_grup`, `cetak_dikoreksi_level`, "
                    + "`cetak_mangkok`, `cetak_pecah`, `cetak_flat`, `cetak_jidun`, `cetak_jidun_real`, `admin_cetak`, `tgl_jam_input_setor` \n"
                    + "FROM `tb_cetak` \n"
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_cetak`.`cetak_dikerjakan` = `tb_karyawan`.`id_pegawai`\n"
                    + "WHERE `tb_cetak`.`no_laporan_produksi` LIKE '%" + txt_search_cetak.getText() + "%' " + ruang + filter_tanggal
                    + "ORDER BY `tb_cetak`.`tgl_mulai_cetak` DESC";
            PreparedStatement pst = Utility.db_sub.getConnection().prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = pst.executeQuery(sql);
            Object[] row = new Object[25];
            while (rs.next()) {
                float mk = rs.getFloat("cetak_mangkok");
                float pecah = rs.getFloat("cetak_pecah");
                float flat = rs.getFloat("cetak_flat");
                float jidun = rs.getFloat("cetak_jidun");
                float kpg = rs.getFloat("jumlah_keping");
                float gram = rs.getFloat("berat_basah");
                float persen_mk = (mk / kpg) * 100;
                float persen_pecah = (pecah / kpg) * 100;
                float persen_flat = (flat / kpg) * 100;
                float persen_jidun = (jidun / kpg) * 100;
                tot_mk = tot_mk + mk;
                tot_pch = tot_pch + pecah;
                tot_flat = tot_flat + flat;
                tot_jidun = tot_jidun + flat;
                total_kpg = total_kpg + kpg;
                total_gram = total_gram + gram;

                row[0] = rs.getString("no_laporan_produksi");
                row[1] = rs.getString("kode_grade");
                row[2] = rs.getString("ruangan");
                row[3] = rs.getFloat("jumlah_keping");
                row[4] = rs.getFloat("berat_basah");
                row[5] = rs.getDate("tgl_mulai_cetak");
                row[6] = rs.getString("cetak_diterima");
                row[7] = rs.getDate("tgl_selesai_cetak");
                row[8] = rs.getString("cetak_diserahkan");
                row[9] = rs.getString("pekerja_cetak");
                row[10] = rs.getString("cetak_dikoreksi");
                row[11] = rs.getFloat("cetak_mangkok");
                row[12] = persen_mk;
                row[13] = rs.getFloat("cetak_pecah");
                row[14] = persen_pecah;
                row[15] = rs.getFloat("cetak_flat");
                row[16] = persen_flat;
                row[17] = rs.getFloat("cetak_jidun");
                row[18] = persen_jidun;
                row[19] = rs.getFloat("cetak_jidun_real");
                row[20] = rs.getString("admin_cetak");
                model.addRow(row);
            }

            ColumnsAutoSizer.sizeColumnsToFit(Table_Data_Cetak);
            float rata2_mk = (tot_mk / total_kpg) * 100;
            float rata2_pch = (tot_pch / total_kpg) * 100;
            float rata2_flat = (tot_flat / total_kpg) * 100;
            float rata2_jidun = (tot_jidun / total_kpg) * 100;

            int rowData = Table_Data_Cetak.getRowCount();
            label_total_data_cetak.setText(Integer.toString(rowData));
            label_total_mk.setText(decimalFormat.format(tot_mk));
            label_total_mk1.setText("(" + decimalFormat.format(rata2_mk) + "%)");
            label_total_pecah.setText(decimalFormat.format(tot_pch));
            label_total_pecah1.setText("(" + decimalFormat.format(rata2_pch) + "%)");
            label_total_flat.setText(decimalFormat.format(tot_flat));
            label_total_flat1.setText("(" + decimalFormat.format(rata2_flat) + "%)");
            label_total_jidun.setText(decimalFormat.format(tot_jidun));
            label_total_jidun1.setText("(" + decimalFormat.format(rata2_jidun) + "%)");
            label_total_kpg.setText(decimalFormat.format(total_kpg));
            label_total_gram.setText(decimalFormat.format(total_gram));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JPanel_DataCetakSub.class.getName()).log(Level.SEVERE, null, e);
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
        jScrollPane4 = new javax.swing.JScrollPane();
        Table_Data_Cetak = new javax.swing.JTable();
        label_total_data_cetak = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        label_total_mk = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        label_total_pecah = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        label_total_flat = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        label_total_kpg = new javax.swing.JLabel();
        label_total_mk1 = new javax.swing.JLabel();
        label_total_pecah1 = new javax.swing.JLabel();
        label_total_flat1 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        label_total_jidun = new javax.swing.JLabel();
        label_total_jidun1 = new javax.swing.JLabel();
        label_total_gram = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        button_export_f2 = new javax.swing.JButton();
        button_download = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel3 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txt_search_cetak = new javax.swing.JTextField();
        ComboBox_ruang = new javax.swing.JComboBox<>();
        button_search_cetak = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        Date1_cetak = new com.toedter.calendar.JDateChooser();
        Date2_cetak = new com.toedter.calendar.JDateChooser();
        ComboBox_filterTgl = new javax.swing.JComboBox<>();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Cetak", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        Table_Data_Cetak.setAutoCreateRowSorter(true);
        Table_Data_Cetak.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_Data_Cetak.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP", "Grade", "Ruang", "Total Keping", "Gram", "Tgl Masuk", "Diterima", "Tgl Selesai", "Diserahkan", "Pekerja Cetak", "Pengoreksi", "Mk (Kpg)", "Mk (%)", "Pecah (Kpg)", "Pecah (%)", "Flat (Kpg)", "Flat (%)", "Jidun", "Jidun(%)", "Jidun Real", "admin"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Object.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_Data_Cetak.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(Table_Data_Cetak);

        label_total_data_cetak.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_cetak.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_data_cetak.setText("TOTAL");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel6.setText("Total Data :");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel7.setText("MK :");

        label_total_mk.setBackground(new java.awt.Color(255, 255, 255));
        label_total_mk.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_mk.setText("TOTAL");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel8.setText("Pecah :");

        label_total_pecah.setBackground(new java.awt.Color(255, 255, 255));
        label_total_pecah.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_pecah.setText("TOTAL");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel9.setText("Flat :");

        label_total_flat.setBackground(new java.awt.Color(255, 255, 255));
        label_total_flat.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_flat.setText("TOTAL");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel10.setText("Total Keping :");

        label_total_kpg.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_kpg.setText("TOTAL");

        label_total_mk1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_mk1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_mk1.setText("(0%)");

        label_total_pecah1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_pecah1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_pecah1.setText("(0%)");

        label_total_flat1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_flat1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_flat1.setText("(0%)");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel11.setText("Jidun :");

        label_total_jidun.setBackground(new java.awt.Color(255, 255, 255));
        label_total_jidun.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_jidun.setText("TOTAL");

        label_total_jidun1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_jidun1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_jidun1.setText("(0%)");

        label_total_gram.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_gram.setText("TOTAL");

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel12.setText("Total Gram :");

        button_export_f2.setBackground(new java.awt.Color(255, 255, 255));
        button_export_f2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_f2.setText("Export to Excel");
        button_export_f2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_f2ActionPerformed(evt);
            }
        });

        button_download.setBackground(new java.awt.Color(255, 255, 255));
        button_download.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_download.setText("Download data to Waleta");
        button_download.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_downloadActionPerformed(evt);
            }
        });

        jProgressBar1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Note : yang di download hanya data yang sudah di setorkan");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Ruang :");

        txt_search_cetak.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_cetak.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_cetakKeyPressed(evt);
            }
        });

        ComboBox_ruang.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_ruang.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "A", "B", "C", "D" }));

        button_search_cetak.setBackground(new java.awt.Color(255, 255, 255));
        button_search_cetak.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_cetak.setText("Search");
        button_search_cetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_cetakActionPerformed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Laporan Produksi :");

        Date1_cetak.setBackground(new java.awt.Color(255, 255, 255));
        Date1_cetak.setDate(new Date(new Date().getTime()- (7 * 24 * 60 * 60 * 1000)));
        Date1_cetak.setDateFormatString("dd MMMM yyyy");
        Date1_cetak.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date2_cetak.setBackground(new java.awt.Color(255, 255, 255));
        Date2_cetak.setDate(new Date());
        Date2_cetak.setDateFormatString("dd MMMM yyyy");
        Date2_cetak.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        ComboBox_filterTgl.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_filterTgl.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tanggal Masuk", "Tanggal Setor" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data_cetak)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_kpg)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_mk)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_mk1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_pecah)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_pecah1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_flat)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_flat1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_jidun)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_jidun1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_ruang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_filterTgl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date1_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date2_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(button_search_cetak)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_f2))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(button_download)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)))
                        .addGap(0, 409, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_ruang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_filterTgl, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date1_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date2_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_f2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(button_download, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 558, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_data_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_mk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_mk1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_pecah, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_pecah1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_flat, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_flat1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_kpg, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_jidun, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_jidun1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
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
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txt_search_cetakKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_cetakKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refresh_cetak_sub_online();
        }
    }//GEN-LAST:event_txt_search_cetakKeyPressed

    private void button_search_cetakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_cetakActionPerformed
        // TODO add your handling code here:
        refresh_cetak_sub_online();
    }//GEN-LAST:event_button_search_cetakActionPerformed

    private void button_export_f2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_f2ActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_Data_Cetak.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_export_f2ActionPerformed

    private void button_downloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_downloadActionPerformed
        // TODO add your handling code here:
        int dialogResult = JOptionPane.showConfirmDialog(this, "Download data sub online into waleta database?", "Warning", 0);
        if (dialogResult == JOptionPane.YES_OPTION) {
            try {
                refresh_cetak_sub_online();
                int panjang = Table_Data_Cetak.getRowCount();
                jProgressBar1.setMinimum(0);
                jProgressBar1.setMaximum(panjang);
                jProgressBar1.setValue(0);
                jProgressBar1.setStringPainted(true);
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            int jumlah_berhasil = 0, jumlah_gagal = 0;
                            rs.beforeFirst();
                            while (rs.next()) {
                                if (rs.getDate("tgl_selesai_cetak") != null) {
                                    String pekerja_cetak = "NULL";
                                    if (rs.getString("cetak_dikerjakan").substring(0, 2).equals("20")) {
                                        pekerja_cetak = "'" + rs.getString("cetak_dikerjakan") + "'";
                                    }
                                    String pekerja_koreksi = "NULL";
                                    if (rs.getString("cetak_dikoreksi_id").substring(0, 2).equals("20")) {
                                        pekerja_koreksi = "'" + rs.getString("cetak_dikoreksi_id") + "'";
                                    }
                                    String insert = "INSERT INTO `tb_cetak`(`no_laporan_produksi`, `tgl_mulai_cetak`, `cetak_diterima`, `tgl_selesai_cetak`, `cetak_diserahkan`, `cetak_dikerjakan`, `cetak_dikerjakan_grup`, `cetak_dikerjakan_level`, `cetak_dikoreksi`, `cetak_dikoreksi_id`, `cetak_dikoreksi_grup`, `cetak_dikoreksi_level`, `cetak_mangkok`, `cetak_pecah`, `cetak_flat`, `cetak_jidun`, `cetak_jidun_real`, `admin_cetak`) "
                                            + "VALUES ('" + rs.getString("no_laporan_produksi") + "',"
                                            + "'" + dateFormat.format(rs.getDate("tgl_mulai_cetak")) + "',"
                                            + "'" + rs.getString("cetak_diterima") + "',"
                                            + "'" + dateFormat.format(rs.getDate("tgl_selesai_cetak")) + "',"
                                            + "'" + rs.getString("cetak_diserahkan") + "',"
                                            + "" + pekerja_cetak + ","
                                            + "'" + rs.getString("cetak_dikerjakan_grup") + "',"
                                            + "'" + rs.getString("cetak_dikerjakan_level") + "',"
                                            + "'" + rs.getString("cetak_dikoreksi") + "',"
                                            + "" + pekerja_koreksi + ","
                                            + "'" + rs.getString("cetak_dikoreksi_grup") + "',"
                                            + "'" + rs.getString("cetak_dikoreksi_level") + "',"
                                            + "'" + rs.getFloat("cetak_mangkok") + "',"
                                            + "'" + rs.getFloat("cetak_pecah") + "',"
                                            + "'" + rs.getFloat("cetak_flat") + "',"
                                            + "'" + rs.getFloat("cetak_jidun") + "',"
                                            + "'" + rs.getFloat("cetak_jidun_real") + "',"
                                            + "'" + rs.getString("admin_cetak") + "'"
                                            + ") "
                                            + "ON DUPLICATE KEY UPDATE "
                                            + "`tgl_mulai_cetak`='" + dateFormat.format(rs.getDate("tgl_mulai_cetak")) + "',"
                                            + "`cetak_diterima`='" + rs.getString("cetak_diterima") + "',"
                                            + "`tgl_selesai_cetak`='" + dateFormat.format(rs.getDate("tgl_selesai_cetak")) + "',"
                                            + "`cetak_diserahkan`='" + rs.getString("cetak_diserahkan") + "',"
                                            + "`cetak_dikerjakan`=" + pekerja_cetak + ","
                                            + "`cetak_dikerjakan_grup`='" + rs.getString("cetak_dikerjakan_grup") + "',"
                                            + "`cetak_dikerjakan_level`='" + rs.getString("cetak_dikerjakan_level") + "',"
                                            + "`cetak_dikoreksi`='" + rs.getString("cetak_dikoreksi") + "',"
                                            + "`cetak_dikoreksi_id`=" + pekerja_koreksi + ","
                                            + "`cetak_dikoreksi_grup`='" + rs.getString("cetak_dikoreksi_grup") + "',"
                                            + "`cetak_dikoreksi_level`='" + rs.getString("cetak_dikoreksi_level") + "',"
                                            + "`cetak_mangkok`='" + rs.getFloat("cetak_mangkok") + "',"
                                            + "`cetak_pecah`='" + rs.getFloat("cetak_pecah") + "',"
                                            + "`cetak_flat`='" + rs.getFloat("cetak_flat") + "',"
                                            + "`cetak_jidun`='" + rs.getFloat("cetak_jidun") + "',"
                                            + "`cetak_jidun_real`='" + rs.getFloat("cetak_jidun_real") + "',"
                                            + "`admin_cetak`='" + rs.getString("admin_cetak") + "'";
                                    Utility.db.getConnection().createStatement();
                                    if ((Utility.db.getStatement().executeUpdate(insert)) > 0) {
                                        jumlah_berhasil++;
                                    } else {
                                        jumlah_gagal++;
                                    }
                                    jProgressBar1.setValue(jProgressBar1.getValue() + 1);
                                }
                            }
                            jProgressBar1.setValue(jProgressBar1.getMaximum());
                            JOptionPane.showMessageDialog(null, "Data download : " + jumlah_berhasil + ", gagal : " + jumlah_gagal);
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(null, e);
                            Logger.getLogger(JPanel_DataCetakSub.class.getName()).log(Level.SEVERE, null, e);
                        }
                    }
                };
                thread.start();
            } catch (Exception e) {
                Logger.getLogger(JPanel_DataCetakSub.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }//GEN-LAST:event_button_downloadActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_filterTgl;
    private javax.swing.JComboBox<String> ComboBox_ruang;
    private com.toedter.calendar.JDateChooser Date1_cetak;
    private com.toedter.calendar.JDateChooser Date2_cetak;
    public static javax.swing.JTable Table_Data_Cetak;
    private javax.swing.JButton button_download;
    private javax.swing.JButton button_export_f2;
    public static javax.swing.JButton button_search_cetak;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel label_total_data_cetak;
    private javax.swing.JLabel label_total_flat;
    private javax.swing.JLabel label_total_flat1;
    private javax.swing.JLabel label_total_gram;
    private javax.swing.JLabel label_total_jidun;
    private javax.swing.JLabel label_total_jidun1;
    private javax.swing.JLabel label_total_kpg;
    private javax.swing.JLabel label_total_mk;
    private javax.swing.JLabel label_total_mk1;
    private javax.swing.JLabel label_total_pecah;
    private javax.swing.JLabel label_total_pecah1;
    private javax.swing.JTextField txt_search_cetak;
    // End of variables declaration//GEN-END:variables

}
