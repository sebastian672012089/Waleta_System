package waleta_system.Finance;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultRowSorter;
import javax.swing.JOptionPane;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.DataPenggajian;
import waleta_system.Class.Utility;

public class JPanel_DataKinerjaCabut extends javax.swing.JPanel {

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

    public JPanel_DataKinerjaCabut() {
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
            try {
                Date tanggal_mulai = new Date(Date_penggajian.getDate().getTime() - (7 * 24 * 60 * 60 * 1000));
                Date tanggal_selesai = new Date(Date_penggajian.getDate().getTime() - (1 * 24 * 60 * 60 * 1000));
                label_periode.setText("Periode : " + new SimpleDateFormat("dd MMM yyyy").format(tanggal_mulai) + " - " + new SimpleDateFormat("dd MMM yyyy").format(tanggal_selesai));
                JUMLAH_HARI_DLM_SEMINGGU = Utility.countDaysWithoutFreeDays(tanggal_mulai, tanggal_selesai) + 1;
                txt_hari_kerja.setText(Integer.toString(JUMLAH_HARI_DLM_SEMINGGU));
                
                getDataPenggajianList(tanggal_mulai, tanggal_selesai);
                loadCabutanABCDE();
                loadDataTopBerat();
                loadDataTopUpah();
                loadDataAll();
                loadGrupRank();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex);
                Logger.getLogger(JPanel_DataKinerjaCabut.class.getName()).log(Level.SEVERE, null, ex);
            }
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
            sql = "SELECT `tgl_penggajian`, `tb_payrol_data`.`id_pegawai`, `nama_pegawai`, `bagian`, A.`jumlah_cabut`, A.`jumlah_gram`, A.`hari_kerja`, A.`bobot_lp`, `tb_payrol_data`.`level_gaji`, `tb_payrol_data`.`jam_kerja`, `tgl_penggajian`, `lembur`, `pot_terlambat`, `pot_ijin_keluar`, `pot_transport`, `pot_bpjs`, `pot_bpjs_tk`, `tunjangan_hadir`, `bonus_tbt`, `bonus_1`, `bonus_2`, `gaji_harian`, `gaji_borong`, `piutang`, `bonus_pencapaian_produksi`, `tb_payrol_data`.`keterangan` \n"
                    + "FROM `tb_payrol_data` \n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_payrol_data`.`id_pegawai` = `tb_karyawan`.`id_pegawai` \n"
                    + "LEFT JOIN (SELECT `id_pegawai`, SUM(`jumlah_cabut`) AS 'jumlah_cabut', SUM(`jumlah_gram`) AS 'jumlah_gram', COUNT(DISTINCT(`tanggal_cabut`)) AS 'hari_kerja', cast(SUM(`keping_upah`/`kpg_lp`) as decimal(8, 6)) AS 'bobot_lp' \n"
                    + "FROM `tb_detail_pencabut` \n"
                    + "LEFT JOIN `tb_cabut` ON `tb_detail_pencabut`.`no_laporan_produksi` = `tb_cabut`.`no_laporan_produksi` \n"
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_detail_pencabut`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah`\n"
                    + "WHERE `tb_cabut`.`tgl_setor_cabut` BETWEEN '" + dateFormat.format(tanggal_mulai) + "' AND '" + dateFormat.format(tanggal_selesai) + "' \n"
                    + "GROUP BY `id_pegawai`) "
                    + "A ON `tb_payrol_data`.`id_pegawai` = A.`id_pegawai` \n"
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
                        rs.getString("bagian"),
                        rs.getFloat("bobot_lp"),
                        rs.getInt("jumlah_cabut"),
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
            Logger.getLogger(JPanel_DataKinerjaCabut.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadCabutanABCDE() {
        DefaultTableModel model_tbA = (DefaultTableModel) Tabel_CabutA.getModel();
        model_tbA.setRowCount(0);
        DefaultTableModel model_tbB = (DefaultTableModel) Tabel_CabutB.getModel();
        model_tbB.setRowCount(0);
        DefaultTableModel model_tbC = (DefaultTableModel) Tabel_CabutC.getModel();
        model_tbC.setRowCount(0);
        DefaultTableModel model_tbD = (DefaultTableModel) Tabel_CabutD.getModel();
        model_tbD.setRowCount(0);
        DefaultTableModel model_tbE = (DefaultTableModel) Tabel_CabutE.getModel();
        model_tbE.setRowCount(0);

        Object[] row = new Object[20];
        int no_tbA = 0, no_tbB = 0, no_tbC = 0, no_tbD = 0, no_tbE = 0;
        for (int i = 0; i < DataPenggajianList.size(); i++) {
            row[0] = 0;
            row[1] = DataPenggajianList.get(i).getId();
            row[2] = DataPenggajianList.get(i).getNama();
            row[3] = DataPenggajianList.get(i).getGrup();
            row[4] = DataPenggajianList.get(i).getJumlah_keping();
            row[5] = DataPenggajianList.get(i).getJumlah_gram();
            row[6] = DataPenggajianList.get(i).getHari_kerja();
            row[7] = DataPenggajianList.get(i).getTunjHadir();
            row[8] = DataPenggajianList.get(i).getBonus1();
            row[9] = DataPenggajianList.get(i).getGaji();

            String grup = DataPenggajianList.get(i).getGrup();
            switch (grup.substring(grup.length() - 2, grup.length())) {
                case "-A":
                    no_tbA++;
                    row[0] = no_tbA;
                    model_tbA.addRow(row);
                    break;
                case "-B":
                    no_tbB++;
                    row[0] = no_tbB;
                    model_tbB.addRow(row);
                    break;
                case "-C":
                    no_tbC++;
                    row[0] = no_tbC;
                    model_tbC.addRow(row);
                    break;
                case "-D":
                    no_tbD++;
                    row[0] = no_tbD;
                    model_tbD.addRow(row);
                    break;
                case "-E":
                    no_tbE++;
                    row[0] = no_tbE;
                    model_tbE.addRow(row);
                    break;
                default:
                    break;
            }
        }
        ColumnsAutoSizer.sizeColumnsToFit(Tabel_CabutA);
        ColumnsAutoSizer.sizeColumnsToFit(Tabel_CabutB);
        ColumnsAutoSizer.sizeColumnsToFit(Tabel_CabutC);
        ColumnsAutoSizer.sizeColumnsToFit(Tabel_CabutD);
        ColumnsAutoSizer.sizeColumnsToFit(Tabel_CabutE);
    }

    private void loadDataTopBerat() {
        DefaultTableModel model_Top_Berat = (DefaultTableModel) Tabel_TOP_Berat.getModel();
        model_Top_Berat.setRowCount(0);

//        Step 1: Sort the DataPenggajianList based on "gaji"
        Collections.sort(DataPenggajianList, new Comparator<DataPenggajian>() {
            public int compare(DataPenggajian d1, DataPenggajian d2) {
                return Integer.compare(d2.getJumlah_gram(), d1.getJumlah_gram());
            }
        });

//        Step 2: Create a new list to store the top 20 data
        ArrayList<DataPenggajian> top20Data = new ArrayList<>();

//        Step 3: Iterate over the sorted DataPenggajianList and add the first 20 elements to the top20Data list
        int count = 0;
        for (DataPenggajian data : DataPenggajianList) {
            top20Data.add(data);
            count++;
            if (count == 20) {
                break;
            }
        }

        Object[] row = new Object[20];
        int nomor = 0;
        for (int i = 0; i < top20Data.size(); i++) {
            nomor++;
            row[0] = nomor;
            row[1] = top20Data.get(i).getId();
            row[2] = top20Data.get(i).getNama();
            row[3] = top20Data.get(i).getGrup();
            row[4] = top20Data.get(i).getHari_kerja();
            row[5] = top20Data.get(i).getJumlah_keping();
            row[6] = Math.round((float) top20Data.get(i).getJumlah_keping() / (float) top20Data.get(i).getHari_kerja() * 10f) / 10f;
            row[7] = top20Data.get(i).getJumlah_gram();
            row[8] = Math.round((float) top20Data.get(i).getJumlah_gram() / (float) top20Data.get(i).getHari_kerja() * 10f) / 10f;
            row[9] = top20Data.get(i).getTunjHadir();
            row[10] = top20Data.get(i).getGajiBorong();
            row[11] = top20Data.get(i).getBonus1();
            row[12] = top20Data.get(i).getGaji();
            model_Top_Berat.addRow(row);
        }
        ColumnsAutoSizer.sizeColumnsToFit(Tabel_TOP_Berat);
    }

    private void loadDataTopUpah() {
        DefaultTableModel model_Top_Upah = (DefaultTableModel) Tabel_TOP_Upah.getModel();
        model_Top_Upah.setRowCount(0);

//        Step 1: Sort the DataPenggajianList based on "gaji"
        Collections.sort(DataPenggajianList, new Comparator<DataPenggajian>() {
            public int compare(DataPenggajian d1, DataPenggajian d2) {
                return Integer.compare(d2.getGaji(), d1.getGaji());
            }
        });

//        Step 2: Create a new list to store the top 20 data
        ArrayList<DataPenggajian> top20Data = new ArrayList<>();

//        Step 3: Iterate over the sorted DataPenggajianList and add the first 20 elements to the top20Data list
        int count = 0;
        for (DataPenggajian data : DataPenggajianList) {
            top20Data.add(data);
            count++;
            if (count == 20) {
                break;
            }
        }

        Object[] row = new Object[20];
        int nomor = 0;
        for (int i = 0; i < top20Data.size(); i++) {
            nomor++;
            row[0] = nomor;
            row[1] = top20Data.get(i).getId();
            row[2] = top20Data.get(i).getNama();
            row[3] = top20Data.get(i).getGrup();
            row[4] = top20Data.get(i).getHari_kerja();
            row[5] = top20Data.get(i).getJumlah_keping();
            row[6] = Math.round((float) top20Data.get(i).getJumlah_keping() / (float) top20Data.get(i).getHari_kerja() * 10f) / 10f;
            row[7] = top20Data.get(i).getJumlah_gram();
            row[8] = Math.round((float) top20Data.get(i).getJumlah_gram() / (float) top20Data.get(i).getHari_kerja() * 10f) / 10f;
            row[9] = top20Data.get(i).getTunjHadir();
            row[10] = top20Data.get(i).getGajiBorong();
            row[11] = top20Data.get(i).getBonus1();
            row[12] = top20Data.get(i).getGaji();
            model_Top_Upah.addRow(row);
        }
        ColumnsAutoSizer.sizeColumnsToFit(Tabel_TOP_Upah);
    }

    private void loadDataAll() {
        DefaultTableModel model = (DefaultTableModel) Tabel_All.getModel();
        model.setRowCount(0);

        Object[] row = new Object[20];
        int nomor = 0;
        for (int i = 0; i < DataPenggajianList.size(); i++) {
            nomor++;
            row[0] = nomor;
            row[1] = DataPenggajianList.get(i).getId();
            row[2] = DataPenggajianList.get(i).getNama();
            row[3] = DataPenggajianList.get(i).getGrup();
            row[4] = DataPenggajianList.get(i).getHari_kerja();
            row[5] = DataPenggajianList.get(i).getJumlah_keping();
            row[6] = Math.round((float) DataPenggajianList.get(i).getJumlah_keping() / (float) DataPenggajianList.get(i).getHari_kerja() * 10f) / 10f;
            row[7] = DataPenggajianList.get(i).getJumlah_gram();
            row[8] = Math.round((float) DataPenggajianList.get(i).getJumlah_gram() / (float) DataPenggajianList.get(i).getHari_kerja() * 10f) / 10f;
            row[9] = DataPenggajianList.get(i).getTunjHadir();
            row[10] = DataPenggajianList.get(i).getGajiBorong();
            row[11] = DataPenggajianList.get(i).getBonus1();
            row[12] = DataPenggajianList.get(i).getGaji();
            model.addRow(row);
        }
        ColumnsAutoSizer.sizeColumnsToFit(Tabel_All);
    }

    private void loadGrupRank() {
        DefaultTableModel model = (DefaultTableModel) Tabel_GrupRank.getModel();
        model.setRowCount(0);

        Map<String, Float> bobot_lp_grup = new HashMap<>();
        Map<String, Integer> keping_grup = new HashMap<>();
        Map<String, Integer> gram_grup = new HashMap<>();
        Map<String, Set<String>> JumlahAnak = new HashMap<>();

        for (DataPenggajian data : DataPenggajianList) {
            String grup = data.getGrup();
            String nama = data.getNama();
            float bobot_lp = data.getBobot_lp();
            int jumlah_keping = data.getJumlah_keping();
            int jumlah_gram = data.getJumlah_gram();

            bobot_lp_grup.put(grup, bobot_lp_grup.getOrDefault(grup, 0f) + bobot_lp);
            keping_grup.put(grup, keping_grup.getOrDefault(grup, 0) + jumlah_keping);
            gram_grup.put(grup, gram_grup.getOrDefault(grup, 0) + jumlah_gram);
            // Check if the grup already exists in the map
            if (JumlahAnak.containsKey(grup)) {
                Set<String> distinctNama = JumlahAnak.get(grup);
                distinctNama.add(nama);
            } else {
                Set<String> distinctNama = new HashSet<>();
                distinctNama.add(nama);
                JumlahAnak.put(grup, distinctNama);
            }
        }

        Object[] row = new Object[20];
        for (String i : keping_grup.keySet()) {
            row[0] = 0;
            row[1] = i;
            row[2] = JumlahAnak.get(i).size();
            row[3] = keping_grup.get(i);
            row[4] = Math.round(((float) keping_grup.get(i) / (float) JumlahAnak.get(i).size()) / (float) JUMLAH_HARI_DLM_SEMINGGU);
            row[5] = gram_grup.get(i);
            row[6] = Math.round(((float) gram_grup.get(i) / (float) JumlahAnak.get(i).size()) / (float) JUMLAH_HARI_DLM_SEMINGGU);
            row[7] = Math.round(bobot_lp_grup.get(i) * 100f) / 100f;
            model.addRow(row);
        }
        ColumnsAutoSizer.sizeColumnsToFit(Tabel_GrupRank);

        // Get the table's row sorter
        DefaultRowSorter<?, ?> rowSorter = (DefaultRowSorter<?, ?>) Tabel_GrupRank.getRowSorter();

        // Set the sorting column index
        int columnIndex = 7; // Replace with the desired column index
        rowSorter.setSortKeys(Collections.singletonList(new RowSorter.SortKey(columnIndex, SortOrder.DESCENDING)));

        for (int i = 0; i < Tabel_GrupRank.getRowCount(); i++) {
            Tabel_GrupRank.setValueAt(i+1, i, 0);
        }
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
        Tabel_CabutA = new javax.swing.JTable();
        jPanel_B = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Tabel_CabutB = new javax.swing.JTable();
        jPanel_C = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        Tabel_CabutC = new javax.swing.JTable();
        jPanel_D = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        Tabel_CabutD = new javax.swing.JTable();
        jPanel_E = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        Tabel_CabutE = new javax.swing.JTable();
        jPanel_TOP_Berat = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        Tabel_TOP_Berat = new javax.swing.JTable();
        jPanel_TOP_Upah = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        Tabel_TOP_Upah = new javax.swing.JTable();
        jPanel_All = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        Tabel_All = new javax.swing.JTable();
        jPanel_GrupRank = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        Tabel_GrupRank = new javax.swing.JTable();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel1.setText("DATA KINERJA CABUT");

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

        Tabel_CabutA.setAutoCreateRowSorter(true);
        Tabel_CabutA.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "ID", "Nama", "Grup", "Kpg", "Gram", "Hari Kerja", "Tunj. hadir", "Bonus 1", "Gaji Transfer"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Tabel_CabutA.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Tabel_CabutA);

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

        jTabbedPane1.addTab("CABUT A", jPanel_A);

        jPanel_B.setBackground(new java.awt.Color(255, 255, 255));

        Tabel_CabutB.setAutoCreateRowSorter(true);
        Tabel_CabutB.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "ID", "Nama", "Grup", "Kpg", "Gram", "Hari Kerja", "Tunj. hadir", "Bonus 1", "Gaji Transfer"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Tabel_CabutB.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(Tabel_CabutB);

        javax.swing.GroupLayout jPanel_BLayout = new javax.swing.GroupLayout(jPanel_B);
        jPanel_B.setLayout(jPanel_BLayout);
        jPanel_BLayout.setHorizontalGroup(
            jPanel_BLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1341, Short.MAX_VALUE)
            .addGroup(jPanel_BLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1341, Short.MAX_VALUE))
        );
        jPanel_BLayout.setVerticalGroup(
            jPanel_BLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 561, Short.MAX_VALUE)
            .addGroup(jPanel_BLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 561, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("CABUT B", jPanel_B);

        jPanel_C.setBackground(new java.awt.Color(255, 255, 255));

        Tabel_CabutC.setAutoCreateRowSorter(true);
        Tabel_CabutC.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "ID", "Nama", "Grup", "Kpg", "Gram", "Hari Kerja", "Tunj. hadir", "Bonus 1", "Gaji Transfer"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Tabel_CabutC.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(Tabel_CabutC);

        javax.swing.GroupLayout jPanel_CLayout = new javax.swing.GroupLayout(jPanel_C);
        jPanel_C.setLayout(jPanel_CLayout);
        jPanel_CLayout.setHorizontalGroup(
            jPanel_CLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1341, Short.MAX_VALUE)
            .addGroup(jPanel_CLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1341, Short.MAX_VALUE))
        );
        jPanel_CLayout.setVerticalGroup(
            jPanel_CLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 561, Short.MAX_VALUE)
            .addGroup(jPanel_CLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 561, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("CABUT C", jPanel_C);

        jPanel_D.setBackground(new java.awt.Color(255, 255, 255));

        Tabel_CabutD.setAutoCreateRowSorter(true);
        Tabel_CabutD.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "ID", "Nama", "Grup", "Kpg", "Gram", "Hari Kerja", "Tunj. hadir", "Bonus 1", "Gaji Transfer"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Tabel_CabutD.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(Tabel_CabutD);

        javax.swing.GroupLayout jPanel_DLayout = new javax.swing.GroupLayout(jPanel_D);
        jPanel_D.setLayout(jPanel_DLayout);
        jPanel_DLayout.setHorizontalGroup(
            jPanel_DLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1341, Short.MAX_VALUE)
            .addGroup(jPanel_DLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 1341, Short.MAX_VALUE))
        );
        jPanel_DLayout.setVerticalGroup(
            jPanel_DLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 561, Short.MAX_VALUE)
            .addGroup(jPanel_DLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 561, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("CABUT D", jPanel_D);

        jPanel_E.setBackground(new java.awt.Color(255, 255, 255));

        Tabel_CabutE.setAutoCreateRowSorter(true);
        Tabel_CabutE.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "ID", "Nama", "Grup", "Kpg", "Gram", "Hari Kerja", "Tunj. hadir", "Bonus 1", "Gaji Transfer"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Tabel_CabutE.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(Tabel_CabutE);

        javax.swing.GroupLayout jPanel_ELayout = new javax.swing.GroupLayout(jPanel_E);
        jPanel_E.setLayout(jPanel_ELayout);
        jPanel_ELayout.setHorizontalGroup(
            jPanel_ELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1341, Short.MAX_VALUE)
            .addGroup(jPanel_ELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 1341, Short.MAX_VALUE))
        );
        jPanel_ELayout.setVerticalGroup(
            jPanel_ELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 561, Short.MAX_VALUE)
            .addGroup(jPanel_ELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 561, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("CABUT E", jPanel_E);

        jPanel_TOP_Berat.setBackground(new java.awt.Color(255, 255, 255));

        Tabel_TOP_Berat.setAutoCreateRowSorter(true);
        Tabel_TOP_Berat.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "ID", "Nama", "Grup", "Hari Kerja", "Kpg", "AVG Kpg", "Gram", "AVG Gram", "Tunj. hadir", "Gaji Borong", "Bonus 1", "Gaji Transfer"
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
        Tabel_TOP_Berat.getTableHeader().setReorderingAllowed(false);
        jScrollPane6.setViewportView(Tabel_TOP_Berat);

        javax.swing.GroupLayout jPanel_TOP_BeratLayout = new javax.swing.GroupLayout(jPanel_TOP_Berat);
        jPanel_TOP_Berat.setLayout(jPanel_TOP_BeratLayout);
        jPanel_TOP_BeratLayout.setHorizontalGroup(
            jPanel_TOP_BeratLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1341, Short.MAX_VALUE)
            .addGroup(jPanel_TOP_BeratLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 1341, Short.MAX_VALUE))
        );
        jPanel_TOP_BeratLayout.setVerticalGroup(
            jPanel_TOP_BeratLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 561, Short.MAX_VALUE)
            .addGroup(jPanel_TOP_BeratLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 561, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("TOP 20 - Berat", jPanel_TOP_Berat);

        jPanel_TOP_Upah.setBackground(new java.awt.Color(255, 255, 255));

        Tabel_TOP_Upah.setAutoCreateRowSorter(true);
        Tabel_TOP_Upah.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "ID", "Nama", "Grup", "Hari Kerja", "Kpg", "AVG Kpg", "Gram", "AVG Gram", "Tunj. hadir", "Gaji Borong", "Bonus 1", "Gaji Transfer"
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
        Tabel_TOP_Upah.getTableHeader().setReorderingAllowed(false);
        jScrollPane7.setViewportView(Tabel_TOP_Upah);

        javax.swing.GroupLayout jPanel_TOP_UpahLayout = new javax.swing.GroupLayout(jPanel_TOP_Upah);
        jPanel_TOP_Upah.setLayout(jPanel_TOP_UpahLayout);
        jPanel_TOP_UpahLayout.setHorizontalGroup(
            jPanel_TOP_UpahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1341, Short.MAX_VALUE)
            .addGroup(jPanel_TOP_UpahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 1341, Short.MAX_VALUE))
        );
        jPanel_TOP_UpahLayout.setVerticalGroup(
            jPanel_TOP_UpahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 561, Short.MAX_VALUE)
            .addGroup(jPanel_TOP_UpahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 561, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("TOP 20 - Upah", jPanel_TOP_Upah);

        jPanel_All.setBackground(new java.awt.Color(255, 255, 255));

        Tabel_All.setAutoCreateRowSorter(true);
        Tabel_All.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "ID", "Nama", "Grup", "Hari Kerja", "Kpg", "AVG Kpg", "Gram", "AVG Gram", "Tunj. hadir", "Gaji Borong", "Bonus 1", "Gaji Transfer"
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
        Tabel_All.getTableHeader().setReorderingAllowed(false);
        jScrollPane8.setViewportView(Tabel_All);

        javax.swing.GroupLayout jPanel_AllLayout = new javax.swing.GroupLayout(jPanel_All);
        jPanel_All.setLayout(jPanel_AllLayout);
        jPanel_AllLayout.setHorizontalGroup(
            jPanel_AllLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1341, Short.MAX_VALUE)
            .addGroup(jPanel_AllLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 1341, Short.MAX_VALUE))
        );
        jPanel_AllLayout.setVerticalGroup(
            jPanel_AllLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 561, Short.MAX_VALUE)
            .addGroup(jPanel_AllLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 561, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("All Data", jPanel_All);

        jPanel_GrupRank.setBackground(new java.awt.Color(255, 255, 255));

        Tabel_GrupRank.setAutoCreateRowSorter(true);
        Tabel_GrupRank.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Grup", "Jmlh Anak", "Kpg", "Kpg/Anak/Hari", "Gram", "Gram/Anak/Hari", "Bobot LP"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        Tabel_GrupRank.getTableHeader().setReorderingAllowed(false);
        jScrollPane9.setViewportView(Tabel_GrupRank);

        javax.swing.GroupLayout jPanel_GrupRankLayout = new javax.swing.GroupLayout(jPanel_GrupRank);
        jPanel_GrupRank.setLayout(jPanel_GrupRankLayout);
        jPanel_GrupRankLayout.setHorizontalGroup(
            jPanel_GrupRankLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1341, Short.MAX_VALUE)
            .addGroup(jPanel_GrupRankLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 1341, Short.MAX_VALUE))
        );
        jPanel_GrupRankLayout.setVerticalGroup(
            jPanel_GrupRankLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 561, Short.MAX_VALUE)
            .addGroup(jPanel_GrupRankLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 561, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Grup Rank", jPanel_GrupRank);

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
    private javax.swing.JTable Tabel_All;
    private javax.swing.JTable Tabel_CabutA;
    private javax.swing.JTable Tabel_CabutB;
    private javax.swing.JTable Tabel_CabutC;
    private javax.swing.JTable Tabel_CabutD;
    private javax.swing.JTable Tabel_CabutE;
    private javax.swing.JTable Tabel_GrupRank;
    private javax.swing.JTable Tabel_TOP_Berat;
    private javax.swing.JTable Tabel_TOP_Upah;
    private javax.swing.JButton button_load;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel_A;
    private javax.swing.JPanel jPanel_All;
    private javax.swing.JPanel jPanel_B;
    private javax.swing.JPanel jPanel_C;
    private javax.swing.JPanel jPanel_D;
    private javax.swing.JPanel jPanel_E;
    private javax.swing.JPanel jPanel_GrupRank;
    private javax.swing.JPanel jPanel_TOP_Berat;
    private javax.swing.JPanel jPanel_TOP_Upah;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label_periode;
    private javax.swing.JTextField txt_hari_kerja;
    // End of variables declaration//GEN-END:variables
}
