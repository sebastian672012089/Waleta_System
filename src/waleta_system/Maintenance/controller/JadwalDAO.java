/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package waleta_system.Maintenance.controller;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import waleta_system.Class.Utility;
import waleta_system.Maintenance.model.Jadwal;

/**
 *
 * @author User
 */
public class JadwalDAO {

    public JadwalDAO() {
    }

    public ArrayList<Jadwal> selectHariIniWhereIdLantai() throws Exception {
        ArrayList<Jadwal> jadwals;
        try {
            

            ResultSet resultSet;
            PreparedStatement preparedStatement;
            preparedStatement = Utility.db.getConnection().prepareStatement("SELECT tb1.id_mt_jadwal, tb5.nama_pegawai, tb1.id_mt_alat, tb1.nama_alat, tb1.nama_kegiatan, tb1.tgl_kegiatan FROM tb_mt_jadwal AS tb1 LEFT JOIN tb_mt_ruangan AS tb2 ON tb1.id_mt_ruangan = tb2.id_mt_ruangan LEFT JOIN tb_mt_alat AS tb3 ON tb1.id_mt_alat = tb3.id_mt_alat LEFT JOIN tb_login AS tb4 ON tb1.id_login_pic = tb4.user LEFT JOIN tb_karyawan AS tb5 ON tb4.id_pegawai = tb5.id_pegawai WHERE DATE(tb1.tgl_kegiatan) = CURDATE()");
            resultSet = preparedStatement.executeQuery();
            jadwals = new ArrayList<>();
            while (resultSet.next()) {
                Jadwal jadwal = new Jadwal(resultSet.getString("id_mt_jadwal"), resultSet.getString("nama_pegawai"), resultSet.getString("nama_alat"), resultSet.getString("id_mt_alat"), resultSet.getString("nama_kegiatan"), resultSet.getDate("tgl_kegiatan"));
                jadwals.add(jadwal);
            }
            return jadwals;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public ArrayList<Jadwal> selectTerlambatWhereIdLantai() throws Exception {
        ArrayList<Jadwal> jadwals;
        try {
            

            ResultSet resultSet;
            PreparedStatement preparedStatement;
            preparedStatement = Utility.db.getConnection().prepareStatement("SELECT tb1.id_mt_jadwal, tb5.nama_pegawai, tb1.id_mt_alat, tb1.nama_alat, tb1.nama_kegiatan, tb1.tgl_kegiatan FROM tb_mt_jadwal AS tb1 LEFT JOIN tb_mt_ruangan AS tb2 ON tb1.id_mt_ruangan = tb2.id_mt_ruangan LEFT JOIN tb_mt_alat AS tb3 ON tb1.id_mt_alat = tb3.id_mt_alat LEFT JOIN tb_login AS tb4 ON tb1.id_login_pic = tb4.user LEFT JOIN tb_karyawan AS tb5 ON tb4.id_pegawai = tb5.id_pegawai WHERE DATE(tb1.tgl_kegiatan) < CURDATE()");
            resultSet = preparedStatement.executeQuery();
            jadwals = new ArrayList<>();
            while (resultSet.next()) {
                Jadwal jadwal = new Jadwal(resultSet.getString("id_mt_jadwal"), resultSet.getString("nama_pegawai"), resultSet.getString("id_mt_alat"), resultSet.getString("nama_alat"), resultSet.getString("nama_kegiatan"), resultSet.getDate("tgl_kegiatan"));
                jadwals.add(jadwal);
            }
            return jadwals;
        } catch (Exception ex) {
            throw ex;
        }
    }
}
