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
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.UUID;
import waleta_system.Class.Utility;
import waleta_system.Maintenance.model.KategoriAlat;
import waleta_system.Maintenance.view.MainFrame_Maintenance;

/**
 *
 * @author User
 */
public class KategoriAlatDAO {


    public KategoriAlatDAO() {
    }

    public ArrayList<KategoriAlat> selectAll() throws Exception {
        ArrayList<KategoriAlat> kategoriAlats;
        try {
            

            ResultSet resultSet;
            PreparedStatement preparedStatement;
            preparedStatement = Utility.db.getConnection().prepareStatement("SELECT id_mt_alat_kat, nama_alat_kat, icon_healthy, icon_in_repair, icon_damaged FROM tb_mt_alat_kat");
            resultSet = preparedStatement.executeQuery();
            kategoriAlats = new ArrayList<>();
            while (resultSet.next()) {
                KategoriAlat kategoriAlat = new KategoriAlat(resultSet.getInt("id_mt_alat_kat"), resultSet.getString("nama_alat_kat"), resultSet.getString("icon_healthy"), resultSet.getString("icon_in_repair"), resultSet.getString("icon_damaged"));
                kategoriAlats.add(kategoriAlat);
            }
            return kategoriAlats;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public ArrayList<KategoriAlat> selectLikePagingSort(String kataKunci, int limit, int start, String sortBy, String sortType) throws Exception {
        ArrayList<KategoriAlat> kategoriAlats;
        try {
            

            ResultSet resultSet;
            PreparedStatement preparedStatement;
            String sql = "SELECT id_mt_alat_kat, nama_alat_kat, icon_healthy, icon_in_repair, icon_damaged FROM tb_mt_alat_kat WHERE nama_alat_kat LIKE ? OR icon_healthy LIKE ? OR icon_in_repair LIKE ? OR icon_damaged LIKE ? ORDER BY ? ? LIMIT ? OFFSET ?";
            preparedStatement = Utility.db.getConnection().prepareStatement(sql);
            preparedStatement.setString(1, "%" + kataKunci + "%");
            preparedStatement.setString(2, "%" + kataKunci + "%");
            preparedStatement.setString(3, "%" + kataKunci + "%");
            preparedStatement.setString(4, "%" + kataKunci + "%");
            if (sortBy == null) {
                preparedStatement.setString(5, "nama_ruangan");
            } else {
                preparedStatement.setString(5, sortBy);
            }
            if (sortType == null) {
                preparedStatement.setString(6, "asc");
            } else {
                preparedStatement.setString(6, sortType);
            }
            preparedStatement.setInt(7, limit);
            preparedStatement.setInt(8, start);
            resultSet = preparedStatement.executeQuery();
            kategoriAlats = new ArrayList<>();
            while (resultSet.next()) {
                KategoriAlat kategoriAlat = new KategoriAlat(resultSet.getInt("id_mt_alat_kat"), resultSet.getString("nama_alat_kat"), resultSet.getString("icon_healthy"), resultSet.getString("icon_in_repair"), resultSet.getString("icon_damaged"));
                kategoriAlats.add(kategoriAlat);
            }
            return kategoriAlats;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public ArrayList<KategoriAlat> selectLike(String kataKunci) throws Exception {
        ArrayList<KategoriAlat> kategoriAlats;
        try {
            

            ResultSet resultSet;
            PreparedStatement preparedStatement;
            preparedStatement = Utility.db.getConnection().prepareStatement("SELECT id_mt_alat_kat, nama_alat_kat, icon_healthy, icon_in_repair, icon_damaged FROM tb_mt_alat_kat WHERE nama_alat_kat LIKE ? OR icon_healthy LIKE ? OR icon_in_repair LIKE ? OR icon_damaged LIKE ?");
            preparedStatement.setString(1, "%" + kataKunci + "%");
            preparedStatement.setString(2, "%" + kataKunci + "%");
            preparedStatement.setString(3, "%" + kataKunci + "%");
            preparedStatement.setString(4, "%" + kataKunci + "%");
            resultSet = preparedStatement.executeQuery();
            kategoriAlats = new ArrayList<>();
            while (resultSet.next()) {
                KategoriAlat kategoriAlat = new KategoriAlat(resultSet.getInt("id_mt_alat_kat"), resultSet.getString("nama_alat_kat"), resultSet.getString("icon_healthy"), resultSet.getString("icon_in_repair"), resultSet.getString("icon_damaged"));
                kategoriAlats.add(kategoriAlat);
            }
            return kategoriAlats;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public int countLike(String kataKunci) throws Exception {
        try {
            

            ResultSet resultSet;
            PreparedStatement preparedStatement;
            String sql = "SELECT COUNT(id_mt_alat_kat) as total FROM tb_mt_alat_kat WHERE nama_alat_kat LIKE ? OR icon_healthy LIKE ? OR icon_in_repair LIKE ? OR icon_damaged LIKE ?";
            preparedStatement = Utility.db.getConnection().prepareStatement(sql);
            preparedStatement.setString(1, "%" + kataKunci + "%");
            preparedStatement.setString(2, "%" + kataKunci + "%");
            preparedStatement.setString(3, "%" + kataKunci + "%");
            preparedStatement.setString(4, "%" + kataKunci + "%");
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("total");
            } else {
                return 0;
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public boolean insert(MainFrame_Maintenance mainJFrame, KategoriAlat newKategoriAlat) throws Exception {
        try {
            
            Utility.db.getConnection().setAutoCommit(false);

            PreparedStatement preparedStatement;
            preparedStatement = Utility.db.getConnection().prepareStatement("INSERT INTO tb_mt_alat_kat(id_mt_alat_kat, nama_alat_kat, icon_healthy, icon_in_repair, icon_damaged) VALUES (NULL, ?, ?, NULL, NULL, NULL)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, newKategoriAlat.getNamaAlat());
            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    int lastInsertedID = resultSet.getInt(1);

                    String pathIconHealthy = newKategoriAlat.getIconHealthy();
                    int lastIndexOfSlash = Math.max(pathIconHealthy.lastIndexOf('/'), pathIconHealthy.lastIndexOf('\\'));
                    if (lastIndexOfSlash < 0) {
                        pathIconHealthy = (PathHelper.path + "kategori_alat/icon_healthy/" + pathIconHealthy).replace("file:/", "").replace("%20", " ");
                    }
                    int lastIndexOfDot = pathIconHealthy.lastIndexOf(".");
                    String namaIconHealthyBaru;
                    if (lastIndexOfDot == -1) {
                        throw new Exception("Pastikan icon healthy berupa berkas gambar");
                    } else {
                        namaIconHealthyBaru = lastInsertedID + "-" + UUID.randomUUID().toString().replace("-", "") + pathIconHealthy.substring(lastIndexOfDot);
                    }

                    File source = new File(pathIconHealthy);
                    File dest = new File((PathHelper.path + "kategori_alat/icon_healthy/" + namaIconHealthyBaru).replace("file:/", "").replace("%20", " "));

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

                    String pathIconInRepair = newKategoriAlat.getIconInRepair();
                    lastIndexOfSlash = Math.max(pathIconInRepair.lastIndexOf('/'), pathIconInRepair.lastIndexOf('\\'));
                    if (lastIndexOfSlash < 0) {
                        pathIconInRepair = (PathHelper.path + "kategori_alat/icon_in_repair/" + pathIconInRepair).replace("file:/", "").replace("%20", " ");
                    }
                    lastIndexOfDot = pathIconInRepair.lastIndexOf(".");
                    String namaIconInRepairBaru;
                    if (lastIndexOfDot == -1) {
                        throw new Exception("Pastikan icon in repair berupa berkas gambar");
                    } else {
                        namaIconInRepairBaru = lastInsertedID + "-" + UUID.randomUUID().toString().replace("-", "") + pathIconInRepair.substring(lastIndexOfDot);
                    }

                    source = new File(pathIconInRepair);
                    dest = new File((PathHelper.path + "kategori_alat/icon_in_repair/" + namaIconInRepairBaru).replace("file:/", "").replace("%20", " "));

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

                    String pathIconDamaged = newKategoriAlat.getIconDamaged();
                    lastIndexOfSlash = Math.max(pathIconDamaged.lastIndexOf('/'), pathIconDamaged.lastIndexOf('\\'));
                    if (lastIndexOfSlash < 0) {
                        pathIconDamaged = (PathHelper.path + "kategori_alat/icon_damaged/" + pathIconDamaged).replace("file:/", "").replace("%20", " ");
                    }
                    lastIndexOfDot = pathIconDamaged.lastIndexOf(".");
                    String namaIconDamagedBaru;
                    if (lastIndexOfDot == -1) {
                        throw new Exception("Pastikan icon damaged berupa berkas gambar");
                    } else {
                        namaIconDamagedBaru = lastInsertedID + "-" + UUID.randomUUID().toString().replace("-", "") + pathIconDamaged.substring(lastIndexOfDot);
                    }

                    source = new File(pathIconDamaged);
                    dest = new File((PathHelper.path + "kategori_alat/icon_damaged/" + namaIconDamagedBaru).replace("file:/", "").replace("%20", " "));

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

                    preparedStatement = Utility.db.getConnection().prepareStatement("UPDATE tb_mt_alat_kat SET icon_healthy = ?, icon_in_repair = ?, icon_damaged = ? WHERE id_mt_alat_kat = ?");
                    preparedStatement.setString(1, namaIconHealthyBaru);
                    preparedStatement.setString(2, namaIconInRepairBaru);
                    preparedStatement.setString(3, namaIconDamagedBaru);
                    preparedStatement.setInt(4, lastInsertedID);
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
                    throw new Exception("Gagal menambahkan data Kategori Alat ke dalam basis data");
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

    public boolean update(MainFrame_Maintenance mainJFrame, int idKategoriAlatUpdate, KategoriAlat newKategoriAlat) throws Exception {
        try {
            
            Utility.db.getConnection().setAutoCommit(false);

            ResultSet resultSet;
            PreparedStatement preparedStatement;
            String oldPathIconHealthy = null;
            String oldPathIconInRepair = null;
            String oldPathIconDamaged = null;
            File iconHealthy = null;
            File iconInRepair = null;
            File iconDamaged = null;

            preparedStatement = Utility.db.getConnection().prepareStatement("SELECT icon_healthy, icon_in_repair, icon_damaged FROM tb_mt_alat_kat WHERE id_mt_alat_kat = ?");
            preparedStatement.setInt(1, idKategoriAlatUpdate);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                iconHealthy = new File((PathHelper.path + "kategori_alat/icon_healthy/" + resultSet.getString("icon_healthy")).replace("file:/", "").replace("%20", " "));
                iconInRepair = new File((PathHelper.path + "kategori_alat/icon_in_repair/" + resultSet.getString("icon_in_repair")).replace("file:/", "").replace("%20", " "));
                iconDamaged = new File((PathHelper.path + "kategori_alat/icon_damaged/" + resultSet.getString("icon_damaged")).replace("file:/", "").replace("%20", " "));

                oldPathIconHealthy = resultSet.getString("icon_healthy");
                oldPathIconInRepair = resultSet.getString("icon_in_repair");
                oldPathIconDamaged = resultSet.getString("icon_damaged");
            }
            String pathIconHealthy = newKategoriAlat.getIconHealthy();
            int lastIndexOfSlash = Math.max(pathIconHealthy.lastIndexOf('/'), pathIconHealthy.lastIndexOf('\\'));
            if (lastIndexOfSlash < 0) {
                pathIconHealthy = (PathHelper.path + "kategori_alat/icon_healthy/" + pathIconHealthy).replace("file:/", "").replace("%20", " ");
            }
            int lastIndexOfDot = pathIconHealthy.lastIndexOf(".");
            String namaIconHealthyBaru;
            if (lastIndexOfDot == -1) {
                throw new Exception("Pastikan icon healthy berupa berkas gambar");
            } else {
                namaIconHealthyBaru = idKategoriAlatUpdate + "-" + UUID.randomUUID().toString().replace("-", "") + pathIconHealthy.substring(lastIndexOfDot);
            }

            String pathIconInRepair = newKategoriAlat.getIconInRepair();
            lastIndexOfSlash = Math.max(pathIconInRepair.lastIndexOf('/'), pathIconInRepair.lastIndexOf('\\'));
            if (lastIndexOfSlash < 0) {
                pathIconInRepair = (PathHelper.path + "kategori_alat/icon_in_repair/" + pathIconInRepair).replace("file:/", "").replace("%20", " ");
            }
            lastIndexOfDot = pathIconInRepair.lastIndexOf(".");
            String namaIconInRepairBaru;
            if (lastIndexOfDot == -1) {
                throw new Exception("Pastikan icon in repair berupa berkas gambar");
            } else {
                namaIconInRepairBaru = idKategoriAlatUpdate + "-" + UUID.randomUUID().toString().replace("-", "") + pathIconInRepair.substring(lastIndexOfDot);
            }

            String pathIconDamaged = newKategoriAlat.getIconDamaged();
            lastIndexOfSlash = Math.max(pathIconDamaged.lastIndexOf('/'), pathIconDamaged.lastIndexOf('\\'));
            if (lastIndexOfSlash < 0) {
                pathIconDamaged = (PathHelper.path + "kategori_alat/icon_damaged/" + pathIconDamaged).replace("file:/", "").replace("%20", " ");
            }
            lastIndexOfDot = pathIconDamaged.lastIndexOf(".");
            String namaIconDamagedBaru;
            if (lastIndexOfDot == -1) {
                throw new Exception("Pastikan icon damaged berupa berkas gambar");
            } else {
                namaIconDamagedBaru = idKategoriAlatUpdate + "-" + UUID.randomUUID().toString().replace("-", "") + pathIconDamaged.substring(lastIndexOfDot);
            }

            preparedStatement = Utility.db.getConnection().prepareStatement("UPDATE tb_mt_alat_kat SET nama_alat_kat = ?, icon_healthy = ?, icon_in_repair = ?, icon_damaged = ? WHERE id_mt_alat_kat = ?");
            preparedStatement.setString(1, newKategoriAlat.getNamaAlat());
            preparedStatement.setString(2, namaIconHealthyBaru);
            preparedStatement.setString(3, namaIconInRepairBaru);
            preparedStatement.setString(4, namaIconDamagedBaru);
            preparedStatement.setInt(5, idKategoriAlatUpdate);
            int result = preparedStatement.executeUpdate();
            if (result > -1) {

                File source = new File(pathIconHealthy);
                File dest = new File((PathHelper.path + "kategori_alat/icon_healthy/" + namaIconHealthyBaru).replace("file:/", "").replace("%20", " "));

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
                    if (oldPathIconHealthy != null) {
                        if (!oldPathIconHealthy.equalsIgnoreCase(namaIconHealthyBaru)) {
                            if (iconHealthy != null) {
                                iconHealthy.delete();
                            }
                        }
                    }
                }

                source = new File(pathIconInRepair);
                dest = new File((PathHelper.path + "kategori_alat/icon_in_repair/" + namaIconInRepairBaru).replace("file:/", "").replace("%20", " "));

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
                    if (oldPathIconInRepair != null) {
                        if (!oldPathIconInRepair.equalsIgnoreCase(namaIconInRepairBaru)) {
                            if (iconInRepair != null) {
                                iconInRepair.delete();
                            }
                        }
                    }
                }

                source = new File(pathIconDamaged);
                dest = new File((PathHelper.path + "kategori_alat/icon_damaged/" + namaIconDamagedBaru).replace("file:/", "").replace("%20", " "));

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
                    if (oldPathIconDamaged != null) {
                        if (!oldPathIconDamaged.equalsIgnoreCase(namaIconDamagedBaru)) {
                            if (iconDamaged != null) {
                                iconDamaged.delete();
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

    public boolean delete(MainFrame_Maintenance mainJFrame, int idKategoriAlatDelete) throws Exception {
        try {
            
            Utility.db.getConnection().setAutoCommit(false);

            ResultSet resultSet;
            PreparedStatement preparedStatement;
            File iconHealthy = null;
            File iconInRepair = null;
            File iconDamaged = null;

            preparedStatement = Utility.db.getConnection().prepareStatement("SELECT icon_healthy, icon_in_repair, icon_damaged FROM tb_mt_alat_kat WHERE id_mt_alat_kat = ?");
            preparedStatement.setInt(1, idKategoriAlatDelete);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                iconHealthy = new File((PathHelper.path + "kategori_alat/icon_healthy/" + resultSet.getString("icon_healthy")).replace("file:/", "").replace("%20", " "));
                iconInRepair = new File((PathHelper.path + "kategori_alat/icon_in_repair/" + resultSet.getString("icon_in_repair")).replace("file:/", "").replace("%20", " "));
                iconDamaged = new File((PathHelper.path + "kategori_alat/icon_damaged/" + resultSet.getString("icon_damaged")).replace("file:/", "").replace("%20", " "));
            }

            preparedStatement = Utility.db.getConnection().prepareStatement("DELETE FROM tb_mt_alat_kat WHERE id_mt_alat_kat = ?");
            preparedStatement.setInt(1, idKategoriAlatDelete);
            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                if (iconHealthy != null) {
                    iconHealthy.delete();
                }
                if (iconInRepair != null) {
                    iconInRepair.delete();
                }
                if (iconDamaged != null) {
                    iconDamaged.delete();
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

    public boolean deleteSelected(MainFrame_Maintenance mainJFrame, ArrayList<Integer> idKategoriAlatDelete) throws Exception {
        try {
            
            Utility.db.getConnection().setAutoCommit(false);

            ResultSet resultSet;
            PreparedStatement preparedStatement;
            ArrayList<File> iconHealthy = new ArrayList<>();
            ArrayList<File> iconInRepair = new ArrayList<>();
            ArrayList<File> iconDamaged = new ArrayList<>();

            boolean sukses = true;

            for (int i = 0; i < idKategoriAlatDelete.size(); i++) {
                File newIconHealthy = null;
                File newIconInRepair = null;
                File newIconDamaged = null;

                preparedStatement = Utility.db.getConnection().prepareStatement("SELECT icon_healthy, icon_in_repair, icon_damaged FROM tb_mt_alat_kat WHERE id_mt_alat_kat = ?");
                preparedStatement.setInt(1, idKategoriAlatDelete.get(i));
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    newIconHealthy = new File((PathHelper.path + "kategori_alat/icon_healthy/" + resultSet.getString("icon_healthy")).replace("file:/", "").replace("%20", " "));
                    newIconInRepair = new File((PathHelper.path + "kategori_alat/icon_in_repair/" + resultSet.getString("icon_in_repair")).replace("file:/", "").replace("%20", " "));
                    newIconDamaged = new File((PathHelper.path + "kategori_alat/icon_damaged/" + resultSet.getString("icon_damaged")).replace("file:/", "").replace("%20", " "));
                }
                iconHealthy.add(newIconHealthy);
                iconInRepair.add(newIconInRepair);
                iconDamaged.add(newIconDamaged);

                preparedStatement = Utility.db.getConnection().prepareStatement("DELETE FROM tb_mt_alat_kat WHERE id_mt_alat_kat = ?");
                preparedStatement.setInt(1, idKategoriAlatDelete.get(i));
                int result = preparedStatement.executeUpdate();
                if (result <= 0) {
                    sukses = false;
                    break;
                }
            }
            if (sukses) {
                for (int i = 0; i < iconHealthy.size(); i++) {
                    if (iconHealthy.get(i) != null) {
                        iconHealthy.get(i).delete();
                    }
                }
                for (int i = 0; i < iconInRepair.size(); i++) {
                    if (iconInRepair.get(i) != null) {
                        iconInRepair.get(i).delete();
                    }
                }
                for (int i = 0; i < iconDamaged.size(); i++) {
                    if (iconDamaged.get(i) != null) {
                        iconDamaged.get(i).delete();
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
