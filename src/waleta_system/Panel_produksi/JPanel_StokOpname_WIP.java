package waleta_system.Panel_produksi;

import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import waleta_system.Class.ExportToExcel;

public class JPanel_StokOpname_WIP extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_StokOpname_WIP() {
        initComponents();
    }

    public void init() {
        try {
            refreshTable_DataSO();
            tabel_stockOpname.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && tabel_stockOpname.getSelectedRow() != -1) {
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                int x = tabel_stockOpname.getSelectedRow();
                                if (x > -1) {
                                    label_tanggal_SO.setText(tabel_stockOpname.getValueAt(x, 0).toString());
                                    label_tanggal_SO1.setText(tabel_stockOpname.getValueAt(x, 0).toString());
                                    refreshTable_detailLP_WIP(tabel_stockOpname.getValueAt(x, 0).toString());
                                    refreshTable_detailLP_SCAN(tabel_stockOpname.getValueAt(x, 0).toString());
                                }
                            }
                        };
                        thread.start();

                    }
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(JPanel_StokOpname_WIP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_DataSO() {
        try {
            DefaultTableModel model = (DefaultTableModel) tabel_stockOpname.getModel();
            model.setRowCount(0);
            Object[] row = new Object[5];
            sql = "SELECT `tgl_stok_opname_wip`, `status`"
                    + "FROM `tb_stokopname_wip` "
                    + "WHERE 1";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                row[0] = rs.getDate("tgl_stok_opname_wip");
                row[1] = rs.getBoolean("status");
                model.addRow(row);
            }
//            ColumnsAutoSizer.sizeColumnsToFit(tabel_stockOpname);
            label_total_data_SO.setText(Integer.toString(tabel_stockOpname.getRowCount()));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_StokOpname_WIP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_detailLP_WIP(String tgl_SO) {
        try {
            float total_keping = 0, total_gram = 0;
            DefaultTableModel model = (DefaultTableModel) tabel_data_LP_WIP.getModel();
            model.setRowCount(0);
            String query = "SELECT `tb_laporan_produksi`.`no_laporan_produksi`, `kode_grade`, `ruangan`, `jumlah_keping`, `berat_basah`, "
                    + "`tanggal_lp`, `tanggal_rendam`, `tgl_masuk_cuci`, `tgl_mulai_cabut`, `tgl_setor_cabut`, `tgl_mulai_cetak`, `tgl_selesai_cetak`, `tgl_masuk_f2`, `tgl_setor_f2`, `tgl_masuk` AS 'tgl_masuk_qc', `tgl_selesai` AS 'tgl_selesai_qc', `tanggal_grading`, `tb_tutupan_grading`.`tgl_statusBox`,"
                    + "DATA_SCAN.`no_laporan_produksi` AS 'lp_scan' \n"
                    + "FROM `tb_laporan_produksi` \n"
                    + "LEFT JOIN `tb_rendam` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_rendam`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_cuci` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cuci`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_cabut` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cabut`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_cetak` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cetak`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_finishing_2` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_finishing_2`.`no_laporan_produksi`"
                    + "LEFT JOIN `tb_lab_laporan_produksi` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_lab_laporan_produksi`.`no_laporan_produksi`"
                    + "LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_bahan_jadi_masuk`.`kode_asal`\n"
                    + "LEFT JOIN `tb_tutupan_grading` ON `tb_bahan_jadi_masuk`.`kode_tutupan` = `tb_tutupan_grading`.`kode_tutupan`\n"
                    + "LEFT JOIN (SELECT * FROM `tb_stokopname_wip_scan` WHERE `tgl_stok_opname_wip` = '" + tgl_SO + "') DATA_SCAN ON `tb_laporan_produksi`.`no_laporan_produksi` = DATA_SCAN.`no_laporan_produksi`\n"
                    + "WHERE `tb_laporan_produksi`.`no_laporan_produksi` LIKE '%" + txt_search_LP_WIP.getText() + "%' "
                    + "AND YEAR(`tanggal_lp`)>=2018 AND `tanggal_lp` <= '" + tgl_SO + "' AND `berat_basah` > 0 \n"
                    + "AND (`tgl_statusBox` > '" + tgl_SO + "' OR `tgl_statusBox` IS NULL)"
                    + " GROUP BY `tb_laporan_produksi`.`no_laporan_produksi`";
            ResultSet result = Utility.db.getStatement().executeQuery(query);
            Object[] row = new Object[10];
            while (result.next()) {
                row[0] = result.getString("no_laporan_produksi");
                row[1] = result.getString("ruangan");
                row[2] = result.getString("kode_grade");
                row[3] = result.getFloat("jumlah_keping");
                row[4] = result.getFloat("berat_basah");
                String posisi = "";
//                if (result.getDate("tanggal_rendam") == null) {
//                    posisi = "RENDAM";
//                } else if (result.getDate("tgl_masuk_cuci") == null) {
//                    posisi = "CUCI";
//                } else if (result.getDate("tgl_mulai_cabut") == null) {
//                    posisi = "SELESAI CUCI";
//                } else if (result.getDate("tgl_setor_cabut") == null) {
//                    posisi = "CABUT";
//                } else if (result.getDate("tgl_mulai_cetak") == null) {
//                    posisi = "SELESAI CABUT";
//                } else if (result.getDate("tgl_selesai_cetak") == null) {
//                    posisi = "CETAK";
//                } else if (result.getDate("tgl_masuk_f2") == null) {
//                    posisi = "SELESAI CETAK";
//                } else if (result.getDate("tgl_setor_f2") == null) {
//                    posisi = "FINISHING 2";
//                } else if (result.getDate("tgl_masuk_qc") == null) {
//                    posisi = "SELESAI FINISHING";
//                } else if (result.getDate("tgl_selesai_qc") == null) {
//                    posisi = "QC NITRIT / TREATMENT";
//                } else if (result.getDate("tanggal_grading") == null) {
//                    posisi = "SELESAI QC";
//                } else if (result.getDate("tgl_statusBox") == null) {
//                    posisi = "GRADING GBJ";
//                }
                if (result.getDate("tgl_statusBox") != null) {
                    posisi = "PRODUK JADI";
                } else if (result.getDate("tanggal_grading") != null) {
                    posisi = "GRADING GBJ";
                } else if (result.getDate("tgl_selesai_qc") != null) {
                    posisi = "SELESAI QC";
                } else if (result.getDate("tgl_masuk_qc") != null) {
                    posisi = "QC NITRIT / TREATMENT";
                } else if (result.getDate("tgl_setor_f2") != null) {
                    posisi = "SELESAI FINISHING";
                } else if (result.getDate("tgl_masuk_f2") != null) {
                    posisi = "FINISHING 2";
                } else if (result.getDate("tgl_selesai_cetak") != null) {
                    posisi = "SELESAI CETAK";
                } else if (result.getDate("tgl_mulai_cetak") != null) {
                    posisi = "CETAK";
                } else if (result.getDate("tgl_setor_cabut") != null) {
                    posisi = "SELESAI CABUT";
                } else if (result.getDate("tgl_mulai_cabut") != null) {
                    posisi = "CABUT";
                } else if (result.getDate("tgl_masuk_cuci") != null) {
                    posisi = "SELESAI CUCI";
                } else if (result.getDate("tanggal_rendam") != null) {
                    posisi = "CUCI";
                } else if (result.getDate("tanggal_lp") != null) {
                    posisi = "RENDAM";
                } else {
                    posisi = "LP TIDAK TERDAFTAR";
                }
                row[5] = posisi;
                row[6] = result.getString("lp_scan");
                if (ComboBox_posisi_lp_wip.getSelectedItem().equals("All")) {
                    model.addRow(row);
                    total_keping = total_keping + result.getFloat("jumlah_keping");
                    total_gram = total_gram + result.getFloat("berat_basah");
                } else {
                    if (ComboBox_posisi_lp_wip.getSelectedItem().equals(posisi)) {
                        model.addRow(row);
                        total_keping = total_keping + result.getFloat("jumlah_keping");
                        total_gram = total_gram + result.getFloat("berat_basah");
                    }
                }
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_data_LP_WIP);
            label_total_keping_lp_wip.setText(decimalFormat.format(total_keping));
            label_total_gram_lp_wip.setText(decimalFormat.format(total_gram));
            label_total_lp_wip.setText(Integer.toString(tabel_data_LP_WIP.getRowCount()));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_StokOpname_WIP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_detailLP_SCAN(String tgl_SO) {
        try {
            float total_keping = 0, total_gram = 0;
            DefaultTableModel model = (DefaultTableModel) tabel_data_LP_SCAN.getModel();
            model.setRowCount(0);
            String filter_posisi = " AND `posisi` = '" + ComboBox_posisi_lp_scan.getSelectedItem().toString() + "' ";
            if (ComboBox_posisi_lp_scan.getSelectedItem().equals("All")) {
                filter_posisi = "";
            }
            String query = "SELECT `tb_stokopname_wip_scan`.`no_laporan_produksi` AS 'lp_scan', `posisi`, `kode_grade`, `ruangan`, `jumlah_keping`, `berat_basah`, LP_WIP.`no_laporan_produksi` "
                    + "FROM `tb_stokopname_wip_scan` \n"
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_stokopname_wip_scan`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN "
                    + "(SELECT `tb_laporan_produksi`.`no_laporan_produksi` FROM `tb_laporan_produksi` \n"
                    + "LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_bahan_jadi_masuk`.`kode_asal`\n"
                    + "LEFT JOIN `tb_tutupan_grading` ON `tb_bahan_jadi_masuk`.`kode_tutupan` = `tb_tutupan_grading`.`kode_tutupan`\n"
                    + "WHERE YEAR(`tanggal_lp`)>=2018 AND `tanggal_lp` <= '" + tgl_SO + "' AND `berat_basah` > 0 AND (`tgl_statusBox` > '" + tgl_SO + "' OR `tgl_statusBox` IS NULL)"
                    + ") LP_WIP ON `tb_stokopname_wip_scan`.`no_laporan_produksi` = LP_WIP.`no_laporan_produksi` "
                    + "WHERE `tb_stokopname_wip_scan`.`no_laporan_produksi` LIKE '%" + txt_search_noLP_Scan.getText() + "%' "
                    + "AND `tgl_stok_opname_wip` = '" + tgl_SO + "'" 
                    + filter_posisi;
            ResultSet result = Utility.db.getStatement().executeQuery(query);
            Object[] row = new Object[10];
            while (result.next()) {
                row[0] = result.getString("lp_scan");
                row[1] = result.getString("ruangan");
                row[2] = result.getString("kode_grade");
                row[3] = result.getFloat("jumlah_keping");
                row[4] = result.getFloat("berat_basah");
                row[5] = result.getString("posisi");
                row[6] = result.getString("no_laporan_produksi");
                model.addRow(row);
                total_keping = total_keping + result.getFloat("jumlah_keping");
                total_gram = total_gram + result.getFloat("berat_basah");
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_data_LP_SCAN);
            label_total_keping_lp_scan.setText(decimalFormat.format(total_keping));
            label_total_gram_lp_scan.setText(decimalFormat.format(total_gram));
            label_total_lp_scan.setText(Integer.toString(tabel_data_LP_SCAN.getRowCount()));
            
            DefaultTableModel model_rekap = (DefaultTableModel) tabel_data_LP_SCAN_rekap_posisi.getModel();
            model_rekap.setRowCount(0);
            String qry_rekap = "SELECT `posisi`, COUNT(`tb_stokopname_wip_scan`.`no_laporan_produksi`) AS 'jumlah_lp', SUM(`jumlah_keping`) AS 'tot_kpg', SUM(`berat_basah`) AS 'tot_gram' "
                    + "FROM `tb_stokopname_wip_scan` \n"
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_stokopname_wip_scan`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN "
                    + "(SELECT `tb_laporan_produksi`.`no_laporan_produksi` FROM `tb_laporan_produksi` \n"
                    + "LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_bahan_jadi_masuk`.`kode_asal`\n"
                    + "LEFT JOIN `tb_tutupan_grading` ON `tb_bahan_jadi_masuk`.`kode_tutupan` = `tb_tutupan_grading`.`kode_tutupan`\n"
                    + "WHERE YEAR(`tanggal_lp`)>=2018 AND `tanggal_lp` <= '" + tgl_SO + "' AND `berat_basah` > 0 AND (`tgl_statusBox` > '" + tgl_SO + "' OR `tgl_statusBox` IS NULL)"
                    + ") LP_WIP ON `tb_stokopname_wip_scan`.`no_laporan_produksi` = LP_WIP.`no_laporan_produksi` \n"
                    + "WHERE \n"
                    + "`tb_stokopname_wip_scan`.`no_laporan_produksi` LIKE '%" + txt_search_LP_WIP.getText() + "%' \n"
                    + "AND `tgl_stok_opname_wip` = '" + tgl_SO + "'\n"
                    + "AND LP_WIP.`no_laporan_produksi` IS NOT NULL \n" 
                    + filter_posisi
                    + "GROUP BY `posisi`";
            ResultSet rs_rekap = Utility.db.getStatement().executeQuery(qry_rekap);
            while (rs_rekap.next()) {
                model_rekap.addRow(new Object[]{rs_rekap.getString("posisi"), rs_rekap.getFloat("jumlah_lp"), rs_rekap.getDouble("tot_kpg"), rs_rekap.getDouble("tot_gram")});
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_data_LP_SCAN_rekap_posisi);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_StokOpname_WIP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        button_new_stokOpname = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        button_refresh_dataSO = new javax.swing.JButton();
        label_total_data_SO = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabel_stockOpname = new javax.swing.JTable();
        button_detele_SO = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        button_selesai = new javax.swing.JButton();
        Date_stokOpname = new com.toedter.calendar.JDateChooser();
        button_print = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel_LP_SCAN = new javax.swing.JPanel();
        label_total_lp_scan = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tabel_data_LP_SCAN = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        button_Export_tabelScanLP = new javax.swing.JButton();
        label_total_keping_lp_scan = new javax.swing.JLabel();
        label_total_gram_lp_scan = new javax.swing.JLabel();
        button_search_noLP_Scan = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        txt_search_noLP_Scan = new javax.swing.JTextField();
        label_tanggal_SO1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        ComboBox_posisi_lp_scan = new javax.swing.JComboBox<>();
        button_delete_noLP_Scan = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        tabel_data_LP_SCAN_rekap_posisi = new javax.swing.JTable();
        jPanel_LP_WIP = new javax.swing.JPanel();
        label_total_keping_lp_wip = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txt_search_LP_WIP = new javax.swing.JTextField();
        label_total_lp_wip = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabel_data_LP_WIP = new javax.swing.JTable();
        label_tanggal_SO = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        button_refresh_LP_WIP = new javax.swing.JButton();
        label_total_gram_lp_wip = new javax.swing.JLabel();
        button_Export_tabelLPWIP = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        ComboBox_posisi_lp_wip = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Stock Opname WIP", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        button_new_stokOpname.setBackground(new java.awt.Color(255, 255, 255));
        button_new_stokOpname.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_new_stokOpname.setText("New");
        button_new_stokOpname.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_new_stokOpnameActionPerformed(evt);
            }
        });

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel13.setText("Data Stock Opname");

        button_refresh_dataSO.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh_dataSO.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh_dataSO.setText("Refresh");
        button_refresh_dataSO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh_dataSOActionPerformed(evt);
            }
        });

        label_total_data_SO.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_SO.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_data_SO.setText("0");

        tabel_stockOpname.setAutoCreateRowSorter(true);
        tabel_stockOpname.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_stockOpname.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tgl Stok Opname", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel_stockOpname.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(tabel_stockOpname);

        button_detele_SO.setBackground(new java.awt.Color(255, 255, 255));
        button_detele_SO.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_detele_SO.setText("Delete");
        button_detele_SO.setEnabled(false);
        button_detele_SO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_detele_SOActionPerformed(evt);
            }
        });

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel14.setText("Total Data :");

        button_selesai.setBackground(new java.awt.Color(255, 255, 255));
        button_selesai.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_selesai.setText("Selesai");
        button_selesai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_selesaiActionPerformed(evt);
            }
        });

        Date_stokOpname.setBackground(new java.awt.Color(255, 255, 255));
        Date_stokOpname.setDateFormatString("dd MMM yyyy");

        button_print.setBackground(new java.awt.Color(255, 255, 255));
        button_print.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_print.setText("Print");
        button_print.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_printActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data_SO))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(Date_stokOpname, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_new_stokOpname)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_detele_SO))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_refresh_dataSO)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_selesai)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_print)))
                        .addGap(0, 23, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_selesai, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh_dataSO, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_print, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel14)
                    .addComponent(label_total_data_SO))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Date_stokOpname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_new_stokOpname, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_detele_SO, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N

        jPanel_LP_SCAN.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_LP_SCAN.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        label_total_lp_scan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_lp_scan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_lp_scan.setText("0");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel15.setText("Total Gram :");

        tabel_data_LP_SCAN.setAutoCreateRowSorter(true);
        tabel_data_LP_SCAN.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_data_LP_SCAN.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP", "Ruang", "Grade", "Kpg", "Gram", "Posisi", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class
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
        tabel_data_LP_SCAN.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(tabel_data_LP_SCAN);
        if (tabel_data_LP_SCAN.getColumnModel().getColumnCount() > 0) {
            tabel_data_LP_SCAN.getColumnModel().getColumn(1).setHeaderValue("Ruang");
            tabel_data_LP_SCAN.getColumnModel().getColumn(2).setHeaderValue("Grade");
            tabel_data_LP_SCAN.getColumnModel().getColumn(6).setHeaderValue("Status");
        }

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("No LP :");

        button_Export_tabelScanLP.setBackground(new java.awt.Color(255, 255, 255));
        button_Export_tabelScanLP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_Export_tabelScanLP.setText("Export to Excel");
        button_Export_tabelScanLP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_Export_tabelScanLPActionPerformed(evt);
            }
        });

        label_total_keping_lp_scan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_lp_scan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_keping_lp_scan.setText("0");

        label_total_gram_lp_scan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_lp_scan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_gram_lp_scan.setText("0");

        button_search_noLP_Scan.setBackground(new java.awt.Color(255, 255, 255));
        button_search_noLP_Scan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_noLP_Scan.setText("Refresh");
        button_search_noLP_Scan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_noLP_ScanActionPerformed(evt);
            }
        });

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel10.setText("Total LP :");

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel16.setText("Total Keping :");

        txt_search_noLP_Scan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_noLP_Scan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_noLP_ScanKeyPressed(evt);
            }
        });

        label_tanggal_SO1.setBackground(new java.awt.Color(255, 255, 255));
        label_tanggal_SO1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_tanggal_SO1.setForeground(new java.awt.Color(255, 0, 0));
        label_tanggal_SO1.setText("yyyy-mm-dd");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Posisi :");

        ComboBox_posisi_lp_scan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_posisi_lp_scan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "LP TIDAK TERDAFTAR", "RENDAM", "CUCI", "SELESAI CUCI", "CABUT", "SELESAI CABUT", "CETAK", "SELESAI CETAK", "FINISHING 2", "SELESAI FINISHING", "QC NITRIT / TREATMENT", "SELESAI QC", "GRADING GBJ", "PRODUK JADI" }));

        button_delete_noLP_Scan.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_noLP_Scan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete_noLP_Scan.setText("Delete");
        button_delete_noLP_Scan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_noLP_ScanActionPerformed(evt);
            }
        });

        tabel_data_LP_SCAN_rekap_posisi.setAutoCreateRowSorter(true);
        tabel_data_LP_SCAN_rekap_posisi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_data_LP_SCAN_rekap_posisi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Posisi", "LP", "Kpg", "Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Double.class, java.lang.Double.class
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
        tabel_data_LP_SCAN_rekap_posisi.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(tabel_data_LP_SCAN_rekap_posisi);

        javax.swing.GroupLayout jPanel_LP_SCANLayout = new javax.swing.GroupLayout(jPanel_LP_SCAN);
        jPanel_LP_SCAN.setLayout(jPanel_LP_SCANLayout);
        jPanel_LP_SCANLayout.setHorizontalGroup(
            jPanel_LP_SCANLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_LP_SCANLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_LP_SCANLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_LP_SCANLayout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_lp_scan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_keping_lp_scan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_lp_scan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_Export_tabelScanLP))
                    .addGroup(jPanel_LP_SCANLayout.createSequentialGroup()
                        .addComponent(label_tanggal_SO1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_noLP_Scan, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_posisi_lp_scan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search_noLP_Scan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_delete_noLP_Scan)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel_LP_SCANLayout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 647, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel_LP_SCANLayout.setVerticalGroup(
            jPanel_LP_SCANLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_LP_SCANLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_LP_SCANLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_search_noLP_Scan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_noLP_Scan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_tanggal_SO1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_posisi_lp_scan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete_noLP_Scan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_LP_SCANLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_Export_tabelScanLP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(label_total_lp_scan)
                    .addComponent(jLabel16)
                    .addComponent(label_total_keping_lp_scan)
                    .addComponent(jLabel15)
                    .addComponent(label_total_gram_lp_scan))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_LP_SCANLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 585, Short.MAX_VALUE)
                    .addComponent(jScrollPane5))
                .addContainerGap())
        );

        jTabbedPane1.addTab("DATA LP SCAN", jPanel_LP_SCAN);

        jPanel_LP_WIP.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_LP_WIP.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        label_total_keping_lp_wip.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_lp_wip.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_keping_lp_wip.setText("0");

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("No LP :");

        txt_search_LP_WIP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_LP_WIP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_LP_WIPKeyPressed(evt);
            }
        });

        label_total_lp_wip.setBackground(new java.awt.Color(255, 255, 255));
        label_total_lp_wip.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_lp_wip.setText("0");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText("Total LP :");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("Total Keping :");

        tabel_data_LP_WIP.setAutoCreateRowSorter(true);
        tabel_data_LP_WIP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_data_LP_WIP.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP", "Ruang", "Grade", "Kpg", "Gram", "Posisi", "Scan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class
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
        tabel_data_LP_WIP.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tabel_data_LP_WIP);

        label_tanggal_SO.setBackground(new java.awt.Color(255, 255, 255));
        label_tanggal_SO.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_tanggal_SO.setForeground(new java.awt.Color(255, 0, 0));
        label_tanggal_SO.setText("yyyy-mm-dd");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Total Gram :");

        button_refresh_LP_WIP.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh_LP_WIP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh_LP_WIP.setText("Refresh");
        button_refresh_LP_WIP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh_LP_WIPActionPerformed(evt);
            }
        });

        label_total_gram_lp_wip.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_lp_wip.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_gram_lp_wip.setText("0");

        button_Export_tabelLPWIP.setBackground(new java.awt.Color(255, 255, 255));
        button_Export_tabelLPWIP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_Export_tabelLPWIP.setText("Export to Excel");
        button_Export_tabelLPWIP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_Export_tabelLPWIPActionPerformed(evt);
            }
        });

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Posisi :");

        ComboBox_posisi_lp_wip.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_posisi_lp_wip.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "LP TIDAK TERDAFTAR", "RENDAM", "CUCI", "SELESAI CUCI", "CABUT", "SELESAI CABUT", "CETAK", "SELESAI CETAK", "FINISHING 2", "SELESAI FINISHING", "QC NITRIT / TREATMENT", "SELESAI QC", "GRADING GBJ", "PRODUK JADI" }));

        javax.swing.GroupLayout jPanel_LP_WIPLayout = new javax.swing.GroupLayout(jPanel_LP_WIP);
        jPanel_LP_WIP.setLayout(jPanel_LP_WIPLayout);
        jPanel_LP_WIPLayout.setHorizontalGroup(
            jPanel_LP_WIPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_LP_WIPLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_LP_WIPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel_LP_WIPLayout.createSequentialGroup()
                        .addComponent(label_tanggal_SO)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_LP_WIP, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_posisi_lp_wip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_refresh_LP_WIP)
                        .addGap(0, 431, Short.MAX_VALUE))
                    .addGroup(jPanel_LP_WIPLayout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_lp_wip)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_keping_lp_wip)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_lp_wip)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_Export_tabelLPWIP)))
                .addContainerGap())
        );
        jPanel_LP_WIPLayout.setVerticalGroup(
            jPanel_LP_WIPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_LP_WIPLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_LP_WIPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_tanggal_SO, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_LP_WIP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_posisi_lp_wip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh_LP_WIP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_LP_WIPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jLabel7)
                    .addComponent(label_total_keping_lp_wip)
                    .addComponent(label_total_gram_lp_wip)
                    .addComponent(button_Export_tabelLPWIP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_lp_wip))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 585, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("DATA LP WIP", jPanel_LP_WIP);

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jTextArea1.setRows(5);
        jTextArea1.setText("Notes :\nKalau tgl_lp tidak ada, posisi = \"LP TIDAK TERDAFTAR\";\nKalau tgl_rendam tidak ada, posisi = \"RENDAM\";\nKalau tgl_masuk_cuci tidak ada, posisi = \"CUCI\";\nKalau tgl_mulai_cabut tidak ada, posisi = \"SELESAI CUCI\";\nKalau tgl_setor_cabut tidak ada, posisi = \"CABUT\";\nKalau tgl_mulai_cetak tidak ada, posisi = \"SELESAI CABUT\";\nKalau tgl_selesai_cetak tidak ada, posisi = \"CETAK\";\nKalau tgl_masuk_f2 tidak ada, posisi = \"SELESAI CETAK\";\nKalau tgl_setor_f2 tidak ada, posisi = \"FINISHING 2\";\nKalau tgl_masuk_qc tidak ada, posisi = \"SELESAI FINISHING\";\nKalau tgl_selesai_qc tidak ada, posisi = \"QC NITRIT / TREATMENT\";\nKalau tgl_grading tidak ada, posisi = \"SELESAI QC\";\nKalau tgl_selesaiBox tidak ada, posisi = \"GRADING GBJ\";\nKalau tgl_selesaiBox ada, posisi = \"PRODUK JADI\";");
        jScrollPane1.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 345, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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

    private void button_refresh_LP_WIPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh_LP_WIPActionPerformed
        // TODO add your handling code here:
        Thread thread = new Thread() {
            @Override
            public void run() {
                refreshTable_detailLP_WIP(label_tanggal_SO.getText());
            }
        };
        thread.start();
    }//GEN-LAST:event_button_refresh_LP_WIPActionPerformed

    private void button_Export_tabelLPWIPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_Export_tabelLPWIPActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_data_LP_WIP.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_Export_tabelLPWIPActionPerformed

    private void txt_search_LP_WIPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_LP_WIPKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    refreshTable_detailLP_WIP(label_tanggal_SO.getText());
                }
            };
            thread.start();
        }
    }//GEN-LAST:event_txt_search_LP_WIPKeyPressed

    private void button_refresh_dataSOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh_dataSOActionPerformed
        // TODO add your handling code here:
        refreshTable_DataSO();
    }//GEN-LAST:event_button_refresh_dataSOActionPerformed

    private void button_detele_SOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_detele_SOActionPerformed
        // TODO add your handling code here:
        try {
            int j = tabel_stockOpname.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih tanggal stok opname yang ingin di hapus !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "hapus data stok opname akan menghapus semua data scan dari stok opname tsb, \nlanjutkan?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    String Query = "DELETE FROM `tb_stokopname_wip` WHERE `tgl_stok_opname_wip` = '" + tabel_stockOpname.getValueAt(j, 0).toString() + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(Query)) > 0) {
                        JOptionPane.showMessageDialog(this, "data DELETE Successfully");
                        refreshTable_DataSO();
                    } else {
                        JOptionPane.showMessageDialog(this, "data not DELETE");
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JPanel_StokOpname_WIP.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_detele_SOActionPerformed

    private void button_new_stokOpnameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_new_stokOpnameActionPerformed
        // TODO add your handling code here:
        try {
            sql = "SELECT `tgl_stok_opname_wip` FROM `tb_stokopname_wip` WHERE `status` = 0";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Maaf Stok Opname tanggal " + rs.getString("tgl_stok_opname_wip") + " belum selesai\ntidak bisa memulai stok opname baru!");
            } else {
                String Query = "INSERT INTO `tb_stokopname_wip`(`tgl_stok_opname_wip`, `status`) "
                        + "VALUES ('" + dateFormat.format(Date_stokOpname.getDate()) + "',0)";
                Utility.db.getConnection().createStatement();
                if ((Utility.db.getStatement().executeUpdate(Query)) > 0) {
                    JOptionPane.showMessageDialog(this, "Stok Opname tgl " + dateFormat.format(Date_stokOpname.getDate()) + " dimulai\nsilahkan mulai Scan semua LP WIP menggunakan aplikasi");
                    refreshTable_DataSO();
                } else {
                    JOptionPane.showMessageDialog(this, "input gagal!");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JPanel_StokOpname_WIP.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_new_stokOpnameActionPerformed

    private void button_Export_tabelScanLPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_Export_tabelScanLPActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_data_LP_SCAN.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_Export_tabelScanLPActionPerformed

    private void txt_search_noLP_ScanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_noLP_ScanKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    refreshTable_detailLP_SCAN(label_tanggal_SO1.getText());
                }
            };
            thread.start();
        }
    }//GEN-LAST:event_txt_search_noLP_ScanKeyPressed

    private void button_search_noLP_ScanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_noLP_ScanActionPerformed
        // TODO add your handling code here:
        Thread thread = new Thread() {
            @Override
            public void run() {
                refreshTable_detailLP_SCAN(label_tanggal_SO1.getText());
            }
        };
        thread.start();
    }//GEN-LAST:event_button_search_noLP_ScanActionPerformed

    private void button_selesaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_selesaiActionPerformed
        // TODO add your handling code here:
        try {
            int j = tabel_stockOpname.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih tanggal stok opname yang sudah selesai !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Stok Opname LP WIP sudah selesai??", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    String Query = "UPDATE `tb_stokopname_wip` SET `status`=1 WHERE `tgl_stok_opname_wip` = '" + tabel_stockOpname.getValueAt(j, 0).toString() + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(Query)) > 0) {
                        JOptionPane.showMessageDialog(this, "data SAVED");
                        refreshTable_DataSO();
                    } else {
                        JOptionPane.showMessageDialog(this, "FAILED !!");
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JPanel_StokOpname_WIP.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_selesaiActionPerformed

    private void button_delete_noLP_ScanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_noLP_ScanActionPerformed
        // TODO add your handling code here:
        try {
            int j = tabel_data_LP_SCAN.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data hasil scan yang ingin dihapus !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Hapus data??", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    String Query = "DELETE FROM `tb_stokopname_wip_scan` WHERE `tgl_stok_opname_wip` = '" + label_tanggal_SO1.getText() + "' AND `no_laporan_produksi` = '" + tabel_data_LP_SCAN.getValueAt(j, 0).toString() + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(Query)) > 0) {
                        JOptionPane.showMessageDialog(this, "data SAVED");
                        refreshTable_DataSO();
                    } else {
                        JOptionPane.showMessageDialog(this, "FAILED !!");
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JPanel_StokOpname_WIP.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_delete_noLP_ScanActionPerformed

    private void button_printActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_printActionPerformed
        // TODO add your handling code here:
        try {
            int j = tabel_stockOpname.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih tanggal SO di tabel!", "warning!", 1);
            } else {
                String query = "SELECT `posisi`, COUNT(`tb_stokopname_wip_scan`.`no_laporan_produksi`) AS 'jumlah_lp', SUM(`jumlah_keping`) AS 'tot_kpg', SUM(`berat_basah`) AS 'tot_gram' "
                    + "FROM `tb_stokopname_wip_scan` \n"
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_stokopname_wip_scan`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "WHERE `tgl_stok_opname_wip` = '" + tabel_stockOpname.getValueAt(j, 0).toString() + "'"
                    + "GROUP BY `posisi`";
                JRDesignQuery newQuery = new JRDesignQuery();
                newQuery.setText(query);
                JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Laporan_StokOpname_WIP.jrxml");
                JASP_DESIGN.setQuery(newQuery);

                JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, null, Utility.db.getConnection());
                JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
            }
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_StokOpname_WIP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_printActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_posisi_lp_scan;
    private javax.swing.JComboBox<String> ComboBox_posisi_lp_wip;
    private com.toedter.calendar.JDateChooser Date_stokOpname;
    private javax.swing.JButton button_Export_tabelLPWIP;
    private javax.swing.JButton button_Export_tabelScanLP;
    private javax.swing.JButton button_delete_noLP_Scan;
    private javax.swing.JButton button_detele_SO;
    private javax.swing.JButton button_new_stokOpname;
    private javax.swing.JButton button_print;
    private javax.swing.JButton button_refresh_LP_WIP;
    private javax.swing.JButton button_refresh_dataSO;
    private javax.swing.JButton button_search_noLP_Scan;
    private javax.swing.JButton button_selesai;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel_LP_SCAN;
    private javax.swing.JPanel jPanel_LP_WIP;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel label_tanggal_SO;
    private javax.swing.JLabel label_tanggal_SO1;
    private javax.swing.JLabel label_total_data_SO;
    private javax.swing.JLabel label_total_gram_lp_scan;
    private javax.swing.JLabel label_total_gram_lp_wip;
    private javax.swing.JLabel label_total_keping_lp_scan;
    private javax.swing.JLabel label_total_keping_lp_wip;
    private javax.swing.JLabel label_total_lp_scan;
    private javax.swing.JLabel label_total_lp_wip;
    private javax.swing.JTable tabel_data_LP_SCAN;
    private javax.swing.JTable tabel_data_LP_SCAN_rekap_posisi;
    private javax.swing.JTable tabel_data_LP_WIP;
    private javax.swing.JTable tabel_stockOpname;
    private javax.swing.JTextField txt_search_LP_WIP;
    private javax.swing.JTextField txt_search_noLP_Scan;
    // End of variables declaration//GEN-END:variables
}
