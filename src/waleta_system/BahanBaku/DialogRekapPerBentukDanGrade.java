package waleta_system.BahanBaku;

import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import waleta_system.Class.Utility;


public class DialogRekapPerBentukDanGrade extends javax.swing.JDialog {

     
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String sql = null;
    ResultSet rs;
    Date today = new Date();
    String no_lp;
    Date Date_LP_1;
    Date Date_LP_2;
    String grade;
    String memo;
    String ruang;
    DecimalFormat decimalFormat = new DecimalFormat();

    public DialogRekapPerBentukDanGrade(java.awt.Frame parent, boolean modal, String no_lp, Date date1, Date date2, String grade, String memo, String ruang) {
        super(parent, modal);
        initComponents();
        this.setResizable(false);
        this.no_lp = no_lp;
        this.Date_LP_1 = date1;
        this.Date_LP_2 = date2;
        this.grade = grade;
        this.memo = memo;
        this.ruang = ruang;
        refreshData();
    }

    public final void refreshData() {
        try {
            
            decimalFormat.setMaximumFractionDigits(2);
            if ("All".equals(ruang)) {
                ruang = "";
            }
            if ("All".equals(grade)) {
                grade = "";
            }

            if (Date_LP_1 != null && Date_LP_2 != null) {
                sql = "SELECT `jenis_bentuk`, `jenis_bulu`, `berat_basah`, `jumlah_keping`, `jumlah_sobek`, `pecah_1_lp` "
                        + "FROM `tb_laporan_produksi` "
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`"
                        + "WHERE `no_laporan_produksi` LIKE '%" + no_lp + "%' AND `tb_laporan_produksi`.`kode_grade` LIKE '%" + grade + "%' AND `memo_lp` LIKE '%" + memo + "%' AND `ruangan` LIKE '%" + ruang + "%' AND `tanggal_lp` BETWEEN '" + dateFormat.format(Date_LP_1) + "' and '" + dateFormat.format(Date_LP_2) + "'ORDER BY `no_laporan_produksi` DESC";
            } else {
                sql = "SELECT `jenis_bentuk`, `jenis_bulu`, `berat_basah`, `jumlah_keping`, `jumlah_sobek`, `pecah_1_lp` "
                        + "FROM `tb_laporan_produksi` "
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`"
                        + "WHERE `no_laporan_produksi` LIKE '%" + no_lp + "%' AND `tb_laporan_produksi`.`kode_grade` LIKE '%" + grade + "%'  AND `memo_lp` LIKE '%" + memo + "%' AND `ruangan` LIKE '%" + ruang + "%' ORDER BY `no_laporan_produksi` DESC";
            }
            rs = Utility.db.getStatement().executeQuery(sql);
            float totalBerat = 0;
            float totalAllBR = 0, totalAllBS = 0, totalAllBB = 0;
            float totalAllBR1 = 0, totalAllBS1 = 0, totalAllBB1 = 0;
            float totalMangkokBR = 0, totalMangkokBS = 0, totalMangkokBB = 0;
            float totalMangkokBR1 = 0, totalMangkokBS1 = 0, totalMangkokBB1 = 0;
            float totalSegitigaBR = 0, totalSegitigaBS = 0, totalSegitigaBB = 0;
            float totalSegitigaBR1 = 0, totalSegitigaBS1 = 0, totalSegitigaBB1 = 0;
            float totalOvalBR = 0, totalOvalBS = 0, totalOvalBB = 0;
            float totalOvalBR1 = 0, totalOvalBS1 = 0, totalOvalBB1 = 0;
            float totalPecah = 0, totalStrip = 0;
            float totalPecah1 = 0, totalStrip1 = 0;
            while (rs.next()) {
                String jenis_bentuk = rs.getString("jenis_bentuk");
                String jenis_bulu = rs.getString("jenis_bulu");
                int berat_basah = rs.getInt("berat_basah");
                totalBerat = totalBerat + rs.getInt("berat_basah");
                int jumlah_keping = rs.getInt("jumlah_keping");
                if (jenis_bentuk.contains("Oval") || jenis_bentuk.contains("Segitiga") || jenis_bentuk.contains("Mangkok")) {
                    if (jenis_bulu.contains("Ringan")) {
                        totalAllBR += jumlah_keping;
                        totalAllBR1 += berat_basah;
                        if (jenis_bentuk.contains("Mangkok")) {//Mangkok
                            totalMangkokBR += jumlah_keping;
                            totalMangkokBR1 += berat_basah;
                        } else if (jenis_bentuk.contains("Segitiga")) {//Mangkok
                            totalSegitigaBR += jumlah_keping;
                            totalSegitigaBR1 += berat_basah;
                        } else if (jenis_bentuk.contains("Oval")) {//Mangkok
                            totalOvalBR += jumlah_keping;
                            totalOvalBR1 += berat_basah;
                        }
                    } else if (jenis_bulu.contains("Sedang")) {
                        totalAllBS += jumlah_keping;
                        totalAllBS1 += berat_basah;
                        if (jenis_bentuk.contains("Mangkok")) {//Mangkok
                            totalMangkokBS += jumlah_keping;
                            totalMangkokBS1 += berat_basah;
                        } else if (jenis_bentuk.contains("Segitiga")) {//Mangkok
                            totalSegitigaBS += jumlah_keping;
                            totalSegitigaBS1 += berat_basah;
                        } else if (jenis_bentuk.contains("Oval")) {//Mangkok
                            totalOvalBS += jumlah_keping;
                            totalOvalBS1 += berat_basah;
                        }
                    } else if (jenis_bulu.contains("Berat")) {
                        totalAllBB += jumlah_keping;
                        totalAllBB1 += berat_basah;
                        if (jenis_bentuk.contains("Mangkok")) {//Mangkok
                            totalMangkokBB += jumlah_keping;
                            totalMangkokBB1 += berat_basah;
                        } else if (jenis_bentuk.contains("Segitiga")) {//Mangkok
                            totalSegitigaBB += jumlah_keping;
                            totalSegitigaBB1 += berat_basah;
                        } else if (jenis_bentuk.contains("Oval")) {//Mangkok
                            totalOvalBB += jumlah_keping;
                            totalOvalBB1 += berat_basah;
                        }
                    }
                } else if (jenis_bentuk.contains("Pecah") || jenis_bentuk.contains("Lubang")) {
                    totalPecah += jumlah_keping;
                    totalPecah1 += berat_basah;
                } else {
                    totalStrip += jumlah_keping;
                    totalStrip1 += berat_basah;
                }
            }
            //all
            float persentaseBR = (totalAllBR1 / totalBerat) * 100;
            txtAllBR_persen.setText(decimalFormat.format(persentaseBR));
            txtAllBR.setText(decimalFormat.format(totalAllBR));
            txtAllBR1.setText(decimalFormat.format(totalAllBR1));
            float persentaseBS = (totalAllBS1 / totalBerat) * 100;
            txtAllBS_persen.setText(decimalFormat.format(persentaseBS));
            txtAllBS.setText(decimalFormat.format(totalAllBS));
            txtAllBS1.setText(decimalFormat.format(totalAllBS1));
            float persentaseBB = (totalAllBB1 / totalBerat) * 100;
            txtAllBB_persen.setText(decimalFormat.format(persentaseBB));
            txtAllBB.setText(decimalFormat.format(totalAllBB));
            txtAllBB1.setText(decimalFormat.format(totalAllBB1));
            //mangkok
            txtMangkokBR.setText(decimalFormat.format(totalMangkokBR));
            txtMangkokBR1.setText(decimalFormat.format(totalMangkokBR1));
            txtMangkokBS.setText(decimalFormat.format(totalMangkokBS));
            txtMangkokBS1.setText(decimalFormat.format(totalMangkokBS1));
            txtMangkokBB.setText(decimalFormat.format(totalMangkokBB));
            txtMangkokBB1.setText(decimalFormat.format(totalMangkokBB1));
            txtMangkokTotKpg.setText(decimalFormat.format(totalMangkokBR+totalMangkokBS+totalMangkokBB));
            txtMangkokTotGram.setText(decimalFormat.format(totalMangkokBR1+totalMangkokBS1+totalMangkokBB1));
            //segitiga
            txtSegitigaBR.setText(decimalFormat.format(totalSegitigaBR));
            txtSegitigaBR1.setText(decimalFormat.format(totalSegitigaBR1));
            txtSegitigaBS.setText(decimalFormat.format(totalSegitigaBS));
            txtSegitigaBS1.setText(decimalFormat.format(totalSegitigaBS1));
            txtSegitigaBB.setText(decimalFormat.format(totalSegitigaBB));
            txtSegitigaBB1.setText(decimalFormat.format(totalSegitigaBB1));
            txtSegitigaTotKpg.setText(decimalFormat.format(totalSegitigaBR+totalSegitigaBS+totalSegitigaBB));
            txtSegitigaTotGram.setText(decimalFormat.format(totalSegitigaBR1+totalSegitigaBS1+totalSegitigaBB1));
            //oval
            txtOvalBR.setText(decimalFormat.format(totalOvalBR));
            txtOvalBR1.setText(decimalFormat.format(totalOvalBR1));
            txtOvalBS.setText(decimalFormat.format(totalOvalBS));
            txtOvalBS1.setText(decimalFormat.format(totalOvalBS1));
            txtOvalBB.setText(decimalFormat.format(totalOvalBB));
            txtOvalBB1.setText(decimalFormat.format(totalOvalBB1));
            txtOvalTotKpg.setText(decimalFormat.format(totalOvalBR+totalOvalBS+totalOvalBB));
            txtOvalTotGram.setText(decimalFormat.format(totalOvalBR1+totalOvalBS1+totalOvalBB1));
            //pecah lubang
            txtPecahLubang.setText(decimalFormat.format(totalPecah));
            txtPecahLubang1.setText(decimalFormat.format(totalPecah1));
            //others
            txtStrip.setText(decimalFormat.format(totalStrip));
            txtStrip1.setText(decimalFormat.format(totalStrip1));
        } catch (Exception ex) {
            Logger.getLogger(DialogRekapPerBentukDanGrade.class.getName()).log(Level.SEVERE, null, ex);
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
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtAllBS = new javax.swing.JLabel();
        txtAllBB = new javax.swing.JLabel();
        txtAllBR = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtAllBR1 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        txtAllBS1 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        txtAllBB1 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        txtAllBR_persen = new javax.swing.JLabel();
        txtAllBS_persen = new javax.swing.JLabel();
        txtAllBB_persen = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtMangkokBS = new javax.swing.JLabel();
        txtMangkokBB = new javax.swing.JLabel();
        txtMangkokBR = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        txtMangkokBR1 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        txtMangkokBS1 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        txtMangkokBB1 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        txtMangkokTotKpg = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        txtMangkokTotGram = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        txtSegitigaBS = new javax.swing.JLabel();
        txtSegitigaBB = new javax.swing.JLabel();
        txtSegitigaBR = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        txtSegitigaBR1 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        txtSegitigaBS1 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        txtSegitigaBB1 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        txtSegitigaTotKpg = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        txtSegitigaTotGram = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        txtOvalBS = new javax.swing.JLabel();
        txtOvalBB = new javax.swing.JLabel();
        txtOvalBR = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        txtOvalBR1 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        txtOvalBS1 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        txtOvalBB1 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        txtOvalTotKpg = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        txtOvalTotGram = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        txtPecahLubang = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        txtPecahLubang1 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        txtStrip = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        txtStrip1 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Rekap per Bentuk & Grade");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "All"));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText("BR / BRS");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("BS");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("BB / BB2");

        txtAllBS.setBackground(new java.awt.Color(255, 255, 255));
        txtAllBS.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtAllBS.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        txtAllBS.setText("-");

        txtAllBB.setBackground(new java.awt.Color(255, 255, 255));
        txtAllBB.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtAllBB.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        txtAllBB.setText("-");

        txtAllBR.setBackground(new java.awt.Color(255, 255, 255));
        txtAllBR.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtAllBR.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        txtAllBR.setText("-");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Keping");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText("Keping");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("Keping");

        txtAllBR1.setBackground(new java.awt.Color(255, 255, 255));
        txtAllBR1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtAllBR1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        txtAllBR1.setText("-");

        jLabel34.setBackground(new java.awt.Color(255, 255, 255));
        jLabel34.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel34.setText("Gram");

        txtAllBS1.setBackground(new java.awt.Color(255, 255, 255));
        txtAllBS1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtAllBS1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        txtAllBS1.setText("-");

        jLabel35.setBackground(new java.awt.Color(255, 255, 255));
        jLabel35.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel35.setText("Gram");

        txtAllBB1.setBackground(new java.awt.Color(255, 255, 255));
        txtAllBB1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtAllBB1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        txtAllBB1.setText("-");

        jLabel36.setBackground(new java.awt.Color(255, 255, 255));
        jLabel36.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel36.setText("Gram");

        txtAllBR_persen.setBackground(new java.awt.Color(255, 255, 255));
        txtAllBR_persen.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtAllBR_persen.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        txtAllBR_persen.setText("XXX");

        txtAllBS_persen.setBackground(new java.awt.Color(255, 255, 255));
        txtAllBS_persen.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtAllBS_persen.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        txtAllBS_persen.setText("XXX");

        txtAllBB_persen.setBackground(new java.awt.Color(255, 255, 255));
        txtAllBB_persen.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtAllBB_persen.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        txtAllBB_persen.setText("XXX");

        jLabel47.setBackground(new java.awt.Color(255, 255, 255));
        jLabel47.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel47.setText("%");

        jLabel48.setBackground(new java.awt.Color(255, 255, 255));
        jLabel48.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel48.setText("%");

        jLabel49.setBackground(new java.awt.Color(255, 255, 255));
        jLabel49.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel49.setText("%");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtAllBR_persen, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtAllBB_persen, javax.swing.GroupLayout.Alignment.LEADING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel49)
                            .addComponent(jLabel47)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtAllBS_persen)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel48)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtAllBR, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtAllBS, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtAllBB, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtAllBR1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtAllBS1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtAllBB1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel35)
                    .addComponent(jLabel34)
                    .addComponent(jLabel36))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtAllBR_persen)
                            .addComponent(jLabel49))
                        .addGap(27, 27, 27)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtAllBB_persen)
                            .addComponent(jLabel47)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtAllBR1)
                            .addComponent(jLabel34))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtAllBS1)
                            .addComponent(jLabel35))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtAllBB1)
                            .addComponent(jLabel36)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtAllBR)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtAllBS_persen)
                                .addComponent(jLabel48))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel2)
                                .addComponent(txtAllBS)
                                .addComponent(jLabel9)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtAllBB)
                            .addComponent(jLabel8))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Mangkok"));

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel10.setText("BR / BRS");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel11.setText("BS");

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel12.setText("BB / BB2");

        txtMangkokBS.setBackground(new java.awt.Color(255, 255, 255));
        txtMangkokBS.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtMangkokBS.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        txtMangkokBS.setText("-");

        txtMangkokBB.setBackground(new java.awt.Color(255, 255, 255));
        txtMangkokBB.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtMangkokBB.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        txtMangkokBB.setText("-");

        txtMangkokBR.setBackground(new java.awt.Color(255, 255, 255));
        txtMangkokBR.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtMangkokBR.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        txtMangkokBR.setText("-");

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel16.setText("Keping");

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel17.setText("Keping");

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel18.setText("Keping");

        txtMangkokBR1.setBackground(new java.awt.Color(255, 255, 255));
        txtMangkokBR1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtMangkokBR1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        txtMangkokBR1.setText("-");

        jLabel37.setBackground(new java.awt.Color(255, 255, 255));
        jLabel37.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel37.setText("Gram");

        txtMangkokBS1.setBackground(new java.awt.Color(255, 255, 255));
        txtMangkokBS1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtMangkokBS1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        txtMangkokBS1.setText("-");

        jLabel38.setBackground(new java.awt.Color(255, 255, 255));
        jLabel38.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel38.setText("Gram");

        txtMangkokBB1.setBackground(new java.awt.Color(255, 255, 255));
        txtMangkokBB1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtMangkokBB1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        txtMangkokBB1.setText("-");

        jLabel39.setBackground(new java.awt.Color(255, 255, 255));
        jLabel39.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel39.setText("Gram");

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel28.setText("TOTAL");

        txtMangkokTotKpg.setBackground(new java.awt.Color(255, 255, 255));
        txtMangkokTotKpg.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtMangkokTotKpg.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        txtMangkokTotKpg.setText("-");

        jLabel29.setBackground(new java.awt.Color(255, 255, 255));
        jLabel29.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel29.setText("Keping");

        txtMangkokTotGram.setBackground(new java.awt.Color(255, 255, 255));
        txtMangkokTotGram.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtMangkokTotGram.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        txtMangkokTotGram.setText("-");

        jLabel50.setBackground(new java.awt.Color(255, 255, 255));
        jLabel50.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel50.setText("Gram");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12)
                    .addComponent(jLabel11)
                    .addComponent(jLabel10)
                    .addComponent(jLabel28))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtMangkokBS, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtMangkokBR, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtMangkokBB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtMangkokTotKpg, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18)
                    .addComponent(jLabel16)
                    .addComponent(jLabel17)
                    .addComponent(jLabel29))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtMangkokBS1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtMangkokBR1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtMangkokBB1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMangkokTotGram, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel38)
                    .addComponent(jLabel37)
                    .addComponent(jLabel39)
                    .addComponent(jLabel50))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtMangkokBR1)
                            .addComponent(jLabel37))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtMangkokBS1)
                            .addComponent(jLabel38))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtMangkokBB1)
                            .addComponent(jLabel39))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtMangkokTotGram)
                            .addComponent(jLabel50)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(txtMangkokBR)
                            .addComponent(jLabel16))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(txtMangkokBS)
                            .addComponent(jLabel18))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(txtMangkokBB)
                            .addComponent(jLabel17))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel28)
                            .addComponent(txtMangkokTotKpg)
                            .addComponent(jLabel29))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Segitiga"));

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel13.setText("BR / BRS");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel14.setText("BS");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel15.setText("BB / BB2");

        txtSegitigaBS.setBackground(new java.awt.Color(255, 255, 255));
        txtSegitigaBS.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtSegitigaBS.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        txtSegitigaBS.setText("-");

        txtSegitigaBB.setBackground(new java.awt.Color(255, 255, 255));
        txtSegitigaBB.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtSegitigaBB.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        txtSegitigaBB.setText("-");

        txtSegitigaBR.setBackground(new java.awt.Color(255, 255, 255));
        txtSegitigaBR.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtSegitigaBR.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        txtSegitigaBR.setText("-");

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel19.setText("Keping");

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel20.setText("Keping");

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel21.setText("Keping");

        txtSegitigaBR1.setBackground(new java.awt.Color(255, 255, 255));
        txtSegitigaBR1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtSegitigaBR1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        txtSegitigaBR1.setText("-");

        jLabel40.setBackground(new java.awt.Color(255, 255, 255));
        jLabel40.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel40.setText("Gram");

        txtSegitigaBS1.setBackground(new java.awt.Color(255, 255, 255));
        txtSegitigaBS1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtSegitigaBS1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        txtSegitigaBS1.setText("-");

        jLabel41.setBackground(new java.awt.Color(255, 255, 255));
        jLabel41.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel41.setText("Gram");

        txtSegitigaBB1.setBackground(new java.awt.Color(255, 255, 255));
        txtSegitigaBB1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtSegitigaBB1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        txtSegitigaBB1.setText("-");

        jLabel42.setBackground(new java.awt.Color(255, 255, 255));
        jLabel42.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel42.setText("Gram");

        jLabel30.setBackground(new java.awt.Color(255, 255, 255));
        jLabel30.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel30.setText("TOTAL");

        txtSegitigaTotKpg.setBackground(new java.awt.Color(255, 255, 255));
        txtSegitigaTotKpg.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtSegitigaTotKpg.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        txtSegitigaTotKpg.setText("-");

        jLabel51.setBackground(new java.awt.Color(255, 255, 255));
        jLabel51.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel51.setText("Keping");

        txtSegitigaTotGram.setBackground(new java.awt.Color(255, 255, 255));
        txtSegitigaTotGram.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtSegitigaTotGram.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        txtSegitigaTotGram.setText("-");

        jLabel52.setBackground(new java.awt.Color(255, 255, 255));
        jLabel52.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel52.setText("Gram");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15)
                    .addComponent(jLabel14)
                    .addComponent(jLabel13)
                    .addComponent(jLabel30))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtSegitigaBS, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtSegitigaBR, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtSegitigaBB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtSegitigaTotKpg, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel21)
                    .addComponent(jLabel19)
                    .addComponent(jLabel20)
                    .addComponent(jLabel51))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtSegitigaBS1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtSegitigaBR1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtSegitigaBB1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSegitigaTotGram, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel41)
                    .addComponent(jLabel40)
                    .addComponent(jLabel42)
                    .addComponent(jLabel52))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtSegitigaBR1)
                            .addComponent(jLabel40))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtSegitigaBS1)
                            .addComponent(jLabel41))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtSegitigaBB1)
                            .addComponent(jLabel42))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtSegitigaTotGram)
                            .addComponent(jLabel52)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(txtSegitigaBR)
                            .addComponent(jLabel19))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14)
                            .addComponent(txtSegitigaBS)
                            .addComponent(jLabel21))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(txtSegitigaBB)
                            .addComponent(jLabel20))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel30)
                            .addComponent(txtSegitigaTotKpg)
                            .addComponent(jLabel51))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Oval"));

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel22.setText("BR / BRS");

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel23.setText("BS");

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel24.setText("BB / BB2");

        txtOvalBS.setBackground(new java.awt.Color(255, 255, 255));
        txtOvalBS.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtOvalBS.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        txtOvalBS.setText("-");

        txtOvalBB.setBackground(new java.awt.Color(255, 255, 255));
        txtOvalBB.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtOvalBB.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        txtOvalBB.setText("-");

        txtOvalBR.setBackground(new java.awt.Color(255, 255, 255));
        txtOvalBR.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtOvalBR.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        txtOvalBR.setText("-");

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel25.setText("Keping");

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel26.setText("Keping");

        jLabel27.setBackground(new java.awt.Color(255, 255, 255));
        jLabel27.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel27.setText("Keping");

        txtOvalBR1.setBackground(new java.awt.Color(255, 255, 255));
        txtOvalBR1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtOvalBR1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        txtOvalBR1.setText("-");

        jLabel43.setBackground(new java.awt.Color(255, 255, 255));
        jLabel43.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel43.setText("Gram");

        txtOvalBS1.setBackground(new java.awt.Color(255, 255, 255));
        txtOvalBS1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtOvalBS1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        txtOvalBS1.setText("-");

        jLabel44.setBackground(new java.awt.Color(255, 255, 255));
        jLabel44.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel44.setText("Gram");

        txtOvalBB1.setBackground(new java.awt.Color(255, 255, 255));
        txtOvalBB1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtOvalBB1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        txtOvalBB1.setText("-");

        jLabel45.setBackground(new java.awt.Color(255, 255, 255));
        jLabel45.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel45.setText("Gram");

        jLabel53.setBackground(new java.awt.Color(255, 255, 255));
        jLabel53.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel53.setText("TOTAL");

        txtOvalTotKpg.setBackground(new java.awt.Color(255, 255, 255));
        txtOvalTotKpg.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtOvalTotKpg.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        txtOvalTotKpg.setText("-");

        jLabel54.setBackground(new java.awt.Color(255, 255, 255));
        jLabel54.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel54.setText("Keping");

        txtOvalTotGram.setBackground(new java.awt.Color(255, 255, 255));
        txtOvalTotGram.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtOvalTotGram.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        txtOvalTotGram.setText("-");

        jLabel55.setBackground(new java.awt.Color(255, 255, 255));
        jLabel55.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel55.setText("Gram");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel24)
                    .addComponent(jLabel23)
                    .addComponent(jLabel22)
                    .addComponent(jLabel53))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtOvalBS, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtOvalBR, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtOvalBB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtOvalTotKpg, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel27)
                    .addComponent(jLabel25)
                    .addComponent(jLabel26)
                    .addComponent(jLabel54))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtOvalBS1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtOvalBR1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtOvalBB1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtOvalTotGram, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel44)
                    .addComponent(jLabel43)
                    .addComponent(jLabel45)
                    .addComponent(jLabel55))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel24)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel53)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtOvalBR)
                                    .addComponent(jLabel25))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtOvalBS)
                                    .addComponent(jLabel27))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtOvalBB)
                                    .addComponent(jLabel26))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtOvalTotKpg)
                                    .addComponent(jLabel54)))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtOvalBR1)
                                    .addComponent(jLabel43))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtOvalBS1)
                                    .addComponent(jLabel44))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtOvalBB1)
                                    .addComponent(jLabel45))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtOvalTotGram)
                                    .addComponent(jLabel55))))))
                .addContainerGap())
        );

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Pecah / Lubang"));

        txtPecahLubang.setBackground(new java.awt.Color(255, 255, 255));
        txtPecahLubang.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtPecahLubang.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        txtPecahLubang.setText("-");

        jLabel31.setBackground(new java.awt.Color(255, 255, 255));
        jLabel31.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel31.setText("Keping");

        txtPecahLubang1.setBackground(new java.awt.Color(255, 255, 255));
        txtPecahLubang1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtPecahLubang1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        txtPecahLubang1.setText("-");

        jLabel32.setBackground(new java.awt.Color(255, 255, 255));
        jLabel32.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel32.setText("Gram");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtPecahLubang, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel31)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPecahLubang1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel32)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtPecahLubang)
                        .addComponent(jLabel31))
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtPecahLubang1)
                        .addComponent(jLabel32)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "-"));

        txtStrip.setBackground(new java.awt.Color(255, 255, 255));
        txtStrip.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtStrip.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        txtStrip.setText("-");

        jLabel33.setBackground(new java.awt.Color(255, 255, 255));
        jLabel33.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel33.setText("Keping");

        txtStrip1.setBackground(new java.awt.Color(255, 255, 255));
        txtStrip1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtStrip1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        txtStrip1.setText("-");

        jLabel46.setBackground(new java.awt.Color(255, 255, 255));
        jLabel46.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel46.setText("Gram");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtStrip, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel33)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtStrip1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel46)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtStrip)
                    .addComponent(jLabel33)
                    .addComponent(txtStrip1)
                    .addComponent(jLabel46))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    private javax.swing.JLabel jLabel3;
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
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JLabel txtAllBB;
    private javax.swing.JLabel txtAllBB1;
    private javax.swing.JLabel txtAllBB_persen;
    private javax.swing.JLabel txtAllBR;
    private javax.swing.JLabel txtAllBR1;
    private javax.swing.JLabel txtAllBR_persen;
    private javax.swing.JLabel txtAllBS;
    private javax.swing.JLabel txtAllBS1;
    private javax.swing.JLabel txtAllBS_persen;
    private javax.swing.JLabel txtMangkokBB;
    private javax.swing.JLabel txtMangkokBB1;
    private javax.swing.JLabel txtMangkokBR;
    private javax.swing.JLabel txtMangkokBR1;
    private javax.swing.JLabel txtMangkokBS;
    private javax.swing.JLabel txtMangkokBS1;
    private javax.swing.JLabel txtMangkokTotGram;
    private javax.swing.JLabel txtMangkokTotKpg;
    private javax.swing.JLabel txtOvalBB;
    private javax.swing.JLabel txtOvalBB1;
    private javax.swing.JLabel txtOvalBR;
    private javax.swing.JLabel txtOvalBR1;
    private javax.swing.JLabel txtOvalBS;
    private javax.swing.JLabel txtOvalBS1;
    private javax.swing.JLabel txtOvalTotGram;
    private javax.swing.JLabel txtOvalTotKpg;
    private javax.swing.JLabel txtPecahLubang;
    private javax.swing.JLabel txtPecahLubang1;
    private javax.swing.JLabel txtSegitigaBB;
    private javax.swing.JLabel txtSegitigaBB1;
    private javax.swing.JLabel txtSegitigaBR;
    private javax.swing.JLabel txtSegitigaBR1;
    private javax.swing.JLabel txtSegitigaBS;
    private javax.swing.JLabel txtSegitigaBS1;
    private javax.swing.JLabel txtSegitigaTotGram;
    private javax.swing.JLabel txtSegitigaTotKpg;
    private javax.swing.JLabel txtStrip;
    private javax.swing.JLabel txtStrip1;
    // End of variables declaration//GEN-END:variables
}
