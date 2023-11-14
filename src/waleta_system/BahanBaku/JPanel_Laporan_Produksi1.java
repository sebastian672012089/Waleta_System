package waleta_system.BahanBaku;

import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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
import waleta_system.Class.ExportToExcel;
import waleta_system.Class.LaporanProduksi;
import waleta_system.Class.Utility;

public class JPanel_Laporan_Produksi1 extends javax.swing.JPanel {

    String sql = null;
    String sql2 = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    float kadar_air_bahan_baku = 0;

    public JPanel_Laporan_Produksi1() {
        initComponents();
    }

    public void init() {
        try {
            Utility.db_sub.connect();
            refreshTable();
            ComboBox_Ruang_LP.removeAllItems();
            ComboBox_Ruang_LP.addItem("All");
            String sub = "SELECT DISTINCT(`ruangan`) AS 'ruangan' FROM `tb_laporan_produksi` WHERE 1";
            ResultSet rs2 = Utility.db.getStatement().executeQuery(sub);
            while (rs2.next()) {
                ComboBox_Ruang_LP.addItem(rs2.getString("ruangan"));
            }

            Table_laporan_produksi.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && Table_laporan_produksi.getSelectedRow() != -1) {
                        int i = Table_laporan_produksi.getSelectedRow();
                    }
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(JPanel_Laporan_Produksi1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList<LaporanProduksi> LaporanProduksiList() {
        ArrayList<LaporanProduksi> LaporanProduksiList = new ArrayList<>();
        try {
            String ruang = "AND `ruangan` = '" + ComboBox_Ruang_LP.getSelectedItem().toString() + "' ";
            if ("All".equals(ComboBox_Ruang_LP.getSelectedItem().toString())) {
                ruang = "";
            }
            String filter_tanggal_lp = "";
            if (Date_Search_LP_1.getDate() != null && Date_Search_LP_2.getDate() != null) {
                filter_tanggal_lp = "AND `tanggal_lp` BETWEEN '" + dateFormat.format(Date_Search_LP_1.getDate()) + "' AND '" + dateFormat.format(Date_Search_LP_2.getDate()) + "' ";
            }
            sql = "SELECT `no_laporan_produksi`, `tb_laporan_produksi`.`no_kartu_waleta`, `tanggal_lp`, `ruangan`, `tb_laporan_produksi`.`kode_grade`, `tb_grade_bahan_baku`.`jenis_bulu`, `tb_grade_bahan_baku`.`jenis_bentuk`, `jenis_bulu_lp`, `memo_lp`, `tb_bahan_baku_masuk`.`kadar_air_bahan_baku`, `tb_bahan_baku_masuk_cheat`.`no_registrasi`, `berat_basah`, `berat_kering`, `jumlah_keping`, `keping_upah`, "
                    + "`kaki_besar_lp`, "
                    + "`kaki_kecil_lp`, "
                    + "`hilang_kaki_lp`, "
                    + "`ada_susur_lp`, "
                    + "`ada_susur_besar_lp`, "
                    + "`tanpa_susur_lp`, "
                    + "`utuh_lp`, "
                    + "`hilang_ujung_lp`, "
                    + "`pecah_1_lp`, "
                    + "`pecah_2`, "
                    + "`jumlah_sobek`, "
                    + "`sobek_lepas`, "
                    + "`jumlah_gumpil`, "
                    + "`tarif_gram`, "
                    + "`grup_lp`  "
                    + "FROM `tb_laporan_produksi` "
                    + "LEFT JOIN `tb_bahan_baku_masuk` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`"
                    + "LEFT JOIN `tb_bahan_baku_masuk_cheat` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta`"
                    + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`\n"
                    + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah`"
                    + "WHERE  `tb_laporan_produksi`.`no_kartu_waleta` LIKE '%" + txt_search_no_kartu.getText() + "%' "
                    + "AND `no_laporan_produksi` LIKE '%" + txt_search_lp.getText() + "%' "
                    + "AND `tb_laporan_produksi`.`kode_grade` LIKE '%" + txt_search_grade.getText() + "%'  "
                    + "AND `memo_lp` LIKE '%" + txt_search_memo.getText() + "%' "
                    + ruang + filter_tanggal_lp
                    + "ORDER BY `no_laporan_produksi` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            LaporanProduksi LaporanProduksi;
            while (rs.next()) {
                LaporanProduksi = new LaporanProduksi(
                        rs.getString("no_laporan_produksi"),
                        rs.getString("no_kartu_waleta"),
                        rs.getDate("tanggal_lp"),
                        null,
                        rs.getString("ruangan"),
                        rs.getString("kode_grade"),
                        rs.getString("jenis_bulu"),
                        rs.getString("jenis_bulu_lp"),
                        rs.getString("memo_lp"),
                        rs.getFloat("kadar_air_bahan_baku"),
                        rs.getInt("berat_basah"),
                        rs.getInt("berat_kering"),
                        rs.getInt("jumlah_keping"),
                        rs.getInt("keping_upah"),
                        rs.getInt("kaki_besar_lp"),
                        rs.getInt("kaki_kecil_lp"),
                        rs.getInt("hilang_kaki_lp"),
                        rs.getInt("ada_susur_lp"),
                        rs.getInt("ada_susur_besar_lp"),
                        rs.getInt("tanpa_susur_lp"),
                        rs.getInt("utuh_lp"),
                        rs.getInt("hilang_ujung_lp"),
                        rs.getInt("pecah_1_lp"),
                        rs.getInt("pecah_2"),
                        rs.getInt("jumlah_gumpil"),
                        rs.getInt("jumlah_sobek"),
                        rs.getInt("sobek_lepas"),
                        0,
                        rs.getString("jenis_bentuk"),
                        rs.getInt("grup_lp"),
                        0
                );
                LaporanProduksiList.add(LaporanProduksi);
            }
        } catch (Exception ex) {
            Logger.getLogger(JPanel_Laporan_Produksi1.class.getName()).log(Level.SEVERE, null, ex);
        }
        return LaporanProduksiList;
    }

    public void refreshTable() {
        try {
            double total_gram = 0, total_kpg = 0;
            DefaultTableModel model = (DefaultTableModel) Table_laporan_produksi.getModel();
            model.setRowCount(0);
            String ruang = "AND `ruangan` = '" + ComboBox_Ruang_LP.getSelectedItem().toString() + "' ";
            if ("All".equals(ComboBox_Ruang_LP.getSelectedItem().toString())) {
                ruang = "";
            }
            String filter_tanggal_lp = "";
            if (Date_Search_LP_1.getDate() != null && Date_Search_LP_2.getDate() != null) {
                filter_tanggal_lp = "AND `cheat_tgl_lp` BETWEEN '" + dateFormat.format(Date_Search_LP_1.getDate()) + "' AND '" + dateFormat.format(Date_Search_LP_2.getDate()) + "' ";
            }
            sql = "SELECT `no_laporan_produksi`, `tb_laporan_produksi`.`no_kartu_waleta`, `tanggal_lp`, `ruangan`, `tb_laporan_produksi`.`kode_grade`, `tb_grade_bahan_baku`.`jenis_bulu`, `tb_grade_bahan_baku`.`jenis_bentuk`, `jenis_bulu_lp`, `memo_lp`, `tb_bahan_baku_masuk`.`kadar_air_bahan_baku`, `berat_basah`, `berat_kering`, `jumlah_keping`, `keping_upah`, "
                    + "`kaki_besar_lp`, "
                    + "`kaki_kecil_lp`, "
                    + "`hilang_kaki_lp`, "
                    + "`ada_susur_lp`, "
                    + "`ada_susur_besar_lp`, "
                    + "`tanpa_susur_lp`, "
                    + "`utuh_lp`, "
                    + "`hilang_ujung_lp`, "
                    + "`pecah_1_lp`, "
                    + "`pecah_2`, "
                    + "`jumlah_sobek`, "
                    + "`sobek_lepas`, "
                    + "`jumlah_gumpil`, "
                    + "`grup_lp`, "
                    + "`cheat_rsb`,  "
                    + "`cheat_no_kartu`,  "
                    + "`cheat_tgl_lp` "
                    + "FROM `tb_laporan_produksi` "
                    + "LEFT JOIN `tb_bahan_baku_masuk` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`"
                    + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`\n"
                    + "WHERE  `cheat_no_kartu` LIKE '%" + txt_search_no_kartu.getText() + "%' "
                    + "AND `no_laporan_produksi` LIKE '%" + txt_search_lp.getText() + "%' "
                    + "AND `tb_laporan_produksi`.`kode_grade` LIKE '%" + txt_search_grade.getText() + "%'  "
                    + "AND `memo_lp` LIKE '%" + txt_search_memo.getText() + "%' "
                    + ruang + filter_tanggal_lp
                    + "ORDER BY `no_laporan_produksi` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[35];
            while (rs.next()) {
                row[0] = rs.getDate("tanggal_lp");
                row[1] = rs.getString("no_laporan_produksi");
                row[2] = rs.getString("kode_grade");
                row[3] = rs.getString("jenis_bulu");
                switch (rs.getString("jenis_bulu")) {
                    case "Bulu Ringan":
                        row[3] = "BR";
                        break;
                    case "Bulu Ringan Sekali/Bulu Ringan":
                        row[3] = "BRS/BR";
                        break;
                    case "Bulu Sedang":
                        row[3] = "BS";
                        break;
                    case "Bulu Berat":
                        row[3] = "BB";
                        break;
                    case "Bulu Berat Sekali":
                        row[3] = "BB2";
                        break;
                    default:
                        row[3] = "-";
                        break;
                }
                row[4] = rs.getString("jenis_bulu_lp");
                row[5] = rs.getInt("jumlah_keping");
                row[6] = rs.getInt("keping_upah");
                row[7] = rs.getInt("berat_basah");
                row[8] = rs.getString("no_kartu_waleta");
                row[9] = rs.getString("ruangan");
                row[10] = rs.getString("memo_lp");
                row[11] = rs.getFloat("kadar_air_bahan_baku");
                row[12] = rs.getInt("berat_kering");
                row[13] = rs.getInt("kaki_besar_lp");
                row[14] = rs.getInt("kaki_kecil_lp");
                row[15] = rs.getInt("hilang_kaki_lp");
                row[16] = rs.getInt("ada_susur_lp");
                row[17] = rs.getInt("ada_susur_besar_lp");
                row[18] = rs.getInt("tanpa_susur_lp");
                row[19] = rs.getInt("utuh_lp");
                row[20] = rs.getInt("hilang_ujung_lp");
                row[21] = rs.getInt("pecah_1_lp");
                row[22] = rs.getInt("pecah_2");
                row[23] = rs.getInt("jumlah_sobek");
                row[24] = rs.getInt("sobek_lepas");
                row[25] = rs.getInt("jumlah_gumpil");
                row[26] = rs.getInt("grup_lp");
                row[27] = rs.getString("cheat_rsb");
                row[28] = rs.getString("cheat_no_kartu");
                row[29] = rs.getDate("cheat_tgl_lp");
                model.addRow(row);
                total_gram = total_gram + rs.getInt("berat_basah");
                total_kpg = total_kpg + rs.getInt("jumlah_keping");
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_laporan_produksi);
            label_total_gram_LP.setText(decimalFormat.format(total_gram) + " Grams");
            label_total_keping_LP.setText(decimalFormat.format(total_kpg) + " Keping");
            int rowData = Table_laporan_produksi.getRowCount();
            label_total_data_laporan_produksi.setText(Integer.toString(rowData));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Laporan_Produksi1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void executeSQLQuery(String query, String message) {
        Utility.db.getConnection();
        try {
            Utility.db.getConnection().createStatement();
            if ((Utility.db.getStatement().executeUpdate(query)) == 1) {
                JOptionPane.showMessageDialog(this, "data " + message + " Successfully");
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "data not " + message);
            }
        } catch (SQLException | HeadlessException e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Laporan_Produksi1.class.getName()).log(Level.SEVERE, null, e);
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

        jPanel_laporan_produksi = new javax.swing.JPanel();
        jPanel_search_laporan_produksi = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        txt_search_lp = new javax.swing.JTextField();
        button_search_lp = new javax.swing.JButton();
        Date_Search_LP_1 = new com.toedter.calendar.JDateChooser();
        Date_Search_LP_2 = new com.toedter.calendar.JDateChooser();
        jLabel16 = new javax.swing.JLabel();
        ComboBox_Ruang_LP = new javax.swing.JComboBox<>();
        jLabel20 = new javax.swing.JLabel();
        txt_search_memo = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        txt_search_no_kartu = new javax.swing.JTextField();
        txt_search_grade = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        label_total_data_laporan_produksi = new javax.swing.JLabel();
        label_total_gram_LP = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        label_total_keping_LP = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        button_print_label = new javax.swing.JButton();
        button_export = new javax.swing.JButton();
        button_rekap = new javax.swing.JButton();
        button_laporan_ozon = new javax.swing.JButton();
        button_report_lp_wlt = new javax.swing.JButton();
        button_report_lp_sub = new javax.swing.JButton();
        button_report_lp_sub1 = new javax.swing.JButton();
        button_report_lp_wlt1 = new javax.swing.JButton();
        button_report_lp_wlt_jdn = new javax.swing.JButton();
        button_print_surat_jalan_lp = new javax.swing.JButton();
        button_total_cucian_hari_ini = new javax.swing.JButton();
        button_cheat_rsb2 = new javax.swing.JButton();
        button_cheat_no_kartu2 = new javax.swing.JButton();
        button_cheat_tgl_lp = new javax.swing.JButton();
        button_print_lp_cheat = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        Table_laporan_produksi = new javax.swing.JTable();
        button_lembar_pengeringan_baku = new javax.swing.JButton();

        jPanel_laporan_produksi.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_laporan_produksi.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Laporan Produksi", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N
        jPanel_laporan_produksi.setPreferredSize(new java.awt.Dimension(1366, 700));

        jPanel_search_laporan_produksi.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_search_laporan_produksi.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        jPanel_search_laporan_produksi.setPreferredSize(new java.awt.Dimension(1334, 61));

        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("No LP :");

        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Grade :");

        jLabel34.setBackground(new java.awt.Color(255, 255, 255));
        jLabel34.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel34.setText("Sampai");

        jLabel35.setBackground(new java.awt.Color(255, 255, 255));
        jLabel35.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel35.setText("Tanggal Cheat :");

        txt_search_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_lp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_lpKeyPressed(evt);
            }
        });

        button_search_lp.setBackground(new java.awt.Color(255, 255, 255));
        button_search_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_lp.setText("Search");
        button_search_lp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_lpActionPerformed(evt);
            }
        });

        Date_Search_LP_1.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search_LP_1.setToolTipText("");
        Date_Search_LP_1.setDate(new Date(new Date().getTime()-(7 * 24 * 60 * 60 * 1000)));
        Date_Search_LP_1.setDateFormatString("dd MMMM yyyy");
        Date_Search_LP_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_Search_LP_1.setMinSelectableDate(new java.util.Date(1420048915000L));

        Date_Search_LP_2.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search_LP_2.setDate(new Date());
        Date_Search_LP_2.setDateFormatString("dd MMMM yyyy");
        Date_Search_LP_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel16.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel16.setText("Ruangan :");

        ComboBox_Ruang_LP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_Ruang_LP.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        jLabel20.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel20.setText("Memo LP :");

        txt_search_memo.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_memo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_memoKeyPressed(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel17.setText("No Kartu Cheat :");

        txt_search_no_kartu.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_no_kartu.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_no_kartuKeyPressed(evt);
            }
        });

        txt_search_grade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_grade.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_gradeKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_search_laporan_produksiLayout = new javax.swing.GroupLayout(jPanel_search_laporan_produksi);
        jPanel_search_laporan_produksi.setLayout(jPanel_search_laporan_produksiLayout);
        jPanel_search_laporan_produksiLayout.setHorizontalGroup(
            jPanel_search_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_laporan_produksiLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_search_no_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_search_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_search_memo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_search_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ComboBox_Ruang_LP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 157, Short.MAX_VALUE)
                .addComponent(jLabel35)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date_Search_LP_1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel34)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date_Search_LP_2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_search_lp)
                .addContainerGap())
        );
        jPanel_search_laporan_produksiLayout.setVerticalGroup(
            jPanel_search_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_laporan_produksiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_search_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Search_LP_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Search_LP_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_no_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_lp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_memo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_Ruang_LP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_grade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel15.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel15.setText("Total Data :");

        label_total_data_laporan_produksi.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        label_total_data_laporan_produksi.setText("TOTAL");

        label_total_gram_LP.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        label_total_gram_LP.setText("TOTAL");

        jLabel26.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel26.setText("Total Berat :");

        label_total_keping_LP.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        label_total_keping_LP.setText("TOTAL");

        jLabel27.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel27.setText("Total Keping :");

        button_print_label.setBackground(new java.awt.Color(255, 255, 255));
        button_print_label.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_print_label.setText("Print Label LP");
        button_print_label.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_print_labelActionPerformed(evt);
            }
        });

        button_export.setBackground(new java.awt.Color(255, 255, 255));
        button_export.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export.setText("Export Excel");
        button_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportActionPerformed(evt);
            }
        });

        button_rekap.setBackground(new java.awt.Color(255, 255, 255));
        button_rekap.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_rekap.setText("Rekap");
        button_rekap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_rekapActionPerformed(evt);
            }
        });

        button_laporan_ozon.setBackground(new java.awt.Color(255, 255, 255));
        button_laporan_ozon.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_laporan_ozon.setText("Laporan Ozon");
        button_laporan_ozon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_laporan_ozonActionPerformed(evt);
            }
        });

        button_report_lp_wlt.setBackground(new java.awt.Color(255, 255, 255));
        button_report_lp_wlt.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_report_lp_wlt.setText("Laporan P. Baku WLT");
        button_report_lp_wlt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_report_lp_wltActionPerformed(evt);
            }
        });

        button_report_lp_sub.setBackground(new java.awt.Color(255, 255, 255));
        button_report_lp_sub.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_report_lp_sub.setText("Laporan P. Baku SUB");
        button_report_lp_sub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_report_lp_subActionPerformed(evt);
            }
        });

        button_report_lp_sub1.setBackground(new java.awt.Color(255, 255, 255));
        button_report_lp_sub1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_report_lp_sub1.setText("Laporan P. Baku SUB+");
        button_report_lp_sub1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_report_lp_sub1ActionPerformed(evt);
            }
        });

        button_report_lp_wlt1.setBackground(new java.awt.Color(255, 255, 255));
        button_report_lp_wlt1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_report_lp_wlt1.setText("Laporan P. Baku WLT+");
        button_report_lp_wlt1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_report_lp_wlt1ActionPerformed(evt);
            }
        });

        button_report_lp_wlt_jdn.setBackground(new java.awt.Color(255, 255, 255));
        button_report_lp_wlt_jdn.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_report_lp_wlt_jdn.setText("Laporan P. Baku JDN");
        button_report_lp_wlt_jdn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_report_lp_wlt_jdnActionPerformed(evt);
            }
        });

        button_print_surat_jalan_lp.setBackground(new java.awt.Color(255, 255, 255));
        button_print_surat_jalan_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_print_surat_jalan_lp.setText("Print Surat Jalan LP");
        button_print_surat_jalan_lp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_print_surat_jalan_lpActionPerformed(evt);
            }
        });

        button_total_cucian_hari_ini.setBackground(new java.awt.Color(255, 255, 255));
        button_total_cucian_hari_ini.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_total_cucian_hari_ini.setText("Total Cucian Hari ini");
        button_total_cucian_hari_ini.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_total_cucian_hari_iniActionPerformed(evt);
            }
        });

        button_cheat_rsb2.setBackground(new java.awt.Color(255, 255, 255));
        button_cheat_rsb2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_cheat_rsb2.setText("Cheat RSB 2");
        button_cheat_rsb2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_cheat_rsb2ActionPerformed(evt);
            }
        });

        button_cheat_no_kartu2.setBackground(new java.awt.Color(255, 255, 255));
        button_cheat_no_kartu2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_cheat_no_kartu2.setText("Cheat No Kartu 2");
        button_cheat_no_kartu2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_cheat_no_kartu2ActionPerformed(evt);
            }
        });

        button_cheat_tgl_lp.setBackground(new java.awt.Color(255, 255, 255));
        button_cheat_tgl_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_cheat_tgl_lp.setText("Cheat Tgl LP");
        button_cheat_tgl_lp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_cheat_tgl_lpActionPerformed(evt);
            }
        });

        button_print_lp_cheat.setBackground(new java.awt.Color(255, 255, 255));
        button_print_lp_cheat.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_print_lp_cheat.setText("Print All LP Cheat");
        button_print_lp_cheat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_print_lp_cheatActionPerformed(evt);
            }
        });

        Table_laporan_produksi.setAutoCreateRowSorter(true);
        Table_laporan_produksi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_laporan_produksi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tanggal LP", "No LP", "Kode Grade", "Bulu Grading", "Bulu Upah", "Keping", "Kpg Upah", "Berat Angin2", "No Kartu", "Ruang", "Memo", "KA (%)", "Berat 0%", "Kaki Besar", "Kaki Kecil", "Hlg Kaki", "Susur Kcl", "Susur Bsr", "Tanpa susur", "Utuh", "Hilang Ujung", "Pecah 1", "Pecah 2", "Sobek", "Sobek lepas", "Gumpil", "GRUP LP", "CT RSB 2", "CT No Kartu 2", "CT Tgl LP"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
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

        button_lembar_pengeringan_baku.setBackground(new java.awt.Color(255, 255, 255));
        button_lembar_pengeringan_baku.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_lembar_pengeringan_baku.setText("Pengeringan Baku");
        button_lembar_pengeringan_baku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_lembar_pengeringan_bakuActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_laporan_produksiLayout = new javax.swing.GroupLayout(jPanel_laporan_produksi);
        jPanel_laporan_produksi.setLayout(jPanel_laporan_produksiLayout);
        jPanel_laporan_produksiLayout.setHorizontalGroup(
            jPanel_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_laporan_produksiLayout.createSequentialGroup()
                .addGroup(jPanel_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_laporan_produksiLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data_laporan_produksi)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel27)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_keping_LP)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_LP)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel_laporan_produksiLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel_search_laporan_produksi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel_laporan_produksiLayout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 1173, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(jPanel_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(button_print_label, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(button_report_lp_wlt, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(button_report_lp_sub, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(button_report_lp_wlt1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(button_report_lp_sub1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addComponent(button_report_lp_wlt_jdn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(button_laporan_ozon, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(button_rekap, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(button_print_surat_jalan_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(button_total_cucian_hari_ini, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(button_cheat_rsb2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(button_cheat_no_kartu2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(button_cheat_tgl_lp, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(button_print_lp_cheat, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(button_lembar_pengeringan_baku, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap())
        );
        jPanel_laporan_produksiLayout.setVerticalGroup(
            jPanel_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_laporan_produksiLayout.createSequentialGroup()
                .addComponent(jPanel_search_laporan_produksi, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_laporan_produksiLayout.createSequentialGroup()
                        .addGap(156, 156, 156)
                        .addComponent(button_print_label, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_report_lp_wlt, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_report_lp_sub, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_report_lp_wlt1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_report_lp_sub1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_report_lp_wlt_jdn, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_laporan_ozon, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_rekap, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_print_surat_jalan_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_total_cucian_hari_ini, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_cheat_rsb2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_cheat_no_kartu2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_cheat_tgl_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_print_lp_cheat, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_lembar_pengeringan_baku, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 26, Short.MAX_VALUE))
                    .addComponent(jScrollPane6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel26)
                    .addComponent(label_total_keping_LP)
                    .addComponent(jLabel27)
                    .addComponent(jLabel15)
                    .addComponent(label_total_data_laporan_produksi)
                    .addComponent(label_total_gram_LP))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jPanel_laporan_produksi, javax.swing.GroupLayout.DEFAULT_SIZE, 1364, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_laporan_produksi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void button_search_lpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_lpActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_button_search_lpActionPerformed

    private void txt_search_lpKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_lpKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_lpKeyPressed

    private void txt_search_memoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_memoKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_memoKeyPressed

    private void button_print_labelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_print_labelActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_laporan_produksi.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Please select data from the table first", "warning!", 1);
            } else {
                String query = "SELECT `no_laporan_produksi`, `tb_laporan_produksi`.`no_kartu_waleta`, `kode_grade`, `jumlah_keping`, `berat_basah`, `jenis_bulu_lp`, `tb_bahan_baku_masuk_cheat`.`no_registrasi`, `cheat_no_kartu`, `cheat_rsb`\n"
                        + "FROM `tb_laporan_produksi`\n"
                        + "LEFT JOIN `tb_bahan_baku_masuk_cheat` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta`"
                        + "WHERE `tb_laporan_produksi`.`no_laporan_produksi` = '" + Table_laporan_produksi.getValueAt(j, 1) + "'";
                JRDesignQuery newQuery = new JRDesignQuery();
                newQuery.setText(query);
                JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Label_LP.jrxml");
                JASP_DESIGN.setQuery(newQuery);

                JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, null, Utility.db.getConnection());
                JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
            }
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(this, ex.getLocalizedMessage());
            Logger.getLogger(JPanel_Laporan_Produksi1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_print_labelActionPerformed

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_laporan_produksi.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_exportActionPerformed

    private void button_rekapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_rekapActionPerformed
        // TODO add your handling code here:
        String txt_lp = txt_search_lp.getText();
        Date Date_LP_1 = Date_Search_LP_1.getDate();
        Date Date_LP_2 = Date_Search_LP_2.getDate();
        String grade = txt_search_grade.getText();
        String memo = txt_search_memo.getText();
        String ruang = ComboBox_Ruang_LP.getSelectedItem().toString();

        DialogRekapPerBentukDanGrade dialog = new DialogRekapPerBentukDanGrade(new javax.swing.JFrame(), true, txt_lp, Date_LP_1, Date_LP_2, grade, memo, ruang);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
    }//GEN-LAST:event_button_rekapActionPerformed

    private void txt_search_no_kartuKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_no_kartuKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_no_kartuKeyPressed

    private void button_report_lp_wltActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_report_lp_wltActionPerformed
        try {
            String ruang = "AND `tb_laporan_produksi`.`ruangan` IN ('A', 'B', 'C', 'D', 'E') ";
            if (Date_Search_LP_1.getDate() != null && Date_Search_LP_2.getDate() != null) {
                sql = "SELECT `no_laporan_produksi`, `keping_upah`, `tb_laporan_produksi`.`no_kartu_waleta`, `tb_bahan_baku_masuk_cheat`.`no_registrasi`, `tb_laporan_produksi`.`tanggal_lp`, `tb_laporan_produksi`.`ruangan`, `tb_laporan_produksi`.`kode_grade`, `tb_grade_bahan_baku`.`jenis_bulu`, `tb_grade_bahan_baku`.`jenis_bentuk`, `jenis_bulu_lp`, `memo_lp`, `berat_basah`, `berat_kering`, `jumlah_keping`, `tarif_gram`, `kpg_lp`, `tb_laporan_produksi`.`grup_lp`, CONCAT(`tb_laporan_produksi`.`tanggal_lp`, `tb_laporan_produksi`.`ruangan`, `tb_laporan_produksi`.`grup_lp`) AS 'kode', y.`bobot`\n"
                        + ", `cheat_no_kartu`, `cheat_rsb`, `cheat_tgl_lp` "
                        + "FROM `tb_laporan_produksi` \n"
                        + "LEFT JOIN `tb_bahan_baku_masuk_cheat` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta`\n"
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`\n"
                        + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah`\n"
                        + "LEFT JOIN "
                        + "(SELECT `tanggal_lp`, `ruangan`, `grup_lp`, CONCAT(`tanggal_lp`, `ruangan`, `grup_lp`) AS 'kode', ROUND(SUM(`keping_upah`/`kpg_lp`), 3) AS 'bobot' "
                        + "FROM `tb_laporan_produksi` "
                        + "LEFT JOIN `tb_bahan_baku_masuk` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta` "
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade` "
                        + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah` "
                        + "WHERE `tb_laporan_produksi`.`no_kartu_waleta` LIKE '%" + txt_search_no_kartu.getText() + "%' "
                        + "AND `no_laporan_produksi` LIKE '%" + txt_search_lp.getText() + "%' "
                        + "AND `cheat_tgl_lp` BETWEEN '" + dateFormat.format(Date_Search_LP_1.getDate()) + "' AND '" + dateFormat.format(Date_Search_LP_2.getDate()) + "' "
                        + "AND `tb_laporan_produksi`.`kode_grade` LIKE '%" + txt_search_grade.getText() + "%' AND `memo_lp` LIKE '%" + txt_search_memo.getText() + "%' "
                        + ruang
                        + "GROUP BY `tanggal_lp`, `ruangan`, `grup_lp`) "
                        + "AS y ON `tb_laporan_produksi`.`tanggal_lp` = y.`tanggal_lp` AND `tb_laporan_produksi`.`ruangan` = y.`ruangan` AND `tb_laporan_produksi`.`grup_lp` = y.`grup_lp`\n"
                        + "WHERE `tb_laporan_produksi`.`no_kartu_waleta` LIKE '%" + txt_search_no_kartu.getText() + "%' "
                        + "AND `tb_laporan_produksi`.`grup_lp` <= 15 "
                        + "AND `no_laporan_produksi` LIKE '%" + txt_search_lp.getText() + "%' "
                        + "AND `tb_laporan_produksi`.`cheat_tgl_lp` BETWEEN '" + dateFormat.format(Date_Search_LP_1.getDate()) + "' AND '" + dateFormat.format(Date_Search_LP_2.getDate()) + "' "
                        + "AND `tb_laporan_produksi`.`kode_grade` LIKE '%" + txt_search_grade.getText() + "%' AND `memo_lp` LIKE '%" + txt_search_memo.getText() + "%' "
                        + ruang
                        + "ORDER BY `tb_laporan_produksi`.`ruangan`, `grup_lp`";
            } else {
                JOptionPane.showMessageDialog(this, "Silahkan pilih range tanggal data LP");
            }
//            System.out.println(sql);
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(sql);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Pengeluaran_Bahan_Baku_Harian_wlt.jrxml");
            Map<String, Object> map = new HashMap<>();
            map.put("CHEAT", 1);
            JASP_DESIGN.setQuery(newQuery);
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)

        } catch (JRException ex) {
            Logger.getLogger(DetailGradingBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_report_lp_wltActionPerformed

    private void button_laporan_ozonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_laporan_ozonActionPerformed
        // TODO add your handling code here:
        refreshTable();
        try {
            String ruang = "AND `ruangan` = '" + ComboBox_Ruang_LP.getSelectedItem().toString() + "' ";
            if ("All".equals(ComboBox_Ruang_LP.getSelectedItem().toString())) {
                ruang = "";
            }
            String qry = "SELECT `no_laporan_produksi`, `tb_laporan_produksi`.`no_kartu_waleta`, `no_registrasi`, `cheat_no_kartu`, `cheat_rsb`  "
                    + "FROM `tb_laporan_produksi` "
                    + "LEFT JOIN `tb_bahan_baku_masuk_cheat` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta` "
                    + "WHERE `tb_laporan_produksi`.`no_kartu_waleta` LIKE '%" + txt_search_no_kartu.getText() + "%' "
                    + "AND `tb_laporan_produksi`.`no_laporan_produksi` LIKE '%" + txt_search_lp.getText() + "%' "
                    + "AND `tb_laporan_produksi`.`kode_grade` LIKE '%" + txt_search_grade.getText() + "%' "
                    + "AND `memo_lp` LIKE '%" + txt_search_memo.getText() + "%' "
                    + ruang
                    + "AND `tanggal_lp` BETWEEN '" + dateFormat.format(Date_Search_LP_1.getDate()) + "' AND '" + dateFormat.format(Date_Search_LP_2.getDate()) + "' "
                    + "ORDER BY `no_laporan_produksi` DESC";
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(qry);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Catatan_Disinfeksi_Bahan_Mentah_Dengan_Ozon.jrxml");
            JASP_DESIGN.setQuery(newQuery);
            Map<String, Object> map = new HashMap<>();
            if (!(Date_Search_LP_1.getDate() == null || Date_Search_LP_2.getDate() == null)) {
                String tanggal = new SimpleDateFormat("dd MMM yyy").format(Date_Search_LP_1.getDate()) + " - " + new SimpleDateFormat("dd MMM yyy").format(Date_Search_LP_2.getDate());
                map.put("tanggal", tanggal);
            }

            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
        } catch (JRException ex) {
            Logger.getLogger(JPanel_Laporan_Produksi1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_laporan_ozonActionPerformed

    private void txt_search_gradeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_gradeKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_gradeKeyPressed

    private void button_report_lp_subActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_report_lp_subActionPerformed
        // TODO add your handling code here:
        try {
            String ruang = "AND `tb_laporan_produksi`.`ruangan` NOT IN ('A', 'B', 'C', 'D', 'E') ";
            if (Date_Search_LP_1.getDate() != null && Date_Search_LP_2.getDate() != null) {
                sql = "SELECT `no_laporan_produksi`, `tb_laporan_produksi`.`no_kartu_waleta`, `tb_bahan_baku_masuk_cheat`.`no_registrasi`, `tb_laporan_produksi`.`tanggal_lp`, `tb_laporan_produksi`.`ruangan`, `tb_laporan_produksi`.`kode_grade`, `tb_grade_bahan_baku`.`jenis_bulu`, `tb_grade_bahan_baku`.`jenis_bentuk`, `jenis_bulu_lp`, `memo_lp`, `berat_basah`, `berat_kering`, `jumlah_keping`, `keping_upah`, `tarif_sub`, `tarif_gram`, `kpg_lp`, `tb_laporan_produksi`.`grup_lp`, CONCAT(`tb_laporan_produksi`.`tanggal_lp`, `tb_laporan_produksi`.`ruangan`, `tb_laporan_produksi`.`grup_lp`) AS 'kode', y.`bobot`\n"
                        + ", `cheat_no_kartu`, `cheat_rsb`, `cheat_tgl_lp`"
                        + "FROM `tb_laporan_produksi` \n"
                        + "LEFT JOIN `tb_bahan_baku_masuk_cheat` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta`\n"
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`\n"
                        + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah`\n"
                        + "LEFT JOIN "
                        + "(SELECT `tanggal_lp`, `ruangan`, `grup_lp`, CONCAT(`tanggal_lp`, `ruangan`, `grup_lp`) AS 'kode', ROUND(SUM(IF(`jumlah_keping`>0, `jumlah_keping`, `berat_basah`/8)/`kpg_lp`), 3) AS 'bobot' "
                        + "FROM `tb_laporan_produksi` "
                        + "LEFT JOIN `tb_bahan_baku_masuk` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta` "
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade` "
                        + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah` "
                        + "WHERE `tb_laporan_produksi`.`no_kartu_waleta` LIKE '%" + txt_search_no_kartu.getText() + "%' "
                        + "AND `no_laporan_produksi` LIKE '%" + txt_search_lp.getText() + "%' "
                        + "AND `cheat_tgl_lp` BETWEEN '" + dateFormat.format(Date_Search_LP_1.getDate()) + "' AND '" + dateFormat.format(Date_Search_LP_2.getDate()) + "' "
                        + "AND `tb_laporan_produksi`.`kode_grade` LIKE '%" + txt_search_grade.getText() + "%' AND `memo_lp` LIKE '%" + txt_search_memo.getText() + "%' "
                        + ruang
                        + "GROUP BY `tanggal_lp`, `ruangan`, `grup_lp`) "
                        + "AS y ON `tb_laporan_produksi`.`tanggal_lp` = y.`tanggal_lp` AND `tb_laporan_produksi`.`ruangan` = y.`ruangan` AND `tb_laporan_produksi`.`grup_lp` = y.`grup_lp`\n"
                        + "WHERE `tb_laporan_produksi`.`no_kartu_waleta` LIKE '%" + txt_search_no_kartu.getText() + "%' "
                        + "AND `tb_laporan_produksi`.`grup_lp` <= 15 "
                        + "AND `no_laporan_produksi` LIKE '%" + txt_search_lp.getText() + "%' "
                        + "AND `tb_laporan_produksi`.`cheat_tgl_lp` BETWEEN '" + dateFormat.format(Date_Search_LP_1.getDate()) + "' AND '" + dateFormat.format(Date_Search_LP_2.getDate()) + "' "
                        + "AND `tb_laporan_produksi`.`kode_grade` LIKE '%" + txt_search_grade.getText() + "%' AND `memo_lp` LIKE '%" + txt_search_memo.getText() + "%' "
                        + ruang
                        + "ORDER BY `tb_laporan_produksi`.`ruangan`, `grup_lp`";
            } else {
                JOptionPane.showMessageDialog(this, "Silahkan pilih range tanggal data LP");
            }
//            System.out.println(sql);
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(sql);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Pengeluaran_Bahan_Baku_Harian_Sub.jrxml");
            JASP_DESIGN.setQuery(newQuery);
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("IF_JDN_WLT", 0);
            params.put("CHEAT", 1);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, params, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)

        } catch (JRException ex) {
            Logger.getLogger(DetailGradingBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_report_lp_subActionPerformed

    private void button_report_lp_sub1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_report_lp_sub1ActionPerformed
        // TODO add your handling code here:
        try {
            String ruang = "AND `tb_laporan_produksi`.`ruangan` NOT IN ('A', 'B', 'C', 'D', 'E') ";
            if (Date_Search_LP_1.getDate() != null && Date_Search_LP_2.getDate() != null) {
                sql = "SELECT `no_laporan_produksi`, `tb_laporan_produksi`.`no_kartu_waleta`, `tb_bahan_baku_masuk_cheat`.`no_registrasi`, `tb_laporan_produksi`.`tanggal_lp`, `tb_laporan_produksi`.`ruangan`, `tb_laporan_produksi`.`kode_grade`, `tb_grade_bahan_baku`.`jenis_bulu`, `tb_grade_bahan_baku`.`jenis_bentuk`, `jenis_bulu_lp`, `memo_lp`, `berat_basah`, `berat_kering`, `jumlah_keping`, `keping_upah`, `tarif_gram`, `tarif_sub`, `kpg_lp`, `tb_laporan_produksi`.`grup_lp`, CONCAT(`tb_laporan_produksi`.`tanggal_lp`, `tb_laporan_produksi`.`ruangan`, `tb_laporan_produksi`.`grup_lp`) AS 'kode', y.`bobot`\n"
                        + ", `cheat_no_kartu`, `cheat_rsb`, `cheat_tgl_lp` "
                        + "FROM `tb_laporan_produksi` \n"
                        + "LEFT JOIN `tb_bahan_baku_masuk_cheat` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta`\n"
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`\n"
                        + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah`\n"
                        + "LEFT JOIN "
                        + "(SELECT `tanggal_lp`, `ruangan`, `grup_lp`, CONCAT(`tanggal_lp`, `ruangan`, `grup_lp`) AS 'kode', ROUND(SUM(IF(`jumlah_keping`>0, `jumlah_keping`, `berat_basah`/8)/`kpg_lp`), 3) AS 'bobot' "
                        + "FROM `tb_laporan_produksi` "
                        + "LEFT JOIN `tb_bahan_baku_masuk` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta` "
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade` "
                        + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah` "
                        + "WHERE `tb_laporan_produksi`.`no_kartu_waleta` LIKE '%" + txt_search_no_kartu.getText() + "%' "
                        + "AND `no_laporan_produksi` LIKE '%" + txt_search_lp.getText() + "%' "
                        + "AND `cheat_tgl_lp` BETWEEN '" + dateFormat.format(Date_Search_LP_1.getDate()) + "' AND '" + dateFormat.format(Date_Search_LP_2.getDate()) + "' "
                        + "AND `tb_laporan_produksi`.`kode_grade` LIKE '%" + txt_search_grade.getText() + "%' AND `memo_lp` LIKE '%" + txt_search_memo.getText() + "%' "
                        + ruang
                        + "GROUP BY `tanggal_lp`, `ruangan`, `grup_lp`) "
                        + "AS y ON `tb_laporan_produksi`.`tanggal_lp` = y.`tanggal_lp` AND `tb_laporan_produksi`.`ruangan` = y.`ruangan` AND `tb_laporan_produksi`.`grup_lp` = y.`grup_lp`\n"
                        + "WHERE `tb_laporan_produksi`.`no_kartu_waleta` LIKE '%" + txt_search_no_kartu.getText() + "%' "
                        + "AND `tb_laporan_produksi`.`grup_lp` > 30 "
                        + "AND `no_laporan_produksi` LIKE '%" + txt_search_lp.getText() + "%' "
                        + "AND `tb_laporan_produksi`.`cheat_tgl_lp` BETWEEN '" + dateFormat.format(Date_Search_LP_1.getDate()) + "' AND '" + dateFormat.format(Date_Search_LP_2.getDate()) + "' "
                        + "AND `tb_laporan_produksi`.`kode_grade` LIKE '%" + txt_search_grade.getText() + "%' AND `memo_lp` LIKE '%" + txt_search_memo.getText() + "%' "
                        + ruang
                        + "ORDER BY `tb_laporan_produksi`.`ruangan`, `grup_lp`";
            } else {
                JOptionPane.showMessageDialog(this, "Silahkan pilih range tanggal data LP");
            }
//            System.out.println(sql);
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(sql);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Pengeluaran_Bahan_Baku_Harian_Sub.jrxml");
            JASP_DESIGN.setQuery(newQuery);
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("IF_JDN_WLT", 0);
            params.put("CHEAT", 1);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, params, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)

        } catch (JRException ex) {
            Logger.getLogger(DetailGradingBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_report_lp_sub1ActionPerformed

    private void button_report_lp_wlt1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_report_lp_wlt1ActionPerformed
        // TODO add your handling code here:
        try {
            String ruang = "AND `tb_laporan_produksi`.`ruangan` IN ('A', 'B', 'C', 'D', 'E') ";
            if (Date_Search_LP_1.getDate() != null && Date_Search_LP_2.getDate() != null) {
                sql = "SELECT `no_laporan_produksi`, `keping_upah`, `tb_laporan_produksi`.`no_kartu_waleta`, `tb_bahan_baku_masuk_cheat`.`no_registrasi`, `tb_laporan_produksi`.`tanggal_lp`, `tb_laporan_produksi`.`ruangan`, `tb_laporan_produksi`.`kode_grade`, `tb_grade_bahan_baku`.`jenis_bulu`, `tb_grade_bahan_baku`.`jenis_bentuk`, `jenis_bulu_lp`, `memo_lp`, `berat_basah`, `berat_kering`, `jumlah_keping`, `tarif_gram`, `kpg_lp`, `tb_laporan_produksi`.`grup_lp`, CONCAT(`tb_laporan_produksi`.`tanggal_lp`, `tb_laporan_produksi`.`ruangan`, `tb_laporan_produksi`.`grup_lp`) AS 'kode', y.`bobot`\n"
                        + ", `cheat_no_kartu`, `cheat_rsb`, `cheat_tgl_lp` "
                        + "FROM `tb_laporan_produksi` \n"
                        + "LEFT JOIN `tb_bahan_baku_masuk_cheat` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta`\n"
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`\n"
                        + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah`\n"
                        + "LEFT JOIN "
                        + "(SELECT `tanggal_lp`, `ruangan`, `grup_lp`, CONCAT(`tanggal_lp`, `ruangan`, `grup_lp`) AS 'kode', ROUND(SUM(`keping_upah`/`kpg_lp`), 3) AS 'bobot' "
                        + "FROM `tb_laporan_produksi` "
                        + "LEFT JOIN `tb_bahan_baku_masuk` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta` "
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade` "
                        + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah` "
                        + "WHERE `tb_laporan_produksi`.`no_kartu_waleta` LIKE '%" + txt_search_no_kartu.getText() + "%' "
                        + "AND `no_laporan_produksi` LIKE '%" + txt_search_lp.getText() + "%' "
                        + "AND `cheat_tgl_lp` BETWEEN '" + dateFormat.format(Date_Search_LP_1.getDate()) + "' AND '" + dateFormat.format(Date_Search_LP_2.getDate()) + "' "
                        + "AND `tb_laporan_produksi`.`kode_grade` LIKE '%" + txt_search_grade.getText() + "%' AND `memo_lp` LIKE '%" + txt_search_memo.getText() + "%' "
                        + ruang
                        + "GROUP BY `tanggal_lp`, `ruangan`, `grup_lp`) "
                        + "AS y ON `tb_laporan_produksi`.`tanggal_lp` = y.`tanggal_lp` AND `tb_laporan_produksi`.`ruangan` = y.`ruangan` AND `tb_laporan_produksi`.`grup_lp` = y.`grup_lp`\n"
                        + "WHERE `tb_laporan_produksi`.`no_kartu_waleta` LIKE '%" + txt_search_no_kartu.getText() + "%' "
                        + "AND `tb_laporan_produksi`.`grup_lp` > 30 "
                        + "AND `no_laporan_produksi` LIKE '%" + txt_search_lp.getText() + "%' "
                        + "AND `tb_laporan_produksi`.`cheat_tgl_lp` BETWEEN '" + dateFormat.format(Date_Search_LP_1.getDate()) + "' AND '" + dateFormat.format(Date_Search_LP_2.getDate()) + "' "
                        + "AND `tb_laporan_produksi`.`kode_grade` LIKE '%" + txt_search_grade.getText() + "%' AND `memo_lp` LIKE '%" + txt_search_memo.getText() + "%' "
                        + ruang
                        + "ORDER BY `tb_laporan_produksi`.`ruangan`, `grup_lp`";
            } else {
                JOptionPane.showMessageDialog(this, "Silahkan pilih range tanggal data LP");
            }
//            System.out.println(sql);
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(sql);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Pengeluaran_Bahan_Baku_Harian_wlt2.jrxml");
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("CHEAT", 1);
            JASP_DESIGN.setQuery(newQuery);
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, params, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)

        } catch (JRException ex) {
            Logger.getLogger(DetailGradingBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_report_lp_wlt1ActionPerformed

    private void button_report_lp_wlt_jdnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_report_lp_wlt_jdnActionPerformed
        // TODO add your handling code here:
        try {
            String ruang = "AND `tb_laporan_produksi`.`ruangan` IN ('A', 'B', 'C', 'D', 'E') ";
            if (Date_Search_LP_1.getDate() != null && Date_Search_LP_2.getDate() != null) {
                sql = "SELECT `no_laporan_produksi`, `keping_upah`, `tb_laporan_produksi`.`no_kartu_waleta`, `tb_bahan_baku_masuk_cheat`.`no_registrasi`, `tb_laporan_produksi`.`tanggal_lp`, `tb_laporan_produksi`.`ruangan`, `tb_laporan_produksi`.`kode_grade`, `tb_grade_bahan_baku`.`jenis_bulu`, `tb_grade_bahan_baku`.`jenis_bentuk`, `jenis_bulu_lp`, `memo_lp`, `berat_basah`, `berat_kering`, `jumlah_keping`, `tarif_sub`, `tarif_gram`, `kpg_lp`, `tb_laporan_produksi`.`grup_lp`, CONCAT(`tb_laporan_produksi`.`tanggal_lp`, `tb_laporan_produksi`.`ruangan`, `tb_laporan_produksi`.`grup_lp`) AS 'kode', y.`bobot`\n"
                        + ", `cheat_no_kartu`, `cheat_rsb`, `cheat_tgl_lp` "
                        + "FROM `tb_laporan_produksi` \n"
                        + "LEFT JOIN `tb_bahan_baku_masuk_cheat` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta`\n"
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`\n"
                        + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah`\n"
                        + "LEFT JOIN "
                        + "(SELECT `tanggal_lp`, `ruangan`, `grup_lp`, CONCAT(`tanggal_lp`, `ruangan`, `grup_lp`) AS 'kode', ROUND(SUM(`keping_upah`/`kpg_lp`), 3) AS 'bobot' "
                        + "FROM `tb_laporan_produksi` "
                        + "LEFT JOIN `tb_bahan_baku_masuk` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta` "
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade` "
                        + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah` "
                        + "WHERE `tb_laporan_produksi`.`no_kartu_waleta` LIKE '%" + txt_search_no_kartu.getText() + "%' "
                        + "AND `no_laporan_produksi` LIKE '%" + txt_search_lp.getText() + "%' "
                        + "AND `cheat_tgl_lp` BETWEEN '" + dateFormat.format(Date_Search_LP_1.getDate()) + "' AND '" + dateFormat.format(Date_Search_LP_2.getDate()) + "' "
                        + "AND `tb_laporan_produksi`.`kode_grade` LIKE '%" + txt_search_grade.getText() + "%' AND `memo_lp` LIKE '%" + txt_search_memo.getText() + "%' "
                        + ruang
                        + "GROUP BY `tanggal_lp`, `ruangan`, `grup_lp`) "
                        + "AS y ON `tb_laporan_produksi`.`tanggal_lp` = y.`tanggal_lp` AND `tb_laporan_produksi`.`ruangan` = y.`ruangan` AND `tb_laporan_produksi`.`grup_lp` = y.`grup_lp`\n"
                        + "WHERE `tb_laporan_produksi`.`no_kartu_waleta` LIKE '%" + txt_search_no_kartu.getText() + "%' "
                        + "AND `tb_laporan_produksi`.`grup_lp` <= 15 "
                        + "AND `no_laporan_produksi` LIKE '%" + txt_search_lp.getText() + "%' "
                        + "AND `tb_laporan_produksi`.`cheat_tgl_lp` BETWEEN '" + dateFormat.format(Date_Search_LP_1.getDate()) + "' AND '" + dateFormat.format(Date_Search_LP_2.getDate()) + "' "
                        + "AND `tb_laporan_produksi`.`kode_grade` LIKE '%" + txt_search_grade.getText() + "%' AND `memo_lp` LIKE '%" + txt_search_memo.getText() + "%' "
                        + ruang
                        + "ORDER BY `tb_laporan_produksi`.`ruangan`, `grup_lp`";
            } else {
                JOptionPane.showMessageDialog(this, "Silahkan pilih range tanggal data LP");
            }
//            System.out.println(sql);
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(sql);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Pengeluaran_Bahan_Baku_Harian_Sub.jrxml");
            JASP_DESIGN.setQuery(newQuery);
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("IF_JDN_WLT", 1);
            params.put("CHEAT", 1);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, params, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)

        } catch (JRException ex) {
            Logger.getLogger(DetailGradingBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_report_lp_wlt_jdnActionPerformed

    private void button_print_surat_jalan_lpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_print_surat_jalan_lpActionPerformed
        // TODO add your handling code here:
        if (Date_Search_LP_1.getDate() != null && Date_Search_LP_2.getDate() != null) {
            try {
//                sql = "SELECT DISTINCT(`ruangan`) AS 'ruangan' "
//                        + "FROM `tb_laporan_produksi` "
//                        + "WHERE  `no_kartu_waleta` LIKE '%" + txt_search_no_kartu.getText() + "%' "
//                        + "AND `no_laporan_produksi` LIKE '%" + txt_search_lp.getText() + "%' "
//                        + "AND `kode_grade` LIKE '%" + txt_search_grade.getText() + "%'  "
//                        + "AND `memo_lp` LIKE '%" + txt_search_memo.getText() + "%' "
//                        + "AND `tanggal_lp` BETWEEN '" + dateFormat.format(Date_Search_LP_1.getDate()) + "' AND '" + dateFormat.format(Date_Search_LP_2.getDate()) + "' "
//                        + "ORDER BY `ruangan`";
                JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Surat_Jalan_LP_Sub.jrxml");
                JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("SUBREPORT_DIR", "Report\\");
                params.put("NO_LP", "%" + txt_search_lp.getText() + "%");
                params.put("NO_KARTU", "%" + txt_search_no_kartu.getText() + "%");
                params.put("GRADE", "%" + txt_search_grade.getText() + "%");
                params.put("MEMO", "%" + txt_search_memo.getText() + "%");
                params.put("TGL1", dateFormat.format(Date_Search_LP_1.getDate()));
                params.put("TGL2", dateFormat.format(Date_Search_LP_2.getDate()));
                JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, params, Utility.db.getConnection());
                JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
            } catch (JRException ex) {
                Logger.getLogger(JPanel_Laporan_Produksi1.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Filter Tanggal LP tidak boleh kosong");
        }
    }//GEN-LAST:event_button_print_surat_jalan_lpActionPerformed

    private void button_total_cucian_hari_iniActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_total_cucian_hari_iniActionPerformed
        // TODO add your handling code here:
        JDialog_total_cucian_hari_ini dialog = new JDialog_total_cucian_hari_ini(new javax.swing.JFrame(), true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        dialog.setResizable(false);
    }//GEN-LAST:event_button_total_cucian_hari_iniActionPerformed

    private void button_cheat_rsb2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_cheat_rsb2ActionPerformed
        // TODO add your handling code here:
        String input = JOptionPane.showInputDialog("Masukkan No Registrasi Rumah Burung (3-4 digit) :");
        if (input == null) {

        } else if (input.equals("")) {
            int dialogResult = JOptionPane.showConfirmDialog(this, "Hapus data cheat rsb??", "Warning", 0);
            if (dialogResult == JOptionPane.YES_OPTION) {
                for (int i = 0; i < Table_laporan_produksi.getRowCount(); i++) {
                    try {
                        sql = "UPDATE `tb_laporan_produksi` SET `cheat_rsb` = NULL "
                                + "WHERE `no_laporan_produksi` = '" + Table_laporan_produksi.getValueAt(i, 1).toString() + "'";
                        Utility.db.getStatement().executeUpdate(sql);
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(this, e);
                        Logger.getLogger(JPanel_Laporan_Produksi1.class.getName()).log(Level.SEVERE, null, e);
                    }
                }
            }
        } else {
            try {
                String query = "SELECT `kode_rb`, `no_registrasi`, `nama_rumah_burung`, `kapasitas_per_tahun` "
                        + "FROM `tb_rumah_burung` WHERE `no_registrasi` = '" + input + "' "
                        + "AND CHAR_LENGTH(`no_registrasi`) IN (3, 4)";
                ResultSet rs = Utility.db.getStatement().executeQuery(query);
                if (rs.next()) {
                    for (int i = 0; i < Table_laporan_produksi.getRowCount(); i++) {
                        try {
                            sql = "UPDATE `tb_laporan_produksi` SET `cheat_rsb` = '" + input + "' "
                                    + "WHERE `no_laporan_produksi` = '" + Table_laporan_produksi.getValueAt(i, 1).toString() + "'";
                            Utility.db.getStatement().executeUpdate(sql);
                        } catch (SQLException e) {
                            JOptionPane.showMessageDialog(this, e);
                            Logger.getLogger(JPanel_Laporan_Produksi1.class.getName()).log(Level.SEVERE, null, e);
                        }
                    }
                    JOptionPane.showMessageDialog(this, "data Saved");
                } else {
                    JOptionPane.showMessageDialog(this, "Maaf Rumah burung belum terdaftar / teregistrasi !");
                }
            } catch (SQLException ex) {
                Logger.getLogger(JPanel_Laporan_Produksi1.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        refreshTable();
    }//GEN-LAST:event_button_cheat_rsb2ActionPerformed

    private void button_cheat_no_kartu2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_cheat_no_kartu2ActionPerformed
        // TODO add your handling code here:
        String input = JOptionPane.showInputDialog("No Kartu :");
        if (input == null) {

        } else if (input.equals("")) {
            int dialogResult = JOptionPane.showConfirmDialog(this, "Hapus data cheat??", "Warning", 0);
            if (dialogResult == JOptionPane.YES_OPTION) {
                for (int i = 0; i < Table_laporan_produksi.getRowCount(); i++) {
                    try {
                        sql = "UPDATE `tb_laporan_produksi` SET `cheat_no_kartu` = NULL "
                                + "WHERE `no_laporan_produksi` = '" + Table_laporan_produksi.getValueAt(i, 1).toString() + "'";
                        Utility.db.getStatement().executeUpdate(sql);
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(this, e);
                        Logger.getLogger(JPanel_Laporan_Produksi1.class.getName()).log(Level.SEVERE, null, e);
                    }
                }
            }
        } else {
            try {
                String query = "SELECT `no_kartu_waleta` FROM `tb_bahan_baku_masuk` WHERE `no_kartu_waleta` = '" + input + "' ";
                ResultSet rs = Utility.db.getStatement().executeQuery(query);
                if (rs.next()) {
                    for (int i = 0; i < Table_laporan_produksi.getRowCount(); i++) {
                        try {
                            sql = "UPDATE `tb_laporan_produksi` SET `cheat_no_kartu` = '" + input + "' "
                                    + "WHERE `no_laporan_produksi` = '" + Table_laporan_produksi.getValueAt(i, 1).toString() + "'";
                            Utility.db.getStatement().executeUpdate(sql);
                        } catch (SQLException e) {
                            JOptionPane.showMessageDialog(this, e);
                            Logger.getLogger(JPanel_Laporan_Produksi1.class.getName()).log(Level.SEVERE, null, e);
                        }
                    }
                    JOptionPane.showMessageDialog(this, "data Saved");
                } else {
                    JOptionPane.showMessageDialog(this, "Maaf No Kartu salah, tidak ada di bahan baku masuk !");
                }
            } catch (SQLException ex) {
                Logger.getLogger(JPanel_Laporan_Produksi1.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        refreshTable();
    }//GEN-LAST:event_button_cheat_no_kartu2ActionPerformed

    private void button_cheat_tgl_lpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_cheat_tgl_lpActionPerformed
        // TODO add your handling code here:
        String input = JOptionPane.showInputDialog("Masukkan Tanggal LP Cheat (yyyy-MM-dd) : ");
        if (input == null) {

        } else if (input.equals("")) {
            int dialogResult = JOptionPane.showConfirmDialog(this, "Hapus data cheat??", "Warning", 0);
            if (dialogResult == JOptionPane.YES_OPTION) {
                for (int i = 0; i < Table_laporan_produksi.getRowCount(); i++) {
                    try {
                        sql = "UPDATE `tb_laporan_produksi` SET `cheat_tgl_lp` = NULL "
                                + "WHERE `no_laporan_produksi` = '" + Table_laporan_produksi.getValueAt(i, 1).toString() + "'";
                        Utility.db.getStatement().executeUpdate(sql);
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(this, e);
                        Logger.getLogger(JPanel_Laporan_Produksi1.class.getName()).log(Level.SEVERE, null, e);
                    }
                }
            }
        } else {
            try {
                Date tanggal = new SimpleDateFormat("yyyy-MM-dd").parse(input);
                for (int i = 0; i < Table_laporan_produksi.getRowCount(); i++) {
                    try {
                        sql = "UPDATE `tb_laporan_produksi` SET `cheat_tgl_lp` = '" + input + "' "
                                + "WHERE `no_laporan_produksi` = '" + Table_laporan_produksi.getValueAt(i, 1).toString() + "'";
                        Utility.db.getStatement().executeUpdate(sql);
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(this, e);
                        Logger.getLogger(JPanel_Laporan_Produksi1.class.getName()).log(Level.SEVERE, null, e);
                    }
                }
                JOptionPane.showMessageDialog(this, "data Saved");
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this, "Maaf format tanggal yang di masukkan salah !");
            }
        }
        refreshTable();
    }//GEN-LAST:event_button_cheat_tgl_lpActionPerformed

    private void button_print_lp_cheatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_print_lp_cheatActionPerformed
        // TODO add your handling code here:
        try {
            // TODO add your handling code here:
            //            DefaultTableModel Table = (DefaultTableModel)Table_laporan_produksi.getModel();
            ArrayList<LaporanProduksi> list = LaporanProduksiList();
            int j = Table_laporan_produksi.getSelectedRow();
            if (list.size() < 1) {
                JOptionPane.showMessageDialog(this, "There is no data !", "warning!", 1);
            } else {
                int target_utuh = 0, target_pch = 0, target_sp = 0, target_sh = 0;
                if (list.get(0).getJenis_bentuk().contains("Mangkok")) {
                    switch (list.get(0).getJenis_bulu_grading()) {
                        case "Bulu Ringan":
                            target_utuh = 70;
                            target_pch = 10;
                            target_sp = 14;
                            target_sh = 6;
                            break;
                        case "Bulu Ringan Sekali/Bulu Ringan":
                            target_utuh = 70;
                            target_pch = 10;
                            target_sp = 14;
                            target_sh = 6;
                            break;
                        case "Bulu Sedang":
                            target_utuh = 60;
                            target_pch = 15;
                            target_sp = 17;
                            target_sh = 8;
                            break;
                        case "Bulu Berat":
                            target_utuh = 45;
                            target_pch = 25;
                            target_sp = 20;
                            target_sh = 10;
                            break;
                        case "Bulu Berat Sekali":
                            target_utuh = 45;
                            target_pch = 25;
                            target_sp = 20;
                            target_sh = 10;
                            break;
                        default:
                            target_utuh = 0;
                            target_pch = 0;
                            target_sp = 0;
                            target_sh = 0;
                            break;
                    }
                } else if (list.get(0).getJenis_bentuk().contains("Oval") || list.get(0).getJenis_bentuk().contains("Segitiga")) {
                    switch (list.get(0).getJenis_bulu_grading()) {
                        case "Bulu Ringan":
                            target_utuh = 65;
                            target_pch = 15;
                            target_sp = 14;
                            target_sh = 6;
                            break;
                        case "Bulu Ringan Sekali/Bulu Ringan":
                            target_utuh = 65;
                            target_pch = 15;
                            target_sp = 14;
                            target_sh = 6;
                            break;
                        case "Bulu Sedang":
                            target_utuh = 55;
                            target_pch = 20;
                            target_sp = 17;
                            target_sh = 8;
                            break;
                        case "Bulu Berat":
                            target_utuh = 45;
                            target_pch = 30;
                            target_sp = 20;
                            target_sh = 10;
                            break;
                        case "Bulu Berat Sekali":
                            target_utuh = 45;
                            target_pch = 30;
                            target_sp = 20;
                            target_sh = 10;
                            break;
                        default:
                            target_utuh = 0;
                            target_pch = 0;
                            target_sp = 0;
                            target_sh = 0;
                            break;
                    }
                } else if (list.get(0).getJenis_bentuk().contains("Pecah")) {
                    switch (list.get(0).getJenis_bulu_grading()) {
                        case "Bulu Ringan":
                            target_utuh = 0;
                            target_pch = 75;
                            target_sp = 19;
                            target_sh = 6;
                            break;
                        case "Bulu Ringan Sekali/Bulu Ringan":
                            target_utuh = 0;
                            target_pch = 75;
                            target_sp = 19;
                            target_sh = 6;
                            break;
                        case "Bulu Sedang":
                            target_utuh = 0;
                            target_pch = 70;
                            target_sp = 22;
                            target_sh = 8;
                            break;
                        case "Bulu Berat":
                            target_utuh = 0;
                            target_pch = 65;
                            target_sp = 25;
                            target_sh = 10;
                            break;
                        case "Bulu Berat Sekali":
                            target_utuh = 0;
                            target_pch = 65;
                            target_sp = 25;
                            target_sh = 10;
                            break;
                        default:
                            target_utuh = 0;
                            target_pch = 0;
                            target_sp = 0;
                            target_sh = 0;
                            break;
                    }
                } else {
                    switch (list.get(0).getJenis_bulu_grading()) {
                        case "Bulu Ringan":
                            target_utuh = 0;
                            target_pch = 75;
                            target_sp = 19;
                            target_sh = 6;
                            break;
                        case "Bulu Ringan Sekali/Bulu Ringan":
                            target_utuh = 0;
                            target_pch = 75;
                            target_sp = 19;
                            target_sh = 6;
                            break;
                        case "Bulu Sedang":
                            target_utuh = 0;
                            target_pch = 70;
                            target_sp = 22;
                            target_sh = 8;
                            break;
                        case "Bulu Berat":
                            target_utuh = 0;
                            target_pch = 65;
                            target_sp = 25;
                            target_sh = 10;
                            break;
                        case "Bulu Berat Sekali":
                            target_utuh = 0;
                            target_pch = 65;
                            target_sp = 25;
                            target_sh = 10;
                            break;
                        default:
                            target_utuh = 0;
                            target_pch = 0;
                            target_sp = 0;
                            target_sh = 0;
                            break;
                    }
                }
//                refreshTable();
//                JRDesignQuery newQuery = new JRDesignQuery();
//                newQuery.setText(sql);
                JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Laporan_Produksi_QR.jrxml");
//                JASP_DESIGN.setQuery(newQuery);
                JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("PARAM_NO_LP", list.get(0).getNo_laporan_produksi());
                params.put("target_utuh", target_utuh);
                params.put("target_pch", target_pch);
                params.put("target_sp", target_sp);
                params.put("target_sh", target_sh);
                params.put("CHEAT", 0);
                params.put("parameterHalaman", 1);
                params.put("parameterJumlahHalaman", 1);
                params.put("SUBREPORT_DIR", "Report\\");
                JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, params, Utility.db.getConnection());

                for (int i = 1; i < list.size(); i++) {
                    if (list.get(i).getJenis_bentuk().contains("Mangkok")) {
                        switch (list.get(i).getJenis_bulu_grading()) {
                            case "Bulu Ringan":
                                target_utuh = 70;
                                target_pch = 10;
                                target_sp = 14;
                                target_sh = 6;
                                break;
                            case "Bulu Ringan Sekali/Bulu Ringan":
                                target_utuh = 70;
                                target_pch = 10;
                                target_sp = 14;
                                target_sh = 6;
                                break;
                            case "Bulu Sedang":
                                target_utuh = 60;
                                target_pch = 15;
                                target_sp = 17;
                                target_sh = 8;
                                break;
                            case "Bulu Berat":
                                target_utuh = 45;
                                target_pch = 25;
                                target_sp = 20;
                                target_sh = 10;
                                break;
                            case "Bulu Berat Sekali":
                                target_utuh = 45;
                                target_pch = 25;
                                target_sp = 20;
                                target_sh = 10;
                                break;
                            default:
                                target_utuh = 0;
                                target_pch = 0;
                                target_sp = 0;
                                target_sh = 0;
                                break;
                        }
                    } else if (list.get(i).getJenis_bentuk().contains("Oval") || list.get(0).getJenis_bentuk().contains("Segitiga")) {
                        switch (list.get(i).getJenis_bulu_grading()) {
                            case "Bulu Ringan":
                                target_utuh = 65;
                                target_pch = 15;
                                target_sp = 14;
                                target_sh = 6;
                                break;
                            case "Bulu Ringan Sekali/Bulu Ringan":
                                target_utuh = 65;
                                target_pch = 15;
                                target_sp = 14;
                                target_sh = 6;
                                break;
                            case "Bulu Sedang":
                                target_utuh = 55;
                                target_pch = 20;
                                target_sp = 17;
                                target_sh = 8;
                                break;
                            case "Bulu Berat":
                                target_utuh = 45;
                                target_pch = 30;
                                target_sp = 20;
                                target_sh = 10;
                                break;
                            case "Bulu Berat Sekali":
                                target_utuh = 45;
                                target_pch = 30;
                                target_sp = 20;
                                target_sh = 10;
                                break;
                            default:
                                target_utuh = 0;
                                target_pch = 0;
                                target_sp = 0;
                                target_sh = 0;
                                break;
                        }
                    } else if (list.get(i).getJenis_bentuk().contains("Pecah")) {
                        switch (list.get(i).getJenis_bulu_grading()) {
                            case "Bulu Ringan":
                                target_utuh = 0;
                                target_pch = 75;
                                target_sp = 19;
                                target_sh = 6;
                                break;
                            case "Bulu Ringan Sekali/Bulu Ringan":
                                target_utuh = 0;
                                target_pch = 75;
                                target_sp = 19;
                                target_sh = 6;
                                break;
                            case "Bulu Sedang":
                                target_utuh = 0;
                                target_pch = 70;
                                target_sp = 22;
                                target_sh = 8;
                                break;
                            case "Bulu Berat":
                                target_utuh = 0;
                                target_pch = 65;
                                target_sp = 25;
                                target_sh = 10;
                                break;
                            case "Bulu Berat Sekali":
                                target_utuh = 0;
                                target_pch = 65;
                                target_sp = 25;
                                target_sh = 10;
                                break;
                            default:
                                target_utuh = 0;
                                target_pch = 0;
                                target_sp = 0;
                                target_sh = 0;
                                break;
                        }
                    } else {
                        switch (list.get(i).getJenis_bulu_grading()) {
                            case "Bulu Ringan":
                                target_utuh = 0;
                                target_pch = 75;
                                target_sp = 19;
                                target_sh = 6;
                                break;
                            case "Bulu Ringan Sekali/Bulu Ringan":
                                target_utuh = 0;
                                target_pch = 75;
                                target_sp = 19;
                                target_sh = 6;
                                break;
                            case "Bulu Sedang":
                                target_utuh = 0;
                                target_pch = 70;
                                target_sp = 22;
                                target_sh = 8;
                                break;
                            case "Bulu Berat":
                                target_utuh = 0;
                                target_pch = 65;
                                target_sp = 25;
                                target_sh = 10;
                                break;
                            case "Bulu Berat Sekali":
                                target_utuh = 0;
                                target_pch = 65;
                                target_sp = 25;
                                target_sh = 10;
                                break;
                            default:
                                target_utuh = 0;
                                target_pch = 0;
                                target_sp = 0;
                                target_sh = 0;
                                break;
                        }
                    }
                    JasperDesign JASP_DESIGN2 = JRXmlLoader.load("Report\\Laporan_Produksi_QR.jrxml");
                    JasperReport JASP_REP2 = JasperCompileManager.compileReport(JASP_DESIGN2);
                    Map<String, Object> params2 = new HashMap<String, Object>();
                    params2.put("PARAM_NO_LP", list.get(i).getNo_laporan_produksi());
                    params2.put("target_utuh", target_utuh);
                    params2.put("target_pch", target_pch);
                    params2.put("target_sp", target_sp);
                    params2.put("target_sh", target_sh);
                    params2.put("CHEAT", 0);
                    params2.put("SUBREPORT_DIR", "Report\\");
                    JasperPrint JASP_PRINT2 = JasperFillManager.fillReport(JASP_REP2, params2, Utility.db.getConnection());
                    List pages = JASP_PRINT2.getPages();
                    for (int k = 0; k < pages.size(); k++) {
                        JRPrintPage object = (JRPrintPage) pages.get(k);
                        JASP_PRINT.addPage(object);
                    }
                }
                JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
            }

        } catch (JRException ex) {
            JOptionPane.showMessageDialog(this, ex.getLocalizedMessage());
            Logger.getLogger(JPanel_Laporan_Produksi1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_print_lp_cheatActionPerformed

    private void button_lembar_pengeringan_bakuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_lembar_pengeringan_bakuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_button_lembar_pengeringan_bakuActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_Ruang_LP;
    private com.toedter.calendar.JDateChooser Date_Search_LP_1;
    private com.toedter.calendar.JDateChooser Date_Search_LP_2;
    public static javax.swing.JTable Table_laporan_produksi;
    private javax.swing.JButton button_cheat_no_kartu2;
    private javax.swing.JButton button_cheat_rsb2;
    private javax.swing.JButton button_cheat_tgl_lp;
    private javax.swing.JButton button_export;
    private javax.swing.JButton button_laporan_ozon;
    private javax.swing.JButton button_lembar_pengeringan_baku;
    private javax.swing.JButton button_print_label;
    private javax.swing.JButton button_print_lp_cheat;
    private javax.swing.JButton button_print_surat_jalan_lp;
    private javax.swing.JButton button_rekap;
    private javax.swing.JButton button_report_lp_sub;
    private javax.swing.JButton button_report_lp_sub1;
    private javax.swing.JButton button_report_lp_wlt;
    private javax.swing.JButton button_report_lp_wlt1;
    private javax.swing.JButton button_report_lp_wlt_jdn;
    private javax.swing.JButton button_search_lp;
    private javax.swing.JButton button_total_cucian_hari_ini;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JPanel jPanel_laporan_produksi;
    private javax.swing.JPanel jPanel_search_laporan_produksi;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JLabel label_total_data_laporan_produksi;
    private javax.swing.JLabel label_total_gram_LP;
    private javax.swing.JLabel label_total_keping_LP;
    private javax.swing.JTextField txt_search_grade;
    private javax.swing.JTextField txt_search_lp;
    private javax.swing.JTextField txt_search_memo;
    private javax.swing.JTextField txt_search_no_kartu;
    // End of variables declaration//GEN-END:variables
}
