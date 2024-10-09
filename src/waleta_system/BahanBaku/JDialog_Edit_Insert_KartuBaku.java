package waleta_system.BahanBaku;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import waleta_system.Class.Utility;

public class JDialog_Edit_Insert_KartuBaku extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String no_kartu = null;
    float berat_real = 0;

    public JDialog_Edit_Insert_KartuBaku(java.awt.Frame parent, boolean modal, String status, String no_kartu) {
        super(parent, modal);
        initComponents();

        try {
            ComboBox_supplier.removeAllItems();
            String query = "SELECT `nama_supplier` FROM `tb_supplier` WHERE `status_aktif` = 1 ORDER BY `nama_supplier` ASC";
            ResultSet rs1 = Utility.db.getStatement().executeQuery(query);
            while (rs1.next()) {
                ComboBox_supplier.addItem(rs1.getString("nama_supplier"));
            }

            ComboBox_RumahBurung.removeAllItems();
            ComboBox_RumahBurung_ct.removeAllItems();
            query = "SELECT `nama_rumah_burung` FROM `tb_rumah_burung` ORDER BY `nama_rumah_burung`";
            rs1 = Utility.db.getStatement().executeQuery(query);
            while (rs1.next()) {
                ComboBox_RumahBurung.addItem(rs1.getString("nama_rumah_burung"));
                ComboBox_RumahBurung_ct.addItem(rs1.getString("nama_rumah_burung"));
            }

            this.no_kartu = no_kartu;
            if (status.equals("insert")) {
                label_kode_rumahBurung1.setVisible(true);
                ComboBox_RumahBurung_ct.setVisible(true);
            } else if (status.equals("update")) {
                label_kode_rumahBurung1.setVisible(false);
                ComboBox_RumahBurung_ct.setVisible(false);
                getDataEdit(no_kartu);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JDialog_Edit_Insert_KartuBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void getDataEdit(String no_kartu) {
        try {
            sql = "SELECT `no_kartu_waleta`, `no_kartu_waleta2`, `no_kartu_pengirim`, `nama_supplier`, `nama_rumah_burung`, `tgl_kh`, `tgl_masuk`, `tgl_panen`, `jumlah_koli`, `berat_awal`, `kadar_air_nota`, `berat_waleta`, `kadar_air_waleta`, `uji_kerapatan`, `uji_kerusakan`, `uji_basah`, `tgl_grading`, `tgl_timbang`, `berat_real`, `keping_real`, `kadar_air_bahan_baku`, `status_kartu_baku`, `surat_keterangan_pengiriman`, `last_stok` "
                    + "FROM `tb_bahan_baku_masuk` "
                    + "LEFT JOIN `tb_supplier` ON `tb_bahan_baku_masuk`.`kode_supplier` = `tb_supplier`.`kode_supplier` "
                    + "LEFT JOIN `tb_rumah_burung` ON `tb_bahan_baku_masuk`.`no_registrasi` = `tb_rumah_burung`.`no_registrasi` "
                    + "WHERE `no_kartu_waleta` = '" + no_kartu + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                txt_no_kartu_pengirim.setText(rs.getString("no_kartu_pengirim"));
                ComboBox_supplier.setSelectedItem(rs.getString("nama_supplier"));
                ComboBox_RumahBurung.setSelectedItem(rs.getString("nama_rumah_burung"));
                Date_KH.setDate(rs.getDate("tgl_kh"));
                Date_Bahan_Baku_Masuk.setDate(rs.getDate("tgl_masuk"));
                Date_Panen.setDate(rs.getDate("tgl_panen"));
                txt_jmlh_koli.setText(rs.getString("jumlah_koli"));
                txt_berat_nota.setText(rs.getString("berat_awal"));
                txt_berat_waleta.setText(rs.getString("berat_waleta"));
                txt_kadar_air_waleta.setText(rs.getString("kadar_air_waleta"));
                berat_real = rs.getFloat("berat_real");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JDialog_Edit_Insert_KartuBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void insert() {
        try {
            int KodeNumber, LastNumber = 0;
            String get_last = "SELECT MAX(RIGHT(`no_kartu_waleta`, 3)) AS 'count_kartu' "
                    + "FROM `tb_bahan_baku_masuk` "
                    + "WHERE `no_kartu_waleta` NOT LIKE '%CMP%' "
                    + "AND `no_kartu_waleta` NOT LIKE '%BY%' "
                    + "AND `no_kartu_waleta` LIKE '" + new SimpleDateFormat("yy").format(Date_Bahan_Baku_Masuk.getDate()) + "%'"
                    + "AND YEAR(`tgl_masuk`) = " + new SimpleDateFormat("yyyy").format(Date_Bahan_Baku_Masuk.getDate());
            ResultSet result_last = Utility.db.getStatement().executeQuery(get_last);
            if (result_last.next()) {
                LastNumber = result_last.getInt("count_kartu") + 1;
            }

            //get kode supplier
            String kode_supplier = null;
            String Query = "SELECT `kode_supplier` FROM `tb_supplier` WHERE `nama_supplier` = '" + ComboBox_supplier.getSelectedItem() + "'";
            ResultSet rs1 = Utility.db.getStatement().executeQuery(Query);
            if (rs1.next()) {
                kode_supplier = rs1.getString("kode_supplier");
            }
            String kode_kartu = new SimpleDateFormat("yy").format(Date_Bahan_Baku_Masuk.getDate()) + kode_supplier + String.format("%03d", LastNumber);

            //get kode rsb
            String kode_rsb = null;
            sql = "SELECT `no_registrasi` FROM `tb_rumah_burung` WHERE `nama_rumah_burung` = '" + ComboBox_RumahBurung.getSelectedItem() + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                kode_rsb = rs.getString("no_registrasi");
            }

            //get kode rsb ct
            String kode_rsb_ct = null;
            sql = "SELECT `no_registrasi` FROM `tb_rumah_burung` WHERE `nama_rumah_burung` = '" + ComboBox_RumahBurung_ct.getSelectedItem() + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                kode_rsb_ct = rs.getString("no_registrasi");
            }
            String kode_kartu2 = kode_rsb_ct + new SimpleDateFormat("yy").format(Date_Bahan_Baku_Masuk.getDate()) + kode_supplier + String.format("%03d", LastNumber);

            float a = Float.valueOf(txt_berat_waleta.getText());
            float b = Float.valueOf(txt_berat_nota.getText());
            float c = Float.valueOf(txt_kadar_air_waleta.getText());
            float KA_nota = (b - (((100 - c) / 100) * a)) / b * 100f;
            Utility.db.getConnection().setAutoCommit(false);
            String Query1 = "INSERT INTO `tb_bahan_baku_masuk`(`no_kartu_waleta`, `no_kartu_waleta2`, `no_kartu_pengirim`, `kode_supplier`, `no_registrasi`, `tgl_kh`, `tgl_masuk`, `tgl_panen`, `jumlah_koli`, `berat_awal`, `kadar_air_nota`, `berat_waleta`, `kadar_air_waleta`)"
                    + " VALUES ('" + kode_kartu + "', '" + kode_kartu2 + "', '" + txt_no_kartu_pengirim.getText() + "','" + kode_supplier + "', '" + kode_rsb + "', '" + dateFormat.format(Date_KH.getDate()) + "','" + dateFormat.format(Date_Bahan_Baku_Masuk.getDate()) + "','" + dateFormat.format(Date_Panen.getDate()) + "','" + txt_jmlh_koli.getText() + "','" + txt_berat_nota.getText() + "','" + decimalFormat.format(KA_nota) + "','" + txt_berat_waleta.getText() + "','" + txt_kadar_air_waleta.getText() + "');";
            Utility.db.getStatement().executeUpdate(Query1);
            String Query2 = "INSERT INTO `tb_bahan_baku_masuk_cheat`(`no_kartu_waleta`, `no_kartu_pengirim`, `kode_supplier`, `no_registrasi`, `tgl_kh`, `tgl_masuk`, `tgl_panen`, `jumlah_koli`, `berat_awal`, `kadar_air_nota`, `berat_waleta`, `kadar_air_waleta`)"
                    + " VALUES ('" + kode_kartu + "','" + txt_no_kartu_pengirim.getText() + "','" + kode_supplier + "', '" + kode_rsb_ct + "', '" + dateFormat.format(Date_KH.getDate()) + "','" + dateFormat.format(Date_Bahan_Baku_Masuk.getDate()) + "','" + dateFormat.format(Date_Panen.getDate()) + "','" + txt_jmlh_koli.getText() + "','" + txt_berat_nota.getText() + "','" + decimalFormat.format(KA_nota) + "','" + txt_berat_waleta.getText() + "','" + txt_kadar_air_waleta.getText() + "');";
            Utility.db.getStatement().executeUpdate(Query2);
            Utility.db.getConnection().commit();
            this.dispose();
            JOptionPane.showMessageDialog(this, "Data berhasil ditambahkan!");
        } catch (Exception e) {
            try {
                Utility.db.getConnection().rollback();
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_Edit_Insert_KartuBaku.class.getName()).log(Level.SEVERE, null, ex);
            }
            JOptionPane.showMessageDialog(this, "data not inserted" + e);
            Logger.getLogger(JDialog_Edit_Insert_KartuBaku.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                Utility.db.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_Edit_Insert_KartuBaku.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void edit() {
        try {
            decimalFormat.setMaximumFractionDigits(2);
            decimalFormat = Utility.DecimalFormatUS(decimalFormat);

            String update_tgl_panen = "";
            if (Date_Panen.getDate() == null) {
                update_tgl_panen = "`tgl_panen` = NULL, ";
            } else {
                update_tgl_panen = "`tgl_panen` = '" + dateFormat.format(Date_Panen.getDate()) + "', ";
            }

            float a = Float.valueOf(txt_berat_waleta.getText());
            float b = Float.valueOf(txt_berat_nota.getText());
            float c = Float.valueOf(txt_kadar_air_waleta.getText());
            float KA_nota = (b - (((100 - c) / 100) * a)) / b * 100f;

            String update_ka_akhir = "";
            if (berat_real > 0) {
                float x = Integer.valueOf(txt_berat_nota.getText());
                float y = berat_real;
                float z = KA_nota;
                float KA_akhir = (y - (((100 - z) / 100) * x)) / y;
                update_ka_akhir = "`kadar_air_bahan_baku`='" + decimalFormat.format(KA_akhir * 100) + "', ";
            }

            sql = "UPDATE `tb_bahan_baku_masuk` SET "
                    + "`no_kartu_pengirim` = '" + txt_no_kartu_pengirim.getText() + "', "
                    + "`kode_supplier` = (SELECT `kode_supplier` FROM `tb_supplier` WHERE `nama_supplier`='" + ComboBox_supplier.getSelectedItem() + "'), "
                    + "`no_registrasi`=(SELECT `no_registrasi` FROM `tb_rumah_burung` WHERE `nama_rumah_burung` = '" + ComboBox_RumahBurung.getSelectedItem() + "'), "
                    + "`tgl_kh` = '" + dateFormat.format(Date_KH.getDate()) + "', "
                    + "`tgl_masuk` = '" + dateFormat.format(Date_Bahan_Baku_Masuk.getDate()) + "', "
                    + update_tgl_panen
                    + update_ka_akhir
                    + "`jumlah_koli` = '" + txt_jmlh_koli.getText() + "', "
                    + "`berat_awal` = '" + txt_berat_nota.getText() + "', "
                    + "`kadar_air_nota` = '" + decimalFormat.format(KA_nota) + "', "
                    + "`berat_waleta` = '" + txt_berat_waleta.getText() + "', "
                    + "`kadar_air_waleta` = '" + txt_kadar_air_waleta.getText() + "' "
                    + "WHERE `tb_bahan_baku_masuk`.`no_kartu_waleta` = '" + no_kartu + "'";
            Utility.db.getConnection().createStatement();
            if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                this.dispose();
                JOptionPane.showMessageDialog(this, "data SAVED !");
            } else {
                JOptionPane.showMessageDialog(this, "Edit data FAILED!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JDialog_Edit_Insert_KartuBaku.class.getName()).log(Level.SEVERE, null, e);
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

        jPanel_operation_bahan_baku = new javax.swing.JPanel();
        label_no_kartu_pengirim = new javax.swing.JLabel();
        label_kode_supplier_bahan_masuk = new javax.swing.JLabel();
        label_tgl_kh = new javax.swing.JLabel();
        label_tgl_masuk = new javax.swing.JLabel();
        label_jumlah_koli = new javax.swing.JLabel();
        label_berat_nota = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        button_SAVE = new javax.swing.JButton();
        txt_no_kartu_pengirim = new javax.swing.JTextField();
        txt_jmlh_koli = new javax.swing.JTextField();
        txt_berat_nota = new javax.swing.JTextField();
        Date_KH = new com.toedter.calendar.JDateChooser();
        Date_Bahan_Baku_Masuk = new com.toedter.calendar.JDateChooser();
        ComboBox_supplier = new javax.swing.JComboBox<>();
        label_kode_rumahBurung = new javax.swing.JLabel();
        ComboBox_RumahBurung = new javax.swing.JComboBox<>();
        label_tgl_masuk1 = new javax.swing.JLabel();
        Date_Panen = new com.toedter.calendar.JDateChooser();
        label_berat_waleta = new javax.swing.JLabel();
        txt_berat_waleta = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        label_kadar_air_awal = new javax.swing.JLabel();
        txt_kadar_air_waleta = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        label_kode_rumahBurung1 = new javax.swing.JLabel();
        ComboBox_RumahBurung_ct = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel_operation_bahan_baku.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_operation_bahan_baku.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Bahan Baku Masuk", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 0, 14))); // NOI18N
        jPanel_operation_bahan_baku.setName("aah"); // NOI18N

        label_no_kartu_pengirim.setBackground(new java.awt.Color(255, 255, 255));
        label_no_kartu_pengirim.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_no_kartu_pengirim.setText("No. Kartu Pengirim :");

        label_kode_supplier_bahan_masuk.setBackground(new java.awt.Color(255, 255, 255));
        label_kode_supplier_bahan_masuk.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_kode_supplier_bahan_masuk.setText("Nama Supplier :");

        label_tgl_kh.setBackground(new java.awt.Color(255, 255, 255));
        label_tgl_kh.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_tgl_kh.setText("Tanggal KH :");

        label_tgl_masuk.setBackground(new java.awt.Color(255, 255, 255));
        label_tgl_masuk.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_tgl_masuk.setText("Tanggal Masuk :");

        label_jumlah_koli.setBackground(java.awt.Color.white);
        label_jumlah_koli.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_jumlah_koli.setText("Jumlah Koli :");

        label_berat_nota.setBackground(java.awt.Color.white);
        label_berat_nota.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_berat_nota.setText("Berat Awal / Nota :");

        jLabel9.setBackground(java.awt.Color.white);
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("Koli");

        jLabel10.setBackground(java.awt.Color.white);
        jLabel10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel10.setText("Gram");

        button_SAVE.setBackground(new java.awt.Color(255, 255, 255));
        button_SAVE.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_SAVE.setText("SAVE");
        button_SAVE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_SAVEActionPerformed(evt);
            }
        });

        txt_no_kartu_pengirim.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        txt_jmlh_koli.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        txt_berat_nota.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        Date_KH.setBackground(new java.awt.Color(255, 255, 255));
        Date_KH.setDateFormatString("dd MMM yyyy");
        Date_KH.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Date_KH.setMaxSelectableDate(new Date());

        Date_Bahan_Baku_Masuk.setBackground(new java.awt.Color(255, 255, 255));
        Date_Bahan_Baku_Masuk.setDateFormatString("dd MMM yyyy");
        Date_Bahan_Baku_Masuk.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Date_Bahan_Baku_Masuk.setMaxSelectableDate(new Date());

        ComboBox_supplier.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        label_kode_rumahBurung.setBackground(new java.awt.Color(255, 255, 255));
        label_kode_rumahBurung.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_kode_rumahBurung.setText("Rumah Burung :");

        ComboBox_RumahBurung.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        label_tgl_masuk1.setBackground(new java.awt.Color(255, 255, 255));
        label_tgl_masuk1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_tgl_masuk1.setText("Tanggal Panen :");

        Date_Panen.setBackground(new java.awt.Color(255, 255, 255));
        Date_Panen.setDateFormatString("dd MMM yyyy");
        Date_Panen.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Date_Panen.setMaxSelectableDate(new Date());

        label_berat_waleta.setBackground(java.awt.Color.white);
        label_berat_waleta.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_berat_waleta.setText("Berat Waleta :");

        txt_berat_waleta.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel24.setBackground(java.awt.Color.white);
        jLabel24.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel24.setText("Gram");

        label_kadar_air_awal.setBackground(java.awt.Color.white);
        label_kadar_air_awal.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_kadar_air_awal.setText("Kadar Air Waleta :");

        txt_kadar_air_waleta.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel23.setBackground(java.awt.Color.white);
        jLabel23.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel23.setText("%");

        label_kode_rumahBurung1.setBackground(new java.awt.Color(255, 255, 255));
        label_kode_rumahBurung1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_kode_rumahBurung1.setText("Rumah Burung CT :");

        ComboBox_RumahBurung_ct.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        javax.swing.GroupLayout jPanel_operation_bahan_bakuLayout = new javax.swing.GroupLayout(jPanel_operation_bahan_baku);
        jPanel_operation_bahan_baku.setLayout(jPanel_operation_bahan_bakuLayout);
        jPanel_operation_bahan_bakuLayout.setHorizontalGroup(
            jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_operation_bahan_bakuLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_kadar_air_awal, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_berat_waleta, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_berat_nota, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_jumlah_koli, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_tgl_masuk1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_tgl_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_tgl_kh, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kode_rumahBurung, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kode_supplier_bahan_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_no_kartu_pengirim, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kode_rumahBurung1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Date_Panen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Date_Bahan_Baku_Masuk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Date_KH, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ComboBox_RumahBurung, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ComboBox_supplier, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txt_no_kartu_pengirim)
                    .addComponent(ComboBox_RumahBurung_ct, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel_operation_bahan_bakuLayout.createSequentialGroup()
                        .addGroup(jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(button_SAVE)
                            .addGroup(jPanel_operation_bahan_bakuLayout.createSequentialGroup()
                                .addGroup(jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txt_kadar_air_waleta, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_berat_waleta, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_berat_nota, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_jmlh_koli, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel23)
                                    .addComponent(jLabel24)
                                    .addComponent(jLabel10)
                                    .addComponent(jLabel9))))
                        .addGap(0, 25, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel_operation_bahan_bakuLayout.setVerticalGroup(
            jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_operation_bahan_bakuLayout.createSequentialGroup()
                .addGroup(jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_no_kartu_pengirim, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_no_kartu_pengirim, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_kode_supplier_bahan_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_kode_rumahBurung, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_RumahBurung, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_kode_rumahBurung1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_RumahBurung_ct, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(Date_KH, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_tgl_kh, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(Date_Bahan_Baku_Masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_tgl_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(Date_Panen, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_tgl_masuk1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_operation_bahan_bakuLayout.createSequentialGroup()
                        .addComponent(label_jumlah_koli, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_berat_nota, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_operation_bahan_bakuLayout.createSequentialGroup()
                        .addGroup(jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_jmlh_koli, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_berat_nota, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_berat_waleta, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_berat_waleta, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_kadar_air_awal, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_kadar_air_waleta, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(button_SAVE, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_operation_bahan_baku, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_operation_bahan_baku, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void button_SAVEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_SAVEActionPerformed
        // TODO add your handling code here:
        Boolean Check = true;

        if ("".equals(txt_berat_nota.getText())
                || Date_KH.getDate() == null
                || Date_Bahan_Baku_Masuk.getDate() == null
                || Date_Panen.getDate() == null
                || "".equals(txt_jmlh_koli.getText())
                || "".equals(txt_berat_nota.getText())
                || "".equals(txt_berat_waleta.getText())
                || "".equals(txt_kadar_air_waleta.getText())) {
            JOptionPane.showMessageDialog(this, "Silahkan Cek dan Lengkapi Data diatas !");
            Check = false;
        }

        if (Check) {
            if (no_kartu == null) {
                insert();
            } else {
                edit();
            }
        }
    }//GEN-LAST:event_button_SAVEActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_RumahBurung;
    private javax.swing.JComboBox<String> ComboBox_RumahBurung_ct;
    private javax.swing.JComboBox<String> ComboBox_supplier;
    private com.toedter.calendar.JDateChooser Date_Bahan_Baku_Masuk;
    private com.toedter.calendar.JDateChooser Date_KH;
    private com.toedter.calendar.JDateChooser Date_Panen;
    public javax.swing.JButton button_SAVE;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel_operation_bahan_baku;
    private javax.swing.JLabel label_berat_nota;
    private javax.swing.JLabel label_berat_waleta;
    private javax.swing.JLabel label_jumlah_koli;
    private javax.swing.JLabel label_kadar_air_awal;
    private javax.swing.JLabel label_kode_rumahBurung;
    private javax.swing.JLabel label_kode_rumahBurung1;
    private javax.swing.JLabel label_kode_supplier_bahan_masuk;
    private javax.swing.JLabel label_no_kartu_pengirim;
    private javax.swing.JLabel label_tgl_kh;
    private javax.swing.JLabel label_tgl_masuk;
    private javax.swing.JLabel label_tgl_masuk1;
    private javax.swing.JTextField txt_berat_nota;
    private javax.swing.JTextField txt_berat_waleta;
    private javax.swing.JTextField txt_jmlh_koli;
    private javax.swing.JTextField txt_kadar_air_waleta;
    private javax.swing.JTextField txt_no_kartu_pengirim;
    // End of variables declaration//GEN-END:variables
}
