package waleta_system.BahanBaku;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;
import waleta_system.Class.Utility;
import waleta_system.MainForm;

public class JPanel_BahanBakuMasuk_Cheat extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_BahanBakuMasuk_Cheat() {
        initComponents();
        TableRowSorter tableRowSorter = new TableRowSorter(Table_Bahan_Baku_Masuk_ct.getModel());
        Table_Bahan_Baku_Masuk_ct.setRowSorter(tableRowSorter);
    }

    public void init() {
        try {
            Table_Bahan_Baku_Masuk_ct.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));
            refreshTable();

            String query = "SELECT `nama_supplier` FROM `tb_supplier` WHERE `status_aktif` = 1 ORDER BY `nama_supplier` ASC";
            ResultSet rs1 = Utility.db.getStatement().executeQuery(query);
            while (rs1.next()) {
                ComboBox_supplier.addItem(rs1.getString("nama_supplier"));
            }

            query = "SELECT `nama_rumah_burung` FROM `tb_rumah_burung` ORDER BY `nama_rumah_burung`";
            rs1 = Utility.db.getStatement().executeQuery(query);
            while (rs1.next()) {
                ComboBox_RumahBurung.addItem(rs1.getString("nama_rumah_burung"));
            }

            query = "SELECT `kode_kh` FROM `tb_dokumen_kh` WHERE 1 ORDER BY `tanggal_kh` DESC";
            rs1 = Utility.db.getStatement().executeQuery(query);
            while (rs1.next()) {
                ComboBox_kode_kh.addItem(rs1.getString("kode_kh"));
            }
            AutoCompleteDecorator.decorate(ComboBox_kode_kh);
        } catch (Exception ex) {
            Logger.getLogger(JPanel_GradeBahanBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
        Table_Bahan_Baku_Masuk_ct.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && Table_Bahan_Baku_Masuk_ct.getSelectedRow() != -1) {
                    try {
                        // TODO add your handling code here:
                        int i = Table_Bahan_Baku_Masuk_ct.getSelectedRow();
                        txt_no_kartu_pengirim.setText(Table_Bahan_Baku_Masuk_ct.getValueAt(i, 1).toString());
                        ComboBox_supplier.setSelectedItem(Table_Bahan_Baku_Masuk_ct.getValueAt(i, 2).toString());
                        ComboBox_RumahBurung.setSelectedItem(Table_Bahan_Baku_Masuk_ct.getValueAt(i, 3).toString());

                        if (Table_Bahan_Baku_Masuk_ct.getValueAt(i, 4) == null) {
                            Date_Panen.setDate(null);
                        } else {
                            String date_kh = Table_Bahan_Baku_Masuk_ct.getValueAt(i, 4).toString();
                            Date_KH.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(date_kh));
                        }

                        if (Table_Bahan_Baku_Masuk_ct.getValueAt(i, 5) == null) {
                            Date_Panen.setDate(null);
                        } else {
                            String date_masuk = Table_Bahan_Baku_Masuk_ct.getValueAt(i, 5).toString();
                            Date_Bahan_Baku_Masuk.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(date_masuk));
                        }

                        if (Table_Bahan_Baku_Masuk_ct.getValueAt(i, 6) == null) {
                            Date_Panen.setDate(null);
                        } else {
                            String date_panen = Table_Bahan_Baku_Masuk_ct.getValueAt(i, 6).toString();
                            Date_Panen.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(date_panen));
                        }

                        txt_jmlh_koli.setText(Table_Bahan_Baku_Masuk_ct.getValueAt(i, 7).toString());
                        txt_berat_nota.setText(Table_Bahan_Baku_Masuk_ct.getValueAt(i, 8).toString());
                        txt_kadar_air_nota.setText(Table_Bahan_Baku_Masuk_ct.getValueAt(i, 9).toString());
                        txt_berat_waleta.setText(Table_Bahan_Baku_Masuk_ct.getValueAt(i, 10).toString());
                        txt_kadar_air_waleta.setText(Table_Bahan_Baku_Masuk_ct.getValueAt(i, 11).toString());

//                        CheckBox_uji_kerapatan.setSelected(Table_Bahan_Baku_Masuk.getValueAt(i, 12).equals("Passed"));
//                        CheckBox_uji_kerusakan.setSelected(Table_Bahan_Baku_Masuk.getValueAt(i, 13).equals("Passed"));
//                        CheckBox_uji_basah.setSelected(Table_Bahan_Baku_Masuk.getValueAt(i, 14).equals("Passed"));
                        //set tanggal harus setelah tanggal masuk
                        /*Date_Grading.setMinSelectableDate(new SimpleDateFormat("yyyy-MM-dd").parse(date_masuk));*/
                        if (Table_Bahan_Baku_Masuk_ct.getValueAt(i, 15) == null) {
                            Date_Grading.setDate(null);
                        } else {
                            String date_grading = Table_Bahan_Baku_Masuk_ct.getValueAt(i, 15).toString();
                            Date_Grading.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(date_grading));
                        }
                        if (Table_Bahan_Baku_Masuk_ct.getValueAt(i, 16) == null) {
                            Date_timbang.setDate(null);
                        } else {
                            String date_timbang = Table_Bahan_Baku_Masuk_ct.getValueAt(i, 16).toString();
                            Date_timbang.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(date_timbang));
                        }
                        txt_kadar_air_akhir.setText(Table_Bahan_Baku_Masuk_ct.getValueAt(i, 19).toString());
                        try {
                            ComboBox_kode_kh.setSelectedItem(Table_Bahan_Baku_Masuk_ct.getValueAt(i, 22).toString());
                        } catch (Exception e) {
                        }
                    } catch (ParseException ex) {
                        Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
    }

    public void refreshTable() {
        try {
            int keping_grading = 0, gram_grading = 0;
            String showCMP = "";
            decimalFormat.setMaximumFractionDigits(0);
            decimalFormat.setGroupingUsed(true);
            DefaultTableModel model = (DefaultTableModel) Table_Bahan_Baku_Masuk_ct.getModel();
            model.setRowCount(0);
            if (CheckBox_showCMP.isSelected()) {
                showCMP = "";
            } else {
                showCMP = "AND `no_kartu_waleta` NOT LIKE '%CMP%' ";
            }
            String filter_kh = " AND (`tb_dokumen_kh`.`kode_kh` LIKE '%" + txt_search_kh.getText() + "%' OR `kh12` LIKE '%" + txt_search_kh.getText() + "%' OR `kh14` LIKE '%" + txt_search_kh.getText() + "%')";
            if (txt_search_kh.getText().equals("")) {
                filter_kh = "";
            }

            String filter_tanggal = "";
            if (Date_filter1.getDate() != null && Date_filter2.getDate() != null) {
                switch (ComboBox_filterTanggal.getSelectedIndex()) {
                    case 0:
                        filter_tanggal = " AND `tgl_masuk` BETWEEN '" + dateFormat.format(Date_filter1.getDate()) + "' AND '" + dateFormat.format(Date_filter2.getDate()) + "'";
                        break;
                    case 1:
                        filter_tanggal = " AND `tgl_kh` BETWEEN '" + dateFormat.format(Date_filter1.getDate()) + "' AND '" + dateFormat.format(Date_filter2.getDate()) + "'";
                        break;
                    case 2:
                        filter_tanggal = " AND `tgl_panen` BETWEEN '" + dateFormat.format(Date_filter1.getDate()) + "' AND '" + dateFormat.format(Date_filter2.getDate()) + "'";
                        break;
                    case 3:
                        filter_tanggal = " AND `tgl_grading` BETWEEN '" + dateFormat.format(Date_filter1.getDate()) + "' AND '" + dateFormat.format(Date_filter2.getDate()) + "'";
                        break;
                    case 4:
                        filter_tanggal = " AND `tgl_timbang` BETWEEN '" + dateFormat.format(Date_filter1.getDate()) + "' AND '" + dateFormat.format(Date_filter2.getDate()) + "'";
                        break;
                    default:
                        break;
                }
            }
            sql = "SELECT `no_kartu_waleta`, `no_kartu_pengirim`, `tb_supplier`.`nama_supplier`, `tb_bahan_baku_masuk_cheat`.`no_registrasi`, `tb_rumah_burung`.`nama_rumah_burung`, `tgl_kh`, `tgl_masuk`, `jumlah_koli`, `berat_awal`, `kadar_air_nota`, `berat_waleta`, `kadar_air_waleta`, `uji_kerapatan`, `uji_kerusakan`, `uji_basah`, `tgl_grading`, `tgl_timbang`, `berat_real`, `keping_real`, `kadar_air_bahan_baku`, `tgl_panen`, `status_kartu_baku`, `surat_keterangan_pengiriman`, \n"
                    + "`tb_dokumen_kh`.`kode_kh`, `tb_dokumen_kh`.`kh12`, `tb_dokumen_kh`.`kh14`, `nitrit_baku_by` "
                    + "FROM `tb_bahan_baku_masuk_cheat` \n"
                    + "JOIN `tb_rumah_burung` ON `tb_bahan_baku_masuk_cheat`.`no_registrasi`=`tb_rumah_burung`.`no_registrasi`\n"
                    + "JOIN `tb_supplier` ON `tb_bahan_baku_masuk_cheat`.`kode_supplier`=`tb_supplier`.`kode_supplier` "
                    + "LEFT JOIN `tb_dokumen_kh` ON `tb_bahan_baku_masuk_cheat`.`kode_kh`=`tb_dokumen_kh`.`kode_kh` "
                    + "WHERE `no_kartu_waleta` LIKE '%" + txt_search_bahan_masuk.getText() + "%' " + showCMP + " AND `nama_supplier` LIKE '%" + txt_search_supplier.getText() + "%' AND `nama_rumah_burung` LIKE '%" + txt_search_rb.getText() + "%'"
                    + filter_tanggal
                    + filter_kh
                    + "ORDER BY `tgl_masuk` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[30];
            while (rs.next()) {
                row[0] = rs.getString("no_kartu_waleta");
                row[1] = rs.getString("no_kartu_pengirim");
                row[2] = rs.getString("nama_supplier");
                row[3] = rs.getString("nama_rumah_burung");
                row[4] = rs.getDate("tgl_kh");
                row[5] = rs.getDate("tgl_masuk");
                row[6] = rs.getDate("tgl_panen");
                row[7] = rs.getInt("jumlah_koli");
                row[8] = rs.getInt("berat_awal");
                row[9] = rs.getFloat("kadar_air_nota");
                row[10] = rs.getInt("berat_waleta");
                row[11] = rs.getFloat("kadar_air_waleta");
                row[12] = rs.getString("uji_kerapatan");
                row[13] = rs.getString("uji_kerusakan");
                row[14] = rs.getString("uji_basah");
                row[15] = rs.getDate("tgl_grading");
                row[16] = rs.getDate("tgl_timbang");
                row[17] = rs.getInt("keping_real");
                row[18] = rs.getInt("berat_real");
                row[19] = rs.getFloat("kadar_air_bahan_baku");
                if (rs.getInt("status_kartu_baku") == 0) {
                    row[20] = "PROSES";
                } else {
                    row[20] = "SELESAI";
                }
                row[21] = rs.getString("surat_keterangan_pengiriman");
                row[22] = rs.getString("kode_kh");
                row[23] = rs.getString("kh12");
                row[24] = rs.getString("kh14");
                row[25] = rs.getFloat("nitrit_baku_by");
                keping_grading = keping_grading + rs.getInt("keping_real");
                gram_grading = gram_grading + rs.getInt("berat_real");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_Bahan_Baku_Masuk_ct);
            Table_Bahan_Baku_Masuk_ct.setDefaultRenderer(Float.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                    if (column == 17) {
                        if ((Float) Table_Bahan_Baku_Masuk_ct.getValueAt(row, column) > 10) {
                            if (isSelected) {
                                comp.setBackground(Color.gray);
                                comp.setForeground(Color.red);
                            } else {
                                comp.setBackground(Color.PINK);
                                comp.setForeground(Color.red);
                            }
                        } else {
                            if (isSelected) {
                                comp.setBackground(Table_Bahan_Baku_Masuk_ct.getSelectionBackground());
                                comp.setForeground(Table_Bahan_Baku_Masuk_ct.getSelectionForeground());
                            } else {
                                comp.setBackground(Table_Bahan_Baku_Masuk_ct.getBackground());
                                comp.setForeground(Table_Bahan_Baku_Masuk_ct.getForeground());
                            }
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(Table_Bahan_Baku_Masuk_ct.getSelectionBackground());
                            comp.setForeground(Table_Bahan_Baku_Masuk_ct.getSelectionForeground());
                        } else {
                            comp.setBackground(Table_Bahan_Baku_Masuk_ct.getBackground());
                            comp.setForeground(Table_Bahan_Baku_Masuk_ct.getForeground());
                        }
                    }
                    return comp;
                }
            });
            Table_Bahan_Baku_Masuk_ct.repaint();
            int rowData = Table_Bahan_Baku_Masuk_ct.getRowCount();
            label_total_data_bahan_baku_masuk.setText(Integer.toString(rowData));
            label_total_keping_grading.setText(decimalFormat.format(keping_grading));
            label_total_gram_grading.setText(decimalFormat.format(gram_grading));
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_BahanBakuMasuk_Cheat.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, e);
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

        jPanel_Bahan_Baku_Masuk = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        Table_Bahan_Baku_Masuk_ct = new javax.swing.JTable();
        jPanel_operation_bahan_baku = new javax.swing.JPanel();
        label_no_kartu_bayangan = new javax.swing.JLabel();
        label_kode_supplier_bahan_masuk = new javax.swing.JLabel();
        label_tgl_kh = new javax.swing.JLabel();
        label_tgl_masuk = new javax.swing.JLabel();
        label_jumlah_koli = new javax.swing.JLabel();
        label_berat_nota = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        button_update = new javax.swing.JButton();
        txt_no_kartu_pengirim = new javax.swing.JTextField();
        txt_jmlh_koli = new javax.swing.JTextField();
        txt_berat_nota = new javax.swing.JTextField();
        Date_KH = new com.toedter.calendar.JDateChooser();
        Date_Bahan_Baku_Masuk = new com.toedter.calendar.JDateChooser();
        ComboBox_supplier = new javax.swing.JComboBox<>();
        label_kode_rumahBurung = new javax.swing.JLabel();
        ComboBox_RumahBurung = new javax.swing.JComboBox<>();
        label_berat_waleta = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        label_tgl_masuk1 = new javax.swing.JLabel();
        Date_Panen = new com.toedter.calendar.JDateChooser();
        label_kadar_air_akhir = new javax.swing.JLabel();
        txt_kadar_air_akhir = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        label_kode_KH = new javax.swing.JLabel();
        ComboBox_kode_kh = new javax.swing.JComboBox<>();
        label_no_kartu_pengirim1 = new javax.swing.JLabel();
        button_insert = new javax.swing.JButton();
        button_delete = new javax.swing.JButton();
        button_clear = new javax.swing.JButton();
        Spinner_no_kartu_bayangan = new javax.swing.JSpinner();
        label_kadar_air_waleta = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        txt_berat_waleta = new javax.swing.JTextField();
        txt_kadar_air_waleta = new javax.swing.JTextField();
        label_kadar_air_nota = new javax.swing.JLabel();
        txt_kadar_air_nota = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        label_kode_KH1 = new javax.swing.JLabel();
        txt_nitrit_baku = new javax.swing.JTextField();
        jPanel_operation_grading = new javax.swing.JPanel();
        label_tgl_grading = new javax.swing.JLabel();
        Date_Grading = new com.toedter.calendar.JDateChooser();
        button_save_timbang_bahan_baku = new javax.swing.JButton();
        button_detail_grading = new javax.swing.JButton();
        label_tgl_grading1 = new javax.swing.JLabel();
        Date_timbang = new com.toedter.calendar.JDateChooser();
        button_input_skp = new javax.swing.JButton();
        label_total_data_bahan_baku_masuk = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        label_total_keping_grading = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        label_total_gram_grading = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        button_Catatan_Pemeriksaan_Kemasan_Sarang_Burung_Mentah = new javax.swing.JButton();
        button_Catatan_Pemeriksaan_Kebersihan_Sarang_Burung_Mentah_Setelah_Kikir = new javax.swing.JButton();
        button_Catatan_Penimbangan_Sarang_Burung_Mentah = new javax.swing.JButton();
        button_Catatan_Penimbangan_Sarang_Burung_Mentah1 = new javax.swing.JButton();
        button_Catatan_Penerimaan_dan_grading = new javax.swing.JButton();
        txt_search_rb = new javax.swing.JTextField();
        txt_search_bahan_masuk = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        txt_search_kh = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txt_search_supplier = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        CheckBox_showCMP = new javax.swing.JCheckBox();
        Date_filter1 = new com.toedter.calendar.JDateChooser();
        Date_filter2 = new com.toedter.calendar.JDateChooser();
        button_search = new javax.swing.JButton();
        ComboBox_filterTanggal = new javax.swing.JComboBox<>();
        button_export_BahanBakuMasuk = new javax.swing.JButton();

        jPanel_Bahan_Baku_Masuk.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_Bahan_Baku_Masuk.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Bahan Baku Masuk CT", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N
        jPanel_Bahan_Baku_Masuk.setPreferredSize(new java.awt.Dimension(1366, 700));

        Table_Bahan_Baku_Masuk_ct.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_Bahan_Baku_Masuk_ct.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Kartu Waleta", "No Kartu Asal", "Supplier", "R. Burung", "Tgl KH", "Tgl Masuk", "Tgl Panen", "Koli", "Berat Awal / Nota", "KA Nota (%)", "Berat Waleta", "KA Waleta (%)", "Uji Kerapatan", "Uji Kerusakan", "Uji Basah", "Tgl Grading", "Tgl Timbang", "Keping Grading", "Berat Grading", "KA (%)", "Status", "SKP", "kode KH", "KH-12", "KH-14", "Nitrit"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Integer.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_Bahan_Baku_Masuk_ct.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(Table_Bahan_Baku_Masuk_ct);

        jPanel_operation_bahan_baku.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_operation_bahan_baku.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Masuk", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Yu Gothic", 0, 12))); // NOI18N
        jPanel_operation_bahan_baku.setName("aah"); // NOI18N

        label_no_kartu_bayangan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_no_kartu_bayangan.setText("No. Kartu Bayangan :");

        label_kode_supplier_bahan_masuk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_kode_supplier_bahan_masuk.setText("Nama Supplier :");

        label_tgl_kh.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_tgl_kh.setText("Tanggal KH :");

        label_tgl_masuk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_tgl_masuk.setText("Tanggal Masuk :");

        label_jumlah_koli.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_jumlah_koli.setText("Jumlah Koli :");

        label_berat_nota.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_berat_nota.setText("Berat Awal / Nota :");

        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("Koli");

        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("Gram");

        button_update.setBackground(new java.awt.Color(255, 255, 255));
        button_update.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_update.setText("Update");
        button_update.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_updateActionPerformed(evt);
            }
        });

        txt_no_kartu_pengirim.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N

        txt_jmlh_koli.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        txt_jmlh_koli.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_jmlh_koliKeyTyped(evt);
            }
        });

        txt_berat_nota.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        txt_berat_nota.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_berat_notaKeyTyped(evt);
            }
        });

        Date_KH.setDateFormatString("dd MMMM yyyy");
        Date_KH.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_KH.setMaxSelectableDate(new Date());

        Date_Bahan_Baku_Masuk.setDateFormatString("dd MMMM yyyy");
        Date_Bahan_Baku_Masuk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_Bahan_Baku_Masuk.setMaxSelectableDate(new Date());

        ComboBox_supplier.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N

        label_kode_rumahBurung.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_kode_rumahBurung.setText("Nama Rumah Burung :");

        ComboBox_RumahBurung.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N

        label_berat_waleta.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_berat_waleta.setText("Berat Waleta :");

        jLabel23.setText("Gram");

        label_tgl_masuk1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_tgl_masuk1.setText("Tanggal Panen :");

        Date_Panen.setDateFormatString("dd MMMM yyyy");
        Date_Panen.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_Panen.setMaxSelectableDate(new Date());

        label_kadar_air_akhir.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_kadar_air_akhir.setText("Kadar Air Akhir :");

        txt_kadar_air_akhir.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        txt_kadar_air_akhir.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_kadar_air_akhirKeyTyped(evt);
            }
        });

        jLabel24.setText("%");

        label_kode_KH.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_kode_KH.setText("Kode KH :");

        ComboBox_kode_kh.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N

        label_no_kartu_pengirim1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_no_kartu_pengirim1.setText("No. Kartu Pengirim :");

        button_insert.setBackground(new java.awt.Color(255, 255, 255));
        button_insert.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_insert.setText("insert");
        button_insert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_insertActionPerformed(evt);
            }
        });

        button_delete.setBackground(new java.awt.Color(255, 255, 255));
        button_delete.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete.setText("Delete");
        button_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_deleteActionPerformed(evt);
            }
        });

        button_clear.setBackground(new java.awt.Color(255, 255, 255));
        button_clear.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_clear.setText("Clear");
        button_clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_clearActionPerformed(evt);
            }
        });

        Spinner_no_kartu_bayangan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Spinner_no_kartu_bayangan.setModel(new javax.swing.SpinnerNumberModel(1, 1, 999, 1));

        label_kadar_air_waleta.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_kadar_air_waleta.setText("Kadar Air Waleta :");

        jLabel25.setText("%");

        txt_berat_waleta.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        txt_berat_waleta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_berat_waletaKeyTyped(evt);
            }
        });

        txt_kadar_air_waleta.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        txt_kadar_air_waleta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_kadar_air_waletaKeyTyped(evt);
            }
        });

        label_kadar_air_nota.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_kadar_air_nota.setText("Kadar Air Nota :");

        txt_kadar_air_nota.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        txt_kadar_air_nota.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_kadar_air_notaKeyTyped(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel17.setText("%");

        label_kode_KH1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_kode_KH1.setText("Nitrit Baku BY :");

        txt_nitrit_baku.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        txt_nitrit_baku.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_nitrit_bakuKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel_operation_bahan_bakuLayout = new javax.swing.GroupLayout(jPanel_operation_bahan_baku);
        jPanel_operation_bahan_baku.setLayout(jPanel_operation_bahan_bakuLayout);
        jPanel_operation_bahan_bakuLayout.setHorizontalGroup(
            jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_operation_bahan_bakuLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_kode_KH1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kode_KH, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kadar_air_akhir, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kadar_air_waleta, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_berat_waleta, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kadar_air_nota, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_berat_nota, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_jumlah_koli, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_tgl_masuk1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_tgl_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_tgl_kh, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kode_rumahBurung, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kode_supplier_bahan_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_no_kartu_pengirim1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_no_kartu_bayangan, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_operation_bahan_bakuLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Date_Bahan_Baku_Masuk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Date_KH, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txt_no_kartu_pengirim)
                            .addComponent(ComboBox_supplier, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ComboBox_RumahBurung, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_operation_bahan_bakuLayout.createSequentialGroup()
                                .addGroup(jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txt_jmlh_koli)
                                    .addComponent(txt_berat_nota)
                                    .addComponent(txt_kadar_air_akhir)
                                    .addComponent(txt_berat_waleta)
                                    .addComponent(txt_kadar_air_waleta)
                                    .addComponent(txt_kadar_air_nota))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel23)
                                    .addComponent(jLabel10)
                                    .addComponent(jLabel24)
                                    .addComponent(jLabel25)
                                    .addComponent(jLabel17)))
                            .addComponent(Date_Panen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Spinner_no_kartu_bayangan)))
                    .addGroup(jPanel_operation_bahan_bakuLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_nitrit_baku)
                            .addComponent(ComboBox_kode_kh, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_operation_bahan_bakuLayout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addComponent(button_clear)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_insert)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_delete)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_update)
                .addGap(10, 10, 10))
        );
        jPanel_operation_bahan_bakuLayout.setVerticalGroup(
            jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_operation_bahan_bakuLayout.createSequentialGroup()
                .addGroup(jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_no_kartu_bayangan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Spinner_no_kartu_bayangan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_no_kartu_pengirim1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_no_kartu_pengirim, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_kode_supplier_bahan_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_kode_rumahBurung, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_RumahBurung, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(Date_KH, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_tgl_kh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(Date_Bahan_Baku_Masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_tgl_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(Date_Panen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_tgl_masuk1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_jmlh_koli, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_jumlah_koli, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_berat_nota, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_berat_nota, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_kadar_air_nota, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kadar_air_nota, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_berat_waleta, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_berat_waleta, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kadar_air_waleta, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_kadar_air_waleta, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_kadar_air_akhir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kadar_air_akhir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_kode_KH, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_kode_kh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_kode_KH1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_nitrit_baku, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_insert, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_clear, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(button_update, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel_operation_grading.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_operation_grading.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Grading", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Yu Gothic", 0, 12))); // NOI18N
        jPanel_operation_grading.setName("aah"); // NOI18N

        label_tgl_grading.setBackground(new java.awt.Color(255, 255, 255));
        label_tgl_grading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_tgl_grading.setText("Tanggal Grading :");

        Date_Grading.setBackground(new java.awt.Color(255, 255, 255));
        Date_Grading.setDateFormatString("dd MMMM yyyy");
        Date_Grading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_Grading.setMaxSelectableDate(new Date());

        button_save_timbang_bahan_baku.setBackground(new java.awt.Color(255, 255, 255));
        button_save_timbang_bahan_baku.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_save_timbang_bahan_baku.setText("Save");
        button_save_timbang_bahan_baku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_save_timbang_bahan_bakuActionPerformed(evt);
            }
        });

        button_detail_grading.setBackground(new java.awt.Color(255, 255, 255));
        button_detail_grading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_detail_grading.setText("Input Grading");
        button_detail_grading.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_detail_gradingActionPerformed(evt);
            }
        });

        label_tgl_grading1.setBackground(new java.awt.Color(255, 255, 255));
        label_tgl_grading1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_tgl_grading1.setText("Tanggal Timbang :");

        Date_timbang.setBackground(new java.awt.Color(255, 255, 255));
        Date_timbang.setDateFormatString("dd MMMM yyyy");
        Date_timbang.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_timbang.setMaxSelectableDate(new Date());

        button_input_skp.setBackground(new java.awt.Color(255, 255, 255));
        button_input_skp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_input_skp.setText("Input SKP");
        button_input_skp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_skpActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_operation_gradingLayout = new javax.swing.GroupLayout(jPanel_operation_grading);
        jPanel_operation_grading.setLayout(jPanel_operation_gradingLayout);
        jPanel_operation_gradingLayout.setHorizontalGroup(
            jPanel_operation_gradingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_operation_gradingLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_operation_gradingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_operation_gradingLayout.createSequentialGroup()
                        .addComponent(label_tgl_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Grading, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel_operation_gradingLayout.createSequentialGroup()
                        .addComponent(label_tgl_grading1, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_timbang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel_operation_gradingLayout.createSequentialGroup()
                        .addComponent(button_save_timbang_bahan_baku)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_detail_grading)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_input_skp)))
                .addContainerGap())
        );
        jPanel_operation_gradingLayout.setVerticalGroup(
            jPanel_operation_gradingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_operation_gradingLayout.createSequentialGroup()
                .addGroup(jPanel_operation_gradingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(Date_Grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_tgl_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_gradingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(Date_timbang, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_tgl_grading1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel_operation_gradingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_save_timbang_bahan_baku, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_detail_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_input_skp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        label_total_data_bahan_baku_masuk.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_bahan_baku_masuk.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        label_total_data_bahan_baku_masuk.setText("TOTAL");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jLabel8.setText("Total Data :");

        label_total_keping_grading.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_grading.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        label_total_keping_grading.setText("TOTAL");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jLabel14.setText("Total Keping Grading :");

        label_total_gram_grading.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_grading.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        label_total_gram_grading.setText("TOTAL");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jLabel15.setText("Total Gram Grading :");

        button_Catatan_Pemeriksaan_Kemasan_Sarang_Burung_Mentah.setBackground(new java.awt.Color(255, 255, 255));
        button_Catatan_Pemeriksaan_Kemasan_Sarang_Burung_Mentah.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_Catatan_Pemeriksaan_Kemasan_Sarang_Burung_Mentah.setText("Catatan_Pemeriksaan_Kemasan_Sarang_Burung_Mentah");
        button_Catatan_Pemeriksaan_Kemasan_Sarang_Burung_Mentah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_Catatan_Pemeriksaan_Kemasan_Sarang_Burung_MentahActionPerformed(evt);
            }
        });

        button_Catatan_Pemeriksaan_Kebersihan_Sarang_Burung_Mentah_Setelah_Kikir.setBackground(new java.awt.Color(255, 255, 255));
        button_Catatan_Pemeriksaan_Kebersihan_Sarang_Burung_Mentah_Setelah_Kikir.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_Catatan_Pemeriksaan_Kebersihan_Sarang_Burung_Mentah_Setelah_Kikir.setText("Catatan_Pemeriksaan_Kebersihan_Sarang_Burung_Mentah_Setelah_Kikir");
        button_Catatan_Pemeriksaan_Kebersihan_Sarang_Burung_Mentah_Setelah_Kikir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_Catatan_Pemeriksaan_Kebersihan_Sarang_Burung_Mentah_Setelah_KikirActionPerformed(evt);
            }
        });

        button_Catatan_Penimbangan_Sarang_Burung_Mentah.setBackground(new java.awt.Color(255, 255, 255));
        button_Catatan_Penimbangan_Sarang_Burung_Mentah.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_Catatan_Penimbangan_Sarang_Burung_Mentah.setText("Catatan_Penimbangan_Sarang_Burung_Mentah");
        button_Catatan_Penimbangan_Sarang_Burung_Mentah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_Catatan_Penimbangan_Sarang_Burung_MentahActionPerformed(evt);
            }
        });

        button_Catatan_Penimbangan_Sarang_Burung_Mentah1.setBackground(new java.awt.Color(255, 255, 255));
        button_Catatan_Penimbangan_Sarang_Burung_Mentah1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_Catatan_Penimbangan_Sarang_Burung_Mentah1.setText("Surat Keterangan Pengiriman");
        button_Catatan_Penimbangan_Sarang_Burung_Mentah1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_Catatan_Penimbangan_Sarang_Burung_Mentah1ActionPerformed(evt);
            }
        });

        button_Catatan_Penerimaan_dan_grading.setBackground(new java.awt.Color(255, 255, 255));
        button_Catatan_Penerimaan_dan_grading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_Catatan_Penerimaan_dan_grading.setText("Catatan_Penerimaan");
        button_Catatan_Penerimaan_dan_grading.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_Catatan_Penerimaan_dan_gradingActionPerformed(evt);
            }
        });

        txt_search_rb.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_rb.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_rbKeyPressed(evt);
            }
        });

        txt_search_bahan_masuk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_bahan_masuk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_bahan_masukKeyPressed(evt);
            }
        });

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("No Kartu :");

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel16.setText("KH :");

        txt_search_kh.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_kh.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_khKeyPressed(evt);
            }
        });

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("Supplier :");

        txt_search_supplier.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_supplier.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_supplierKeyPressed(evt);
            }
        });

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Rumah Burung :");

        CheckBox_showCMP.setBackground(new java.awt.Color(255, 255, 255));
        CheckBox_showCMP.setText("Show CMP");
        CheckBox_showCMP.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                CheckBox_showCMPItemStateChanged(evt);
            }
        });

        Date_filter1.setBackground(new java.awt.Color(255, 255, 255));
        Date_filter1.setDateFormatString("dd MMM yyyy");
        Date_filter1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_filter2.setBackground(new java.awt.Color(255, 255, 255));
        Date_filter2.setDateFormatString("dd MMM yyyy");
        Date_filter2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_search.setBackground(new java.awt.Color(255, 255, 255));
        button_search.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search.setText("Search");
        button_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_searchActionPerformed(evt);
            }
        });

        ComboBox_filterTanggal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_filterTanggal.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tanggal Masuk", "Tanggal KH", "Tanggal Panen", "Tanggal Grading", "Tanggal Timbang" }));

        button_export_BahanBakuMasuk.setBackground(new java.awt.Color(255, 255, 255));
        button_export_BahanBakuMasuk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_BahanBakuMasuk.setText("Export To Excel");
        button_export_BahanBakuMasuk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_BahanBakuMasukActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_Bahan_Baku_MasukLayout = new javax.swing.GroupLayout(jPanel_Bahan_Baku_Masuk);
        jPanel_Bahan_Baku_Masuk.setLayout(jPanel_Bahan_Baku_MasukLayout);
        jPanel_Bahan_Baku_MasukLayout.setHorizontalGroup(
            jPanel_Bahan_Baku_MasukLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Bahan_Baku_MasukLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Bahan_Baku_MasukLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_Bahan_Baku_MasukLayout.createSequentialGroup()
                        .addComponent(jScrollPane5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_Bahan_Baku_MasukLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel_operation_grading, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel_operation_bahan_baku, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_Bahan_Baku_MasukLayout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data_bahan_baku_masuk))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_Bahan_Baku_MasukLayout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_keping_grading))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_Bahan_Baku_MasukLayout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_grading))))
                    .addGroup(jPanel_Bahan_Baku_MasukLayout.createSequentialGroup()
                        .addComponent(button_Catatan_Penimbangan_Sarang_Burung_Mentah1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_Catatan_Pemeriksaan_Kemasan_Sarang_Burung_Mentah)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_Catatan_Pemeriksaan_Kebersihan_Sarang_Burung_Mentah_Setelah_Kikir)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_Catatan_Penimbangan_Sarang_Burung_Mentah)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_Catatan_Penerimaan_dan_grading)
                        .addGap(0, 45, Short.MAX_VALUE))
                    .addGroup(jPanel_Bahan_Baku_MasukLayout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_bahan_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_rb, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_kh, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_filterTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_filter1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_filter2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(CheckBox_showCMP)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_export_BahanBakuMasuk)))
                .addContainerGap())
        );
        jPanel_Bahan_Baku_MasukLayout.setVerticalGroup(
            jPanel_Bahan_Baku_MasukLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Bahan_Baku_MasukLayout.createSequentialGroup()
                .addGroup(jPanel_Bahan_Baku_MasukLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_bahan_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_rb, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_kh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_filterTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_filter1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_filter2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CheckBox_showCMP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_BahanBakuMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Bahan_Baku_MasukLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_Catatan_Penimbangan_Sarang_Burung_Mentah1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_Catatan_Pemeriksaan_Kemasan_Sarang_Burung_Mentah, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_Catatan_Pemeriksaan_Kebersihan_Sarang_Burung_Mentah_Setelah_Kikir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_Catatan_Penimbangan_Sarang_Burung_Mentah, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_Catatan_Penerimaan_dan_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Bahan_Baku_MasukLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_Bahan_Baku_MasukLayout.createSequentialGroup()
                        .addComponent(jPanel_operation_bahan_baku, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel_operation_grading, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_Bahan_Baku_MasukLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_data_bahan_baku_masuk)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_Bahan_Baku_MasukLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_keping_grading)
                            .addComponent(jLabel14))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_Bahan_Baku_MasukLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_gram_grading)
                            .addComponent(jLabel15))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane5))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel_Bahan_Baku_Masuk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_Bahan_Baku_Masuk, javax.swing.GroupLayout.DEFAULT_SIZE, 746, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void button_updateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_updateActionPerformed
        // TODO add your handling code here:
        decimalFormat.setMaximumFractionDigits(2);
        decimalFormat = Utility.DecimalFormatUS(decimalFormat);
        int j = Table_Bahan_Baku_Masuk_ct.getSelectedRow();
        Boolean Check = true;
        if ("".equals(txt_berat_nota.getText())
                || Date_KH.getDate() == null
                || Date_Bahan_Baku_Masuk.getDate() == null
                || Date_Panen.getDate() == null
                || "".equals(txt_jmlh_koli.getText())
                || "".equals(txt_berat_nota.getText())
                || "".equals(txt_kadar_air_nota.getText())
                || "".equals(txt_berat_waleta.getText())
                || "".equals(txt_kadar_air_waleta.getText())
                || "".equals(txt_kadar_air_akhir.getText())) {
            JOptionPane.showMessageDialog(this, "Silahkan Cek dan Lengkapi Data diatas !");
            Check = false;
        }
        try {
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih salah satu data pada tabel !");
            } else if (Check) {
                String update_tgl_panen = "`tgl_panen` = '" + dateFormat.format(Date_Panen.getDate()) + "', ";
                if (Date_Panen.getDate() == null) {
                    update_tgl_panen = "`tgl_panen` = NULL, ";
                }
                sql = "UPDATE `tb_bahan_baku_masuk_cheat` SET "
                        + "`no_kartu_pengirim` = '" + txt_no_kartu_pengirim.getText() + "', "
                        + "`kode_supplier` = (SELECT `kode_supplier` FROM `tb_supplier` WHERE `nama_supplier`='" + ComboBox_supplier.getSelectedItem() + "'), "
                        + "`no_registrasi`=(SELECT `no_registrasi` FROM `tb_rumah_burung` WHERE `nama_rumah_burung` = '" + ComboBox_RumahBurung.getSelectedItem() + "'), "
                        + "`tgl_kh` = '" + dateFormat.format(Date_KH.getDate()) + "', "
                        + "`tgl_masuk` = '" + dateFormat.format(Date_Bahan_Baku_Masuk.getDate()) + "', "
                        + update_tgl_panen
                        + "`jumlah_koli` = '" + txt_jmlh_koli.getText() + "', "
                        + "`berat_awal` = '" + txt_berat_nota.getText() + "', "
                        + "`kadar_air_waleta` = '" + txt_kadar_air_nota.getText() + "', "
                        + "`berat_waleta` = '" + txt_berat_waleta.getText() + "', "
                        + "`kadar_air_waleta` = '" + txt_kadar_air_waleta.getText() + "', "
                        + "`kadar_air_bahan_baku`='" + txt_kadar_air_akhir.getText() + "', "
                        + "`kode_kh`='" + ComboBox_kode_kh.getSelectedItem() + "', "
                        + "`nitrit_baku_by`='" + txt_nitrit_baku.getText() + "' "
                        + "WHERE `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta` = '" + Table_Bahan_Baku_Masuk_ct.getValueAt(j, 0).toString() + "'";
                executeSQLQuery(sql, "updated !");
            }
        } catch (HeadlessException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, e);
        }
    }//GEN-LAST:event_button_updateActionPerformed

    private void txt_search_bahan_masukKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_bahan_masukKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_bahan_masukKeyPressed

    private void button_export_BahanBakuMasukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_BahanBakuMasukActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_Bahan_Baku_Masuk_ct.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_export_BahanBakuMasukActionPerformed

    private void button_save_timbang_bahan_bakuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_save_timbang_bahan_bakuActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_Bahan_Baku_Masuk_ct.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih salah satu data pada tabel !");
            } else {
                if (Date_Grading.getDate() != null && Date_timbang.getDate() != null) {
                    String Query = "UPDATE `tb_bahan_baku_masuk_cheat` SET `tgl_grading` = '" + dateFormat.format(Date_Grading.getDate()) + "', `tgl_timbang` = '" + dateFormat.format(Date_timbang.getDate()) + "' WHERE `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta` = '" + Table_Bahan_Baku_Masuk_ct.getValueAt(j, 0).toString() + "'";
                    executeSQLQuery(Query, "updated !");
                } else if (Date_Grading.getDate() != null && Date_timbang.getDate() == null) {
                    String Query = "UPDATE `tb_bahan_baku_masuk_cheat` SET `tgl_grading` = '" + dateFormat.format(Date_Grading.getDate()) + "' WHERE `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta` = '" + Table_Bahan_Baku_Masuk_ct.getValueAt(j, 0).toString() + "'";
                    executeSQLQuery(Query, "updated !");
                } else if (Date_Grading.getDate() == null && Date_timbang.getDate() != null) {
                    String Query = "UPDATE `tb_bahan_baku_masuk_cheat` SET `tgl_timbang` = '" + dateFormat.format(Date_timbang.getDate()) + "' WHERE `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta` = '" + Table_Bahan_Baku_Masuk_ct.getValueAt(j, 0).toString() + "'";
                    executeSQLQuery(Query, "updated !");
                } else {
                    JOptionPane.showMessageDialog(this, "silahkan memasukkan tanggal timbang / grading.");
                }
            }
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, e);
        }
    }//GEN-LAST:event_button_save_timbang_bahan_bakuActionPerformed

    private void button_detail_gradingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_detail_gradingActionPerformed
        int j = Table_Bahan_Baku_Masuk_ct.getSelectedRow();
        if (j == -1) {
            JOptionPane.showMessageDialog(this, "Please Select Data First !");
        } else {
            DetailGradingBaku_Cheat FormGrading = new DetailGradingBaku_Cheat();
            FormGrading.setVisible(true);
            FormGrading.setEnabled(true);
            FormGrading.init();
        }
    }//GEN-LAST:event_button_detail_gradingActionPerformed

    private void button_Catatan_Pemeriksaan_Kemasan_Sarang_Burung_MentahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_Catatan_Pemeriksaan_Kemasan_Sarang_Burung_MentahActionPerformed
        try {
            refreshTable();
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(sql);
            //String REPORT = "C:\\Users\\Z475\\Documents\\NetBeansProjects\\JavaApplication5\\src\\javaapplication5\\report1.jrxml";
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Catatan_Pemeriksaan_Kemasan_Sarang_Burung_Mentah.jrxml");
            JASP_DESIGN.setQuery(newQuery);

            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, null, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
        } catch (JRException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_Catatan_Pemeriksaan_Kemasan_Sarang_Burung_MentahActionPerformed

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_button_searchActionPerformed

    private void txt_search_supplierKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_supplierKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_supplierKeyPressed

    private void txt_search_rbKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_rbKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_rbKeyPressed

    private void CheckBox_showCMPItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_CheckBox_showCMPItemStateChanged
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_CheckBox_showCMPItemStateChanged

    private void button_Catatan_Pemeriksaan_Kebersihan_Sarang_Burung_Mentah_Setelah_KikirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_Catatan_Pemeriksaan_Kebersihan_Sarang_Burung_Mentah_Setelah_KikirActionPerformed
        // TODO add your handling code here:
        try {
            refreshTable();
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(sql);
            //String REPORT = "C:\\Users\\Z475\\Documents\\NetBeansProjects\\JavaApplication5\\src\\javaapplication5\\report1.jrxml";
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Catatan_Pemeriksaan_Kebersihan_Sarang_Burung_Mentah_Setelah_Kikir.jrxml");
            JASP_DESIGN.setQuery(newQuery);

            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, null, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
        } catch (JRException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_Catatan_Pemeriksaan_Kebersihan_Sarang_Burung_Mentah_Setelah_KikirActionPerformed

    private void button_Catatan_Penimbangan_Sarang_Burung_MentahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_Catatan_Penimbangan_Sarang_Burung_MentahActionPerformed
        // TODO add your handling code here:
        try {
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(sql);
            //String REPORT = "C:\\Users\\Z475\\Documents\\NetBeansProjects\\JavaApplication5\\src\\javaapplication5\\report1.jrxml";
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Catatan_Penimbangan_Sarang_Burung_Mentah.jrxml");
            JASP_DESIGN.setQuery(newQuery);

            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, null, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
        } catch (JRException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_Catatan_Penimbangan_Sarang_Burung_MentahActionPerformed

    private void button_Catatan_Penimbangan_Sarang_Burung_Mentah1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_Catatan_Penimbangan_Sarang_Burung_Mentah1ActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_Bahan_Baku_Masuk_ct.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Pilih salah satu data !", "warning!", 1);
            } else {
                String nama_file = Table_Bahan_Baku_Masuk_ct.getValueAt(j, 22).toString();
                if (nama_file == null || nama_file.equals("")) {
                    JOptionPane.showMessageDialog(this, "Maaf file tidak di temukan");
                } else {
                    try {
                        Runtime rt = Runtime.getRuntime();
                        Process pr = rt.exec("explorer.exe \\\\192.168.10.2\\Shared Folder\\DOKUMEN SISTEM\\1_Surat_Keterangan_Pengiriman\\" + nama_file + ".pdf");
                    } catch (IOException ex) {
                        Logger.getLogger(JPanel_BahanBakuMasuk_Cheat.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getLocalizedMessage());
            Logger.getLogger(JPanel_BahanBakuMasuk_Cheat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_Catatan_Penimbangan_Sarang_Burung_Mentah1ActionPerformed

    private void button_input_skpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_skpActionPerformed
        // TODO add your handling code here:
        int j = Table_Bahan_Baku_Masuk_ct.getSelectedRow();
        if (j == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih salah satu kartu terlebih dahulu");
        } else {
            String loc = JOptionPane.showInputDialog("Masukkan nama file (.pdf) :");
            if (loc != null && !loc.equals("")) {
                String Query = "UPDATE `tb_bahan_baku_masuk_cheat` SET `surat_keterangan_pengiriman` = '" + loc + "' "
                        + "WHERE `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta` = '" + Table_Bahan_Baku_Masuk_ct.getValueAt(j, 0).toString() + "'";
                executeSQLQuery(Query, "updated !");
            }
        }
    }//GEN-LAST:event_button_input_skpActionPerformed

    private void txt_search_khKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_khKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_khKeyPressed

    private void button_insertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_insertActionPerformed
        // TODO add your handling code here:
        try {
            boolean check = true;
            String LastNumber = String.format("%03d", (int) Spinner_no_kartu_bayangan.getValue());
            //get kode supplier
            String kode_supplier = "BY";
            String kode_kartu = new SimpleDateFormat("yy").format(Date_Bahan_Baku_Masuk.getDate()) + kode_supplier + LastNumber;

            if ("".equals(txt_berat_nota.getText())
                    || Date_KH.getDate() == null
                    || Date_Bahan_Baku_Masuk.getDate() == null
                    || Date_Panen.getDate() == null
                    || "".equals(txt_jmlh_koli.getText())
                    || "".equals(txt_berat_nota.getText())
                    || "".equals(txt_kadar_air_nota.getText())
                    || "".equals(txt_berat_waleta.getText())
                    || "".equals(txt_kadar_air_waleta.getText())
                    || "".equals(txt_kadar_air_akhir.getText())) {
                JOptionPane.showMessageDialog(this, "Silahkan Cek dan Lengkapi Data diatas !");
                check = false;
            }

            if (check) {
                Utility.db.getConnection().setAutoCommit(false);
                String Query2 = "INSERT INTO `tb_bahan_baku_masuk_cheat`("
                        + "`no_kartu_waleta`, "
                        + "`no_kartu_pengirim`, "
                        + "`kode_supplier`, "
                        + "`no_registrasi`, "
                        + "`tgl_kh`, "
                        + "`tgl_masuk`, "
                        + "`tgl_panen`, "
                        + "`jumlah_koli`, "
                        + "`berat_awal`, "
                        + "`kadar_air_nota`, "
                        + "`berat_waleta`, "
                        + "`kadar_air_waleta`, "
                        + "`nitrit_baku_by`"
                        + ")"
                        + " VALUES ("
                        + "'" + kode_kartu + "',"
                        + "'" + txt_no_kartu_pengirim.getText() + "',"
                        + "'" + kode_supplier + "', "
                        + "(SELECT `no_registrasi` FROM `tb_rumah_burung` WHERE `nama_rumah_burung` = '" + ComboBox_RumahBurung.getSelectedItem() + "'), "
                        + "'" + dateFormat.format(Date_KH.getDate()) + "',"
                        + "'" + dateFormat.format(Date_Bahan_Baku_Masuk.getDate()) + "',"
                        + "'" + dateFormat.format(Date_Panen.getDate()) + "',"
                        + "'" + txt_jmlh_koli.getText() + "',"
                        + "'" + txt_berat_nota.getText() + "',"
                        + "'" + txt_kadar_air_nota.getText() + "',"
                        + "'" + txt_berat_waleta.getText() + "',"
                        + "'" + txt_kadar_air_waleta.getText() + "', "
                        + "'" + txt_nitrit_baku.getText() + "'"
                        + ");";
                Utility.db.getStatement().executeUpdate(Query2);
                Utility.db.getConnection().commit();
                button_clear.doClick();
                refreshTable();
                JOptionPane.showMessageDialog(this, "Data berhasil ditambahkan!");
            }
        } catch (SQLException e) {
            try {
                Utility.db.getConnection().rollback();
            } catch (SQLException ex) {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
            }
            JOptionPane.showMessageDialog(this, "data not inserted" + e);
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                Utility.db.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_insertActionPerformed

    private void button_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_deleteActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_Bahan_Baku_Masuk_ct.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang ingin di hapus !");
            } else {
                String no_kartu_bayangan = Table_Bahan_Baku_Masuk_ct.getValueAt(j, 0).toString();
                if (no_kartu_bayangan.contains("BY")) {
                    int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Warning", 0);
                    if (dialogResult == JOptionPane.YES_OPTION) {
                        // delete code here
                        String Query = "DELETE FROM `tb_bahan_baku_masuk_cheat` WHERE `no_kartu_waleta` = '" + no_kartu_bayangan + "'";
                        executeSQLQuery(Query, "deleted !");
                        refreshTable();
                    }
                    button_clear.doClick();
                } else {
                    JOptionPane.showMessageDialog(this, "Maaf, hanya bisa delete kartu bayangan saja!");
                }
            }
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, e);
        }
    }//GEN-LAST:event_button_deleteActionPerformed

    private void button_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_clearActionPerformed
        // TODO add your handling code here:
        Spinner_no_kartu_bayangan.setValue(1);
        txt_no_kartu_pengirim.setText(null);
        ComboBox_supplier.setSelectedIndex(0);
        Date_KH.setDate(null);
        Date_Bahan_Baku_Masuk.setDate(null);
        txt_jmlh_koli.setText(null);
        txt_berat_nota.setText(null);
        txt_kadar_air_waleta.setText(null);
        Date_Grading.setDate(null);
    }//GEN-LAST:event_button_clearActionPerformed

    private void button_Catatan_Penerimaan_dan_gradingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_Catatan_Penerimaan_dan_gradingActionPerformed
        // TODO add your handling code here:
        int j = Table_Bahan_Baku_Masuk_ct.getSelectedRow();
        if (j == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih 1 kartu !");
        } else {
            try {
                String query = "SELECT `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta`, `no_kartu_pengirim`, `tb_supplier`.`nama_supplier`, `tb_bahan_baku_masuk_cheat`.`no_registrasi`, `tb_rumah_burung`.`nama_rumah_burung`, `tgl_kh`, `tgl_masuk`, `jumlah_koli`, `berat_awal`, `kadar_air_waleta`, `uji_kerapatan`, `uji_kerusakan`, `uji_basah`, `tgl_grading`, `tgl_timbang`, `berat_real`, `keping_real`, `kadar_air_bahan_baku`, `tgl_panen`, `kode_grade`, `jumlah_keping`, `total_berat`, `harga_bahanbaku`\n"
                        + "FROM `tb_bahan_baku_masuk_cheat`\n"
                        + "LEFT JOIN `tb_rumah_burung` ON `tb_bahan_baku_masuk_cheat`.`no_registrasi`=`tb_rumah_burung`.`no_registrasi`\n"
                        + "LEFT JOIN `tb_supplier` ON `tb_bahan_baku_masuk_cheat`.`kode_supplier`=`tb_supplier`.`kode_supplier`\n"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta`= `tb_grading_bahan_baku`.`no_kartu_waleta`\n"
                        + "WHERE `tb_grading_bahan_baku`.`no_kartu_waleta` = '" + Table_Bahan_Baku_Masuk_ct.getValueAt(j, 0).toString() + "' ORDER BY `kode_grade`";
                JRDesignQuery newQuery = new JRDesignQuery();
                newQuery.setText(query);
                JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Catatan_Penerimaan_dan_Grading_Sarang_Burung_Mentah_cheat.jrxml");
                JASP_DESIGN.setQuery(newQuery);
                Map<String, Object> map = new HashMap<>();
                map.put("no_kartu_waleta", Table_Bahan_Baku_Masuk_ct.getValueAt(j, 0).toString());//parameter name should be like it was named inside your report.
                JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
                JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
            } catch (JRException ex) {
                Logger.getLogger(DetailGradingBaku_Cheat.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_Catatan_Penerimaan_dan_gradingActionPerformed

    private void txt_jmlh_koliKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_jmlh_koliKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_jmlh_koliKeyTyped

    private void txt_berat_notaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_berat_notaKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_berat_notaKeyTyped

    private void txt_kadar_air_notaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_kadar_air_notaKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_kadar_air_notaKeyTyped

    private void txt_berat_waletaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_berat_waletaKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_berat_waletaKeyTyped

    private void txt_kadar_air_waletaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_kadar_air_waletaKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_kadar_air_waletaKeyTyped

    private void txt_kadar_air_akhirKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_kadar_air_akhirKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_kadar_air_akhirKeyTyped

    private void txt_nitrit_bakuKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_nitrit_bakuKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_nitrit_bakuKeyTyped


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox CheckBox_showCMP;
    private javax.swing.JComboBox<String> ComboBox_RumahBurung;
    private javax.swing.JComboBox<String> ComboBox_filterTanggal;
    private javax.swing.JComboBox<String> ComboBox_kode_kh;
    private javax.swing.JComboBox<String> ComboBox_supplier;
    private com.toedter.calendar.JDateChooser Date_Bahan_Baku_Masuk;
    private com.toedter.calendar.JDateChooser Date_Grading;
    private com.toedter.calendar.JDateChooser Date_KH;
    private com.toedter.calendar.JDateChooser Date_Panen;
    private com.toedter.calendar.JDateChooser Date_filter1;
    private com.toedter.calendar.JDateChooser Date_filter2;
    private com.toedter.calendar.JDateChooser Date_timbang;
    private javax.swing.JSpinner Spinner_no_kartu_bayangan;
    public static javax.swing.JTable Table_Bahan_Baku_Masuk_ct;
    private javax.swing.JButton button_Catatan_Pemeriksaan_Kebersihan_Sarang_Burung_Mentah_Setelah_Kikir;
    private javax.swing.JButton button_Catatan_Pemeriksaan_Kemasan_Sarang_Burung_Mentah;
    private javax.swing.JButton button_Catatan_Penerimaan_dan_grading;
    private javax.swing.JButton button_Catatan_Penimbangan_Sarang_Burung_Mentah;
    private javax.swing.JButton button_Catatan_Penimbangan_Sarang_Burung_Mentah1;
    private javax.swing.JButton button_clear;
    public javax.swing.JButton button_delete;
    private javax.swing.JButton button_detail_grading;
    private javax.swing.JButton button_export_BahanBakuMasuk;
    private javax.swing.JButton button_input_skp;
    public javax.swing.JButton button_insert;
    public javax.swing.JButton button_save_timbang_bahan_baku;
    public static javax.swing.JButton button_search;
    public javax.swing.JButton button_update;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel_Bahan_Baku_Masuk;
    private javax.swing.JPanel jPanel_operation_bahan_baku;
    private javax.swing.JPanel jPanel_operation_grading;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JLabel label_berat_nota;
    private javax.swing.JLabel label_berat_waleta;
    private javax.swing.JLabel label_jumlah_koli;
    private javax.swing.JLabel label_kadar_air_akhir;
    private javax.swing.JLabel label_kadar_air_nota;
    private javax.swing.JLabel label_kadar_air_waleta;
    private javax.swing.JLabel label_kode_KH;
    private javax.swing.JLabel label_kode_KH1;
    private javax.swing.JLabel label_kode_rumahBurung;
    private javax.swing.JLabel label_kode_supplier_bahan_masuk;
    private javax.swing.JLabel label_no_kartu_bayangan;
    private javax.swing.JLabel label_no_kartu_pengirim1;
    private javax.swing.JLabel label_tgl_grading;
    private javax.swing.JLabel label_tgl_grading1;
    private javax.swing.JLabel label_tgl_kh;
    private javax.swing.JLabel label_tgl_masuk;
    private javax.swing.JLabel label_tgl_masuk1;
    private javax.swing.JLabel label_total_data_bahan_baku_masuk;
    private javax.swing.JLabel label_total_gram_grading;
    private javax.swing.JLabel label_total_keping_grading;
    private javax.swing.JTextField txt_berat_nota;
    private javax.swing.JTextField txt_berat_waleta;
    private javax.swing.JTextField txt_jmlh_koli;
    private javax.swing.JTextField txt_kadar_air_akhir;
    private javax.swing.JTextField txt_kadar_air_nota;
    private javax.swing.JTextField txt_kadar_air_waleta;
    private javax.swing.JTextField txt_nitrit_baku;
    private javax.swing.JTextField txt_no_kartu_pengirim;
    private javax.swing.JTextField txt_search_bahan_masuk;
    private javax.swing.JTextField txt_search_kh;
    private javax.swing.JTextField txt_search_rb;
    private javax.swing.JTextField txt_search_supplier;
    // End of variables declaration//GEN-END:variables
}
