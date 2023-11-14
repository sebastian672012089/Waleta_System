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
import waleta_system.Maintenance.model.Lantai;
import waleta_system.Maintenance.view.MainFrame_Maintenance;

/**
 *
 * @author User
 */
public class LantaiDAO {

    public LantaiDAO() {
    }

    public ArrayList<Lantai> selectAll() throws Exception {
        ArrayList<Lantai> lantais;
        try {
            

            ResultSet resultSet;
            PreparedStatement preparedStatement;
            preparedStatement = Utility.db.getConnection().prepareStatement("SELECT id_mt_lantai, nama_lantai, gambar FROM tb_mt_lantai");
            resultSet = preparedStatement.executeQuery();
            lantais = new ArrayList<>();
            while (resultSet.next()) {
                Lantai lantai = new Lantai(resultSet.getString("id_mt_lantai"), resultSet.getString("nama_lantai"), resultSet.getString("gambar"));
                lantais.add(lantai);
            }
            return lantais;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public ArrayList<Lantai> selectLikePagingSort(String kataKunci, int limit, int start, String sortBy, String sortType) throws Exception {
        ArrayList<Lantai> lantais;
        try {
            

            ResultSet resultSet;
            PreparedStatement preparedStatement;
            String sql = "SELECT id_mt_lantai, nama_lantai, gambar FROM tb_mt_lantai WHERE nama_lantai LIKE ? OR gambar LIKE ? ORDER BY ? ? LIMIT ? OFFSET ?";
            preparedStatement = Utility.db.getConnection().prepareStatement(sql);
            preparedStatement.setString(1, "%" + kataKunci + "%");
            preparedStatement.setString(2, "%" + kataKunci + "%");
            if (sortBy == null) {
                preparedStatement.setString(3, "nama_lantai");
            } else {
                preparedStatement.setString(3, sortBy);
            }
            if (sortType == null) {
                preparedStatement.setString(4, "asc");
            } else {
                preparedStatement.setString(4, sortType);
            }
            preparedStatement.setInt(5, limit);
            preparedStatement.setInt(6, start);
            resultSet = preparedStatement.executeQuery();
            lantais = new ArrayList<>();
            while (resultSet.next()) {
                Lantai lantai = new Lantai(resultSet.getString("id_mt_lantai"), resultSet.getString("nama_lantai"), resultSet.getString("gambar"));
                lantais.add(lantai);
            }
            return lantais;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public ArrayList<Lantai> selectLikeSort(String kataKunci, String sortBy, String sortType) throws Exception {
        ArrayList<Lantai> lantais;
        try {
            

            ResultSet resultSet;
            PreparedStatement preparedStatement;
            String sql = "SELECT id_mt_lantai, nama_lantai, gambar FROM tb_mt_lantai WHERE nama_lantai LIKE ? OR gambar LIKE ? ORDER BY ? ?";
            preparedStatement = Utility.db.getConnection().prepareStatement(sql);
            preparedStatement.setString(1, "%" + kataKunci + "%");
            preparedStatement.setString(2, "%" + kataKunci + "%");
            if (sortBy == null) {
                preparedStatement.setString(3, "nama_lantai");
            } else {
                preparedStatement.setString(3, sortBy);
            }
            if (sortType == null) {
                preparedStatement.setString(4, "asc");
            } else {
                preparedStatement.setString(4, sortType);
            }
            resultSet = preparedStatement.executeQuery();
            lantais = new ArrayList<>();
            while (resultSet.next()) {
                Lantai lantai = new Lantai(resultSet.getString("id_mt_lantai"), resultSet.getString("nama_lantai"), resultSet.getString("gambar"));
                lantais.add(lantai);
            }
            return lantais;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public ArrayList<Lantai> selectLike(String kataKunci) throws Exception {
        ArrayList<Lantai> lantais;
        try {
            

            ResultSet resultSet;
            PreparedStatement preparedStatement;
            preparedStatement = Utility.db.getConnection().prepareStatement("SELECT id_mt_lantai, nama_lantai, gambar FROM tb_mt_lantai WHERE nama_lantai LIKE ? OR gambar LIKE ?");
            preparedStatement.setString(1, "%" + kataKunci + "%");
            preparedStatement.setString(2, "%" + kataKunci + "%");
            resultSet = preparedStatement.executeQuery();
            lantais = new ArrayList<>();
            while (resultSet.next()) {
                Lantai lantai = new Lantai(resultSet.getString("id_mt_lantai"), resultSet.getString("nama_lantai"), resultSet.getString("gambar"));
                lantais.add(lantai);
            }
            return lantais;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public int countLike(String kataKunci) throws Exception {
        try {
            

            ResultSet resultSet;
            PreparedStatement preparedStatement;
            String sql = "SELECT COUNT(id_mt_lantai) as total FROM tb_mt_lantai WHERE nama_lantai LIKE ? OR gambar LIKE ?";
            preparedStatement = Utility.db.getConnection().prepareStatement(sql);
            preparedStatement.setString(1, "%" + kataKunci + "%");
            preparedStatement.setString(2, "%" + kataKunci + "%");
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

    public boolean insert(MainFrame_Maintenance mainJFrame, Lantai newLantai) throws Exception {
        try {
            
            Utility.db.getConnection().setAutoCommit(false);

            PreparedStatement preparedStatement;
            preparedStatement = Utility.db.getConnection().prepareStatement("INSERT INTO tb_mt_lantai(id_mt_lantai, nama_lantai, gambar) VALUES (NULL, ?, NULL)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, newLantai.getNamaLantai());
            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    int last_inserted_id = resultSet.getInt(1);

                    String pathGambar = newLantai.getGambar();
                    int lastIndexOfSlash = Math.max(pathGambar.lastIndexOf('/'), pathGambar.lastIndexOf('\\'));
                    if (lastIndexOfSlash < 0) {
                        pathGambar = (PathHelper.path + "lantai/" + pathGambar).replace("file:/", "").replace("%20", " ");
                    }
                    int lastIndexOfDot = pathGambar.lastIndexOf(".");
                    String namaGambarBaru;
                    if (lastIndexOfDot == -1) {
                        throw new Exception("Pastikan gambar berupa berkas gambar");
                    } else {
                        namaGambarBaru = last_inserted_id + "-" + UUID.randomUUID().toString().replace("-", "") + pathGambar.substring(lastIndexOfDot);
                    }

                    File source = new File(pathGambar);
                    File dest = new File((PathHelper.path + "lantai/" + namaGambarBaru).replace("file:/", "").replace("%20", " "));

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

                    preparedStatement = Utility.db.getConnection().prepareStatement("UPDATE tb_mt_lantai SET gambar = ? WHERE id_mt_lantai = ?");
                    preparedStatement.setString(1, namaGambarBaru);
                    preparedStatement.setInt(2, last_inserted_id);
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
                    throw new Exception("Gagal menambahkan data Lantai ke dalam basis data");
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

    public boolean update(MainFrame_Maintenance mainJFrame, String idLantaiUpdate, Lantai newLantai) throws Exception {
        try {
            
            Utility.db.getConnection().setAutoCommit(false);

            ResultSet resultSet;
            PreparedStatement preparedStatement;
            File gambar = null;
            String oldPath = null;

            preparedStatement = Utility.db.getConnection().prepareStatement("SELECT gambar FROM tb_mt_lantai WHERE id_mt_lantai = ?");
            preparedStatement.setString(1, idLantaiUpdate);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                gambar = new File((PathHelper.path + "lantai/" + resultSet.getString("gambar")).replace("file:/", "").replace("%20", " "));
                oldPath = resultSet.getString("gambar");
            }

            String pathGambar = newLantai.getGambar();
            int lastIndexOfSlash = Math.max(pathGambar.lastIndexOf('/'), pathGambar.lastIndexOf('\\'));
            if (lastIndexOfSlash < 0) {
                pathGambar = (PathHelper.path + "lantai/" + pathGambar).replace("file:/", "").replace("%20", " ");
            }
            int lastIndexOfDot = pathGambar.lastIndexOf(".");
            String namaGambarBaru;
            if (lastIndexOfDot == -1) {
                throw new Exception("Pastikan gambar berupa berkas gambar");
            } else {
                namaGambarBaru = idLantaiUpdate + "-" + UUID.randomUUID().toString().replace("-", "") + pathGambar.substring(lastIndexOfDot);
            }

            preparedStatement = Utility.db.getConnection().prepareStatement("UPDATE tb_mt_lantai SET nama_lantai = ?, gambar = ? WHERE id_mt_lantai = ?");
            preparedStatement.setString(1, newLantai.getNamaLantai());
            preparedStatement.setString(2, namaGambarBaru);
            preparedStatement.setString(3, idLantaiUpdate);
            int result = preparedStatement.executeUpdate();
            if (result > -1) {

                File source = new File(pathGambar);
                File dest = new File((PathHelper.path + "lantai/" + namaGambarBaru).replace("file:/", "").replace("%20", " "));

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
                        if (!oldPath.equalsIgnoreCase(namaGambarBaru)) {
                            if (gambar != null) {
                                gambar.delete();
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

    public boolean delete(MainFrame_Maintenance mainJFrame, String idLantaiDelete) throws Exception {
        try {
            
            Utility.db.getConnection().setAutoCommit(false);

            ResultSet resultSet;
            PreparedStatement preparedStatement;
            File gambar = null;

            preparedStatement = Utility.db.getConnection().prepareStatement("SELECT gambar FROM tb_mt_lantai WHERE id_mt_lantai = ?");
            preparedStatement.setString(1, idLantaiDelete);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                gambar = new File((PathHelper.path + "lantai/" + resultSet.getString("gambar")).replace("file:/", "").replace("%20", " "));
            }

            preparedStatement = Utility.db.getConnection().prepareStatement("DELETE FROM tb_mt_lantai WHERE id_mt_lantai = ?");
            preparedStatement.setString(1, idLantaiDelete);
            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                if (gambar != null) {
                    gambar.delete();
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

    public boolean deleteSelected(MainFrame_Maintenance mainJFrame, ArrayList<String> idLantaiDelete) throws Exception {
        try {
            
            Utility.db.getConnection().setAutoCommit(false);

            ResultSet resultSet;
            PreparedStatement preparedStatement;
            ArrayList<File> gambar = new ArrayList<>();

            boolean sukses = true;

            for (int i = 0; i < idLantaiDelete.size(); i++) {
                File newGambar = null;

                preparedStatement = Utility.db.getConnection().prepareStatement("SELECT gambar FROM tb_mt_lantai WHERE id_mt_lantai = ?");
                preparedStatement.setString(1, idLantaiDelete.get(i));
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    newGambar = new File((PathHelper.path + "lantai/" + resultSet.getString("gambar")).replace("file:/", "").replace("%20", " "));
                }
                gambar.add(newGambar);

                preparedStatement = Utility.db.getConnection().prepareStatement("DELETE FROM tb_mt_lantai WHERE id_mt_lantai = ?");
                preparedStatement.setString(1, idLantaiDelete.get(i));
                int result = preparedStatement.executeUpdate();
                if (result <= 0) {
                    sukses = false;
                    break;
                }
            }

            if (sukses) {
                for (int i = 0; i < gambar.size(); i++) {
                    if (gambar.get(i) != null) {
                        gambar.get(i).delete();
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
