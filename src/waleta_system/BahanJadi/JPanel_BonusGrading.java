package waleta_system.BahanJadi;

import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.BahanBaku.JPanel_RumahBurung;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;
import waleta_system.Class.ExportToExcel;

public class JPanel_BonusGrading extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    PreparedStatement pst;
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    int row = 0;
    int jmb = 0, jmb_r = 0, spr = 0, spr_r = 0, prem = 0, prem_r = 0, std = 0, ovl1 = 0, ovl2 = 0, sgtg_bsr = 0, sgtg1 = 0, sgtg2 = 0,
            flat_mk_pch = 0, flat_mk_pch1 = 0, flat_mk_pch2 = 0, flat_swr = 0, flat_zt = 0, flat_ztb = 0, flat_yt_kcl = 0, flat_yt_sdg = 0, flat_yt_bsr = 0, flat_srt_kcl = 0, total_bonus = 0;

    public JPanel_BonusGrading() {
        initComponents();
    }

    public void init() {
        try {

            refreshTable_harga();
            refreshTable_bonusLP();
            tabel_bonus_grading.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && tabel_bonus_grading.getSelectedRow() != -1) {
                        row = tabel_bonus_grading.getSelectedRow();
                        label_no_lp.setText(tabel_bonus_grading.getValueAt(row, 0).toString());
                        refresh_TableGrading();
                    }
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(JPanel_RumahBurung.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refresh_TableGrading() {
        try {
            DefaultTableModel model = (DefaultTableModel) tabel_detailGrading.getModel();
            model.setRowCount(0);
            sql = "SELECT `kode_grade`, `keping`, `gram` FROM `tb_grading_bahan_jadi` LEFT JOIN `tb_grade_bahan_jadi` ON `tb_grading_bahan_jadi`.`grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`"
                    + "WHERE `kode_asal_bahan_jadi` = '" + tabel_bonus_grading.getValueAt(row, 0) + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("kode_grade"),
                    rs.getInt("keping"),
                    rs.getInt("gram"),});
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_detailGrading);
        } catch (SQLException ex) {
            Logger.getLogger(JDialog_rePacking.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_harga() {
        try {
            DefaultTableModel model = (DefaultTableModel) Tabel_harga_bonus.getModel();
            model.setRowCount(0);
            sql = "SELECT * FROM `tb_bonus_grading` WHERE 1";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("grade_baku"),
                    rs.getInt("jumbo"),
                    rs.getInt("jumbo_r"),
                    rs.getInt("super"),
                    rs.getInt("super_r"),
                    rs.getInt("premium"),
                    rs.getInt("premium_r"),
                    rs.getInt("standart"),
                    rs.getInt("oval_1"),
                    rs.getInt("oval_2"),
                    rs.getInt("segitiga_besar"),
                    rs.getInt("segitiga_1"),
                    rs.getInt("segitiga_2"),
                    rs.getInt("pch_flat"),
                    new SimpleDateFormat("dd MMMM yyyy").format(rs.getDate("last_update"))
                });
            }
            ColumnsAutoSizer.sizeColumnsToFit(Tabel_harga_bonus);
        } catch (SQLException ex) {
            Logger.getLogger(JDialog_rePacking.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void CountBonus(String lp, String grade) {
        try {
            jmb = 0;
            jmb_r = 0;
            spr = 0;
            spr_r = 0;
            prem = 0;
            prem_r = 0;
            std = 0;
            ovl1 = 0;
            ovl2 = 0;
            sgtg_bsr = 0;
            sgtg1 = 0;
            sgtg2 = 0;
            flat_mk_pch = 0;
            flat_mk_pch1 = 0;
            flat_mk_pch2 = 0;
            flat_swr = 0;
            flat_zt = 0;
            flat_ztb = 0;
            flat_yt_kcl = 0;
            flat_yt_sdg = 0;
            flat_yt_bsr = 0;
            flat_srt_kcl = 0;
            total_bonus = 0;
            int bonus_jmb = 0, bonus_jmb_r = 0, bonus_spr = 0, bonus_spr_r = 0, bonus_prem = 0, bonus_prem_r = 0, bonus_std = 0,
                    bonus_ovl1 = 0, bonus_ovl2 = 0, bonus_sgtg_bsr = 0, bonus_sgtg1 = 0, bonus_sgtg2 = 0, bonus_flat = 0, bonus_total_bonus = 0;

            sql = "SELECT `jumbo`, `jumbo_r`, `super`, `super_r`, `premium`, `premium_r`, `standart`, "
                    + "`oval_1`, `oval_2`, `segitiga_besar`, `segitiga_1`, `segitiga_2`, `pch_flat`"
                    + "FROM `tb_bonus_grading` WHERE `grade_baku` = '" + grade + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                bonus_jmb = rs.getInt("jumbo");
                bonus_jmb_r = rs.getInt("jumbo_r");
                bonus_spr = rs.getInt("super");
                bonus_spr_r = rs.getInt("super_r");
                bonus_prem = rs.getInt("premium");
                bonus_prem_r = rs.getInt("premium_r");
                bonus_std = rs.getInt("standart");
                bonus_ovl1 = rs.getInt("oval_1");
                bonus_ovl2 = rs.getInt("oval_2");
                bonus_sgtg_bsr = rs.getInt("segitiga_besar");
                bonus_sgtg1 = rs.getInt("segitiga_1");
                bonus_sgtg2 = rs.getInt("segitiga_2");
                bonus_flat = rs.getInt("pch_flat");
            }

            sql = "SELECT `kode_asal_bahan_jadi`, `grade_bahan_jadi`, `keping`, `gram` FROM `tb_grading_bahan_jadi`"
                    + "WHERE `kode_asal_bahan_jadi` = '" + lp + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                switch (rs.getString("grade_bahan_jadi")) {
                    case "001":
                        jmb = rs.getInt("keping") * bonus_jmb;
                        break;
                    case "002":
                        jmb_r = rs.getInt("keping") * bonus_jmb_r;
                        break;
                    case "004":
                        spr = rs.getInt("keping") * bonus_spr;
                        break;
                    case "005":
                        spr_r = rs.getInt("keping") * bonus_spr_r;
                        break;
                    case "007":
                        prem = rs.getInt("keping") * bonus_prem;
                        break;
                    case "008":
                        prem_r = rs.getInt("keping") * bonus_prem_r;
                        break;
                    case "010":
                        std = rs.getInt("keping") * bonus_std;
                        break;
                    case "012":
                        ovl1 = rs.getInt("keping") * bonus_ovl1;
                        break;
                    case "014":
                        ovl2 = rs.getInt("keping") * bonus_ovl2;
                        break;
                    case "017":
                        sgtg_bsr = rs.getInt("keping") * bonus_sgtg_bsr;
                        break;
                    case "018":
                        sgtg1 = rs.getInt("keping") * bonus_sgtg1;
                        break;
                    case "019":
                        sgtg2 = rs.getInt("keping") * bonus_sgtg2;
                        break;
                    case "066":
                        flat_mk_pch = rs.getInt("keping") * bonus_flat;
                        break;
                    case "029":
                        flat_mk_pch1 = rs.getInt("keping") * bonus_flat;
                        break;
                    case "030":
                        flat_mk_pch2 = rs.getInt("keping") * bonus_flat;
                        break;
                    case "036":
                        flat_zt = rs.getInt("keping") * bonus_flat;
                        break;
                    case "037":
                        flat_ztb = rs.getInt("keping") * bonus_flat;
                        break;
                    case "038":
                        flat_yt_kcl = rs.getInt("keping") * bonus_flat;
                        break;
                    case "039":
                        flat_yt_sdg = rs.getInt("keping") * bonus_flat;
                        break;
                    case "040":
                        flat_yt_bsr = rs.getInt("keping") * bonus_flat;
                        break;
                    case "041":
                        flat_srt_kcl = rs.getInt("keping") * bonus_flat;
                        break;
                    case "043":
                        flat_swr = rs.getInt("keping") * bonus_flat;
                        break;
                    default:
                        break;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_BonusGrading.class.getName()).log(Level.SEVERE, null, ex);
        }
        total_bonus = jmb + jmb_r + spr + spr_r + prem + prem_r + std + ovl1 + ovl2 + sgtg_bsr + sgtg1 + sgtg2 + flat_mk_pch + flat_mk_pch1 + flat_mk_pch2 + flat_swr + flat_zt + flat_ztb + flat_yt_kcl + flat_yt_sdg + flat_yt_bsr + flat_srt_kcl;
    }

    public void refreshTable_bonusLP() {
        try {
            DefaultTableModel model = (DefaultTableModel) tabel_bonus_grading.getModel();
            model.setRowCount(0);
            String ruangan = ComboBox_ruangan.getSelectedItem().toString();
            if ("All".equals(ruangan)) {
                ruangan = "";
            }
            String query = "SELECT `tb_bahan_jadi_masuk`.`kode_asal`,`tb_laporan_produksi`.`kode_grade`, `tb_laporan_produksi`.`ruangan`, `tb_bahan_jadi_masuk`.`tanggal_grading`, `tb_cabut`.`ketua_regu`, `tb_karyawan`.`nama_pegawai`\n"
                    + "FROM `tb_bahan_jadi_masuk` \n"
                    + "JOIN `tb_laporan_produksi` ON `tb_bahan_jadi_masuk`.`kode_asal` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_cabut` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cabut`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_cetak` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cetak`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_karyawan`.`id_pegawai` = `tb_cetak`.`cetak_dikerjakan`\n"
                    + "WHERE `tb_bahan_jadi_masuk`.`kode_asal` LIKE '%" + txt_search_no_lp.getText() + "%' AND `tb_laporan_produksi`.`ruangan` LIKE '%" + ruangan + "%' AND `tb_bahan_jadi_masuk`.`tanggal_grading` IS NOT NULL \n"
                    + "ORDER BY `tb_bahan_jadi_masuk`.`tanggal_grading` DESC";
            if (Date_filter1.getDate() != null && Date_filter2.getDate() != null) {
                query = "SELECT `tb_bahan_jadi_masuk`.`kode_asal`,`tb_laporan_produksi`.`kode_grade`, `tb_laporan_produksi`.`ruangan`, `tb_bahan_jadi_masuk`.`tanggal_grading`, `tb_cabut`.`ketua_regu`, `tb_karyawan`.`nama_pegawai`\n"
                        + "FROM `tb_bahan_jadi_masuk` \n"
                        + "JOIN `tb_laporan_produksi` ON `tb_bahan_jadi_masuk`.`kode_asal` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "LEFT JOIN `tb_cabut` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cabut`.`no_laporan_produksi`\n"
                        + "LEFT JOIN `tb_cetak` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cetak`.`no_laporan_produksi`\n"
                        + "LEFT JOIN `tb_karyawan` ON `tb_karyawan`.`id_pegawai` = `tb_cetak`.`cetak_dikerjakan`\n"
                        + "WHERE `tb_bahan_jadi_masuk`.`kode_asal` LIKE '%" + txt_search_no_lp.getText() + "%' AND `tb_laporan_produksi`.`ruangan` LIKE '%" + ruangan + "%' AND `tb_bahan_jadi_masuk`.`tanggal_grading` IS NOT NULL\n"
                        + "AND (`tb_bahan_jadi_masuk`.`tanggal_grading` BETWEEN '" + dateFormat.format(Date_filter1.getDate()) + "' AND '" + dateFormat.format(Date_filter2.getDate()) + "')\n"
                        + "ORDER BY `tb_bahan_jadi_masuk`.`tanggal_grading` DESC";
            }
            pst = Utility.db.getConnection().prepareStatement(query);
            ResultSet result = pst.executeQuery();
            while (result.next()) {
                CountBonus(result.getString("kode_asal"), result.getString("kode_grade"));
                int total = flat_mk_pch + flat_mk_pch1 + flat_mk_pch2 + flat_swr + flat_zt + flat_ztb + flat_yt_kcl + flat_yt_sdg + flat_yt_bsr + flat_srt_kcl;
                model.addRow(new Object[]{
                    result.getString("kode_asal"),
                    result.getString("kode_grade"),
                    result.getString("ruangan"),
                    new SimpleDateFormat("dd MMMM yyyy").format(result.getDate("tanggal_grading")),
                    jmb, jmb_r, spr, spr_r, prem, prem_r, std, ovl1, ovl2, sgtg_bsr, sgtg1, sgtg2, total, total_bonus,
                    result.getString("ketua_regu"),
                    result.getString("nama_pegawai")
                });
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_bonus_grading);
            label_total_data.setText(Integer.toString(tabel_bonus_grading.getRowCount()));
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_BonusGrading.class.getName()).log(Level.SEVERE, null, ex);
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

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txt_search_no_lp = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        Date_filter1 = new com.toedter.calendar.JDateChooser();
        Date_filter2 = new com.toedter.calendar.JDateChooser();
        button_search = new javax.swing.JButton();
        button_export = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_bonus_grading = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        ComboBox_ruangan = new javax.swing.JComboBox<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabel_detailGrading = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        label_no_lp = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        label_total_data = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Tabel_harga_bonus = new javax.swing.JTable();
        button_edit = new javax.swing.JButton();
        button_exportHarga = new javax.swing.JButton();

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Bonus Hasil Grading", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("No Laporan Produksi :");

        txt_search_no_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_no_lp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_no_lpKeyPressed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Tanggal Grading :");

        Date_filter1.setBackground(new java.awt.Color(255, 255, 255));
        Date_filter1.setDate(new Date(new Date().getTime()- (7 * 24 * 60 * 60 * 1000)));
        Date_filter1.setDateFormatString("dd MMMM yyyy");
        Date_filter1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_filter2.setBackground(new java.awt.Color(255, 255, 255));
        Date_filter2.setDate(new Date());
        Date_filter2.setDateFormatString("dd MMMM yyyy");
        Date_filter2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_search.setBackground(new java.awt.Color(255, 255, 255));
        button_search.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search.setText("Search");
        button_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_searchActionPerformed(evt);
            }
        });

        button_export.setBackground(new java.awt.Color(255, 255, 255));
        button_export.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export.setText("Export to Excel");
        button_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportActionPerformed(evt);
            }
        });

        tabel_bonus_grading.setAutoCreateRowSorter(true);
        tabel_bonus_grading.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP", "Grade", "Ruang", "Tgl Grading", "JMB", "JMB R", "SPR", "SPR R", "PREM", "PREM R", "STD", "OVL 1", "OVL 2", "SGTG BSR", "SGTG 1", "SGTG 2", "Flat", "TOTAL", "Cabut", "Cetak"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Long.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tabel_bonus_grading);

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Ruangan :");

        ComboBox_ruangan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_ruangan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "A", "B", "C", "D" }));
        ComboBox_ruangan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboBox_ruanganActionPerformed(evt);
            }
        });

        tabel_detailGrading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_detailGrading.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade", "Keping", "Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(tabel_detailGrading);

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel3.setText("Hasil Grading");

        label_no_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_no_lp.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_no_lp.setText("-");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel5.setText("Total Data :");

        label_total_data.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_data.setText("-");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data)
                        .addGap(278, 1268, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_no_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_filter1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_filter2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_no_lp)))
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(Date_filter1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Date_filter2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ComboBox_ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_no_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(label_no_lp)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 579, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(label_total_data))
                .addContainerGap())
        );

        jTabbedPane1.addTab("DATA LP BONUS", jPanel1);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Harga Bonus perGrade", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        Tabel_harga_bonus.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Tabel_harga_bonus.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade Baku", "Jumbo", "Jumbo R", "Super", "Super R", "Premium", "Premium R", "Standart", "Oval 1", "Oval 2", "Sgtg Besar", "Sgtg 1", "Sgtg 2", "Pecah/Flat", "Last Update"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(Tabel_harga_bonus);

        button_edit.setBackground(new java.awt.Color(255, 255, 255));
        button_edit.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_edit.setText("Edit");
        button_edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_editActionPerformed(evt);
            }
        });

        button_exportHarga.setBackground(new java.awt.Color(255, 255, 255));
        button_exportHarga.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_exportHarga.setText("Export");
        button_exportHarga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportHargaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1331, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(button_edit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_exportHarga)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_edit)
                    .addComponent(button_exportHarga))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 608, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("VIEW HARGA BONUS", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void ComboBox_ruanganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboBox_ruanganActionPerformed
        // TODO add your handling code here:
        refreshTable_bonusLP();
    }//GEN-LAST:event_ComboBox_ruanganActionPerformed

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_bonus_grading.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_exportActionPerformed

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        refreshTable_bonusLP();
    }//GEN-LAST:event_button_searchActionPerformed

    private void txt_search_no_lpKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_no_lpKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_bonusLP();
        }
    }//GEN-LAST:event_txt_search_no_lpKeyPressed

    private void button_exportHargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportHargaActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Tabel_harga_bonus.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_exportHargaActionPerformed

    private void button_editActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_editActionPerformed
        // TODO add your handling code here:
        try {
            int j = Tabel_harga_bonus.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang akan di edit !");
            } else {
                JDialog_Edit_Data_BonusGrading dialog = new JDialog_Edit_Data_BonusGrading(new javax.swing.JFrame(), true, Tabel_harga_bonus.getValueAt(j, 0).toString());
                dialog.pack();
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
                dialog.setEnabled(true);
                refreshTable_harga();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_BonusGrading.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_editActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_ruangan;
    private com.toedter.calendar.JDateChooser Date_filter1;
    private com.toedter.calendar.JDateChooser Date_filter2;
    private javax.swing.JTable Tabel_harga_bonus;
    private javax.swing.JButton button_edit;
    private javax.swing.JButton button_export;
    private javax.swing.JButton button_exportHarga;
    private javax.swing.JButton button_search;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label_no_lp;
    private javax.swing.JLabel label_total_data;
    private javax.swing.JTable tabel_bonus_grading;
    private javax.swing.JTable tabel_detailGrading;
    private javax.swing.JTextField txt_search_no_lp;
    // End of variables declaration//GEN-END:variables
}
