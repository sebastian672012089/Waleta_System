package waleta_system.Panel_produksi;

import waleta_system.Class.Utility;

import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
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
import waleta_system.Interface.InterfacePanel;
import waleta_system.MainForm;

public class JPanel_DataCuci extends javax.swing.JPanel implements InterfacePanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    DecimalFormat decimalFormat = new DecimalFormat();

    public JPanel_DataCuci() {
        initComponents();
    }

    @Override
    public void init() {
        refreshTable_Cuci();
    }

    public void executeSQLQuery(String query, String message) {
        Utility.db.getConnection();
        try {
            Utility.db.getConnection().createStatement();
            if ((Utility.db.getStatement().executeUpdate(query)) == 1) {
                refreshTable_Cuci();
                JOptionPane.showMessageDialog(this, "data " + message + " Successfully");
            } else {
                JOptionPane.showMessageDialog(this, "data not " + message);
            }
        } catch (SQLException | HeadlessException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void refreshTable_Cuci() {
        try {
            decimalFormat.setGroupingUsed(true);
            int gram = 0, keping = 0;
            DefaultTableModel model = (DefaultTableModel) Table_Data_Cuci.getModel();
            model.setRowCount(0);
            String filter_tanggal_cuci = "";
            if (Date1_cuci.getDate() != null && Date2_cuci.getDate() != null) {
                filter_tanggal_cuci = "AND (`tb_cuci`.`tgl_masuk_cuci` BETWEEN '" + dateFormat.format(Date1_cuci.getDate()) + "' AND '" + dateFormat.format(Date2_cuci.getDate()) + "') \n";
            }
            String filter_ruangan = "";
            if (ComboBox_ruangan.getSelectedIndex() == 1) {
                filter_ruangan = "AND `tb_laporan_produksi`.`ruangan` IN ('A', 'B', 'C', 'D', 'E') \n";
            } else if (ComboBox_ruangan.getSelectedIndex() == 2) {
                filter_ruangan = "AND `tb_laporan_produksi`.`ruangan` NOT IN ('A', 'B', 'C', 'D', 'E') \n";
            }

            sql = "SELECT `tb_cuci`.`no_laporan_produksi`, `cheat_rsb`, `tb_bahan_baku_masuk_cheat`.`no_registrasi`, `tb_laporan_produksi`.`kode_grade`, `jumlah_keping`, `tgl_masuk_cuci`, `cuci_diterima`, `cuci_diserahkan`, "
                    + "`cuci_kaki_besar`, `cuci_kaki_kecil`, `cuci_hilang_kaki`, \n"
                    + "`cuci_ada_susur`, `cuci_ada_susur_besar`, `cuci_tanpa_susur`, `cuci_utuh`, \n"
                    + "`cuci_hilang_ujung`, `cuci_pecah`, `cuci_pecah_2`, `cuci_sobek_lepas`, `cuci_gumpil`, `cuci_sobek`, \n"
                    + "`tb_cuci`.`id_pegawai`, cuci.`nama_pegawai`, `admin_cuci`, `jenis_treatment`, `tb_laporan_produksi`.`berat_basah`, `tb_laporan_produksi`.`memo_lp`, `tb_grade_bahan_baku`.`jenis_bulu`, "
                    + "kopyok.`nama_pegawai` AS 'pekerja_kopyok_cuci' \n"
                    + "FROM `tb_cuci` \n"
                    + "LEFT JOIN `tb_rendam` ON `tb_cuci`.`no_laporan_produksi` = `tb_rendam`.`no_laporan_produksi` \n"
                    + "LEFT JOIN `tb_karyawan` cuci ON `tb_cuci`.`id_pegawai` = cuci.`id_pegawai`\n"
                    + "LEFT JOIN `tb_karyawan` kopyok ON `tb_cuci`.`pekerja_kopyok_cuci` = kopyok.`id_pegawai`\n"
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_cuci`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`\n"
                    + "LEFT JOIN `tb_bahan_baku_masuk_cheat` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta` \n"
                    + "WHERE `tb_cuci`.`no_laporan_produksi` LIKE '%" + txt_search_no_lp.getText() + "%' \n"
                    + "AND `tb_laporan_produksi`.`no_kartu_waleta` " + ComboBox_no_kartu_like.getSelectedItem().toString() + " '%" + txt_search_no_kartu.getText() + "%' \n"
                    + "AND `tb_laporan_produksi`.`memo_lp` LIKE '%" + txt_search_memo.getText() + "%' \n"
                    + filter_tanggal_cuci
                    + filter_ruangan
                    + "ORDER BY `tb_cuci`.`tgl_masuk_cuci` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[30];
            while (rs.next()) {
                row[0] = rs.getString("no_laporan_produksi");
                row[1] = rs.getString("kode_grade");
                switch (rs.getString("jenis_bulu")) {
                    case "Bulu Ringan":
                        row[2] = "BR";
                        break;
                    case "Bulu Ringan Sekali/Bulu Ringan":
                        row[2] = "BRS/BR";
                        break;
                    case "Bulu Sedang":
                        row[2] = "BS";
                        break;
                    case "Bulu Berat":
                        row[2] = "BB";
                        break;
                    case "Bulu Berat Sekali":
                        row[2] = "BB2";
                        break;
                    default:
                        row[2] = "-";
                        break;
                }
                row[3] = rs.getInt("jumlah_keping");
                row[4] = rs.getInt("berat_basah");
                row[5] = rs.getDate("tgl_masuk_cuci");
                row[6] = rs.getString("cuci_diterima");
                row[7] = rs.getString("cuci_diserahkan");

                row[8] = rs.getInt("cuci_kaki_besar");
                row[9] = rs.getInt("cuci_kaki_kecil");
                row[10] = rs.getInt("cuci_hilang_kaki");
                row[11] = rs.getInt("cuci_ada_susur");
                row[12] = rs.getInt("cuci_ada_susur_besar");
                row[13] = rs.getInt("cuci_tanpa_susur");

                row[14] = rs.getInt("cuci_utuh");
                row[15] = rs.getInt("cuci_hilang_ujung");
                row[16] = rs.getInt("cuci_pecah");
                row[17] = rs.getInt("cuci_pecah_2");
                row[18] = rs.getInt("cuci_sobek");
                row[19] = rs.getInt("cuci_sobek_lepas");
                row[20] = rs.getInt("cuci_gumpil");

                row[21] = rs.getString("memo_lp");
                row[22] = rs.getString("nama_pegawai");
                row[23] = rs.getString("id_pegawai");
                row[24] = rs.getString("admin_cuci");
                row[25] = rs.getString("jenis_treatment");
                row[26] = rs.getString("pekerja_kopyok_cuci");
                model.addRow(row);
                keping = keping + rs.getInt("jumlah_keping");
                gram = gram + rs.getInt("berat_basah");
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_Data_Cuci);
            int rowData = Table_Data_Cuci.getRowCount();
            label_total_data_cuci.setText(Integer.toString(rowData));
            label_total_kpg.setText(decimalFormat.format(keping));
            label_total_gram.setText(decimalFormat.format(gram));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_DataCuci.class.getName()).log(Level.SEVERE, null, ex);
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

        jPanel_Data_Cuci = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        Table_Data_Cuci = new javax.swing.JTable();
        label_total_data_cuci = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        Date2_cuci = new com.toedter.calendar.JDateChooser();
        Date1_cuci = new com.toedter.calendar.JDateChooser();
        txt_search_no_lp = new javax.swing.JTextField();
        button_search_cuci = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        button_export_data_cuci = new javax.swing.JButton();
        button_terima_cuci = new javax.swing.JButton();
        button_setor_cuci = new javax.swing.JButton();
        button_edit_cuci = new javax.swing.JButton();
        button_delete_cuci = new javax.swing.JButton();
        button_print = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        label_total_gram = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        label_total_kpg = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txt_search_memo = new javax.swing.JTextField();
        button_tv = new javax.swing.JButton();
        button_rank_pencuci = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        txt_search_no_kartu = new javax.swing.JTextField();
        ComboBox_no_kartu_like = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        ComboBox_ruangan = new javax.swing.JComboBox<>();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Cuci", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        jPanel_Data_Cuci.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_Data_Cuci.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jScrollPane3.setBackground(new java.awt.Color(255, 255, 255));

        Table_Data_Cuci.setAutoCreateRowSorter(true);
        Table_Data_Cuci.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_Data_Cuci.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP", "Grade", "Jenis Bulu", "Keping", "Gram", "Tgl Cuci", "Diterima", "Diserahkan", "Kaki Besar", "Kaki Kecil", "Tanpa kaki", "Ada Susur", "Ada Susur Besar", "Tanpa Susur", "Utuh", "Hilang Ujung", "Pecah", "Pecah 2", "Sobek", "Sobek Lepas", "Gumpil", "Memo LP", "Dicuci oleh", "ID Pegawai", "Admin", "Jenis Treatment", "Pekerja Kopyok"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(Table_Data_Cuci);

        label_total_data_cuci.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_cuci.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_total_data_cuci.setText("TOTAL");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel5.setText("Total LP :");

        Date2_cuci.setBackground(new java.awt.Color(255, 255, 255));
        Date2_cuci.setDate(new Date());
        Date2_cuci.setDateFormatString("dd MMMM yyyy");
        Date2_cuci.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date1_cuci.setBackground(new java.awt.Color(255, 255, 255));
        Date1_cuci.setDate(new Date(new Date().getTime()- (7 * 24 * 60 * 60 * 1000)));
        Date1_cuci.setDateFormatString("dd MMMM yyyy");
        Date1_cuci.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        txt_search_no_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_no_lp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_no_lpKeyPressed(evt);
            }
        });

        button_search_cuci.setBackground(new java.awt.Color(255, 255, 255));
        button_search_cuci.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_cuci.setText("Search");
        button_search_cuci.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_cuciActionPerformed(evt);
            }
        });

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Laporan Produksi :");

        button_export_data_cuci.setBackground(new java.awt.Color(255, 255, 255));
        button_export_data_cuci.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_data_cuci.setText("Export to Excel");
        button_export_data_cuci.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_data_cuciActionPerformed(evt);
            }
        });

        button_terima_cuci.setBackground(new java.awt.Color(255, 255, 255));
        button_terima_cuci.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_terima_cuci.setText("Terima LP");
        button_terima_cuci.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_terima_cuciActionPerformed(evt);
            }
        });

        button_setor_cuci.setBackground(new java.awt.Color(255, 255, 255));
        button_setor_cuci.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_setor_cuci.setText("Setor LP");
        button_setor_cuci.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_setor_cuciActionPerformed(evt);
            }
        });

        button_edit_cuci.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_cuci.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_edit_cuci.setText("Edit Data");
        button_edit_cuci.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_cuciActionPerformed(evt);
            }
        });

        button_delete_cuci.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_cuci.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete_cuci.setText("Delete");
        button_delete_cuci.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_cuciActionPerformed(evt);
            }
        });

        button_print.setBackground(new java.awt.Color(255, 255, 255));
        button_print.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_print.setText("Print");
        button_print.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_printActionPerformed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Tanggal Cuci :");

        label_total_gram.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_total_gram.setText("TOTAL");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel6.setText("Total Gram :");

        label_total_kpg.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_total_kpg.setText("TOTAL");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel7.setText("Total Kpg :");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Memo LP :");

        txt_search_memo.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_memo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_memoKeyPressed(evt);
            }
        });

        button_tv.setBackground(new java.awt.Color(255, 255, 255));
        button_tv.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_tv.setText("TV");
        button_tv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_tvActionPerformed(evt);
            }
        });

        button_rank_pencuci.setBackground(new java.awt.Color(255, 255, 255));
        button_rank_pencuci.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_rank_pencuci.setText("RANK PENCUCI");
        button_rank_pencuci.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_rank_pencuciActionPerformed(evt);
            }
        });

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("No Kartu");

        txt_search_no_kartu.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_no_kartu.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_no_kartuKeyPressed(evt);
            }
        });

        ComboBox_no_kartu_like.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_no_kartu_like.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "LIKE", "NOT LIKE" }));

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Ruangan :");

        ComboBox_ruangan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_ruangan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ALL", "WALETA", "EKSTERNAL" }));

        javax.swing.GroupLayout jPanel_Data_CuciLayout = new javax.swing.GroupLayout(jPanel_Data_Cuci);
        jPanel_Data_Cuci.setLayout(jPanel_Data_CuciLayout);
        jPanel_Data_CuciLayout.setHorizontalGroup(
            jPanel_Data_CuciLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Data_CuciLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Data_CuciLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(jPanel_Data_CuciLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_no_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_no_kartu_like, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_no_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_memo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date1_cuci, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date2_cuci, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search_cuci)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel_Data_CuciLayout.createSequentialGroup()
                        .addComponent(button_terima_cuci)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_setor_cuci)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_edit_cuci)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_delete_cuci)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export_data_cuci)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_tv)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_rank_pencuci)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_print)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 321, Short.MAX_VALUE)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_kpg)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data_cuci)))
                .addContainerGap())
        );
        jPanel_Data_CuciLayout.setVerticalGroup(
            jPanel_Data_CuciLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Data_CuciLayout.createSequentialGroup()
                .addGroup(jPanel_Data_CuciLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_memo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_no_kartu_like, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date2_cuci, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date1_cuci, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_no_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search_cuci, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_no_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 610, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Data_CuciLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_Data_CuciLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_terima_cuci)
                        .addComponent(button_setor_cuci)
                        .addComponent(button_edit_cuci)
                        .addComponent(button_delete_cuci)
                        .addComponent(button_export_data_cuci)
                        .addComponent(button_print)
                        .addComponent(button_tv)
                        .addComponent(button_rank_pencuci))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_Data_CuciLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel_Data_CuciLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_kpg, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel_Data_CuciLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_gram, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel_Data_CuciLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_data_cuci, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_Data_Cuci, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_Data_Cuci, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void button_delete_cuciActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_cuciActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_Data_Cuci.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang ingin di hapus !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    String Query = "DELETE FROM `tb_cuci` WHERE `tb_cuci`.`no_laporan_produksi` = '" + Table_Data_Cuci.getValueAt(j, 0) + "'";
                    executeSQLQuery(Query, "deleted !");
                }
            }
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_button_delete_cuciActionPerformed

    private void txt_search_no_lpKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_no_lpKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_Cuci();
        }
    }//GEN-LAST:event_txt_search_no_lpKeyPressed

    private void button_search_cuciActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_cuciActionPerformed
        // TODO add your handling code here:
        refreshTable_Cuci();
    }//GEN-LAST:event_button_search_cuciActionPerformed

    private void button_export_data_cuciActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_data_cuciActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_Data_Cuci.getModel();
        ExportToExcel.writeToExcel(model, jPanel_Data_Cuci);
    }//GEN-LAST:event_button_export_data_cuciActionPerformed

    private void button_terima_cuciActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_terima_cuciActionPerformed
        // TODO add your handling code here:
        JDialog_Terima_LP_Cuci terima_lp = new JDialog_Terima_LP_Cuci(new javax.swing.JFrame(), true);
        terima_lp.pack();
        terima_lp.setLocationRelativeTo(this);
        terima_lp.setVisible(true);
        terima_lp.setEnabled(true);
        refreshTable_Cuci();
    }//GEN-LAST:event_button_terima_cuciActionPerformed

    private void button_setor_cuciActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_setor_cuciActionPerformed
        // TODO add your handling code here:
        int j = Table_Data_Cuci.getSelectedRow();
        if (j == -1) {
            JOptionPane.showMessageDialog(this, "Anda belum memilih LP yang akan di setorkan !");
        } else {
            try {
                String sql_rendam = "SELECT `pekerja_steam` FROM `tb_rendam` WHERE `no_laporan_produksi` = '" + Table_Data_Cuci.getValueAt(j, 0).toString() + "'";
                rs = Utility.db.getStatement().executeQuery(sql_rendam);
                if (rs.next()) {
                    if (rs.getString("pekerja_steam") == null) {
                        JOptionPane.showMessageDialog(this, "Maaf data treatment belum di input, silahkan input data treatment terlebih dulu");
                    } else if (Table_Data_Cuci.getValueAt(j, 7) != "-" && Table_Data_Cuci.getValueAt(j, 23) != null) {
                        JOptionPane.showMessageDialog(this, "No Laporan Produksi : " + Table_Data_Cuci.getValueAt(j, 0).toString() + "\n Sudah disetorkan");
                    } else {
                        JDialog_Setor_LP_Cuci setor_lp = new JDialog_Setor_LP_Cuci(new javax.swing.JFrame(), true, Table_Data_Cuci.getValueAt(j, 0).toString());
                        setor_lp.pack();
                        setor_lp.setLocationRelativeTo(this);
                        setor_lp.setVisible(true);
                        setor_lp.setEnabled(true);
                    }
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
                Logger.getLogger(JPanel_DataCuci.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_setor_cuciActionPerformed

    private void button_edit_cuciActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_cuciActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_Data_Cuci.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang akan di edit !");
            } else {
                String no_lp = Table_Data_Cuci.getValueAt(j, 0).toString();
                JDialog_Edit_Data_Cuci edit_cuci = new JDialog_Edit_Data_Cuci(new javax.swing.JFrame(), true, no_lp);
                edit_cuci.pack();
                edit_cuci.setLocationRelativeTo(this);
                edit_cuci.setVisible(true);
                edit_cuci.setEnabled(true);
            }
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_edit_cuciActionPerformed

    private void button_printActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_printActionPerformed
        try {
            String no_lp = "";
            for (int i = 0; i < Table_Data_Cuci.getRowCount(); i++) {
                if (i != 0) {
                    no_lp = no_lp + ", ";
                }
                no_lp = no_lp + "'" + Table_Data_Cuci.getValueAt(i, 0).toString() + "'";
            }
            sql = "SELECT `tb_cuci`.`no_laporan_produksi`, `cheat_no_kartu`, `cheat_rsb`, `tb_bahan_baku_masuk`.`no_registrasi`, `tb_laporan_produksi`.`kode_grade`, `jumlah_keping`, `tgl_masuk_cuci`, `tb_cuci`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `admin_cuci`, `tb_laporan_produksi`.`berat_basah`, `tb_laporan_produksi`.`memo_lp`\n"
                    + "FROM `tb_cuci`\n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_cuci`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_cuci`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_bahan_baku_masuk` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta` "
                    + "WHERE "
                    + "`tb_cuci`.`no_laporan_produksi` IN (" + no_lp + ") "
//                    + "AND `cheat_rsb` IS NOT NULL "
//                    + "AND `cheat_no_kartu` IS NOT NULL "
                    + "ORDER BY `tb_cuci`.`tgl_masuk_cuci` ASC";
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(sql);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Lembar_Kerja_Pencucian.jrxml");
            JASP_DESIGN.setQuery(newQuery);
            Map<String, Object> map = new HashMap<>();
            if (!(Date1_cuci.getDate() == null || Date2_cuci.getDate() == null)) {
                String tanggal = new SimpleDateFormat("dd MMM yyy").format(Date1_cuci.getDate()) + " - " + new SimpleDateFormat("dd MMM yyy").format(Date2_cuci.getDate());
                map.put("TGL_CUCI", tanggal);
            }
            map.put("CHEAT", 0);
            map.put("REPORT_MAX_COUNT", Table_Data_Cuci.getRowCount());
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)

        } catch (JRException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_printActionPerformed

    private void txt_search_memoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_memoKeyPressed
        // TODO add your handling code here:
        refreshTable_Cuci();
    }//GEN-LAST:event_txt_search_memoKeyPressed

    private void button_tvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_tvActionPerformed
        // TODO add your handling code here:
        JFrame_Tampilan_Cuci frame = new JFrame_Tampilan_Cuci();
        frame.pack();
        frame.setLocationRelativeTo(this);
        frame.setVisible(true);
        frame.setEnabled(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }//GEN-LAST:event_button_tvActionPerformed

    private void button_rank_pencuciActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_rank_pencuciActionPerformed
        // TODO add your handling code here:
        JFrame_Rank_Cuci frame = new JFrame_Rank_Cuci();
        frame.pack();
        frame.setLocationRelativeTo(this);
        frame.setVisible(true);
        frame.setEnabled(true);
        frame.init();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }//GEN-LAST:event_button_rank_pencuciActionPerformed

    private void txt_search_no_kartuKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_no_kartuKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_search_no_kartuKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_no_kartu_like;
    private javax.swing.JComboBox<String> ComboBox_ruangan;
    private com.toedter.calendar.JDateChooser Date1_cuci;
    private com.toedter.calendar.JDateChooser Date2_cuci;
    public static javax.swing.JTable Table_Data_Cuci;
    public javax.swing.JButton button_delete_cuci;
    public javax.swing.JButton button_edit_cuci;
    private javax.swing.JButton button_export_data_cuci;
    private javax.swing.JButton button_print;
    private javax.swing.JButton button_rank_pencuci;
    public static javax.swing.JButton button_search_cuci;
    public javax.swing.JButton button_setor_cuci;
    public javax.swing.JButton button_terima_cuci;
    private javax.swing.JButton button_tv;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel_Data_Cuci;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel label_total_data_cuci;
    private javax.swing.JLabel label_total_gram;
    private javax.swing.JLabel label_total_kpg;
    private javax.swing.JTextField txt_search_memo;
    private javax.swing.JTextField txt_search_no_kartu;
    private javax.swing.JTextField txt_search_no_lp;
    // End of variables declaration//GEN-END:variables

}
