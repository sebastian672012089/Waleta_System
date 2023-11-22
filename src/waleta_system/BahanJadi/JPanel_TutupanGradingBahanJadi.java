package waleta_system.BahanJadi;

import java.awt.Color;
import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
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
import waleta_system.BahanBaku.JPanel_GradeBahanBaku;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;
import waleta_system.Class.DataTutupanGrading;
import waleta_system.Class.ExportToExcel;

public class JPanel_TutupanGradingBahanJadi extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    int row;

    public JPanel_TutupanGradingBahanJadi() {
        initComponents();
    }

    public void init() {
        try {
            refreshTable_Tutupan();
            Table_TutupanGrading.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && Table_TutupanGrading.getSelectedRow() != -1) {
                        row = Table_TutupanGrading.getSelectedRow();
                        label_KodeAsal.setText(Table_TutupanGrading.getValueAt(row, 0).toString());
                        refreshTable_Asal();
                        refreshTable_HasilGrading();
                        refreshTable_RincianLP();

                        if (Table_TutupanGrading.getValueAt(row, 7).toString().equals("PROSES")) {
                            button_edit_tutupan.setEnabled(true);
                            button_selesai_tutupan.setEnabled(true);
                            button_selesai_box.setEnabled(false);
                        } else {
                            button_edit_tutupan.setEnabled(false);
                            button_selesai_tutupan.setEnabled(false);
                            if (Table_TutupanGrading.getValueAt(row, 8).toString().equals("PROSES")) {
                                button_selesai_box.setEnabled(true);
                            } else {
                                button_selesai_box.setEnabled(false);
                            }
                        }

                    }
                }
            });
            Table_Hasil_grading.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && Table_Hasil_grading.getSelectedRow() != -1) {
                        int i = Table_Hasil_grading.getSelectedRow();
                        if (i >= 0) {
                            refreshTable_RincianLP();
                            refreshTable_rincianBox(Table_Hasil_grading.getValueAt(i, 0).toString());
                            if (Table_RincianBox.getRowCount() > 0) {
                                button_add_box.setEnabled(false);
                                button_edit_box.setEnabled(true);
                            } else {
                                button_add_box.setEnabled(true);
                                button_edit_box.setEnabled(false);
                            }
                        }
                    }
                }
            });
            Table_Detail_Asal.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && Table_Detail_Asal.getSelectedRow() != -1) {
                        int i = Table_Detail_Asal.getSelectedRow();
                        if (i >= 0) {

                        }
                    }
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(JPanel_GradeBahanBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList<DataTutupanGrading> DataTutupanGradingList() {
        ArrayList<DataTutupanGrading> DataTutupanGradingList = new ArrayList<>();

        return DataTutupanGradingList;
    }

    public void refreshTable_Tutupan() {
        try {
            double total_gram = 0, total_kpg = 0, total_asal = 0;
            DefaultTableModel model = (DefaultTableModel) Table_TutupanGrading.getModel();
            model.setRowCount(0);
            String filter_tanggal = "";
            if (Date_filter1.getDate() != null && Date_filter2.getDate() != null) {
                filter_tanggal = "AND `tgl_selesai_tutupan` BETWEEN '" + dateFormat.format(Date_filter1.getDate()) + "' AND '" + dateFormat.format(Date_filter2.getDate()) + "' ";
            }
            sql = "SELECT `tb_tutupan_grading`.`kode_tutupan`, `tgl_mulai_tutupan`, `tgl_selesai_tutupan`, `nama_rumah_burung`, COUNT(`tb_bahan_jadi_masuk`.`kode_tutupan`) AS 'asal', SUM(`tb_bahan_jadi_masuk`.`keping`) AS 'jumlah_keping', SUM(`tb_bahan_jadi_masuk`.`berat`) AS 'jumlah_berat', `status`, `status_box`, `tgl_statusBox`, `memo_tutupan`\n"
                    + "FROM `tb_tutupan_grading` \n"
                    + "LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_tutupan_grading`.`kode_tutupan` = `tb_bahan_jadi_masuk`.`kode_tutupan`\n"
                    + "LEFT JOIN `tb_rumah_burung` ON `tb_tutupan_grading`.`kode_rumah_burung` = `tb_rumah_burung`.`no_registrasi` \n"
                    + "WHERE "
                    + "`tb_tutupan_grading`.`kode_tutupan` LIKE '%" + txt_search_kodeTutupan.getText() + "%' "
                    + filter_tanggal
                    + "GROUP BY `tb_tutupan_grading`.`kode_tutupan` "
                    + "ORDER BY `tgl_mulai_tutupan` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] baris = new Object[15];
            while (rs.next()) {
                baris[0] = rs.getString("kode_tutupan");
                baris[1] = rs.getDate("tgl_mulai_tutupan");
                baris[2] = rs.getDate("tgl_selesai_tutupan");
                baris[3] = rs.getString("nama_rumah_burung");
                baris[4] = rs.getInt("asal");
                baris[5] = rs.getInt("jumlah_keping");
                baris[6] = rs.getInt("jumlah_berat");
                baris[7] = rs.getString("status");
                baris[8] = rs.getString("status_box");
                baris[9] = rs.getDate("tgl_statusBox");
                baris[10] = rs.getString("memo_tutupan");
                String filter_status = ComboBox_FilterStatus.getSelectedItem().toString();
                if (filter_status.equals("All")) {
                    total_asal = total_asal + rs.getInt("asal");
                    total_kpg = total_kpg + rs.getInt("jumlah_keping");
                    total_gram = total_gram + rs.getInt("jumlah_berat");
                    model.addRow(baris);
                } else if (filter_status.equals("Proses - Proses") && rs.getString("status").equals("PROSES") && rs.getString("status_box").equals("PROSES")) {
                    total_asal = total_asal + rs.getInt("asal");
                    total_kpg = total_kpg + rs.getInt("jumlah_keping");
                    total_gram = total_gram + rs.getInt("jumlah_berat");
                    model.addRow(baris);
                } else if (filter_status.equals("Selesai - Proses") && rs.getString("status").equals("SELESAI") && rs.getString("status_box").equals("PROSES")) {
                    total_asal = total_asal + rs.getInt("asal");
                    total_kpg = total_kpg + rs.getInt("jumlah_keping");
                    total_gram = total_gram + rs.getInt("jumlah_berat");
                    model.addRow(baris);
                } else if (filter_status.equals("Selesai - Selesai") && rs.getString("status").equals("SELESAI") && rs.getString("status_box").equals("SELESAI")) {
                    total_asal = total_asal + rs.getInt("asal");
                    total_kpg = total_kpg + rs.getInt("jumlah_keping");
                    total_gram = total_gram + rs.getInt("jumlah_berat");
                    model.addRow(baris);
                }
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_TutupanGrading);

            final Color green = new Color(0, 145, 0);
            Table_TutupanGrading.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (column == 7 || column == 8) {
                        if (Table_TutupanGrading.getValueAt(row, column).toString().equals("SELESAI")) {
                            if (isSelected) {
                                comp.setBackground(Table_TutupanGrading.getSelectionBackground());
                                comp.setForeground(green);
                            } else {
                                comp.setBackground(Table_TutupanGrading.getBackground());
                                comp.setForeground(green);
                            }
                        } else {
                            if (isSelected) {
                                comp.setBackground(Table_TutupanGrading.getSelectionBackground());
                                comp.setForeground(Color.red);
                            } else {
                                comp.setBackground(Table_TutupanGrading.getBackground());
                                comp.setForeground(Color.red);
                            }
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(Table_TutupanGrading.getSelectionBackground());
                            comp.setForeground(Table_TutupanGrading.getSelectionForeground());
                        } else {
                            comp.setBackground(Table_TutupanGrading.getBackground());
                            comp.setForeground(Table_TutupanGrading.getForeground());
                        }
                    }
                    return comp;
                }
            });
            Table_TutupanGrading.repaint();

            decimalFormat.setMaximumFractionDigits(0);
            int rowData = Table_TutupanGrading.getRowCount();
            label_total_data.setText(decimalFormat.format(rowData));
            label_jumlah_lp.setText(decimalFormat.format(total_asal));
            label_total_berat.setText(decimalFormat.format(total_gram));
            label_total_keping.setText(decimalFormat.format(total_kpg));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_TutupanGradingBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_Asal() {
        try {
            int total_keping = 0, total_berat = 0;
            DefaultTableModel model = (DefaultTableModel) Table_Detail_Asal.getModel();
            model.setRowCount(0);
            int i = Table_TutupanGrading.getSelectedRow();
            if (i >= 0) {
                sql = "SELECT `kode_asal`, `tanggal_masuk`, `keping`, `berat`, `tanggal_grading`, `kode_tutupan`, (`tb_finishing_2`.`jidun_utuh_f2` +  `tb_finishing_2`.`jidun_pecah_f2`) AS 'jidun' "
                        + "FROM `tb_bahan_jadi_masuk` LEFT JOIN `tb_finishing_2` ON `tb_bahan_jadi_masuk`.`kode_asal` = `tb_finishing_2`.`no_laporan_produksi`"
                        + "WHERE `kode_tutupan` = '" + Table_TutupanGrading.getValueAt(i, 0) + "'";
                rs = Utility.db.getStatement().executeQuery(sql);
                Object[] baris = new Object[6];
                while (rs.next()) {
                    baris[0] = rs.getString("kode_asal");
                    baris[1] = rs.getDate("tanggal_masuk");
                    baris[2] = rs.getDate("tanggal_grading");
                    baris[3] = rs.getInt("keping");
                    total_keping = total_keping + rs.getInt("keping");
                    baris[4] = rs.getInt("berat");
                    total_berat = total_berat + rs.getInt("berat");
                    baris[5] = rs.getInt("jidun");
                    model.addRow(baris);
                }
                ColumnsAutoSizer.sizeColumnsToFit(Table_Detail_Asal);
                int total_data = Table_Detail_Asal.getRowCount();
                label_total_asal_detail_asal.setText(decimalFormat.format(total_data));
                label_total_kpg_detail_asal.setText(decimalFormat.format(total_keping));
                label_total_gram_detail_asal.setText(decimalFormat.format(total_berat));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_TutupanGradingBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_HasilGrading() {
        int i = Table_TutupanGrading.getSelectedRow();
        if (i >= 0) {
            try {
                int total_box = 0, total_keping = 0;
                float total_berat = 0;
                DefaultTableModel model = (DefaultTableModel) Table_Hasil_grading.getModel();
                model.setRowCount(0);
                sql = "SELECT `kode_tutupan`, `tb_grade_bahan_jadi`.`kode_grade`, SUM(`tb_grading_bahan_jadi`.`keping`) AS 'keping_grading', SUM(`tb_grading_bahan_jadi`.`gram`) AS 'gram_grading'\n"
                        + "FROM `tb_bahan_jadi_masuk` \n"
                        + "JOIN `tb_grading_bahan_jadi` ON `tb_bahan_jadi_masuk`.`kode_asal` = `tb_grading_bahan_jadi`.`kode_asal_bahan_jadi`\n"
                        + "JOIN `tb_grade_bahan_jadi` ON `tb_grading_bahan_jadi`.`grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`\n"
                        + "WHERE `kode_tutupan` LIKE '%" + label_KodeAsal.getText() + "%' "
                        + "GROUP BY `tb_grading_bahan_jadi`.`grade_bahan_jadi`";
                PreparedStatement pst = Utility.db.getConnection().prepareStatement(sql);
                rs = pst.executeQuery();
                Object[] baris = new Object[6];
                while (rs.next()) {
                    baris[0] = rs.getString("kode_grade");
                    baris[1] = rs.getInt("keping_grading");
                    total_keping = total_keping + rs.getInt("keping_grading");
                    baris[2] = rs.getFloat("gram_grading");
                    total_berat = total_berat + rs.getFloat("gram_grading");

                    String query = "SELECT SUM(`berat`) AS 'gram', COUNT(`no_box`) AS 'jumlah_box' FROM `tb_box_bahan_jadi` WHERE `no_tutupan` = '" + label_KodeAsal.getText() + "'"
                            + " AND `kode_grade_bahan_jadi` = (SELECT `kode` FROM `tb_grade_bahan_jadi` WHERE `kode_grade` = '" + rs.getString("kode_grade") + "')";
                    ResultSet result = Utility.db.getStatement().executeQuery(query);
                    if (result.next()) {
                        baris[3] = result.getInt("jumlah_box");
                        baris[4] = result.getFloat("gram");
                        baris[5] = Math.round(((rs.getFloat("gram_grading") - result.getFloat("gram")) / rs.getFloat("gram_grading")) * 10000.f) / 100.f;
                        total_box = total_box + result.getInt("jumlah_box");
                    }

                    model.addRow(baris);
                }
                ColumnsAutoSizer.sizeColumnsToFit(Table_Hasil_grading);

                int total_data = Table_Hasil_grading.getRowCount();
                label_total_grade_hasil_grading.setText(decimalFormat.format(total_data));
                label_total_kpg_hasil_grading.setText(decimalFormat.format(total_keping));
                label_total_gram_hasil_grading.setText(decimalFormat.format(total_berat));
                label_total_box_hasil_grading.setText(decimalFormat.format(total_box));
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex);
                Logger.getLogger(JPanel_TutupanGradingBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void refreshTable_RincianLP() {
        int i = Table_Hasil_grading.getSelectedRow();
        if (i >= 0) {
            try {
                int total_kpg = 0;
                float total_gram = 0;
                DefaultTableModel model = (DefaultTableModel) Table_RincianLP.getModel();
                model.setRowCount(0);
                sql = "SELECT `kode_asal_bahan_jadi`, `tb_grading_bahan_jadi`.`keping`, `tb_grading_bahan_jadi`.`gram` FROM `tb_grading_bahan_jadi` JOIN `tb_bahan_jadi_masuk` ON `tb_grading_bahan_jadi`.`kode_asal_bahan_jadi` = `tb_bahan_jadi_masuk`.`kode_asal`"
                        + "WHERE `kode_tutupan` = '" + label_KodeAsal.getText() + "' AND `grade_bahan_jadi` = (SELECT `kode` FROM `tb_grade_bahan_jadi` WHERE `kode_grade` = '" + Table_Hasil_grading.getValueAt(i, 0) + "')";
                rs = Utility.db.getStatement().executeQuery(sql);
                Object[] baris = new Object[3];
                while (rs.next()) {
                    baris[0] = rs.getString("kode_asal_bahan_jadi");
                    baris[1] = rs.getInt("keping");
                    total_kpg = total_kpg + rs.getInt("keping");
                    baris[2] = rs.getInt("gram");
                    total_gram = total_gram + rs.getInt("gram");
                    model.addRow(baris);
                }
                ColumnsAutoSizer.sizeColumnsToFit(Table_RincianLP);

                int total_data = Table_RincianLP.getRowCount();
                label_total_asal_rincianLP.setText(decimalFormat.format(total_data));
                label_total_kpg_rincianLP.setText(decimalFormat.format(total_kpg));
                label_total_gram_rincianLP.setText(decimalFormat.format(total_gram));
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex);
                Logger.getLogger(JPanel_TutupanGradingBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void refreshTable_rincianBox(String kode_grade) {
        try {
            int total_kpg = 0;
            float total_gram = 0;
            DefaultTableModel model = (DefaultTableModel) Table_RincianBox.getModel();
            model.setRowCount(0);
            Object[] baris = new Object[4];
            sql = "SELECT * FROM `tb_box_bahan_jadi` WHERE `no_tutupan` = '" + label_KodeAsal.getText() + "' "
                    + "AND `kode_grade_bahan_jadi` = (SELECT `kode` FROM `tb_grade_bahan_jadi` WHERE `kode_grade` = '" + kode_grade + "')";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                baris[0] = rs.getString("no_box");
                baris[1] = rs.getInt("keping");
                baris[2] = rs.getFloat("berat");
                baris[3] = rs.getString("lokasi_terakhir");
                model.addRow(baris);

                total_kpg += rs.getInt("keping");
                total_gram += rs.getFloat("berat");
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_RincianBox);
            int total_data = Table_RincianBox.getRowCount();
            label_total_rincian_box.setText(decimalFormat.format(total_data));
            label_total_kpg_rincianBox.setText(decimalFormat.format(total_kpg));
            label_total_gram_rincianBox.setText(decimalFormat.format(total_gram));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_TutupanGradingBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
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
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_TutupanGradingBahanJadi.class.getName()).log(Level.SEVERE, null, e);
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

        jPopupMenu_TB_Tutupan = new javax.swing.JPopupMenu();
        jMenuItem_Box = new javax.swing.JMenuItem();
        jPanel1 = new javax.swing.JPanel();
        jPanel_Data_Tutupan = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txt_search_kodeTutupan = new javax.swing.JTextField();
        button_search = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        Date_filter1 = new com.toedter.calendar.JDateChooser();
        Date_filter2 = new com.toedter.calendar.JDateChooser();
        button_tambah_data = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_TutupanGrading = new javax.swing.JTable();
        label_total_data = new javax.swing.JLabel();
        label_total_keping = new javax.swing.JLabel();
        label_total_berat = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        button_export_Tutupan = new javax.swing.JButton();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        Table_RincianLP = new javax.swing.JTable();
        label_total_kpg_rincianLP = new javax.swing.JLabel();
        label_total_asal_rincianLP = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        label_total_gram_rincianLP = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        Table_RincianBox = new javax.swing.JTable();
        label_total_gram_rincianBox = new javax.swing.JLabel();
        label_total_kpg_rincianBox = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        label_total_rincian_box = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        button_add_box = new javax.swing.JButton();
        button_edit_box = new javax.swing.JButton();
        button_edit_tutupan = new javax.swing.JButton();
        button_print = new javax.swing.JButton();
        button_selesai_tutupan = new javax.swing.JButton();
        button_selesai_box = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        ComboBox_FilterStatus = new javax.swing.JComboBox<>();
        label_jumlah_lp = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        button_tambah_data1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        label_KodeAsal = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        Table_Hasil_grading = new javax.swing.JTable();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        label_total_kpg_hasil_grading = new javax.swing.JLabel();
        label_total_gram_hasil_grading = new javax.swing.JLabel();
        label_total_box_hasil_grading = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        label_total_grade_hasil_grading = new javax.swing.JLabel();
        button_export_gradingTutupan = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        Table_Detail_Asal = new javax.swing.JTable();
        label_total_asal_detail_asal = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        label_total_kpg_detail_asal = new javax.swing.JLabel();
        label_total_gram_detail_asal = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        button_export_LPTutupan = new javax.swing.JButton();
        button_tambah_data2 = new javax.swing.JButton();

        jMenuItem_Box.setText("Lihat Data Box");
        jMenuItem_Box.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_BoxActionPerformed(evt);
            }
        });
        jPopupMenu_TB_Tutupan.add(jMenuItem_Box);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Tutupan Grading", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        jPanel_Data_Tutupan.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Kode Tutupan Grading :");

        txt_search_kodeTutupan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_kodeTutupan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_kodeTutupanKeyPressed(evt);
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
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Tanggal selesai Tutupan :");

        Date_filter1.setBackground(new java.awt.Color(255, 255, 255));
        Date_filter1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_filter2.setBackground(new java.awt.Color(255, 255, 255));
        Date_filter2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_tambah_data.setBackground(new java.awt.Color(255, 255, 255));
        button_tambah_data.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_tambah_data.setText("+ Tutupan GRD");
        button_tambah_data.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_tambah_dataActionPerformed(evt);
            }
        });

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Total Data Tutupan :");

        Table_TutupanGrading.setAutoCreateRowSorter(true);
        Table_TutupanGrading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_TutupanGrading.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Tutupan", "Tgl Mulai", "Tgl Selesai", "Rumah Burung", "Jumlah LP", "Keping", "Berat", "Status", "Status Box", "Tgl Selesai Box", "Memo"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_TutupanGrading.getTableHeader().setReorderingAllowed(false);
        Table_TutupanGrading.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Table_TutupanGradingMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(Table_TutupanGrading);

        label_total_data.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_data.setText("0");

        label_total_keping.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_keping.setText("0");

        label_total_berat.setBackground(new java.awt.Color(255, 255, 255));
        label_total_berat.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_berat.setText("0");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Gram :");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Keping :");

        button_export_Tutupan.setBackground(new java.awt.Color(255, 255, 255));
        button_export_Tutupan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_Tutupan.setText("Export");
        button_export_Tutupan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_TutupanActionPerformed(evt);
            }
        });

        jTabbedPane2.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        Table_RincianLP.setAutoCreateRowSorter(true);
        Table_RincianLP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_RincianLP.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No. LP / Kode", "Keping", "Gram"
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
        jScrollPane7.setViewportView(Table_RincianLP);

        label_total_kpg_rincianLP.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg_rincianLP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_kpg_rincianLP.setText("0");

        label_total_asal_rincianLP.setBackground(new java.awt.Color(255, 255, 255));
        label_total_asal_rincianLP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_asal_rincianLP.setText("0");

        jLabel48.setBackground(new java.awt.Color(255, 255, 255));
        jLabel48.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel48.setText("Keping :");

        jLabel29.setBackground(new java.awt.Color(255, 255, 255));
        jLabel29.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel29.setText("Total LP / Beli :");

        jLabel49.setBackground(new java.awt.Color(255, 255, 255));
        jLabel49.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel49.setText("Gram :");

        label_total_gram_rincianLP.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_rincianLP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_gram_rincianLP.setText("0");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_asal_rincianLP)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel48)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_kpg_rincianLP)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel49)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_rincianLP)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 558, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(label_total_asal_rincianLP)
                    .addComponent(jLabel48)
                    .addComponent(jLabel49)
                    .addComponent(label_total_kpg_rincianLP)
                    .addComponent(label_total_gram_rincianLP))
                .addContainerGap())
        );

        jTabbedPane2.addTab("Rincian LP dari Grading", jPanel3);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        Table_RincianBox.setAutoCreateRowSorter(true);
        Table_RincianBox.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_RincianBox.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Box", "Keping", "Gram", "Lokasi"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Float.class, java.lang.String.class
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
        jScrollPane8.setViewportView(Table_RincianBox);

        label_total_gram_rincianBox.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_rincianBox.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_gram_rincianBox.setText("0");

        label_total_kpg_rincianBox.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg_rincianBox.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_kpg_rincianBox.setText("0");

        jLabel50.setBackground(new java.awt.Color(255, 255, 255));
        jLabel50.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel50.setText("Keping :");

        label_total_rincian_box.setBackground(new java.awt.Color(255, 255, 255));
        label_total_rincian_box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_rincian_box.setText("0");

        jLabel51.setBackground(new java.awt.Color(255, 255, 255));
        jLabel51.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel51.setText("Gram :");

        jLabel30.setBackground(new java.awt.Color(255, 255, 255));
        jLabel30.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel30.setText("Total Box :");

        button_add_box.setBackground(new java.awt.Color(255, 255, 255));
        button_add_box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_add_box.setText("Add Box");
        button_add_box.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_add_boxActionPerformed(evt);
            }
        });

        button_edit_box.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_edit_box.setText("Edit");
        button_edit_box.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_boxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel30)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_rincian_box)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel50)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_kpg_rincianBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel51)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_rincianBox))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(button_edit_box)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_add_box)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 532, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_add_box, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_edit_box, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(label_total_rincian_box)
                    .addComponent(jLabel50)
                    .addComponent(jLabel51)
                    .addComponent(label_total_kpg_rincianBox)
                    .addComponent(label_total_gram_rincianBox))
                .addContainerGap())
        );

        jTabbedPane2.addTab("Rincian Box", jPanel4);

        button_edit_tutupan.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_tutupan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_edit_tutupan.setText("Edit");
        button_edit_tutupan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_tutupanActionPerformed(evt);
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

        button_selesai_tutupan.setBackground(new java.awt.Color(255, 255, 255));
        button_selesai_tutupan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_selesai_tutupan.setText("Selesai Tutupan");
        button_selesai_tutupan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_selesai_tutupanActionPerformed(evt);
            }
        });

        button_selesai_box.setBackground(new java.awt.Color(255, 255, 255));
        button_selesai_box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_selesai_box.setText("Selesai Box");
        button_selesai_box.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_selesai_boxActionPerformed(evt);
            }
        });

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Status :");

        ComboBox_FilterStatus.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_FilterStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Proses - Proses", "Selesai - Proses", "Selesai - Selesai" }));

        label_jumlah_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_jumlah_lp.setText("0");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Total LP :");

        button_tambah_data1.setBackground(new java.awt.Color(255, 255, 255));
        button_tambah_data1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_tambah_data1.setText("+ Tutupan PBJ");
        button_tambah_data1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_tambah_data1ActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel14.setText("Kode Tutupan Grading :");

        label_KodeAsal.setBackground(new java.awt.Color(255, 255, 255));
        label_KodeAsal.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_KodeAsal.setText("KODE");

        Table_Hasil_grading.setAutoCreateRowSorter(true);
        Table_Hasil_grading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_Hasil_grading.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade Bahan Jadi", "Keping", "Gram", "Tot Box", "Gr Box", "Susut"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class
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
        Table_Hasil_grading.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(Table_Hasil_grading);

        jLabel34.setBackground(new java.awt.Color(255, 255, 255));
        jLabel34.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel34.setText("Keping :");

        jLabel35.setBackground(new java.awt.Color(255, 255, 255));
        jLabel35.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel35.setText("Gram :");

        label_total_kpg_hasil_grading.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg_hasil_grading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_kpg_hasil_grading.setText("0");

        label_total_gram_hasil_grading.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_hasil_grading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_gram_hasil_grading.setText("0");

        label_total_box_hasil_grading.setBackground(new java.awt.Color(255, 255, 255));
        label_total_box_hasil_grading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_box_hasil_grading.setText("0");

        jLabel38.setBackground(new java.awt.Color(255, 255, 255));
        jLabel38.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel38.setText("Box :");

        jLabel33.setBackground(new java.awt.Color(255, 255, 255));
        jLabel33.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel33.setText("Total Grade :");

        label_total_grade_hasil_grading.setBackground(new java.awt.Color(255, 255, 255));
        label_total_grade_hasil_grading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_grade_hasil_grading.setText("0");

        button_export_gradingTutupan.setBackground(new java.awt.Color(255, 255, 255));
        button_export_gradingTutupan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_gradingTutupan.setText("Export");
        button_export_gradingTutupan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_gradingTutupanActionPerformed(evt);
            }
        });

        Table_Detail_Asal.setAutoCreateRowSorter(true);
        Table_Detail_Asal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_Detail_Asal.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP / Kode Pembelian", "Tgl Masuk", "Tgl Grading", "Kpg", "Gram", "Jidun"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class
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
        Table_Detail_Asal.setSelectionBackground(new java.awt.Color(153, 204, 255));
        Table_Detail_Asal.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(Table_Detail_Asal);

        label_total_asal_detail_asal.setBackground(new java.awt.Color(255, 255, 255));
        label_total_asal_detail_asal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_asal_detail_asal.setText("0");

        jLabel36.setBackground(new java.awt.Color(255, 255, 255));
        jLabel36.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel36.setText("Keping :");

        label_total_kpg_detail_asal.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg_detail_asal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_kpg_detail_asal.setText("0");

        label_total_gram_detail_asal.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_detail_asal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_gram_detail_asal.setText("0");

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel28.setText("Total LP / Beli :");

        jLabel37.setBackground(new java.awt.Color(255, 255, 255));
        jLabel37.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel37.setText("Gram :");

        button_export_LPTutupan.setBackground(new java.awt.Color(255, 255, 255));
        button_export_LPTutupan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_LPTutupan.setText("Export");
        button_export_LPTutupan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_LPTutupanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_KodeAsal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_export_gradingTutupan))
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel33)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_grade_hasil_grading)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel34)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_kpg_hasil_grading)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel35)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_hasil_grading)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel38)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_box_hasil_grading)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_asal_detail_asal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel36)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_kpg_detail_asal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel37)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_detail_asal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 142, Short.MAX_VALUE)
                        .addComponent(button_export_LPTutupan)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_KodeAsal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_gradingTutupan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel34)
                    .addComponent(jLabel35)
                    .addComponent(label_total_kpg_hasil_grading)
                    .addComponent(label_total_gram_hasil_grading)
                    .addComponent(label_total_box_hasil_grading)
                    .addComponent(jLabel38)
                    .addComponent(jLabel33)
                    .addComponent(label_total_grade_hasil_grading))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_total_asal_detail_asal)
                    .addComponent(jLabel36)
                    .addComponent(label_total_kpg_detail_asal)
                    .addComponent(label_total_gram_detail_asal)
                    .addComponent(jLabel28)
                    .addComponent(jLabel37)
                    .addComponent(button_export_LPTutupan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14))
        );

        button_tambah_data2.setBackground(new java.awt.Color(255, 255, 255));
        button_tambah_data2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_tambah_data2.setText("+ Tutupan EST");
        button_tambah_data2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_tambah_data2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_Data_TutupanLayout = new javax.swing.GroupLayout(jPanel_Data_Tutupan);
        jPanel_Data_Tutupan.setLayout(jPanel_Data_TutupanLayout);
        jPanel_Data_TutupanLayout.setHorizontalGroup(
            jPanel_Data_TutupanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Data_TutupanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Data_TutupanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_Data_TutupanLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_kodeTutupan, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_filter1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_filter2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_FilterStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel_Data_TutupanLayout.createSequentialGroup()
                        .addGroup(jPanel_Data_TutupanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_Data_TutupanLayout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_jumlah_lp)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_keping)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_berat))
                            .addGroup(jPanel_Data_TutupanLayout.createSequentialGroup()
                                .addComponent(button_selesai_box)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_selesai_tutupan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_print)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_Tutupan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_edit_tutupan))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 605, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel_Data_TutupanLayout.createSequentialGroup()
                                .addComponent(button_tambah_data)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_tambah_data1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_tambah_data2)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        jPanel_Data_TutupanLayout.setVerticalGroup(
            jPanel_Data_TutupanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_Data_TutupanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Data_TutupanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_kodeTutupan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_filter1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_filter2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_FilterStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel_Data_TutupanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_Data_TutupanLayout.createSequentialGroup()
                        .addGroup(jPanel_Data_TutupanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(button_edit_tutupan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel_Data_TutupanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(button_export_Tutupan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(button_print, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(button_selesai_tutupan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(button_selesai_box, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_Data_TutupanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(button_tambah_data, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_tambah_data1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_tambah_data2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_Data_TutupanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_Data_TutupanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(label_total_keping, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(label_total_berat, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel_Data_TutupanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(label_jumlah_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel_Data_TutupanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(label_total_data, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTabbedPane2))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jPanel_Data_Tutupan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(3, 3, 3))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_Data_Tutupan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

    private void Table_TutupanGradingMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Table_TutupanGradingMouseClicked
        row = Table_TutupanGrading.rowAtPoint(evt.getPoint());
        if (SwingUtilities.isRightMouseButton(evt)) {
            //if(evt.getButton() == MouseEvent.BUTTON3){
//            col = Table_TutupanGrading.columnAtPoint(evt.getPoint());
            jPopupMenu_TB_Tutupan.show(Table_TutupanGrading, evt.getX(), evt.getY());
        }
        label_KodeAsal.setText(Table_TutupanGrading.getValueAt(row, 0).toString());
    }//GEN-LAST:event_Table_TutupanGradingMouseClicked

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        refreshTable_Tutupan();
    }//GEN-LAST:event_button_searchActionPerformed

    private void txt_search_kodeTutupanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_kodeTutupanKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_Tutupan();
        }
    }//GEN-LAST:event_txt_search_kodeTutupanKeyPressed

    private void jMenuItem_BoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_BoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem_BoxActionPerformed

    private void button_export_TutupanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_TutupanActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_TutupanGrading.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_export_TutupanActionPerformed

    private void button_tambah_dataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_tambah_dataActionPerformed
        // TODO add your handling code here:
        JDialog_TambahDataTutupan dialog = new JDialog_TambahDataTutupan(new javax.swing.JFrame(), true, "Tambah", "GRD", null, null, null);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
    }//GEN-LAST:event_button_tambah_dataActionPerformed

    private void button_add_boxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_add_boxActionPerformed
        // TODO add your handling code here:
        int i = Table_Hasil_grading.getSelectedRow();
        if (i >= 0) {
            try {
                String kode_tutupan = label_KodeAsal.getText();
                String grade = Table_Hasil_grading.getValueAt(i, 0).toString();
                int keping = Integer.valueOf(Table_Hasil_grading.getValueAt(i, 1).toString());
                float gram = Float.valueOf(Table_Hasil_grading.getValueAt(i, 2).toString());

                sql = "SELECT `status` FROM `tb_tutupan_grading` WHERE `kode_tutupan` = '" + kode_tutupan + "'";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    if ("SELESAI".equals(rs.getString("status"))) {
                        JDialog_AddBox dialog = new JDialog_AddBox(new javax.swing.JFrame(), true, kode_tutupan, grade, keping, gram);
                        dialog.pack();
                        dialog.setLocationRelativeTo(this);
                        dialog.setVisible(true);
                        dialog.setEnabled(true);
                        refreshTable_HasilGrading();
                    } else {
                        JOptionPane.showMessageDialog(this, "Maaf Status tutupan " + kode_tutupan + " belum selesai");
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(JPanel_TutupanGradingBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Silahkan Pilih Gradenya dari tabel hasil grading tutupan");
        }
    }//GEN-LAST:event_button_add_boxActionPerformed

    private void button_edit_tutupanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_tutupanActionPerformed
        // TODO add your handling code here:
        try {
            int x = Table_TutupanGrading.getSelectedRow();
            String kode_tutupan = Table_TutupanGrading.getValueAt(x, 0).toString();
            String status = Table_TutupanGrading.getValueAt(x, 7).toString();
            String memo_tutupan = Table_TutupanGrading.getValueAt(x, 10) == null ? "" : Table_TutupanGrading.getValueAt(x, 10).toString();
            Date mulai = dateFormat.parse(Table_TutupanGrading.getValueAt(x, 1).toString());
            Date selesai = dateFormat.parse(Table_TutupanGrading.getValueAt(x, 2).toString());

            sql = "SELECT * FROM `tb_box_bahan_jadi` WHERE `no_tutupan` = '" + kode_tutupan + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Maaf, Tutupan " + kode_tutupan + " tidak dapat diedit karena sudah ada box terbuat");
            } else if ("SELESAI".equals(status)) {
                JOptionPane.showMessageDialog(this, "Maaf, tutupan yang sudah berstatus 'SELESAI' tidak dapat di edit");
            } else {
                JDialog_TambahDataTutupan dialog = new JDialog_TambahDataTutupan(new javax.swing.JFrame(), true, "Edit", kode_tutupan, mulai, selesai, memo_tutupan);
                dialog.pack();
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
                dialog.setEnabled(true);
            }
        } catch (ParseException | SQLException ex) {
            Logger.getLogger(JPanel_TutupanGradingBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_edit_tutupanActionPerformed

    private void button_edit_boxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_boxActionPerformed
        // TODO add your handling code here:
        int i = Table_Hasil_grading.getSelectedRow();
        if (i >= 0) {
            try {
                String kode_tutupan = label_KodeAsal.getText();
                String grade = Table_Hasil_grading.getValueAt(i, 0).toString();
                int keping = Integer.valueOf(Table_Hasil_grading.getValueAt(i, 1).toString());
                float gram = Float.valueOf(Table_Hasil_grading.getValueAt(i, 2).toString());

                sql = "SELECT `status_box` FROM `tb_tutupan_grading` WHERE `kode_tutupan` = '" + kode_tutupan + "'";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    if ("PROSES".equals(rs.getString("status_box"))) {
                        JDialog_EditBox dialog = new JDialog_EditBox(new javax.swing.JFrame(), true, kode_tutupan, grade, keping, gram);
                        dialog.pack();
                        dialog.setLocationRelativeTo(this);
                        dialog.setVisible(true);
                        dialog.setEnabled(true);
                        refreshTable_HasilGrading();
                    } else {
                        JOptionPane.showMessageDialog(this, "Maaf Status Box " + kode_tutupan + " Sudah selesai, tidak diperbolehkan melakukan editing");
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(JPanel_TutupanGradingBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Silahkan Pilih Gradenya dari tabel hasil grading tutupan");
        }
    }//GEN-LAST:event_button_edit_boxActionPerformed

    private void button_printActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_printActionPerformed
        try {
            // TODO add your handling code here:
            //            DefaultTableModel Table = (DefaultTableModel)Table_Bahan_Baku_Masuk.getModel();
            int j = Table_TutupanGrading.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Please select data from the table first", "warning!", 1);
            } else {

                JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Tutupan_Grading_Bahan_Jadi.jrxml");

                JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);

                Map<String, Object> params = new HashMap<String, Object>();
                params.put("PARAM_KODE_TUTUPAN", Table_TutupanGrading.getValueAt(j, 0));
                params.put("SUBREPORT_DIR", "Report\\");
                JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, params, Utility.db.getConnection());
                JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
            }

        } catch (JRException ex) {
            JOptionPane.showMessageDialog(this, ex.getLocalizedMessage());
            Logger.getLogger(JPanel_TutupanGradingBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_printActionPerformed

    private void button_export_gradingTutupanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_gradingTutupanActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_Hasil_grading.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_export_gradingTutupanActionPerformed

    private void button_export_LPTutupanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_LPTutupanActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_Detail_Asal.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_export_LPTutupanActionPerformed

    private void button_selesai_tutupanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_selesai_tutupanActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_TutupanGrading.getSelectedRow();
            String kode_tutupan = Table_TutupanGrading.getValueAt(j, 0).toString();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih tutupan yang sudah selesai !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Apakah kamu yakin tutupan " + kode_tutupan + " sudah selesai ??", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    String Query = "UPDATE `tb_tutupan_grading` SET `status`='SELESAI' WHERE `kode_tutupan` = '" + kode_tutupan + "'";
                    executeSQLQuery(Query, "Tutupan Selesai !");
                    button_search.doClick();
                }
            }
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, e);
        }
    }//GEN-LAST:event_button_selesai_tutupanActionPerformed

    private void button_selesai_boxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_selesai_boxActionPerformed
        // TODO add your handling code here:
        try {
            boolean check = true;
            int j = Table_TutupanGrading.getSelectedRow();

            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih no Tutupan !");
                check = false;
            } else {
                for (int i = 0; i < Table_Hasil_grading.getRowCount(); i++) {
                    if ("0".equals(Table_Hasil_grading.getValueAt(i, 3).toString())) {
                        JOptionPane.showMessageDialog(this, "Maaf ada Box yang belum dimasukkan, silahkan cek kembali");
                        check = false;
                        break;
                    } else {
                        check = true;
                    }
                }
            }

            if (check) {
                String kode_tutupan = Table_TutupanGrading.getValueAt(j, 0).toString();
                int dialogResult = JOptionPane.showConfirmDialog(this, "Apakah kamu yakin Input Box untuk tutupan " + kode_tutupan + " sudah selesai ??", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    String Query = "UPDATE `tb_tutupan_grading` SET `status_box`='SELESAI', `tgl_statusBox` = '" + dateFormat.format(date) + "' WHERE `kode_tutupan` = '" + kode_tutupan + "'";
                    executeSQLQuery(Query, "Input Rincian Box Selesai !");
                    button_search.doClick();
                }
            }
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, e);
        }
    }//GEN-LAST:event_button_selesai_boxActionPerformed

    private void button_tambah_data1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_tambah_data1ActionPerformed
        // TODO add your handling code here:
        JDialog_TambahDataTutupan dialog = new JDialog_TambahDataTutupan(new javax.swing.JFrame(), true, "Tambah", "PBJ", null, null, null);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
    }//GEN-LAST:event_button_tambah_data1ActionPerformed

    private void button_tambah_data2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_tambah_data2ActionPerformed
        // TODO add your handling code here:
        JDialog_TambahDataTutupan dialog = new JDialog_TambahDataTutupan(new javax.swing.JFrame(), true, "Tambah", "EST", null, null, null);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
    }//GEN-LAST:event_button_tambah_data2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_FilterStatus;
    private com.toedter.calendar.JDateChooser Date_filter1;
    private com.toedter.calendar.JDateChooser Date_filter2;
    private javax.swing.JTable Table_Detail_Asal;
    private javax.swing.JTable Table_Hasil_grading;
    public static javax.swing.JTable Table_RincianBox;
    private javax.swing.JTable Table_RincianLP;
    private javax.swing.JTable Table_TutupanGrading;
    private javax.swing.JButton button_add_box;
    private javax.swing.JButton button_edit_box;
    private javax.swing.JButton button_edit_tutupan;
    private javax.swing.JButton button_export_LPTutupan;
    private javax.swing.JButton button_export_Tutupan;
    private javax.swing.JButton button_export_gradingTutupan;
    private javax.swing.JButton button_print;
    public static javax.swing.JButton button_search;
    private javax.swing.JButton button_selesai_box;
    private javax.swing.JButton button_selesai_tutupan;
    private javax.swing.JButton button_tambah_data;
    private javax.swing.JButton button_tambah_data1;
    private javax.swing.JButton button_tambah_data2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JMenuItem jMenuItem_Box;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel_Data_Tutupan;
    private javax.swing.JPopupMenu jPopupMenu_TB_Tutupan;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JLabel label_KodeAsal;
    private javax.swing.JLabel label_jumlah_lp;
    private javax.swing.JLabel label_total_asal_detail_asal;
    private javax.swing.JLabel label_total_asal_rincianLP;
    private javax.swing.JLabel label_total_berat;
    private javax.swing.JLabel label_total_box_hasil_grading;
    private javax.swing.JLabel label_total_data;
    private javax.swing.JLabel label_total_grade_hasil_grading;
    private javax.swing.JLabel label_total_gram_detail_asal;
    private javax.swing.JLabel label_total_gram_hasil_grading;
    private javax.swing.JLabel label_total_gram_rincianBox;
    private javax.swing.JLabel label_total_gram_rincianLP;
    private javax.swing.JLabel label_total_keping;
    private javax.swing.JLabel label_total_kpg_detail_asal;
    private javax.swing.JLabel label_total_kpg_hasil_grading;
    private javax.swing.JLabel label_total_kpg_rincianBox;
    private javax.swing.JLabel label_total_kpg_rincianLP;
    private javax.swing.JLabel label_total_rincian_box;
    private javax.swing.JTextField txt_search_kodeTutupan;
    // End of variables declaration//GEN-END:variables
}
