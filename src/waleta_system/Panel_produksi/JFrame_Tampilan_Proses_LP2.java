package waleta_system.Panel_produksi;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;

public class JFrame_Tampilan_Proses_LP2 extends javax.swing.JFrame {

     
    String sql = null, sql_rekap = null;
    ResultSet rs;
    Date today = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    String ruang = "";
    static Timer t;
    static int detik = 0, menit = 0, jam = 0;
    static int tab = 0;
    String filter_ruangan = "";

    public JFrame_Tampilan_Proses_LP2(String ruang) {
        try {
            
            ArrayList<String> subs = new ArrayList<>();
            Utility.db_sub.connect();
            sql = "SELECT `kode_sub` FROM `tb_sub_waleta` WHERE 1";
            rs = Utility.db_sub.getStatement().executeQuery(sql);
            while (rs.next()) {
                subs.add(rs.getString("kode_sub"));
            }
            filter_ruangan = "'" + ruang + "'";
            if (ruang.equals("D")) {
                for (String sub : subs) {
                    filter_ruangan = filter_ruangan + ", '" + sub + "'";
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(JFrame_Tampilan_Proses_LP2.class.getName()).log(Level.SEVERE, null, ex);
        }
        initComponents();
        label_judul.setText("RUANG " + ruang);
        label_judul1.setText("CABUT");
        if (!ruang.equals("All")) {
            this.ruang = ruang;
        }
        init();
    }

    public void init() {
        try {
            detik = 0;
            menit = 0;
            jam = 0;
            tab = 0;

            refreshTable_Cabut1();
//            refreshTable_Cabut2();
//            refreshTable_Cetak();
//            refreshTable_Cetak_jidun();
//            refreshTable_f2();
//            refreshIsu1();
//            refreshIsu2();
//            refreshIsu3();

            TimerTask timer;
            timer = new TimerTask() {
                @Override
                public void run() {
                    boolean changeTab = true;
                    label_waktu.setText(jam + " : " + menit + " : " + detik);
                    if (detik == 0) {
                        while (changeTab) {
                            jTabbedPane1.setSelectedIndex(tab);
                            if (tab == 0 && Integer.valueOf(label_awas1.getText()) == 0 && Integer.valueOf(label_siaga1.getText()) == 0) {
                                tab = 1;
                            } else if (tab == 1 && Integer.valueOf(label_awas2.getText()) == 0 && Integer.valueOf(label_siaga2.getText()) == 0) {
                                tab = 2;
                            } else if (tab == 2 && Integer.valueOf(label_awas3.getText()) == 0 && Integer.valueOf(label_siaga3.getText()) == 0) {
                                tab = 3;
                            } else if (tab == 3 && Integer.valueOf(label_awas5.getText()) == 0 && Integer.valueOf(label_siaga5.getText()) == 0) {
                                tab = 4;
                            } else if (tab == 4 && Integer.valueOf(label_awas4.getText()) == 0 && Integer.valueOf(label_siaga4.getText()) == 0) {
                                tab = 5;
                            } else if (tab == 5 && false) {
                                tab = 6;
                            } else if (tab == 6 && label_kode.getText().equals("-")) {
                                tab = 7;
                            } else if (tab == 7 && label_kode1.getText().equals("-")) {
                                tab = 8;
                            } else if (tab == 8 && label_kode2.getText().equals("-")) {
                                tab = 9;
                            } else if (tab == 9 && label_kode3.getText().equals("-")) {
                                tab = 10;
                            } else if (tab == 10 && label_kode4.getText().equals("-")) {
                                tab = 0;
                            } else {
                                changeTab = false;
                            }
                        }
//                        jTabbedPane1.setSelectedIndex(tab);
                        tab++;
                        if (tab > 10) {
                            tab = 0;
                        }
                    }

                    detik++;
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
            t.schedule(timer, 1000, 1000);

        } catch (Exception ex) {
            Logger.getLogger(JPanel_ProgressLP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_Cabut1() {
        try {
            label_judul1.setText("CABUT");
            label_target_awas.setText(">2");
            label_target_siaga.setText("=2");
            label_target_normal.setText("<2");

            DefaultTableModel model = (DefaultTableModel) Table_WIP_Cabut1.getModel();
            model.setRowCount(0);
            int x = 1;
            int awas_lp = 0, awas_gram = 0, awas_kpg = 0;
            int siaga_lp = 0, siaga_gram = 0, siaga_kpg = 0;
            int normal_lp = 0, normal_gram = 0, normal_kpg = 0;
            Object[] row = new Object[6];
            sql = "SELECT `tb_cabut`.`no_laporan_produksi`, `tb_laporan_produksi`.`kode_grade`, `tb_laporan_produksi`.`jumlah_keping`, `tb_laporan_produksi`.`berat_basah`, `tgl_mulai_cabut`, `tb_detail_pencabut`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, DATEDIFF(CURRENT_DATE(), `tgl_mulai_cabut`) AS 'Result'\n"
                    + "FROM `tb_cabut` LEFT JOIN `tb_laporan_produksi` ON `tb_cabut`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_detail_pencabut` ON `tb_cabut`.`no_laporan_produksi` = `tb_detail_pencabut`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_detail_pencabut`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                    + "WHERE `tb_laporan_produksi`.`ruangan` IN (" + filter_ruangan + ")\n"
                    + "AND `tb_cabut`.`tgl_setor_cabut` IS NULL\n"
                    + "GROUP BY `tb_cabut`.`no_laporan_produksi`\n"
                    + "ORDER BY `result` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                row[0] = x;
                row[1] = rs.getString("no_laporan_produksi");
                row[2] = rs.getString("kode_grade");
                row[3] = rs.getInt("jumlah_keping");
                row[4] = rs.getString("nama_pegawai");
                row[5] = rs.getInt("Result");

                if (rs.getInt("Result") > 2) {
                    awas_lp++;
                    awas_kpg = awas_kpg + rs.getInt("jumlah_keping");
                    awas_gram = awas_gram + rs.getInt("berat_basah");
                    model.addRow(row);
                    x++;
                } else if (rs.getInt("Result") == 2) {
                    siaga_lp++;
                    siaga_kpg = siaga_kpg + rs.getInt("jumlah_keping");
                    siaga_gram = siaga_gram + rs.getInt("berat_basah");
                    model.addRow(row);
                    x++;
                } else {
                    normal_lp++;
                    normal_kpg = normal_kpg + rs.getInt("jumlah_keping");
                    normal_gram = normal_gram + rs.getInt("berat_basah");
                }
            }
            Table_WIP_Cabut1.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if ((int) Table_WIP_Cabut1.getValueAt(row, 5) > 2) {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_Cabut1.getSelectionBackground());
                            comp.setForeground(Table_WIP_Cabut1.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.red);
                            comp.setForeground(Color.WHITE);
                        }
                    } else if ((int) Table_WIP_Cabut1.getValueAt(row, 5) == 2) {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_Cabut1.getSelectionBackground());
                            comp.setForeground(Table_WIP_Cabut1.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.yellow);
                            comp.setForeground(Color.BLACK);
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_Cabut1.getSelectionBackground());
                            comp.setForeground(Table_WIP_Cabut1.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.cyan);
                            comp.setForeground(Color.BLACK);
                        }
                    }
                    return comp;
                }
            });
            Table_WIP_Cabut1.repaint();
            Table_WIP_Cabut1.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if ((int) Table_WIP_Cabut1.getValueAt(row, 5) > 2) {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_Cabut1.getSelectionBackground());
                            comp.setForeground(Table_WIP_Cabut1.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.red);
                            comp.setForeground(Color.WHITE);
                        }
                    } else if ((int) Table_WIP_Cabut1.getValueAt(row, 5) == 2) {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_Cabut1.getSelectionBackground());
                            comp.setForeground(Table_WIP_Cabut1.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.yellow);
                            comp.setForeground(Color.BLACK);
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_Cabut1.getSelectionBackground());
                            comp.setForeground(Table_WIP_Cabut1.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.cyan);
                            comp.setForeground(Color.BLACK);
                        }
                    }
                    return comp;
                }
            });
            Table_WIP_Cabut1.repaint();
            ColumnsAutoSizer.sizeColumnsToFit(Table_WIP_Cabut1);

            label_awas1.setText(Integer.toString(awas_lp));
            label_siaga1.setText(Integer.toString(siaga_lp));
            label_normal1.setText(Integer.toString(normal_lp));

        } catch (SQLException ex) {
            Logger.getLogger(JFrame_Tampilan_Proses_LP2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_Cabut2() {
        try {
            label_judul1.setText("SETOR SUSUTAN");
            label_target_awas.setText(">3");
            label_target_siaga.setText("=3");
            label_target_normal.setText("<3");
            DefaultTableModel model = (DefaultTableModel) Table_WIP_Cabut2.getModel();
            model.setRowCount(0);
            int x = 1;
            int awas_lp = 0, awas_gram = 0, awas_kpg = 0;
            int siaga_lp = 0, siaga_gram = 0, siaga_kpg = 0;
            int normal_lp = 0, normal_gram = 0, normal_kpg = 0;
            Object[] row = new Object[6];
            sql = "SELECT `tb_cabut`.`no_laporan_produksi`, `tb_laporan_produksi`.`kode_grade`, `tb_laporan_produksi`.`jumlah_keping`, `tb_laporan_produksi`.`berat_basah`, `tgl_setor_cabut`, `tb_detail_pencabut`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, DATEDIFF(CURRENT_DATE(), `tgl_setor_cabut`) AS 'Result', `tb_finishing_2`.`tgl_input_byProduct`\n"
                    + "FROM `tb_cabut` LEFT JOIN `tb_laporan_produksi` ON `tb_cabut`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_finishing_2` ON `tb_cabut`.`no_laporan_produksi` = `tb_finishing_2`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_detail_pencabut` ON `tb_cabut`.`no_laporan_produksi` = `tb_detail_pencabut`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_detail_pencabut`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                    + "WHERE `tb_laporan_produksi`.`ruangan` IN (" + filter_ruangan + ")\n"
                    + "AND `tb_cabut`.`tgl_setor_cabut` > '2019-06-01' AND `tb_finishing_2`.`tgl_input_byProduct` IS NULL\n"
                    + "GROUP BY `tb_cabut`.`no_laporan_produksi` \n"
                    + "ORDER BY `result` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                row[0] = x;
                row[1] = rs.getString("no_laporan_produksi");
                row[2] = rs.getString("kode_grade");
                row[3] = rs.getInt("jumlah_keping");
                row[4] = rs.getString("nama_pegawai");
                row[5] = rs.getInt("Result");

                if (rs.getInt("Result") > 3) {
                    awas_lp++;
                    awas_kpg = awas_kpg + rs.getInt("jumlah_keping");
                    awas_gram = awas_gram + rs.getInt("berat_basah");
                    model.addRow(row);
                    x++;
                } else if (rs.getInt("Result") == 3) {
                    siaga_lp++;
                    siaga_kpg = siaga_kpg + rs.getInt("jumlah_keping");
                    siaga_gram = siaga_gram + rs.getInt("berat_basah");
                    model.addRow(row);
                    x++;
                } else {
                    normal_lp++;
                    normal_kpg = normal_kpg + rs.getInt("jumlah_keping");
                    normal_gram = normal_gram + rs.getInt("berat_basah");
                }

            }
            Table_WIP_Cabut2.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if ((int) Table_WIP_Cabut2.getValueAt(row, 5) > 3) {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_Cabut2.getSelectionBackground());
                            comp.setForeground(Table_WIP_Cabut2.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.red);
                            comp.setForeground(Color.WHITE);
                        }
                    } else if ((int) Table_WIP_Cabut2.getValueAt(row, 5) == 3) {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_Cabut2.getSelectionBackground());
                            comp.setForeground(Table_WIP_Cabut2.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.yellow);
                            comp.setForeground(Color.BLACK);
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_Cabut2.getSelectionBackground());
                            comp.setForeground(Table_WIP_Cabut2.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.cyan);
                            comp.setForeground(Color.BLACK);
                        }
                    }
                    return comp;
                }
            });
            Table_WIP_Cabut2.repaint();
            Table_WIP_Cabut2.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if ((int) Table_WIP_Cabut2.getValueAt(row, 5) > 3) {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_Cabut2.getSelectionBackground());
                            comp.setForeground(Table_WIP_Cabut2.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.red);
                            comp.setForeground(Color.WHITE);
                        }
                    } else if ((int) Table_WIP_Cabut2.getValueAt(row, 5) == 3) {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_Cabut2.getSelectionBackground());
                            comp.setForeground(Table_WIP_Cabut2.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.yellow);
                            comp.setForeground(Color.BLACK);
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_Cabut2.getSelectionBackground());
                            comp.setForeground(Table_WIP_Cabut2.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.cyan);
                            comp.setForeground(Color.BLACK);
                        }
                    }
                    return comp;
                }
            });
            Table_WIP_Cabut2.repaint();
            ColumnsAutoSizer.sizeColumnsToFit(Table_WIP_Cabut2);

            label_awas2.setText(Integer.toString(awas_lp));
            label_siaga2.setText(Integer.toString(siaga_lp));
            label_normal2.setText(Integer.toString(normal_lp));

        } catch (SQLException ex) {
            Logger.getLogger(JFrame_Tampilan_Proses_LP2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_Cetak() {
        try {
            label_judul1.setText("CETAK");
            label_target_awas.setText(">2");
            label_target_siaga.setText("=2");
            label_target_normal.setText("<2");
            DefaultTableModel model = (DefaultTableModel) Table_WIP_Cetak.getModel();
            model.setRowCount(0);
            int x = 1;
            int awas_lp = 0, awas_gram = 0, awas_kpg = 0;
            int siaga_lp = 0, siaga_gram = 0, siaga_kpg = 0;
            int normal_lp = 0, normal_gram = 0, normal_kpg = 0;
            Object[] row = new Object[6];
            sql = "SELECT `tb_cetak`.`no_laporan_produksi`, `tb_laporan_produksi`.`kode_grade`, `tb_laporan_produksi`.`jumlah_keping`, `tb_laporan_produksi`.`berat_basah`, `tgl_mulai_cetak`, A.`nama_pegawai`, DATEDIFF(CURRENT_DATE(), `tgl_mulai_cetak`) AS 'Result'\n"
                    + "FROM `tb_cetak` LEFT JOIN `tb_laporan_produksi` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_karyawan` A ON `tb_cetak`.`cetak_dikerjakan` = A.`id_pegawai`\n"
                    + "LEFT JOIN `tb_karyawan` B ON `tb_cetak`.`cetak_diterima` = B.`nama_pegawai`\n"
                    + "WHERE `tb_laporan_produksi`.`ruangan` IN (" + filter_ruangan + ") AND `tb_laporan_produksi`.`memo_lp` NOT LIKE '%JDN%'\n"
                    + "AND `tb_cetak`.`tgl_selesai_cetak` IS NULL\n"
                    + "GROUP BY `tb_cetak`.`no_laporan_produksi`\n"
                    + "ORDER BY `result` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                row[0] = x;
                row[1] = rs.getString("no_laporan_produksi");
                row[2] = rs.getString("kode_grade");
                row[3] = rs.getInt("jumlah_keping");
                row[4] = rs.getString("nama_pegawai");
                row[5] = rs.getInt("Result");

                if (rs.getInt("Result") > 2) {
                    awas_lp++;
                    awas_kpg = awas_kpg + rs.getInt("jumlah_keping");
                    awas_gram = awas_gram + rs.getInt("berat_basah");
                    model.addRow(row);
                    x++;
                } else if (rs.getInt("Result") == 2) {
                    siaga_lp++;
                    siaga_kpg = siaga_kpg + rs.getInt("jumlah_keping");
                    siaga_gram = siaga_gram + rs.getInt("berat_basah");
                    model.addRow(row);
                    x++;
                } else {
                    normal_lp++;
                    normal_kpg = normal_kpg + rs.getInt("jumlah_keping");
                    normal_gram = normal_gram + rs.getInt("berat_basah");
                }
            }
            Table_WIP_Cetak.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if ((int) Table_WIP_Cetak.getValueAt(row, 5) > 2) {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_Cetak.getSelectionBackground());
                            comp.setForeground(Table_WIP_Cetak.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.red);
                            comp.setForeground(Color.WHITE);
                        }
                    } else if ((int) Table_WIP_Cetak.getValueAt(row, 5) == 2) {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_Cetak.getSelectionBackground());
                            comp.setForeground(Table_WIP_Cetak.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.yellow);
                            comp.setForeground(Color.BLACK);
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_Cetak.getSelectionBackground());
                            comp.setForeground(Table_WIP_Cetak.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.cyan);
                            comp.setForeground(Color.BLACK);
                        }
                    }
                    return comp;
                }
            });
            Table_WIP_Cetak.repaint();
            Table_WIP_Cetak.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if ((int) Table_WIP_Cetak.getValueAt(row, 5) > 2) {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_Cetak.getSelectionBackground());
                            comp.setForeground(Table_WIP_Cetak.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.red);
                            comp.setForeground(Color.WHITE);
                        }
                    } else if ((int) Table_WIP_Cetak.getValueAt(row, 5) == 2) {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_Cetak.getSelectionBackground());
                            comp.setForeground(Table_WIP_Cetak.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.yellow);
                            comp.setForeground(Color.BLACK);
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_Cetak.getSelectionBackground());
                            comp.setForeground(Table_WIP_Cetak.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.cyan);
                            comp.setForeground(Color.BLACK);
                        }
                    }
                    return comp;
                }
            });
            Table_WIP_Cetak.repaint();
            ColumnsAutoSizer.sizeColumnsToFit(Table_WIP_Cetak);

            label_awas3.setText(Integer.toString(awas_lp));
            label_siaga3.setText(Integer.toString(siaga_lp));
            label_normal3.setText(Integer.toString(normal_lp));

        } catch (SQLException ex) {
            Logger.getLogger(JFrame_Tampilan_Proses_LP2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Mlem() {
        try {
            int jumlah_bulan = 6;
            int[] bulan = new int[jumlah_bulan];
            int[] tahun = new int[jumlah_bulan];
            float[] MLem = new float[jumlah_bulan];
            float[] TotalGram = new float[jumlah_bulan];

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
            String header_bulan = "";
            DateFormatSymbols dfs = new DateFormatSymbols();
            String[] months = dfs.getMonths();

            for (int x = 0; x < jumlah_bulan; x++) {
                header_bulan = months[bulan[x] - 1].substring(0, 3) + " " + tahun[x];
                TableColumn tc = TColumnModel.getColumn(x + 1);
                tc.setHeaderValue(header_bulan);
            }

            DefaultTableModel model = (DefaultTableModel) Table_data_mlem.getModel();
            model.setRowCount(0);
            Table_data_mlem.getTableHeader().setFont(new Font("Arial Narrow", Font.BOLD, 20));

            sql = "SELECT MONTH(`tanggal_grading`) AS 'bulan', YEAR(`tanggal_grading`) AS 'tahun', `ruangan`, SUM(`tb_grading_bahan_jadi`.`keping`) AS 'keping'\n"
                    + "FROM `tb_laporan_produksi` \n"
                    + "LEFT JOIN `tb_grading_bahan_jadi` ON `tb_grading_bahan_jadi`.`kode_asal_bahan_jadi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_grading_bahan_jadi`.`kode_asal_bahan_jadi` = `tb_bahan_jadi_masuk`.`kode_asal`\n"
                    + "WHERE `ruangan` IN (" + filter_ruangan + ") AND `tanggal_grading` BETWEEN '" + tahun[jumlah_bulan - 1] + "-" + bulan[jumlah_bulan - 1] + "-01' AND '" + tahun[0] + "-" + bulan[0] + "-31' AND `grade_bahan_jadi` = 033 "
                    + "GROUP BY MONTH(`tanggal_grading`), YEAR(`tanggal_grading`)\n"
                    + "ORDER BY `tb_bahan_jadi_masuk`.`tanggal_grading`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                for (int i = 0; i < jumlah_bulan; i++) {
                    if (rs.getInt("bulan") == bulan[i]) {
                        MLem[i] = rs.getInt("keping");
                    }
                }
            }
            sql = "SELECT MONTH(`tanggal_grading`) AS 'bulan', YEAR(`tanggal_grading`) AS 'tahun', `ruangan`, SUM(`tb_grading_bahan_jadi`.`keping`) AS 'keping'\n"
                    + "FROM `tb_laporan_produksi` \n"
                    + "LEFT JOIN `tb_grading_bahan_jadi` ON `tb_grading_bahan_jadi`.`kode_asal_bahan_jadi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_grading_bahan_jadi`.`kode_asal_bahan_jadi` = `tb_bahan_jadi_masuk`.`kode_asal`\n"
                    + "WHERE `ruangan` IN (" + filter_ruangan + ") AND `tanggal_grading` BETWEEN '" + tahun[jumlah_bulan - 1] + "-" + bulan[jumlah_bulan - 1] + "-01' AND '" + tahun[0] + "-" + bulan[0] + "-31' "
                    + "GROUP BY MONTH(`tanggal_grading`), YEAR(`tanggal_grading`)\n"
                    + "ORDER BY `tb_bahan_jadi_masuk`.`tanggal_grading`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                for (int i = 0; i < jumlah_bulan; i++) {
                    if (rs.getInt("bulan") == bulan[i]) {
                        TotalGram[i] = rs.getInt("keping");
                    }
                }
            }

            decimalFormat.setMaximumFractionDigits(2);
            Object[] row = new Object[13];
            row[0] = "Persentase MLEM";
            for (int i = 0; i < jumlah_bulan; i++) {
                row[i + 1] = decimalFormat.format((MLem[i] / TotalGram[i]) * 100.f) + "%  ";
            }
            model.addRow(row);
        } catch (SQLException e) {
            Logger.getLogger(JFrame_Tampilan_Proses_LP2.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void refreshTable_Cetak_jidun() {
        try {
            label_judul1.setText("CETAK JDN");
            label_target_awas.setText(">3");
            label_target_siaga.setText("=3");
            label_target_normal.setText("<3");
            DefaultTableModel model = (DefaultTableModel) Table_WIP_Cetak_Jidun.getModel();
            model.setRowCount(0);
            int x = 1;
            int awas_lp = 0, awas_gram = 0, awas_kpg = 0;
            int siaga_lp = 0, siaga_gram = 0, siaga_kpg = 0;
            int normal_lp = 0, normal_gram = 0, normal_kpg = 0;
            Object[] row = new Object[6];
            sql = "SELECT `tb_cetak`.`no_laporan_produksi`, `tb_laporan_produksi`.`kode_grade`, `tb_laporan_produksi`.`jumlah_keping`, `tb_laporan_produksi`.`berat_basah`, `tgl_mulai_cetak`, A.`nama_pegawai`, DATEDIFF(CURRENT_DATE(), `tgl_mulai_cetak`) AS 'Result'\n"
                    + "FROM `tb_cetak` LEFT JOIN `tb_laporan_produksi` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_karyawan` A ON `tb_cetak`.`cetak_dikerjakan` = A.`id_pegawai`\n"
                    + "LEFT JOIN `tb_karyawan` B ON `tb_cetak`.`cetak_diterima` = B.`nama_pegawai`\n"
                    + "WHERE `tb_laporan_produksi`.`ruangan` IN (" + filter_ruangan + ") AND `tb_laporan_produksi`.`memo_lp` LIKE '%JDN%'\n"
                    + "AND `tb_cetak`.`tgl_selesai_cetak` IS NULL\n"
                    + "GROUP BY `tb_cetak`.`no_laporan_produksi`\n"
                    + "ORDER BY `result` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                row[0] = x;
                row[1] = rs.getString("no_laporan_produksi");
                row[2] = rs.getString("kode_grade");
                row[3] = rs.getInt("jumlah_keping");
                row[4] = rs.getString("nama_pegawai");
                row[5] = rs.getInt("Result");

                if (rs.getInt("Result") > 3) {
                    awas_lp++;
                    awas_kpg = awas_kpg + rs.getInt("jumlah_keping");
                    awas_gram = awas_gram + rs.getInt("berat_basah");
                    model.addRow(row);
                    x++;
                } else if (rs.getInt("Result") == 3) {
                    siaga_lp++;
                    siaga_kpg = siaga_kpg + rs.getInt("jumlah_keping");
                    siaga_gram = siaga_gram + rs.getInt("berat_basah");
                    model.addRow(row);
                    x++;
                } else {
                    normal_lp++;
                    normal_kpg = normal_kpg + rs.getInt("jumlah_keping");
                    normal_gram = normal_gram + rs.getInt("berat_basah");
                }
            }
            Table_WIP_Cetak_Jidun.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if ((int) Table_WIP_Cetak_Jidun.getValueAt(row, 5) > 3) {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_Cetak_Jidun.getSelectionBackground());
                            comp.setForeground(Table_WIP_Cetak_Jidun.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.red);
                            comp.setForeground(Color.WHITE);
                        }
                    } else if ((int) Table_WIP_Cetak_Jidun.getValueAt(row, 5) == 3) {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_Cetak_Jidun.getSelectionBackground());
                            comp.setForeground(Table_WIP_Cetak_Jidun.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.yellow);
                            comp.setForeground(Color.BLACK);
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_Cetak_Jidun.getSelectionBackground());
                            comp.setForeground(Table_WIP_Cetak_Jidun.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.cyan);
                            comp.setForeground(Color.BLACK);
                        }
                    }
                    return comp;
                }
            });
            Table_WIP_Cetak_Jidun.repaint();
            Table_WIP_Cetak_Jidun.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if ((int) Table_WIP_Cetak_Jidun.getValueAt(row, 5) > 3) {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_Cetak_Jidun.getSelectionBackground());
                            comp.setForeground(Table_WIP_Cetak_Jidun.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.red);
                            comp.setForeground(Color.WHITE);
                        }
                    } else if ((int) Table_WIP_Cetak_Jidun.getValueAt(row, 5) == 3) {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_Cetak_Jidun.getSelectionBackground());
                            comp.setForeground(Table_WIP_Cetak_Jidun.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.yellow);
                            comp.setForeground(Color.BLACK);
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_Cetak_Jidun.getSelectionBackground());
                            comp.setForeground(Table_WIP_Cetak_Jidun.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.cyan);
                            comp.setForeground(Color.BLACK);
                        }
                    }
                    return comp;
                }
            });
            Table_WIP_Cetak_Jidun.repaint();
            ColumnsAutoSizer.sizeColumnsToFit(Table_WIP_Cetak_Jidun);

            label_awas5.setText(Integer.toString(awas_lp));
            label_siaga5.setText(Integer.toString(siaga_lp));
            label_normal5.setText(Integer.toString(normal_lp));

        } catch (SQLException ex) {
            Logger.getLogger(JFrame_Tampilan_Proses_LP2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_f2() {
        try {
            label_judul1.setText("FINISHING 2");
            label_target_awas.setText(">3");
            label_target_siaga.setText("=3");
            label_target_normal.setText("<3");
            DefaultTableModel model = (DefaultTableModel) Table_WIP_Finishing2.getModel();
            model.setRowCount(0);
            int x = 1;
            int awas_lp = 0, awas_gram = 0, awas_kpg = 0;
            int siaga_lp = 0, siaga_gram = 0, siaga_kpg = 0;
            int normal_lp = 0, normal_gram = 0, normal_kpg = 0;
            Object[] row = new Object[6];
            sql = "SELECT `tb_finishing_2`.`no_laporan_produksi`, `tb_laporan_produksi`.`kode_grade`, `tb_laporan_produksi`.`jumlah_keping`, `tb_laporan_produksi`.`berat_basah`,`tgl_input_byProduct`, `tgl_masuk_f2`, `tgl_dikerjakan_f2`, `tgl_f1`, `tgl_f2`, `tgl_setor_f2`, DATEDIFF(CURRENT_DATE(), `tgl_masuk_f2`) AS 'Result'\n"
                    + "FROM `tb_finishing_2` LEFT JOIN `tb_laporan_produksi` ON `tb_finishing_2`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "WHERE `tb_laporan_produksi`.`ruangan` IN (" + filter_ruangan + ")\n"
                    + "AND `tb_finishing_2`.`tgl_setor_f2` IS NULL AND `tgl_f2` IS NULL\n"
                    + "GROUP BY `tb_finishing_2`.`no_laporan_produksi`\n"
                    + "ORDER BY `result` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                row[0] = x;
                row[1] = rs.getString("no_laporan_produksi");
                row[2] = rs.getString("kode_grade");
                row[3] = rs.getInt("jumlah_keping");
                if (rs.getDate("tgl_masuk_f2") == null) {
                    row[4] = "LP Belum Masuk";
                } else if (rs.getDate("tgl_dikerjakan_f2") == null) {
                    row[4] = "Koreksi Kering";
                } else if (rs.getDate("tgl_f1") == null) {
                    row[4] = "F1";
                } else if (rs.getDate("tgl_f2") == null) {
                    row[4] = "F2";
                } else {
                    row[4] = "Final Check";
                }
                row[5] = rs.getInt("Result");

                if (rs.getInt("Result") > 3) {
                    awas_lp++;
                    awas_kpg = awas_kpg + rs.getInt("jumlah_keping");
                    awas_gram = awas_gram + rs.getInt("berat_basah");
                    model.addRow(row);
                    x++;
                } else if (rs.getInt("Result") == 3) {
                    siaga_lp++;
                    siaga_kpg = siaga_kpg + rs.getInt("jumlah_keping");
                    siaga_gram = siaga_gram + rs.getInt("berat_basah");
                    model.addRow(row);
                    x++;
                } else {
                    normal_lp++;
                    normal_kpg = normal_kpg + rs.getInt("jumlah_keping");
                    normal_gram = normal_gram + rs.getInt("berat_basah");
                }
            }
            Table_WIP_Finishing2.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if ((int) Table_WIP_Finishing2.getValueAt(row, 5) > 3) {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_Finishing2.getSelectionBackground());
                            comp.setForeground(Table_WIP_Finishing2.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.red);
                            comp.setForeground(Color.WHITE);
                        }
                    } else if ((int) Table_WIP_Finishing2.getValueAt(row, 5) == 3) {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_Finishing2.getSelectionBackground());
                            comp.setForeground(Table_WIP_Finishing2.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.yellow);
                            comp.setForeground(Color.BLACK);
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_Finishing2.getSelectionBackground());
                            comp.setForeground(Table_WIP_Finishing2.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.cyan);
                            comp.setForeground(Color.BLACK);
                        }
                    }
                    return comp;
                }
            });
            Table_WIP_Finishing2.repaint();
            Table_WIP_Finishing2.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if ((int) Table_WIP_Finishing2.getValueAt(row, 5) > 3) {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_Finishing2.getSelectionBackground());
                            comp.setForeground(Table_WIP_Finishing2.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.red);
                            comp.setForeground(Color.WHITE);
                        }
                    } else if ((int) Table_WIP_Finishing2.getValueAt(row, 5) == 3) {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_Finishing2.getSelectionBackground());
                            comp.setForeground(Table_WIP_Finishing2.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.yellow);
                            comp.setForeground(Color.BLACK);
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(Table_WIP_Finishing2.getSelectionBackground());
                            comp.setForeground(Table_WIP_Finishing2.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.cyan);
                            comp.setForeground(Color.BLACK);
                        }
                    }
                    return comp;
                }
            });
            Table_WIP_Finishing2.repaint();
            ColumnsAutoSizer.sizeColumnsToFit(Table_WIP_Finishing2);

            label_awas4.setText(Integer.toString(awas_lp));
            label_siaga4.setText(Integer.toString(siaga_lp));
            label_normal4.setText(Integer.toString(normal_lp));

        } catch (SQLException ex) {
            Logger.getLogger(JFrame_Tampilan_Proses_LP2.class.getName()).log(Level.SEVERE, null, ex);
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
            ColumnsAutoSizer.sizeColumnsToFit(Table_Data_Nitrit_jadi);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JFrame_Tampilan_Proses_LP2.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(JFrame_Tampilan_Proses_LP2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//    public void refreshTable_daily() {
//        try {
//            DefaultTableModel model = (DefaultTableModel) Tabel_jumlah_hadir.getModel();
//            model.setRowCount(0);
//            Object[] row = new Object[2];
//            sql = "SELECT COUNT(`id_pegawai`) AS 'jumlah',  `tb_bagian`.`nama_bagian`\n"
//                    + "FROM `tb_karyawan` LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
//                    + "LEFT JOIN `att_log` ON `tb_karyawan`.`pin_finger` = `att_log`.`pin`\n"
//                    + "WHERE `tb_bagian`.`nama_bagian` IN ('F2 JIDUN', 'FINISHING 2', 'CUCI " + ruang + "', 'CUCI HANCURAN " + ruang + "', 'CUCI KOPYOK " + ruang + "', 'CABUT " + ruang + "', 'KOREKSI CABUT " + ruang + "', 'PENGAWAS " + ruang + "', 'ASISTEN PENGAWAS " + ruang + "', 'CETAK " + ruang + "') "
//                    + "AND DATE(`att_log`.`scan_date`) = '" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "'\n"
//                    + "GROUP BY `tb_bagian`.`nama_bagian`"
//                    + "ORDER BY `nama_bagian`";
//            rs = Utility.db.getStatement().executeQuery(sql);
//            while (rs.next()) {
//                row[0] = rs.getString("nama_bagian");
//                row[1] = rs.getString("jumlah");
//                model.addRow(row);
//            }
//
//            sql = "SELECT SUM(`tb_laporan_produksi`.`jumlah_keping`) AS 'kpg', SUM(`tb_laporan_produksi`.`berat_basah`) AS 'gram' "
//                    + "FROM `tb_cuci` LEFT JOIN `tb_laporan_produksi` ON `tb_cuci`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi` "
//                    + "WHERE `tb_laporan_produksi`.`ruangan` LIKE '%" + ruang + "%' AND `tgl_masuk_cuci` = '" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "'";
//            rs = Utility.db.getStatement().executeQuery(sql);
//            if (rs.next()) {
//                label_kpg_total_cucian.setText(decimalFormat.format(rs.getFloat("kpg")));
//                label_gram_total_cucian.setText(decimalFormat.format(rs.getFloat("gram")));
//            }
//
//            sql = "SELECT SUM(`tb_laporan_produksi`.`jumlah_keping`) AS 'kpg', SUM(`tb_laporan_produksi`.`berat_basah`) AS 'gram' "
//                    + "FROM `tb_cabut` LEFT JOIN `tb_laporan_produksi` ON `tb_cabut`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi` "
//                    + "WHERE `tb_laporan_produksi`.`ruangan` LIKE '%" + ruang + "%' AND `tgl_setor_cabut` = '" + new SimpleDateFormat("yyyy-MM-dd").format(new Date(new Date().getTime() - (1 * 24 * 60 * 60 * 1000))) + "'";
//            rs = Utility.db.getStatement().executeQuery(sql);
//            if (rs.next()) {
//                label_kpg_total_setoran_cabut.setText(decimalFormat.format(rs.getFloat("kpg")));
//                label_gram_total_setoran_cabut.setText(decimalFormat.format(rs.getFloat("gram")));
//            }
//
//            sql = "SELECT SUM(`tb_laporan_produksi`.`jumlah_keping`) AS 'kpg', SUM(`tb_laporan_produksi`.`berat_basah`) AS 'gram' "
//                    + "FROM `tb_cetak` LEFT JOIN `tb_laporan_produksi` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi` "
//                    + "WHERE `tb_laporan_produksi`.`ruangan` LIKE '%" + ruang + "%' AND `tgl_selesai_cetak` = '" + new SimpleDateFormat("yyyy-MM-dd").format(new Date(new Date().getTime() - (1 * 24 * 60 * 60 * 1000))) + "'";
//            rs = Utility.db.getStatement().executeQuery(sql);
//            if (rs.next()) {
//                label_kpg_total_setoran_cetak.setText(decimalFormat.format(rs.getFloat("kpg")));
//                label_gram_total_setoran_cetak.setText(decimalFormat.format(rs.getFloat("gram")));
//            }
//
//            sql = "SELECT `tanggal_cabut`, AVG(ROUND(`jumlah_gram`/8)) AS 'keping', `tb_laporan_produksi`.`ruangan`\n"
//                    + "FROM `tb_detail_pencabut` LEFT JOIN `tb_laporan_produksi` ON `tb_detail_pencabut`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`"
//                    + "WHERE `tb_laporan_produksi`.`ruangan` LIKE '%" + ruang + "%' AND `tanggal_cabut` = '" + new SimpleDateFormat("yyyy-MM-dd").format(new Date(new Date().getTime() - (1 * 24 * 60 * 60 * 1000))) + "'";
//            rs = Utility.db.getStatement().executeQuery(sql);
//            if (rs.next()) {
//                label_rata2_cabutan.setText(decimalFormat.format(rs.getFloat("keping")));
//            }
//
//            ColumnsAutoSizer.sizeColumnsToFit(Tabel_jumlah_hadir);
//        } catch (SQLException ex) {
//            Logger.getLogger(JFrame_Tampilan_Proses_LP2.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    public void refreshIsu1() {
        try {
            int data = 0;
            sql = "SELECT * FROM `tb_isu_produksi` WHERE (`dept` LIKE '%ALL%' OR `dept` LIKE '%CABUT%' OR `dept` LIKE '%CETAK%') AND `tanggal_selesai` IS NULL ORDER BY `tanggal_isu` DESC";
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
            Logger.getLogger(JFrame_Tampilan_Proses_LP2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshIsu2() {
        try {
            int data = 0;
            sql = "SELECT * FROM `tb_isu_produksi` WHERE (`dept` LIKE '%ALL%' OR `dept` LIKE '%CABUT%' OR `dept` LIKE '%CETAK%') AND `tanggal_selesai` IS NULL ORDER BY `tanggal_isu` DESC";
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
            Logger.getLogger(JFrame_Tampilan_Proses_LP2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshIsu3() {
        try {
            int data = 0;
            sql = "SELECT * FROM `tb_isu_produksi` WHERE (`dept` LIKE '%ALL%' OR `dept` LIKE '%CABUT%' OR `dept` LIKE '%CETAK%') AND `tanggal_selesai` IS NULL ORDER BY `tanggal_isu` DESC";
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
            Logger.getLogger(JFrame_Tampilan_Proses_LP2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void refreshIsu4() {
        try {
            Image image;
            int data = 0;
            sql = "SELECT * FROM `tb_isu_produksi` WHERE (`dept` LIKE '%ALL%' OR `dept` LIKE '%CABUT%' OR `dept` LIKE '%CETAK%') AND `tanggal_selesai` IS NULL ORDER BY `tanggal_isu` DESC";
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
            Logger.getLogger(JFrame_Tampilan_Proses_LP2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void refreshIsu5() {
        try {
            int data = 0;
            sql = "SELECT * FROM `tb_isu_produksi` WHERE (`dept` LIKE '%ALL%' OR `dept` LIKE '%CABUT%' OR `dept` LIKE '%CETAK%') AND `tanggal_selesai` IS NULL ORDER BY `tanggal_isu` DESC";
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
            Logger.getLogger(JFrame_Tampilan_Proses_LP2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel_Cabut = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_WIP_Cabut1 = new javax.swing.JTable();
        jPanel_SetorSusutan = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Table_WIP_Cabut2 = new javax.swing.JTable();
        jPanel_Cetak = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        Table_WIP_Cetak = new javax.swing.JTable();
        jScrollPane10 = new javax.swing.JScrollPane();
        Table_data_mlem = new javax.swing.JTable();
        jPanel_Cetak_Jidun = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        Table_WIP_Cetak_Jidun = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jPanel_Finishing2 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        Table_WIP_Finishing2 = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        label_total_kartu_jadi_oren = new javax.swing.JLabel();
        label_total_kartu_jadi_putih = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        Table_Data_Nitrit_jadi = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        label_jumlah_kartu_hold = new javax.swing.JLabel();
        label_total_kartu_jadi_merah = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane16 = new javax.swing.JScrollPane();
        Table_Data_nitrit_baku = new javax.swing.JTable();
        jLabel20 = new javax.swing.JLabel();
        label_tgl_nitrit_baku = new javax.swing.JLabel();
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
        jLabel37 = new javax.swing.JLabel();
        label_tgl_isu3 = new javax.swing.JLabel();
        label_departemen3 = new javax.swing.JLabel();
        label_image3 = new javax.swing.JLabel();
        label_penanggungjawab3 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        label_kode3 = new javax.swing.JLabel();
        jScrollPane14 = new javax.swing.JScrollPane();
        jTextArea4 = new javax.swing.JTextArea();
        jPanel_isu5 = new javax.swing.JPanel();
        jLabel41 = new javax.swing.JLabel();
        label_tgl_isu4 = new javax.swing.JLabel();
        label_departemen4 = new javax.swing.JLabel();
        label_image4 = new javax.swing.JLabel();
        label_penanggungjawab4 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        label_kode4 = new javax.swing.JLabel();
        jScrollPane15 = new javax.swing.JScrollPane();
        jTextArea5 = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        label_awas1 = new javax.swing.JLabel();
        label_siaga1 = new javax.swing.JLabel();
        label_normal1 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        label_awas2 = new javax.swing.JLabel();
        label_siaga2 = new javax.swing.JLabel();
        label_normal2 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        label_awas3 = new javax.swing.JLabel();
        label_siaga3 = new javax.swing.JLabel();
        label_normal3 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        label_awas4 = new javax.swing.JLabel();
        label_siaga4 = new javax.swing.JLabel();
        label_normal4 = new javax.swing.JLabel();
        label_judul = new javax.swing.JLabel();
        space = new javax.swing.JLabel();
        space1 = new javax.swing.JLabel();
        space2 = new javax.swing.JLabel();
        space3 = new javax.swing.JLabel();
        space4 = new javax.swing.JLabel();
        space5 = new javax.swing.JLabel();
        space6 = new javax.swing.JLabel();
        space7 = new javax.swing.JLabel();
        label_judul1 = new javax.swing.JLabel();
        label_waktu = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        label_awas5 = new javax.swing.JLabel();
        label_siaga5 = new javax.swing.JLabel();
        label_normal5 = new javax.swing.JLabel();
        space8 = new javax.swing.JLabel();
        space9 = new javax.swing.JLabel();
        label_target_awas = new javax.swing.JLabel();
        space10 = new javax.swing.JLabel();
        label_target_siaga = new javax.swing.JLabel();
        space11 = new javax.swing.JLabel();
        label_target_normal = new javax.swing.JLabel();
        space12 = new javax.swing.JLabel();

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

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 0, 8)); // NOI18N
        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });

        jPanel_Cabut.setBackground(new java.awt.Color(255, 255, 255));

        Table_WIP_Cabut1.setFont(new java.awt.Font("Arial Narrow", 1, 36)); // NOI18N
        Table_WIP_Cabut1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "No LP", "Grade", "Kpg", "Pekerja", "Lama Inap (Hari)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class
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
        Table_WIP_Cabut1.setRowHeight(35);
        Table_WIP_Cabut1.setRowSelectionAllowed(false);
        Table_WIP_Cabut1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Table_WIP_Cabut1);
        if (Table_WIP_Cabut1.getColumnModel().getColumnCount() > 0) {
            Table_WIP_Cabut1.getColumnModel().getColumn(4).setMaxWidth(270);
        }

        javax.swing.GroupLayout jPanel_CabutLayout = new javax.swing.GroupLayout(jPanel_Cabut);
        jPanel_Cabut.setLayout(jPanel_CabutLayout);
        jPanel_CabutLayout.setHorizontalGroup(
            jPanel_CabutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1116, Short.MAX_VALUE)
        );
        jPanel_CabutLayout.setVerticalGroup(
            jPanel_CabutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("CABUT", jPanel_Cabut);

        jPanel_SetorSusutan.setBackground(new java.awt.Color(255, 255, 255));

        Table_WIP_Cabut2.setFont(new java.awt.Font("Arial Narrow", 1, 36)); // NOI18N
        Table_WIP_Cabut2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "No LP", "Grade", "Kpg", "Pekerja", "Lama Inap (Hari)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class
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
        Table_WIP_Cabut2.setRowHeight(35);
        Table_WIP_Cabut2.setRowSelectionAllowed(false);
        Table_WIP_Cabut2.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(Table_WIP_Cabut2);
        if (Table_WIP_Cabut2.getColumnModel().getColumnCount() > 0) {
            Table_WIP_Cabut2.getColumnModel().getColumn(4).setMaxWidth(270);
        }

        javax.swing.GroupLayout jPanel_SetorSusutanLayout = new javax.swing.GroupLayout(jPanel_SetorSusutan);
        jPanel_SetorSusutan.setLayout(jPanel_SetorSusutanLayout);
        jPanel_SetorSusutanLayout.setHorizontalGroup(
            jPanel_SetorSusutanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1116, Short.MAX_VALUE)
        );
        jPanel_SetorSusutanLayout.setVerticalGroup(
            jPanel_SetorSusutanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("SETOR SUSUTAN", jPanel_SetorSusutan);

        jPanel_Cetak.setBackground(new java.awt.Color(255, 255, 255));

        Table_WIP_Cetak.setFont(new java.awt.Font("Arial Narrow", 1, 36)); // NOI18N
        Table_WIP_Cetak.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "No LP", "Grade", "Kpg", "Pekerja", "Lama Inap (Hari)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class
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
        Table_WIP_Cetak.setRowHeight(35);
        Table_WIP_Cetak.setRowSelectionAllowed(false);
        Table_WIP_Cetak.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(Table_WIP_Cetak);
        if (Table_WIP_Cetak.getColumnModel().getColumnCount() > 0) {
            Table_WIP_Cetak.getColumnModel().getColumn(4).setMaxWidth(300);
        }

        Table_data_mlem.setFont(new java.awt.Font("Arial Narrow", 1, 32)); // NOI18N
        Table_data_mlem.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Persentase MLem", "", null, null, null, null, null}
            },
            new String [] {
                "Deskripsi", "1", "2", "3", "4", "5", "6"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        Table_data_mlem.setFocusable(false);
        Table_data_mlem.setRowHeight(40);
        Table_data_mlem.setRowSelectionAllowed(false);
        Table_data_mlem.getTableHeader().setReorderingAllowed(false);
        jScrollPane10.setViewportView(Table_data_mlem);
        if (Table_data_mlem.getColumnModel().getColumnCount() > 0) {
            Table_data_mlem.getColumnModel().getColumn(0).setMinWidth(180);
        }

        javax.swing.GroupLayout jPanel_CetakLayout = new javax.swing.GroupLayout(jPanel_Cetak);
        jPanel_Cetak.setLayout(jPanel_CetakLayout);
        jPanel_CetakLayout.setHorizontalGroup(
            jPanel_CetakLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1116, Short.MAX_VALUE)
            .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 1116, Short.MAX_VALUE)
        );
        jPanel_CetakLayout.setVerticalGroup(
            jPanel_CetakLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_CetakLayout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 527, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPane1.addTab("CETAK", jPanel_Cetak);

        jPanel_Cetak_Jidun.setBackground(new java.awt.Color(255, 255, 255));

        Table_WIP_Cetak_Jidun.setFont(new java.awt.Font("Arial Narrow", 1, 36)); // NOI18N
        Table_WIP_Cetak_Jidun.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "No LP", "Grade", "Kpg", "Pekerja", "Lama Inap (Hari)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class
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
        Table_WIP_Cetak_Jidun.setRowHeight(35);
        Table_WIP_Cetak_Jidun.setRowSelectionAllowed(false);
        Table_WIP_Cetak_Jidun.getTableHeader().setReorderingAllowed(false);
        jScrollPane6.setViewportView(Table_WIP_Cetak_Jidun);
        if (Table_WIP_Cetak_Jidun.getColumnModel().getColumnCount() > 0) {
            Table_WIP_Cetak_Jidun.getColumnModel().getColumn(4).setMaxWidth(300);
        }

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 2, 12)); // NOI18N
        jLabel1.setText("NOTE : cetak jidun hanya LP yang punya memo \"JDN\"");

        javax.swing.GroupLayout jPanel_Cetak_JidunLayout = new javax.swing.GroupLayout(jPanel_Cetak_Jidun);
        jPanel_Cetak_Jidun.setLayout(jPanel_Cetak_JidunLayout);
        jPanel_Cetak_JidunLayout.setHorizontalGroup(
            jPanel_Cetak_JidunLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 1116, Short.MAX_VALUE)
            .addGroup(jPanel_Cetak_JidunLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_Cetak_JidunLayout.setVerticalGroup(
            jPanel_Cetak_JidunLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Cetak_JidunLayout.createSequentialGroup()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 588, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addContainerGap())
        );

        jTabbedPane1.addTab("CETAK JDN", jPanel_Cetak_Jidun);

        jPanel_Finishing2.setBackground(new java.awt.Color(255, 255, 255));

        Table_WIP_Finishing2.setFont(new java.awt.Font("Arial Narrow", 1, 36)); // NOI18N
        Table_WIP_Finishing2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "No LP", "Grade", "Kpg", "Posisi", "Lama Inap (Hari)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class
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
        Table_WIP_Finishing2.setRowHeight(35);
        Table_WIP_Finishing2.setRowSelectionAllowed(false);
        Table_WIP_Finishing2.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(Table_WIP_Finishing2);

        javax.swing.GroupLayout jPanel_Finishing2Layout = new javax.swing.GroupLayout(jPanel_Finishing2);
        jPanel_Finishing2.setLayout(jPanel_Finishing2Layout);
        jPanel_Finishing2Layout.setHorizontalGroup(
            jPanel_Finishing2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 1116, Short.MAX_VALUE)
        );
        jPanel_Finishing2Layout.setVerticalGroup(
            jPanel_Finishing2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("FINISHING 2", jPanel_Finishing2);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel15.setText("/");

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
        jScrollPane4.setViewportView(Table_Data_Nitrit_jadi);

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

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 3, 24)); // NOI18N
        jLabel6.setText("Keterangan :");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 3, 24)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 0, 255));
        jLabel9.setText("TR Sela untuk grade W3, GRS BRT, ");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 3, 24)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 0, 255));
        jLabel11.setText("GRS RGN, SORTIR, KK KNG, Kartu Khusus");

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 3, 24)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 0, 0));
        jLabel16.setText("Persentase hold >20% & Kartu Khusus : TR Sela 2,5 jam");

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 3, 24)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(195, 195, 0));
        jLabel19.setText("Persentase hold <20% : TR Sela 1,5 jam");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane4)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_jumlah_kartu_hold)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_kartu_jadi_putih)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_kartu_jadi_oren)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_kartu_jadi_merah)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(label_total_kartu_jadi_oren)
                        .addComponent(label_total_kartu_jadi_merah)
                        .addComponent(jLabel12)
                        .addComponent(jLabel15)
                        .addComponent(label_jumlah_kartu_hold)
                        .addComponent(label_total_kartu_jadi_putih)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel16)
                .addContainerGap())
        );

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

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
        jScrollPane16.setViewportView(Table_Data_nitrit_baku);

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel20.setText("NITRIT BAHAN MENTAH");

        label_tgl_nitrit_baku.setBackground(new java.awt.Color(255, 255, 255));
        label_tgl_nitrit_baku.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        label_tgl_nitrit_baku.setText("Treatment tanggal");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane16, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel20)
                            .addComponent(label_tgl_nitrit_baku))
                        .addGap(0, 132, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_tgl_nitrit_baku)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane16, javax.swing.GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("NITRIT & TREATMENT", jPanel4);

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
        jTextArea1.setBorder(null);
        jScrollPane8.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel_isu1Layout = new javax.swing.GroupLayout(jPanel_isu1);
        jPanel_isu1.setLayout(jPanel_isu1Layout);
        jPanel_isu1Layout.setHorizontalGroup(
            jPanel_isu1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_isu1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_image, javax.swing.GroupLayout.PREFERRED_SIZE, 594, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                    .addComponent(jScrollPane8))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_isu1Layout.setVerticalGroup(
            jPanel_isu1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_isu1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_isu1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_image, javax.swing.GroupLayout.PREFERRED_SIZE, 598, javax.swing.GroupLayout.PREFERRED_SIZE)
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
        jTextArea2.setBorder(null);
        jScrollPane9.setViewportView(jTextArea2);

        javax.swing.GroupLayout jPanel_isu2Layout = new javax.swing.GroupLayout(jPanel_isu2);
        jPanel_isu2.setLayout(jPanel_isu2Layout);
        jPanel_isu2Layout.setHorizontalGroup(
            jPanel_isu2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_isu2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_image1, javax.swing.GroupLayout.PREFERRED_SIZE, 584, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_isu2Layout.setVerticalGroup(
            jPanel_isu2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_isu2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_isu2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_image1, javax.swing.GroupLayout.PREFERRED_SIZE, 598, javax.swing.GroupLayout.PREFERRED_SIZE)
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
        jTextArea3.setBorder(null);
        jScrollPane7.setViewportView(jTextArea3);

        javax.swing.GroupLayout jPanel_isu3Layout = new javax.swing.GroupLayout(jPanel_isu3);
        jPanel_isu3.setLayout(jPanel_isu3Layout);
        jPanel_isu3Layout.setHorizontalGroup(
            jPanel_isu3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_isu3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_image2, javax.swing.GroupLayout.PREFERRED_SIZE, 584, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                    .addComponent(label_image2, javax.swing.GroupLayout.PREFERRED_SIZE, 598, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        jLabel37.setBackground(new java.awt.Color(255, 255, 255));
        jLabel37.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel37.setText("Departemen :");

        label_tgl_isu3.setBackground(new java.awt.Color(255, 255, 255));
        label_tgl_isu3.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_tgl_isu3.setText("-");

        label_departemen3.setBackground(new java.awt.Color(255, 255, 255));
        label_departemen3.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_departemen3.setText("-");

        label_image3.setBackground(new java.awt.Color(255, 255, 255));
        label_image3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_penanggungjawab3.setBackground(new java.awt.Color(255, 255, 255));
        label_penanggungjawab3.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_penanggungjawab3.setText("-");

        jLabel38.setBackground(new java.awt.Color(255, 255, 255));
        jLabel38.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel38.setText("Penanggung Jawab :");

        jLabel39.setBackground(new java.awt.Color(255, 255, 255));
        jLabel39.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel39.setText("Tanggal isu :");

        jLabel40.setBackground(new java.awt.Color(255, 255, 255));
        jLabel40.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel40.setText("Kode : ");

        label_kode3.setBackground(new java.awt.Color(255, 255, 255));
        label_kode3.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_kode3.setText("-");

        jScrollPane14.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane14.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N

        jTextArea4.setEditable(false);
        jTextArea4.setColumns(5);
        jTextArea4.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jTextArea4.setLineWrap(true);
        jTextArea4.setRows(2);
        jTextArea4.setText("-");
        jTextArea4.setWrapStyleWord(true);
        jTextArea4.setBorder(null);
        jScrollPane14.setViewportView(jTextArea4);

        javax.swing.GroupLayout jPanel_isu4Layout = new javax.swing.GroupLayout(jPanel_isu4);
        jPanel_isu4.setLayout(jPanel_isu4Layout);
        jPanel_isu4Layout.setHorizontalGroup(
            jPanel_isu4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_isu4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_image3, javax.swing.GroupLayout.PREFERRED_SIZE, 584, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_isu4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel_isu4Layout.createSequentialGroup()
                        .addGroup(jPanel_isu4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_isu4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(label_departemen3, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_penanggungjawab3, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_kode3, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_tgl_isu3, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane14))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_isu4Layout.setVerticalGroup(
            jPanel_isu4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_isu4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_isu4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_image3, javax.swing.GroupLayout.PREFERRED_SIZE, 598, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_isu4Layout.createSequentialGroup()
                        .addComponent(jScrollPane14, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_isu4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_isu4Layout.createSequentialGroup()
                                .addComponent(jLabel40)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel39))
                            .addGroup(jPanel_isu4Layout.createSequentialGroup()
                                .addComponent(label_kode3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_tgl_isu3)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_isu4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_isu4Layout.createSequentialGroup()
                                .addComponent(jLabel37)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel38))
                            .addGroup(jPanel_isu4Layout.createSequentialGroup()
                                .addComponent(label_departemen3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_penanggungjawab3)))))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Isu 4", jPanel_isu4);

        jPanel_isu5.setBackground(new java.awt.Color(255, 255, 255));

        jLabel41.setBackground(new java.awt.Color(255, 255, 255));
        jLabel41.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel41.setText("Departemen :");

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

        jLabel42.setBackground(new java.awt.Color(255, 255, 255));
        jLabel42.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel42.setText("Penanggung Jawab :");

        jLabel43.setBackground(new java.awt.Color(255, 255, 255));
        jLabel43.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel43.setText("Tanggal isu :");

        jLabel44.setBackground(new java.awt.Color(255, 255, 255));
        jLabel44.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel44.setText("Kode : ");

        label_kode4.setBackground(new java.awt.Color(255, 255, 255));
        label_kode4.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_kode4.setText("-");

        jScrollPane15.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane15.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N

        jTextArea5.setEditable(false);
        jTextArea5.setColumns(5);
        jTextArea5.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jTextArea5.setLineWrap(true);
        jTextArea5.setRows(2);
        jTextArea5.setText("-");
        jTextArea5.setWrapStyleWord(true);
        jTextArea5.setBorder(null);
        jScrollPane15.setViewportView(jTextArea5);

        javax.swing.GroupLayout jPanel_isu5Layout = new javax.swing.GroupLayout(jPanel_isu5);
        jPanel_isu5.setLayout(jPanel_isu5Layout);
        jPanel_isu5Layout.setHorizontalGroup(
            jPanel_isu5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_isu5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_image4, javax.swing.GroupLayout.PREFERRED_SIZE, 584, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_isu5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel_isu5Layout.createSequentialGroup()
                        .addGroup(jPanel_isu5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_isu5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(label_departemen4, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_penanggungjawab4, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_kode4, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_tgl_isu4, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane15))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_isu5Layout.setVerticalGroup(
            jPanel_isu5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_isu5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_isu5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_image4, javax.swing.GroupLayout.PREFERRED_SIZE, 598, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_isu5Layout.createSequentialGroup()
                        .addComponent(jScrollPane15, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_isu5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_isu5Layout.createSequentialGroup()
                                .addComponent(jLabel44)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel43))
                            .addGroup(jPanel_isu5Layout.createSequentialGroup()
                                .addComponent(label_kode4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_tgl_isu4)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_isu5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_isu5Layout.createSequentialGroup()
                                .addComponent(jLabel41)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel42))
                            .addGroup(jPanel_isu5Layout.createSequentialGroup()
                                .addComponent(label_departemen4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_penanggungjawab4)))))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Isu 5", jPanel_isu5);

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial Narrow", 1, 38)); // NOI18N
        jLabel4.setText("CBT :");

        label_awas1.setBackground(new java.awt.Color(255, 255, 255));
        label_awas1.setFont(new java.awt.Font("Arial Narrow", 1, 60)); // NOI18N
        label_awas1.setForeground(new java.awt.Color(255, 0, 0));
        label_awas1.setText("88");

        label_siaga1.setBackground(new java.awt.Color(255, 255, 255));
        label_siaga1.setFont(new java.awt.Font("Arial Narrow", 1, 60)); // NOI18N
        label_siaga1.setForeground(new java.awt.Color(255, 188, 0));
        label_siaga1.setText("88");

        label_normal1.setBackground(new java.awt.Color(255, 255, 255));
        label_normal1.setFont(new java.awt.Font("Arial Narrow", 1, 60)); // NOI18N
        label_normal1.setForeground(new java.awt.Color(0, 0, 255));
        label_normal1.setText("88");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial Narrow", 1, 38)); // NOI18N
        jLabel7.setText("SST :");

        label_awas2.setBackground(new java.awt.Color(255, 255, 255));
        label_awas2.setFont(new java.awt.Font("Arial Narrow", 1, 60)); // NOI18N
        label_awas2.setForeground(new java.awt.Color(255, 0, 0));
        label_awas2.setText("88");

        label_siaga2.setBackground(new java.awt.Color(255, 255, 255));
        label_siaga2.setFont(new java.awt.Font("Arial Narrow", 1, 60)); // NOI18N
        label_siaga2.setForeground(new java.awt.Color(255, 188, 0));
        label_siaga2.setText("88");

        label_normal2.setBackground(new java.awt.Color(255, 255, 255));
        label_normal2.setFont(new java.awt.Font("Arial Narrow", 1, 60)); // NOI18N
        label_normal2.setForeground(new java.awt.Color(0, 0, 255));
        label_normal2.setText("88");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial Narrow", 1, 38)); // NOI18N
        jLabel13.setText("CTK :");

        label_awas3.setBackground(new java.awt.Color(255, 255, 255));
        label_awas3.setFont(new java.awt.Font("Arial Narrow", 1, 60)); // NOI18N
        label_awas3.setForeground(new java.awt.Color(255, 0, 0));
        label_awas3.setText("88");

        label_siaga3.setBackground(new java.awt.Color(255, 255, 255));
        label_siaga3.setFont(new java.awt.Font("Arial Narrow", 1, 60)); // NOI18N
        label_siaga3.setForeground(new java.awt.Color(255, 188, 0));
        label_siaga3.setText("88");

        label_normal3.setBackground(new java.awt.Color(255, 255, 255));
        label_normal3.setFont(new java.awt.Font("Arial Narrow", 1, 60)); // NOI18N
        label_normal3.setForeground(new java.awt.Color(0, 0, 255));
        label_normal3.setText("88");

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial Narrow", 1, 38)); // NOI18N
        jLabel17.setText("F2 :");

        label_awas4.setBackground(new java.awt.Color(255, 255, 255));
        label_awas4.setFont(new java.awt.Font("Arial Narrow", 1, 60)); // NOI18N
        label_awas4.setForeground(new java.awt.Color(255, 0, 0));
        label_awas4.setText("88");

        label_siaga4.setBackground(new java.awt.Color(255, 255, 255));
        label_siaga4.setFont(new java.awt.Font("Arial Narrow", 1, 60)); // NOI18N
        label_siaga4.setForeground(new java.awt.Color(255, 188, 0));
        label_siaga4.setText("88");

        label_normal4.setBackground(new java.awt.Color(255, 255, 255));
        label_normal4.setFont(new java.awt.Font("Arial Narrow", 1, 60)); // NOI18N
        label_normal4.setForeground(new java.awt.Color(0, 0, 255));
        label_normal4.setText("88");

        label_judul.setBackground(new java.awt.Color(255, 255, 255));
        label_judul.setFont(new java.awt.Font("Arial", 1, 40)); // NOI18N
        label_judul.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_judul.setText("RUANG ALL");
        label_judul.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        space.setBackground(new java.awt.Color(255, 255, 255));
        space.setFont(new java.awt.Font("Arial Narrow", 1, 60)); // NOI18N
        space.setText("/");

        space1.setBackground(new java.awt.Color(255, 255, 255));
        space1.setFont(new java.awt.Font("Arial Narrow", 1, 60)); // NOI18N
        space1.setText("/");

        space2.setBackground(new java.awt.Color(255, 255, 255));
        space2.setFont(new java.awt.Font("Arial Narrow", 1, 60)); // NOI18N
        space2.setText("/");

        space3.setBackground(new java.awt.Color(255, 255, 255));
        space3.setFont(new java.awt.Font("Arial Narrow", 1, 60)); // NOI18N
        space3.setText("/");

        space4.setBackground(new java.awt.Color(255, 255, 255));
        space4.setFont(new java.awt.Font("Arial Narrow", 1, 60)); // NOI18N
        space4.setText("/");

        space5.setBackground(new java.awt.Color(255, 255, 255));
        space5.setFont(new java.awt.Font("Arial Narrow", 1, 60)); // NOI18N
        space5.setText("/");

        space6.setBackground(new java.awt.Color(255, 255, 255));
        space6.setFont(new java.awt.Font("Arial Narrow", 1, 60)); // NOI18N
        space6.setText("/");

        space7.setBackground(new java.awt.Color(255, 255, 255));
        space7.setFont(new java.awt.Font("Arial Narrow", 1, 60)); // NOI18N
        space7.setText("/");

        label_judul1.setBackground(new java.awt.Color(255, 255, 255));
        label_judul1.setFont(new java.awt.Font("Arial", 1, 40)); // NOI18N
        label_judul1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_judul1.setText("CABUT");
        label_judul1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        label_waktu.setBackground(new java.awt.Color(255, 255, 255));
        label_waktu.setFont(new java.awt.Font("Arial Narrow", 1, 18)); // NOI18N
        label_waktu.setText("-- : -- : --");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial Narrow", 1, 38)); // NOI18N
        jLabel14.setText("CTK JDN :");

        label_awas5.setBackground(new java.awt.Color(255, 255, 255));
        label_awas5.setFont(new java.awt.Font("Arial Narrow", 1, 60)); // NOI18N
        label_awas5.setForeground(new java.awt.Color(255, 0, 0));
        label_awas5.setText("88");

        label_siaga5.setBackground(new java.awt.Color(255, 255, 255));
        label_siaga5.setFont(new java.awt.Font("Arial Narrow", 1, 60)); // NOI18N
        label_siaga5.setForeground(new java.awt.Color(255, 188, 0));
        label_siaga5.setText("88");

        label_normal5.setBackground(new java.awt.Color(255, 255, 255));
        label_normal5.setFont(new java.awt.Font("Arial Narrow", 1, 60)); // NOI18N
        label_normal5.setForeground(new java.awt.Color(0, 0, 255));
        label_normal5.setText("88");

        space8.setBackground(new java.awt.Color(255, 255, 255));
        space8.setFont(new java.awt.Font("Arial Narrow", 1, 60)); // NOI18N
        space8.setText("/");

        space9.setBackground(new java.awt.Color(255, 255, 255));
        space9.setFont(new java.awt.Font("Arial Narrow", 1, 60)); // NOI18N
        space9.setText("/");

        label_target_awas.setBackground(new java.awt.Color(255, 255, 255));
        label_target_awas.setFont(new java.awt.Font("Arial Narrow", 1, 40)); // NOI18N
        label_target_awas.setForeground(new java.awt.Color(255, 0, 0));
        label_target_awas.setText("88");

        space10.setBackground(new java.awt.Color(255, 255, 255));
        space10.setFont(new java.awt.Font("Arial Narrow", 1, 40)); // NOI18N
        space10.setText("/");

        label_target_siaga.setBackground(new java.awt.Color(255, 255, 255));
        label_target_siaga.setFont(new java.awt.Font("Arial Narrow", 1, 40)); // NOI18N
        label_target_siaga.setForeground(new java.awt.Color(255, 188, 0));
        label_target_siaga.setText("88");

        space11.setBackground(new java.awt.Color(255, 255, 255));
        space11.setFont(new java.awt.Font("Arial Narrow", 1, 40)); // NOI18N
        space11.setText("/");

        label_target_normal.setBackground(new java.awt.Color(255, 255, 255));
        label_target_normal.setFont(new java.awt.Font("Arial Narrow", 1, 40)); // NOI18N
        label_target_normal.setForeground(new java.awt.Color(0, 0, 255));
        label_target_normal.setText("88");

        space12.setBackground(new java.awt.Color(255, 255, 255));
        space12.setFont(new java.awt.Font("Arial Narrow", 1, 40)); // NOI18N
        space12.setText("Target :");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_judul, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel13)
                            .addComponent(jLabel17)
                            .addComponent(jLabel4)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(label_awas3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(space6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_siaga3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(space5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_normal3))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(label_awas4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(space4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_siaga4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(space7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_normal4))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(label_awas2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(space)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_siaga2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(space2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_normal2))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(label_awas1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(space3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_siaga1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(space1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_normal1))
                            .addComponent(jLabel14)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(label_awas5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(space9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_siaga5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(space8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_normal5)))))
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(label_judul1, javax.swing.GroupLayout.PREFERRED_SIZE, 459, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(space12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_target_awas)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(space10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_target_siaga)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(space11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_target_normal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(label_waktu)
                        .addContainerGap())
                    .addComponent(jTabbedPane1)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_target_awas)
                        .addComponent(label_target_siaga)
                        .addComponent(label_target_normal)
                        .addComponent(space11)
                        .addComponent(space10)
                        .addComponent(space12))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_judul, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_judul1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(label_waktu)))
                .addGap(10, 10, 10)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_awas1)
                            .addComponent(label_siaga1)
                            .addComponent(label_normal1)
                            .addComponent(space1)
                            .addComponent(space3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_awas2)
                            .addComponent(label_siaga2)
                            .addComponent(label_normal2)
                            .addComponent(space)
                            .addComponent(space2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_awas3)
                            .addComponent(label_siaga3)
                            .addComponent(label_normal3)
                            .addComponent(space5)
                            .addComponent(space6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_awas5)
                            .addComponent(label_siaga5)
                            .addComponent(label_normal5)
                            .addComponent(space8)
                            .addComponent(space9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_awas4)
                            .addComponent(label_siaga4)
                            .addComponent(label_normal4)
                            .addComponent(space4)
                            .addComponent(space7))
                        .addGap(15, 15, 15))
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
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
                refreshTable_Cabut1();
                break;
            case 1:
                refreshTable_Cabut2();
                break;
            case 2:
                refreshTable_Cetak();
                Mlem();
                break;
            case 3:
                refreshTable_Cetak_jidun();
                break;
            case 4:
                refreshTable_f2();
                break;
            case 5:
                refreshTabel_hold_baku();
                refreshTabel_hold_jadi();
                break;
            case 6:
                label_judul1.setText("Isu Produksi");
                label_target_awas.setText("-");
                label_target_siaga.setText("-");
                label_target_normal.setText("-");
                refreshIsu1();
                break;
            case 7:
                label_judul1.setText("Isu Produksi");
                label_target_awas.setText("-");
                label_target_siaga.setText("-");
                label_target_normal.setText("-");
                refreshIsu2();
                break;
            case 8:
                label_judul1.setText("Isu Produksi");
                label_target_awas.setText("-");
                label_target_siaga.setText("-");
                label_target_normal.setText("-");
                refreshIsu3();
                break;
            case 9:
                label_judul1.setText("Isu Produksi");
                label_target_awas.setText("-");
                label_target_siaga.setText("-");
                label_target_normal.setText("-");
                refreshIsu4();
                break;
            case 10:
                label_judul1.setText("Isu Produksi");
                label_target_awas.setText("-");
                label_target_siaga.setText("-");
                label_target_normal.setText("-");
                refreshIsu5();
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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrame_Tampilan_Proses_LP2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame_Tampilan_Proses_LP2 dialog = new JFrame_Tampilan_Proses_LP2("A");
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable Table_Data_Nitrit_jadi;
    public static javax.swing.JTable Table_Data_nitrit_baku;
    private javax.swing.JTable Table_WIP_Cabut1;
    private javax.swing.JTable Table_WIP_Cabut2;
    private javax.swing.JTable Table_WIP_Cetak;
    private javax.swing.JTable Table_WIP_Cetak_Jidun;
    private javax.swing.JTable Table_WIP_Finishing2;
    private javax.swing.JTable Table_data_mlem;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel_Cabut;
    private javax.swing.JPanel jPanel_Cetak;
    private javax.swing.JPanel jPanel_Cetak_Jidun;
    private javax.swing.JPanel jPanel_Finishing2;
    private javax.swing.JPanel jPanel_SetorSusutan;
    private javax.swing.JPanel jPanel_isu1;
    private javax.swing.JPanel jPanel_isu2;
    private javax.swing.JPanel jPanel_isu3;
    private javax.swing.JPanel jPanel_isu4;
    private javax.swing.JPanel jPanel_isu5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane16;
    private javax.swing.JScrollPane jScrollPane2;
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
    private javax.swing.JLabel label_awas1;
    private javax.swing.JLabel label_awas2;
    private javax.swing.JLabel label_awas3;
    private javax.swing.JLabel label_awas4;
    private javax.swing.JLabel label_awas5;
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
    private javax.swing.JLabel label_judul;
    private javax.swing.JLabel label_judul1;
    private javax.swing.JLabel label_jumlah_kartu_hold;
    private javax.swing.JLabel label_kode;
    private javax.swing.JLabel label_kode1;
    private javax.swing.JLabel label_kode2;
    private javax.swing.JLabel label_kode3;
    private javax.swing.JLabel label_kode4;
    private javax.swing.JLabel label_normal1;
    private javax.swing.JLabel label_normal2;
    private javax.swing.JLabel label_normal3;
    private javax.swing.JLabel label_normal4;
    private javax.swing.JLabel label_normal5;
    private javax.swing.JLabel label_penanggungjawab;
    private javax.swing.JLabel label_penanggungjawab1;
    private javax.swing.JLabel label_penanggungjawab2;
    private javax.swing.JLabel label_penanggungjawab3;
    private javax.swing.JLabel label_penanggungjawab4;
    private javax.swing.JLabel label_siaga1;
    private javax.swing.JLabel label_siaga2;
    private javax.swing.JLabel label_siaga3;
    private javax.swing.JLabel label_siaga4;
    private javax.swing.JLabel label_siaga5;
    private javax.swing.JLabel label_target_awas;
    private javax.swing.JLabel label_target_normal;
    private javax.swing.JLabel label_target_siaga;
    private javax.swing.JLabel label_tgl_isu;
    private javax.swing.JLabel label_tgl_isu1;
    private javax.swing.JLabel label_tgl_isu2;
    private javax.swing.JLabel label_tgl_isu3;
    private javax.swing.JLabel label_tgl_isu4;
    private javax.swing.JLabel label_tgl_nitrit_baku;
    private javax.swing.JLabel label_total_kartu_jadi_merah;
    private javax.swing.JLabel label_total_kartu_jadi_oren;
    private javax.swing.JLabel label_total_kartu_jadi_putih;
    private javax.swing.JLabel label_waktu;
    private javax.swing.JLabel space;
    private javax.swing.JLabel space1;
    private javax.swing.JLabel space10;
    private javax.swing.JLabel space11;
    private javax.swing.JLabel space12;
    private javax.swing.JLabel space2;
    private javax.swing.JLabel space3;
    private javax.swing.JLabel space4;
    private javax.swing.JLabel space5;
    private javax.swing.JLabel space6;
    private javax.swing.JLabel space7;
    private javax.swing.JLabel space8;
    private javax.swing.JLabel space9;
    // End of variables declaration//GEN-END:variables
}
