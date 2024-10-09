package waleta_system.Cabuto;

import waleta_system.Class.Utility;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class JDialog_Data_User_Tambah_Edit extends javax.swing.JDialog {

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date date = new Date();
    PreparedStatement pst;
    String sql = null;
    ResultSet rs;
    String id_user = null;

    public JDialog_Data_User_Tambah_Edit(java.awt.Frame parent, boolean modal, String id_user) {
        super(parent, modal);
        initComponents();
        this.setResizable(false);
        this.id_user = id_user;
        if (id_user == null) {

        } else {
            try {
                Utility.db_cabuto.connect();
                sql = "SELECT `id_user`, `username`, `password`, `nama_user`, `role`, `induk`, `saldo`, `nik_ktp`, `alamat`, `tanggal_lahir`, `no_hp`, `email`, `tanggal_masuk`, `tanggal_keluar`, `status`, `level` "
                        + "FROM `tb_user` WHERE `id_user` = '" + id_user + "'";
                rs = Utility.db_cabuto.getStatement().executeQuery(sql);
                if (rs.next()) {
                    txt_id_user.setText(rs.getString("id_user"));
                    txt_username.setText(rs.getString("username"));
                    txt_password.setText(rs.getString("password"));
                    ComboBox_role.setSelectedItem(rs.getString("role"));
                    txt_induk.setText(rs.getString("induk"));
                    txt_nik_ktp.setText(rs.getString("nik_ktp"));
                    txt_nama.setText(rs.getString("nama_user"));
                    txt_alamat.setText(rs.getString("alamat"));
                    Date_Lahir.setDate(rs.getDate("tanggal_lahir"));
                    txt_no_hp.setText(rs.getString("no_hp"));
                    txt_email.setText(rs.getString("email"));
                    Date_masuk.setDate(rs.getDate("tanggal_masuk"));
                    Date_keluar.setDate(rs.getDate("tanggal_keluar"));
                    ComboBox_Level.setSelectedItem(rs.getString("level"));
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
                Logger.getLogger(JDialog_Data_User_Tambah_Edit.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void tambah_data() {
        try {
            Utility.db_cabuto.connect();
            Boolean Check = true;
            if (txt_id_user.getText() == null || txt_id_user.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "ID user tidak boleh kosong!");
                Check = false;
            } else if (txt_username.getText() == null || txt_username.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "Username tidak boleh kosong!");
                Check = false;
            } else if (txt_password.getText() == null || txt_password.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "Password tidak boleh kosong!");
                Check = false;
            } else if (Date_masuk.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Tanggal masuk tidak boleh kosong!");
                Check = false;
            } else {
                String query = "SELECT `id_user` FROM `tb_user` WHERE `id_user` = '" + txt_id_user.getText() + "'";
                ResultSet result = Utility.db_cabuto.getStatement().executeQuery(query);
                if (result.next()) {
                    JOptionPane.showMessageDialog(this, "ID user (" + txt_id_user.getText() + ") Sudah terpakai !!");
                    Check = false;
                }
            }

            if (Check) {
                String tgl_keluar = "NULL";
                String status = "IN";
                if (Date_keluar.getDate() != null) {
                    tgl_keluar = "'" + dateFormat.format(Date_keluar.getDate()) + "'";
                    status = "OUT";
                }
                String tgl_lahir = "NULL";
                if (Date_Lahir.getDate() != null) {
                    tgl_lahir = "'" + dateFormat.format(Date_Lahir.getDate()) + "'";
                }
                String Query = "INSERT INTO `tb_user`(`id_user`, `username`, `password`, `nama_user`, `role`, `induk`, `nik_ktp`, `alamat`, `tanggal_lahir`, `no_hp`, `email`, `tanggal_masuk`, `tanggal_keluar`, `status`, `level`) "
                        + "VALUES ("
                        + "'" + txt_id_user.getText() + "',"
                        + "'" + txt_username.getText() + "',"
                        + "'" + txt_password.getText() + "',"
                        + "'" + txt_nama.getText() + "',"
                        + "'" + ComboBox_role.getSelectedItem().toString() + "',"
                        + "'" + txt_induk.getText() + "',"
                        + "'" + txt_nik_ktp.getText() + "',"
                        + "'" + txt_alamat.getText() + "',"
                        + "" + tgl_lahir + ","
                        + "'" + txt_no_hp.getText() + "',"
                        + "'" + txt_email.getText() + "',"
                        + "'" + dateFormat.format(Date_masuk.getDate()) + "',"
                        + "" + tgl_keluar + ","
                        + "'" + status + "',"
                        + "'" + ComboBox_Level.getSelectedItem().toString() + "'"
                        + ")";
                Utility.db_cabuto.getConnection().createStatement();
                Utility.db_cabuto.getStatement().executeUpdate(Query);
                this.dispose();
                JOptionPane.showMessageDialog(this, "Data Saved !");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JDialog_Data_User_Tambah_Edit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void edit_data() {
        try {
            Utility.db_cabuto.connect();
            Boolean Check = true;
            if (txt_id_user.getText() == null || txt_id_user.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "ID user tidak boleh kosong!");
                Check = false;
            } else if (txt_username.getText() == null || txt_username.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "Username tidak boleh kosong!");
                Check = false;
            } else if (txt_password.getText() == null || txt_password.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "Password tidak boleh kosong!");
                Check = false;
            } else if (Date_masuk.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Tanggal masuk tidak boleh kosong!");
                Check = false;
            } else if (!txt_id_user.getText().equals(id_user)) {
                String query = "SELECT `id_user` FROM `tb_user` WHERE `id_user` = '" + txt_id_user.getText() + "'";
                ResultSet result = Utility.db_cabuto.getStatement().executeQuery(query);
                if (result.next()) {
                    JOptionPane.showMessageDialog(this, "ID user (" + txt_id_user.getText() + ") Sudah terpakai !!");
                    Check = false;
                }
            }

            if (Check) {
                String tgl_keluar = "NULL";
                String status = "IN";
                if (Date_keluar.getDate() != null) {
                    tgl_keluar = "'" + dateFormat.format(Date_keluar.getDate()) + "'";
                    status = "OUT";
                }
                String tgl_lahir = "NULL";
                if (Date_Lahir.getDate() != null) {
                    tgl_lahir = "'" + dateFormat.format(Date_Lahir.getDate()) + "'";
                }
                String Query = "UPDATE `tb_user` SET "
                        + "`id_user`='" + txt_id_user.getText() + "',"
                        + "`username`='" + txt_username.getText() + "',"
                        + "`password`='" + txt_password.getText() + "',"
                        + "`nama_user`='" + txt_nama.getText() + "',"
                        + "`role`='" + ComboBox_role.getSelectedItem().toString() + "',"
                        + "`induk`='" + txt_induk.getText() + "',"
                        + "`nik_ktp`='" + txt_nik_ktp.getText() + "',"
                        + "`alamat`='" + txt_alamat.getText() + "',"
                        + "`tanggal_lahir`=" + tgl_lahir + ","
                        + "`no_hp`='" + txt_no_hp.getText() + "',"
                        + "`email`='" + txt_email.getText() + "',"
                        + "`tanggal_masuk`='" + dateFormat.format(Date_masuk.getDate()) + "',"
                        + "`tanggal_keluar`=" + tgl_keluar + ","
                        + "`status`='" + status + "',"
                        + "`level`='" + ComboBox_Level.getSelectedItem().toString() + "' "
                        + "WHERE `id_user`='" + id_user + "'";
                Utility.db_cabuto.getConnection().createStatement();
                Utility.db_cabuto.getStatement().executeUpdate(Query);
                this.dispose();
                JOptionPane.showMessageDialog(this, "Data Saved !");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JDialog_Data_User_Tambah_Edit.class.getName()).log(Level.SEVERE, null, ex);
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
        jLabel9 = new javax.swing.JLabel();
        button_cancel = new javax.swing.JButton();
        button_save = new javax.swing.JButton();
        txt_id_user = new javax.swing.JTextField();
        label_title_terima_lp = new javax.swing.JLabel();
        Date_masuk = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        txt_username = new javax.swing.JTextField();
        txt_password = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txt_induk = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txt_nik_ktp = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txt_nama = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txt_alamat = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txt_no_hp = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txt_email = new javax.swing.JTextField();
        ComboBox_role = new javax.swing.JComboBox<>();
        Date_Lahir = new com.toedter.calendar.JDateChooser();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        Date_keluar = new com.toedter.calendar.JDateChooser();
        ComboBox_Level = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Terima LP Oleh Cabut");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("ID User :");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("Tanggal Masuk :");

        button_cancel.setBackground(new java.awt.Color(255, 255, 255));
        button_cancel.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_cancel.setText("Cancel");
        button_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_cancelActionPerformed(evt);
            }
        });

        button_save.setBackground(new java.awt.Color(255, 255, 255));
        button_save.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_save.setText("Save");
        button_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_saveActionPerformed(evt);
            }
        });

        txt_id_user.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        label_title_terima_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_title_terima_lp.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_title_terima_lp.setText("Data User");

        Date_masuk.setBackground(new java.awt.Color(255, 255, 255));
        Date_masuk.setDateFormatString("dd MMMM yyyy");
        Date_masuk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_masuk.setMaxSelectableDate(new Date());

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Username :");

        txt_username.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        txt_password.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Password :");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Role :");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Induk :");

        txt_induk.setEditable(false);
        txt_induk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_induk.setText("Waleta");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("NIK KTP :");

        txt_nik_ktp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Nama :");

        txt_nama.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("Alamat :");

        txt_alamat.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Tanggal Lahir :");

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("No HP :");

        txt_no_hp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Email :");

        txt_email.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        ComboBox_role.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_role.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Admin", "Pencabut" }));

        Date_Lahir.setBackground(new java.awt.Color(255, 255, 255));
        Date_Lahir.setDateFormatString("dd MMMM yyyy");
        Date_Lahir.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_Lahir.setMaxSelectableDate(new Date());

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Level :");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel15.setText("Tanggal Keluar :");

        Date_keluar.setBackground(new java.awt.Color(255, 255, 255));
        Date_keluar.setDateFormatString("dd MMMM yyyy");
        Date_keluar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_keluar.setMaxSelectableDate(new Date());

        ComboBox_Level.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_Level.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "HIJAU", "KUNING", "ORANYE", "MERAH", "BORONG", "ADMIN" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ComboBox_role, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Date_Lahir, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(txt_id_user)
                    .addComponent(txt_induk)
                    .addComponent(txt_nik_ktp)
                    .addComponent(txt_nama)
                    .addComponent(txt_alamat)
                    .addComponent(txt_no_hp)
                    .addComponent(txt_email)
                    .addComponent(Date_masuk, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(txt_username)
                    .addComponent(txt_password)
                    .addComponent(Date_keluar, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(ComboBox_Level, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(196, Short.MAX_VALUE)
                .addComponent(button_cancel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_save)
                .addGap(10, 10, 10))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(label_title_terima_lp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_title_terima_lp)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_id_user, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_username, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_password, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(ComboBox_role, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_induk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_nik_ktp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_alamat, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(Date_Lahir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_no_hp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_email, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_Level, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_save, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_cancel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
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

    private void button_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_cancelActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_button_cancelActionPerformed

    private void button_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_saveActionPerformed
        if (id_user == null) {
            tambah_data();
        } else {
            edit_data();
        }
    }//GEN-LAST:event_button_saveActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_Level;
    private javax.swing.JComboBox<String> ComboBox_role;
    private com.toedter.calendar.JDateChooser Date_Lahir;
    private com.toedter.calendar.JDateChooser Date_keluar;
    private com.toedter.calendar.JDateChooser Date_masuk;
    private javax.swing.JButton button_cancel;
    private javax.swing.JButton button_save;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel label_title_terima_lp;
    private javax.swing.JTextField txt_alamat;
    private javax.swing.JTextField txt_email;
    private javax.swing.JTextField txt_id_user;
    private javax.swing.JTextField txt_induk;
    private javax.swing.JTextField txt_nama;
    private javax.swing.JTextField txt_nik_ktp;
    private javax.swing.JTextField txt_no_hp;
    private javax.swing.JTextField txt_password;
    private javax.swing.JTextField txt_username;
    // End of variables declaration//GEN-END:variables
}
