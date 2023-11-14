package waleta_system.Panel_produksi;

import waleta_system.Class.Utility;
import java.awt.Color;
import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;
import waleta_system.Class.ProgressLP;
import waleta_system.Interface.InterfacePanel;
import waleta_system.MainForm;

public class JPanel_ProgressLP extends javax.swing.JPanel implements InterfacePanel {

    String sql = null, sql_rekap = null;
    ResultSet rs;
    Date today = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_ProgressLP() {
        initComponents();
    }

    @Override
    public void init() {
        try {
            ComboBox_ruangan.removeAllItems();
            ComboBox_ruangan.addItem("All");
            sql = "SELECT DISTINCT(`ruangan`) AS 'ruangan' FROM `tb_laporan_produksi` WHERE 1";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_ruangan.addItem(rs.getString("ruangan"));
            }
//            refreshTable();
        } catch (Exception ex) {
            Logger.getLogger(JPanel_ProgressLP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList<ProgressLP> prosesList() {
        ArrayList<ProgressLP> prosesList = new ArrayList<>();
        try {
            String filter_status = "";
            if (ComboBox_filter_status.getSelectedIndex() == 1) {
                filter_status = "AND `tanggal_grading` IS NULL";
            } else if (ComboBox_filter_status.getSelectedIndex() == 2) {
                filter_status = "AND `tanggal_grading` IS NOT NULL AND (`tb_tutupan_grading`.`status_box` = 'PROSES' OR `tb_tutupan_grading`.`status_box` IS NULL)";
            } else if (ComboBox_filter_status.getSelectedIndex() == 3) {
                filter_status = "AND `tb_tutupan_grading`.`status_box` = 'SELESAI'";
            }

            String ruang = "";
            if (ComboBox_ruangan.getSelectedItem() != "All") {
                ruang = ComboBox_ruangan.getSelectedItem().toString();
            }
            String search = "";
            switch (ComboBox_search.getSelectedIndex()) {
                case 0:
                    search = "`tb_laporan_produksi`.`no_laporan_produksi`";
                    break;
                case 1:
                    search = "`tb_laporan_produksi`.`no_kartu_waleta`";
                    break;
                case 2:
                    search = "`tb_laporan_produksi`.`memo_lp`";
                    break;
                case 3:
                    search = "`tb_laporan_produksi`.`kode_grade`";
                    break;
                default:
                    break;
            }
            String jenis_filter_tanggal = "";
            if (Date_filter1.getDate() != null && Date_filter2.getDate() != null) {
                switch (ComboBox_filter_tanggal.getSelectedIndex()) {
                    case 0:
                        jenis_filter_tanggal = "`tanggal_lp`";
                        break;
                    case 1:
                        jenis_filter_tanggal = "`tanggal_rendam`";
                        break;
                    case 2:
                        jenis_filter_tanggal = "`tgl_masuk_cuci`";
                        break;
                    case 3:
                        jenis_filter_tanggal = "MIN(`tb_detail_pencabut`.`tanggal_cabut`)";
                        break;
                    case 4:
                        jenis_filter_tanggal = "`tgl_setor_cabut`";
                        break;
                    case 5:
                        jenis_filter_tanggal = "`tgl_mulai_cetak`";
                        break;
                    case 6:
                        jenis_filter_tanggal = "`tgl_selesai_cetak`";
                        break;
                    case 7:
                        jenis_filter_tanggal = "`tgl_masuk_f2`";
                        break;
                    case 8:
                        jenis_filter_tanggal = "`tgl_dikerjakan_f2`";
                        break;
                    case 9:
                        jenis_filter_tanggal = "`tgl_f1`";
                        break;
                    case 10:
                        jenis_filter_tanggal = "`tgl_f2`";
                        break;
                    case 11:
                        jenis_filter_tanggal = "`tgl_setor_f2`";
                        break;
                    case 12:
                        jenis_filter_tanggal = "`tgl_masuk`";
                        break;
                    case 13:
                        jenis_filter_tanggal = "`tgl_selesai`";
                        break;
                    case 14:
                        jenis_filter_tanggal = "`tanggal_grading`";
                        break;
                    default:
                        break;
                }
                jenis_filter_tanggal = "AND (" + jenis_filter_tanggal + " BETWEEN '" + dateFormat.format(Date_filter1.getDate()) + "' AND '" + dateFormat.format(Date_filter2.getDate()) + "')\n";
            } 
            sql = "SELECT `tb_laporan_produksi`.`no_laporan_produksi`,`tb_laporan_produksi`.`no_kartu_waleta`,`tb_laporan_produksi`.`memo_lp`,`tb_laporan_produksi`.`kode_grade`, `jenis_bulu_lp`, `ruangan`, `jumlah_keping`, `berat_basah`, `tb_laporan_produksi`.`tanggal_lp`, `tanggal_rendam`, `tgl_masuk_cuci`, `tgl_mulai_cabut`, MIN(`tb_detail_pencabut`.`tanggal_cabut`) AS 'mulai_cabut', `tgl_setor_cabut`, `tgl_mulai_cetak`, `tgl_selesai_cetak`, `tgl_masuk_f2`, `tgl_dikerjakan_f2`, `tgl_f1`, `tgl_f2`, `tgl_setor_f2`, `tb_lab_laporan_produksi`.`tgl_masuk`, `tb_lab_laporan_produksi`.`tgl_selesai`, `tanggal_grading`, `tb_tutupan_grading`.`status_box`, `tb_bahan_baku_masuk_cheat`.`no_registrasi` AS 'rsb_ct1' \n"
                        + "FROM `tb_laporan_produksi` \n"
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`\n"
                        + "LEFT JOIN `tb_rendam` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_rendam`.`no_laporan_produksi`\n"
                        + "LEFT JOIN `tb_cuci` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cuci`.`no_laporan_produksi`\n"
                        + "LEFT JOIN `tb_cabut` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cabut`.`no_laporan_produksi`\n"
                        + "LEFT JOIN `tb_detail_pencabut` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_detail_pencabut`.`no_laporan_produksi`\n"
                        + "LEFT JOIN `tb_cetak` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cetak`.`no_laporan_produksi`\n"
                        + "LEFT JOIN `tb_finishing_2` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_finishing_2`.`no_laporan_produksi`"
                        + "LEFT JOIN `tb_lab_laporan_produksi` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_lab_laporan_produksi`.`no_laporan_produksi`"
                        + "LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_bahan_jadi_masuk`.`kode_asal`\n"
                        + "LEFT JOIN `tb_bahan_baku_masuk_cheat` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta`\n"
                        + "LEFT JOIN `tb_tutupan_grading` ON `tb_bahan_jadi_masuk`.`kode_tutupan` = `tb_tutupan_grading`.`kode_tutupan`\n"
                        + "WHERE " + search + " LIKE '%" + txt_search_proses.getText() + "%' AND `tb_laporan_produksi`.`ruangan` LIKE '%" + ruang + "%' AND YEAR(`tb_laporan_produksi`.`tanggal_lp`)>=2018  AND `berat_basah` > 0 \n"
                        + jenis_filter_tanggal
                        + filter_status
                        + " GROUP BY `tb_laporan_produksi`.`no_laporan_produksi`";
            rs = Utility.db.getStatement().executeQuery(sql);
            ProgressLP proses;
            while (rs.next()) {
                proses = new ProgressLP(rs.getString("no_laporan_produksi"), rs.getString("no_kartu_waleta"), rs.getString("memo_lp"), rs.getString("kode_grade"), rs.getString("jenis_bulu_lp"), rs.getString("ruangan"), rs.getInt("jumlah_keping"), rs.getInt("berat_basah"), rs.getDate("tanggal_lp"), rs.getDate("tanggal_rendam"), rs.getDate("tgl_masuk_cuci"), rs.getDate("tgl_mulai_cabut"), rs.getDate("mulai_cabut"), rs.getDate("tgl_setor_cabut"), rs.getDate("tgl_mulai_cetak"), rs.getDate("tgl_selesai_cetak"), rs.getDate("tgl_masuk_f2"), rs.getDate("tgl_dikerjakan_f2"), rs.getDate("tgl_f1"), rs.getDate("tgl_f2"), rs.getDate("tgl_setor_f2"), rs.getDate("tgl_masuk"), rs.getDate("tgl_selesai"), rs.getDate("tanggal_grading"), rs.getString("status_box"), rs.getString("rsb_ct1"));
                prosesList.add(proses);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        return prosesList;
    }

    public void show_progress_lp() {
        decimalFormat.setMaximumFractionDigits(2);
        decimalFormat.setGroupingUsed(true);
        double tot_berat_basah = 0;
        double avg_lama_tandon = 0, avg_lama_cabut = 0, avg_lama_koreksi = 0, avg_lama_cetak = 0, avg_lama_f2 = 0, avg_lama_QC = 0, avg_lama_proses = 0, avg_lama_grading = 0;
        int jumlah_tandon = 0, jumlah_cabut = 0, jumlah_koreksi = 0, jumlah_cetak = 0, jumlah_f2 = 0, jumlah_QC = 0, jumlah_proses = 0, jumlah_grading = 0;
        ArrayList<ProgressLP> list = prosesList();
        DefaultTableModel model = (DefaultTableModel) Table_progress_lp.getModel();
        Object[] row = new Object[35];
        jProgressBar1.setMaximum(list.size());
        for (int i = 0; i < list.size(); i++) {
            jProgressBar1.setValue(jProgressBar1.getValue() + 1);
            row[0] = list.get(i).getNo_lp();
            row[1] = list.get(i).getNo_kartu();
            row[2] = list.get(i).getMemo();
            row[3] = list.get(i).getKode_grade();
            row[4] = list.get(i).getJenis_bulu();
            row[5] = list.get(i).getRuangan();
            row[6] = list.get(i).getKeping();
            row[7] = list.get(i).getBerat_basah();
            row[8] = list.get(i).getTgl_lp();
            row[9] = list.get(i).getTgl_rendam();
            row[10] = list.get(i).getTgl_cuci();
            row[11] = list.get(i).getTgl_masuk_cabut();
            row[12] = list.get(i).getTgl_mulai_cabut();
            row[13] = list.get(i).getTgl_selesai_cabut();
            row[14] = list.get(i).getTgl_masuk_cetak();
            row[15] = list.get(i).getTgl_selesai_cetak();
            row[16] = list.get(i).getTgl_masuk_f2();
            row[17] = list.get(i).getTgl_koreksi();
            row[18] = list.get(i).getTgl_f1();
            row[19] = list.get(i).getTgl_f2();
            row[20] = list.get(i).getTgl_setor_f2();
            row[21] = list.get(i).getTgl_masuk_lab();
            row[22] = list.get(i).getTgl_setor_lab();
            row[23] = list.get(i).getTgl_grading_bj();

            long lama_tandon = 0;
            if (list.get(i).getTgl_cuci() != null) {
                if (list.get(i).getTgl_mulai_cabut() != null) {
                    lama_tandon = Math.abs(list.get(i).getTgl_mulai_cabut().getTime() - list.get(i).getTgl_cuci().getTime());
                    avg_lama_tandon = avg_lama_tandon + TimeUnit.MILLISECONDS.toDays(lama_tandon);
                    jumlah_tandon++;
                } else {
                    lama_tandon = Math.abs(today.getTime() - list.get(i).getTgl_cuci().getTime());
                }
            }
            row[24] = TimeUnit.MILLISECONDS.toDays(lama_tandon);

            long lama_cabut = 0;
            if (list.get(i).getTgl_masuk_cabut() != null) {
                if (list.get(i).getTgl_selesai_cabut() != null) {
                    lama_cabut = Math.abs(list.get(i).getTgl_selesai_cabut().getTime() - list.get(i).getTgl_masuk_cabut().getTime());
                    avg_lama_cabut = avg_lama_cabut + TimeUnit.MILLISECONDS.toDays(lama_cabut);
                    jumlah_cabut++;
                } else {
                    lama_cabut = Math.abs(today.getTime() - list.get(i).getTgl_masuk_cabut().getTime());
                }
            }
            row[25] = TimeUnit.MILLISECONDS.toDays(lama_cabut);

            long lama_koreksi = 0;
            if (list.get(i).getTgl_selesai_cabut() != null) {
                if (list.get(i).getTgl_masuk_cetak() != null) {
                    lama_koreksi = Math.abs(list.get(i).getTgl_masuk_cetak().getTime() - list.get(i).getTgl_selesai_cabut().getTime());
                    avg_lama_koreksi = avg_lama_koreksi + TimeUnit.MILLISECONDS.toDays(lama_koreksi);
                    jumlah_koreksi++;
                } else {
                    lama_koreksi = Math.abs(today.getTime() - list.get(i).getTgl_selesai_cabut().getTime());
                }
            }
            row[26] = TimeUnit.MILLISECONDS.toDays(lama_koreksi);

            long lama_cetak = 0;
            if (list.get(i).getTgl_masuk_cetak() != null) {
                if (list.get(i).getTgl_selesai_cetak() != null) {
                    lama_cetak = Math.abs(list.get(i).getTgl_selesai_cetak().getTime() - list.get(i).getTgl_masuk_cetak().getTime());
                    avg_lama_cetak = avg_lama_cetak + TimeUnit.MILLISECONDS.toDays(lama_cetak);
                    jumlah_cetak++;
                } else {
                    lama_cetak = Math.abs(today.getTime() - list.get(i).getTgl_masuk_cetak().getTime());
                }
            }
            row[27] = TimeUnit.MILLISECONDS.toDays(lama_cetak);

            long lama_f2 = 0;
            if (list.get(i).getTgl_masuk_f2() != null) {
                if (list.get(i).getTgl_setor_f2() != null) {
                    lama_f2 = Math.abs(list.get(i).getTgl_setor_f2().getTime() - list.get(i).getTgl_masuk_f2().getTime());
                    avg_lama_f2 = avg_lama_f2 + TimeUnit.MILLISECONDS.toDays(lama_f2);
                    jumlah_f2++;
                } else {
                    lama_f2 = Math.abs(today.getTime() - list.get(i).getTgl_masuk_f2().getTime());
                }
            }
            row[28] = TimeUnit.MILLISECONDS.toDays(lama_f2);

            long lama_qc = 0;
            if (list.get(i).getTgl_masuk_lab() != null) {
                if (list.get(i).getTgl_setor_lab() != null) {
                    lama_qc = Math.abs(list.get(i).getTgl_setor_lab().getTime() - list.get(i).getTgl_masuk_lab().getTime());
                    avg_lama_QC = avg_lama_QC + TimeUnit.MILLISECONDS.toDays(lama_qc);
                    jumlah_QC++;
                } else {
                    lama_qc = Math.abs(today.getTime() - list.get(i).getTgl_masuk_lab().getTime());
                }
            }
            row[29] = TimeUnit.MILLISECONDS.toDays(lama_qc);

//            Date tgl_masuk_lama_proses = list.get(i).getTgl_lp();
//            Date tgl_keluar_lama_proses = list.get(i).getTgl_setor_lab();
            long lama_proses = 0;
            if (list.get(i).getTgl_lp() != null) {
                if (list.get(i).getTgl_setor_lab() != null) {
                    lama_proses = Math.abs(list.get(i).getTgl_setor_lab().getTime() - list.get(i).getTgl_lp().getTime());
                    avg_lama_proses = avg_lama_proses + TimeUnit.MILLISECONDS.toDays(lama_proses);
                    jumlah_proses++;
                } else {
                    lama_proses = Math.abs(today.getTime() - list.get(i).getTgl_lp().getTime());
                }
            }
            row[30] = TimeUnit.MILLISECONDS.toDays(lama_proses);

            long lama_grading = 0;
            if (list.get(i).getTgl_setor_lab() != null) {
                if (list.get(i).getTgl_grading_bj() != null) {
                    lama_grading = Math.abs(list.get(i).getTgl_grading_bj().getTime() - list.get(i).getTgl_setor_lab().getTime());
                    avg_lama_grading = avg_lama_grading + TimeUnit.MILLISECONDS.toDays(lama_grading);
                    jumlah_grading++;
                } else {
                    lama_grading = Math.abs(today.getTime() - list.get(i).getTgl_setor_lab().getTime());
                }
            }
            row[31] = TimeUnit.MILLISECONDS.toDays(lama_grading);

            if ("SELESAI".equals(list.get(i).getStatus_box())) {
                row[32] = "Barang Jadi";
            } else {
                if (list.get(i).getTgl_grading_bj() == null) {
                    row[32] = "LP Belum Grading";
                } else {
                    row[32] = "LP Proses Grading";
                }
            }
            row[33] = list.get(i).getRsb_ct1();

            if (null != ComboBox_filter_status.getSelectedItem().toString()) {
                switch (ComboBox_filter_status.getSelectedItem().toString()) {
                    case "LP Belum Grading":
                        if (row[32] == "LP Belum Grading") {
                            tot_berat_basah = tot_berat_basah + list.get(i).getBerat_basah();
                            model.addRow(row);
                        }
                        break;
                    case "LP Proses Grading":
                        if (row[32] == "LP Proses Grading") {
                            tot_berat_basah = tot_berat_basah + list.get(i).getBerat_basah();
                            model.addRow(row);
                        }
                        break;
                    case "Barang Jadi":
                        if (row[32] == "Barang Jadi") {
                            tot_berat_basah = tot_berat_basah + list.get(i).getBerat_basah();
                            model.addRow(row);
                        }
                        break;
                    case "All":
                        tot_berat_basah = tot_berat_basah + list.get(i).getBerat_basah();
                        model.addRow(row);
                        break;
                    default:
                        break;
                }
            }
        }
        Table_progress_lp.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                switch (column) {
                    case 24:
                        if ((long) Table_progress_lp.getValueAt(row, column) == 2) {
                            if (isSelected) {
                                comp.setBackground(Color.gray);
                                comp.setForeground(Color.yellow);
                            } else {
                                comp.setBackground(Color.yellow);
                                comp.setForeground(Color.BLACK);
                            }
                        } else if ((long) Table_progress_lp.getValueAt(row, column) > 2) {
                            if (isSelected) {
                                comp.setBackground(Color.gray);
                                comp.setForeground(Color.WHITE);
                            } else {
                                comp.setBackground(Color.PINK);
                                comp.setForeground(Color.red);
                            }
                        } else {
                            if (isSelected) {
                                comp.setBackground(Table_progress_lp.getSelectionBackground());
                                comp.setForeground(Table_progress_lp.getSelectionForeground());
                            } else {
                                comp.setBackground(Table_progress_lp.getBackground());
                                comp.setForeground(Table_progress_lp.getForeground());
                            }
                        }
                        break;
                    case 25:
                        if ((long) Table_progress_lp.getValueAt(row, column) == 2) {
                            if (isSelected) {
                                comp.setBackground(Color.gray);
                                comp.setForeground(Color.yellow);
                            } else {
                                comp.setBackground(Color.yellow);
                                comp.setForeground(Color.BLACK);
                            }
                        } else if ((long) Table_progress_lp.getValueAt(row, column) > 2) {
                            if (isSelected) {
                                comp.setBackground(Color.gray);
                                comp.setForeground(Color.WHITE);
                            } else {
                                comp.setBackground(Color.PINK);
                                comp.setForeground(Color.red);
                            }
                        } else {
                            if (isSelected) {
                                comp.setBackground(Table_progress_lp.getSelectionBackground());
                                comp.setForeground(Table_progress_lp.getSelectionForeground());
                            } else {
                                comp.setBackground(Table_progress_lp.getBackground());
                                comp.setForeground(Table_progress_lp.getForeground());
                            }
                        }
                        break;
                    case 26:
                        if ((long) Table_progress_lp.getValueAt(row, column) == 2) {
                            if (isSelected) {
                                comp.setBackground(Color.gray);
                                comp.setForeground(Color.yellow);
                            } else {
                                comp.setBackground(Color.yellow);
                                comp.setForeground(Color.BLACK);
                            }
                        } else if ((long) Table_progress_lp.getValueAt(row, column) > 2) {
                            if (isSelected) {
                                comp.setBackground(Color.gray);
                                comp.setForeground(Color.WHITE);
                            } else {
                                comp.setBackground(Color.PINK);
                                comp.setForeground(Color.red);
                            }
                        } else {
                            if (isSelected) {
                                comp.setBackground(Table_progress_lp.getSelectionBackground());
                                comp.setForeground(Table_progress_lp.getSelectionForeground());
                            } else {
                                comp.setBackground(Table_progress_lp.getBackground());
                                comp.setForeground(Table_progress_lp.getForeground());
                            }
                        }
                        break;
                    case 27:
                        if ((long) Table_progress_lp.getValueAt(row, column) == 3) {
                            if (isSelected) {
                                comp.setBackground(Color.gray);
                                comp.setForeground(Color.yellow);
                            } else {
                                comp.setBackground(Color.yellow);
                                comp.setForeground(Color.BLACK);
                            }
                        } else if ((long) Table_progress_lp.getValueAt(row, column) > 3) {
                            if (isSelected) {
                                comp.setBackground(Color.gray);
                                comp.setForeground(Color.WHITE);
                            } else {
                                comp.setBackground(Color.PINK);
                                comp.setForeground(Color.red);
                            }
                        } else {
                            if (isSelected) {
                                comp.setBackground(Table_progress_lp.getSelectionBackground());
                                comp.setForeground(Table_progress_lp.getSelectionForeground());
                            } else {
                                comp.setBackground(Table_progress_lp.getBackground());
                                comp.setForeground(Table_progress_lp.getForeground());
                            }
                        }
                        break;
                    case 28://lama F2
                        if ((long) Table_progress_lp.getValueAt(row, column) == 4) {
                            if (isSelected) {
                                comp.setBackground(Color.gray);
                                comp.setForeground(Color.yellow);
                            } else {
                                comp.setBackground(Color.yellow);
                                comp.setForeground(Color.BLACK);
                            }
                        } else if ((long) Table_progress_lp.getValueAt(row, column) > 4) {
                            if (isSelected) {
                                comp.setBackground(Color.gray);
                                comp.setForeground(Color.WHITE);
                            } else {
                                comp.setBackground(Color.PINK);
                                comp.setForeground(Color.red);
                            }
                        } else {
                            if (isSelected) {
                                comp.setBackground(Table_progress_lp.getSelectionBackground());
                                comp.setForeground(Table_progress_lp.getSelectionForeground());
                            } else {
                                comp.setBackground(Table_progress_lp.getBackground());
                                comp.setForeground(Table_progress_lp.getForeground());
                            }
                        }
                        break;
                    default:
                        if (isSelected) {
                            comp.setBackground(Table_progress_lp.getSelectionBackground());
                            comp.setForeground(Table_progress_lp.getSelectionForeground());
                        } else {
                            comp.setBackground(Table_progress_lp.getBackground());
                            comp.setForeground(Table_progress_lp.getForeground());
                        }
                        break;
                }
                return comp;
            }
        });

        Table_progress_lp.repaint();
        int rowData = Table_progress_lp.getRowCount();
        label_total_data_rendam.setText(decimalFormat.format(rowData));
        label_total_bk.setText(decimalFormat.format(tot_berat_basah));

//        System.out.println("avg_lama_tandon = " + avg_lama_tandon + ", jumlah_tandon = " + jumlah_tandon);
//        System.out.println("avg_lama_cabut = " + avg_lama_cabut + ", jumlah_cabut = " + jumlah_cabut);
//        System.out.println("avg_lama_koreksi = " + avg_lama_koreksi + ", jumlah_koreksi = " + jumlah_koreksi);
//        System.out.println("avg_lama_cetak = " + avg_lama_cetak + ", jumlah_cetak = " + jumlah_cetak);
//        System.out.println("avg_lama_f2 = " + avg_lama_f2 + ", jumlah_f2 = " + jumlah_f2);
//        System.out.println("avg_lama_QC = " + avg_lama_QC + ", jumlah_QC = " + jumlah_QC);
//        System.out.println("avg_lama_proses = " + avg_lama_proses + ", jumlah_proses = " + jumlah_proses);
//        System.out.println("avg_lama_grading = " + avg_lama_grading + ", jumlah_grading = " + jumlah_grading);
        avg_lama_tandon = avg_lama_tandon / jumlah_tandon;
        avg_lama_cabut = avg_lama_cabut / jumlah_cabut;
        avg_lama_koreksi = avg_lama_koreksi / jumlah_koreksi;
        avg_lama_cetak = avg_lama_cetak / jumlah_cetak;
        avg_lama_f2 = avg_lama_f2 / jumlah_f2;
        avg_lama_QC = avg_lama_QC / jumlah_QC;
        avg_lama_proses = avg_lama_proses / jumlah_proses;
        avg_lama_grading = avg_lama_grading / jumlah_grading;

        decimalFormat.setMaximumFractionDigits(0);
        label_rata_tandon.setText(decimalFormat.format(avg_lama_tandon));
        label_rata_cabut.setText(decimalFormat.format(avg_lama_cabut));
        label_rata_koreksi.setText(decimalFormat.format(avg_lama_koreksi));
        label_rata_cetak.setText(decimalFormat.format(avg_lama_cetak));
        label_rata_f2.setText(decimalFormat.format(avg_lama_f2));
        label_rata_qc.setText(decimalFormat.format(avg_lama_QC));
        label_rata_proses.setText(decimalFormat.format(avg_lama_proses));
        label_rata_grading.setText(decimalFormat.format(avg_lama_grading));

//        label_rata_tandon.setText(Double.toString(Math.round(avg_lama_tandon)));
//        label_rata_cabut.setText(Double.toString(Math.round(avg_lama_cabut)));
//        label_rata_koreksi.setText(Double.toString(Math.round(avg_lama_koreksi)));
//        label_rata_cetak.setText(Double.toString(Math.round(avg_lama_cetak)));
//        label_rata_f2.setText(Double.toString(Math.round(avg_lama_f2)));
//        label_rata_qc.setText(Double.toString(Math.round(avg_lama_QC)));
//        label_rata_proses.setText(Double.toString(Math.round(avg_lama_proses)));
//        label_rata_grading.setText(Double.toString(Math.round(avg_lama_grading)));
    }

    public void refreshTable() {
        DefaultTableModel model = (DefaultTableModel) Table_progress_lp.getModel();
        model.setRowCount(0);
        show_progress_lp();
        ColumnsAutoSizer.sizeColumnsToFit(Table_progress_lp);

        /*TableAlignment.setHorizontalAlignment(JLabel.CENTER);
        //tabel Data Bahan Baku
        for (int i = 0; i < Table_progress_lp.getColumnCount(); i++) {
            Table_progress_lp.getColumnModel().getColumn(i).setCellRenderer(TableAlignment);
        }*/
    }

    public void executeSQLQuery(String query, String message) {
        Utility.db.getConnection();
        try {
            Utility.db.getConnection().createStatement();
            if ((Utility.db.getStatement().executeUpdate(query)) == 1) {
                refreshTable();
                JOptionPane.showMessageDialog(this, "data " + message + " Successfully");
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

        jPanel_DataRendam = new javax.swing.JPanel();
        txt_search_proses = new javax.swing.JTextField();
        button_search_proses = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        Table_progress_lp = new javax.swing.JTable();
        button_export_data_rendam = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        label_total_data_rendam = new javax.swing.JLabel();
        label_total_bk = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        ComboBox_ruangan = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        ComboBox_filter_tanggal = new javax.swing.JComboBox<>();
        Date_filter1 = new com.toedter.calendar.JDateChooser();
        Date_filter2 = new com.toedter.calendar.JDateChooser();
        ComboBox_filter_status = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        button_rekap = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        label_rata_tandon = new javax.swing.JLabel();
        label_rata_cabut = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        label_rata_koreksi = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        label_rata_cetak = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        label_rata_f2 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        label_rata_qc = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        label_rata_proses = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        label_rata_grading = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        button_evaluasi_lama_progres = new javax.swing.JButton();
        ComboBox_search = new javax.swing.JComboBox<>();
        jProgressBar1 = new javax.swing.JProgressBar();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Proses Laporan Produksi", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N
        setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        setPreferredSize(new java.awt.Dimension(1366, 700));

        jPanel_DataRendam.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_DataRendam.setPreferredSize(new java.awt.Dimension(1366, 688));

        txt_search_proses.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_proses.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_prosesKeyPressed(evt);
            }
        });

        button_search_proses.setBackground(new java.awt.Color(255, 255, 255));
        button_search_proses.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_proses.setText("Search");
        button_search_proses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_prosesActionPerformed(evt);
            }
        });

        jScrollPane4.setBackground(new java.awt.Color(255, 255, 255));

        Table_progress_lp.setAutoCreateRowSorter(true);
        Table_progress_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_progress_lp.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP", "No Kartu", "Memo", "Grade", "Bulu Upah", "Ruang", "Kpg", "Gram", "Tgl LP", "Rendam", "Cuci", "Cabut", "Mulai Cabut", "Selesai Cbt", "Cetak", "Selesai Ctk", "F2", "Koreksi F2", "F1", "F2", "Selesai F2", "QC", "Selesai QC", "Grading BJ", "Lama Tandon", "Lama Cabut", "Lama Koreksi", "Lama Cetak", "Lama F2", "Lama QC", "Lama Proses", "Lama Grading", "Status", "RSB CT 1"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_progress_lp.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(Table_progress_lp);

        button_export_data_rendam.setBackground(new java.awt.Color(255, 255, 255));
        button_export_data_rendam.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_data_rendam.setText("Export to Excel");
        button_export_data_rendam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_data_rendamActionPerformed(evt);
            }
        });

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel6.setText("Total LP :");

        label_total_data_rendam.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_rendam.setFont(new java.awt.Font("Calibri", 1, 12)); // NOI18N
        label_total_data_rendam.setText("TOTAL");

        label_total_bk.setBackground(new java.awt.Color(255, 255, 255));
        label_total_bk.setFont(new java.awt.Font("Calibri", 1, 12)); // NOI18N
        label_total_bk.setText("TOTAL");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel7.setText("Berat Basah :");

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel1.setText("Grams");

        ComboBox_ruangan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_ruangan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "A", "B", "C", "D" }));

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Ruangan :");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Filter tanggal");

        ComboBox_filter_tanggal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_filter_tanggal.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Laporan Produksi", "Rendam", "Cuci", "Masuk Cabut", "Selesai Cabut", "Masuk Cetak", "Selesai Cetak", "Masuk F2", "Koreksi Kering", "F1", "F2", "Selesai F2", "QC", "Selesai QC", "Grading BJ" }));

        Date_filter1.setBackground(new java.awt.Color(255, 255, 255));
        Date_filter1.setDateFormatString("dd MMMM yyyy");
        Date_filter1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_filter2.setBackground(new java.awt.Color(255, 255, 255));
        Date_filter2.setDateFormatString("dd MMMM yyyy");
        Date_filter2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        ComboBox_filter_status.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_filter_status.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "LP Belum Grading", "LP Proses Grading", "Barang Jadi" }));

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Status LP :");

        button_rekap.setBackground(new java.awt.Color(255, 255, 255));
        button_rekap.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_rekap.setText("Rekap per Grade");
        button_rekap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_rekapActionPerformed(evt);
            }
        });

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel9.setText("Rata2 Lama Tandon :");

        label_rata_tandon.setBackground(new java.awt.Color(255, 255, 255));
        label_rata_tandon.setFont(new java.awt.Font("Calibri", 1, 12)); // NOI18N
        label_rata_tandon.setText("TOTAL");

        label_rata_cabut.setBackground(new java.awt.Color(255, 255, 255));
        label_rata_cabut.setFont(new java.awt.Font("Calibri", 1, 12)); // NOI18N
        label_rata_cabut.setText("TOTAL");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel10.setText("Rata2 Lama Cabut :");

        label_rata_koreksi.setBackground(new java.awt.Color(255, 255, 255));
        label_rata_koreksi.setFont(new java.awt.Font("Calibri", 1, 12)); // NOI18N
        label_rata_koreksi.setText("TOTAL");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel11.setText("Rata2 Lama Koreksi :");

        label_rata_cetak.setBackground(new java.awt.Color(255, 255, 255));
        label_rata_cetak.setFont(new java.awt.Font("Calibri", 1, 12)); // NOI18N
        label_rata_cetak.setText("TOTAL");

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel12.setText("Rata2 Lama Cetak :");

        label_rata_f2.setBackground(new java.awt.Color(255, 255, 255));
        label_rata_f2.setFont(new java.awt.Font("Calibri", 1, 12)); // NOI18N
        label_rata_f2.setText("TOTAL");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel13.setText("Rata2 Lama F2 :");

        label_rata_qc.setBackground(new java.awt.Color(255, 255, 255));
        label_rata_qc.setFont(new java.awt.Font("Calibri", 1, 12)); // NOI18N
        label_rata_qc.setText("TOTAL");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel14.setText("Rata2 Lama QC :");

        label_rata_proses.setBackground(new java.awt.Color(255, 255, 255));
        label_rata_proses.setFont(new java.awt.Font("Calibri", 1, 12)); // NOI18N
        label_rata_proses.setText("TOTAL");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel15.setText("Rata2 Lama proses :");

        label_rata_grading.setBackground(new java.awt.Color(255, 255, 255));
        label_rata_grading.setFont(new java.awt.Font("Calibri", 1, 12)); // NOI18N
        label_rata_grading.setText("TOTAL");

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel16.setText("Rata2 Lama Grading :");

        button_evaluasi_lama_progres.setBackground(new java.awt.Color(255, 255, 255));
        button_evaluasi_lama_progres.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_evaluasi_lama_progres.setText("Evaluasi Lama Progres");
        button_evaluasi_lama_progres.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_evaluasi_lama_progresActionPerformed(evt);
            }
        });

        ComboBox_search.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_search.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No LP", "No Kartu", "Memo", "Grade" }));

        jProgressBar1.setBackground(new java.awt.Color(255, 255, 255));
        jProgressBar1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        javax.swing.GroupLayout jPanel_DataRendamLayout = new javax.swing.GroupLayout(jPanel_DataRendam);
        jPanel_DataRendam.setLayout(jPanel_DataRendamLayout);
        jPanel_DataRendamLayout.setHorizontalGroup(
            jPanel_DataRendamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_DataRendamLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_DataRendamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1336, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_DataRendamLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_search, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_proses, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_filter_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_filter1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_filter2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_filter_status, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search_proses)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel_DataRendamLayout.createSequentialGroup()
                        .addGroup(jPanel_DataRendamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_DataRendamLayout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_bk)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel1))
                            .addGroup(jPanel_DataRendamLayout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data_rendam)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_DataRendamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_DataRendamLayout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_rata_cabut))
                            .addGroup(jPanel_DataRendamLayout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_rata_tandon)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_DataRendamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_DataRendamLayout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_rata_cetak))
                            .addGroup(jPanel_DataRendamLayout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_rata_koreksi)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_DataRendamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_DataRendamLayout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_rata_f2))
                            .addGroup(jPanel_DataRendamLayout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_rata_qc)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_DataRendamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel15)
                            .addComponent(jLabel16))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_DataRendamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_DataRendamLayout.createSequentialGroup()
                                .addComponent(label_rata_proses)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(button_rekap)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_data_rendam))
                            .addComponent(label_rata_grading))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_evaluasi_lama_progres)))
                .addContainerGap())
        );
        jPanel_DataRendamLayout.setVerticalGroup(
            jPanel_DataRendamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_DataRendamLayout.createSequentialGroup()
                .addGroup(jPanel_DataRendamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_proses, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search_proses, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_filter_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_filter1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_filter2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_filter_status, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_search, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 591, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_DataRendamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_DataRendamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel_DataRendamLayout.createSequentialGroup()
                            .addGroup(jPanel_DataRendamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel6)
                                .addComponent(label_total_data_rendam))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel_DataRendamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel7)
                                .addComponent(label_total_bk)
                                .addComponent(jLabel1)))
                        .addGroup(jPanel_DataRendamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_DataRendamLayout.createSequentialGroup()
                                .addGroup(jPanel_DataRendamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel9)
                                    .addComponent(label_rata_tandon))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel_DataRendamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel10)
                                    .addComponent(label_rata_cabut)))
                            .addGroup(jPanel_DataRendamLayout.createSequentialGroup()
                                .addGroup(jPanel_DataRendamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel11)
                                    .addComponent(label_rata_koreksi))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel_DataRendamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel12)
                                    .addComponent(label_rata_cetak)))
                            .addGroup(jPanel_DataRendamLayout.createSequentialGroup()
                                .addGroup(jPanel_DataRendamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel13)
                                    .addComponent(label_rata_f2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel_DataRendamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel_DataRendamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel16)
                                        .addComponent(label_rata_grading))
                                    .addGroup(jPanel_DataRendamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel14)
                                        .addComponent(label_rata_qc))))))
                    .addGroup(jPanel_DataRendamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel15)
                        .addComponent(label_rata_proses))
                    .addGroup(jPanel_DataRendamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(button_export_data_rendam, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_rekap, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(button_evaluasi_lama_progres, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_DataRendam, javax.swing.GroupLayout.DEFAULT_SIZE, 1356, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_DataRendam, javax.swing.GroupLayout.DEFAULT_SIZE, 676, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txt_search_prosesKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_prosesKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_prosesKeyPressed

    private void button_search_prosesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_prosesActionPerformed
        // TODO add your handling code here:
        ComboBox_ruangan.setEnabled(false);
        ComboBox_search.setEnabled(false);
        txt_search_proses.setEnabled(false);
        ComboBox_filter_tanggal.setEnabled(false);
        Date_filter1.setEnabled(false);
        Date_filter2.setEnabled(false);
        ComboBox_filter_status.setEnabled(false);
        button_search_proses.setEnabled(false);
        try {
            jProgressBar1.setMinimum(0);
            jProgressBar1.setValue(0);
            jProgressBar1.setStringPainted(true);
            Thread thread = new Thread() {
                @Override
                public void run() {
                    refreshTable();
                    jProgressBar1.setValue(jProgressBar1.getMaximum());
                    JOptionPane.showMessageDialog(null, "Proses Selesai !");
                    ComboBox_ruangan.setEnabled(true);
                    ComboBox_search.setEnabled(true);
                    txt_search_proses.setEnabled(true);
                    ComboBox_filter_tanggal.setEnabled(true);
                    Date_filter1.setEnabled(true);
                    Date_filter2.setEnabled(true);
                    ComboBox_filter_status.setEnabled(true);
                    button_search_proses.setEnabled(true);
                }
            };
            thread.start();
        } catch (Exception e) {
            Logger.getLogger(JPanel_ProgressLP.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_search_prosesActionPerformed

    private void button_export_data_rendamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_data_rendamActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_progress_lp.getModel();
        ExportToExcel.writeToExcel(model, jPanel_DataRendam);
    }//GEN-LAST:event_button_export_data_rendamActionPerformed

    private void button_rekapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_rekapActionPerformed
        // TODO add your handling code here:
        String search = "";
        switch (ComboBox_search.getSelectedIndex()) {
            case 0:
                search = "`tb_laporan_produksi`.`no_laporan_produksi`";
                break;
            case 1:
                search = "`tb_laporan_produksi`.`no_kartu_waleta`";
                break;
            case 2:
                search = "`tb_laporan_produksi`.`memo_lp`";
                break;
            case 3:
                search = "`tb_laporan_produksi`.`kode_grade`";
                break;
            default:
                break;
        }
        String filter_status = "";
        if (ComboBox_filter_status.getSelectedIndex() == 1) {
            filter_status = "AND `tanggal_grading` IS NULL";
        } else if (ComboBox_filter_status.getSelectedIndex() == 2) {
            filter_status = "AND `tanggal_grading` IS NOT NULL AND (`tb_tutupan_grading`.`status_box` = 'PROSES' OR `tb_tutupan_grading`.`status_box` IS NULL)";
        } else if (ComboBox_filter_status.getSelectedIndex() == 3) {
            filter_status = "AND `tb_tutupan_grading`.`status_box` = 'SELESAI'";
        }
        String ruang = "";
        if (ComboBox_ruangan.getSelectedItem() != "All") {
            ruang = ComboBox_ruangan.getSelectedItem().toString();
        }
        if (Date_filter1.getDate() != null && Date_filter2.getDate() != null) {
            String jenis_filter_tanggal = null;
            switch (ComboBox_filter_tanggal.getSelectedIndex()) {
                case 0:
                    jenis_filter_tanggal = "`tanggal_lp`";
                    break;
                case 1:
                    jenis_filter_tanggal = "`tanggal_rendam`";
                    break;
                case 2:
                    jenis_filter_tanggal = "`tgl_masuk_cuci`";
                    break;
                case 3:
                    jenis_filter_tanggal = "MIN(`tb_detail_pencabut`.`tanggal_cabut`)";
                    break;
                case 4:
                    jenis_filter_tanggal = "`tgl_setor_cabut`";
                    break;
                case 5:
                    jenis_filter_tanggal = "`tgl_mulai_cetak`";
                    break;
                case 6:
                    jenis_filter_tanggal = "`tgl_selesai_cetak`";
                    break;
                case 7:
                    jenis_filter_tanggal = "`tgl_masuk_f2`";
                    break;
                case 8:
                    jenis_filter_tanggal = "`tgl_setor_f2`";
                    break;
                case 9:
                    jenis_filter_tanggal = "`tgl_masuk`";
                    break;
                case 10:
                    jenis_filter_tanggal = "`tgl_selesai`";
                    break;
                case 11:
                    jenis_filter_tanggal = "`tanggal_grading`";
                    break;
                default:
                    break;
            }
            sql_rekap = "SELECT *, `tb_laporan_produksi`.`kode_grade`, SUM(`jumlah_keping`) AS 'total_keping', SUM(`berat_basah`) AS 'total_gram'\n"
                    + "FROM `tb_laporan_produksi` \n"
                    + "LEFT JOIN `tb_rendam` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_rendam`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_cuci` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cuci`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_cabut` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cabut`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_cetak` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cetak`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_finishing_2` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_finishing_2`.`no_laporan_produksi`"
                    + "LEFT JOIN `tb_lab_laporan_produksi` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_lab_laporan_produksi`.`no_laporan_produksi`"
                    + "LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_bahan_jadi_masuk`.`kode_asal`\n"
                    + "LEFT JOIN `tb_tutupan_grading` ON `tb_bahan_jadi_masuk`.`kode_tutupan` = `tb_tutupan_grading`.`kode_tutupan`\n"
                    + "WHERE " + search + " LIKE '%" + txt_search_proses.getText() + "%' AND `tb_laporan_produksi`.`ruangan` LIKE '%" + ruang + "%' AND YEAR(`tb_laporan_produksi`.`tanggal_lp`)>=2018 \n"
                    + "AND (" + jenis_filter_tanggal + " BETWEEN '" + dateFormat.format(Date_filter1.getDate()) + "' AND '" + dateFormat.format(Date_filter2.getDate()) + "') " + filter_status + "\n"
                    + "GROUP BY `tb_laporan_produksi`.`kode_grade`";
        } else {
            sql_rekap = "SELECT *, `tb_laporan_produksi`.`kode_grade`, SUM(`jumlah_keping`) AS 'total_keping', SUM(`berat_basah`) AS 'total_gram'\n"
                    + "FROM `tb_laporan_produksi` \n"
                    + "LEFT JOIN `tb_rendam` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_rendam`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_cuci` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cuci`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_cabut` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cabut`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_cetak` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cetak`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_finishing_2` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_finishing_2`.`no_laporan_produksi`"
                    + "LEFT JOIN `tb_lab_laporan_produksi` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_lab_laporan_produksi`.`no_laporan_produksi`"
                    + "LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_bahan_jadi_masuk`.`kode_asal`\n"
                    + "LEFT JOIN `tb_tutupan_grading` ON `tb_bahan_jadi_masuk`.`kode_tutupan` = `tb_tutupan_grading`.`kode_tutupan`\n"
                    + "WHERE " + search + " LIKE '%" + txt_search_proses.getText() + "%' AND `tb_laporan_produksi`.`ruangan` LIKE '%" + ruang + "%' AND YEAR(`tb_laporan_produksi`.`tanggal_lp`)>=2018 " + filter_status + "\n"
                    + "GROUP BY `tb_laporan_produksi`.`kode_grade`";
        }
        JFrame_RekapProgressLP dialog = new JFrame_RekapProgressLP(sql_rekap);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
    }//GEN-LAST:event_button_rekapActionPerformed

    private void button_evaluasi_lama_progresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_evaluasi_lama_progresActionPerformed
        // TODO add your handling code here:
        JFrame_Evaluasi_lama_proses FRAME = new JFrame_Evaluasi_lama_proses();
        FRAME.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        FRAME.pack();
        FRAME.setLocationRelativeTo(this);
        FRAME.setVisible(true);
        FRAME.setEnabled(true);
    }//GEN-LAST:event_button_evaluasi_lama_progresActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_filter_status;
    private javax.swing.JComboBox<String> ComboBox_filter_tanggal;
    private javax.swing.JComboBox<String> ComboBox_ruangan;
    private javax.swing.JComboBox<String> ComboBox_search;
    private com.toedter.calendar.JDateChooser Date_filter1;
    private com.toedter.calendar.JDateChooser Date_filter2;
    private javax.swing.JTable Table_progress_lp;
    private javax.swing.JButton button_evaluasi_lama_progres;
    private javax.swing.JButton button_export_data_rendam;
    private javax.swing.JButton button_rekap;
    private javax.swing.JButton button_search_proses;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel_DataRendam;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel label_rata_cabut;
    private javax.swing.JLabel label_rata_cetak;
    private javax.swing.JLabel label_rata_f2;
    private javax.swing.JLabel label_rata_grading;
    private javax.swing.JLabel label_rata_koreksi;
    private javax.swing.JLabel label_rata_proses;
    private javax.swing.JLabel label_rata_qc;
    private javax.swing.JLabel label_rata_tandon;
    private javax.swing.JLabel label_total_bk;
    private javax.swing.JLabel label_total_data_rendam;
    private javax.swing.JTextField txt_search_proses;
    // End of variables declaration//GEN-END:variables

}
