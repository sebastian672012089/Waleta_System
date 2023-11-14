package waleta_system.HRD;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import waleta_system.Class.Utility;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class JDialog_Rekap_CabutCetakAbsen extends javax.swing.JDialog {

    ResultSet rs;
    String sql;
    String rekap_sub = "";

    public JDialog_Rekap_CabutCetakAbsen(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setResizable(false);
        loaddata();
        loadSUB();
    }

    public void loaddata_old() {
        try {
            int cabut_borongA = 0, cabut_borongB = 0, cabut_borongC = 0, cabut_borongD = 0, TOTAL_CABUT_BORONG = 0;
            int cabut_trainingA = 0, cabut_trainingB = 0, cabut_trainingC = 0, cabut_trainingD = 0, TOTAL_CABUT_TRAINING = 0;
            int cetak_borongA = 0, cetak_borongB = 0, cetak_borongC = 0, cetak_borongD = 0, cetak_borongE = 0, TOTAL_CETAK_BORONG = 0;
            int cetak_mandiriA = 0, cetak_mandiriB = 0, cetak_mandiriC = 0, cetak_mandiriD = 0, cetak_mandiriE = 0, TOTAL_CETAK_MANDIRI = 0;
            int cetak_trainingA = 0, cetak_trainingB = 0, cetak_trainingC = 0, cetak_trainingD = 0, cetak_trainingE = 0, TOTAL_CETAK_TRAINING = 0;
            sql = "SELECT DISTINCT(`pin`), `level_gaji`, `tb_karyawan`.`kode_bagian`, `tb_bagian`.`nama_bagian`,"
                    + "`divisi_bagian`, `bagian_bagian`, `ruang_bagian` "
                    + "FROM `att_log` "
                    + "LEFT JOIN `tb_karyawan` ON `att_log`.`pin` = `tb_karyawan`.`pin_finger` "
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian` "
                    + "WHERE DATE(`scan_date`) = CURRENT_DATE AND `tb_karyawan`.`kode_bagian` IS NOT NULL "
                    + "AND `level_gaji` IS NOT NULL "
                    + "AND `posisi` = 'PEJUANG' "
                    + "AND `divisi_bagian` IS NOT NULL "
                    + "AND `bagian_bagian` IS NOT NULL ";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                String divisi_bagian = rs.getString("divisi_bagian");
                String bagian_bagian = rs.getString("bagian_bagian");
                String ruang_bagian = rs.getString("ruang_bagian");
                String level_gaji = rs.getString("level_gaji");
                if ((divisi_bagian.equals("CABUT") && bagian_bagian.contains("BORONG") && ruang_bagian.equals("A"))
                        || (divisi_bagian.equals("CABUT") && bagian_bagian.contains("TRAINING") && ruang_bagian.equals("A"))) {//CABUT A
                    switch (level_gaji) {
                        case "BORONG":
                        case "BORONG HIJAU":
                        case "BORONG KUNING":
                        case "BORONG MERAH":
                        case "BORONG ORANYE":
                            cabut_borongA++;
                            break;
                        case "Training Borong Hijau":
                        case "Training Borong Kuning":
                        case "Training Borong Merah":
                        case "Training Borong Oranye":
                            cabut_trainingA++;
                            break;
                        default:
                            break;
                    }
                } else if ((divisi_bagian.equals("CABUT") && bagian_bagian.contains("BORONG") && ruang_bagian.equals("B"))
                        || (divisi_bagian.equals("CABUT") && bagian_bagian.contains("TRAINING") && ruang_bagian.equals("B"))) {//CABUT B
                    switch (level_gaji) {
                        case "BORONG":
                        case "BORONG HIJAU":
                        case "BORONG KUNING":
                        case "BORONG MERAH":
                        case "BORONG ORANYE":
                            cabut_borongB++;
                            break;
                        case "Training Borong Hijau":
                        case "Training Borong Kuning":
                        case "Training Borong Merah":
                        case "Training Borong Oranye":
                            cabut_trainingB++;
                            break;
                        default:
                            break;
                    }
                } else if ((divisi_bagian.equals("CABUT") && bagian_bagian.contains("BORONG") && ruang_bagian.equals("C"))
                        || (divisi_bagian.equals("CABUT") && bagian_bagian.contains("TRAINING") && ruang_bagian.equals("C"))) {//CABUT C
                    switch (level_gaji) {
                        case "BORONG":
                        case "BORONG HIJAU":
                        case "BORONG KUNING":
                        case "BORONG MERAH":
                        case "BORONG ORANYE":
                            cabut_borongC++;
                            break;
                        case "Training Borong Hijau":
                        case "Training Borong Kuning":
                        case "Training Borong Merah":
                        case "Training Borong Oranye":
                            cabut_trainingC++;
                            break;
                        default:
                            break;
                    }
                } else if ((divisi_bagian.equals("CABUT") && bagian_bagian.contains("BORONG") && ruang_bagian.equals("D"))
                        || (divisi_bagian.equals("CABUT") && bagian_bagian.contains("TRAINING") && ruang_bagian.equals("D"))) {//CABUT D
                    switch (level_gaji) {
                        case "BORONG":
                        case "BORONG HIJAU":
                        case "BORONG KUNING":
                        case "BORONG MERAH":
                        case "BORONG ORANYE":
                            cabut_borongD++;
                            break;
                        case "Training Borong Hijau":
                        case "Training Borong Kuning":
                        case "Training Borong Merah":
                        case "Training Borong Oranye":
                            cabut_trainingD++;
                            break;
                        default:
                            break;
                    }
                } else if ((divisi_bagian.equals("CETAK") && bagian_bagian.contains("BORONG") && ruang_bagian.equals("A"))
                        || (divisi_bagian.equals("CETAK") && bagian_bagian.contains("MANDIRI") && ruang_bagian.equals("A"))) {//CETAK A
                    switch (level_gaji) {
                        case "MANDIRI CTK BRG":
                            cetak_borongA++;
                            break;
                        case "MANDIRI 0":
                        case "MANDIRI 1":
                        case "MANDIRI 1+":
                        case "MANDIRI 2":
                        case "MANDIRI 2-":
                        case "MANDIRI 3":
                        case "MANDIRI 3-":
                            cetak_mandiriA++;
                            break;
                        case "TRAINING":
                            cetak_trainingA++;
                            break;
                        default:
                            break;
                    }
                } else if ((divisi_bagian.equals("CETAK") && bagian_bagian.contains("BORONG") && ruang_bagian.equals("B"))
                        || (divisi_bagian.equals("CETAK") && bagian_bagian.contains("MANDIRI") && ruang_bagian.equals("B"))) {//CETAK B
                    switch (level_gaji) {
                        case "MANDIRI CTK BRG":
                            cetak_borongB++;
                            break;
                        case "MANDIRI 0":
                        case "MANDIRI 1":
                        case "MANDIRI 1+":
                        case "MANDIRI 2":
                        case "MANDIRI 2-":
                        case "MANDIRI 3":
                        case "MANDIRI 3-":
                            cetak_mandiriB++;
                            break;
                        case "TRAINING":
                            cetak_trainingB++;
                            break;
                        default:
                            break;
                    }
                } else if ((divisi_bagian.equals("CETAK") && bagian_bagian.contains("BORONG") && ruang_bagian.equals("C"))
                        || (divisi_bagian.equals("CETAK") && bagian_bagian.contains("MANDIRI") && ruang_bagian.equals("C"))) {//CETAK C
                    switch (rs.getString("level_gaji")) {
                        case "MANDIRI CTK BRG":
                            cetak_borongC++;
                            break;
                        case "MANDIRI 0":
                        case "MANDIRI 1":
                        case "MANDIRI 1+":
                        case "MANDIRI 2":
                        case "MANDIRI 2-":
                        case "MANDIRI 3":
                        case "MANDIRI 3-":
                            cetak_mandiriC++;
                            break;
                        case "TRAINING":
                            cetak_trainingC++;
                            break;
                        default:
                            break;
                    }
                } else if ((divisi_bagian.equals("CETAK") && bagian_bagian.contains("BORONG") && ruang_bagian.equals("D"))
                        || (divisi_bagian.equals("CETAK") && bagian_bagian.contains("MANDIRI") && ruang_bagian.equals("D"))) {//CETAK D
                    switch (level_gaji) {
                        case "MANDIRI CTK BRG":
                            cetak_borongD++;
                            break;
                        case "MANDIRI 0":
                        case "MANDIRI 1":
                        case "MANDIRI 1+":
                        case "MANDIRI 2":
                        case "MANDIRI 2-":
                        case "MANDIRI 3":
                        case "MANDIRI 3-":
                            cetak_mandiriD++;
                            break;
                        case "TRAINING":
                            cetak_trainingD++;
                            break;
                        default:
                            break;
                    }
                } else if ((divisi_bagian.equals("CETAK") && bagian_bagian.contains("BORONG") && ruang_bagian.equals("E"))
                        || (divisi_bagian.equals("CETAK") && bagian_bagian.contains("MANDIRI") && ruang_bagian.equals("E"))) {//CETAK E
                    switch (level_gaji) {
                        case "MANDIRI CTK BRG":
                            cetak_borongE++;
                            break;
                        case "MANDIRI 0":
                        case "MANDIRI 1":
                        case "MANDIRI 1+":
                        case "MANDIRI 2":
                        case "MANDIRI 2-":
                        case "MANDIRI 3":
                        case "MANDIRI 3-":
                            cetak_mandiriE++;
                            break;
                        case "TRAINING":
                            cetak_trainingE++;
                            break;
                        default:
                            break;
                    }
                }
            }
            TOTAL_CABUT_BORONG = cabut_borongA + cabut_borongB + cabut_borongC + cabut_borongD;
            TOTAL_CABUT_TRAINING = cabut_trainingA + cabut_trainingB + cabut_trainingC + cabut_trainingD;
            TOTAL_CETAK_BORONG = cetak_borongA + cetak_borongB + cetak_borongC + cetak_borongD + cetak_borongE;
            TOTAL_CETAK_MANDIRI = cetak_mandiriA + cetak_mandiriB + cetak_mandiriC + cetak_mandiriD + cetak_mandiriE;
            TOTAL_CETAK_TRAINING = cetak_trainingA + cetak_trainingB + cetak_trainingC + cetak_trainingD + cetak_trainingE;
            label_hari_tanggal.setText("Hari, tanggal : " + new SimpleDateFormat("dd MMM yyyy").format(new Date()));
            label_cabut_borongA.setText("Borong A : " + cabut_borongA);
            label_cabut_borongB.setText("Borong B : " + cabut_borongB);
            label_cabut_borongC.setText("Borong C : " + cabut_borongC);
            label_cabut_borongD.setText("Borong D : " + cabut_borongD);
            label_cabut_totalborong.setText("Total Borong : " + TOTAL_CABUT_BORONG);
            label_cabut_trainingA.setText("Training A : " + cabut_trainingA);
            label_cabut_trainingB.setText("Training B : " + cabut_trainingB);
            label_cabut_trainingC.setText("Training C : " + cabut_trainingC);
            label_cabut_trainingD.setText("Training D : " + cabut_trainingD);
            label_cabut_totaltraining.setText("Total Training : " + TOTAL_CABUT_TRAINING);
            label_cabut_total.setText("Total Cabut : " + (TOTAL_CABUT_BORONG + TOTAL_CABUT_TRAINING));
            label_cetak_borongA.setText("Borong A : " + cetak_borongA);
            label_cetak_borongB.setText("Borong B : " + cetak_borongB);
            label_cetak_borongC.setText("Borong C : " + cetak_borongC);
            label_cetak_borongD.setText("Borong D : " + cetak_borongD);
            label_cetak_borongE.setText("Borong E : " + cetak_borongE);
            label_cetak_totalborong.setText("Total Borong : " + TOTAL_CETAK_BORONG);
            label_cetak_mandiriA.setText("Mandiri A : " + cetak_mandiriA);
            label_cetak_mandiriB.setText("Mandiri B : " + cetak_mandiriB);
            label_cetak_mandiriC.setText("Mandiri C : " + cetak_mandiriC);
            label_cetak_mandiriD.setText("Mandiri D : " + cetak_mandiriD);
            label_cetak_mandiriE.setText("Mandiri E : " + cetak_mandiriE);
            label_cetak_totalmandiri.setText("Total Mandiri : " + TOTAL_CETAK_MANDIRI);
            label_cetak_trainingA.setText("Training A : " + cetak_trainingA);
            label_cetak_trainingB.setText("Training B : " + cetak_trainingB);
            label_cetak_trainingC.setText("Training C : " + cetak_trainingC);
            label_cetak_trainingD.setText("Training D : " + cetak_trainingD);
            label_cetak_trainingE.setText("Training E : " + cetak_trainingE);
            label_cetak_totaltraining.setText("Total Training : " + TOTAL_CETAK_TRAINING);
            label_cetak_total.setText("Total Cetak : " + (TOTAL_CETAK_BORONG + TOTAL_CETAK_MANDIRI + TOTAL_CETAK_TRAINING));
        } catch (SQLException ex) {
            Logger.getLogger(JDialog_Rekap_CabutCetakAbsen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loaddata() {
        try {
            int cabut_borongA = 0, cabut_borongB = 0, cabut_borongC = 0, cabut_borongD = 0, TOTAL_CABUT_BORONG = 0;
            int cabut_trainingA = 0, cabut_trainingB = 0, cabut_trainingC = 0, cabut_trainingD = 0, TOTAL_CABUT_TRAINING = 0;
            int cetak_borongA = 0, cetak_borongB = 0, cetak_borongC = 0, cetak_borongD = 0, cetak_borongE = 0, TOTAL_CETAK_BORONG = 0;
            int cetak_mandiriA = 0, cetak_mandiriB = 0, cetak_mandiriC = 0, cetak_mandiriD = 0, cetak_mandiriE = 0, TOTAL_CETAK_MANDIRI = 0;
            int cetak_trainingA = 0, cetak_trainingB = 0, cetak_trainingC = 0, cetak_trainingD = 0, cetak_trainingE = 0, TOTAL_CETAK_TRAINING = 0;
            sql = "SELECT DISTINCT(`pin`), `level_gaji`, `tb_karyawan`.`kode_bagian`, `tb_bagian`.`nama_bagian`,"
                    + "`divisi_bagian`, `bagian_bagian`, `ruang_bagian` "
                    + "FROM `att_log` "
                    + "LEFT JOIN `tb_karyawan` ON `att_log`.`pin` = `tb_karyawan`.`pin_finger` "
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian` "
                    + "WHERE DATE(`scan_date`) = CURRENT_DATE "
                    + "AND `tb_karyawan`.`kode_bagian` IS NOT NULL "
                    + "AND `posisi` = 'PEJUANG' "
                    + "AND `divisi_bagian` IS NOT NULL "
                    + "AND `bagian_bagian` IS NOT NULL ";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                String divisi_bagian = rs.getString("divisi_bagian");
                String bagian_bagian = rs.getString("bagian_bagian");
                String ruang_bagian = rs.getString("ruang_bagian");
                String level_gaji = rs.getString("level_gaji");
                if (divisi_bagian.equals("CABUT") && bagian_bagian.contains("BORONG")) {//CABUT BORONG
                    switch (ruang_bagian) {
                        case "A":
                            cabut_borongA++;
                            break;
                        case "B":
                            cabut_borongB++;
                            break;
                        case "C":
                            cabut_borongC++;
                            break;
                        case "D":
                            cabut_borongD++;
                            break;
                        default:
                            break;
                    }
                } else if (divisi_bagian.equals("CABUT") && bagian_bagian.contains("TRAINING")) {//CABUT TRAINING
                    switch (ruang_bagian) {
                        case "A":
                            cabut_trainingA++;
                            break;
                        case "B":
                            cabut_trainingB++;
                            break;
                        case "C":
                            cabut_trainingC++;
                            break;
                        case "D":
                            cabut_trainingD++;
                            break;
                        default:
                            break;
                    }
                } else if (divisi_bagian.equals("CETAK") && level_gaji != null && level_gaji.contains("TRAINING")) {//CETAK TRAINING
                    switch (ruang_bagian) {
                        case "A":
                            cetak_trainingA++;
                            break;
                        case "B":
                            cetak_trainingB++;
                            break;
                        case "C":
                            cetak_trainingC++;
                            break;
                        case "D":
                            cetak_trainingD++;
                            break;
                        case "E":
                            cetak_trainingE++;
                            break;
                        default:
                            break;
                    }
                } else if (divisi_bagian.equals("CETAK") && bagian_bagian.contains("BORONG")) {//CETAK BORONG
                    switch (ruang_bagian) {
                        case "A":
                            cetak_borongA++;
                            break;
                        case "B":
                            cetak_borongB++;
                            break;
                        case "C":
                            cetak_borongC++;
                            break;
                        case "D":
                            cetak_borongD++;
                            break;
                        case "E":
                            cetak_borongE++;
                            break;
                        default:
                            break;
                    }
                } else if (divisi_bagian.equals("CETAK") && bagian_bagian.contains("MANDIRI")) {//CETAK MANDIRI
                    switch (ruang_bagian) {
                        case "A":
                            cetak_mandiriA++;
                            break;
                        case "B":
                            cetak_mandiriB++;
                            break;
                        case "C":
                            cetak_mandiriC++;
                            break;
                        case "D":
                            cetak_mandiriD++;
                            break;
                        case "E":
                            cetak_mandiriE++;
                            break;
                        default:
                            break;
                    }
                }
            }
            TOTAL_CABUT_BORONG = cabut_borongA + cabut_borongB + cabut_borongC + cabut_borongD;
            TOTAL_CABUT_TRAINING = cabut_trainingA + cabut_trainingB + cabut_trainingC + cabut_trainingD;
            TOTAL_CETAK_BORONG = cetak_borongA + cetak_borongB + cetak_borongC + cetak_borongD + cetak_borongE;
            TOTAL_CETAK_MANDIRI = cetak_mandiriA + cetak_mandiriB + cetak_mandiriC + cetak_mandiriD + cetak_mandiriE;
            TOTAL_CETAK_TRAINING = cetak_trainingA + cetak_trainingB + cetak_trainingC + cetak_trainingD + cetak_trainingE;
            label_hari_tanggal.setText("Hari, tanggal : " + new SimpleDateFormat("dd MMM yyyy").format(new Date()));
            label_cabut_borongA.setText("Borong A : " + cabut_borongA);
            label_cabut_borongB.setText("Borong B : " + cabut_borongB);
            label_cabut_borongC.setText("Borong C : " + cabut_borongC);
            label_cabut_borongD.setText("Borong D : " + cabut_borongD);
            label_cabut_totalborong.setText("Total Borong : " + TOTAL_CABUT_BORONG);
            label_cabut_trainingA.setText("Training A : " + cabut_trainingA);
            label_cabut_trainingB.setText("Training B : " + cabut_trainingB);
            label_cabut_trainingC.setText("Training C : " + cabut_trainingC);
            label_cabut_trainingD.setText("Training D : " + cabut_trainingD);
            label_cabut_totaltraining.setText("Total Training : " + TOTAL_CABUT_TRAINING);
            label_cabut_total.setText("Total Cabut : " + (TOTAL_CABUT_BORONG + TOTAL_CABUT_TRAINING));
            label_cetak_borongA.setText("Borong A : " + cetak_borongA);
            label_cetak_borongB.setText("Borong B : " + cetak_borongB);
            label_cetak_borongC.setText("Borong C : " + cetak_borongC);
            label_cetak_borongD.setText("Borong D : " + cetak_borongD);
            label_cetak_borongE.setText("Borong E : " + cetak_borongE);
            label_cetak_totalborong.setText("Total Borong : " + TOTAL_CETAK_BORONG);
            label_cetak_mandiriA.setText("Mandiri A : " + cetak_mandiriA);
            label_cetak_mandiriB.setText("Mandiri B : " + cetak_mandiriB);
            label_cetak_mandiriC.setText("Mandiri C : " + cetak_mandiriC);
            label_cetak_mandiriD.setText("Mandiri D : " + cetak_mandiriD);
            label_cetak_mandiriE.setText("Mandiri E : " + cetak_mandiriE);
            label_cetak_totalmandiri.setText("Total Mandiri : " + TOTAL_CETAK_MANDIRI);
            label_cetak_trainingA.setText("Training A : " + cetak_trainingA);
            label_cetak_trainingB.setText("Training B : " + cetak_trainingB);
            label_cetak_trainingC.setText("Training C : " + cetak_trainingC);
            label_cetak_trainingD.setText("Training D : " + cetak_trainingD);
            label_cetak_trainingE.setText("Training E : " + cetak_trainingE);
            label_cetak_totaltraining.setText("Total Training : " + TOTAL_CETAK_TRAINING);
            label_cetak_total.setText("Total Cetak : " + (TOTAL_CETAK_BORONG + TOTAL_CETAK_MANDIRI + TOTAL_CETAK_TRAINING));
        } catch (SQLException ex) {
            Logger.getLogger(JDialog_Rekap_CabutCetakAbsen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadSUB() {
        try {
            Utility.db_sub.connect();
            int total_sub = 0;
            DefaultTableModel model = (DefaultTableModel) tabel_sub.getModel();
            model.setRowCount(0);
            sql = "SELECT `bagian`, COUNT(DISTINCT(`pin`)) AS 'jumlah_absen' "
                    + "FROM `att_log` "
                    + "LEFT JOIN `tb_karyawan` ON `att_log`.`pin` = `tb_karyawan`.`pin_calculated` "
                    + "WHERE `jenis_pegawai` = 'KARYAWAN' AND DATE(`scan_date`) = CURRENT_DATE "
                    + "GROUP BY `bagian`";
            rs = Utility.db_sub.getStatement().executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString("bagian"), rs.getInt("jumlah_absen")});
                total_sub = total_sub + rs.getInt("jumlah_absen");
                rekap_sub = rekap_sub + rs.getString("bagian") + " : " + rs.getInt("jumlah_absen") + "\n";
            }
            rekap_sub = rekap_sub + "*TOTAL : " + total_sub + "*";
            label_totalSUB.setText(Integer.toString(total_sub));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_Rekap_CabutCetakAbsen.class.getName()).log(Level.SEVERE, null, ex);
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
        jPanel2 = new javax.swing.JPanel();
        label_hari_tanggal = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        label_cabut = new javax.swing.JLabel();
        label_cabut_borongA = new javax.swing.JLabel();
        label_cabut_borongB = new javax.swing.JLabel();
        label_cabut_borongC = new javax.swing.JLabel();
        label_cabut_borongD = new javax.swing.JLabel();
        label_cabut_totalborong = new javax.swing.JLabel();
        label_cabut_trainingA = new javax.swing.JLabel();
        label_cabut_trainingB = new javax.swing.JLabel();
        label_cabut_trainingC = new javax.swing.JLabel();
        label_cabut_trainingD = new javax.swing.JLabel();
        label_cabut_totaltraining = new javax.swing.JLabel();
        label_cabut_total = new javax.swing.JLabel();
        label_cetak_totalmandiri = new javax.swing.JLabel();
        label_cetak_mandiriA = new javax.swing.JLabel();
        label_cetak_borongB = new javax.swing.JLabel();
        label_cetak_total = new javax.swing.JLabel();
        label_cetak_borongC = new javax.swing.JLabel();
        label_cetak_borongD = new javax.swing.JLabel();
        label_cetak_totalborong = new javax.swing.JLabel();
        label_cetak_mandiriB = new javax.swing.JLabel();
        label_CETAK = new javax.swing.JLabel();
        label_cetak_mandiriC = new javax.swing.JLabel();
        label_cetak_borongA = new javax.swing.JLabel();
        label_cetak_mandiriD = new javax.swing.JLabel();
        label_cetak_trainingB = new javax.swing.JLabel();
        label_cetak_trainingC = new javax.swing.JLabel();
        label_cetak_trainingD = new javax.swing.JLabel();
        label_cetak_totaltraining = new javax.swing.JLabel();
        label_cetak_trainingA = new javax.swing.JLabel();
        label_cetak_borongE = new javax.swing.JLabel();
        label_cetak_mandiriE = new javax.swing.JLabel();
        label_cetak_trainingE = new javax.swing.JLabel();
        label_totalSUB = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_sub = new javax.swing.JTable();
        button_copy_text = new javax.swing.JButton();
        button_copy_text1 = new javax.swing.JButton();

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

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        label_hari_tanggal.setBackground(new java.awt.Color(255, 255, 255));
        label_hari_tanggal.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_hari_tanggal.setText("Hari, tanggal :");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel2.setText("Jumlah Karyawan masuk WALETA");

        label_cabut.setBackground(new java.awt.Color(255, 255, 255));
        label_cabut.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_cabut.setText("Cabut");

        label_cabut_borongA.setBackground(new java.awt.Color(255, 255, 255));
        label_cabut_borongA.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_cabut_borongA.setText("Borong A :");

        label_cabut_borongB.setBackground(new java.awt.Color(255, 255, 255));
        label_cabut_borongB.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_cabut_borongB.setText("Borong B :");

        label_cabut_borongC.setBackground(new java.awt.Color(255, 255, 255));
        label_cabut_borongC.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_cabut_borongC.setText("Borong C :");

        label_cabut_borongD.setBackground(new java.awt.Color(255, 255, 255));
        label_cabut_borongD.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_cabut_borongD.setText("Borong D :");

        label_cabut_totalborong.setBackground(new java.awt.Color(255, 255, 255));
        label_cabut_totalborong.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_cabut_totalborong.setText("Total Borong :");

        label_cabut_trainingA.setBackground(new java.awt.Color(255, 255, 255));
        label_cabut_trainingA.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_cabut_trainingA.setText("Training A :");

        label_cabut_trainingB.setBackground(new java.awt.Color(255, 255, 255));
        label_cabut_trainingB.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_cabut_trainingB.setText("Training B :");

        label_cabut_trainingC.setBackground(new java.awt.Color(255, 255, 255));
        label_cabut_trainingC.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_cabut_trainingC.setText("Training C :");

        label_cabut_trainingD.setBackground(new java.awt.Color(255, 255, 255));
        label_cabut_trainingD.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_cabut_trainingD.setText("Training D :");

        label_cabut_totaltraining.setBackground(new java.awt.Color(255, 255, 255));
        label_cabut_totaltraining.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_cabut_totaltraining.setText("Total Training :");

        label_cabut_total.setBackground(new java.awt.Color(255, 255, 255));
        label_cabut_total.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_cabut_total.setText("Total Cabut :");

        label_cetak_totalmandiri.setBackground(new java.awt.Color(255, 255, 255));
        label_cetak_totalmandiri.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_cetak_totalmandiri.setText("Total Mandiri :");

        label_cetak_mandiriA.setBackground(new java.awt.Color(255, 255, 255));
        label_cetak_mandiriA.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_cetak_mandiriA.setText("Mandiri A :");

        label_cetak_borongB.setBackground(new java.awt.Color(255, 255, 255));
        label_cetak_borongB.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_cetak_borongB.setText("Borong B :");

        label_cetak_total.setBackground(new java.awt.Color(255, 255, 255));
        label_cetak_total.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_cetak_total.setText("Total Cetak :");

        label_cetak_borongC.setBackground(new java.awt.Color(255, 255, 255));
        label_cetak_borongC.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_cetak_borongC.setText("Borong C :");

        label_cetak_borongD.setBackground(new java.awt.Color(255, 255, 255));
        label_cetak_borongD.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_cetak_borongD.setText("Borong D :");

        label_cetak_totalborong.setBackground(new java.awt.Color(255, 255, 255));
        label_cetak_totalborong.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_cetak_totalborong.setText("Total Borong :");

        label_cetak_mandiriB.setBackground(new java.awt.Color(255, 255, 255));
        label_cetak_mandiriB.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_cetak_mandiriB.setText("Mandiri B :");

        label_CETAK.setBackground(new java.awt.Color(255, 255, 255));
        label_CETAK.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_CETAK.setText("Cetak");

        label_cetak_mandiriC.setBackground(new java.awt.Color(255, 255, 255));
        label_cetak_mandiriC.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_cetak_mandiriC.setText("Mandiri C :");

        label_cetak_borongA.setBackground(new java.awt.Color(255, 255, 255));
        label_cetak_borongA.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_cetak_borongA.setText("Borong A :");

        label_cetak_mandiriD.setBackground(new java.awt.Color(255, 255, 255));
        label_cetak_mandiriD.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_cetak_mandiriD.setText("Mandiri D :");

        label_cetak_trainingB.setBackground(new java.awt.Color(255, 255, 255));
        label_cetak_trainingB.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_cetak_trainingB.setText("Training B :");

        label_cetak_trainingC.setBackground(new java.awt.Color(255, 255, 255));
        label_cetak_trainingC.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_cetak_trainingC.setText("Training C :");

        label_cetak_trainingD.setBackground(new java.awt.Color(255, 255, 255));
        label_cetak_trainingD.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_cetak_trainingD.setText("Training D :");

        label_cetak_totaltraining.setBackground(new java.awt.Color(255, 255, 255));
        label_cetak_totaltraining.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_cetak_totaltraining.setText("Total Training :");

        label_cetak_trainingA.setBackground(new java.awt.Color(255, 255, 255));
        label_cetak_trainingA.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_cetak_trainingA.setText("Training A :");

        label_cetak_borongE.setBackground(new java.awt.Color(255, 255, 255));
        label_cetak_borongE.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_cetak_borongE.setText("Borong E :");

        label_cetak_mandiriE.setBackground(new java.awt.Color(255, 255, 255));
        label_cetak_mandiriE.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_cetak_mandiriE.setText("Mandiri D :");

        label_cetak_trainingE.setBackground(new java.awt.Color(255, 255, 255));
        label_cetak_trainingE.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_cetak_trainingE.setText("Training D :");

        label_totalSUB.setBackground(new java.awt.Color(255, 255, 255));
        label_totalSUB.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_totalSUB.setText("00");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel3.setText("Jumlah Karyawan masuk SUB");

        tabel_sub.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        tabel_sub.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "SUB", "Absen"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class
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
        tabel_sub.setRowHeight(20);
        tabel_sub.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tabel_sub);

        button_copy_text.setBackground(new java.awt.Color(255, 255, 255));
        button_copy_text.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_copy_text.setText("Copy Text");
        button_copy_text.setToolTipText("Copy text into clipboard");
        button_copy_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_copy_textActionPerformed(evt);
            }
        });

        button_copy_text1.setBackground(new java.awt.Color(255, 255, 255));
        button_copy_text1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_copy_text1.setText("Copy Text");
        button_copy_text1.setToolTipText("Copy text into clipboard");
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
                .addGap(30, 30, 30)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2)
                    .addComponent(label_hari_tanggal)
                    .addComponent(label_cabut, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_cetak_total, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_cabut_borongA, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_cabut_borongB, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_cabut_borongC, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_cabut_borongD, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_cabut_totalborong, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_cetak_borongB, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_cetak_borongC, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_cetak_borongD, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_cetak_totalborong, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_cetak_borongA, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_cabut_total, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_CETAK, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_cetak_borongE, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_cabut_trainingA, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_cabut_trainingB, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_cabut_trainingC, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_cabut_trainingD, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_cabut_totaltraining, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(button_copy_text)
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(label_cetak_totalmandiri, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label_cetak_mandiriA, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label_cetak_mandiriB, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label_cetak_mandiriC, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label_cetak_mandiriD, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label_cetak_mandiriE, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(label_cetak_totaltraining, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label_cetak_trainingA, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label_cetak_trainingB, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label_cetak_trainingC, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label_cetak_trainingD, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label_cetak_trainingE, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(label_totalSUB, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_copy_text1))
                    .addComponent(jScrollPane1))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(button_copy_text, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_hari_tanggal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(label_cabut)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(label_cabut_borongA)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_cabut_borongB)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_cabut_borongC)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_cabut_borongD)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_cabut_totalborong)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_cabut_total)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(label_CETAK)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_cetak_borongA)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_cetak_borongB)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_cetak_borongC)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_cetak_borongD)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_cetak_borongE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_cetak_totalborong))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(label_cabut_trainingA)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_cabut_trainingB)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_cabut_trainingC)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_cabut_trainingD)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_cabut_totaltraining)
                        .addGap(57, 57, 57)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(label_cetak_mandiriA)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_cetak_mandiriB)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_cetak_mandiriC)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_cetak_mandiriD)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_cetak_mandiriE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_cetak_totalmandiri))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addComponent(label_cetak_trainingA)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_cetak_trainingB)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_cetak_trainingC)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_cetak_trainingD)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_cetak_trainingE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_cetak_totaltraining)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_cetak_total)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(label_totalSUB)
                    .addComponent(button_copy_text1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
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

    private void button_copy_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_copy_textActionPerformed
        // TODO add your handling code here:
        String myString = "*Jumlah Karyawan PRODUKSI Waleta masuk*\n"
                + label_hari_tanggal.getText() + "\n"
                + "*CABUT*\n"
                + label_cabut_borongA.getText() + "\n"
                + label_cabut_borongB.getText() + "\n"
                + label_cabut_borongC.getText() + "\n"
                + label_cabut_borongD.getText() + "\n"
                + "*" + label_cabut_totalborong.getText() + "*\n"
                + "\n"
                + label_cabut_trainingA.getText() + "\n"
                + label_cabut_trainingB.getText() + "\n"
                + label_cabut_trainingC.getText() + "\n"
                + label_cabut_trainingD.getText() + "\n"
                + "*" + label_cabut_totaltraining.getText() + "*\n"
                + "*" + label_cabut_total.getText() + "*\n"
                + "\n"
                + "*CETAK*\n"
                + label_cetak_borongA.getText() + "\n"
                + label_cetak_borongB.getText() + "\n"
                + label_cetak_borongC.getText() + "\n"
                + label_cetak_borongD.getText() + "\n"
                + label_cetak_borongE.getText() + "\n"
                + "*" + label_cetak_totalborong.getText() + "*\n"
                + "\n"
                + label_cetak_mandiriA.getText() + "\n"
                + label_cetak_mandiriB.getText() + "\n"
                + label_cetak_mandiriC.getText() + "\n"
                + label_cetak_mandiriD.getText() + "\n"
                + label_cetak_mandiriE.getText() + "\n"
                + "*" + label_cetak_totalmandiri.getText() + "*\n"
                + "\n"
                + label_cetak_trainingA.getText() + "\n"
                + label_cetak_trainingB.getText() + "\n"
                + label_cetak_trainingC.getText() + "\n"
                + label_cetak_trainingD.getText() + "\n"
                + label_cetak_trainingE.getText() + "\n"
                + "*" + label_cetak_totaltraining.getText() + "*\n"
                + "*" + label_cetak_total.getText() + "*\n";
        StringSelection stringSelection = new StringSelection(myString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }//GEN-LAST:event_button_copy_textActionPerformed

    private void button_copy_text1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_copy_text1ActionPerformed
        // TODO add your handling code here:
        String myString = "*Jumlah Karyawan PRODUKSI SUB masuk*\n"
                + label_hari_tanggal.getText() + "\n"
                + rekap_sub;
        StringSelection stringSelection = new StringSelection(myString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }//GEN-LAST:event_button_copy_text1ActionPerformed

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
            java.util.logging.Logger.getLogger(JDialog_Rekap_CabutCetakAbsen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JDialog_Rekap_CabutCetakAbsen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JDialog_Rekap_CabutCetakAbsen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JDialog_Rekap_CabutCetakAbsen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JDialog_Rekap_CabutCetakAbsen dialog = new JDialog_Rekap_CabutCetakAbsen(new javax.swing.JFrame(), true);
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
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_CETAK;
    private javax.swing.JLabel label_cabut;
    private javax.swing.JLabel label_cabut_borongA;
    private javax.swing.JLabel label_cabut_borongB;
    private javax.swing.JLabel label_cabut_borongC;
    private javax.swing.JLabel label_cabut_borongD;
    private javax.swing.JLabel label_cabut_total;
    private javax.swing.JLabel label_cabut_totalborong;
    private javax.swing.JLabel label_cabut_totaltraining;
    private javax.swing.JLabel label_cabut_trainingA;
    private javax.swing.JLabel label_cabut_trainingB;
    private javax.swing.JLabel label_cabut_trainingC;
    private javax.swing.JLabel label_cabut_trainingD;
    private javax.swing.JLabel label_cetak_borongA;
    private javax.swing.JLabel label_cetak_borongB;
    private javax.swing.JLabel label_cetak_borongC;
    private javax.swing.JLabel label_cetak_borongD;
    private javax.swing.JLabel label_cetak_borongE;
    private javax.swing.JLabel label_cetak_mandiriA;
    private javax.swing.JLabel label_cetak_mandiriB;
    private javax.swing.JLabel label_cetak_mandiriC;
    private javax.swing.JLabel label_cetak_mandiriD;
    private javax.swing.JLabel label_cetak_mandiriE;
    private javax.swing.JLabel label_cetak_total;
    private javax.swing.JLabel label_cetak_totalborong;
    private javax.swing.JLabel label_cetak_totalmandiri;
    private javax.swing.JLabel label_cetak_totaltraining;
    private javax.swing.JLabel label_cetak_trainingA;
    private javax.swing.JLabel label_cetak_trainingB;
    private javax.swing.JLabel label_cetak_trainingC;
    private javax.swing.JLabel label_cetak_trainingD;
    private javax.swing.JLabel label_cetak_trainingE;
    private javax.swing.JLabel label_hari_tanggal;
    private javax.swing.JLabel label_totalSUB;
    private javax.swing.JTable tabel_sub;
    // End of variables declaration//GEN-END:variables
}
