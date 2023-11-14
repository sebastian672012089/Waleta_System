package waleta_system.Panel_produksi;

import waleta_system.Class.Utility;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import waleta_system.Browse_Karyawan;

public class JDialog_Edit_Data_Cetak_Detail extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DecimalFormat decimalFormat = new DecimalFormat();
    String nomor;
    float berat_basah_lp = 0, total_keping_lp = 0;

    public JDialog_Edit_Data_Cetak_Detail(java.awt.Frame parent, boolean modal, String nomor, String no_lp) {
        super(parent, modal);
        try {
            this.setResizable(false);
            initComponents();
            label_no_lp.setText(no_lp);
            if (nomor != null) {
                this.nomor = nomor;
                getdata(nomor);
            }

            String query = "SELECT `berat_basah`, `jumlah_keping`, `keping_upah` FROM `tb_laporan_produksi` WHERE `no_laporan_produksi` = '" + no_lp + "'";
            ResultSet result = Utility.db.getStatement().executeQuery(query);
            if (result.next()) {
                berat_basah_lp = result.getFloat("berat_basah");
                total_keping_lp = result.getFloat("keping_upah");
            }
        } catch (SQLException ex) {
            Logger.getLogger(JDialog_Edit_Data_Cetak_Detail.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void getdata(String nomor) {
        try {
            int i = JPanel_DataCetak.Table_Data_Cetak.getSelectedRow();
            sql = "SELECT `nomor`, `no_laporan_produksi`, `tanggal_cetak`, `tb_detail_pencetak`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `bagian`, `kpg_cetak`, `gram_cetak`, "
                    + "`cetak_mangkok`, `cetak_pecah`, `cetak_flat`, `cetak_jidun`, `cetak_jidun_real` "
                    + "FROM `tb_detail_pencetak` \n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_detail_pencetak`.`id_pegawai` = `tb_karyawan`.`id_pegawai` \n"
                    + "WHERE `nomor` = '" + nomor + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                txt_id_pekerja_cetak.setText(rs.getString("id_pegawai"));
                txt_nama_pekerja_cetak.setText(rs.getString("nama_pegawai"));
                txt_bagian_pekerja_cetak.setText(rs.getString("bagian"));
                Date_pekerja_cetak.setDate(rs.getDate("tanggal_cetak"));
                txt_keping_pekerja_cetak.setText(rs.getString("kpg_cetak"));
                txt_mk.setText(rs.getString("cetak_mangkok"));
                txt_mk_pecah.setText(rs.getString("cetak_pecah"));
                txt_flat.setText(rs.getString("cetak_flat"));
                txt_jidun.setText(rs.getString("cetak_jidun"));
                txt_jidun_real.setText(rs.getString("cetak_jidun_real"));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_Edit_Data_Cetak_Detail.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void add(int mk, int pecah, int flat, int jidun, int jidun_real) {
        try {
            decimalFormat.setMaximumFractionDigits(2);
            Boolean Check = true;

            String no_lp = label_no_lp.getText();
            String id_pekerja_cetak = txt_id_pekerja_cetak.getText();
            String nama_pekerja_cetak = txt_nama_pekerja_cetak.getText();
            String bagian_pekerja_cetak = txt_bagian_pekerja_cetak.getText();
            String tanggal_cetak = dateFormat.format(Date_pekerja_cetak.getDate());

            sql = "SELECT `nomor` FROM `tb_detail_pencetak` WHERE "
                    + "`no_laporan_produksi` = '" + no_lp + "' \n"
                    + "AND `tanggal_cetak` = '" + tanggal_cetak + "' \n"
                    + "AND `id_pegawai` = '" + id_pekerja_cetak + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Maaf, " + nama_pekerja_cetak + " pada tanggal " + tanggal_cetak + " sudah mengerjakan lp " + no_lp);
                Check = false;
            }

            if (Check) {
                float kpg_cetak = Float.valueOf(txt_keping_pekerja_cetak.getText());
                float gram = 0;
                gram = (berat_basah_lp / total_keping_lp) * kpg_cetak;

                String Query = "INSERT INTO `tb_detail_pencetak`(`no_laporan_produksi`, `id_pegawai`, `bagian`, `tanggal_cetak`, `kpg_cetak`, `gram_cetak`, `cetak_mangkok`, `cetak_pecah`, `cetak_flat`, `cetak_jidun`, `cetak_jidun_real`) "
                        + "VALUES ("
                        + "'" + no_lp + "',"
                        + "'" + id_pekerja_cetak + "', "
                        + "'" + bagian_pekerja_cetak + "', "
                        + "'" + tanggal_cetak + "', "
                        + "'" + kpg_cetak + "', "
                        + "'" + gram + "', "
                        + "'" + mk + "', "
                        + "'" + pecah + "', "
                        + "'" + flat + "', "
                        + "'" + jidun + "', "
                        + "'" + jidun_real + "' "
                        + ")";
                Utility.db.getConnection().createStatement();
                if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                    JOptionPane.showMessageDialog(this, "data inserted Successfully");
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "insert failed!");
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_Edit_Data_Cetak_Detail.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void edit(int mk, int pecah, int flat, int jidun, int jidun_real) {
        try {
            String id_pekerja_cetak = txt_id_pekerja_cetak.getText();
            String bagian_pekerja_cetak = txt_bagian_pekerja_cetak.getText();
            String tanggal_cetak = dateFormat.format(Date_pekerja_cetak.getDate());

            float kpg_cetak = Float.valueOf(txt_keping_pekerja_cetak.getText());
            float gram = 0;
            gram = (berat_basah_lp / total_keping_lp) * kpg_cetak;

            String Query = "UPDATE `tb_detail_pencetak` SET "
                    + "`id_pegawai`='" + id_pekerja_cetak + "',"
                    + "`bagian`='" + bagian_pekerja_cetak + "', "
                    + "`tanggal_cetak`='" + tanggal_cetak + "',"
                    + "`kpg_cetak`='" + kpg_cetak + "', "
                    + "`gram_cetak`='" + gram + "', "
                    + "`cetak_mangkok`='" + mk + "', "
                    + "`cetak_pecah`='" + pecah + "', "
                    + "`cetak_flat`='" + flat + "', "
                    + "`cetak_jidun`='" + jidun + "', "
                    + "`cetak_jidun_real`='" + jidun_real + "' "
                    + "WHERE `nomor`='" + nomor + "'";
            Utility.db.getConnection().createStatement();
            if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                JOptionPane.showMessageDialog(this, "data UPDATED Successfully");
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Tidak ada perubahan data!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_DataCetak.class.getName()).log(Level.SEVERE, null, e);
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

        jPanel3 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        txt_id_pekerja_cetak = new javax.swing.JTextField();
        txt_nama_pekerja_cetak = new javax.swing.JTextField();
        txt_keping_pekerja_cetak = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        Date_pekerja_cetak = new com.toedter.calendar.JDateChooser();
        button_pick_pencabut = new javax.swing.JButton();
        jLabel26 = new javax.swing.JLabel();
        txt_bagian_pekerja_cetak = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txt_mk = new javax.swing.JTextField();
        txt_mk_pecah = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txt_flat = new javax.swing.JTextField();
        txt_jidun = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        txt_jidun_real = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        button_save = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        label_no_lp = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Edit Data Cetak");
        setResizable(false);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel21.setText("ID Pegawai :");

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel22.setText("Nama Pegawai :");

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel23.setText("Tanggal :");

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel24.setText("Jumlah Cetak :");

        txt_id_pekerja_cetak.setEditable(false);
        txt_id_pekerja_cetak.setBackground(new java.awt.Color(255, 255, 255));
        txt_id_pekerja_cetak.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        txt_nama_pekerja_cetak.setEditable(false);
        txt_nama_pekerja_cetak.setBackground(new java.awt.Color(255, 255, 255));
        txt_nama_pekerja_cetak.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        txt_keping_pekerja_cetak.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel25.setText("Keping");

        Date_pekerja_cetak.setBackground(new java.awt.Color(255, 255, 255));
        Date_pekerja_cetak.setDate(new Date());
        Date_pekerja_cetak.setDateFormatString("dd MMM yyyy");
        Date_pekerja_cetak.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_pick_pencabut.setBackground(new java.awt.Color(255, 255, 255));
        button_pick_pencabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_pick_pencabut.setText("...");
        button_pick_pencabut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_pick_pencabutActionPerformed(evt);
            }
        });

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel26.setText("Bagian :");

        txt_bagian_pekerja_cetak.setEditable(false);
        txt_bagian_pekerja_cetak.setBackground(new java.awt.Color(255, 255, 255));
        txt_bagian_pekerja_cetak.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Mangkok Pecah :");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Flat :");

        txt_mk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_mk.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_mk.setText("0");

        txt_mk_pecah.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_mk_pecah.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_mk_pecah.setText("0");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("Jidun :");

        txt_flat.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_flat.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_flat.setText("0");

        txt_jidun.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_jidun.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_jidun.setText("0");

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel16.setText("Keping");

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel19.setText("Jidun Real :");

        txt_jidun_real.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_jidun_real.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_jidun_real.setText("0");

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel20.setText("Keping");

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("Keping");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Keping");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Keping");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Mangkok Utuh :");

        button_save.setBackground(new java.awt.Color(255, 255, 255));
        button_save.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_save.setText("Save");
        button_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_saveActionPerformed(evt);
            }
        });

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel1.setText("Data Pekerja Cetak");

        label_no_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_no_lp.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        label_no_lp.setText("NO. Laporan Produksi");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_id_pekerja_cetak)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(txt_keping_pekerja_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel25)
                                .addGap(0, 189, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_pick_pencabut, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txt_nama_pekerja_cetak)
                    .addComponent(txt_bagian_pekerja_cetak)
                    .addComponent(Date_pekerja_cetak, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_mk, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_mk_pecah, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_flat, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_jidun, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_jidun_real, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(jLabel13)
                            .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(label_no_lp))
                .addGap(154, 154, 154))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(button_save)
                .addGap(10, 10, 10))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_no_lp)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_id_pekerja_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_pick_pencabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_nama_pekerja_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_bagian_pekerja_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_pekerja_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_keping_pekerja_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_mk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_mk_pecah, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_flat, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_jidun, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_jidun_real, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(button_save, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void button_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_saveActionPerformed
        // TODO add your handling code here:
        boolean Check = true;
        int kpg = 0, mk = 0, pecah = 0, flat = 0, jidun = 0, jidun_real = 0;
        try {
            if (txt_keping_pekerja_cetak.getText() != null || !txt_keping_pekerja_cetak.getText().equals("")) {
                kpg = Integer.valueOf(txt_keping_pekerja_cetak.getText());
            }
            if (txt_mk.getText() != null && !txt_mk.getText().equals("")) {
                mk = Integer.valueOf(txt_mk.getText());
            }
            if (txt_mk_pecah.getText() != null && !txt_mk_pecah.getText().equals("")) {
                pecah = Integer.valueOf(txt_mk_pecah.getText());
            }
            if (txt_flat.getText() != null && !txt_flat.getText().equals("")) {
                flat = Integer.valueOf(txt_flat.getText());
            }
            if (txt_jidun.getText() != null && !txt_jidun.getText().equals("")) {
                jidun = Integer.valueOf(txt_jidun.getText());
            }
            if (txt_jidun_real.getText() != null && !txt_jidun_real.getText().equals("")) {
                jidun_real = Integer.valueOf(txt_jidun_real.getText());
            }
            if (txt_id_pekerja_cetak.getText() == null || txt_id_pekerja_cetak.getText().equals("")
                    || Date_pekerja_cetak.getDate() == null) {
                JOptionPane.showMessageDialog(this, "failed!, Silahkan lengkapi data diatas!");
                Check = false;
            } else {
                String absen = "SELECT * FROM `att_log` WHERE "
                        + "DATE(`scan_date`) = '" + dateFormat.format(Date_pekerja_cetak.getDate()) + "' "
                        + "AND `pin` = '" + Integer.valueOf(txt_id_pekerja_cetak.getText().substring(7)) + "'";
                ResultSet result_absen = Utility.db.getStatement().executeQuery(absen);
                if (!result_absen.next()) {
                    JOptionPane.showMessageDialog(this, "Maaf, Pegawai tidak masuk pada tanggal tersebut");
                    Check = false;
                }
            }
        } catch (Exception e) {
            Logger.getLogger(JPanel_DataCetak.class.getName()).log(Level.SEVERE, null, e);
            JOptionPane.showMessageDialog(this, "failed!, Inputan Salah!\nSilahkan cek kembali, Terima kasih !");
            Check = false;
        }

        if (Check) {
            if (nomor == null) {
                add(mk, pecah, flat, jidun, jidun_real);
            } else {
                edit(mk, pecah, flat, jidun, jidun_real);
            }
        }
    }//GEN-LAST:event_button_saveActionPerformed

    private void button_pick_pencabutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_pick_pencabutActionPerformed
        // TODO add your handling code here:
        String filter_tgl = "AND ((`tb_bagian`.`kode_departemen` = 'PRODUKSI' AND DATE(`att_log`.`scan_date`) = CURRENT_DATE()) OR `tb_bagian`.`kode_departemen` = 'SUB') ";
        Browse_Karyawan dialog = new Browse_Karyawan(new javax.swing.JFrame(), true, filter_tgl);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);

        int x = Browse_Karyawan.table_list_karyawan.getSelectedRow();
        if (x != -1) {
            txt_id_pekerja_cetak.setText(Browse_Karyawan.table_list_karyawan.getValueAt(x, 0).toString());
            txt_nama_pekerja_cetak.setText(Browse_Karyawan.table_list_karyawan.getValueAt(x, 1).toString());
            txt_bagian_pekerja_cetak.setText(Browse_Karyawan.table_list_karyawan.getValueAt(x, 2).toString());

            if (!txt_bagian_pekerja_cetak.getText().toUpperCase().contains("CETAK")) {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Apakah anda yakin pegawai bukan dari bagian CETAK?", "Warning", JOptionPane.YES_NO_OPTION);
                if (dialogResult != JOptionPane.YES_OPTION) {
                    txt_id_pekerja_cetak.setText(null);
                    txt_nama_pekerja_cetak.setText(null);
                    txt_bagian_pekerja_cetak.setText(null);
                }
            }
        }
    }//GEN-LAST:event_button_pick_pencabutActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser Date_pekerja_cetak;
    private javax.swing.JButton button_pick_pencabut;
    private javax.swing.JButton button_save;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel label_no_lp;
    private javax.swing.JTextField txt_bagian_pekerja_cetak;
    private javax.swing.JTextField txt_flat;
    private javax.swing.JTextField txt_id_pekerja_cetak;
    private javax.swing.JTextField txt_jidun;
    private javax.swing.JTextField txt_jidun_real;
    private javax.swing.JTextField txt_keping_pekerja_cetak;
    private javax.swing.JTextField txt_mk;
    private javax.swing.JTextField txt_mk_pecah;
    private javax.swing.JTextField txt_nama_pekerja_cetak;
    // End of variables declaration//GEN-END:variables
}
