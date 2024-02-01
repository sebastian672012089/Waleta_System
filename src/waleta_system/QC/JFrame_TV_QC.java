package waleta_system.QC;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;

public class JFrame_TV_QC extends javax.swing.JFrame {

    String sql = null, sql_rekap = null;
    ResultSet rs;
    PreparedStatement pst;
    Date today = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    static Timer t;
    static int detik = 0, menit = 0, jam = 0;
    static int tab = 0;
    int[] R = new int[]{102, 153, 153, 204, 255, 255, 255, 255, 255, 204, 153, 102};
    int[] G = new int[]{255, 255, 255, 255, 255, 204, 153, 153, 153, 153, 204, 204};
    int[] B = new int[]{255, 204, 153, 153, 153, 153, 153, 204, 255, 255, 255, 255};
    int spk = 0, baris = -1, check = 0;

    public JFrame_TV_QC() {
        initComponents();
        init();
    }

    public void init() {
        try {
            detik = 0;
            menit = 0;
            jam = 0;
            refreshTabel_hold_jadi();
            refreshTabel_hold_baku();

            TimerTask timer;
            timer = new TimerTask() {
                @Override
                public void run() {
                    boolean changeTab = true;
//                    label_waktu.setText(jam + " : " + menit + " : " + detik);
                    if (detik == 0) {
                        while (changeTab) {
                            jTabbedPane1.setSelectedIndex(tab);
                            if (tab == 2 && label_kode.getText().equals("-")) {
                                tab = 3;
                            } else if (tab == 3 && label_kode1.getText().equals("-")) {
                                tab = 4;
                            } else if (tab == 4 && label_kode2.getText().equals("-")) {
                                tab = 5;
                            } else if (tab == 5 && label_kode3.getText().equals("-")) {
                                tab = 6;
                            } else if (tab == 6 && label_kode4.getText().equals("-")) {
                                tab = 0;
                            } else {
                                changeTab = false;
                            }
                        }

                        tab++;
                        if (tab >= jTabbedPane1.getTabCount()) {
                            tab = 0;
                        }
                    }

                    detik = detik + 1;
                    if (detik > 60) {
                        detik = 0;
                        menit++;
                        if (menit > 60) {
                            menit = 0;
                            jam++;

                        }
                    }
                }
            };
            t = new Timer();
            t.schedule(timer, 100, 1000);
        } catch (Exception ex) {
            Logger.getLogger(JFrame_TV_QC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTabel_hold_jadi() {
        try {
            DefaultTableModel model1 = (DefaultTableModel) Table_Data_Nitrit_jadi.getModel();
            model1.setRowCount(0);
            sql = "SELECT *, (`jumlah_hold` / `jumlah_lp`) * 100 AS 'persentase_hold' FROM "
                    + "(SELECT `tb_laporan_produksi`.`no_kartu_waleta`, COUNT(`tb_lab_laporan_produksi`.`no_laporan_produksi`) AS 'jumlah_lp', "
                    + "COUNT(IF((`nitrit_utuh`>21 AND `nitrit_utuh`>0) OR (`nitrit_flat`>21 AND `nitrit_flat`>0), 1, NULL)) AS 'jumlah_hold' "
                    + "FROM `tb_laporan_produksi` "
                    + "LEFT JOIN `tb_lab_laporan_produksi` ON `tb_lab_laporan_produksi`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi` "
                    + "WHERE `tgl_uji` IS NOT NULL AND `tgl_uji` BETWEEN ADDDATE(CURRENT_DATE, INTERVAL -7 DAY) AND CURRENT_DATE "
                    + "GROUP BY `tb_laporan_produksi`.`no_kartu_waleta` "
                    + "ORDER BY `tb_laporan_produksi`.`no_kartu_waleta`) data "
                    + "WHERE `jumlah_hold` > 0 "
                    + "ORDER BY (`jumlah_hold` / `jumlah_lp`) * 100 DESC ";
//            System.out.println(sql);
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[6];
            int putih = 0, oren = 0, merah = 0;
            while (rs.next()) {
                row[0] = rs.getString("no_kartu_waleta");
                row[1] = rs.getInt("jumlah_hold");
                row[2] = Math.round(rs.getFloat("persentase_hold") * 10.f) / 10.f;
                float persen = Math.round(rs.getFloat("persentase_hold") * 10.f) / 10.f;
                if (persen < 50) {
                    putih++;
                } else if (persen < 75) {
                    oren++;
                } else {
                    merah++;
                }
                model1.addRow(row);
            }

            Table_Data_Nitrit_jadi.setDefaultRenderer(Float.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (isSelected) {
                        comp.setBackground(Table_Data_Nitrit_jadi.getSelectionBackground());
                        comp.setForeground(Table_Data_Nitrit_jadi.getSelectionForeground());
                    } else {
                        if ((float) Table_Data_Nitrit_jadi.getValueAt(row, 2) < 50) {
                            comp.setBackground(Color.WHITE);
                            comp.setForeground(Color.BLACK);
                        } else if ((float) Table_Data_Nitrit_jadi.getValueAt(row, 2) < 75) {
                            comp.setBackground(Color.YELLOW);
                            comp.setForeground(Color.BLACK);
                        } else {
                            comp.setBackground(Color.RED);
                            comp.setForeground(Color.WHITE);
                        }
                    }
                    return comp;
                }
            });
            Table_Data_Nitrit_jadi.repaint();

            label_total_kartu_jadi_putih.setText(Integer.toString(putih));
            label_total_kartu_jadi_oren.setText(Integer.toString(oren));
            label_total_kartu_jadi_merah.setText(Integer.toString(merah));
            ColumnsAutoSizer.sizeColumnsToFit(Table_Data_Nitrit_jadi);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JFrame_TV_QC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTabel_hold_baku() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_Data_nitrit_baku.getModel();
            model.setRowCount(0);
            sql = "SELECT `tb_laporan_produksi`.`no_kartu_waleta`, `tb_bahan_baku_masuk`.`no_registrasi`, `tb_lab_bahan_baku`.`nitrit_bm_w3`, ADDDATE(CURRENT_DATE, INTERVAL 1 DAY) AS 'tanggal_besok' "
                    + "FROM `tb_laporan_produksi` LEFT JOIN `tb_bahan_baku_masuk` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta` "
                    + "LEFT JOIN `tb_lab_bahan_baku` ON `tb_bahan_baku_masuk`.`no_kartu_waleta` = `tb_lab_bahan_baku`.`no_kartu_waleta` "
                    + "WHERE `tanggal_lp` = ADDDATE(CURRENT_DATE, INTERVAL 1 DAY) AND `tb_laporan_produksi`.`no_kartu_waleta` NOT LIKE '%CMP%'"
                    + "GROUP BY `tb_laporan_produksi`.`no_kartu_waleta`"
                    + "ORDER BY `tb_lab_bahan_baku`.`nitrit_bm_w3` DESC";
//            System.out.println(sql);
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[3];
            while (rs.next()) {
                row[0] = rs.getString("no_kartu_waleta");
                row[1] = rs.getString("no_registrasi");
                row[2] = Math.round(rs.getFloat("nitrit_bm_w3") * 10.f) / 10.f;
                model.addRow(row);
                label_tgl_nitrit_baku.setText("Treatment " + new SimpleDateFormat("dd MMM yyyy").format(rs.getDate("tanggal_besok")));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JFrame_TV_QC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void refreshTabel_warning_dokumen() {
        try {
            int total_merah = 0;
            DefaultTableModel model = (DefaultTableModel) tabel_warning_dokumen.getModel();
            model.setRowCount(0);
            sql = "SELECT `tb_dokumen_qc_update`.`kode_dokumen`, `tb_dokumen_qc`.`nama_dokumen`, `tanggal_dokumen`, `tanggal_kadaluarsa`, DATEDIFF(`tanggal_kadaluarsa`, CURRENT_DATE) AS 'hari'\n"
                    + "FROM `tb_dokumen_qc_update` LEFT JOIN `tb_dokumen_qc` ON `tb_dokumen_qc_update`.`kode_dokumen` = `tb_dokumen_qc`.`kode_dokumen`\n"
                    + "WHERE 1 ORDER BY `hari`";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            int no = 0;
            while (rs.next()) {
                no++;
                row[0] = no;
                row[1] = rs.getString("kode_dokumen");
                row[2] = rs.getString("nama_dokumen");
                row[3] = rs.getDate("tanggal_kadaluarsa");
                row[4] = rs.getInt("hari");
                model.addRow(row);
                if (rs.getInt("hari") < 31) {
                    total_merah++;
                }
            }

            ColumnsAutoSizer.sizeColumnsToFit(tabel_warning_dokumen);
            label_total_warning_dokumen.setText("TOTAL : " + decimalFormat.format(total_merah));
            tabel_warning_dokumen.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (isSelected) {
                        comp.setBackground(tabel_warning_dokumen.getSelectionBackground());
                        comp.setForeground(tabel_warning_dokumen.getSelectionForeground());
                    } else {
                        if ((int) tabel_warning_dokumen.getValueAt(row, 4) < 31) {
                            comp.setBackground(Color.RED);
                            comp.setForeground(Color.WHITE);
                        } else {
                            comp.setBackground(tabel_warning_dokumen.getBackground());
                            comp.setForeground(tabel_warning_dokumen.getForeground());
                        }
                    }
                    return comp;
                }
            });
            tabel_warning_dokumen.repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JFrame_TV_QC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshIsu1() {
        try {
            int data = 0;
            sql = "SELECT * FROM `tb_isu_produksi` WHERE (`dept` LIKE '%ALL%' OR `dept` LIKE '%TREATMENT%') AND `tanggal_selesai` IS NULL ORDER BY `tanggal_isu` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                if (data == 0) {
                    jTextArea1.setText(rs.getString("masalah"));
                    label_kode.setText(rs.getString("kode_isu"));
                    label_tgl_isu.setText(new SimpleDateFormat("dd MMMM yyyy").format(rs.getDate("tanggal_isu")));
                    label_departemen.setText(rs.getString("dept"));
                    label_penanggungjawab.setText(rs.getString("penanggungjawab"));
                    label_image.setIcon(Utility.ResizeImage(null, rs.getBytes("gambar_isu"), label_image.getWidth(), label_image.getHeight()));
                }
                data++;
            }

        } catch (Exception ex) {
            Logger.getLogger(JFrame_TV_QC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshIsu2() {
        try {
            int data = 0;
            sql = "SELECT * FROM `tb_isu_produksi` WHERE (`dept` LIKE '%ALL%' OR `dept` LIKE '%TREATMENT%') AND `tanggal_selesai` IS NULL ORDER BY `tanggal_isu` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                if (data == 1) {
                    jTextArea2.setText(rs.getString("masalah"));
                    label_kode1.setText(rs.getString("kode_isu"));
                    label_tgl_isu1.setText(new SimpleDateFormat("dd MMMM yyyy").format(rs.getDate("tanggal_isu")));
                    label_departemen1.setText(rs.getString("dept"));
                    label_penanggungjawab1.setText(rs.getString("penanggungjawab"));
                    label_image1.setIcon(Utility.ResizeImage(null, rs.getBytes("gambar_isu"), label_image1.getWidth(), label_image1.getHeight()));
                }
                data++;
            }

        } catch (Exception ex) {
            Logger.getLogger(JFrame_TV_QC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshIsu3() {
        try {
            int data = 0;
            sql = "SELECT * FROM `tb_isu_produksi` WHERE (`dept` LIKE '%ALL%' OR `dept` LIKE '%TREATMENT%') AND `tanggal_selesai` IS NULL ORDER BY `tanggal_isu` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                if (data == 2) {
                    jTextArea3.setText(rs.getString("masalah"));
                    label_kode2.setText(rs.getString("kode_isu"));
                    label_tgl_isu2.setText(new SimpleDateFormat("dd MMMM yyyy").format(rs.getDate("tanggal_isu")));
                    label_departemen2.setText(rs.getString("dept"));
                    label_penanggungjawab2.setText(rs.getString("penanggungjawab"));
                    label_image2.setIcon(Utility.ResizeImage(null, rs.getBytes("gambar_isu"), label_image2.getWidth(), label_image2.getHeight()));
                }
                data++;
            }

        } catch (Exception ex) {
            Logger.getLogger(JFrame_TV_QC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshIsu4() {
        try {
            int data = 0;
            sql = "SELECT * FROM `tb_isu_produksi` WHERE (`dept` LIKE '%ALL%' OR `dept` LIKE '%TREATMENT%') AND `tanggal_selesai` IS NULL ORDER BY `tanggal_isu` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                if (data == 3) {
                    jTextArea4.setText(rs.getString("masalah"));
                    label_kode3.setText(rs.getString("kode_isu"));
                    label_tgl_isu3.setText(new SimpleDateFormat("dd MMMM yyyy").format(rs.getDate("tanggal_isu")));
                    label_departemen3.setText(rs.getString("dept"));
                    label_penanggungjawab3.setText(rs.getString("penanggungjawab"));
                    label_image3.setIcon(Utility.ResizeImage(null, rs.getBytes("gambar_isu"), label_image3.getWidth(), label_image3.getHeight()));
                }
                data++;
            }

        } catch (Exception ex) {
            Logger.getLogger(JFrame_TV_QC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshIsu5() {
        try {
            int data = 0;
            sql = "SELECT * FROM `tb_isu_produksi` WHERE (`dept` LIKE '%ALL%' OR `dept` LIKE '%TREATMENT%') AND `tanggal_selesai` IS NULL ORDER BY `tanggal_isu` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                if (data == 4) {
                    jTextArea5.setText(rs.getString("masalah"));
                    label_kode4.setText(rs.getString("kode_isu"));
                    label_tgl_isu4.setText(new SimpleDateFormat("dd MMMM yyyy").format(rs.getDate("tanggal_isu")));
                    label_departemen4.setText(rs.getString("dept"));
                    label_penanggungjawab4.setText(rs.getString("penanggungjawab"));
                    label_image4.setIcon(Utility.ResizeImage(null, rs.getBytes("gambar_isu"), label_image4.getWidth(), label_image4.getHeight()));
                }
                data++;
            }

        } catch (Exception ex) {
            Logger.getLogger(JFrame_TV_QC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel_treatment = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        label_total_kartu_jadi_oren = new javax.swing.JLabel();
        label_total_kartu_jadi_putih = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_Data_Nitrit_jadi = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        label_jumlah_kartu_hold1 = new javax.swing.JLabel();
        label_total_kartu_jadi_merah = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        Table_Data_nitrit_baku = new javax.swing.JTable();
        jLabel22 = new javax.swing.JLabel();
        label_tgl_nitrit_baku = new javax.swing.JLabel();
        jPanel_warning_dokumen = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabel_warning_dokumen = new javax.swing.JTable();
        label_total_warning_dokumen = new javax.swing.JLabel();
        jPanel_isu1 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        label_tgl_isu = new javax.swing.JLabel();
        label_departemen = new javax.swing.JLabel();
        label_image = new javax.swing.JLabel();
        label_penanggungjawab = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        label_kode = new javax.swing.JLabel();
        jScrollPane13 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jPanel_isu2 = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        label_tgl_isu1 = new javax.swing.JLabel();
        label_departemen1 = new javax.swing.JLabel();
        label_image1 = new javax.swing.JLabel();
        label_penanggungjawab1 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        label_kode1 = new javax.swing.JLabel();
        jScrollPane14 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jPanel_isu3 = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        label_tgl_isu2 = new javax.swing.JLabel();
        label_departemen2 = new javax.swing.JLabel();
        label_image2 = new javax.swing.JLabel();
        label_penanggungjawab2 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        label_kode2 = new javax.swing.JLabel();
        jScrollPane15 = new javax.swing.JScrollPane();
        jTextArea3 = new javax.swing.JTextArea();
        jPanel_isu4 = new javax.swing.JPanel();
        label_kode3 = new javax.swing.JLabel();
        label_tgl_isu3 = new javax.swing.JLabel();
        label_departemen3 = new javax.swing.JLabel();
        label_penanggungjawab3 = new javax.swing.JLabel();
        label_image3 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jScrollPane16 = new javax.swing.JScrollPane();
        jTextArea4 = new javax.swing.JTextArea();
        jPanel_isu5 = new javax.swing.JPanel();
        jLabel40 = new javax.swing.JLabel();
        label_tgl_isu4 = new javax.swing.JLabel();
        label_departemen4 = new javax.swing.JLabel();
        label_image4 = new javax.swing.JLabel();
        label_penanggungjawab4 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        label_kode4 = new javax.swing.JLabel();
        jScrollPane17 = new javax.swing.JScrollPane();
        jTextArea5 = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });

        jPanel_treatment.setBackground(new java.awt.Color(255, 255, 255));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel14.setText("/");

        label_total_kartu_jadi_oren.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kartu_jadi_oren.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        label_total_kartu_jadi_oren.setForeground(new java.awt.Color(255, 153, 0));
        label_total_kartu_jadi_oren.setText("0");

        label_total_kartu_jadi_putih.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kartu_jadi_putih.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        label_total_kartu_jadi_putih.setText("0");

        Table_Data_Nitrit_jadi.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        Table_Data_Nitrit_jadi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Kartu", "LP HOLD", "% HOLD"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Float.class
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
        Table_Data_Nitrit_jadi.setRowHeight(28);
        Table_Data_Nitrit_jadi.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Table_Data_Nitrit_jadi);

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel7.setText("NITRIT JADI");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel15.setText("/");

        label_jumlah_kartu_hold1.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_kartu_hold1.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        label_jumlah_kartu_hold1.setText("Total Kartu :");

        label_total_kartu_jadi_merah.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kartu_jadi_merah.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        label_total_kartu_jadi_merah.setForeground(new java.awt.Color(255, 0, 0));
        label_total_kartu_jadi_merah.setText("0");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 3, 18)); // NOI18N
        jLabel2.setText("Keterangan :");

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 3, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(0, 0, 255));
        jLabel16.setText("TR Sela untuk grade W3, GRS BRT, ");

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 3, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(0, 0, 255));
        jLabel17.setText("GRS RGN, SORTIR, KK KNG, Kartu Khusus");

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 3, 14)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 0, 0));
        jLabel19.setText("Persentase hold >20% & Kartu Khusus : TR Sela 2,5 jam");

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 3, 14)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(195, 195, 0));
        jLabel20.setText("Persentase hold <20% : TR Sela 1,5 jam");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 619, Short.MAX_VALUE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel7)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(label_jumlah_kartu_hold1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_kartu_jadi_putih)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_kartu_jadi_oren)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_kartu_jadi_merah)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_total_kartu_jadi_oren)
                    .addComponent(label_total_kartu_jadi_merah)
                    .addComponent(jLabel15)
                    .addComponent(jLabel14)
                    .addComponent(label_jumlah_kartu_hold1)
                    .addComponent(label_total_kartu_jadi_putih))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 477, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel19)
                .addContainerGap())
        );

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        Table_Data_nitrit_baku.setAutoCreateRowSorter(true);
        Table_Data_nitrit_baku.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        Table_Data_nitrit_baku.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Kartu", "RSB", "Nitrit"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Float.class
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
        Table_Data_nitrit_baku.setRowHeight(28);
        Table_Data_nitrit_baku.getTableHeader().setReorderingAllowed(false);
        jScrollPane8.setViewportView(Table_Data_nitrit_baku);

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel22.setText("NITRIT BAHAN MENTAH");

        label_tgl_nitrit_baku.setBackground(new java.awt.Color(255, 255, 255));
        label_tgl_nitrit_baku.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        label_tgl_nitrit_baku.setText("Treatment tanggal");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 692, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel22)
                            .addComponent(label_tgl_nitrit_baku))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_tgl_nitrit_baku)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel_treatmentLayout = new javax.swing.GroupLayout(jPanel_treatment);
        jPanel_treatment.setLayout(jPanel_treatmentLayout);
        jPanel_treatmentLayout.setHorizontalGroup(
            jPanel_treatmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_treatmentLayout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_treatmentLayout.setVerticalGroup(
            jPanel_treatmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("TREATMENT", jPanel_treatment);

        jPanel_warning_dokumen.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel1.setText("DOKUMEN PERLU DI PERBARUI");

        tabel_warning_dokumen.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        tabel_warning_dokumen.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Kode", "Dokumen", "Tgl Kadaluarsa", "Hari"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel_warning_dokumen.setRowHeight(25);
        tabel_warning_dokumen.setRowSelectionAllowed(false);
        tabel_warning_dokumen.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tabel_warning_dokumen);

        label_total_warning_dokumen.setBackground(new java.awt.Color(255, 255, 255));
        label_total_warning_dokumen.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        label_total_warning_dokumen.setText("TOTAL :");

        javax.swing.GroupLayout jPanel_warning_dokumenLayout = new javax.swing.GroupLayout(jPanel_warning_dokumen);
        jPanel_warning_dokumen.setLayout(jPanel_warning_dokumenLayout);
        jPanel_warning_dokumenLayout.setHorizontalGroup(
            jPanel_warning_dokumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_warning_dokumenLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_warning_dokumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(label_total_warning_dokumen)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 900, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(451, Short.MAX_VALUE))
        );
        jPanel_warning_dokumenLayout.setVerticalGroup(
            jPanel_warning_dokumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_warning_dokumenLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_total_warning_dokumen)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("WARNING DOKUMEN", jPanel_warning_dokumen);

        jPanel_isu1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel10.setText("Departemen :");

        label_tgl_isu.setBackground(new java.awt.Color(255, 255, 255));
        label_tgl_isu.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_tgl_isu.setText("-");

        label_departemen.setBackground(new java.awt.Color(255, 255, 255));
        label_departemen.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_departemen.setText("-");

        label_image.setBackground(new java.awt.Color(255, 255, 255));
        label_image.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_penanggungjawab.setBackground(new java.awt.Color(255, 255, 255));
        label_penanggungjawab.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_penanggungjawab.setText("-");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel8.setText("Penanggung Jawab :");

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel18.setText("Tanggal isu :");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel3.setText("Kode : ");

        label_kode.setBackground(new java.awt.Color(255, 255, 255));
        label_kode.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_kode.setText("-");

        jScrollPane13.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane13.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(5);
        jTextArea1.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(2);
        jTextArea1.setText("-");
        jTextArea1.setWrapStyleWord(true);
        jScrollPane13.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel_isu1Layout = new javax.swing.GroupLayout(jPanel_isu1);
        jPanel_isu1.setLayout(jPanel_isu1Layout);
        jPanel_isu1Layout.setHorizontalGroup(
            jPanel_isu1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_isu1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_image, javax.swing.GroupLayout.PREFERRED_SIZE, 839, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_isu1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel_isu1Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_kode, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_isu1Layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_penanggungjawab, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_isu1Layout.createSequentialGroup()
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_tgl_isu, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_isu1Layout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_departemen, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane13))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_isu1Layout.setVerticalGroup(
            jPanel_isu1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_isu1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_isu1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_image, javax.swing.GroupLayout.PREFERRED_SIZE, 598, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_isu1Layout.createSequentialGroup()
                        .addComponent(jScrollPane13, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_isu1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(label_kode))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_isu1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel18)
                            .addComponent(label_tgl_isu))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_isu1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(label_departemen))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_isu1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(label_penanggungjawab))))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Isu 1", jPanel_isu1);

        jPanel_isu2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel26.setText("Departemen :");

        label_tgl_isu1.setBackground(new java.awt.Color(255, 255, 255));
        label_tgl_isu1.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_tgl_isu1.setText("-");

        label_departemen1.setBackground(new java.awt.Color(255, 255, 255));
        label_departemen1.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_departemen1.setText("-");

        label_image1.setBackground(new java.awt.Color(255, 255, 255));
        label_image1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_penanggungjawab1.setBackground(new java.awt.Color(255, 255, 255));
        label_penanggungjawab1.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_penanggungjawab1.setText("-");

        jLabel27.setBackground(new java.awt.Color(255, 255, 255));
        jLabel27.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel27.setText("Penanggung Jawab :");

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel28.setText("Tanggal isu :");

        jLabel30.setBackground(new java.awt.Color(255, 255, 255));
        jLabel30.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel30.setText("Kode : ");

        label_kode1.setBackground(new java.awt.Color(255, 255, 255));
        label_kode1.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_kode1.setText("-");

        jScrollPane14.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane14.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N

        jTextArea2.setEditable(false);
        jTextArea2.setColumns(5);
        jTextArea2.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jTextArea2.setLineWrap(true);
        jTextArea2.setRows(2);
        jTextArea2.setText("-");
        jTextArea2.setWrapStyleWord(true);
        jScrollPane14.setViewportView(jTextArea2);

        javax.swing.GroupLayout jPanel_isu2Layout = new javax.swing.GroupLayout(jPanel_isu2);
        jPanel_isu2.setLayout(jPanel_isu2Layout);
        jPanel_isu2Layout.setHorizontalGroup(
            jPanel_isu2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_isu2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_image1, javax.swing.GroupLayout.PREFERRED_SIZE, 829, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_isu2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel_isu2Layout.createSequentialGroup()
                        .addGroup(jPanel_isu2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_isu2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(label_departemen1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_penanggungjawab1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_kode1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_tgl_isu1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane14))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_isu2Layout.setVerticalGroup(
            jPanel_isu2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_isu2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_isu2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_image1, javax.swing.GroupLayout.PREFERRED_SIZE, 598, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_isu2Layout.createSequentialGroup()
                        .addComponent(jScrollPane14, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_isu2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_isu2Layout.createSequentialGroup()
                                .addComponent(jLabel30)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel28))
                            .addGroup(jPanel_isu2Layout.createSequentialGroup()
                                .addComponent(label_kode1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_tgl_isu1)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_isu2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_isu2Layout.createSequentialGroup()
                                .addComponent(jLabel26)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel27))
                            .addGroup(jPanel_isu2Layout.createSequentialGroup()
                                .addComponent(label_departemen1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_penanggungjawab1)))))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Isu 2", jPanel_isu2);

        jPanel_isu3.setBackground(new java.awt.Color(255, 255, 255));

        jLabel32.setBackground(new java.awt.Color(255, 255, 255));
        jLabel32.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel32.setText("Departemen :");

        label_tgl_isu2.setBackground(new java.awt.Color(255, 255, 255));
        label_tgl_isu2.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_tgl_isu2.setText("-");

        label_departemen2.setBackground(new java.awt.Color(255, 255, 255));
        label_departemen2.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_departemen2.setText("-");

        label_image2.setBackground(new java.awt.Color(255, 255, 255));
        label_image2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_penanggungjawab2.setBackground(new java.awt.Color(255, 255, 255));
        label_penanggungjawab2.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_penanggungjawab2.setText("-");

        jLabel33.setBackground(new java.awt.Color(255, 255, 255));
        jLabel33.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel33.setText("Penanggung Jawab :");

        jLabel34.setBackground(new java.awt.Color(255, 255, 255));
        jLabel34.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel34.setText("Tanggal isu :");

        jLabel36.setBackground(new java.awt.Color(255, 255, 255));
        jLabel36.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel36.setText("Kode : ");

        label_kode2.setBackground(new java.awt.Color(255, 255, 255));
        label_kode2.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_kode2.setText("-");

        jScrollPane15.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane15.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N

        jTextArea3.setEditable(false);
        jTextArea3.setColumns(5);
        jTextArea3.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jTextArea3.setLineWrap(true);
        jTextArea3.setRows(2);
        jTextArea3.setText("-");
        jTextArea3.setWrapStyleWord(true);
        jScrollPane15.setViewportView(jTextArea3);

        javax.swing.GroupLayout jPanel_isu3Layout = new javax.swing.GroupLayout(jPanel_isu3);
        jPanel_isu3.setLayout(jPanel_isu3Layout);
        jPanel_isu3Layout.setHorizontalGroup(
            jPanel_isu3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_isu3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_image2, javax.swing.GroupLayout.PREFERRED_SIZE, 829, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_isu3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel_isu3Layout.createSequentialGroup()
                        .addGroup(jPanel_isu3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_isu3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(label_departemen2, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_penanggungjawab2, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_kode2, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_tgl_isu2, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane15))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_isu3Layout.setVerticalGroup(
            jPanel_isu3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_isu3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_isu3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_image2, javax.swing.GroupLayout.PREFERRED_SIZE, 598, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_isu3Layout.createSequentialGroup()
                        .addComponent(jScrollPane15, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_isu3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_isu3Layout.createSequentialGroup()
                                .addComponent(jLabel36)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel34))
                            .addGroup(jPanel_isu3Layout.createSequentialGroup()
                                .addComponent(label_kode2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_tgl_isu2)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_isu3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_isu3Layout.createSequentialGroup()
                                .addComponent(jLabel32)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel33))
                            .addGroup(jPanel_isu3Layout.createSequentialGroup()
                                .addComponent(label_departemen2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_penanggungjawab2)))))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Isu 3", jPanel_isu3);

        jPanel_isu4.setBackground(new java.awt.Color(255, 255, 255));

        label_kode3.setBackground(new java.awt.Color(255, 255, 255));
        label_kode3.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_kode3.setText("-");

        label_tgl_isu3.setBackground(new java.awt.Color(255, 255, 255));
        label_tgl_isu3.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_tgl_isu3.setText("-");

        label_departemen3.setBackground(new java.awt.Color(255, 255, 255));
        label_departemen3.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_departemen3.setText("-");

        label_penanggungjawab3.setBackground(new java.awt.Color(255, 255, 255));
        label_penanggungjawab3.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_penanggungjawab3.setText("-");

        label_image3.setBackground(new java.awt.Color(255, 255, 255));
        label_image3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel35.setBackground(new java.awt.Color(255, 255, 255));
        jLabel35.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel35.setText("Departemen :");

        jLabel37.setBackground(new java.awt.Color(255, 255, 255));
        jLabel37.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel37.setText("Penanggung Jawab :");

        jLabel38.setBackground(new java.awt.Color(255, 255, 255));
        jLabel38.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel38.setText("Tanggal isu :");

        jLabel39.setBackground(new java.awt.Color(255, 255, 255));
        jLabel39.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel39.setText("Kode : ");

        jScrollPane16.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane16.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N

        jTextArea4.setEditable(false);
        jTextArea4.setColumns(5);
        jTextArea4.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jTextArea4.setLineWrap(true);
        jTextArea4.setRows(2);
        jTextArea4.setText("-");
        jTextArea4.setWrapStyleWord(true);
        jScrollPane16.setViewportView(jTextArea4);

        javax.swing.GroupLayout jPanel_isu4Layout = new javax.swing.GroupLayout(jPanel_isu4);
        jPanel_isu4.setLayout(jPanel_isu4Layout);
        jPanel_isu4Layout.setHorizontalGroup(
            jPanel_isu4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_isu4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_image3, javax.swing.GroupLayout.PREFERRED_SIZE, 829, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_isu4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel_isu4Layout.createSequentialGroup()
                        .addGroup(jPanel_isu4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_isu4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(label_departemen3, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_penanggungjawab3, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_kode3, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_tgl_isu3, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane16))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_isu4Layout.setVerticalGroup(
            jPanel_isu4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_isu4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_isu4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_image3, javax.swing.GroupLayout.PREFERRED_SIZE, 598, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_isu4Layout.createSequentialGroup()
                        .addComponent(jScrollPane16, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_isu4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_isu4Layout.createSequentialGroup()
                                .addComponent(jLabel39)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel38))
                            .addGroup(jPanel_isu4Layout.createSequentialGroup()
                                .addComponent(label_kode3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_tgl_isu3)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_isu4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_isu4Layout.createSequentialGroup()
                                .addComponent(jLabel35)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel37))
                            .addGroup(jPanel_isu4Layout.createSequentialGroup()
                                .addComponent(label_departemen3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_penanggungjawab3)))))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Isu 4", jPanel_isu4);

        jPanel_isu5.setBackground(new java.awt.Color(255, 255, 255));

        jLabel40.setBackground(new java.awt.Color(255, 255, 255));
        jLabel40.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel40.setText("Departemen :");

        label_tgl_isu4.setBackground(new java.awt.Color(255, 255, 255));
        label_tgl_isu4.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_tgl_isu4.setText("-");

        label_departemen4.setBackground(new java.awt.Color(255, 255, 255));
        label_departemen4.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_departemen4.setText("-");

        label_image4.setBackground(new java.awt.Color(255, 255, 255));
        label_image4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_penanggungjawab4.setBackground(new java.awt.Color(255, 255, 255));
        label_penanggungjawab4.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_penanggungjawab4.setText("-");

        jLabel41.setBackground(new java.awt.Color(255, 255, 255));
        jLabel41.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel41.setText("Penanggung Jawab :");

        jLabel42.setBackground(new java.awt.Color(255, 255, 255));
        jLabel42.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel42.setText("Tanggal isu :");

        jLabel43.setBackground(new java.awt.Color(255, 255, 255));
        jLabel43.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel43.setText("Kode : ");

        label_kode4.setBackground(new java.awt.Color(255, 255, 255));
        label_kode4.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_kode4.setText("-");

        jScrollPane17.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane17.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N

        jTextArea5.setEditable(false);
        jTextArea5.setColumns(5);
        jTextArea5.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jTextArea5.setLineWrap(true);
        jTextArea5.setRows(2);
        jTextArea5.setText("-");
        jTextArea5.setWrapStyleWord(true);
        jScrollPane17.setViewportView(jTextArea5);

        javax.swing.GroupLayout jPanel_isu5Layout = new javax.swing.GroupLayout(jPanel_isu5);
        jPanel_isu5.setLayout(jPanel_isu5Layout);
        jPanel_isu5Layout.setHorizontalGroup(
            jPanel_isu5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_isu5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_image4, javax.swing.GroupLayout.PREFERRED_SIZE, 829, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_isu5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel_isu5Layout.createSequentialGroup()
                        .addGroup(jPanel_isu5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_isu5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(label_departemen4, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_penanggungjawab4, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_kode4, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_tgl_isu4, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane17))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_isu5Layout.setVerticalGroup(
            jPanel_isu5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_isu5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_isu5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_image4, javax.swing.GroupLayout.PREFERRED_SIZE, 598, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_isu5Layout.createSequentialGroup()
                        .addComponent(jScrollPane17, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_isu5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_isu5Layout.createSequentialGroup()
                                .addComponent(jLabel43)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel42))
                            .addGroup(jPanel_isu5Layout.createSequentialGroup()
                                .addComponent(label_kode4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_tgl_isu4)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_isu5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_isu5Layout.createSequentialGroup()
                                .addComponent(jLabel40)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel41))
                            .addGroup(jPanel_isu5Layout.createSequentialGroup()
                                .addComponent(label_departemen4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_penanggungjawab4)))))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Isu 5", jPanel_isu5);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
        // TODO add your handling code here:
        switch (jTabbedPane1.getSelectedIndex()) {
            case 0:
                refreshTabel_hold_jadi();
                refreshTabel_hold_baku();
                break;
            case 1:
                refreshTabel_warning_dokumen();
                break;
            case 2:
                refreshIsu1();
                break;
            case 3:
                refreshIsu2();
                break;
            case 4:
                refreshIsu3();
                break;
            case 5:
                refreshIsu4();
                break;
            case 6:
                refreshIsu5();
                break;
        }
    }//GEN-LAST:event_jTabbedPane1StateChanged

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        t.cancel();
        System.out.println("STOP");
    }//GEN-LAST:event_formWindowClosing

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JFrame_TV_QC.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFrame_TV_QC.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFrame_TV_QC.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrame_TV_QC.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFrame_TV_QC frame = new JFrame_TV_QC();
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                frame.setEnabled(true);
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable Table_Data_Nitrit_jadi;
    private javax.swing.JTable Table_Data_nitrit_baku;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel_isu1;
    private javax.swing.JPanel jPanel_isu2;
    private javax.swing.JPanel jPanel_isu3;
    private javax.swing.JPanel jPanel_isu4;
    private javax.swing.JPanel jPanel_isu5;
    private javax.swing.JPanel jPanel_treatment;
    private javax.swing.JPanel jPanel_warning_dokumen;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane16;
    private javax.swing.JScrollPane jScrollPane17;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextArea jTextArea3;
    private javax.swing.JTextArea jTextArea4;
    private javax.swing.JTextArea jTextArea5;
    private javax.swing.JLabel label_departemen;
    private javax.swing.JLabel label_departemen1;
    private javax.swing.JLabel label_departemen2;
    private javax.swing.JLabel label_departemen3;
    private javax.swing.JLabel label_departemen4;
    private javax.swing.JLabel label_image;
    private javax.swing.JLabel label_image1;
    private javax.swing.JLabel label_image2;
    private javax.swing.JLabel label_image3;
    private javax.swing.JLabel label_image4;
    private javax.swing.JLabel label_jumlah_kartu_hold1;
    private javax.swing.JLabel label_kode;
    private javax.swing.JLabel label_kode1;
    private javax.swing.JLabel label_kode2;
    private javax.swing.JLabel label_kode3;
    private javax.swing.JLabel label_kode4;
    private javax.swing.JLabel label_penanggungjawab;
    private javax.swing.JLabel label_penanggungjawab1;
    private javax.swing.JLabel label_penanggungjawab2;
    private javax.swing.JLabel label_penanggungjawab3;
    private javax.swing.JLabel label_penanggungjawab4;
    private javax.swing.JLabel label_tgl_isu;
    private javax.swing.JLabel label_tgl_isu1;
    private javax.swing.JLabel label_tgl_isu2;
    private javax.swing.JLabel label_tgl_isu3;
    private javax.swing.JLabel label_tgl_isu4;
    private javax.swing.JLabel label_tgl_nitrit_baku;
    private javax.swing.JLabel label_total_kartu_jadi_merah;
    private javax.swing.JLabel label_total_kartu_jadi_oren;
    private javax.swing.JLabel label_total_kartu_jadi_putih;
    private javax.swing.JLabel label_total_warning_dokumen;
    private javax.swing.JTable tabel_warning_dokumen;
    // End of variables declaration//GEN-END:variables
}
