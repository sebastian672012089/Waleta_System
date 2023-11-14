package waleta_system.Finance;

import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
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
import waleta_system.BahanJadi.JDialog_Create_LPSuwir;
import waleta_system.BahanJadi.JDialog_Edit_rePacking;
import waleta_system.BahanJadi.JDialog_Insert_Edit_kinerjaGBJ;
import waleta_system.BahanJadi.JDialog_Out;
import waleta_system.BahanJadi.JDialog_Setor_Packing;
import waleta_system.BahanJadi.JDialog_rePacking;
import waleta_system.BahanJadi.JDialog_reProcess;
import waleta_system.BahanJadi.JDialog_terima_retur;
import waleta_system.BahanJadi.JDialog_treatment_box;
import waleta_system.BahanJadi.JFrame_Reproses_Belum_Repack;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;
import waleta_system.Class.Utility;
import waleta_system.Packing.JDialog_new_SPK_SE_Lokal;

public class JPanel_BoxBahanJadi_Keuangan extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_BoxBahanJadi_Keuangan() {
        initComponents();
        table_data_repacking.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && table_data_repacking.getSelectedRow() != -1) {
                    int i = table_data_repacking.getSelectedRow();
                    label_kode_repacking1.setText(table_data_repacking.getValueAt(table_data_repacking.getSelectedRow(), 0).toString());
                    label_kode_repacking2.setText(table_data_repacking.getValueAt(table_data_repacking.getSelectedRow(), 0).toString());
                    refreshTable_detailRepacking();
                }
            }
        });
    }

    public void init() {
        try {
            String this_year = new SimpleDateFormat("yyyy").format(new Date());
            Calendar calendar = Calendar.getInstance();
            calendar.set(Integer.valueOf(this_year), new Date().getMonth(), 1);
            Date first_date = calendar.getTime();
            Date_Search_Repacking1.setDate(first_date);
            Date_Search_Repacking2.setDate(new Date());

            ComboBox_lokasi.removeAllItems();
            ComboBox_lokasi.addItem("All");
            String lokasi = "SELECT DISTINCT(`lokasi_terakhir`) FROM `tb_box_bahan_jadi`";
            ResultSet rs_lokasi = Utility.db.getStatement().executeQuery(lokasi);
            while (rs_lokasi.next()) {
                ComboBox_lokasi.addItem(rs_lokasi.getString("lokasi_terakhir"));
            }
            ComboBox_lokasi.setSelectedItem("GRADING");
            refreshTable_DataBox();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_BoxBahanJadi_Keuangan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_DataBox() {
        try {
            float total_kpg = 0, total_gram = 0;
            decimalFormat.setGroupingUsed(true);
            String lokasi = " AND `lokasi_terakhir` = '" + ComboBox_lokasi.getSelectedItem().toString() + "' ";
            if (ComboBox_lokasi.getSelectedItem() == "All") {
                lokasi = "";
            }
            String grade = "";
            if (txt_search_grade.getText() != null && !txt_search_grade.getText().equals("")) {
                grade = "AND `kode_grade` = '" + txt_search_grade.getText() + "' \n";
            }

            String tanggal = "";
            if (Date_box1.getDate() != null && Date_box2.getDate() != null) {
                tanggal = " AND (`tanggal_box` BETWEEN '" + dateFormat.format(Date_box1.getDate()) + "' AND '" + dateFormat.format(Date_box2.getDate()) + "')";
            }

            String search_invoice = " AND `tb_pengiriman`.`invoice_no` LIKE '%" + txt_search_no_invoice.getText() + "%' ";
            if ("".equals(txt_search_no_invoice.getText()) || txt_search_no_invoice.getText() == null) {
                search_invoice = "";
            }

            sql = "SELECT `tb_box_bahan_jadi`.`no_box`, `tanggal_box`, `tb_grade_bahan_jadi`.`kode_grade`, `tb_box_bahan_jadi`.`keping`, `tb_box_bahan_jadi`.`berat`, `no_tutupan`, `status_terakhir`, `lokasi_terakhir`, `tgl_proses_terakhir`, `tb_spk_detail`.`no`, `tb_spk_detail`.`kode_spk`, `tb_spk_detail`.`grade_buyer`, `tb_box_bahan_jadi`.`kode_rsb`, `tb_rumah_burung`.`nama_rumah_burung`, `tb_box_bahan_jadi`.`kode_kh`, `tb_dokumen_kh`.`no_registrasi_rsb`, `no_box_ct1`, `tb_pengiriman`.`invoice_no`, `memo_box_bj` "
                    + "FROM `tb_box_bahan_jadi` "
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`"
                    + "LEFT JOIN `tb_box_packing` ON `tb_box_bahan_jadi`.`no_box` = `tb_box_packing`.`no_box`"
                    + "LEFT JOIN `tb_spk_detail` ON `tb_box_packing`.`no_grade_spk` = `tb_spk_detail`.`no` "
                    + "LEFT JOIN `tb_pengiriman` ON `tb_spk_detail`.`kode_spk` = `tb_pengiriman`.`kode_spk` "
                    + "LEFT JOIN `tb_rumah_burung` ON `tb_box_bahan_jadi`.`kode_rsb` = `tb_rumah_burung`.`no_registrasi`"
                    + "LEFT JOIN `tb_dokumen_kh` ON `tb_box_bahan_jadi`.`kode_kh` = `tb_dokumen_kh`.`kode_kh`"
                    + "WHERE "
                    + "`tb_box_bahan_jadi`.`no_box` LIKE '%" + txt_search_no_box.getText() + "%' "
                    + "AND `no_tutupan` LIKE '%" + txt_search_no_tutupan.getText() + "%' "
                    + search_invoice
                    + tanggal 
                    + grade 
                    + lokasi
                    + " ORDER BY `tgl_proses_terakhir` DESC";
//            System.out.println(sql);
            rs = Utility.db.getStatement().executeQuery(sql);
            DefaultTableModel model = (DefaultTableModel) table_dataBox.getModel();
            model.setRowCount(0);
            Object[] baris = new Object[20];
            while (rs.next()) {
                baris[0] = rs.getString("no_box");
                baris[1] = rs.getDate("tanggal_box");
                baris[2] = rs.getString("kode_grade");
                baris[3] = rs.getFloat("keping");
                baris[4] = rs.getFloat("berat");
                baris[5] = rs.getString("no_tutupan");
                baris[6] = rs.getString("status_terakhir");
                baris[7] = rs.getString("lokasi_terakhir");
                baris[8] = rs.getDate("tgl_proses_terakhir");
                baris[9] = rs.getString("kode_spk");
                baris[10] = rs.getString("grade_buyer");
                baris[11] = rs.getString("kode_rsb");
                baris[12] = rs.getString("kode_kh");
                baris[13] = rs.getString("no_registrasi_rsb");
                baris[14] = rs.getString("no_box_ct1");
                baris[15] = rs.getString("invoice_no");
                baris[16] = rs.getString("memo_box_bj");
                model.addRow(baris);

                total_kpg += rs.getFloat("keping");
                total_gram += rs.getFloat("berat");
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_dataBox);
            int total_data = table_dataBox.getRowCount();
            label_total_data_box.setText(Integer.toString(total_data));
            label_total_kpg_data_box.setText(decimalFormat.format(total_kpg));
            label_total_gram_data_box.setText(decimalFormat.format(total_gram));
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_BoxBahanJadi_Keuangan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_Repacking() {
        try {
            double total_sh = 0;
            DefaultTableModel model = (DefaultTableModel) table_data_repacking.getModel();
            model.setRowCount(0);
            if (Date_Search_Repacking1.getDate() != null && Date_Search_Repacking2.getDate() != null) {
                sql = "SELECT `kode_repacking`, `tanggal_repacking`, `status_repacking`, `keterangan_repacking`, `pekerja_repacking`, `nama_pegawai`, SUM(IF(`tb_repacking`.`status` = 'ASAL', `gram`, 0)) AS 'gram_asal', SUM(IF(`tb_repacking`.`status` = 'HASIL', `gram`, 0)) AS 'gram_hasil', `kode_rsb` "
                        + "FROM `tb_repacking` "
                        + "LEFT JOIN `tb_karyawan` ON `tb_repacking`.`pekerja_repacking` = `tb_karyawan`.`id_pegawai` "
                        + "WHERE `kode_repacking` LIKE '%" + txt_search_kode_repacking.getText() + "%' AND `tanggal_repacking` BETWEEN '" + dateFormat.format(Date_Search_Repacking1.getDate()) + "' AND '" + dateFormat.format(Date_Search_Repacking2.getDate()) + "' "
                        + "GROUP BY `kode_repacking` ORDER BY `tanggal_repacking` DESC";
            } else {
                sql = "SELECT `kode_repacking`, `tanggal_repacking`, `status_repacking`, `keterangan_repacking`, `pekerja_repacking`, `nama_pegawai`, SUM(IF(`tb_repacking`.`status` = 'ASAL', `gram`, 0)) AS 'gram_asal', SUM(IF(`tb_repacking`.`status` = 'HASIL', `gram`, 0)) AS 'gram_hasil', `kode_rsb` "
                        + "FROM `tb_repacking` "
                        + "LEFT JOIN `tb_karyawan` ON `tb_repacking`.`pekerja_repacking` = `tb_karyawan`.`id_pegawai` "
                        + "WHERE `kode_repacking` LIKE '%" + txt_search_kode_repacking.getText() + "%'  "
                        + "GROUP BY `kode_repacking` ORDER BY `tanggal_repacking` DESC";
            }
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[7];
            while (rs.next()) {
                row[0] = rs.getString("kode_repacking");
                row[1] = rs.getString("tanggal_repacking");
                row[2] = rs.getString("status_repacking");
                row[3] = rs.getString("keterangan_repacking");
                row[4] = rs.getString("nama_pegawai");
                double susut_hilang = Math.round((rs.getFloat("gram_asal") - rs.getFloat("gram_hasil")) / rs.getFloat("gram_asal") * 10000.f) / 100.f;
                total_sh = total_sh + susut_hilang;
                row[5] = susut_hilang;
                row[6] = rs.getString("kode_rsb");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_data_repacking);
            int total_data_repacking = table_data_repacking.getRowCount();
            label_avg_sh_repacking.setText(decimalFormat.format(total_sh / total_data_repacking));
            label_total_repacking.setText(Integer.toString(total_data_repacking));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_BoxBahanJadi_Keuangan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_detailRepacking() {
        try {
            DefaultTableModel model_asal = (DefaultTableModel) table_asal_repacking.getModel();
            DefaultTableModel model_hasil = (DefaultTableModel) table_hasil_repacking.getModel();
            model_asal.setRowCount(0);
            model_hasil.setRowCount(0);
            int i = table_data_repacking.getSelectedRow();
            int total_kpg_asal = 0, total_kpg_hasil = 0;
            float total_gram_asal = 0, total_gram_hasil = 0;
            if (i > -1) {
                sql = "SELECT `kode_repacking`, `tb_repacking`.`no_box`, `kode_grade`, `tb_repacking`.`keping`, `tb_repacking`.`gram`, `tb_repacking`.`status`, `tb_box_bahan_jadi`.`status_terakhir`, `tb_box_bahan_jadi`.`lokasi_terakhir`, `no_tutupan`, `nama_pegawai` AS 'pekerja_repacking', `tb_box_bahan_jadi`.`tanggal_repacking`"
                        + "FROM `tb_repacking` "
                        + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_repacking`.`no_box` = `tb_box_bahan_jadi`.`no_box`"
                        + "LEFT JOIN `tb_karyawan` ON `tb_box_bahan_jadi`.`pekerja_repacking` = `tb_karyawan`.`id_pegawai` "
                        + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`"
                        + "WHERE `kode_repacking` LIKE '%" + table_data_repacking.getValueAt(i, 0) + "%'";
                rs = Utility.db.getStatement().executeQuery(sql);
                Object[] row = new Object[10];
                while (rs.next()) {
                    row[0] = rs.getString("kode_repacking");
                    row[1] = rs.getString("no_box");
                    row[2] = rs.getString("kode_grade");
                    row[3] = rs.getInt("keping");
                    row[4] = rs.getFloat("gram");
                    row[5] = rs.getString("status_terakhir");
                    row[6] = rs.getString("lokasi_terakhir");
                    row[7] = rs.getString("no_tutupan");
                    row[8] = rs.getString("pekerja_repacking");
                    row[9] = rs.getDate("tanggal_repacking");
                    if (null == rs.getString("status")) {
                    } else {
                        switch (rs.getString("status")) {
                            case "ASAL":
                                total_kpg_asal = total_kpg_asal + rs.getInt("keping");
                                total_gram_asal = total_gram_asal + rs.getFloat("gram");
                                model_asal.addRow(row);
                                break;
                            case "HASIL":
                                total_kpg_hasil = total_kpg_hasil + rs.getInt("keping");
                                total_gram_hasil = total_gram_hasil + rs.getFloat("gram");
                                model_hasil.addRow(row);
                                break;
                            default:
                                break;
                        }
                    }

                }
                label_total_asal_repacking.setText(Integer.toString(table_asal_repacking.getRowCount()));
                label_total_keping_asal_repacking.setText(Integer.toString(total_kpg_asal));
                label_total_gram_asal_repacking.setText(Float.toString(total_gram_asal));
                label_total_hasil_repacking.setText(Integer.toString(table_hasil_repacking.getRowCount()));
                label_total_keping_hasil_repacking.setText(Integer.toString(total_kpg_hasil));
                label_total_gram_hasil_repacking.setText(Float.toString(total_gram_hasil));
                ColumnsAutoSizer.sizeColumnsToFit(table_asal_repacking);
                ColumnsAutoSizer.sizeColumnsToFit(table_hasil_repacking);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_BoxBahanJadi_Keuangan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel61 = new javax.swing.JLabel();
        label_total_stok1 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel_Data_Box = new javax.swing.JPanel();
        button_search_Box = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        table_dataBox = new javax.swing.JTable();
        jLabel38 = new javax.swing.JLabel();
        txt_search_no_tutupan = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        txt_search_no_box = new javax.swing.JTextField();
        button_export_data_box = new javax.swing.JButton();
        jLabel41 = new javax.swing.JLabel();
        label_total_data_box = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        label_total_kpg_data_box = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        label_total_gram_data_box = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        ComboBox_lokasi = new javax.swing.JComboBox<>();
        jLabel53 = new javax.swing.JLabel();
        Date_box1 = new com.toedter.calendar.JDateChooser();
        Date_box2 = new com.toedter.calendar.JDateChooser();
        jLabel63 = new javax.swing.JLabel();
        txt_search_no_invoice = new javax.swing.JTextField();
        jLabel42 = new javax.swing.JLabel();
        txt_search_grade = new javax.swing.JTextField();
        jLabel46 = new javax.swing.JLabel();
        label_total_nilai_hpp = new javax.swing.JLabel();
        jPanel_data_rePacking = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_data_repacking = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txt_search_kode_repacking = new javax.swing.JTextField();
        Date_Search_Repacking1 = new com.toedter.calendar.JDateChooser();
        Date_Search_Repacking2 = new com.toedter.calendar.JDateChooser();
        button_search_repacking = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        label_total_repacking = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        table_asal_repacking = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        table_hasil_repacking = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        label_total_hasil_repacking = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        label_total_keping_hasil_repacking = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        label_total_gram_hasil_repacking = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        label_total_asal_repacking = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        label_total_keping_asal_repacking = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        label_total_gram_asal_repacking = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        label_kode_repacking1 = new javax.swing.JLabel();
        label_kode_repacking2 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        button_export_asalRepacking = new javax.swing.JButton();
        button_export_hasilRepacking = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        label_avg_sh_repacking = new javax.swing.JLabel();

        jLabel61.setBackground(new java.awt.Color(255, 255, 255));
        jLabel61.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel61.setText("Total Stok :");

        label_total_stok1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_stok1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_stok1.setText("0");

        setBackground(new java.awt.Color(255, 255, 255));

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel_Data_Box.setBackground(new java.awt.Color(255, 255, 255));

        button_search_Box.setBackground(new java.awt.Color(255, 255, 255));
        button_search_Box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_Box.setText("Search");
        button_search_Box.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_BoxActionPerformed(evt);
            }
        });

        table_dataBox.setAutoCreateRowSorter(true);
        table_dataBox.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_dataBox.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No. Box", "Tgl Box", "Grade", "Keping", "Berat", "No Tutupan", "Status terakhir", "Lokasi", "invoice"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        jScrollPane6.setViewportView(table_dataBox);

        jLabel38.setBackground(new java.awt.Color(255, 255, 255));
        jLabel38.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel38.setText("No. Tutupan :");

        txt_search_no_tutupan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_no_tutupan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_no_tutupanKeyPressed(evt);
            }
        });

        jLabel39.setBackground(new java.awt.Color(255, 255, 255));
        jLabel39.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel39.setText("No. Box :");

        txt_search_no_box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_no_box.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_no_boxKeyPressed(evt);
            }
        });

        button_export_data_box.setBackground(new java.awt.Color(255, 255, 255));
        button_export_data_box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_data_box.setText("Export to Excel");
        button_export_data_box.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_data_boxActionPerformed(evt);
            }
        });

        jLabel41.setBackground(new java.awt.Color(255, 255, 255));
        jLabel41.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel41.setText("Total Box :");

        label_total_data_box.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_box.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_data_box.setText("0");

        jLabel43.setBackground(new java.awt.Color(255, 255, 255));
        jLabel43.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel43.setText("Keping :");

        label_total_kpg_data_box.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg_data_box.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_kpg_data_box.setText("0");

        jLabel45.setBackground(new java.awt.Color(255, 255, 255));
        jLabel45.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel45.setText("Gram :");

        label_total_gram_data_box.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_data_box.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gram_data_box.setText("0");

        jLabel47.setBackground(new java.awt.Color(255, 255, 255));
        jLabel47.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel47.setText("Lokasi :");

        ComboBox_lokasi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_lokasi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        jLabel53.setBackground(new java.awt.Color(255, 255, 255));
        jLabel53.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel53.setText("Tanggal Box :");

        Date_box1.setBackground(new java.awt.Color(255, 255, 255));
        Date_box1.setDateFormatString("dd MMMM yyyy");
        Date_box1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_box2.setBackground(new java.awt.Color(255, 255, 255));
        Date_box2.setDateFormatString("dd MMMM yyyy");
        Date_box2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel63.setBackground(new java.awt.Color(255, 255, 255));
        jLabel63.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel63.setText("No Invoice :");

        txt_search_no_invoice.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_no_invoice.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_no_invoiceKeyPressed(evt);
            }
        });

        jLabel42.setBackground(new java.awt.Color(255, 255, 255));
        jLabel42.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel42.setText("Grade :");

        txt_search_grade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_grade.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_gradeKeyPressed(evt);
            }
        });

        jLabel46.setBackground(new java.awt.Color(255, 255, 255));
        jLabel46.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel46.setText("Total Nilai HPP :");

        label_total_nilai_hpp.setBackground(new java.awt.Color(255, 255, 255));
        label_total_nilai_hpp.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_nilai_hpp.setText("0");

        javax.swing.GroupLayout jPanel_Data_BoxLayout = new javax.swing.GroupLayout(jPanel_Data_Box);
        jPanel_Data_Box.setLayout(jPanel_Data_BoxLayout);
        jPanel_Data_BoxLayout.setHorizontalGroup(
            jPanel_Data_BoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Data_BoxLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Data_BoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6)
                    .addGroup(jPanel_Data_BoxLayout.createSequentialGroup()
                        .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_no_tutupan, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel39)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_no_box, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel42)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel53)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_box1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_box2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel63)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_no_invoice, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel47)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_lokasi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search_Box)
                        .addGap(0, 157, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_Data_BoxLayout.createSequentialGroup()
                        .addComponent(jLabel41)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data_box)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel43)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_kpg_data_box)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel45)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_data_box)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel46)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_nilai_hpp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_export_data_box)))
                .addContainerGap())
        );
        jPanel_Data_BoxLayout.setVerticalGroup(
            jPanel_Data_BoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Data_BoxLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Data_BoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel_Data_BoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_no_tutupan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_Data_BoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_no_box, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_box1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_box2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_Data_BoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel63, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_no_invoice, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ComboBox_lokasi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_search_Box, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Data_BoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_export_data_box, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_data_box, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_kpg_data_box, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram_data_box, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_Data_BoxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_nilai_hpp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 598, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Data Box Barang Jadi", jPanel_Data_Box);

        jPanel_data_rePacking.setBackground(new java.awt.Color(255, 255, 255));

        table_data_repacking.setAutoCreateRowSorter(true);
        table_data_repacking.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_repacking.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Repacking", "Tanggal Repacking", "Status", "Keterangan", "Pekerja", "% SH", "Kode RSB"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.String.class
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
        table_data_repacking.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table_data_repacking);

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Kode Repacking :");

        txt_search_kode_repacking.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_kode_repacking.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_kode_repackingKeyPressed(evt);
            }
        });

        Date_Search_Repacking1.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search_Repacking1.setDateFormatString("dd MMMM yyyy");
        Date_Search_Repacking1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_Search_Repacking2.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search_Repacking2.setDateFormatString("dd MMMM yyyy");
        Date_Search_Repacking2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_search_repacking.setBackground(new java.awt.Color(255, 255, 255));
        button_search_repacking.setText("Search");
        button_search_repacking.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_repackingActionPerformed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Total Data :");

        label_total_repacking.setBackground(new java.awt.Color(255, 255, 255));
        label_total_repacking.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_repacking.setText("0");

        table_asal_repacking.setAutoCreateRowSorter(true);
        table_asal_repacking.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_asal_repacking.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Repacking", "No Box", "Grade", "Keping", "Gram", "Status Box", "Lokasi Box", "Tutupan", "Pekerja Repacking", "Tgl Repacking"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_asal_repacking.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(table_asal_repacking);

        table_hasil_repacking.setAutoCreateRowSorter(true);
        table_hasil_repacking.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_hasil_repacking.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Repacking", "No Box", "Grade", "Keping", "Gram", "Status Box", "Lokasi Box"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_hasil_repacking.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(table_hasil_repacking);

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel3.setText("Tabel Data Hasil Re-Packing");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel4.setText("Tabel Data Asal Re-Packing");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Total Data :");

        label_total_hasil_repacking.setBackground(new java.awt.Color(255, 255, 255));
        label_total_hasil_repacking.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_hasil_repacking.setText("0");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Keping :");

        label_total_keping_hasil_repacking.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_hasil_repacking.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_keping_hasil_repacking.setText("0");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Gram :");

        label_total_gram_hasil_repacking.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_hasil_repacking.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_gram_hasil_repacking.setText("0");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Total Data :");

        label_total_asal_repacking.setBackground(new java.awt.Color(255, 255, 255));
        label_total_asal_repacking.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_asal_repacking.setText("0");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("Keping :");

        label_total_keping_asal_repacking.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_asal_repacking.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_keping_asal_repacking.setText("0");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("Gram :");

        label_total_gram_asal_repacking.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_asal_repacking.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_gram_asal_repacking.setText("0");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel11.setText("Data Re-Packing");

        label_kode_repacking1.setBackground(new java.awt.Color(255, 255, 255));
        label_kode_repacking1.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_kode_repacking1.setText("KODE");

        label_kode_repacking2.setBackground(new java.awt.Color(255, 255, 255));
        label_kode_repacking2.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_kode_repacking2.setText("KODE");

        jLabel50.setBackground(new java.awt.Color(255, 255, 255));
        jLabel50.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel50.setText("Tanggal Repacking :");

        button_export_asalRepacking.setBackground(new java.awt.Color(255, 255, 255));
        button_export_asalRepacking.setText("Export");
        button_export_asalRepacking.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_asalRepackingActionPerformed(evt);
            }
        });

        button_export_hasilRepacking.setBackground(new java.awt.Color(255, 255, 255));
        button_export_hasilRepacking.setText("Export");
        button_export_hasilRepacking.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_hasilRepackingActionPerformed(evt);
            }
        });

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("AVG SH :");

        label_avg_sh_repacking.setBackground(new java.awt.Color(255, 255, 255));
        label_avg_sh_repacking.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_avg_sh_repacking.setText("0");

        javax.swing.GroupLayout jPanel_data_rePackingLayout = new javax.swing.GroupLayout(jPanel_data_rePacking);
        jPanel_data_rePacking.setLayout(jPanel_data_rePackingLayout);
        jPanel_data_rePackingLayout.setHorizontalGroup(
            jPanel_data_rePackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_data_rePackingLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_data_rePackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_data_rePackingLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_kode_repacking, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel50)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Search_Repacking1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Search_Repacking2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search_repacking)
                        .addGap(0, 607, Short.MAX_VALUE))
                    .addGroup(jPanel_data_rePackingLayout.createSequentialGroup()
                        .addGroup(jPanel_data_rePackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addGroup(jPanel_data_rePackingLayout.createSequentialGroup()
                                .addGroup(jPanel_data_rePackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel_data_rePackingLayout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_repacking)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel12)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_avg_sh_repacking))
                                    .addComponent(jLabel11))
                                .addGap(0, 503, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_data_rePackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel_data_rePackingLayout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_kode_repacking1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(button_export_asalRepacking))
                            .addGroup(jPanel_data_rePackingLayout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_asal_repacking)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_keping_asal_repacking)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_asal_repacking))
                            .addGroup(jPanel_data_rePackingLayout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_kode_repacking2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(button_export_hasilRepacking))
                            .addGroup(jPanel_data_rePackingLayout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_hasil_repacking)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_keping_hasil_repacking)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_hasil_repacking))
                            .addComponent(jScrollPane2)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 701, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel_data_rePackingLayout.setVerticalGroup(
            jPanel_data_rePackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_data_rePackingLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_data_rePackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_data_rePackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_kode_repacking, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_data_rePackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel_data_rePackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date_Search_Repacking1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date_Search_Repacking2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(button_search_repacking, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_data_rePackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_data_rePackingLayout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_data_rePackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_data_rePackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(label_avg_sh_repacking, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel_data_rePackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(label_total_repacking, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel_data_rePackingLayout.createSequentialGroup()
                        .addGroup(jPanel_data_rePackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_kode_repacking1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_export_asalRepacking, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_data_rePackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_asal_repacking, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_keping_asal_repacking, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_gram_asal_repacking, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_data_rePackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_kode_repacking2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_export_hasilRepacking, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_data_rePackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_hasil_repacking, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_keping_hasil_repacking, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_gram_hasil_repacking, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Data Re-Packing", jPanel_data_rePacking);

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

    private void button_search_BoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_BoxActionPerformed
        // TODO add your handling code here:
        refreshTable_DataBox();
    }//GEN-LAST:event_button_search_BoxActionPerformed

    private void txt_search_no_tutupanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_no_tutupanKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_DataBox();
        }
    }//GEN-LAST:event_txt_search_no_tutupanKeyPressed

    private void txt_search_no_boxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_no_boxKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_DataBox();
        }
    }//GEN-LAST:event_txt_search_no_boxKeyPressed

    private void button_export_data_boxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_data_boxActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_dataBox.getModel();
        ExportToExcel.writeToExcel(model, jPanel_data_rePacking);
    }//GEN-LAST:event_button_export_data_boxActionPerformed

    private void txt_search_kode_repackingKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_kode_repackingKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_Repacking();
        }
    }//GEN-LAST:event_txt_search_kode_repackingKeyPressed

    private void button_search_repackingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_repackingActionPerformed
        // TODO add your handling code here:
        refreshTable_Repacking();
        DefaultTableModel model1 = (DefaultTableModel) table_asal_repacking.getModel();
        model1.setRowCount(0);
        DefaultTableModel model2 = (DefaultTableModel) table_hasil_repacking.getModel();
        model2.setRowCount(0);
    }//GEN-LAST:event_button_search_repackingActionPerformed

    private void button_export_asalRepackingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_asalRepackingActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_asal_repacking.getModel();
        ExportToExcel.writeToExcel(model, jPanel_data_rePacking);
    }//GEN-LAST:event_button_export_asalRepackingActionPerformed

    private void button_export_hasilRepackingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_hasilRepackingActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_hasil_repacking.getModel();
        ExportToExcel.writeToExcel(model, jPanel_data_rePacking);
    }//GEN-LAST:event_button_export_hasilRepackingActionPerformed

    private void txt_search_no_invoiceKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_no_invoiceKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_DataBox();
        }
    }//GEN-LAST:event_txt_search_no_invoiceKeyPressed

    private void txt_search_gradeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_gradeKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_search_gradeKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_lokasi;
    private com.toedter.calendar.JDateChooser Date_Search_Repacking1;
    private com.toedter.calendar.JDateChooser Date_Search_Repacking2;
    private com.toedter.calendar.JDateChooser Date_box1;
    private com.toedter.calendar.JDateChooser Date_box2;
    private javax.swing.JButton button_export_asalRepacking;
    private javax.swing.JButton button_export_data_box;
    private javax.swing.JButton button_export_hasilRepacking;
    public static javax.swing.JButton button_search_Box;
    private javax.swing.JButton button_search_repacking;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel_Data_Box;
    private javax.swing.JPanel jPanel_data_rePacking;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label_avg_sh_repacking;
    private javax.swing.JLabel label_kode_repacking1;
    private javax.swing.JLabel label_kode_repacking2;
    private javax.swing.JLabel label_total_asal_repacking;
    private javax.swing.JLabel label_total_data_box;
    private javax.swing.JLabel label_total_gram_asal_repacking;
    private javax.swing.JLabel label_total_gram_data_box;
    private javax.swing.JLabel label_total_gram_hasil_repacking;
    private javax.swing.JLabel label_total_hasil_repacking;
    private javax.swing.JLabel label_total_keping_asal_repacking;
    private javax.swing.JLabel label_total_keping_hasil_repacking;
    private javax.swing.JLabel label_total_kpg_data_box;
    private javax.swing.JLabel label_total_nilai_hpp;
    private javax.swing.JLabel label_total_repacking;
    private javax.swing.JLabel label_total_stok1;
    private javax.swing.JTable table_asal_repacking;
    public static javax.swing.JTable table_dataBox;
    private javax.swing.JTable table_data_repacking;
    private javax.swing.JTable table_hasil_repacking;
    private javax.swing.JTextField txt_search_grade;
    private javax.swing.JTextField txt_search_kode_repacking;
    private javax.swing.JTextField txt_search_no_box;
    private javax.swing.JTextField txt_search_no_invoice;
    public static javax.swing.JTextField txt_search_no_tutupan;
    // End of variables declaration//GEN-END:variables
}
