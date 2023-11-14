package waleta_system.Panel_produksi;

import waleta_system.Class.Utility;

import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import waleta_system.Class.ColumnsAutoSizer;

import waleta_system.Class.DataEvaluasi;

public class JFrame_Evaluasi_lama_proses extends javax.swing.JFrame {

    
    String sql = null;
    ResultSet rs;
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    public JFrame_Evaluasi_lama_proses() {
        initComponents();
        
    }
    
    public ArrayList<DataEvaluasi> EvaluasiList() {
        ArrayList<DataEvaluasi> EvaluasiList = new ArrayList<>();
        try {
            
            if (Date_Mulai.getDate() != null && Date_Selesai.getDate() != null) {
                String search_tgl = null;
                sql = "select (SELECT COUNT(no_laporan_produksi) FROM tb_laporan_produksi WHERE tb_laporan_produksi.tanggal_lp=v.selected_date) AS total_lp, v.selected_date as tanggal_lp, "
                        + "(SELECT SUM(berat_basah) FROM tb_laporan_produksi WHERE tb_laporan_produksi.tanggal_lp=v.selected_date) AS total_berat, "
                        + "(SELECT SUM(berat_basah) FROM tb_laporan_produksi JOIN tb_cuci ON tb_laporan_produksi.no_laporan_produksi = tb_cuci.no_laporan_produksi WHERE tb_laporan_produksi.tanggal_lp BETWEEN '" + dateFormat.format(Date_Mulai.getDate()) + "' AND '" + dateFormat.format(Date_Selesai.getDate()) + "' AND tb_cuci.tgl_masuk_cuci = v.selected_date) AS total_berat_cuci, "
                        + "(SELECT SUM(berat_basah) FROM tb_laporan_produksi JOIN tb_cabut ON tb_laporan_produksi.no_laporan_produksi = tb_cabut.no_laporan_produksi WHERE tb_laporan_produksi.tanggal_lp BETWEEN '" + dateFormat.format(Date_Mulai.getDate()) + "' AND '" + dateFormat.format(Date_Selesai.getDate()) + "' AND tb_cabut.tgl_setor_cabut = v.selected_date) AS total_berat_selesai_cabut, "
                        + "(SELECT SUM(berat_basah) FROM tb_laporan_produksi JOIN tb_cetak ON tb_laporan_produksi.no_laporan_produksi = tb_cetak.no_laporan_produksi WHERE tb_laporan_produksi.tanggal_lp BETWEEN '" + dateFormat.format(Date_Mulai.getDate()) + "' AND '" + dateFormat.format(Date_Selesai.getDate()) + "' AND tb_cetak.tgl_selesai_cetak = v.selected_date) AS total_berat_selesai_cetak, "
                        + "(SELECT SUM(berat_basah) FROM tb_laporan_produksi JOIN tb_finishing_2 ON tb_laporan_produksi.no_laporan_produksi = tb_finishing_2.no_laporan_produksi WHERE tb_laporan_produksi.tanggal_lp BETWEEN '" + dateFormat.format(Date_Mulai.getDate()) + "' AND '" + dateFormat.format(Date_Selesai.getDate()) + "' AND tb_finishing_2.tgl_setor_f2 = v.selected_date) AS total_berat_selesai_f2, "
                        + "(SELECT SUM(berat_basah) FROM tb_laporan_produksi JOIN tb_lab_laporan_produksi ON tb_laporan_produksi.no_laporan_produksi = tb_lab_laporan_produksi.no_laporan_produksi WHERE tb_laporan_produksi.tanggal_lp BETWEEN '" + dateFormat.format(Date_Mulai.getDate()) + "' AND '" + dateFormat.format(Date_Selesai.getDate()) + "' AND tb_lab_laporan_produksi.tgl_selesai = v.selected_date) AS total_berat_selesai_qc, "
                        + "(SELECT SUM(berat_basah) FROM tb_laporan_produksi JOIN tb_bahan_jadi_masuk ON tb_laporan_produksi.no_laporan_produksi = tb_bahan_jadi_masuk.kode_asal WHERE tb_laporan_produksi.tanggal_lp BETWEEN '" + dateFormat.format(Date_Mulai.getDate()) + "' AND '" + dateFormat.format(Date_Selesai.getDate()) + "' AND tb_bahan_jadi_masuk.tanggal_grading = v.selected_date) AS total_berat_selesai_grading_bj FROM \n"
                        + "(select adddate('1970-01-01',t4.i*10000 + t3.i*1000 + t2.i*100 + t1.i*10 + t0.i) selected_date from\n"
                        + " (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t0,\n"
                        + " (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t1,\n"
                        + " (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t2,\n"
                        + " (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t3,\n"
                        + " (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t4) v\n"
                        + "where selected_date BETWEEN '" + dateFormat.format(Date_Mulai.getDate()) + "' AND DATE_ADD('" + dateFormat.format(Date_Selesai.getDate()) + "', INTERVAL "+(int)jSpinner_Tambahan.getValue()+" DAY)";
                rs = Utility.db.getStatement().executeQuery(sql);
                DataEvaluasi evaluasi;
                while (rs.next()) {
                    evaluasi = new DataEvaluasi(rs.getDate("tanggal_lp"),
                            rs.getInt("total_lp"),
                            rs.getInt("total_berat"),
                            rs.getInt("total_berat_cuci"),
                            rs.getInt("total_berat_selesai_cabut"),
                            rs.getInt("total_berat_selesai_cetak"),
                            rs.getInt("total_berat_selesai_f2"),
                            rs.getInt("total_berat_selesai_qc"),
                            rs.getInt("total_berat_selesai_grading_bj")
                    );
                    EvaluasiList.add(evaluasi);
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(JFrame_Evaluasi_lama_proses.class.getName()).log(Level.SEVERE, null, ex);
        }
        return EvaluasiList;
    }

    private void show_data_evaluasi() {
        ArrayList<DataEvaluasi> list = EvaluasiList();
        Object[][] data = new Object[7][list.size() + 2];
        String[] judul = new String[list.size() + 2];
        judul[0] = "Proses";
        judul[1] = "Total";
//        data[0] = new Object[list.size()];
//        data[1] = new Object[list.size()];
//        data[2] = new Object[list.size()];
//        data[3] = new Object[list.size()];
//        data[4] = new Object[list.size()];
        data[0][0] = "Cuci";
        data[1][0] = "Selesai Cbt";
        data[2][0] = "Selesai Ctk";
        data[3][0] = "Selesai F2";
        data[4][0] = "Selesai QC";
        data[5][0] = "Grading BJ";
        data[6][0] = "Total LP";
        int totalBeratCuci = 0;
        int totalBeratSelesaiCabut = 0;
        int totalBeratSelesaiCetak = 0;
        int totalBeratSelesaiF2 = 0;
        int totalBeratSelesaiQC = 0;
        int totalBeratSelesaiGradingBJ = 0;
        int totalLP = 0;
        for (int i = 0; i < list.size(); i++) {
            totalBeratCuci += list.get(i).getTotal_berat_cuci();
            totalBeratSelesaiCabut += list.get(i).getTotal_berat_selesai_cabut();
            totalBeratSelesaiCetak += list.get(i).getTotal_berat_selesai_cetak();
            totalBeratSelesaiF2 += list.get(i).getTotal_berat_selesai_f2();
            totalBeratSelesaiQC += list.get(i).getTotal_berat_selesai_qc();
            totalBeratSelesaiGradingBJ += list.get(i).getTotal_berat_selesai_grading_bj();
            totalLP += list.get(i).getTotal_lp();
            data[0][i + 2] = decimalFormat.format(list.get(i).getTotal_berat_cuci());
            data[1][i + 2] = decimalFormat.format(list.get(i).getTotal_berat_selesai_cabut());
            data[2][i + 2] = decimalFormat.format(list.get(i).getTotal_berat_selesai_cetak());
            data[3][i + 2] = decimalFormat.format(list.get(i).getTotal_berat_selesai_f2());
            data[4][i + 2] = decimalFormat.format(list.get(i).getTotal_berat_selesai_qc());
            data[5][i + 2] = decimalFormat.format(list.get(i).getTotal_berat_selesai_grading_bj());
            data[6][i + 2] = decimalFormat.format(list.get(i).getTotal_lp());
            judul[i + 2] = dateFormat.format(list.get(i).getTanggal_lp());
        }
        decimalFormat.setGroupingUsed(true);
        data[0][1] = decimalFormat.format(totalBeratCuci);
        data[1][1] = decimalFormat.format(totalBeratSelesaiCabut);
        data[2][1] = decimalFormat.format(totalBeratSelesaiCetak);
        data[3][1] = decimalFormat.format(totalBeratSelesaiF2);
        data[4][1] = decimalFormat.format(totalBeratSelesaiQC);
        data[5][1] = decimalFormat.format(totalBeratSelesaiGradingBJ);
        data[6][1] = totalLP;

        Table_EvaluasiLamaProsesWaleta.setModel(new javax.swing.table.DefaultTableModel(
                data,
                judul
        ));
        
        /*Table_EvaluasiLamaProsesWaleta.getColumnModel().getColumn(0).setPreferredWidth(120);
        Table_EvaluasiLamaProsesWaleta.getColumnModel().getColumn(1).setPreferredWidth(120);
        for (int i = 0; i < list.size(); i++) {
            Table_EvaluasiLamaProsesWaleta.getColumnModel().getColumn(i+2).setPreferredWidth(120);
        }
        Table_EvaluasiLamaProsesWaleta.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);*/
        ColumnsAutoSizer.sizeColumnsToFit(Table_EvaluasiLamaProsesWaleta);
        jScrollPane1.setViewportView(Table_EvaluasiLamaProsesWaleta);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        Date_Mulai = new com.toedter.calendar.JDateChooser();
        Date_Selesai = new com.toedter.calendar.JDateChooser();
        jSpinner_Tambahan = new javax.swing.JSpinner();
        button_search_dataMasuk = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_EvaluasiLamaProsesWaleta = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel15.setText("Evaluasi Lama Proses Waleta");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Tanggal :");

        Date_Mulai.setBackground(new java.awt.Color(255, 255, 255));
        Date_Mulai.setDate(new Date(new Date().getTime()- (7 * 24 * 60 * 60 * 1000)));
        Date_Mulai.setDateFormatString("dd MMMM yyyy");
        Date_Mulai.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_Selesai.setBackground(new java.awt.Color(255, 255, 255));
        Date_Selesai.setDate(new Date());
        Date_Selesai.setDateFormatString("dd MMMM yyyy");
        Date_Selesai.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jSpinner_Tambahan.setModel(new javax.swing.SpinnerNumberModel(5, 5, 40, 5));

        button_search_dataMasuk.setBackground(new java.awt.Color(255, 255, 255));
        button_search_dataMasuk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_dataMasuk.setText("Refresh");
        button_search_dataMasuk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_dataMasukActionPerformed(evt);
            }
        });

        Table_EvaluasiLamaProsesWaleta.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(Table_EvaluasiLamaProsesWaleta);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 780, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel15)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_Mulai, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_Selesai, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSpinner_Tambahan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search_dataMasuk)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(Date_Selesai, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_search_dataMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jSpinner_Tambahan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(Date_Mulai, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 524, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void button_search_dataMasukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_dataMasukActionPerformed
        show_data_evaluasi();
    }//GEN-LAST:event_button_search_dataMasukActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JFrame_Evaluasi_lama_proses.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFrame_Evaluasi_lama_proses.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFrame_Evaluasi_lama_proses.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrame_Evaluasi_lama_proses.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new JFrame_Evaluasi_lama_proses().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser Date_Mulai;
    private com.toedter.calendar.JDateChooser Date_Selesai;
    private javax.swing.JTable Table_EvaluasiLamaProsesWaleta;
    public static javax.swing.JButton button_search_dataMasuk;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner jSpinner_Tambahan;
    // End of variables declaration//GEN-END:variables
}
