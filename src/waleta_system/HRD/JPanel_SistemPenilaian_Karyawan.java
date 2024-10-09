package waleta_system.HRD;

import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;

public class JPanel_SistemPenilaian_Karyawan extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    HashMap<String, String> nama_karyawan = new HashMap<>();
    HashMap<String, String> bagian_karyawan = new HashMap<>();
    HashMap<String, Date> tgl_masuk_karyawan = new HashMap<>();
    HashMap<String, Float> poin_karyawan = new HashMap<>();

    public JPanel_SistemPenilaian_Karyawan() {
        initComponents();
    }

    public void init() {
        refreshAllTable();
    }

    public void refreshAllTable() {
        if (Date1.getDate() != null && Date2.getDate() != null) {
            nama_karyawan = new HashMap<>();
            bagian_karyawan = new HashMap<>();
            tgl_masuk_karyawan = new HashMap<>();
            poin_karyawan = new HashMap<>();
            refreshTable_Pelanggaran_hp();
            refreshTable_Absensi();
            refreshTable_Keterlambatan();
            refreshTable_ijin_keluar();
            refreshTable_ijin_keluar_ruangan();
            refreshTable_pelanggaran_lain();

            DefaultTableModel model = (DefaultTableModel) Table_total_poin_pelanggaran.getModel();
            model.setRowCount(0);
            Object[] row = new Object[10];
            for (String i : nama_karyawan.keySet()) {
                row[0] = i;
                row[1] = nama_karyawan.get(i);
                row[2] = bagian_karyawan.get(i);
                row[3] = new SimpleDateFormat("dd MMM yyyy").format(tgl_masuk_karyawan.get(i));
                row[4] = poin_karyawan.get(i);
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_total_poin_pelanggaran);
        } else {
            JOptionPane.showMessageDialog(this, "Range tanggal harus dipilih!");
        }
    }

    public void refreshTable_Pelanggaran_hp() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_pelanggaran_ph.getModel();
            model.setRowCount(0);
            String filter_status = "";
            if (!ComboBox_status.getSelectedItem().toString().equals("All")) {
                filter_status = " AND `status` = '" + ComboBox_status.getSelectedItem().toString() + "' ";
            }
            String filter_tanggal = " `tanggal_pelanggaran` BETWEEN '" + dateFormat.format(Date1.getDate()) + "' AND '" + dateFormat.format(Date2.getDate()) + "'";
            sql = "SELECT `tb_karyawan`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `tb_bagian`.`nama_bagian`, `tanggal_masuk`, "
                    + "SUM(IF(" + filter_tanggal + ", 1, 0)) AS 'jumlah_pelanggaran' \n"
                    + "FROM `tb_karyawan` \n"
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                    + "LEFT JOIN `tb_pelanggaran_personal_hygiene` ON `tb_karyawan`.`id_pegawai` = `tb_pelanggaran_personal_hygiene`.`id_pegawai`\n"
                    + "WHERE "
                    + "`nama_pegawai` LIKE '%" + txt_search_nama.getText() + "%' "
                    + "AND `nama_bagian` LIKE '%" + txt_search_bagian.getText() + "%'"
                    + "AND `posisi` = 'PEJUANG'"
                    + filter_status
                    + "GROUP BY `tb_karyawan`.`id_pegawai` "
                    + "ORDER BY `tb_karyawan`.`nama_pegawai`";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getString("id_pegawai");
                row[1] = rs.getString("nama_pegawai");
                row[2] = rs.getString("nama_bagian");
                row[3] = rs.getInt("jumlah_pelanggaran");
                row[4] = rs.getInt("jumlah_pelanggaran") * 10;
                float poin = rs.getFloat("jumlah_pelanggaran") * 10f;
                model.addRow(row);

                String id_pegawai = rs.getString("id_pegawai");
                if (nama_karyawan.containsKey(id_pegawai)) {
                    poin_karyawan.put(id_pegawai, poin_karyawan.get(id_pegawai) + poin);
                } else {
                    nama_karyawan.put(id_pegawai, rs.getString("nama_pegawai"));
                    bagian_karyawan.put(id_pegawai, rs.getString("nama_bagian"));
                    tgl_masuk_karyawan.put(id_pegawai, rs.getDate("tanggal_masuk"));
                    poin_karyawan.put(id_pegawai, poin);
                }
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_pelanggaran_ph);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_SistemPenilaian_Karyawan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_Absensi() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_absensi.getModel();
            model.setRowCount(0);
            String filter_status = "";
            if (!ComboBox_status.getSelectedItem().toString().equals("All")) {
                filter_status = " AND `status` = '" + ComboBox_status.getSelectedItem().toString() + "' ";
            }
            String filter_tanggal = "`tanggal_cuti` BETWEEN '" + dateFormat.format(Date1.getDate()) + "' AND '" + dateFormat.format(Date2.getDate()) + "'";
            sql = "SELECT `tb_karyawan`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `tb_bagian`.`nama_bagian`, "
                    + "SUM(IF(`jenis_cuti` = 'Absen' AND " + filter_tanggal + ", 1, 0)) AS 'jumlah_absen' \n"
                    + "FROM `tb_karyawan` \n"
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                    + "LEFT JOIN `tb_cuti` ON `tb_karyawan`.`id_pegawai` = `tb_cuti`.`id_pegawai`\n"
                    + "WHERE "
                    + "`nama_pegawai` LIKE '%" + txt_search_nama.getText() + "%' "
                    + "AND `nama_bagian` LIKE '%" + txt_search_bagian.getText() + "%' \n"
                    + "AND `posisi` = 'PEJUANG'"
                    + filter_status
                    + "GROUP BY `tb_karyawan`.`id_pegawai` "
                    + "ORDER BY `tb_karyawan`.`nama_pegawai`";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getString("id_pegawai");
                row[1] = rs.getString("nama_pegawai");
                row[2] = rs.getString("nama_bagian");
                row[3] = rs.getInt("jumlah_absen");
                row[4] = rs.getInt("jumlah_absen") * 50;
                float poin = rs.getFloat("jumlah_absen") * 50f;
                model.addRow(row);

                String id_pegawai = rs.getString("id_pegawai");
                if (nama_karyawan.containsKey(id_pegawai)) {
                    poin_karyawan.put(id_pegawai, poin_karyawan.get(id_pegawai) + poin);
                } else {
                    nama_karyawan.put(id_pegawai, rs.getString("nama_pegawai"));
                    bagian_karyawan.put(id_pegawai, rs.getString("nama_bagian"));
                    poin_karyawan.put(id_pegawai, poin);
                }
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_absensi);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_SistemPenilaian_Karyawan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_Keterlambatan() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_keterlambatan.getModel();
            model.setRowCount(0);
            String filter_status = "";
            if (!ComboBox_status.getSelectedItem().toString().equals("All")) {
                filter_status = " AND `status` = '" + ComboBox_status.getSelectedItem().toString() + "' ";
            }
            String filter_tanggal = "`tb_lembur_rekap`.`tanggal` BETWEEN '" + dateFormat.format(Date1.getDate()) + "' AND '" + dateFormat.format(Date2.getDate()) + "'";
            sql = "SELECT `tb_karyawan`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `tb_bagian`.`nama_bagian`, "
                    + "SUM(IF(" + filter_tanggal + ", `menit_terlambat`, 0)) AS 'menit_terlambat' \n"
                    + "FROM `tb_karyawan` \n"
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                    + "LEFT JOIN `tb_lembur_rekap` ON `tb_karyawan`.`id_pegawai` = `tb_lembur_rekap`.`id_pegawai`\n"
                    + "WHERE "
                    + "`nama_pegawai` LIKE '%" + txt_search_nama.getText() + "%' "
                    + "AND `nama_bagian` LIKE '%" + txt_search_bagian.getText() + "%'"
                    + "AND `posisi` = 'PEJUANG'"
                    + filter_status
                    + "GROUP BY `tb_karyawan`.`id_pegawai` "
                    + "ORDER BY `tb_karyawan`.`nama_pegawai`";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getString("id_pegawai");
                row[1] = rs.getString("nama_pegawai");
                row[2] = rs.getString("nama_bagian");
                row[3] = rs.getFloat("menit_terlambat");
                row[4] = rs.getFloat("menit_terlambat") * 0.5f;
                float poin = rs.getFloat("menit_terlambat") * 0.5f;
                model.addRow(row);

                String id_pegawai = rs.getString("id_pegawai");
                if (nama_karyawan.containsKey(id_pegawai)) {
                    poin_karyawan.put(id_pegawai, poin_karyawan.get(id_pegawai) + poin);
                } else {
                    nama_karyawan.put(id_pegawai, rs.getString("nama_pegawai"));
                    bagian_karyawan.put(id_pegawai, rs.getString("nama_bagian"));
                    poin_karyawan.put(id_pegawai, poin);
                }
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_keterlambatan);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_SistemPenilaian_Karyawan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_ijin_keluar() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_ijin_keluar.getModel();
            model.setRowCount(0);
            String filter_status = "";
            if (!ComboBox_status.getSelectedItem().toString().equals("All")) {
                filter_status = " AND `status` = '" + ComboBox_status.getSelectedItem().toString() + "' ";
            }
            String filter_tanggal = " AND (`tb_ijin_keluar`.`tanggal_keluar` BETWEEN '" + dateFormat.format(Date1.getDate()) + "' AND '" + dateFormat.format(Date2.getDate()) + "')";
            sql = "SELECT `tb_karyawan`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `tb_bagian`.`nama_bagian`, "
                    + "SUM(TIME_TO_SEC(TIMEDIFF(IF(`jam_kembali` IS NOT NULL, `jam_kembali`, (SELECT `jam_pulang` FROM `tb_lembur_rekap` WHERE `tanggal` = `tb_ijin_keluar`.`tanggal_keluar` AND `id_pegawai` = `tb_ijin_keluar`.`id_pegawai`)), `jam_keluar`))) AS 'menit_ijin' \n"
                    + "FROM `tb_karyawan` \n"
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                    + "LEFT JOIN `tb_ijin_keluar` ON `tb_karyawan`.`id_pegawai` = `tb_ijin_keluar`.`id_pegawai`\n"
                    + "WHERE "
                    + "`nama_pegawai` LIKE '%" + txt_search_nama.getText() + "%' "
                    + "AND `nama_bagian` LIKE '%" + txt_search_bagian.getText() + "%'"
                    + "AND `posisi` = 'PEJUANG'"
                    + filter_tanggal
                    + filter_status
                    + "GROUP BY `tb_karyawan`.`id_pegawai` "
                    + "ORDER BY `tb_karyawan`.`nama_pegawai`";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getString("id_pegawai");
                row[1] = rs.getString("nama_pegawai");
                row[2] = rs.getString("nama_bagian");
                row[3] = Math.round(rs.getFloat("menit_ijin") / 60f);//detik jadi menit
                row[4] = (Math.round(rs.getFloat("menit_ijin") / 60f)) * 0.5f;
                float poin = (Math.round(rs.getFloat("menit_ijin") / 60f)) * 0.5f;
                model.addRow(row);

                String id_pegawai = rs.getString("id_pegawai");
                if (nama_karyawan.containsKey(id_pegawai)) {
                    poin_karyawan.put(id_pegawai, poin_karyawan.get(id_pegawai) + poin);
                } else {
                    nama_karyawan.put(id_pegawai, rs.getString("nama_pegawai"));
                    bagian_karyawan.put(id_pegawai, rs.getString("nama_bagian"));
                    poin_karyawan.put(id_pegawai, poin);
                }
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_ijin_keluar);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_SistemPenilaian_Karyawan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_ijin_keluar_ruangan() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_ijin_keluar_ruangan.getModel();
            model.setRowCount(0);
            String filter_status = "";
            if (!ComboBox_status.getSelectedItem().toString().equals("All")) {
                filter_status = " AND `status` = '" + ComboBox_status.getSelectedItem().toString() + "' ";
            }
            String filter_tanggal = " AND (DATE(`waktu_ijin`) BETWEEN '" + dateFormat.format(Date1.getDate()) + "' AND '" + dateFormat.format(Date2.getDate()) + "')";
            sql = "SELECT `tb_karyawan`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `tb_bagian`.`nama_bagian`, "
                    + "SUM(IF(`waktu_kembali` IS NOT NULL AND `keterangan_ijin` = 'Toilet', IF(TIME_TO_SEC(TIMEDIFF(`waktu_kembali`, `waktu_ijin`))>1800, TIME_TO_SEC(TIMEDIFF(`waktu_kembali`, `waktu_ijin`))-1800, 0), 0)) AS 'poin_toilet', \n"//lebih dari 30 menit
                    + "SUM(IF(`waktu_kembali` IS NOT NULL AND `keterangan_ijin` = 'Toilet', TIME_TO_SEC(TIMEDIFF(`waktu_kembali`, `waktu_ijin`)), 0)) AS 'menit_toilet', \n"
                    + "SUM(IF(`waktu_kembali` IS NOT NULL AND `keterangan_ijin` = 'Mushola', TIME_TO_SEC(TIMEDIFF(`waktu_kembali`, `waktu_ijin`)), 0)) AS 'menit_Mushola', \n"
                    + "SUM(IF(`waktu_kembali` IS NOT NULL AND `keterangan_ijin` = 'Makan', TIME_TO_SEC(TIMEDIFF(`waktu_kembali`, `waktu_ijin`)), 0)) AS 'menit_makan', \n"
                    + "SUM(IF(`waktu_kembali` IS NOT NULL AND `keterangan_ijin` NOT IN ('Toilet', 'Mushola', 'Makan'), TIME_TO_SEC(TIMEDIFF(`waktu_kembali`, `waktu_ijin`)), 0)) AS 'menit_lain' \n"
                    + "FROM `tb_karyawan` \n"
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                    + "LEFT JOIN `tb_ijin_keluar_ruangan` ON `tb_karyawan`.`id_pegawai` = `tb_ijin_keluar_ruangan`.`id_pegawai`\n"
                    + "WHERE "
                    + "`nama_pegawai` LIKE '%" + txt_search_nama.getText() + "%' "
                    + "AND `nama_bagian` LIKE '%" + txt_search_bagian.getText() + "%'"
                    + "AND `posisi` = 'PEJUANG'"
                    + filter_tanggal
                    + filter_status
                    + "GROUP BY `tb_karyawan`.`id_pegawai` "
                    + "ORDER BY `tb_karyawan`.`nama_pegawai`";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getString("id_pegawai");
                row[1] = rs.getString("nama_pegawai");
                row[2] = rs.getString("nama_bagian");
                row[3] = Math.round(rs.getFloat("menit_toilet") / 60f);//detik jadi menit
                row[4] = Math.round(rs.getFloat("menit_Mushola") / 60f);//detik jadi menit
                row[5] = Math.round(rs.getFloat("menit_makan") / 60f);//detik jadi menit
                row[6] = Math.round(rs.getFloat("menit_lain") / 60f);//detik jadi menit
                row[7] = Math.round(rs.getFloat("poin_toilet") / 60f) * 1f;//detik jadi menit
                float poin = Math.round(rs.getFloat("poin_toilet") / 60f) * 1f;
                model.addRow(row);

                String id_pegawai = rs.getString("id_pegawai");
                if (nama_karyawan.containsKey(id_pegawai)) {
                    poin_karyawan.put(id_pegawai, poin_karyawan.get(id_pegawai) + poin);
                } else {
                    nama_karyawan.put(id_pegawai, rs.getString("nama_pegawai"));
                    bagian_karyawan.put(id_pegawai, rs.getString("nama_bagian"));
                    poin_karyawan.put(id_pegawai, poin);
                }
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_ijin_keluar_ruangan);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_SistemPenilaian_Karyawan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_pelanggaran_lain() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_pelanggaran_lain2.getModel();
            model.setRowCount(0);
            ColumnsAutoSizer.sizeColumnsToFit(Table_pelanggaran_lain2);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_SistemPenilaian_Karyawan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        button_refresh = new javax.swing.JButton();
        txt_search_nama = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        Date1 = new com.toedter.calendar.JDateChooser();
        Date2 = new com.toedter.calendar.JDateChooser();
        jLabel6 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel_personal_hygene = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_pelanggaran_ph = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jPanel_absensi = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Table_absensi = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel_keterlambatan = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        Table_keterlambatan = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jPanel_ijinKeluar = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        Table_ijin_keluar = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        jPanel_ijinKeluarRuangan = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        Table_ijin_keluar_ruangan = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        jPanel_lain2 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        Table_pelanggaran_lain2 = new javax.swing.JTable();
        jPanel_total = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        Table_total_poin_pelanggaran = new javax.swing.JTable();
        txt_search_bagian = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        ComboBox_status = new javax.swing.JComboBox<>();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Sistem Penilaian Karyawan PEJUANG", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        button_refresh.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_refresh.setText("Refresh");
        button_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refreshActionPerformed(evt);
            }
        });

        txt_search_nama.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_nama.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_namaKeyPressed(evt);
            }
        });

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Tanggal :");

        Date1.setBackground(new java.awt.Color(255, 255, 255));
        Date1.setDate(new Date());
        Date1.setDateFormatString("dd MMM yyyy");
        Date1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date2.setBackground(new java.awt.Color(255, 255, 255));
        Date2.setDate(new Date());
        Date2.setDateFormatString("dd MMM yyyy");
        Date2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Nama :");

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jPanel_personal_hygene.setBackground(new java.awt.Color(255, 255, 255));

        Table_pelanggaran_ph.setAutoCreateRowSorter(true);
        Table_pelanggaran_ph.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Table_pelanggaran_ph.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Karyawan", "Nama", "Bagian", "Jumlah Pelanggaran", "Total Poin"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_pelanggaran_ph.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Table_pelanggaran_ph);

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText("Note : Setiap pelanggaran dikenakan 10 poin.");

        javax.swing.GroupLayout jPanel_personal_hygeneLayout = new javax.swing.GroupLayout(jPanel_personal_hygene);
        jPanel_personal_hygene.setLayout(jPanel_personal_hygeneLayout);
        jPanel_personal_hygeneLayout.setHorizontalGroup(
            jPanel_personal_hygeneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_personal_hygeneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_personal_hygeneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1331, Short.MAX_VALUE)
                    .addGroup(jPanel_personal_hygeneLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel_personal_hygeneLayout.setVerticalGroup(
            jPanel_personal_hygeneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_personal_hygeneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 642, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Personal Hygene", jPanel_personal_hygene);

        jPanel_absensi.setBackground(new java.awt.Color(255, 255, 255));

        Table_absensi.setAutoCreateRowSorter(true);
        Table_absensi.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Table_absensi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID pegawai", "Nama", "Bagian", "Jumlah Absen", "Total Poin"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_absensi.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(Table_absensi);

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("Note : Setiap pelanggaran dikenakan 50 poin.");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("Note : Masih membingungkan karena semua berjenis Absen, ada yang tanpa keterangan, ada yang dengan keterangan, yang mau terhitung poin yang mana?");

        javax.swing.GroupLayout jPanel_absensiLayout = new javax.swing.GroupLayout(jPanel_absensi);
        jPanel_absensi.setLayout(jPanel_absensiLayout);
        jPanel_absensiLayout.setHorizontalGroup(
            jPanel_absensiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_absensiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_absensiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1331, Short.MAX_VALUE)
                    .addGroup(jPanel_absensiLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel_absensiLayout.setVerticalGroup(
            jPanel_absensiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_absensiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_absensiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 642, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Absensi", jPanel_absensi);

        jPanel_keterlambatan.setBackground(new java.awt.Color(255, 255, 255));

        Table_keterlambatan.setAutoCreateRowSorter(true);
        Table_keterlambatan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Table_keterlambatan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID pegawai", "Nama", "Bagian", "Total Menit Terlambat", "Total Poin"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_keterlambatan.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(Table_keterlambatan);

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("Note : Setiap menit keterlambatan dikenakan 0.5 poin.");

        javax.swing.GroupLayout jPanel_keterlambatanLayout = new javax.swing.GroupLayout(jPanel_keterlambatan);
        jPanel_keterlambatan.setLayout(jPanel_keterlambatanLayout);
        jPanel_keterlambatanLayout.setHorizontalGroup(
            jPanel_keterlambatanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_keterlambatanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_keterlambatanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1331, Short.MAX_VALUE)
                    .addGroup(jPanel_keterlambatanLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel_keterlambatanLayout.setVerticalGroup(
            jPanel_keterlambatanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_keterlambatanLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 642, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Keterlambatan", jPanel_keterlambatan);

        jPanel_ijinKeluar.setBackground(new java.awt.Color(255, 255, 255));

        Table_ijin_keluar.setAutoCreateRowSorter(true);
        Table_ijin_keluar.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Table_ijin_keluar.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID pegawai", "Nama", "Bagian", "Total Menit Ijin", "Total Poin"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_ijin_keluar.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(Table_ijin_keluar);

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("Note : Setiap menit ijin dikenakan 0.5 poin.");

        javax.swing.GroupLayout jPanel_ijinKeluarLayout = new javax.swing.GroupLayout(jPanel_ijinKeluar);
        jPanel_ijinKeluar.setLayout(jPanel_ijinKeluarLayout);
        jPanel_ijinKeluarLayout.setHorizontalGroup(
            jPanel_ijinKeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_ijinKeluarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_ijinKeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 1331, Short.MAX_VALUE)
                    .addGroup(jPanel_ijinKeluarLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel_ijinKeluarLayout.setVerticalGroup(
            jPanel_ijinKeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_ijinKeluarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 642, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Ijin Keluar & Ijin Pulang", jPanel_ijinKeluar);

        jPanel_ijinKeluarRuangan.setBackground(new java.awt.Color(255, 255, 255));

        Table_ijin_keluar_ruangan.setAutoCreateRowSorter(true);
        Table_ijin_keluar_ruangan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Table_ijin_keluar_ruangan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID pegawai", "Nama", "Bagian", "Toilet", "Mushola", "Makan", "Lain2", "Total Poin"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        Table_ijin_keluar_ruangan.getTableHeader().setReorderingAllowed(false);
        jScrollPane6.setViewportView(Table_ijin_keluar_ruangan);

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText("Note : Setiap ijin ke Toilet hanya di batasi maksimal 30 menit, lebih dari itu setiap menit dikenakan 1 poin.");

        javax.swing.GroupLayout jPanel_ijinKeluarRuanganLayout = new javax.swing.GroupLayout(jPanel_ijinKeluarRuangan);
        jPanel_ijinKeluarRuangan.setLayout(jPanel_ijinKeluarRuanganLayout);
        jPanel_ijinKeluarRuanganLayout.setHorizontalGroup(
            jPanel_ijinKeluarRuanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_ijinKeluarRuanganLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_ijinKeluarRuanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 1331, Short.MAX_VALUE)
                    .addGroup(jPanel_ijinKeluarRuanganLayout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel_ijinKeluarRuanganLayout.setVerticalGroup(
            jPanel_ijinKeluarRuanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_ijinKeluarRuanganLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 642, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Ijin keluar Ruangan", jPanel_ijinKeluarRuangan);

        jPanel_lain2.setBackground(new java.awt.Color(255, 255, 255));

        Table_pelanggaran_lain2.setAutoCreateRowSorter(true);
        Table_pelanggaran_lain2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Table_pelanggaran_lain2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID pegawai", "Nama", "Bagian", "Pelanggaran Berat", "Pelanggaran Sedang", "Pelanggaran Ringan", "Total poin"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_pelanggaran_lain2.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(Table_pelanggaran_lain2);

        javax.swing.GroupLayout jPanel_lain2Layout = new javax.swing.GroupLayout(jPanel_lain2);
        jPanel_lain2.setLayout(jPanel_lain2Layout);
        jPanel_lain2Layout.setHorizontalGroup(
            jPanel_lain2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_lain2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4)
                .addContainerGap())
        );
        jPanel_lain2Layout.setVerticalGroup(
            jPanel_lain2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_lain2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 663, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Pelanggaran Lain2", jPanel_lain2);

        jPanel_total.setBackground(new java.awt.Color(255, 255, 255));

        Table_total_poin_pelanggaran.setAutoCreateRowSorter(true);
        Table_total_poin_pelanggaran.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Table_total_poin_pelanggaran.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID pegawai", "Nama", "Bagian", "Tgl masuk", "Total Poin"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_total_poin_pelanggaran.getTableHeader().setReorderingAllowed(false);
        jScrollPane8.setViewportView(Table_total_poin_pelanggaran);

        javax.swing.GroupLayout jPanel_totalLayout = new javax.swing.GroupLayout(jPanel_total);
        jPanel_total.setLayout(jPanel_totalLayout);
        jPanel_totalLayout.setHorizontalGroup(
            jPanel_totalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_totalLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane8)
                .addContainerGap())
        );
        jPanel_totalLayout.setVerticalGroup(
            jPanel_totalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_totalLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 663, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Total Poin Pelanggaran Karyawan", jPanel_total);

        txt_search_bagian.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_bagian.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_bagianKeyPressed(evt);
            }
        });

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("Status :");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel10.setText("Bagian :");

        ComboBox_status.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        ComboBox_status.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "IN", "OUT" }));
        ComboBox_status.setSelectedIndex(1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_search_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_search_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ComboBox_status, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_refresh)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_search_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_status, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1))
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

    private void button_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refreshActionPerformed
        // TODO add your handling code here:
        refreshAllTable();
    }//GEN-LAST:event_button_refreshActionPerformed

    private void txt_search_namaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_namaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshAllTable();
        }
    }//GEN-LAST:event_txt_search_namaKeyPressed

    private void txt_search_bagianKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_bagianKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshAllTable();
        }
    }//GEN-LAST:event_txt_search_bagianKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_status;
    private com.toedter.calendar.JDateChooser Date1;
    private com.toedter.calendar.JDateChooser Date2;
    private javax.swing.JTable Table_absensi;
    private javax.swing.JTable Table_ijin_keluar;
    private javax.swing.JTable Table_ijin_keluar_ruangan;
    private javax.swing.JTable Table_keterlambatan;
    private javax.swing.JTable Table_pelanggaran_lain2;
    private javax.swing.JTable Table_pelanggaran_ph;
    private javax.swing.JTable Table_total_poin_pelanggaran;
    private javax.swing.JButton button_refresh;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel_absensi;
    private javax.swing.JPanel jPanel_ijinKeluar;
    private javax.swing.JPanel jPanel_ijinKeluarRuangan;
    private javax.swing.JPanel jPanel_keterlambatan;
    private javax.swing.JPanel jPanel_lain2;
    private javax.swing.JPanel jPanel_personal_hygene;
    private javax.swing.JPanel jPanel_total;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField txt_search_bagian;
    private javax.swing.JTextField txt_search_nama;
    // End of variables declaration//GEN-END:variables
}
