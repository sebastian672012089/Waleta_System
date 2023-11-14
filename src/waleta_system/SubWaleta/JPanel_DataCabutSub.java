package waleta_system.SubWaleta;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;
import waleta_system.Class.Utility;
import waleta_system.Interface.InterfacePanel;

public class JPanel_DataCabutSub extends javax.swing.JPanel implements InterfacePanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DecimalFormat decimalFormat = new DecimalFormat();
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_DataCabutSub() {
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
            Logger.getLogger(JPanel_DataCabutSub.class.getName()).log(Level.SEVERE, null, ex);
        }
        refresh_cabut_sub_online();
        decimalFormat = Utility.DecimalFormatUS(decimalFormat);
        Table_Data_Cabut_Online.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && Table_Data_Cabut_Online.getSelectedRow() != -1) {
                    int row = Table_Data_Cabut_Online.getSelectedRow();
                    if (row != -1) {
                        label_lp.setText(Table_Data_Cabut_Online.getValueAt(row, 0).toString());
                        refreshTable_Pencabut_Online();
                    }
                }
            }
        });
        table_data_pencabut.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && table_data_pencabut.getSelectedRow() != -1) {
                    try {
                        int i = table_data_pencabut.getSelectedRow();
                        txt_no.setText(table_data_pencabut.getValueAt(i, 0).toString());
                        txt_id_pegawai.setText(table_data_pencabut.getValueAt(i, 1).toString());
                        txt_nama_pegawai.setText(table_data_pencabut.getValueAt(i, 2).toString());
                        txt_bagian.setText(table_data_pencabut.getValueAt(i, 3).toString());
                        String tgl_pencabut = table_data_pencabut.getValueAt(i, 5).toString();
                        Date_pencabut.setDate(dateFormat.parse(tgl_pencabut));
                        txt_jmlh_keping.setText(table_data_pencabut.getValueAt(i, 6).toString());
                    } catch (ParseException ex) {
                        Logger.getLogger(JPanel_DataCabutSub.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
    }

    public void refresh_cabut_sub_online() {
        try {
            Utility.db_sub.connect();
            DefaultTableModel model = (DefaultTableModel) Table_Data_Cabut_Online.getModel();
            model.setRowCount(0);
            int total_kpg_upah = 0, total_gram = 0;
            String filter_tanggal = "";
            String ruang = "AND `tb_laporan_produksi`.`ruangan` = '" + ComboBox_ruang.getSelectedItem().toString() + "'";
            if (ComboBox_ruang.getSelectedItem().toString().equals("All")) {
                ruang = "";
            }
            if (ComboBox_filterDate.getSelectedIndex() == 0 && Date1_cabut.getDate() != null && Date2_cabut.getDate() != null) {
                filter_tanggal = "`tb_cabut`.`tgl_mulai_cabut`";
                filter_tanggal = " AND (" + filter_tanggal + " BETWEEN '" + dateFormat.format(Date1_cabut.getDate()) + "' and '" + dateFormat.format(Date2_cabut.getDate()) + "')";
            } else if (ComboBox_filterDate.getSelectedIndex() == 1 && Date1_cabut.getDate() != null && Date2_cabut.getDate() != null) {
                filter_tanggal = "`tb_cabut`.`tgl_setor_cabut`";
                filter_tanggal = " AND (" + filter_tanggal + " BETWEEN '" + dateFormat.format(Date1_cabut.getDate()) + "' and '" + dateFormat.format(Date2_cabut.getDate()) + "')";
            }
            sql = "SELECT `tb_cabut`.`no_laporan_produksi`, `tb_laporan_produksi`.`kode_grade`, `keping_upah`, `berat_basah`, `pekerja_sesek`, `pekerja_hancuran`, `pekerja_kopyok`, `cabut_diterima`, `tgl_mulai_cabut`, `cabut_diserahkan`, `tgl_setor_cabut`, `sobek_cabut`, `cabut_sobek_lepas`, `gumpil_cabut`, `pecah_cabut`, `cabut_pecah_2`, `cabut_lubang`, `cabut_hilang_kaki`, `cabut_hilang_ujung`, `cabut_kaki_besar`, `cabut_kaki_kecil`, `cabut_hilang_bawah`, `admin_cabut`, `ketua_regu`, `tgl_cabut`, MIN(`tb_detail_pencabut`.`tanggal_cabut`) AS 'tanggal_cabut'"
                    + "FROM `tb_cabut` "
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_cabut`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_detail_pencabut` ON `tb_cabut`.`no_laporan_produksi` = `tb_detail_pencabut`.`no_laporan_produksi`\n"
                    + "WHERE `tb_cabut`.`no_laporan_produksi` LIKE '%" + txt_search_cabut.getText() + "%' " + ruang + filter_tanggal
                    + "GROUP BY `tb_cabut`.`no_laporan_produksi`\n"
                    + "ORDER BY `tb_cabut`.`tgl_mulai_cabut` DESC";
            Object[] row = new Object[20];
            rs = Utility.db_sub.getStatement().executeQuery(sql);
            while (rs.next()) {
                row[0] = rs.getString("no_laporan_produksi");
                row[1] = rs.getString("kode_grade");
                row[2] = rs.getInt("keping_upah");
                row[3] = rs.getInt("berat_basah");
                row[4] = rs.getDate("tgl_mulai_cabut");
                row[5] = rs.getString("cabut_diterima");
                row[6] = rs.getString("pekerja_sesek");
                row[7] = rs.getString("pekerja_hancuran");
                row[8] = rs.getString("pekerja_kopyok");
                row[9] = rs.getDate("tgl_setor_cabut");
                row[10] = rs.getString("cabut_diserahkan");
                row[11] = rs.getInt("pecah_cabut");
                row[12] = rs.getInt("sobek_cabut");
                row[13] = rs.getInt("gumpil_cabut");
                row[14] = rs.getInt("cabut_hilang_kaki");
                row[15] = rs.getInt("cabut_hilang_ujung");
                row[16] = rs.getString("admin_cabut");
                row[17] = rs.getString("ketua_regu");
                row[18] = rs.getDate("tgl_cabut");
                model.addRow(row);
                total_kpg_upah = total_kpg_upah + rs.getInt("keping_upah");
                total_gram = total_gram + rs.getInt("berat_basah");
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_Data_Cabut_Online);
            int rowData = Table_Data_Cabut_Online.getRowCount();
            label_total_data_cabut.setText(Integer.toString(rowData));
            label_total_kpg.setText(decimalFormat.format(total_kpg_upah));
            label_total_gram.setText(decimalFormat.format(total_gram));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_DataCabutSub.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void refreshTable_Pencabut_Online() {
        try {
            Utility.db_sub.connect();
            DefaultTableModel model = (DefaultTableModel) table_data_pencabut.getModel();
            model.setRowCount(0);
            int total_kpg = 0, total_gram = 0;
            int CabutSelectedRow = Table_Data_Cabut_Online.getSelectedRow();
            sql = "SELECT `nomor`, `grup_cabut`, `no_laporan_produksi`, `tb_detail_pencabut`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `bagian`, `tanggal_cabut`, `jumlah_cabut`, `jumlah_gram` \n"
                    + "FROM `tb_detail_pencabut` \n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_detail_pencabut`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                    + "WHERE `no_laporan_produksi` = '" + Table_Data_Cabut_Online.getValueAt(CabutSelectedRow, 0) + "'";
            Object[] row = new Object[9];
            rs = Utility.db_sub.getStatement().executeQuery(sql);
            while (rs.next()) {
                row[0] = rs.getString("nomor");
                row[1] = rs.getString("id_pegawai");
                row[2] = rs.getString("nama_pegawai");
                row[3] = rs.getString("bagian");
                row[4] = rs.getString("grup_cabut");
                row[5] = rs.getDate("tanggal_cabut");
                row[6] = rs.getFloat("jumlah_cabut");
                row[7] = rs.getFloat("jumlah_gram");
                model.addRow(row);
                total_kpg += rs.getFloat("jumlah_cabut");
                total_gram += rs.getFloat("jumlah_gram");
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_data_pencabut);
            label_total_cabutan.setText(decimalFormat.format(total_kpg));
            label_total_gram_cabutan.setText(decimalFormat.format(total_gram));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_DataCabutSub.class.getName()).log(Level.SEVERE, null, e);
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
        txt_search_cabut = new javax.swing.JTextField();
        button_search_cabut = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        Date1_cabut = new com.toedter.calendar.JDateChooser();
        Date2_cabut = new com.toedter.calendar.JDateChooser();
        jScrollPane4 = new javax.swing.JScrollPane();
        Table_Data_Cabut_Online = new javax.swing.JTable();
        jLabel6 = new javax.swing.JLabel();
        label_total_data_cabut = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        label_total_cabutan = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_data_pencabut = new javax.swing.JTable();
        label_total_cabutan1 = new javax.swing.JLabel();
        button_export_data_cabut = new javax.swing.JButton();
        label_lp = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        ComboBox_ruang = new javax.swing.JComboBox<>();
        ComboBox_filterDate = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        label_total_gram = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        label_total_kpg = new javax.swing.JLabel();
        button_download = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel3 = new javax.swing.JLabel();
        button_edit_cabut_online = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txt_id_pegawai = new javax.swing.JTextField();
        txt_nama_pegawai = new javax.swing.JTextField();
        txt_jmlh_keping = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        Date_pencabut = new com.toedter.calendar.JDateChooser();
        button_pick_pencabut = new javax.swing.JButton();
        button_clear_pencabut = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        txt_bagian = new javax.swing.JTextField();
        button_tambah_pencabut_online = new javax.swing.JButton();
        button_edit_pencabut_online = new javax.swing.JButton();
        button_hapus_pencabut_online = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        txt_no = new javax.swing.JTextField();
        label_total_gram_cabutan = new javax.swing.JLabel();
        label_total_cabutan3 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Cabut Online Sub", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        txt_search_cabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_cabut.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_cabutKeyPressed(evt);
            }
        });

        button_search_cabut.setBackground(new java.awt.Color(255, 255, 255));
        button_search_cabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_cabut.setText("Search");
        button_search_cabut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_cabutActionPerformed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Laporan Produksi :");

        Date1_cabut.setBackground(new java.awt.Color(255, 255, 255));
        Date1_cabut.setDate(new Date());
        Date1_cabut.setDateFormatString("dd MMMM yyyy");
        Date1_cabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date2_cabut.setBackground(new java.awt.Color(255, 255, 255));
        Date2_cabut.setDate(new Date());
        Date2_cabut.setDateFormatString("dd MMMM yyyy");
        Date2_cabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Table_Data_Cabut_Online.setAutoCreateRowSorter(true);
        Table_Data_Cabut_Online.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_Data_Cabut_Online.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP", "Grade", "Kpg upah", "Berat", "Tgl Masuk", "Diterima", "Pekerja Ssk", "Pekerja Hc", "Pekerja Kopyok", "Tgl Selesai", "Diserahkan", "Pecah", "Sobek", "Gumpil", "Hilang Kaki", "Hilang Ujung", "admin", "Ketua Regu", "Tgl Cabut"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_Data_Cabut_Online.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(Table_Data_Cabut_Online);

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Total LP :");

        label_total_data_cabut.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_cabut.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_data_cabut.setText("TOTAL");

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel1.setText("Total Cabutan :");

        label_total_cabutan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_cabutan.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_cabutan.setText("88");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel9.setText("DATA PENCABUT SETIAP LP");

        table_data_pencabut.setAutoCreateRowSorter(true);
        table_data_pencabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_pencabut.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "ID Pegawai", "Nama", "Bagian", "Grup", "Tanggal", "Kpg", "Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Float.class, java.lang.Float.class
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
        jScrollPane1.setViewportView(table_data_pencabut);

        label_total_cabutan1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_cabutan1.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_cabutan1.setText("Keping");

        button_export_data_cabut.setBackground(new java.awt.Color(255, 255, 255));
        button_export_data_cabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_data_cabut.setText("Export to Excel");
        button_export_data_cabut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_data_cabutActionPerformed(evt);
            }
        });

        label_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_lp.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_lp.setText("Laporan Produksi");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Ruang :");

        ComboBox_ruang.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_ruang.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "A", "B", "C", "D" }));

        ComboBox_filterDate.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_filterDate.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tanggal Masuk", "Tanggal Setor", "Tanggal Mulai Cabut" }));

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel12.setText("Total Gram :");

        label_total_gram.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_gram.setText("TOTAL");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel15.setText("Total Kpg :");

        label_total_kpg.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_kpg.setText("TOTAL");

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
        jLabel3.setText("Note : yang di download hanya data yang sudah di setorkan dari Sub");

        button_edit_cabut_online.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_cabut_online.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_edit_cabut_online.setText("Edit");
        button_edit_cabut_online.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_cabut_onlineActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("No :");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Nama Pegawai :");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Tanggal :");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Jumlah Cabutan :");

        txt_id_pegawai.setEditable(false);
        txt_id_pegawai.setBackground(new java.awt.Color(255, 255, 255));
        txt_id_pegawai.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        txt_nama_pegawai.setEditable(false);
        txt_nama_pegawai.setBackground(new java.awt.Color(255, 255, 255));
        txt_nama_pegawai.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        txt_jmlh_keping.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("Keping");

        Date_pencabut.setBackground(new java.awt.Color(255, 255, 255));
        Date_pencabut.setDate(new Date());
        Date_pencabut.setDateFormatString("dd MMMM yyyy");
        Date_pencabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_pick_pencabut.setBackground(new java.awt.Color(255, 255, 255));
        button_pick_pencabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_pick_pencabut.setText("...");
        button_pick_pencabut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_pick_pencabutActionPerformed(evt);
            }
        });

        button_clear_pencabut.setBackground(new java.awt.Color(255, 255, 255));
        button_clear_pencabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_clear_pencabut.setText("Clear");
        button_clear_pencabut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_clear_pencabutActionPerformed(evt);
            }
        });

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Bagian :");

        txt_bagian.setEditable(false);
        txt_bagian.setBackground(new java.awt.Color(255, 255, 255));
        txt_bagian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_tambah_pencabut_online.setBackground(new java.awt.Color(255, 255, 255));
        button_tambah_pencabut_online.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_tambah_pencabut_online.setText("Tambah");
        button_tambah_pencabut_online.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_tambah_pencabut_onlineActionPerformed(evt);
            }
        });

        button_edit_pencabut_online.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_pencabut_online.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_edit_pencabut_online.setText("Edit");
        button_edit_pencabut_online.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_pencabut_onlineActionPerformed(evt);
            }
        });

        button_hapus_pencabut_online.setBackground(new java.awt.Color(255, 255, 255));
        button_hapus_pencabut_online.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_hapus_pencabut_online.setText("Delete");
        button_hapus_pencabut_online.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_hapus_pencabut_onlineActionPerformed(evt);
            }
        });

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("ID Pegawai :");

        txt_no.setEditable(false);
        txt_no.setBackground(new java.awt.Color(255, 255, 255));
        txt_no.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txt_nama_pegawai, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(txt_id_pegawai, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(button_pick_pencabut, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(txt_jmlh_keping, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel10))
                            .addComponent(Date_pencabut, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                            .addComponent(txt_bagian, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_no, javax.swing.GroupLayout.Alignment.LEADING)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(button_tambah_pencabut_online)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_edit_pencabut_online)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_hapus_pencabut_online)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_clear_pencabut)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_no, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txt_id_pegawai, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_pick_pencabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_nama_pegawai, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_pencabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_jmlh_keping, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_tambah_pencabut_online, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_edit_pencabut_online, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_hapus_pencabut_online, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_clear_pencabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        label_total_gram_cabutan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_cabutan.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gram_cabutan.setText("88");

        label_total_cabutan3.setBackground(new java.awt.Color(255, 255, 255));
        label_total_cabutan3.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_cabutan3.setText("Gram");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_cabut, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_ruang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_filterDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date1_cabut, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date2_cabut, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search_cabut))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(label_total_cabutan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_cabutan1)
                                .addGap(18, 18, 18)
                                .addComponent(label_total_gram_cabutan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_cabutan3)))
                        .addGap(0, 578, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane4)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_lp)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data_cabut)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_kpg)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(button_export_data_cabut)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_edit_cabut_online)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_download)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jScrollPane1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_search_cabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search_cabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_ruang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date1_cabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date2_cabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_filterDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_export_data_cabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_edit_cabut_online, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(button_download, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel9)
                        .addComponent(label_lp))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_data_cabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_kpg, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_gram, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_cabutan1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(label_total_cabutan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_cabutan3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram_cabutan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txt_search_cabutKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_cabutKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refresh_cabut_sub_online();
        }
    }//GEN-LAST:event_txt_search_cabutKeyPressed

    private void button_search_cabutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_cabutActionPerformed
        // TODO add your handling code here:
        refresh_cabut_sub_online();
    }//GEN-LAST:event_button_search_cabutActionPerformed

    private void button_export_data_cabutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_data_cabutActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_Data_Cabut_Online.getModel();
        ExportToExcel.writeToExcel(model, jPanel2);
    }//GEN-LAST:event_button_export_data_cabutActionPerformed

    private void button_downloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_downloadActionPerformed
        // TODO add your handling code here:
        int dialogResult = JOptionPane.showConfirmDialog(this, "Download data sub online into waleta database?", "Warning", 0);
        if (dialogResult == JOptionPane.YES_OPTION) {
            try {
                refresh_cabut_sub_online();
                URL url = new URL("http://waleta019.com/subwaleta/select_cabut_ke_pc.php?sql=" + URLEncoder.encode(sql, "UTF-8"));
                URLConnection conn = url.openConnection();
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String json = rd.readLine();
                JSONObject obj = (JSONObject) JSONValue.parse(json);
                final JSONArray tb_cabut = (JSONArray) obj.get("tb_cabut");

                int panjang = tb_cabut.size();
                jProgressBar1.setMinimum(0);
                jProgressBar1.setMaximum(panjang);
                jProgressBar1.setValue(0);
                jProgressBar1.setStringPainted(true);
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        int jumlah_berhasil = 0, jumlah_gagal = 0;
                        for (int i = 0; i < tb_cabut.size(); i++) {
                            JSONObject rb = (JSONObject) tb_cabut.get(i);
                            String insert_pencabut = "";
                            try {
                                if (rb.get("tgl_setor_cabut") != null && !rb.get("tgl_setor_cabut").toString().equals("")) {
                                    String insert = "INSERT INTO `tb_cabut`(`no_laporan_produksi`, `pekerja_hancuran`, `pekerja_kopyok`, `cabut_diterima`, `tgl_mulai_cabut`, `cabut_diserahkan`, `tgl_setor_cabut`, `sobek_cabut`, `cabut_sobek_lepas`, `gumpil_cabut`, `pecah_cabut`, `cabut_pecah_2`, `cabut_lubang`, `cabut_hilang_kaki`, `cabut_hilang_ujung`, `cabut_kaki_besar`, `cabut_kaki_kecil`, `cabut_hilang_bawah`, `admin_cabut`, `ketua_regu`, `tgl_cabut`) "
                                            + "VALUES ('" + rb.get("no_laporan_produksi") + "',"
                                            + "'" + rb.get("pekerja_hancuran") + "',"
                                            + "'" + rb.get("pekerja_kopyok") + "',"
                                            + "'" + rb.get("cabut_diterima") + "',"
                                            + "'" + rb.get("tgl_mulai_cabut") + "',"
                                            + "'" + rb.get("cabut_diserahkan") + "',"
                                            + "'" + rb.get("tgl_setor_cabut") + "',"
                                            + "'" + rb.get("sobek_cabut") + "',"
                                            + "'" + rb.get("cabut_sobek_lepas") + "',"
                                            + "'" + rb.get("gumpil_cabut") + "',"
                                            + "'" + rb.get("pecah_cabut") + "',"
                                            + "'" + rb.get("cabut_pecah_2") + "',"
                                            + "'" + rb.get("cabut_lubang") + "',"
                                            + "'" + rb.get("cabut_hilang_kaki") + "',"
                                            + "'" + rb.get("cabut_hilang_ujung") + "',"
                                            + "'" + rb.get("cabut_kaki_besar") + "',"
                                            + "'" + rb.get("cabut_kaki_kecil") + "',"
                                            + "'" + rb.get("cabut_hilang_bawah") + "',"
                                            + "'" + rb.get("admin_cabut") + "',"
                                            + "'" + rb.get("ketua_regu") + "',"
                                            + "'" + rb.get("tgl_cabut") + "'"
                                            + ") "
                                            + "ON DUPLICATE KEY UPDATE "
                                            + "`pekerja_hancuran`='" + rb.get("pekerja_hancuran") + "',"
                                            + "`pekerja_kopyok`='" + rb.get("pekerja_kopyok") + "',"
                                            + "`cabut_diterima`='" + rb.get("cabut_diterima") + "',"
                                            + "`tgl_mulai_cabut`='" + rb.get("tgl_mulai_cabut") + "',"
                                            + "`cabut_diserahkan`='" + rb.get("cabut_diserahkan") + "',"
                                            + "`tgl_setor_cabut`='" + rb.get("tgl_setor_cabut") + "',"
                                            + "`sobek_cabut`='" + rb.get("sobek_cabut") + "',"
                                            + "`cabut_sobek_lepas`='" + rb.get("cabut_sobek_lepas") + "',"
                                            + "`gumpil_cabut`='" + rb.get("gumpil_cabut") + "',"
                                            + "`pecah_cabut`='" + rb.get("pecah_cabut") + "',"
                                            + "`cabut_pecah_2`='" + rb.get("cabut_pecah_2") + "',"
                                            + "`cabut_lubang`='" + rb.get("cabut_lubang") + "',"
                                            + "`cabut_hilang_kaki`='" + rb.get("cabut_hilang_kaki") + "',"
                                            + "`cabut_hilang_ujung`='" + rb.get("cabut_hilang_ujung") + "',"
                                            + "`cabut_kaki_besar`='" + rb.get("cabut_kaki_besar") + "',"
                                            + "`cabut_kaki_kecil`='" + rb.get("cabut_kaki_kecil") + "',"
                                            + "`cabut_hilang_bawah`='" + rb.get("cabut_hilang_bawah") + "',"
                                            + "`admin_cabut`='" + rb.get("admin_cabut") + "',"
                                            + "`ketua_regu`='" + rb.get("ketua_regu") + "',"
                                            + "`tgl_cabut`='" + rb.get("tgl_cabut") + "'";
                                    Utility.db.getConnection().createStatement();
                                    if ((Utility.db.getStatement().executeUpdate(insert)) > 0) {
                                        jumlah_berhasil++;
//                                    String select = "SELECT `no_laporan_produksi` FROM `tb_cabut` "
//                                            + "WHERE `no_laporan_produksi` = '" + rb.get("no_laporan_produksi") + "' ";
//                                    ResultSet result = Utility.db.getStatement().executeQuery(select);
//                                    if (result.next()) {
//                                        System.out.println("sudah masuk");
//                                    } else {
//                                        System.out.println("belum masuk");
//                                    }
                                    } else {
                                        jumlah_gagal++;
                                    }
                                    jProgressBar1.setValue(jProgressBar1.getValue() + 1);

                                    String query = "SELECT `nomor`, `grup_cabut`, `no_laporan_produksi`, `tb_detail_pencabut`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `bagian`, `tanggal_cabut`, `jumlah_cabut`, `jumlah_gram` \n"
                                            + "FROM `tb_detail_pencabut` \n"
                                            + "LEFT JOIN `tb_karyawan` ON `tb_detail_pencabut`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                                            + "WHERE `no_laporan_produksi` = '" + rb.get("no_laporan_produksi") + "'";
                                    System.out.println(query);
                                    URL url2 = new URL("http://waleta019.com/subwaleta/select_pencabut_ke_pc.php?sql=" + URLEncoder.encode(query, "UTF-8"));
                                    URLConnection conn2 = url2.openConnection();
                                    BufferedReader rd2 = new BufferedReader(new InputStreamReader(conn2.getInputStream()));
                                    String json2 = rd2.readLine();
                                    JSONObject obj2 = (JSONObject) JSONValue.parse(json2);
                                    JSONArray detail_pencabut = (JSONArray) obj2.get("detail_pencabut");
                                    System.out.println(detail_pencabut.size());
                                    for (int j = 0; j < detail_pencabut.size(); j++) {
                                        JSONObject rb2 = (JSONObject) detail_pencabut.get(j);
                                        String select = "SELECT `nomor` FROM `tb_detail_pencabut` "
                                                + "WHERE `id_pegawai` = '" + rb2.get("id_pegawai") + "' "
                                                + "AND `tanggal_cabut` = '" + rb2.get("tanggal_cabut") + "' "
                                                + "AND `no_laporan_produksi`='" + rb2.get("no_laporan_produksi") + "'";
                                        ResultSet result = Utility.db.getStatement().executeQuery(select);
                                        if (result.next()) {
                                            String update_pencabut = "UPDATE `tb_detail_pencabut` SET "
                                                    + "`jumlah_cabut`='" + rb2.get("jumlah_cabut") + "',"
                                                    + "`jumlah_gram`='" + rb2.get("jumlah_gram") + "',"
                                                    + "`grup_cabut`='" + rb2.get("grup_cabut") + "' "
                                                    + "WHERE "
                                                    + "`nomor`='" + result.getString("nomor") + "' ";
                                            Utility.db.getConnection().createStatement();
                                            Utility.db.getStatement().executeUpdate(update_pencabut);
                                        } else {
                                            insert_pencabut = "INSERT INTO `tb_detail_pencabut`(`no_laporan_produksi`, `id_pegawai`, `tanggal_cabut`, `jumlah_cabut`, `jumlah_gram`, `grup_cabut`) "
                                                    + "VALUE ('" + rb2.get("no_laporan_produksi") + "',"
                                                    + "'" + rb2.get("id_pegawai") + "',"
                                                    + "'" + rb2.get("tanggal_cabut") + "',"
                                                    + "'" + rb2.get("jumlah_cabut") + "',"
                                                    + "'" + rb2.get("jumlah_gram") + "',"
                                                    + "'" + rb2.get("grup_cabut") + "')";
                                            Utility.db.getConnection().createStatement();
                                            Utility.db.getStatement().executeUpdate(insert_pencabut);
                                        }
                                    }
                                }
                            } catch (Exception ex) {
                                System.out.println(insert_pencabut);
                                JOptionPane.showMessageDialog(null, ex);
                                Logger.getLogger(JPanel_DataCabutSub.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        jProgressBar1.setValue(jProgressBar1.getMaximum());
                        JOptionPane.showMessageDialog(null, "Data download : " + jumlah_berhasil + ", gagal : " + jumlah_gagal);
                    }
                };
                thread.start();
                long success = (long) obj.get("success");
                System.out.println("success = " + success);
                rd.close();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e);
                Logger.getLogger(JPanel_DataCabutSub.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }//GEN-LAST:event_button_downloadActionPerformed

    private void button_edit_cabut_onlineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_cabut_onlineActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_Data_Cabut_Online.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang ingin di ubah !");
            } else {
                String no_lp = Table_Data_Cabut_Online.getValueAt(j, 0).toString();
                JDialog_Edit_Data_Cabut_sub edit_cabut = new JDialog_Edit_Data_Cabut_sub(new javax.swing.JFrame(), true, no_lp);
                edit_cabut.pack();
                edit_cabut.setLocationRelativeTo(this);
                edit_cabut.setVisible(true);
                edit_cabut.setEnabled(true);
                refresh_cabut_sub_online();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_DataCabutSub.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_edit_cabut_onlineActionPerformed

    private void button_tambah_pencabut_onlineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_tambah_pencabut_onlineActionPerformed
        // TODO add your handling code here:
        int x = Table_Data_Cabut_Online.getSelectedRow();
        decimalFormat.setGroupingUsed(false);
        if (x == -1) {
            JOptionPane.showMessageDialog(this, "Pilih No LP di tabel Cabut terlebih dahulu !");
        } else {
            String no_lp = Table_Data_Cabut_Online.getValueAt(x, 0).toString();
            try {
                Utility.db_sub.connect();
                decimalFormat.setMaximumFractionDigits(2);
                Boolean Check = true;
                int total_baris = table_data_pencabut.getRowCount();
                int keping_cabut = Integer.valueOf(txt_jmlh_keping.getText());
                float berat_basah = 0, total_keping = 0, gram_cabutan = 0;
                sql = "SELECT `berat_basah`, `jumlah_keping`, `keping_upah` FROM `tb_laporan_produksi` WHERE `no_laporan_produksi` = '" + no_lp + "'";
                rs = Utility.db_sub.getStatement().executeQuery(sql);
                if (rs.next()) {
                    berat_basah = rs.getFloat("berat_basah");
                    total_keping = rs.getFloat("keping_upah");
                }
                gram_cabutan = (berat_basah / total_keping) * Float.valueOf(txt_jmlh_keping.getText());

                sql = "SELECT * FROM `tb_detail_pencabut` WHERE `no_laporan_produksi` = '" + no_lp + "' AND `id_pegawai` = '" + txt_id_pegawai.getText() + "' AND `tanggal_cabut` = '" + dateFormat.format(Date_pencabut.getDate()) + "'";
                rs = Utility.db_sub.getStatement().executeQuery(sql);
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Pegawai dengan nama : " + txt_nama_pegawai.getText() + " pada tanggal " + dateFormat.format(Date_pencabut.getDate()) + "\n sudah terdaftar !");
                    Check = false;
                }

                if (Check) {
                    if (table_data_pencabut.getRowCount() == 0) {
                        sql = "UPDATE `tb_cabut` SET `tgl_cabut`=CURRENT_DATE WHERE `no_laporan_produksi` = '" + no_lp + "'";
                        Utility.db_sub.getConnection().createStatement();
                        Utility.db_sub.getStatement().executeUpdate(sql);
                    }
                    sql = "INSERT INTO `tb_detail_pencabut`(`no_laporan_produksi`, `id_pegawai`, `tanggal_cabut`, `jumlah_cabut`, `jumlah_gram`, `grup_cabut`) "
                            + "VALUES ('" + no_lp + "','" + txt_id_pegawai.getText() + "','" + dateFormat.format(Date_pencabut.getDate()) + "','" + keping_cabut + "', " + decimalFormat.format(gram_cabutan) + ", '-')";
                    Utility.db_sub.getConnection().createStatement();
                    if ((Utility.db_sub.getStatement().executeUpdate(sql)) == 1) {
                        JOptionPane.showMessageDialog(this, "Berhasil tambah data pencabut !");
                    } else {
                        JOptionPane.showMessageDialog(this, "Tambah data gagal !");
                    }
                    refreshTable_Pencabut_Online();
                    button_clear_pencabut.doClick();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex);
                Logger.getLogger(JPanel_DataCabutSub.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_tambah_pencabut_onlineActionPerformed

    private void button_edit_pencabut_onlineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_pencabut_onlineActionPerformed
        // TODO add your handling code here:
        int j = table_data_pencabut.getSelectedRow();
        try {
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang akan di edit !");
            } else {
                Utility.db_sub.connect();
                Boolean Check = true;
                int keping_cabut = Math.round(Float.valueOf(txt_jmlh_keping.getText()));
                float berat_basah = 0, total_keping = 0;
                float gram_cabutan = 0;
                String query = "SELECT `berat_basah`, `jumlah_keping`, `keping_upah` FROM `tb_laporan_produksi` WHERE `no_laporan_produksi` = '" + label_lp.getText() + "'";
                ResultSet result = Utility.db_sub.getStatement().executeQuery(query);
                if (result.next()) {
                    berat_basah = result.getFloat("berat_basah");
                    total_keping = result.getFloat("keping_upah");
                }
                gram_cabutan = (berat_basah / total_keping) * Float.valueOf(txt_jmlh_keping.getText());

                sql = "SELECT * FROM `tb_detail_pencabut` WHERE `no_laporan_produksi` = '" + label_lp.getText() + "' AND `id_pegawai` = '" + txt_id_pegawai.getText() + "' AND `tanggal_cabut` = '" + dateFormat.format(Date_pencabut.getDate()) + "' AND `nomor`<>'" + txt_no.getText() + "'";
                rs = Utility.db_sub.getStatement().executeQuery(sql);
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Pegawai dengan nama : " + txt_nama_pegawai.getText() + " pada tanggal " + dateFormat.format(Date_pencabut.getDate()) + "\n sudah terdaftar !");
                    Check = false;
                }

                if (Check) {
                    sql = "UPDATE `tb_detail_pencabut` SET "
                            + "`id_pegawai`='" + txt_id_pegawai.getText() + "',"
                            + "`tanggal_cabut`='" + dateFormat.format(Date_pencabut.getDate()) + "',"
                            + "`jumlah_cabut`='" + keping_cabut + "', "
                            + "`jumlah_gram`='" + decimalFormat.format(gram_cabutan) + "', "
                            + "`grup_cabut`=(SELECT `bagian` FROM `tb_karyawan` WHERE `id_pegawai` = '" + txt_id_pegawai.getText() + "') "
                            + "WHERE `nomor`='" + txt_no.getText() + "'";
                    Utility.db_sub.getConnection().createStatement();
                    if ((Utility.db_sub.getStatement().executeUpdate(sql)) == 1) {
                        JOptionPane.showMessageDialog(this, "Berhasil edit data pencabut !");
                    } else {
                        JOptionPane.showMessageDialog(this, "Edit data gagal !");
                    }
                    refreshTable_Pencabut_Online();
                    button_clear_pencabut.doClick();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_DataCabutSub.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_edit_pencabut_onlineActionPerformed

    private void button_hapus_pencabut_onlineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_hapus_pencabut_onlineActionPerformed
        // TODO add your handling code here:
        try {
            int j = table_data_pencabut.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Please Select Row Data that you want to Delete !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    Utility.db_sub.connect();
                    sql = "DELETE FROM `tb_detail_pencabut` WHERE `tb_detail_pencabut`.`nomor` = '" + table_data_pencabut.getValueAt(j, 0) + "'";
                    Utility.db_sub.getConnection().createStatement();
                    if ((Utility.db_sub.getStatement().executeUpdate(sql)) == 1) {
                        JOptionPane.showMessageDialog(this, "Berhasil Hapus data pencabut !");
                    } else {
                        JOptionPane.showMessageDialog(this, "Hapus data gagal !");
                    }
                    refreshTable_Pencabut_Online();
                    button_clear_pencabut.doClick();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_DataCabutSub.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_hapus_pencabut_onlineActionPerformed

    private void button_pick_pencabutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_pick_pencabutActionPerformed
        // TODO add your handling code here:
        String filter_tgl = "";
        JDialog_Browse_KaryawanSub dialog = new JDialog_Browse_KaryawanSub(new javax.swing.JFrame(), true, filter_tgl);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);

        int x = JDialog_Browse_KaryawanSub.table_list_karyawan.getSelectedRow();
        if (x != -1) {
            txt_id_pegawai.setText(JDialog_Browse_KaryawanSub.table_list_karyawan.getValueAt(x, 0).toString());
            txt_nama_pegawai.setText(JDialog_Browse_KaryawanSub.table_list_karyawan.getValueAt(x, 1).toString());
            txt_bagian.setText(JDialog_Browse_KaryawanSub.table_list_karyawan.getValueAt(x, 2).toString());
        }
    }//GEN-LAST:event_button_pick_pencabutActionPerformed

    private void button_clear_pencabutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_clear_pencabutActionPerformed
        // TODO add your handling code here:
        txt_id_pegawai.setText(null);
        txt_nama_pegawai.setText(null);
        Date_pencabut.setDate(null);
        txt_jmlh_keping.setText(null);
    }//GEN-LAST:event_button_clear_pencabutActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_filterDate;
    private javax.swing.JComboBox<String> ComboBox_ruang;
    private com.toedter.calendar.JDateChooser Date1_cabut;
    private com.toedter.calendar.JDateChooser Date2_cabut;
    private com.toedter.calendar.JDateChooser Date_pencabut;
    public static javax.swing.JTable Table_Data_Cabut_Online;
    private javax.swing.JButton button_clear_pencabut;
    private javax.swing.JButton button_download;
    public static javax.swing.JButton button_edit_cabut_online;
    public static javax.swing.JButton button_edit_pencabut_online;
    private javax.swing.JButton button_export_data_cabut;
    public static javax.swing.JButton button_hapus_pencabut_online;
    private javax.swing.JButton button_pick_pencabut;
    public static javax.swing.JButton button_search_cabut;
    public static javax.swing.JButton button_tambah_pencabut_online;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel label_lp;
    private javax.swing.JLabel label_total_cabutan;
    private javax.swing.JLabel label_total_cabutan1;
    private javax.swing.JLabel label_total_cabutan3;
    private javax.swing.JLabel label_total_data_cabut;
    private javax.swing.JLabel label_total_gram;
    private javax.swing.JLabel label_total_gram_cabutan;
    private javax.swing.JLabel label_total_kpg;
    private javax.swing.JTable table_data_pencabut;
    private javax.swing.JTextField txt_bagian;
    private javax.swing.JTextField txt_id_pegawai;
    private javax.swing.JTextField txt_jmlh_keping;
    private javax.swing.JTextField txt_nama_pegawai;
    private javax.swing.JTextField txt_no;
    private javax.swing.JTextField txt_search_cabut;
    // End of variables declaration//GEN-END:variables

}
