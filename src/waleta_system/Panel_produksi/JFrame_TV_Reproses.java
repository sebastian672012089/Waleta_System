package waleta_system.Panel_produksi;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;

public class JFrame_TV_Reproses extends javax.swing.JFrame {

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

    public JFrame_TV_Reproses() {
        initComponents();
        init();
    }

    public void init() {
        try {
            detik = 0;
            menit = 0;
            jam = 0;
            refreshTabel_setoran_cetak();

            TimerTask timer;
            timer = new TimerTask() {
                @Override
                public void run() {
                    boolean changeTab = true;
//                    label_waktu.setText(jam + " : " + menit + " : " + detik);
                    if (detik == 0) {
                        while (changeTab) {
                            jTabbedPane1.setSelectedIndex(tab);
                            if (tab == 7 && label_kode.getText().equals("-")) {
                                tab = 8;
                            } else if (tab == 8 && label_kode1.getText().equals("-")) {
                                tab = 9;
                            } else if (tab == 9 && label_kode2.getText().equals("-")) {
                                tab = 10;
                            } else if (tab == 10 && label_kode3.getText().equals("-")) {
                                tab = 11;
                            } else if (tab == 11 && label_kode4.getText().equals("-")) {
                                tab = 12;
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
            Logger.getLogger(JPanel_ProgressLP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTabel_setoran_cetak() {
        //TABEL 1
        try {
            DefaultTableModel model = (DefaultTableModel) Table_Setoran_cetak1.getModel();
            model.setRowCount(0);
            sql = "SELECT `tgl_selesai_cetak`, `tb_laporan_produksi`.`ruangan`, COUNT(`tb_cetak`.`no_laporan_produksi`) AS 'jumlah_lp', SUM(`cetak_mangkok`) AS 'ctk_mk', SUM(`cetak_pecah`) AS 'ctk_pch', SUM(`cetak_flat`) AS 'ctk_flat', SUM(`cetak_jidun_real`) AS 'ctk_jdn' \n"
                    + "FROM `tb_cetak` "
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "WHERE "
                    + "`tgl_selesai_cetak` = (SELECT `tgl_selesai_cetak` FROM `tb_cetak` WHERE `tgl_selesai_cetak` <> CURRENT_DATE \n"
                    + "GROUP BY `tgl_selesai_cetak` ORDER BY `tgl_selesai_cetak` DESC LIMIT 1)\n"
                    + "GROUP BY `tb_laporan_produksi`.`ruangan` WITH ROLLUP";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[6];
            while (rs.next()) {
                label_setoran_cetak1.setText("CTK H-1 : " + new SimpleDateFormat("dd-MMM").format(rs.getDate("tgl_selesai_cetak")));
                row[0] = rs.getString("ruangan");
                row[1] = rs.getInt("ctk_mk");
                row[2] = rs.getInt("ctk_pch");
                row[3] = rs.getInt("ctk_flat");
                row[4] = rs.getInt("ctk_jdn");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_Setoran_cetak1);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JFrame_TV_Reproses.class.getName()).log(Level.SEVERE, null, ex);
        }
        //TABEL 2
        try {
            DefaultTableModel model = (DefaultTableModel) Table_Setoran_cetak2.getModel();
            model.setRowCount(0);
            sql = "SELECT `tgl_dikerjakan_f2`, `tb_laporan_produksi`.`ruangan`, COUNT(`tb_cetak`.`no_laporan_produksi`) AS 'jumlah_lp', SUM(`cetak_mangkok`) AS 'ctk_mk', SUM(`cetak_pecah`) AS 'ctk_pch', SUM(`cetak_flat`) AS 'ctk_flat', SUM(`cetak_jidun_real`) AS 'ctk_jdn' \n"
                    + "FROM `tb_cetak` "
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_finishing_2` ON `tb_cetak`.`no_laporan_produksi` = `tb_finishing_2`.`no_laporan_produksi`\n"
                    + "WHERE "
                    + "`tgl_dikerjakan_f2` = (SELECT `tgl_dikerjakan_f2` FROM `tb_finishing_2` WHERE `tgl_dikerjakan_f2` <> CURRENT_DATE \n"
                    + "GROUP BY `tgl_dikerjakan_f2` ORDER BY `tgl_dikerjakan_f2` DESC LIMIT 1)\n"
                    + "GROUP BY `tb_laporan_produksi`.`ruangan` WITH ROLLUP";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[6];
            while (rs.next()) {
                label_setoran_cetak2.setText("Tgl Koreksi : " + new SimpleDateFormat("dd-MMM").format(rs.getDate("tgl_dikerjakan_f2")));
                row[0] = rs.getString("ruangan");
                row[1] = rs.getInt("ctk_mk");
                row[2] = rs.getInt("ctk_pch");
                row[3] = rs.getInt("ctk_flat");
                row[4] = rs.getInt("ctk_jdn");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_Setoran_cetak2);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JFrame_TV_Reproses.class.getName()).log(Level.SEVERE, null, ex);
        }
        //TABEL 3
        try {
            DefaultTableModel model = (DefaultTableModel) Table_Setoran_cetak3.getModel();
            model.setRowCount(0);
            sql = "SELECT `tgl_f1`, `tb_laporan_produksi`.`ruangan`, COUNT(`tb_cetak`.`no_laporan_produksi`) AS 'jumlah_lp', SUM(`cetak_mangkok`) AS 'ctk_mk', SUM(`cetak_pecah`) AS 'ctk_pch', SUM(`cetak_flat`) AS 'ctk_flat', SUM(`cetak_jidun_real`) AS 'ctk_jdn' \n"
                    + "FROM `tb_cetak` "
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_finishing_2` ON `tb_cetak`.`no_laporan_produksi` = `tb_finishing_2`.`no_laporan_produksi`\n"
                    + "WHERE "
                    + "`tgl_f1` = (SELECT `tgl_f1` FROM `tb_finishing_2` WHERE `tgl_f1` <> CURRENT_DATE \n"
                    + "GROUP BY `tgl_f1` ORDER BY `tgl_f1` DESC LIMIT 1)\n"
                    + "GROUP BY `tb_laporan_produksi`.`ruangan` WITH ROLLUP";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[6];
            while (rs.next()) {
                label_setoran_cetak3.setText("Tgl F1 : " + new SimpleDateFormat("dd-MMM").format(rs.getDate("tgl_f1")));
                row[0] = rs.getString("ruangan");
                row[1] = rs.getInt("ctk_mk");
                row[2] = rs.getInt("ctk_pch");
                row[3] = rs.getInt("ctk_flat");
                row[4] = rs.getInt("ctk_jdn");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_Setoran_cetak3);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JFrame_TV_Reproses.class.getName()).log(Level.SEVERE, null, ex);
        }
        //TABEL 4
        try {
            DefaultTableModel model = (DefaultTableModel) Table_Setoran_cetak4.getModel();
            model.setRowCount(0);
            sql = "SELECT `tgl_f2`, `tb_laporan_produksi`.`ruangan`, COUNT(`tb_cetak`.`no_laporan_produksi`) AS 'jumlah_lp', SUM(`cetak_mangkok`) AS 'ctk_mk', SUM(`cetak_pecah`) AS 'ctk_pch', SUM(`cetak_flat`) AS 'ctk_flat', SUM(`cetak_jidun_real`) AS 'ctk_jdn' \n"
                    + "FROM `tb_cetak` "
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_finishing_2` ON `tb_cetak`.`no_laporan_produksi` = `tb_finishing_2`.`no_laporan_produksi`\n"
                    + "WHERE "
                    + "`tgl_f2` = (SELECT `tgl_f2` FROM `tb_finishing_2` WHERE `tgl_f2` <> CURRENT_DATE \n"
                    + "GROUP BY `tgl_f2` ORDER BY `tgl_f2` DESC LIMIT 1)\n"
                    + "GROUP BY `tb_laporan_produksi`.`ruangan` WITH ROLLUP";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[6];
            while (rs.next()) {
                label_setoran_cetak4.setText("Tgl F2 : " + new SimpleDateFormat("dd-MMM").format(rs.getDate("tgl_f2")));
                row[0] = rs.getString("ruangan");
                row[1] = rs.getInt("ctk_mk");
                row[2] = rs.getInt("ctk_pch");
                row[3] = rs.getInt("ctk_flat");
                row[4] = rs.getInt("ctk_jdn");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_Setoran_cetak4);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JFrame_TV_Reproses.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_f2() {
        try {
            DefaultTableModel model1 = (DefaultTableModel) Table_WIP_F1.getModel();
            model1.setRowCount(0);
            DefaultTableModel model2 = (DefaultTableModel) Table_WIP_F2.getModel();
            model2.setRowCount(0);
            DefaultTableModel model3 = (DefaultTableModel) Table_WIP_Final.getModel();
            model3.setRowCount(0);
            int x = 1;
            int awas_lp = 0, awas_gram = 0, awas_kpg = 0;
            int siaga_lp = 0, siaga_gram = 0, siaga_kpg = 0;
            int normal_lp = 0, normal_gram = 0, normal_kpg = 0;
            Object[] row = new Object[7];
            sql = "SELECT `tb_finishing_2`.`no_laporan_produksi`, `tb_laporan_produksi`.`kode_grade`, `tb_laporan_produksi`.`ruangan`, `tb_laporan_produksi`.`jumlah_keping`, `tb_laporan_produksi`.`berat_basah`,`tgl_input_byProduct`, `tgl_masuk_f2`, `tgl_dikerjakan_f2`, `tgl_f1`, `tgl_f2`, `tgl_setor_f2`, DATEDIFF(CURRENT_DATE(), `tgl_masuk_f2`) AS 'Result'\n"
                    + "FROM `tb_finishing_2` "
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_finishing_2`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "WHERE `tb_finishing_2`.`tgl_setor_f2` IS NULL\n"
                    + "GROUP BY `tb_finishing_2`.`no_laporan_produksi`\n"
                    + "ORDER BY `result` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                row[0] = x;
                row[1] = rs.getString("no_laporan_produksi");
                row[2] = rs.getString("kode_grade");
                row[3] = rs.getString("ruangan");
                row[4] = rs.getInt("jumlah_keping");
                String status = "";
                if (rs.getDate("tgl_masuk_f2") == null) {
                    status = "LP Belum Masuk";
                } else if (rs.getDate("tgl_dikerjakan_f2") == null) {
                    status = "Koreksi Kering";
                } else if (rs.getDate("tgl_f1") == null) {
                    status = "F1";
                } else if (rs.getDate("tgl_f2") == null) {
                    status = "F2";
                } else {
                    status = "Final Check";
                }
                row[5] = status;
                int hari = rs.getInt("Result");
                row[6] = hari;

                if (hari > 3) {
                    awas_lp++;
                    awas_kpg = awas_kpg + rs.getInt("jumlah_keping");
                    awas_gram = awas_gram + rs.getInt("berat_basah");
                    x++;
                } else if (hari == 3) {
                    siaga_lp++;
                    siaga_kpg = siaga_kpg + rs.getInt("jumlah_keping");
                    siaga_gram = siaga_gram + rs.getInt("berat_basah");
                    x++;
                } else {
                    normal_lp++;
                    normal_kpg = normal_kpg + rs.getInt("jumlah_keping");
                    normal_gram = normal_gram + rs.getInt("berat_basah");
                }

                if (hari >= 3) {
                    switch (status) {
                        case "F1":
                            model1.addRow(row);
                            break;
                        case "F2":
                            model2.addRow(row);
                            break;
                        case "Final Check":
                            model3.addRow(row);
                            break;
                        default:
                            break;
                    }
                }
            }
            Table_WIP_F1.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if ((int) Table_WIP_F1.getValueAt(row, 6) > 3) {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_F1.getSelectionBackground());
                            comp.setForeground(Table_WIP_F1.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.red);
                            comp.setForeground(Color.WHITE);
                        }
                    } else if ((int) Table_WIP_F1.getValueAt(row, 6) == 3) {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_F1.getSelectionBackground());
                            comp.setForeground(Table_WIP_F1.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.yellow);
                            comp.setForeground(Color.BLACK);
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_F1.getSelectionBackground());
                            comp.setForeground(Table_WIP_F1.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.cyan);
                            comp.setForeground(Color.BLACK);
                        }
                    }
                    return comp;
                }
            });
            Table_WIP_F1.repaint();
            Table_WIP_F1.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if ((int) Table_WIP_F1.getValueAt(row, 6) > 3) {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_F1.getSelectionBackground());
                            comp.setForeground(Table_WIP_F1.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.red);
                            comp.setForeground(Color.WHITE);
                        }
                    } else if ((int) Table_WIP_F1.getValueAt(row, 6) == 3) {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_F1.getSelectionBackground());
                            comp.setForeground(Table_WIP_F1.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.yellow);
                            comp.setForeground(Color.BLACK);
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_F1.getSelectionBackground());
                            comp.setForeground(Table_WIP_F1.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.cyan);
                            comp.setForeground(Color.BLACK);
                        }
                    }
                    return comp;
                }
            });
            Table_WIP_F1.repaint();
            Table_WIP_F2.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if ((int) Table_WIP_F2.getValueAt(row, 6) > 3) {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_F2.getSelectionBackground());
                            comp.setForeground(Table_WIP_F2.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.red);
                            comp.setForeground(Color.WHITE);
                        }
                    } else if ((int) Table_WIP_F2.getValueAt(row, 6) == 3) {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_F2.getSelectionBackground());
                            comp.setForeground(Table_WIP_F2.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.yellow);
                            comp.setForeground(Color.BLACK);
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_F2.getSelectionBackground());
                            comp.setForeground(Table_WIP_F2.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.cyan);
                            comp.setForeground(Color.BLACK);
                        }
                    }
                    return comp;
                }
            });
            Table_WIP_F2.repaint();
            Table_WIP_F2.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if ((int) Table_WIP_F2.getValueAt(row, 6) > 3) {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_F2.getSelectionBackground());
                            comp.setForeground(Table_WIP_F2.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.red);
                            comp.setForeground(Color.WHITE);
                        }
                    } else if ((int) Table_WIP_F2.getValueAt(row, 6) == 3) {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_F2.getSelectionBackground());
                            comp.setForeground(Table_WIP_F2.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.yellow);
                            comp.setForeground(Color.BLACK);
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_F2.getSelectionBackground());
                            comp.setForeground(Table_WIP_F2.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.cyan);
                            comp.setForeground(Color.BLACK);
                        }
                    }
                    return comp;
                }
            });
            Table_WIP_F2.repaint();
            Table_WIP_Final.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if ((int) Table_WIP_Final.getValueAt(row, 6) > 3) {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_Final.getSelectionBackground());
                            comp.setForeground(Table_WIP_Final.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.red);
                            comp.setForeground(Color.WHITE);
                        }
                    } else if ((int) Table_WIP_Final.getValueAt(row, 6) == 3) {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_Final.getSelectionBackground());
                            comp.setForeground(Table_WIP_Final.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.yellow);
                            comp.setForeground(Color.BLACK);
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_Final.getSelectionBackground());
                            comp.setForeground(Table_WIP_Final.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.cyan);
                            comp.setForeground(Color.BLACK);
                        }
                    }
                    return comp;
                }
            });
            Table_WIP_Final.repaint();
            Table_WIP_Final.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if ((int) Table_WIP_Final.getValueAt(row, 6) > 3) {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_Final.getSelectionBackground());
                            comp.setForeground(Table_WIP_Final.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.red);
                            comp.setForeground(Color.WHITE);
                        }
                    } else if ((int) Table_WIP_Final.getValueAt(row, 6) == 3) {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_Final.getSelectionBackground());
                            comp.setForeground(Table_WIP_Final.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.yellow);
                            comp.setForeground(Color.BLACK);
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_Final.getSelectionBackground());
                            comp.setForeground(Table_WIP_Final.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.cyan);
                            comp.setForeground(Color.BLACK);
                        }
                    }
                    return comp;
                }
            });
            Table_WIP_Final.repaint();
            ColumnsAutoSizer.sizeColumnsToFit(Table_WIP_F1);
            ColumnsAutoSizer.sizeColumnsToFit(Table_WIP_F2);
            ColumnsAutoSizer.sizeColumnsToFit(Table_WIP_Final);

            label_total_gram_f2_awas1.setText(decimalFormat.format(awas_gram));
            label_total_gram_f2_siaga1.setText(decimalFormat.format(siaga_gram));
            label_total_gram_f2_awas2.setText(decimalFormat.format(awas_gram));
            label_total_gram_f2_siaga2.setText(decimalFormat.format(siaga_gram));
            label_total_gram_f2_awas3.setText(decimalFormat.format(awas_gram));
            label_total_gram_f2_siaga3.setText(decimalFormat.format(siaga_gram));
            label_awas1.setText(Integer.toString(awas_lp));
            label_siaga1.setText(Integer.toString(siaga_lp));
            label_normal1.setText(Integer.toString(normal_lp));

        } catch (SQLException ex) {
            Logger.getLogger(JFrame_TV_Reproses.class.getName()).log(Level.SEVERE, null, ex);
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
            label_awas1.setText(Integer.toString(awas_lp));
            label_siaga1.setText(Integer.toString(siaga_lp));
            label_normal1.setText(Integer.toString(normal_lp));

        } catch (SQLException ex) {
            Logger.getLogger(JFrame_TV_Reproses.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(JFrame_TV_Reproses.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(JFrame_TV_Reproses.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refresh_ByProduct_WIP() {
        try {
            DefaultTableModel model = (DefaultTableModel) tabel_BP_WIP.getModel();
            sql = "SELECT COUNT(`tb_finishing_2`.`no_laporan_produksi`) AS 'jumlah_lp', SUM(`sesekan`) AS 'ssk', SUM(`hancuran`) AS 'hc', SUM(`serabut`) AS 'srbt'\n"
                    + "FROM `tb_finishing_2` "
                    + "LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_finishing_2`.`no_laporan_produksi` = `tb_bahan_jadi_masuk`.`kode_asal` \n"
                    + "LEFT JOIN `tb_tutupan_grading` ON `tb_bahan_jadi_masuk`.`kode_tutupan` = `tb_tutupan_grading`.`kode_tutupan` \n"
                    + "WHERE (`tb_tutupan_grading`.`status_box` = 'PROSES' OR `tb_tutupan_grading`.`status_box` IS NULL)";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
//                System.out.println(rs.getFloat("jumlah_lp"));
                model.setValueAt(rs.getFloat("ssk"), 0, 0);
                model.setValueAt(rs.getFloat("hc"), 0, 1);
                model.setValueAt(rs.getFloat("srbt"), 0, 2);
            }
        } catch (SQLException ex) {
            Logger.getLogger(JFrame_TV_Reproses.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refresh_lp_suwir() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_LP_suwir.getModel();
            model.setRowCount(0);
            int x = 1;
            int awas_lp = 0, awas_gram = 0, awas_kpg = 0;
            int siaga_lp = 0, siaga_gram = 0, siaga_kpg = 0;
            int normal_lp = 0, normal_gram = 0, normal_kpg = 0;
            float total_stok = 0;
            model.setRowCount(0);
            sql = "SELECT `tb_lp_suwir`.`no_lp_suwir`, `tgl_lp_suwir`, `keping`, `gram`, `gram_akhir`, COUNT(`no_box`) AS 'jumlah_box', DATEDIFF(CURRENT_DATE(), `tgl_lp_suwir`) AS 'Result'\n"
                    + "FROM `tb_lp_suwir` \n"
                    + "LEFT JOIN `tb_lp_suwir_detail` ON `tb_lp_suwir`.`no_lp_suwir` = `tb_lp_suwir_detail`.`no_lp_suwir`"
                    + "WHERE `tgl_lp_suwir` >= '2022-12-30'\n"
                    + "GROUP BY `tb_lp_suwir`.`no_lp_suwir` ORDER BY `result` DESC";
            PreparedStatement pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            Object[] baris = new Object[7];
            while (rs.next()) {
                baris[0] = rs.getString("no_lp_suwir");
                baris[1] = rs.getDate("tgl_lp_suwir");
                baris[2] = rs.getInt("keping");
                baris[3] = rs.getFloat("gram");
                baris[4] = rs.getFloat("gram_akhir");

                float keluar_f2 = 0;
                String lp_kaki = rs.getString("no_lp_suwir");
                String sql1 = "SELECT `no_laporan_produksi`, `tambahan_kaki1`, `lp_kaki1`, `tambahan_kaki2`, `lp_kaki2` FROM `tb_finishing_2` "
                        + "WHERE `lp_kaki1` = '" + lp_kaki + "' OR `lp_kaki2` = '" + lp_kaki + "'";
                pst = Utility.db.getConnection().prepareStatement(sql1);
                ResultSet rs_keluar1 = pst.executeQuery();
                while (rs_keluar1.next()) {
                    if (rs_keluar1.getString("lp_kaki1") != null && rs_keluar1.getString("lp_kaki1").equals(lp_kaki)) {
                        keluar_f2 = keluar_f2 + rs_keluar1.getFloat("tambahan_kaki1");
                    }
                    if (rs_keluar1.getString("lp_kaki2") != null && rs_keluar1.getString("lp_kaki2").equals(lp_kaki)) {
                        keluar_f2 = keluar_f2 + rs_keluar1.getFloat("tambahan_kaki2");
                    }
                }
                float keluar_reproses = 0;
                String sql2 = "SELECT `no_lp_suwir`, `no_lp_suwir2`, `gram_kaki`, `gram_kaki2` FROM `tb_reproses` "
                        + "WHERE `no_lp_suwir` = '" + lp_kaki + "' OR `no_lp_suwir2` = '" + lp_kaki + "'";
                pst = Utility.db.getConnection().prepareStatement(sql2);
                ResultSet rs_keluar2 = pst.executeQuery();
                while (rs_keluar2.next()) {
                    try {
                        if (rs_keluar2.getString("no_lp_suwir").equals(lp_kaki)) {
                            keluar_reproses = keluar_reproses + rs_keluar2.getFloat("gram_kaki");
                        }
                    } catch (NullPointerException e) {
                    }

                    try {
                        if (rs_keluar2.getString("no_lp_suwir2").equals(lp_kaki)) {
                            keluar_reproses = keluar_reproses + rs_keluar2.getFloat("gram_kaki2");
                        }
                    } catch (NullPointerException e) {
                    }
                }

                float stok = (rs.getFloat("gram_akhir") - (keluar_f2 + keluar_reproses));
                baris[5] = stok;
                total_stok = total_stok + stok;
                baris[6] = rs.getInt("result");
                if (stok > 0) {
                    model.addRow(baris);
                }
            }

            ColumnsAutoSizer.sizeColumnsToFit(Table_LP_suwir);
            label_total.setText(Integer.toString(Table_LP_suwir.getRowCount()));
            label_total_stok.setText(decimalFormat.format(total_stok));
            label_awas1.setText(Integer.toString(awas_lp));
            label_siaga1.setText(Integer.toString(siaga_lp));
            label_normal1.setText(Integer.toString(normal_lp));

        } catch (SQLException ex) {
            Logger.getLogger(JFrame_TV_Reproses.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Mlem_dan_kinerja() {
        try {
            int jumlah_bulan = 4;
            int[] bulan = new int[jumlah_bulan];
            int[] tahun = new int[jumlah_bulan];
            float[] MLem_A = new float[jumlah_bulan];
            float[] TotalGram_A = new float[jumlah_bulan];
            float[] MLem_B = new float[jumlah_bulan];
            float[] TotalGram_B = new float[jumlah_bulan];
            float[] MLem_C = new float[jumlah_bulan];
            float[] TotalGram_C = new float[jumlah_bulan];
            float[] MLem_D = new float[jumlah_bulan];
            float[] TotalGram_D = new float[jumlah_bulan];

            bulan[0] = Integer.valueOf(new SimpleDateFormat("MM").format(today));
            tahun[0] = Integer.valueOf(new SimpleDateFormat("yyyy").format(today));
            for (int x = 1; x < jumlah_bulan; x++) {
                bulan[x] = bulan[0] - x;
                tahun[x] = tahun[0];
                if (bulan[x] < 1) {
                    bulan[x] = bulan[x] + 12;
                    tahun[x] = tahun[x] - 1;
                }
            }

            JTableHeader TableHeader = Table_data_mlem.getTableHeader();
            TableColumnModel TColumnModel = TableHeader.getColumnModel();
            TableColumnModel TColumnModel_data_kinerja = Table_data_kinerja.getTableHeader().getColumnModel();
            String header_bulan = "";
            DateFormatSymbols dfs = new DateFormatSymbols();
            String[] months = dfs.getMonths();

            for (int x = 0; x < jumlah_bulan; x++) {
                header_bulan = months[bulan[x] - 1].substring(0, 3) + " " + tahun[x];
                TableColumn tc = TColumnModel.getColumn(x + 1);
                tc.setHeaderValue(header_bulan);
                TColumnModel_data_kinerja.getColumn(x + 1).setHeaderValue(header_bulan);
            }

            Table_data_mlem.getTableHeader().setFont(new Font("Arial Narrow", Font.BOLD, 20));
            DefaultTableModel model = (DefaultTableModel) Table_data_mlem.getModel();
            model.setRowCount(0);
            sql = "SELECT MONTH(`tanggal_grading`) AS 'bulan', YEAR(`tanggal_grading`) AS 'tahun', `ruangan`, SUM(`tb_grading_bahan_jadi`.`keping`) AS 'keping'\n"
                    + "FROM `tb_laporan_produksi` \n"
                    + "LEFT JOIN `tb_grading_bahan_jadi` ON `tb_grading_bahan_jadi`.`kode_asal_bahan_jadi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_grading_bahan_jadi`.`kode_asal_bahan_jadi` = `tb_bahan_jadi_masuk`.`kode_asal`\n"
                    + "WHERE `tanggal_grading` BETWEEN '" + tahun[jumlah_bulan - 1] + "-" + bulan[jumlah_bulan - 1] + "-01' AND '" + tahun[0] + "-" + bulan[0] + "-31' AND `grade_bahan_jadi` = 033 "
                    + "GROUP BY MONTH(`tanggal_grading`), `ruangan`\n"
                    + "ORDER BY `tb_bahan_jadi_masuk`.`tanggal_grading`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                for (int i = 0; i < jumlah_bulan; i++) {
                    if (rs.getInt("bulan") == bulan[i]) {
                        if (rs.getString("ruangan").equals("A")) {
                            MLem_A[i] = rs.getInt("keping");
                        } else if (rs.getString("ruangan").equals("B")) {
                            MLem_B[i] = rs.getInt("keping");
                        } else if (rs.getString("ruangan").equals("C")) {
                            MLem_C[i] = rs.getInt("keping");
                        } else if (rs.getString("ruangan").equals("D")) {
                            MLem_D[i] = rs.getInt("keping");
                        }
                    }
                }
            }
            sql = "SELECT MONTH(`tanggal_grading`) AS 'bulan', YEAR(`tanggal_grading`) AS 'tahun', `ruangan`, SUM(`tb_grading_bahan_jadi`.`keping`) AS 'keping'\n"
                    + "FROM `tb_laporan_produksi` \n"
                    + "LEFT JOIN `tb_grading_bahan_jadi` ON `tb_grading_bahan_jadi`.`kode_asal_bahan_jadi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_grading_bahan_jadi`.`kode_asal_bahan_jadi` = `tb_bahan_jadi_masuk`.`kode_asal`\n"
                    + "WHERE `tanggal_grading` BETWEEN '" + tahun[jumlah_bulan - 1] + "-" + bulan[jumlah_bulan - 1] + "-01' AND '" + tahun[0] + "-" + bulan[0] + "-31' "
                    + "GROUP BY MONTH(`tanggal_grading`), `ruangan`\n"
                    + "ORDER BY `tb_bahan_jadi_masuk`.`tanggal_grading`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                for (int i = 0; i < jumlah_bulan; i++) {
                    if (rs.getInt("bulan") == bulan[i]) {
                        if (rs.getString("ruangan").equals("A")) {
                            TotalGram_A[i] = rs.getInt("keping");
                        } else if (rs.getString("ruangan").equals("B")) {
                            TotalGram_B[i] = rs.getInt("keping");
                        } else if (rs.getString("ruangan").equals("C")) {
                            TotalGram_C[i] = rs.getInt("keping");
                        } else if (rs.getString("ruangan").equals("D")) {
                            TotalGram_D[i] = rs.getInt("keping");
                        }
                    }
                }
            }

            decimalFormat.setMaximumFractionDigits(2);
            Object[] row_A = new Object[7];
            row_A[0] = "Persentase MLEM A";
            Object[] row_B = new Object[7];
            row_B[0] = "Persentase MLEM B";
            Object[] row_C = new Object[7];
            row_C[0] = "Persentase MLEM C";
            Object[] row_D = new Object[7];
            row_D[0] = "Persentase MLEM D";
            Object[] row_TOTAL = new Object[7];
            row_TOTAL[0] = "Persentase Rata2";
            for (int i = 0; i < jumlah_bulan; i++) {
                row_A[i + 1] = decimalFormat.format((MLem_A[i] / TotalGram_A[i]) * 100.f) + "%  ";
                row_B[i + 1] = decimalFormat.format((MLem_B[i] / TotalGram_B[i]) * 100.f) + "%  ";
                row_C[i + 1] = decimalFormat.format((MLem_C[i] / TotalGram_C[i]) * 100.f) + "%  ";
                row_D[i + 1] = decimalFormat.format((MLem_D[i] / TotalGram_D[i]) * 100.f) + "%  ";
                row_TOTAL[i + 1] = decimalFormat.format(((MLem_A[i] + MLem_B[i] + MLem_C[i] + MLem_D[i]) / (TotalGram_A[i] + TotalGram_B[i] + TotalGram_C[i] + TotalGram_D[i])) * 100.f) + "%  ";
            }
            model.addRow(row_A);
            model.addRow(row_B);
            model.addRow(row_C);
            model.addRow(row_D);
            model.addRow(row_TOTAL);

            //KINERJA RUANGAN PER BULAN
            float[] Kinerja_ruanganA = new float[jumlah_bulan];
            float[] Kinerja_ruanganB = new float[jumlah_bulan];
            float[] Kinerja_ruanganC = new float[jumlah_bulan];
            float[] Kinerja_ruanganD = new float[jumlah_bulan];
            Table_data_kinerja.getTableHeader().setFont(new Font("Arial Narrow", Font.BOLD, 20));
            DefaultTableModel model_data_kinerja = (DefaultTableModel) Table_data_kinerja.getModel();
            model_data_kinerja.setRowCount(0);
            for (int x = 0; x < jumlah_bulan; x++) {
                float Kinerja_lp_A = 0, avg_kehadiran_A = 0;
                float Kinerja_lp_B = 0, avg_kehadiran_B = 0;
                float Kinerja_lp_C = 0, avg_kehadiran_C = 0;
                float Kinerja_lp_D = 0, avg_kehadiran_D = 0;
                sql = "SELECT `ruangan`, SUM(`keping_upah` / `tb_tarif_cabut`.`kpg_lp`) AS 'bobot_lp'\n"
                        + "FROM `tb_laporan_produksi` LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah`\n"
                        + "WHERE `tanggal_lp` BETWEEN '" + tahun[x] + "-" + bulan[x] + "-01' AND '" + tahun[x] + "-" + bulan[x] + "-31'  \n"
                        + "GROUP BY `ruangan`";
//                System.out.println(sql);
                rs = Utility.db.getStatement().executeQuery(sql);
                while (rs.next()) {
                    if (rs.getString("ruangan").equals("A")) {
                        Kinerja_lp_A = rs.getFloat("bobot_lp");
                    } else if (rs.getString("ruangan").equals("B")) {
                        Kinerja_lp_B = rs.getFloat("bobot_lp");
                    } else if (rs.getString("ruangan").equals("C")) {
                        Kinerja_lp_C = rs.getFloat("bobot_lp");
                    } else if (rs.getString("ruangan").equals("D")) {
                        Kinerja_lp_D = rs.getFloat("bobot_lp");
                    }
                }
                sql = "SELECT `grup`, AVG(`jumlah_karyawan`) AS 'avg_kehadiran' "
                        + "FROM (SELECT DATE(`scan_date`), `tb_bagian`.`grup`, COUNT(DISTINCT(`pin`)) AS 'jumlah_karyawan'\n"
                        + "FROM `att_log` LEFT JOIN `tb_karyawan` ON `att_log`.`pin` = `tb_karyawan`.`pin_finger`\n"
                        + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                        + "WHERE DATE(`scan_date`) BETWEEN '" + tahun[x] + "-" + bulan[x] + "-01' AND '" + tahun[x] + "-" + bulan[x] + "-31' AND `tb_bagian`.`grup` LIKE 'Ruang%'\n"
                        + "GROUP BY `tb_bagian`.`grup`, DATE(`scan_date`)) tabel\n"
                        + "GROUP BY `grup`";
//                System.out.println(sql);
                rs = Utility.db.getStatement().executeQuery(sql);
                while (rs.next()) {
                    if (rs.getString("grup").equals("Ruang A")) {
                        avg_kehadiran_A = rs.getFloat("avg_kehadiran");
                    } else if (rs.getString("grup").equals("Ruang B")) {
                        avg_kehadiran_B = rs.getFloat("avg_kehadiran");
                    } else if (rs.getString("grup").equals("Ruang C")) {
                        avg_kehadiran_C = rs.getFloat("avg_kehadiran");
                    } else if (rs.getString("grup").equals("Ruang D")) {
                        avg_kehadiran_D = rs.getFloat("avg_kehadiran");
                    }
                }
                Kinerja_ruanganA[x] = Kinerja_lp_A / avg_kehadiran_A;
                Kinerja_ruanganB[x] = Kinerja_lp_B / avg_kehadiran_B;
                Kinerja_ruanganC[x] = Kinerja_lp_C / avg_kehadiran_C;
                Kinerja_ruanganD[x] = Kinerja_lp_D / avg_kehadiran_D;
            }
            decimalFormat.setMaximumFractionDigits(2);
            Object[] row_kinerjaA = new Object[7];
            row_kinerjaA[0] = "Tot LP/Anak R.A";
            Object[] row_kinerjaB = new Object[7];
            row_kinerjaB[0] = "Tot LP/Anak R.B";
            Object[] row_kinerjaC = new Object[7];
            row_kinerjaC[0] = "Tot LP/Anak R.C";
            Object[] row_kinerjaD = new Object[7];
            row_kinerjaD[0] = "Tot LP/Anak R.D";
            for (int i = 0; i < jumlah_bulan; i++) {
                row_kinerjaA[i + 1] = decimalFormat.format(Kinerja_ruanganA[i]);
                row_kinerjaB[i + 1] = decimalFormat.format(Kinerja_ruanganB[i]);
                row_kinerjaC[i + 1] = decimalFormat.format(Kinerja_ruanganC[i]);
                row_kinerjaD[i + 1] = decimalFormat.format(Kinerja_ruanganD[i]);
            }
            model_data_kinerja.addRow(row_kinerjaA);
            model_data_kinerja.addRow(row_kinerjaB);
            model_data_kinerja.addRow(row_kinerjaC);
            model_data_kinerja.addRow(row_kinerjaD);
        } catch (SQLException e) {
            Logger.getLogger(JFrame_TV_Reproses.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void refreshTable_SPK() {
        try {

            DefaultTableModel model1 = (DefaultTableModel) Tabel_Detail_SPK.getModel();
            model1.setRowCount(0);
            DefaultTableModel model2 = (DefaultTableModel) Tabel_Detail_SPK1.getModel();
            model2.setRowCount(0);
            int row_counter1 = 0, row_counter2 = 0;
            sql = "SELECT `tb_spk`.`kode_spk`, `tb_buyer`.`nama`, `tanggal_spk`, `tanggal_tk`, `detail`, "
                    + "(SELECT SUM(`tb_spk_detail`.`berat`) FROM `tb_spk_detail` WHERE `kode_spk` = `tb_spk`.`kode_spk`) AS 'berat', "
                    + "SUM(`tb_box_bahan_jadi`.`berat`) AS 'progress'"
                    + "FROM `tb_spk` "
                    + "LEFT JOIN `tb_buyer` ON `tb_spk`.`buyer` = `tb_buyer`.`kode_buyer`"
                    + "LEFT JOIN `tb_spk_detail` ON `tb_spk`.`kode_spk` = `tb_spk_detail`.`kode_spk`"
                    + "LEFT JOIN `tb_box_packing` ON `tb_spk_detail`.`no` = `tb_box_packing`.`no_grade_spk` "
                    + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_box_packing`.`no_box` = `tb_box_bahan_jadi`.`no_box` "
                    + "WHERE `tb_spk`.`status` = 'PROSES' AND `tb_spk`.`kode_spk` NOT IN ('Sample Internal')"
                    + "GROUP BY `kode_spk` ORDER BY `tanggal_tk` DESC";
//            System.out.println(sql);
            pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            Object[] row = new Object[8];
            decimalFormat.setMaximumFractionDigits(1);
            while (rs.next()) {
                row[0] = rs.getString("kode_spk");
                row[1] = rs.getString("nama");
                row[2] = rs.getDate("tanggal_tk");
                row[3] = null;
                row[4] = decimalFormat.format(rs.getFloat("berat"));
                row[5] = null;
                row[6] = decimalFormat.format(rs.getFloat("progress"));
//                row[7] = Math.round((rs.getFloat("progress") / rs.getFloat("berat")) * 1000.f) / 10.f;
                row[7] = decimalFormat.format((rs.getFloat("progress") / rs.getFloat("berat")) * 100.f);
//                System.out.println(row_counter1);
                if (row_counter1 < 20) {
                    model1.addRow(row);
                    row_counter1++;
                } else {
                    model2.addRow(row);
                    row_counter2++;
                }

                String query_detail = "SELECT `no`, `grade_waleta`, `grade_buyer`, `berat_kemasan`, `jumlah_kemasan`, `tb_spk_detail`.`berat`, `harga_cny`, `keterangan`, `kode_spk`, SUM(`tb_box_bahan_jadi`.`berat`) AS 'progress'"
                        + "FROM `tb_spk_detail` "
                        + "LEFT JOIN `tb_box_packing` ON `tb_spk_detail`.`no` = `tb_box_packing`.`no_grade_spk` "
                        + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_box_bahan_jadi`.`no_box` = `tb_box_packing`.`no_box` "
                        + "WHERE `kode_spk` = '" + rs.getString("kode_spk") + "' GROUP BY `no`";
//                System.out.println(query_detail);
                PreparedStatement psts = Utility.db.getConnection().prepareStatement(query_detail);
                ResultSet rst = psts.executeQuery();
                while (rst.next()) {
                    row[0] = rst.getString("grade_waleta");
                    row[1] = rst.getString("grade_buyer");
                    row[2] = rst.getInt("berat_kemasan");
                    row[3] = decimalFormat.format(rst.getFloat("jumlah_kemasan"));
                    row[4] = decimalFormat.format(rst.getFloat("berat"));
                    row[5] = rst.getString("keterangan");
                    row[6] = decimalFormat.format(rst.getFloat("progress"));
//                    row[7] = Math.round((rst.getFloat("progress") / rst.getFloat("berat")) * 1000.f) / 10.f;
                    row[7] = decimalFormat.format((rst.getFloat("progress") / rst.getFloat("berat")) * 100.f);
                    if (row_counter1 < 26) {
                        model1.addRow(row);
                        row_counter1++;
                    } else {
                        model2.addRow(row);
                        row_counter2++;
                    }
                }
//                if (row_counter1 < 27) {
//                    model1.addRow(new Object[]{});
//                    row_counter1++;
//                } else {
//                    model2.addRow(new Object[]{});
//                    row_counter2++;
//                }
            }

            Tabel_Detail_SPK.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (row == 0) {
                        check = 1;
                    } else {
                        check = 0;
                    }
                    if (Tabel_Detail_SPK.getValueAt(row, 3) == null) {
                        if (baris != row) {
                            if (check == 1) {
                                spk = 0;
                            }
                            baris = row;
                            spk++;
//                            System.out.println("spk = " + spk);
//                            System.out.println("row = " + row);
                        }
                        comp.setFont(new Font("Arial", Font.BOLD, 16));
                    }
                    if (Tabel_Detail_SPK.getValueAt(row, 2) != null) {
                        if (isSelected) {
                            comp.setBackground(Tabel_Detail_SPK.getSelectionBackground());
                            comp.setForeground(Tabel_Detail_SPK.getSelectionForeground());
                        } else {
                            comp.setBackground(new Color(R[spk], G[spk], B[spk]));
                            comp.setForeground(Color.BLACK);
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(Tabel_Detail_SPK.getSelectionBackground());
                            comp.setForeground(Tabel_Detail_SPK.getSelectionForeground());
                        } else {
                            comp.setBackground(Tabel_Detail_SPK.getBackground());
                            comp.setForeground(Tabel_Detail_SPK.getForeground());
                        }
                    }
                    return comp;
                }
            });
            Tabel_Detail_SPK.repaint();
            Tabel_Detail_SPK1.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (row == 0) {
                        check = 1;
                    } else {
                        check = 0;
                    }
                    if (Tabel_Detail_SPK1.getValueAt(row, 3) == null) {
                        if (baris != row) {
                            if (check == 1) {
                                spk = 0;
                            }
                            baris = row;
                            spk++;
//                            System.out.println("spk = " + spk);
//                            System.out.println("row = " + row);
                        }
                        comp.setFont(new Font("Arial", Font.BOLD, 16));
                    }
                    if (Tabel_Detail_SPK1.getValueAt(row, 2) != null) {
                        if (isSelected) {
                            comp.setBackground(Tabel_Detail_SPK1.getSelectionBackground());
                            comp.setForeground(Tabel_Detail_SPK1.getSelectionForeground());
                        } else {
                            comp.setBackground(new Color(R[spk], G[spk], B[spk]));
                            comp.setForeground(Color.BLACK);
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(Tabel_Detail_SPK1.getSelectionBackground());
                            comp.setForeground(Tabel_Detail_SPK1.getSelectionForeground());
                        } else {
                            comp.setBackground(Tabel_Detail_SPK1.getBackground());
                            comp.setForeground(Tabel_Detail_SPK1.getForeground());
                        }
                    }
                    return comp;
                }
            });
            Tabel_Detail_SPK1.repaint();

            ColumnsAutoSizer.sizeColumnsToFit(Tabel_Detail_SPK);
            ColumnsAutoSizer.sizeColumnsToFit(Tabel_Detail_SPK1);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JFrame_TV_Reproses.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshIsu1() {
        try {
            int data = 0;
            sql = "SELECT * FROM `tb_isu_produksi` WHERE (`dept` LIKE '%ALL%' OR `dept` LIKE '%FINISHING%') AND `tanggal_selesai` IS NULL ORDER BY `tanggal_isu` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                if (data == 0) {
                    label_judul1.setText(rs.getString("kode_isu"));
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
            Logger.getLogger(JFrame_TV_Reproses.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshIsu2() {
        try {
            int data = 0;
            sql = "SELECT * FROM `tb_isu_produksi` WHERE (`dept` LIKE '%ALL%' OR `dept` LIKE '%FINISHING%') AND `tanggal_selesai` IS NULL ORDER BY `tanggal_isu` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                if (data == 1) {
                    label_judul1.setText(rs.getString("kode_isu"));
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
            Logger.getLogger(JFrame_TV_Reproses.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshIsu3() {
        try {
            int data = 0;
            sql = "SELECT * FROM `tb_isu_produksi` WHERE (`dept` LIKE '%ALL%' OR `dept` LIKE '%FINISHING%') AND `tanggal_selesai` IS NULL ORDER BY `tanggal_isu` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                if (data == 2) {
                    label_judul1.setText(rs.getString("kode_isu"));
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
            Logger.getLogger(JFrame_TV_Reproses.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshIsu4() {
        try {
            Image image;
            int data = 0;
            sql = "SELECT * FROM `tb_isu_produksi` WHERE (`dept` LIKE '%ALL%' OR `dept` LIKE '%FINISHING%') AND `tanggal_selesai` IS NULL ORDER BY `tanggal_isu` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                if (data == 3) {
                    label_judul1.setText(rs.getString("kode_isu"));
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
            Logger.getLogger(JFrame_TV_Reproses.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshIsu5() {
        try {
            int data = 0;
            sql = "SELECT * FROM `tb_isu_produksi` WHERE (`dept` LIKE '%ALL%' OR `dept` LIKE '%FINISHING%') AND `tanggal_selesai` IS NULL ORDER BY `tanggal_isu` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                if (data == 4) {
                    label_judul1.setText(rs.getString("kode_isu"));
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
            Logger.getLogger(JFrame_TV_Reproses.class.getName()).log(Level.SEVERE, null, ex);
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

            label_jumlah_kartu_hold.setText("Total Kartu : ");
            label_total_kartu_jadi_putih.setText(Integer.toString(putih));
            label_total_kartu_jadi_oren.setText(Integer.toString(oren));
            label_total_kartu_jadi_merah.setText(Integer.toString(merah));
            label_normal1.setText(Integer.toString(putih));
            label_siaga1.setText(Integer.toString(oren));
            label_awas1.setText(Integer.toString(merah));
            ColumnsAutoSizer.sizeColumnsToFit(Table_Data_Nitrit_jadi);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JFrame_Tampilan_Treatment.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        label_judul1 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel_setoran_cetak = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        label_setoran_cetak1 = new javax.swing.JLabel();
        jScrollPane21 = new javax.swing.JScrollPane();
        Table_Setoran_cetak1 = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        label_setoran_cetak3 = new javax.swing.JLabel();
        jScrollPane25 = new javax.swing.JScrollPane();
        Table_Setoran_cetak3 = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        label_setoran_cetak2 = new javax.swing.JLabel();
        jScrollPane24 = new javax.swing.JScrollPane();
        Table_Setoran_cetak2 = new javax.swing.JTable();
        jPanel8 = new javax.swing.JPanel();
        label_setoran_cetak4 = new javax.swing.JLabel();
        jScrollPane26 = new javax.swing.JScrollPane();
        Table_Setoran_cetak4 = new javax.swing.JTable();
        jPanel_F1 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        Table_WIP_F1 = new javax.swing.JTable();
        jLabel62 = new javax.swing.JLabel();
        label_total_gram_f2_awas1 = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        label_total_gram_f2_siaga1 = new javax.swing.JLabel();
        jPanel_F2 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        Table_WIP_F2 = new javax.swing.JTable();
        jLabel65 = new javax.swing.JLabel();
        label_total_gram_f2_awas2 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        label_total_gram_f2_siaga2 = new javax.swing.JLabel();
        jPanel_Final = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        Table_WIP_Final = new javax.swing.JTable();
        jLabel67 = new javax.swing.JLabel();
        label_total_gram_f2_awas3 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        label_total_gram_f2_siaga3 = new javax.swing.JLabel();
        jPanel_Reproses = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_reproses = new javax.swing.JTable();
        jLabel61 = new javax.swing.JLabel();
        label_total_gram_reproses = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabel_rekap_grade = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        tabel_stok_reproses = new javax.swing.JTable();
        jLabel64 = new javax.swing.JLabel();
        label_total_stok_reproses = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tabel_BP_WIP = new javax.swing.JTable();
        jPanel_LP_Suwir = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Table_LP_suwir = new javax.swing.JTable();
        jLabel52 = new javax.swing.JLabel();
        label_total = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        label_total_stok = new javax.swing.JLabel();
        jScrollPane10 = new javax.swing.JScrollPane();
        Table_data_mlem = new javax.swing.JTable();
        jScrollPane12 = new javax.swing.JScrollPane();
        Table_data_kinerja = new javax.swing.JTable();
        jPanel_spk = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        Tabel_Detail_SPK = new javax.swing.JTable();
        label_jam = new javax.swing.JLabel();
        jScrollPane11 = new javax.swing.JScrollPane();
        Tabel_Detail_SPK1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
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
        jPanel_nitrit = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        label_total_kartu_jadi_oren = new javax.swing.JLabel();
        label_total_kartu_jadi_putih = new javax.swing.JLabel();
        jScrollPane18 = new javax.swing.JScrollPane();
        Table_Data_Nitrit_jadi = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        label_jumlah_kartu_hold = new javax.swing.JLabel();
        label_total_kartu_jadi_merah = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane19 = new javax.swing.JScrollPane();
        jTextArea6 = new javax.swing.JTextArea();
        label_awas1 = new javax.swing.JLabel();
        space3 = new javax.swing.JLabel();
        label_siaga1 = new javax.swing.JLabel();
        space1 = new javax.swing.JLabel();
        label_normal1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        label_judul1.setBackground(new java.awt.Color(255, 255, 255));
        label_judul1.setFont(new java.awt.Font("Arial", 1, 40)); // NOI18N
        label_judul1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_judul1.setText("REPROSES");
        label_judul1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 0, 8)); // NOI18N
        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });

        jPanel_setoran_cetak.setBackground(new java.awt.Color(255, 255, 255));

        jPanel4.setBackground(new java.awt.Color(204, 255, 255));

        label_setoran_cetak1.setBackground(new java.awt.Color(255, 255, 255));
        label_setoran_cetak1.setFont(new java.awt.Font("Arial", 1, 30)); // NOI18N
        label_setoran_cetak1.setText("CTK H-1 : dd-MMM");

        Table_Setoran_cetak1.setAutoCreateRowSorter(true);
        Table_Setoran_cetak1.setFont(new java.awt.Font("Arial", 0, 22)); // NOI18N
        Table_Setoran_cetak1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Ruang", "MK", "PCH", "Flat", "JDN"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        Table_Setoran_cetak1.setRowHeight(30);
        Table_Setoran_cetak1.getTableHeader().setReorderingAllowed(false);
        jScrollPane21.setViewportView(Table_Setoran_cetak1);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane21, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(label_setoran_cetak1)
                        .addGap(0, 50, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_setoran_cetak1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane21)
                .addContainerGap())
        );

        jPanel5.setBackground(new java.awt.Color(204, 255, 255));

        label_setoran_cetak3.setBackground(new java.awt.Color(255, 255, 255));
        label_setoran_cetak3.setFont(new java.awt.Font("Arial", 1, 30)); // NOI18N
        label_setoran_cetak3.setText("Tgl F1 : dd-MMM");

        Table_Setoran_cetak3.setAutoCreateRowSorter(true);
        Table_Setoran_cetak3.setFont(new java.awt.Font("Arial", 0, 22)); // NOI18N
        Table_Setoran_cetak3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Ruang", "MK", "PCH", "Flat", "JDN"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        Table_Setoran_cetak3.setRowHeight(30);
        Table_Setoran_cetak3.getTableHeader().setReorderingAllowed(false);
        jScrollPane25.setViewportView(Table_Setoran_cetak3);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(label_setoran_cetak3)
                        .addGap(0, 81, Short.MAX_VALUE))
                    .addComponent(jScrollPane25, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_setoran_cetak3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane25, javax.swing.GroupLayout.DEFAULT_SIZE, 557, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel6.setBackground(new java.awt.Color(204, 255, 255));

        label_setoran_cetak2.setBackground(new java.awt.Color(255, 255, 255));
        label_setoran_cetak2.setFont(new java.awt.Font("Arial", 1, 30)); // NOI18N
        label_setoran_cetak2.setText("Tgl Koreksi : dd-MMM");

        Table_Setoran_cetak2.setAutoCreateRowSorter(true);
        Table_Setoran_cetak2.setFont(new java.awt.Font("Arial", 0, 22)); // NOI18N
        Table_Setoran_cetak2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Ruang", "MK", "PCH", "Flat", "JDN"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        Table_Setoran_cetak2.setRowHeight(30);
        Table_Setoran_cetak2.getTableHeader().setReorderingAllowed(false);
        jScrollPane24.setViewportView(Table_Setoran_cetak2);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(label_setoran_cetak2)
                        .addGap(0, 8, Short.MAX_VALUE))
                    .addComponent(jScrollPane24, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_setoran_cetak2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane24, javax.swing.GroupLayout.DEFAULT_SIZE, 557, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel8.setBackground(new java.awt.Color(204, 255, 255));

        label_setoran_cetak4.setBackground(new java.awt.Color(255, 255, 255));
        label_setoran_cetak4.setFont(new java.awt.Font("Arial", 1, 30)); // NOI18N
        label_setoran_cetak4.setText("Tgl F2 : dd-MMM");

        Table_Setoran_cetak4.setAutoCreateRowSorter(true);
        Table_Setoran_cetak4.setFont(new java.awt.Font("Arial", 0, 22)); // NOI18N
        Table_Setoran_cetak4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Ruang", "MK", "PCH", "Flat", "JDN"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        Table_Setoran_cetak4.setRowHeight(30);
        Table_Setoran_cetak4.getTableHeader().setReorderingAllowed(false);
        jScrollPane26.setViewportView(Table_Setoran_cetak4);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(label_setoran_cetak4)
                        .addGap(0, 76, Short.MAX_VALUE))
                    .addComponent(jScrollPane26, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_setoran_cetak4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane26, javax.swing.GroupLayout.DEFAULT_SIZE, 557, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel_setoran_cetakLayout = new javax.swing.GroupLayout(jPanel_setoran_cetak);
        jPanel_setoran_cetak.setLayout(jPanel_setoran_cetakLayout);
        jPanel_setoran_cetakLayout.setHorizontalGroup(
            jPanel_setoran_cetakLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_setoran_cetakLayout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_setoran_cetakLayout.setVerticalGroup(
            jPanel_setoran_cetakLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("SETORAN CETAK", jPanel_setoran_cetak);

        jPanel_F1.setBackground(new java.awt.Color(255, 255, 255));

        Table_WIP_F1.setFont(new java.awt.Font("Arial Narrow", 1, 36)); // NOI18N
        Table_WIP_F1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "No LP", "Grade", "Ruang", "Kpg", "Posisi", "Lama Inap (Hari)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class
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
        Table_WIP_F1.setRowHeight(35);
        Table_WIP_F1.setRowSelectionAllowed(false);
        Table_WIP_F1.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(Table_WIP_F1);

        jLabel62.setBackground(new java.awt.Color(255, 255, 255));
        jLabel62.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel62.setForeground(new java.awt.Color(255, 0, 0));
        jLabel62.setText("Total Gram :");

        label_total_gram_f2_awas1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_f2_awas1.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        label_total_gram_f2_awas1.setForeground(new java.awt.Color(255, 0, 0));
        label_total_gram_f2_awas1.setText("0");

        jLabel63.setBackground(new java.awt.Color(255, 255, 255));
        jLabel63.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel63.setForeground(new java.awt.Color(255, 188, 0));
        jLabel63.setText("Total Gram :");

        label_total_gram_f2_siaga1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_f2_siaga1.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        label_total_gram_f2_siaga1.setForeground(new java.awt.Color(255, 188, 0));
        label_total_gram_f2_siaga1.setText("0");

        javax.swing.GroupLayout jPanel_F1Layout = new javax.swing.GroupLayout(jPanel_F1);
        jPanel_F1.setLayout(jPanel_F1Layout);
        jPanel_F1Layout.setHorizontalGroup(
            jPanel_F1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 1361, Short.MAX_VALUE)
            .addGroup(jPanel_F1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel62)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_total_gram_f2_awas1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel63)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_total_gram_f2_siaga1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_F1Layout.setVerticalGroup(
            jPanel_F1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_F1Layout.createSequentialGroup()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 583, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_F1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_F1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel63, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_gram_f2_siaga1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_F1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel62, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_gram_f2_awas1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("F1", jPanel_F1);

        jPanel_F2.setBackground(new java.awt.Color(255, 255, 255));

        Table_WIP_F2.setFont(new java.awt.Font("Arial Narrow", 1, 36)); // NOI18N
        Table_WIP_F2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "No LP", "Grade", "Ruang", "Kpg", "Posisi", "Lama Inap (Hari)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class
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
        Table_WIP_F2.setRowHeight(35);
        Table_WIP_F2.setRowSelectionAllowed(false);
        Table_WIP_F2.getTableHeader().setReorderingAllowed(false);
        jScrollPane7.setViewportView(Table_WIP_F2);

        jLabel65.setBackground(new java.awt.Color(255, 255, 255));
        jLabel65.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel65.setForeground(new java.awt.Color(255, 0, 0));
        jLabel65.setText("Total Gram :");

        label_total_gram_f2_awas2.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_f2_awas2.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        label_total_gram_f2_awas2.setForeground(new java.awt.Color(255, 0, 0));
        label_total_gram_f2_awas2.setText("0");

        jLabel66.setBackground(new java.awt.Color(255, 255, 255));
        jLabel66.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel66.setForeground(new java.awt.Color(255, 188, 0));
        jLabel66.setText("Total Gram :");

        label_total_gram_f2_siaga2.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_f2_siaga2.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        label_total_gram_f2_siaga2.setForeground(new java.awt.Color(255, 188, 0));
        label_total_gram_f2_siaga2.setText("0");

        javax.swing.GroupLayout jPanel_F2Layout = new javax.swing.GroupLayout(jPanel_F2);
        jPanel_F2.setLayout(jPanel_F2Layout);
        jPanel_F2Layout.setHorizontalGroup(
            jPanel_F2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 1361, Short.MAX_VALUE)
            .addGroup(jPanel_F2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel65)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_total_gram_f2_awas2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel66)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_total_gram_f2_siaga2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_F2Layout.setVerticalGroup(
            jPanel_F2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_F2Layout.createSequentialGroup()
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 583, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_F2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_F2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel66, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_gram_f2_siaga2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_F2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel65, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_gram_f2_awas2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("F2", jPanel_F2);

        jPanel_Final.setBackground(new java.awt.Color(255, 255, 255));

        Table_WIP_Final.setFont(new java.awt.Font("Arial Narrow", 1, 36)); // NOI18N
        Table_WIP_Final.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "No LP", "Grade", "Ruang", "Kpg", "Posisi", "Lama Inap (Hari)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class
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
        Table_WIP_Final.setRowHeight(35);
        Table_WIP_Final.setRowSelectionAllowed(false);
        Table_WIP_Final.getTableHeader().setReorderingAllowed(false);
        jScrollPane8.setViewportView(Table_WIP_Final);

        jLabel67.setBackground(new java.awt.Color(255, 255, 255));
        jLabel67.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel67.setForeground(new java.awt.Color(255, 0, 0));
        jLabel67.setText("Total Gram :");

        label_total_gram_f2_awas3.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_f2_awas3.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        label_total_gram_f2_awas3.setForeground(new java.awt.Color(255, 0, 0));
        label_total_gram_f2_awas3.setText("0");

        jLabel68.setBackground(new java.awt.Color(255, 255, 255));
        jLabel68.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel68.setForeground(new java.awt.Color(255, 188, 0));
        jLabel68.setText("Total Gram :");

        label_total_gram_f2_siaga3.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_f2_siaga3.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        label_total_gram_f2_siaga3.setForeground(new java.awt.Color(255, 188, 0));
        label_total_gram_f2_siaga3.setText("0");

        javax.swing.GroupLayout jPanel_FinalLayout = new javax.swing.GroupLayout(jPanel_Final);
        jPanel_Final.setLayout(jPanel_FinalLayout);
        jPanel_FinalLayout.setHorizontalGroup(
            jPanel_FinalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 1361, Short.MAX_VALUE)
            .addGroup(jPanel_FinalLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel67)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_total_gram_f2_awas3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel68)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_total_gram_f2_siaga3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_FinalLayout.setVerticalGroup(
            jPanel_FinalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_FinalLayout.createSequentialGroup()
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 583, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_FinalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_FinalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel68, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_gram_f2_siaga3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_FinalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel67, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_gram_f2_awas3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("FINAL", jPanel_Final);

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
        jScrollPane1.setViewportView(Table_reproses);

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
        jScrollPane3.setViewportView(tabel_rekap_grade);

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
        jScrollPane4.setViewportView(tabel_stok_reproses);

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
        jScrollPane6.setViewportView(tabel_BP_WIP);

        javax.swing.GroupLayout jPanel_ReprosesLayout = new javax.swing.GroupLayout(jPanel_Reproses);
        jPanel_Reproses.setLayout(jPanel_ReprosesLayout);
        jPanel_ReprosesLayout.setHorizontalGroup(
            jPanel_ReprosesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_ReprosesLayout.createSequentialGroup()
                .addGroup(jPanel_ReprosesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 833, Short.MAX_VALUE)
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
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 512, Short.MAX_VALUE)
                    .addComponent(jScrollPane6)
                    .addComponent(jScrollPane4))
                .addContainerGap())
        );
        jPanel_ReprosesLayout.setVerticalGroup(
            jPanel_ReprosesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_ReprosesLayout.createSequentialGroup()
                .addGroup(jPanel_ReprosesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 583, Short.MAX_VALUE)
                    .addGroup(jPanel_ReprosesLayout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_ReprosesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel61, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram_reproses, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel64, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_stok_reproses, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("REPROSES", jPanel_Reproses);

        jPanel_LP_Suwir.setBackground(new java.awt.Color(255, 255, 255));

        Table_LP_suwir.setFont(new java.awt.Font("Arial Narrow", 1, 36)); // NOI18N
        Table_LP_suwir.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP", "Tanggal", "Kpg", "Gram", "Gram Akhir", "Stok", "Lama Inap (Hari)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Integer.class
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
        Table_LP_suwir.setRowHeight(35);
        Table_LP_suwir.setRowSelectionAllowed(false);
        Table_LP_suwir.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(Table_LP_suwir);

        jLabel52.setBackground(new java.awt.Color(255, 255, 255));
        jLabel52.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel52.setText("Total Data :");

        label_total.setBackground(new java.awt.Color(255, 255, 255));
        label_total.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        label_total.setText("0");

        jLabel60.setBackground(new java.awt.Color(255, 255, 255));
        jLabel60.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel60.setText("Total Stok :");

        label_total_stok.setBackground(new java.awt.Color(255, 255, 255));
        label_total_stok.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        label_total_stok.setText("0");

        Table_data_mlem.setFont(new java.awt.Font("Arial Narrow", 1, 28)); // NOI18N
        Table_data_mlem.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Persentase MLem", "", null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Deskripsi", "1", "2", "3", "4"
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
        Table_data_mlem.setFocusable(false);
        Table_data_mlem.setRowHeight(32);
        Table_data_mlem.setRowSelectionAllowed(false);
        Table_data_mlem.getTableHeader().setReorderingAllowed(false);
        jScrollPane10.setViewportView(Table_data_mlem);
        if (Table_data_mlem.getColumnModel().getColumnCount() > 0) {
            Table_data_mlem.getColumnModel().getColumn(0).setMinWidth(180);
        }

        Table_data_kinerja.setFont(new java.awt.Font("Arial Narrow", 1, 28)); // NOI18N
        Table_data_kinerja.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Tot LP/Anak R.A", "", null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Deskripsi", "1", "2", "3", "4"
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
        Table_data_kinerja.setFocusable(false);
        Table_data_kinerja.setRowHeight(32);
        Table_data_kinerja.setRowSelectionAllowed(false);
        Table_data_kinerja.getTableHeader().setReorderingAllowed(false);
        jScrollPane12.setViewportView(Table_data_kinerja);
        if (Table_data_kinerja.getColumnModel().getColumnCount() > 0) {
            Table_data_kinerja.getColumnModel().getColumn(0).setMinWidth(180);
        }

        javax.swing.GroupLayout jPanel_LP_SuwirLayout = new javax.swing.GroupLayout(jPanel_LP_Suwir);
        jPanel_LP_Suwir.setLayout(jPanel_LP_SuwirLayout);
        jPanel_LP_SuwirLayout.setHorizontalGroup(
            jPanel_LP_SuwirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1361, Short.MAX_VALUE)
            .addGroup(jPanel_LP_SuwirLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel52)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_total)
                .addGap(21, 21, 21)
                .addComponent(jLabel60)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_total_stok)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 1361, Short.MAX_VALUE)
            .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 1361, Short.MAX_VALUE)
        );
        jPanel_LP_SuwirLayout.setVerticalGroup(
            jPanel_LP_SuwirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_LP_SuwirLayout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_LP_SuwirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_LP_SuwirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel60, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_stok, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_LP_SuwirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPane1.addTab("LP SUWIR", jPanel_LP_Suwir);

        jPanel_spk.setBackground(new java.awt.Color(255, 255, 255));

        Tabel_Detail_SPK.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        Tabel_Detail_SPK.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade WLT", "Grade Buyer", "Berat / Kemasan", "Pack", "Net Weight", "Ket.", "Turun", "%"
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
        Tabel_Detail_SPK.setRowHeight(25);
        Tabel_Detail_SPK.setRowSelectionAllowed(false);
        Tabel_Detail_SPK.getTableHeader().setReorderingAllowed(false);
        jScrollPane9.setViewportView(Tabel_Detail_SPK);
        if (Tabel_Detail_SPK.getColumnModel().getColumnCount() > 0) {
            Tabel_Detail_SPK.getColumnModel().getColumn(1).setMaxWidth(150);
        }

        label_jam.setBackground(new java.awt.Color(255, 255, 255));
        label_jam.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        label_jam.setText("Jam :");

        Tabel_Detail_SPK1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        Tabel_Detail_SPK1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade WLT", "Grade Buyer", "Berat / Kemasan", "Pack", "Net Weight", "Ket.", "Turun", "%"
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
        Tabel_Detail_SPK1.setRowHeight(25);
        Tabel_Detail_SPK1.setRowSelectionAllowed(false);
        Tabel_Detail_SPK1.getTableHeader().setReorderingAllowed(false);
        jScrollPane11.setViewportView(Tabel_Detail_SPK1);
        if (Tabel_Detail_SPK1.getColumnModel().getColumnCount() > 0) {
            Tabel_Detail_SPK1.getColumnModel().getColumn(1).setMaxWidth(150);
        }

        jButton1.setText("jButton1");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_spkLayout = new javax.swing.GroupLayout(jPanel_spk);
        jPanel_spk.setLayout(jPanel_spkLayout);
        jPanel_spkLayout.setHorizontalGroup(
            jPanel_spkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_spkLayout.createSequentialGroup()
                .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 683, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 672, Short.MAX_VALUE))
            .addGroup(jPanel_spkLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_jam)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );
        jPanel_spkLayout.setVerticalGroup(
            jPanel_spkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_spkLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_spkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_jam, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_spkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 573, Short.MAX_VALUE)
                    .addComponent(jScrollPane11)))
        );

        jTabbedPane1.addTab("SPK", jPanel_spk);

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

        jPanel_nitrit.setBackground(new java.awt.Color(255, 255, 255));

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel13.setText("/");

        label_total_kartu_jadi_oren.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kartu_jadi_oren.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        label_total_kartu_jadi_oren.setForeground(new java.awt.Color(255, 153, 0));
        label_total_kartu_jadi_oren.setText("0");

        label_total_kartu_jadi_putih.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kartu_jadi_putih.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
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
        jScrollPane18.setViewportView(Table_Data_Nitrit_jadi);

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel5.setText("NITRIT JADI");

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel12.setText("/");

        label_jumlah_kartu_hold.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_kartu_hold.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        label_jumlah_kartu_hold.setText("Total Kartu :");

        label_total_kartu_jadi_merah.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kartu_jadi_merah.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        label_total_kartu_jadi_merah.setForeground(new java.awt.Color(255, 0, 0));
        label_total_kartu_jadi_merah.setText("0");

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 3, 24)); // NOI18N
        jLabel1.setText("Keterangan :");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 3, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 255));
        jLabel4.setText("TR Sela untuk grade W3, GRS BRT, ");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 3, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 0, 255));
        jLabel6.setText("GRS RGN, SORTIR, KK KNG, Kartu Khusus");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 3, 24)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 0, 0));
        jLabel9.setText("Persentase hold >20% & Kartu Khusus : TR Sela 2,5 jam");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 3, 24)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(195, 195, 0));
        jLabel11.setText("Persentase hold <20% : TR Sela 1,5 jam");

        jTextArea6.setColumns(20);
        jTextArea6.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jTextArea6.setRows(5);
        jTextArea6.setText("Catatan :\n");
        jScrollPane19.setViewportView(jTextArea6);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(label_jumlah_kartu_hold)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_kartu_jadi_putih)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_kartu_jadi_oren)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_kartu_jadi_merah))
                    .addComponent(jScrollPane18, javax.swing.GroupLayout.PREFERRED_SIZE, 524, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane19)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel6)
                            .addComponent(jLabel11)
                            .addComponent(jLabel9)
                            .addComponent(jLabel1))
                        .addGap(0, 175, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5)
                            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                .addComponent(label_total_kartu_jadi_oren)
                                .addComponent(label_total_kartu_jadi_merah)
                                .addComponent(jLabel12)
                                .addComponent(jLabel13)
                                .addComponent(label_jumlah_kartu_hold)
                                .addComponent(label_total_kartu_jadi_putih)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane18, javax.swing.GroupLayout.DEFAULT_SIZE, 561, Short.MAX_VALUE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane19)))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel_nitritLayout = new javax.swing.GroupLayout(jPanel_nitrit);
        jPanel_nitrit.setLayout(jPanel_nitritLayout);
        jPanel_nitritLayout.setHorizontalGroup(
            jPanel_nitritLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel_nitritLayout.setVerticalGroup(
            jPanel_nitritLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("NITRIT & TREATMENT", jPanel_nitrit);

        label_awas1.setBackground(new java.awt.Color(255, 255, 255));
        label_awas1.setFont(new java.awt.Font("Arial Narrow", 1, 60)); // NOI18N
        label_awas1.setForeground(new java.awt.Color(255, 0, 0));
        label_awas1.setText("88");

        space3.setBackground(new java.awt.Color(255, 255, 255));
        space3.setFont(new java.awt.Font("Arial Narrow", 1, 60)); // NOI18N
        space3.setText("/");

        label_siaga1.setBackground(new java.awt.Color(255, 255, 255));
        label_siaga1.setFont(new java.awt.Font("Arial Narrow", 1, 60)); // NOI18N
        label_siaga1.setForeground(new java.awt.Color(255, 188, 0));
        label_siaga1.setText("88");

        space1.setBackground(new java.awt.Color(255, 255, 255));
        space1.setFont(new java.awt.Font("Arial Narrow", 1, 60)); // NOI18N
        space1.setText("/");

        label_normal1.setBackground(new java.awt.Color(255, 255, 255));
        label_normal1.setFont(new java.awt.Font("Arial Narrow", 1, 60)); // NOI18N
        label_normal1.setForeground(new java.awt.Color(0, 0, 255));
        label_normal1.setText("88");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(label_judul1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_awas1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(space3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_siaga1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(space1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_normal1)
                .addContainerGap())
            .addComponent(jTabbedPane1)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_judul1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_awas1)
                        .addComponent(label_siaga1)
                        .addComponent(label_normal1)
                        .addComponent(space1)
                        .addComponent(space3)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1))
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
        label_awas1.setText(Integer.toString(0));
        label_siaga1.setText(Integer.toString(0));
        label_normal1.setText(Integer.toString(0));
        switch (jTabbedPane1.getSelectedIndex()) {
            case 0:
                label_judul1.setText("SETORAN CETAK");
                refreshTabel_setoran_cetak();
                break;
            case 1:
                label_judul1.setText("FINISHING 2");
                refreshTable_f2();
                break;
            case 4:
                label_judul1.setText("REPROSES");
                refresh_reproses();
                refresh_tabel_rekap();
                refresh_stok_reproses();
                refresh_ByProduct_WIP();
                break;
            case 5:
                label_judul1.setText("LP SUWIR");
                refresh_lp_suwir();
                Mlem_dan_kinerja();
                break;
            case 6:
                label_judul1.setText("SPK");
                refreshTable_SPK();
                break;
            case 7:
                refreshIsu1();
                break;
            case 8:
                refreshIsu2();
                break;
            case 9:
                refreshIsu3();
                break;
            case 10:
                refreshIsu4();
                break;
            case 11:
                refreshIsu5();
                break;
            case 12:
                label_judul1.setText("NITRIT BARANG JADI");
                refreshTabel_hold_jadi();
                break;
        }
    }//GEN-LAST:event_jTabbedPane1StateChanged

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        t.cancel();
        System.out.println("STOP");
    }//GEN-LAST:event_formWindowClosing

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        refreshTable_SPK();
    }//GEN-LAST:event_jButton1ActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JFrame_TV_Reproses.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFrame_TV_Reproses.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFrame_TV_Reproses.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrame_TV_Reproses.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFrame_TV_Reproses frame = new JFrame_TV_Reproses();
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                frame.setEnabled(true);
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable Tabel_Detail_SPK;
    private javax.swing.JTable Tabel_Detail_SPK1;
    private javax.swing.JTable Table_Data_Nitrit_jadi;
    private javax.swing.JTable Table_LP_suwir;
    public static javax.swing.JTable Table_Setoran_cetak1;
    public static javax.swing.JTable Table_Setoran_cetak2;
    public static javax.swing.JTable Table_Setoran_cetak3;
    public static javax.swing.JTable Table_Setoran_cetak4;
    private javax.swing.JTable Table_WIP_F1;
    private javax.swing.JTable Table_WIP_F2;
    private javax.swing.JTable Table_WIP_Final;
    private javax.swing.JTable Table_data_kinerja;
    private javax.swing.JTable Table_data_mlem;
    private javax.swing.JTable Table_reproses;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel18;
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
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel_F1;
    private javax.swing.JPanel jPanel_F2;
    private javax.swing.JPanel jPanel_Final;
    private javax.swing.JPanel jPanel_LP_Suwir;
    private javax.swing.JPanel jPanel_Reproses;
    private javax.swing.JPanel jPanel_isu1;
    private javax.swing.JPanel jPanel_isu2;
    private javax.swing.JPanel jPanel_isu3;
    private javax.swing.JPanel jPanel_isu4;
    private javax.swing.JPanel jPanel_isu5;
    private javax.swing.JPanel jPanel_nitrit;
    private javax.swing.JPanel jPanel_setoran_cetak;
    private javax.swing.JPanel jPanel_spk;
    private javax.swing.JScrollPane jScrollPane1;
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
    private javax.swing.JScrollPane jScrollPane21;
    private javax.swing.JScrollPane jScrollPane24;
    private javax.swing.JScrollPane jScrollPane25;
    private javax.swing.JScrollPane jScrollPane26;
    private javax.swing.JScrollPane jScrollPane3;
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
    private javax.swing.JLabel label_awas1;
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
    private javax.swing.JLabel label_judul1;
    private javax.swing.JLabel label_jumlah_kartu_hold;
    private javax.swing.JLabel label_kode;
    private javax.swing.JLabel label_kode1;
    private javax.swing.JLabel label_kode2;
    private javax.swing.JLabel label_kode3;
    private javax.swing.JLabel label_kode4;
    private javax.swing.JLabel label_normal1;
    private javax.swing.JLabel label_penanggungjawab;
    private javax.swing.JLabel label_penanggungjawab1;
    private javax.swing.JLabel label_penanggungjawab2;
    private javax.swing.JLabel label_penanggungjawab3;
    private javax.swing.JLabel label_penanggungjawab4;
    private javax.swing.JLabel label_setoran_cetak1;
    private javax.swing.JLabel label_setoran_cetak2;
    private javax.swing.JLabel label_setoran_cetak3;
    private javax.swing.JLabel label_setoran_cetak4;
    private javax.swing.JLabel label_siaga1;
    private javax.swing.JLabel label_tgl_isu;
    private javax.swing.JLabel label_tgl_isu1;
    private javax.swing.JLabel label_tgl_isu2;
    private javax.swing.JLabel label_tgl_isu3;
    private javax.swing.JLabel label_tgl_isu4;
    private javax.swing.JLabel label_total;
    private javax.swing.JLabel label_total_gram_f2_awas1;
    private javax.swing.JLabel label_total_gram_f2_awas2;
    private javax.swing.JLabel label_total_gram_f2_awas3;
    private javax.swing.JLabel label_total_gram_f2_siaga1;
    private javax.swing.JLabel label_total_gram_f2_siaga2;
    private javax.swing.JLabel label_total_gram_f2_siaga3;
    private javax.swing.JLabel label_total_gram_reproses;
    private javax.swing.JLabel label_total_kartu_jadi_merah;
    private javax.swing.JLabel label_total_kartu_jadi_oren;
    private javax.swing.JLabel label_total_kartu_jadi_putih;
    private javax.swing.JLabel label_total_stok;
    private javax.swing.JLabel label_total_stok_reproses;
    private javax.swing.JLabel space1;
    private javax.swing.JLabel space3;
    private javax.swing.JTable tabel_BP_WIP;
    private javax.swing.JTable tabel_rekap_grade;
    private javax.swing.JTable tabel_stok_reproses;
    // End of variables declaration//GEN-END:variables
}
