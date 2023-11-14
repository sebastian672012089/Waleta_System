package waleta_system.BahanJadi;

import java.awt.Color;
import java.awt.Component;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;

public class JFrame_TV_Grading extends javax.swing.JFrame {

    String sql = null, sql_rekap = null;
    ResultSet rs;
    Date today = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    static Timer t;
    static int detik = 0, menit = 0, jam = 0;
    static int tab = 0;

    public JFrame_TV_Grading() {
        try {
            
        } catch (Exception ex) {
            Logger.getLogger(JFrame_TV_Grading.class.getName()).log(Level.SEVERE, null, ex);
        }
        initComponents();
        init();
    }

    public void init() {
        try {
            detik = 0;
            menit = 0;
            jam = 0;
            refreshTable_1();
            refreshTable_2();
            refreshTable_3();

            TimerTask timer;
            timer = new TimerTask() {
                @Override
                public void run() {
                    boolean changeTab = true;
                    label_waktu.setText(jam + " : " + menit + " : " + detik);

                    if (detik == 0) {
                        while (changeTab) {
                            if (tab == 0 && Integer.valueOf(label_awas.getText()) == 0 && Integer.valueOf(label_siaga.getText()) == 0) {
                                tab = 1;
                            } else {
                                changeTab = false;
                            }
                        }
                        jTabbedPane1.setSelectedIndex(tab);
                        
                        tab++;
                        if (tab == 3) {
                            tab = 0;
                        }
                    }

                    detik = detik + 1;
                    if (detik == 60) {
                        detik = 0;
                        menit++;
                        if (menit == 60) {
                            menit = 0;
                            jam++;

                        }
                    }
                }
            };
            t = new Timer();
            t.schedule(timer, 1000, 1000);

        } catch (Exception ex) {
            Logger.getLogger(JFrame_TV_Grading.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_1() {
        try {
            System.out.println("refresh tabel 1");
            DefaultTableModel model = (DefaultTableModel) Table_Belum_Grading.getModel();
            model.setRowCount(0);
            int x = 1;
            int awas_lp = 0, awas_gram = 0, awas_kpg = 0;
            int siaga_lp = 0, siaga_gram = 0, siaga_kpg = 0;
            int normal_lp = 0, normal_gram = 0, normal_kpg = 0;
            Object[] row = new Object[6];
            sql = "SELECT `kode_asal`, `tb_laporan_produksi`.`kode_grade`, `tb_laporan_produksi`.`memo_lp`, `berat`, DATEDIFF(CURRENT_DATE(), `tanggal_masuk`) AS 'Result' \n"
                    + "FROM `tb_bahan_jadi_masuk` LEFT JOIN `tb_laporan_produksi` ON `tb_bahan_jadi_masuk`.`kode_asal` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "WHERE `tanggal_grading` IS NULL ORDER BY `result` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                row[0] = x;
                row[1] = rs.getString("kode_asal");
                row[2] = rs.getString("kode_grade");
                row[3] = rs.getString("memo_lp");
                row[4] = rs.getInt("berat");
                row[5] = rs.getInt("Result");

                if (rs.getInt("Result") > 3) {
                    awas_lp++;
                    awas_gram = awas_gram + rs.getInt("berat");
                    model.addRow(row);
                    x++;
                } else if (rs.getInt("Result") == 3) {
                    siaga_lp++;
                    siaga_gram = siaga_gram + rs.getInt("berat");
                    model.addRow(row);
                    x++;
                } else {
                    normal_lp++;
                    normal_gram = normal_gram + rs.getInt("berat");
                }
            }
            Table_Belum_Grading.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if ((int) Table_Belum_Grading.getValueAt(row, 5) > 3) {
                        if (isSelected) {
                            comp.setBackground(Table_Belum_Grading.getSelectionBackground());
                            comp.setForeground(Table_Belum_Grading.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.red);
                            comp.setForeground(Color.WHITE);
                        }
                    } else if ((int) Table_Belum_Grading.getValueAt(row, 5) == 3) {
                        if (isSelected) {
                            comp.setBackground(Table_Belum_Grading.getSelectionBackground());
                            comp.setForeground(Table_Belum_Grading.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.yellow);
                            comp.setForeground(Color.BLACK);
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(Table_Belum_Grading.getSelectionBackground());
                            comp.setForeground(Table_Belum_Grading.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.cyan);
                            comp.setForeground(Color.BLACK);
                        }
                    }
                    return comp;
                }
            });
            Table_Belum_Grading.repaint();
            Table_Belum_Grading.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if ((int) Table_Belum_Grading.getValueAt(row, 5) > 3) {
                        if (isSelected) {
                            comp.setBackground(Table_Belum_Grading.getSelectionBackground());
                            comp.setForeground(Table_Belum_Grading.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.red);
                            comp.setForeground(Color.WHITE);
                        }
                    } else if ((int) Table_Belum_Grading.getValueAt(row, 5) == 3) {
                        if (isSelected) {
                            comp.setBackground(Table_Belum_Grading.getSelectionBackground());
                            comp.setForeground(Table_Belum_Grading.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.yellow);
                            comp.setForeground(Color.BLACK);
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(Table_Belum_Grading.getSelectionBackground());
                            comp.setForeground(Table_Belum_Grading.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.cyan);
                            comp.setForeground(Color.BLACK);
                        }
                    }
                    return comp;
                }
            });
            Table_Belum_Grading.repaint();
            ColumnsAutoSizer.sizeColumnsToFit(Table_Belum_Grading);

            label_awas.setText(Integer.toString(awas_lp));
            label_siaga.setText(Integer.toString(siaga_lp));
            label_normal.setText(Integer.toString(normal_lp));
            label_total_gram.setText(decimalFormat.format(awas_gram + siaga_gram + normal_gram));

        } catch (SQLException ex) {
            Logger.getLogger(JFrame_TV_Grading.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_2() {
        try {
            System.out.println("refresh tabel 2");
            DefaultTableModel model = (DefaultTableModel) Table_LP_Belum_tutupan.getModel();
            model.setRowCount(0);
            int x = 1;
            Object[] row = new Object[5];
            sql = "SELECT `kode_asal`, `berat`, `tanggal_grading`, `tb_bahan_jadi_masuk`.`kode_tutupan`, `tb_tutupan_grading`.`status_box`, DATEDIFF(CURRENT_DATE(), `tanggal_grading`) AS 'Result'\n"
                    + "FROM `tb_bahan_jadi_masuk` LEFT JOIN `tb_tutupan_grading` ON `tb_bahan_jadi_masuk`.`kode_tutupan` = `tb_tutupan_grading`.`kode_tutupan`\n"
                    + "WHERE `tb_bahan_jadi_masuk`.`kode_tutupan` IS NULL AND `tanggal_grading` IS NOT NULL  \n"
                    + "ORDER BY `result`  DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                row[0] = x;
                row[1] = rs.getString("kode_asal");
                row[2] = rs.getInt("berat");
                row[3] = rs.getDate("tanggal_grading");
                row[4] = rs.getInt("Result");
                model.addRow(row);
                x++;
            }
            label_total_LP.setText(Integer.toString(x));
            ColumnsAutoSizer.sizeColumnsToFit(Table_LP_Belum_tutupan);
        } catch (SQLException ex) {
            Logger.getLogger(JFrame_TV_Grading.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void refreshTable_3(){
        try {
            System.out.println("refresh tabel 3");
            DefaultTableModel model = (DefaultTableModel) Table_Grade_Belum_tutupan.getModel();
            model.setRowCount(0);
            int x = 1;
            int total_kpg = 0, total_gram = 0;
            Object[] row = new Object[4];
            sql = "SELECT `grade_bahan_jadi`, `tb_grade_bahan_jadi`.`kode_grade`, SUM(`tb_grading_bahan_jadi`.`keping`) AS 'kpg', SUM(`tb_grading_bahan_jadi`.`gram`) AS 'gram' "
                    + "FROM `tb_grading_bahan_jadi` LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_grading_bahan_jadi`.`kode_asal_bahan_jadi` = `tb_bahan_jadi_masuk`.`kode_asal` "
                    + "LEFT JOIN `tb_tutupan_grading` ON `tb_bahan_jadi_masuk`.`kode_tutupan` = `tb_tutupan_grading`.`kode_tutupan` "
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_grading_bahan_jadi`.`grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode` "
                    + "WHERE `tb_bahan_jadi_masuk`.`kode_tutupan` IS NULL AND `tanggal_grading` IS NOT NULL "
                    + "GROUP BY `grade_bahan_jadi`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                row[0] = x;
                row[1] = rs.getString("kode_grade");
                row[2] = rs.getInt("kpg");
                total_kpg = total_kpg + rs.getInt("kpg");
                row[3] = rs.getInt("gram");
                total_gram = total_gram + rs.getInt("gram");
                model.addRow(row);
                x++;
            }

            label_total_kpg_belum_tutupan.setText(decimalFormat.format(total_kpg));
            label_total_gram_belum_tutupan.setText(decimalFormat.format(total_gram));
            ColumnsAutoSizer.sizeColumnsToFit(Table_Grade_Belum_tutupan);
        } catch (SQLException ex) {
            Logger.getLogger(JFrame_TV_Grading.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public void refresh_reproses() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_reproses.getModel();
            model.setRowCount(0);
            int x = 1;
            int awas_lp = 0, awas_gram = 0, awas_kpg = 0;
            int siaga_lp = 0, siaga_gram = 0, siaga_kpg = 0;
            int normal_lp = 0, normal_gram = 0, normal_kpg = 0;
            Object[] row = new Object[6];
            sql = "SELECT `no_reproses`, `tb_reproses`.`no_box`, `tb_grade_bahan_jadi`.`kode_grade`, `tb_box_bahan_jadi`.`keping`, `tb_box_bahan_jadi`.`berat`, DATEDIFF(CURRENT_DATE(), `tanggal_proses`) AS 'Result'\n"
                    + "FROM `tb_reproses` LEFT JOIN `tb_box_bahan_jadi` ON `tb_reproses`.`no_box` = `tb_box_bahan_jadi`.`no_box`\n"
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`\n"
                    + "WHERE `status` = 'IN PROSES' AND `tgl_selesai` IS NULL\n"
                    + "ORDER BY `result` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                row[0] = x;
                row[1] = rs.getString("no_box");
                row[2] = rs.getString("kode_grade");
                row[3] = rs.getInt("keping");
                row[4] = rs.getInt("berat");
                row[5] = rs.getInt("Result");

                if (rs.getInt("Result") > 7) {
                    awas_lp++;
                    awas_kpg = awas_kpg + rs.getInt("keping");
                    awas_gram = awas_gram + rs.getInt("berat");
                    model.addRow(row);
                    x++;
                } else if (rs.getInt("Result") == 7) {
                    siaga_lp++;
                    siaga_kpg = siaga_kpg + rs.getInt("keping");
                    siaga_gram = siaga_gram + rs.getInt("berat");
                    model.addRow(row);
                    x++;
                } else {
                    normal_lp++;
                    normal_kpg = normal_kpg + rs.getInt("keping");
                    normal_gram = normal_gram + rs.getInt("berat");
                }
            }
            Table_reproses.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if ((int) Table_reproses.getValueAt(row, 5) > 2) {
                        if (isSelected) {
                            comp.setBackground(Table_reproses.getSelectionBackground());
                            comp.setForeground(Table_reproses.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.red);
                            comp.setForeground(Color.WHITE);
                        }
                    } else if ((int) Table_reproses.getValueAt(row, 5) == 2) {
                        if (isSelected) {
                            comp.setBackground(Table_reproses.getSelectionBackground());
                            comp.setForeground(Table_reproses.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.yellow);
                            comp.setForeground(Color.BLACK);
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(Table_reproses.getSelectionBackground());
                            comp.setForeground(Table_reproses.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.cyan);
                            comp.setForeground(Color.BLACK);
                        }
                    }
                    return comp;
                }
            });
            Table_reproses.repaint();
            Table_reproses.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if ((int) Table_reproses.getValueAt(row, 5) > 2) {
                        if (isSelected) {
                            comp.setBackground(Table_reproses.getSelectionBackground());
                            comp.setForeground(Table_reproses.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.red);
                            comp.setForeground(Color.WHITE);
                        }
                    } else if ((int) Table_reproses.getValueAt(row, 5) == 2) {
                        if (isSelected) {
                            comp.setBackground(Table_reproses.getSelectionBackground());
                            comp.setForeground(Table_reproses.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.yellow);
                            comp.setForeground(Color.BLACK);
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(Table_reproses.getSelectionBackground());
                            comp.setForeground(Table_reproses.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.cyan);
                            comp.setForeground(Color.BLACK);
                        }
                    }
                    return comp;
                }
            });
            Table_reproses.repaint();
            ColumnsAutoSizer.sizeColumnsToFit(Table_reproses);

            label_total_gram_reproses.setText(decimalFormat.format(awas_gram + siaga_gram + normal_gram));
//            label_awas1.setText(Integer.toString(awas_lp));
//            label_siaga1.setText(Integer.toString(siaga_lp));
//            label_normal1.setText(Integer.toString(normal_lp));

        } catch (SQLException ex) {
            Logger.getLogger(JFrame_TV_Grading.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refresh_tabel_rekap() {
        try {
            DefaultTableModel model = (DefaultTableModel) tabel_rekap_grade.getModel();
            model.setRowCount(0);
            sql = "SELECT COUNT(`tb_reproses`.`no_box`) AS 'total_box', `bagian`, SUM(`tb_box_bahan_jadi`.`keping`) AS 'kpg', SUM(`tb_box_bahan_jadi`.`berat`) AS 'berat'\n"
                    + "FROM `tb_reproses` LEFT JOIN `tb_box_bahan_jadi` ON `tb_reproses`.`no_box` = `tb_box_bahan_jadi`.`no_box`\n"
                    + "WHERE `status` = 'IN PROSES' AND `tgl_selesai` IS NULL\n"
                    + "GROUP BY `bagian` ORDER BY `berat` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[6];
            while (rs.next()) {
                row[0] = rs.getString("bagian");
                row[1] = rs.getInt("total_box");
                row[2] = rs.getInt("kpg");
                row[3] = rs.getFloat("berat");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_rekap_grade);
        } catch (SQLException ex) {
            Logger.getLogger(JFrame_TV_Grading.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refresh_stok_reproses() {
        try {
            int total_stok_reproses = 0;
            DefaultTableModel model = (DefaultTableModel) tabel_stok_reproses.getModel();
            model.setRowCount(0);
            sql = "SELECT `tb_grade_bahan_jadi`.`Kategori1`, SUM(`berat`) AS 'berat', `bentuk_grade` \n"
                    + "FROM `tb_box_bahan_jadi` \n"
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_grade_bahan_jadi`.`kode` = `tb_box_bahan_jadi`.`kode_grade_bahan_jadi`\n"
                    + "LEFT JOIN `tb_reproses` ON `tb_box_bahan_jadi`.`no_box` = `tb_reproses`.`no_box`\n"
                    + "WHERE `lokasi_terakhir` = 'GRADING' AND `tb_grade_bahan_jadi`.`kode_grade` LIKE 'GNS%' \n"
                    + "AND `tb_grade_bahan_jadi`.`Kategori1` IN ('Rprs D JDN', 'Rprs D UTUH', 'Rprs D STR', 'Rprs D SWR', 'Rprs D HC', 'Pch, Kecil') "
                    + "AND `tb_reproses`.`no_box` IS NULL "
                    + "AND CONCAT(SUBSTRING(`no_tutupan`, 1, 3), `bentuk_grade`) <> 'PBJUtuh' "
                    + "GROUP BY `tb_grade_bahan_jadi`.`Kategori1`"
                    + "ORDER BY `berat` DESC";
//            System.out.println(sql);
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[6];
            while (rs.next()) {
                row[0] = rs.getString("Kategori1");
                row[1] = rs.getInt("berat");
                total_stok_reproses = total_stok_reproses + rs.getInt("berat");
                model.addRow(row);
            }

            sql = "SELECT SUM(`berat`) AS 'berat' \n"
                    + "FROM `tb_box_bahan_jadi` \n"
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_grade_bahan_jadi`.`kode` = `tb_box_bahan_jadi`.`kode_grade_bahan_jadi`\n"
                    + "LEFT JOIN `tb_reproses` ON `tb_box_bahan_jadi`.`no_box` = `tb_reproses`.`no_box`\n"
                    + "WHERE `lokasi_terakhir` = 'GRADING' "
                    + "AND `tb_grade_bahan_jadi`.`kode_grade` LIKE 'GNS%' \n"
                    + "AND `tb_grade_bahan_jadi`.`bentuk_grade` = 'Utuh'  "
                    + "AND `tb_reproses`.`no_box` IS NULL "
                    + "AND SUBSTRING(`no_tutupan`, 1, 3) = 'PBJ' ";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                row[0] = "Rprs D UTUH BELI";
                row[1] = rs.getInt("berat");
                total_stok_reproses = total_stok_reproses + rs.getInt("berat");
                model.addRow(row);
            }

            ColumnsAutoSizer.sizeColumnsToFit(tabel_stok_reproses);
            label_total_stok_reproses.setText(decimalFormat.format(total_stok_reproses));
        } catch (SQLException ex) {
            Logger.getLogger(JFrame_TV_Grading.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refresh_ByProduct_WIP() {
        try {
            DefaultTableModel model = (DefaultTableModel) tabel_BP_WIP.getModel();
            sql = "SELECT SUM(`sesekan`) AS 'ssk', SUM(`hancuran`) AS 'hc', SUM(`serabut`) AS 'srbt'\n"
                    + "FROM `tb_finishing_2` "
                    + "LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_finishing_2`.`no_laporan_produksi` = `tb_bahan_jadi_masuk`.`kode_asal` \n"
                    + "LEFT JOIN `tb_tutupan_grading` ON `tb_bahan_jadi_masuk`.`kode_tutupan` = `tb_tutupan_grading`.`kode_tutupan` \n"
                    + "WHERE (`tb_tutupan_grading`.`status_box` = 'PROSES' OR `tb_tutupan_grading`.`status_box` IS NULL)";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                model.setValueAt(rs.getFloat("ssk"), 0, 0);
                model.setValueAt(rs.getFloat("hc"), 0, 1);
                model.setValueAt(rs.getFloat("srbt"), 0, 2);
            }
        } catch (SQLException ex) {
            Logger.getLogger(JFrame_TV_Grading.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        label_judul1 = new javax.swing.JLabel();
        label_waktu = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel_tab1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_Belum_Grading = new javax.swing.JTable();
        label_awas = new javax.swing.JLabel();
        label_siaga = new javax.swing.JLabel();
        label_normal = new javax.swing.JLabel();
        space1 = new javax.swing.JLabel();
        space3 = new javax.swing.JLabel();
        label_total_gram = new javax.swing.JLabel();
        label_total_gram1 = new javax.swing.JLabel();
        label_total_gram2 = new javax.swing.JLabel();
        jPanel_tab2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Table_LP_Belum_tutupan = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        Table_Grade_Belum_tutupan = new javax.swing.JTable();
        label_waktu6 = new javax.swing.JLabel();
        label_total_LP = new javax.swing.JLabel();
        label_waktu1 = new javax.swing.JLabel();
        label_total_kpg_belum_tutupan = new javax.swing.JLabel();
        label_waktu3 = new javax.swing.JLabel();
        label_total_gram_belum_tutupan = new javax.swing.JLabel();
        label_waktu5 = new javax.swing.JLabel();
        jPanel_Reproses = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        Table_reproses = new javax.swing.JTable();
        jLabel61 = new javax.swing.JLabel();
        label_total_gram_reproses = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tabel_rekap_grade = new javax.swing.JTable();
        jScrollPane6 = new javax.swing.JScrollPane();
        tabel_stok_reproses = new javax.swing.JTable();
        jLabel64 = new javax.swing.JLabel();
        label_total_stok_reproses = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        tabel_BP_WIP = new javax.swing.JTable();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        label_judul1.setBackground(new java.awt.Color(255, 255, 255));
        label_judul1.setFont(new java.awt.Font("Arial", 1, 40)); // NOI18N
        label_judul1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_judul1.setText("LP BELUM GRADING");
        label_judul1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        label_waktu.setBackground(new java.awt.Color(255, 255, 255));
        label_waktu.setFont(new java.awt.Font("Arial", 1, 40)); // NOI18N
        label_waktu.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_waktu.setText("HH:MM:SS");
        label_waktu.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 0, 8)); // NOI18N
        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });

        jPanel_tab1.setBackground(new java.awt.Color(255, 255, 255));

        Table_Belum_Grading.setFont(new java.awt.Font("Arial Narrow", 1, 36)); // NOI18N
        Table_Belum_Grading.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "No LP", "Grade", "Memo", "Gram", "Lama Inap (Hari)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
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
        Table_Belum_Grading.setRowHeight(35);
        Table_Belum_Grading.setRowSelectionAllowed(false);
        Table_Belum_Grading.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Table_Belum_Grading);
        if (Table_Belum_Grading.getColumnModel().getColumnCount() > 0) {
            Table_Belum_Grading.getColumnModel().getColumn(3).setMaxWidth(600);
        }

        label_awas.setBackground(new java.awt.Color(255, 255, 255));
        label_awas.setFont(new java.awt.Font("Arial Narrow", 1, 60)); // NOI18N
        label_awas.setForeground(new java.awt.Color(255, 0, 0));
        label_awas.setText("88");

        label_siaga.setBackground(new java.awt.Color(255, 255, 255));
        label_siaga.setFont(new java.awt.Font("Arial Narrow", 1, 60)); // NOI18N
        label_siaga.setForeground(new java.awt.Color(255, 188, 0));
        label_siaga.setText("88");

        label_normal.setBackground(new java.awt.Color(255, 255, 255));
        label_normal.setFont(new java.awt.Font("Arial Narrow", 1, 60)); // NOI18N
        label_normal.setForeground(new java.awt.Color(0, 0, 255));
        label_normal.setText("88");

        space1.setBackground(new java.awt.Color(255, 255, 255));
        space1.setFont(new java.awt.Font("Arial Narrow", 1, 60)); // NOI18N
        space1.setText("/");

        space3.setBackground(new java.awt.Color(255, 255, 255));
        space3.setFont(new java.awt.Font("Arial Narrow", 1, 60)); // NOI18N
        space3.setText("/");

        label_total_gram.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram.setFont(new java.awt.Font("Arial Narrow", 1, 24)); // NOI18N
        label_total_gram.setText("0");

        label_total_gram1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram1.setFont(new java.awt.Font("Arial Narrow", 1, 24)); // NOI18N
        label_total_gram1.setText("Gram");

        label_total_gram2.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram2.setFont(new java.awt.Font("Arial Narrow", 1, 24)); // NOI18N
        label_total_gram2.setText("TOTAL :");

        javax.swing.GroupLayout jPanel_tab1Layout = new javax.swing.GroupLayout(jPanel_tab1);
        jPanel_tab1.setLayout(jPanel_tab1Layout);
        jPanel_tab1Layout.setHorizontalGroup(
            jPanel_tab1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1361, Short.MAX_VALUE)
            .addGroup(jPanel_tab1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_awas)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(space3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_siaga)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(space1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_normal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(label_total_gram2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_total_gram)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_total_gram1)
                .addContainerGap())
        );
        jPanel_tab1Layout.setVerticalGroup(
            jPanel_tab1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_tab1Layout.createSequentialGroup()
                .addGroup(jPanel_tab1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_awas)
                    .addComponent(label_siaga)
                    .addComponent(label_normal)
                    .addComponent(space1)
                    .addComponent(space3)
                    .addComponent(label_total_gram)
                    .addComponent(label_total_gram1)
                    .addComponent(label_total_gram2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 540, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("LP BELUM GRADING", jPanel_tab1);

        jPanel_tab2.setBackground(new java.awt.Color(255, 255, 255));

        Table_LP_Belum_tutupan.setFont(new java.awt.Font("Arial Narrow", 1, 24)); // NOI18N
        Table_LP_Belum_tutupan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "No LP / Beli BJ", "Gram", "Tgl Grading", "Lama Inap (Hari)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Integer.class
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
        Table_LP_Belum_tutupan.setRowHeight(30);
        Table_LP_Belum_tutupan.setRowSelectionAllowed(false);
        Table_LP_Belum_tutupan.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(Table_LP_Belum_tutupan);

        Table_Grade_Belum_tutupan.setFont(new java.awt.Font("Arial Narrow", 1, 24)); // NOI18N
        Table_Grade_Belum_tutupan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Grade BJ", "Kpg", "Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
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
        Table_Grade_Belum_tutupan.setRowHeight(30);
        Table_Grade_Belum_tutupan.setRowSelectionAllowed(false);
        Table_Grade_Belum_tutupan.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(Table_Grade_Belum_tutupan);

        label_waktu6.setBackground(new java.awt.Color(255, 255, 255));
        label_waktu6.setFont(new java.awt.Font("Arial Narrow", 1, 24)); // NOI18N
        label_waktu6.setText("TOTAL LP :");

        label_total_LP.setBackground(new java.awt.Color(255, 255, 255));
        label_total_LP.setFont(new java.awt.Font("Arial Narrow", 1, 24)); // NOI18N
        label_total_LP.setText("0");

        label_waktu1.setBackground(new java.awt.Color(255, 255, 255));
        label_waktu1.setFont(new java.awt.Font("Arial Narrow", 1, 24)); // NOI18N
        label_waktu1.setText("TOTAL :");

        label_total_kpg_belum_tutupan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg_belum_tutupan.setFont(new java.awt.Font("Arial Narrow", 1, 24)); // NOI18N
        label_total_kpg_belum_tutupan.setText("0");

        label_waktu3.setBackground(new java.awt.Color(255, 255, 255));
        label_waktu3.setFont(new java.awt.Font("Arial Narrow", 1, 24)); // NOI18N
        label_waktu3.setText("Kpg");

        label_total_gram_belum_tutupan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_belum_tutupan.setFont(new java.awt.Font("Arial Narrow", 1, 24)); // NOI18N
        label_total_gram_belum_tutupan.setText("0");

        label_waktu5.setBackground(new java.awt.Color(255, 255, 255));
        label_waktu5.setFont(new java.awt.Font("Arial Narrow", 1, 24)); // NOI18N
        label_waktu5.setText("Gram");

        javax.swing.GroupLayout jPanel_tab2Layout = new javax.swing.GroupLayout(jPanel_tab2);
        jPanel_tab2.setLayout(jPanel_tab2Layout);
        jPanel_tab2Layout.setHorizontalGroup(
            jPanel_tab2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_tab2Layout.createSequentialGroup()
                .addGroup(jPanel_tab2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_tab2Layout.createSequentialGroup()
                        .addComponent(label_waktu6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_LP)))
                .addGroup(jPanel_tab2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_tab2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 655, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_tab2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(label_waktu1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_kpg_belum_tutupan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_waktu3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_belum_tutupan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_waktu5)
                        .addContainerGap())))
        );
        jPanel_tab2Layout.setVerticalGroup(
            jPanel_tab2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_tab2Layout.createSequentialGroup()
                .addGroup(jPanel_tab2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE)
                    .addComponent(jScrollPane3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_tab2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_tab2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_waktu1)
                        .addComponent(label_total_kpg_belum_tutupan)
                        .addComponent(label_waktu3)
                        .addComponent(label_total_gram_belum_tutupan)
                        .addComponent(label_waktu5))
                    .addGroup(jPanel_tab2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_waktu6)
                        .addComponent(label_total_LP)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("REKAP LP BELUM DI TUTUP", jPanel_tab2);

        jPanel_Reproses.setBackground(new java.awt.Color(255, 255, 255));

        Table_reproses.setFont(new java.awt.Font("Arial Narrow", 1, 36)); // NOI18N
        Table_reproses.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "No BOX", "Grade", "Kpg", "Gram", "Lama Inap (Hari)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class
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
        Table_reproses.setRowHeight(35);
        Table_reproses.setRowSelectionAllowed(false);
        Table_reproses.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(Table_reproses);

        jLabel61.setBackground(new java.awt.Color(255, 255, 255));
        jLabel61.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel61.setText("Total Gram :");

        label_total_gram_reproses.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_reproses.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        label_total_gram_reproses.setText("0");

        tabel_rekap_grade.setFont(new java.awt.Font("Arial Narrow", 1, 32)); // NOI18N
        tabel_rekap_grade.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Bagian", "Box", "Kpg", "Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class
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
        tabel_rekap_grade.setRowHeight(38);
        tabel_rekap_grade.setRowSelectionAllowed(false);
        tabel_rekap_grade.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(tabel_rekap_grade);

        tabel_stok_reproses.setFont(new java.awt.Font("Arial Narrow", 1, 32)); // NOI18N
        tabel_stok_reproses.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Kategori", "Berat"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class
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
        tabel_stok_reproses.setRowHeight(38);
        tabel_stok_reproses.setRowSelectionAllowed(false);
        tabel_stok_reproses.getTableHeader().setReorderingAllowed(false);
        jScrollPane6.setViewportView(tabel_stok_reproses);

        jLabel64.setBackground(new java.awt.Color(255, 255, 255));
        jLabel64.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel64.setText("Total Stok Reproses :");

        label_total_stok_reproses.setBackground(new java.awt.Color(255, 255, 255));
        label_total_stok_reproses.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        label_total_stok_reproses.setText("0");

        tabel_BP_WIP.setFont(new java.awt.Font("Arial Narrow", 1, 32)); // NOI18N
        tabel_BP_WIP.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null}
            },
            new String [] {
                "Sesekan", "Hancuran", "Serabut"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        tabel_BP_WIP.setRowHeight(38);
        tabel_BP_WIP.setRowSelectionAllowed(false);
        tabel_BP_WIP.getTableHeader().setReorderingAllowed(false);
        jScrollPane7.setViewportView(tabel_BP_WIP);

        javax.swing.GroupLayout jPanel_ReprosesLayout = new javax.swing.GroupLayout(jPanel_Reproses);
        jPanel_Reproses.setLayout(jPanel_ReprosesLayout);
        jPanel_ReprosesLayout.setHorizontalGroup(
            jPanel_ReprosesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_ReprosesLayout.createSequentialGroup()
                .addGroup(jPanel_ReprosesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 834, Short.MAX_VALUE)
                    .addGroup(jPanel_ReprosesLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel61)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_reproses)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_ReprosesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_ReprosesLayout.createSequentialGroup()
                        .addComponent(jLabel64)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_stok_reproses)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE)
                    .addComponent(jScrollPane7)
                    .addComponent(jScrollPane6))
                .addContainerGap())
        );
        jPanel_ReprosesLayout.setVerticalGroup(
            jPanel_ReprosesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_ReprosesLayout.createSequentialGroup()
                .addGroup(jPanel_ReprosesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 579, Short.MAX_VALUE)
                    .addGroup(jPanel_ReprosesLayout.createSequentialGroup()
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_ReprosesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel61, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram_reproses, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel64, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_stok_reproses, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("REPROSES", jPanel_Reproses);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(label_judul1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_waktu, javax.swing.GroupLayout.PREFERRED_SIZE, 397, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_judul1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_waktu, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
        // TODO add your handling code here:
        switch (jTabbedPane1.getSelectedIndex()) {
            case 0:
                label_judul1.setText("LP BELUM GRADING");
                refreshTable_1();
                break;
            case 1:
                label_judul1.setText("LP BELUM TUTUPAN & BELUM JADI BOX");
                refreshTable_2();
                refreshTable_3();
                break;
            case 2:
                label_judul1.setText("REPROSES");
                refresh_reproses();
                refresh_tabel_rekap();
                refresh_stok_reproses();
                refresh_ByProduct_WIP();
                break;
            default:
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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrame_TV_Grading.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame_TV_Grading frame = new JFrame_TV_Grading();
                frame.setVisible(true);
                frame.setLocationRelativeTo(null);
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable Table_Belum_Grading;
    private javax.swing.JTable Table_Grade_Belum_tutupan;
    private javax.swing.JTable Table_LP_Belum_tutupan;
    private javax.swing.JTable Table_reproses;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel_Reproses;
    private javax.swing.JPanel jPanel_tab1;
    private javax.swing.JPanel jPanel_tab2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label_awas;
    private javax.swing.JLabel label_judul1;
    private javax.swing.JLabel label_normal;
    private javax.swing.JLabel label_siaga;
    private javax.swing.JLabel label_total_LP;
    private javax.swing.JLabel label_total_gram;
    private javax.swing.JLabel label_total_gram1;
    private javax.swing.JLabel label_total_gram2;
    private javax.swing.JLabel label_total_gram_belum_tutupan;
    private javax.swing.JLabel label_total_gram_reproses;
    private javax.swing.JLabel label_total_kpg_belum_tutupan;
    private javax.swing.JLabel label_total_stok_reproses;
    private javax.swing.JLabel label_waktu;
    private javax.swing.JLabel label_waktu1;
    private javax.swing.JLabel label_waktu3;
    private javax.swing.JLabel label_waktu5;
    private javax.swing.JLabel label_waktu6;
    private javax.swing.JLabel space1;
    private javax.swing.JLabel space3;
    private javax.swing.JTable tabel_BP_WIP;
    private javax.swing.JTable tabel_rekap_grade;
    private javax.swing.JTable tabel_stok_reproses;
    // End of variables declaration//GEN-END:variables
}
