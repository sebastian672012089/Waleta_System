package waleta_system.Manajemen;

import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;
import waleta_system.Class.ExportToExcel;

public class JPanel_Rekap_BiayaLP extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public JPanel_Rekap_BiayaLP() {
        initComponents();
    }

    public void init() {
        try {
//            String this_year = new SimpleDateFormat("yyyy").format(new Date());
//            String this_month = new SimpleDateFormat("MM").format(new Date());
//            Calendar calendar = Calendar.getInstance();
//            calendar.set(Integer.valueOf(this_year), Integer.valueOf(this_month)-1, 1);
//            Date first_date = calendar.getTime();
//            Date_1.setDate(first_date);
//            Date_2.setDate(new Date());

            try {
                ComboBox_Grade.removeAllItems();
                ComboBox_Grade.addItem("All");
                sql = "SELECT `kode_grade` FROM `tb_grade_bahan_baku` ORDER BY `kode_grade` ASC";
                ResultSet rs1 = Utility.db.getStatement().executeQuery(sql);
                while (rs1.next()) {
                    ComboBox_Grade.addItem(rs1.getString("kode_grade"));
                }

                ComboBox_Ruangan.removeAllItems();
                ComboBox_Ruangan.addItem("All");
                sql = "SELECT DISTINCT(`ruangan`) AS 'ruangan' FROM `tb_laporan_produksi` WHERE 1";
                rs = Utility.db.getStatement().executeQuery(sql);
                while (rs.next()) {
                    ComboBox_Ruangan.addItem(rs.getString("ruangan"));
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e);
                Logger.getLogger(JPanel_Rekap_BiayaLP.class.getName()).log(Level.SEVERE, null, e);
            }
            AutoCompleteDecorator.decorate(ComboBox_Grade);

            Table_laporan_produksi.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && Table_laporan_produksi.getSelectedRow() != -1) {
                        int i = Table_laporan_produksi.getSelectedRow();
                        if (i > -1) {
                            String no_lp = Table_laporan_produksi.getValueAt(i, 0).toString();
                            label_grading_no_lp.setText(no_lp);
                            refreshTableGradingGBJ(no_lp);
                        }
                    }
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(JPanel_Rekap_BiayaLP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTableLP() {
        loadTableLP();
        refreshTableGradingGBJ_Rekap();
    }

    public void loadTableLP() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_laporan_produksi.getModel();
            model.setRowCount(0);
            decimalFormat.setMaximumFractionDigits(0);
            float total_gram = 0, total_gram_lengkap = 0, total_lp_lengkap = 0, total_gram_bjd = 0;
            double total_nilai_baku = 0, total_biaya_produksi = 0, total_biaya_kaki = 0, total_harga_jual = 0, total_margin = 0;
            double biaya_produksi_per_gr = 0, kurs = 0;
            float harga_kaki = 0;
            try {
                harga_kaki = Integer.valueOf(txt_harga_kaki.getText());
            } catch (NumberFormatException e) {
            }
            try {
                biaya_produksi_per_gr = Float.valueOf(txt_biaya_produksi.getText());
            } catch (NumberFormatException e) {
            }
            try {
                kurs = Double.valueOf(txt_kurs.getText());
            } catch (NumberFormatException e) {
            }

            String ruang = "";
            if (!"All".equals(ComboBox_Ruangan.getSelectedItem().toString())) {
                ruang = "AND `ruangan` = '" + ComboBox_Ruangan.getSelectedItem().toString() + "'";
            }

            String tanggal = "";
            if (Date_1.getDate() != null && Date_2.getDate() != null) {
                tanggal = "AND `tanggal_lp` BETWEEN '" + dateFormat.format(Date_1.getDate()) + "' AND '" + dateFormat.format(Date_2.getDate()) + "'";
            }

            String kode_grade = "AND `tb_laporan_produksi`.`kode_grade` IN (";
            if (tabel_gradeBaku.getRowCount() > 0) {
                for (int i = 0; i < tabel_gradeBaku.getRowCount(); i++) {
                    kode_grade = kode_grade + "'" + tabel_gradeBaku.getValueAt(i, 0).toString() + "'";
                    if (i < tabel_gradeBaku.getRowCount() - 1) {
                        kode_grade = kode_grade + " ,";
                    }
                }
                kode_grade = kode_grade + ")";
            } else {
                kode_grade = "";
            }

            sql = "SELECT `tb_laporan_produksi`.`no_laporan_produksi`, `tb_laporan_produksi`.`no_kartu_waleta`, `memo_lp`, `tanggal_lp`, `tb_laporan_produksi`.`kode_grade`, `ruangan`, `berat_basah`, (`tambahan_kaki1` + `tambahan_kaki2`) AS 'kaki', `cetak_dikerjakan`, `ketua_regu`,  "
                    + "ROUND((`berat_basah` * `tb_grading_bahan_baku`.`harga_bahanbaku`), 5) AS 'nilai_baku', "
                    + "BJD_PRICE.`berat_grading_bj`, "
                    + "BJD_PRICE.`rmb_jual`, "
                    + "`tb_bahan_jadi_masuk`.`tanggal_grading` "
                    + "FROM `tb_laporan_produksi` "
                    + "LEFT JOIN `tb_finishing_2` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_finishing_2`.`no_laporan_produksi`"
                    + "LEFT JOIN `tb_cabut` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cabut`.`no_laporan_produksi`"
                    + "LEFT JOIN `tb_cetak` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cetak`.`no_laporan_produksi`"
                    + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_grading_bahan_baku`.`no_kartu_waleta` AND `tb_laporan_produksi`.`kode_grade` = `tb_grading_bahan_baku`.`kode_grade` "
                    + "LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_bahan_jadi_masuk`.`kode_asal` "
                    + "LEFT JOIN (SELECT `kode_asal_bahan_jadi`, SUM(`tb_grading_bahan_jadi`.`gram`) AS 'berat_grading_bj', SUM((`tb_grading_bahan_jadi`.`gram`/1000) * ("
                    + "SELECT `cny_kg` FROM `tb_grade_bahan_jadi_harga` WHERE `grade` = `tb_grading_bahan_jadi`.`grade_bahan_jadi` ORDER BY `tanggal` DESC LIMIT 1 \n"
                    + ")) AS 'rmb_jual' \n"
                    + "FROM `tb_grading_bahan_jadi` \n"
                    + "WHERE 1 GROUP BY `kode_asal_bahan_jadi`) BJD_PRICE "
                    + "ON `tb_laporan_produksi`.`no_laporan_produksi` = BJD_PRICE.`kode_asal_bahan_jadi`\n"
                    + "WHERE "
                    + "`tb_laporan_produksi`.`no_laporan_produksi` LIKE '%" + txt_search_lp.getText() + "%' "
                    + "AND `tb_laporan_produksi`.`no_kartu_waleta` LIKE '%" + txt_no_kartu.getText() + "%'"
                    + "AND `tb_laporan_produksi`.`memo_lp` LIKE '%" + txt_search_memo.getText() + "%'"
                    + tanggal + ruang + kode_grade
                    + "GROUP BY `tb_laporan_produksi`.`no_laporan_produksi`";
            PreparedStatement pst = Utility.db.getConnection().prepareStatement(sql);
            ResultSet result = pst.executeQuery();
            Object[] row = new Object[25];
            while (result.next()) {
                row[0] = result.getString("no_laporan_produksi");
                row[1] = result.getString("no_kartu_waleta");
                row[2] = result.getString("kode_grade");
                row[3] = result.getDate("tanggal_lp");
                row[4] = result.getString("memo_lp");
                row[5] = result.getString("ruangan");
                row[6] = result.getInt("berat_basah");
                total_gram = total_gram + result.getInt("berat_basah");
                row[7] = result.getDouble("nilai_baku");
                row[8] = result.getInt("kaki");
                double biaya_kaki = (harga_kaki / 1000) * kurs * result.getFloat("kaki");
                row[9] = biaya_kaki;
                double biaya_produksi = biaya_produksi_per_gr * result.getInt("berat_basah");
                row[10] = Math.round(biaya_produksi);
                int berat_grading_bj = result.getInt("berat_grading_bj");
                row[11] = berat_grading_bj;
                double harga_jual = result.getDouble("rmb_jual") * kurs * 0.98d;
                row[12] = Math.round(harga_jual);
                double gross_margin = 0;
                if (result.getInt("nilai_baku") > 0 && berat_grading_bj > 0) {
                    gross_margin = harga_jual - (biaya_produksi + result.getDouble("nilai_baku") + biaya_kaki);
                    total_lp_lengkap++;
                    total_gram_lengkap = total_gram_lengkap + result.getInt("berat_basah");
                    total_nilai_baku = total_nilai_baku + result.getDouble("nilai_baku");
                    total_gram_bjd = total_gram_bjd + berat_grading_bj;
                    total_biaya_produksi = total_biaya_produksi + biaya_produksi;
                    total_biaya_kaki = total_biaya_kaki + biaya_kaki;
                    total_harga_jual = total_harga_jual + harga_jual;
                }
                row[13] = Math.round(gross_margin);
                total_margin = total_margin + gross_margin;
                float persen_margin = ((float) gross_margin / (result.getFloat("nilai_baku") + (float) biaya_produksi)) * 100f;
                row[14] = Math.round(persen_margin * 100f) / 100f;
                row[15] = Math.round(((result.getFloat("berat_basah") - (berat_grading_bj - result.getFloat("kaki"))) / result.getFloat("berat_basah")) * 10000.f) / 100.f;
                row[16] = result.getString("ketua_regu");
                row[17] = result.getDate("tanggal_grading");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_laporan_produksi);

            int total_data = Table_laporan_produksi.getRowCount();
            float persen_lengkap = Math.round((total_gram_lengkap / total_gram) * 1000) / 10;
            float rendemen = Math.round((total_gram_bjd / total_gram_lengkap) * 1000) / 10;
            label_total_keseluruhan.setText(decimalFormat.format(total_data) + " LP   " + decimalFormat.format(total_gram) + " Gram");
            label_persen_data_lengkap.setText(persen_lengkap + "%");
            label_total_bahan_baku.setText(decimalFormat.format(total_lp_lengkap) + " LP   " + decimalFormat.format(total_gram_lengkap) + " Gram");
            label_total_nilai_baku.setText(decimalFormat.format(total_nilai_baku));
            label_total_gram_bjd.setText(decimalFormat.format(total_gram_bjd) + " Gram");
            label_total_rendemen.setText(decimalFormat.format(rendemen) + "%");
            label_total_jual.setText(decimalFormat.format(total_harga_jual));
            label_total_produksi.setText(decimalFormat.format(total_biaya_produksi));
            label_total_kaki.setText(decimalFormat.format(total_biaya_kaki));
            label_total_margin.setText(decimalFormat.format(total_margin));
            double persen_margin = (total_margin / (total_nilai_baku + total_biaya_produksi + total_biaya_kaki)) * 100;
            decimalFormat.setMaximumFractionDigits(1);
            label_total_margin_persen.setText("(" + decimalFormat.format(persen_margin) + "%)");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Rekap_BiayaLP.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void refreshTableGradingGBJ_Rekap() {
        try {
            double kurs = 0;
            long total_gram = 0;
            double total_harga = 0;
            DefaultTableModel model = (DefaultTableModel) tabel_hasilGrading_rekap.getModel();
            model.setRowCount(0);
            decimalFormat.setMaximumFractionDigits(0);

            try {
                kurs = Double.valueOf(txt_kurs.getText());
            } catch (NumberFormatException e) {
            }

            String no_lp = "";
            for (int i = 0; i < Table_laporan_produksi.getRowCount(); i++) {
                if ((double) Table_laporan_produksi.getValueAt(i, 7) > 0 && (int) Table_laporan_produksi.getValueAt(i, 11) > 0) {
                    if (i != 0) {
                        no_lp = no_lp + ", ";
                    }
                    no_lp = no_lp + "'" + Table_laporan_produksi.getValueAt(i, 0).toString() + "'";
                }
            }
            sql = "SELECT `tb_grade_bahan_jadi`.`kode_grade`, SUM(`gram`) AS 'gram', "
                    + "(SELECT `cny_kg` FROM `tb_grade_bahan_jadi_harga` WHERE `grade` = `tb_grading_bahan_jadi`.`grade_bahan_jadi` ORDER BY `tanggal` DESC LIMIT 1) AS 'harga' \n"
                    + "FROM `tb_grading_bahan_jadi` "
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_grading_bahan_jadi`.`grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`\n"
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_grading_bahan_jadi`.`kode_asal_bahan_jadi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "WHERE "
                    + "`tb_laporan_produksi`.`no_laporan_produksi` IN (" + no_lp + ") "
                    + "GROUP BY `tb_grade_bahan_jadi`.`kode_grade`";
            PreparedStatement pst = Utility.db.getConnection().prepareStatement(sql);
            ResultSet result = pst.executeQuery();
            Object[] row = new Object[5];
            while (result.next()) {
                row[0] = result.getString("kode_grade");
                row[1] = result.getInt("gram");
                total_gram = total_gram + result.getInt("gram");
                double harga_rmb_per_gram = result.getFloat("harga") / 1000d;
                row[2] = Math.round(harga_rmb_per_gram * 100f) / 100f;
                double harga_rmb = result.getFloat("gram") * harga_rmb_per_gram;
                row[3] = Math.round(harga_rmb * 100f) / 100f;
                double harga_rupiah = harga_rmb * kurs * 0.98d;
                row[4] = Math.round(harga_rupiah);
                total_harga = total_harga + harga_rupiah;
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_hasilGrading_rekap);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Rekap_BiayaLP.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void refreshTableGradingGBJ(String no_lp) {
        try {
            double kurs = 0;
            DefaultTableModel model = (DefaultTableModel) tabel_hasilGrading.getModel();
            model.setRowCount(0);
            decimalFormat.setMaximumFractionDigits(0);

            try {
                kurs = Double.valueOf(txt_kurs.getText());
            } catch (NumberFormatException e) {
            }
//            sql = "SELECT `kode_asal_bahan_jadi`, `tb_grade_bahan_jadi`.`kode_grade`, `keping`, `gram`, `tb_grade_bahan_jadi`.`harga` \n"
//                    + "FROM `tb_grading_bahan_jadi` "
//                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_grading_bahan_jadi`.`grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`\n"
//                    + "WHERE `kode_asal_bahan_jadi` = '" + no_lp + "'";
            sql = "SELECT `kode_asal_bahan_jadi`, `tb_grade_bahan_jadi`.`kode_grade`, `keping`, `gram`, "
                    + "(SELECT `cny_kg` FROM `tb_grade_bahan_jadi_harga` WHERE `grade` = `tb_grading_bahan_jadi`.`grade_bahan_jadi` ORDER BY `tanggal` DESC LIMIT 1) AS 'harga' \n"
                    + "FROM `tb_grading_bahan_jadi` "
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_grading_bahan_jadi`.`grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`\n"
                    + "WHERE `kode_asal_bahan_jadi` = '" + no_lp + "'";
            PreparedStatement pst = Utility.db.getConnection().prepareStatement(sql);
            ResultSet result = pst.executeQuery();
            Object[] row = new Object[5];
            long total_gram = 0;
            double total_harga = 0;
            while (result.next()) {
                row[0] = result.getString("kode_grade");
                row[1] = result.getInt("gram");
                total_gram = total_gram + result.getInt("gram");
                double harga_rmb_per_gram = result.getFloat("harga") / 1000d;
                row[2] = Math.round(harga_rmb_per_gram * 100f) / 100f;
                double harga_rmb = result.getFloat("gram") * harga_rmb_per_gram;
                row[3] = Math.round(harga_rmb * 100f) / 100f;
                double harga_rupiah = harga_rmb * kurs * 0.98d;
                row[4] = Math.round(harga_rupiah);
                total_harga = total_harga + harga_rupiah;
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_hasilGrading);

            label_total_gram_grading.setText(decimalFormat.format(total_gram) + " Gram");
            label_total_harga_grading.setText("Rp. " + decimalFormat.format(total_harga));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Rekap_BiayaLP.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel_search_laporan_produksi = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        txt_search_lp = new javax.swing.JTextField();
        button_refresh = new javax.swing.JButton();
        Date_1 = new com.toedter.calendar.JDateChooser();
        Date_2 = new com.toedter.calendar.JDateChooser();
        jLabel15 = new javax.swing.JLabel();
        txt_no_kartu = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        ComboBox_Ruangan = new javax.swing.JComboBox<>();
        jLabel17 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        txt_kurs = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        txt_biaya_produksi = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        txt_harga_kaki = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        txt_search_memo = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        label_total_gradebaku = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabel_gradeBaku = new javax.swing.JTable();
        ComboBox_Grade = new javax.swing.JComboBox<>();
        button_add_grade = new javax.swing.JButton();
        button_remove_grade = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        button_removeALL_grade = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabel_hasilGrading = new javax.swing.JTable();
        button_export = new javax.swing.JButton();
        jLabel22 = new javax.swing.JLabel();
        label_total_bahan_baku = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        label_total_nilai_baku = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        label_total_produksi = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        label_total_jual = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        label_total_margin = new javax.swing.JLabel();
        label_total_harga_grading = new javax.swing.JLabel();
        label_total_gram_grading = new javax.swing.JLabel();
        label_total_keseluruhan = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        label_total_kaki = new javax.swing.JLabel();
        label_total_margin_persen = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        label_total_gram_bjd = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        label_total_rendemen = new javax.swing.JLabel();
        label_persen_data_lengkap = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        label_grading_no_lp = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane6 = new javax.swing.JScrollPane();
        Table_laporan_produksi = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        tabel_hasilGrading_rekap = new javax.swing.JTable();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel_search_laporan_produksi.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_search_laporan_produksi.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        jPanel_search_laporan_produksi.setPreferredSize(new java.awt.Dimension(1334, 61));

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("No LP :");

        jLabel34.setBackground(new java.awt.Color(255, 255, 255));
        jLabel34.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel34.setText("Sampai");

        txt_search_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_lp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_lpKeyPressed(evt);
            }
        });

        button_refresh.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh.setText("Refresh");
        button_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refreshActionPerformed(evt);
            }
        });

        Date_1.setBackground(new java.awt.Color(255, 255, 255));
        Date_1.setToolTipText("");
        Date_1.setDateFormatString("dd MMM yyyy");
        Date_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_1.setMinSelectableDate(new java.util.Date(1420048915000L));

        Date_2.setBackground(new java.awt.Color(255, 255, 255));
        Date_2.setDateFormatString("dd MMM yyyy");
        Date_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel15.setText("No Kartu :");

        txt_no_kartu.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_no_kartu.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_no_kartuKeyPressed(evt);
            }
        });

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel16.setText("Ruangan :");

        ComboBox_Ruangan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel17.setText("Tanggal LP :");

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel20.setText("Kurs RMB to IDR :");

        txt_kurs.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_kurs.setText("2050");
        txt_kurs.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_kursKeyPressed(evt);
            }
        });

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel21.setText("Biaya Produksi / Gr (Rp) :");

        txt_biaya_produksi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_biaya_produksi.setText("3550");
        txt_biaya_produksi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_biaya_produksiKeyPressed(evt);
            }
        });

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel23.setText("Harga Kaki (RMB/Kg)");

        txt_harga_kaki.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_harga_kaki.setText("2000");
        txt_harga_kaki.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_harga_kakiKeyPressed(evt);
            }
        });

        jLabel31.setBackground(new java.awt.Color(255, 255, 255));
        jLabel31.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel31.setText("Memo LP :");

        txt_search_memo.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_memo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_memoKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_search_laporan_produksiLayout = new javax.swing.GroupLayout(jPanel_search_laporan_produksi);
        jPanel_search_laporan_produksi.setLayout(jPanel_search_laporan_produksiLayout);
        jPanel_search_laporan_produksiLayout.setHorizontalGroup(
            jPanel_search_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_laporan_produksiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_search_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_search_laporan_produksiLayout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_no_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_kurs, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_biaya_produksi, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_harga_kaki, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_search_laporan_produksiLayout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel31)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_memo, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_Ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel34)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_2, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_refresh)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_search_laporan_produksiLayout.setVerticalGroup(
            jPanel_search_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_laporan_produksiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_search_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_memo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_Ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_search_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_no_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_kurs, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_biaya_produksi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_harga_kaki, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Kode Grade :");

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel19.setText("Hasil Grading");

        label_total_gradebaku.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gradebaku.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gradebaku.setText("0");

        tabel_gradeBaku.setAutoCreateRowSorter(true);
        tabel_gradeBaku.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_gradeBaku.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade", "Bentuk", "Bulu", "Warna"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        tabel_gradeBaku.setRowSelectionAllowed(false);
        jScrollPane3.setViewportView(tabel_gradeBaku);

        ComboBox_Grade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_add_grade.setBackground(new java.awt.Color(255, 255, 255));
        button_add_grade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_add_grade.setText("Add");
        button_add_grade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_add_gradeActionPerformed(evt);
            }
        });

        button_remove_grade.setBackground(new java.awt.Color(255, 255, 255));
        button_remove_grade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_remove_grade.setText("Remove");
        button_remove_grade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_remove_gradeActionPerformed(evt);
            }
        });

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel18.setText("Grade");

        button_removeALL_grade.setBackground(new java.awt.Color(255, 255, 255));
        button_removeALL_grade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_removeALL_grade.setText("Remove All");
        button_removeALL_grade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_removeALL_gradeActionPerformed(evt);
            }
        });

        tabel_hasilGrading.setAutoCreateRowSorter(true);
        tabel_hasilGrading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_hasilGrading.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade BJ", "Gram", "RMB/Gr", "RMB", "Harga Rp"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        tabel_hasilGrading.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tabel_hasilGrading);

        button_export.setBackground(new java.awt.Color(255, 255, 255));
        button_export.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export.setText("Export");
        button_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportActionPerformed(evt);
            }
        });

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel22.setText("Bahan Baku :");

        label_total_bahan_baku.setBackground(new java.awt.Color(255, 255, 255));
        label_total_bahan_baku.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_bahan_baku.setText("0");

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel24.setText("Biaya Baku :");

        label_total_nilai_baku.setBackground(new java.awt.Color(255, 255, 255));
        label_total_nilai_baku.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_nilai_baku.setText("0");

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel26.setText("Biaya Produksi :");

        label_total_produksi.setBackground(new java.awt.Color(255, 255, 255));
        label_total_produksi.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_produksi.setText("0");

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel28.setText("Harga Jual :");

        label_total_jual.setBackground(new java.awt.Color(255, 255, 255));
        label_total_jual.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_jual.setText("0");

        jLabel30.setBackground(new java.awt.Color(255, 255, 255));
        jLabel30.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel30.setText("Total Gross Margin :");

        label_total_margin.setBackground(new java.awt.Color(255, 255, 255));
        label_total_margin.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_margin.setText("0");

        label_total_harga_grading.setBackground(new java.awt.Color(255, 255, 255));
        label_total_harga_grading.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_harga_grading.setText("Total Harga");

        label_total_gram_grading.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_grading.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gram_grading.setText("Total Gram");

        label_total_keseluruhan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keseluruhan.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_keseluruhan.setText("0");

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel25.setText("Jumlah Keseluruhan  :");

        jLabel27.setBackground(new java.awt.Color(255, 255, 255));
        jLabel27.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel27.setText("Biaya Kaki :");

        label_total_kaki.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kaki.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_kaki.setText("0");

        label_total_margin_persen.setBackground(new java.awt.Color(255, 255, 255));
        label_total_margin_persen.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_margin_persen.setText("0");

        jLabel29.setBackground(new java.awt.Color(255, 255, 255));
        jLabel29.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel29.setText("Note : Price list x 98%");

        jLabel32.setBackground(new java.awt.Color(255, 255, 255));
        jLabel32.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel32.setText("Total BJD :");

        label_total_gram_bjd.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_bjd.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_gram_bjd.setText("0");

        jLabel33.setBackground(new java.awt.Color(255, 255, 255));
        jLabel33.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel33.setText("Rendemen :");

        label_total_rendemen.setBackground(new java.awt.Color(255, 255, 255));
        label_total_rendemen.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_rendemen.setText("0");

        label_persen_data_lengkap.setBackground(new java.awt.Color(255, 255, 255));
        label_persen_data_lengkap.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_persen_data_lengkap.setText("0");

        jLabel35.setBackground(new java.awt.Color(255, 255, 255));
        jLabel35.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel35.setText("Data Lengkap :");

        label_grading_no_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_grading_no_lp.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_grading_no_lp.setText("no_lp");

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        Table_laporan_produksi.setAutoCreateRowSorter(true);
        Table_laporan_produksi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_laporan_produksi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP", "No Kartu", "Grade", "Tgl LP", "memo", "Ruang", "Berat Angin", "Baku (Rp)", "Kaki (Gr)", "Nilai Kaki", "Biaya Prod.", "BJD", "Nilai BJD", "Gross Margin", "%", "SH", "Grup Cabut", "Tgl Grading BJD"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_laporan_produksi.getTableHeader().setReorderingAllowed(false);
        jScrollPane6.setViewportView(Table_laporan_produksi);

        jTabbedPane1.addTab("Data LP", jScrollPane6);

        tabel_hasilGrading_rekap.setAutoCreateRowSorter(true);
        tabel_hasilGrading_rekap.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_hasilGrading_rekap.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade BJ", "Gram", "RMB/Gr", "RMB", "Harga Rp"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        tabel_hasilGrading_rekap.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(tabel_hasilGrading_rekap);

        jTabbedPane1.addTab("Rekap Hasil Grading", jScrollPane4);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel_search_laporan_produksi, javax.swing.GroupLayout.DEFAULT_SIZE, 1346, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel29)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(button_export))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel22)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_bahan_baku)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel24)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_nilai_baku))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel32)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_gram_bjd)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel33)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_rendemen)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel28)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_jual))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel25)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_keseluruhan)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel35)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_persen_data_lengkap))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel26)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_produksi)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel27)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_kaki)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel30)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_margin)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_margin_persen)))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jTabbedPane1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(button_remove_grade)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_removeALL_grade)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 111, Short.MAX_VALUE)
                                .addComponent(label_total_gradebaku)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel18))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(label_grading_no_lp))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(ComboBox_Grade, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_add_grade))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(label_total_gram_grading)
                                .addGap(18, 18, 18)
                                .addComponent(label_total_harga_grading))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel_search_laporan_produksi, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(ComboBox_Grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_add_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_gradebaku, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_removeALL_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_remove_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_grading_no_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 282, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_harga_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_gram_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jTabbedPane1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(label_persen_data_lengkap, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(label_total_keseluruhan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_bahan_baku, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_nilai_baku, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_gram_bjd, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_rendemen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_jual, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(label_total_kaki, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(label_total_margin, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(label_total_margin_persen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(label_total_produksi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))))
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

    private void txt_search_lpKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_lpKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTableLP();
        }
    }//GEN-LAST:event_txt_search_lpKeyPressed

    private void button_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refreshActionPerformed
        // TODO add your handling code here:
        refreshTableLP();
    }//GEN-LAST:event_button_refreshActionPerformed

    private void txt_no_kartuKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_no_kartuKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTableLP();
        }
    }//GEN-LAST:event_txt_no_kartuKeyPressed

    private void button_add_gradeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_add_gradeActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_gradeBaku.getModel();
        boolean add = true;

        if (add) {
            try {
                for (int i = 0; i < tabel_gradeBaku.getRowCount(); i++) {
                    if (ComboBox_Grade.getSelectedItem().toString().equals(tabel_gradeBaku.getValueAt(i, 0).toString())) {
                        throw new Exception("Maaf Grade " + ComboBox_Grade.getSelectedItem().toString() + " Sudah Masuk !");
                    }
                }
                sql = "SELECT * FROM `tb_grade_bahan_baku` WHERE `kode_grade` = '" + ComboBox_Grade.getSelectedItem().toString() + "'";
                ResultSet result = Utility.db.getStatement().executeQuery(sql);
                while (result.next()) {
                    String bulu = null;
                    if (null == result.getString("jenis_bulu")) {
                        bulu = "-";
                    } else {
                        switch (result.getString("jenis_bulu")) {
                            case "Bulu Ringan":
                                bulu = "BR";
                                break;
                            case "Bulu Ringan Sekali/Bulu Ringan":
                                bulu = "BRS/BR";
                                break;
                            case "Bulu Sedang":
                                bulu = "BS";
                                break;
                            case "Bulu Berat":
                                bulu = "BB";
                                break;
                            case "Bulu Berat Sekali":
                                bulu = "BB2";
                                break;
                            default:
                                bulu = "-";
                                break;
                        }
                    }

                    String bentuk = null;
                    if (result.getString("jenis_bentuk").contains("Mangkok")) {
                        bentuk = "Mangkok";
                    } else if (result.getString("jenis_bentuk").contains("Oval")) {
                        bentuk = "Oval";
                    } else if (result.getString("jenis_bentuk").contains("Segitiga") || result.getString("jenis_bentuk").contains("SGTG")) {
                        bentuk = "Segitiga";
                    } else {
                        bentuk = "-";
                    }

                    model.addRow(new Object[]{result.getString("kode_grade"), bentuk, bulu, result.getString("jenis_warna")});
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e);
                Logger.getLogger(JPanel_Rekap_BiayaLP.class.getName()).log(Level.SEVERE, null, e);
            }

            ComboBox_Grade.requestFocus();
            ColumnsAutoSizer.sizeColumnsToFit(tabel_gradeBaku);
            label_total_gradebaku.setText(Integer.toString(tabel_gradeBaku.getRowCount()));
        }
    }//GEN-LAST:event_button_add_gradeActionPerformed

    private void button_remove_gradeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_remove_gradeActionPerformed
        // TODO add your handling code here:
        int i = tabel_gradeBaku.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) tabel_gradeBaku.getModel();
        if (i != -1) {
            model.removeRow(tabel_gradeBaku.getRowSorter().convertRowIndexToModel(i));
        }
        ColumnsAutoSizer.sizeColumnsToFit(tabel_gradeBaku);
        label_total_gradebaku.setText(Integer.toString(tabel_gradeBaku.getRowCount()));
    }//GEN-LAST:event_button_remove_gradeActionPerformed

    private void button_removeALL_gradeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_removeALL_gradeActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_gradeBaku.getModel();
        model.setRowCount(0);
    }//GEN-LAST:event_button_removeALL_gradeActionPerformed

    private void txt_kursKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_kursKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTableLP();
        }
    }//GEN-LAST:event_txt_kursKeyPressed

    private void txt_biaya_produksiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_biaya_produksiKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTableLP();
        }
    }//GEN-LAST:event_txt_biaya_produksiKeyPressed

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_laporan_produksi.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_exportActionPerformed

    private void txt_harga_kakiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_harga_kakiKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTableLP();
        }
    }//GEN-LAST:event_txt_harga_kakiKeyPressed

    private void txt_search_memoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_memoKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTableLP();
        }
    }//GEN-LAST:event_txt_search_memoKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_Grade;
    private javax.swing.JComboBox<String> ComboBox_Ruangan;
    private com.toedter.calendar.JDateChooser Date_1;
    private com.toedter.calendar.JDateChooser Date_2;
    public static javax.swing.JTable Table_laporan_produksi;
    private javax.swing.JButton button_add_grade;
    private javax.swing.JButton button_export;
    private javax.swing.JButton button_refresh;
    private javax.swing.JButton button_removeALL_grade;
    private javax.swing.JButton button_remove_grade;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel_search_laporan_produksi;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label_grading_no_lp;
    private javax.swing.JLabel label_persen_data_lengkap;
    private javax.swing.JLabel label_total_bahan_baku;
    private javax.swing.JLabel label_total_gradebaku;
    private javax.swing.JLabel label_total_gram_bjd;
    private javax.swing.JLabel label_total_gram_grading;
    private javax.swing.JLabel label_total_harga_grading;
    private javax.swing.JLabel label_total_jual;
    private javax.swing.JLabel label_total_kaki;
    private javax.swing.JLabel label_total_keseluruhan;
    private javax.swing.JLabel label_total_margin;
    private javax.swing.JLabel label_total_margin_persen;
    private javax.swing.JLabel label_total_nilai_baku;
    private javax.swing.JLabel label_total_produksi;
    private javax.swing.JLabel label_total_rendemen;
    private javax.swing.JTable tabel_gradeBaku;
    private javax.swing.JTable tabel_hasilGrading;
    private javax.swing.JTable tabel_hasilGrading_rekap;
    private javax.swing.JTextField txt_biaya_produksi;
    private javax.swing.JTextField txt_harga_kaki;
    private javax.swing.JTextField txt_kurs;
    private javax.swing.JTextField txt_no_kartu;
    private javax.swing.JTextField txt_search_lp;
    private javax.swing.JTextField txt_search_memo;
    // End of variables declaration//GEN-END:variables
}
