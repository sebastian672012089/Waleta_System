package waleta_system.BahanJadi;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import waleta_system.Browse_Karyawan;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;

public class JDialog_rePacking extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs = null;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    float total_keping_asal = 0, tot_keping = 0;
    float total_gram_asal = 0, tot_gram = 0;

    public JDialog_rePacking(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        try {
            initComponents();
            this.setResizable(false);

            //give icon for button
            button_tambah.setIcon(Utility.ResizeImageIcon(new javax.swing.ImageIcon(getClass().getResource("/waleta_system/Images/add_green.png")), button_tambah.getWidth(), button_tambah.getHeight()));
            button_kurang.setIcon(Utility.ResizeImageIcon(new javax.swing.ImageIcon(getClass().getResource("/waleta_system/Images/delete-icon.png")), button_kurang.getWidth(), button_kurang.getHeight()));
            button_hapus_boxAsal.setIcon(Utility.ResizeImageIcon(new javax.swing.ImageIcon(getClass().getResource("/waleta_system/Images/delete-icon.png")), button_hapus_boxAsal.getWidth(), button_hapus_boxAsal.getHeight()));
            refresh_TabelDaftarBox();
            ComboBox_Grade();
            String list_rsb = "SELECT `no_registrasi` FROM `tb_rumah_burung` WHERE CHAR_LENGTH(`no_registrasi`) IN (3, 4)";
            ResultSet result = Utility.db.getStatement().executeQuery(list_rsb);
            while (result.next()) {
                ComboBox_kodeRSB.addItem(result.getString("no_registrasi"));
            }
        } catch (Exception ex) {
            Logger.getLogger(JDialog_rePacking.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void ComboBox_Grade() {
        ComboBox_kode_grading.removeAllItems();
        if (ComboBox_GNS_NS.getSelectedIndex() == 0) {
            try {
                String grade_COMBOBOX = "SELECT `kode_grade` FROM `tb_grade_bahan_jadi` WHERE `status_grade` = 'AKTIF' AND `kode_grade` LIKE 'GNS%'";
                ResultSet grade_result = Utility.db.getStatement().executeQuery(grade_COMBOBOX);
                while (grade_result.next()) {
                    ComboBox_kode_grading.addItem(grade_result.getString("kode_grade"));
                }
                AutoCompleteDecorator.decorate(ComboBox_kode_grading);
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_rePacking.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (ComboBox_GNS_NS.getSelectedIndex() == 1) {
            try {
                String grade_COMBOBOX = "SELECT `kode_grade` FROM `tb_grade_bahan_jadi` WHERE `status_grade` = 'AKTIF' AND `kode_grade` LIKE 'Non NS%'";
                ResultSet grade_result = Utility.db.getStatement().executeQuery(grade_COMBOBOX);
                while (grade_result.next()) {
                    ComboBox_kode_grading.addItem(grade_result.getString("kode_grade"));
                }
                AutoCompleteDecorator.decorate(ComboBox_kode_grading);
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_rePacking.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void refresh_TabelDaftarBox() {
        try {
            int total_keping = 0;
            float total_gram = 0;
            DefaultTableModel model = (DefaultTableModel) Table_daftarBox.getModel();
            model.setRowCount(0);
            sql = "SELECT `no_box`, `kode_grade`, `tanggal_box`, `keping`, `berat`, `status_terakhir`, `kode_rsb`, `nama_pegawai` AS 'pekerja_repacking' "
                    + "FROM `tb_box_bahan_jadi` "
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`"
                    + "LEFT JOIN `tb_karyawan` ON `tb_box_bahan_jadi`.`pekerja_repacking` = `tb_karyawan`.`id_pegawai` "
                    + "WHERE `no_box` LIKE '%" + txt_search_box.getText() + "%' AND `lokasi_terakhir` = 'GRADING' ";
//                    + "AND `berat` > 0";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("no_box"),
                    rs.getString("kode_grade"),
                    new SimpleDateFormat("dd MMMM yyyy").format(rs.getDate("tanggal_box")),
                    rs.getInt("keping"),
                    rs.getFloat("berat"),
                    rs.getString("status_terakhir"),
                    rs.getString("kode_rsb"),
                    rs.getString("pekerja_repacking")
                });
                total_keping = total_keping + rs.getInt("keping");
                total_gram = total_gram + rs.getFloat("berat");
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_daftarBox);
            int x = Table_daftarBox.getRowCount();
            label_total_daftarBox.setText(Integer.toString(x));
            label_total_keping_daftarBox.setText(decimalFormat.format(total_keping));
            label_total_gram_daftarBox.setText(decimalFormat.format(total_gram));
        } catch (SQLException ex) {
            Logger.getLogger(JDialog_rePacking.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getNewKodeRepacking() {
        String newCode = null;
        try {
            int number = 0;
            String new_number = null;
            sql = "SELECT COUNT(DISTINCT(`kode_repacking`)) AS 'last' FROM `tb_repacking` "
                    + "WHERE YEAR(`tanggal_repacking`)='" + new SimpleDateFormat("yyyy").format(Date_repacking.getDate()) + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                number = rs.getInt("last") + 1;
            }
            if (number < 10) {
                new_number = "000" + number;
            } else if (number < 100) {
                new_number = "00" + number;
            } else if (number < 1000) {
                new_number = "0" + number;
            } else if (number < 10000) {
                new_number = Integer.toString(number);
            }
            newCode = "-" + new SimpleDateFormat("yyMM").format(Date_repacking.getDate()) + new_number;
        } catch (SQLException | NullPointerException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_TambahDataTutupan.class.getName()).log(Level.SEVERE, null, ex);
        }
        return newCode;
    }

    public void count_hasil() {
        decimalFormat.setMaximumFractionDigits(2);
        tot_keping = 0;
        tot_gram = 0;
        int x = table_hasil.getRowCount();
        for (int i = 0; i < x; i++) {
            try {
                tot_keping = tot_keping + Integer.valueOf(table_hasil.getValueAt(i, 2).toString());
                tot_gram = tot_gram + Float.valueOf(table_hasil.getValueAt(i, 3).toString());
            } catch (NullPointerException e) {
                JOptionPane.showMessageDialog(this, "Tidak Boleh ada data yang kosong !");
            }
        }

        float susut = ((total_gram_asal - tot_gram) / total_gram_asal) * 100.f;

        label_total_hasil.setText(Integer.toString(x));
        label_total_keping_hasil.setText(decimalFormat.format(tot_keping));
        label_total_gram_hasil.setText(decimalFormat.format(tot_gram));
        label_sh_hasil.setText(decimalFormat.format(susut));
    }

    public void count_asal() {
        total_keping_asal = 0;
        total_gram_asal = 0;
        int x = Table_asal.getRowCount();
        for (int i = 0; i < x; i++) {
            try {
                total_keping_asal = total_keping_asal + Integer.valueOf(Table_asal.getValueAt(i, 3).toString());
                total_gram_asal = total_gram_asal + Float.valueOf(Table_asal.getValueAt(i, 4).toString());
            } catch (NullPointerException e) {
                JOptionPane.showMessageDialog(this, "Tidak Boleh ada data yang kosong !");
            }
        }
        label_total_asal.setText(Integer.toString(x));
        label_total_keping_asal.setText(decimalFormat.format(total_keping_asal));
        label_total_gram_asal.setText(decimalFormat.format(total_gram_asal));
        ColumnsAutoSizer.sizeColumnsToFit(Table_asal);
    }

    public String newBox(String grade, Date repacking) {
        String no_box_baru = null;
        try {
            String kode_grade = null;
            String nomor_box = null;
            int total_box = 0;
            sql = "SELECT COUNT(`no_box`) AS 'total_box', `tb_grade_bahan_jadi`.`kode` "
                    + "FROM `tb_box_bahan_jadi` "
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`"
                    + "WHERE YEAR(`tanggal_box`) = '" + new SimpleDateFormat("yyyy").format(repacking) + "' "
                    + "AND `tb_grade_bahan_jadi`.`kode_grade` = '" + grade + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                total_box = rs.getInt("total_box") + 1;
                kode_grade = rs.getString("kode");
            }

            if (total_box < 10) {
                nomor_box = "0000" + Integer.toString(total_box);
            } else if (total_box < 100) {
                nomor_box = "000" + Integer.toString(total_box);
            } else if (total_box < 1000) {
                nomor_box = "00" + Integer.toString(total_box);
            } else if (total_box < 10000) {
                nomor_box = "0" + Integer.toString(total_box);
            } else if (total_box < 100000) {
                nomor_box = Integer.toString(total_box);
            }

            no_box_baru = "BOX" + kode_grade + "-" + new SimpleDateFormat("yyMM").format(repacking) + nomor_box;

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_AddBox.class.getName()).log(Level.SEVERE, null, ex);
        }
        return no_box_baru;
    }

    public boolean DuplicateGrade(String grade) {
        int i = table_hasil.getRowCount();
        for (int j = 0; j < i; j++) {
            if (ComboBox_kode_grading.getSelectedItem().toString().equals(table_hasil.getValueAt(j, 1).toString())) {
                return true;
            }
        }
        return false;
    }

    public boolean CheckDuplicateBoxAsal(String no_box) {
        int i = Table_asal.getRowCount();
        for (int j = 0; j < i; j++) {
            if (no_box.equals(Table_asal.getValueAt(j, 0).toString())) {
                return true;
            }
        }
        return false;
    }

    public void NewRepacking() {
        boolean check = true;
        boolean check_TRNEST = true;
        count_asal();
        count_hasil();
        if (table_hasil.getRowCount() == 0 || Table_asal.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Silahkan memasukkan data asal dan hasil terlebih dahulu");
            check = false;
        } else if (txt_pekerja_id.getText() == null || txt_pekerja_id.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Silahkan memilih pekerja repacking");
            check = false;
        }
        for (int i = 0; i < table_hasil.getRowCount(); i++) {
            if (table_hasil.getValueAt(i, 1).toString().equals("GNS TR NEST")) {
                check_TRNEST = false;
                break;
            }
        }
        if (check && check_TRNEST) {
            if (tot_keping != total_keping_asal) {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Maaf Jumlah keping awal dan akhir tidak sama, apakah ingin melanjutkan?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    JDialog_otorisasi_gradingBJ dialog = new JDialog_otorisasi_gradingBJ(new javax.swing.JFrame(), true);
                    dialog.pack();
                    dialog.setLocationRelativeTo(this);
                    dialog.setVisible(true);
                    dialog.setEnabled(true);
                    check = dialog.akses();
                } else {
                    check = false;
                }
            }
        }
        try {
            Date a = Date_repacking.getDate();
            label_kode_repacking.setText(getNewKodeRepacking());
        } catch (Exception e) {
            check = false;
        }
        String NewKodeRepacking = getNewKodeRepacking();
        if (Date_repacking.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Maaf tanggal repacking salah");
            check = false;
        } else if (NewKodeRepacking == null || NewKodeRepacking.equals("")) {
            JOptionPane.showMessageDialog(this, "Tidak bisa mendapatkan kode repacking baru, silahkan cek koneksi internet dan coba save kembali");
            check = false;
        }

        if (check) {
            try {
                Utility.db.getConnection().setAutoCommit(false);
                label_kode_repacking.setText(NewKodeRepacking);
                String kode_repacking = ComboBox_kodeRepacking.getSelectedItem() + label_kode_repacking.getText();

                for (int i = 0; i < table_hasil.getRowCount(); i++) {
                    String no_box = newBox(table_hasil.getValueAt(i, 1).toString(), Date_repacking.getDate());
                    String insert = "INSERT INTO `tb_box_bahan_jadi`(`no_box`, `tanggal_box`, `kode_grade_bahan_jadi`, `keping`, `berat`, `no_tutupan`, `status_terakhir`, `lokasi_terakhir`, `tgl_proses_terakhir`, `kode_rsb`) "
                            + "VALUES (TRIM('" + no_box + "'),'" + dateFormat.format(Date_repacking.getDate()) + "',(SELECT `kode` FROM `tb_grade_bahan_jadi` WHERE `kode_grade` = '" + table_hasil.getValueAt(i, 1) + "'),'" + table_hasil.getValueAt(i, 2) + "','" + table_hasil.getValueAt(i, 3) + "', '" + ComboBox_kodeRepacking.getSelectedItem() + label_kode_repacking.getText() + "', 'NEW BOX', 'GRADING', '" + dateFormat.format(Date_repacking.getDate()) + "', '" + ComboBox_kodeRSB.getSelectedItem().toString() + "')";
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(insert);

                    String repacking = "INSERT INTO `tb_repacking`(`kode_repacking`, `tanggal_repacking`, `keterangan_repacking`, `pekerja_repacking`, `no_box`, `keping`, `gram`, `status`, `kode_rsb`) "
                            + "VALUES ('" + kode_repacking + "','" + dateFormat.format(Date_repacking.getDate()) + "','" + txt_keterangan.getText() + "', '" + txt_pekerja_id.getText() + "','" + no_box + "','" + table_hasil.getValueAt(i, 2) + "','" + table_hasil.getValueAt(i, 3) + "','HASIL', '" + ComboBox_kodeRSB.getSelectedItem().toString() + "')";
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(repacking);
                }

                for (int i = 0; i < Table_asal.getRowCount(); i++) {
                    String update = "UPDATE `tb_box_bahan_jadi` SET "
                            + "`status_terakhir`='" + kode_repacking + "',"
                            + "`lokasi_terakhir`='REPACKING',"
                            + "`tgl_proses_terakhir`='" + dateFormat.format(Date_repacking.getDate()) + "' "
                            + "WHERE `no_box` = '" + Table_asal.getValueAt(i, 0).toString() + "'";
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(update);

                    String repacking = "INSERT INTO `tb_repacking`(`kode_repacking`, `tanggal_repacking`, `keterangan_repacking`, `pekerja_repacking`, `no_box`, `keping`, `gram`, `status`, `kode_rsb`) "
                            + "VALUES ('" + kode_repacking + "','" + dateFormat.format(Date_repacking.getDate()) + "', '" + txt_keterangan.getText() + "', '" + txt_pekerja_id.getText() + "','" + Table_asal.getValueAt(i, 0) + "','" + Table_asal.getValueAt(i, 3) + "','" + Table_asal.getValueAt(i, 4) + "','ASAL', '" + ComboBox_kodeRSB.getSelectedItem().toString() + "')";
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(repacking);

                }
                Utility.db.getConnection().commit();
                JOptionPane.showMessageDialog(this, "Data Re-packing berhasil di tambahkan");
                JPanel_BoxBahanJadi.button_search_Box.doClick();
                this.dispose();
            } catch (Exception e) {
                try {
                    Utility.db.getConnection().rollback();
                } catch (SQLException ex) {
                    Logger.getLogger(JDialog_reProcess.class.getName()).log(Level.SEVERE, null, ex);
                }
                JOptionPane.showMessageDialog(this, e);
                Logger.getLogger(JDialog_reProcess.class.getName()).log(Level.SEVERE, null, e);
            } finally {
                try {
                    Utility.db.getConnection().setAutoCommit(true);
                } catch (SQLException ex) {
                    Logger.getLogger(JDialog_reProcess.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            this.dispose();
        }
    }

    public void InsertRow() {
        DefaultTableModel model = (DefaultTableModel) table_hasil.getModel();
        boolean add = true;
        int kpg = 0;
        float gram = 0;
        try {
//            if (DuplicateGrade(ComboBox_kode_grading.getSelectedItem().toString())) {
//                ComboBox_kode_grading.requestFocus();
//                throw new Exception("Grade " + ComboBox_kode_grading.getSelectedItem().toString() + "sudah ada !");
//            } else 
            if ("".equals(txt_Jumlah_keping.getText()) || txt_Jumlah_keping.getText() == null) {
                txt_Jumlah_keping.requestFocus();
                throw new Exception("Keping TIDAK BOLEH Kosong");
            } else if ("".equals(txt_jumlah_berat.getText()) || txt_jumlah_berat.getText() == null || "0".equals(txt_jumlah_berat.getText())) {
                txt_jumlah_berat.requestFocus();
                throw new Exception("Gram TIDAK BOLEH Kosong");
            } else {
                kpg = Integer.valueOf(txt_Jumlah_keping.getText());
                gram = Float.valueOf(txt_jumlah_berat.getText());
            }
        } catch (Exception e) {
            add = false;
            JOptionPane.showMessageDialog(this, e);
        }

        if (add) {
            model.addRow(new Object[]{table_hasil.getRowCount() + 1, ComboBox_kode_grading.getSelectedItem().toString(), kpg, gram});
            ComboBox_kode_grading.requestFocus();
            txt_Jumlah_keping.setText("");
            txt_jumlah_berat.setText("");
            ColumnsAutoSizer.sizeColumnsToFit(table_hasil);
            count_hasil();
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
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        label_kode_repacking = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        Date_repacking = new com.toedter.calendar.JDateChooser();
        ComboBox_kodeRepacking = new javax.swing.JComboBox<>();
        jLabel20 = new javax.swing.JLabel();
        txt_keterangan = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        txt_pekerja_id = new javax.swing.JTextField();
        txt_pekerja_nama = new javax.swing.JTextField();
        button_pekerja = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        txt_Jumlah_keping = new javax.swing.JTextField();
        ComboBox_kode_grading = new javax.swing.JComboBox<>();
        label_total_gram_hasil = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txt_jumlah_berat = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        table_hasil = new javax.swing.JTable();
        jLabel16 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        button_kurang = new javax.swing.JButton();
        label_total_hasil = new javax.swing.JLabel();
        button_tambah = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        label_sh_hasil = new javax.swing.JLabel();
        label_total_keping_hasil = new javax.swing.JLabel();
        ComboBox_GNS_NS = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        button_save = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        button_count = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        label_total_gram_daftarBox = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        label_keterangan = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        label_total_keping_daftarBox = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        label_total_daftarBox = new javax.swing.JLabel();
        txt_search_box = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        Table_daftarBox = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        button_hapus_boxAsal = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_asal = new javax.swing.JTable();
        label_total_keping_asal = new javax.swing.JLabel();
        label_total_asal = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        label_total_gram_asal = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        ComboBox_kodeRSB = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("REPACKING");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText(new SimpleDateFormat("dd MMMM yyyy").format(date));

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel5.setText("KODE :");

        label_kode_repacking.setBackground(new java.awt.Color(255, 255, 255));
        label_kode_repacking.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_kode_repacking.setText("-");

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel19.setText("Tanggal Repacking :");

        Date_repacking.setBackground(new java.awt.Color(255, 255, 255));
        Date_repacking.setDateFormatString("dd MMMM yyyy");
        Date_repacking.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_repacking.setMaxSelectableDate(new Date());
        Date_repacking.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                Date_repackingPropertyChange(evt);
            }
        });

        ComboBox_kodeRepacking.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        ComboBox_kodeRepacking.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "RPK", "PCH", "RPB" }));

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel20.setText("Keterangan :");

        txt_keterangan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel22.setText("Kode RSB :");

        txt_pekerja_id.setEditable(false);
        txt_pekerja_id.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        txt_pekerja_nama.setEditable(false);
        txt_pekerja_nama.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_pekerja.setBackground(new java.awt.Color(255, 255, 255));
        button_pekerja.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_pekerja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_pekerjaActionPerformed(evt);
            }
        });

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        txt_Jumlah_keping.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_Jumlah_keping.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_Jumlah_kepingKeyPressed(evt);
            }
        });

        ComboBox_kode_grading.setEditable(true);
        ComboBox_kode_grading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_kode_grading.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ComboBox_kode_gradingKeyPressed(evt);
            }
        });

        label_total_gram_hasil.setText("0");

        jLabel7.setText("Total :");

        txt_jumlah_berat.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_jumlah_berat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_jumlah_beratKeyPressed(evt);
            }
        });

        table_hasil.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_hasil.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Grade", "Kpg", "Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_hasil.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(table_hasil);
        if (table_hasil.getColumnModel().getColumnCount() > 0) {
            table_hasil.getColumnModel().getColumn(0).setResizable(false);
            table_hasil.getColumnModel().getColumn(0).setPreferredWidth(25);
        }

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel16.setText("Keping :");

        jLabel8.setText("Kpg :");

        button_kurang.setBackground(new java.awt.Color(255, 255, 255));
        button_kurang.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_kurang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_kurangActionPerformed(evt);
            }
        });

        label_total_hasil.setText("0");

        button_tambah.setBackground(new java.awt.Color(255, 255, 255));
        button_tambah.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_tambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_tambahActionPerformed(evt);
            }
        });

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel17.setText("Berat :");

        jLabel21.setText("SH:");

        label_sh_hasil.setText("0");

        label_total_keping_hasil.setText("0");

        ComboBox_GNS_NS.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_GNS_NS.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "GNS", "Non NS" }));
        ComboBox_GNS_NS.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ComboBox_GNS_NSItemStateChanged(evt);
            }
        });

        jLabel10.setText("Gr :");

        button_save.setBackground(new java.awt.Color(255, 255, 255));
        button_save.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_save.setText("Save");
        button_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_saveActionPerformed(evt);
            }
        });

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Tabel Hasil");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel15.setText("Grade :");

        button_count.setBackground(new java.awt.Color(255, 255, 255));
        button_count.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_count.setText("Count");
        button_count.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_countActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_hasil)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_keping_hasil)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_hasil)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_sh_hasil)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_kurang, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_count))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ComboBox_kode_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_GNS_NS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16)
                            .addComponent(txt_Jumlah_keping, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(txt_jumlah_berat, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel17))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_save)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_total_gram_hasil, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_kurang, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_count, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_hasil, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_keping_hasil, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_sh_hasil, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_GNS_NS, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txt_Jumlah_keping, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_jumlah_berat, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(ComboBox_kode_grading, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_save, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        label_total_gram_daftarBox.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_daftarBox.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_gram_daftarBox.setText("0");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Keping :");

        label_keterangan.setBackground(new java.awt.Color(255, 255, 255));
        label_keterangan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_keterangan.setForeground(new java.awt.Color(102, 102, 102));
        label_keterangan.setText("*Press ENTER to insert");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Total :");

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel18.setText("No Box :");

        label_total_keping_daftarBox.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_daftarBox.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_keping_daftarBox.setText("0");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Gram :");

        label_total_daftarBox.setBackground(new java.awt.Color(255, 255, 255));
        label_total_daftarBox.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_daftarBox.setText("0");

        txt_search_box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_box.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_boxKeyPressed(evt);
            }
        });

        Table_daftarBox.setAutoCreateRowSorter(true);
        Table_daftarBox.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_daftarBox.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Box", "Grade", "Tgl Box", "Kpg", "Gram", "Status", "CT RSB", "Pekerja"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_daftarBox.setFocusable(false);
        Table_daftarBox.getTableHeader().setReorderingAllowed(false);
        Table_daftarBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Table_daftarBoxMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(Table_daftarBox);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_box, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_keterangan, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_daftarBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_keping_daftarBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_daftarBox))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 733, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_search_box, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_keterangan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_keping_daftarBox, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram_daftarBox, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_daftarBox, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Daftar Box Grading", jPanel4);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        button_hapus_boxAsal.setBackground(new java.awt.Color(255, 255, 255));
        button_hapus_boxAsal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_hapus_boxAsal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_hapus_boxAsalActionPerformed(evt);
            }
        });

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Gram :");

        Table_asal.setAutoCreateRowSorter(true);
        Table_asal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_asal.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Box", "Grade", "Tgl Box", "Kpg", "Gram", "Status", "CT RSB", "Pekerja"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_asal.setFocusable(false);
        Table_asal.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Table_asal);

        label_total_keping_asal.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_asal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_keping_asal.setText("0");

        label_total_asal.setBackground(new java.awt.Color(255, 255, 255));
        label_total_asal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_asal.setText("0");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("Keping :");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Tabel Asal");

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Total :");

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 0, 0));
        jLabel12.setText("NB : Total Keping Asal dan Hasil harus sama !!");

        label_total_gram_asal.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_asal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_gram_asal.setText("0");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_hapus_boxAsal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_asal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_keping_asal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_asal))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 733, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_hapus_boxAsal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_keping_asal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram_asal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_asal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Box Terpilih", jPanel5);

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel23.setText("Pekerja :");

        ComboBox_kodeRSB.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_kodeRSB.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ComboBox_kodeRSBItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 758, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_kodeRepacking, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_kode_repacking))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_repacking, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel23)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_pekerja_id, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_pekerja_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_pekerja, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel22)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_kodeRSB, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_keterangan, javax.swing.GroupLayout.PREFERRED_SIZE, 358, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_kode_repacking, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_kodeRepacking, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel19)
                    .addComponent(Date_repacking, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(txt_keterangan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel22)
                    .addComponent(txt_pekerja_id, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_pekerja_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_pekerja, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23)
                    .addComponent(ComboBox_kodeRSB, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addComponent(jTabbedPane1))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void button_tambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_tambahActionPerformed
        InsertRow();
    }//GEN-LAST:event_button_tambahActionPerformed

    private void button_kurangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_kurangActionPerformed
        int i = table_hasil.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) table_hasil.getModel();
        if (i != -1) {
//            model.removeRow(Table_asal.getRowSorter().convertRowIndexToModel(i));
            model.removeRow(i);
            for (int j = 0; j < table_hasil.getRowCount(); j++) {
                table_hasil.setValueAt(j + 1, j, 0);
            }
        }
        ColumnsAutoSizer.sizeColumnsToFit(table_hasil);
        count_hasil();
    }//GEN-LAST:event_button_kurangActionPerformed

    private void button_countActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_countActionPerformed
        // TODO add your handling code here:
        count_hasil();
    }//GEN-LAST:event_button_countActionPerformed

    private void button_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_saveActionPerformed
        try {
            boolean check = true;
            String no_box = "";
            for (int i = 0; i < Table_asal.getRowCount(); i++) {
                if (i != 0) {
                    no_box = no_box + ", ";
                }
                no_box = no_box + "'" + Table_asal.getValueAt(i, 0).toString() + "'";
            }

            sql = "SELECT `no_box`, `lokasi_terakhir`, `status_terakhir` "
                    + "FROM `tb_box_bahan_jadi` "
                    + "WHERE `no_box` IN (" + no_box + ")";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                if (!rs.getString("lokasi_terakhir").equals("GRADING")) {
                    JOptionPane.showMessageDialog(this,
                            "No Box : " + rs.getString("no_box") + "\n"
                            + "Status : " + rs.getString("status_terakhir") + "\n"
                            + "Lokasi : " + rs.getString("lokasi_terakhir") + "\n"
                            + "Box tidak tersedia untuk di repacking!");
                    check = false;
                }
            }
            if (check) {
                NewRepacking();
            }
        } catch (SQLException ex) {
            Logger.getLogger(JDialog_rePacking.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_saveActionPerformed

    private void ComboBox_kode_gradingKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ComboBox_kode_gradingKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            InsertRow();
        }
    }//GEN-LAST:event_ComboBox_kode_gradingKeyPressed

    private void txt_Jumlah_kepingKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_Jumlah_kepingKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            InsertRow();
        }
    }//GEN-LAST:event_txt_Jumlah_kepingKeyPressed

    private void txt_jumlah_beratKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_jumlah_beratKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            InsertRow();
        }
    }//GEN-LAST:event_txt_jumlah_beratKeyPressed

    private void txt_search_boxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_boxKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refresh_TabelDaftarBox();
            if (Table_daftarBox.getRowCount() == 1) {
                if (CheckDuplicateBoxAsal(Table_daftarBox.getValueAt(0, 0).toString())) {
                    JOptionPane.showMessageDialog(this, "Box " + Table_daftarBox.getValueAt(0, 0).toString() + " sudah Masuk");
                } else {
                    DefaultTableModel model = (DefaultTableModel) Table_asal.getModel();
                    model.addRow(new Object[]{Table_daftarBox.getValueAt(0, 0),
                        Table_daftarBox.getValueAt(0, 1),
                        Table_daftarBox.getValueAt(0, 2),
                        Table_daftarBox.getValueAt(0, 3),
                        Table_daftarBox.getValueAt(0, 4),
                        Table_daftarBox.getValueAt(0, 5),
                        Table_daftarBox.getValueAt(0, 6),
                        Table_daftarBox.getValueAt(0, 7)});
                    ColumnsAutoSizer.sizeColumnsToFit(Table_asal);
                    label_keterangan.setVisible(true);
                    label_keterangan.setForeground(Color.green);
                    label_keterangan.setText("Inserted !");
                    txt_search_box.setText("");
                    txt_search_box.requestFocus();
                    count_asal();
                }
            } else if (Table_daftarBox.getRowCount() > 0) {
                label_keterangan.setVisible(true);
                label_keterangan.setForeground(Color.red);
                label_keterangan.setText("Multiple data selected !");
            } else {
                label_keterangan.setVisible(true);
                label_keterangan.setForeground(Color.red);
                label_keterangan.setText("No Data !");
            }
        }
    }//GEN-LAST:event_txt_search_boxKeyPressed

    private void button_hapus_boxAsalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_hapus_boxAsalActionPerformed
        // TODO add your handling code here:
        int i = Table_asal.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) Table_asal.getModel();
        if (i != -1) {
            model.removeRow(Table_asal.getRowSorter().convertRowIndexToModel(i));
        }
        ColumnsAutoSizer.sizeColumnsToFit(Table_asal);
        count_asal();
    }//GEN-LAST:event_button_hapus_boxAsalActionPerformed

    private void Table_daftarBoxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Table_daftarBoxMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            DefaultTableModel model = (DefaultTableModel) Table_asal.getModel();
            int x = Table_daftarBox.getSelectedRow();
            if (CheckDuplicateBoxAsal(Table_daftarBox.getValueAt(x, 0).toString())) {
                JOptionPane.showMessageDialog(this, "Box " + Table_daftarBox.getValueAt(0, 0).toString() + " sudah Masuk");
            } else {
                model.addRow(new Object[]{Table_daftarBox.getValueAt(x, 0),
                    Table_daftarBox.getValueAt(x, 1),
                    Table_daftarBox.getValueAt(x, 2),
                    Table_daftarBox.getValueAt(x, 3),
                    Table_daftarBox.getValueAt(x, 4),
                    Table_daftarBox.getValueAt(x, 5),
                    Table_daftarBox.getValueAt(x, 6),
                    Table_daftarBox.getValueAt(x, 7)});
                count_asal();
            }
        }
    }//GEN-LAST:event_Table_daftarBoxMouseClicked

    private void ComboBox_GNS_NSItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ComboBox_GNS_NSItemStateChanged
        // TODO add your handling code here:
        ComboBox_Grade();
    }//GEN-LAST:event_ComboBox_GNS_NSItemStateChanged

    private void Date_repackingPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_Date_repackingPropertyChange
        // TODO add your handling code here:
        if (Date_repacking.getDate() != null) {
            label_kode_repacking.setText(getNewKodeRepacking());
        } else {
            System.out.println("null");
        }
    }//GEN-LAST:event_Date_repackingPropertyChange

    private void ComboBox_kodeRSBItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ComboBox_kodeRSBItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_ComboBox_kodeRSBItemStateChanged

    private void button_pekerjaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_pekerjaActionPerformed
        // TODO add your handling code here:
        Browse_Karyawan dialog = new Browse_Karyawan(new javax.swing.JFrame(), true, null);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);

        int x = Browse_Karyawan.table_list_karyawan.getSelectedRow();
        if (x != -1) {
            txt_pekerja_id.setText(Browse_Karyawan.table_list_karyawan.getValueAt(x, 0).toString());
            txt_pekerja_nama.setText(Browse_Karyawan.table_list_karyawan.getValueAt(x, 1).toString());
        }
    }//GEN-LAST:event_button_pekerjaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_GNS_NS;
    private javax.swing.JComboBox<String> ComboBox_kodeRSB;
    private javax.swing.JComboBox<String> ComboBox_kodeRepacking;
    private javax.swing.JComboBox<String> ComboBox_kode_grading;
    private com.toedter.calendar.JDateChooser Date_repacking;
    private javax.swing.JTable Table_asal;
    private javax.swing.JTable Table_daftarBox;
    private javax.swing.JButton button_count;
    private javax.swing.JButton button_hapus_boxAsal;
    private javax.swing.JButton button_kurang;
    private javax.swing.JButton button_pekerja;
    private javax.swing.JButton button_save;
    private javax.swing.JButton button_tambah;
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
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label_keterangan;
    private javax.swing.JLabel label_kode_repacking;
    private javax.swing.JLabel label_sh_hasil;
    private javax.swing.JLabel label_total_asal;
    private javax.swing.JLabel label_total_daftarBox;
    private javax.swing.JLabel label_total_gram_asal;
    private javax.swing.JLabel label_total_gram_daftarBox;
    private javax.swing.JLabel label_total_gram_hasil;
    private javax.swing.JLabel label_total_hasil;
    private javax.swing.JLabel label_total_keping_asal;
    private javax.swing.JLabel label_total_keping_daftarBox;
    private javax.swing.JLabel label_total_keping_hasil;
    private javax.swing.JTable table_hasil;
    private javax.swing.JTextField txt_Jumlah_keping;
    private javax.swing.JTextField txt_jumlah_berat;
    private javax.swing.JTextField txt_keterangan;
    private javax.swing.JTextField txt_pekerja_id;
    private javax.swing.JTextField txt_pekerja_nama;
    private javax.swing.JTextField txt_search_box;
    // End of variables declaration//GEN-END:variables
}
