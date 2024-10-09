package waleta_system.HRD;

import waleta_system.Class.Utility;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import waleta_system.Browse_Bagian;

public class Insert_DataKaryawan extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String imgPath = null;
    Boolean Check = true;
    String id_pegawai;
    Date today = new Date();
    int fc_ktp, sertifikat_vaksin1, sertifikat_vaksin2, surat_pernyataan;

    public Insert_DataKaryawan(java.awt.Frame parent, boolean modal, String ID_pegawai, String new_edit) {
        initComponents();
        this.id_pegawai = ID_pegawai;
        txt_id_pegawai.setText(ID_pegawai);
        this.setResizable(false);

        try {
            String sql1 = "SELECT `status`, `rek_cimb` FROM `tb_karyawan` WHERE `id_pegawai` = '" + ID_pegawai + "'";
            ResultSet rs1 = Utility.db.getStatement().executeQuery(sql1);
            if (rs1.next()) {
                if (rs1.getString("rek_cimb") != null && rs1.getString("status").equals("IN")) {
                    txt_nama.setEditable(false);
                } else {
                    txt_nama.setEditable(true);
                }
                if (new_edit.equals("edit") && !rs1.getString("status").equals("-")) {
                    button_pilih_bagian.setEnabled(false);
                } else {
                    button_pilih_bagian.setEnabled(true);
                }
            }

            ComboBox_posisi.removeAllItems();
            sql = "SELECT DISTINCT(`posisi`) AS 'posisi' FROM `tb_karyawan` WHERE 1";
            pst = Utility.db.getConnection().prepareStatement(sql);
            ResultSet rs2 = pst.executeQuery();
            while (rs2.next()) {
                ComboBox_posisi.addItem(rs2.getString("posisi"));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(Insert_DataKaryawan.class.getName()).log(Level.SEVERE, null, e);
        }

        if (new_edit.equals("edit")) {
            show_item(ID_pegawai);
        }
    }

    public boolean CheckInput() {
        //return false kalau ada yg null
//        System.out.println("NIK : " + txt_nik_ktp.getText());
//        System.out.println("nama : " + txt_nama.getText());
//        System.out.println("tmpt lahir : " + txt_tempat_lahir.getText());
//        System.out.println("tgl lahir : " + Date_tgl_lahir.getDate());
//        System.out.println("tgl masuk : " + Date_masuk.getDate());
//        System.out.println("tgl surat : " + Date_tgl_surat.getDate());
//        System.out.println("desa : " + txt_desa.getText());
//        System.out.println("kec : " + txt_kecamatan.getText());
//        System.out.println("id : " + txt_id_pegawai.getText());
        return !(txt_nik_ktp.getText() == null || txt_nik_ktp.getText().equals("")
                || txt_id_pegawai.getText() == null || txt_id_pegawai.getText().equals("")
                || txt_nama.getText() == null || txt_nama.getText().equals("")
                || txt_tempat_lahir.getText() == null || txt_tempat_lahir.getText().equals("")
                || Date_tgl_lahir.getDate() == null
                || Date_tgl_surat.getDate() == null
                || txt_desa.getText() == null || txt_desa.getText().equals("")
                || txt_kecamatan.getText() == null || txt_kecamatan.getText().equals("")
                || txt_kota_kabupaten.getText() == null || txt_kota_kabupaten.getText().equals("")
                || txt_provinsi.getText() == null || txt_provinsi.getText().equals(""));
    }

    public void CheckBoxIdentification() {
        if (check_ktp.isSelected()) {
            fc_ktp = 1;
        } else {
            fc_ktp = 0;
        }
        if (check_sertifikat_vaksin1.isSelected()) {
            sertifikat_vaksin1 = 1;
        } else {
            sertifikat_vaksin1 = 0;
        }
        if (check_sertifikat_vaksin2.isSelected()) {
            sertifikat_vaksin2 = 1;
        } else {
            sertifikat_vaksin2 = 0;
        }
        if (check_surat_pernyataan.isSelected()) {
            surat_pernyataan = 1;
        } else {
            surat_pernyataan = 0;
        }
    }

    public String generate_Nama(String nama, String nik, String Desa) {
        String nama_baru = "";
        boolean nik_belum_ada = true;
        try {
            String query = "SELECT `nama_pegawai` FROM `tb_karyawan` "
                    + "WHERE `status` NOT LIKE '%SUB' AND `nik_ktp` = '" + nik + "'";
            ResultSet result = Utility.db.getStatement().executeQuery(query);
            if (result.next()) {
                nik_belum_ada = false;
                nama_baru = result.getString("nama_pegawai");
            }

            if (nik_belum_ada) {
                query = "SELECT `nama_pegawai`, MAX(SUBSTRING(`nama_pegawai`, " + (nama.length() + 2) + ", 2)+0) AS 'nomor' "
                        + "FROM `tb_karyawan`  "
                        + "WHERE `status` NOT LIKE '%SUB' AND `nama_pegawai` LIKE '" + nama + "%'";
                result = Utility.db.getStatement().executeQuery(query);
                if (result.next()) {
                    if (result.getString("nama_pegawai") == null) {
                        nama_baru = nama;
                    } else if (result.getInt("nomor") == 0) {
                        String query_check = "SELECT `nama_pegawai` FROM `tb_karyawan` WHERE `nama_pegawai` = '" + nama + "'";
                        ResultSet result_check = Utility.db.getStatement().executeQuery(query_check);
                        if (result_check.next()) {
                            nama_baru = nama + " 2 " + Desa;
                            JOptionPane.showMessageDialog(this, nama_baru + " teridentifikasi nomor 2, harap menambahkan untuk yang nomor 1");
                        } else {
                            nama_baru = nama;
                        }
                    } else {
                        nama_baru = nama + " " + (result.getInt("nomor") + 1) + " " + Desa;
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Data_Karyawan.class.getName()).log(Level.SEVERE, null, ex);
        }
        return nama_baru;
    }

    public void insert() {
        Check = true;
        CheckBoxIdentification();
        try {
            if (!CheckInput()) {
                JOptionPane.showMessageDialog(this, "Silahkan lengkapi data diatas");
                Check = false;
            }
            String masuk = "NULL";
            if (Date_masuk.getDate() != null) {
                masuk = "'" + dateFormat.format(Date_masuk.getDate()) + "'";
            }
            if (Check) {
                String query = "SELECT * FROM `tb_karyawan` WHERE `nik_ktp` = '" + txt_nik_ktp.getText() + "'";
                PreparedStatement pst1 = Utility.db.getConnection().prepareStatement(query);
                rs = pst1.executeQuery();
                if (rs.next()) {
                    int dialogResult = JOptionPane.showConfirmDialog(this, "NIK sudah ada, \nApakah " + rs.getString("nama_pegawai") + " masuk kembali?", "Warning", 0);
                    if (dialogResult == JOptionPane.YES_OPTION) {
                        String new_id = txt_id_pegawai.getText();
                        String insert = "INSERT INTO `tb_karyawan`(`id_pegawai`, `pin_finger`, `nik_ktp`, `nama_pegawai`, `jenis_kelamin`, `tempat_lahir`, `tanggal_lahir`, `agama`, `alamat`, `desa`, `kecamatan`, `kota_kabupaten`, `provinsi`, `golongan_darah`, `no_telp`, `email`, `status_kawin`, `nama_ibu`, `kode_bagian`, `posisi`, `pendidikan`, `tanggal_interview`, `tanggal_masuk`, `tanggal_keluar`, `status`, `level_gaji`, `jam_kerja`, `fc_ktp`, `sertifikat_vaksin`, `berkas_surat_pernyataan`, `tanggal_surat`, `rek_cimb`, `keterangan`) "
                                + "VALUES ("
                                + "'" + new_id + "',"
                                + "'" + new_id.substring(7) + "',"
                                + "'" + rs.getString("nik_ktp") + "',"
                                + "'" + rs.getString("nama_pegawai") + "',"
                                + "'" + rs.getString("jenis_kelamin") + "',"
                                + "'" + rs.getString("tempat_lahir") + "',"
                                + "'" + rs.getString("tanggal_lahir") + "',"
                                + "'" + rs.getString("agama") + "',"
                                + "'" + rs.getString("alamat") + "',"
                                + "'" + rs.getString("desa") + "',"
                                + "'" + rs.getString("kecamatan") + "',"
                                + "'" + rs.getString("kota_kabupaten") + "',"
                                + "'" + rs.getString("provinsi") + "',"
                                + "'" + rs.getString("golongan_darah") + "',"
                                + "'" + rs.getString("no_telp") + "',"
                                + "'" + rs.getString("email") + "',"
                                + "'" + rs.getString("status_kawin") + "',"
                                + "'" + rs.getString("nama_ibu") + "',"
                                + "NULL,"
                                + "'" + rs.getString("posisi") + "',"
                                + "'" + rs.getString("pendidikan") + "',"
                                + "'" + new SimpleDateFormat("yyyy-MM-dd").format(today) + "',"
                                + "'" + new SimpleDateFormat("yyyy-MM-dd").format(today) + "',"
                                + "NULL,"
                                + "'-',"
                                + "NULL,"
                                + "NULL,"
                                + "'" + rs.getInt("fc_ktp") + "',"
                                + "'" + rs.getInt("sertifikat_vaksin") + "',"
                                + "'" + rs.getInt("berkas_surat_pernyataan") + "',"
                                + "NULL,"
                                + "'" + rs.getString("rek_cimb") + "',"
                                + "'" + rs.getString("keterangan") + "'"
                                + ")";
                        pst = Utility.db.getConnection().prepareStatement(insert);
                        pst.executeUpdate();
                    }
                } else {
                    if (imgPath != null) {
                        sql = "INSERT INTO `tb_karyawan`(`id_pegawai`, `pin_finger`, `nik_ktp`, `nama_pegawai`, `jenis_kelamin`, `tempat_lahir`, `tanggal_lahir`, `alamat`, `desa`, `kecamatan`, `kota_kabupaten`, `provinsi`, `golongan_darah`, `status_kawin`, `email`, `kode_bagian`, `posisi`, `pendidikan`, `tanggal_masuk`, `tanggal_keluar`, `status`, `fc_ktp`, `sertifikat_vaksin1`, `sertifikat_vaksin2`, `berkas_surat_pernyataan`, `tanggal_surat`, `tanggal_interview`, `nama_ibu`, `no_telp`) "
                                + "VALUES ('" + txt_id_pegawai.getText() + "',"
                                + "'" + txt_id_pegawai.getText().substring(7) + "',"
                                + "'" + txt_nik_ktp.getText() + "',"
                                + "'" + generate_Nama(txt_nama.getText(), txt_nik_ktp.getText(), txt_desa.getText()) + "',"
                                + "'" + ComboBox_Kelamin.getSelectedItem() + "',"
                                + "'" + txt_tempat_lahir.getText() + "',"
                                + "'" + dateFormat.format(Date_tgl_lahir.getDate()) + "',"
                                + "'" + txt_alamat.getText() + "',"
                                + "'" + txt_desa.getText() + "',"
                                + "'" + txt_kecamatan.getText() + "',"
                                + "'" + txt_kota_kabupaten.getText() + "',"
                                + "'" + txt_provinsi.getText() + "',"
                                + "'" + ComboBox_golDarah.getSelectedItem().toString() + "',"
                                + "'" + ComboBox_kawin.getSelectedItem() + "',"
                                + "'" + txt_email.getText() + "',"
                                + "'" + txt_kode_bagian.getText() + "',"
                                + "'" + ComboBox_posisi.getSelectedItem() + "',"
                                + "'" + ComboBox_pendidikan.getSelectedItem() + "',"
                                + "" + masuk + ","
                                + "NULL,"
                                + "'-',"
                                + "'" + fc_ktp + "',"
                                + "'" + sertifikat_vaksin1 + "',"
                                + "'" + sertifikat_vaksin2 + "',"
                                + "'" + surat_pernyataan + "',"
                                + "'" + dateFormat.format(Date_tgl_surat.getDate()) + "',"
                                + "'" + dateFormat.format(today) + "', "
                                + "'" + txt_nama_ibu.getText() + "', "
                                + "'" + txt_no_telp.getText() + "' )";
                        Utility.db.getConnection().createStatement();
                        if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                            File output = new File("\\\\192.168.10.2\\Shared Folder\\DOKUMEN SISTEM\\FC_KTP\\" + txt_id_pegawai.getText() + ".JPG");
                            Utility.createImage(new File(imgPath), output);
                            JOptionPane.showMessageDialog(this, "data SAVED!");
                            this.dispose();
                        } else {
                            JOptionPane.showMessageDialog(this, "FAILED!");
                        }
                    } else {
                        sql = "INSERT INTO `tb_karyawan`(`id_pegawai`, `pin_finger`, `nik_ktp`, `nama_pegawai`, `jenis_kelamin`, `tempat_lahir`, `tanggal_lahir`, `alamat`, `desa`, `kecamatan`, `kota_kabupaten`, `provinsi`, `golongan_darah`, `status_kawin`, `email`, `kode_bagian`, `posisi`, `pendidikan`, `tanggal_masuk`, `tanggal_keluar`, `status`, `fc_ktp`, `sertifikat_vaksin1`, `sertifikat_vaksin2`, `berkas_surat_pernyataan`, `tanggal_surat`, `tanggal_interview`, `nama_ibu`, `no_telp`) "
                                + "VALUES ('" + txt_id_pegawai.getText() + "',"
                                + "'" + txt_id_pegawai.getText().substring(7) + "',"
                                + "'" + txt_nik_ktp.getText() + "',"
                                + "'" + generate_Nama(txt_nama.getText(), txt_nik_ktp.getText(), txt_desa.getText()) + "','" + ComboBox_Kelamin.getSelectedItem() + "',"
                                + "'" + txt_tempat_lahir.getText() + "',"
                                + "'" + dateFormat.format(Date_tgl_lahir.getDate()) + "',"
                                + "'" + txt_alamat.getText() + "',"
                                + "'" + txt_desa.getText() + "',"
                                + "'" + txt_kecamatan.getText() + "',"
                                + "'" + txt_kota_kabupaten.getText() + "',"
                                + "'" + txt_provinsi.getText() + "',"
                                + "'" + ComboBox_golDarah.getSelectedItem().toString() + "',"
                                + "'" + ComboBox_kawin.getSelectedItem() + "',"
                                + "'" + txt_email.getText() + "',"
                                + "'" + txt_kode_bagian.getText() + "',"
                                + "'" + ComboBox_posisi.getSelectedItem() + "',"
                                + "'" + ComboBox_pendidikan.getSelectedItem() + "',"
                                + "" + masuk + ","
                                + "NULL,"
                                + "'-',"
                                + "'" + fc_ktp + "',"
                                + "'" + sertifikat_vaksin1 + "',"
                                + "'" + sertifikat_vaksin2 + "',"
                                + "'" + surat_pernyataan + "',"
                                + "'" + dateFormat.format(Date_tgl_surat.getDate()) + "',"
                                + "'" + dateFormat.format(today) + "', "
                                + "'" + txt_nama_ibu.getText() + "', "
                                + "'" + txt_no_telp.getText() + "')";
                        Utility.db.getConnection().createStatement();
                        if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                            JOptionPane.showMessageDialog(this, "data SAVED!");
                            this.dispose();
                        } else {
                            JOptionPane.showMessageDialog(this, "FAILED!");
                        }
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(Insert_DataKaryawan.class.getName()).log(Level.SEVERE, null, e);
        }
        imgPath = null;
    }

    public void update() {
        try {
            CheckBoxIdentification();
            String tanggal_masuk = "";
            if (Date_masuk.getDate() != null) {
                tanggal_masuk = ",`tanggal_masuk`='" + dateFormat.format(Date_masuk.getDate()) + "'";
            }
            String nik = "`nik_ktp`='" + txt_nik_ktp.getText() + "',";
            if ("".equals(txt_nik_ktp.getText())) {
                nik = "`nik_ktp`= NULL,";
            }
            String golongan_darah = "";
            if (ComboBox_golDarah.getSelectedItem() != null) {
                golongan_darah = "`golongan_darah`='" + ComboBox_golDarah.getSelectedItem().toString() + "',";
            }
            String status_kawin = "";
            if (ComboBox_kawin.getSelectedItem() != null) {
                status_kawin = "`status_kawin`='" + ComboBox_kawin.getSelectedItem().toString() + "', ";
            }
            String pendidikan = "";
            if (ComboBox_pendidikan.getSelectedItem() != null) {
                pendidikan = "`pendidikan`='" + ComboBox_pendidikan.getSelectedItem().toString() + "', ";
            }
            String posisi = "";
            if (ComboBox_posisi.getSelectedItem() != null) {
                posisi = "`posisi`='" + ComboBox_posisi.getSelectedItem().toString() + "', ";
            }
            String update_tgl_surat = "";
            if (Date_tgl_surat.getDate() != null) {
                update_tgl_surat = "`tanggal_surat`='" + dateFormat.format(Date_tgl_surat.getDate()) + "', ";
            }
            sql = "UPDATE `tb_karyawan` SET "
                    + nik
                    + "`nama_pegawai`='" + txt_nama.getText() + "',"
                    + "`jenis_kelamin`='" + ComboBox_Kelamin.getSelectedItem() + "',"
                    + "`tempat_lahir`='" + txt_tempat_lahir.getText() + "',"
                    + "`tanggal_lahir`='" + dateFormat.format(Date_tgl_lahir.getDate()) + "',"
                    + "`alamat`='" + txt_alamat.getText() + "',"
                    + "`desa`='" + txt_desa.getText() + "',"
                    + "`kecamatan`='" + txt_kecamatan.getText() + "',"
                    + "`kota_kabupaten`='" + txt_kota_kabupaten.getText() + "',"
                    + "`provinsi`='" + txt_provinsi.getText() + "',"
                    + golongan_darah
                    + status_kawin
                    + "`nama_ibu` = '" + txt_nama_ibu.getText() + "', "
                    + "`no_telp` = '" + txt_no_telp.getText() + "', "
                    + "`kode_bagian`='" + txt_kode_bagian.getText() + "',"
                    + posisi
                    + pendidikan
                    + "`fc_ktp`='" + fc_ktp + "',"
                    + "`sertifikat_vaksin1`='" + sertifikat_vaksin1 + "',"
                    + "`sertifikat_vaksin2`='" + sertifikat_vaksin2 + "',"
                    + "`berkas_surat_pernyataan`='" + surat_pernyataan + "',"
                    + update_tgl_surat
                    + "`email`='" + txt_email.getText() + "'"
                    + tanggal_masuk
                    + "WHERE `id_pegawai` = '" + id_pegawai + "'";
            Utility.db.getConnection().createStatement();
            if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                if (imgPath != null) {
                    File output = new File("\\\\192.168.10.2\\Shared Folder\\DOKUMEN SISTEM\\FC_KTP\\" + txt_id_pegawai.getText() + ".JPG");
                    Utility.createImage(new File(imgPath), output);
//                    InputStream img = new FileInputStream(new File(imgPath));
//                    String query = "UPDATE `tb_karyawan` SET `ktp_image`=? WHERE `id_pegawai` = '" + txt_id_pegawai.getText() + "'";
//                    pst = Utility.db.getConnection().prepareStatement(query);
//                    pst.setBlob(1, img);
//                    pst.executeUpdate();
                }
                JOptionPane.showMessageDialog(this, "data SAVED!");
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "FAILED!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(Insert_DataKaryawan.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void show_item(String id_pegawai) {
        try {
            String get_karyawan_sql = "SELECT `id_pegawai`, `pin_finger`, `nik_ktp`, `nama_pegawai`, `jenis_kelamin`, `tempat_lahir`, `tanggal_lahir`, `agama`, `alamat`, `desa`, `kecamatan`, `kota_kabupaten`, `provinsi`, `golongan_darah`, `no_telp`, `email`, `status_kawin`, `nama_ibu`, `tb_karyawan`.`kode_bagian`, `nama_bagian`, `kode_departemen`, `posisi`, `pendidikan`, `tanggal_interview`, `tanggal_masuk`, `tanggal_keluar`, `status`, `level_gaji`, `jam_kerja`, "
                    + "`fc_ktp`, `sertifikat_vaksin1`, `sertifikat_vaksin2`, `berkas_surat_pernyataan`, `tanggal_surat`, `rek_cimb`, `jalur_jemputan`, `potongan_bpjs`, `keterangan`, DATE_ADD(`tanggal_surat`, INTERVAL 6 MONTH) AS 'tgl_surat_berakhir' "
                    + "FROM `tb_karyawan` "
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian` "
                    + "WHERE `id_pegawai`='" + id_pegawai + "'";
            pst = Utility.db.getConnection().prepareStatement(get_karyawan_sql);
            ResultSet result = pst.executeQuery();
            if (result.next()) {
                txt_id_pegawai.setText(result.getString("id_pegawai"));
                txt_nik_ktp.setText(result.getString("nik_ktp"));
                txt_nama.setText(result.getString("nama_pegawai"));
                ComboBox_Kelamin.setSelectedItem(result.getString("jenis_kelamin"));
                txt_tempat_lahir.setText(result.getString("tempat_lahir"));
                Date_tgl_lahir.setDate(result.getDate("tanggal_lahir"));
                txt_alamat.setText(result.getString("alamat"));
                txt_desa.setText(result.getString("desa"));
                txt_kecamatan.setText(result.getString("kecamatan"));
                txt_kota_kabupaten.setText(result.getString("kota_kabupaten"));
                txt_provinsi.setText(result.getString("provinsi"));
                ComboBox_golDarah.setSelectedItem(result.getString("golongan_darah"));
                ComboBox_kawin.setSelectedItem(result.getString("status_kawin"));
                txt_nama_ibu.setText(result.getString("nama_ibu"));
                txt_no_telp.setText(result.getString("no_telp"));
                ComboBox_posisi.setSelectedItem(result.getString("posisi"));
                txt_kode_bagian.setText(result.getString("kode_bagian"));
                txt_bagian.setText(result.getString("nama_bagian"));
                ComboBox_pendidikan.setSelectedItem(result.getString("pendidikan"));
                txt_email.setText(result.getString("email"));
                Date_masuk.setDate(result.getDate("tanggal_masuk"));

                if (result.getInt("fc_ktp") == 1) {
                    Insert_DataKaryawan.check_ktp.setSelected(true);
                } else {
                    Insert_DataKaryawan.check_ktp.setSelected(false);
                }
                if (result.getInt("sertifikat_vaksin1") == 1) {
                    Insert_DataKaryawan.check_sertifikat_vaksin1.setSelected(true);
                } else {
                    Insert_DataKaryawan.check_sertifikat_vaksin1.setSelected(false);
                }
                if (result.getInt("sertifikat_vaksin2") == 1) {
                    Insert_DataKaryawan.check_sertifikat_vaksin2.setSelected(true);
                } else {
                    Insert_DataKaryawan.check_sertifikat_vaksin2.setSelected(false);
                }
                if (result.getInt("berkas_surat_pernyataan") == 1) {
                    Insert_DataKaryawan.check_surat_pernyataan.setSelected(true);
                } else {
                    Insert_DataKaryawan.check_surat_pernyataan.setSelected(false);
                }

                Insert_DataKaryawan.Date_tgl_surat.setDate(result.getDate("tanggal_surat"));
//        lbl_Image.setIcon(ResizeImage(null, list.get(0).getImage()));
            }
        } catch (Exception e) {
            Logger.getLogger(Insert_DataKaryawan.class.getName()).log(Level.SEVERE, null, e);
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

        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        Date_tgl_surat = new com.toedter.calendar.JDateChooser();
        check_surat_pernyataan = new javax.swing.JCheckBox();
        check_sertifikat_vaksin1 = new javax.swing.JCheckBox();
        check_ktp = new javax.swing.JCheckBox();
        jLabel19 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        check_sertifikat_vaksin2 = new javax.swing.JCheckBox();
        label_title_op_karyawan = new javax.swing.JLabel();
        label_ktp_location = new javax.swing.JLabel();
        button_browse_ktp = new javax.swing.JButton();
        lbl_Image = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txt_nik_ktp = new javax.swing.JTextField();
        txt_nama = new javax.swing.JTextField();
        txt_tempat_lahir = new javax.swing.JTextField();
        txt_kecamatan = new javax.swing.JTextField();
        Date_tgl_lahir = new com.toedter.calendar.JDateChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        txt_alamat = new javax.swing.JTextArea();
        jLabel11 = new javax.swing.JLabel();
        txt_desa = new javax.swing.JTextField();
        ComboBox_kawin = new javax.swing.JComboBox<>();
        ComboBox_Kelamin = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        txt_nama_ibu = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        txt_no_telp = new javax.swing.JTextField();
        ComboBox_pendidikan = new javax.swing.JComboBox<>();
        jLabel24 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txt_id_pegawai = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        ComboBox_posisi = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        Date_masuk = new com.toedter.calendar.JDateChooser();
        jLabel28 = new javax.swing.JLabel();
        txt_email = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        ComboBox_golDarah = new javax.swing.JComboBox<>();
        jLabel30 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txt_kota_kabupaten = new javax.swing.JTextField();
        txt_provinsi = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        txt_kode_bagian = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        txt_bagian = new javax.swing.JTextArea();
        button_pilih_bagian = new javax.swing.JButton();
        button_cancel = new javax.swing.JButton();
        button_proceed_insert = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Data Karyawan");

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 153, 153)), "Input Data Berkas Karyawan", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Yu Gothic UI Semibold", 0, 11), new java.awt.Color(255, 0, 0))); // NOI18N

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        jLabel26.setText("Tanggal Surat :");

        Date_tgl_surat.setBackground(new java.awt.Color(255, 255, 255));
        Date_tgl_surat.setDateFormatString("dd MMMM yyyy");

        check_surat_pernyataan.setBackground(new java.awt.Color(255, 255, 255));
        check_surat_pernyataan.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N

        check_sertifikat_vaksin1.setBackground(new java.awt.Color(255, 255, 255));
        check_sertifikat_vaksin1.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N

        check_ktp.setBackground(new java.awt.Color(255, 255, 255));
        check_ktp.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        jLabel19.setText("FC KTP :");

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        jLabel22.setText("Sertifikat Vaksin 1 :");

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        jLabel23.setText("Surat Pernyataan :");

        jLabel29.setBackground(new java.awt.Color(255, 255, 255));
        jLabel29.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        jLabel29.setText("Sertifikat Vaksin 2 :");

        check_sertifikat_vaksin2.setBackground(new java.awt.Color(255, 255, 255));
        check_sertifikat_vaksin2.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_tgl_surat, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(104, 104, 104)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(check_surat_pernyataan, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(check_sertifikat_vaksin1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(check_ktp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(check_sertifikat_vaksin2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(check_ktp))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(check_sertifikat_vaksin1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(check_sertifikat_vaksin2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(check_surat_pernyataan))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(Date_tgl_surat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        label_title_op_karyawan.setBackground(new java.awt.Color(255, 255, 255));
        label_title_op_karyawan.setFont(new java.awt.Font("MV Boli", 1, 18)); // NOI18N
        label_title_op_karyawan.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_title_op_karyawan.setText("Data Karyawan");

        label_ktp_location.setBackground(new java.awt.Color(255, 255, 255));
        label_ktp_location.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        label_ktp_location.setText("Image Location");
        label_ktp_location.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        label_ktp_location.setMaximumSize(new java.awt.Dimension(280, 15));

        button_browse_ktp.setBackground(new java.awt.Color(255, 255, 255));
        button_browse_ktp.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        button_browse_ktp.setText("Browse Scan KTP");
        button_browse_ktp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_browse_ktpActionPerformed(evt);
            }
        });

        lbl_Image.setBackground(new java.awt.Color(255, 255, 255));
        lbl_Image.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_Image.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 153, 153)), "Input Data Sesuai Ktp", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Yu Gothic UI Semibold", 0, 11), new java.awt.Color(255, 0, 0))); // NOI18N

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        jLabel9.setText("Kecamatan :");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        jLabel6.setText("Tanggal Lahir :");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        jLabel7.setText("Alamat :");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        jLabel5.setText("Tempat Lahir :");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        jLabel10.setText("Status Perkawinan :");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        jLabel2.setText("NIK KTP :");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        jLabel3.setText("Nama Lengkap :");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        jLabel4.setText("Jenis Kelamin :");

        txt_nik_ktp.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        txt_nik_ktp.setText("-");

        txt_nama.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N

        txt_tempat_lahir.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N

        txt_kecamatan.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N

        Date_tgl_lahir.setBackground(new java.awt.Color(255, 255, 255));
        Date_tgl_lahir.setDateFormatString("dd MMMM yyyy");
        Date_tgl_lahir.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_tgl_lahir.setMaxSelectableDate(new Date());

        jScrollPane1.setPreferredSize(new java.awt.Dimension(150, 60));

        txt_alamat.setColumns(20);
        txt_alamat.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        txt_alamat.setLineWrap(true);
        txt_alamat.setRows(3);
        txt_alamat.setTabSize(5);
        jScrollPane1.setViewportView(txt_alamat);

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        jLabel11.setText("Kel / Desa :");

        txt_desa.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N

        ComboBox_kawin.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        ComboBox_kawin.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "BELUM KAWIN", "KAWIN", "CERAI HIDUP", "CERAI MATI" }));

        ComboBox_Kelamin.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        ComboBox_Kelamin.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "LAKI-LAKI", "PEREMPUAN" }));

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        jLabel12.setText("Nama Ibu :");

        txt_nama_ibu.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N

        jLabel27.setBackground(new java.awt.Color(255, 255, 255));
        jLabel27.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        jLabel27.setText("No Telp :");

        txt_no_telp.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N

        ComboBox_pendidikan.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        ComboBox_pendidikan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-", "TK", "SD", "SMP", "SMA", "SMK", "D1", "D3", "S1", "S2" }));

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        jLabel24.setText("kode bagian :");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        jLabel13.setText("ID Pegawai :");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        jLabel14.setText("Posisi :");

        txt_id_pegawai.setEditable(false);
        txt_id_pegawai.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        jLabel25.setText("Pendidikan :");

        ComboBox_posisi.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        jLabel16.setText("Tanggal Masuk :");

        Date_masuk.setBackground(new java.awt.Color(255, 255, 255));
        Date_masuk.setDateFormatString("dd MMMM yyyy");
        Date_masuk.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        jLabel28.setText("E-mail :");

        txt_email.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Yu Gothic UI", 0, 8)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 0, 0));
        jLabel8.setText("*) Umur Harus diatas 18 Tahun");

        ComboBox_golDarah.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        ComboBox_golDarah.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-", "A", "A+", "B", "B+", "AB", "AB+", "O", "O+" }));

        jLabel30.setBackground(new java.awt.Color(255, 255, 255));
        jLabel30.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        jLabel30.setText("Golongan Darah :");

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        jLabel17.setText("Kota / Kabupaten :");

        txt_kota_kabupaten.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N

        txt_provinsi.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        jLabel18.setText("Provinsi :");

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        jLabel21.setText("Bagian :");

        txt_kode_bagian.setEditable(false);
        txt_kode_bagian.setBackground(new java.awt.Color(255, 255, 255));
        txt_kode_bagian.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N

        jScrollPane2.setPreferredSize(new java.awt.Dimension(150, 60));

        txt_bagian.setEditable(false);
        txt_bagian.setColumns(20);
        txt_bagian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_bagian.setLineWrap(true);
        txt_bagian.setRows(2);
        txt_bagian.setTabSize(5);
        jScrollPane2.setViewportView(txt_bagian);

        button_pilih_bagian.setBackground(new java.awt.Color(255, 255, 255));
        button_pilih_bagian.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        button_pilih_bagian.setText("Pilih Departemen dan bagian");
        button_pilih_bagian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_pilih_bagianActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_kecamatan)
                    .addComponent(txt_email)
                    .addComponent(txt_tempat_lahir)
                    .addComponent(txt_nama)
                    .addComponent(Date_masuk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ComboBox_posisi, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txt_nik_ktp)
                    .addComponent(txt_id_pegawai)
                    .addComponent(ComboBox_pendidikan, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txt_no_telp)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
                    .addComponent(txt_nama_ibu)
                    .addComponent(txt_provinsi)
                    .addComponent(ComboBox_Kelamin, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ComboBox_kawin, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txt_desa)
                    .addComponent(txt_kota_kabupaten)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
                    .addComponent(ComboBox_golDarah, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Date_tgl_lahir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txt_kode_bagian)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_pilih_bagian)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_nik_ktp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_nama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_Kelamin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_tempat_lahir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date_tgl_lahir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_desa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_kecamatan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_kota_kabupaten, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_provinsi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_golDarah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_kawin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_nama_ibu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_no_telp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_id_pegawai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_posisi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(txt_kode_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_pilih_bagian))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_pendidikan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_email, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(514, 514, 514)
                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        button_cancel.setBackground(new java.awt.Color(255, 255, 255));
        button_cancel.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        button_cancel.setText("Cancel");
        button_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_cancelActionPerformed(evt);
            }
        });

        button_proceed_insert.setBackground(new java.awt.Color(255, 255, 255));
        button_proceed_insert.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        button_proceed_insert.setText("Save");
        button_proceed_insert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_proceed_insertActionPerformed(evt);
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
                        .addComponent(button_cancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_proceed_insert))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_title_op_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(label_ktp_location, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(47, 47, 47)
                        .addComponent(button_browse_ktp))
                    .addComponent(lbl_Image, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(label_title_op_karyawan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_Image, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_ktp_location, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_browse_ktp)
                    .addComponent(button_proceed_insert)
                    .addComponent(button_cancel))
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

    private void button_proceed_insertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_proceed_insertActionPerformed
        if (null != label_title_op_karyawan.getText()) {
            switch (label_title_op_karyawan.getText()) {
                case "Tambah Data Karyawan":
                    try {
                        sql = "SELECT (DATEDIFF(CURRENT_DATE(), '" + dateFormat.format(Date_tgl_lahir.getDate()) + "')/365) AS 'umur'";
                        rs = Utility.db.getStatement().executeQuery(sql);
                        if (rs.next()) {
                            if (rs.getFloat("umur") < 18.f) {
                                JOptionPane.showMessageDialog(this, "Maaf anak di bawah 18 tahun tidak boleh masuk !");
                            } else {
                                insert();
                            }
                        }
                    } catch (SQLException e) {
                        Logger.getLogger(Insert_DataKaryawan.class.getName()).log(Level.SEVERE, null, e);
                    }
                    break;
                case "Edit Data Karyawan":
                    update();
                    break;
                default:
                    break;
            }
        }

//        JPanel_Data_Karyawan.button_edit_data_karyawan.setEnabled(true);
//        JPanel_Data_Karyawan.button_insert_karyawan.setEnabled(true);
    }//GEN-LAST:event_button_proceed_insertActionPerformed

    private void button_browse_ktpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_browse_ktpActionPerformed
        // TODO add your handling code here:
        JFileChooser file = new JFileChooser();
        file.setCurrentDirectory(new File(System.getProperty("user.home")));
        //filter files extension
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JPEG file", "jpg", "jpeg", "png");
        file.addChoosableFileFilter(filter);
        int result = file.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = file.getSelectedFile();
            String path = selectedFile.getAbsolutePath();
            lbl_Image.setIcon(Utility.ResizeImage(path, null, lbl_Image.getWidth(), lbl_Image.getHeight()));
            label_ktp_location.setText(path);
            imgPath = path;
        } else if (result == JFileChooser.CANCEL_OPTION) {
            System.out.println("NO FILE SELECTED !");
        }
    }//GEN-LAST:event_button_browse_ktpActionPerformed

    private void button_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_cancelActionPerformed
        // TODO add your handling code here:
//        JPanel_Data_Karyawan.button_edit_data_karyawan.setEnabled(true);
//        JPanel_Data_Karyawan.button_insert_karyawan.setEnabled(true);
        this.dispose();
    }//GEN-LAST:event_button_cancelActionPerformed

    private void button_pilih_bagianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_pilih_bagianActionPerformed
        // TODO add your handling code here:
        Browse_Bagian dialog = new Browse_Bagian(new javax.swing.JFrame(), true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);

        int x = Browse_Bagian.table_bagian.getSelectedRow();
        if (x != -1) {
            txt_kode_bagian.setText(Browse_Bagian.table_bagian.getValueAt(x, 0).toString());
            String posisi = "";
            if (Browse_Bagian.table_bagian.getValueAt(x, 1) != null) {
                posisi = Browse_Bagian.table_bagian.getValueAt(x, 1).toString();
            }
            String dept = "";
            if (Browse_Bagian.table_bagian.getValueAt(x, 2) != null) {
                dept = Browse_Bagian.table_bagian.getValueAt(x, 2).toString();
            }
            String divisi = "";
            if (Browse_Bagian.table_bagian.getValueAt(x, 3) != null) {
                divisi = Browse_Bagian.table_bagian.getValueAt(x, 3).toString();
            }
            String bagian = "";
            if (Browse_Bagian.table_bagian.getValueAt(x, 4) != null) {
                bagian = Browse_Bagian.table_bagian.getValueAt(x, 4).toString();
            }
            String ruang = "";
            if (Browse_Bagian.table_bagian.getValueAt(x, 5) != null) {
                ruang = Browse_Bagian.table_bagian.getValueAt(x, 5).toString();
            }
            txt_bagian.setText(posisi + "-" + dept + "-" + divisi + "-" + bagian + "-" + ruang);
        }
    }//GEN-LAST:event_button_pilih_bagianActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JComboBox<String> ComboBox_Kelamin;
    public static javax.swing.JComboBox<String> ComboBox_golDarah;
    public static javax.swing.JComboBox<String> ComboBox_kawin;
    public static javax.swing.JComboBox<String> ComboBox_pendidikan;
    public static javax.swing.JComboBox<String> ComboBox_posisi;
    public static com.toedter.calendar.JDateChooser Date_masuk;
    public static com.toedter.calendar.JDateChooser Date_tgl_lahir;
    public static com.toedter.calendar.JDateChooser Date_tgl_surat;
    private javax.swing.JButton button_browse_ktp;
    private javax.swing.JButton button_cancel;
    private javax.swing.JButton button_pilih_bagian;
    private javax.swing.JButton button_proceed_insert;
    public static javax.swing.JCheckBox check_ktp;
    public static javax.swing.JCheckBox check_sertifikat_vaksin1;
    public static javax.swing.JCheckBox check_sertifikat_vaksin2;
    public static javax.swing.JCheckBox check_surat_pernyataan;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
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
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    public static javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    public static javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel label_ktp_location;
    public javax.swing.JLabel label_title_op_karyawan;
    private javax.swing.JLabel lbl_Image;
    public static javax.swing.JTextArea txt_alamat;
    public static javax.swing.JTextArea txt_bagian;
    public static javax.swing.JTextField txt_desa;
    public static javax.swing.JTextField txt_email;
    public static javax.swing.JTextField txt_id_pegawai;
    public static javax.swing.JTextField txt_kecamatan;
    public static javax.swing.JTextField txt_kode_bagian;
    public static javax.swing.JTextField txt_kota_kabupaten;
    public static javax.swing.JTextField txt_nama;
    public static javax.swing.JTextField txt_nama_ibu;
    public static javax.swing.JTextField txt_nik_ktp;
    public static javax.swing.JTextField txt_no_telp;
    public static javax.swing.JTextField txt_provinsi;
    public static javax.swing.JTextField txt_tempat_lahir;
    // End of variables declaration//GEN-END:variables
}
