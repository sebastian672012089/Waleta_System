package waleta_system.Packing;

import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import waleta_system.BahanBaku.JPanel_GradeBahanBaku;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;
import waleta_system.Class.ExportToExcel;
import waleta_system.Class.TableColumnHider;
import waleta_system.MainForm;

public class JPanel_SPK extends javax.swing.JPanel {

    String sql = null;
    String sql2 = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    PreparedStatement pst;
//    static TableColumnHider hider;

    public JPanel_SPK() {
        initComponents();
    }

    public void HidePriceColumn() {
        TableColumnHider hider1 = new TableColumnHider(tabel_data_spk);
        hider1.hide("Harga");
        TableColumnHider hider2 = new TableColumnHider(tabel_detail_spk);
        hider2.hide("CNY / Kg");
        hider2.hide("CNY");
    }

    public void ShowPriceColumn() {
        TableColumnHider hider1 = new TableColumnHider(tabel_data_spk);
        hider1.show("Harga");
        TableColumnHider hider2 = new TableColumnHider(tabel_detail_spk);
        hider2.show("CNY / Kg");
        hider2.show("CNY");
    }

    public void init() {
        try {
            refreshTable_spk();
            tabel_data_spk.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && tabel_data_spk.getSelectedRow() != -1) {
                        int i = tabel_data_spk.getSelectedRow();
                        label_no_spk.setText(tabel_data_spk.getValueAt(i, 0).toString());
                        refreshTable_detail();

                        if (tabel_data_spk.getValueAt(i, 10).toString().equals("FIXED")) {
                            button_delete_spk.setEnabled(false);
                            button_fix.setText("Not Fix");
                            button_send_out.setEnabled(true);
                            if (tabel_data_spk.getValueAt(i, 11).toString().equals("SEND OUT")) {
                                button_send_out.setText("PROSES");
                                button_edit_spk.setEnabled(false);
                            } else {
                                button_send_out.setText("SEND OUT");
                                button_edit_spk.setEnabled(true);
                            }
                        } else {
                            button_delete_spk.setEnabled(true);
                            button_edit_spk.setEnabled(true);
                            button_fix.setText("Fix");
                            button_send_out.setEnabled(false);
                        }
                    }
                }
            });
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_GradeBahanBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_spk() {
        try {
            String Search = null, filter_status = "", filter_send_out = "";
            double total_harga = 0;
            double total_gram = 0;
            float berat_spk = 0, berat_proses = 0;
            switch (ComboBox_Search.getSelectedIndex()) {
                case 0:
                    Search = "`tb_spk`.`kode_spk`";
                    break;
                case 1:
                    Search = "`tb_buyer`.`nama`";
                    break;
                default:
                    break;
            }

            switch (ComboBox_Search_status.getSelectedIndex()) {
                case 0:
                    filter_status = "";
                    break;
                case 1:
                    filter_status = " AND `tb_spk`.`fix` = 'FIXED'";
                    break;
                case 2:
                    filter_status = " AND `tb_spk`.`fix` = 'Not Fix'";
                    break;
                default:
                    filter_status = "";
                    break;
            }

            switch (ComboBox_send_out.getSelectedIndex()) {
                case 0:
                    filter_send_out = "";
                    break;
                case 1:
                    filter_send_out = " AND `tb_spk`.`status` = 'SEND OUT' ";
                    break;
                case 2:
                    filter_send_out = " AND `tb_spk`.`status` = 'PROSES' ";
                    break;
                default:
                    filter_send_out = "";
                    break;
            }

            DefaultTableModel model = (DefaultTableModel) tabel_data_spk.getModel();
            model.setRowCount(0);
            if (Date_Search1.getDate() != null && Date_Search2.getDate() != null) {
                sql = "SELECT `tb_spk`.`kode_spk`, `tb_buyer`.`nama`, `no_revisi`, `tanggal_spk`, `tanggal_keluar`, `tanggal_awb`, `tanggal_fix`, `detail`, `status`, `fix`, SUM(`tb_spk_detail`.`jumlah_kemasan`) AS 'jumlah_kemasan' , SUM(`tb_spk_detail`.`berat`) AS 'berat' , SUM(`tb_spk_detail`.`berat` * (`tb_spk_detail`.`harga_cny`/1000)) AS 'harga', `approved` "
                        + "FROM `tb_spk` "
                        + "LEFT JOIN `tb_buyer` ON `tb_spk`.`buyer` = `tb_buyer`.`kode_buyer` "
                        + "LEFT JOIN `tb_spk_detail` ON `tb_spk`.`kode_spk` = `tb_spk_detail`.`kode_spk`"
                        + "WHERE " + Search + " LIKE '%" + txt_search_keywords.getText() + "%'" + filter_status + filter_send_out + " "
                        + "AND (`tanggal_keluar` BETWEEN '" + dateFormat.format(Date_Search1.getDate()) + "' AND '" + dateFormat.format(Date_Search2.getDate()) + "')"
                        + "GROUP BY `tb_spk`.`kode_spk` ORDER BY `tanggal_spk` DESC";
            } else {
                sql = "SELECT `tb_spk`.`kode_spk`, `tb_buyer`.`nama`, `no_revisi`, `tanggal_spk`, `tanggal_keluar`, `tanggal_awb`, `tanggal_fix`, `detail`, `status`, `fix`, SUM(`tb_spk_detail`.`jumlah_kemasan`) AS 'jumlah_kemasan' , SUM(`tb_spk_detail`.`berat`) AS 'berat' , SUM(`tb_spk_detail`.`berat` * (`tb_spk_detail`.`harga_cny`/1000)) AS 'harga', `approved` "
                        + "FROM `tb_spk` "
                        + "LEFT JOIN `tb_buyer` ON `tb_spk`.`buyer` = `tb_buyer`.`kode_buyer` "
                        + "LEFT JOIN `tb_spk_detail` ON `tb_spk`.`kode_spk` = `tb_spk_detail`.`kode_spk`"
                        + "WHERE " + Search + " LIKE '%" + txt_search_keywords.getText() + "%'" + filter_status + filter_send_out
                        + "GROUP BY `tb_spk`.`kode_spk` ORDER BY `tanggal_spk` DESC";
            }
            Connection con = Utility.db.getConnection();
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            Object[] row = new Object[17];
            while (rs.next()) {
                berat_proses = 0;
                berat_spk = 0;
                String spk = rs.getString("kode_spk");
                row[0] = spk;
                row[1] = rs.getString("no_revisi");
                row[2] = rs.getString("nama");
                row[3] = rs.getDate("tanggal_spk");
                row[4] = rs.getDate("tanggal_keluar");
                row[5] = rs.getDate("tanggal_awb");
                row[6] = rs.getDate("tanggal_fix");
                row[7] = rs.getInt("jumlah_kemasan");
                berat_spk = rs.getInt("berat");
                row[8] = rs.getInt("berat");
                row[9] = rs.getString("detail");
                row[10] = rs.getString("fix");
                row[11] = rs.getString("status");
                String query1 = "SELECT SUM(`tb_box_bahan_jadi`.`berat`) AS 'berat' "
                        + "FROM `tb_box_bahan_jadi` LEFT JOIN `tb_box_packing` ON `tb_box_bahan_jadi`.`no_box` = `tb_box_packing`.`no_box`"
                        + "LEFT JOIN `tb_spk_detail` ON `tb_spk_detail`.`no` = `tb_box_packing`.`no_grade_spk`"
                        + "WHERE `tb_spk_detail`.`kode_spk`='" + spk + "' AND `tb_box_packing`.`status` <> 'PENDING'";
                ResultSet result = Utility.db.getStatement().executeQuery(query1);
                if (result.next()) {
                    berat_proses = result.getFloat("berat");
                    row[12] = result.getFloat("berat");
                }

                if (berat_spk > 0) {
                    row[13] = Math.round((berat_proses / berat_spk) * 1000.f) / 10.f;
                } else {
                    row[13] = 0;
                }

                String query2 = "SELECT COUNT(`no_barcode`) AS 'jumlah', `final_packing` \n"
                        + "FROM `tb_barcode_pengiriman` LEFT JOIN `tb_spk_detail` ON `tb_barcode_pengiriman`.`no_grade_spk` = `tb_spk_detail`.`no`\n"
                        + "WHERE `tb_spk_detail`.`kode_spk` = '" + spk + "' GROUP BY `final_packing`";
                ResultSet result2 = Utility.db.getStatement().executeQuery(query2);
                float jumlah_box_selesai_packing = 0, jumlah_box_keseluruhan = 0;
                while (result2.next()) {
                    if (result2.getString("final_packing").equals("SELESAI")) {
                        jumlah_box_selesai_packing = result2.getFloat("jumlah");
                    }
                    jumlah_box_keseluruhan = jumlah_box_keseluruhan + result2.getFloat("jumlah");
                }
                if (jumlah_box_keseluruhan > 0) {
                    row[14] = Math.round((jumlah_box_selesai_packing / jumlah_box_keseluruhan) * 1000.f) / 10.f;
                } else {
                    row[14] = 0;
                }
                row[15] = rs.getString("approved");
                row[16] = rs.getInt("harga");
                model.addRow(row);
            }
            label_total_data_spk.setText(Integer.toString(tabel_data_spk.getRowCount()));
            label_total_harga_spk.setText(decimalFormat.format(total_harga));
            label_total_gram_spk.setText(decimalFormat.format(total_gram));
            ColumnsAutoSizer.sizeColumnsToFit(tabel_data_spk);
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_SPK.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_detail() {
        try {
            float total_harga = 0, total_gram = 0;
            DefaultTableModel model = (DefaultTableModel) tabel_detail_spk.getModel();
            model.setRowCount(0);
            sql2 = "SELECT `tb_spk_detail`.`no`, `grade_waleta`, `grade_buyer`, `berat_kemasan`, `jumlah_kemasan`, `berat` AS 'berat_spk', `harga_cny`, `keterangan`, `prod_date`, `kode_kh`, progress.`berat_progress` \n"
                    + "FROM `tb_spk_detail` "
                    + "LEFT JOIN (SELECT `tb_box_packing`.`no_grade_spk`, SUM(`berat`) AS 'berat_progress' FROM `tb_box_bahan_jadi` \n"
                    + "LEFT JOIN `tb_box_packing` ON `tb_box_bahan_jadi`.`no_box` = `tb_box_packing`.`no_box`\n"
                    + "WHERE `tb_box_packing`.`status` <> 'PENDING' GROUP BY `tb_box_packing`.`no_grade_spk`) progress ON `tb_spk_detail`.`no` = progress.`no_grade_spk` "
                    + "WHERE `kode_spk` = '" + label_no_spk.getText() + "' ";
            Connection con = Utility.db.getConnection();
            pst = con.prepareStatement(sql2);
            rs = pst.executeQuery();
            int nomor = 0;
            while (rs.next()) {
                Object[] row = new Object[20];
                nomor++;
                row[0] = rs.getInt("no");
                row[1] = nomor;
                row[2] = rs.getString("grade_waleta");
                row[3] = rs.getString("grade_buyer");
                row[4] = rs.getInt("berat_kemasan");
                row[5] = rs.getInt("jumlah_kemasan");
                row[6] = rs.getInt("berat_spk");
                row[7] = rs.getDate("prod_date");
                if (rs.getString("kode_kh") != null) {
                    row[8] = rs.getString("kode_kh").split("-")[0];
                    row[9] = rs.getString("kode_kh");
                }
                row[10] = rs.getInt("harga_cny");
                row[11] = (float) Math.round(rs.getFloat("berat_spk") * (rs.getFloat("harga_cny") / 1000));
                row[12] = rs.getString("keterangan");
                row[13] = rs.getInt("berat_progress");
                if (rs.getInt("berat_spk") > 0) {
                    row[14] = Math.round((rs.getFloat("berat_progress") / rs.getFloat("berat_spk")) * 1000.f) / 10.f;
                } else {
                    row[14] = 0;
                }
                model.addRow(row);
                total_harga = total_harga + (rs.getFloat("berat_spk") * (rs.getFloat("harga_cny") / 1000));
                total_gram = total_gram + rs.getFloat("berat_spk");
            }
            label_total_grade_spk.setText(Integer.toString(tabel_detail_spk.getRowCount()));
            label_total_gram_grade.setText(decimalFormat.format(total_gram));
            label_total_harga_grade.setText(decimalFormat.format(total_harga));
            ColumnsAutoSizer.sizeColumnsToFit(tabel_detail_spk);
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_SPK.class.getName()).log(Level.SEVERE, null, ex);
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
        ComboBox_Search = new javax.swing.JComboBox<>();
        txt_search_keywords = new javax.swing.JTextField();
        button_search = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        Date_Search1 = new com.toedter.calendar.JDateChooser();
        Date_Search2 = new com.toedter.calendar.JDateChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_data_spk = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        label_total_gram_spk = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        label_total_data_spk = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        label_total_harga_spk = new javax.swing.JLabel();
        button_export_dataSPK = new javax.swing.JButton();
        button_print = new javax.swing.JButton();
        button_edit_spk = new javax.swing.JButton();
        button_fix = new javax.swing.JButton();
        button_spk_baru = new javax.swing.JButton();
        button_delete_spk = new javax.swing.JButton();
        button_send_out = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        ComboBox_Search_status = new javax.swing.JComboBox<>();
        button_spk_baru_se = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        label_total_grade_spk = new javax.swing.JLabel();
        label_no_spk = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        label_total_harga_grade = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabel_detail_spk = new javax.swing.JTable();
        jLabel12 = new javax.swing.JLabel();
        label_total_gram_grade = new javax.swing.JLabel();
        button_export_dataBoxPengiriman = new javax.swing.JButton();
        button_edit_tgl_produksi = new javax.swing.JButton();
        button_edit_kh = new javax.swing.JButton();
        button_spk_baru_lokal = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        ComboBox_send_out = new javax.swing.JComboBox<>();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Surat Perintah Kerja", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(1366, 700));

        ComboBox_Search.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_Search.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "NO SPK", "NAMA BUYER" }));

        txt_search_keywords.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_keywords.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_keywordsKeyPressed(evt);
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

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Tgl Keluar :");

        Date_Search1.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search1.setDateFormatString("dd MMMM yyyy");
        Date_Search1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_Search2.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search2.setDateFormatString("dd MMMM yyyy");
        Date_Search2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        tabel_data_spk.setAutoCreateRowSorter(true);
        tabel_data_spk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_data_spk.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No SPK", "Rev", "Buyer", "Tgl SPK", "Tgl Keluar", "Tgl AWB", "Tgl FIX", "Tot Kemasan", "Berat", "Detail", "FIX", "SEND", "Packing", "%", "% Final", "Approved by", "Harga"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel_data_spk.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tabel_data_spk);
        if (tabel_data_spk.getColumnModel().getColumnCount() > 0) {
            tabel_data_spk.getColumnModel().getColumn(2).setMaxWidth(180);
            tabel_data_spk.getColumnModel().getColumn(9).setMaxWidth(100);
        }

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel4.setText("Tabel Data SPK Waleta");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Total Gram :");

        label_total_gram_spk.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_spk.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_gram_spk.setText("0");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText("Total SPK :");

        label_total_data_spk.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_spk.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_data_spk.setText("0");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("Total Harga :");

        label_total_harga_spk.setBackground(new java.awt.Color(255, 255, 255));
        label_total_harga_spk.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_harga_spk.setText("0");

        button_export_dataSPK.setBackground(new java.awt.Color(255, 255, 255));
        button_export_dataSPK.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_dataSPK.setText("Export");
        button_export_dataSPK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_dataSPKActionPerformed(evt);
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

        button_edit_spk.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_spk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_edit_spk.setText("Edit");
        button_edit_spk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_spkActionPerformed(evt);
            }
        });

        button_fix.setBackground(new java.awt.Color(255, 255, 255));
        button_fix.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_fix.setText("Fix");
        button_fix.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_fixActionPerformed(evt);
            }
        });

        button_spk_baru.setBackground(new java.awt.Color(255, 255, 255));
        button_spk_baru.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_spk_baru.setText("SPK EXPOR");
        button_spk_baru.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_spk_baruActionPerformed(evt);
            }
        });

        button_delete_spk.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_spk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete_spk.setText("Delete");
        button_delete_spk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_spkActionPerformed(evt);
            }
        });

        button_send_out.setBackground(new java.awt.Color(255, 255, 255));
        button_send_out.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_send_out.setText("SEND OUT");
        button_send_out.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_send_outActionPerformed(evt);
            }
        });

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Status :");

        ComboBox_Search_status.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_Search_status.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "FIXED", "Not Fix" }));

        button_spk_baru_se.setBackground(new java.awt.Color(255, 255, 255));
        button_spk_baru_se.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_spk_baru_se.setText("SPK SE");
        button_spk_baru_se.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_spk_baru_seActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel11.setText("Total Grade :");

        label_total_grade_spk.setBackground(new java.awt.Color(255, 255, 255));
        label_total_grade_spk.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_grade_spk.setText("0");

        label_no_spk.setBackground(new java.awt.Color(255, 255, 255));
        label_no_spk.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_no_spk.setText("No SPK");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel10.setText("Total Gram :");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel13.setText("Detail Grade SPK");

        label_total_harga_grade.setBackground(new java.awt.Color(255, 255, 255));
        label_total_harga_grade.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_harga_grade.setText("0");

        tabel_detail_spk.setAutoCreateRowSorter(true);
        tabel_detail_spk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_detail_spk.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "kode", "No", "019 Grade", "Grade Buyer", "Gr / Pack", "Tot Pack", "Gram", "Tgl Produksi", "RSB", "Kode KH", "CNY / Kg", "CNY", "Keterangan", "Progress", "%"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class
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
        tabel_detail_spk.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tabel_detail_spk);

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel12.setText("Total Harga :");

        label_total_gram_grade.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_grade.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_gram_grade.setText("0");

        button_export_dataBoxPengiriman.setBackground(new java.awt.Color(255, 255, 255));
        button_export_dataBoxPengiriman.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_dataBoxPengiriman.setText("Export");
        button_export_dataBoxPengiriman.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_dataBoxPengirimanActionPerformed(evt);
            }
        });

        button_edit_tgl_produksi.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_tgl_produksi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_edit_tgl_produksi.setText("Edit Tgl Produksi");
        button_edit_tgl_produksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_tgl_produksiActionPerformed(evt);
            }
        });

        button_edit_kh.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_kh.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_edit_kh.setText("Edit KH");
        button_edit_kh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_khActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_no_spk)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_edit_kh)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_edit_tgl_produksi)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export_dataBoxPengiriman))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_grade_spk)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_grade)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_harga_grade)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_no_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_dataBoxPengiriman, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_edit_tgl_produksi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_edit_kh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel12)
                        .addComponent(label_total_harga_grade))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel11)
                        .addComponent(label_total_grade_spk)
                        .addComponent(jLabel10)
                        .addComponent(label_total_gram_grade)))
                .addContainerGap())
        );

        button_spk_baru_lokal.setBackground(new java.awt.Color(255, 255, 255));
        button_spk_baru_lokal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_spk_baru_lokal.setText("SPK LOKAL");
        button_spk_baru_lokal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_spk_baru_lokalActionPerformed(evt);
            }
        });

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("SEND :");

        ComboBox_send_out.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_send_out.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "SEND OUT", "PROSES" }));
        ComboBox_send_out.setSelectedIndex(2);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_spk_baru)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_spk_baru_se)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_spk_baru_lokal)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_dataSPK)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_delete_spk)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_edit_spk)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_fix)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_send_out)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_print))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data_spk)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_spk)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_harga_spk))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(ComboBox_Search, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_keywords, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_Search1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_Search2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_Search_status, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_send_out, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(Date_Search2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Search1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_keywords, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_Search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_Search_status, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_send_out, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_dataSPK, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_print, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_edit_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_fix, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_spk_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_send_out, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_spk_baru_se, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_spk_baru_lokal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(label_total_data_spk)
                    .addComponent(jLabel9)
                    .addComponent(label_total_harga_spk)
                    .addComponent(jLabel7)
                    .addComponent(label_total_gram_spk))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1369, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txt_search_keywordsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_keywordsKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_spk();
        }
    }//GEN-LAST:event_txt_search_keywordsKeyPressed

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        refreshTable_spk();
    }//GEN-LAST:event_button_searchActionPerformed

    private void button_export_dataSPKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_dataSPKActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_data_spk.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_export_dataSPKActionPerformed

    private void button_export_dataBoxPengirimanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_dataBoxPengirimanActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_detail_spk.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_export_dataBoxPengirimanActionPerformed

    private void button_printActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_printActionPerformed
        try {
            int j = tabel_data_spk.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Please select data from the table first", "warning!", 1);
            } else {
                JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Surat_Perintah_Kerja.jrxml");
                Map<String, Object> map = new HashMap<>();
                map.put("KODE_SPK", tabel_data_spk.getValueAt(j, 0).toString());

                JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
                JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
            }
        } catch (JRException ex) {
            Logger.getLogger(JPanel_SPK.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_printActionPerformed

    private void button_edit_spkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_spkActionPerformed
        // TODO add your handling code here:
        int i = tabel_data_spk.getSelectedRow();
        if (i == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih data yang akan di edit !");
        } else {
            String kode_spk = tabel_data_spk.getValueAt(i, 0).toString();
            JDialog_EDIT_SPK dialog = new JDialog_EDIT_SPK(new javax.swing.JFrame(), true, kode_spk);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            dialog.setEnabled(true);
            dialog.setResizable(false);
            refreshTable_spk();
        }
    }//GEN-LAST:event_button_edit_spkActionPerformed

    private void button_fixActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_fixActionPerformed
        // TODO add your handling code here:
        int i = tabel_data_spk.getSelectedRow();
        if (i == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih salah satu kode SPK pada tabel !");
        } else {
            try {
                String kode_spk = tabel_data_spk.getValueAt(i, 0).toString();
                if (button_fix.getText().equals("Fix")) {
                    String Query = "UPDATE `tb_spk` SET `fix`='FIXED', `tanggal_fix` = '" + dateFormat.format(date) + "' WHERE `kode_spk` = '" + kode_spk + "'";
                    if ((Utility.db.getStatement().executeUpdate(Query)) > 0) {
                        JOptionPane.showMessageDialog(this, kode_spk + " Sudah FIXED !");
                        refreshTable_spk();
                    }
                } else if (button_fix.getText().equals("Not Fix")) {
                    if (MainForm.Login_kodeBagian != 248) {
                        JOptionPane.showMessageDialog(this, "Maaf hanya Kadep EKSPOR yang dapat mengembalikan SPK ke Status Not Fix!");
                    } else if (tabel_data_spk.getValueAt(i, 11).toString().equals("PROSES")) {
                        JOptionPane.showMessageDialog(this, "Maaf hanya bisa mengembalikan status Fix jika masih dalam PROSES!");
                    } else {
                        String Query = "UPDATE `tb_spk` SET `fix`='Not Fix', `tanggal_fix` = NULL WHERE `kode_spk` = '" + kode_spk + "'";
                        if ((Utility.db.getStatement().executeUpdate(Query)) > 0) {
                            JOptionPane.showMessageDialog(this, kode_spk + " kembali Not Fix !");
                            refreshTable_spk();
                        }
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
                Logger.getLogger(JPanel_SPK.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_fixActionPerformed

    private void button_spk_baruActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_spk_baruActionPerformed
        // TODO add your handling code here:
        JDialog_new_SPK_Expor dialog = new JDialog_new_SPK_Expor(new javax.swing.JFrame(), true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        dialog.setResizable(false);
        refreshTable_spk();
    }//GEN-LAST:event_button_spk_baruActionPerformed

    private void button_delete_spkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_spkActionPerformed
        // TODO add your handling code here:
        try {
            int j = tabel_data_spk.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang mau di hapus !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    String Query = "DELETE FROM `tb_spk_detail` WHERE `kode_spk` = '" + tabel_data_spk.getValueAt(j, 0).toString() + "'";
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(Query);
                    Query = "DELETE FROM `tb_spk` WHERE `kode_spk` = '" + tabel_data_spk.getValueAt(j, 0).toString() + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(Query)) > 0) {
                        JOptionPane.showMessageDialog(this, "Data terhapus !");
                        refreshTable_spk();
                    }
                }
            }
        } catch (HeadlessException | SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JDialog_pengiriman.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_delete_spkActionPerformed

    private void button_send_outActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_send_outActionPerformed
        // TODO add your handling code here:
        int i = tabel_data_spk.getSelectedRow();
        if (i == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih SPK yang Sudah Fix !");
        } else {
            try {
                boolean check = true;
                String kode_spk = tabel_data_spk.getValueAt(i, 0).toString();
                sql = "SELECT `invoice_no`, `status_pengiriman` FROM `tb_pengiriman` WHERE `kode_spk` = '" + kode_spk + "'";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    if (rs.getString("status_pengiriman").equals("DELIVERED")) {
                        check = false;
                        JOptionPane.showMessageDialog(this, "Maaf status invoice " + rs.getString("invoice_no") + " dari " + kode_spk + " sudah DELIVERED!\nTidak diperbolehkan edit status SPK!");
                    }
                }
                if (check) {
                    if (tabel_data_spk.getValueAt(i, 11).toString().equals("SEND OUT")) {
                        if (MainForm.Login_kodeBagian != 248) {
                            JOptionPane.showMessageDialog(this, "Maaf hanya Kadep EKSPOR yang dapat mengembalikan SPK ke Status PROSES!");
                        } else {
                            String Query = "UPDATE `tb_spk` SET `status`='PROSES' WHERE `kode_spk` = '" + kode_spk + "'";
                            Utility.db.getConnection().createStatement();
                            if ((Utility.db.getStatement().executeUpdate(Query)) > 0) {
                                JOptionPane.showMessageDialog(this, kode_spk + " kembali ke status PROSES !");
                                refreshTable_spk();
                            }
                        }
                    } else if (tabel_data_spk.getValueAt(i, 11).toString().equals("PROSES")) {
                        String Query = "UPDATE `tb_spk` SET `status`='SEND OUT' WHERE `kode_spk` = '" + kode_spk + "'";
                        Utility.db.getConnection().createStatement();
                        if ((Utility.db.getStatement().executeUpdate(Query)) > 0) {
                            JOptionPane.showMessageDialog(this, kode_spk + " status menjadi SEND OUT !");
                            refreshTable_spk();
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, kode_spk + " status tidak dikenali, harap hubungi bagian IT !");
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
                Logger.getLogger(JPanel_SPK.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_send_outActionPerformed

    private void button_spk_baru_seActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_spk_baru_seActionPerformed
        // TODO add your handling code here:
        JDialog_new_SPK_SE_Lokal dialog = new JDialog_new_SPK_SE_Lokal(new javax.swing.JFrame(), true, "SE");
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        dialog.setResizable(false);
        refreshTable_spk();
    }//GEN-LAST:event_button_spk_baru_seActionPerformed

    private void button_spk_baru_lokalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_spk_baru_lokalActionPerformed
        // TODO add your handling code here:
        JDialog_new_SPK_SE_Lokal dialog = new JDialog_new_SPK_SE_Lokal(new javax.swing.JFrame(), true, "LOKAL");
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        dialog.setResizable(false);
        refreshTable_spk();
    }//GEN-LAST:event_button_spk_baru_lokalActionPerformed

    private void button_edit_tgl_produksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_tgl_produksiActionPerformed
        // TODO add your handling code here:
        int i = tabel_detail_spk.getSelectedRow();
        if (i > -1) {
            String kode = tabel_detail_spk.getValueAt(i, 0).toString();
            JDialog_EDIT_SPK_RSB dialog = new JDialog_EDIT_SPK_RSB(new javax.swing.JFrame(), true, kode, "tgl_produksi");
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            dialog.setEnabled(true);
            dialog.setResizable(false);
            refreshTable_detail();
        } else {
            JOptionPane.showMessageDialog(this, "Maaf anda belum memilih grade yg akan di edit");
        }
    }//GEN-LAST:event_button_edit_tgl_produksiActionPerformed

    private void button_edit_khActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_khActionPerformed
        // TODO add your handling code here:
        int i = tabel_detail_spk.getSelectedRow();
        if (i > -1) {
            String kode = tabel_detail_spk.getValueAt(i, 0).toString();
            JDialog_EDIT_SPK_RSB dialog = new JDialog_EDIT_SPK_RSB(new javax.swing.JFrame(), true, kode, "kh");
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            dialog.setEnabled(true);
            dialog.setResizable(false);
            refreshTable_detail();
        } else {
            JOptionPane.showMessageDialog(this, "Maaf anda belum memilih grade yg akan di edit");
        }
    }//GEN-LAST:event_button_edit_khActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_Search;
    private javax.swing.JComboBox<String> ComboBox_Search_status;
    private javax.swing.JComboBox<String> ComboBox_send_out;
    private com.toedter.calendar.JDateChooser Date_Search1;
    private com.toedter.calendar.JDateChooser Date_Search2;
    public javax.swing.JButton button_delete_spk;
    private javax.swing.JButton button_edit_kh;
    public javax.swing.JButton button_edit_spk;
    private javax.swing.JButton button_edit_tgl_produksi;
    private javax.swing.JButton button_export_dataBoxPengiriman;
    private javax.swing.JButton button_export_dataSPK;
    public javax.swing.JButton button_fix;
    private javax.swing.JButton button_print;
    private javax.swing.JButton button_search;
    public javax.swing.JButton button_send_out;
    public javax.swing.JButton button_spk_baru;
    public javax.swing.JButton button_spk_baru_lokal;
    public javax.swing.JButton button_spk_baru_se;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
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
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel label_no_spk;
    private javax.swing.JLabel label_total_data_spk;
    private javax.swing.JLabel label_total_grade_spk;
    private javax.swing.JLabel label_total_gram_grade;
    private javax.swing.JLabel label_total_gram_spk;
    private javax.swing.JLabel label_total_harga_grade;
    private javax.swing.JLabel label_total_harga_spk;
    public static javax.swing.JTable tabel_data_spk;
    private javax.swing.JTable tabel_detail_spk;
    private javax.swing.JTextField txt_search_keywords;
    // End of variables declaration//GEN-END:variables
}
