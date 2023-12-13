package waleta_system.Finance;

import java.awt.event.KeyEvent;
import waleta_system.Class.Utility;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import waleta_system.MainForm;

public class JDialog_Aset_PengajuanPembelian_NewEdit extends javax.swing.JDialog {

    private String sql = null;
    private ResultSet rs;
    private String operand = null;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DecimalFormat decimalFormat = new DecimalFormat();

    public JDialog_Aset_PengajuanPembelian_NewEdit(java.awt.Frame parent, boolean modal, String no) {
        super(parent, modal);
        initComponents();
        this.setResizable(false);
        try {
            ComboBox_departemen.removeAllItems();
            sql = "SELECT `kode_dep` FROM `tb_departemen` WHERE 1";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_departemen.addItem(rs.getString("kode_dep"));
            }

            if (no == null) {
                this.operand = "tambah";
                txt_diajukan_id.setText(MainForm.Login_idPegawai);
                txt_diajukan_nama.setText(MainForm.Login_NamaPegawai);
            } else {
                this.operand = "edit";
                txt_no.setText(no);

                sql = "SELECT `no`, `tanggal_pengajuan`, `departemen`, `keperluan`, `nama_barang`, `jumlah`, `estimasi_harga_satuan`, `link_pembelian`, `dibutuhkan_tanggal`, `diajukan`, `tb_karyawan`.`nama_pegawai`, `diketahui`, `disetujui`, `diproses`, `tb_aset_pengajuan`.`status`, `tb_aset_pengajuan`.`keterangan` \n"
                        + "FROM `tb_aset_pengajuan` \n"
                        + "LEFT JOIN `tb_karyawan` ON `tb_aset_pengajuan`.`diajukan` = `tb_karyawan`.`id_pegawai`\n"
                        + "WHERE `no` = '" + no + "'";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    txt_no.setText(rs.getString("no"));
                    Date_pengajuan.setDate(rs.getDate("tanggal_pengajuan"));
                    ComboBox_departemen.setSelectedItem(rs.getString("departemen"));
                    txt_keperluan.setText(rs.getString("keperluan"));
                    txt_nama_barang.setText(rs.getString("nama_barang"));
                    Spinner_jumlah.setValue(rs.getInt("jumlah"));
                    txt_estimasi_harga_satuan.setText(rs.getString("estimasi_harga_satuan"));
                    txt_keterangan.setText(rs.getString("keterangan"));
                    txt_link.setText(rs.getString("link_pembelian"));
                    Date_dibutuhkan.setDate(rs.getDate("dibutuhkan_tanggal"));
                    txt_diajukan_id.setText(rs.getString("diajukan"));
                    txt_diajukan_nama.setText(rs.getString("nama_pegawai"));
                }
                hitung_total_harga();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_Aset_PengajuanPembelian_NewEdit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void hitung_total_harga() {
        try {
            int jumlah = (int) Spinner_jumlah.getValue();
            int harga = txt_estimasi_harga_satuan.getText() == null || txt_estimasi_harga_satuan.getText().equals("") ? 0 : Integer.valueOf(txt_estimasi_harga_satuan.getText());
            int total = jumlah * harga;
            txt_estimasi_harga_total.setText("Rp. " + decimalFormat.format(total));
        } catch (Exception ex) {
            Logger.getLogger(JDialog_Aset_PengajuanPembelian_NewEdit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void baru() {
        try {
            String link = txt_link.getText() == null ? "" : txt_link.getText();
            String harga = txt_estimasi_harga_satuan.getText() == null || txt_estimasi_harga_satuan.getText().equals("") ? "0" : txt_estimasi_harga_satuan.getText();
            sql = "INSERT INTO `tb_aset_pengajuan`(`tanggal_pengajuan`, `departemen`, `keperluan`, `nama_barang`, `jumlah`, `estimasi_harga_satuan`, `link_pembelian`, `dibutuhkan_tanggal`, `diajukan`, `status`, `keterangan`) "
                    + "VALUES ("
                    + "'" + dateFormat.format(Date_pengajuan.getDate()) + "', "
                    + "'" + ComboBox_departemen.getSelectedItem().toString() + "', "
                    + "'" + txt_keperluan.getText() + "', "
                    + "'" + txt_nama_barang.getText() + "', "
                    + "'" + Spinner_jumlah.getValue().toString() + "', "
                    + "'" + harga + "', "
                    + "'" + link + "', "
                    + "'" + dateFormat.format(Date_dibutuhkan.getDate()) + "', "
                    + "'" + txt_diajukan_id.getText() + "', "
                    + "'Diajukan', "
                    + "'" + txt_keterangan.getText() + "'"
                    + ")";
            Utility.db.getConnection().createStatement();
            if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                this.dispose();
                JOptionPane.showMessageDialog(this, "Data Berhasil simpan");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_Aset_PengajuanPembelian_NewEdit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void edit() {
        try {
            String link = txt_link.getText() == null ? "" : txt_link.getText();
            String harga = txt_estimasi_harga_satuan.getText() == null || txt_estimasi_harga_satuan.getText().equals("") ? "0" : txt_estimasi_harga_satuan.getText();
            sql = "UPDATE `tb_aset_pengajuan` SET "
                    + "`tanggal_pengajuan`='" + dateFormat.format(Date_pengajuan.getDate()) + "',"
                    + "`departemen`='" + ComboBox_departemen.getSelectedItem().toString() + "',"
                    + "`keperluan`='" + txt_keperluan.getText() + "',"
                    + "`nama_barang`='" + txt_nama_barang.getText() + "',"
                    + "`jumlah`='" + Spinner_jumlah.getValue().toString() + "',"
                    + "`estimasi_harga_satuan`='" + harga + "',"
                    + "`link_pembelian`='" + link + "',"
                    + "`dibutuhkan_tanggal`='" + dateFormat.format(Date_dibutuhkan.getDate()) + "', "
                    + "`keterangan`='" + txt_keterangan.getText() + "' "
                    + "WHERE `no`='" + txt_no.getText() + "'";
            Utility.db.getConnection().createStatement();
            if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                this.dispose();
                JOptionPane.showMessageDialog(this, "Data Berhasil simpan");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_Aset_PengajuanPembelian_NewEdit.class.getName()).log(Level.SEVERE, null, ex);
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
        label_title = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        button_save = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txt_nama_barang = new javax.swing.JTextField();
        txt_no = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        Date_pengajuan = new com.toedter.calendar.JDateChooser();
        ComboBox_departemen = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        Date_dibutuhkan = new com.toedter.calendar.JDateChooser();
        jLabel14 = new javax.swing.JLabel();
        txt_diajukan_id = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        txt_keperluan = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        txt_keterangan = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        txt_link = new javax.swing.JTextArea();
        txt_diajukan_nama = new javax.swing.JTextField();
        Spinner_jumlah = new javax.swing.JSpinner();
        jLabel15 = new javax.swing.JLabel();
        txt_estimasi_harga_satuan = new javax.swing.JTextField();
        txt_estimasi_harga_total = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        label_title.setBackground(new java.awt.Color(255, 255, 255));
        label_title.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_title.setText("Pengajuan Pembelian Alat Kerja");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("No :");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("Keperluan :");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Tanggal Pengajuan :");

        button_save.setBackground(new java.awt.Color(255, 255, 255));
        button_save.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_save.setText("Save");
        button_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_saveActionPerformed(evt);
            }
        });

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Nama Barang :");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText("Jumlah :");

        txt_nama_barang.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        txt_no.setEditable(false);
        txt_no.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel10.setText("Departemen :");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel11.setText("Keterangan* :");

        Date_pengajuan.setBackground(new java.awt.Color(255, 255, 255));
        Date_pengajuan.setDateFormatString("dd MMM yyyy");

        ComboBox_departemen.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel12.setText("Link Pembelian* :");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 2, 10)); // NOI18N
        jLabel13.setText("*) Opsional, boleh kosong");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("Dibutuhkan Tanggal :");

        Date_dibutuhkan.setBackground(new java.awt.Color(255, 255, 255));
        Date_dibutuhkan.setDateFormatString("dd MMM yyyy");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel14.setText("Diajukan Oleh :");

        txt_diajukan_id.setEditable(false);
        txt_diajukan_id.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        txt_keperluan.setColumns(20);
        txt_keperluan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_keperluan.setLineWrap(true);
        txt_keperluan.setRows(3);
        jScrollPane1.setViewportView(txt_keperluan);

        txt_keterangan.setColumns(20);
        txt_keterangan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_keterangan.setLineWrap(true);
        txt_keterangan.setRows(3);
        jScrollPane2.setViewportView(txt_keterangan);

        txt_link.setColumns(20);
        txt_link.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_link.setLineWrap(true);
        txt_link.setRows(3);
        jScrollPane3.setViewportView(txt_link);

        txt_diajukan_nama.setEditable(false);
        txt_diajukan_nama.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        Spinner_jumlah.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Spinner_jumlah.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
        Spinner_jumlah.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                Spinner_jumlahFocusLost(evt);
            }
        });

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel15.setText("Estimasi Harga Satuan :");

        txt_estimasi_harga_satuan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_estimasi_harga_satuan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_estimasi_harga_satuanKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_estimasi_harga_satuanKeyTyped(evt);
            }
        });

        txt_estimasi_harga_total.setEditable(false);
        txt_estimasi_harga_total.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel16.setText("Estimasi Harga Total :");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(button_save)
                .addGap(10, 10, 10))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_no)
                            .addComponent(Date_dibutuhkan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txt_diajukan_id)
                            .addComponent(Date_pengajuan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
                            .addComponent(ComboBox_departemen, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
                            .addComponent(txt_nama_barang)
                            .addComponent(txt_diajukan_nama)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(Spinner_jumlah, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(txt_estimasi_harga_satuan)
                            .addComponent(txt_estimasi_harga_total)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(label_title)))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_title, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_no, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_pengajuan, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_departemen, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_nama_barang, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Spinner_jumlah, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_estimasi_harga_satuan, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_estimasi_harga_total, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_dibutuhkan, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_diajukan_id, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_diajukan_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_save, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void button_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_saveActionPerformed
        // TODO add your handling code here:
        if (Date_pengajuan.getDate() == null || Date_dibutuhkan.getDate() == null
                || txt_keperluan.getText() == null || txt_keperluan.getText().equals("")
                || txt_nama_barang.getText() == null || txt_nama_barang.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Silahkan lengkapi data diatas!");
        } else {
            if (operand.equals("tambah")) {
                baru();
            } else if (operand.equals("edit")) {
                edit();
            }
        }
    }//GEN-LAST:event_button_saveActionPerformed

    private void txt_estimasi_harga_satuanKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_estimasi_harga_satuanKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_estimasi_harga_satuanKeyTyped

    private void Spinner_jumlahFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_Spinner_jumlahFocusLost
        // TODO add your handling code here:
        hitung_total_harga();
    }//GEN-LAST:event_Spinner_jumlahFocusLost

    private void txt_estimasi_harga_satuanKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_estimasi_harga_satuanKeyReleased
        // TODO add your handling code here:
        hitung_total_harga();
    }//GEN-LAST:event_txt_estimasi_harga_satuanKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_departemen;
    private com.toedter.calendar.JDateChooser Date_dibutuhkan;
    private com.toedter.calendar.JDateChooser Date_pengajuan;
    private javax.swing.JSpinner Spinner_jumlah;
    private javax.swing.JButton button_save;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel label_title;
    private javax.swing.JTextField txt_diajukan_id;
    private javax.swing.JTextField txt_diajukan_nama;
    private javax.swing.JTextField txt_estimasi_harga_satuan;
    private javax.swing.JTextField txt_estimasi_harga_total;
    private javax.swing.JTextArea txt_keperluan;
    private javax.swing.JTextArea txt_keterangan;
    private javax.swing.JTextArea txt_link;
    private javax.swing.JTextField txt_nama_barang;
    private javax.swing.JTextField txt_no;
    // End of variables declaration//GEN-END:variables
}
