package waleta_system;

import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import waleta_system.Class.Utility;

public class JDialog_NewEdit_IsuProduksi extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String imgPath = null;
    Date today = new Date();
    String status = null;
    JPanel_Isu_Produksi parentForm;

    public JDialog_NewEdit_IsuProduksi(java.awt.Frame parent, boolean modal, String status, JPanel_Isu_Produksi parentForm) {
        super(parent, modal);
        initComponents();
        this.status = status;
        this.parentForm = parentForm;
        if ("new".equals(status)) {
            label_title.setText("Tambah Data");
            generateNewCode();
        } else if ("edit".equals(status)) {
            int i = parentForm.tabel_data_isu_produksi.getSelectedRow();
            label_title.setText("Edit Data");
            label_kode.setText(parentForm.tabel_data_isu_produksi.getValueAt(i, 0).toString());
            try {
                Date_isu.setDate(dateFormat.parse(parentForm.tabel_data_isu_produksi.getValueAt(i, 1).toString()));
                if (parentForm.tabel_data_isu_produksi.getValueAt(i, 6) == null) {
                    Date_selesai.setDate(null);
                } else {
                    Date_selesai.setDate(dateFormat.parse(parentForm.tabel_data_isu_produksi.getValueAt(i, 6).toString()));
                }
            } catch (ParseException ex) {
                Logger.getLogger(JDialog_NewEdit_IsuProduksi.class.getName()).log(Level.SEVERE, null, ex);
            }
            txt_masalah_isu.setText(parentForm.tabel_data_isu_produksi.getValueAt(i, 2).toString());
            String[] dept = parentForm.tabel_data_isu_produksi.getValueAt(i, 3).toString().split(";");
            ComboBox1.setSelectedItem(dept[0]);
            if (dept.length > 1) {
                ComboBox2.setSelectedItem(dept[1]);
            }
            txt_penanggungjawab.setText(parentForm.tabel_data_isu_produksi.getValueAt(i, 4).toString());
            txt_penyelesaian.setText(parentForm.tabel_data_isu_produksi.getValueAt(i, 5).toString());
            try {
                sql = "SELECT `kode_isu`, `gambar_isu` FROM `tb_isu_produksi` WHERE `kode_isu` = '" + parentForm.tabel_data_isu_produksi.getValueAt(i, 0).toString() + "'";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    label_image.setIcon(Utility.ResizeImage(null, rs.getBytes("gambar_isu"), label_image.getWidth(), label_image.getHeight()));
                }
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_NewEdit_IsuProduksi.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void generateNewCode() {
        try {
            int last = 0;
            String last_number = "";
            sql = "SELECT RIGHT(`kode_isu`, 3) AS 'kode' FROM `tb_isu_produksi` WHERE YEAR(`tanggal_isu`) = '" + new SimpleDateFormat("yyyy").format(today) + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                if (rs.getInt("kode") > last) {
                    last = rs.getInt("kode");
                }
            }
            last++;
            if (last < 10) {
                last_number = "ISU-" + new SimpleDateFormat("yy").format(today) + "00" + Integer.toString(last);
            } else if (last < 100) {
                last_number = "ISU-" + new SimpleDateFormat("yy").format(today) + "0" + Integer.toString(last);
            } else if (last < 1000) {
                last_number = "ISU-" + new SimpleDateFormat("yy").format(today) + Integer.toString(last);
            }
            label_kode.setText(last_number);
        } catch (Exception ex) {
            Logger.getLogger(JDialog_NewEdit_IsuProduksi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void insert() {
        boolean Check = true;
        try {
            if (imgPath == null
                    || Date_isu.getDate() == null
                    || txt_masalah_isu.getText() == null) {
                JOptionPane.showMessageDialog(this, "Silahkan lengkapi data diatas");
                Check = false;
            }
            if (Check) {
                String penanggungjawab = txt_penanggungjawab.getText();
                if (txt_penanggungjawab.getText() == null || txt_penanggungjawab.getText().equals("")) {
                    penanggungjawab = "-";
                }
                String solusi = txt_penyelesaian.getText();
                if (txt_penyelesaian.getText() == null || txt_penyelesaian.getText().equals("")) {
                    solusi = "-";
                }
                InputStream img = new FileInputStream(new File(imgPath));
                if (Date_selesai.getDate() == null) {
                    PreparedStatement ps = Utility.db.getConnection().prepareStatement("INSERT INTO `tb_isu_produksi`(`kode_isu`, `tanggal_isu`, `masalah`, `dept`, `penanggungjawab`, `penyelesaian`, `gambar_isu`, `lokasi_gambar`) VALUES (?,?,?,?,?,?,?,?)");
                    ps.setString(1, label_kode.getText());
                    ps.setString(2, dateFormat.format(Date_isu.getDate()));
                    ps.setString(3, txt_masalah_isu.getText());
                    String dept = ComboBox1.getSelectedItem().toString();
                    if (!ComboBox2.getSelectedItem().toString().equals("-")) {
                        dept = dept + ";" + ComboBox2.getSelectedItem().toString();
                    }
                    ps.setString(4, dept);
                    ps.setString(5, penanggungjawab);
                    ps.setString(6, solusi);
                    ps.setBlob(7, img);
                    ps.setString(8, "\\\\192.168.10.2\\Shared Folder\\DOKUMEN SISTEM\\ISSUE_IMAGE\\" + label_kode.getText() + ".jpg");
                    ps.executeUpdate();
                    Utility.createImage(new File(imgPath), new File("\\\\192.168.10.2\\Shared Folder\\DOKUMEN SISTEM\\ISSUE_IMAGE\\" + label_kode.getText() + ".jpg"));
                } else {
                    PreparedStatement ps = Utility.db.getConnection().prepareStatement("INSERT INTO `tb_isu_produksi`(`kode_isu`, `tanggal_isu`, `masalah`, `dept`, `penanggungjawab`, `penyelesaian`, `tanggal_selesai`, `gambar_isu`, `lokasi_gambar`) VALUES (?,?,?,?,?,?,?,?,?)");
                    ps.setString(1, label_kode.getText());
                    ps.setString(2, dateFormat.format(Date_isu.getDate()));
                    ps.setString(3, txt_masalah_isu.getText());
                    String dept = ComboBox1.getSelectedItem().toString();
                    if (!ComboBox2.getSelectedItem().toString().equals("-")) {
                        dept = dept + ";" + ComboBox2.getSelectedItem().toString();
                    }
                    ps.setString(4, dept);
                    ps.setString(5, txt_penanggungjawab.getText());
                    ps.setString(6, txt_penyelesaian.getText());
                    ps.setString(7, dateFormat.format(Date_selesai.getDate()));
                    ps.setBlob(8, img);
                    ps.setString(9, "\\\\192.168.10.2\\Shared Folder\\DOKUMEN SISTEM\\ISSUE_IMAGE\\" + label_kode.getText() + ".jpg");
                    ps.executeUpdate();
                    Utility.createImage(new File(imgPath), new File("\\\\192.168.10.2\\Shared Folder\\DOKUMEN SISTEM\\ISSUE_IMAGE\\" + label_kode.getText() + ".jpg"));
                }

                JOptionPane.showMessageDialog(this, "Insert Success !");
                parentForm.refreshTable();
                this.dispose();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JDialog_NewEdit_IsuProduksi.class.getName()).log(Level.SEVERE, null, e);
        }
        imgPath = null;
    }

    public void update() {
        boolean Check = true;
        try {
            if (Date_isu.getDate() == null
                    || txt_masalah_isu.getText() == null
                    || txt_penanggungjawab.getText() == null
                    || txt_penyelesaian.getText() == null) {
                JOptionPane.showMessageDialog(this, "Silahkan lengkapi data diatas");
                Check = false;
            }
            if (Check) {
                String dept = ComboBox1.getSelectedItem().toString();
                if (!ComboBox2.getSelectedItem().toString().equals("-")) {
                    dept = dept + ";" + ComboBox2.getSelectedItem().toString();
                }
                String tanggal_selesai = "`tanggal_selesai`=NULL, ";
                if (Date_selesai.getDate() != null) {
                    tanggal_selesai = "`tanggal_selesai`='" + dateFormat.format(Date_selesai.getDate()) + "', ";
                }
                if (imgPath == null) {
                    sql = "UPDATE `tb_isu_produksi` SET "
                            + "`tanggal_isu`='" + dateFormat.format(Date_isu.getDate()) + "', "
                            + "`masalah`='" + txt_masalah_isu.getText() + "', "
                            + "`dept`='" + dept + "', "
                            + "`penanggungjawab`='" + txt_penanggungjawab.getText() + "', "
                            + tanggal_selesai
                            + "`penyelesaian`='" + txt_penyelesaian.getText() + "' "
                            + "WHERE `kode_isu`='" + label_kode.getText() + "'";
                    PreparedStatement ps = Utility.db.getConnection().prepareStatement(sql);
                    ps.executeUpdate();
                } else {
                    InputStream img = new FileInputStream(new File(imgPath));
                    sql = "UPDATE `tb_isu_produksi` SET "
                            + "`tanggal_isu`='" + dateFormat.format(Date_isu.getDate()) + "', "
                            + "`masalah`='" + txt_masalah_isu.getText() + "', "
                            + "`dept`='" + dept + "', "
                            + "`penanggungjawab`='" + txt_penanggungjawab.getText() + "', "
                            + tanggal_selesai
                            + "`penyelesaian`='" + txt_penyelesaian.getText() + "', "
                            + "`gambar_isu`=?, "
                            + "`lokasi_gambar`='\\\\192.168.10.2\\Shared Folder\\DOKUMEN SISTEM\\ISSUE_IMAGE\\" + label_kode.getText() + ".jpg' "
                            + "WHERE `kode_isu`='" + label_kode.getText() + "'";
                    PreparedStatement ps = Utility.db.getConnection().prepareStatement(sql);
                    ps.setBlob(1, img);
                    ps.executeUpdate();
                    Utility.createImage(new File(imgPath), new File("\\\\192.168.10.2\\Shared Folder\\DOKUMEN SISTEM\\ISSUE_IMAGE\\" + label_kode.getText() + ".jpg"));
                }
                JOptionPane.showMessageDialog(this, "Update Success !");
                parentForm.refreshTable();
                this.dispose();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JDialog_NewEdit_IsuProduksi.class.getName()).log(Level.SEVERE, null, e);
        }
        imgPath = null;
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
        jLabel9 = new javax.swing.JLabel();
        button_browse = new javax.swing.JButton();
        label_image = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        Date_isu = new com.toedter.calendar.JDateChooser();
        txt_penanggungjawab = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        Date_selesai = new com.toedter.calendar.JDateChooser();
        ComboBox1 = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txt_penyelesaian = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        label_kode = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txt_masalah_isu = new javax.swing.JTextArea();
        ComboBox2 = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        button_save = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        label_title.setBackground(new java.awt.Color(255, 255, 255));
        label_title.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_title.setText("Isu Produksi");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("Gambar :");

        button_browse.setBackground(new java.awt.Color(255, 255, 255));
        button_browse.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_browse.setText("Browse");
        button_browse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_browseActionPerformed(evt);
            }
        });

        label_image.setBackground(new java.awt.Color(255, 255, 255));
        label_image.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        Date_isu.setBackground(new java.awt.Color(255, 255, 255));
        Date_isu.setDateFormatString("dd MMMM yyyy");
        Date_isu.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        txt_penanggungjawab.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText("Kode : ");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Penyelesaian :");

        Date_selesai.setBackground(new java.awt.Color(255, 255, 255));
        Date_selesai.setDateFormatString("dd MMMM yyyy");
        Date_selesai.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        ComboBox1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ALL", "GBM", "CABUT", "CUCI", "TREATMENT", "FINISHING", "PACKING" }));

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Tanggal isu :");

        txt_penyelesaian.setColumns(20);
        txt_penyelesaian.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_penyelesaian.setLineWrap(true);
        txt_penyelesaian.setRows(5);
        txt_penyelesaian.setText("-");
        jScrollPane2.setViewportView(txt_penyelesaian);

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("Masalah / isu :");

        label_kode.setBackground(new java.awt.Color(255, 255, 255));
        label_kode.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_kode.setText("-");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("Departemen :");

        txt_masalah_isu.setColumns(20);
        txt_masalah_isu.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_masalah_isu.setLineWrap(true);
        txt_masalah_isu.setRows(5);
        jScrollPane1.setViewportView(txt_masalah_isu);

        ComboBox2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-", "GBM", "CABUT", "CUCI", "TREATMENT", "FINISHING", "PACKING" }));

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText("Tanggal Selesai :");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("Penanggung Jawab :");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(label_kode, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(ComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txt_penanggungjawab)
                            .addComponent(Date_isu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(Date_selesai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kode, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_isu, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_penanggungjawab, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_selesai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        button_save.setBackground(new java.awt.Color(255, 255, 255));
        button_save.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_save.setText("Save");
        button_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_saveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(label_title))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_browse)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_save)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(label_image, javax.swing.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_title)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_browse)
                    .addComponent(button_save))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label_image, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void button_browseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_browseActionPerformed
        // TODO add your handling code here:
        JFileChooser file = new JFileChooser();
        file.setCurrentDirectory(new File(System.getProperty("user.home")));
        //filter files extension
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JPEG file", "jpg", "jpeg", "png");
        file.addChoosableFileFilter(filter);
        int result = file.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = file.getSelectedFile();
            String path = selectedFile.getAbsolutePath();
            label_image.setIcon(Utility.ResizeImage(path, null, label_image.getWidth(), label_image.getHeight()));
            imgPath = path;
        } else if (result == JFileChooser.CANCEL_OPTION) {
            System.out.println("NO FILE SELECTED !");
        }
    }//GEN-LAST:event_button_browseActionPerformed

    private void button_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_saveActionPerformed
        // TODO add your handling code here:
        if ("new".equals(status)) {
            insert();
        } else if ("edit".equals(status)) {
            update();
        }
    }//GEN-LAST:event_button_saveActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox1;
    private javax.swing.JComboBox<String> ComboBox2;
    private com.toedter.calendar.JDateChooser Date_isu;
    private com.toedter.calendar.JDateChooser Date_selesai;
    private javax.swing.JButton button_browse;
    private javax.swing.JButton button_save;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel label_image;
    private javax.swing.JLabel label_kode;
    private javax.swing.JLabel label_title;
    private javax.swing.JTextArea txt_masalah_isu;
    private javax.swing.JTextField txt_penanggungjawab;
    private javax.swing.JTextArea txt_penyelesaian;
    // End of variables declaration//GEN-END:variables
}
