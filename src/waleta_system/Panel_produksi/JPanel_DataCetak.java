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
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;

public class JPanel_DataCetak extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_DataCetak() {
        initComponents();
        Table_Data_Cetak.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && Table_Data_Cetak.getSelectedRow() != -1) {
                    int row = Table_Data_Cetak.getSelectedRow();
                    if (row != -1) {
                        label_lp.setText(Table_Data_Cetak.getValueAt(row, 0).toString());
                        refreshTable_Pekerja_cetak();
                        if (Table_Data_Cetak.getValueAt(row, 11) == null) {
                            button_cetak_edit.setEnabled(false);
                            button_cetak_setor_lp.setEnabled(true);
                        } else {
                            button_cetak_edit.setEnabled(true);
                            button_cetak_setor_lp.setEnabled(false);
                        }
                    }
                }
            }
        });

        table_data_pekerja_cetak.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && table_data_pekerja_cetak.getSelectedRow() != -1) {
                    int i = table_data_pekerja_cetak.getSelectedRow();
                }
            }
        });
    }

    public void init() {
        refreshTable_Cetak();
    }

    public void refreshTable_Cetak() {
        try {
            decimalFormat.setMaximumFractionDigits(2);
            float tot_mk = 0, tot_pch = 0, tot_flat = 0, tot_jidun = 0, total_kpg_lp = 0, total_gram_lp = 0;
            DefaultTableModel model = (DefaultTableModel) Table_Data_Cetak.getModel();
            model.setRowCount(0);
            String filter_tanggal = "";
            if (Date1_cetak.getDate() != null && Date2_cetak.getDate() != null) {
                if (ComboBox_filterTgl.getSelectedIndex() == 0) {
                    filter_tanggal = "AND `tb_cetak`.`tgl_mulai_cetak` BETWEEN '" + dateFormat.format(Date1_cetak.getDate()) + "' and '" + dateFormat.format(Date2_cetak.getDate()) + "'\n";
                } else if (ComboBox_filterTgl.getSelectedIndex() == 1) {
                    filter_tanggal = "AND `tb_cetak`.`tgl_selesai_cetak` BETWEEN '" + dateFormat.format(Date1_cetak.getDate()) + "' and '" + dateFormat.format(Date2_cetak.getDate()) + "'\n";
                }
            }
            sql = "SELECT `tb_cetak`.`no_laporan_produksi`, `kode_grade`, `ruangan`, `jumlah_keping`, `berat_basah`, `tgl_mulai_cetak`, `cetak_diterima`, `tgl_selesai_cetak`, `cetak_diserahkan`, "
                    + "CTK1.`nama_pegawai` AS 'pekerja_cetak1', CTK2.`nama_pegawai` AS 'pekerja_cetak2', `cetak_dikoreksi`, `cetak_mangkok`, `cetak_pecah`, `cetak_flat`, `cetak_jidun`, `cetak_jidun_real`, `admin_cetak` \n"
                    + "FROM `tb_cetak` "
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_karyawan` CTK1 ON `tb_cetak`.`cetak_dikerjakan1` = CTK1.`id_pegawai`\n"
                    + "LEFT JOIN `tb_karyawan` CTK2 ON `tb_cetak`.`cetak_dikerjakan` = CTK2.`id_pegawai`\n"
                    + "WHERE `tb_cetak`.`no_laporan_produksi` LIKE '%" + txt_search_cetak.getText() + "%' "
                    + filter_tanggal
                    + "ORDER BY `tb_cetak`.`tgl_mulai_cetak` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[30];
            while (rs.next()) {
                float mk = rs.getInt("cetak_mangkok");
                float pecah = rs.getInt("cetak_pecah");
                float flat = rs.getInt("cetak_flat");
                float jidun = rs.getInt("cetak_jidun");
                float jidun_real = rs.getInt("cetak_jidun_real");
                float kpg_lp = rs.getInt("jumlah_keping");
                float gram_lp = rs.getInt("berat_basah");
                float persen_mk = (mk / kpg_lp) * 100;
                float persen_pecah = (pecah / kpg_lp) * 100;
                float persen_flat = (flat / kpg_lp) * 100;
                float persen_jidun = (jidun / kpg_lp) * 100;
                tot_mk = tot_mk + mk;
                tot_pch = tot_pch + pecah;
                tot_flat = tot_flat + flat;
                tot_jidun = tot_jidun + flat;
                total_kpg_lp = total_kpg_lp + kpg_lp;
                total_gram_lp = total_gram_lp + gram_lp;

                row[0] = rs.getString("no_laporan_produksi");
                row[1] = rs.getString("kode_grade");
                row[2] = rs.getString("ruangan");
                row[3] = kpg_lp;
                row[4] = gram_lp;
                row[5] = rs.getDate("tgl_mulai_cetak");
                row[6] = rs.getString("cetak_diterima");
                row[7] = rs.getString("pekerja_cetak1");
                row[8] = rs.getString("pekerja_cetak2");
                row[9] = rs.getString("cetak_dikoreksi");
                row[10] = rs.getString("cetak_diserahkan");
                row[11] = rs.getDate("tgl_selesai_cetak");
                row[12] = mk;
                row[13] = persen_mk;
                row[14] = pecah;
                row[15] = persen_pecah;
                row[16] = flat;
                row[17] = persen_flat;
                row[18] = jidun;
                row[19] = persen_jidun;
                row[20] = jidun_real;
                row[21] = rs.getString("admin_cetak");
                model.addRow(row);

            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_Data_Cetak);
            float rata2_mk = (tot_mk / total_kpg_lp) * 100;
            float rata2_pch = (tot_pch / total_kpg_lp) * 100;
            float rata2_flat = (tot_flat / total_kpg_lp) * 100;
            float rata2_jidun = (tot_jidun / total_kpg_lp) * 100;

            int rowData = Table_Data_Cetak.getRowCount();
            label_total_data_cetak.setText(Integer.toString(rowData));
            label_total_mk.setText(decimalFormat.format(tot_mk));
            label_total_mk1.setText("(" + decimalFormat.format(rata2_mk) + "%)");
            label_total_pecah.setText(decimalFormat.format(tot_pch));
            label_total_pecah1.setText("(" + decimalFormat.format(rata2_pch) + "%)");
            label_total_flat.setText(decimalFormat.format(tot_flat));
            label_total_flat1.setText("(" + decimalFormat.format(rata2_flat) + "%)");
            label_total_jidun.setText(decimalFormat.format(tot_jidun));
            label_total_jidun1.setText("(" + decimalFormat.format(rata2_jidun) + "%)");
            label_total_kpg.setText(decimalFormat.format(total_kpg_lp));
            label_total_gram.setText(decimalFormat.format(total_gram_lp));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataCetak.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_Pekerja_cetak() {
        try {
            DefaultTableModel model = (DefaultTableModel) table_data_pekerja_cetak.getModel();
            model.setRowCount(0);
            int total_kpg = 0;
            float total_gram = 0;
            sql = "SELECT `nomor`, `no_laporan_produksi`, `tb_detail_pencetak`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `bagian`, `tanggal_cetak`, `kpg_cetak`, `gram_cetak`, "
                    + "`cetak_mangkok`, `cetak_pecah`, `cetak_flat`, `cetak_jidun`, `cetak_jidun_real` \n"
                    + "FROM `tb_detail_pencetak` \n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_detail_pencetak`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                    + "WHERE `no_laporan_produksi` = '" + label_lp.getText() + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[15];
            while (rs.next()) {
                row[0] = rs.getString("nomor");
                row[1] = rs.getDate("tanggal_cetak");
                row[2] = rs.getString("id_pegawai");
                row[3] = rs.getString("nama_pegawai");
                row[4] = rs.getString("bagian");
                row[5] = rs.getInt("kpg_cetak");
                row[6] = rs.getFloat("gram_cetak");
                row[7] = rs.getInt("cetak_mangkok");
                row[8] = rs.getInt("cetak_pecah");
                row[9] = rs.getInt("cetak_flat");
                row[10] = rs.getInt("cetak_jidun");
                row[11] = rs.getInt("cetak_jidun_real");
                model.addRow(row);
                total_kpg += rs.getInt("kpg_cetak");
                total_gram += rs.getFloat("gram_cetak");
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_data_pekerja_cetak);
            label_total_kpg_pekerja_cetak.setText(decimalFormat.format(total_kpg));
            label_total_gram_pekerja_cetak.setText(decimalFormat.format(total_gram));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataCetak.class.getName()).log(Level.SEVERE, null, ex);
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

        jPanel1 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        Table_Data_Cetak = new javax.swing.JTable();
        button_cetak_terima_lp = new javax.swing.JButton();
        button_cetak_setor_lp = new javax.swing.JButton();
        button_cetak_edit = new javax.swing.JButton();
        button_cetak_delete = new javax.swing.JButton();
        label_total_data_cetak = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        button_export_f2 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        label_total_mk = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        label_total_pecah = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        label_total_flat = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        label_total_kpg = new javax.swing.JLabel();
        label_total_mk1 = new javax.swing.JLabel();
        label_total_pecah1 = new javax.swing.JLabel();
        label_total_flat1 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        label_total_jidun = new javax.swing.JLabel();
        label_total_jidun1 = new javax.swing.JLabel();
        label_total_gram = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        label_total_cabutan2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_data_pekerja_cetak = new javax.swing.JTable();
        label_total_gram_pekerja_cetak = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        label_total_cabutan1 = new javax.swing.JLabel();
        label_total_kpg_pekerja_cetak = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        label_lp = new javax.swing.JLabel();
        label_lp1 = new javax.swing.JLabel();
        button_delete_pekerja_cetak = new javax.swing.JButton();
        button_add_pekerja_cetak = new javax.swing.JButton();
        button_edit_pekerja_cetak = new javax.swing.JButton();
        button_search_cetak = new javax.swing.JButton();
        txt_search_cetak = new javax.swing.JTextField();
        button_laporan_terima_cetak_f2 = new javax.swing.JButton();
        button_catatan_pengeringan_sarang_burung = new javax.swing.JButton();
        button_laporan_terima_cetak_sub = new javax.swing.JButton();
        button_laporan_terima_cetak = new javax.swing.JButton();
        ComboBox_filterTgl = new javax.swing.JComboBox<>();
        Date2_cetak = new com.toedter.calendar.JDateChooser();
        Date1_cetak = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        button_jumlah_lp_target_disetor = new javax.swing.JButton();
        button_laporan_setor_cetak = new javax.swing.JButton();
        button_laporan_terima_cetak1 = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        Table_Data_Cetak.setAutoCreateRowSorter(true);
        Table_Data_Cetak.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_Data_Cetak.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP", "Grade", "Ruang", "Total Keping", "Gram", "Tgl Masuk", "Diterima", "Pekerja Cetak 1", "Pekerja Cetak 2", "Pengoreksi", "Diserahkan", "Tgl Selesai", "Mk (Kpg)", "Mk (%)", "Pecah (Kpg)", "Pecah (%)", "Flat (Kpg)", "Flat (%)", "Jidun", "Jidun(%)", "Jidun Real", "admin"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_Data_Cetak.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(Table_Data_Cetak);

        button_cetak_terima_lp.setBackground(new java.awt.Color(255, 255, 255));
        button_cetak_terima_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_cetak_terima_lp.setText("Terima LP");
        button_cetak_terima_lp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_cetak_terima_lpActionPerformed(evt);
            }
        });

        button_cetak_setor_lp.setBackground(new java.awt.Color(255, 255, 255));
        button_cetak_setor_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_cetak_setor_lp.setText("Setor LP");
        button_cetak_setor_lp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_cetak_setor_lpActionPerformed(evt);
            }
        });

        button_cetak_edit.setBackground(new java.awt.Color(255, 255, 255));
        button_cetak_edit.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_cetak_edit.setText("Edit");
        button_cetak_edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_cetak_editActionPerformed(evt);
            }
        });

        button_cetak_delete.setBackground(new java.awt.Color(255, 255, 255));
        button_cetak_delete.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_cetak_delete.setText("Delete");
        button_cetak_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_cetak_deleteActionPerformed(evt);
            }
        });

        label_total_data_cetak.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_cetak.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_data_cetak.setText("TOTAL");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Total Data :");

        button_export_f2.setBackground(new java.awt.Color(255, 255, 255));
        button_export_f2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_f2.setText("Export to Excel");
        button_export_f2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_f2ActionPerformed(evt);
            }
        });

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("MK :");

        label_total_mk.setBackground(new java.awt.Color(255, 255, 255));
        label_total_mk.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_mk.setText("TOTAL");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText("Pecah :");

        label_total_pecah.setBackground(new java.awt.Color(255, 255, 255));
        label_total_pecah.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_pecah.setText("TOTAL");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("Flat :");

        label_total_flat.setBackground(new java.awt.Color(255, 255, 255));
        label_total_flat.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_flat.setText("TOTAL");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel10.setText("Total Keping :");

        label_total_kpg.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_kpg.setText("TOTAL");

        label_total_mk1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_mk1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_mk1.setText("(0%)");

        label_total_pecah1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_pecah1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_pecah1.setText("(0%)");

        label_total_flat1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_flat1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_flat1.setText("(0%)");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel11.setText("Jidun :");

        label_total_jidun.setBackground(new java.awt.Color(255, 255, 255));
        label_total_jidun.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_jidun.setText("TOTAL");

        label_total_jidun1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_jidun1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_jidun1.setText("(0%)");

        label_total_gram.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_gram.setText("TOTAL");

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel12.setText("Total Gram :");

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        label_total_cabutan2.setBackground(new java.awt.Color(255, 255, 255));
        label_total_cabutan2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_cabutan2.setText("Gram");

        table_data_pekerja_cetak.setAutoCreateRowSorter(true);
        table_data_pekerja_cetak.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_pekerja_cetak.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Tanggal", "ID Pegawai", "Nama", "Bagian", "Kpg", "Gram", "MK", "Pecah", "Flat", "Jidun", "Jidun Real"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(table_data_pekerja_cetak);

        label_total_gram_pekerja_cetak.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_pekerja_cetak.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_gram_pekerja_cetak.setText("8888");

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel17.setText("Total Gram :");

        label_total_cabutan1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_cabutan1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_cabutan1.setText("Keping");

        label_total_kpg_pekerja_cetak.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg_pekerja_cetak.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_kpg_pekerja_cetak.setText("88");

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText("Total Kpg :");

        label_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_lp.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_lp.setForeground(new java.awt.Color(255, 0, 0));
        label_lp.setText("Laporan Produksi");

        label_lp1.setBackground(new java.awt.Color(255, 255, 255));
        label_lp1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_lp1.setText("Pekerja Cetak");

        button_delete_pekerja_cetak.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_pekerja_cetak.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete_pekerja_cetak.setText("Delete");
        button_delete_pekerja_cetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_pekerja_cetakActionPerformed(evt);
            }
        });

        button_add_pekerja_cetak.setBackground(new java.awt.Color(255, 255, 255));
        button_add_pekerja_cetak.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_add_pekerja_cetak.setText("Add");
        button_add_pekerja_cetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_add_pekerja_cetakActionPerformed(evt);
            }
        });

        button_edit_pekerja_cetak.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_pekerja_cetak.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_edit_pekerja_cetak.setText("Edit");
        button_edit_pekerja_cetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_pekerja_cetakActionPerformed(evt);
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
                        .addComponent(label_lp1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_lp)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(button_add_pekerja_cetak)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_edit_pekerja_cetak)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_delete_pekerja_cetak)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_kpg_pekerja_cetak)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_cabutan1)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_pekerja_cetak)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_cabutan2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_lp)
                    .addComponent(label_lp1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram_pekerja_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_cabutan2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_kpg_pekerja_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_cabutan1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_delete_pekerja_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_edit_pekerja_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_add_pekerja_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        button_search_cetak.setBackground(new java.awt.Color(255, 255, 255));
        button_search_cetak.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_cetak.setText("Search");
        button_search_cetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_cetakActionPerformed(evt);
            }
        });

        txt_search_cetak.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_cetak.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_cetakKeyPressed(evt);
            }
        });

        button_laporan_terima_cetak_f2.setBackground(new java.awt.Color(255, 255, 255));
        button_laporan_terima_cetak_f2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_laporan_terima_cetak_f2.setText("Laporan Terima Cetak F2");
        button_laporan_terima_cetak_f2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_laporan_terima_cetak_f2ActionPerformed(evt);
            }
        });

        button_catatan_pengeringan_sarang_burung.setBackground(new java.awt.Color(255, 255, 255));
        button_catatan_pengeringan_sarang_burung.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_catatan_pengeringan_sarang_burung.setText("Catatan Pengeringan Sarang Burung");
        button_catatan_pengeringan_sarang_burung.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_catatan_pengeringan_sarang_burungActionPerformed(evt);
            }
        });

        button_laporan_terima_cetak_sub.setBackground(new java.awt.Color(255, 255, 255));
        button_laporan_terima_cetak_sub.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_laporan_terima_cetak_sub.setText("Laporan Terima Cetak Sub");
        button_laporan_terima_cetak_sub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_laporan_terima_cetak_subActionPerformed(evt);
            }
        });

        button_laporan_terima_cetak.setBackground(new java.awt.Color(255, 255, 255));
        button_laporan_terima_cetak.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_laporan_terima_cetak.setText("Laporan Terima Cetak tanpa F2");
        button_laporan_terima_cetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_laporan_terima_cetakActionPerformed(evt);
            }
        });

        ComboBox_filterTgl.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_filterTgl.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tanggal Masuk", "Tanggal Setor" }));

        Date2_cetak.setBackground(new java.awt.Color(255, 255, 255));
        Date2_cetak.setDate(new Date());
        Date2_cetak.setDateFormatString("dd MMMM yyyy");
        Date2_cetak.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date1_cetak.setBackground(new java.awt.Color(255, 255, 255));
        Date1_cetak.setDate(new Date(new Date().getTime()- (7 * 24 * 60 * 60 * 1000)));
        Date1_cetak.setDateFormatString("dd MMMM yyyy");
        Date1_cetak.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Laporan Produksi :");

        button_jumlah_lp_target_disetor.setBackground(new java.awt.Color(255, 255, 255));
        button_jumlah_lp_target_disetor.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_jumlah_lp_target_disetor.setText("Jumlah LP Target disetor");
        button_jumlah_lp_target_disetor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_jumlah_lp_target_disetorActionPerformed(evt);
            }
        });

        button_laporan_setor_cetak.setBackground(new java.awt.Color(255, 255, 255));
        button_laporan_setor_cetak.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_laporan_setor_cetak.setText("Laporan Setor Cetak");
        button_laporan_setor_cetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_laporan_setor_cetakActionPerformed(evt);
            }
        });

        button_laporan_terima_cetak1.setBackground(new java.awt.Color(255, 255, 255));
        button_laporan_terima_cetak1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_laporan_terima_cetak1.setText("Laporan Terima Cetak dengan F2");
        button_laporan_terima_cetak1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_laporan_terima_cetak1ActionPerformed(evt);
            }
        });

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel16.setText("DATA CETAK");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(button_cetak_terima_lp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_cetak_setor_lp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_cetak_edit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_cetak_delete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export_f2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_kpg)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_mk)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_mk1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_pecah)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_pecah1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_flat)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_flat1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_jidun)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_jidun1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data_cetak))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_filterTgl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date1_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date2_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(button_search_cetak))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(button_laporan_terima_cetak)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_laporan_terima_cetak1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_laporan_terima_cetak_f2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_laporan_terima_cetak_sub)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_catatan_pengeringan_sarang_burung)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_laporan_setor_cetak)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_jumlah_lp_target_disetor))
                            .addComponent(jLabel16))
                        .addGap(0, 133, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_filterTgl, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date1_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date2_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_laporan_terima_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_laporan_terima_cetak_sub, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_laporan_terima_cetak_f2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_catatan_pengeringan_sarang_burung, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_laporan_terima_cetak1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_laporan_setor_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_jumlah_lp_target_disetor, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(button_cetak_terima_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_cetak_setor_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_cetak_edit, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_cetak_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_export_f2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_data_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_mk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_mk1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_pecah, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_pecah1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_flat, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_flat1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_kpg, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_jidun, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_jidun1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txt_search_cetakKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_cetakKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_Cetak();
        }
    }//GEN-LAST:event_txt_search_cetakKeyPressed

    private void button_search_cetakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_cetakActionPerformed
        // TODO add your handling code here:
        refreshTable_Cetak();
    }//GEN-LAST:event_button_search_cetakActionPerformed

    private void button_cetak_terima_lpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_cetak_terima_lpActionPerformed
        // TODO add your handling code here:
        JDialog_Terima_LP_Cetak terima_lp = new JDialog_Terima_LP_Cetak(new javax.swing.JFrame(), true);
        terima_lp.pack();
        terima_lp.setLocationRelativeTo(this);
        terima_lp.setVisible(true);
        terima_lp.setEnabled(true);
        refreshTable_Cetak();
    }//GEN-LAST:event_button_cetak_terima_lpActionPerformed

    private void button_cetak_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_cetak_deleteActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_Data_Cetak.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Please Select Row Data that you want to Delete !");
            } else {
                sql = "SELECT `no_laporan_produksi` FROM `tb_finishing_2` WHERE `tb_finishing_2`.`no_laporan_produksi` = '" + Table_Data_Cetak.getValueAt(j, 0) + "'";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Maaf tidak bisa hapus No LP ini karena sudah masuk F2, mohon hapus data F2 terlebih dahulu!");
                } else {
                    int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Warning", 0);
                    if (dialogResult == JOptionPane.YES_OPTION) {
                        // delete code here
                        String Query = "DELETE FROM `tb_cetak` WHERE `tb_cetak`.`no_laporan_produksi` = '" + Table_Data_Cetak.getValueAt(j, 0) + "'";
                        Utility.db.getConnection().createStatement();
                        if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                            JOptionPane.showMessageDialog(this, "data deleted!");
                            refreshTable_Cetak();
                        } else {
                            JOptionPane.showMessageDialog(this, "Delete Failed!");
                        }
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_DataCetak.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_cetak_deleteActionPerformed

    private void button_cetak_editActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_cetak_editActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_Data_Cetak.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Please Select Row Data that you want to change !");
            } else {
                JDialog_Edit_Data_Cetak edit_cetak = new JDialog_Edit_Data_Cetak(new javax.swing.JFrame(), true);
                edit_cetak.pack();
                edit_cetak.setLocationRelativeTo(this);
                edit_cetak.setVisible(true);
                edit_cetak.setEnabled(true);
            }
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_DataCetak.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_cetak_editActionPerformed

    private void button_cetak_setor_lpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_cetak_setor_lpActionPerformed
        // TODO add your handling code here:
        int j = Table_Data_Cetak.getSelectedRow();
        if (j == -1) {
            JOptionPane.showMessageDialog(this, "Anda belum memilih LP yang akan di setorkan !");
        } else {
            if (Table_Data_Cetak.getValueAt(j, 11) != null) {
                JOptionPane.showMessageDialog(this, "No Laporan Produksi : " + Table_Data_Cetak.getValueAt(j, 0).toString() + "\n Sudah disetorkan");
            } else {
                String no_lp = Table_Data_Cetak.getValueAt(j, 0).toString();
                JDialog_Setor_LP_Cetak setor_lp = new JDialog_Setor_LP_Cetak(new javax.swing.JFrame(), true, no_lp);
                setor_lp.pack();
                setor_lp.setLocationRelativeTo(this);
                setor_lp.setVisible(true);
                setor_lp.setEnabled(true);
                refreshTable_Cetak();
            }
        }
    }//GEN-LAST:event_button_cetak_setor_lpActionPerformed

    private void button_export_f2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_f2ActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_Data_Cetak.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_export_f2ActionPerformed

    private void button_laporan_terima_cetakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_laporan_terima_cetakActionPerformed
        // TODO add your handling code here:
        try {
            String tgl = "";
            if (Date1_cetak.getDate() != null && Date2_cetak.getDate() != null) {
                if (ComboBox_filterTgl.getSelectedIndex() == 0) {
                    tgl = "AND `tb_cetak`.`tgl_mulai_cetak` BETWEEN '" + dateFormat.format(Date1_cetak.getDate()) + "' and '" + dateFormat.format(Date2_cetak.getDate()) + "' ";
                } else if (ComboBox_filterTgl.getSelectedIndex() == 1) {
                    tgl = "AND `tb_cetak`.`tgl_selesai_cetak` BETWEEN '" + dateFormat.format(Date1_cetak.getDate()) + "' and '" + dateFormat.format(Date2_cetak.getDate()) + "' ";
                }
            } else {
                tgl = "AND `tgl_mulai_cetak` = CURDATE()";
            }
            String query = "SELECT `tb_cetak`.`no_laporan_produksi`, `no_kartu_waleta`, `memo_lp`, `tgl_mulai_cetak`, `ruangan`, `tb_laporan_produksi`.`jumlah_keping`, `tb_laporan_produksi`.`berat_basah`, `tb_laporan_produksi`.`kode_grade` \n"
                    + "FROM `tb_cetak` "
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN (SELECT MAX(`id_pegawai`) AS 'last_id', `nama_pegawai` FROM `tb_karyawan` WHERE 1 GROUP BY `nama_pegawai`) data_karyawan ON `tb_cetak`.`cetak_diterima` = data_karyawan.`nama_pegawai`\n"
                    + "LEFT JOIN `tb_karyawan` ON data_karyawan.`last_id` = `tb_karyawan`.`id_pegawai` "
                    + "WHERE `tb_cetak`.`no_laporan_produksi` LIKE 'WL-%' " + tgl + " "
                    + "AND `tb_karyawan`.`kode_bagian` NOT IN ('24', '70', '86', '94') "
                    + "ORDER BY `ruangan`, `no_laporan_produksi`";

            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(query);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Laporan_cetak.jrxml");
            JASP_DESIGN.setQuery(newQuery);
            Map<String, Object> map = new HashMap<>();
            map.put("SUBREPORT_DIR", "Report\\");
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
        } catch (JRException ex) {
            Logger.getLogger(JPanel_DataCetak.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_laporan_terima_cetakActionPerformed

    private void button_laporan_terima_cetak_subActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_laporan_terima_cetak_subActionPerformed
        // TODO add your handling code here:
        try {
            String query = "";
            if (Date1_cetak.getDate() != null && Date2_cetak.getDate() != null) {
                String tgl = "";
                if (ComboBox_filterTgl.getSelectedIndex() == 0) {
                    tgl = "tgl_mulai_cetak";
                } else if (ComboBox_filterTgl.getSelectedIndex() == 1) {
                    tgl = "tgl_selesai_cetak";
                }
                query = "SELECT `tb_cetak`.`no_laporan_produksi`, `no_kartu_waleta`, `memo_lp`, `tgl_mulai_cetak`, `ruangan`, `tb_laporan_produksi`.`jumlah_keping`, `tb_laporan_produksi`.`berat_basah`, `tb_laporan_produksi`.`kode_grade`\n"
                        + "FROM `tb_cetak` LEFT JOIN `tb_laporan_produksi` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "WHERE `tb_cetak`.`" + tgl + "` BETWEEN '" + dateFormat.format(Date1_cetak.getDate()) + "' and '" + dateFormat.format(Date2_cetak.getDate()) + "' AND `tb_cetak`.`no_laporan_produksi` LIKE 'WL.%' ORDER BY `ruangan`";
            } else {
                query = "SELECT `tb_cetak`.`no_laporan_produksi`, `no_kartu_waleta`, `memo_lp`, `tgl_mulai_cetak`, `ruangan`, `tb_laporan_produksi`.`jumlah_keping`, `tb_laporan_produksi`.`berat_basah`, `tb_laporan_produksi`.`kode_grade`\n"
                        + "FROM `tb_cetak` LEFT JOIN `tb_laporan_produksi` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "WHERE `tgl_mulai_cetak` = CURDATE() AND `tb_cetak`.`no_laporan_produksi` LIKE 'WL.%' ORDER BY `ruangan`";
            }
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(query);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Laporan_cetak_SUB.jrxml");
            JASP_DESIGN.setQuery(newQuery);
            Map<String, Object> map = new HashMap<>();
            map.put("SUBREPORT_DIR", "Report\\");
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
        } catch (JRException ex) {
            Logger.getLogger(JPanel_DataCetak.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_laporan_terima_cetak_subActionPerformed

    private void button_catatan_pengeringan_sarang_burungActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_catatan_pengeringan_sarang_burungActionPerformed
        // TODO add your handling code here:
        try {
            if (Date1_cetak.getDate() != null && Date2_cetak.getDate() != null) {
                String tgl = "";
                if (ComboBox_filterTgl.getSelectedIndex() == 0) {
                    tgl = "tgl_mulai_cetak";
                } else if (ComboBox_filterTgl.getSelectedIndex() == 1) {
                    tgl = "tgl_selesai_cetak";
                }
                String query = "SELECT `tb_cetak`.`no_laporan_produksi`, `tb_laporan_produksi`.`no_kartu_waleta`, `no_registrasi` AS 'kode_rsb', `kode_grade`, `cetak_diterima`, `tgl_selesai_cetak`, `cetak_diserahkan`, `cetak_mangkok`, `cetak_pecah`, `cetak_flat`, `cetak_jidun`, `ruangan`, `jumlah_keping`, `berat_basah`, `waktu_mulai_pengeringan`, `waktu_selesai_pengeringan` \n"
                        + "FROM `tb_cetak` "
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "LEFT JOIN `tb_bahan_baku_masuk_cheat` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta`\n"
                        + "WHERE `tb_cetak`.`" + tgl + "` BETWEEN '" + dateFormat.format(Date1_cetak.getDate()) + "' and '" + dateFormat.format(Date2_cetak.getDate()) + "' AND `tb_cetak`.`no_laporan_produksi` LIKE '%" + txt_search_cetak.getText() + "%' "
                        + " AND `tgl_selesai_cetak` IS NOT NULL "
                        + "ORDER BY `ruangan`, `tb_cetak`.`no_laporan_produksi` ASC";
                JRDesignQuery newQuery = new JRDesignQuery();
                newQuery.setText(query);
                JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Catatan_Pengeringan_Sarang_Burung.jrxml");
                JASP_DESIGN.setQuery(newQuery);
                Map<String, Object> map = new HashMap<>();
                map.put("TGL_SETOR_CETAK", dateFormat.format(new Date()));
                JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
                JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
            } else {
                String query = "SELECT `tb_cetak`.`no_laporan_produksi`, `tb_laporan_produksi`.`no_kartu_waleta`, `no_registrasi` AS 'kode_rsb', `kode_grade`, `cetak_diterima`, `tgl_selesai_cetak`, `cetak_diserahkan`, `cetak_mangkok`, `cetak_pecah`, `cetak_flat`, `cetak_jidun`, `ruangan`, `jumlah_keping`, `berat_basah`, `waktu_mulai_pengeringan`, `waktu_selesai_pengeringan` \n"
                        + "FROM `tb_cetak` "
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "LEFT JOIN `tb_bahan_baku_masuk_cheat` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta`\n"
                        + "WHERE `tb_cetak`.`no_laporan_produksi` LIKE '%" + txt_search_cetak.getText() + "%'"
                        + " AND `tgl_selesai_cetak` IS NOT NULL "
                        + "ORDER BY `ruangan`, `tb_cetak`.`no_laporan_produksi` ASC";
                JRDesignQuery newQuery = new JRDesignQuery();
                newQuery.setText(query);
                JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Catatan_Pengeringan_Sarang_Burung.jrxml");
                JASP_DESIGN.setQuery(newQuery);
                Map<String, Object> map = new HashMap<>();
                map.put("TGL_SETOR_CETAK", dateFormat.format(new Date()));
                JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
                JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
            }
        } catch (JRException ex) {
            Logger.getLogger(JPanel_DataCetak.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_catatan_pengeringan_sarang_burungActionPerformed

    private void button_laporan_terima_cetak_f2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_laporan_terima_cetak_f2ActionPerformed
        // TODO add your handling code here:
        try {
            String tgl = "";
            if (Date1_cetak.getDate() != null && Date2_cetak.getDate() != null) {
                if (ComboBox_filterTgl.getSelectedIndex() == 0) {
                    tgl = "AND `tb_cetak`.`tgl_mulai_cetak` BETWEEN '" + dateFormat.format(Date1_cetak.getDate()) + "' and '" + dateFormat.format(Date2_cetak.getDate()) + "' ";
                } else if (ComboBox_filterTgl.getSelectedIndex() == 1) {
                    tgl = "AND `tb_cetak`.`tgl_selesai_cetak` BETWEEN '" + dateFormat.format(Date1_cetak.getDate()) + "' and '" + dateFormat.format(Date2_cetak.getDate()) + "' ";
                }
            } else {
                tgl = "AND `tgl_mulai_cetak` = CURDATE()";
            }
            String query = "SELECT `tb_cetak`.`no_laporan_produksi`, `no_kartu_waleta`, `memo_lp`, `tgl_mulai_cetak`, `ruangan`, `tb_laporan_produksi`.`jumlah_keping`, `tb_laporan_produksi`.`berat_basah`, `tb_laporan_produksi`.`kode_grade` \n"
                    + "FROM `tb_cetak` "
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN (SELECT MAX(`id_pegawai`) AS 'last_id', `nama_pegawai` FROM `tb_karyawan` WHERE 1 GROUP BY `nama_pegawai`) data_karyawan ON `tb_cetak`.`cetak_diterima` = data_karyawan.`nama_pegawai`\n"
                    + "LEFT JOIN `tb_karyawan` ON data_karyawan.`last_id` = `tb_karyawan`.`id_pegawai` "
                    + "WHERE `tb_cetak`.`no_laporan_produksi` LIKE 'WL-%' " + tgl + " "
                    + "AND `tb_karyawan`.`kode_bagian` IN ('24', '70', '86', '94') "
                    + "ORDER BY `ruangan`, `no_laporan_produksi`";
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(query);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Laporan_cetak_f2.jrxml");
            JASP_DESIGN.setQuery(newQuery);
            Map<String, Object> map = new HashMap<>();
            map.put("SUBREPORT_DIR", "Report\\");
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
        } catch (JRException ex) {
            Logger.getLogger(JPanel_DataCetak.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_laporan_terima_cetak_f2ActionPerformed

    private void button_laporan_terima_cetak1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_laporan_terima_cetak1ActionPerformed
        // TODO add your handling code here:
        try {
            String tgl = "";
            if (Date1_cetak.getDate() != null && Date2_cetak.getDate() != null) {
                if (ComboBox_filterTgl.getSelectedIndex() == 0) {
                    tgl = "AND `tb_cetak`.`tgl_mulai_cetak` BETWEEN '" + dateFormat.format(Date1_cetak.getDate()) + "' and '" + dateFormat.format(Date2_cetak.getDate()) + "' ";
                } else if (ComboBox_filterTgl.getSelectedIndex() == 1) {
                    tgl = "AND `tb_cetak`.`tgl_selesai_cetak` BETWEEN '" + dateFormat.format(Date1_cetak.getDate()) + "' and '" + dateFormat.format(Date2_cetak.getDate()) + "' ";
                }
            } else {
                tgl = "AND `tgl_mulai_cetak` = CURDATE()";
            }
            String query = "SELECT `tb_cetak`.`no_laporan_produksi`, `no_kartu_waleta`, `memo_lp`, `tgl_mulai_cetak`, `ruangan`, `tb_laporan_produksi`.`jumlah_keping`, `tb_laporan_produksi`.`berat_basah`, `tb_laporan_produksi`.`kode_grade` \n"
                    + "FROM `tb_cetak` "
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN (SELECT MAX(`id_pegawai`) AS 'last_id', `nama_pegawai` FROM `tb_karyawan` WHERE 1 GROUP BY `nama_pegawai`) data_karyawan ON `tb_cetak`.`cetak_diterima` = data_karyawan.`nama_pegawai`\n"
                    + "LEFT JOIN `tb_karyawan` ON data_karyawan.`last_id` = `tb_karyawan`.`id_pegawai` "
                    + "WHERE `tb_cetak`.`no_laporan_produksi` LIKE 'WL-%' " + tgl + " "
                    + "ORDER BY `ruangan`, `no_laporan_produksi`";
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(query);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Laporan_cetak_f2.jrxml");
            JASP_DESIGN.setQuery(newQuery);
            Map<String, Object> map = new HashMap<>();
            map.put("SUBREPORT_DIR", "Report\\");
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
        } catch (JRException ex) {
            Logger.getLogger(JPanel_DataCetak.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_laporan_terima_cetak1ActionPerformed

    private void button_laporan_setor_cetakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_laporan_setor_cetakActionPerformed
        // TODO add your handling code here:
        try {
            String tgl = "";
            if (Date1_cetak.getDate() != null && Date2_cetak.getDate() != null) {
                if (ComboBox_filterTgl.getSelectedIndex() == 0) {
                    tgl = "AND `tb_cetak`.`tgl_mulai_cetak` BETWEEN '" + dateFormat.format(Date1_cetak.getDate()) + "' and '" + dateFormat.format(Date2_cetak.getDate()) + "' ";
                } else if (ComboBox_filterTgl.getSelectedIndex() == 1) {
                    tgl = "AND `tb_cetak`.`tgl_selesai_cetak` BETWEEN '" + dateFormat.format(Date1_cetak.getDate()) + "' and '" + dateFormat.format(Date2_cetak.getDate()) + "' ";
                }
            } else {
                tgl = "AND `tgl_selesai_cetak` = CURDATE()";
            }
            String query = "SELECT `tb_cetak`.`no_laporan_produksi`, `no_kartu_waleta`, `memo_lp`, `tgl_mulai_cetak`, `tgl_selesai_cetak`, `ruangan`, `tb_laporan_produksi`.`jumlah_keping`, `tb_laporan_produksi`.`berat_basah`, `tb_laporan_produksi`.`kode_grade` \n"
                    + "FROM `tb_cetak` "
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    //                    + "LEFT JOIN (SELECT MAX(`id_pegawai`) AS 'last_id', `nama_pegawai` FROM `tb_karyawan` WHERE 1 GROUP BY `nama_pegawai`) data_karyawan ON `tb_cetak`.`cetak_diterima` = data_karyawan.`nama_pegawai`\n"
                    //                    + "LEFT JOIN `tb_karyawan` ON data_karyawan.`last_id` = `tb_karyawan`.`id_pegawai` "
                    + "WHERE `tb_cetak`.`no_laporan_produksi` LIKE '%" + txt_search_cetak.getText() + "%' " + tgl + " "
                    //                    + "AND `tb_karyawan`.`kode_bagian` IN ('24', '70', '86', '94') "
                    + "ORDER BY `ruangan`, `no_laporan_produksi`";
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(query);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Laporan_Setor_Cetak.jrxml");
            JASP_DESIGN.setQuery(newQuery);
            Map<String, Object> map = new HashMap<>();
            map.put("SUBREPORT_DIR", "Report\\");
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
        } catch (JRException ex) {
            Logger.getLogger(JPanel_DataCetak.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_laporan_setor_cetakActionPerformed

    private void button_jumlah_lp_target_disetorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_jumlah_lp_target_disetorActionPerformed
        // TODO add your handling code here:
        JDialog_Rekap_TargetLPCetakdiSetor dialog = new JDialog_Rekap_TargetLPCetakdiSetor(new javax.swing.JFrame(), true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
    }//GEN-LAST:event_button_jumlah_lp_target_disetorActionPerformed

    private void button_delete_pekerja_cetakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_pekerja_cetakActionPerformed
        // TODO add your handling code here:
        try {
            int j = table_data_pekerja_cetak.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang ingin di hapus pada tabel !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    String Query = "DELETE FROM `tb_detail_pencetak` WHERE `nomor` = '" + table_data_pekerja_cetak.getValueAt(j, 0) + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                        JOptionPane.showMessageDialog(this, "data deleted Successfully");
                    } else {
                        JOptionPane.showMessageDialog(this, "delete failed!");
                    }
                    refreshTable_Pekerja_cetak();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_DataCetak.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_delete_pekerja_cetakActionPerformed

    private void button_edit_pekerja_cetakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_pekerja_cetakActionPerformed
        // TODO add your handling code here:
        int j = table_data_pekerja_cetak.getSelectedRow();
        try {
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang mau di edit !");
            } else {
                String nomor = table_data_pekerja_cetak.getValueAt(j, 0).toString();
                String no_lp = label_lp.getText();
                JDialog_Edit_Data_Cetak_Detail dialog = new JDialog_Edit_Data_Cetak_Detail(new javax.swing.JFrame(), true, nomor, no_lp);
                dialog.pack();
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
                dialog.setEnabled(true);
                refreshTable_Pekerja_cetak();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_DataCetak.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_edit_pekerja_cetakActionPerformed

    private void button_add_pekerja_cetakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_add_pekerja_cetakActionPerformed
        // TODO add your handling code here:
        decimalFormat.setGroupingUsed(false);
        int x = Table_Data_Cetak.getSelectedRow();
        if (x == -1) {
            JOptionPane.showMessageDialog(this, "Pilih No LP di tabel Cabut terlebih dahulu !");
        } else {
            String no_lp = label_lp.getText();
            JDialog_Edit_Data_Cetak_Detail dialog = new JDialog_Edit_Data_Cetak_Detail(new javax.swing.JFrame(), true, null, no_lp);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            dialog.setEnabled(true);
            refreshTable_Pekerja_cetak();
        }
    }//GEN-LAST:event_button_add_pekerja_cetakActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_filterTgl;
    private com.toedter.calendar.JDateChooser Date1_cetak;
    private com.toedter.calendar.JDateChooser Date2_cetak;
    public static javax.swing.JTable Table_Data_Cetak;
    public javax.swing.JButton button_add_pekerja_cetak;
    private javax.swing.JButton button_catatan_pengeringan_sarang_burung;
    public static javax.swing.JButton button_cetak_delete;
    public static javax.swing.JButton button_cetak_edit;
    public javax.swing.JButton button_cetak_setor_lp;
    public javax.swing.JButton button_cetak_terima_lp;
    public javax.swing.JButton button_delete_pekerja_cetak;
    public javax.swing.JButton button_edit_pekerja_cetak;
    private javax.swing.JButton button_export_f2;
    private javax.swing.JButton button_jumlah_lp_target_disetor;
    private javax.swing.JButton button_laporan_setor_cetak;
    private javax.swing.JButton button_laporan_terima_cetak;
    private javax.swing.JButton button_laporan_terima_cetak1;
    private javax.swing.JButton button_laporan_terima_cetak_f2;
    private javax.swing.JButton button_laporan_terima_cetak_sub;
    public static javax.swing.JButton button_search_cetak;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel label_lp;
    private javax.swing.JLabel label_lp1;
    private javax.swing.JLabel label_total_cabutan1;
    private javax.swing.JLabel label_total_cabutan2;
    private javax.swing.JLabel label_total_data_cetak;
    private javax.swing.JLabel label_total_flat;
    private javax.swing.JLabel label_total_flat1;
    private javax.swing.JLabel label_total_gram;
    private javax.swing.JLabel label_total_gram_pekerja_cetak;
    private javax.swing.JLabel label_total_jidun;
    private javax.swing.JLabel label_total_jidun1;
    private javax.swing.JLabel label_total_kpg;
    private javax.swing.JLabel label_total_kpg_pekerja_cetak;
    private javax.swing.JLabel label_total_mk;
    private javax.swing.JLabel label_total_mk1;
    private javax.swing.JLabel label_total_pecah;
    private javax.swing.JLabel label_total_pecah1;
    private javax.swing.JTable table_data_pekerja_cetak;
    private javax.swing.JTextField txt_search_cetak;
    // End of variables declaration//GEN-END:variables

}
