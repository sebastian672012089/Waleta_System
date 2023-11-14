package waleta_system.HRD;

import waleta_system.Class.Utility;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.AksesMenu;
import waleta_system.MainForm;

public class JPanel_Data_Jam_Kerja extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public JPanel_Data_Jam_Kerja() {
        initComponents();
    }

    public void init() {
        try {
            if ('0' == MainForm.dataMenu.get(AksesMenu.searchMenuByName(MainForm.dataMenu, AksesMenu.MENU_ITEM_JAM_KERJA)).akses.charAt(1)) {
                button_new.setEnabled(false);
            } else {
                button_new.setEnabled(true);
            }
            if ('0' == MainForm.dataMenu.get(AksesMenu.searchMenuByName(MainForm.dataMenu, AksesMenu.MENU_ITEM_JAM_KERJA)).akses.charAt(2)) {
                button_edit.setEnabled(false);
            } else {
                button_edit.setEnabled(true);
            }
            if ('0' == MainForm.dataMenu.get(AksesMenu.searchMenuByName(MainForm.dataMenu, AksesMenu.MENU_ITEM_JAM_KERJA)).akses.charAt(3)) {
                button_delete.setEnabled(false);
            } else {
                button_delete.setEnabled(true);
            }
            ComboBox_departemen.removeAllItems();
            ComboBox_departemen.addItem("All");
            sql = "SELECT `kode_dep` FROM `tb_departemen` ORDER BY `kode_dep`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_departemen.addItem(rs.getString("kode_dep"));
            }

            ComboBox_posisi.removeAllItems();
            ComboBox_posisi.addItem("All");
            sql = "SELECT DISTINCT(`posisi`) AS 'posisi' FROM `tb_karyawan` WHERE `status` = 'IN'";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_posisi.addItem(rs.getString("posisi"));
            }

            ComboBox_jam_kerja.removeAllItems();
            ComboBox_jam_kerja.addItem("All");
            sql = "SELECT DISTINCT(`jam_kerja`) AS 'jam_kerja' FROM `tb_karyawan` WHERE `status` = 'IN'";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_jam_kerja.addItem(rs.getString("jam_kerja"));
            }
            refreshTable();
            Table_Jam_kerja.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && Table_Jam_kerja.getSelectedRow() != -1) {
                        int i = Table_Jam_kerja.getSelectedRow();
                        txt_kode.setText(Table_Jam_kerja.getValueAt(i, 0).toString());

                        if (Table_Jam_kerja.getValueAt(i, 1) == null) {
                            CheckBox_masuk1.setSelected(false);
                        } else {
                            CheckBox_masuk1.setSelected(true);
                            String[] masuk1 = Table_Jam_kerja.getValueAt(i, 1).toString().split(":");
                            int jam_masuk1 = Integer.valueOf(masuk1[0]);
                            int menit_masuk1 = Integer.valueOf(masuk1[1]);
                            jSpinner_jam_masuk_1.setValue(jam_masuk1);
                            jSpinner_menit_masuk_1.setValue(menit_masuk1);

                            String[] pulang1 = Table_Jam_kerja.getValueAt(i, 2).toString().split(":");
                            int jam_pulang1 = Integer.valueOf(pulang1[0]);
                            int menit_pulang1 = Integer.valueOf(pulang1[1]);
                            jSpinner_jam_pulang_1.setValue(jam_pulang1);
                            jSpinner_menit_pulang_1.setValue(menit_pulang1);

                            int istirahat1 = Integer.valueOf(Table_Jam_kerja.getValueAt(i, 3).toString());
                            jSpinner_istirahat_1.setValue(istirahat1);
                        }

                        if (Table_Jam_kerja.getValueAt(i, 4) == null) {
                            CheckBox_masuk2.setSelected(false);
                        } else {
                            CheckBox_masuk2.setSelected(true);
                            String[] masuk2 = Table_Jam_kerja.getValueAt(i, 4).toString().split(":");
                            int jam_masuk2 = Integer.valueOf(masuk2[0]);
                            int menit_masuk2 = Integer.valueOf(masuk2[1]);
                            jSpinner_jam_masuk_2.setValue(jam_masuk2);
                            jSpinner_menit_masuk_2.setValue(menit_masuk2);

                            String[] pulang2 = Table_Jam_kerja.getValueAt(i, 5).toString().split(":");
                            int jam_pulang2 = Integer.valueOf(pulang2[0]);
                            int menit_pulang2 = Integer.valueOf(pulang2[1]);
                            jSpinner_jam_pulang_2.setValue(jam_pulang2);
                            jSpinner_menit_pulang_2.setValue(menit_pulang2);

                            int istirahat2 = Integer.valueOf(Table_Jam_kerja.getValueAt(i, 6).toString());
                            jSpinner_istirahat_2.setValue(istirahat2);
                        }

                        if (Table_Jam_kerja.getValueAt(i, 7) == null) {
                            CheckBox_masuk3.setSelected(false);
                        } else {
                            CheckBox_masuk3.setSelected(true);
                            String[] masuk3 = Table_Jam_kerja.getValueAt(i, 7).toString().split(":");
                            int jam_masuk3 = Integer.valueOf(masuk3[0]);
                            int menit_masuk3 = Integer.valueOf(masuk3[1]);
                            jSpinner_jam_masuk_3.setValue(jam_masuk3);
                            jSpinner_menit_masuk_3.setValue(menit_masuk3);

                            String[] pulang3 = Table_Jam_kerja.getValueAt(i, 8).toString().split(":");
                            int jam_pulang3 = Integer.valueOf(pulang3[0]);
                            int menit_pulang3 = Integer.valueOf(pulang3[1]);
                            jSpinner_jam_pulang_3.setValue(jam_pulang3);
                            jSpinner_menit_pulang_3.setValue(menit_pulang3);

                            int istirahat3 = Integer.valueOf(Table_Jam_kerja.getValueAt(i, 9).toString());
                            jSpinner_istirahat_3.setValue(istirahat3);
                        }

                        if (Table_Jam_kerja.getValueAt(i, 10) == null) {
                            CheckBox_masuk4.setSelected(false);
                        } else {
                            CheckBox_masuk4.setSelected(true);
                            String[] masuk4 = Table_Jam_kerja.getValueAt(i, 10).toString().split(":");
                            int jam_masuk4 = Integer.valueOf(masuk4[0]);
                            int menit_masuk4 = Integer.valueOf(masuk4[1]);
                            jSpinner_jam_masuk_4.setValue(jam_masuk4);
                            jSpinner_menit_masuk_4.setValue(menit_masuk4);

                            String[] pulang4 = Table_Jam_kerja.getValueAt(i, 11).toString().split(":");
                            int jam_pulang4 = Integer.valueOf(pulang4[0]);
                            int menit_pulang4 = Integer.valueOf(pulang4[1]);
                            jSpinner_jam_pulang_4.setValue(jam_pulang4);
                            jSpinner_menit_pulang_4.setValue(menit_pulang4);

                            int istirahat4 = Integer.valueOf(Table_Jam_kerja.getValueAt(i, 12).toString());
                            jSpinner_istirahat_4.setValue(istirahat4);
                        }

                        if (Table_Jam_kerja.getValueAt(i, 13) == null) {
                            CheckBox_masuk5.setSelected(false);
                        } else {
                            CheckBox_masuk5.setSelected(true);
                            String[] masuk5 = Table_Jam_kerja.getValueAt(i, 13).toString().split(":");
                            int jam_masuk5 = Integer.valueOf(masuk5[0]);
                            int menit_masuk5 = Integer.valueOf(masuk5[1]);
                            jSpinner_jam_masuk_5.setValue(jam_masuk5);
                            jSpinner_menit_masuk_5.setValue(menit_masuk5);

                            String[] pulang5 = Table_Jam_kerja.getValueAt(i, 14).toString().split(":");
                            int jam_pulang5 = Integer.valueOf(pulang5[0]);
                            int menit_pulang5 = Integer.valueOf(pulang5[1]);
                            jSpinner_jam_pulang_5.setValue(jam_pulang5);
                            jSpinner_menit_pulang_5.setValue(menit_pulang5);

                            int istirahat5 = Integer.valueOf(Table_Jam_kerja.getValueAt(i, 15).toString());
                            jSpinner_istirahat_5.setValue(istirahat5);
                        }

                        if (Table_Jam_kerja.getValueAt(i, 16) == null) {
                            CheckBox_masuk6.setSelected(false);
                        } else {
                            CheckBox_masuk6.setSelected(true);
                            String[] masuk_6 = Table_Jam_kerja.getValueAt(i, 16).toString().split(":");
                            int jam_masuk_6 = Integer.valueOf(masuk_6[0]);
                            int menit_masuk_6 = Integer.valueOf(masuk_6[1]);
                            jSpinner_jam_masuk_6.setValue(jam_masuk_6);
                            jSpinner_menit_masuk_6.setValue(menit_masuk_6);

                            String[] pulang_sabtu = Table_Jam_kerja.getValueAt(i, 17).toString().split(":");
                            int jam_pulang_sabtu = Integer.valueOf(pulang_sabtu[0]);
                            int menit_pulang_sabtu = Integer.valueOf(pulang_sabtu[1]);
                            jSpinner_jam_pulang_6.setValue(jam_pulang_sabtu);
                            jSpinner_menit_pulang_6.setValue(menit_pulang_sabtu);

                            int istirahat = Integer.valueOf(Table_Jam_kerja.getValueAt(i, 18).toString());
                            jSpinner_istirahat_6.setValue(istirahat);
                        }

                        if (Table_Jam_kerja.getValueAt(i, 19) == null) {
                            CheckBox_masuk7.setSelected(false);
                        } else {
                            CheckBox_masuk7.setSelected(true);
                            String[] masuk_7 = Table_Jam_kerja.getValueAt(i, 19).toString().split(":");
                            int jam_masuk_7 = Integer.valueOf(masuk_7[0]);
                            int menit_masuk_7 = Integer.valueOf(masuk_7[1]);
                            jSpinner_jam_masuk_7.setValue(jam_masuk_7);
                            jSpinner_menit_masuk_7.setValue(menit_masuk_7);

                            String[] pulang_7 = Table_Jam_kerja.getValueAt(i, 20).toString().split(":");
                            int jam_pulang_7 = Integer.valueOf(pulang_7[0]);
                            int menit_pulang_7 = Integer.valueOf(pulang_7[1]);
                            jSpinner_jam_pulang_7.setValue(jam_pulang_7);
                            jSpinner_menit_pulang_7.setValue(menit_pulang_7);

                            int istirahat = Integer.valueOf(Table_Jam_kerja.getValueAt(i, 21).toString());
                            jSpinner_istirahat_7.setValue(istirahat);
                        }
                        txt_keterangan.setText(Table_Jam_kerja.getValueAt(i, 22).toString());
                    }
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(JPanel_Data_Jam_Kerja.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable() {
        try {
            sql = "SELECT COUNT(`id_pegawai`) AS 'total' FROM `tb_karyawan` WHERE `status` = 'IN'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                label_total_karyawan_in.setText("Total Karyawan IN : " + Float.toString(rs.getFloat("total")));
            }
            float total_karyawan_IN_jamKerja = 0;
            DefaultTableModel model = (DefaultTableModel) Table_Jam_kerja.getModel();
            model.setRowCount(0);
            sql = "SELECT `tb_jam_kerja`.`jam_kerja`, "
                    + "`masuk1`, `pulang1`, `istirahat1`, "
                    + "`masuk2`, `pulang2`, `istirahat2`, "
                    + "`masuk3`, `pulang3`, `istirahat3`, "
                    + "`masuk4`, `pulang4`, `istirahat4`, "
                    + "`masuk5`, `pulang5`, `istirahat5`, "
                    + "`masuk6`, `pulang6`, `istirahat6`, "
                    + "`masuk7`, `pulang7`, `istirahat7`, "
                    + "`tb_jam_kerja`.`Keterangan`, "
                    + "SUM(IF(`tb_karyawan`.`status` = 'IN', 1, 0)) AS 'jumlah' "
                    + "FROM `tb_jam_kerja` "
                    + "LEFT JOIN `tb_karyawan` ON `tb_jam_kerja`.`jam_kerja` = `tb_karyawan`.`jam_kerja`"
                    + "WHERE 1 GROUP BY `tb_jam_kerja`.`jam_kerja`";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[25];
            while (rs.next()) {
                row[0] = rs.getString("jam_kerja");
                row[1] = rs.getTime("masuk1");
                row[2] = rs.getTime("pulang1");
                row[3] = rs.getInt("istirahat1");
                row[4] = rs.getTime("masuk2");
                row[5] = rs.getTime("pulang2");
                row[6] = rs.getInt("istirahat2");
                row[7] = rs.getTime("masuk3");
                row[8] = rs.getTime("pulang3");
                row[9] = rs.getInt("istirahat3");
                row[10] = rs.getTime("masuk4");
                row[11] = rs.getTime("pulang4");
                row[12] = rs.getInt("istirahat4");
                row[13] = rs.getTime("masuk5");
                row[14] = rs.getTime("pulang5");
                row[15] = rs.getInt("istirahat5");
                row[16] = rs.getTime("masuk6");
                row[17] = rs.getTime("pulang6");
                row[18] = rs.getInt("istirahat6");
                row[19] = rs.getTime("masuk7");
                row[20] = rs.getTime("pulang7");
                row[21] = rs.getInt("istirahat7");
                row[22] = rs.getString("Keterangan");
                row[23] = rs.getInt("jumlah");
                model.addRow(row);
                total_karyawan_IN_jamKerja = total_karyawan_IN_jamKerja + rs.getInt("jumlah");
            }
            label_total_karyawan_in_jamKerja.setText("Total Karyawan IN dengan jam kerja : " + Float.toString(total_karyawan_IN_jamKerja));
            for (int i = 0; i < Table_Jam_kerja.getRowCount(); i++) {
                float jumlah = (int) Table_Jam_kerja.getValueAt(i, 23);
                float percent = jumlah / total_karyawan_IN_jamKerja * 100f;
                Table_Jam_kerja.setValueAt(Math.round(percent * 10f) / 10f, i, 24);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_Jam_kerja);
            Table_Jam_kerja.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (isSelected) {
                        comp.setBackground(Table_Jam_kerja.getSelectionBackground());
                        comp.setForeground(Table_Jam_kerja.getSelectionForeground());
                    } else {
                        comp.setBackground(Color.cyan);
                        comp.setForeground(Color.BLACK);
                    }
                    return comp;
                }
            });
            Table_Jam_kerja.repaint();
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Data_Jam_Kerja.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTabel_karyawan() {
        try {
            DefaultTableModel model = (DefaultTableModel) table_karyawan.getModel();
            model.setRowCount(0);
            String bagian = "AND `nama_bagian` = '" + ComboBox_bagian.getSelectedItem().toString() + "' ";
            String departemen = "AND `kode_departemen` = '" + ComboBox_departemen.getSelectedItem().toString() + "' ";
            String kelamin = "AND `jenis_kelamin` = '" + ComboBox_jenis_kelamin.getSelectedItem().toString() + "' ";
            String jam_kerja = "";
            if ("All".equals(ComboBox_bagian.getSelectedItem().toString())) {
                bagian = "";
            }
            if ("All".equals(ComboBox_departemen.getSelectedItem().toString())) {
                departemen = "";
            }
            if ("All".equals(ComboBox_jenis_kelamin.getSelectedItem().toString())) {
                kelamin = "";
            }
            if (ComboBox_jam_kerja.getSelectedItem() == null || ComboBox_jam_kerja.getSelectedItem().toString().equals("")) {
                jam_kerja = "AND (`jam_kerja` IS NULL OR `jam_kerja` = '') ";
            } else {
                if ("All".equals(ComboBox_jam_kerja.getSelectedItem().toString())) {
                    jam_kerja = "";
                } else {
                    jam_kerja = "AND `jam_kerja` = '" + ComboBox_jam_kerja.getSelectedItem().toString() + "' ";
                }
            }
            sql = "SELECT `id_pegawai`, `nama_pegawai`, `status`, `tb_bagian`.`nama_bagian`,`tb_bagian`.`kode_departemen`, `posisi`, `jenis_kelamin`, `jam_kerja` "
                    + "FROM `tb_karyawan` LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`"
                    + "WHERE "
                    + "`id_pegawai` LIKE '%" + txt_search_id.getText() + "%'"
                    + " AND `nama_pegawai` LIKE '%" + txt_search_karyawan.getText() + "%' "
                    + bagian
                    + departemen
                    + kelamin
                    + jam_kerja
                    + "AND `status` = 'IN' "
                    + "ORDER BY `id_pegawai` DESC";
            PreparedStatement pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            Object[] row = new Object[10];

            while (rs.next()) {
                row[0] = rs.getString("id_pegawai");
                row[1] = rs.getString("nama_pegawai");
                row[2] = rs.getString("status");
                row[3] = rs.getString("posisi");
                row[4] = rs.getString("kode_departemen");
                row[5] = rs.getString("nama_bagian");
                row[6] = rs.getString("jenis_kelamin");
                row[7] = rs.getString("jam_kerja");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_karyawan);
            label_total.setText(Integer.toString(table_karyawan.getRowCount()));
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Data_Jam_Kerja.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_Jam_kerja = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txt_kode = new javax.swing.JTextField();
        label_hari1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        label_hari6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jSpinner_jam_masuk_1 = new javax.swing.JSpinner();
        jSpinner_menit_masuk_1 = new javax.swing.JSpinner();
        jSpinner_jam_pulang_1 = new javax.swing.JSpinner();
        jSpinner_menit_pulang_1 = new javax.swing.JSpinner();
        jSpinner_jam_masuk_6 = new javax.swing.JSpinner();
        jSpinner_menit_masuk_6 = new javax.swing.JSpinner();
        jSpinner_jam_pulang_6 = new javax.swing.JSpinner();
        jSpinner_menit_pulang_6 = new javax.swing.JSpinner();
        jSpinner_istirahat_1 = new javax.swing.JSpinner();
        txt_keterangan = new javax.swing.JTextField();
        CheckBox_masuk6 = new javax.swing.JCheckBox();
        button_new = new javax.swing.JButton();
        button_edit = new javax.swing.JButton();
        button_delete = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jSpinner_istirahat_6 = new javax.swing.JSpinner();
        jSpinner_jam_pulang_2 = new javax.swing.JSpinner();
        jSpinner_menit_pulang_2 = new javax.swing.JSpinner();
        label_hari2 = new javax.swing.JLabel();
        jSpinner_istirahat_2 = new javax.swing.JSpinner();
        jSpinner_jam_masuk_2 = new javax.swing.JSpinner();
        jSpinner_menit_masuk_2 = new javax.swing.JSpinner();
        jSpinner_istirahat_3 = new javax.swing.JSpinner();
        jSpinner_jam_pulang_3 = new javax.swing.JSpinner();
        jSpinner_jam_masuk_3 = new javax.swing.JSpinner();
        jSpinner_menit_pulang_3 = new javax.swing.JSpinner();
        jSpinner_menit_masuk_3 = new javax.swing.JSpinner();
        label_hari3 = new javax.swing.JLabel();
        jSpinner_istirahat_4 = new javax.swing.JSpinner();
        jSpinner_jam_masuk_4 = new javax.swing.JSpinner();
        label_hari4 = new javax.swing.JLabel();
        jSpinner_jam_pulang_4 = new javax.swing.JSpinner();
        jSpinner_menit_pulang_4 = new javax.swing.JSpinner();
        jSpinner_menit_masuk_4 = new javax.swing.JSpinner();
        jSpinner_menit_masuk_5 = new javax.swing.JSpinner();
        jSpinner_menit_pulang_5 = new javax.swing.JSpinner();
        jSpinner_jam_pulang_5 = new javax.swing.JSpinner();
        jSpinner_istirahat_5 = new javax.swing.JSpinner();
        jSpinner_jam_masuk_5 = new javax.swing.JSpinner();
        label_hari5 = new javax.swing.JLabel();
        label_hari7 = new javax.swing.JLabel();
        jSpinner_istirahat_7 = new javax.swing.JSpinner();
        CheckBox_masuk7 = new javax.swing.JCheckBox();
        jSpinner_menit_pulang_7 = new javax.swing.JSpinner();
        jSpinner_jam_pulang_7 = new javax.swing.JSpinner();
        jSpinner_menit_masuk_7 = new javax.swing.JSpinner();
        jSpinner_jam_masuk_7 = new javax.swing.JSpinner();
        CheckBox_masuk1 = new javax.swing.JCheckBox();
        CheckBox_masuk2 = new javax.swing.JCheckBox();
        CheckBox_masuk3 = new javax.swing.JCheckBox();
        CheckBox_masuk4 = new javax.swing.JCheckBox();
        CheckBox_masuk5 = new javax.swing.JCheckBox();
        label_total_karyawan_in = new javax.swing.JLabel();
        label_total_karyawan_in_jamKerja = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel_search_karyawan = new javax.swing.JPanel();
        txt_search_karyawan = new javax.swing.JTextField();
        button_search_karyawan = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        ComboBox_departemen = new javax.swing.JComboBox<>();
        ComboBox_bagian = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        txt_search_id = new javax.swing.JTextField();
        ComboBox_posisi = new javax.swing.JComboBox<>();
        jLabel17 = new javax.swing.JLabel();
        ComboBox_jam_kerja = new javax.swing.JComboBox<>();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        ComboBox_jenis_kelamin = new javax.swing.JComboBox<>();
        jLabel20 = new javax.swing.JLabel();
        label_total = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        table_karyawan = new javax.swing.JTable();

        setBackground(new java.awt.Color(255, 255, 255));

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        Table_Jam_kerja.setAutoCreateRowSorter(true);
        Table_Jam_kerja.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Table_Jam_kerja.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "Senin M", "Senin P", "Senin I", "Selasa M", "Selasa P", "Selasa I", "Rabu M", "Rabu P", "Rabu I", "Kamis M", "Kamis P", "Kamis I", "Jumat M", "Jumat P", "Jumat I", "Sabtu M", "Sabtu P", "Sabtu I", "Minggu M", "Minggu P", "Minggu I", "Keterangan", "Jumlah", "%"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_Jam_kerja.setRowHeight(20);
        Table_Jam_kerja.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Table_Jam_kerja);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText("Kode :");

        txt_kode.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        label_hari1.setBackground(new java.awt.Color(255, 255, 255));
        label_hari1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_hari1.setText("Senin :");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("Masuk");

        label_hari6.setBackground(new java.awt.Color(255, 255, 255));
        label_hari6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_hari6.setText("Sabtu :");
        label_hari6.setEnabled(false);

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText("Istirahat (Menit) :");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("Keterangan :");

        jSpinner_jam_masuk_1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jSpinner_jam_masuk_1.setModel(new javax.swing.SpinnerNumberModel(7, 0, 23, 1));

        jSpinner_menit_masuk_1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jSpinner_menit_masuk_1.setModel(new javax.swing.SpinnerNumberModel(30, 0, 55, 5));

        jSpinner_jam_pulang_1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jSpinner_jam_pulang_1.setModel(new javax.swing.SpinnerNumberModel(16, 0, 23, 1));

        jSpinner_menit_pulang_1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jSpinner_menit_pulang_1.setModel(new javax.swing.SpinnerNumberModel(0, 0, 55, 5));

        jSpinner_jam_masuk_6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jSpinner_jam_masuk_6.setModel(new javax.swing.SpinnerNumberModel(7, 0, 23, 1));
        jSpinner_jam_masuk_6.setEnabled(false);

        jSpinner_menit_masuk_6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jSpinner_menit_masuk_6.setModel(new javax.swing.SpinnerNumberModel(30, 0, 55, 5));
        jSpinner_menit_masuk_6.setEnabled(false);

        jSpinner_jam_pulang_6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jSpinner_jam_pulang_6.setModel(new javax.swing.SpinnerNumberModel(16, 0, 23, 1));
        jSpinner_jam_pulang_6.setEnabled(false);

        jSpinner_menit_pulang_6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jSpinner_menit_pulang_6.setModel(new javax.swing.SpinnerNumberModel(0, 0, 55, 5));
        jSpinner_menit_pulang_6.setEnabled(false);

        jSpinner_istirahat_1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jSpinner_istirahat_1.setModel(new javax.swing.SpinnerNumberModel(30, 0, 60, 5));

        txt_keterangan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_keterangan.setText("-");

        CheckBox_masuk6.setBackground(new java.awt.Color(255, 255, 255));
        CheckBox_masuk6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        CheckBox_masuk6.setText("Sabtu Masuk");
        CheckBox_masuk6.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                CheckBox_masuk6ItemStateChanged(evt);
            }
        });

        button_new.setBackground(new java.awt.Color(255, 255, 255));
        button_new.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_new.setText("New");
        button_new.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_newActionPerformed(evt);
            }
        });

        button_edit.setBackground(new java.awt.Color(255, 255, 255));
        button_edit.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_edit.setText("Edit");
        button_edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_editActionPerformed(evt);
            }
        });

        button_delete.setBackground(new java.awt.Color(255, 255, 255));
        button_delete.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_delete.setText("Delete");
        button_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_deleteActionPerformed(evt);
            }
        });

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("Pulang");

        jSpinner_istirahat_6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jSpinner_istirahat_6.setModel(new javax.swing.SpinnerNumberModel(30, 0, 60, 5));
        jSpinner_istirahat_6.setEnabled(false);

        jSpinner_jam_pulang_2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jSpinner_jam_pulang_2.setModel(new javax.swing.SpinnerNumberModel(16, 0, 23, 1));

        jSpinner_menit_pulang_2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jSpinner_menit_pulang_2.setModel(new javax.swing.SpinnerNumberModel(0, 0, 55, 5));

        label_hari2.setBackground(new java.awt.Color(255, 255, 255));
        label_hari2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_hari2.setText("Selasa :");

        jSpinner_istirahat_2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jSpinner_istirahat_2.setModel(new javax.swing.SpinnerNumberModel(30, 0, 60, 5));

        jSpinner_jam_masuk_2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jSpinner_jam_masuk_2.setModel(new javax.swing.SpinnerNumberModel(7, 0, 23, 1));

        jSpinner_menit_masuk_2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jSpinner_menit_masuk_2.setModel(new javax.swing.SpinnerNumberModel(30, 0, 55, 5));

        jSpinner_istirahat_3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jSpinner_istirahat_3.setModel(new javax.swing.SpinnerNumberModel(30, 0, 60, 5));

        jSpinner_jam_pulang_3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jSpinner_jam_pulang_3.setModel(new javax.swing.SpinnerNumberModel(16, 0, 23, 1));

        jSpinner_jam_masuk_3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jSpinner_jam_masuk_3.setModel(new javax.swing.SpinnerNumberModel(7, 0, 23, 1));

        jSpinner_menit_pulang_3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jSpinner_menit_pulang_3.setModel(new javax.swing.SpinnerNumberModel(0, 0, 55, 5));

        jSpinner_menit_masuk_3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jSpinner_menit_masuk_3.setModel(new javax.swing.SpinnerNumberModel(30, 0, 55, 5));

        label_hari3.setBackground(new java.awt.Color(255, 255, 255));
        label_hari3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_hari3.setText("Rabu :");

        jSpinner_istirahat_4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jSpinner_istirahat_4.setModel(new javax.swing.SpinnerNumberModel(30, 0, 60, 5));

        jSpinner_jam_masuk_4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jSpinner_jam_masuk_4.setModel(new javax.swing.SpinnerNumberModel(7, 0, 23, 1));

        label_hari4.setBackground(new java.awt.Color(255, 255, 255));
        label_hari4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_hari4.setText("Kamis :");

        jSpinner_jam_pulang_4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jSpinner_jam_pulang_4.setModel(new javax.swing.SpinnerNumberModel(16, 0, 23, 1));

        jSpinner_menit_pulang_4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jSpinner_menit_pulang_4.setModel(new javax.swing.SpinnerNumberModel(0, 0, 55, 5));

        jSpinner_menit_masuk_4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jSpinner_menit_masuk_4.setModel(new javax.swing.SpinnerNumberModel(30, 0, 55, 5));

        jSpinner_menit_masuk_5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jSpinner_menit_masuk_5.setModel(new javax.swing.SpinnerNumberModel(30, 0, 55, 5));

        jSpinner_menit_pulang_5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jSpinner_menit_pulang_5.setModel(new javax.swing.SpinnerNumberModel(0, 0, 55, 5));

        jSpinner_jam_pulang_5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jSpinner_jam_pulang_5.setModel(new javax.swing.SpinnerNumberModel(16, 0, 23, 1));

        jSpinner_istirahat_5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jSpinner_istirahat_5.setModel(new javax.swing.SpinnerNumberModel(30, 0, 60, 5));

        jSpinner_jam_masuk_5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jSpinner_jam_masuk_5.setModel(new javax.swing.SpinnerNumberModel(7, 0, 23, 1));

        label_hari5.setBackground(new java.awt.Color(255, 255, 255));
        label_hari5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_hari5.setText("Jumat :");

        label_hari7.setBackground(new java.awt.Color(255, 255, 255));
        label_hari7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_hari7.setText("Minggu :");
        label_hari7.setEnabled(false);

        jSpinner_istirahat_7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jSpinner_istirahat_7.setModel(new javax.swing.SpinnerNumberModel(30, 0, 60, 5));
        jSpinner_istirahat_7.setEnabled(false);

        CheckBox_masuk7.setBackground(new java.awt.Color(255, 255, 255));
        CheckBox_masuk7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        CheckBox_masuk7.setText("Minggu Masuk");
        CheckBox_masuk7.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                CheckBox_masuk7ItemStateChanged(evt);
            }
        });

        jSpinner_menit_pulang_7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jSpinner_menit_pulang_7.setModel(new javax.swing.SpinnerNumberModel(0, 0, 55, 5));
        jSpinner_menit_pulang_7.setEnabled(false);

        jSpinner_jam_pulang_7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jSpinner_jam_pulang_7.setModel(new javax.swing.SpinnerNumberModel(16, 0, 23, 1));
        jSpinner_jam_pulang_7.setEnabled(false);

        jSpinner_menit_masuk_7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jSpinner_menit_masuk_7.setModel(new javax.swing.SpinnerNumberModel(30, 0, 55, 5));
        jSpinner_menit_masuk_7.setEnabled(false);

        jSpinner_jam_masuk_7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jSpinner_jam_masuk_7.setModel(new javax.swing.SpinnerNumberModel(7, 0, 23, 1));
        jSpinner_jam_masuk_7.setEnabled(false);

        CheckBox_masuk1.setBackground(new java.awt.Color(255, 255, 255));
        CheckBox_masuk1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        CheckBox_masuk1.setSelected(true);
        CheckBox_masuk1.setText("Senin Masuk");
        CheckBox_masuk1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                CheckBox_masuk1ItemStateChanged(evt);
            }
        });

        CheckBox_masuk2.setBackground(new java.awt.Color(255, 255, 255));
        CheckBox_masuk2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        CheckBox_masuk2.setSelected(true);
        CheckBox_masuk2.setText("Selasa Masuk");
        CheckBox_masuk2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                CheckBox_masuk2ItemStateChanged(evt);
            }
        });

        CheckBox_masuk3.setBackground(new java.awt.Color(255, 255, 255));
        CheckBox_masuk3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        CheckBox_masuk3.setSelected(true);
        CheckBox_masuk3.setText("Rabu Masuk");
        CheckBox_masuk3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                CheckBox_masuk3ItemStateChanged(evt);
            }
        });

        CheckBox_masuk4.setBackground(new java.awt.Color(255, 255, 255));
        CheckBox_masuk4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        CheckBox_masuk4.setSelected(true);
        CheckBox_masuk4.setText("Kamis Masuk");
        CheckBox_masuk4.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                CheckBox_masuk4ItemStateChanged(evt);
            }
        });

        CheckBox_masuk5.setBackground(new java.awt.Color(255, 255, 255));
        CheckBox_masuk5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        CheckBox_masuk5.setSelected(true);
        CheckBox_masuk5.setText("Jumat Masuk");
        CheckBox_masuk5.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                CheckBox_masuk5ItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_hari3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_hari4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_hari1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_hari5, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_hari2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_hari6, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_hari7, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_kode, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jSpinner_jam_masuk_1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jSpinner_menit_masuk_1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jSpinner_jam_masuk_2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jSpinner_menit_masuk_2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jSpinner_jam_masuk_3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jSpinner_menit_masuk_3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jSpinner_jam_masuk_4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jSpinner_menit_masuk_4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jSpinner_jam_masuk_5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jSpinner_menit_masuk_5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jSpinner_jam_pulang_1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jSpinner_menit_pulang_1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jSpinner_jam_pulang_2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jSpinner_menit_pulang_2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jSpinner_jam_pulang_3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jSpinner_menit_pulang_3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jSpinner_jam_pulang_4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jSpinner_menit_pulang_4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jSpinner_jam_pulang_5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jSpinner_menit_pulang_5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jSpinner_jam_masuk_6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSpinner_menit_masuk_6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jSpinner_jam_pulang_6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSpinner_menit_pulang_6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jSpinner_jam_masuk_7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSpinner_menit_masuk_7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jSpinner_jam_pulang_7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSpinner_menit_pulang_7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(31, 31, 31)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jSpinner_istirahat_6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(CheckBox_masuk6, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jSpinner_istirahat_3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(CheckBox_masuk3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jSpinner_istirahat_4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(CheckBox_masuk4, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jSpinner_istirahat_5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(CheckBox_masuk5, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jSpinner_istirahat_7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(CheckBox_masuk7, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jSpinner_istirahat_1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jSpinner_istirahat_2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(CheckBox_masuk1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(CheckBox_masuk2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(button_new)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_edit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_delete))
                    .addComponent(txt_keterangan, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_kode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(31, 31, 31))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(label_hari1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jSpinner_jam_masuk_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jSpinner_menit_masuk_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jSpinner_jam_pulang_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jSpinner_menit_pulang_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jSpinner_istirahat_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(label_hari2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jSpinner_jam_masuk_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jSpinner_menit_masuk_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jSpinner_jam_pulang_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jSpinner_menit_pulang_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jSpinner_istirahat_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(CheckBox_masuk2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(CheckBox_masuk1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_hari3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSpinner_jam_masuk_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSpinner_menit_masuk_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSpinner_jam_pulang_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSpinner_menit_pulang_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSpinner_istirahat_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CheckBox_masuk3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_hari4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSpinner_jam_masuk_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSpinner_menit_masuk_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSpinner_jam_pulang_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSpinner_menit_pulang_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSpinner_istirahat_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CheckBox_masuk4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_hari5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSpinner_jam_masuk_5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSpinner_menit_masuk_5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSpinner_jam_pulang_5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSpinner_menit_pulang_5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSpinner_istirahat_5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CheckBox_masuk5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_hari6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSpinner_jam_masuk_6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSpinner_menit_masuk_6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CheckBox_masuk6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSpinner_jam_pulang_6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSpinner_menit_pulang_6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSpinner_istirahat_6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_hari7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSpinner_jam_masuk_7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSpinner_menit_masuk_7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CheckBox_masuk7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSpinner_jam_pulang_7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSpinner_menit_pulang_7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSpinner_istirahat_7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_keterangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_new)
                    .addComponent(button_edit)
                    .addComponent(button_delete))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        label_total_karyawan_in.setBackground(new java.awt.Color(255, 255, 255));
        label_total_karyawan_in.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_total_karyawan_in.setText("Total Karyawan IN dengan jam kerja :");

        label_total_karyawan_in_jamKerja.setBackground(new java.awt.Color(255, 255, 255));
        label_total_karyawan_in_jamKerja.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_total_karyawan_in_jamKerja.setText("Total Karyawan IN :");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_total_karyawan_in, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(label_total_karyawan_in_jamKerja, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(562, 562, 562))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1359, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(label_total_karyawan_in_jamKerja)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_karyawan_in)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("DATA JAM KERJA", jPanel1);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jPanel_search_karyawan.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_search_karyawan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        txt_search_karyawan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_karyawan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_karyawanKeyPressed(evt);
            }
        });

        button_search_karyawan.setBackground(new java.awt.Color(255, 255, 255));
        button_search_karyawan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_karyawan.setText("Search");
        button_search_karyawan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_karyawanActionPerformed(evt);
            }
        });

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Nama :");

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("Departemen :");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Bagian :");

        ComboBox_departemen.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_departemen.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));
        ComboBox_departemen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboBox_departemenActionPerformed(evt);
            }
        });

        ComboBox_bagian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_bagian.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel15.setText("ID :");

        txt_search_id.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_id.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_idKeyPressed(evt);
            }
        });

        ComboBox_posisi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_posisi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel17.setText("Posisi :");

        ComboBox_jam_kerja.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_jam_kerja.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel18.setText("Jam Kerja :");

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel19.setText("Gender :");

        ComboBox_jenis_kelamin.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_jenis_kelamin.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "LAKI-LAKI", "PEREMPUAN" }));

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel20.setText("Total :");

        label_total.setBackground(new java.awt.Color(255, 255, 255));
        label_total.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total.setText("0");

        javax.swing.GroupLayout jPanel_search_karyawanLayout = new javax.swing.GroupLayout(jPanel_search_karyawan);
        jPanel_search_karyawan.setLayout(jPanel_search_karyawanLayout);
        jPanel_search_karyawanLayout.setHorizontalGroup(
            jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_karyawanLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_search_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_search_id, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ComboBox_departemen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ComboBox_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ComboBox_posisi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ComboBox_jenis_kelamin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ComboBox_jam_kerja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_search_karyawan)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_total)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_search_karyawanLayout.setVerticalGroup(
            jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_karyawanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_search_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_departemen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_id, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_posisi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_jam_kerja, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_jenis_kelamin, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        table_karyawan.setAutoCreateRowSorter(true);
        table_karyawan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        table_karyawan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nama", "Status", "Posisi", "Dept", "Bagian", "Gender", "Jam Kerja"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        table_karyawan.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(table_karyawan);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel_search_karyawan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1361, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel_search_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 624, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("DATA KARYAWAN", jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void CheckBox_masuk6ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_CheckBox_masuk6ItemStateChanged
        // TODO add your handling code here:
        if (CheckBox_masuk6.isSelected()) {
            label_hari6.setEnabled(true);
            jSpinner_jam_masuk_6.setEnabled(true);
            jSpinner_menit_masuk_6.setEnabled(true);
            jSpinner_jam_pulang_6.setEnabled(true);
            jSpinner_menit_pulang_6.setEnabled(true);
            jSpinner_istirahat_6.setEnabled(true);
        } else {
            label_hari6.setEnabled(false);
            jSpinner_jam_masuk_6.setEnabled(false);
            jSpinner_menit_masuk_6.setEnabled(false);
            jSpinner_jam_pulang_6.setEnabled(false);
            jSpinner_menit_pulang_6.setEnabled(false);
            jSpinner_istirahat_6.setEnabled(false);
        }
    }//GEN-LAST:event_CheckBox_masuk6ItemStateChanged

    private void button_newActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_newActionPerformed
        // TODO add your handling code here:
        try {
            Boolean Check = true;
            String insert = "";
            String value = "";
            if (CheckBox_masuk1.isSelected()) {
                insert = insert + ", `masuk1`, `pulang1`, `istirahat1`";
                value = value + "'" + jSpinner_jam_masuk_1.getValue() + ":" + jSpinner_menit_masuk_1.getValue() + ":00',"
                        + "'" + jSpinner_jam_pulang_1.getValue() + ":" + jSpinner_menit_pulang_1.getValue() + ":00',"
                        + "'" + jSpinner_istirahat_1.getValue() + "',";
            }
            if (CheckBox_masuk2.isSelected()) {
                insert = insert + ", `masuk2`, `pulang2`, `istirahat2`";
                value = value + "'" + jSpinner_jam_masuk_2.getValue() + ":" + jSpinner_menit_masuk_2.getValue() + ":00',"
                        + "'" + jSpinner_jam_pulang_2.getValue() + ":" + jSpinner_menit_pulang_2.getValue() + ":00',"
                        + "'" + jSpinner_istirahat_2.getValue() + "',";
            }
            if (CheckBox_masuk3.isSelected()) {
                insert = insert + ", `masuk3`, `pulang3`, `istirahat3`";
                value = value + "'" + jSpinner_jam_masuk_3.getValue() + ":" + jSpinner_menit_masuk_3.getValue() + ":00',"
                        + "'" + jSpinner_jam_pulang_3.getValue() + ":" + jSpinner_menit_pulang_3.getValue() + ":00',"
                        + "'" + jSpinner_istirahat_3.getValue() + "',";
            }
            if (CheckBox_masuk4.isSelected()) {
                insert = insert + ", `masuk4`, `pulang4`, `istirahat4`";
                value = value + "'" + jSpinner_jam_masuk_4.getValue() + ":" + jSpinner_menit_masuk_4.getValue() + ":00',"
                        + "'" + jSpinner_jam_pulang_4.getValue() + ":" + jSpinner_menit_pulang_4.getValue() + ":00',"
                        + "'" + jSpinner_istirahat_4.getValue() + "',";
            }
            if (CheckBox_masuk5.isSelected()) {
                insert = insert + ", `masuk5`, `pulang5`, `istirahat5`";
                value = value + "'" + jSpinner_jam_masuk_5.getValue() + ":" + jSpinner_menit_masuk_5.getValue() + ":00',"
                        + "'" + jSpinner_jam_pulang_5.getValue() + ":" + jSpinner_menit_pulang_5.getValue() + ":00',"
                        + "'" + jSpinner_istirahat_5.getValue() + "',";
            }
            if (CheckBox_masuk6.isSelected()) {
                insert = insert + ", `masuk6`, `pulang6`, `istirahat6`";
                value = value + "'" + jSpinner_jam_masuk_6.getValue() + ":" + jSpinner_menit_masuk_6.getValue() + ":00',"
                        + "'" + jSpinner_jam_pulang_6.getValue() + ":" + jSpinner_menit_pulang_6.getValue() + ":00',"
                        + "'" + jSpinner_istirahat_6.getValue() + "',";
            }
            if (CheckBox_masuk7.isSelected()) {
                insert = insert + ", `masuk7`, `pulang7`, `istirahat7`";
                value = value + "'" + jSpinner_jam_masuk_7.getValue() + ":" + jSpinner_menit_masuk_7.getValue() + ":00',"
                        + "'" + jSpinner_jam_pulang_7.getValue() + ":" + jSpinner_menit_pulang_7.getValue() + ":00',"
                        + "'" + jSpinner_istirahat_7.getValue() + "',";
            }
            if (txt_kode.getText() == null || txt_kode.getText().equals("")) {
                Check = false;
                JOptionPane.showMessageDialog(this, "Kode / nama jam kerja harus diisi !");
            } else if (insert.equals("")) {
                Check = false;
                JOptionPane.showMessageDialog(this, "Minimal ada 1 hari masuk kerja !");
            }
            if (Check) {
                sql = "INSERT INTO `tb_jam_kerja`(`jam_kerja` " + insert + ", `Keterangan`) "
                        + "VALUES ('" + txt_kode.getText() + "',"
                        + value
                        + "'" + txt_keterangan.getText() + "')";
                Utility.db.getConnection().createStatement();
                if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                    JOptionPane.showMessageDialog(this, "Data Saved !");
                    refreshTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed !");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Data_Jam_Kerja.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_newActionPerformed

    private void button_editActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_editActionPerformed
        // TODO add your handling code here:
        int j = Table_Jam_kerja.getSelectedRow();
        try {
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Please Select Row Data that you want to Update !");
            } else {
                String kode_lama = Table_Jam_kerja.getValueAt(j, 0).toString();
                String hari1 = "", hari2 = "", hari3 = "", hari4 = "", hari5 = "", hari6 = "", hari7 = "";
                if (CheckBox_masuk1.isSelected()) {
                    hari1 = "`masuk1` = '" + jSpinner_jam_masuk_1.getValue() + ":" + jSpinner_menit_masuk_1.getValue() + ":00', "
                            + "`pulang1` = '" + jSpinner_jam_pulang_1.getValue() + ":" + jSpinner_menit_pulang_1.getValue() + ":00', "
                            + "`istirahat1` = '" + jSpinner_istirahat_1.getValue().toString() + "', ";
                } else {
                    hari1 = "`masuk1` = NULL, "
                            + "`pulang1` = NULL, "
                            + "`istirahat1` = NULL, ";
                }
                if (CheckBox_masuk2.isSelected()) {
                    hari2 = "`masuk2` = '" + jSpinner_jam_masuk_2.getValue() + ":" + jSpinner_menit_masuk_2.getValue() + ":00', "
                            + "`pulang2` = '" + jSpinner_jam_pulang_2.getValue() + ":" + jSpinner_menit_pulang_2.getValue() + ":00', "
                            + "`istirahat2` = '" + jSpinner_istirahat_2.getValue().toString() + "', ";
                } else {
                    hari2 = "`masuk2` = NULL, "
                            + "`pulang2` = NULL, "
                            + "`istirahat2` = NULL, ";
                }
                if (CheckBox_masuk3.isSelected()) {
                    hari3 = "`masuk3` = '" + jSpinner_jam_masuk_3.getValue() + ":" + jSpinner_menit_masuk_3.getValue() + ":00', "
                            + "`pulang3` = '" + jSpinner_jam_pulang_3.getValue() + ":" + jSpinner_menit_pulang_3.getValue() + ":00', "
                            + "`istirahat3` = '" + jSpinner_istirahat_3.getValue().toString() + "', ";
                } else {
                    hari3 = "`masuk3` = NULL, "
                            + "`pulang3` = NULL, "
                            + "`istirahat3` = NULL, ";
                }
                if (CheckBox_masuk4.isSelected()) {
                    hari4 = "`masuk4` = '" + jSpinner_jam_masuk_4.getValue() + ":" + jSpinner_menit_masuk_4.getValue() + ":00', "
                            + "`pulang4` = '" + jSpinner_jam_pulang_4.getValue() + ":" + jSpinner_menit_pulang_4.getValue() + ":00', "
                            + "`istirahat4` = '" + jSpinner_istirahat_4.getValue().toString() + "', ";
                } else {
                    hari4 = "`masuk4` = NULL, "
                            + "`pulang4` = NULL, "
                            + "`istirahat4` = NULL, ";
                }
                if (CheckBox_masuk5.isSelected()) {
                    hari5 = "`masuk5` = '" + jSpinner_jam_masuk_5.getValue() + ":" + jSpinner_menit_masuk_5.getValue() + ":00', "
                            + "`pulang5` = '" + jSpinner_jam_pulang_5.getValue() + ":" + jSpinner_menit_pulang_5.getValue() + ":00', "
                            + "`istirahat5` = '" + jSpinner_istirahat_5.getValue().toString() + "', ";
                } else {
                    hari5 = "`masuk5` = NULL, "
                            + "`pulang5` = NULL, "
                            + "`istirahat5` = NULL, ";
                }
                if (CheckBox_masuk6.isSelected()) {
                    hari6 = "`masuk6` = '" + jSpinner_jam_masuk_6.getValue() + ":" + jSpinner_menit_masuk_6.getValue() + ":00', "
                            + "`pulang6` = '" + jSpinner_jam_pulang_6.getValue() + ":" + jSpinner_menit_pulang_6.getValue() + ":00', "
                            + "`istirahat6` = '" + jSpinner_istirahat_6.getValue().toString() + "', ";
                } else {
                    hari6 = "`masuk6` = NULL, "
                            + "`pulang6` = NULL, "
                            + "`istirahat6` = NULL, ";
                }
                if (CheckBox_masuk7.isSelected()) {
                    hari7 = "`masuk7` = '" + jSpinner_jam_masuk_7.getValue() + ":" + jSpinner_menit_masuk_7.getValue() + ":00', "
                            + "`pulang7` = '" + jSpinner_jam_pulang_7.getValue() + ":" + jSpinner_menit_pulang_7.getValue() + ":00', "
                            + "`istirahat7` = '" + jSpinner_istirahat_7.getValue().toString() + "', ";
                } else {
                    hari7 = "`masuk7` = NULL, "
                            + "`pulang7` = NULL, "
                            + "`istirahat7` = NULL, ";
                }
                sql = "UPDATE `tb_jam_kerja` SET "
                        + "`jam_kerja` = '" + txt_kode.getText() + "', "
                        + hari1
                        + hari2
                        + hari3
                        + hari4
                        + hari5
                        + hari6
                        + hari7
                        + "`Keterangan` = '" + txt_keterangan.getText() + "' "
                        + "WHERE `jam_kerja` = '" + kode_lama + "'";
                Utility.db.getConnection().createStatement();
                if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                    JOptionPane.showMessageDialog(this, "Data Saved !");
                    refreshTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed !");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Data_Jam_Kerja.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_editActionPerformed

    private void button_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_deleteActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_Jam_kerja.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Please Select Row Data that you want to Delete !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Delete this data?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    sql = "DELETE FROM `tb_jam_kerja` WHERE `jam_kerja` = '" + Table_Jam_kerja.getValueAt(j, 0).toString() + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                        JOptionPane.showMessageDialog(this, "Data Saved !");
                        refreshTable();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed !");
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Data_Jam_Kerja.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_deleteActionPerformed

    private void CheckBox_masuk7ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_CheckBox_masuk7ItemStateChanged
        // TODO add your handling code here:
        if (CheckBox_masuk7.isSelected()) {
            label_hari7.setEnabled(true);
            jSpinner_jam_masuk_7.setEnabled(true);
            jSpinner_menit_masuk_7.setEnabled(true);
            jSpinner_jam_pulang_7.setEnabled(true);
            jSpinner_menit_pulang_7.setEnabled(true);
            jSpinner_istirahat_7.setEnabled(true);
        } else {
            label_hari7.setEnabled(false);
            jSpinner_jam_masuk_7.setEnabled(false);
            jSpinner_menit_masuk_7.setEnabled(false);
            jSpinner_jam_pulang_7.setEnabled(false);
            jSpinner_menit_pulang_7.setEnabled(false);
            jSpinner_istirahat_7.setEnabled(false);
        }
    }//GEN-LAST:event_CheckBox_masuk7ItemStateChanged

    private void txt_search_karyawanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_karyawanKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTabel_karyawan();
        }
    }//GEN-LAST:event_txt_search_karyawanKeyPressed

    private void button_search_karyawanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_karyawanActionPerformed
        // TODO add your handling code here:
        refreshTabel_karyawan();
    }//GEN-LAST:event_button_search_karyawanActionPerformed

    private void ComboBox_departemenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboBox_departemenActionPerformed
        // TODO add your handling code here:
        try {
            ComboBox_bagian.removeAllItems();
            String query = "SELECT `kode_bagian`, `nama_bagian`, `kode_departemen`, `status_bagian` "
                    + "FROM `tb_bagian` WHERE `status_bagian` = '1'"
                    + "ORDER BY `nama_bagian`";
            if (ComboBox_departemen.getSelectedItem() != "All") {
                query = "SELECT `kode_bagian`, `nama_bagian`, `kode_departemen`, `status_bagian` "
                        + "FROM `tb_bagian` "
                        + "WHERE `status_bagian` = '1' AND `kode_departemen`='" + ComboBox_departemen.getSelectedItem() + "' "
                        + "ORDER BY `nama_bagian`";
            }
            rs = Utility.db.getStatement().executeQuery(query);
            ComboBox_bagian.addItem("All");
            while (rs.next()) {
                ComboBox_bagian.addItem(rs.getString("nama_bagian"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e);
        }
    }//GEN-LAST:event_ComboBox_departemenActionPerformed

    private void txt_search_idKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_idKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTabel_karyawan();
        }
    }//GEN-LAST:event_txt_search_idKeyPressed

    private void CheckBox_masuk1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_CheckBox_masuk1ItemStateChanged
        // TODO add your handling code here:
        if (CheckBox_masuk1.isSelected()) {
            label_hari1.setEnabled(true);
            jSpinner_jam_masuk_1.setEnabled(true);
            jSpinner_menit_masuk_1.setEnabled(true);
            jSpinner_jam_pulang_1.setEnabled(true);
            jSpinner_menit_pulang_1.setEnabled(true);
            jSpinner_istirahat_1.setEnabled(true);
        } else {
            label_hari1.setEnabled(false);
            jSpinner_jam_masuk_1.setEnabled(false);
            jSpinner_menit_masuk_1.setEnabled(false);
            jSpinner_jam_pulang_1.setEnabled(false);
            jSpinner_menit_pulang_1.setEnabled(false);
            jSpinner_istirahat_1.setEnabled(false);
        }
    }//GEN-LAST:event_CheckBox_masuk1ItemStateChanged

    private void CheckBox_masuk2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_CheckBox_masuk2ItemStateChanged
        // TODO add your handling code here:
        if (CheckBox_masuk2.isSelected()) {
            label_hari2.setEnabled(true);
            jSpinner_jam_masuk_2.setEnabled(true);
            jSpinner_menit_masuk_2.setEnabled(true);
            jSpinner_jam_pulang_2.setEnabled(true);
            jSpinner_menit_pulang_2.setEnabled(true);
            jSpinner_istirahat_2.setEnabled(true);
        } else {
            label_hari2.setEnabled(false);
            jSpinner_jam_masuk_2.setEnabled(false);
            jSpinner_menit_masuk_2.setEnabled(false);
            jSpinner_jam_pulang_2.setEnabled(false);
            jSpinner_menit_pulang_2.setEnabled(false);
            jSpinner_istirahat_2.setEnabled(false);
        }
    }//GEN-LAST:event_CheckBox_masuk2ItemStateChanged

    private void CheckBox_masuk3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_CheckBox_masuk3ItemStateChanged
        // TODO add your handling code here:
        if (CheckBox_masuk3.isSelected()) {
            label_hari3.setEnabled(true);
            jSpinner_jam_masuk_3.setEnabled(true);
            jSpinner_menit_masuk_3.setEnabled(true);
            jSpinner_jam_pulang_3.setEnabled(true);
            jSpinner_menit_pulang_3.setEnabled(true);
            jSpinner_istirahat_3.setEnabled(true);
        } else {
            label_hari3.setEnabled(false);
            jSpinner_jam_masuk_3.setEnabled(false);
            jSpinner_menit_masuk_3.setEnabled(false);
            jSpinner_jam_pulang_3.setEnabled(false);
            jSpinner_menit_pulang_3.setEnabled(false);
            jSpinner_istirahat_3.setEnabled(false);
        }
    }//GEN-LAST:event_CheckBox_masuk3ItemStateChanged

    private void CheckBox_masuk4ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_CheckBox_masuk4ItemStateChanged
        // TODO add your handling code here:
        if (CheckBox_masuk4.isSelected()) {
            label_hari4.setEnabled(true);
            jSpinner_jam_masuk_4.setEnabled(true);
            jSpinner_menit_masuk_4.setEnabled(true);
            jSpinner_jam_pulang_4.setEnabled(true);
            jSpinner_menit_pulang_4.setEnabled(true);
            jSpinner_istirahat_4.setEnabled(true);
        } else {
            label_hari4.setEnabled(false);
            jSpinner_jam_masuk_4.setEnabled(false);
            jSpinner_menit_masuk_4.setEnabled(false);
            jSpinner_jam_pulang_4.setEnabled(false);
            jSpinner_menit_pulang_4.setEnabled(false);
            jSpinner_istirahat_4.setEnabled(false);
        }
    }//GEN-LAST:event_CheckBox_masuk4ItemStateChanged

    private void CheckBox_masuk5ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_CheckBox_masuk5ItemStateChanged
        // TODO add your handling code here:
        if (CheckBox_masuk5.isSelected()) {
            label_hari5.setEnabled(true);
            jSpinner_jam_masuk_5.setEnabled(true);
            jSpinner_menit_masuk_5.setEnabled(true);
            jSpinner_jam_pulang_5.setEnabled(true);
            jSpinner_menit_pulang_5.setEnabled(true);
            jSpinner_istirahat_5.setEnabled(true);
        } else {
            label_hari5.setEnabled(false);
            jSpinner_jam_masuk_5.setEnabled(false);
            jSpinner_menit_masuk_5.setEnabled(false);
            jSpinner_jam_pulang_5.setEnabled(false);
            jSpinner_menit_pulang_5.setEnabled(false);
            jSpinner_istirahat_5.setEnabled(false);
        }
    }//GEN-LAST:event_CheckBox_masuk5ItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox CheckBox_masuk1;
    private javax.swing.JCheckBox CheckBox_masuk2;
    private javax.swing.JCheckBox CheckBox_masuk3;
    private javax.swing.JCheckBox CheckBox_masuk4;
    private javax.swing.JCheckBox CheckBox_masuk5;
    private javax.swing.JCheckBox CheckBox_masuk6;
    private javax.swing.JCheckBox CheckBox_masuk7;
    private javax.swing.JComboBox<String> ComboBox_bagian;
    private javax.swing.JComboBox<String> ComboBox_departemen;
    private javax.swing.JComboBox<String> ComboBox_jam_kerja;
    private javax.swing.JComboBox<String> ComboBox_jenis_kelamin;
    private javax.swing.JComboBox<String> ComboBox_posisi;
    private javax.swing.JTable Table_Jam_kerja;
    private javax.swing.JButton button_delete;
    private javax.swing.JButton button_edit;
    private javax.swing.JButton button_new;
    public static javax.swing.JButton button_search_karyawan;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel_search_karyawan;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSpinner jSpinner_istirahat_1;
    private javax.swing.JSpinner jSpinner_istirahat_2;
    private javax.swing.JSpinner jSpinner_istirahat_3;
    private javax.swing.JSpinner jSpinner_istirahat_4;
    private javax.swing.JSpinner jSpinner_istirahat_5;
    private javax.swing.JSpinner jSpinner_istirahat_6;
    private javax.swing.JSpinner jSpinner_istirahat_7;
    private javax.swing.JSpinner jSpinner_jam_masuk_1;
    private javax.swing.JSpinner jSpinner_jam_masuk_2;
    private javax.swing.JSpinner jSpinner_jam_masuk_3;
    private javax.swing.JSpinner jSpinner_jam_masuk_4;
    private javax.swing.JSpinner jSpinner_jam_masuk_5;
    private javax.swing.JSpinner jSpinner_jam_masuk_6;
    private javax.swing.JSpinner jSpinner_jam_masuk_7;
    private javax.swing.JSpinner jSpinner_jam_pulang_1;
    private javax.swing.JSpinner jSpinner_jam_pulang_2;
    private javax.swing.JSpinner jSpinner_jam_pulang_3;
    private javax.swing.JSpinner jSpinner_jam_pulang_4;
    private javax.swing.JSpinner jSpinner_jam_pulang_5;
    private javax.swing.JSpinner jSpinner_jam_pulang_6;
    private javax.swing.JSpinner jSpinner_jam_pulang_7;
    private javax.swing.JSpinner jSpinner_menit_masuk_1;
    private javax.swing.JSpinner jSpinner_menit_masuk_2;
    private javax.swing.JSpinner jSpinner_menit_masuk_3;
    private javax.swing.JSpinner jSpinner_menit_masuk_4;
    private javax.swing.JSpinner jSpinner_menit_masuk_5;
    private javax.swing.JSpinner jSpinner_menit_masuk_6;
    private javax.swing.JSpinner jSpinner_menit_masuk_7;
    private javax.swing.JSpinner jSpinner_menit_pulang_1;
    private javax.swing.JSpinner jSpinner_menit_pulang_2;
    private javax.swing.JSpinner jSpinner_menit_pulang_3;
    private javax.swing.JSpinner jSpinner_menit_pulang_4;
    private javax.swing.JSpinner jSpinner_menit_pulang_5;
    private javax.swing.JSpinner jSpinner_menit_pulang_6;
    private javax.swing.JSpinner jSpinner_menit_pulang_7;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label_hari1;
    private javax.swing.JLabel label_hari2;
    private javax.swing.JLabel label_hari3;
    private javax.swing.JLabel label_hari4;
    private javax.swing.JLabel label_hari5;
    private javax.swing.JLabel label_hari6;
    private javax.swing.JLabel label_hari7;
    private javax.swing.JLabel label_total;
    private javax.swing.JLabel label_total_karyawan_in;
    private javax.swing.JLabel label_total_karyawan_in_jamKerja;
    public static javax.swing.JTable table_karyawan;
    private javax.swing.JTextField txt_keterangan;
    private javax.swing.JTextField txt_kode;
    private javax.swing.JTextField txt_search_id;
    private javax.swing.JTextField txt_search_karyawan;
    // End of variables declaration//GEN-END:variables
}
