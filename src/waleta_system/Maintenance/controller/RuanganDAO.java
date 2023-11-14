/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package waleta_system.Maintenance.controller;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import waleta_system.Maintenance.model.Lantai;
import waleta_system.Maintenance.model.Ruangan;
import waleta_system.Maintenance.view.MainFrame_Maintenance;
import waleta_system.Class.Utility;

/**
 *
 * @author User
 */
public class RuanganDAO {

    public RuanganDAO() {
    }

    public ArrayList<Ruangan> selectAll() throws Exception {
        ArrayList<Ruangan> ruangans;
        try {
            ResultSet resultSet;
            PreparedStatement preparedStatement;
            preparedStatement = Utility.db.getConnection().prepareStatement("SELECT tb1.id_mt_ruangan, tb1.nama_ruangan, tb1.id_mt_lantai, tb2.nama_lantai, tb2.gambar FROM tb_mt_ruangan AS tb1 LEFT JOIN tb_mt_lantai AS tb2 ON tb1.id_mt_lantai = tb2.id_mt_lantai");
            resultSet = preparedStatement.executeQuery();
            ruangans = new ArrayList<>();
            while (resultSet.next()) {
                Ruangan ruangan = new Ruangan(resultSet.getString("id_mt_ruangan"), resultSet.getString("nama_ruangan"), new Lantai(resultSet.getString("id_mt_lantai"), resultSet.getString("nama_lantai"), resultSet.getString("gambar")));
                ruangans.add(ruangan);
            }
            return ruangans;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public ArrayList<Ruangan> selectLikePagingSort(String kataKunci, int limit, int start, String sortBy, String sortType) throws Exception {
        ArrayList<Ruangan> ruangans;
        try {
            

            ResultSet resultSet;
            PreparedStatement preparedStatement;
            String sql = "SELECT tb1.id_mt_ruangan, tb1.nama_ruangan, tb1.id_mt_lantai, tb2.nama_lantai, tb2.gambar FROM tb_mt_ruangan AS tb1 LEFT JOIN tb_mt_lantai AS tb2 ON tb1.id_mt_lantai = tb2.id_mt_lantai WHERE tb1.nama_ruangan LIKE ? OR tb2.nama_lantai LIKE ? ORDER BY ? ? LIMIT ? OFFSET ?";            
            preparedStatement = Utility.db.getConnection().prepareStatement(sql);
            preparedStatement.setString(1, "%" + kataKunci + "%");
            preparedStatement.setString(2, "%" + kataKunci + "%");
            if (sortBy == null)
            {
                preparedStatement.setString(3, "nama_ruangan");
            }
            else
            {
                preparedStatement.setString(3, sortBy);
            }
            if (sortType == null)
            {
                preparedStatement.setString(4, "asc");
            }
            else
            {
                preparedStatement.setString(4, sortType);
            }
            preparedStatement.setInt(5, limit);
            preparedStatement.setInt(6, start);
            resultSet = preparedStatement.executeQuery();
            ruangans = new ArrayList<>();
            while (resultSet.next()) {
                Ruangan ruangan = new Ruangan(resultSet.getString("id_mt_ruangan"), resultSet.getString("nama_ruangan"), new Lantai(resultSet.getString("id_mt_lantai"), resultSet.getString("nama_lantai"), resultSet.getString("gambar")));
                ruangans.add(ruangan);
            }
            return ruangans;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public ArrayList<Ruangan> selectLikeSort(String kataKunci, String sortBy, String sortType) throws Exception {
        ArrayList<Ruangan> ruangans;
        try {
            

            ResultSet resultSet;
            PreparedStatement preparedStatement;
            String sql = "SELECT tb1.id_mt_ruangan, tb1.nama_ruangan, tb1.id_mt_lantai, tb2.nama_lantai, tb2.gambar FROM tb_mt_ruangan AS tb1 LEFT JOIN tb_mt_lantai AS tb2 ON tb1.id_mt_lantai = tb2.id_mt_lantai WHERE tb1.nama_ruangan LIKE ? OR tb2.nama_lantai LIKE ? ORDER BY ? ?";            
            preparedStatement = Utility.db.getConnection().prepareStatement(sql);
            preparedStatement.setString(1, "%" + kataKunci + "%");
            preparedStatement.setString(2, "%" + kataKunci + "%");
            if (sortBy == null)
            {
                preparedStatement.setString(3, "nama_ruangan");
            }
            else
            {
                preparedStatement.setString(3, sortBy);
            }
            if (sortType == null)
            {
                preparedStatement.setString(4, "asc");
            }
            else
            {
                preparedStatement.setString(4, sortType);
            }
            resultSet = preparedStatement.executeQuery();
            ruangans = new ArrayList<>();
            while (resultSet.next()) {
                Ruangan ruangan = new Ruangan(resultSet.getString("id_mt_ruangan"), resultSet.getString("nama_ruangan"), new Lantai(resultSet.getString("id_mt_lantai"), resultSet.getString("nama_lantai"), resultSet.getString("gambar")));
                ruangans.add(ruangan);
            }
            return ruangans;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public ArrayList<Ruangan> selectLike(String kataKunci) throws Exception {
        ArrayList<Ruangan> ruangans;
        try {
            

            ResultSet resultSet;
            PreparedStatement preparedStatement;
            preparedStatement = Utility.db.getConnection().prepareStatement("SELECT tb1.id_mt_ruangan, tb1.nama_ruangan, tb1.id_mt_lantai, tb2.nama_lantai, tb2.gambar FROM tb_mt_ruangan AS tb1 LEFT JOIN tb_mt_lantai AS tb2 ON tb1.id_mt_lantai = tb2.id_mt_lantai WHERE tb1.nama_ruangan LIKE ? OR tb2.nama_lantai LIKE ?");
            preparedStatement.setString(1, "%" + kataKunci + "%");
            preparedStatement.setString(2, "%" + kataKunci + "%");
            resultSet = preparedStatement.executeQuery();
            ruangans = new ArrayList<>();
            while (resultSet.next()) {
                Ruangan ruangan = new Ruangan(resultSet.getString("id_mt_ruangan"), resultSet.getString("nama_ruangan"), new Lantai(resultSet.getString("id_mt_lantai"), resultSet.getString("nama_lantai"), resultSet.getString("gambar")));
                ruangans.add(ruangan);
            }
            return ruangans;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public int countLike(String kataKunci) throws Exception {
        try {
            

            ResultSet resultSet;
            PreparedStatement preparedStatement;
            String sql = "SELECT COUNT(tb1.id_mt_ruangan) as total FROM tb_mt_ruangan AS tb1 LEFT JOIN tb_mt_lantai AS tb2 ON tb1.id_mt_lantai = tb2.id_mt_lantai WHERE tb1.nama_ruangan LIKE ? OR tb2.nama_lantai LIKE ?";            
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

    public boolean insert(MainFrame_Maintenance mainJFrame, Ruangan newRuangan) throws Exception {
        try {
            
            Utility.db.getConnection().setAutoCommit(false);

            PreparedStatement preparedStatement;
            preparedStatement = Utility.db.getConnection().prepareStatement("INSERT INTO tb_mt_ruangan(id_mt_ruangan, nama_ruangan, id_mt_lantai) VALUES (NULL, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, newRuangan.getNamaRuangan());
            preparedStatement.setString(2, newRuangan.getLantai().getIdLantai());
            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    Utility.db.getConnection().commit();
                    return true;
                } else {
                    Utility.db.getConnection().rollback();
                    throw new Exception("Gagal menambahkan data Ruangan ke dalam basis data");
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

    public boolean update(MainFrame_Maintenance mainJFrame, String idRuanganUpdate, Ruangan newRuangan) throws Exception {
        try {
            
            Utility.db.getConnection().setAutoCommit(false);

            PreparedStatement preparedStatement;

            preparedStatement = Utility.db.getConnection().prepareStatement("UPDATE tb_mt_ruangan SET nama_ruangan = ?, id_mt_lantai = ? WHERE id_mt_ruangan = ?");
            preparedStatement.setString(1, newRuangan.getNamaRuangan());
            preparedStatement.setString(2, newRuangan.getLantai().getIdLantai());
            preparedStatement.setString(3, idRuanganUpdate);
            int result = preparedStatement.executeUpdate();
            if (result > -1) {
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

    public boolean delete(MainFrame_Maintenance mainJFrame, String idRuanganDelete) throws Exception {
        try {
            
            Utility.db.getConnection().setAutoCommit(false);

            ResultSet resultSet;
            PreparedStatement preparedStatement;

            preparedStatement = Utility.db.getConnection().prepareStatement("DELETE FROM tb_mt_ruangan WHERE id_mt_ruangan = ?");
            preparedStatement.setString(1, idRuanganDelete);
            int result = preparedStatement.executeUpdate();
            if (result > 0) {
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

    public boolean deleteSelected(MainFrame_Maintenance mainJFrame, ArrayList<String> idRuanganDelete) throws Exception {
        try {
            
            Utility.db.getConnection().setAutoCommit(false);

            ResultSet resultSet;
            PreparedStatement preparedStatement;

            boolean sukses = true;

            for (int i = 0; i < idRuanganDelete.size(); i++) {

                preparedStatement = Utility.db.getConnection().prepareStatement("DELETE FROM tb_mt_ruangan WHERE id_mt_ruangan = ?");
                preparedStatement.setString(1, idRuanganDelete.get(i));
                int result = preparedStatement.executeUpdate();
                if (result <= 0) {
                    sukses = false;
                    break;
                }
            }

            if (sukses) {
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
