package waleta_system.Finance;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.DataPenggajian;
import waleta_system.Class.Utility;

public class JPanel_DataKinerjaCetak extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date today = new Date();
    PreparedStatement pst;
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    ArrayList<DataPenggajian> DataPenggajianList = new ArrayList<>();
    int JUMLAH_HARI_DLM_SEMINGGU = 0;

    int total_lembur = 0, total_pot_terlambat = 0, total_pot_ijin_keluar = 0, total_pot_transport = 0, total_pot_bpjs = 0, total_pot_bpjs_tk = 0, total_tunjangan_hadir = 0,
            total_gaji_harian = 0, total_gaji_borong = 0, total_bonus_tbt = 0, total_bonus_1 = 0, total_bonus_2 = 0, total_bonus_pencapaian_produksi = 0, total_piutang = 0, total_gaji = 0;

    public JPanel_DataKinerjaCetak() {
        initComponents();
    }

    public void init() {

    }

    private void refreshTable() {
        boolean check = true;
        if (Date_penggajian.getDate() == null) {
            check = false;
            JOptionPane.showMessageDialog(this, "Harap masukkan tanggal penggajian");
        } else if (!(new SimpleDateFormat("EEEEE").format(Date_penggajian.getDate()).toUpperCase().equals("THURSDAY") || new SimpleDateFormat("EEEEE").format(Date_penggajian.getDate()).toUpperCase().equals("KAMIS"))) {
            check = false;
            JOptionPane.showMessageDialog(this, "Tanggal penggajian seharusnya hari kamis");
        }

        if (check) {
            Date tanggal_mulai = new Date(Date_penggajian.getDate().getTime() - (7 * 24 * 60 * 60 * 1000));
            Date tanggal_selesai = new Date(Date_penggajian.getDate().getTime() - (1 * 24 * 60 * 60 * 1000));
            label_periode.setText("Periode : " + new SimpleDateFormat("dd MMM yyyy").format(tanggal_mulai) + " - " + new SimpleDateFormat("dd MMM yyyy").format(tanggal_selesai));
            JUMLAH_HARI_DLM_SEMINGGU = Utility.countDaysWithoutFreeDays(tanggal_mulai, tanggal_selesai) + 1;
            txt_hari_kerja.setText(Integer.toString(JUMLAH_HARI_DLM_SEMINGGU));

            getDataPenggajianList(tanggal_mulai, tanggal_selesai);
            Refresh_RankUpahBorong();
            Refresh_RankKinerjaBorong();
            Refresh_RankKinerjaMandiri();
        }
    }

    private void getDataPenggajianList(Date tanggal_mulai, Date tanggal_selesai) {
        try {
            DataPenggajianList.clear();
            total_lembur = 0;
            total_pot_terlambat = 0;
            total_pot_ijin_keluar = 0;
            total_pot_transport = 0;
            total_pot_bpjs = 0;
            total_pot_bpjs_tk = 0;
            total_tunjangan_hadir = 0;
            total_gaji_harian = 0;
            total_gaji_borong = 0;
            total_bonus_tbt = 0;
            total_bonus_1 = 0;
            total_bonus_2 = 0;
            total_bonus_pencapaian_produksi = 0;
            total_piutang = 0;
            total_gaji = 0;
            sql = "SELECT `tgl_penggajian`, `tb_payrol_data`.`id_pegawai`, `nama_pegawai`, `tb_payrol_data`.`bagian`, `tb_payrol_data`.`level_gaji`, A.`jumlah_keping`, A.`jumlah_gram`, A.`bobot_lp`, `tb_payrol_data`.`level_gaji`, `tb_payrol_data`.`jam_kerja`, `tgl_penggajian`, `lembur`, `pot_terlambat`, `pot_ijin_keluar`, `pot_transport`, `pot_bpjs`, `pot_bpjs_tk`, `tunjangan_hadir`, `bonus_tbt`, `bonus_1`, `bonus_2`, `gaji_harian`, `gaji_borong`, `piutang`, `bonus_pencapaian_produksi`, `tb_payrol_data`.`keterangan`, \n"
                    + "(SELECT COUNT(`tanggal`) AS 'hari_kerja' FROM `tb_lembur_rekap` WHERE `tb_lembur_rekap`.`id_pegawai` = `tb_payrol_data`.`id_pegawai` AND `premi_hadir` > 0 AND `tanggal` BETWEEN '" + dateFormat.format(tanggal_mulai) + "' AND '" + dateFormat.format(tanggal_selesai) + "' ) AS 'hari_kerja' "
                    + "FROM `tb_payrol_data` \n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_payrol_data`.`id_pegawai` = `tb_karyawan`.`id_pegawai` \n"
                    + "LEFT JOIN (SELECT `cetak_dikerjakan`, SUM(`tb_laporan_produksi`.`jumlah_keping`) AS 'jumlah_keping', "
                    + "SUM(`tb_laporan_produksi`.`berat_basah`) AS 'jumlah_gram', "
                    + "cast(SUM(`keping_upah`/`kpg_lp`) as decimal(8, 6)) AS 'bobot_lp'\n"
                    + "FROM `tb_cetak` \n"
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah`\n"
                    + "WHERE `tgl_selesai_cetak` BETWEEN '" + dateFormat.format(tanggal_mulai) + "' AND '" + dateFormat.format(tanggal_selesai) + "'\n"
                    + "GROUP BY `cetak_dikerjakan`) "
                    + "A ON `tb_payrol_data`.`id_pegawai` = A.`cetak_dikerjakan` \n"
                    + "WHERE \n"
                    + "`tgl_penggajian` = '" + dateFormat.format(Date_penggajian.getDate()) + "' \n"
                    + "AND A.`jumlah_gram` > 0 \n"
                    + "ORDER BY `nama_pegawai`";
            pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            DataPenggajian Data;
            while (rs.next()) {
                int gaji_transfer = rs.getInt("lembur")
                        - rs.getInt("pot_terlambat")
                        - rs.getInt("pot_ijin_keluar")
                        - rs.getInt("pot_transport")
                        - rs.getInt("pot_bpjs")
                        - rs.getInt("pot_bpjs_tk")
                        + rs.getInt("tunjangan_hadir")
                        + rs.getInt("gaji_harian")
                        + rs.getInt("gaji_borong")
                        + rs.getInt("bonus_1")
                        + rs.getInt("bonus_2")
                        + rs.getInt("bonus_pencapaian_produksi")
                        + rs.getInt("bonus_tbt")
                        + rs.getInt("piutang");
                Data = new DataPenggajian(
                        rs.getString("id_pegawai"),
                        rs.getString("nama_pegawai"),
                        rs.getString("level_gaji"),
                        rs.getFloat("bobot_lp"),
                        rs.getInt("jumlah_keping"),
                        rs.getInt("jumlah_gram"),
                        rs.getInt("hari_kerja"),
                        rs.getInt("gaji_borong"),
                        rs.getInt("gaji_harian"),
                        rs.getInt("tunjangan_hadir"),
                        rs.getInt("bonus_1"),
                        rs.getInt("bonus_2"),
                        rs.getInt("bonus_pencapaian_produksi"),
                        rs.getInt("bonus_tbt"),
                        rs.getInt("lembur"),
                        rs.getInt("piutang"),
                        rs.getInt("pot_terlambat"),
                        rs.getInt("pot_ijin_keluar"),
                        rs.getInt("pot_transport"),
                        rs.getInt("pot_bpjs"),
                        rs.getInt("pot_bpjs_tk"),
                        gaji_transfer
                );
                DataPenggajianList.add(Data);

                total_lembur = total_lembur + rs.getInt("lembur");
                total_pot_terlambat = total_pot_terlambat + rs.getInt("pot_terlambat");
                total_pot_ijin_keluar = total_pot_ijin_keluar + rs.getInt("pot_ijin_keluar");
                total_pot_transport = total_pot_transport + rs.getInt("pot_transport");
                total_pot_bpjs = total_pot_bpjs + rs.getInt("pot_bpjs");
                total_pot_bpjs_tk = total_pot_bpjs_tk + rs.getInt("pot_bpjs_tk");
                total_tunjangan_hadir = total_tunjangan_hadir + rs.getInt("tunjangan_hadir");
                total_gaji_harian = total_gaji_harian + rs.getInt("gaji_harian");
                total_gaji_borong = total_gaji_borong + rs.getInt("gaji_borong");
                total_bonus_tbt = total_bonus_tbt + rs.getInt("bonus_tbt");
                total_bonus_1 = total_bonus_1 + rs.getInt("bonus_1");
                total_bonus_2 = total_bonus_2 + rs.getInt("bonus_2");
                total_bonus_pencapaian_produksi = total_bonus_pencapaian_produksi + rs.getInt("bonus_pencapaian_produksi");
                total_piutang = total_piutang + rs.getInt("piutang");
                total_gaji = total_gaji + gaji_transfer;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataKinerjaCetak.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void Refresh_RankUpahBorong() {
        DefaultTableModel model = (DefaultTableModel) Tabel_RankUpahBorong.getModel();
        model.setRowCount(0);

//        Step 1: Sort the DataPenggajianList based on "gaji"
        Collections.sort(DataPenggajianList, new Comparator<DataPenggajian>() {
            @Override
            public int compare(DataPenggajian d1, DataPenggajian d2) {
                return Integer.compare(d2.getGaji(), d1.getGaji());
            }
        });

//        Step 2: Create a new list to store the top 20 data
        ArrayList<DataPenggajian> Data = new ArrayList<>();

//        Step 3: Iterate over the sorted DataPenggajianList and add the first 20 elements to the top20Data list
        for (DataPenggajian data : DataPenggajianList) {
            if (data.getGrup().contains("BRG")) {
                Data.add(data);
            }
        }

        Object[] row = new Object[20];
        int nomor = 0;
        for (int i = 0; i < Data.size(); i++) {
            nomor++;
            row[0] = nomor;
            row[1] = Data.get(i).getId();
            row[2] = Data.get(i).getNama();
            row[3] = Data.get(i).getGrup();
            row[4] = Math.round(Data.get(i).getBobot_lp() * 100f) / 100f;
            row[5] = Math.round((float) Data.get(i).getJumlah_keping() / (float) Data.get(i).getHari_kerja() * 100f) / 100f;
            row[6] = Data.get(i).getJumlah_keping();
            row[7] = Data.get(i).getHari_kerja();
            row[8] = Data.get(i).getGajiBorong();
            row[9] = Data.get(i).getTunjHadir();
            row[10] = Data.get(i).getBonus1();
            row[11] = Data.get(i).getPot_bpjs() + Data.get(i).getPot_bpjs_tk() + Data.get(i).getPot_ijin_keluar() + Data.get(i).getPot_terlambat() + Data.get(i).getPot_transport();
            row[12] = Data.get(i).getGaji();
            model.addRow(row);
        }
        ColumnsAutoSizer.sizeColumnsToFit(Tabel_RankUpahBorong);
    }
    
    private void Refresh_RankKinerjaBorong() {
        DefaultTableModel model = (DefaultTableModel) Tabel_RankKinerjaBorong.getModel();
        model.setRowCount(0);

//        Step 1: Sort the DataPenggajianList based on "gaji"
        Collections.sort(DataPenggajianList, new Comparator<DataPenggajian>() {
            @Override
            public int compare(DataPenggajian d1, DataPenggajian d2) {
                return Float.compare(d2.getBobot_lp(), d1.getBobot_lp());
            }
        });

//        Step 2: Create a new list to store the top 20 data
        ArrayList<DataPenggajian> Data = new ArrayList<>();

//        Step 3: Iterate over the sorted DataPenggajianList and add the first 20 elements to the top20Data list
        for (DataPenggajian data : DataPenggajianList) {
            if (data.getGrup().contains("BRG")) {
                Data.add(data);
            }
        }

        Object[] row = new Object[20];
        int nomor = 0;
        for (int i = 0; i < Data.size(); i++) {
            nomor++;
            row[0] = nomor;
            row[1] = Data.get(i).getId();
            row[2] = Data.get(i).getNama();
            row[3] = Data.get(i).getGrup();
            row[4] = Math.round(Data.get(i).getBobot_lp() * 100f) / 100f;
            row[5] = Math.round((float) Data.get(i).getJumlah_keping() / (float) Data.get(i).getHari_kerja() * 100f) / 100f;
            row[6] = Data.get(i).getJumlah_keping();
            row[7] = Data.get(i).getHari_kerja();
            row[8] = Data.get(i).getGajiBorong();
            row[9] = Data.get(i).getTunjHadir();
            row[10] = Data.get(i).getBonus1();
            row[11] = Data.get(i).getPot_bpjs() + Data.get(i).getPot_bpjs_tk() + Data.get(i).getPot_ijin_keluar() + Data.get(i).getPot_terlambat() + Data.get(i).getPot_transport();
            row[12] = Data.get(i).getGaji();
            model.addRow(row);
        }
        ColumnsAutoSizer.sizeColumnsToFit(Tabel_RankKinerjaBorong);
    }

    private void Refresh_RankKinerjaMandiri() {
        DefaultTableModel model = (DefaultTableModel) Tabel_RankKinerjaMandiri.getModel();
        model.setRowCount(0);

//        Step 1: Sort the DataPenggajianList based on "gaji"
        Collections.sort(DataPenggajianList, new Comparator<DataPenggajian>() {
            @Override
            public int compare(DataPenggajian d1, DataPenggajian d2) {
                return Float.compare(d2.getBobot_lp(), d1.getBobot_lp());
            }
        });

//        Step 2: Create a new list to store the top 20 data
        ArrayList<DataPenggajian> Data = new ArrayList<>();

//        Step 3: Iterate over the sorted DataPenggajianList and add the first 20 elements to the top20Data list
        for (DataPenggajian data : DataPenggajianList) {
            if (!data.getGrup().contains("BRG")) {
                Data.add(data);
            }
        }

        Object[] row = new Object[20];
        int nomor = 0;
        for (int i = 0; i < Data.size(); i++) {
            nomor++;
            row[0] = nomor;
            row[1] = Data.get(i).getId();
            row[2] = Data.get(i).getNama();
            row[3] = Data.get(i).getGrup();
            row[4] = Math.round(Data.get(i).getBobot_lp() * 100f) / 100f;
            row[5] = Math.round((float) Data.get(i).getJumlah_keping() / (float) Data.get(i).getHari_kerja() * 100f) / 100f;
            row[6] = Data.get(i).getJumlah_keping();
            row[7] = Data.get(i).getHari_kerja();
            row[8] = Data.get(i).getGajiBorong();
            row[9] = Data.get(i).getTunjHadir();
            row[10] = Data.get(i).getBonus1();
            row[11] = Data.get(i).getPot_bpjs() + Data.get(i).getPot_bpjs_tk() + Data.get(i).getPot_ijin_keluar() + Data.get(i).getPot_terlambat() + Data.get(i).getPot_transport();
            row[12] = Data.get(i).getGaji();
            model.addRow(row);
        }
        ColumnsAutoSizer.sizeColumnsToFit(Tabel_RankKinerjaMandiri);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        Date_penggajian = new com.toedter.calendar.JDateChooser();
        button_load = new javax.swing.JButton();
        label_periode = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        txt_hari_kerja = new javax.swing.JTextField();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel_A = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Tabel_RankUpahBorong = new javax.swing.JTable();
        jPanel_B = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        Tabel_RankKinerjaBorong = new javax.swing.JTable();
        jPanel_C = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        Tabel_RankKinerjaMandiri = new javax.swing.JTable();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel1.setText("DATA KINERJA CETAK");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("Tanggal Penggajian :");

        Date_penggajian.setBackground(new java.awt.Color(255, 255, 255));
        Date_penggajian.setDateFormatString("dd MMMM yyyy");
        Date_penggajian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_load.setBackground(new java.awt.Color(255, 255, 255));
        button_load.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_load.setText("Refresh");
        button_load.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_loadActionPerformed(evt);
            }
        });

        label_periode.setBackground(new java.awt.Color(255, 255, 255));
        label_periode.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_periode.setText("Periode :");

        jLabel32.setBackground(new java.awt.Color(255, 255, 255));
        jLabel32.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel32.setText("Hari Kerja :");

        txt_hari_kerja.setEditable(false);
        txt_hari_kerja.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N

        jPanel_A.setBackground(new java.awt.Color(255, 255, 255));

        Tabel_RankUpahBorong.setAutoCreateRowSorter(true);
        Tabel_RankUpahBorong.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Rank", "ID", "Nama", "Kelompok", "Total LP", "Avg Kpg/hari", "Kpg/Minggu", "Jumlah Hadir", "Upah Borong", "Tunj. hadir", "Bonus Kecepatan", "Potongan", "Gaji Transfer"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Tabel_RankUpahBorong.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Tabel_RankUpahBorong);

        javax.swing.GroupLayout jPanel_ALayout = new javax.swing.GroupLayout(jPanel_A);
        jPanel_A.setLayout(jPanel_ALayout);
        jPanel_ALayout.setHorizontalGroup(
            jPanel_ALayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1341, Short.MAX_VALUE)
            .addGroup(jPanel_ALayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1341, Short.MAX_VALUE))
        );
        jPanel_ALayout.setVerticalGroup(
            jPanel_ALayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 561, Short.MAX_VALUE)
            .addGroup(jPanel_ALayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 561, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("RANK UPAH BORONG", jPanel_A);

        jPanel_B.setBackground(new java.awt.Color(255, 255, 255));

        Tabel_RankKinerjaBorong.setAutoCreateRowSorter(true);
        Tabel_RankKinerjaBorong.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Rank", "ID", "Nama", "Kelompok", "Total LP", "Avg Kpg/hari", "Kpg/Minggu", "Jumlah Hadir", "Upah Borong", "Tunj. hadir", "Bonus Kecepatan", "Potongan", "Gaji Transfer"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Tabel_RankKinerjaBorong.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(Tabel_RankKinerjaBorong);

        javax.swing.GroupLayout jPanel_BLayout = new javax.swing.GroupLayout(jPanel_B);
        jPanel_B.setLayout(jPanel_BLayout);
        jPanel_BLayout.setHorizontalGroup(
            jPanel_BLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1341, Short.MAX_VALUE)
            .addGroup(jPanel_BLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 1341, Short.MAX_VALUE))
        );
        jPanel_BLayout.setVerticalGroup(
            jPanel_BLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 561, Short.MAX_VALUE)
            .addGroup(jPanel_BLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 561, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("RANK KINERJA BORONG", jPanel_B);

        jPanel_C.setBackground(new java.awt.Color(255, 255, 255));

        Tabel_RankKinerjaMandiri.setAutoCreateRowSorter(true);
        Tabel_RankKinerjaMandiri.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Rank", "ID", "Nama", "Kelompok", "Total LP", "Avg Kpg/hari", "Kpg/Minggu", "Jumlah Hadir", "Upah Borong", "Tunj. hadir", "Bonus Kecepatan", "Potongan", "Gaji Transfer"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Tabel_RankKinerjaMandiri.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(Tabel_RankKinerjaMandiri);

        javax.swing.GroupLayout jPanel_CLayout = new javax.swing.GroupLayout(jPanel_C);
        jPanel_C.setLayout(jPanel_CLayout);
        jPanel_CLayout.setHorizontalGroup(
            jPanel_CLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1341, Short.MAX_VALUE)
            .addGroup(jPanel_CLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 1341, Short.MAX_VALUE))
        );
        jPanel_CLayout.setVerticalGroup(
            jPanel_CLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 561, Short.MAX_VALUE)
            .addGroup(jPanel_CLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 561, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("RANK KINERJA MANDIRI", jPanel_C);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(label_periode)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_penggajian, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_load)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel32)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_hari_kerja, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_penggajian, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_load, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_hari_kerja, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_periode, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1)
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

    private void button_loadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_loadActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_button_loadActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser Date_penggajian;
    private javax.swing.JTable Tabel_RankKinerjaBorong;
    private javax.swing.JTable Tabel_RankKinerjaMandiri;
    private javax.swing.JTable Tabel_RankUpahBorong;
    private javax.swing.JButton button_load;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel_A;
    private javax.swing.JPanel jPanel_B;
    private javax.swing.JPanel jPanel_C;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label_periode;
    private javax.swing.JTextField txt_hari_kerja;
    // End of variables declaration//GEN-END:variables
}
