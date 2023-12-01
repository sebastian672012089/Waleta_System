package waleta_system.RND;

import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Browse_Karyawan;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;
import waleta_system.Class.Utility;

public class JPanel_ProduksiATB extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DecimalFormat decimalFormat = new DecimalFormat();
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    // HashMap to store the sum of values for each id
    HashMap<String, float[]> summerizeById = new HashMap<>();
    // HashMap to store the sum of values for each grade
    HashMap<String, float[]> summerizeByGrade = new HashMap<>();

    public JPanel_ProduksiATB() {
        initComponents();
    }

    public void init() {
        try {
            decimalFormat = Utility.DecimalFormatUS(decimalFormat);
            ComboBox_id_pegawai_atb.removeAllItems();
            sql = "SELECT `id_pegawai_atb` FROM `tb_karyawan_atb` WHERE 1";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_id_pegawai_atb.addItem(rs.getString("id_pegawai_atb"));
            }
            refreshTable_produksi();
            table_data_produksi_atb.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && table_data_produksi_atb.getSelectedRow() != -1) {
                        try {
                            int i = table_data_produksi_atb.getSelectedRow();
                            txt_no_lp_input.setText(table_data_produksi_atb.getValueAt(i, 1).toString());
                            ComboBox_id_pegawai_atb.setSelectedItem(table_data_produksi_atb.getValueAt(i, 3).toString());
                            txt_keping.setText(table_data_produksi_atb.getValueAt(i, 4).toString());
                            if (table_data_produksi_atb.getValueAt(i, 6) != null) {//tangal selesai
                                Date_mulai.setDate(dateFormat.parse(table_data_produksi_atb.getValueAt(i, 6).toString()));
                            }
                            if (table_data_produksi_atb.getValueAt(i, 7) != null) {//waktu selesai
                                Spinner_jam_mulai.setValue(Integer.valueOf(table_data_produksi_atb.getValueAt(i, 7).toString().split(":")[0]));
                                Spinner_menit_mulai.setValue(Integer.valueOf(table_data_produksi_atb.getValueAt(i, 7).toString().split(":")[1]));
                            }
                            txt_operator_id.setText(table_data_produksi_atb.getValueAt(i, 17).toString());
                            txt_operator_nama.setText(table_data_produksi_atb.getValueAt(i, 18).toString());

                            if (table_data_produksi_atb.getValueAt(i, 8) != null) {//tangal selesai
                                Date_selesai.setDate(dateFormat.parse(table_data_produksi_atb.getValueAt(i, 8).toString()));
                            }
                            if (table_data_produksi_atb.getValueAt(i, 9) != null) {//waktu selesai
                                Spinner_jam_selesai.setValue(Integer.valueOf(table_data_produksi_atb.getValueAt(i, 9).toString().split(":")[0]));
                                Spinner_menit_selesai.setValue(Integer.valueOf(table_data_produksi_atb.getValueAt(i, 9).toString().split(":")[1]));
                            }
                            if (table_data_produksi_atb.getValueAt(i, 19) != null) {//layer selesai
                                ComboBox_layer_selesai.setSelectedItem(table_data_produksi_atb.getValueAt(i, 19).toString());
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(JPanel_ProduksiATB.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            });
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_ProduksiATB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_produksi() {
        try {
            summerizeById = new HashMap<>();
            summerizeByGrade = new HashMap<>();
            DefaultTableModel model = (DefaultTableModel) table_data_produksi_atb.getModel();
            model.setRowCount(0);
            float total_kpg = 0, total_gram = 0, total_biaya_listrik = 0;
            float watt = Float.valueOf(txt_watt_atb.getText());
            float tarif_kwh_normal = Float.valueOf(txt_tarif_kwh_normal.getText());
            float tarif_kwh_malam = Float.valueOf(txt_tarif_kwh_malam.getText());
            float tarif_wh_normal = tarif_kwh_normal * (watt / 1000f);
            float tarif_wh_malam = tarif_kwh_malam * (watt / 1000f);

            String filter_tanggal = "";
            if (Datefilter1.getDate() != null && Datefilter2.getDate() != null) {
                if (ComboBox_jenis_filter_tanggal.getSelectedIndex() == 0) {
                    filter_tanggal = "AND (DATE(`waktu_mulai`) BETWEEN '" + dateFormat.format(Datefilter1.getDate()) + "' AND '" + dateFormat.format(Datefilter2.getDate()) + "')";
                } else if (ComboBox_jenis_filter_tanggal.getSelectedIndex() == 1) {
                    filter_tanggal = "AND (`tb_laporan_produksi_penilaian_bulu`.`tanggal` BETWEEN '" + dateFormat.format(Datefilter1.getDate()) + "' AND '" + dateFormat.format(Datefilter2.getDate()) + "')";
                }
            }
            String filter_id_mesin_ATB = "";
            if (txt_search_id_mesin_ATB.getText() != null && !txt_search_id_mesin_ATB.getText().equals("")) {
                filter_id_mesin_ATB = "AND `id_pegawai_atb` LIKE '%" + txt_search_id_mesin_ATB.getText() + "%' \n";
            }
            String filter_grade = "";
            if (txt_search_grade.getText() != null && !txt_search_grade.getText().equals("")) {
                filter_grade = "AND `tb_laporan_produksi`.`kode_grade` LIKE '%" + txt_search_grade.getText() + "%' \n";
            }

            sql = "SELECT `no`, `tb_atb_produksi`.`no_laporan_produksi`, `tb_laporan_produksi`.`kode_grade`, `id_pegawai_atb`, `keping`, `gram`, `waktu_mulai`, `waktu_selesai`, `tb_atb_produksi`.`operator`, `nama_pegawai`, `layer_selesai`, "
                    + "`jenis_bulu_lp`, `tarif_sub`, `tb_laporan_produksi_penilaian_bulu`.`tanggal` AS 'tgl_penilaian_bulu' \n"
                    + "FROM `tb_atb_produksi` \n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_atb_produksi`.`operator` = `tb_karyawan`.`id_pegawai`\n"
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_atb_produksi`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_laporan_produksi_penilaian_bulu` ON `tb_atb_produksi`.`no_laporan_produksi` = `tb_laporan_produksi_penilaian_bulu`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah`\n"
                    + "WHERE \n"
                    + "`tb_atb_produksi`.`no_laporan_produksi` LIKE '%" + txt_search_no_lp.getText() + "%' \n"
                    + "AND `nama_pegawai` LIKE '%" + txt_search_nama_operator.getText() + "%' \n"
                    + filter_grade
                    + filter_id_mesin_ATB
                    + filter_tanggal;
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[30];
            while (rs.next()) {
                row[0] = rs.getString("no");
                row[1] = rs.getString("no_laporan_produksi");
                row[2] = rs.getString("kode_grade");
                row[3] = rs.getString("id_pegawai_atb");
                row[4] = rs.getFloat("keping");
                row[5] = rs.getFloat("gram");
                row[6] = rs.getDate("waktu_mulai");
                row[7] = rs.getTime("waktu_mulai");
                row[8] = rs.getDate("waktu_selesai");
                row[9] = rs.getTime("waktu_selesai");
                long difference_normal = 0;
                long difference_malam = 0;
                if (rs.getDate("waktu_mulai") != null && rs.getDate("waktu_selesai") != null) {
                    difference_normal = rs.getTimestamp("waktu_selesai").getTime() - rs.getTimestamp("waktu_mulai").getTime();
                    if (rs.getDate("waktu_mulai") == rs.getDate("waktu_selesai")) {
                        if (rs.getTime("waktu_mulai").after(new SimpleDateFormat("HH:mm:ss").parse("17:00:00"))
                                && rs.getTime("waktu_selesai").before(new SimpleDateFormat("HH:mm:ss").parse("22:00:00"))) {
                            difference_malam = rs.getTimestamp("waktu_selesai").getTime() - rs.getTimestamp("waktu_mulai").getTime();
                        } else if (rs.getTime("waktu_mulai").before(new SimpleDateFormat("HH:mm:ss").parse("17:00:00"))
                                && rs.getTime("waktu_selesai").after(new SimpleDateFormat("HH:mm:ss").parse("17:00:00"))
                                && rs.getTime("waktu_selesai").before(new SimpleDateFormat("HH:mm:ss").parse("22:00:00"))) {
                            difference_malam = rs.getTime("waktu_selesai").getTime() - new SimpleDateFormat("HH:mm:ss").parse("17:00:00").getTime();
                        } else if (rs.getTime("waktu_mulai").after(new SimpleDateFormat("HH:mm:ss").parse("17:00:00"))
                                && rs.getTime("waktu_mulai").before(new SimpleDateFormat("HH:mm:ss").parse("22:00:00"))
                                && rs.getTime("waktu_selesai").after(new SimpleDateFormat("HH:mm:ss").parse("22:00:00"))) {
                            difference_malam = new SimpleDateFormat("HH:mm:ss").parse("22:00:00").getTime() - rs.getTime("waktu_mulai").getTime();
                        } else if (rs.getTime("waktu_mulai").before(new SimpleDateFormat("HH:mm:ss").parse("17:00:00"))
                                && rs.getTime("waktu_selesai").after(new SimpleDateFormat("HH:mm:ss").parse("22:00:00"))) {
                            difference_malam = 5 * 3600 * 1000;
                        } else {
                            difference_malam = 0;
                        }
                    } else {
                        long diff = rs.getDate("waktu_selesai").getTime() - rs.getDate("waktu_mulai").getTime();
                        long jumlah_hari = TimeUnit.MILLISECONDS.toDays(diff);
                        difference_malam = ((jumlah_hari - 1) * 5 * 3600 * 1000);
                        if (rs.getTime("waktu_mulai").before(new SimpleDateFormat("HH:mm:ss").parse("17:00:00"))) {
                            difference_malam += (5 * 3600 * 1000);
                        } else if (rs.getTime("waktu_mulai").after(new SimpleDateFormat("HH:mm:ss").parse("22:00:00"))) {
                        } else {
                            difference_malam += (new SimpleDateFormat("HH:mm:ss").parse("22:00:00").getTime() - rs.getTime("waktu_mulai").getTime());
                        }
                        if (rs.getTime("waktu_selesai").before(new SimpleDateFormat("HH:mm:ss").parse("17:00:00"))) {
                        } else if (rs.getTime("waktu_selesai").after(new SimpleDateFormat("HH:mm:ss").parse("22:00:00"))) {
                            difference_malam += (5 * 3600 * 1000);
                        } else {
                            difference_malam += (rs.getTime("waktu_selesai").getTime() - new SimpleDateFormat("HH:mm:ss").parse("17:00:00").getTime());
                        }
                    }
                }

                double durasi_menit_normal = TimeUnit.MILLISECONDS.toMinutes(difference_normal);
                double durasi_menit_malam = TimeUnit.MILLISECONDS.toMinutes(difference_malam);
                durasi_menit_normal = durasi_menit_normal - durasi_menit_malam;
                double durasi_jam_normal = Math.round((durasi_menit_normal / 60d) * 100d) / 100d;
                double durasi_jam_malam = Math.round((durasi_menit_malam / 60d) * 100d) / 100d;
                row[10] = durasi_menit_normal;
                row[11] = durasi_menit_malam;
                double biaya = (durasi_jam_normal * tarif_wh_normal) + (durasi_jam_malam * tarif_wh_malam);
                double biaya_per_kpg = biaya / rs.getFloat("keping");
                row[12] = Math.round(biaya);
                row[13] = Math.round(biaya_per_kpg);
                row[14] = rs.getString("jenis_bulu_lp");
                row[15] = rs.getFloat("tarif_sub");
                float upah_borong = Math.round((rs.getFloat("tarif_sub") * rs.getFloat("gram")));
                row[16] = upah_borong;
                row[17] = rs.getString("operator");
                row[18] = rs.getString("nama_pegawai");
                row[19] = rs.getString("layer_selesai");
                row[20] = rs.getDate("tgl_penilaian_bulu");
                model.addRow(row);
                total_kpg += rs.getFloat("keping");
                total_gram += rs.getFloat("gram");
                total_biaya_listrik += biaya;

                //Initialize Variable
                String id = rs.getString("id_pegawai_atb");
                String grade = rs.getString("kode_grade");
                float keping = rs.getFloat("keping");
                float gram = rs.getFloat("gram");
                float menit_durasi = (float) durasi_menit_normal + (float) durasi_menit_malam;
                // Check if the id already exists in the sumMap
                if (summerizeById.containsKey(id)) {
                    // If the id exists, update the sum of values
                    float[] sumArray = summerizeById.get(id);
                    sumArray[0] += keping;
                    sumArray[1] += gram;
                    sumArray[2] += menit_durasi;
                    sumArray[3] += (float) biaya;
                    sumArray[4] += upah_borong;
                } else {
                    // If the id does not exist, create a new sum array
                    float[] sumArray = {keping, gram, menit_durasi, (float) biaya, upah_borong};
                    summerizeById.put(id, sumArray);
                }
                if (!rs.getString("id_pegawai_atb").equals("Bayangan")) {
                    // Check if the grade already exists in the sumMap
                    if (summerizeByGrade.containsKey(grade)) {
                        // If the grade exists, update the sum of values
                        float[] sumArray = summerizeByGrade.get(grade);
                        sumArray[0] += keping;
                        sumArray[1] += gram;
                        sumArray[2] += menit_durasi;
                        sumArray[3] += (float) biaya;
                        sumArray[4] += upah_borong;
                    } else {
                        // If the grade does not exist, create a new sum array
                        float[] sumArray = {keping, gram, menit_durasi, (float) biaya, upah_borong};
                        summerizeByGrade.put(grade, sumArray);
                    }
                }
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_data_produksi_atb);
            decimalFormat.setMaximumFractionDigits(1);
            int total_data = table_data_produksi_atb.getRowCount();
            label_total_data.setText(decimalFormat.format(total_data));
            label_total_kpg.setText(decimalFormat.format(total_kpg));
            label_total_gram.setText(decimalFormat.format(total_gram));
            label_total_biaya_listrik.setText(decimalFormat.format(total_biaya_listrik));

            refreshTable_rekapKinerja();
            refreshTable_rekap_per_grade();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_ProduksiATB.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void refreshTable_rekapKinerja() {
        try {
            float total_kpg_per_shift = 0;
            DefaultTableModel model = (DefaultTableModel) table_data_rekap_per_karyawan.getModel();
            model.setRowCount(0);
            Object[] row = new Object[10];

            for (String id : summerizeById.keySet()) {
                float[] sumArray = summerizeById.get(id);

                row[0] = id;
                row[1] = sumArray[0];//Keping
                row[2] = Math.round(sumArray[1] * 10f) / 10f;//Gram
                row[3] = Math.round(sumArray[2] * 10f) / 10f;//Total menit durasi
                row[4] = Math.round(sumArray[2] / sumArray[0] * 10f) / 10f;//Total menit durasi/keping
                row[5] = Math.round(480f / (sumArray[2] / sumArray[0]) * 10f) / 10f;//Jumlah Keping per 1 Shift (7 jam)
                row[6] = Math.round(sumArray[3]);//total biaya listrik
                row[7] = Math.round(sumArray[3] / sumArray[0] * 10f) / 10f;//total biaya listrik/keping
                row[8] = Math.round(sumArray[4] * 10f) / 10f;//upah borong
                model.addRow(row);
                total_kpg_per_shift += Math.round(480f / (sumArray[2] / sumArray[0]) * 10f) / 10f;
            }

            ColumnsAutoSizer.sizeColumnsToFit(table_data_rekap_per_karyawan);
            float total_data = table_data_rekap_per_karyawan.getRowCount();
            label_total_data_rekap_kinerja.setText(decimalFormat.format(total_data));
            label_avg_kpg_per_shift_rekap_kinerja.setText(decimalFormat.format(total_kpg_per_shift / total_data));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_ProduksiATB.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void refreshTable_rekap_per_grade() {
        try {
            float total_kpg_per_shift = 0;
            DefaultTableModel model = (DefaultTableModel) table_data_rekap_per_grade.getModel();
            model.setRowCount(0);
            Object[] row = new Object[10];

            for (String id : summerizeByGrade.keySet()) {
                float[] sumArray = summerizeByGrade.get(id);

                row[0] = id;
                row[1] = sumArray[0];//Keping
                row[2] = Math.round(sumArray[1] * 10f) / 10f;//Gram
                row[3] = Math.round(sumArray[2] * 10f) / 10f;//Total menit durasi
                row[4] = Math.round(sumArray[2] / sumArray[0] * 10f) / 10f;//Total menit durasi/keping
                row[5] = Math.round(480f / (sumArray[2] / sumArray[0]) * 10f) / 10f;//Jumlah Keping per 1 Shift (7 jam)
                row[6] = Math.round(sumArray[3]);//total biaya listrik
                row[7] = Math.round(sumArray[3] / sumArray[0] * 10f) / 10f;//total biaya listrik/keping
                row[8] = Math.round(sumArray[4] * 10f) / 10f;//upah borong
                model.addRow(row);
                total_kpg_per_shift += Math.round(480f / (sumArray[2] / sumArray[0]) * 10f) / 10f;
            }

            ColumnsAutoSizer.sizeColumnsToFit(table_data_rekap_per_grade);
            float total_data = table_data_rekap_per_grade.getRowCount();
            label_total_data_rekap_grade.setText(decimalFormat.format(total_data));
            label_avg_kpg_per_shift_rekap_grade.setText(decimalFormat.format(total_kpg_per_shift / total_data));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_ProduksiATB.class.getName()).log(Level.SEVERE, null, e);
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
        txt_search_no_lp = new javax.swing.JTextField();
        button_search = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        Datefilter1 = new com.toedter.calendar.JDateChooser();
        Datefilter2 = new com.toedter.calendar.JDateChooser();
        jLabel6 = new javax.swing.JLabel();
        label_total_data = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_data_produksi_atb = new javax.swing.JTable();
        button_export = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        label_total_gram = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        label_total_kpg = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        button_delete = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txt_keping = new javax.swing.JTextField();
        Date_mulai = new com.toedter.calendar.JDateChooser();
        button_tambah = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        ComboBox_id_pegawai_atb = new javax.swing.JComboBox<>();
        Spinner_jam_mulai = new javax.swing.JSpinner();
        Spinner_menit_mulai = new javax.swing.JSpinner();
        jLabel17 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txt_no_lp_input = new javax.swing.JTextField();
        txt_operator_nama = new javax.swing.JTextField();
        button_pick_pekerja = new javax.swing.JButton();
        txt_operator_id = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        button_edit = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        txt_watt_atb = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txt_tarif_kwh_normal = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        txt_tarif_kwh_malam = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        button_waktu_selesai = new javax.swing.JButton();
        jLabel28 = new javax.swing.JLabel();
        Date_selesai = new com.toedter.calendar.JDateChooser();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        Spinner_menit_selesai = new javax.swing.JSpinner();
        Spinner_jam_selesai = new javax.swing.JSpinner();
        jLabel31 = new javax.swing.JLabel();
        ComboBox_layer_selesai = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        txt_search_grade = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        txt_search_id_mesin_ATB = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        label_total_biaya_listrik = new javax.swing.JLabel();
        ComboBox_jenis_filter_tanggal = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        txt_search_nama_operator = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        label_total_data_rekap_kinerja = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        table_data_rekap_per_karyawan = new javax.swing.JTable();
        Button_export_rekap_karyawan = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        label_avg_kpg_per_shift_rekap_kinerja = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        label_total_data_rekap_grade = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        table_data_rekap_per_grade = new javax.swing.JTable();
        Button_export_rekap_grade = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        label_avg_kpg_per_shift_rekap_grade = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Produksi ATB", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        txt_search_no_lp.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_no_lp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_no_lpKeyPressed(evt);
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

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("No LP :");

        Datefilter1.setBackground(new java.awt.Color(255, 255, 255));
        Datefilter1.setDate(new Date(new Date().getTime()- (7 * 24 * 60 * 60 * 1000)));
        Datefilter1.setDateFormatString("dd MMMM yyyy");
        Datefilter1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Datefilter2.setBackground(new java.awt.Color(255, 255, 255));
        Datefilter2.setDate(new Date());
        Datefilter2.setDateFormatString("dd MMMM yyyy");
        Datefilter2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Total Data :");

        label_total_data.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_data.setText("TOTAL");

        table_data_produksi_atb.setAutoCreateRowSorter(true);
        table_data_produksi_atb.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_produksi_atb.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "No LP", "Grade", "ID Pegawai ATB", "Kpg", "Gram", "Tgl Mulai", "Waktu Mulai", "Tgl Selesai", "Waktu Selesai", "Jam Normal", "Jam Malam", "Total Biaya Listrik", "Biaya Listrik/Kpg", "Jenis Bulu", "Upah/gr", "Upah Brg", "Operator ID", "Operator", "Layer Selesai", "Tgl Penilaian"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
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
        jScrollPane1.setViewportView(table_data_produksi_atb);

        button_export.setBackground(new java.awt.Color(255, 255, 255));
        button_export.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export.setText("Export to Excel");
        button_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportActionPerformed(evt);
            }
        });

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

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        button_delete.setBackground(new java.awt.Color(255, 255, 255));
        button_delete.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete.setText("Delete");
        button_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_deleteActionPerformed(evt);
            }
        });

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("ID Pegawai :");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Tgl Mulai :");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Keping :");

        txt_keping.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_keping.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_kepingKeyTyped(evt);
            }
        });

        Date_mulai.setBackground(new java.awt.Color(255, 255, 255));
        Date_mulai.setDate(new Date());
        Date_mulai.setDateFormatString("dd MMMM yyyy");
        Date_mulai.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_tambah.setBackground(new java.awt.Color(255, 255, 255));
        button_tambah.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_tambah.setText("Tambah");
        button_tambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_tambahActionPerformed(evt);
            }
        });

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Waktu Mulai :");

        ComboBox_id_pegawai_atb.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Spinner_jam_mulai.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Spinner_jam_mulai.setModel(new javax.swing.SpinnerNumberModel(8, 0, 23, 1));

        Spinner_menit_mulai.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Spinner_menit_mulai.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText(":");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("No LP :");

        txt_no_lp_input.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        txt_operator_nama.setEditable(false);
        txt_operator_nama.setBackground(new java.awt.Color(255, 255, 255));
        txt_operator_nama.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_pick_pekerja.setBackground(new java.awt.Color(255, 255, 255));
        button_pick_pekerja.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_pick_pekerja.setText("...");
        button_pick_pekerja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_pick_pekerjaActionPerformed(evt);
            }
        });

        txt_operator_id.setEditable(false);
        txt_operator_id.setBackground(new java.awt.Color(255, 255, 255));
        txt_operator_id.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel32.setBackground(new java.awt.Color(255, 255, 255));
        jLabel32.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel32.setText("Operator :");

        button_edit.setBackground(new java.awt.Color(255, 255, 255));
        button_edit.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_edit.setText("Ubah");
        button_edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_editActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ComboBox_id_pegawai_atb, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Date_mulai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(Spinner_jam_mulai, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Spinner_menit_mulai, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txt_no_lp_input)
                    .addComponent(txt_keping)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txt_operator_id)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_pick_pekerja, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txt_operator_nama)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(button_tambah)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_delete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_edit)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_no_lp_input, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_id_pegawai_atb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_mulai, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Spinner_jam_mulai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Spinner_menit_mulai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_keping, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txt_operator_id, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_pick_pekerja, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_operator_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_edit, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel10.setText("Watt ATB :");

        txt_watt_atb.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        txt_watt_atb.setText("300");
        txt_watt_atb.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_watt_atbKeyPressed(evt);
            }
        });

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel11.setText("Tarif / KWh (Normal) :");

        txt_tarif_kwh_normal.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        txt_tarif_kwh_normal.setText("1002");
        txt_tarif_kwh_normal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_tarif_kwh_normalKeyPressed(evt);
            }
        });

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel19.setText("Tarif / KWh (Malam) :");

        txt_tarif_kwh_malam.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        txt_tarif_kwh_malam.setText("1483");
        txt_tarif_kwh_malam.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_tarif_kwh_malamKeyPressed(evt);
            }
        });

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        button_waktu_selesai.setBackground(new java.awt.Color(255, 255, 255));
        button_waktu_selesai.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_waktu_selesai.setText("Simpan Waktu Selesai");
        button_waktu_selesai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_waktu_selesaiActionPerformed(evt);
            }
        });

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel28.setText("Waktu Selesai :");

        Date_selesai.setBackground(new java.awt.Color(255, 255, 255));
        Date_selesai.setDate(new Date());
        Date_selesai.setDateFormatString("dd MMMM yyyy");
        Date_selesai.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel29.setBackground(new java.awt.Color(255, 255, 255));
        jLabel29.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel29.setText("Tgl Selesai :");

        jLabel30.setBackground(new java.awt.Color(255, 255, 255));
        jLabel30.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setText(":");

        Spinner_menit_selesai.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Spinner_menit_selesai.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));

        Spinner_jam_selesai.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Spinner_jam_selesai.setModel(new javax.swing.SpinnerNumberModel(8, 0, 23, 1));

        jLabel31.setBackground(new java.awt.Color(255, 255, 255));
        jLabel31.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel31.setText("Layer Selesai :");

        ComboBox_layer_selesai.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_layer_selesai.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Selesai 1 Layer", "Selesai 2 Layer" }));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Date_selesai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ComboBox_layer_selesai, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(Spinner_jam_selesai, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Spinner_menit_selesai, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 76, Short.MAX_VALUE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(button_waktu_selesai)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_selesai, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Spinner_jam_selesai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Spinner_menit_selesai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_layer_selesai, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(button_waktu_selesai, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("Grade :");

        txt_search_grade.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_grade.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_gradeKeyPressed(evt);
            }
        });

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel23.setText("ID ATB :");

        txt_search_id_mesin_ATB.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_id_mesin_ATB.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_id_mesin_ATBKeyPressed(evt);
            }
        });

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel13.setText("Total Biaya Listrik :");

        label_total_biaya_listrik.setBackground(new java.awt.Color(255, 255, 255));
        label_total_biaya_listrik.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_biaya_listrik.setText("TOTAL");

        ComboBox_jenis_filter_tanggal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_jenis_filter_tanggal.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tgl Mulai", "Tgl Penilaian" }));

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel16.setText("Nama Operator :");

        txt_search_nama_operator.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_nama_operator.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_nama_operatorKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_kpg)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_biaya_listrik))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_no_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_jenis_filter_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Datefilter1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Datefilter2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_nama_operator, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel23)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_id_mesin_ATB, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_watt_atb, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_tarif_kwh_normal, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_tarif_kwh_malam, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 169, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_search_no_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Datefilter1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Datefilter2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_jenis_filter_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_search_nama_operator, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_id_mesin_ATB, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_watt_atb, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_tarif_kwh_normal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_tarif_kwh_malam, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_data, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_kpg, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_gram, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_biaya_listrik, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Summary", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        label_total_data_rekap_kinerja.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_rekap_kinerja.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_data_rekap_kinerja.setText("TOTAL");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("Total Data :");

        table_data_rekap_per_karyawan.setAutoCreateRowSorter(true);
        table_data_rekap_per_karyawan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_rekap_per_karyawan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Pegawai ATB", "Total Kpg", "Total Gram", "Total Menit", "Menit / Kpg", "Kpg/Shift", "Total Biaya Listrik", "Biaya Listrik / Kpg", "Upah Borong"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        jScrollPane2.setViewportView(table_data_rekap_per_karyawan);

        Button_export_rekap_karyawan.setText("Export");
        Button_export_rekap_karyawan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_export_rekap_karyawanActionPerformed(evt);
            }
        });

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel18.setText("AVG Kpg / Shift :");

        label_avg_kpg_per_shift_rekap_kinerja.setBackground(new java.awt.Color(255, 255, 255));
        label_avg_kpg_per_shift_rekap_kinerja.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_avg_kpg_per_shift_rekap_kinerja.setText("TOTAL");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data_rekap_kinerja)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_avg_kpg_per_shift_rekap_kinerja)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Button_export_rekap_karyawan)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_avg_kpg_per_shift_rekap_kinerja, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_data_rekap_kinerja, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(Button_export_rekap_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));

        label_total_data_rekap_grade.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_rekap_grade.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_data_rekap_grade.setText("TOTAL");

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel22.setText("Total Data :");

        table_data_rekap_per_grade.setAutoCreateRowSorter(true);
        table_data_rekap_per_grade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_rekap_per_grade.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Grade", "Total Kpg", "Total Gram", "Total Menit", "Menit / Kpg", "Kpg/Shift", "Total Biaya Listrik", "Biaya Listrik / Kpg", "Upah Borong"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        jScrollPane4.setViewportView(table_data_rekap_per_grade);

        Button_export_rekap_grade.setText("Export");
        Button_export_rekap_grade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_export_rekap_gradeActionPerformed(evt);
            }
        });

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel20.setText("AVG Kpg / Shift :");

        label_avg_kpg_per_shift_rekap_grade.setBackground(new java.awt.Color(255, 255, 255));
        label_avg_kpg_per_shift_rekap_grade.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_avg_kpg_per_shift_rekap_grade.setText("TOTAL");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data_rekap_grade)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_avg_kpg_per_shift_rekap_grade)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Button_export_rekap_grade)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_avg_kpg_per_shift_rekap_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_data_rekap_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(Button_export_rekap_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel24.setText("1 Shift Kerja = 7 jam = 480 Menit");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel24)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txt_search_no_lpKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_no_lpKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_produksi();
        }
    }//GEN-LAST:event_txt_search_no_lpKeyPressed

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        refreshTable_produksi();
    }//GEN-LAST:event_button_searchActionPerformed

    private void button_tambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_tambahActionPerformed
        try {
            // TODO add your handling code here:
            boolean check = true;
            float keping = 0;
            float berat = 0;

            sql = "SELECT `no_laporan_produksi`, `keping_upah`, `berat_basah` FROM `tb_laporan_produksi` WHERE `no_laporan_produksi` = '" + txt_no_lp_input.getText() + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "No LP " + txt_no_lp_input.getText() + " tidak ditemukan!");
                check = false;
            } else if (txt_no_lp_input.getText() == null || txt_no_lp_input.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "No LP tidak bisa kosong");
                check = false;
            } else if (Date_mulai.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Tanggal belum di pilih");
                check = false;
            } else {
                try {
                    keping = Float.valueOf(txt_keping.getText());
                    float berat_per_kpg = rs.getFloat("berat_basah") / rs.getFloat("keping_upah");
                    berat = Math.round(keping * berat_per_kpg * 10f) / 10f;
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Format Keping salah!");
                    check = false;
                }
            }

            if (check) {
                try {
                    Utility.db.getConnection().setAutoCommit(false);

                    String waktu_mulai = dateFormat.format(Date_mulai.getDate()) + " " + Spinner_jam_mulai.getValue().toString() + ":" + Spinner_menit_mulai.getValue().toString() + ":00";
                    String Query = "INSERT INTO `tb_atb_produksi`(`no_laporan_produksi`, `id_pegawai_atb`, `waktu_mulai`, `keping`, `gram`, `operator`) "
                            + "VALUES ("
                            + "'" + txt_no_lp_input.getText() + "',"
                            + "'" + ComboBox_id_pegawai_atb.getSelectedItem().toString() + "',"
                            + "'" + waktu_mulai + "',"
                            + "" + keping + ", "
                            + "" + berat + ", "
                            + "'" + txt_operator_id.getText() + "')";
                    Utility.db.getStatement().executeUpdate(Query);

                    String Query2 = "UPDATE `tb_laporan_produksi` SET `memo_lp` = CONCAT(`memo_lp`, ' - ATB') WHERE `no_laporan_produksi` = '" + txt_no_lp_input.getText() + "' AND `memo_lp` NOT LIKE '% - ATB%'";
                    Utility.db.getStatement().executeUpdate(Query2);

                    Utility.db.getConnection().commit();
                    JOptionPane.showMessageDialog(this, "data SAVED!");
                    refreshTable_produksi();
                } catch (Exception e) {
                    Utility.db.getConnection().rollback();
                    JOptionPane.showMessageDialog(this, e);
                    Logger.getLogger(JPanel_ProduksiATB.class.getName()).log(Level.SEVERE, null, e);
                } finally {
                    Utility.db.getConnection().setAutoCommit(true);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_ProduksiATB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_tambahActionPerformed

    private void button_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_deleteActionPerformed
        // TODO add your handling code here:
        try {
            int j = table_data_produksi_atb.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan Pilih Data yang akan di hapus");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    String Query = "DELETE FROM `tb_atb_produksi` WHERE `no` = '" + table_data_produksi_atb.getValueAt(j, 0) + "'";
                    if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                        JOptionPane.showMessageDialog(this, "data deleted Successfully");
                        refreshTable_produksi();
                    } else {
                        JOptionPane.showMessageDialog(this, "delete failed!");
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_ProduksiATB.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_deleteActionPerformed

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_data_produksi_atb.getModel();
        ExportToExcel.writeToExcel(model, jPanel2);
    }//GEN-LAST:event_button_exportActionPerformed

    private void txt_watt_atbKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_watt_atbKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_produksi();
        }
    }//GEN-LAST:event_txt_watt_atbKeyPressed

    private void txt_tarif_kwh_normalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_tarif_kwh_normalKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_produksi();
        }
    }//GEN-LAST:event_txt_tarif_kwh_normalKeyPressed

    private void txt_tarif_kwh_malamKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_tarif_kwh_malamKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_produksi();
        }
    }//GEN-LAST:event_txt_tarif_kwh_malamKeyPressed

    private void button_waktu_selesaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_waktu_selesaiActionPerformed
        // TODO add your handling code here:
        try {
            int j = table_data_produksi_atb.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan Pilih Data yang sudah selesai");
            } else {
                String waktu_selesai = dateFormat.format(Date_selesai.getDate()) + " " + Spinner_jam_selesai.getValue().toString() + ":" + Spinner_menit_selesai.getValue().toString() + ":00";
                sql = "SELECT IF(`waktu_mulai` > '" + waktu_selesai + "', 1, 0) AS 'cek' FROM `tb_atb_produksi` WHERE `no` = '" + table_data_produksi_atb.getValueAt(j, 0) + "'";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    if (rs.getInt("cek") == 1) {
                        JOptionPane.showMessageDialog(this, "Waktu selesai tidak bisa lebih awal dari waktu mulai");
                    } else {
                        String Query = "UPDATE `tb_atb_produksi` SET "
                                + "`waktu_selesai`='" + waktu_selesai + "', "
                                + "`layer_selesai`='" + ComboBox_layer_selesai.getSelectedItem().toString() + "' "
                                + "WHERE `no` = '" + table_data_produksi_atb.getValueAt(j, 0) + "'";
                        if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                            JOptionPane.showMessageDialog(this, "data SAVED");
                            refreshTable_produksi();
                        } else {
                            JOptionPane.showMessageDialog(this, "UPDATE failed!");
                        }
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_ProduksiATB.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_waktu_selesaiActionPerformed

    private void txt_search_gradeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_gradeKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_produksi();
        }
    }//GEN-LAST:event_txt_search_gradeKeyPressed

    private void txt_search_id_mesin_ATBKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_id_mesin_ATBKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_produksi();
        }
    }//GEN-LAST:event_txt_search_id_mesin_ATBKeyPressed

    private void Button_export_rekap_karyawanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_export_rekap_karyawanActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_data_rekap_per_karyawan.getModel();
        ExportToExcel.writeToExcel(model, jPanel2);
    }//GEN-LAST:event_Button_export_rekap_karyawanActionPerformed

    private void Button_export_rekap_gradeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_export_rekap_gradeActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_data_rekap_per_grade.getModel();
        ExportToExcel.writeToExcel(model, jPanel2);
    }//GEN-LAST:event_Button_export_rekap_gradeActionPerformed

    private void button_pick_pekerjaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_pick_pekerjaActionPerformed
        // TODO add your handling code here:
        String filter_tgl = "";
        Browse_Karyawan dialog = new Browse_Karyawan(new javax.swing.JFrame(), true, filter_tgl);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);

        int x = Browse_Karyawan.table_list_karyawan.getSelectedRow();
        if (x != -1) {
            txt_operator_id.setText(Browse_Karyawan.table_list_karyawan.getValueAt(x, 0).toString());
            txt_operator_nama.setText(Browse_Karyawan.table_list_karyawan.getValueAt(x, 1).toString());
        }
    }//GEN-LAST:event_button_pick_pekerjaActionPerformed

    private void txt_search_nama_operatorKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_nama_operatorKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_produksi();
        }
    }//GEN-LAST:event_txt_search_nama_operatorKeyPressed

    private void button_editActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_editActionPerformed
        // TODO add your handling code here:
        try {
            int j = table_data_produksi_atb.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan Pilih Data yang akan di hapus");
            } else {
                boolean check = true;
                float keping = 0;
                float berat = 0;

                sql = "SELECT `no_laporan_produksi`, `keping_upah`, `berat_basah` FROM `tb_laporan_produksi` WHERE `no_laporan_produksi` = '" + txt_no_lp_input.getText() + "'";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(this, "No LP " + txt_no_lp_input.getText() + " tidak ditemukan!");
                    check = false;
                } else if (txt_no_lp_input.getText() == null || txt_no_lp_input.getText().equals("")) {
                    JOptionPane.showMessageDialog(this, "No LP tidak bisa kosong");
                    check = false;
                } else if (Date_mulai.getDate() == null) {
                    JOptionPane.showMessageDialog(this, "Tanggal belum di pilih");
                    check = false;
                } else {
                    try {
                        keping = Float.valueOf(txt_keping.getText());
                        float berat_per_kpg = rs.getFloat("berat_basah") / rs.getFloat("keping_upah");
                        berat = Math.round(keping * berat_per_kpg * 10f) / 10f;
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, "Format Keping salah!");
                        check = false;
                    }
                }
                if (check) {
                    String waktu_mulai = dateFormat.format(Date_mulai.getDate()) + " " + Spinner_jam_mulai.getValue().toString() + ":" + Spinner_menit_mulai.getValue().toString() + ":00";
                    String Query = "UPDATE `tb_atb_produksi` SET "
                            + "`id_pegawai_atb`='" + ComboBox_id_pegawai_atb.getSelectedItem().toString() + "', "
                            + "`keping`='" + keping + "', "
                            + "`gram`='" + berat + "', "
                            + "`waktu_mulai`='" + waktu_mulai + "', "
                            + "`operator`='" + txt_operator_id.getText() + "' "
                            + "WHERE `no` = '" + table_data_produksi_atb.getValueAt(j, 0).toString() + "'";
                    if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                        JOptionPane.showMessageDialog(this, "data SAVED");
                        refreshTable_produksi();
                    } else {
                        JOptionPane.showMessageDialog(this, "UPDATE failed!");
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_ProduksiATB.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_editActionPerformed

    private void txt_kepingKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_kepingKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_kepingKeyTyped


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Button_export_rekap_grade;
    private javax.swing.JButton Button_export_rekap_karyawan;
    private javax.swing.JComboBox<String> ComboBox_id_pegawai_atb;
    private javax.swing.JComboBox<String> ComboBox_jenis_filter_tanggal;
    private javax.swing.JComboBox<String> ComboBox_layer_selesai;
    private com.toedter.calendar.JDateChooser Date_mulai;
    private com.toedter.calendar.JDateChooser Date_selesai;
    private com.toedter.calendar.JDateChooser Datefilter1;
    private com.toedter.calendar.JDateChooser Datefilter2;
    private javax.swing.JSpinner Spinner_jam_mulai;
    private javax.swing.JSpinner Spinner_jam_selesai;
    private javax.swing.JSpinner Spinner_menit_mulai;
    private javax.swing.JSpinner Spinner_menit_selesai;
    public javax.swing.JButton button_delete;
    public javax.swing.JButton button_edit;
    private javax.swing.JButton button_export;
    private javax.swing.JButton button_pick_pekerja;
    public static javax.swing.JButton button_search;
    public javax.swing.JButton button_tambah;
    public javax.swing.JButton button_waktu_selesai;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel label_avg_kpg_per_shift_rekap_grade;
    private javax.swing.JLabel label_avg_kpg_per_shift_rekap_kinerja;
    private javax.swing.JLabel label_total_biaya_listrik;
    private javax.swing.JLabel label_total_data;
    private javax.swing.JLabel label_total_data_rekap_grade;
    private javax.swing.JLabel label_total_data_rekap_kinerja;
    private javax.swing.JLabel label_total_gram;
    private javax.swing.JLabel label_total_kpg;
    private javax.swing.JTable table_data_produksi_atb;
    private javax.swing.JTable table_data_rekap_per_grade;
    private javax.swing.JTable table_data_rekap_per_karyawan;
    private javax.swing.JTextField txt_keping;
    private javax.swing.JTextField txt_no_lp_input;
    private javax.swing.JTextField txt_operator_id;
    private javax.swing.JTextField txt_operator_nama;
    private javax.swing.JTextField txt_search_grade;
    private javax.swing.JTextField txt_search_id_mesin_ATB;
    private javax.swing.JTextField txt_search_nama_operator;
    private javax.swing.JTextField txt_search_no_lp;
    private javax.swing.JTextField txt_tarif_kwh_malam;
    private javax.swing.JTextField txt_tarif_kwh_normal;
    private javax.swing.JTextField txt_watt_atb;
    // End of variables declaration//GEN-END:variables

}
