package waleta_system.Panel_produksi;

import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import static org.apache.commons.lang.StringUtils.containsIgnoreCase;
import waleta_system.Browse_Karyawan;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;
import waleta_system.Class.Utility;
import waleta_system.Interface.InterfacePanel;
import waleta_system.MainForm;

public class JPanel_DataCabut extends javax.swing.JPanel implements InterfacePanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DecimalFormat decimalFormat = new DecimalFormat();
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_DataCabut() {
        initComponents();
    }

    @Override
    public void init() {
        try {
            ComboBox_ruang.removeAllItems();
            ComboBox_ruang.addItem("All");
            sql = "SELECT DISTINCT(`ruangan`) AS 'ruangan' FROM `tb_laporan_produksi` WHERE 1";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_ruang.addItem(rs.getString("ruangan"));
            }
        } catch (Exception ex) {
            Logger.getLogger(JPanel_DataCabut.class.getName()).log(Level.SEVERE, null, ex);
        }
        refreshTable_Cabut();
        decimalFormat = Utility.DecimalFormatUS(decimalFormat);
        Table_Data_Cabut.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && Table_Data_Cabut.getSelectedRow() != -1) {
                    int row = Table_Data_Cabut.getSelectedRow();
                    if (row != -1) {
                        label_lp.setText(Table_Data_Cabut.getValueAt(row, 0).toString());
                        refreshTable_Pencabut();
                        try {
                            Date tgl_masuk = dateFormat.parse(Table_Data_Cabut.getValueAt(row, 3).toString());
                            Date_pencabut.setMinSelectableDate(tgl_masuk);
                        } catch (ParseException ex) {
                            Logger.getLogger(JPanel_DataCabut.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        if (Table_Data_Cabut.getValueAt(row, 10) == null) {
                            button_edit_cabut.setEnabled(false);
                            button_setor_cabut.setEnabled(true);

                            button_add_pencabut.setEnabled(true);
                            button_edit_pencabut.setEnabled(true);
                            button_delete_pencabut.setEnabled(true);
                        } else {
                            button_edit_cabut.setEnabled(true);
                            button_setor_cabut.setEnabled(false);

                            button_add_pencabut.setEnabled(false);
                            button_edit_pencabut.setEnabled(false);
                            button_delete_pencabut.setEnabled(false);
                        }
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
                        txt_id_pegawai.setText(table_data_pencabut.getValueAt(i, 1).toString());
                        txt_nama_pegawai.setText(table_data_pencabut.getValueAt(i, 2).toString());
                        txt_bagian.setText(table_data_pencabut.getValueAt(i, 3).toString());
                        String tgl_pencabut = table_data_pencabut.getValueAt(i, 4).toString();
                        Date_pencabut.setDate(dateFormat.parse(tgl_pencabut));
                        txt_jmlh_keping.setText(table_data_pencabut.getValueAt(i, 5).toString());
                    } catch (ParseException ex) {
                        Logger.getLogger(JPanel_DataCabut.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
    }

    public void refreshTable_Cabut() {
        try {
            int total_kpg_upah = 0, total_gram = 0;
            DefaultTableModel model = (DefaultTableModel) Table_Data_Cabut.getModel();
            model.setRowCount(0);
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
            sql = "SELECT `tb_cabut`.`no_laporan_produksi`, `ruangan`, `tb_laporan_produksi`.`kode_grade`, `keping_upah`, `berat_basah`, `tb_karyawan`.`nama_pegawai` AS 'pekerja_sesekan', `pekerja_hancuran`, `pekerja_kopyok`, `cabut_diterima`, `tgl_mulai_cabut`, `cabut_diserahkan`, `tgl_setor_cabut`, `sobek_cabut`, `cabut_sobek_lepas`, `gumpil_cabut`, `pecah_cabut`, `cabut_pecah_2`, `cabut_lubang`, `cabut_hilang_kaki`, `cabut_hilang_ujung`, `cabut_kaki_besar`, `cabut_kaki_kecil`, `cabut_hilang_bawah`, `admin_cabut`, `ketua_regu`, `tgl_cabut`, MIN(`tb_detail_pencabut`.`tanggal_cabut`) AS 'tanggal_cabut', `jenis_bulu_cabut`"
                    + "FROM `tb_cabut` "
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_cabut`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_laporan_produksi`.`pekerja_sesekan` = `tb_karyawan`.`id_pegawai`\n"
                    + "LEFT JOIN `tb_cuci` ON `tb_cuci`.`no_laporan_produksi` = `tb_cabut`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_detail_pencabut` ON `tb_cabut`.`no_laporan_produksi` = `tb_detail_pencabut`.`no_laporan_produksi`\n"
                    + "WHERE `tb_cabut`.`no_laporan_produksi` LIKE '%" + txt_search_cabut.getText() + "%' "
                    + ruang
                    + filter_tanggal
                    + "GROUP BY `tb_cabut`.`no_laporan_produksi`\n"
                    + "ORDER BY `tb_cabut`.`tgl_mulai_cabut` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[25];
            while (rs.next()) {
                row[0] = rs.getString("no_laporan_produksi");
                row[1] = rs.getString("ruangan");
                row[2] = rs.getString("kode_grade");
                row[3] = rs.getDate("tgl_mulai_cabut");
                row[4] = rs.getDate("tanggal_cabut");
                row[5] = rs.getString("cabut_diterima");
                row[6] = rs.getString("pekerja_sesekan");
                row[7] = rs.getString("pekerja_hancuran");
                row[8] = rs.getString("pekerja_kopyok");
                row[9] = rs.getInt("keping_upah");
                row[10] = rs.getDate("tgl_setor_cabut");
                row[11] = rs.getString("cabut_diserahkan");
                row[12] = rs.getInt("pecah_cabut");
                row[13] = rs.getInt("sobek_cabut");
                row[14] = rs.getInt("gumpil_cabut");
                row[15] = rs.getInt("cabut_hilang_kaki");
                row[16] = rs.getInt("cabut_hilang_ujung");
                row[17] = rs.getString("admin_cabut");
                row[18] = rs.getString("ketua_regu");
                row[19] = rs.getDate("tgl_cabut");
                row[20] = rs.getString("jenis_bulu_cabut");
                if (ComboBox_filterDate.getSelectedIndex() == 2 && Date1_cabut.getDate() != null && Date2_cabut.getDate() != null) {
                    if (rs.getDate("tanggal_cabut") != null
                            && rs.getDate("tanggal_cabut").after(new Date(Date1_cabut.getDate().getTime() - (1000 * 60 * 60 * 24)))
                            && rs.getDate("tgl_mulai_cabut").before(new Date(Date2_cabut.getDate().getTime() + (1000 * 60 * 60 * 24)))) {
                        model.addRow(row);
                        total_kpg_upah = total_kpg_upah + rs.getInt("keping_upah");
                        total_gram = total_gram + rs.getInt("berat_basah");
                    }
                } else {
                    model.addRow(row);
                    total_kpg_upah = total_kpg_upah + rs.getInt("keping_upah");
                    total_gram = total_gram + rs.getInt("berat_basah");
                }
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_Data_Cabut);
            int rowData = Table_Data_Cabut.getRowCount();
            label_total_data_cabut.setText(Integer.toString(rowData));
            label_total_kpg.setText(decimalFormat.format(total_kpg_upah));
            label_total_gram.setText(decimalFormat.format(total_gram));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_DataCabut.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_Pencabut() {
        try {
            DefaultTableModel model = (DefaultTableModel) table_data_pencabut.getModel();
            model.setRowCount(0);
            int total_kpg = 0;
            sql = "SELECT `nomor`, `no_laporan_produksi`, `tb_detail_pencabut`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `tb_bagian`.`nama_bagian`, `tanggal_cabut`, `jumlah_cabut`, `jumlah_gram` \n"
                    + "FROM `tb_detail_pencabut` \n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_detail_pencabut`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                    + "LEFT JOIN `tb_bagian` ON `tb_detail_pencabut`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                    + "WHERE `no_laporan_produksi` = '" + label_lp.getText() + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[9];
            while (rs.next()) {
                row[0] = rs.getString("nomor");
                row[1] = rs.getString("id_pegawai");
                row[2] = rs.getString("nama_pegawai");
                row[3] = rs.getString("nama_bagian");
                row[4] = rs.getDate("tanggal_cabut");
                row[5] = rs.getInt("jumlah_cabut");
                row[6] = rs.getFloat("jumlah_gram");
                model.addRow(row);
                total_kpg += rs.getInt("jumlah_cabut");
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_data_pencabut);
            label_total_cabutan.setText(Integer.toString(total_kpg));
            String query = "SELECT DATEDIFF(MAX(`tanggal_cabut`), MIN(`tanggal_cabut`))+1 AS 'Hari', COUNT(DISTINCT(`id_pegawai`)) AS 'pencabut' "
                    + "FROM `tb_detail_pencabut` WHERE `no_laporan_produksi` = '" + label_lp.getText() + "'";
            rs = Utility.db.getStatement().executeQuery(query);
            if (rs.next()) {
                label_total_hari.setText(rs.getString("Hari"));
                label_total_pencabut.setText(rs.getString("pencabut"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JPanel_DataCabut.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void addPencabutSUB(String no_lp) {
        try {
            String kode_sub = no_lp.substring(3, 8);
            int berat_basah = 0, total_keping = 0;
            float gram_cabutan = 0;
            boolean check = true;
            sql = "SELECT * FROM `tb_detail_pencabut` WHERE `no_laporan_produksi` = '" + no_lp + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            check = rs.next();
            if (check) {
                JOptionPane.showMessageDialog(this, "data sudah diinput");
            } else {
                sql = "SELECT `berat_basah`, `jumlah_keping` FROM `tb_laporan_produksi` WHERE `no_laporan_produksi` = '" + no_lp + "'";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    berat_basah = rs.getInt("berat_basah");
                    total_keping = rs.getInt("jumlah_keping");
                }
                if (table_data_pencabut.getRowCount() == 0) {
                    sql = "UPDATE `tb_cabut` SET `tgl_cabut`=CURRENT_DATE WHERE `no_laporan_produksi` = '" + no_lp + "'";
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(sql);
                }
                String Query = "INSERT INTO `tb_detail_pencabut`(`no_laporan_produksi`, `id_pegawai`, `tanggal_cabut`, `jumlah_cabut`, `jumlah_gram`, `grup_cabut`) "
                        + "VALUES ('" + no_lp + "','" + txt_id_pegawai.getText() + "','" + dateFormat.format(date) + "','" + total_keping + "', " + berat_basah + ", '" + kode_sub + "')";
                executeSQLQuery(Query, "inserted !");
                refreshTable_Pencabut();
                button_clear_pencabut.doClick();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_DataCabut.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addPencabutWLT(String no_lp) {
        try {
            decimalFormat.setMaximumFractionDigits(2);
            Boolean Check = true;
            int total_baris = table_data_pencabut.getRowCount();
            int keping_cabut = Integer.valueOf(txt_jmlh_keping.getText());
            float berat_basah = 0, total_keping = 0, gram_cabutan = 0;
            String query = "SELECT `berat_basah`, `jumlah_keping`, `keping_upah` FROM `tb_laporan_produksi` WHERE `no_laporan_produksi` = '" + no_lp + "'";
            ResultSet result = Utility.db.getStatement().executeQuery(query);
            if (result.next()) {
                berat_basah = result.getFloat("berat_basah");
                total_keping = result.getFloat("keping_upah");
            }
            gram_cabutan = (berat_basah / total_keping) * Float.valueOf(txt_jmlh_keping.getText());
//            if (total_keping > 0) {
//                gram_cabutan = (berat_basah / total_keping) * Float.valueOf(txt_jmlh_keping.getText());
//            } else {
//                gram_cabutan = (berat_basah / Math.round(berat_basah / 8)) * Float.valueOf(txt_jmlh_keping.getText());
//                keping_cabut = 0;
//            }

            String nama_grup = null;
            String query_grup = "SELECT `kode_grup` FROM `tb_grup_cabut` WHERE `id_pegawai` = '" + txt_id_pegawai.getText() + "'";
            ResultSet result_grup = Utility.db.getStatement().executeQuery(query_grup);
            if (result_grup.next()) {
                nama_grup = result_grup.getString("kode_grup");
            } else {
                nama_grup = "-";
            }

            String absen = "SELECT * FROM `att_log` WHERE DATE(`scan_date`) = '" + dateFormat.format(Date_pencabut.getDate()) + "' AND `pin` = '" + Integer.valueOf(txt_id_pegawai.getText().substring(7)) + "'";
            ResultSet result_absen = Utility.db.getStatement().executeQuery(absen);
            Check = result_absen.next();
            if (!Check) {
                JOptionPane.showMessageDialog(this, "Maaf, Pegawai tidak masuk pada tanggal tersebut");
            }

            for (int i = 0; i < total_baris; i++) {
                if (txt_id_pegawai.getText().equals(table_data_pencabut.getValueAt(i, 1))
                        && dateFormat.format(Date_pencabut.getDate()).equals(table_data_pencabut.getValueAt(i, 4).toString())) {
                    JOptionPane.showMessageDialog(this, "Pegawai dengan nama : " + txt_nama_pegawai.getText() + " pada tanggal " + dateFormat.format(Date_pencabut.getDate()) + "\n sudah terdaftar !");
                    Check = false;
                }
            }
            if (Check) {
                if (containsIgnoreCase(txt_bagian.getText(), "CABUT")) {
                    if (table_data_pencabut.getRowCount() == 0) {
                        sql = "UPDATE `tb_cabut` SET `tgl_cabut`=CURRENT_DATE WHERE `no_laporan_produksi` = '" + no_lp + "'";
                        Utility.db.getConnection().createStatement();
                        Utility.db.getStatement().executeUpdate(sql);
                    }
                    String Query = "INSERT INTO `tb_detail_pencabut`(`no_laporan_produksi`, `id_pegawai`, `tanggal_cabut`, `jumlah_cabut`, `jumlah_gram`, `grup_cabut`, `kode_bagian`) "
                            + "VALUES ('" + no_lp + "','" + txt_id_pegawai.getText() + "','" + dateFormat.format(Date_pencabut.getDate()) + "','" + keping_cabut + "', " + decimalFormat.format(gram_cabutan) + ", '" + nama_grup + "', (SELECT `kode_bagian` FROM `tb_karyawan` WHERE `id_pegawai` = '" + txt_id_pegawai.getText() + "'))";
                    executeSQLQuery(Query, "inserted !");
                    refreshTable_Pencabut();
                    button_clear_pencabut.doClick();
                } else {
                    int dialogResult = JOptionPane.showConfirmDialog(null, "Apakah anda yakin pegawai bukan dari bagian CABUT?", "Warning", JOptionPane.YES_NO_OPTION);
                    if (dialogResult == JOptionPane.YES_OPTION) {
                        // Saving code here
                        if (table_data_pencabut.getRowCount() == 0) {
                            sql = "UPDATE `tb_cabut` SET `tgl_cabut`=CURRENT_DATE WHERE `no_laporan_produksi` = '" + no_lp + "'";
                            Utility.db.getConnection().createStatement();
                            Utility.db.getStatement().executeUpdate(sql);
                        }
                        String Query = "INSERT INTO `tb_detail_pencabut`(`no_laporan_produksi`, `id_pegawai`, `tanggal_cabut`, `jumlah_cabut`, `jumlah_gram`, `grup_cabut`, `kode_bagian`) "
                                + "VALUES ('" + no_lp + "','" + txt_id_pegawai.getText() + "','" + dateFormat.format(Date_pencabut.getDate()) + "','" + keping_cabut + "', " + decimalFormat.format(gram_cabutan) + ", '" + nama_grup + "', (SELECT `kode_bagian` FROM `tb_karyawan` WHERE `id_pegawai` = '" + txt_id_pegawai.getText() + "'))";
                        executeSQLQuery(Query, "inserted !");
                        refreshTable_Pencabut();
                        button_clear_pencabut.doClick();
                    } else if (dialogResult == JOptionPane.NO_OPTION) {

                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_DataCabut.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void executeSQLQuery(String query, String message) {
        Utility.db.getConnection();
        try {
            Utility.db.getConnection().createStatement();
            if ((Utility.db.getStatement().executeUpdate(query)) == 1) {
                JOptionPane.showMessageDialog(this, "data " + message + " Successfully");
            } else {
                JOptionPane.showMessageDialog(this, "data not " + message);
            }
        } catch (SQLException | HeadlessException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JPanel_DataCabut.class.getName()).log(Level.SEVERE, null, e);
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
        Table_Data_Cabut = new javax.swing.JTable();
        jLabel6 = new javax.swing.JLabel();
        label_total_data_cabut = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        label_total_cabutan = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        label_total_pencabut = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_data_pencabut = new javax.swing.JTable();
        button_terima_cabut = new javax.swing.JButton();
        button_setor_cabut = new javax.swing.JButton();
        button_edit_cabut = new javax.swing.JButton();
        button_delete_cabut = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        button_delete_pencabut = new javax.swing.JButton();
        button_edit_pencabut = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txt_id_pegawai = new javax.swing.JTextField();
        txt_nama_pegawai = new javax.swing.JTextField();
        txt_jmlh_keping = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        Date_pencabut = new com.toedter.calendar.JDateChooser();
        button_add_pencabut = new javax.swing.JButton();
        button_pick_pencabut = new javax.swing.JButton();
        button_clear_pencabut = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        txt_bagian = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        label_total_hari = new javax.swing.JLabel();
        label_total_cabutan1 = new javax.swing.JLabel();
        label_total_cabutan2 = new javax.swing.JLabel();
        label_total_cabutan3 = new javax.swing.JLabel();
        button_export_data_cabut = new javax.swing.JButton();
        label_lp = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        ComboBox_ruang = new javax.swing.JComboBox<>();
        button_tandon_cabut = new javax.swing.JButton();
        ComboBox_filterDate = new javax.swing.JComboBox<>();
        button_print_laporan = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        label_total_gram = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        label_total_kpg = new javax.swing.JLabel();
        button_rank_pencabut = new javax.swing.JButton();
        button_terima_cabut_lp_om = new javax.swing.JButton();
        button_setor_cabut_lp_om = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Cabut", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

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
        Date1_cabut.setDate(new Date(new Date().getTime()- (7 * 24 * 60 * 60 * 1000)));
        Date1_cabut.setDateFormatString("dd MMMM yyyy");
        Date1_cabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date2_cabut.setBackground(new java.awt.Color(255, 255, 255));
        Date2_cabut.setDate(new Date());
        Date2_cabut.setDateFormatString("dd MMMM yyyy");
        Date2_cabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Table_Data_Cabut.setAutoCreateRowSorter(true);
        Table_Data_Cabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_Data_Cabut.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP", "Ruangan", "Grade", "Tgl Masuk", "Tgl Cabut", "Diterima", "Pekerja Ssk", "Pekerja Hc", "Pekerja Kopyok", "Kpg upah", "Tgl Selesai", "Diserahkan", "Pecah", "Sobek", "Gumpil", "Hilang Kaki", "Hilang Ujung", "admin", "Ketua Regu", "Tgl Cabut", "Jenis Bulu"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Object.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class
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
        Table_Data_Cabut.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(Table_Data_Cabut);

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
        jLabel9.setText("DATA PENCABUT");

        label_total_pencabut.setBackground(new java.awt.Color(255, 255, 255));
        label_total_pencabut.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_pencabut.setText("88");

        table_data_pencabut.setAutoCreateRowSorter(true);
        table_data_pencabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_pencabut.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "ID Pegawai", "Nama", "Bagian", "Tanggal", "Kpg", "Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(table_data_pencabut);

        button_terima_cabut.setBackground(new java.awt.Color(255, 255, 255));
        button_terima_cabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_terima_cabut.setText("Terima LP");
        button_terima_cabut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_terima_cabutActionPerformed(evt);
            }
        });

        button_setor_cabut.setBackground(new java.awt.Color(255, 255, 255));
        button_setor_cabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_setor_cabut.setText("Setor LP");
        button_setor_cabut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_setor_cabutActionPerformed(evt);
            }
        });

        button_edit_cabut.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_cabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_edit_cabut.setText("Edit Data");
        button_edit_cabut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_cabutActionPerformed(evt);
            }
        });

        button_delete_cabut.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_cabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete_cabut.setText("Delete");
        button_delete_cabut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_cabutActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        button_delete_pencabut.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_pencabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete_pencabut.setText("Delete");
        button_delete_pencabut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_pencabutActionPerformed(evt);
            }
        });

        button_edit_pencabut.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_pencabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_edit_pencabut.setText("Edit");
        button_edit_pencabut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_pencabutActionPerformed(evt);
            }
        });

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("ID Pegawai :");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Nama Pegawai :");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Tanggal :");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Jumlah Cabutan :");

        txt_id_pegawai.setEditable(false);
        txt_id_pegawai.setBackground(new java.awt.Color(255, 255, 255));
        txt_id_pegawai.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        txt_nama_pegawai.setEditable(false);
        txt_nama_pegawai.setBackground(new java.awt.Color(255, 255, 255));
        txt_nama_pegawai.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        txt_jmlh_keping.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Keping");

        Date_pencabut.setBackground(new java.awt.Color(255, 255, 255));
        Date_pencabut.setDate(new Date());
        Date_pencabut.setDateFormatString("dd MMM yyyy");
        Date_pencabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_add_pencabut.setBackground(new java.awt.Color(255, 255, 255));
        button_add_pencabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_add_pencabut.setText("Add");
        button_add_pencabut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_add_pencabutActionPerformed(evt);
            }
        });

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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txt_nama_pegawai, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(txt_id_pegawai)
                                .addGap(0, 0, 0)
                                .addComponent(button_pick_pencabut, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(txt_jmlh_keping, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel8))
                            .addComponent(Date_pencabut, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
                            .addComponent(txt_bagian, javax.swing.GroupLayout.Alignment.LEADING)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(button_add_pencabut)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_edit_pencabut)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_delete_pencabut)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_clear_pencabut)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_id_pegawai, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_pick_pencabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_nama_pegawai, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_pencabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_jmlh_keping, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_delete_pencabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_edit_pencabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_add_pencabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_clear_pencabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel10.setText("Total Pekerja :");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel11.setText("Total Hari :");

        label_total_hari.setBackground(new java.awt.Color(255, 255, 255));
        label_total_hari.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_hari.setText("88");

        label_total_cabutan1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_cabutan1.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_cabutan1.setText("Keping");

        label_total_cabutan2.setBackground(new java.awt.Color(255, 255, 255));
        label_total_cabutan2.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_cabutan2.setText("Orang");

        label_total_cabutan3.setBackground(new java.awt.Color(255, 255, 255));
        label_total_cabutan3.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_cabutan3.setText("Hari");

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
        label_lp.setForeground(new java.awt.Color(255, 0, 0));
        label_lp.setText("Laporan Produksi");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Ruang :");

        ComboBox_ruang.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_ruang.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "A", "B", "C", "D", "E" }));

        button_tandon_cabut.setBackground(new java.awt.Color(255, 255, 255));
        button_tandon_cabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_tandon_cabut.setText("Tandon Cabut");
        button_tandon_cabut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_tandon_cabutActionPerformed(evt);
            }
        });

        ComboBox_filterDate.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_filterDate.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tanggal Masuk", "Tanggal Setor", "Tanggal Mulai Cabut" }));

        button_print_laporan.setBackground(new java.awt.Color(255, 255, 255));
        button_print_laporan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_print_laporan.setText("Print Laporan");
        button_print_laporan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_print_laporanActionPerformed(evt);
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

        button_rank_pencabut.setBackground(new java.awt.Color(255, 255, 255));
        button_rank_pencabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_rank_pencabut.setText("RANK PENCABUT");
        button_rank_pencabut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_rank_pencabutActionPerformed(evt);
            }
        });

        button_terima_cabut_lp_om.setBackground(new java.awt.Color(255, 255, 255));
        button_terima_cabut_lp_om.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_terima_cabut_lp_om.setText("Terima LP CABUT OM");
        button_terima_cabut_lp_om.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_terima_cabut_lp_omActionPerformed(evt);
            }
        });

        button_setor_cabut_lp_om.setBackground(new java.awt.Color(255, 255, 255));
        button_setor_cabut_lp_om.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_setor_cabut_lp_om.setText("Setor LP CABUT OM");
        button_setor_cabut_lp_om.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_setor_cabut_lp_omActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 966, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())
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
                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(label_total_pencabut)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_cabutan2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(label_total_hari)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_cabutan3, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(button_terima_cabut)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_setor_cabut)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_edit_cabut)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_delete_cabut)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_data_cabut)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_print_laporan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_tandon_cabut)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_rank_pencabut)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_terima_cabut_lp_om)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_setor_cabut_lp_om)))
                        .addGap(0, 0, Short.MAX_VALUE))))
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
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_terima_cabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_setor_cabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_edit_cabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete_cabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_data_cabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_print_laporan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_tandon_cabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_rank_pencabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_terima_cabut_lp_om, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_setor_cabut_lp_om, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(42, 42, 42))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_cabutan1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_cabutan2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_pencabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(label_total_cabutan, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_hari, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(label_total_cabutan3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
            refreshTable_Cabut();
        }
    }//GEN-LAST:event_txt_search_cabutKeyPressed

    private void button_search_cabutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_cabutActionPerformed
        // TODO add your handling code here:
        refreshTable_Cabut();
    }//GEN-LAST:event_button_search_cabutActionPerformed

    private void button_terima_cabutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_terima_cabutActionPerformed
        // TODO add your handling code here:
        JDialog_Terima_LP_Cabut terima_lp = new JDialog_Terima_LP_Cabut(new javax.swing.JFrame(), true);
        terima_lp.pack();
        terima_lp.setLocationRelativeTo(this);
        terima_lp.setVisible(true);
        terima_lp.setEnabled(true);
    }//GEN-LAST:event_button_terima_cabutActionPerformed

    private void button_delete_cabutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_cabutActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_Data_Cabut.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang ingin di hapus !");
            } else {
                sql = "SELECT `no_laporan_produksi` FROM `tb_cetak` WHERE `tb_cetak`.`no_laporan_produksi` = '" + Table_Data_Cabut.getValueAt(j, 0) + "'";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Maaf tidak bisa hapus No LP ini karena sudah masuk QC, mohon hapus data QC terlebih dahulu!");
                } else {
                    int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Warning", 0);
                    if (dialogResult == JOptionPane.YES_OPTION) {
                        // delete code here
                        String Query = "DELETE FROM `tb_cabut` WHERE `tb_cabut`.`no_laporan_produksi` = '" + Table_Data_Cabut.getValueAt(j, 0) + "'";
                        executeSQLQuery(Query, "deleted !");
                        refreshTable_Cabut();
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_delete_cabutActionPerformed

    private void button_edit_cabutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_cabutActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_Data_Cabut.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang akan di edit !");
            } else {
                String no_lp = Table_Data_Cabut.getValueAt(j, 0).toString();
                JDialog_Edit_Data_Cabut edit_cabut = new JDialog_Edit_Data_Cabut(new javax.swing.JFrame(), true, no_lp);
//                JDialog_Edit_Data_Cabut.label_.setText("CABUT Terima LP");
                edit_cabut.pack();
                edit_cabut.setLocationRelativeTo(this);
                edit_cabut.setVisible(true);
                edit_cabut.setEnabled(true);
            }
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, e);
        }

    }//GEN-LAST:event_button_edit_cabutActionPerformed

    private void button_pick_pencabutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_pick_pencabutActionPerformed
        // TODO add your handling code here:
        String filter_tgl = "AND ((`tb_bagian`.`kode_departemen` = 'PRODUKSI' AND DATE(`att_log`.`scan_date`) = CURRENT_DATE()) OR `nama_bagian` LIKE '%-C') ";
        Browse_Karyawan dialog = new Browse_Karyawan(new javax.swing.JFrame(), true, filter_tgl);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);

        int x = Browse_Karyawan.table_list_karyawan.getSelectedRow();
        if (x != -1) {
            txt_id_pegawai.setText(Browse_Karyawan.table_list_karyawan.getValueAt(x, 0).toString());
            txt_nama_pegawai.setText(Browse_Karyawan.table_list_karyawan.getValueAt(x, 1).toString());
            txt_bagian.setText(Browse_Karyawan.table_list_karyawan.getValueAt(x, 2).toString());
        }
    }//GEN-LAST:event_button_pick_pencabutActionPerformed

    private void button_add_pencabutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_add_pencabutActionPerformed
        // TODO add your handling code here:
        decimalFormat.setGroupingUsed(false);
        int x = Table_Data_Cabut.getSelectedRow();
        if (x == -1) {
            JOptionPane.showMessageDialog(this, "Pilih No LP di tabel Cabut terlebih dahulu !");
        } else {
            String no_lp = Table_Data_Cabut.getValueAt(x, 0).toString();
            String ruangan = Table_Data_Cabut.getValueAt(x, 1).toString();
            if (ruangan.length() == 5) {
                JOptionPane.showMessageDialog(this, "Maaf tidak bisa menambahkan pencabut LP sub, kelola data cabut lp sub hanya dilakukan melalui aplikasi sarange");
//                addPencabutSUB(no_lp);
            } else {
                addPencabutWLT(no_lp);
            }
        }
    }//GEN-LAST:event_button_add_pencabutActionPerformed

    private void button_clear_pencabutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_clear_pencabutActionPerformed
        // TODO add your handling code here:
        txt_id_pegawai.setText(null);
        txt_nama_pegawai.setText(null);
        txt_bagian.setText(null);
        Date_pencabut.setDate(null);
        txt_jmlh_keping.setText(null);
    }//GEN-LAST:event_button_clear_pencabutActionPerformed

    private void button_delete_pencabutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_pencabutActionPerformed
        // TODO add your handling code here:
        try {
            int j = table_data_pencabut.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang ingin di hapus !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    String Query = "DELETE FROM `tb_detail_pencabut` WHERE `tb_detail_pencabut`.`nomor` = \'" + table_data_pencabut.getValueAt(j, 0) + "\'";
                    executeSQLQuery(Query, "deleted !");
                    refreshTable_Pencabut();
                    button_clear_pencabut.doClick();
                }
            }
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_delete_pencabutActionPerformed

    private void button_edit_pencabutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_pencabutActionPerformed
        // TODO add your handling code here:
        int x = Table_Data_Cabut.getSelectedRow();
        int j = table_data_pencabut.getSelectedRow();
        try {
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih salah satu data pada tabel !");
            } else {
                int keping_cabut = Integer.valueOf(txt_jmlh_keping.getText());
                float berat_basah = 0, total_keping = 0;
                float gram_cabutan = 0;
                String query = "SELECT `berat_basah`, `jumlah_keping`, `keping_upah` FROM `tb_laporan_produksi` WHERE `no_laporan_produksi` = '" + label_lp.getText() + "'";
                ResultSet result = Utility.db.getStatement().executeQuery(query);
                if (result.next()) {
                    berat_basah = result.getFloat("berat_basah");
                    total_keping = result.getFloat("keping_upah");
                }
                gram_cabutan = (berat_basah / total_keping) * Float.valueOf(txt_jmlh_keping.getText());
//                if (total_keping > 0) {
//                    gram_cabutan = (berat_basah / total_keping) * Float.valueOf(txt_jmlh_keping.getText());
//                } else {
//                    gram_cabutan = (berat_basah / Math.round(berat_basah / 8)) * Float.valueOf(txt_jmlh_keping.getText());
//                    keping_cabut = 0;
//                }

                String nama_grup = null;
                String query_grup = "SELECT `kode_grup` FROM `tb_grup_cabut` WHERE `id_pegawai` = '" + txt_id_pegawai.getText() + "'";
                ResultSet result_grup = Utility.db.getStatement().executeQuery(query_grup);
                if (result_grup.next()) {
                    nama_grup = result_grup.getString("kode_grup");
                } else {
                    nama_grup = "-";
                }

                sql = "UPDATE `tb_detail_pencabut` SET "
                        + "`id_pegawai`='" + txt_id_pegawai.getText() + "',"
                        + "`tanggal_cabut`='" + dateFormat.format(Date_pencabut.getDate()) + "',"
                        + "`jumlah_cabut`='" + keping_cabut + "', "
                        + "`jumlah_gram`='" + decimalFormat.format(gram_cabutan) + "', "
                        + "`grup_cabut`='" + nama_grup + "', "
                        + "`kode_bagian`=(SELECT `kode_bagian` FROM `tb_karyawan` WHERE `id_pegawai` = '" + txt_id_pegawai.getText() + "') "
                        + "WHERE `nomor`='" + table_data_pencabut.getValueAt(j, 0) + "'";
                executeSQLQuery(sql, "updated !");
                refreshTable_Pencabut();
                button_clear_pencabut.doClick();
            }
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_DataCabut.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_edit_pencabutActionPerformed

    private void button_setor_cabutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_setor_cabutActionPerformed
        // TODO add your handling code here:
        int j = Table_Data_Cabut.getSelectedRow();
        float gram_awal = 0;
        float gram_akhir = 0;

        try {
            String a = "SELECT `berat_basah` FROM `tb_laporan_produksi` WHERE `no_laporan_produksi` = '" + Table_Data_Cabut.getValueAt(j, 0) + "'";
            ResultSet result1 = Utility.db.getStatement().executeQuery(a);
            if (result1.next()) {
                gram_awal = result1.getFloat("berat_basah");
            }

            String b = "SELECT SUM(`jumlah_gram`) AS 'total' FROM `tb_detail_pencabut` WHERE `no_laporan_produksi` = '" + Table_Data_Cabut.getValueAt(j, 0) + "'";
            ResultSet result2 = Utility.db.getStatement().executeQuery(b);
            if (result2.next()) {
                gram_akhir = result2.getFloat("total");
            }
        } catch (SQLException ex) {
            gram_awal = -1;
            gram_akhir = 0;
            Logger.getLogger(JPanel_DataCabut.class.getName()).log(Level.SEVERE, null, ex);
        }

        gram_awal = Math.round(gram_awal);
        gram_akhir = Math.round(gram_akhir);
//        System.out.println(gram_awal);
//        System.out.println(gram_akhir);
//        System.out.println(gram_awal==gram_akhir);

        if (j == -1) {
            JOptionPane.showMessageDialog(this, "Anda belum memilih LP yang akan di setorkan !");
        } else {
            if (Table_Data_Cabut.getValueAt(j, 11) != "-" && Table_Data_Cabut.getValueAt(j, 10) != null) {
                JOptionPane.showMessageDialog(this, "No Laporan Produksi : " + Table_Data_Cabut.getValueAt(j, 0).toString() + "\n Sudah disetor");
            } else if (!Table_Data_Cabut.getValueAt(j, 9).toString().equals(label_total_cabutan.getText())) {
                JOptionPane.showMessageDialog(this, "Maaf, data keping yang sudah dicabut dan jumlah keping LP tidak sesuai\nSilahkan Cek Kembali !");
            } else if (gram_awal != gram_akhir) {
                JOptionPane.showMessageDialog(this, "Maaf, data Gram LP dan Gram Cabutan tidak sesuai\nSilahkan Cek Kembali !");
            } else {
                int i = Table_Data_Cabut.getSelectedRow();
                String no_lp = Table_Data_Cabut.getValueAt(i, 0).toString();
                JDialog_Setor_LP_Cabut setor_lp = new JDialog_Setor_LP_Cabut(new javax.swing.JFrame(), true, no_lp);
                setor_lp.pack();
                setor_lp.setLocationRelativeTo(this);
                setor_lp.setVisible(true);
                setor_lp.setEnabled(true);
            }
        }

    }//GEN-LAST:event_button_setor_cabutActionPerformed

    private void button_export_data_cabutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_data_cabutActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_Data_Cabut.getModel();
        ExportToExcel.writeToExcel(model, jPanel2);
    }//GEN-LAST:event_button_export_data_cabutActionPerformed

    private void button_tandon_cabutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_tandon_cabutActionPerformed
        // TODO add your handling code here:
        String ruang = ComboBox_ruang.getSelectedItem().toString();
        JFrame_Tampilan_Proses_LP2 frame = new JFrame_Tampilan_Proses_LP2(ruang);
        frame.pack();
        frame.setLocationRelativeTo(this);
        frame.setVisible(true);
        frame.setEnabled(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }//GEN-LAST:event_button_tandon_cabutActionPerformed

    private void button_print_laporanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_print_laporanActionPerformed
        // TODO add your handling code here:
        try {
            String ruang = ComboBox_ruang.getSelectedItem().toString();
            if (ruang.equals("All")) {
                ruang = "";
            }
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Laporan_cabut.jrxml");
            Map<String, Object> map = new HashMap<>();
            map.put("SUBREPORT_DIR", "Report\\");
            map.put("ruang", ruang);//parameter name should be like it was named inside your report.
            map.put("tgl_lp", dateFormat.format(Date1_cabut.getDate()));//parameter name should be like it was named inside your report.
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
        } catch (JRException ex) {
            Logger.getLogger(JPanel_DataCabut.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_print_laporanActionPerformed

    private void button_rank_pencabutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_rank_pencabutActionPerformed
        // TODO add your handling code here:
        JFrame_Rank_Cabut frame = new JFrame_Rank_Cabut();
        frame.pack();
        frame.setLocationRelativeTo(this);
        frame.setVisible(true);
        frame.setEnabled(true);
        frame.init();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }//GEN-LAST:event_button_rank_pencabutActionPerformed

    private void button_terima_cabut_lp_omActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_terima_cabut_lp_omActionPerformed
        // TODO add your handling code here:
        JDialog_Terima_LP_Cabut_OM terima_lp = new JDialog_Terima_LP_Cabut_OM(new javax.swing.JFrame(), true);
        terima_lp.pack();
        terima_lp.setLocationRelativeTo(this);
        terima_lp.setVisible(true);
        terima_lp.setEnabled(true);
    }//GEN-LAST:event_button_terima_cabut_lp_omActionPerformed

    private void button_setor_cabut_lp_omActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_setor_cabut_lp_omActionPerformed
        // TODO add your handling code here:
        int j = Table_Data_Cabut.getSelectedRow();
        if (j == -1) {
            JOptionPane.showMessageDialog(this, "Anda belum memilih LP yang akan di setorkan !");
        } else {
            boolean check = true;
            String ruangan = Table_Data_Cabut.getValueAt(j, 1).toString();
            if (Table_Data_Cabut.getValueAt(j, 11) != "-" && Table_Data_Cabut.getValueAt(j, 10) != null) {
                JOptionPane.showMessageDialog(this, "No Laporan Produksi : " + Table_Data_Cabut.getValueAt(j, 0).toString() + "\n Sudah disetor");
            } else if (!ruangan.equals("C") && !ruangan.equals("CABUTO")) {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Ruangan bukan merupakan ruangan CABUTO\nApakah yakin ingin dilanjutkan?", "Warning", 0);
                check = dialogResult == JOptionPane.YES_OPTION;
            }

            if (check) {
                try {
                    // Get the value of no_lp from the table
                    String no_lp = Table_Data_Cabut.getValueAt(j, 0).toString();

                    // Define the SQL query using a prepared statement
                    String Query = "UPDATE `tb_cabut` SET `tgl_setor_cabut`=CURRENT_DATE WHERE `no_laporan_produksi`=?";

                    // Get the database connection and prepare the statement
                    Connection conn = Utility.db.getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(Query);

                    // Set the value for the placeholder (?)
                    pstmt.setString(1, no_lp);

                    // Execute the update
                    int rowsAffected = pstmt.executeUpdate();

                    // Check if the update was successful
                    if (rowsAffected == 1) {
                        JOptionPane.showMessageDialog(this, "LP berhasil disetorkan");
                        refreshTable_Cabut();
                    } else {
                        JOptionPane.showMessageDialog(this, "Data failed!");
                    }

                    // Close the prepared statement and the connection
                    pstmt.close();
//                    conn.close();
                } catch (SQLException e) {
                    // Handle any SQL exceptions
                    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
                }
            }
        }
    }//GEN-LAST:event_button_setor_cabut_lp_omActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_filterDate;
    private javax.swing.JComboBox<String> ComboBox_ruang;
    private com.toedter.calendar.JDateChooser Date1_cabut;
    private com.toedter.calendar.JDateChooser Date2_cabut;
    private com.toedter.calendar.JDateChooser Date_pencabut;
    private javax.swing.JTable Table_Data_Cabut;
    public javax.swing.JButton button_add_pencabut;
    private javax.swing.JButton button_clear_pencabut;
    public javax.swing.JButton button_delete_cabut;
    public javax.swing.JButton button_delete_pencabut;
    public javax.swing.JButton button_edit_cabut;
    public javax.swing.JButton button_edit_pencabut;
    private javax.swing.JButton button_export_data_cabut;
    private javax.swing.JButton button_pick_pencabut;
    private javax.swing.JButton button_print_laporan;
    private javax.swing.JButton button_rank_pencabut;
    public static javax.swing.JButton button_search_cabut;
    public javax.swing.JButton button_setor_cabut;
    public javax.swing.JButton button_setor_cabut_lp_om;
    public static javax.swing.JButton button_tandon_cabut;
    public javax.swing.JButton button_terima_cabut;
    public javax.swing.JButton button_terima_cabut_lp_om;
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel label_lp;
    private javax.swing.JLabel label_total_cabutan;
    private javax.swing.JLabel label_total_cabutan1;
    private javax.swing.JLabel label_total_cabutan2;
    private javax.swing.JLabel label_total_cabutan3;
    private javax.swing.JLabel label_total_data_cabut;
    private javax.swing.JLabel label_total_gram;
    public static javax.swing.JLabel label_total_hari;
    private javax.swing.JLabel label_total_kpg;
    private javax.swing.JLabel label_total_pencabut;
    private javax.swing.JTable table_data_pencabut;
    private javax.swing.JTextField txt_bagian;
    private javax.swing.JTextField txt_id_pegawai;
    private javax.swing.JTextField txt_jmlh_keping;
    private javax.swing.JTextField txt_nama_pegawai;
    private javax.swing.JTextField txt_search_cabut;
    // End of variables declaration//GEN-END:variables

}
