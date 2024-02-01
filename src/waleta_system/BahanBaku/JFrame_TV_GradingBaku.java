package waleta_system.BahanBaku;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import static java.lang.Thread.sleep;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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

public class JFrame_TV_GradingBaku extends javax.swing.JFrame {

    String sql = null;
    ResultSet rs;
    Date today = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    DecimalFormat decimalFormat = new DecimalFormat();
    PreparedStatement pst;
    static Timer t;
    TimerTask timer;
    Thread clock;
    int detik = 0, tab = 0;

    public JFrame_TV_GradingBaku() {
        initComponents();
        Table_Data_Nitrit_baku1.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        Table_Data_Nitrit_baku2.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        Table_Data_Nitrit_baku_oren.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        Table_Data_Nitrit_baku_merah.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        clock = new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        refresh_JAM();
                        sleep(1000);
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(JFrame_TV_GradingBaku.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        clock.start();
        try {
            Refresh_proses_grading();
            timer = new TimerTask() {
                @Override
                public void run() {
                    boolean changeTab = true;
                    if (detik == 0) {
                        while (changeTab) {
                            jTabbedPane1.setSelectedIndex(tab);
                            if (tab == 4 && jTextArea1.getText().equals("-")) {
                                tab = 5;
                            } else if (tab == 5 && jTextArea2.getText().equals("-")) {
                                tab = 6;
                            } else if (tab == 6 && jTextArea3.getText().equals("-")) {
                                tab = 7;
                            } else if (tab == 7 && jTextArea4.getText().equals("-")) {
                                tab = 8;
                            } else if (tab == 8 && jTextArea5.getText().equals("-")) {
                                tab = 0;
                            } else {
                                changeTab = false;
                            }
                        }
                        tab++;
                        if (tab > 8) {
                            tab = 0;
                        }
                    }

                    detik = detik + 1;
                    if (detik > 59) {
                        detik = 0;
                    }
                }
            };
            t = new Timer();
            t.schedule(timer, 1000, 1000);
        } catch (Exception ex) {
            Logger.getLogger(JFrame_TV_GradingBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refresh_JAM() {
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");

        String formattedDate = myDateObj.format(myFormatObj);
        label_jam.setText(formattedDate);
    }

    public void Refresh_proses_grading() {
        try {
            DefaultTableModel model_on_proses = (DefaultTableModel) table_grading.getModel();
            model_on_proses.setRowCount(0);
            int total_berat_onproses = 0;
            sql = "SELECT `no_kartu_waleta`, `tgl_masuk`, `tb_supplier`.`nama_supplier`, `berat_awal`, DATEDIFF(CURRENT_DATE, `tgl_masuk`) AS 'hari' FROM `tb_bahan_baku_masuk` "
                    + "LEFT JOIN `tb_supplier` ON `tb_bahan_baku_masuk`.`kode_supplier` = `tb_supplier`.`kode_supplier` WHERE `tgl_timbang` IS NULL AND `berat_awal` > 0 ORDER BY `hari` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[5];
            int no = 0;
            while (rs.next()) {
                no++;
                row[0] = no;
                row[1] = rs.getString("no_kartu_waleta");
                row[2] = rs.getString("nama_supplier");
                row[3] = decimalFormat.format(rs.getFloat("berat_awal"));
                row[4] = rs.getInt("hari");
                total_berat_onproses = total_berat_onproses + rs.getInt("berat_awal");
                model_on_proses.addRow(row);
            }
            table_grading.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//                    if (column == 4) {
                    if ((int) table_grading.getValueAt(row, 4) > 7) {
                        if (isSelected) {
                            comp.setBackground(table_grading.getSelectionBackground());
                            comp.setForeground(table_grading.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.red);
                            comp.setForeground(Color.WHITE);
                        }
                    } else if ((int) table_grading.getValueAt(row, 4) > 6) {
                        if (isSelected) {
                            comp.setBackground(table_grading.getSelectionBackground());
                            comp.setForeground(table_grading.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.YELLOW);
                            comp.setForeground(table_grading.getForeground());
                        }
                    } else if ((int) table_grading.getValueAt(row, 4) <= 6) {
                        if (isSelected) {
                            comp.setBackground(table_grading.getSelectionBackground());
                            comp.setForeground(table_grading.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.green);
                            comp.setForeground(table_grading.getForeground());
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(table_grading.getSelectionBackground());
                            comp.setForeground(table_grading.getSelectionForeground());
                        } else {
                            comp.setBackground(table_grading.getBackground());
                            comp.setForeground(table_grading.getForeground());
                        }
                    }
//                    } else {
//                        if (isSelected) {
//                            comp.setBackground(table_stok_for_waleta.getSelectionBackground());
//                            comp.setForeground(table_stok_for_waleta.getSelectionForeground());
//                        } else {
//                            comp.setBackground(table_stok_for_waleta.getBackground());
//                            comp.setForeground(table_stok_for_waleta.getForeground());
//                        }
//                    }
                    return comp;
                }
            });
            table_grading.repaint();
            table_grading.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if ((int) table_grading.getValueAt(row, 4) > 7) {
                        if (isSelected) {
                            comp.setBackground(table_grading.getSelectionBackground());
                            comp.setForeground(table_grading.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.red);
                            comp.setForeground(Color.WHITE);
                        }
                    } else if ((int) table_grading.getValueAt(row, 4) > 6) {
                        if (isSelected) {
                            comp.setBackground(table_grading.getSelectionBackground());
                            comp.setForeground(table_grading.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.YELLOW);
                            comp.setForeground(table_grading.getForeground());
                        }
                    } else if ((int) table_grading.getValueAt(row, 4) <= 6) {
                        if (isSelected) {
                            comp.setBackground(table_grading.getSelectionBackground());
                            comp.setForeground(table_grading.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.green);
                            comp.setForeground(table_grading.getForeground());
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(table_grading.getSelectionBackground());
                            comp.setForeground(table_grading.getSelectionForeground());
                        } else {
                            comp.setBackground(table_grading.getBackground());
                            comp.setForeground(table_grading.getForeground());
                        }
                    }
                    return comp;
                }
            });
            table_grading.repaint();
            txt_total_berat.setText(decimalFormat.format(total_berat_onproses));
        } catch (SQLException ex) {
            Logger.getLogger(JFrame_TV_GradingBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refresh_ketahanan_baku() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                refresh_TabelKetahanan();
                Load_Ketahanan_Waleta();
                Refresh_grading_on_proses();
                this.stop();
                this.destroy();
            }
        };
        thread.start();
    }

    public void refresh_TabelKetahanan() {
        decimalFormat.setMaximumFractionDigits(0);
        int kpg_masuk = 0, gram_masuk = 0, kpg_lp = 0, gram_lp = 0, kpg_jual = 0, gram_keluar = 0, kpg_cmp = 0, gram_cmp = 0;
        float stok_waleta = 0, stok_sub = 0, stok_bp = 0, stok_jual = 0;
        ResultSet rs_grading, rs_jual, rs_lp, rs_cmp;
        try {
            DefaultTableModel model_waleta = (DefaultTableModel) table_stok_for_waleta.getModel();
            model_waleta.setRowCount(0);
            DefaultTableModel model_sub = (DefaultTableModel) table_stok_for_sub.getModel();
            model_sub.setRowCount(0);
            sql = "SELECT `kode_grade`, `jenis_bentuk`, `jenis_bulu`, `kategori_proses` FROM `tb_grade_bahan_baku` "
                    + "WHERE `kategori_proses` IN ('WALETA', 'SUB') GROUP BY `jenis_bentuk`, `jenis_bulu`, `kategori_proses`";
            pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            Object[] row = new Object[4];
            while (rs.next()) {
                String sql_grading = "SELECT SUM(`tb_grading_bahan_baku`.`jumlah_keping`) AS 'jumlah_keping', SUM(`tb_grading_bahan_baku`.`total_berat`) AS 'total_berat' "
                        + "FROM `tb_grading_bahan_baku` "
                        + "LEFT JOIN `tb_bahan_baku_masuk` ON `tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`"
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_grading_bahan_baku`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`"
                        + "WHERE `tb_grade_bahan_baku`.`jenis_bentuk` = '" + rs.getString("jenis_bentuk") + "' "
//                        + "AND `tb_bahan_baku_masuk`.`last_stok` > 0 "
                        + "AND `tb_grade_bahan_baku`.`jenis_bulu` = '" + rs.getString("jenis_bulu") + "' "
                        + "AND `tb_grade_bahan_baku`.`kategori_proses` = '" + rs.getString("kategori_proses") + "' "
                        + "AND `tgl_masuk`<='" + dateFormat.format(today) + "'";
                rs_grading = Utility.db.getStatement().executeQuery(sql_grading);
                if (rs_grading.next()) {
                    kpg_masuk = rs_grading.getInt("jumlah_keping");
                    gram_masuk = rs_grading.getInt("total_berat");
                }
                String sql_lp = "SELECT SUM(`jumlah_keping`) AS 'keping', SUM(`berat_basah`) AS 'berat' "
                        + "FROM `tb_laporan_produksi` "
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`"
                        + "LEFT JOIN `tb_bahan_baku_masuk` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`"
                        + "WHERE `tb_grade_bahan_baku`.`jenis_bentuk` = '" + rs.getString("jenis_bentuk") + "' "
//                        + "AND `tb_bahan_baku_masuk`.`last_stok` > 0 "
                        + "AND `tb_grade_bahan_baku`.`jenis_bulu` = '" + rs.getString("jenis_bulu") + "' "
                        + "AND `tb_grade_bahan_baku`.`kategori_proses` = '" + rs.getString("kategori_proses") + "' "
                        + "AND `tanggal_lp`<='" + dateFormat.format(today) + "'";
                rs_lp = Utility.db.getStatement().executeQuery(sql_lp);
                if (rs_lp.next()) {
                    kpg_lp = rs_lp.getInt("keping");
                    gram_lp = rs_lp.getInt("berat");
                }
                String sql_jual = "SELECT SUM(`total_keping_keluar`) AS 'keping', SUM(`total_berat_keluar`) AS 'berat' "
                        + "FROM `tb_bahan_baku_keluar` "
                        + "LEFT JOIN `tb_bahan_baku_keluar1` ON `tb_bahan_baku_keluar`.`kode_pengeluaran` = `tb_bahan_baku_keluar1`.`kode_pengeluaran` "
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_bahan_baku_keluar`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`"
                        + "LEFT JOIN `tb_bahan_baku_masuk` ON `tb_bahan_baku_keluar`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`"
                        + "WHERE `tb_grade_bahan_baku`.`jenis_bentuk` = '" + rs.getString("jenis_bentuk") + "' "
//                        + "AND `tb_bahan_baku_masuk`.`last_stok` > 0 "
                        + "AND `tb_grade_bahan_baku`.`jenis_bulu` = '" + rs.getString("jenis_bulu") + "' "
                        + "AND `tb_grade_bahan_baku`.`kategori_proses` = '" + rs.getString("kategori_proses") + "' "
                        + "AND `tgl_keluar`<='" + dateFormat.format(today) + "'";
                rs_jual = Utility.db.getStatement().executeQuery(sql_jual);
                if (rs_jual.next()) {
                    kpg_jual = rs_jual.getInt("keping");
                    gram_keluar = rs_jual.getInt("berat");
                }
                String sql_cmp = "SELECT SUM(`keping`) AS 'keping_cmp', SUM(`gram`) AS 'berat_cmp' "
                        + "FROM `tb_kartu_cmp_detail`\n"
                        + "LEFT JOIN `tb_kartu_cmp` ON `tb_kartu_cmp_detail`.`kode_kartu_cmp` = `tb_kartu_cmp`.`kode_kartu_cmp`"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_kartu_cmp_detail`.`no_grading` = `tb_grading_bahan_baku`.`no_grading`\n"
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_grading_bahan_baku`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`"
                        + "LEFT JOIN `tb_bahan_baku_masuk` ON `tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`"
                        + "WHERE `tb_grade_bahan_baku`.`jenis_bentuk` = '" + rs.getString("jenis_bentuk") + "' "
//                        + "AND `tb_bahan_baku_masuk`.`last_stok` > 0 "
                        + "AND `tb_grade_bahan_baku`.`jenis_bulu` = '" + rs.getString("jenis_bulu") + "' "
                        + "AND `tb_grade_bahan_baku`.`kategori_proses` = '" + rs.getString("kategori_proses") + "' "
                        + "AND `tanggal`<='" + dateFormat.format(today) + "'";
                rs_cmp = Utility.db.getStatement().executeQuery(sql_cmp);
                if (rs_cmp.next()) {
                    kpg_cmp = rs_cmp.getInt("keping_cmp");
                    gram_cmp = rs_cmp.getInt("berat_cmp");
                }

                float kpg_sisa = kpg_masuk - (kpg_lp + kpg_jual + kpg_cmp);
                float gram_sisa = gram_masuk - (gram_lp + gram_keluar + gram_cmp);

                row[0] = rs.getString("jenis_bentuk");
                row[1] = rs.getString("jenis_bulu");
                row[2] = kpg_sisa;
                row[3] = gram_sisa;
                switch (rs.getString("kategori_proses")) {
                    case "WALETA":
                        stok_waleta = stok_waleta + gram_sisa;
                        model_waleta.addRow(row);
                        switch (rs.getString("jenis_bentuk").toUpperCase()) {
                            case "MANGKOK":
                                table_stok_for_waleta1.setValueAt((float) table_stok_for_waleta1.getValueAt(0, 1) + kpg_sisa, 0, 1);
                                table_stok_for_waleta1.setValueAt((float) table_stok_for_waleta1.getValueAt(0, 2) + gram_sisa, 0, 2);
                                break;
                            case "PECAH":
                                table_stok_for_waleta1.setValueAt((float) table_stok_for_waleta1.getValueAt(1, 1) + kpg_sisa, 1, 1);
                                table_stok_for_waleta1.setValueAt((float) table_stok_for_waleta1.getValueAt(1, 2) + gram_sisa, 1, 2);
                                break;
                            case "OVAL":
                                table_stok_for_waleta1.setValueAt((float) table_stok_for_waleta1.getValueAt(2, 1) + kpg_sisa, 2, 1);
                                table_stok_for_waleta1.setValueAt((float) table_stok_for_waleta1.getValueAt(2, 2) + gram_sisa, 2, 2);
                                break;
                            case "SEGITIGA":
                                table_stok_for_waleta1.setValueAt((float) table_stok_for_waleta1.getValueAt(3, 1) + kpg_sisa, 3, 1);
                                table_stok_for_waleta1.setValueAt((float) table_stok_for_waleta1.getValueAt(3, 2) + gram_sisa, 3, 2);
                                break;
                            default:
                                break;
                        }
                        break;
                    case "SUB":
                        stok_sub = stok_sub + gram_sisa;
                        model_sub.addRow(row);
                        switch (rs.getString("jenis_bentuk").toUpperCase()) {
                            case "MANGKOK":
                                table_stok_for_sub1.setValueAt((float) table_stok_for_sub1.getValueAt(0, 1) + kpg_sisa, 0, 1);
                                table_stok_for_sub1.setValueAt((float) table_stok_for_sub1.getValueAt(0, 2) + gram_sisa, 0, 2);
                                break;
                            case "PECAH":
                                table_stok_for_sub1.setValueAt((float) table_stok_for_sub1.getValueAt(1, 1) + kpg_sisa, 1, 1);
                                table_stok_for_sub1.setValueAt((float) table_stok_for_sub1.getValueAt(1, 2) + gram_sisa, 1, 2);
                                break;
                            case "OVAL":
                                table_stok_for_sub1.setValueAt((float) table_stok_for_sub1.getValueAt(2, 1) + kpg_sisa, 2, 1);
                                table_stok_for_sub1.setValueAt((float) table_stok_for_sub1.getValueAt(2, 2) + gram_sisa, 2, 2);
                                break;
                            case "SEGITIGA":
                                table_stok_for_sub1.setValueAt((float) table_stok_for_sub1.getValueAt(3, 1) + kpg_sisa, 3, 1);
                                table_stok_for_sub1.setValueAt((float) table_stok_for_sub1.getValueAt(3, 2) + gram_sisa, 3, 2);
                                break;
                            default:
                                break;
                        }
                        break;
                    default:
                        break;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(JFrame_TV_GradingBaku.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            DefaultTableModel model_bp = (DefaultTableModel) table_stok_for_bp.getModel();
            model_bp.setRowCount(0);
            DefaultTableModel model_jual = (DefaultTableModel) table_stok_for_jual.getModel();
            model_jual.setRowCount(0);
            sql = "SELECT `kode_grade`, `jenis_bentuk`, `jenis_bulu`, `kategori_proses` FROM `tb_grade_bahan_baku` WHERE 1";
            pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            Object[] row = new Object[4];
            while (rs.next()) {
                String sql_grading = "SELECT SUM(`tb_grading_bahan_baku`.`jumlah_keping`) AS 'jumlah_keping', SUM(`tb_grading_bahan_baku`.`total_berat`) AS 'total_berat' "
                        + "FROM `tb_grading_bahan_baku` LEFT JOIN `tb_bahan_baku_masuk` ON `tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`"
                        + "WHERE `kode_grade` = '" + rs.getString("kode_grade") + "' AND `tgl_masuk`<='" + dateFormat.format(today) + "'";
                rs_grading = Utility.db.getStatement().executeQuery(sql_grading);
                if (rs_grading.next()) {
                    kpg_masuk = rs_grading.getInt("jumlah_keping");
                    gram_masuk = rs_grading.getInt("total_berat");
                }
                String sql_lp = "SELECT SUM(`jumlah_keping`) AS 'keping', SUM(`berat_basah`) AS 'berat' FROM `tb_laporan_produksi` "
                        + "WHERE `kode_grade` = '" + rs.getString("kode_grade") + "'  AND `tanggal_lp`<='" + dateFormat.format(today) + "'";
                rs_lp = Utility.db.getStatement().executeQuery(sql_lp);
                if (rs_lp.next()) {
                    kpg_lp = rs_lp.getInt("keping");
                    gram_lp = rs_lp.getInt("berat");
                }
                String sql_jual = "SELECT SUM(`total_keping_keluar`) AS 'keping', SUM(`total_berat_keluar`) AS 'berat' "
                        + "FROM `tb_bahan_baku_keluar` LEFT JOIN `tb_bahan_baku_keluar1` ON `tb_bahan_baku_keluar`.`kode_pengeluaran` = `tb_bahan_baku_keluar1`.`kode_pengeluaran` "
                        + "WHERE `tb_bahan_baku_keluar`.`kode_grade` = '" + rs.getString("kode_grade") + "'  AND `tgl_keluar`<='" + dateFormat.format(today) + "'";
                rs_jual = Utility.db.getStatement().executeQuery(sql_jual);
                if (rs_jual.next()) {
                    kpg_jual = rs_jual.getInt("keping");
                    gram_keluar = rs_jual.getInt("berat");
                }
                String sql_cmp = "SELECT SUM(`keping`) AS 'keping_cmp', SUM(`gram`) AS 'berat_cmp' FROM `tb_kartu_cmp_detail`\n"
                        + "LEFT JOIN `tb_kartu_cmp` ON `tb_kartu_cmp_detail`.`kode_kartu_cmp` = `tb_kartu_cmp`.`kode_kartu_cmp`"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_kartu_cmp_detail`.`no_grading` = `tb_grading_bahan_baku`.`no_grading`\n"
                        + "WHERE `tb_grading_bahan_baku`.`kode_grade` = '" + rs.getString("kode_grade") + "' AND `tanggal`<='" + dateFormat.format(today) + "'";
                rs_cmp = Utility.db.getStatement().executeQuery(sql_cmp);
                if (rs_cmp.next()) {
                    kpg_cmp = rs_cmp.getInt("keping_cmp");
                    gram_cmp = rs_cmp.getInt("berat_cmp");
                }
                float kpg_sisa = kpg_masuk - (kpg_lp + kpg_jual + kpg_cmp);
                float gram_sisa = gram_masuk - (gram_lp + gram_keluar + gram_cmp);

                row[0] = rs.getString("kode_grade");
                row[1] = kpg_sisa;
                row[2] = gram_sisa;
                switch (rs.getString("kategori_proses")) {
                    case "BP":
                        stok_bp = stok_bp + gram_sisa;
                        model_bp.addRow(row);
                        break;
                    case "JUAL":
                        stok_jual = stok_jual + gram_sisa;
                        model_jual.addRow(row);
                        break;
                    default:
                        break;
                }
            }
            double total_stok = stok_waleta + stok_sub + stok_bp + stok_jual;
            double persen_stok_waleta = Math.round(stok_waleta / total_stok * 1000d) / 10d;
            double persen_stok_sub = Math.round(stok_sub / total_stok * 1000d) / 10d;
            double persen_stok_bp = Math.round(stok_bp / total_stok * 1000d) / 10d;
            double persen_stok_jual = Math.round(stok_jual / total_stok * 1000d) / 10d;
            table_stok_for_waleta1.setValueAt(Math.round((float) table_stok_for_waleta1.getValueAt(0, 2) / total_stok * 1000d) / 10d, 0, 3);
            table_stok_for_waleta1.setValueAt(Math.round((float) table_stok_for_waleta1.getValueAt(1, 2) / total_stok * 1000d) / 10d, 1, 3);
            table_stok_for_waleta1.setValueAt(Math.round((float) table_stok_for_waleta1.getValueAt(2, 2) / total_stok * 1000d) / 10d, 2, 3);
            table_stok_for_waleta1.setValueAt(Math.round((float) table_stok_for_waleta1.getValueAt(3, 2) / total_stok * 1000d) / 10d, 3, 3);
            table_stok_for_sub1.setValueAt(Math.round((float) table_stok_for_sub1.getValueAt(0, 2) / total_stok * 1000d) / 10d, 0, 3);
            table_stok_for_sub1.setValueAt(Math.round((float) table_stok_for_sub1.getValueAt(1, 2) / total_stok * 1000d) / 10d, 1, 3);
            table_stok_for_sub1.setValueAt(Math.round((float) table_stok_for_sub1.getValueAt(2, 2) / total_stok * 1000d) / 10d, 2, 3);
            table_stok_for_sub1.setValueAt(Math.round((float) table_stok_for_sub1.getValueAt(3, 2) / total_stok * 1000d) / 10d, 3, 3);
            txt_stok_waleta1.setText(decimalFormat.format(stok_waleta) + " Gram - " + persen_stok_waleta + "%");
            txt_stok_sub1.setText(decimalFormat.format(stok_sub) + " Gram - " + persen_stok_sub + "%");
            txt_stok_bp1.setText(decimalFormat.format(stok_bp) + " Gram - " + persen_stok_bp + "%");
            txt_stok_unworkable1.setText(decimalFormat.format(stok_jual) + " Gram - " + persen_stok_jual + "%");
            ColumnsAutoSizer.sizeColumnsToFit(table_stok_for_waleta);
            ColumnsAutoSizer.sizeColumnsToFit(table_stok_for_sub);
            ColumnsAutoSizer.sizeColumnsToFit(table_stok_for_bp);
            ColumnsAutoSizer.sizeColumnsToFit(table_stok_for_jual);
            ColumnsAutoSizer.sizeColumnsToFit(table_stok_for_waleta1);
            ColumnsAutoSizer.sizeColumnsToFit(table_stok_for_sub1);

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -1);
            Date a_month_before_today = cal.getTime();
            sql = "SELECT AVG(`jumlah_per_hari`) AS 'avg' FROM \n"
                    + "(SELECT SUM(`berat_basah`) AS 'jumlah_per_hari' "
                    + "FROM `tb_laporan_produksi` \n"
                    + "WHERE "
                    + "LENGTH(`ruangan`) = 5 "
                    + "AND `tanggal_lp` BETWEEN '" + dateFormat.format(a_month_before_today) + "' AND '" + dateFormat.format(today) + "'\n"
                    + "GROUP BY `tanggal_lp`) AS T";
            rs = Utility.db.getStatement().executeQuery(sql);
            float rata2_pengeluaran_sub = 0;
            if (rs.next()) {
                rata2_pengeluaran_sub = rs.getInt("avg");
                txt_pengeluaran_sub.setText(decimalFormat.format(rata2_pengeluaran_sub));
            }
            float ketahanan_sub = stok_sub / rata2_pengeluaran_sub;
//            Calendar cal2 = Calendar.getInstance();
//            cal2.add(Calendar.DATE, Math.round(ketahanan_sub));
//            Date END_SUB = cal2.getTime();
            txt_ketahanan_sub.setText(decimalFormat.format(ketahanan_sub));
            Date END_SUB = Utility.addDaysSkippingSundays(new Date(), Math.round(ketahanan_sub));
            txt_tanggal_end_sub.setText(new SimpleDateFormat("dd MMMM yyyy").format(END_SUB));
        } catch (SQLException ex) {
            Logger.getLogger(JFrame_TV_GradingBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Load_Ketahanan_Waleta() {
        try {
            float stok_BRS_WLT = 0, stok_BR_WLT = 0, stok_BS_WLT = 0, stok_BuluLain_WLT = 0;
            for (int i = 0; i < table_stok_for_waleta.getRowCount(); i++) {
                switch (table_stok_for_waleta.getValueAt(i, 1).toString()) {
                    case "Bulu Ringan Sekali/Bulu Ringan":
                        stok_BRS_WLT = stok_BRS_WLT + Float.valueOf(table_stok_for_waleta.getValueAt(i, 3).toString());
                        break;
                    case "Bulu Ringan":
                        stok_BR_WLT = stok_BR_WLT + Float.valueOf(table_stok_for_waleta.getValueAt(i, 3).toString());
                        break;
                    case "Bulu Sedang":
                        stok_BS_WLT = stok_BS_WLT + Float.valueOf(table_stok_for_waleta.getValueAt(i, 3).toString());
                        break;
                    default:
                        stok_BuluLain_WLT = stok_BuluLain_WLT + Float.valueOf(table_stok_for_waleta.getValueAt(i, 3).toString());
                        break;
                }
            }

            DefaultTableModel model = (DefaultTableModel) table_ketahanan_stok_waleta.getModel();
            model.setRowCount(0);
            sql = "SELECT COUNT(`tb_karyawan`.`id_pegawai`) AS 'borong_cabut' "
                    + "FROM `tb_karyawan` "
                    + "LEFT JOIN `tb_grup_cabut` ON `tb_karyawan`.`id_pegawai` = `tb_grup_cabut`.`id_pegawai` "
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian` "
                    + "WHERE `status` = 'IN' AND `nama_bagian` LIKE '%CABUT-BORONG%'";
            rs = Utility.db.getStatement().executeQuery(sql);
            int jumlah_cabut_borong = 0;
            if (rs.next()) {
                jumlah_cabut_borong = rs.getInt("borong_cabut");
            }

            float ketahanan_BRS = stok_BRS_WLT / (jumlah_cabut_borong * 20 * 8);
            float ketahanan_BR = stok_BR_WLT / (jumlah_cabut_borong * 20 * 8);
            float ketahanan_BS = stok_BS_WLT / (jumlah_cabut_borong * 13 * 8);
            float ketahanan_Other = stok_BuluLain_WLT / (jumlah_cabut_borong * 16 * 8);
            decimalFormat.setMaximumFractionDigits(2);
            model.addRow(new Object[]{"BRS/BR", stok_BRS_WLT, jumlah_cabut_borong, 20, 8, (jumlah_cabut_borong * 20 * 8), decimalFormat.format(ketahanan_BRS)});
            model.addRow(new Object[]{"BR", stok_BR_WLT, jumlah_cabut_borong, 20, 8, (jumlah_cabut_borong * 20 * 8), decimalFormat.format(ketahanan_BR)});
            model.addRow(new Object[]{"BS", stok_BS_WLT, jumlah_cabut_borong, 13, 8, (jumlah_cabut_borong * 13 * 8), decimalFormat.format(ketahanan_BS)});
            model.addRow(new Object[]{"Other", stok_BuluLain_WLT, jumlah_cabut_borong, 16, 8, (jumlah_cabut_borong * 16 * 8), decimalFormat.format(ketahanan_Other)});
            model.addRow(new Object[]{"TOTAL", (stok_BRS_WLT + stok_BR_WLT + stok_BS_WLT + stok_BuluLain_WLT), null, null, null, null, decimalFormat.format(ketahanan_BRS + ketahanan_BR + ketahanan_BS + ketahanan_Other)});

            float ketahanan_waleta = ketahanan_BRS + ketahanan_BR + ketahanan_BS + ketahanan_Other;
            Date END_WLT = Utility.addDaysSkippingSundays(new Date(), Math.round(ketahanan_waleta));
            txt_tanggal_end_waleta.setText(new SimpleDateFormat("dd MMMM yyyy").format(END_WLT));
            ColumnsAutoSizer.sizeColumnsToFit(table_ketahanan_stok_waleta);
        } catch (NumberFormatException | SQLException e) {
            Logger.getLogger(JFrame_TV_GradingBaku.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void Refresh_grading_on_proses() {
        try {
            DefaultTableModel model_on_proses = (DefaultTableModel) table_on_proses.getModel();
            model_on_proses.setRowCount(0);
            int total_berat_onproses = 0;
            sql = "SELECT `no_kartu_waleta`, `tgl_masuk`, `tb_supplier`.`nama_supplier`, `berat_awal`, DATEDIFF(CURRENT_DATE, `tgl_masuk`) AS 'hari' FROM `tb_bahan_baku_masuk` "
                    + "LEFT JOIN `tb_supplier` ON `tb_bahan_baku_masuk`.`kode_supplier` = `tb_supplier`.`kode_supplier` WHERE `tgl_timbang` IS NULL AND `berat_awal` > 0 ORDER BY `hari` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] baris = new Object[6];
            int no = 0;
            while (rs.next()) {
                no++;
                baris[0] = no;
                baris[1] = rs.getString("no_kartu_waleta");
                baris[2] = rs.getString("tgl_masuk");
                baris[3] = rs.getString("nama_supplier");
                baris[4] = decimalFormat.format(rs.getFloat("berat_awal"));
                baris[5] = rs.getInt("hari");
                total_berat_onproses = total_berat_onproses + rs.getInt("berat_awal");
                model_on_proses.addRow(baris);
            }
            txt_total_proses_grading.setText(decimalFormat.format(total_berat_onproses));
            table_on_proses.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//                    if (column == 4) {
                    if ((int) table_on_proses.getValueAt(row, 5) > 7) {
                        if (isSelected) {
                            comp.setBackground(table_on_proses.getSelectionBackground());
                            comp.setForeground(table_on_proses.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.red);
                            comp.setForeground(Color.WHITE);
                        }
                    } else if ((int) table_on_proses.getValueAt(row, 5) > 6) {
                        if (isSelected) {
                            comp.setBackground(table_on_proses.getSelectionBackground());
                            comp.setForeground(table_on_proses.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.YELLOW);
                            comp.setForeground(table_on_proses.getForeground());
                        }
                    } else if ((int) table_on_proses.getValueAt(row, 5) <= 6) {
                        if (isSelected) {
                            comp.setBackground(table_on_proses.getSelectionBackground());
                            comp.setForeground(table_on_proses.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.green);
                            comp.setForeground(table_on_proses.getForeground());
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(table_on_proses.getSelectionBackground());
                            comp.setForeground(table_on_proses.getSelectionForeground());
                        } else {
                            comp.setBackground(table_on_proses.getBackground());
                            comp.setForeground(table_on_proses.getForeground());
                        }
                    }
                    return comp;
                }
            });
            table_on_proses.repaint();
            table_on_proses.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if ((int) table_on_proses.getValueAt(row, 5) > 7) {
                        if (isSelected) {
                            comp.setBackground(table_on_proses.getSelectionBackground());
                            comp.setForeground(table_on_proses.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.red);
                            comp.setForeground(Color.WHITE);
                        }
                    } else if ((int) table_on_proses.getValueAt(row, 5) > 6) {
                        if (isSelected) {
                            comp.setBackground(table_on_proses.getSelectionBackground());
                            comp.setForeground(table_on_proses.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.YELLOW);
                            comp.setForeground(table_on_proses.getForeground());
                        }
                    } else if ((int) table_on_proses.getValueAt(row, 5) <= 6) {
                        if (isSelected) {
                            comp.setBackground(table_on_proses.getSelectionBackground());
                            comp.setForeground(table_on_proses.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.green);
                            comp.setForeground(table_on_proses.getForeground());
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(table_on_proses.getSelectionBackground());
                            comp.setForeground(table_on_proses.getSelectionForeground());
                        } else {
                            comp.setBackground(table_on_proses.getBackground());
                            comp.setForeground(table_on_proses.getForeground());
                        }
                    }
                    return comp;
                }
            });
            table_on_proses.repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataBahanBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Refresh_plan_order() {
        try {
            DefaultTableModel model_on_proses = (DefaultTableModel) table_plan_order.getModel();
            model_on_proses.setRowCount(0);
            int total_berat = 0;
            sql = "SELECT `tb_spk`.`tanggal_tk`, `tb_spk_detail`.`kode_spk`, `tb_buyer`.`nama`, `grade_waleta`, `grade_buyer`, `berat`, `keterangan` FROM `tb_spk_detail` \n"
                    + "LEFT JOIN `tb_spk` ON `tb_spk_detail`.`kode_spk` = `tb_spk`.`kode_spk`\n"
                    + "LEFT JOIN `tb_buyer` ON `tb_spk`.`buyer` = `tb_buyer`.`kode_buyer`\n"
                    + "WHERE `tb_spk`.`tanggal_keluar` >= CURRENT_DATE() AND `keterangan` LIKE '%prioritas%'";
//            System.out.println(sql);
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[8];
            int no = 0;
            while (rs.next()) {
                no++;
                row[0] = no;
                row[1] = dateFormat.format(rs.getDate("tanggal_tk"));
                row[2] = rs.getString("kode_spk");
                row[3] = rs.getString("nama");
                row[4] = rs.getString("grade_waleta");
                row[5] = rs.getString("grade_buyer");
                row[6] = rs.getFloat("berat");
                row[7] = rs.getString("keterangan");
                total_berat = total_berat + rs.getInt("berat");
                model_on_proses.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_plan_order);
            txt_total_berat_plan_order.setText(decimalFormat.format(total_berat));
        } catch (SQLException ex) {
            Logger.getLogger(JFrame_TV_GradingBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Refresh_plan_order_stok() {
        decimalFormat.setMaximumFractionDigits(0);
        int kpg_masuk = 0, gram_masuk = 0, kpg_lp = 0, gram_lp = 0, kpg_jual = 0, gram_keluar = 0, kpg_cmp = 0, gram_cmp = 0;
        float stok_plan_order = 0, stok_kpg_utuh = 0, stok_gram_utuh = 0;
        ResultSet rs_grading, rs_jual, rs_lp, rs_cmp;
        try {
            DefaultTableModel model = (DefaultTableModel) table_plan_order_stok.getModel();
            model.setRowCount(0);
            sql = "SELECT DISTINCT(`kategori`) AS 'kategori' FROM `tb_grade_bahan_baku` WHERE `kategori` IN ('MK A', 'MK B', 'SGTG', 'OVL', 'FLAT', 'JDN')";
            pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            Object[] row = new Object[4];
            while (rs.next()) {
                String sql_grading = "SELECT SUM(`tb_grading_bahan_baku`.`jumlah_keping`) AS 'jumlah_keping', SUM(`tb_grading_bahan_baku`.`total_berat`) AS 'total_berat' "
                        + "FROM `tb_grading_bahan_baku` LEFT JOIN `tb_bahan_baku_masuk` ON `tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`"
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_grading_bahan_baku`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`"
                        + "WHERE `tb_grade_bahan_baku`.`kategori` = '" + rs.getString("kategori") + "' "
                        + "AND `tgl_masuk`<=CURRENT_DATE()";
                rs_grading = Utility.db.getStatement().executeQuery(sql_grading);
                if (rs_grading.next()) {
                    kpg_masuk = rs_grading.getInt("jumlah_keping");
                    gram_masuk = rs_grading.getInt("total_berat");
                }
                String sql_lp = "SELECT SUM(`jumlah_keping`) AS 'keping', SUM(`berat_basah`) AS 'berat' FROM `tb_laporan_produksi` "
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`"
                        + "WHERE `tb_grade_bahan_baku`.`kategori` = '" + rs.getString("kategori") + "' "
                        + "AND `tanggal_lp`<=CURRENT_DATE()";
                rs_lp = Utility.db.getStatement().executeQuery(sql_lp);
                if (rs_lp.next()) {
                    kpg_lp = rs_lp.getInt("keping");
                    gram_lp = rs_lp.getInt("berat");
                }
                String sql_jual = "SELECT SUM(`total_keping_keluar`) AS 'keping', SUM(`total_berat_keluar`) AS 'berat' "
                        + "FROM `tb_bahan_baku_keluar` LEFT JOIN `tb_bahan_baku_keluar1` ON `tb_bahan_baku_keluar`.`kode_pengeluaran` = `tb_bahan_baku_keluar1`.`kode_pengeluaran` "
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_bahan_baku_keluar`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`"
                        + "WHERE `tb_grade_bahan_baku`.`kategori` = '" + rs.getString("kategori") + "' "
                        + "AND `tgl_keluar`<=CURRENT_DATE()";
                rs_jual = Utility.db.getStatement().executeQuery(sql_jual);
                if (rs_jual.next()) {
                    kpg_jual = rs_jual.getInt("keping");
                    gram_keluar = rs_jual.getInt("berat");
                }
                String sql_cmp = "SELECT SUM(`keping`) AS 'keping_cmp', SUM(`gram`) AS 'berat_cmp' FROM `tb_kartu_cmp_detail`\n"
                        + "LEFT JOIN `tb_kartu_cmp` ON `tb_kartu_cmp_detail`.`kode_kartu_cmp` = `tb_kartu_cmp`.`kode_kartu_cmp`"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_kartu_cmp_detail`.`no_grading` = `tb_grading_bahan_baku`.`no_grading`\n"
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_grading_bahan_baku`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`"
                        + "WHERE `tb_grade_bahan_baku`.`kategori` = '" + rs.getString("kategori") + "' "
                        + "AND `tanggal`<=CURRENT_DATE()";
                rs_cmp = Utility.db.getStatement().executeQuery(sql_cmp);
                if (rs_cmp.next()) {
                    kpg_cmp = rs_cmp.getInt("keping_cmp");
                    gram_cmp = rs_cmp.getInt("berat_cmp");
                }

                float kpg_sisa = kpg_masuk - (kpg_lp + kpg_jual + kpg_cmp);
                float gram_sisa = gram_masuk - (gram_lp + gram_keluar + gram_cmp);

                row[0] = rs.getString("kategori");
                row[1] = kpg_sisa;
                row[2] = gram_sisa;
                row[3] = 0;
                stok_plan_order = stok_plan_order + gram_sisa;
                if (rs.getString("kategori").equals("FLAT") || rs.getString("kategori").equals("JDN")) {
                    model.addRow(row);
                } else {
                    stok_kpg_utuh = stok_kpg_utuh + kpg_sisa;
                    stok_gram_utuh = stok_gram_utuh + gram_sisa;
                }
            }
            row[0] = "UTUH";
            row[1] = stok_kpg_utuh;
            row[2] = stok_gram_utuh;
            row[3] = 0;
            model.addRow(row);
            for (int i = 0; i < table_plan_order_stok.getRowCount(); i++) {
                float gram = (float) table_plan_order_stok.getValueAt(i, 2);
                float persen = Math.round((gram / stok_plan_order) * 10000.f) / 100.f;
                table_plan_order_stok.setValueAt(persen, i, 3);
            }
            txt_total_berat_plan_order_stok.setText(decimalFormat.format(stok_plan_order));
            ColumnsAutoSizer.sizeColumnsToFit(table_plan_order_stok);
        } catch (Exception ex) {
            Logger.getLogger(JFrame_TV_GradingBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTabel_hold_baku() {
        try {
            DefaultTableModel model1 = (DefaultTableModel) Table_Data_Nitrit_baku1.getModel();
            model1.setRowCount(0);
            DefaultTableModel model2 = (DefaultTableModel) Table_Data_Nitrit_baku2.getModel();
            model2.setRowCount(0);
            DefaultTableModel model3 = (DefaultTableModel) Table_Data_Nitrit_baku_oren.getModel();
            model3.setRowCount(0);
            DefaultTableModel model4 = (DefaultTableModel) Table_Data_Nitrit_baku_merah.getModel();
            model4.setRowCount(0);
            sql = "SELECT `tb_lab_bahan_baku`.`no_kartu_waleta`, `nitrit_bm_w3` "
                    + "FROM `tb_lab_bahan_baku` LEFT JOIN `tb_bahan_baku_masuk` ON `tb_lab_bahan_baku`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta` "
                    + "WHERE `tb_lab_bahan_baku`.`nitrit_bm_w3` >= 100 AND `tb_bahan_baku_masuk`.`tgl_masuk` BETWEEN ADDDATE(CURRENT_DATE, INTERVAL -1 YEAR) AND CURRENT_DATE "
                    + "ORDER BY `nitrit_bm_w3` DESC";
//            System.out.println(sql);
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[3];
            int no_putih = 0, no_oren = 0, no_merah = 0;
            while (rs.next()) {
                row[0] = rs.getString("no_kartu_waleta");
                row[1] = Math.round(rs.getFloat("nitrit_bm_w3") * 10.f) / 10.f;
                if (rs.getFloat("nitrit_bm_w3") <= 150) {
                    if ((no_putih % 2) == 0) {
                        model1.addRow(row);
                    } else {
                        model2.addRow(row);
                    }
                    no_putih++;
                } else if (rs.getFloat("nitrit_bm_w3") <= 200) {
                    model3.addRow(row);
                    no_oren++;
                } else {
                    model4.addRow(row);
                    no_merah++;
                }
            }

            label_total_kartu_baku_putih.setText(Integer.toString(no_putih));
            label_total_kartu_baku_oren.setText(Integer.toString(no_oren));
            label_total_kartu_baku_merah.setText(Integer.toString(no_merah));
            ColumnsAutoSizer.sizeColumnsToFit(Table_Data_Nitrit_baku1);
            ColumnsAutoSizer.sizeColumnsToFit(Table_Data_Nitrit_baku2);
            ColumnsAutoSizer.sizeColumnsToFit(Table_Data_Nitrit_baku_oren);
            ColumnsAutoSizer.sizeColumnsToFit(Table_Data_Nitrit_baku_merah);

            Table_Data_Nitrit_baku_oren.setDefaultRenderer(Float.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (isSelected) {
                        comp.setBackground(Table_Data_Nitrit_baku_oren.getSelectionBackground());
                        comp.setForeground(Table_Data_Nitrit_baku_oren.getSelectionForeground());
                    } else {
                        comp.setBackground(Color.ORANGE);
                        comp.setForeground(Color.BLACK);
                    }
                    return comp;
                }
            });
            Table_Data_Nitrit_baku_oren.repaint();

            Table_Data_Nitrit_baku_merah.setDefaultRenderer(Float.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (isSelected) {
                        comp.setBackground(Table_Data_Nitrit_baku_merah.getSelectionBackground());
                        comp.setForeground(Table_Data_Nitrit_baku_merah.getSelectionForeground());
                    } else {
                        comp.setBackground(Color.RED);
                        comp.setForeground(Color.WHITE);
                    }
                    return comp;
                }
            });
            Table_Data_Nitrit_baku_merah.repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JFrame_TV_GradingBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshIsu1() {
        try {
            int data = 0;
            sql = "SELECT * FROM `tb_isu_produksi` WHERE `dept` LIKE (`dept` LIKE '%ALL%' OR `dept` LIKE '%GBM%') AND `tanggal_selesai` IS NULL ORDER BY `tanggal_isu` DESC";
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
            Logger.getLogger(JFrame_TV_GradingBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshIsu2() {
        try {
            int data = 0;
            sql = "SELECT * FROM `tb_isu_produksi` WHERE `dept` LIKE (`dept` LIKE '%ALL%' OR `dept` LIKE '%GBM%') AND `tanggal_selesai` IS NULL ORDER BY `tanggal_isu` DESC";
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
            Logger.getLogger(JFrame_TV_GradingBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshIsu3() {
        try {
            int data = 0;
            sql = "SELECT * FROM `tb_isu_produksi` WHERE `dept` LIKE (`dept` LIKE '%ALL%' OR `dept` LIKE '%GBM%') AND `tanggal_selesai` IS NULL ORDER BY `tanggal_isu` DESC";
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
            Logger.getLogger(JFrame_TV_GradingBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshIsu4() {
        try {
            int data = 0;
            sql = "SELECT * FROM `tb_isu_produksi` WHERE `dept` LIKE (`dept` LIKE '%ALL%' OR `dept` LIKE '%GBM%') AND `tanggal_selesai` IS NULL ORDER BY `tanggal_isu` DESC";
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
            Logger.getLogger(JFrame_TV_GradingBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshIsu5() {
        try {
            int data = 0;
            sql = "SELECT * FROM `tb_isu_produksi` WHERE `dept` LIKE (`dept` LIKE '%ALL%' OR `dept` LIKE '%GBM%') AND `tanggal_selesai` IS NULL ORDER BY `tanggal_isu` DESC";
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
            Logger.getLogger(JFrame_TV_GradingBaku.class.getName()).log(Level.SEVERE, null, ex);
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
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel_on_proses_grading = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        table_grading = new javax.swing.JTable();
        jLabel55 = new javax.swing.JLabel();
        txt_total_berat = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        jPanel_ketahanan_stok = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel63 = new javax.swing.JLabel();
        txt_tanggal_end_waleta = new javax.swing.JLabel();
        jScrollPane23 = new javax.swing.JScrollPane();
        table_stok_for_waleta1 = new javax.swing.JTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        table_stok_for_waleta = new javax.swing.JTable();
        jScrollPane19 = new javax.swing.JScrollPane();
        table_ketahanan_stok_waleta = new javax.swing.JTable();
        jLabel56 = new javax.swing.JLabel();
        txt_stok_waleta1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel58 = new javax.swing.JLabel();
        txt_stok_bp1 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        txt_stok_unworkable1 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        table_stok_for_bp = new javax.swing.JTable();
        jScrollPane18 = new javax.swing.JScrollPane();
        table_stok_for_jual = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jLabel62 = new javax.swing.JLabel();
        txt_tanggal_end_sub = new javax.swing.JLabel();
        jScrollPane17 = new javax.swing.JScrollPane();
        table_stok_for_sub = new javax.swing.JTable();
        txt_total_proses_grading = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        txt_pengeluaran_sub = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        txt_stok_sub1 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        txt_ketahanan_sub = new javax.swing.JLabel();
        jScrollPane13 = new javax.swing.JScrollPane();
        table_on_proses = new javax.swing.JTable();
        jLabel66 = new javax.swing.JLabel();
        jScrollPane24 = new javax.swing.JScrollPane();
        table_stok_for_sub1 = new javax.swing.JTable();
        jPanel_shipment_order = new javax.swing.JPanel();
        jScrollPane12 = new javax.swing.JScrollPane();
        table_plan_order = new javax.swing.JTable();
        jLabel67 = new javax.swing.JLabel();
        txt_total_berat_plan_order = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        jLabel70 = new javax.swing.JLabel();
        txt_total_berat_plan_order_stok = new javax.swing.JLabel();
        jScrollPane14 = new javax.swing.JScrollPane();
        table_plan_order_stok = new javax.swing.JTable();
        jPanel_nitrit_baku = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane15 = new javax.swing.JScrollPane();
        Table_Data_Nitrit_baku1 = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        label_total_kartu_baku_putih = new javax.swing.JLabel();
        jScrollPane16 = new javax.swing.JScrollPane();
        Table_Data_Nitrit_baku2 = new javax.swing.JTable();
        jScrollPane20 = new javax.swing.JScrollPane();
        Table_Data_Nitrit_baku_oren = new javax.swing.JTable();
        jScrollPane21 = new javax.swing.JScrollPane();
        Table_Data_Nitrit_baku_merah = new javax.swing.JTable();
        label_total_kartu_baku_oren = new javax.swing.JLabel();
        label_total_kartu_baku_merah = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane22 = new javax.swing.JScrollPane();
        Table_Data_Nitrit_baku_keterangan = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea6 = new javax.swing.JTextArea();
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
        jScrollPane8 = new javax.swing.JScrollPane();
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
        jScrollPane9 = new javax.swing.JScrollPane();
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
        jScrollPane7 = new javax.swing.JScrollPane();
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
        jScrollPane10 = new javax.swing.JScrollPane();
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
        jScrollPane11 = new javax.swing.JScrollPane();
        jTextArea5 = new javax.swing.JTextArea();
        label_jam = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });

        jPanel_on_proses_grading.setBackground(new java.awt.Color(255, 255, 255));

        table_grading.setAutoCreateRowSorter(true);
        table_grading.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        table_grading.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "No Kartu", "Asal", "Gram Awal", "Waktu Grading"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
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
        table_grading.setFocusable(false);
        table_grading.setRowHeight(35);
        table_grading.setRowSelectionAllowed(false);
        table_grading.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(table_grading);
        if (table_grading.getColumnModel().getColumnCount() > 0) {
            table_grading.getColumnModel().getColumn(0).setMinWidth(50);
            table_grading.getColumnModel().getColumn(0).setMaxWidth(50);
        }

        jLabel55.setBackground(new java.awt.Color(255, 255, 255));
        jLabel55.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel55.setForeground(new java.awt.Color(255, 0, 0));
        jLabel55.setText("ON PROSES GRADING");

        txt_total_berat.setBackground(new java.awt.Color(255, 255, 255));
        txt_total_berat.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        txt_total_berat.setText("0");

        jLabel65.setBackground(new java.awt.Color(255, 255, 255));
        jLabel65.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel65.setText("Total Berat :");

        javax.swing.GroupLayout jPanel_on_proses_gradingLayout = new javax.swing.GroupLayout(jPanel_on_proses_grading);
        jPanel_on_proses_grading.setLayout(jPanel_on_proses_gradingLayout);
        jPanel_on_proses_gradingLayout.setHorizontalGroup(
            jPanel_on_proses_gradingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_on_proses_gradingLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_on_proses_gradingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 1371, Short.MAX_VALUE)
                    .addGroup(jPanel_on_proses_gradingLayout.createSequentialGroup()
                        .addComponent(jLabel55)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel65)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_total_berat)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel_on_proses_gradingLayout.setVerticalGroup(
            jPanel_on_proses_gradingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_on_proses_gradingLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_on_proses_gradingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_on_proses_gradingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel65)
                        .addComponent(txt_total_berat))
                    .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 629, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("PROSES GRADING", jPanel_on_proses_grading);

        jPanel_ketahanan_stok.setBackground(new java.awt.Color(255, 255, 255));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jLabel63.setBackground(new java.awt.Color(255, 255, 255));
        jLabel63.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel63.setText("Tanggal perkiraan stok habis :");

        txt_tanggal_end_waleta.setBackground(new java.awt.Color(255, 255, 255));
        txt_tanggal_end_waleta.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        txt_tanggal_end_waleta.setText("0");

        table_stok_for_waleta1.setAutoCreateRowSorter(true);
        table_stok_for_waleta1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        table_stok_for_waleta1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"MANGKOK",  new Float(0.0),  new Float(0.0),  new Float(0.0)},
                {"PECAH",  new Float(0.0),  new Float(0.0),  new Float(0.0)},
                {"OVAL",  new Float(0.0),  new Float(0.0),  new Float(0.0)},
                {"SEGITIGA",  new Float(0.0),  new Float(0.0),  new Float(0.0)}
            },
            new String [] {
                "Jenis Bentuk", "Stok Keping", "Stok Gram", "%"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        table_stok_for_waleta1.setRowHeight(20);
        table_stok_for_waleta1.getTableHeader().setReorderingAllowed(false);
        jScrollPane23.setViewportView(table_stok_for_waleta1);

        table_stok_for_waleta.setAutoCreateRowSorter(true);
        table_stok_for_waleta.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        table_stok_for_waleta.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Jenis Bentuk", "Jenis Bulu", "Stok Keping", "Stok Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class
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
        table_stok_for_waleta.setRowHeight(20);
        table_stok_for_waleta.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(table_stok_for_waleta);

        table_ketahanan_stok_waleta.setAutoCreateRowSorter(true);
        table_ketahanan_stok_waleta.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        table_ketahanan_stok_waleta.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Jenis Bulu", "Stok (Gram)", "Anak CBT", "Kpg/Anak", "Gr/Kpg", "Max Load/hari", "Ketahanan (Hari)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class, java.lang.Integer.class
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
        table_ketahanan_stok_waleta.setRowHeight(20);
        table_ketahanan_stok_waleta.getTableHeader().setReorderingAllowed(false);
        jScrollPane19.setViewportView(table_ketahanan_stok_waleta);

        jLabel56.setBackground(new java.awt.Color(255, 255, 255));
        jLabel56.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel56.setText("WORKABLE MATERIAL FOR WALETA : ");

        txt_stok_waleta1.setBackground(new java.awt.Color(255, 255, 255));
        txt_stok_waleta1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        txt_stok_waleta1.setText("0");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane19, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
                    .addComponent(jScrollPane5)
                    .addComponent(jScrollPane23, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel63)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_tanggal_end_waleta))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel56)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_stok_waleta1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_stok_waleta1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane23, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane19, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel63)
                    .addComponent(txt_tanggal_end_waleta))
                .addContainerGap())
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        jLabel58.setBackground(new java.awt.Color(255, 255, 255));
        jLabel58.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel58.setText("UNWORKABLE MATERIAL : ");

        txt_stok_bp1.setBackground(new java.awt.Color(255, 255, 255));
        txt_stok_bp1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        txt_stok_bp1.setText("0");

        jLabel57.setBackground(new java.awt.Color(255, 255, 255));
        jLabel57.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel57.setText("WORKABLE BY PRODUCT : ");

        txt_stok_unworkable1.setBackground(new java.awt.Color(255, 255, 255));
        txt_stok_unworkable1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        txt_stok_unworkable1.setText("0");

        table_stok_for_bp.setAutoCreateRowSorter(true);
        table_stok_for_bp.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        table_stok_for_bp.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade", "Stok Keping", "Stok Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class
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
        table_stok_for_bp.setRowHeight(20);
        table_stok_for_bp.getTableHeader().setReorderingAllowed(false);
        jScrollPane6.setViewportView(table_stok_for_bp);

        table_stok_for_jual.setAutoCreateRowSorter(true);
        table_stok_for_jual.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        table_stok_for_jual.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade", "Stok Keping", "Stok Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class
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
        table_stok_for_jual.setRowHeight(20);
        table_stok_for_jual.getTableHeader().setReorderingAllowed(false);
        jScrollPane18.setViewportView(table_stok_for_jual);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
                    .addComponent(jScrollPane18, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel57)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_stok_bp1))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel58)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_stok_unworkable1)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_stok_bp1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel58, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_stok_unworkable1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane18, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        jLabel62.setBackground(new java.awt.Color(255, 255, 255));
        jLabel62.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel62.setText("Tanggal perkiraan stok habis :");

        txt_tanggal_end_sub.setBackground(new java.awt.Color(255, 255, 255));
        txt_tanggal_end_sub.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        txt_tanggal_end_sub.setText("0");

        table_stok_for_sub.setAutoCreateRowSorter(true);
        table_stok_for_sub.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        table_stok_for_sub.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Jenis Bentuk", "Jenis Bulu", "Stok Keping", "Stok Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class
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
        table_stok_for_sub.setRowHeight(20);
        table_stok_for_sub.getTableHeader().setReorderingAllowed(false);
        jScrollPane17.setViewportView(table_stok_for_sub);

        txt_total_proses_grading.setBackground(new java.awt.Color(255, 255, 255));
        txt_total_proses_grading.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        txt_total_proses_grading.setText("0");

        jLabel61.setBackground(new java.awt.Color(255, 255, 255));
        jLabel61.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel61.setText("Ketahanan Sub (Hari) :");

        txt_pengeluaran_sub.setBackground(new java.awt.Color(255, 255, 255));
        txt_pengeluaran_sub.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        txt_pengeluaran_sub.setText("0");

        jLabel59.setBackground(new java.awt.Color(255, 255, 255));
        jLabel59.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel59.setText("BAKU PROSES GRADING");

        txt_stok_sub1.setBackground(new java.awt.Color(255, 255, 255));
        txt_stok_sub1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        txt_stok_sub1.setText("0");

        jLabel60.setBackground(new java.awt.Color(255, 255, 255));
        jLabel60.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel60.setText("Rata2 Pengeluaran Baku untuk SUB :");

        jLabel64.setBackground(new java.awt.Color(255, 255, 255));
        jLabel64.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel64.setText("Total Berat :");

        txt_ketahanan_sub.setBackground(new java.awt.Color(255, 255, 255));
        txt_ketahanan_sub.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        txt_ketahanan_sub.setText("0");

        table_on_proses.setAutoCreateRowSorter(true);
        table_on_proses.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        table_on_proses.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "No Kartu", "Tgl Masuk", "Supplier", "Berat (Gr)", "Hari"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
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
        table_on_proses.setRowHeight(20);
        table_on_proses.getTableHeader().setReorderingAllowed(false);
        jScrollPane13.setViewportView(table_on_proses);

        jLabel66.setBackground(new java.awt.Color(255, 255, 255));
        jLabel66.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel66.setText("WORKABLE MATERIAL FOR SUB : ");

        table_stok_for_sub1.setAutoCreateRowSorter(true);
        table_stok_for_sub1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        table_stok_for_sub1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"MANGKOK",  new Float(0.0),  new Float(0.0),  new Float(0.0)},
                {"PECAH",  new Float(0.0),  new Float(0.0),  new Float(0.0)},
                {"OVAL",  new Float(0.0),  new Float(0.0),  new Float(0.0)},
                {"SEGITIGA",  new Float(0.0),  new Float(0.0),  new Float(0.0)}
            },
            new String [] {
                "Jenis Bentuk", "Stok Keping", "Stok Gram", "%"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        table_stok_for_sub1.setRowHeight(20);
        table_stok_for_sub1.getTableHeader().setReorderingAllowed(false);
        jScrollPane24.setViewportView(table_stok_for_sub1);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane17)
                    .addComponent(jScrollPane13)
                    .addComponent(jScrollPane24, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel64)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_total_proses_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel60)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_pengeluaran_sub, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel61)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_ketahanan_sub, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel62)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_tanggal_end_sub))
                            .addComponent(jLabel59)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel66)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_stok_sub1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel66, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_stok_sub1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane17, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane24, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel60)
                    .addComponent(txt_pengeluaran_sub))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel61)
                    .addComponent(txt_ketahanan_sub))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel62)
                    .addComponent(txt_tanggal_end_sub))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel59, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane13, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_total_proses_grading)
                    .addComponent(jLabel64))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel_ketahanan_stokLayout = new javax.swing.GroupLayout(jPanel_ketahanan_stok);
        jPanel_ketahanan_stok.setLayout(jPanel_ketahanan_stokLayout);
        jPanel_ketahanan_stokLayout.setHorizontalGroup(
            jPanel_ketahanan_stokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_ketahanan_stokLayout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel_ketahanan_stokLayout.setVerticalGroup(
            jPanel_ketahanan_stokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_ketahanan_stokLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_ketahanan_stokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jTabbedPane1.addTab("KETAHANAN STOK BAKU", jPanel_ketahanan_stok);

        jPanel_shipment_order.setBackground(new java.awt.Color(255, 255, 255));

        table_plan_order.setAutoCreateRowSorter(true);
        table_plan_order.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        table_plan_order.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Tgl TK", "No SPK", "Buyer", "Grade WLT", "Grade Buyer", "Order", "Keterangan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.String.class
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
        table_plan_order.setFocusable(false);
        table_plan_order.setRowHeight(35);
        table_plan_order.setRowSelectionAllowed(false);
        table_plan_order.getTableHeader().setReorderingAllowed(false);
        jScrollPane12.setViewportView(table_plan_order);
        if (table_plan_order.getColumnModel().getColumnCount() > 0) {
            table_plan_order.getColumnModel().getColumn(0).setMinWidth(50);
            table_plan_order.getColumnModel().getColumn(0).setMaxWidth(50);
            table_plan_order.getColumnModel().getColumn(0).setHeaderValue("No");
            table_plan_order.getColumnModel().getColumn(1).setHeaderValue("Tgl TK");
            table_plan_order.getColumnModel().getColumn(6).setHeaderValue("Order");
            table_plan_order.getColumnModel().getColumn(7).setHeaderValue("Keterangan");
        }

        jLabel67.setBackground(new java.awt.Color(255, 255, 255));
        jLabel67.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel67.setForeground(new java.awt.Color(255, 0, 0));
        jLabel67.setText("PLAN ORDER");

        txt_total_berat_plan_order.setBackground(new java.awt.Color(255, 255, 255));
        txt_total_berat_plan_order.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        txt_total_berat_plan_order.setText("0");

        jLabel68.setBackground(new java.awt.Color(255, 255, 255));
        jLabel68.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel68.setText("Total Berat :");

        jLabel69.setBackground(new java.awt.Color(255, 255, 255));
        jLabel69.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel69.setForeground(new java.awt.Color(255, 0, 0));
        jLabel69.setText("STOK BAKU");

        jLabel70.setBackground(new java.awt.Color(255, 255, 255));
        jLabel70.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel70.setText("Total Berat :");

        txt_total_berat_plan_order_stok.setBackground(new java.awt.Color(255, 255, 255));
        txt_total_berat_plan_order_stok.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        txt_total_berat_plan_order_stok.setText("0");

        table_plan_order_stok.setAutoCreateRowSorter(true);
        table_plan_order_stok.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        table_plan_order_stok.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "KATEGORI", "KPG", "GRAM", "%"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        table_plan_order_stok.setFocusable(false);
        table_plan_order_stok.setRowHeight(35);
        table_plan_order_stok.setRowSelectionAllowed(false);
        table_plan_order_stok.getTableHeader().setReorderingAllowed(false);
        jScrollPane14.setViewportView(table_plan_order_stok);

        javax.swing.GroupLayout jPanel_shipment_orderLayout = new javax.swing.GroupLayout(jPanel_shipment_order);
        jPanel_shipment_order.setLayout(jPanel_shipment_orderLayout);
        jPanel_shipment_orderLayout.setHorizontalGroup(
            jPanel_shipment_orderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_shipment_orderLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_shipment_orderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 1371, Short.MAX_VALUE)
                    .addGroup(jPanel_shipment_orderLayout.createSequentialGroup()
                        .addGroup(jPanel_shipment_orderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_shipment_orderLayout.createSequentialGroup()
                                .addComponent(jLabel67)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel68)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_total_berat_plan_order))
                            .addGroup(jPanel_shipment_orderLayout.createSequentialGroup()
                                .addComponent(jLabel69)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel70)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_total_berat_plan_order_stok)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane14, javax.swing.GroupLayout.DEFAULT_SIZE, 1371, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel_shipment_orderLayout.setVerticalGroup(
            jPanel_shipment_orderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_shipment_orderLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_shipment_orderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_shipment_orderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel68)
                        .addComponent(txt_total_berat_plan_order))
                    .addComponent(jLabel67, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_shipment_orderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_shipment_orderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel70)
                        .addComponent(txt_total_berat_plan_order_stok))
                    .addComponent(jLabel69, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane14, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("PLAN ORDER & STOK", jPanel_shipment_order);

        jPanel_nitrit_baku.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        jLabel1.setText("NITRIT BAKU");

        Table_Data_Nitrit_baku1.setAutoCreateRowSorter(true);
        Table_Data_Nitrit_baku1.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        Table_Data_Nitrit_baku1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Kartu", "Nitrit BAKU"
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
        Table_Data_Nitrit_baku1.setRowHeight(28);
        Table_Data_Nitrit_baku1.getTableHeader().setReorderingAllowed(false);
        jScrollPane15.setViewportView(Table_Data_Nitrit_baku1);

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        jLabel4.setText("Total Kartu :");

        label_total_kartu_baku_putih.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kartu_baku_putih.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        label_total_kartu_baku_putih.setText("0");

        Table_Data_Nitrit_baku2.setAutoCreateRowSorter(true);
        Table_Data_Nitrit_baku2.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        Table_Data_Nitrit_baku2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Kartu", "Nitrit BAKU"
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
        Table_Data_Nitrit_baku2.setRowHeight(28);
        Table_Data_Nitrit_baku2.getTableHeader().setReorderingAllowed(false);
        jScrollPane16.setViewportView(Table_Data_Nitrit_baku2);

        Table_Data_Nitrit_baku_oren.setAutoCreateRowSorter(true);
        Table_Data_Nitrit_baku_oren.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        Table_Data_Nitrit_baku_oren.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Kartu", "Nitrit BAKU"
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
        Table_Data_Nitrit_baku_oren.setRowHeight(28);
        Table_Data_Nitrit_baku_oren.getTableHeader().setReorderingAllowed(false);
        jScrollPane20.setViewportView(Table_Data_Nitrit_baku_oren);

        Table_Data_Nitrit_baku_merah.setAutoCreateRowSorter(true);
        Table_Data_Nitrit_baku_merah.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        Table_Data_Nitrit_baku_merah.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Kartu", "Nitrit BAKU"
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
        Table_Data_Nitrit_baku_merah.setRowHeight(28);
        Table_Data_Nitrit_baku_merah.getTableHeader().setReorderingAllowed(false);
        jScrollPane21.setViewportView(Table_Data_Nitrit_baku_merah);

        label_total_kartu_baku_oren.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kartu_baku_oren.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        label_total_kartu_baku_oren.setForeground(new java.awt.Color(255, 153, 0));
        label_total_kartu_baku_oren.setText("0");

        label_total_kartu_baku_merah.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kartu_baku_merah.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        label_total_kartu_baku_merah.setForeground(new java.awt.Color(255, 0, 0));
        label_total_kartu_baku_merah.setText("0");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        jLabel9.setText("/");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        jLabel11.setText("/");

        jPanel2.setBackground(new java.awt.Color(204, 204, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Keterangan", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        Table_Data_Nitrit_baku_keterangan.setAutoCreateRowSorter(true);
        Table_Data_Nitrit_baku_keterangan.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        Table_Data_Nitrit_baku_keterangan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"< 100 ppm", "1 Jam"},
                {"101 - 150 ppm", "2 Jam"},
                {"151 - 200 ppm", "3 Jam"},
                {"> 201 ppm", "4 Jam"}
            },
            new String [] {
                "Nitrit Baku", "Waktu Tr OZ"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
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
        Table_Data_Nitrit_baku_keterangan.setRowHeight(28);
        Table_Data_Nitrit_baku_keterangan.getTableHeader().setReorderingAllowed(false);
        jScrollPane22.setViewportView(Table_Data_Nitrit_baku_keterangan);
        if (Table_Data_Nitrit_baku_keterangan.getColumnModel().getColumnCount() > 0) {
            Table_Data_Nitrit_baku_keterangan.getColumnModel().getColumn(0).setMinWidth(50);
        }

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("Setiap penerimaan baku dilakukan ozonisasi selama 3 jam");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("Setiap proses grading dilakukan Tr OZ sesuai nitrit awal baku");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane22, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 401, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane22, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTextArea6.setColumns(20);
        jTextArea6.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jTextArea6.setRows(5);
        jTextArea6.setText("Catatan :\n");
        jScrollPane2.setViewportView(jTextArea6);

        javax.swing.GroupLayout jPanel_nitrit_bakuLayout = new javax.swing.GroupLayout(jPanel_nitrit_baku);
        jPanel_nitrit_baku.setLayout(jPanel_nitrit_bakuLayout);
        jPanel_nitrit_bakuLayout.setHorizontalGroup(
            jPanel_nitrit_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_nitrit_bakuLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_nitrit_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane15, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_nitrit_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_nitrit_bakuLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_kartu_baku_putih)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_kartu_baku_oren)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_kartu_baku_merah)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel_nitrit_bakuLayout.createSequentialGroup()
                        .addComponent(jScrollPane16, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane20, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane21, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_nitrit_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane2))))
                .addContainerGap())
        );
        jPanel_nitrit_bakuLayout.setVerticalGroup(
            jPanel_nitrit_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_nitrit_bakuLayout.createSequentialGroup()
                .addGroup(jPanel_nitrit_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel4)
                    .addComponent(label_total_kartu_baku_putih)
                    .addComponent(label_total_kartu_baku_oren)
                    .addComponent(label_total_kartu_baku_merah)
                    .addComponent(jLabel9)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_nitrit_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane15, javax.swing.GroupLayout.DEFAULT_SIZE, 630, Short.MAX_VALUE)
                    .addComponent(jScrollPane16)
                    .addComponent(jScrollPane20)
                    .addComponent(jScrollPane21)
                    .addGroup(jPanel_nitrit_bakuLayout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2)
                        .addContainerGap())))
        );

        jTabbedPane1.addTab("NITRIT BAKU", jPanel_nitrit_baku);

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

        jScrollPane8.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane8.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(5);
        jTextArea1.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(2);
        jTextArea1.setText("-");
        jTextArea1.setWrapStyleWord(true);
        jScrollPane8.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel_isu1Layout = new javax.swing.GroupLayout(jPanel_isu1);
        jPanel_isu1.setLayout(jPanel_isu1Layout);
        jPanel_isu1Layout.setHorizontalGroup(
            jPanel_isu1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_isu1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_image, javax.swing.GroupLayout.PREFERRED_SIZE, 827, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_isu1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_isu1Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_kode, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_isu1Layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_penanggungjawab, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_isu1Layout.createSequentialGroup()
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_tgl_isu, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_isu1Layout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_departemen, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 508, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel_isu1Layout.setVerticalGroup(
            jPanel_isu1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_isu1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_isu1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_image, javax.swing.GroupLayout.PREFERRED_SIZE, 645, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_isu1Layout.createSequentialGroup()
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        jScrollPane9.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane9.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N

        jTextArea2.setEditable(false);
        jTextArea2.setColumns(5);
        jTextArea2.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jTextArea2.setLineWrap(true);
        jTextArea2.setRows(2);
        jTextArea2.setText("-");
        jTextArea2.setWrapStyleWord(true);
        jScrollPane9.setViewportView(jTextArea2);

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
                    .addComponent(jScrollPane9))
                .addContainerGap())
        );
        jPanel_isu2Layout.setVerticalGroup(
            jPanel_isu2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_isu2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_isu2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_image1, javax.swing.GroupLayout.PREFERRED_SIZE, 645, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_isu2Layout.createSequentialGroup()
                        .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        jScrollPane7.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane7.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N

        jTextArea3.setEditable(false);
        jTextArea3.setColumns(5);
        jTextArea3.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jTextArea3.setLineWrap(true);
        jTextArea3.setRows(2);
        jTextArea3.setText("-");
        jTextArea3.setWrapStyleWord(true);
        jScrollPane7.setViewportView(jTextArea3);

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
                    .addComponent(jScrollPane7))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_isu3Layout.setVerticalGroup(
            jPanel_isu3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_isu3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_isu3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_image2, javax.swing.GroupLayout.PREFERRED_SIZE, 645, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_isu3Layout.createSequentialGroup()
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        jScrollPane10.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane10.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N

        jTextArea4.setEditable(false);
        jTextArea4.setColumns(5);
        jTextArea4.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jTextArea4.setLineWrap(true);
        jTextArea4.setRows(2);
        jTextArea4.setText("-");
        jTextArea4.setWrapStyleWord(true);
        jScrollPane10.setViewportView(jTextArea4);

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
                    .addComponent(jScrollPane10))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_isu4Layout.setVerticalGroup(
            jPanel_isu4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_isu4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_isu4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_image3, javax.swing.GroupLayout.PREFERRED_SIZE, 645, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_isu4Layout.createSequentialGroup()
                        .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        jScrollPane11.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane11.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N

        jTextArea5.setEditable(false);
        jTextArea5.setColumns(5);
        jTextArea5.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jTextArea5.setLineWrap(true);
        jTextArea5.setRows(2);
        jTextArea5.setText("-");
        jTextArea5.setWrapStyleWord(true);
        jScrollPane11.setViewportView(jTextArea5);

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
                    .addComponent(jScrollPane11))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_isu5Layout.setVerticalGroup(
            jPanel_isu5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_isu5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_isu5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_image4, javax.swing.GroupLayout.PREFERRED_SIZE, 645, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_isu5Layout.createSequentialGroup()
                        .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        label_jam.setBackground(new java.awt.Color(255, 255, 255));
        label_jam.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        label_jam.setText("JAM");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_jam)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_jam)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
        clock.stop();
        t.cancel();
        detik = 0;
    }//GEN-LAST:event_formWindowClosed

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
        // TODO add your handling code here:
        switch (jTabbedPane1.getSelectedIndex()) {
            case 0:
                Refresh_proses_grading();
                break;
            case 1:
                refresh_ketahanan_baku();
                break;
            case 2:
                Refresh_plan_order();
                Refresh_plan_order_stok();
                break;
            case 3:
                refreshTabel_hold_baku();
                break;
            case 4:
                refreshIsu1();
                break;
            case 5:
                refreshIsu2();
                break;
            case 6:
                refreshIsu3();
                break;
            case 7:
                refreshIsu4();
                break;
            case 8:
                refreshIsu5();
                break;
            default:
                Refresh_proses_grading();
                break;
        }
    }//GEN-LAST:event_jTabbedPane1StateChanged

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JFrame_TV_GradingBaku.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFrame_TV_GradingBaku.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFrame_TV_GradingBaku.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrame_TV_GradingBaku.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFrame_TV_GradingBaku chart = new JFrame_TV_GradingBaku();
                chart.pack();
                chart.setResizable(true);
                chart.setLocationRelativeTo(null);
                chart.setVisible(true);
                chart.setEnabled(true);
                chart.setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JTable Table_Data_Nitrit_baku1;
    public static javax.swing.JTable Table_Data_Nitrit_baku2;
    public static javax.swing.JTable Table_Data_Nitrit_baku_keterangan;
    public static javax.swing.JTable Table_Data_Nitrit_baku_merah;
    public static javax.swing.JTable Table_Data_Nitrit_baku_oren;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
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
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel_isu1;
    private javax.swing.JPanel jPanel_isu2;
    private javax.swing.JPanel jPanel_isu3;
    private javax.swing.JPanel jPanel_isu4;
    private javax.swing.JPanel jPanel_isu5;
    private javax.swing.JPanel jPanel_ketahanan_stok;
    private javax.swing.JPanel jPanel_nitrit_baku;
    private javax.swing.JPanel jPanel_on_proses_grading;
    private javax.swing.JPanel jPanel_shipment_order;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane16;
    private javax.swing.JScrollPane jScrollPane17;
    private javax.swing.JScrollPane jScrollPane18;
    private javax.swing.JScrollPane jScrollPane19;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane20;
    private javax.swing.JScrollPane jScrollPane21;
    private javax.swing.JScrollPane jScrollPane22;
    private javax.swing.JScrollPane jScrollPane23;
    private javax.swing.JScrollPane jScrollPane24;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextArea jTextArea3;
    private javax.swing.JTextArea jTextArea4;
    private javax.swing.JTextArea jTextArea5;
    private javax.swing.JTextArea jTextArea6;
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
    private javax.swing.JLabel label_jam;
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
    private javax.swing.JLabel label_total_kartu_baku_merah;
    private javax.swing.JLabel label_total_kartu_baku_oren;
    private javax.swing.JLabel label_total_kartu_baku_putih;
    private javax.swing.JTable table_grading;
    private javax.swing.JTable table_ketahanan_stok_waleta;
    private javax.swing.JTable table_on_proses;
    private javax.swing.JTable table_plan_order;
    private javax.swing.JTable table_plan_order_stok;
    private javax.swing.JTable table_stok_for_bp;
    private javax.swing.JTable table_stok_for_jual;
    private javax.swing.JTable table_stok_for_sub;
    private javax.swing.JTable table_stok_for_sub1;
    private javax.swing.JTable table_stok_for_waleta;
    private javax.swing.JTable table_stok_for_waleta1;
    private javax.swing.JLabel txt_ketahanan_sub;
    private javax.swing.JLabel txt_pengeluaran_sub;
    private javax.swing.JLabel txt_stok_bp1;
    private javax.swing.JLabel txt_stok_sub1;
    private javax.swing.JLabel txt_stok_unworkable1;
    private javax.swing.JLabel txt_stok_waleta1;
    private javax.swing.JLabel txt_tanggal_end_sub;
    private javax.swing.JLabel txt_tanggal_end_waleta;
    private javax.swing.JLabel txt_total_berat;
    private javax.swing.JLabel txt_total_berat_plan_order;
    private javax.swing.JLabel txt_total_berat_plan_order_stok;
    private javax.swing.JLabel txt_total_proses_grading;
    // End of variables declaration//GEN-END:variables
}
