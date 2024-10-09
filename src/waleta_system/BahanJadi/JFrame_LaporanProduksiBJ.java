package waleta_system.BahanJadi;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;

public class JFrame_LaporanProduksiBJ extends javax.swing.JFrame {

    
    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    static int loop = 0;
    static Timer t;

    public JFrame_LaporanProduksiBJ() {
        initComponents();
    }

    public void init() {
        try {
            
            
            refreshTableLP();
//            refreshTableGrading();

            tabel_LaporanProduksi.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && tabel_LaporanProduksi.getSelectedRow() != -1) {
                        int i = tabel_LaporanProduksi.getSelectedRow();
                        refreshTableGrading();
                        refreshTableCabut();
                    }
                }
            });

            TimerTask timer = new TimerTask() {
                @Override
                public void run() {
                    tabel_LaporanProduksi.setRowSelectionInterval(loop, loop);
                    loop++;
                    if (loop == tabel_LaporanProduksi.getRowCount()) {
                        loop = 0;
                    }
                }
            };
            t = new Timer();
            t.schedule(timer, 1000, 5000);
        } catch (Exception ex) {
            Logger.getLogger(JPanel_LaporanProduksi_BahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTableLP() {
        try {
            decimalFormat.setMaximumFractionDigits(2);
            float bk, bk12, utuh, fbonus, fnol, flat, sh, sp, kaki, netto;
            float rend_utuh, rend_flat, rend_sh, rend_sp;
            float rend_utuh12, rend_flat12, rend_sh12, rend_sp12;
            float tot_kpg = 0, tot_utuh = 0, tot_kaki = 0, tot_pecah = 0, tot_flat = 0, tot_berat_basah = 0, tot_berat_kering = 0;
            float tot_sesekan = 0, tot_hancuran = 0, tot_rontokan = 0, tot_bonggol = 0, tot_serabut = 0;
            float rata2_rend_utuh = 0, rata2_rend_flat = 0, rata2_rend_sp = 0, rata2_rend_sh = 0;
            float rata2_rend_utuh12 = 0, rata2_rend_flat12 = 0, rata2_rend_sp12 = 0, rata2_rend_sh12 = 0;
            DefaultTableModel model = (DefaultTableModel) tabel_LaporanProduksi.getModel();
            model.setRowCount(0);

            String tgl1 = dateFormat.format(new Date(date.getTime() - (2 * 24 * 60 * 60 * 1000)));
            String tgl2 = dateFormat.format(date);
            label_tgl1.setText(new SimpleDateFormat("dd MMM yyyy").format(new Date(date.getTime() - (2 * 24 * 60 * 60 * 1000))));
            label_tgl2.setText(new SimpleDateFormat("dd MMM yyyy").format(date));

            sql = "SELECT `tb_laporan_produksi`.`no_kartu_waleta`, `kode_asal`, `tb_laporan_produksi`.`kode_grade`, `tb_laporan_produksi`.`memo_lp`, `tb_laporan_produksi`.`ruangan`, `tb_laporan_produksi`.`tanggal_lp`, `tanggal_grading`, `tb_laporan_produksi`.`jumlah_keping`, `tb_laporan_produksi`.`berat_basah`, `tb_laporan_produksi`.`berat_kering`, `tb_cabut`.`ketua_regu`, `tb_karyawan`.`nama_pegawai`, "
                    + "`fbonus_f2`, `berat_fbonus`, `fnol_f2`, `berat_fnol`, `pecah_f2`, `berat_pecah`, `flat_f2`, `berat_flat`, `sesekan`, `hancuran`, `rontokan`, `bonggol`, `serabut`, `tambahan_kaki1`, `lp_kaki1`, `tambahan_kaki2`, `lp_kaki2`"
                    + "FROM `tb_bahan_jadi_masuk` LEFT JOIN `tb_laporan_produksi` ON `tb_bahan_jadi_masuk`.`kode_asal` = `tb_laporan_produksi`.`no_laporan_produksi` "
                    + "LEFT JOIN `tb_finishing_2` ON `tb_finishing_2`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`"
                    + "LEFT JOIN `tb_cabut` ON `tb_cabut`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`"
                    + "LEFT JOIN `tb_cetak` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`"
                    + "LEFT JOIN `tb_karyawan` ON `tb_cetak`.`cetak_dikerjakan` = `tb_karyawan`.`id_pegawai`"
                    + "WHERE `tanggal_grading` BETWEEN '" + tgl1 + "' AND '" + tgl2 + "' ORDER BY `tanggal_grading` DESC";

            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[15];
            while (rs.next()) {
                bk = rs.getFloat("berat_kering");
                bk12 = bk * 1.12f;
                fbonus = rs.getFloat("berat_fbonus");
                fnol = rs.getFloat("berat_fnol");
                utuh = fbonus + fnol;
                kaki = rs.getFloat("tambahan_kaki1") + rs.getFloat("tambahan_kaki2");
                netto = utuh - kaki;
                flat = rs.getFloat("berat_pecah") + rs.getFloat("berat_flat");
                sp = rs.getFloat("sesekan") + rs.getFloat("hancuran") + rs.getFloat("rontokan") + rs.getFloat("bonggol") + rs.getFloat("serabut");
                sh = bk - (netto + flat + sp);

                rend_utuh = (utuh / bk) * 100;
                rend_flat = (flat / bk) * 100;
                rend_sp = (sp / bk) * 100;
                rend_sh = (sh / bk) * 100;
                rend_utuh12 = (utuh / bk12) * 100;
                rend_flat12 = (flat / bk12) * 100;
                rend_sp12 = (sp / bk12) * 100;
                rend_sh12 = (sh / bk12) * 100;

                row[0] = rs.getString("kode_asal");
                row[1] = rs.getString("memo_lp");
                row[2] = rs.getString("kode_grade");
                row[3] = rs.getString("ruangan");
                row[4] = rs.getDate("tanggal_grading");
                row[5] = rs.getInt("jumlah_keping");
                row[6] = rs.getInt("berat_basah");
                row[7] = rs.getInt("berat_kering");
                row[8] = bk12;
                row[9] = (double) Math.round(rend_utuh * 100) / 100;
                row[10] = (double) Math.round(rend_flat * 100) / 100;
                row[11] = (double) Math.round(rend_sp * 100) / 100;
                row[12] = (double) Math.round(rend_sh * 100) / 100;
                row[13] = rs.getString("ketua_regu");
                row[14] = rs.getString("nama_pegawai");

                tot_kpg = tot_kpg + rs.getInt("jumlah_keping");
                tot_utuh = tot_utuh + utuh;
                tot_berat_basah = tot_berat_basah + rs.getInt("berat_basah");
                tot_berat_kering = tot_berat_kering + rs.getInt("berat_kering");
                tot_pecah = tot_pecah + rs.getFloat("berat_pecah");
                tot_flat = tot_flat + rs.getFloat("berat_flat");
                tot_sesekan = tot_sesekan + rs.getFloat("sesekan");
                tot_hancuran = tot_hancuran + rs.getFloat("hancuran");
                tot_rontokan = tot_rontokan + rs.getFloat("rontokan");
                tot_bonggol = tot_bonggol + rs.getFloat("bonggol");
                tot_serabut = tot_serabut + rs.getFloat("serabut");
                tot_kaki = tot_kaki + kaki;

                rata2_rend_utuh = rata2_rend_utuh + rend_utuh;
                rata2_rend_flat = rata2_rend_flat + rend_flat;
                rata2_rend_sp = rata2_rend_sp + rend_sp;
                rata2_rend_sh = rata2_rend_sh + rend_sh;
                rata2_rend_utuh12 = rata2_rend_utuh12 + rend_utuh12;
                rata2_rend_flat12 = rata2_rend_flat12 + rend_flat12;
                rata2_rend_sp12 = rata2_rend_sp12 + rend_sp12;
                rata2_rend_sh12 = rata2_rend_sh12 + rend_sh12;
                model.addRow(row);
            }

            rata2_rend_utuh = rata2_rend_utuh / tabel_LaporanProduksi.getRowCount();
            rata2_rend_flat = rata2_rend_flat / tabel_LaporanProduksi.getRowCount();
            rata2_rend_sp = rata2_rend_sp / tabel_LaporanProduksi.getRowCount();
            rata2_rend_sh = rata2_rend_sh / tabel_LaporanProduksi.getRowCount();

            rata2_rend_utuh12 = rata2_rend_utuh12 / tabel_LaporanProduksi.getRowCount();
            rata2_rend_flat12 = rata2_rend_flat12 / tabel_LaporanProduksi.getRowCount();
            rata2_rend_sp12 = rata2_rend_sp12 / tabel_LaporanProduksi.getRowCount();
            rata2_rend_sh12 = rata2_rend_sh12 / tabel_LaporanProduksi.getRowCount();

            decimalFormat.setGroupingUsed(true);
            ColumnsAutoSizer.sizeColumnsToFit(tabel_LaporanProduksi);
            label_totalLP.setText(Integer.toString(tabel_LaporanProduksi.getRowCount()));
            label_total_kpg.setText(decimalFormat.format(tot_kpg));
            label_total_gram_basah.setText(decimalFormat.format(tot_berat_basah));
            label_total_gram_kering.setText(decimalFormat.format(tot_berat_kering));
            label_total_gram_utuh.setText(decimalFormat.format(tot_utuh));
            label_total_gram_pch.setText(decimalFormat.format(tot_pecah));
            label_total_gram_flat.setText(decimalFormat.format(tot_flat));
            label_total_gram_kaki.setText(decimalFormat.format(tot_kaki));

            label_rata2_rend_utuh.setText(decimalFormat.format(rata2_rend_utuh));
            label_rata2_rend_flat.setText(decimalFormat.format(rata2_rend_flat));
            label_rata2_rend_sp.setText(decimalFormat.format(rata2_rend_sp));
            label_rata2_rend_sh.setText(decimalFormat.format(rata2_rend_sh));
//            label_rata2_rend_utuh12.setText(decimalFormat.format(rata2_rend_utuh12));
//            label_rata2_rend_flat12.setText(decimalFormat.format(rata2_rend_flat12));
//            label_rata2_rend_sp12.setText(decimalFormat.format(rata2_rend_sp12));
//            label_rata2_rend_sh12.setText(decimalFormat.format(rata2_rend_sh12));

            label_tot_sesekan.setText(decimalFormat.format(tot_sesekan));
            label_tot_hancuran.setText(decimalFormat.format(tot_hancuran));
            label_tot_rontokan.setText(decimalFormat.format(tot_rontokan));
            label_tot_bonggol.setText(decimalFormat.format(tot_bonggol));
            label_tot_serabut.setText(decimalFormat.format(tot_serabut));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_LaporanProduksi_BahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTableGrading() {
        try {
            decimalFormat.setMaximumFractionDigits(2);
            float tot_kpg = 0, tot_gram = 0;
            int i = tabel_LaporanProduksi.getSelectedRow();
            DefaultTableModel model = (DefaultTableModel) tabel_hasilGrading.getModel();
            model.setRowCount(0);

            sql = "SELECT `kode_asal_bahan_jadi`, `tb_grade_bahan_jadi`.`kode_grade`, `keping`, `gram` \n"
                    + "FROM `tb_grading_bahan_jadi`  LEFT JOIN `tb_grade_bahan_jadi` ON `tb_grading_bahan_jadi`.`grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`\n"
                    + "WHERE `kode_asal_bahan_jadi` = '" + tabel_LaporanProduksi.getValueAt(i, 0).toString() + "'"
                    + "ORDER BY `gram` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[4];
            while (rs.next()) {
                row[0] = rs.getString("kode_grade");
                row[1] = rs.getInt("keping");
                row[2] = rs.getInt("gram");

                tot_kpg = tot_kpg + rs.getInt("keping");
                tot_gram = tot_gram + rs.getInt("gram");
                if (rs.getString("kode_grade") != null) {
                    model.addRow(row);
                }
            }

            float total_persen_gram = 0;
            for (int j = 0; j < tabel_hasilGrading.getRowCount(); j++) {
                float gram_grading = Float.valueOf(tabel_hasilGrading.getValueAt(j, 2).toString());
                float persen_gram = (gram_grading / tot_gram) * 100;
                total_persen_gram = total_persen_gram + persen_gram;
                tabel_hasilGrading.setValueAt(((double) Math.round(persen_gram * 100) / 100), j, 3);
            }

            decimalFormat.setGroupingUsed(true);
            ColumnsAutoSizer.sizeColumnsToFit(tabel_hasilGrading);
            label_total_grade_hasil.setText(Integer.toString(tabel_hasilGrading.getRowCount()));
            label_total_kpg_grading.setText(decimalFormat.format(tot_kpg));
            label_total_gram_grading.setText(decimalFormat.format(tot_gram));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_LaporanProduksi_BahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTableCabut() {
        try {
            int i = tabel_LaporanProduksi.getSelectedRow();
            DefaultTableModel model = (DefaultTableModel) tabel_pencabut.getModel();
            model.setRowCount(0);

            sql = "SELECT `tb_detail_pencabut`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `grup_cabut`, SUM(`jumlah_cabut`) AS 'kpg_cabut' \n"
                    + "FROM `tb_detail_pencabut` LEFT JOIN `tb_karyawan` ON `tb_detail_pencabut`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                    + "WHERE `no_laporan_produksi` = '" + tabel_LaporanProduksi.getValueAt(i, 0).toString() + "'\n"
                    + "GROUP BY `tb_detail_pencabut`.`id_pegawai`";
//            System.out.println(sql);
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[3];
            while (rs.next()) {
                row[0] = rs.getString("nama_pegawai");
                row[1] = rs.getString("grup_cabut");
                row[2] = rs.getInt("kpg_cabut");
                model.addRow(row);
            }

            decimalFormat.setGroupingUsed(true);
            ColumnsAutoSizer.sizeColumnsToFit(tabel_pencabut);
            label_total_pekerja.setText(Integer.toString(tabel_pencabut.getRowCount()));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_LaporanProduksi_BahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_LaporanProduksi = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabel_hasilGrading = new javax.swing.JTable();
        label_totalLP = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        label_total_gram_utuh = new javax.swing.JLabel();
        label_total_gram_kaki = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        label_total_gram_grading = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        label_total_grade_hasil = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        label_total_kpg_grading = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        label_total_gram_pch = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        label_total_gram_flat = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        label_total_gram_basah = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        label_total_gram_kering = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        label_rata2_rend_flat = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        label_rata2_rend_sp = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        label_rata2_rend_sh = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        label_rata2_rend_utuh = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        label_tot_hancuran = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        label_tot_rontokan = new javax.swing.JLabel();
        label_tot_bonggol = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        label_tot_sesekan = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        label_tot_serabut = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        label_total_kpg = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        label_tgl1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        label_tgl2 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabel_pencabut = new javax.swing.JTable();
        jLabel17 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        label_total_pekerja = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Laporan Produksi Bahan Jadi", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 20))); // NOI18N

        tabel_LaporanProduksi.setAutoCreateRowSorter(true);
        tabel_LaporanProduksi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_LaporanProduksi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP", "Memo", "Grade", "Ruang", "Tgl Grading", "Kpg", "Berat", "BK", "BK 12%", "% Utuh", "% Flat", "% SP", "% SH", "Ketua Cabut", "Pekerja Cetak"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class
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
        tabel_LaporanProduksi.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tabel_LaporanProduksi);

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Total Laporan Produksi :");

        tabel_hasilGrading.setAutoCreateRowSorter(true);
        tabel_hasilGrading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_hasilGrading.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade BJ", "Kpg", "Gram", "Persentase (%)"
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
        tabel_hasilGrading.setRowSelectionAllowed(false);
        jScrollPane2.setViewportView(tabel_hasilGrading);

        label_totalLP.setBackground(new java.awt.Color(255, 255, 255));
        label_totalLP.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_totalLP.setText("0");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Total Gram Utuh :");

        label_total_gram_utuh.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_utuh.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gram_utuh.setText("0");

        label_total_gram_kaki.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_kaki.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gram_kaki.setText("0");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("Total Gram Kaki :");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel11.setText("Gram");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel13.setText("Gram");

        label_total_gram_grading.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_grading.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gram_grading.setText("0");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Gram");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel15.setText("Keping");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Total Grade :");

        label_total_grade_hasil.setBackground(new java.awt.Color(255, 255, 255));
        label_total_grade_hasil.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_grade_hasil.setText("0");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Total :");

        label_total_kpg_grading.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg_grading.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_kpg_grading.setText("0");

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel16.setText("Hasil Grading");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Total Gram Pecah :");

        label_total_gram_pch.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_pch.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gram_pch.setText("0");

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel18.setText("Gram");

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel19.setText("Total Gram Flat :");

        label_total_gram_flat.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_flat.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gram_flat.setText("0");

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel20.setText("Gram");

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel21.setText("Total Gram Berat Basah :");

        label_total_gram_basah.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_basah.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gram_basah.setText("0");

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel22.setText("Gram");

        label_total_gram_kering.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_kering.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gram_kering.setText("0");

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel23.setText("Total Gram Berat Kering :");

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel24.setText("Gram");

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel25.setText("Rendemen Flat :");

        label_rata2_rend_flat.setBackground(new java.awt.Color(255, 255, 255));
        label_rata2_rend_flat.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_rata2_rend_flat.setText("0");

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel26.setText("%");

        jLabel27.setBackground(new java.awt.Color(255, 255, 255));
        jLabel27.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel27.setText("Susut Proses :");

        label_rata2_rend_sp.setBackground(new java.awt.Color(255, 255, 255));
        label_rata2_rend_sp.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_rata2_rend_sp.setText("0");

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel28.setText("%");

        label_rata2_rend_sh.setBackground(new java.awt.Color(255, 255, 255));
        label_rata2_rend_sh.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_rata2_rend_sh.setText("0");

        jLabel29.setBackground(new java.awt.Color(255, 255, 255));
        jLabel29.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel29.setText("Susut Hilang :");

        jLabel30.setBackground(new java.awt.Color(255, 255, 255));
        jLabel30.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel30.setText("%");

        jLabel31.setBackground(new java.awt.Color(255, 255, 255));
        jLabel31.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel31.setText("Rendemen Utuh :");

        label_rata2_rend_utuh.setBackground(new java.awt.Color(255, 255, 255));
        label_rata2_rend_utuh.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_rata2_rend_utuh.setText("0");

        jLabel32.setBackground(new java.awt.Color(255, 255, 255));
        jLabel32.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel32.setText("%");

        jLabel33.setBackground(new java.awt.Color(255, 255, 255));
        jLabel33.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel33.setText("Total Hancuran :");

        label_tot_hancuran.setBackground(new java.awt.Color(255, 255, 255));
        label_tot_hancuran.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_tot_hancuran.setText("0");

        jLabel35.setBackground(new java.awt.Color(255, 255, 255));
        jLabel35.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel35.setText("Total Rontokan :");

        label_tot_rontokan.setBackground(new java.awt.Color(255, 255, 255));
        label_tot_rontokan.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_tot_rontokan.setText("0");

        label_tot_bonggol.setBackground(new java.awt.Color(255, 255, 255));
        label_tot_bonggol.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_tot_bonggol.setText("0");

        jLabel37.setBackground(new java.awt.Color(255, 255, 255));
        jLabel37.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel37.setText("Total Bonggol :");

        jLabel39.setBackground(new java.awt.Color(255, 255, 255));
        jLabel39.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel39.setText("Total Sesekan :");

        label_tot_sesekan.setBackground(new java.awt.Color(255, 255, 255));
        label_tot_sesekan.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_tot_sesekan.setText("0");

        jLabel34.setBackground(new java.awt.Color(255, 255, 255));
        jLabel34.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel34.setText("Gram");

        jLabel36.setBackground(new java.awt.Color(255, 255, 255));
        jLabel36.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel36.setText("Gram");

        jLabel38.setBackground(new java.awt.Color(255, 255, 255));
        jLabel38.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel38.setText("Gram");

        jLabel40.setBackground(new java.awt.Color(255, 255, 255));
        jLabel40.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel40.setText("Gram");

        jLabel41.setBackground(new java.awt.Color(255, 255, 255));
        jLabel41.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel41.setText("Total Serabut :");

        label_tot_serabut.setBackground(new java.awt.Color(255, 255, 255));
        label_tot_serabut.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_tot_serabut.setText("0");

        jLabel42.setBackground(new java.awt.Color(255, 255, 255));
        jLabel42.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel42.setText("Gram");

        jLabel43.setBackground(new java.awt.Color(255, 255, 255));
        jLabel43.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel43.setText("Total Keping :");

        jLabel44.setBackground(new java.awt.Color(255, 255, 255));
        jLabel44.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel44.setText("Kpg");

        label_total_kpg.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_kpg.setText("0");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel2.setText("Periode :");

        label_tgl1.setBackground(new java.awt.Color(255, 255, 255));
        label_tgl1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_tgl1.setText("dd-mm-yyyy");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel4.setText("-");

        label_tgl2.setBackground(new java.awt.Color(255, 255, 255));
        label_tgl2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_tgl2.setText("dd-mm-yyyy");

        tabel_pencabut.setAutoCreateRowSorter(true);
        tabel_pencabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_pencabut.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nama", "Grup", "Kpg"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class
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
        tabel_pencabut.setRowSelectionAllowed(false);
        tabel_pencabut.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(tabel_pencabut);

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel17.setText("Pekerja Cabut");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("Total Pekerja :");

        label_total_pekerja.setBackground(new java.awt.Color(255, 255, 255));
        label_total_pekerja.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_pekerja.setText("0");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_tgl1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_tgl2))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel21)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_gram_basah)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel22))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel23)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_gram_kering)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel24))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_totalLP)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_gram_utuh)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel13))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel8)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_gram_pch)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel18))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel19)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_gram_flat)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel20))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel10)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_gram_kaki)
                                        .addGap(5, 5, 5)
                                        .addComponent(jLabel11)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel31)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_rata2_rend_utuh)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel32))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel25)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_rata2_rend_flat)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel26))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel27)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_rata2_rend_sp)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel28))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel29)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_rata2_rend_sh)
                                        .addGap(5, 5, 5)
                                        .addComponent(jLabel30))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel43)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_kpg)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel44)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel39)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_tot_sesekan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel40))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel33)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_tot_hancuran)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel34))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel35)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_tot_rontokan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel36))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel37)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_tot_bonggol)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel38))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel41)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_tot_serabut)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel42))))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1068, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_pekerja))
                            .addComponent(jLabel17)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_grade_hasil))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_kpg_grading)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_grading)
                                .addGap(5, 5, 5)
                                .addComponent(jLabel14)))
                        .addGap(0, 139, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(1084, 1084, 1084)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGap(10, 10, 10))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(1084, 1084, 1084)
                .addComponent(jLabel16)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_tgl1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_tgl2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label_rata2_rend_utuh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label_total_gram_utuh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label_totalLP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(label_total_gram_basah, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(7, 7, 7)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(label_total_gram_kering, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(label_total_kpg, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(label_total_gram_pch, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(label_total_gram_flat, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(label_total_gram_kaki, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(label_rata2_rend_flat, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(label_rata2_rend_sp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(label_rata2_rend_sh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(104, 104, 104)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_total_pekerja, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_tot_sesekan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_tot_hancuran, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_tot_rontokan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_tot_bonggol, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_tot_serabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_grade_hasil, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_kpg_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_gram_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(26, 26, 26)))
                .addContainerGap())
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
            java.util.logging.Logger.getLogger(JFrame_LaporanProduksiBJ.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame_LaporanProduksiBJ LPBJ = new JFrame_LaporanProduksiBJ();
                LPBJ.setVisible(true);
                LPBJ.setExtendedState(MAXIMIZED_BOTH);
                LPBJ.init();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
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
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel label_rata2_rend_flat;
    private javax.swing.JLabel label_rata2_rend_sh;
    private javax.swing.JLabel label_rata2_rend_sp;
    private javax.swing.JLabel label_rata2_rend_utuh;
    private javax.swing.JLabel label_tgl1;
    private javax.swing.JLabel label_tgl2;
    private javax.swing.JLabel label_tot_bonggol;
    private javax.swing.JLabel label_tot_hancuran;
    private javax.swing.JLabel label_tot_rontokan;
    private javax.swing.JLabel label_tot_serabut;
    private javax.swing.JLabel label_tot_sesekan;
    private javax.swing.JLabel label_totalLP;
    private javax.swing.JLabel label_total_grade_hasil;
    private javax.swing.JLabel label_total_gram_basah;
    private javax.swing.JLabel label_total_gram_flat;
    private javax.swing.JLabel label_total_gram_grading;
    private javax.swing.JLabel label_total_gram_kaki;
    private javax.swing.JLabel label_total_gram_kering;
    private javax.swing.JLabel label_total_gram_pch;
    private javax.swing.JLabel label_total_gram_utuh;
    private javax.swing.JLabel label_total_kpg;
    private javax.swing.JLabel label_total_kpg_grading;
    private javax.swing.JLabel label_total_pekerja;
    private javax.swing.JTable tabel_LaporanProduksi;
    private javax.swing.JTable tabel_hasilGrading;
    private javax.swing.JTable tabel_pencabut;
    // End of variables declaration//GEN-END:variables
}
