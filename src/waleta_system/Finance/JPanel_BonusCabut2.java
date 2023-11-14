package waleta_system.Finance;

import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;
import waleta_system.Class.Utility;
import waleta_system.Interface.InterfacePanel;

public class JPanel_BonusCabut2 extends javax.swing.JPanel implements InterfacePanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    DecimalFormat decimalFormat = new DecimalFormat();
    double total_bonus_lp_rekap_karyawan = 0, total_bonus_lp2_rekap_karyawan = 0, total_gram_cabutan;
    int max_orang = 0;
    float bobot_lp_min = 0;

    public JPanel_BonusCabut2() {
        initComponents();
    }
    
    @Override
    public void init() {
        
    }

    public void refreshTable_bonusLP() {
        try {
//            int total_kpg = 0;
            DefaultTableModel model = (DefaultTableModel) table_bonus_kecepatan.getModel();
            model.setRowCount(0);
            DefaultTableModel model_rekap1 = (DefaultTableModel) table_data_rekap_bonus1.getModel();
            model_rekap1.setRowCount(0);
            DefaultTableModel model_rekap2 = (DefaultTableModel) table_data_rekap_bonus2.getModel();
            model_rekap2.setRowCount(0);
            int bonus_kecepatan_per_LP = Integer.valueOf(txt_bonus_kecepatan_lp.getText());
            max_orang = Integer.valueOf(txt_bonus_max_orang.getText());
            bobot_lp_min = Float.valueOf(txt_bobot_min.getText());
            int total_bonus_per_bagian = 0;
            total_bonus_lp_rekap_karyawan = 0;
            total_bonus_lp2_rekap_karyawan = 0;
            total_gram_cabutan = 0;
            sql = "SELECT y.`nama_bagian`, `tb_laporan_produksi`.`no_laporan_produksi`, `jenis_bulu_lp`, `keping_upah`, `berat_basah`, cast((`keping_upah`/`kpg_lp`) as decimal(8, 6)) AS 'bobot_lp',  y.`count_bagian`, y.`jumlah_pencabut`, "
                    + "`tb_cuci`.`tgl_masuk_cuci`, `tb_cabut`.`tgl_cabut`, `tb_cabut`.`tgl_setor_cabut`, `tb_cetak`.`tgl_mulai_cetak`, DATEDIFF(`tb_cetak`.`tgl_mulai_cetak`, `tb_cuci`.`tgl_masuk_cuci`) AS 'cuci_ke_cetak'\n"
                    + "FROM `tb_laporan_produksi`\n"
                    + "LEFT JOIN `tb_cabut` ON `tb_laporan_produksi`.`no_laporan_produksi` =`tb_cabut`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_cuci` ON `tb_laporan_produksi`.`no_laporan_produksi` =`tb_cuci`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_cetak` ON `tb_laporan_produksi`.`no_laporan_produksi` =`tb_cetak`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` =`tb_tarif_cabut`.`bulu_upah`\n"
                    + "LEFT JOIN "
                    + "(SELECT `no_laporan_produksi`, CONCAT(`divisi_bagian`, '-', `ruang_bagian`) AS 'nama_bagian', "
                    + "COUNT(DISTINCT(CONCAT(`divisi_bagian`, '-', `ruang_bagian`))) AS 'count_bagian', "
                    + "COUNT(DISTINCT(`id_pegawai`)) AS 'jumlah_pencabut' \n"
                    + "FROM `tb_detail_pencabut` "
                    + "LEFT JOIN `tb_bagian` ON `tb_detail_pencabut`.`kode_bagian` = `tb_bagian`.`kode_bagian` "
                    + "GROUP BY `no_laporan_produksi`) "
                    + "y ON `tb_laporan_produksi`.`no_laporan_produksi` = y.`no_laporan_produksi`\n"
                    + "WHERE `tb_laporan_produksi`.`no_laporan_produksi` LIKE 'WL-%'"
                    + "AND `tb_cabut`.`tgl_setor_cabut` BETWEEN '" + dateFormat.format(Date_bonus1.getDate()) + "' AND '" + dateFormat.format(Date_bonus2.getDate()) + "' \n"
                    + "AND `tb_cetak`.`tgl_mulai_cetak` IS NOT NULL \n"
//                    + "AND y.`count_bagian` = 1 "
                    + "AND  y.`jumlah_pencabut` <= " + max_orang + " "
                    + "AND `tb_cabut`.`tgl_cabut` = `tb_cabut`.`tgl_setor_cabut` "
                    + "AND y.`nama_bagian` LIKE '%CABUT%' "
                    + "AND y.`nama_bagian` LIKE '%" + txt_filter_bagian_bonus_lp.getText() + "%' "
                    + "ORDER BY `tb_cabut`.`tgl_setor_cabut`, y.`nama_bagian`";
//            System.out.println(sql);
            PreparedStatement pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            Object[] row = new Object[15];
            Object[] row_rekap = new Object[4];
            String tgl = "", bagian = "";
            double bobot_lp = 0;
            ArrayList<Integer> hari_bonus = new ArrayList();
            ArrayList<String> nama_bagian = new ArrayList();
            decimalFormat.setMaximumFractionDigits(7);
            while (rs.next()) {
                row[0] = rs.getString("nama_bagian");
                row[1] = rs.getString("no_laporan_produksi");
                row[2] = rs.getString("jenis_bulu_lp");
                row[3] = rs.getInt("keping_upah");
                row[4] = rs.getInt("berat_basah");
                row[5] = rs.getString("bobot_lp");
                row[6] = rs.getInt("jumlah_pencabut");
                row[7] = rs.getDate("tgl_masuk_cuci");
                row[8] = rs.getDate("tgl_cabut");
                row[9] = rs.getDate("tgl_setor_cabut");
                row[10] = rs.getDate("tgl_mulai_cetak");
                int cuci_ke_cabut = Utility.countDaysWithoutFreeDays(rs.getDate("tgl_masuk_cuci"), rs.getDate("tgl_cabut")) + 1;
                int cuci_ke_cetak = Utility.countDaysWithoutFreeDays(rs.getDate("tgl_masuk_cuci"), rs.getDate("tgl_mulai_cetak")) + 1;
                row[11] = cuci_ke_cabut;
                row[12] = cuci_ke_cetak;

                if (cuci_ke_cetak <= 3 && cuci_ke_cabut <= 2) {
                    model.addRow(row);
                    if (tgl.equals(rs.getString("tgl_setor_cabut")) && bagian.equals(rs.getString("nama_bagian"))) {
                        bobot_lp = bobot_lp + rs.getDouble("bobot_lp");
                    } else {
                        if (!tgl.equals("") && !bagian.equals("")) {
                            row_rekap[0] = bagian;
                            row_rekap[1] = tgl;
                            double bonus_per_bagian = bonus_kecepatan_per_LP;
                            bobot_lp = Math.round(bobot_lp * 100000d) / 100000d;
                            if (bobot_lp < bobot_lp_min) {
                                bonus_per_bagian = 0;
                            } else if (bobot_lp >= 1.1d) {
                                bonus_per_bagian = bobot_lp * bonus_kecepatan_per_LP;
                            } else {
                                bonus_per_bagian = bonus_kecepatan_per_LP;
                            }
                            row_rekap[2] = decimalFormat.format(bobot_lp);
                            total_bonus_per_bagian = (int) (total_bonus_per_bagian + Math.round(bonus_per_bagian));
                            row_rekap[3] = Math.round(bonus_per_bagian * 1000.f) / 1000.f;
                            if (!nama_bagian.contains(bagian)) {
                                nama_bagian.add(bagian);
                                hari_bonus.add(0);
                            }
                            if (bonus_per_bagian > 0) {
                                int index = nama_bagian.indexOf(bagian);
                                hari_bonus.set(index, hari_bonus.get(index) + 1);
                            }
                            model_rekap1.addRow(row_rekap);
                        }
                        tgl = rs.getString("tgl_setor_cabut");
                        bagian = rs.getString("nama_bagian");
                        bobot_lp = rs.getDouble("bobot_lp");
                    }
                }
            }
            row_rekap[0] = bagian;
            row_rekap[1] = tgl;
            bobot_lp = Math.round(bobot_lp * 100000d) / 100000d;
            double bonus_per_bagian = bonus_kecepatan_per_LP;
            if (bobot_lp < bobot_lp_min) {
                bonus_per_bagian = 0;
            } else if (bobot_lp >= 1.1d) {
                bonus_per_bagian = bobot_lp * bonus_kecepatan_per_LP;
            } else {
                bonus_per_bagian = bonus_kecepatan_per_LP;
            }
            row_rekap[2] = decimalFormat.format(bobot_lp);
            row_rekap[3] = Math.round(bonus_per_bagian * 1000.f) / 1000.f;
            total_bonus_per_bagian = (int) (total_bonus_per_bagian + Math.round(bonus_per_bagian));
            if (!nama_bagian.contains(bagian)) {
                nama_bagian.add(bagian);
                hari_bonus.add(0);
            }
            if (bonus_per_bagian > 0) {
                int index = nama_bagian.indexOf(bagian);
                hari_bonus.set(index, hari_bonus.get(index) + 1);
            }
            model_rekap1.addRow(row_rekap);

            jProgressBar1.setMaximum(table_data_rekap_bonus1.getRowCount());
            int hari_kerja = Utility.countDaysWithoutFreeDays(Date_bonus1.getDate(), Date_bonus2.getDate()) + 1;
            label_total_hariKerja.setText("Hari kerja : " + hari_kerja);
            double bonus_berturut2 = Double.valueOf(txt_bonus_berturut2.getText());
            double total_bonus_tambahan = 0;
            for (int i = 0; i < table_data_rekap_bonus1.getRowCount(); i++) {
                jProgressBar1.setValue(jProgressBar1.getValue() + 1);
                String bagian1 = table_data_rekap_bonus1.getValueAt(i, 0).toString();
                String tanggal1 = table_data_rekap_bonus1.getValueAt(i, 1).toString();
                double bobot = Double.valueOf(table_data_rekap_bonus1.getValueAt(i, 2).toString());
                double bonus_tambahan = 0;
                if (hari_bonus.get(nama_bagian.indexOf(bagian1)) >= hari_kerja) {
                    bonus_tambahan = bobot * bonus_berturut2;
                }
                float bonus_lp = Float.valueOf(table_data_rekap_bonus1.getValueAt(i, 3).toString());
                table_data_rekap_bonus1.setValueAt(bonus_tambahan, i, 4);
                table_data_rekap_bonus1.setValueAt(hari_bonus.get(nama_bagian.indexOf(bagian1)), i, 5);
                refreshTabel_rekap_bonus(bagian1, tanggal1, Math.round(bonus_lp * 1000.f) / 1000.f, Math.round(bonus_tambahan * 1000.f) / 1000.f);
                total_bonus_tambahan = total_bonus_tambahan + bonus_tambahan;
            }

            ColumnsAutoSizer.sizeColumnsToFit(table_bonus_kecepatan);
            ColumnsAutoSizer.sizeColumnsToFit(table_data_rekap_bonus1);
            label_jumlah_lp_bonus.setText("Jumlah LP Bonus : " + decimalFormat.format(table_bonus_kecepatan.getRowCount()));
            label_total_bonus_lp_rekapgrup.setText("Total Bonus : Rp. " + decimalFormat.format(total_bonus_per_bagian));
            label_total_bonus_lp2_rekapgrup.setText("Total Bonus+ : Rp. " + decimalFormat.format(total_bonus_tambahan));
            label_total_bonus_lp_rekap_karyawan.setText("Total Bonus = " + decimalFormat.format(Math.round(total_bonus_lp_rekap_karyawan)));
            label_total_bonus_lp2_rekap_karyawan.setText("Total Bonus = " + decimalFormat.format(Math.round(total_bonus_lp2_rekap_karyawan)));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_BonusCabut2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String count_minggu_query(String tgl1, String tgl2) {
        String count_minggu_query = "select COUNT(`selected_date`) AS 'hari_minggu' from \n"
                + "(select adddate('1970-01-01',t4.i*10000 + t3.i*1000 + t2.i*100 + t1.i*10 + t0.i) AS 'selected_date' from\n"
                + " (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t0,\n"
                + " (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t1,\n"
                + " (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t2,\n"
                + " (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t3,\n"
                + " (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t4) v\n"
                + "where `selected_date` between " + tgl1 + " and " + tgl2 + "\n"
                + "AND DAYNAME(`selected_date`) = 'SUNDAY'";
        return count_minggu_query;
    }

    public String count_libur_query(String tgl1, String tgl2) {
        String count_libur_query = "SELECT COUNT(`tanggal_libur`) AS 'hari_libur' FROM `tb_libur` "
                + "WHERE `tanggal_libur` BETWEEN " + tgl1 + " and " + tgl2;
        return count_libur_query;
    }

    public void refreshTabel_rekap_bonus(String bagian, String tgl_setor, float bonus_lp, float bonus_berturut2) {
        try {
            decimalFormat.setMaximumFractionDigits(2);
            float total_gaji_bagian = 0;
            DefaultTableModel model = (DefaultTableModel) table_data_rekap_bonus2.getModel();

            String query = "SELECT `tb_cabut`.`no_laporan_produksi`, (`berat_basah` * `tarif_gram`) AS 'total_gaji_bagian', "
                    + "DATEDIFF(`tb_cabut`.`tgl_cabut`, `tb_cuci`.`tgl_masuk_cuci`) - (" + count_minggu_query("`tb_cuci`.`tgl_masuk_cuci`", "`tb_cabut`.`tgl_cabut`") + ") - (" + count_libur_query("`tb_cuci`.`tgl_masuk_cuci`", "`tb_cabut`.`tgl_cabut`") + ") AS 'cuci_ke_cabut', "
                    + "DATEDIFF(`tb_cetak`.`tgl_mulai_cetak`, `tb_cuci`.`tgl_masuk_cuci`) - (" + count_minggu_query("`tb_cuci`.`tgl_masuk_cuci`", "`tb_cetak`.`tgl_mulai_cetak`") + ") - (" + count_libur_query("`tb_cuci`.`tgl_masuk_cuci`", "`tb_cetak`.`tgl_mulai_cetak`") + ") AS 'cuci_ke_cetak' "
                    + "FROM `tb_laporan_produksi`\n"
                    + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah`\n"
                    + "LEFT JOIN `tb_cuci` ON `tb_laporan_produksi`.`no_laporan_produksi` =`tb_cuci`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_cabut` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cabut`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_cetak` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cetak`.`no_laporan_produksi`\n"
                    + "LEFT JOIN "
                    + "(SELECT `no_laporan_produksi`, CONCAT(`divisi_bagian`, '-', `ruang_bagian`) AS 'nama_bagian', "
                    + "COUNT(DISTINCT(CONCAT(`divisi_bagian`, '-', `ruang_bagian`))) AS 'count_bagian', "
                    + "COUNT(DISTINCT(`id_pegawai`)) AS 'jumlah_pencabut' \n"
                    + "FROM `tb_detail_pencabut` "
                    + "LEFT JOIN `tb_bagian` ON `tb_detail_pencabut`.`kode_bagian` = `tb_bagian`.`kode_bagian` "
                    + "GROUP BY `no_laporan_produksi`) "
                    + "y ON `tb_laporan_produksi`.`no_laporan_produksi` = y.`no_laporan_produksi`\n"
                    + "WHERE `tb_cabut`.`tgl_setor_cabut` = '" + tgl_setor + "' "
                    + "AND `tb_cetak`.`tgl_mulai_cetak` IS NOT NULL \n"
                    + "AND y.`nama_bagian` LIKE '%CABUT%' "
                    + "AND y.`nama_bagian` = '" + bagian + "' \n"
                    + "AND y.`count_bagian` = 1 \n"
                    + "AND  y.`jumlah_pencabut`  <= " + max_orang + " "
                    + "AND `tb_cabut`.`tgl_cabut` = `tb_cabut`.`tgl_setor_cabut`";
            ResultSet result = Utility.db.getStatement().executeQuery(query);
            while (result.next()) {
                int cuci_ke_cabut = result.getInt("cuci_ke_cabut");
                int cuci_ke_cetak = result.getInt("cuci_ke_cetak");
                if (cuci_ke_cetak <= 3 && cuci_ke_cabut <= 1) {
                    total_gaji_bagian = total_gaji_bagian + result.getFloat("total_gaji_bagian");
                }
            }
            query = "SELECT *, SUM(`gaji`) AS 'jumlah_gaji' "
                    + "FROM (SELECT y.`nama_bagian`, `tb_cabut`.`tgl_setor_cabut`, `tb_detail_pencabut`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, (`jumlah_gram` * `tarif_gram`) AS 'gaji', "
                    + "DATEDIFF(`tb_cabut`.`tgl_cabut`, `tb_cuci`.`tgl_masuk_cuci`) - (" + count_minggu_query("`tb_cuci`.`tgl_masuk_cuci`", "`tb_cabut`.`tgl_cabut`") + ") - (" + count_libur_query("`tb_cuci`.`tgl_masuk_cuci`", "`tb_cabut`.`tgl_cabut`") + ") AS 'cuci_ke_cabut', "
                    + "DATEDIFF(`tb_cetak`.`tgl_mulai_cetak`, `tb_cuci`.`tgl_masuk_cuci`) - (" + count_minggu_query("`tb_cuci`.`tgl_masuk_cuci`", "`tb_cetak`.`tgl_mulai_cetak`") + ") - (" + count_libur_query("`tb_cuci`.`tgl_masuk_cuci`", "`tb_cetak`.`tgl_mulai_cetak`") + ") AS 'cuci_ke_cetak' "
                    + "FROM `tb_detail_pencabut`\n"
                    + "LEFT JOIN `tb_cuci` ON `tb_detail_pencabut`.`no_laporan_produksi` =`tb_cuci`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_cabut` ON `tb_detail_pencabut`.`no_laporan_produksi` = `tb_cabut`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_cetak` ON `tb_detail_pencabut`.`no_laporan_produksi` = `tb_cetak`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_detail_pencabut`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah`\n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_detail_pencabut`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                    + "LEFT JOIN "
                    + "(SELECT `no_laporan_produksi`, CONCAT(`divisi_bagian`, '-', `ruang_bagian`) AS 'nama_bagian', "
                    + "COUNT(DISTINCT(CONCAT(`divisi_bagian`, '-', `ruang_bagian`))) AS 'count_bagian', "
                    + "COUNT(DISTINCT(`id_pegawai`)) AS 'jumlah_pencabut' \n"
                    + "FROM `tb_detail_pencabut` "
                    + "LEFT JOIN `tb_bagian` ON `tb_detail_pencabut`.`kode_bagian` = `tb_bagian`.`kode_bagian` "
                    + "GROUP BY `no_laporan_produksi`) "
                    + "y ON `tb_laporan_produksi`.`no_laporan_produksi` = y.`no_laporan_produksi`\n"
                    + "WHERE `tb_cabut`.`tgl_setor_cabut` = '" + tgl_setor + "' "
                    + "AND `tb_cetak`.`tgl_mulai_cetak` IS NOT NULL \n"
                    + "AND y.`nama_bagian` LIKE '%CABUT%' "
                    + "AND y.`nama_bagian` = '" + bagian + "' \n"
                    + "AND y.`count_bagian` = 1 \n"
                    + "AND  y.`jumlah_pencabut`  <= " + max_orang + " "
                    + "AND `tb_cabut`.`tgl_cabut` = `tb_cabut`.`tgl_setor_cabut`) DATA "
                    + "WHERE `cuci_ke_cabut` <= 1 AND `cuci_ke_cetak` <= 3 "
                    + "GROUP BY `id_pegawai`, `tgl_setor_cabut`";
//            System.out.println(query);
            result = Utility.db.getStatement().executeQuery(query);
            Object[] row = new Object[10];
            while (result.next()) {
                row[0] = result.getString("nama_bagian");
                row[1] = result.getString("tgl_setor_cabut");
                row[2] = result.getString("id_pegawai");
                row[3] = result.getString("nama_pegawai");
                row[4] = Math.round(result.getFloat("jumlah_gaji") * 1000.f) / 1000.f;
                row[5] = Math.round(result.getFloat("jumlah_gaji") / total_gaji_bagian * bonus_lp * 1000.f) / 1000.f;
                row[6] = Math.round(result.getFloat("jumlah_gaji") / total_gaji_bagian * bonus_berturut2 * 1000.f) / 1000.f;
                float bonus_per_karyawan = Math.round(result.getFloat("jumlah_gaji") / total_gaji_bagian * (bonus_lp + bonus_berturut2) * 1000.f) / 1000.f;
                row[7] = bonus_per_karyawan;
                row[8] = total_gaji_bagian;
                total_bonus_lp_rekap_karyawan = total_bonus_lp_rekap_karyawan + Math.round(result.getFloat("jumlah_gaji") / total_gaji_bagian * bonus_lp * 1000.f) / 1000.f;
                total_bonus_lp2_rekap_karyawan = total_bonus_lp2_rekap_karyawan + Math.round(result.getFloat("jumlah_gaji") / total_gaji_bagian * bonus_berturut2 * 1000.f) / 1000.f;
                total_gram_cabutan = total_gram_cabutan + result.getFloat("jumlah_gaji");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_data_rekap_bonus2);
        } catch (Exception e) {
            Logger.getLogger(JPanel_BonusCabut2.class.getName()).log(Level.SEVERE, null, e);
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
        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        table_bonus_kecepatan = new javax.swing.JTable();
        button_refresh_bonus = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();
        Date_bonus1 = new com.toedter.calendar.JDateChooser();
        Date_bonus2 = new com.toedter.calendar.JDateChooser();
        jLabel8 = new javax.swing.JLabel();
        txt_bonus_kecepatan_lp = new javax.swing.JTextField();
        label_jumlah_lp_bonus = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txt_filter_bagian_bonus_lp = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txt_bonus_berturut2 = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        button_save_data_bonus_kecepatan = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        table_data_rekap_bonus2 = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        label_total_bonus_lp_rekap_karyawan = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        table_data_rekap_bonus1 = new javax.swing.JTable();
        label_total_bonus_lp2_rekapgrup = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        label_total_hariKerja = new javax.swing.JLabel();
        label_total_bonus_lp_rekapgrup = new javax.swing.JLabel();
        button_export_bonus_LP = new javax.swing.JButton();
        label_total_bonus_lp2_rekap_karyawan = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txt_bonus_max_orang = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txt_bobot_min = new javax.swing.JTextField();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Kinerja Cabut", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        table_bonus_kecepatan.setAutoCreateRowSorter(true);
        table_bonus_kecepatan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_bonus_kecepatan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nama Grup", "No LP", "Bulu", "Kpg Upah", "Gram", "Bobot", "Pencabut", "Tgl Cuci", "Tgl Cabut", "Setor CBT", "Terima CTK", "Cuci-Cbt", "Cuci-Ctk"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class, java.lang.String.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Integer.class
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
        table_bonus_kecepatan.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(table_bonus_kecepatan);

        button_refresh_bonus.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh_bonus.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh_bonus.setText("Refresh");
        button_refresh_bonus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh_bonusActionPerformed(evt);
            }
        });

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("NOTES :\n1. LP dikerjakan max 10 orang.\n2. LP dikerjakan oleh 1 grup yang sama\n3. LP di kerjakan 1 hari (tgl cabut = tgl setor cabut)\n4. LP bukan LP Tandon (Selisih tgl cuci dan tgl mulai cabut MAX 2 hari kerja) -- intinya cucian hari ini besok harus dicabut, bukan dicabut 2 hari lagi\n5. Hari Cuci - Cetak MAX 3 hari kerja (tidak terhitung hari libur / HARI MINGGU) contoh : cuci tgl 10 - tgl 13 Minggu - terima cetak tgl 14 = 4 Hari.\n6. Harus load seminggu supaya tambahan bonus berturut-turutnya benar.\n7. Total bobot LP dikerjakan dalam 1 hari > 1 maka bonus LP 145.000, jika bobot >= 1.1 maka 155.000 x bobot LP di kerjakan.\n8. Bobot setiap LP akan dibulatkan normal ke 6 angka desimal, dan total bobot LP per grup per hari dibulatkan normal ke 5 angka desimal.\n9. bonus LP hanya menampilkan dan diberikan kepada bagian CABUT BORONG.");
        jScrollPane5.setViewportView(jTextArea1);

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Setor Cabut :");

        Date_bonus1.setBackground(new java.awt.Color(255, 255, 255));
        Date_bonus1.setDate(new Date(new Date().getTime()- (7 * 24 * 60 * 60 * 1000)));
        Date_bonus1.setDateFormatString("dd MMM yyyy");
        Date_bonus1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_bonus2.setBackground(new java.awt.Color(255, 255, 255));
        Date_bonus2.setDate(new Date());
        Date_bonus2.setDateFormatString("dd MMM yyyy");
        Date_bonus2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText("Bonus / LP : ");

        txt_bonus_kecepatan_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_bonus_kecepatan_lp.setText("155000");

        label_jumlah_lp_bonus.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_lp_bonus.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_jumlah_lp_bonus.setText("Jumlah LP : ");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("LOADING PROGRESS :");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel11.setText("Bagian");

        txt_filter_bagian_bonus_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel13.setText("Bonus Berturut2 / LP : ");

        txt_bonus_berturut2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_bonus_berturut2.setText("10000");

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        button_save_data_bonus_kecepatan.setBackground(new java.awt.Color(255, 255, 255));
        button_save_data_bonus_kecepatan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_save_data_bonus_kecepatan.setText("Save Data Bonus");
        button_save_data_bonus_kecepatan.setEnabled(false);
        button_save_data_bonus_kecepatan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_save_data_bonus_kecepatanActionPerformed(evt);
            }
        });

        table_data_rekap_bonus2.setAutoCreateRowSorter(true);
        table_data_rekap_bonus2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_rekap_bonus2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grup", "Tgl Setor Cabut", "ID karyawan", "Nama Karyawan", "Gaji Cabutan", "Bonus LP", "Bonus berturut2", "Tot Bonus", "Gaji Grup"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_data_rekap_bonus2.getTableHeader().setReorderingAllowed(false);
        jScrollPane6.setViewportView(table_data_rekap_bonus2);

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel4.setText("Bonus /karyawan /Hari");

        label_total_bonus_lp_rekap_karyawan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_bonus_lp_rekap_karyawan.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_total_bonus_lp_rekap_karyawan.setText("Total");

        table_data_rekap_bonus1.setAutoCreateRowSorter(true);
        table_data_rekap_bonus1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_rekap_bonus1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Divisi-Bagian_Ruang", "Tgl Setor Cabut", "Bobot LP Dikerjakan", "Bonus (Rp.)", "+Bonus", "h"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_data_rekap_bonus1.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(table_data_rekap_bonus1);

        label_total_bonus_lp2_rekapgrup.setBackground(new java.awt.Color(255, 255, 255));
        label_total_bonus_lp2_rekapgrup.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_total_bonus_lp2_rekapgrup.setText("Total Bonus LP+");

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel1.setText("Rekap /(Divisi-Bagian-Ruang) /Hari");

        label_total_hariKerja.setBackground(new java.awt.Color(255, 255, 255));
        label_total_hariKerja.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_total_hariKerja.setText("Total Hari Kerja");

        label_total_bonus_lp_rekapgrup.setBackground(new java.awt.Color(255, 255, 255));
        label_total_bonus_lp_rekapgrup.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_total_bonus_lp_rekapgrup.setText("Total Bonus LP");

        button_export_bonus_LP.setBackground(new java.awt.Color(255, 255, 255));
        button_export_bonus_LP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_bonus_LP.setText("Export");
        button_export_bonus_LP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_bonus_LPActionPerformed(evt);
            }
        });

        label_total_bonus_lp2_rekap_karyawan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_bonus_lp2_rekap_karyawan.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_total_bonus_lp2_rekap_karyawan.setText("Total+");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_bonus_lp_rekapgrup)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(label_total_bonus_lp2_rekapgrup)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(label_total_hariKerja))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 577, Short.MAX_VALUE)
                            .addComponent(jScrollPane6)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_bonus_lp_rekap_karyawan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_bonus_lp2_rekap_karyawan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_export_bonus_LP)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_save_data_bonus_kecepatan)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(label_total_bonus_lp_rekapgrup)
                    .addComponent(label_total_hariKerja)
                    .addComponent(label_total_bonus_lp2_rekapgrup))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(label_total_bonus_lp_rekap_karyawan)
                    .addComponent(button_save_data_bonus_kecepatan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_bonus_LP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_bonus_lp2_rekap_karyawan))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel12.setText("Max Orang:");

        txt_bonus_max_orang.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_bonus_max_orang.setText("10");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel15.setText("Bobot LP min :");

        txt_bobot_min.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_bobot_min.setText("1");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jScrollPane5)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 736, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_bonus1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_bonus2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_filter_bagian_bonus_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_bonus_kecepatan_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_bonus_berturut2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_bonus_max_orang, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_bobot_min, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_jumlah_lp_bonus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_refresh_bonus)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(Date_bonus1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_bonus2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_filter_bagian_bonus_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_bonus_max_orang, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_bobot_min, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh_bonus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_bonus_berturut2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_bonus_kecepatan_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_jumlah_lp_bonus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 444, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(3, 3, 3))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

    private void button_refresh_bonusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh_bonusActionPerformed
        // TODO add your handling code here:
        boolean check = true;
        try {
            double bonus_berturut2 = Double.valueOf(txt_bonus_berturut2.getText());
            int bonus_kecepatan_per_LP = Integer.valueOf(txt_bonus_kecepatan_lp.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Nominal bonus salah !");
            check = false;
        }
        if (Date_bonus1.getDate() == null && Date_bonus2.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Filter Tanggal tidak boleh kosong !");
            check = false;
        }
        if (check) {
            txt_bonus_kecepatan_lp.setEnabled(false);
            txt_bonus_berturut2.setEnabled(false);
            Date_bonus1.setEnabled(false);
            Date_bonus2.setEnabled(false);
            txt_filter_bagian_bonus_lp.setEnabled(false);
            button_refresh_bonus.setEnabled(false);
            button_save_data_bonus_kecepatan.setEnabled(false);
            try {
                jProgressBar1.setMinimum(0);
                jProgressBar1.setValue(0);
                jProgressBar1.setStringPainted(true);
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        refreshTable_bonusLP();
                        jProgressBar1.setValue(jProgressBar1.getMaximum());
                        JOptionPane.showMessageDialog(null, "Proses Selesai !");
                        txt_bonus_kecepatan_lp.setEnabled(true);
                        txt_bonus_berturut2.setEnabled(true);
                        Date_bonus1.setEnabled(true);
                        Date_bonus2.setEnabled(true);
                        txt_filter_bagian_bonus_lp.setEnabled(true);
                        button_refresh_bonus.setEnabled(true);
                        button_save_data_bonus_kecepatan.setEnabled(true);
                    }
                };
                thread.start();
            } catch (Exception e) {
                Logger.getLogger(JPanel_BonusCabut2.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }//GEN-LAST:event_button_refresh_bonusActionPerformed

    private void button_save_data_bonus_kecepatanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_save_data_bonus_kecepatanActionPerformed
        // TODO add your handling code here:
        int dialogResult = JOptionPane.showConfirmDialog(this, "Save " + table_data_rekap_bonus2.getRowCount() + " data ?", "Warning", 0);
        if (dialogResult == JOptionPane.YES_OPTION) {
            try {
                for (int i = 0; i < table_data_rekap_bonus2.getRowCount(); i++) {
                    String Query = "INSERT INTO `tb_lembur_rekap`(`id_pegawai`, `tanggal`, `bonus1_kecepatan`) "
                            + "VALUES ("
                            + "'" + table_data_rekap_bonus2.getValueAt(i, 2).toString() + "',"
                            + "'" + table_data_rekap_bonus2.getValueAt(i, 1).toString() + "',"
                            + table_data_rekap_bonus2.getValueAt(i, 7).toString() + ") "
                            + "ON DUPLICATE KEY UPDATE "
                            + "`bonus1_kecepatan`=" + table_data_rekap_bonus2.getValueAt(i, 7).toString();
                    System.out.println(Query);
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(Query);
                }
                JOptionPane.showMessageDialog(this, "Data Saved Successfully");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Save Failed !" + e);
                Logger.getLogger(JPanel_BonusCabut2.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }//GEN-LAST:event_button_save_data_bonus_kecepatanActionPerformed

    private void button_export_bonus_LPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_bonus_LPActionPerformed
        // TODO add your handling code here:
        DefaultTableModel table = (DefaultTableModel) table_data_rekap_bonus2.getModel();
        ExportToExcel.writeToExcel(table, this);
    }//GEN-LAST:event_button_export_bonus_LPActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser Date_bonus1;
    private com.toedter.calendar.JDateChooser Date_bonus2;
    private javax.swing.JButton button_export_bonus_LP;
    private javax.swing.JButton button_refresh_bonus;
    private javax.swing.JButton button_save_data_bonus_kecepatan;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel label_jumlah_lp_bonus;
    private javax.swing.JLabel label_total_bonus_lp2_rekap_karyawan;
    private javax.swing.JLabel label_total_bonus_lp2_rekapgrup;
    private javax.swing.JLabel label_total_bonus_lp_rekap_karyawan;
    private javax.swing.JLabel label_total_bonus_lp_rekapgrup;
    private javax.swing.JLabel label_total_hariKerja;
    private javax.swing.JTable table_bonus_kecepatan;
    private javax.swing.JTable table_data_rekap_bonus1;
    private javax.swing.JTable table_data_rekap_bonus2;
    private javax.swing.JTextField txt_bobot_min;
    private javax.swing.JTextField txt_bonus_berturut2;
    private javax.swing.JTextField txt_bonus_kecepatan_lp;
    private javax.swing.JTextField txt_bonus_max_orang;
    private javax.swing.JTextField txt_filter_bagian_bonus_lp;
    // End of variables declaration//GEN-END:variables

}
