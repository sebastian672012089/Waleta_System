package waleta_system.BahanJadi;

import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import waleta_system.Class.Utility;

public class JPanel_Reproses extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_Reproses() {
        initComponents();
    }

    public void init() {
        try {
            table_data_reproses.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && table_data_reproses.getSelectedRow() != -1) {
                        int i = table_data_reproses.getSelectedRow();
                        if (table_data_reproses.getValueAt(i, 31).toString().equals("FINISHED")) {
                            button_edit_reproses.setEnabled(true);
                            button_selesai_reproses_GBJ.setEnabled(false);
                            button_selesai_reproses_QC.setEnabled(false);
                            button_input_kaki.setEnabled(false);
                            button_input_cetak.setEnabled(false);
                            button_input_f1.setEnabled(false);
                            button_input_f2.setEnabled(false);
                        } else {
                            button_edit_reproses.setEnabled(false);
                            button_selesai_reproses_GBJ.setEnabled(true);
                            button_selesai_reproses_QC.setEnabled(true);
                            button_input_kaki.setEnabled(true);
                            button_input_cetak.setEnabled(true);
                            button_input_f1.setEnabled(true);
                            button_input_f2.setEnabled(true);
                        }
                    }
                }
            });

            table_data_reproses_cabut.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && table_data_reproses_cabut.getSelectedRow() != -1) {
                        int i = table_data_reproses_cabut.getSelectedRow();
                        label_no_reproses.setText(table_data_reproses_cabut.getValueAt(i, 0).toString());
                        refreshTable_reProses_pencabut();
                        if (table_data_reproses_cabut.getValueAt(i, 5) == null) {
                            button_tgl_selesai_cabut.setEnabled(false);
                        } else {
                            button_tgl_selesai_cabut.setEnabled(true);
                        }
                        if (table_data_reproses_cabut.getValueAt(i, 6) == null) {//tanggal selesai
                            button_input_pekerja_cabut.setEnabled(true);
                            button_edit_pekerja_cabut.setEnabled(true);
                            button_delete_pekerja_cabut.setEnabled(true);
                        } else {
                            button_input_pekerja_cabut.setEnabled(false);
                            button_edit_pekerja_cabut.setEnabled(false);
                            button_delete_pekerja_cabut.setEnabled(false);
                        }
                    }
                }
            });

            String this_year = new SimpleDateFormat("yyyy").format(new Date());
            Calendar calendar = Calendar.getInstance();
            calendar.set(Integer.valueOf(this_year), new Date().getMonth(), 1);
            Date first_date = calendar.getTime();

        } catch (Exception ex) {
            Logger.getLogger(JPanel_Reproses.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_reProses() {
        try {
            int keping_reproses = 0;
            float gram_reproses = 0;
            float total_netto = 0, total_sp = 0, total_sh = 0;
            float avg_netto = 0, avg_sp = 0, avg_sh = 0;
            DefaultTableModel model = (DefaultTableModel) table_data_reproses.getModel();
            model.setRowCount(0);
            DefaultTableModel model_cabut = (DefaultTableModel) table_data_reproses_cabut.getModel();
            model_cabut.setRowCount(0);
            DefaultTableModel model_cetak = (DefaultTableModel) table_data_reproses_cetak.getModel();
            model_cetak.setRowCount(0);
            String status = ComboBox_status_reproses.getSelectedItem().toString();
            if (ComboBox_status_reproses.getSelectedItem().equals("All")) {
                status = "";
            }

            String tujuan = "";
            if (ComboBox_tujuan_reproses.getSelectedItem().equals("All")) {
                tujuan = "";
            } else {
                tujuan = "AND `bagian` = '" + ComboBox_tujuan_reproses.getSelectedItem().toString() + "'";
            }

            String filter_tanggal = "";
            if (Date_Search_reproses1.getDate() != null && Date_Search_reproses2.getDate() != null) {
                String tanggal = "";
                if (ComboBox_Filter_Tgl_reproses.getSelectedIndex() == 0) {
                    tanggal = "`tanggal_proses`";
                } else if (ComboBox_Filter_Tgl_reproses.getSelectedIndex() == 1) {
                    tanggal = "`tgl_cetak`";
                } else if (ComboBox_Filter_Tgl_reproses.getSelectedIndex() == 2) {
                    tanggal = "`tgl_f1`";
                } else if (ComboBox_Filter_Tgl_reproses.getSelectedIndex() == 3) {
                    tanggal = "`tgl_f2`";
                } else if (ComboBox_Filter_Tgl_reproses.getSelectedIndex() == 4) {
                    tanggal = "`tgl_selesai`";
                }
                filter_tanggal = "AND " + tanggal + " BETWEEN '" + dateFormat.format(Date_Search_reproses1.getDate()) + "' AND '" + dateFormat.format(Date_Search_reproses2.getDate()) + "'";
            }
            sql = "SELECT `no_reproses`, `tb_reproses`.`no_box`, `tb_box_bahan_jadi`.`kode_grade_bahan_jadi`, `tb_grade_bahan_jadi`.`kode_grade`, `tanggal_proses`, `tb_reproses`.`keping`, `gram`, `bagian`, `tgl_selesai`, `kpg_akhir`, `gram_akhir`, "
                    + "`no_lp_suwir`, `gram_kaki`, `no_lp_suwir2`, `gram_kaki2`, `rend_bersih`, `ront_bersih`, `ront_kuning`, `ront_kotor`, `hancuran`, `bonggol`, `serabut`, `tgl_f1`, f1.`nama_pegawai` AS 'pekerja_f1', `tgl_f2`, f2.`nama_pegawai` AS 'pekerja_f2', pt.`nama_pegawai` AS 'pekerja_timbang', `tb_reproses`.`status`, "
                    + "`tgl_cabut`, `tgl_cabut_selesai`, `tgl_cetak`, pc.`nama_pegawai` AS 'pekerja_cetak', `mk`, `pch`, `flat`, `jdn`, `kpg_tdk_ctk`  "
                    + "FROM `tb_reproses` "
                    + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_reproses`.`no_box` = `tb_box_bahan_jadi`.`no_box`\n"
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`"
                    + "LEFT JOIN `tb_karyawan` f1 ON `tb_reproses`.`pekerja_f1` = f1.`id_pegawai`"
                    + "LEFT JOIN `tb_karyawan` f2 ON `tb_reproses`.`pekerja_f2` = f2.`id_pegawai`"
                    + "LEFT JOIN `tb_karyawan` pt ON `tb_reproses`.`pekerja_timbang` = pt.`id_pegawai`"
                    + "LEFT JOIN `tb_karyawan` pc ON `tb_reproses`.`pekerja_cetak` = pc.`id_pegawai`"
                    + "WHERE "
                    + "`tb_reproses`.`status` LIKE '%" + status + "%' "
                    + filter_tanggal
                    + "AND `tb_reproses`.`no_box` LIKE '%" + txt_search_box_reproses.getText() + "%' "
                    + tujuan;
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[32];
            while (rs.next()) {
                row[0] = rs.getString("no_reproses");
                row[1] = rs.getString("no_box");
                row[2] = rs.getString("kode_grade");
                row[3] = rs.getDate("tanggal_proses");
                row[4] = rs.getInt("keping");
                keping_reproses = keping_reproses + rs.getInt("keping");
                row[5] = rs.getFloat("gram");
                gram_reproses = gram_reproses + rs.getFloat("gram");
                row[6] = rs.getString("bagian");
                row[7] = rs.getDate("tgl_selesai");
                row[8] = rs.getInt("kpg_akhir");
                row[9] = rs.getFloat("gram_akhir");
                row[10] = rs.getString("no_lp_suwir");
                row[11] = rs.getFloat("gram_kaki");
                row[12] = rs.getString("no_lp_suwir2");
                row[13] = rs.getFloat("gram_kaki2");
                float netto = rs.getInt("rend_bersih") - (rs.getInt("gram_kaki") + rs.getInt("gram_kaki2"));
                if (netto < 0) {
                    netto = 0;
                }
                row[14] = netto;
                float persen_netto = (netto / rs.getFloat("gram")) * 100;
                persen_netto = Math.round(persen_netto * 100) / 100.f;
                row[15] = persen_netto + "%";
                total_netto = total_netto + persen_netto;
                row[16] = rs.getInt("ront_bersih");
                row[17] = rs.getInt("ront_kuning");
                row[18] = rs.getInt("ront_kotor");
                row[19] = rs.getInt("hancuran");
                row[20] = rs.getInt("bonggol");
                row[21] = rs.getInt("serabut");
                int sp = rs.getInt("ront_bersih") + rs.getInt("ront_kuning") + rs.getInt("ront_kotor") + rs.getInt("hancuran") + rs.getInt("bonggol") + rs.getInt("serabut");
                row[22] = sp;
                float persen_sp = (sp / rs.getFloat("gram")) * 100;
                persen_sp = Math.round(persen_sp * 100) / 100.f;
                row[23] = persen_sp + "%";
                total_sp = total_sp + persen_sp;
                float sh = rs.getFloat("gram") - (netto + sp);
                row[24] = sh;
                float persen_sh = (sh / rs.getFloat("gram")) * 100;
                persen_sh = Math.round(persen_sh * 100) / 100.f;
                row[25] = persen_sh + "%";
                total_sh = total_sh + persen_sh;
                row[26] = rs.getDate("tgl_f1");
                row[27] = rs.getString("pekerja_f1");
                row[28] = rs.getDate("tgl_f2");
                row[29] = rs.getString("pekerja_f2");
                row[30] = rs.getString("pekerja_timbang");
                row[31] = rs.getString("status");
                model.addRow(row);

//                row[0] = rs.getString("no_reproses");
//                row[1] = rs.getString("no_box");
                row[2] = rs.getInt("keping");
                row[3] = rs.getInt("gram");
                row[4] = rs.getDate("tgl_cetak");
                row[5] = rs.getString("pekerja_cetak");
                row[6] = rs.getInt("mk");
                row[7] = rs.getInt("pch");
                row[8] = rs.getInt("flat");
                row[9] = rs.getInt("jdn");
                row[10] = rs.getInt("kpg_tdk_ctk");
                model_cetak.addRow(row);

                row[2] = rs.getString("kode_grade");
                row[3] = rs.getInt("keping");
                row[4] = rs.getFloat("gram");
                row[5] = rs.getDate("tgl_cabut");
                row[6] = rs.getDate("tgl_cabut_selesai");
                model_cabut.addRow(row);
            }

            avg_netto = total_netto / (float) table_data_reproses.getRowCount();
            avg_sp = total_sp / (float) table_data_reproses.getRowCount();
            avg_sh = total_sh / (float) table_data_reproses.getRowCount();
            label_total_reproses.setText(Integer.toString(table_data_reproses.getRowCount()));
            label_total_keping_reproses.setText(decimalFormat.format(keping_reproses));
            label_total_gram_reproses.setText(decimalFormat.format(gram_reproses));

            decimalFormat.setMaximumFractionDigits(2);
            label_avg_netto_reproses.setText(decimalFormat.format(avg_netto) + "%");
            label_avg_sp_reproses.setText(decimalFormat.format(avg_sp) + "%");
            label_avg_sh_reproses.setText(decimalFormat.format(avg_sh) + "%");
            ColumnsAutoSizer.sizeColumnsToFit(table_data_reproses);
            ColumnsAutoSizer.sizeColumnsToFit(table_data_reproses_cetak);
            ColumnsAutoSizer.sizeColumnsToFit(table_data_reproses_cabut);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Reproses.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_reProses_pencabut() {
        try {
            float total_gram = 0;
            String no_reproses = label_no_reproses.getText();
            DefaultTableModel model = (DefaultTableModel) table_data_pencabut_reproses.getModel();
            model.setRowCount(0);
            sql = "SELECT `no_reproses`, `tb_reproses_pencabut`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `tb_bagian`.`nama_bagian`, `tanggal_cabut`, `jumlah_cabut`, `jumlah_gram`, `grup_cabut` "
                    + "FROM `tb_reproses_pencabut` "
                    + "LEFT JOIN `tb_karyawan` ON `tb_reproses_pencabut`.`id_pegawai` = `tb_karyawan`.`id_pegawai` "
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian` "
                    + "WHERE `no_reproses` = '" + no_reproses + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getString("no_reproses");
                row[1] = rs.getDate("tanggal_cabut");
                row[2] = rs.getString("id_pegawai");
                row[3] = rs.getString("nama_pegawai");
                row[4] = rs.getString("nama_bagian");
                row[5] = rs.getString("grup_cabut");
                row[6] = rs.getInt("jumlah_cabut");
                row[7] = rs.getFloat("jumlah_gram");
                model.addRow(row);
                total_gram = total_gram + rs.getFloat("jumlah_gram");
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_data_pencabut_reproses);
            int total_data = table_data_pencabut_reproses.getRowCount();
            label_total_data_pencabut.setText(Integer.toString(total_data));
            label_total_gram_cabutan.setText(decimalFormat.format(total_gram));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Reproses.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel61 = new javax.swing.JLabel();
        label_total_stok1 = new javax.swing.JLabel();
        jPanel_data_reproses = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        txt_search_box_reproses = new javax.swing.JTextField();
        Date_Search_reproses1 = new com.toedter.calendar.JDateChooser();
        Date_Search_reproses2 = new com.toedter.calendar.JDateChooser();
        button_search_reproses = new javax.swing.JButton();
        jLabel34 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        ComboBox_status_reproses = new javax.swing.JComboBox<>();
        jLabel62 = new javax.swing.JLabel();
        ComboBox_tujuan_reproses = new javax.swing.JComboBox<>();
        ComboBox_Filter_Tgl_reproses = new javax.swing.JComboBox<>();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        table_data_reproses = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        label_total_reproses = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        label_total_keping_reproses = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        label_total_gram_reproses = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        label_avg_netto_reproses = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        label_avg_sp_reproses = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        label_avg_sh_reproses = new javax.swing.JLabel();
        button_input_kaki = new javax.swing.JButton();
        button_input_f1 = new javax.swing.JButton();
        button_input_f2 = new javax.swing.JButton();
        button_edit_reproses = new javax.swing.JButton();
        button_selesai_reproses_GBJ = new javax.swing.JButton();
        button_selesai_reproses_QC = new javax.swing.JButton();
        button_LP_Reproses_Sub = new javax.swing.JButton();
        button_LP_Reproses = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane11 = new javax.swing.JScrollPane();
        table_data_reproses_cabut = new javax.swing.JTable();
        button_tgl_selesai_cabut = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_data_pencabut_reproses = new javax.swing.JTable();
        button_input_pekerja_cabut = new javax.swing.JButton();
        button_delete_pekerja_cabut = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        label_total_data_pencabut = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        label_total_gram_cabutan = new javax.swing.JLabel();
        button_edit_pekerja_cabut = new javax.swing.JButton();
        label_no_reproses = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane10 = new javax.swing.JScrollPane();
        table_data_reproses_cetak = new javax.swing.JTable();
        button_input_cetak = new javax.swing.JButton();

        jLabel61.setBackground(new java.awt.Color(255, 255, 255));
        jLabel61.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel61.setText("Total Stok :");

        label_total_stok1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_stok1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_stok1.setText("0");

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel_data_reproses.setBackground(new java.awt.Color(255, 255, 255));

        jLabel27.setBackground(new java.awt.Color(255, 255, 255));
        jLabel27.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel27.setText("No Box :");

        txt_search_box_reproses.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_box_reproses.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_box_reprosesKeyPressed(evt);
            }
        });

        Date_Search_reproses1.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search_reproses1.setDate(new Date(new Date().getTime()-(7 * 24 * 60 * 60 * 1000)));
        Date_Search_reproses1.setDateFormatString("dd MMMM yyyy");
        Date_Search_reproses1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_Search_reproses2.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search_reproses2.setDate(new Date());
        Date_Search_reproses2.setDateFormatString("dd MMMM yyyy");
        Date_Search_reproses2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_search_reproses.setBackground(new java.awt.Color(255, 255, 255));
        button_search_reproses.setText("Search");
        button_search_reproses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_reprosesActionPerformed(evt);
            }
        });

        jLabel34.setBackground(new java.awt.Color(255, 255, 255));
        jLabel34.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel34.setText("Data re-proses");

        jLabel59.setBackground(new java.awt.Color(255, 255, 255));
        jLabel59.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel59.setText("Status :");

        ComboBox_status_reproses.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_status_reproses.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "IN PROSES", "FINISHED" }));

        jLabel62.setBackground(new java.awt.Color(255, 255, 255));
        jLabel62.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel62.setText("Tujuan :");

        ComboBox_tujuan_reproses.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_tujuan_reproses.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "RND", "F2 - MLem", "F2 - Jidun", "F2 - Kakian", "F2 - Mess", "Eksternal" }));

        ComboBox_Filter_Tgl_reproses.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_Filter_Tgl_reproses.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tanggal Reproses", "Tanggal Cetak", "Tanggal F1", "Tanggal F2", "Tanggal Selesai" }));

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        table_data_reproses.setAutoCreateRowSorter(true);
        table_data_reproses.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_reproses.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "No Box", "Grade", "Tgl Proses", "Keping", "Gram", "Grup tujuan", "Tgl Selesai", "Kpg Akhir", "Gr AKhir", "LP Kaki 1", "Gram kaki 1", "LP Kaki 2", "Gram kaki 2", "Netto", "Netto(%)", "Ront Brsh", "Ront Kng", "Ront Ktr", "Hc", "Bgl", "Srbt", "SP", "Sp(%)", "SH", "SH(%)", "Tgl F1", "Pekerja F1", "Tgl F2", "Pekerja F2", "Pekerja Timbang", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Float.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.Float.class, java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_data_reproses.getTableHeader().setReorderingAllowed(false);
        jScrollPane9.setViewportView(table_data_reproses);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Summary", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 11))); // NOI18N

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel28.setText("Total Data :");

        label_total_reproses.setBackground(new java.awt.Color(255, 255, 255));
        label_total_reproses.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_reproses.setText("0");

        jLabel29.setBackground(new java.awt.Color(255, 255, 255));
        jLabel29.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel29.setText("Keping :");

        label_total_keping_reproses.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_reproses.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_keping_reproses.setText("0");

        jLabel30.setBackground(new java.awt.Color(255, 255, 255));
        jLabel30.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel30.setText("Gram :");

        label_total_gram_reproses.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_reproses.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_gram_reproses.setText("0");

        jLabel58.setBackground(new java.awt.Color(255, 255, 255));
        jLabel58.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel58.setText("Avg Netto :");

        label_avg_netto_reproses.setBackground(new java.awt.Color(255, 255, 255));
        label_avg_netto_reproses.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_avg_netto_reproses.setText("0");

        jLabel63.setBackground(new java.awt.Color(255, 255, 255));
        jLabel63.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel63.setText("Avg SP :");

        label_avg_sp_reproses.setBackground(new java.awt.Color(255, 255, 255));
        label_avg_sp_reproses.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_avg_sp_reproses.setText("0");

        jLabel64.setBackground(new java.awt.Color(255, 255, 255));
        jLabel64.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel64.setText("Avg SH :");

        label_avg_sh_reproses.setBackground(new java.awt.Color(255, 255, 255));
        label_avg_sh_reproses.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_avg_sh_reproses.setText("0");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_reproses)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_keping_reproses)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel30)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_reproses))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel58)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_avg_netto_reproses)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel63)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_avg_sp_reproses)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel64)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_avg_sh_reproses)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_reproses, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_keping_reproses, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram_reproses, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel58, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_avg_netto_reproses, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel63, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_avg_sp_reproses, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel64, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_avg_sh_reproses, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        button_input_kaki.setBackground(new java.awt.Color(255, 255, 255));
        button_input_kaki.setText("Input Kaki");
        button_input_kaki.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_kakiActionPerformed(evt);
            }
        });

        button_input_f1.setBackground(new java.awt.Color(255, 255, 255));
        button_input_f1.setText("Input F1");
        button_input_f1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_f1ActionPerformed(evt);
            }
        });

        button_input_f2.setBackground(new java.awt.Color(255, 255, 255));
        button_input_f2.setText("Input F2");
        button_input_f2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_f2ActionPerformed(evt);
            }
        });

        button_edit_reproses.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_reproses.setText("Edit");
        button_edit_reproses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_reprosesActionPerformed(evt);
            }
        });

        button_selesai_reproses_GBJ.setBackground(new java.awt.Color(255, 255, 255));
        button_selesai_reproses_GBJ.setText("Setor Ke Grading");
        button_selesai_reproses_GBJ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_selesai_reproses_GBJActionPerformed(evt);
            }
        });

        button_selesai_reproses_QC.setBackground(new java.awt.Color(255, 255, 255));
        button_selesai_reproses_QC.setText("Setor Treatment QC");
        button_selesai_reproses_QC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_selesai_reproses_QCActionPerformed(evt);
            }
        });

        button_LP_Reproses_Sub.setBackground(new java.awt.Color(255, 255, 255));
        button_LP_Reproses_Sub.setText("Cetak LP Reproses Sub");
        button_LP_Reproses_Sub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_LP_Reproses_SubActionPerformed(evt);
            }
        });

        button_LP_Reproses.setBackground(new java.awt.Color(255, 255, 255));
        button_LP_Reproses.setText("Cetak LP Reproses");
        button_LP_Reproses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_LP_ReprosesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(button_input_kaki)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_input_f1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_input_f2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_edit_reproses)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_selesai_reproses_GBJ)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_selesai_reproses_QC)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_LP_Reproses_Sub)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_LP_Reproses)
                        .addGap(0, 553, Short.MAX_VALUE))
                    .addComponent(jScrollPane9)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_input_kaki, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_input_f1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_input_f2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_edit_reproses, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_selesai_reproses_GBJ, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_selesai_reproses_QC, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_LP_Reproses_Sub, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_LP_Reproses, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 465, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("REPROSES", jPanel1);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        table_data_reproses_cabut.setAutoCreateRowSorter(true);
        table_data_reproses_cabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_reproses_cabut.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "No Box", "Grade", "Kpg", "Gram", "Tgl Cabut", "Tgl Selesai Cbt"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Object.class, java.lang.Object.class
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
        table_data_reproses_cabut.getTableHeader().setReorderingAllowed(false);
        jScrollPane11.setViewportView(table_data_reproses_cabut);

        button_tgl_selesai_cabut.setBackground(new java.awt.Color(255, 255, 255));
        button_tgl_selesai_cabut.setText("Selesai Cabut");
        button_tgl_selesai_cabut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_tgl_selesai_cabutActionPerformed(evt);
            }
        });

        table_data_pencabut_reproses.setAutoCreateRowSorter(true);
        table_data_pencabut_reproses.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_pencabut_reproses.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Tanggal", "ID Pegawai", "Nama", "Bagian", "Grup", "Kpg", "Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class
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
        jScrollPane1.setViewportView(table_data_pencabut_reproses);

        button_input_pekerja_cabut.setBackground(new java.awt.Color(255, 255, 255));
        button_input_pekerja_cabut.setText("Input Pekerja Cabut");
        button_input_pekerja_cabut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_pekerja_cabutActionPerformed(evt);
            }
        });

        button_delete_pekerja_cabut.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_pekerja_cabut.setText("Delete Pekerja Cabut");
        button_delete_pekerja_cabut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_pekerja_cabutActionPerformed(evt);
            }
        });

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText("Total Data Pencabut :");

        label_total_data_pencabut.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_pencabut.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_data_pencabut.setText("0");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("Total Gram :");

        label_total_gram_cabutan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_cabutan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_gram_cabutan.setText("0");

        button_edit_pekerja_cabut.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_pekerja_cabut.setText("Edit Pekerja Cabut");
        button_edit_pekerja_cabut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_pekerja_cabutActionPerformed(evt);
            }
        });

        label_no_reproses.setBackground(new java.awt.Color(255, 255, 255));
        label_no_reproses.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_no_reproses.setText("NO REPROSES");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_tgl_selesai_cabut))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 775, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(button_input_pekerja_cabut)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_edit_pekerja_cabut)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_delete_pekerja_cabut)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_no_reproses)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data_pencabut)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_cabutan)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_input_pekerja_cabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete_pekerja_cabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(label_total_data_pencabut)
                    .addComponent(jLabel3)
                    .addComponent(label_total_gram_cabutan)
                    .addComponent(button_tgl_selesai_cabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_edit_pekerja_cabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_no_reproses))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("CABUT", jPanel2);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        table_data_reproses_cetak.setAutoCreateRowSorter(true);
        table_data_reproses_cetak.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_reproses_cetak.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "No Box", "Kpg", "Gram", "Tgl Cetak", "Pekerja Ctk", "MK", "PCH", "FLAT", "JDN", "Kpg Tidak Cetak"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Object.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class
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
        table_data_reproses_cetak.getTableHeader().setReorderingAllowed(false);
        jScrollPane10.setViewportView(table_data_reproses_cetak);

        button_input_cetak.setBackground(new java.awt.Color(255, 255, 255));
        button_input_cetak.setText("Input Cetak");
        button_input_cetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_cetakActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(button_input_cetak)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1381, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(button_input_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("CETAK", jPanel4);

        javax.swing.GroupLayout jPanel_data_reprosesLayout = new javax.swing.GroupLayout(jPanel_data_reproses);
        jPanel_data_reproses.setLayout(jPanel_data_reprosesLayout);
        jPanel_data_reprosesLayout.setHorizontalGroup(
            jPanel_data_reprosesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_data_reprosesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_data_reprosesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel34)
                    .addGroup(jPanel_data_reprosesLayout.createSequentialGroup()
                        .addComponent(jLabel27)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_box_reproses, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_Filter_Tgl_reproses, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Search_reproses1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Search_reproses2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel59)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_status_reproses, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel62)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_tujuan_reproses, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search_reproses)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel_data_reprosesLayout.setVerticalGroup(
            jPanel_data_reprosesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_data_reprosesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_data_reprosesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel_data_reprosesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel_data_reprosesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_search_box_reproses, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_Filter_Tgl_reproses, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(Date_Search_reproses1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Date_Search_reproses2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_data_reprosesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel_data_reprosesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel62, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_tujuan_reproses, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_search_reproses, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel_data_reprosesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel59, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_status_reproses, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_data_reproses, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_data_reproses, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txt_search_box_reprosesKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_box_reprosesKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_reProses();
        }
    }//GEN-LAST:event_txt_search_box_reprosesKeyPressed

    private void button_search_reprosesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_reprosesActionPerformed
        // TODO add your handling code here:
        refreshTable_reProses();
    }//GEN-LAST:event_button_search_reprosesActionPerformed

    private void button_selesai_reproses_GBJActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_selesai_reproses_GBJActionPerformed
        // TODO add your handling code here:
        int x = table_data_reproses.getSelectedRow();
        if (x < 0) {
            JOptionPane.showMessageDialog(this, "Silahkan dulu Klik data box yang sudah selesai !");
        } else {
            if (table_data_reproses.getValueAt(x, 7) != null) {
                JOptionPane.showMessageDialog(this, "Maaf Box yang dipilih sudah selesai proses \ndan sudah di setor");
            } else {
                String no_box = table_data_reproses.getValueAt(x, 1).toString();
                String no_reproses = table_data_reproses.getValueAt(x, 0).toString();
                int kpg_awal = Integer.valueOf(table_data_reproses.getValueAt(x, 4).toString());
                float gram_awal = Float.valueOf(table_data_reproses.getValueAt(x, 5).toString());
                Object kaki1 = table_data_reproses.getValueAt(x, 10);
                float gram_kaki1 = Float.valueOf(table_data_reproses.getValueAt(x, 11).toString());
                Object kaki2 = table_data_reproses.getValueAt(x, 12);
                float gram_kaki2 = Float.valueOf(table_data_reproses.getValueAt(x, 13).toString());
                JDialog_reproses_selesai dialog = new JDialog_reproses_selesai(new javax.swing.JFrame(), true, no_box, no_reproses, kpg_awal, gram_awal, kaki1, gram_kaki1, kaki2, gram_kaki2, "GBJ");
                dialog.pack();
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
                dialog.setEnabled(true);
                refreshTable_reProses();
            }
        }
    }//GEN-LAST:event_button_selesai_reproses_GBJActionPerformed

    private void button_input_kakiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_kakiActionPerformed
        // TODO add your handling code here:
        JDialog_Input_Kaki_reproses input_kaki = new JDialog_Input_Kaki_reproses(new javax.swing.JFrame(), true);
        input_kaki.pack();
        input_kaki.setLocationRelativeTo(this);
        input_kaki.setVisible(true);
        input_kaki.setEnabled(true);
    }//GEN-LAST:event_button_input_kakiActionPerformed

    private void button_edit_reprosesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_reprosesActionPerformed
        // TODO add your handling code here:
        int x = table_data_reproses.getSelectedRow();
        if (x < 0) {
            JOptionPane.showMessageDialog(this, "Silahkan dulu Klik data box yang sudah selesai !");
        } else {
            String no_box = table_data_reproses.getValueAt(x, 1).toString();
            String no_reproses = table_data_reproses.getValueAt(x, 0).toString();
            JDialog_reproses_edit dialog = new JDialog_reproses_edit(new javax.swing.JFrame(), true, no_box, no_reproses);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            dialog.setEnabled(true);
            refreshTable_reProses();
        }
    }//GEN-LAST:event_button_edit_reprosesActionPerformed

    private void button_selesai_reproses_QCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_selesai_reproses_QCActionPerformed
        // TODO add your handling code here:
        int x = table_data_reproses.getSelectedRow();
        if (x < 0) {
            JOptionPane.showMessageDialog(this, "Silahkan dulu Klik data box yang sudah selesai !");
        } else {
            if (table_data_reproses.getValueAt(x, 7) != null) {
                JOptionPane.showMessageDialog(this, "Maaf Box yang dipilih sudah selesai proses \ndan sudah di setor");
            } else {
                String no_box = table_data_reproses.getValueAt(x, 1).toString();
                String no_reproses = table_data_reproses.getValueAt(x, 0).toString();
                int kpg_awal = Integer.valueOf(table_data_reproses.getValueAt(x, 4).toString());
                float gram_awal = Float.valueOf(table_data_reproses.getValueAt(x, 5).toString());
                Object kaki1 = table_data_reproses.getValueAt(x, 10);
                float gram_kaki1 = Float.valueOf(table_data_reproses.getValueAt(x, 11).toString());
                Object kaki2 = table_data_reproses.getValueAt(x, 12);
                float gram_kaki2 = Float.valueOf(table_data_reproses.getValueAt(x, 13).toString());
                JDialog_reproses_selesai dialog = new JDialog_reproses_selesai(new javax.swing.JFrame(), true, no_box, no_reproses, kpg_awal, gram_awal, kaki1, gram_kaki1, kaki2, gram_kaki2, "QC");
                dialog.pack();
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
                dialog.setEnabled(true);
                refreshTable_reProses();
            }
        }
    }//GEN-LAST:event_button_selesai_reproses_QCActionPerformed

    private void button_input_f2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_f2ActionPerformed
        // TODO add your handling code here:
        int x = table_data_reproses.getSelectedRow();
        if (x < 0) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih data !");
        } else {
            String no_box = table_data_reproses.getValueAt(x, 1).toString();
            String no_reproses = table_data_reproses.getValueAt(x, 0).toString();
            JDialog_reproses_Input_F12 dialog = new JDialog_reproses_Input_F12(new javax.swing.JFrame(), true, no_box, no_reproses, "f2");
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            dialog.setEnabled(true);
            refreshTable_reProses();
        }
    }//GEN-LAST:event_button_input_f2ActionPerformed

    private void button_input_f1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_f1ActionPerformed
        // TODO add your handling code here:
        int x = table_data_reproses.getSelectedRow();
        if (x < 0) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih data !");
        } else {
            String no_box = table_data_reproses.getValueAt(x, 1).toString();
            String no_reproses = table_data_reproses.getValueAt(x, 0).toString();
            JDialog_reproses_Input_F12 dialog = new JDialog_reproses_Input_F12(new javax.swing.JFrame(), true, no_box, no_reproses, "f1");
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            dialog.setEnabled(true);
            refreshTable_reProses();
        }
    }//GEN-LAST:event_button_input_f1ActionPerformed

    private void button_input_cetakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_cetakActionPerformed
        // TODO add your handling code here:
        int x = table_data_reproses_cetak.getSelectedRow();
        if (x < 0) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih data !");
        } else {
            String no_reproses = table_data_reproses_cetak.getValueAt(x, 0).toString();
            String no_box = table_data_reproses_cetak.getValueAt(x, 1).toString();
            int keping_awal = (int) table_data_reproses_cetak.getValueAt(x, 2);
            JDialog_reproses_cetak dialog = new JDialog_reproses_cetak(new javax.swing.JFrame(), true, no_box, no_reproses, keping_awal);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            dialog.setEnabled(true);
            refreshTable_reProses();
        }
    }//GEN-LAST:event_button_input_cetakActionPerformed

    private void button_tgl_selesai_cabutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_tgl_selesai_cabutActionPerformed
        // TODO add your handling code here:
        try {
            int j = table_data_reproses_cabut.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih datanya !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Input Selesai cabut?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    String no_reproses = table_data_reproses_cabut.getValueAt(j, 0).toString();
                    double total_gram_cabutan = 0, gram_box = Double.valueOf(table_data_reproses_cabut.getValueAt(j, 4).toString());
                    sql = "SELECT ROUND(SUM(`jumlah_gram`), 1) AS 'total_gram_cabutan' "
                            + "FROM `tb_reproses_pencabut` WHERE `no_reproses` = '" + no_reproses + "'";
                    rs = Utility.db.getStatement().executeQuery(sql);
                    if (rs.next()) {
                        total_gram_cabutan = rs.getDouble("total_gram_cabutan");
                    }
                    if (Math.round(total_gram_cabutan) == gram_box) {
                        sql = "UPDATE `tb_reproses` SET `tgl_cabut_selesai`=CURRENT_DATE() WHERE `no_reproses` = '" + no_reproses + "'";
                        Utility.db.getConnection().createStatement();
                        if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                            JOptionPane.showMessageDialog(this, "data Tersimpan !");
                        } else {
                            JOptionPane.showMessageDialog(this, "Gagal / Tidak ada perubahan data !");
                        }
                        refreshTable_reProses();
                    } else {
                        JOptionPane.showMessageDialog(this, "Maaf total gram cabutan dan gram box tidak sama !");
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Reproses.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_tgl_selesai_cabutActionPerformed

    private void button_input_pekerja_cabutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_pekerja_cabutActionPerformed
        // TODO add your handling code here:
        int x = table_data_reproses_cabut.getSelectedRow();
        if (x < 0) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih data !");
        } else {
            String no_reproses = label_no_reproses.getText();
            JDialog_reProses_pencabut dialog = new JDialog_reProses_pencabut(new javax.swing.JFrame(), true, "new", no_reproses, "", "");
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            dialog.setEnabled(true);
            refreshTable_reProses_pencabut();
            refreshTable_reProses();
            table_data_reproses_cabut.setRowSelectionInterval(x, x);
        }
    }//GEN-LAST:event_button_input_pekerja_cabutActionPerformed

    private void button_delete_pekerja_cabutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_pekerja_cabutActionPerformed
        // TODO add your handling code here:
        try {
            int j = table_data_pencabut_reproses.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang ingin di hapus !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    String no_reproses = table_data_pencabut_reproses.getValueAt(j, 0).toString();
                    String tgl = table_data_pencabut_reproses.getValueAt(j, 1).toString();
                    String id = table_data_pencabut_reproses.getValueAt(j, 2).toString();
                    sql = "DELETE FROM `tb_reproses_pencabut` WHERE "
                            + "`no_reproses`='" + no_reproses + "' AND "
                            + "`id_pegawai`='" + id + "' AND "
                            + "`tanggal_cabut`='" + tgl + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                        JOptionPane.showMessageDialog(this, "data Saved !");
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed !");
                    }
                    refreshTable_reProses_pencabut();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Reproses.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_delete_pekerja_cabutActionPerformed

    private void button_edit_pekerja_cabutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_pekerja_cabutActionPerformed
        // TODO add your handling code here:
        int x = table_data_pencabut_reproses.getSelectedRow();
        if (x < 0) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih data !");
        } else {
            String no_reproses = table_data_pencabut_reproses.getValueAt(x, 0).toString();
            String tgl_cabut = table_data_pencabut_reproses.getValueAt(x, 1).toString();
            String id_pegawai = table_data_pencabut_reproses.getValueAt(x, 2).toString();
            JDialog_reProses_pencabut dialog = new JDialog_reProses_pencabut(new javax.swing.JFrame(), true, "edit", no_reproses, id_pegawai, tgl_cabut);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            dialog.setEnabled(true);
            refreshTable_reProses_pencabut();
        }
    }//GEN-LAST:event_button_edit_pekerja_cabutActionPerformed

    private void button_LP_Reproses_SubActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_LP_Reproses_SubActionPerformed
        // TODO add your handling code here:
        try {
            String no_reproses = "";
            for (int i = 0; i < table_data_reproses.getRowCount(); i++) {
                if (i != 0) {
                    no_reproses = no_reproses + ", ";
                }
                no_reproses = no_reproses + "'" + table_data_reproses.getValueAt(i, 0).toString() + "'";
            }
            String query = "SELECT `tanggal_proses`, `no_reproses`, `tb_reproses`.`no_box`, `tb_grade_bahan_jadi`.`kode_grade`, `tb_reproses`.`keping`, `tb_reproses`.`gram` \n"
                    + "FROM `tb_reproses` \n"
                    + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_reproses`.`no_box` = `tb_box_bahan_jadi`.`no_box`\n"
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`\n"
                    + "WHERE `no_reproses` IN (" + no_reproses + ")";
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(query);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Label_Reproses_Sub.jrxml");
            JASP_DESIGN.setQuery(newQuery);
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            Map<String, Object> params = new HashMap<String, Object>();
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, params, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_LP_Reproses_SubActionPerformed

    private void button_LP_ReprosesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_LP_ReprosesActionPerformed
        // TODO add your handling code here:
        try {
            String no_box = "";
            for (int i = 0; i < table_data_reproses.getRowCount(); i++) {
                if (i != 0) {
                    no_box = no_box + ", ";
                }
                no_box = no_box + "'" + table_data_reproses.getValueAt(i, 1).toString() + "'";
            }
            String query = "SELECT `no_box`, `tanggal_box`, `keping`, `berat`, `kode_grade`, `tb_box_bahan_jadi`.`kode_rsb`,\n"
                    + "(SELECT `no_kartu_waleta` FROM `tb_bahan_baku_masuk_cheat` WHERE `kode_kh` = `tb_dokumen_kh`.`kode_kh` AND `no_kartu_waleta` NOT LIKE '%CMP%' ORDER BY `no_kartu_waleta` LIMIT 1) AS 'no_kartu_waleta'\n"
                    + "FROM `tb_box_bahan_jadi`\n"
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`\n"
                    + "LEFT JOIN `tb_dokumen_kh` ON `tb_box_bahan_jadi`.`kode_kh` = `tb_dokumen_kh`.`kode_kh`\n"
                    + "WHERE `no_box` IN (" + no_box + ")";
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(query);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Label_Reproses.jrxml");
            JASP_DESIGN.setQuery(newQuery);
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            Map<String, Object> params = new HashMap<String, Object>();
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, params, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_LP_ReprosesActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_Filter_Tgl_reproses;
    private javax.swing.JComboBox<String> ComboBox_status_reproses;
    private javax.swing.JComboBox<String> ComboBox_tujuan_reproses;
    private com.toedter.calendar.JDateChooser Date_Search_reproses1;
    private com.toedter.calendar.JDateChooser Date_Search_reproses2;
    public javax.swing.JButton button_LP_Reproses;
    public javax.swing.JButton button_LP_Reproses_Sub;
    public javax.swing.JButton button_delete_pekerja_cabut;
    public javax.swing.JButton button_edit_pekerja_cabut;
    public javax.swing.JButton button_edit_reproses;
    public javax.swing.JButton button_input_cetak;
    public javax.swing.JButton button_input_f1;
    public javax.swing.JButton button_input_f2;
    public javax.swing.JButton button_input_kaki;
    public javax.swing.JButton button_input_pekerja_cabut;
    private javax.swing.JButton button_search_reproses;
    public javax.swing.JButton button_selesai_reproses_GBJ;
    public javax.swing.JButton button_selesai_reproses_QC;
    public javax.swing.JButton button_tgl_selesai_cabut;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel_data_reproses;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label_avg_netto_reproses;
    private javax.swing.JLabel label_avg_sh_reproses;
    private javax.swing.JLabel label_avg_sp_reproses;
    private javax.swing.JLabel label_no_reproses;
    private javax.swing.JLabel label_total_data_pencabut;
    private javax.swing.JLabel label_total_gram_cabutan;
    private javax.swing.JLabel label_total_gram_reproses;
    private javax.swing.JLabel label_total_keping_reproses;
    private javax.swing.JLabel label_total_reproses;
    private javax.swing.JLabel label_total_stok1;
    private javax.swing.JTable table_data_pencabut_reproses;
    private javax.swing.JTable table_data_reproses;
    private javax.swing.JTable table_data_reproses_cabut;
    private javax.swing.JTable table_data_reproses_cetak;
    private javax.swing.JTextField txt_search_box_reproses;
    // End of variables declaration//GEN-END:variables
}
