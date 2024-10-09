package waleta_system.BahanBaku;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;

public class JDialog_total_cucian_hari_ini extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    java.util.Date date = new java.util.Date();//date berisikan tanggal hari ini
    DecimalFormat decimalFormat = new DecimalFormat();
    String myTextWaleta = "";
    String myTextSub = "";

    public JDialog_total_cucian_hari_ini(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        Load_Data();
    }

    public void Load_Data() {
        try {
            Utility.db.connect();
            String tanggal_cuci = new SimpleDateFormat("EEEEE, dd/MM/yyyy").format(date);
            String tanggal_cabut = new SimpleDateFormat("EEEEE, dd/MM/yyyy").format(Utility.addDaysSkippingFreeDays(date, 1));
            label_tgl_cuci.setText("Tgl Cuci : " + tanggal_cuci);
            label_tgl_cuci1.setText("Tgl Cuci : " + tanggal_cuci);
            label_tgl_cabut.setText("Tgl Cabut : " + tanggal_cabut);
            label_tgl_cabut1.setText("Tgl Cabut : " + tanggal_cabut);
            myTextWaleta = myTextWaleta + "*Total Cucian Waleta Hari ini*\n";
            myTextWaleta = myTextWaleta + label_tgl_cuci.getText() + "\n";
            myTextWaleta = myTextWaleta + label_tgl_cabut.getText() + "\n";
            myTextWaleta = myTextWaleta + "\n";
            myTextSub = myTextSub + "*Total Cucian Sub Hari ini*\n";
            myTextSub = myTextSub + label_tgl_cuci.getText() + "\n";
            myTextSub = myTextSub + label_tgl_cabut.getText() + "\n";
            myTextSub = myTextSub + "\n";
            int total_kpg_waleta = 0, total_gram_waleta = 0;
            int total_kpg_sub = 0, total_gram_sub = 0;
            DefaultTableModel model_waleta = (DefaultTableModel) tabel_cucian_waleta_hari_ini.getModel();
            model_waleta.setRowCount(0);
            DefaultTableModel model_sub = (DefaultTableModel) tabel_cucian_sub_hari_ini.getModel();
            model_sub.setRowCount(0);
            sql = "SELECT "
                    + "`ruangan`, "
                    + "SUM(`jumlah_keping`) AS 'kpg', "
                    + "SUM(`berat_basah`) AS 'gram'\n"
                    + "FROM `tb_laporan_produksi` "
                    + "WHERE `tanggal_lp` = CURRENT_DATE "
                    + "GROUP BY `ruangan`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                if (rs.getString("ruangan").length() == 5) {
                    model_sub.addRow(new Object[]{rs.getString("ruangan"), rs.getFloat("kpg"), rs.getFloat("gram")});
                    myTextSub = myTextSub + rs.getString("ruangan") + " : " + decimalFormat.format(rs.getFloat("kpg")) + " - " + decimalFormat.format(rs.getFloat("gram")) + "\n";
                    total_kpg_sub = total_kpg_sub + rs.getInt("kpg");
                    total_gram_sub = total_gram_sub + rs.getInt("gram");
                } else {
                    model_waleta.addRow(new Object[]{rs.getString("ruangan"), rs.getFloat("kpg"), rs.getFloat("gram")});
                    myTextWaleta = myTextWaleta + rs.getString("ruangan") + " : " + decimalFormat.format(rs.getFloat("kpg")) + " - " + decimalFormat.format(rs.getFloat("gram")) + "\n";
                    total_kpg_waleta = total_kpg_waleta + rs.getInt("kpg");
                    total_gram_waleta = total_gram_waleta + rs.getInt("gram");
                }
            }
            myTextWaleta = myTextWaleta + "\n";
            myTextSub = myTextSub + "\n";
            ColumnsAutoSizer.sizeColumnsToFit(tabel_cucian_waleta_hari_ini);
            ColumnsAutoSizer.sizeColumnsToFit(tabel_cucian_sub_hari_ini);
            decimalFormat.setGroupingUsed(true);
            decimalFormat.setMaximumFractionDigits(1);
            label_total_waleta.setText("WALETA : " + decimalFormat.format(total_kpg_waleta) + " - " + decimalFormat.format(total_gram_waleta));
            label_total_sub1.setText("SUB : " + decimalFormat.format(total_kpg_sub) + " - " + decimalFormat.format(total_gram_sub));
            label_total_waleta_sub.setText("WALETA + SUB : " + decimalFormat.format(total_kpg_waleta + total_kpg_sub) + " - " + decimalFormat.format(total_gram_waleta + total_gram_sub));
            label_total_waleta_sub1.setText("WALETA + SUB : " + decimalFormat.format(total_kpg_waleta + total_kpg_sub) + " - " + decimalFormat.format(total_gram_waleta + total_gram_sub));

            myTextWaleta = myTextWaleta + "*TOTAL (KPG - GRAM)*\n";
            myTextWaleta = myTextWaleta + label_total_waleta.getText() + "\n";
            myTextWaleta = myTextWaleta + label_total_waleta_sub.getText() + "\n";
            myTextWaleta = myTextWaleta + "\n";
            myTextSub = myTextSub + "*TOTAL (KPG - GRAM)*\n";
            myTextSub = myTextSub + label_total_sub1.getText() + "\n";
            myTextSub = myTextSub + label_total_waleta_sub1.getText() + "\n";
            myTextSub = myTextSub + "\n";

            //Berdasarkan jenis bulu upah
            float total_gram_br_wlt = 0, total_gram_bs_wlt = 0, total_gram_bb_wlt = 0, total_gram_br_bs_bb_wlt = 0;
            float total_gram_br_sub = 0, total_gram_bs_sub = 0, total_gram_bb_sub = 0, total_gram_br_bs_bb_sub = 0;
            sql = "SELECT `ruangan`, `jenis_bulu_lp`, SUM(`berat_basah`) AS 'gram'\n"
                    + "FROM `tb_laporan_produksi` "
                    + "WHERE `tanggal_lp` = CURRENT_DATE "
                    + "GROUP BY `ruangan`, `jenis_bulu_lp`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                if (rs.getString("ruangan").length() == 5) {
                    if (rs.getString("jenis_bulu_lp").contains("BR")) {
                        total_gram_br_sub = total_gram_br_sub + rs.getInt("gram");
                    } else if (rs.getString("jenis_bulu_lp").contains("BS")) {
                        total_gram_bs_sub = total_gram_bs_sub + rs.getInt("gram");
                    } else if (rs.getString("jenis_bulu_lp").contains("BB")) {
                        total_gram_bb_sub = total_gram_bb_sub + rs.getInt("gram");
                    }
                } else {
                    if (rs.getString("jenis_bulu_lp").contains("BR")) {
                        total_gram_br_wlt = total_gram_br_wlt + rs.getInt("gram");
                    } else if (rs.getString("jenis_bulu_lp").contains("BS")) {
                        total_gram_bs_wlt = total_gram_bs_wlt + rs.getInt("gram");
                    } else if (rs.getString("jenis_bulu_lp").contains("BB")) {
                        total_gram_bb_wlt = total_gram_bb_wlt + rs.getInt("gram");
                    }
                }
            }
            total_gram_br_bs_bb_wlt = total_gram_br_wlt + total_gram_bs_wlt + total_gram_bb_wlt;
            total_gram_br_bs_bb_sub = total_gram_br_sub + total_gram_bs_sub + total_gram_bb_sub;
            float persen_br_wlt = (total_gram_br_wlt / total_gram_br_bs_bb_wlt) * 100f;
            float persen_bs_wlt = (total_gram_bs_wlt / total_gram_br_bs_bb_wlt) * 100f;
            float persen_bb_wlt = (total_gram_bb_wlt / total_gram_br_bs_bb_wlt) * 100f;
            float persen_br_sub = (total_gram_br_sub / total_gram_br_bs_bb_sub) * 100f;
            float persen_bs_sub = (total_gram_bs_sub / total_gram_br_bs_bb_sub) * 100f;
            float persen_bb_sub = (total_gram_bb_sub / total_gram_br_bs_bb_sub) * 100f;
            label_total_br.setText("BR : " + decimalFormat.format(total_gram_br_wlt) + " (" + decimalFormat.format(persen_br_wlt) + "%)");
            label_total_bs.setText("BS : " + decimalFormat.format(total_gram_bs_wlt) + " (" + decimalFormat.format(persen_bs_wlt) + "%)");
            label_total_bb.setText("BB : " + decimalFormat.format(total_gram_bb_wlt) + " (" + decimalFormat.format(persen_bb_wlt) + "%)");
            label_total_br1.setText("BR : " + decimalFormat.format(total_gram_br_sub) + " (" + decimalFormat.format(persen_br_sub) + "%)");
            label_total_bs1.setText("BS : " + decimalFormat.format(total_gram_bs_sub) + " (" + decimalFormat.format(persen_bs_sub) + "%)");
            label_total_bb1.setText("BB : " + decimalFormat.format(total_gram_bb_sub) + " (" + decimalFormat.format(persen_bb_sub) + "%)");
            myTextWaleta = myTextWaleta + "*Berdasarkan Jenis Bulu Upah (Gram)*\n";
            myTextWaleta = myTextWaleta + label_total_br.getText() + "\n";
            myTextWaleta = myTextWaleta + label_total_bs.getText() + "\n";
            myTextWaleta = myTextWaleta + label_total_bb.getText() + "\n";
            myTextWaleta = myTextWaleta + "\n";
            myTextSub = myTextSub + "*Berdasarkan Jenis Bulu Upah (Gram)*\n";
            myTextSub = myTextSub + label_total_br.getText() + "\n";
            myTextSub = myTextSub + label_total_bs.getText() + "\n";
            myTextSub = myTextSub + label_total_bb.getText() + "\n";
            myTextSub = myTextSub + "\n";

            //Berdasarkan tujuan
            float total_gram_utuh_wlt = 0, total_gram_flat_wlt = 0, total_gram_jidun_wlt = 0, total_gram_lain2_wlt = 0, total_gram_kategori_wlt = 0;
            float total_gram_utuh_sub = 0, total_gram_flat_sub = 0, total_gram_jidun_sub = 0, total_gram_lain2_sub = 0, total_gram_kategori_sub = 0;
            sql = "SELECT `ruangan`, `tb_grade_bahan_baku`.`kategori`, SUM(`berat_basah`) AS 'gram'\n"
                    + "FROM `tb_laporan_produksi` \n"
                    + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`\n"
                    + "WHERE `tanggal_lp` = CURRENT_DATE \n"
                    + "GROUP BY `ruangan`, `tb_grade_bahan_baku`.`kategori`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                if (rs.getString("ruangan").length() != 5) {
                    if (rs.getString("kategori").contains("MK A") 
                            || rs.getString("kategori").contains("MK B") 
                            || rs.getString("kategori").contains("OVL") 
                            || rs.getString("kategori").contains("SGTG")) {
                        total_gram_utuh_wlt = total_gram_utuh_wlt + rs.getInt("gram");
                    } else if (rs.getString("kategori").contains("FLAT")) {
                        total_gram_flat_wlt = total_gram_flat_wlt + rs.getInt("gram");
                    } else if (rs.getString("kategori").contains("JDN")) {
                        total_gram_jidun_wlt = total_gram_jidun_wlt + rs.getInt("gram");
                    } else {
                        total_gram_lain2_wlt = total_gram_lain2_wlt + rs.getInt("gram");
                    }
                } else {
                    if (rs.getString("kategori").contains("MK A") 
                            || rs.getString("kategori").contains("MK B") 
                            || rs.getString("kategori").contains("OVL") 
                            || rs.getString("kategori").contains("SGTG")) {
                        total_gram_utuh_sub = total_gram_utuh_sub + rs.getInt("gram");
                    } else if (rs.getString("kategori").contains("FLAT")) {
                        total_gram_flat_sub = total_gram_flat_sub + rs.getInt("gram");
                    } else if (rs.getString("kategori").contains("JDN")) {
                        total_gram_jidun_sub = total_gram_jidun_sub + rs.getInt("gram");
                    } else {
                        total_gram_lain2_sub = total_gram_lain2_sub + rs.getInt("gram");
                    }
                }
            }
            total_gram_kategori_wlt = total_gram_utuh_wlt + total_gram_flat_wlt + total_gram_jidun_wlt + total_gram_lain2_wlt;
            total_gram_kategori_sub = total_gram_utuh_sub + total_gram_flat_sub + total_gram_jidun_sub + total_gram_lain2_sub;
            float persen_utuh_wlt = (total_gram_utuh_wlt / total_gram_kategori_wlt) * 100f;
            float persen_flat_wlt = (total_gram_flat_wlt / total_gram_kategori_wlt) * 100f;
            float persen_jidun_wlt = (total_gram_jidun_wlt / total_gram_kategori_wlt) * 100f;
            float persen_lain_wlt = (total_gram_lain2_wlt / total_gram_kategori_wlt) * 100f;
            float persen_utuh_sub = (total_gram_utuh_sub / total_gram_kategori_sub) * 100f;
            float persen_flat_sub = (total_gram_flat_sub / total_gram_kategori_sub) * 100f;
            float persen_jidun_sub = (total_gram_jidun_sub / total_gram_kategori_sub) * 100f;
            float persen_lain_sub = (total_gram_lain2_sub / total_gram_kategori_sub) * 100f;
            
            label_persen_utuh.setText("UTUH : " + decimalFormat.format(persen_utuh_wlt) + "% (MK, OVL, SGTG)");
            label_persen_flat.setText("FLAT : " + decimalFormat.format(persen_flat_wlt) + "% (FLAT)");
            label_persen_jdn.setText("JDN : " + decimalFormat.format(persen_jidun_wlt) + "% (JDN)");
            label_persen_lain2.setText("LAIN2 : " + decimalFormat.format(persen_lain_wlt) + "%");
            label_persen_utuh1.setText("UTUH : " + decimalFormat.format(persen_utuh_sub) + "% (MK, OVL, SGTG)");
            label_persen_flat1.setText("FLAT : " + decimalFormat.format(persen_flat_sub) + "% (FLAT)");
            label_persen_jdn1.setText("JDN : " + decimalFormat.format(persen_jidun_sub) + "% (JDN)");
            label_persen_lain21.setText("LAIN2 : " + decimalFormat.format(persen_lain_sub) + "%");
            
            myTextWaleta = myTextWaleta + "*Berdasarkan Tujuan (Gram) Kategori grade baku*\n";
            myTextWaleta = myTextWaleta + label_persen_utuh.getText() + "\n";
            myTextWaleta = myTextWaleta + label_persen_flat.getText() + "\n";
            myTextWaleta = myTextWaleta + label_persen_jdn.getText() + "\n";
            myTextWaleta = myTextWaleta + label_persen_lain2.getText() + "\n";
            myTextWaleta = myTextWaleta + "\n";
            myTextSub = myTextSub + "*Berdasarkan Tujuan (Gram) Kategori grade baku*\n";
            myTextSub = myTextSub + label_persen_utuh.getText() + "\n";
            myTextSub = myTextSub + label_persen_flat.getText() + "\n";
            myTextSub = myTextSub + label_persen_jdn.getText() + "\n";
            myTextSub = myTextSub + label_persen_lain2.getText() + "\n";
            myTextSub = myTextSub + "\n";

            //Berdasarkan MEMO JD
            float total_jdn_waleta = 0, total_jdn_sub = 0;
            sql = "SELECT "
                    + "`ruangan`, `berat_basah` "
                    + "FROM `tb_laporan_produksi` "
                    + "WHERE "
                    + "`tanggal_lp` = CURRENT_DATE "
                    + "AND `memo_lp` LIKE '%JD%'";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                if (rs.getString("ruangan").length() == 5) {
                    total_jdn_sub = total_jdn_sub + rs.getInt("berat_basah");
                } else {
                    total_jdn_waleta = total_jdn_waleta + rs.getInt("berat_basah");
                }
            }
            label_total_jdn_waleta.setText("WALETA : " + decimalFormat.format(total_jdn_waleta) + "");
            label_total_jdn_sub1.setText("SUB : " + decimalFormat.format(total_jdn_sub) + "");
            
            myTextWaleta = myTextWaleta + "*Total Bahan Jidun (Gram) memo LP mengandung JD*\n";
            myTextWaleta = myTextWaleta + label_total_jdn_waleta.getText() + "\n";
            myTextSub = myTextSub + "*Total Bahan Jidun (Gram) memo LP mengandung JD*\n";
            myTextSub = myTextSub + label_total_jdn_sub1.getText() + "\n";
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JDialog_total_cucian_hari_ini.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        label_tgl_cuci = new javax.swing.JLabel();
        label_tgl_cabut = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_cucian_waleta_hari_ini = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        label_total_waleta = new javax.swing.JLabel();
        label_total_waleta_sub = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        label_total_br = new javax.swing.JLabel();
        label_total_bs = new javax.swing.JLabel();
        label_total_bb = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        label_persen_utuh = new javax.swing.JLabel();
        label_persen_flat = new javax.swing.JLabel();
        label_persen_jdn = new javax.swing.JLabel();
        label_persen_lain2 = new javax.swing.JLabel();
        label_total_jdn_waleta = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        button_copy_text = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        label_tgl_cuci1 = new javax.swing.JLabel();
        label_tgl_cabut1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabel_cucian_sub_hari_ini = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        label_total_sub1 = new javax.swing.JLabel();
        label_total_waleta_sub1 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        label_total_br1 = new javax.swing.JLabel();
        label_total_bs1 = new javax.swing.JLabel();
        label_total_bb1 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        label_persen_utuh1 = new javax.swing.JLabel();
        label_persen_flat1 = new javax.swing.JLabel();
        label_persen_jdn1 = new javax.swing.JLabel();
        label_persen_lain21 = new javax.swing.JLabel();
        label_total_jdn_sub1 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        button_copy_text1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel1.setText("Total Cucian WALETA Hari ini");

        label_tgl_cuci.setBackground(new java.awt.Color(255, 255, 255));
        label_tgl_cuci.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_tgl_cuci.setText("Tgl Cuci : Hari, dd/mm/yyyy (tgl lp)");

        label_tgl_cabut.setBackground(new java.awt.Color(255, 255, 255));
        label_tgl_cabut.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_tgl_cabut.setText("Tgl Cabut :  Hari, dd/mm/yyyy");

        tabel_cucian_waleta_hari_ini.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        tabel_cucian_waleta_hari_ini.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Ruang", "KPG", "GRAM"
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
        jScrollPane1.setViewportView(tabel_cucian_waleta_hari_ini);

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel4.setText("TOTAL (KPG - GRAM)");

        label_total_waleta.setBackground(new java.awt.Color(255, 255, 255));
        label_total_waleta.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_waleta.setText("WALETA : KPG - GRAM");

        label_total_waleta_sub.setBackground(new java.awt.Color(255, 255, 255));
        label_total_waleta_sub.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_waleta_sub.setText("WALETA + SUB = KPG - GRAM");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel8.setText("Berdasarkan Jenis Bulu Upah (Gram)");

        label_total_br.setBackground(new java.awt.Color(255, 255, 255));
        label_total_br.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_br.setText("BR gram (%)");

        label_total_bs.setBackground(new java.awt.Color(255, 255, 255));
        label_total_bs.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_bs.setText("BS gram (%)");

        label_total_bb.setBackground(new java.awt.Color(255, 255, 255));
        label_total_bb.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_bb.setText("BB gram (%)");

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel12.setText("Berdasarkan Tujuan (Gram) Kategori grade baku");

        label_persen_utuh.setBackground(new java.awt.Color(255, 255, 255));
        label_persen_utuh.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_persen_utuh.setText("Utuh : % (MK, OVL, SGTG)");

        label_persen_flat.setBackground(new java.awt.Color(255, 255, 255));
        label_persen_flat.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_persen_flat.setText("Flat : %");

        label_persen_jdn.setBackground(new java.awt.Color(255, 255, 255));
        label_persen_jdn.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_persen_jdn.setText("JDN : %");

        label_persen_lain2.setBackground(new java.awt.Color(255, 255, 255));
        label_persen_lain2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_persen_lain2.setText("Lain : %");

        label_total_jdn_waleta.setBackground(new java.awt.Color(255, 255, 255));
        label_total_jdn_waleta.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_jdn_waleta.setText("WALETA : gram");

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel19.setText("Total Bahan Jidun (Gram) memo LP mengandung JD");

        button_copy_text.setBackground(new java.awt.Color(255, 255, 255));
        button_copy_text.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_copy_text.setText("Copy text");
        button_copy_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_copy_textActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_copy_text))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(label_total_waleta)
                                    .addComponent(label_total_waleta_sub)
                                    .addComponent(jLabel8)
                                    .addComponent(label_total_br)
                                    .addComponent(label_total_bs)
                                    .addComponent(label_total_bb)
                                    .addComponent(jLabel12)
                                    .addComponent(label_persen_utuh)
                                    .addComponent(label_persen_flat)
                                    .addComponent(label_persen_jdn)
                                    .addComponent(label_persen_lain2)
                                    .addComponent(jLabel19)
                                    .addComponent(label_total_jdn_waleta)))
                            .addComponent(label_tgl_cuci)
                            .addComponent(label_tgl_cabut))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(button_copy_text))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_tgl_cuci)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_tgl_cabut)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_waleta)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_waleta_sub)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_br)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_bs)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_bb)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_persen_utuh)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_persen_flat)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_persen_jdn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_persen_lain2)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_jdn_waleta))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel2.setText("Total Cucian SUB Hari ini");

        label_tgl_cuci1.setBackground(new java.awt.Color(255, 255, 255));
        label_tgl_cuci1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_tgl_cuci1.setText("Tgl Cuci : Hari, dd/mm/yyyy (tgl lp)");

        label_tgl_cabut1.setBackground(new java.awt.Color(255, 255, 255));
        label_tgl_cabut1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_tgl_cabut1.setText("Tgl Cabut :  Hari, dd/mm/yyyy");

        tabel_cucian_sub_hari_ini.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        tabel_cucian_sub_hari_ini.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Ruang", "KPG", "GRAM"
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
        jScrollPane2.setViewportView(tabel_cucian_sub_hari_ini);

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel5.setText("TOTAL (KPG - GRAM)");

        label_total_sub1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_sub1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_sub1.setText("SUB : KPG - GRAM");

        label_total_waleta_sub1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_waleta_sub1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_waleta_sub1.setText("WALETA + SUB = KPG - GRAM");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel9.setText("Berdasarkan Jenis Bulu Upah (Gram)");

        label_total_br1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_br1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_br1.setText("BR gram (%)");

        label_total_bs1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_bs1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_bs1.setText("BS gram (%)");

        label_total_bb1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_bb1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_bb1.setText("BB gram (%)");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel13.setText("Berdasarkan Tujuan (Gram) Kategori grade baku");

        label_persen_utuh1.setBackground(new java.awt.Color(255, 255, 255));
        label_persen_utuh1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_persen_utuh1.setText("Utuh : % (MK, OVL, SGTG)");

        label_persen_flat1.setBackground(new java.awt.Color(255, 255, 255));
        label_persen_flat1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_persen_flat1.setText("Flat : %");

        label_persen_jdn1.setBackground(new java.awt.Color(255, 255, 255));
        label_persen_jdn1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_persen_jdn1.setText("JDN : %");

        label_persen_lain21.setBackground(new java.awt.Color(255, 255, 255));
        label_persen_lain21.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_persen_lain21.setText("Lain : %");

        label_total_jdn_sub1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_jdn_sub1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_jdn_sub1.setText("SUB : gram");

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel20.setText("Total Bahan Jidun (Gram) memo LP mengandung JD");

        button_copy_text1.setBackground(new java.awt.Color(255, 255, 255));
        button_copy_text1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_copy_text1.setText("Copy text");
        button_copy_text1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_copy_text1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_copy_text1))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(label_total_sub1)
                                    .addComponent(label_total_waleta_sub1)
                                    .addComponent(jLabel9)
                                    .addComponent(label_total_br1)
                                    .addComponent(label_total_bs1)
                                    .addComponent(label_total_bb1)
                                    .addComponent(jLabel13)
                                    .addComponent(label_persen_utuh1)
                                    .addComponent(label_persen_flat1)
                                    .addComponent(label_persen_jdn1)
                                    .addComponent(label_persen_lain21)
                                    .addComponent(jLabel20)
                                    .addComponent(label_total_jdn_sub1)))
                            .addComponent(label_tgl_cuci1)
                            .addComponent(label_tgl_cabut1))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(button_copy_text1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_tgl_cuci1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_tgl_cabut1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_sub1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_waleta_sub1)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_br1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_bs1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_bb1)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_persen_utuh1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_persen_flat1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_persen_jdn1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_persen_lain21)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_jdn_sub1))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void button_copy_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_copy_textActionPerformed
        // TODO add your handling code here:
        StringSelection stringSelection = new StringSelection(myTextWaleta);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
        JOptionPane.showMessageDialog(this, "Text copied to clipboard!");
    }//GEN-LAST:event_button_copy_textActionPerformed

    private void button_copy_text1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_copy_text1ActionPerformed
        // TODO add your handling code here:
        StringSelection stringSelection = new StringSelection(myTextSub);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
        JOptionPane.showMessageDialog(this, "Text copied to clipboard!");
    }//GEN-LAST:event_button_copy_text1ActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JDialog_total_cucian_hari_ini.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JDialog_total_cucian_hari_ini.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JDialog_total_cucian_hari_ini.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JDialog_total_cucian_hari_ini.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JDialog_total_cucian_hari_ini dialog = new JDialog_total_cucian_hari_ini(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton button_copy_text;
    private javax.swing.JButton button_copy_text1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel label_persen_flat;
    private javax.swing.JLabel label_persen_flat1;
    private javax.swing.JLabel label_persen_jdn;
    private javax.swing.JLabel label_persen_jdn1;
    private javax.swing.JLabel label_persen_lain2;
    private javax.swing.JLabel label_persen_lain21;
    private javax.swing.JLabel label_persen_utuh;
    private javax.swing.JLabel label_persen_utuh1;
    private javax.swing.JLabel label_tgl_cabut;
    private javax.swing.JLabel label_tgl_cabut1;
    private javax.swing.JLabel label_tgl_cuci;
    private javax.swing.JLabel label_tgl_cuci1;
    private javax.swing.JLabel label_total_bb;
    private javax.swing.JLabel label_total_bb1;
    private javax.swing.JLabel label_total_br;
    private javax.swing.JLabel label_total_br1;
    private javax.swing.JLabel label_total_bs;
    private javax.swing.JLabel label_total_bs1;
    private javax.swing.JLabel label_total_jdn_sub1;
    private javax.swing.JLabel label_total_jdn_waleta;
    private javax.swing.JLabel label_total_sub1;
    private javax.swing.JLabel label_total_waleta;
    private javax.swing.JLabel label_total_waleta_sub;
    private javax.swing.JLabel label_total_waleta_sub1;
    private javax.swing.JTable tabel_cucian_sub_hari_ini;
    private javax.swing.JTable tabel_cucian_waleta_hari_ini;
    // End of variables declaration//GEN-END:variables
}
