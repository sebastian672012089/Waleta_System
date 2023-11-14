/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package waleta_system.Maintenance.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.UUID;
import waleta_system.Class.Utility;
import waleta_system.Maintenance.model.Alat;
import waleta_system.Maintenance.model.KategoriAlat;
import waleta_system.Maintenance.model.Lantai;
import waleta_system.Maintenance.model.Ruangan;
import waleta_system.Maintenance.view.MainFrame_Maintenance;

/**
 *
 * @author User
 */
public class AlatDAO {

    public AlatDAO() {
    }

    public ArrayList<Alat> selectAll() throws Exception {
        ArrayList<Alat> alats;
        try {
            

            ResultSet resultSet;
            PreparedStatement preparedStatement;
            preparedStatement = Utility.db.getConnection().prepareStatement("SELECT tb1.id_mt_alat, tb1.id_mt_alat_kat, tb2.nama_alat_kat AS nama_kategori_alat, tb2.icon_healthy, tb2.icon_in_repair, tb2.icon_damaged, tb1.id_mt_ruangan, tb3.nama_ruangan, tb3.gambar, tb3.id_mt_lantai, tb4.nama_lantai, tb4.gambar, tb1.merk, tb1.spesifikasi, tb1.foto_alat, tb1.status_alat, tb1.posisi_x, tb1.posisi_y, tb1.rotasi, tb1.skala FROM tb_mt_alat AS tb1 LEFT JOIN tb_mt_alat_kat AS tb2 ON tb1.id_mt_alat_kat = tb2.id_mt_alat_kat LEFT JOIN tb_mt_ruangan AS tb3 ON tb1.id_mt_ruangan = tb3.id_mt_ruangan LEFT JOIN tb_mt_lantai AS tb4 ON tb3.id_mt_lantai = tb4.id_mt_lantai");
            resultSet = preparedStatement.executeQuery();
            alats = new ArrayList<>();
            while (resultSet.next()) {
                Alat alat = new Alat(resultSet.getString("id_mt_alat"), new KategoriAlat(resultSet.getInt("id_mt_alat_kat"), resultSet.getString("nama_kategori_alat"), resultSet.getString("icon_healthy"), resultSet.getString("icon_in_repair"), resultSet.getString("icon_damaged")), new Ruangan(resultSet.getString("id_mt_ruangan"), resultSet.getString("nama_ruangan"), new Lantai(resultSet.getString("id_mt_lantai"), resultSet.getString("nama_lantai"), resultSet.getString("gambar"))), resultSet.getString("merk"), resultSet.getString("spesifikasi"), resultSet.getString("foto_alat"), resultSet.getInt("status_alat"), resultSet.getInt("posisi_x"), resultSet.getInt("posisi_y"), resultSet.getFloat("rotasi"), resultSet.getInt("skala"));
                alats.add(alat);
            }
            return alats;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public Alat selectWhereIdAlat(String IdAlat) throws Exception {
        ArrayList<Alat> alats;
        try {
            

            ResultSet resultSet;
            PreparedStatement preparedStatement;
            preparedStatement = Utility.db.getConnection().prepareStatement("SELECT tb1.id_mt_alat, tb1.id_mt_alat_kat, tb2.nama_alat_kat AS nama_kategori_alat, tb2.icon_healthy, tb2.icon_in_repair, tb2.icon_damaged, tb1.id_mt_ruangan, tb3.nama_ruangan, tb3.id_mt_lantai, tb4.nama_lantai, tb4.gambar, tb1.merk, tb1.spesifikasi, tb1.foto_alat, tb1.status_alat, tb1.posisi_x, tb1.posisi_y, tb1.rotasi, tb1.skala FROM tb_mt_alat AS tb1 LEFT JOIN tb_mt_alat_kat AS tb2 ON tb1.id_mt_alat_kat = tb2.id_mt_alat_kat LEFT JOIN tb_mt_ruangan AS tb3 ON tb1.id_mt_ruangan = tb3.id_mt_ruangan LEFT JOIN tb_mt_lantai AS tb4 ON tb3.id_mt_lantai = tb4.id_mt_lantai WHERE tb1.id_mt_alat = ?");
            preparedStatement.setString(1, IdAlat);
            resultSet = preparedStatement.executeQuery();
            alats = new ArrayList<>();
            if (resultSet.next()) {
                return new Alat(resultSet.getString("id_mt_alat"), new KategoriAlat(resultSet.getInt("id_mt_alat_kat"), resultSet.getString("nama_kategori_alat"), resultSet.getString("icon_healthy"), resultSet.getString("icon_in_repair"), resultSet.getString("icon_damaged")), new Ruangan(resultSet.getString("id_mt_ruangan"), resultSet.getString("nama_ruangan"), new Lantai(resultSet.getString("id_mt_lantai"), resultSet.getString("nama_lantai"), resultSet.getString("gambar"))), resultSet.getString("merk"), resultSet.getString("spesifikasi"), resultSet.getString("foto_alat"), resultSet.getInt("status_alat"), resultSet.getInt("posisi_x"), resultSet.getInt("posisi_y"), resultSet.getFloat("rotasi"), resultSet.getInt("skala"));
            } else {
                return null;
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public ArrayList<Alat> selectWhereIdLantai(String idLantai) throws Exception {
        ArrayList<Alat> alats;
        try {
            

            ResultSet resultSet;
            PreparedStatement preparedStatement;
            preparedStatement = Utility.db.getConnection().prepareStatement("SELECT tb1.id_mt_alat, tb1.id_mt_alat_kat, tb2.nama_alat_kat AS nama_kategori_alat, tb2.icon_healthy, tb2.icon_in_repair, tb2.icon_damaged, tb1.id_mt_ruangan, tb3.nama_ruangan, tb3.gambar tb3.id_mt_lantai, tb4.nama_lantai, tb4.gambar, tb1.merk, tb1.spesifikasi, tb1.foto_alat, tb1.status_alat, tb1.posisi_x, tb1.posisi_y, tb1.rotasi, tb1.skala FROM tb_mt_alat AS tb1 LEFT JOIN tb_mt_alat_kat AS tb2 ON tb1.id_mt_alat_kat = tb2.id_mt_alat_kat LEFT JOIN tb_mt_ruangan AS tb3 ON tb1.id_mt_ruangan = tb3.id_mt_ruangan LEFT JOIN tb_mt_lantai AS tb4 ON tb3.id_mt_lantai = tb4.id_mt_lantai WHERE tb3.id_mt_lantai = ?");
            preparedStatement.setString(1, idLantai);
            resultSet = preparedStatement.executeQuery();
            alats = new ArrayList<>();
            while (resultSet.next()) {
                Alat alat = new Alat(resultSet.getString("id_mt_alat"), new KategoriAlat(resultSet.getInt("id_mt_alat_kat"), resultSet.getString("nama_kategori_alat"), resultSet.getString("icon_healthy"), resultSet.getString("icon_in_repair"), resultSet.getString("icon_damaged")), new Ruangan(resultSet.getString("id_mt_ruangan"), resultSet.getString("nama_ruangan"), new Lantai(resultSet.getString("id_mt_lantai"), resultSet.getString("nama_lantai"), resultSet.getString("gambar"))), resultSet.getString("merk"), resultSet.getString("spesifikasi"), resultSet.getString("foto_alat"), resultSet.getInt("status_alat"), resultSet.getInt("posisi_x"), resultSet.getInt("posisi_y"), resultSet.getFloat("rotasi"), resultSet.getInt("skala"));
                alats.add(alat);
            }
            return alats;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public ArrayList<Alat> selectWhereIdLantaiLike(String idLantai, String kataKunci) throws Exception {
        ArrayList<Alat> alats;
        try {
            

            ResultSet resultSet;
            PreparedStatement preparedStatement;
            String sql = "SELECT tb1.id_mt_alat, tb1.id_mt_alat_kat, tb2.nama_alat_kat AS nama_kategori_alat, tb2.icon_healthy, tb2.icon_in_repair, tb2.icon_damaged, tb1.id_mt_ruangan, tb3.nama_ruangan, tb3.id_mt_lantai, tb4.nama_lantai, tb4.gambar, tb1.merk, tb1.spesifikasi, tb1.foto_alat, tb1.status_alat, tb1.posisi_x, tb1.posisi_y, tb1.rotasi, tb1.skala FROM tb_mt_alat AS tb1 LEFT JOIN tb_mt_alat_kat AS tb2 ON tb1.id_mt_alat_kat = tb2.id_mt_alat_kat LEFT JOIN tb_mt_ruangan AS tb3 ON tb1.id_mt_ruangan = tb3.id_mt_ruangan LEFT JOIN tb_mt_lantai AS tb4 ON tb3.id_mt_lantai = tb4.id_mt_lantai WHERE tb3.id_mt_lantai = ? AND (tb1.id_mt_alat LIKE ? OR tb2.nama_alat_kat LIKE ? OR tb1.merk LIKE ? OR tb1.spesifikasi LIKE ?) ORDER BY id_mt_alat asc";
            preparedStatement = Utility.db.getConnection().prepareStatement(sql);
            preparedStatement.setString(1, idLantai);
            preparedStatement.setString(2, "%" + kataKunci + "%");
            preparedStatement.setString(3, "%" + kataKunci + "%");
            preparedStatement.setString(4, "%" + kataKunci + "%");
            preparedStatement.setString(5, "%" + kataKunci + "%");
            resultSet = preparedStatement.executeQuery();
            alats = new ArrayList<>();
            while (resultSet.next()) {
                Alat alat = new Alat(resultSet.getString("id_mt_alat"), new KategoriAlat(resultSet.getInt("id_mt_alat_kat"), resultSet.getString("nama_kategori_alat"), resultSet.getString("icon_healthy"), resultSet.getString("icon_in_repair"), resultSet.getString("icon_damaged")), new Ruangan(resultSet.getString("id_mt_ruangan"), resultSet.getString("nama_ruangan"), new Lantai(resultSet.getString("id_mt_lantai"), resultSet.getString("nama_lantai"), resultSet.getString("gambar"))), resultSet.getString("merk"), resultSet.getString("spesifikasi"), resultSet.getString("foto_alat"), resultSet.getInt("status_alat"), resultSet.getInt("posisi_x"), resultSet.getInt("posisi_y"), resultSet.getFloat("rotasi"), resultSet.getInt("skala"));
                alats.add(alat);
            }
            return alats;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public ArrayList<Alat> selectTemuanWhereIdLantaiLike(String idLantai, String kataKunci) throws Exception {
        ArrayList<Alat> alats;
        try {
            

            ResultSet resultSet;
            PreparedStatement preparedStatement;
            String sql = "SELECT tb1.id_mt_alat, tb1.id_mt_alat_kat, tb2.nama_alat_kat AS nama_kategori_alat, tb2.icon_healthy, tb2.icon_in_repair, tb2.icon_damaged, tb1.id_mt_ruangan, tb3.nama_ruangan, tb3.id_mt_lantai, tb4.nama_lantai, tb4.gambar, tb1.merk, tb1.spesifikasi, tb1.foto_alat, tb1.status_alat, tb1.posisi_x, tb1.posisi_y, tb1.rotasi, tb1.skala, tb5.tgl_lapor, tb5.keluhan, tb5.status as status_temuan FROM tb_mt_alat AS tb1 LEFT JOIN tb_mt_alat_kat AS tb2 ON tb1.id_mt_alat_kat = tb2.id_mt_alat_kat LEFT JOIN tb_mt_ruangan AS tb3 ON tb1.id_mt_ruangan = tb3.id_mt_ruangan LEFT JOIN tb_mt_lantai AS tb4 ON tb3.id_mt_lantai = tb4.id_mt_lantai LEFT JOIN tb_mt_temuan AS tb5 ON tb1.id_mt_alat = tb5.id_mt_alat WHERE tb3.id_mt_lantai = ? AND (tb5.status = 1 OR tb5.status = 2 OR tb5.status = 3 OR tb5.status = 4) AND (tb1.id_mt_alat LIKE ? OR tb2.nama_alat_kat LIKE ? OR tb1.merk LIKE ? OR tb1.spesifikasi LIKE ? OR tb5.tgl_lapor LIKE ? OR tb5.keluhan LIKE ?) ORDER BY id_mt_alat asc";
            preparedStatement = Utility.db.getConnection().prepareStatement(sql);
            preparedStatement.setString(1, idLantai);
            preparedStatement.setString(2, "%" + kataKunci + "%");
            preparedStatement.setString(3, "%" + kataKunci + "%");
            preparedStatement.setString(4, "%" + kataKunci + "%");
            preparedStatement.setString(5, "%" + kataKunci + "%");
            preparedStatement.setString(6, "%" + kataKunci + "%");
            preparedStatement.setString(7, "%" + kataKunci + "%");
            resultSet = preparedStatement.executeQuery();
            alats = new ArrayList<>();
            while (resultSet.next()) {
                Alat alat = new Alat(resultSet.getString("id_mt_alat"), new KategoriAlat(resultSet.getInt("id_mt_alat_kat"), resultSet.getString("nama_kategori_alat"), resultSet.getString("icon_healthy"), resultSet.getString("icon_in_repair"), resultSet.getString("icon_damaged")), new Ruangan(resultSet.getString("id_mt_ruangan"), resultSet.getString("nama_ruangan"), new Lantai(resultSet.getString("id_mt_lantai"), resultSet.getString("nama_lantai"), resultSet.getString("gambar"))), resultSet.getString("merk"), resultSet.getString("spesifikasi"), resultSet.getString("foto_alat"), resultSet.getInt("status_alat"), resultSet.getInt("posisi_x"), resultSet.getInt("posisi_y"), resultSet.getFloat("rotasi"), resultSet.getInt("skala"), new java.util.Date(resultSet.getTimestamp("tgl_lapor").getTime()), resultSet.getString("keluhan"), resultSet.getInt("status_temuan"));
                alats.add(alat);
            }
            return alats;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public ArrayList<Alat> selectWhereIdLantaiLikePagingSort(String idLantai, String kataKunci, int limit, int start, String sortBy, String sortType) throws Exception {
        ArrayList<Alat> alats;
        try {
            

            ResultSet resultSet;
            PreparedStatement preparedStatement;
            String sql = "SELECT tb1.id_mt_alat, tb1.id_mt_alat_kat, tb2.nama_alat_kat AS nama_kategori_alat, tb2.icon_healthy, tb2.icon_in_repair, tb2.icon_damaged, tb1.id_mt_ruangan, tb3.nama_ruangan, tb3.id_mt_lantai, tb4.nama_lantai, tb4.gambar, tb1.merk, tb1.spesifikasi, tb1.foto_alat, tb1.status_alat, tb1.posisi_x, tb1.posisi_y, tb1.rotasi, tb1.skala FROM tb_mt_alat AS tb1 LEFT JOIN tb_mt_alat_kat AS tb2 ON tb1.id_mt_alat_kat = tb2.id_mt_alat_kat LEFT JOIN tb_mt_ruangan AS tb3 ON tb1.id_mt_ruangan = tb3.id_mt_ruangan LEFT JOIN tb_mt_lantai AS tb4 ON tb3.id_mt_lantai = tb4.id_mt_lantai WHERE tb3.id_mt_lantai = ? AND (tb1.id_mt_alat LIKE ? OR tb2.nama_alat_kat LIKE ? OR tb1.merk LIKE ? OR tb1.spesifikasi LIKE ?) ORDER BY ? ? LIMIT ? OFFSET ?";
            preparedStatement = Utility.db.getConnection().prepareStatement(sql);
            preparedStatement.setString(1, idLantai);
            preparedStatement.setString(2, "%" + kataKunci + "%");
            preparedStatement.setString(3, "%" + kataKunci + "%");
            preparedStatement.setString(4, "%" + kataKunci + "%");
            preparedStatement.setString(5, "%" + kataKunci + "%");
            if (sortBy == null) {
                preparedStatement.setString(6, "id_mt_alat");
            } else {
                preparedStatement.setString(6, sortBy);
            }
            if (sortType == null) {
                preparedStatement.setString(7, "asc");
            } else {
                preparedStatement.setString(7, sortType);
            }
            preparedStatement.setInt(8, limit);
            preparedStatement.setInt(9, start);
            resultSet = preparedStatement.executeQuery();
            alats = new ArrayList<>();
            while (resultSet.next()) {
                Alat alat = new Alat(resultSet.getString("id_mt_alat"), new KategoriAlat(resultSet.getInt("id_mt_alat_kat"), resultSet.getString("nama_kategori_alat"), resultSet.getString("icon_healthy"), resultSet.getString("icon_in_repair"), resultSet.getString("icon_damaged")), new Ruangan(resultSet.getString("id_mt_ruangan"), resultSet.getString("nama_ruangan"), new Lantai(resultSet.getString("id_mt_lantai"), resultSet.getString("nama_lantai"), resultSet.getString("gambar"))), resultSet.getString("merk"), resultSet.getString("spesifikasi"), resultSet.getString("foto_alat"), resultSet.getInt("status_alat"), resultSet.getInt("posisi_x"), resultSet.getInt("posisi_y"), resultSet.getFloat("rotasi"), resultSet.getInt("skala"));
                alats.add(alat);
            }
            return alats;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public ArrayList<Alat> selectLike(String kataKunci) throws Exception {
        ArrayList<Alat> alats;
        try {
            

            ResultSet resultSet;
            PreparedStatement preparedStatement;
            preparedStatement = Utility.db.getConnection().prepareStatement("SELECT tb1.id_mt_alat, tb1.id_mt_alat_kat, tb2.nama_alat_kat AS nama_kategori_alat, tb2.icon_healthy, tb2.icon_in_repair, tb2.icon_damaged, tb1.id_mt_ruangan, tb3.nama_ruangan, tb3.id_mt_lantai, tb4.nama_lantai, tb4.gambar, tb1.id_mt_alat AS nama_alat, tb1.posisi_x, tb1.posisi_y, tb1.rotasi, tb1.skala, tb1.spesifikasi, tb1.foto_alat, tb1.status_alat FROM tb_mt_alat AS tb1 LEFT JOIN tb_mt_alat_kat AS tb2 ON tb1.id_mt_alat_kat = tb2.id_mt_alat_kat LEFT JOIN tb_mt_ruangan AS tb3 ON tb1.id_mt_ruangan = tb3.id_mt_ruangan LEFT JOIN tb_mt_lantai AS tb4 ON tb3.id_mt_lantai = tb4.id_mt_lantai WHERE tb2.nama_alat_kat LIKE ? OR tb3.nama_ruangan LIKE ? OR tb4.nama_lantai LIKE ? OR tb1.id_mt_alat LIKE ? OR tb1.posisi_x LIKE ? OR tb1.posisi_y LIKE ? OR tb1.rotasi LIKE ? OR tb1.skala LIKE ? OR tb1.spesifikasi LIKE ?");
            preparedStatement.setString(1, "%" + kataKunci + "%");
            preparedStatement.setString(2, "%" + kataKunci + "%");
            preparedStatement.setString(3, "%" + kataKunci + "%");
            preparedStatement.setString(4, "%" + kataKunci + "%");
            preparedStatement.setString(5, "%" + kataKunci + "%");
            preparedStatement.setString(6, "%" + kataKunci + "%");
            preparedStatement.setString(7, "%" + kataKunci + "%");
            preparedStatement.setString(8, "%" + kataKunci + "%");
            preparedStatement.setString(9, "%" + kataKunci + "%");
            resultSet = preparedStatement.executeQuery();
            alats = new ArrayList<>();
            while (resultSet.next()) {
                Alat alat = new Alat(resultSet.getString("id_mt_alat"), new KategoriAlat(resultSet.getInt("id_mt_alat_kat"), resultSet.getString("nama_kategori_alat"), resultSet.getString("icon_healthy"), resultSet.getString("icon_in_repair"), resultSet.getString("icon_damaged")), new Ruangan(resultSet.getString("id_mt_ruangan"), resultSet.getString("nama_ruangan"), new Lantai(resultSet.getString("id_mt_lantai"), resultSet.getString("nama_lantai"), resultSet.getString("gambar"))), resultSet.getString("merk"), resultSet.getString("spesifikasi"), resultSet.getString("foto_alat"), resultSet.getInt("status_alat"), resultSet.getInt("posisi_x"), resultSet.getInt("posisi_y"), resultSet.getFloat("rotasi"), resultSet.getInt("skala"));
                alats.add(alat);
            }
            return alats;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public boolean insert(MainFrame_Maintenance mainJFrame, Alat newAlat) throws Exception {
        try {
            
            Utility.db.getConnection().setAutoCommit(false);

            ResultSet resultSet;
            PreparedStatement preparedStatement;
            preparedStatement = Utility.db.getConnection().prepareStatement("SELECT MAX(kode_alat_urutan)+1 as kode_alat_urutan, COUNT(kode_alat_total)+1 AS kode_alat_total FROM tb_mt_alat WHERE kode_alat = ?");
            preparedStatement.setString(1, newAlat.getKodeAlat());
            resultSet = preparedStatement.executeQuery();
            int kode_alat_urutan = 1;
            int kode_alat_total = 1;
            if (resultSet.next()) {
                kode_alat_urutan = resultSet.getInt("kode_alat_urutan");
                kode_alat_total = resultSet.getInt("kode_alat_total");
            }
            String id_alat = "AL-" + newAlat.getKodeAlat() + kode_alat_urutan + "/" + kode_alat_total;

            preparedStatement = Utility.db.getConnection().prepareStatement("INSERT INTO tb_mt_alat(id_mt_alat, kode_alat, kode_alat_urutan, kode_alat_total, id_mt_alat_kat, id_mt_ruangan, merk, spesifikasi, status_alat, posisi_x, posisi_y, rotasi, skala) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            preparedStatement.setString(1, id_alat);
            preparedStatement.setString(2, newAlat.getKodeAlat());
            preparedStatement.setInt(3, kode_alat_urutan);
            preparedStatement.setInt(4, kode_alat_total);
            preparedStatement.setInt(5, newAlat.getKategoriAlat().getIdKategoriAlat());
            preparedStatement.setString(6, newAlat.getRuangan().getIdRuangan());
            preparedStatement.setString(7, newAlat.getMerk());
            preparedStatement.setString(8, newAlat.getSpesifikasi());
            preparedStatement.setInt(9, newAlat.getStatusAlat());
            preparedStatement.setInt(10, newAlat.getPosisiX());
            preparedStatement.setInt(11, newAlat.getPosisiY());
            preparedStatement.setFloat(12, newAlat.getRotasi());
            preparedStatement.setInt(13, newAlat.getSkala());
            int result = preparedStatement.executeUpdate();
            if (result > 0) {

                String pathFotoAlat = newAlat.getFotoAlat();
                int lastIndexOfSlash = Math.max(pathFotoAlat.lastIndexOf('/'), pathFotoAlat.lastIndexOf('\\'));
                if (lastIndexOfSlash < 0) {
                    pathFotoAlat = (PathHelper.path + "alat/" + pathFotoAlat).replace("file:/", "").replace("%20", " ");
                }
                int lastIndexOfDot = pathFotoAlat.lastIndexOf(".");
                String namaFotoAlatBaru;
                if (lastIndexOfDot == -1) {
                    throw new Exception("Pastikan foto alat berupa berkas gambar");
                } else {
                    namaFotoAlatBaru = id_alat.replace("/", "-") + "-" + UUID.randomUUID().toString().replace("-", "") + pathFotoAlat.substring(lastIndexOfDot);
                }

                File source = new File(pathFotoAlat);
                File dest = new File((PathHelper.path + "alat/" + namaFotoAlatBaru).replace("file:/", "").replace("%20", " "));

                if (!source.getAbsolutePath().equalsIgnoreCase(dest.getAbsolutePath())) {
                    InputStream is = null;
                    OutputStream os = null;
                    try {
                        is = new FileInputStream(source);
                        os = new FileOutputStream(dest);
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = is.read(buffer)) > 0) {
                            os.write(buffer, 0, length);
                        }
                    } finally {
                        if (is != null) {
                            is.close();
                        }
                        if (os != null) {
                            os.close();
                        }
                    }
                }

                preparedStatement = Utility.db.getConnection().prepareStatement("UPDATE tb_mt_alat SET foto_alat = ? WHERE id_mt_alat = ?");
                preparedStatement.setString(1, namaFotoAlatBaru);
                preparedStatement.setString(2, id_alat);
                result = preparedStatement.executeUpdate();

                if (result > -1) {
                    Utility.db.getConnection().commit();
                    return true;
                } else {
                    Utility.db.getConnection().rollback();
                    return false;
                }
            } else {
                Utility.db.getConnection().rollback();
                return false;
            }
        } catch (Exception ex) {
            if (!Utility.db.getConnection().isClosed()) {
                Utility.db.getConnection().rollback();
            }
            throw ex;
        }
    }

    public boolean update(MainFrame_Maintenance mainJFrame, String idAlatUpdate, Alat newAlat) throws Exception {
        try {
            
            Utility.db.getConnection().setAutoCommit(false);

            ResultSet resultSet;
            PreparedStatement preparedStatement;
            File fotoAlat = null;
            String oldPath = null;

            preparedStatement = Utility.db.getConnection().prepareStatement("SELECT foto_alat FROM tb_mt_alat WHERE id_mt_alat = ?");
            preparedStatement.setString(1, idAlatUpdate);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                fotoAlat = new File((PathHelper.path + "alat/" + resultSet.getString("foto_alat")).replace("file:/", "").replace("%20", " "));
                oldPath = resultSet.getString("foto_alat");
            }

            String pathFotoAlat = newAlat.getFotoAlat();
            int lastIndexOfSlash = Math.max(pathFotoAlat.lastIndexOf('/'), pathFotoAlat.lastIndexOf('\\'));
            if (lastIndexOfSlash < 0) {
                pathFotoAlat = (PathHelper.path + "alat/" + pathFotoAlat).replace("file:/", "").replace("%20", " ");
            }
            int lastIndexOfDot = pathFotoAlat.lastIndexOf(".");
            String namaFotoAlatBaru;
            if (lastIndexOfDot == -1) {
                throw new Exception("Pastikan foto alat berupa berkas gambar");
            } else {
                namaFotoAlatBaru = idAlatUpdate.replace("/", "-") + "-" + UUID.randomUUID().toString().replace("-", "") + pathFotoAlat.substring(lastIndexOfDot);
            }

            preparedStatement = Utility.db.getConnection().prepareStatement("UPDATE tb_mt_alat SET id_mt_alat = ?, id_mt_alat_kat = ?, id_mt_ruangan = ?, merk = ?, spesifikasi = ?, foto_alat = ?, status_alat = ?, posisi_x = ?, posisi_y = ?, rotasi = ?, skala = ? WHERE id_mt_alat = ?");
            preparedStatement.setString(1, newAlat.getIdAlat());
            preparedStatement.setInt(2, newAlat.getKategoriAlat().getIdKategoriAlat());
            preparedStatement.setString(3, newAlat.getRuangan().getIdRuangan());
            preparedStatement.setString(4, newAlat.getMerk());
            preparedStatement.setString(5, newAlat.getSpesifikasi());
            preparedStatement.setString(6, namaFotoAlatBaru);
            preparedStatement.setInt(7, newAlat.getStatusAlat());
            preparedStatement.setInt(8, newAlat.getPosisiX());
            preparedStatement.setInt(9, newAlat.getPosisiY());
            preparedStatement.setFloat(10, newAlat.getRotasi());
            preparedStatement.setInt(11, newAlat.getSkala());
            preparedStatement.setString(12, idAlatUpdate);
            int result = preparedStatement.executeUpdate();
            if (result > -1) {

                File source = new File(pathFotoAlat);
                File dest = new File((PathHelper.path + "alat/" + namaFotoAlatBaru).replace("file:/", "").replace("%20", " "));

                if (!source.getAbsolutePath().equalsIgnoreCase(dest.getAbsolutePath())) {
                    InputStream is = null;
                    OutputStream os = null;
                    try {
                        is = new FileInputStream(source);
                        os = new FileOutputStream(dest);
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = is.read(buffer)) > 0) {
                            os.write(buffer, 0, length);
                        }
                    } finally {
                        if (is != null) {
                            is.close();
                        }
                        if (os != null) {
                            os.close();
                        }
                    }
                    if (oldPath != null) {
                        if (!oldPath.equalsIgnoreCase(namaFotoAlatBaru)) {
                            if (fotoAlat != null) {
                                fotoAlat.delete();
                            }
                        }
                    }
                }

                Utility.db.getConnection().commit();
                return true;
            } else {
                Utility.db.getConnection().rollback();
                return false;
            }
        } catch (Exception ex) {
            if (!Utility.db.getConnection().isClosed()) {
                Utility.db.getConnection().rollback();
            }
            throw ex;
        }
    }

    public boolean delete(MainFrame_Maintenance mainJFrame, String idAlatDelete) throws Exception {
        try {
            
            Utility.db.getConnection().setAutoCommit(false);

            ResultSet resultSet;
            PreparedStatement preparedStatement;
            File fotoAlat = null;

            preparedStatement = Utility.db.getConnection().prepareStatement("SELECT foto_alat FROM tb_mt_alat WHERE id_mt_alat = ?");
            preparedStatement.setString(1, idAlatDelete);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                fotoAlat = new File((PathHelper.path + "alat/" + resultSet.getString("foto_alat")).replace("file:/", "").replace("%20", " "));
            }

            preparedStatement = Utility.db.getConnection().prepareStatement("DELETE FROM tb_mt_alat WHERE id_mt_alat = ?");
            preparedStatement.setString(1, idAlatDelete);
            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                if (fotoAlat != null) {
                    fotoAlat.delete();
                }
                Utility.db.getConnection().commit();
                return true;
            } else {
                Utility.db.getConnection().rollback();
                return false;
            }
        } catch (Exception ex) {
            if (!Utility.db.getConnection().isClosed()) {
                Utility.db.getConnection().rollback();
            }
            throw ex;
        }
    }

    public boolean deleteSelected(MainFrame_Maintenance mainJFrame, ArrayList<String> idAlatDelete) throws Exception {
        try {
            
            Utility.db.getConnection().setAutoCommit(false);

            ResultSet resultSet;
            PreparedStatement preparedStatement;
            ArrayList<File> fotoAlat = new ArrayList<>();

            boolean sukses = true;

            for (int i = 0; i < idAlatDelete.size(); i++) {
                File newFotoAlat = null;

                preparedStatement = Utility.db.getConnection().prepareStatement("SELECT foto_alat FROM tb_mt_alat WHERE id_mt_alat = ?");
                preparedStatement.setString(1, idAlatDelete.get(i));
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    newFotoAlat = new File((PathHelper.path + "alat/" + resultSet.getString("foto_alat")).replace("file:/", "").replace("%20", " "));
                }
                fotoAlat.add(newFotoAlat);

                preparedStatement = Utility.db.getConnection().prepareStatement("DELETE FROM tb_mt_alat WHERE id_mt_alat = ?");
                preparedStatement.setString(1, idAlatDelete.get(i));
                int result = preparedStatement.executeUpdate();
                if (result <= 0) {
                    sukses = false;
                    break;
                }
            }

            if (sukses) {
                for (int i = 0; i < fotoAlat.size(); i++) {
                    if (fotoAlat.get(i) != null) {
                        fotoAlat.get(i).delete();
                    }
                }
                Utility.db.getConnection().commit();
                return true;
            } else {
                Utility.db.getConnection().rollback();
                return false;
            }
        } catch (Exception ex) {
            if (!Utility.db.getConnection().isClosed()) {
                Utility.db.getConnection().rollback();
            }
            throw ex;
        }
    }
}
