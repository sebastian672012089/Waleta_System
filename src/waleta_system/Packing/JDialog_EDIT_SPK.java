package waleta_system.Packing;

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
import waleta_system.BahanJadi.JDialog_Setor_Packing;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;

public class JDialog_EDIT_SPK extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    float total_harga = 0, total_gram = 0, total_kemasan = 0;
    String kode_spk = null, status_fix = null;
//    ArrayList<String> DetailGradeAwal = new ArrayList<>();

    public JDialog_EDIT_SPK(java.awt.Frame parent, boolean modal, String kode_spk) {
        super(parent, modal);
        try {
            initComponents();
            this.setResizable(false);
            button_tambah.setIcon(Utility.ResizeImageIcon(new javax.swing.ImageIcon(getClass().getResource("/waleta_system/Images/add_green.png")), button_hapus.getWidth(), button_hapus.getHeight()));
            button_hapus.setIcon(Utility.ResizeImageIcon(new javax.swing.ImageIcon(getClass().getResource("/waleta_system/Images/delete-icon.png")), button_hapus.getWidth(), button_hapus.getHeight()));

            Utility.db.getConnection().setAutoCommit(false);

            this.kode_spk = kode_spk;
            jLabel1.setText("Revisi SPK");
            txt_kode_spk.setText(kode_spk);
            Load_DetailSPK();

        } catch (Exception ex) {
            Logger.getLogger(JDialog_EDIT_SPK.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Load_DetailSPK() {
        try {
            sql = "SELECT `tb_spk`.`buyer`, `tb_buyer`.`nama`, `no_revisi`, `tanggal_spk`, `tanggal_tk`, `tanggal_keluar`, `tanggal_awb`, `detail`, `fix`, `status` "
                    + "FROM `tb_spk` LEFT JOIN `tb_buyer` ON `tb_spk`.`buyer` = `tb_buyer`.`kode_buyer`"
                    + "WHERE `kode_spk` = '" + kode_spk + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                label_no_revisi.setText(Integer.toString(rs.getInt("no_revisi") + 1));
                Date_spk.setDate(rs.getDate("tanggal_spk"));
                Date_tk.setDate(rs.getDate("tanggal_tk"));
                Date_awb.setDate(rs.getDate("tanggal_awb"));
                Date_keluar.setDate(rs.getDate("tanggal_keluar"));
                txt_buyer_code.setText(rs.getString("buyer"));
                txt_buyer_name.setText(rs.getString("nama"));
                txt_catatan.setText(rs.getString("detail"));
                this.status_fix = rs.getString("fix");
                if (rs.getString("fix").equals("FIXED")) {
                    Table_daftarGrade.setEnabled(false);
                    button_tambah.setEnabled(false);
                    button_hapus.setEnabled(false);
                } else {
                    this.status_fix = kode_spk;
                    Table_daftarGrade.setEnabled(true);
                    button_tambah.setEnabled(true);
                    button_hapus.setEnabled(true);
                }
            }

            DefaultTableModel model = (DefaultTableModel) Table_daftarGrade.getModel();
            model.setRowCount(0);
            sql = "SELECT `no`, `grade_waleta`, `grade_buyer`, `berat_kemasan`, `jumlah_kemasan`, `berat`, `harga_cny`, `keterangan` "
                    + "FROM `tb_spk_detail` WHERE `kode_spk` = '" + kode_spk + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[8];
            while (rs.next()) {
                row[0] = rs.getString("no");
                row[1] = rs.getString("grade_waleta");
                row[2] = rs.getString("grade_buyer");
                row[3] = rs.getInt("berat_kemasan");
                row[4] = rs.getInt("jumlah_kemasan");
                row[5] = rs.getInt("berat");
                row[6] = rs.getInt("harga_cny");
                row[7] = rs.getString("keterangan");
                model.addRow(row);
            }
            count();
            ColumnsAutoSizer.sizeColumnsToFit(Table_daftarGrade);
        } catch (SQLException ex) {
            Logger.getLogger(JDialog_Setor_Packing.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void count() {
        total_harga = 0;
        total_gram = 0;
        total_kemasan = 0;
        int x = Table_daftarGrade.getRowCount();
        for (int i = 0; i < x; i++) {
            try {
                String grade_wlt = Table_daftarGrade.getValueAt(i, 1).toString();
                String grade_buyer = Table_daftarGrade.getValueAt(i, 2).toString();
                int total_berat = Integer.valueOf(Table_daftarGrade.getValueAt(i, 5).toString());
                int berat_per_kemasan = Integer.valueOf(Table_daftarGrade.getValueAt(i, 3).toString());
                int jumlah_kemasan = total_berat / berat_per_kemasan;
                Table_daftarGrade.setValueAt(jumlah_kemasan, i, 4);
                total_harga = total_harga + ((Float.valueOf(Table_daftarGrade.getValueAt(i, 6).toString()) / 1000) * Float.valueOf(Table_daftarGrade.getValueAt(i, 5).toString()));
                total_gram = total_gram + Integer.valueOf(Table_daftarGrade.getValueAt(i, 5).toString());
                total_kemasan = total_kemasan + jumlah_kemasan;
            } catch (NullPointerException e) {
                JOptionPane.showMessageDialog(this, "Tidak Boleh ada data yang kosong !");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Format Angka salah");
            }
        }
        label_total_grade.setText(Integer.toString(x));
        label_total_harga.setText(decimalFormat.format(total_harga));
        label_total_gram.setText(decimalFormat.format(total_gram));
        label_total_kemasan.setText(decimalFormat.format(total_kemasan));
        ColumnsAutoSizer.sizeColumnsToFit(Table_daftarGrade);
    }

    public void Edit() {
        try {
            Utility.db.getConnection().setAutoCommit(false);
            String tanggal_tk = "`tanggal_tk`=NULL,";
            if (Date_tk.getDate() != null) {
                tanggal_tk = "`tanggal_tk`='" + dateFormat.format(Date_tk.getDate()) + "',";
            }
            String tanggal_awb = "`tanggal_awb`=NULL,";
            if (Date_awb.getDate() != null) {
                tanggal_awb = "`tanggal_awb`='" + dateFormat.format(Date_awb.getDate()) + "',";
            }
            sql = "UPDATE `tb_spk` SET "
                    + "`kode_spk`='" + txt_kode_spk.getText() + "',"
                    + "`buyer`='" + txt_buyer_code.getText() + "',"
                    + "`no_revisi`='" + label_no_revisi.getText() + "',"
                    + tanggal_tk
                    + "`tanggal_spk`='" + dateFormat.format(Date_spk.getDate()) + "',"
                    + "`tanggal_keluar`='" + dateFormat.format(Date_keluar.getDate()) + "',"
                    + tanggal_awb
                    + "`detail`='" + txt_catatan.getText() + "'"
                    + "WHERE `kode_spk`='" + kode_spk + "'";
            Utility.db.getConnection().createStatement();
            if ((Utility.db.getStatement().executeUpdate(sql)) > 0) {
                if (!this.status_fix.equals("FIXED")) {
                    for (int i = 0; i < Table_daftarGrade.getRowCount(); i++) {
                        if (Table_daftarGrade.getValueAt(i, 0).toString().equals("0")) {
                            sql = "INSERT INTO `tb_spk_detail`(`grade_waleta`, `grade_buyer`, `berat_kemasan`, `jumlah_kemasan`, `berat`, `harga_cny`, `keterangan`, `kode_spk`) "
                                    + "VALUES ("
                                    + "'" + Table_daftarGrade.getValueAt(i, 1).toString() + "',"
                                    + "'" + Table_daftarGrade.getValueAt(i, 2).toString() + "',"
                                    + "'" + Table_daftarGrade.getValueAt(i, 3).toString() + "',"
                                    + "'" + Table_daftarGrade.getValueAt(i, 4).toString() + "',"
                                    + "'" + Table_daftarGrade.getValueAt(i, 5).toString() + "',"
                                    + "'" + Table_daftarGrade.getValueAt(i, 6).toString() + "',"
                                    + "'" + Table_daftarGrade.getValueAt(i, 7).toString() + "',"
                                    + "'" + kode_spk + "')";
                        } else {
                            sql = "UPDATE `tb_spk_detail` SET "
                                    + "`grade_waleta`='" + Table_daftarGrade.getValueAt(i, 1).toString() + "',"
                                    + "`grade_buyer`='" + Table_daftarGrade.getValueAt(i, 2).toString() + "',"
                                    + "`berat_kemasan`='" + Table_daftarGrade.getValueAt(i, 3).toString() + "',"
                                    + "`jumlah_kemasan`='" + Table_daftarGrade.getValueAt(i, 4).toString() + "',"
                                    + "`berat`='" + Table_daftarGrade.getValueAt(i, 5).toString() + "',"
                                    + "`harga_cny`='" + Table_daftarGrade.getValueAt(i, 6).toString() + "',"
                                    + "`keterangan`='" + Table_daftarGrade.getValueAt(i, 7).toString() + "'"
                                    + "WHERE `no`='" + Table_daftarGrade.getValueAt(i, 0).toString() + "'";
                        }
                        Utility.db.getConnection().createStatement();
                        Utility.db.getStatement().executeUpdate(sql);
                    }
                }
                Utility.db.getConnection().commit();
                this.dispose();
                JOptionPane.showMessageDialog(this, "Data Saved !");
            }
        } catch (SQLException | NullPointerException ex) {
            try {
                Utility.db.getConnection().rollback();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
                Logger.getLogger(JDialog_EDIT_SPK.class.getName()).log(Level.SEVERE, null, e);
            }
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JDialog_EDIT_SPK.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                Utility.db.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_EDIT_SPK.class.getName()).log(Level.SEVERE, null, ex);
            }
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
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txt_kode_spk = new javax.swing.JTextField();
        Date_spk = new com.toedter.calendar.JDateChooser();
        jLabel9 = new javax.swing.JLabel();
        txt_buyer_name = new javax.swing.JTextField();
        button_pilih_pembeli = new javax.swing.JButton();
        txt_buyer_code = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        label_total_harga = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        label_total_gram = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        label_total_grade = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        Table_daftarGrade = new javax.swing.JTable();
        button_hapus = new javax.swing.JButton();
        button_count = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        label_total_kemasan = new javax.swing.JLabel();
        button_tambah = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        Date_keluar = new com.toedter.calendar.JDateChooser();
        button_cancel = new javax.swing.JButton();
        button_save = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txt_catatan = new javax.swing.JTextArea();
        label_catatan_counter = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        label_no_revisi = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        Date_tk = new com.toedter.calendar.JDateChooser();
        jLabel12 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        Date_awb = new com.toedter.calendar.JDateChooser();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("PENGIRIMAN");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        jLabel1.setText("EDIT SPK");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel2.setText("DEPARTEMEN SALES & EKSPOR");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel3.setText("Kode SPK :");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel5.setText("Tanggal SPK :");

        txt_kode_spk.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        Date_spk.setBackground(new java.awt.Color(255, 255, 255));
        Date_spk.setDate(new Date());
        Date_spk.setDateFormatString("dd MMMM yyyy");
        Date_spk.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel9.setText("Kode Pembeli :");

        txt_buyer_name.setEditable(false);
        txt_buyer_name.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        button_pilih_pembeli.setBackground(new java.awt.Color(255, 255, 255));
        button_pilih_pembeli.setFont(new java.awt.Font("SansSerif", 0, 11)); // NOI18N
        button_pilih_pembeli.setText("Pilih Pembeli");
        button_pilih_pembeli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_pilih_pembeliActionPerformed(evt);
            }
        });

        txt_buyer_code.setEditable(false);
        txt_buyer_code.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel10.setText("Nama Pembeli :");

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Detail SPK", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N

        label_total_harga.setBackground(new java.awt.Color(255, 255, 255));
        label_total_harga.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_harga.setText("0");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Gram :");

        label_total_gram.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_gram.setText("0");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Total Grade :");

        label_total_grade.setBackground(new java.awt.Color(255, 255, 255));
        label_total_grade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_grade.setText("0");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Harga CNY :");

        Table_daftarGrade.setAutoCreateRowSorter(true);
        Table_daftarGrade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_daftarGrade.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "Grade WLT", "Grade Buyer", "Berat / Kemasan", "Jumlah Kemasan", "Total Berat", "Harga CNY", "Keterangan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true, false, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_daftarGrade.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(Table_daftarGrade);

        button_hapus.setBackground(new java.awt.Color(255, 255, 255));
        button_hapus.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_hapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_hapusActionPerformed(evt);
            }
        });

        button_count.setBackground(new java.awt.Color(255, 255, 255));
        button_count.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_count.setText("Count");
        button_count.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_countActionPerformed(evt);
            }
        });

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel17.setText("Kemasan :");

        label_total_kemasan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kemasan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_kemasan.setText("0");

        button_tambah.setBackground(new java.awt.Color(255, 255, 255));
        button_tambah.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_tambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_tambahActionPerformed(evt);
            }
        });

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel18.setText("NOTE : Hapus Grade akan menghapus Scan QR yang sudah terinput");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 748, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_grade)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_harga)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_kemasan))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(button_count)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel18)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_count, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_total_harga)
                    .addComponent(jLabel14)
                    .addComponent(label_total_gram)
                    .addComponent(jLabel11)
                    .addComponent(label_total_grade)
                    .addComponent(jLabel13)
                    .addComponent(label_total_kemasan)
                    .addComponent(jLabel17))
                .addContainerGap())
        );

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel15.setText("Catatan :");

        Date_keluar.setBackground(new java.awt.Color(255, 255, 255));
        Date_keluar.setDateFormatString("dd MMMM yyyy");
        Date_keluar.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        button_cancel.setBackground(new java.awt.Color(255, 255, 255));
        button_cancel.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_cancel.setText("CANCEL");
        button_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_cancelActionPerformed(evt);
            }
        });

        button_save.setBackground(new java.awt.Color(255, 255, 255));
        button_save.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_save.setText("SAVE");
        button_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_saveActionPerformed(evt);
            }
        });

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel16.setText("Tanggal Keluar :");

        txt_catatan.setColumns(20);
        txt_catatan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_catatan.setRows(5);
        txt_catatan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_catatanKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(txt_catatan);

        label_catatan_counter.setBackground(new java.awt.Color(255, 255, 255));
        label_catatan_counter.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        label_catatan_counter.setText("(0/250)");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel4.setText("No Revisi :");

        label_no_revisi.setBackground(new java.awt.Color(255, 255, 255));
        label_no_revisi.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        label_no_revisi.setText("1");

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel20.setText("Tanggal TK :");

        Date_tk.setBackground(new java.awt.Color(255, 255, 255));
        Date_tk.setDateFormatString("dd MMMM yyyy");
        Date_tk.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("NOTE : Jika SPK sudah FIXED, hanya bisa edit tanggal / Catatan");

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel26.setText("Tanggal AWB :");

        Date_awb.setBackground(new java.awt.Color(255, 255, 255));
        Date_awb.setDateFormatString("dd MMMM yyyy");
        Date_awb.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_catatan_counter, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txt_kode_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txt_buyer_code, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(button_pilih_pembeli)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jLabel4)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(label_no_revisi))))
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
                                        .addComponent(txt_buyer_name))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(Date_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(Date_tk, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(Date_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(Date_awb, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(jLabel2)
                            .addComponent(jLabel1)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_cancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_save)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_kode_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_no_revisi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(Date_tk, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(Date_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(Date_awb, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_buyer_code, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_pilih_pembeli, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_buyer_name, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_catatan_counter, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_save, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_cancel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
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

    private void button_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_saveActionPerformed
        // TODO add your handling code here:
        boolean check_null = false;
        for (int j = 0; j < Table_daftarGrade.getColumnCount(); j++) {
            for (int i = 0; i < Table_daftarGrade.getRowCount(); i++) {
                check_null = (Table_daftarGrade.getValueAt(i, j) == null);
                break;
            }
        }

        if (txt_kode_spk.getText() == null
                || txt_buyer_name.getText() == null || txt_buyer_name.getText().equals("")
                || txt_buyer_code.getText() == null || txt_buyer_code.getText().equals("")
                || Date_spk.getDate() == null || Date_keluar.getDate() == null || check_null) {
            JOptionPane.showMessageDialog(this, "Silahkan Lengkapi Data diatas !!");
        } else {
            count();
            Edit();
        }
    }//GEN-LAST:event_button_saveActionPerformed

    private void button_pilih_pembeliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_pilih_pembeliActionPerformed
        // TODO add your handling code here:
        JDialog_ChooseBuyer dialog = new JDialog_ChooseBuyer(new javax.swing.JFrame(), true, "All");
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);

        int a = JDialog_ChooseBuyer.table_list_buyer.getSelectedRow();
        if (a >= 0) {
            String nama_buyer = JDialog_ChooseBuyer.table_list_buyer.getValueAt(a, 1).toString();
            String kode_buyer = JDialog_ChooseBuyer.table_list_buyer.getValueAt(a, 0).toString();
            txt_buyer_code.setText(kode_buyer);
            txt_buyer_name.setText(nama_buyer);
        }
    }//GEN-LAST:event_button_pilih_pembeliActionPerformed

    private void button_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_cancelActionPerformed
        // TODO add your handling code here:
        try {
            Utility.db.getConnection().rollback();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JDialog_EDIT_SPK.class.getName()).log(Level.SEVERE, null, e);
        }
        this.dispose();
    }//GEN-LAST:event_button_cancelActionPerformed

    private void button_hapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_hapusActionPerformed
        int i = Table_daftarGrade.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) Table_daftarGrade.getModel();
        if (i != -1) {
            int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin detele grade " + Table_daftarGrade.getValueAt(i, 2).toString() + "?", "Warning", 0);
            if (dialogResult == JOptionPane.YES_OPTION) {
                try {
                    sql = "DELETE FROM `tb_spk_detail` WHERE `no` = '" + Table_daftarGrade.getValueAt(i, 0).toString() + "'";
                    Utility.db.getConnection().createStatement();
                    if (Utility.db.getStatement().executeUpdate(sql) > 0) {
                        model.removeRow(i);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                    Logger.getLogger(JDialog_EDIT_SPK.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        ColumnsAutoSizer.sizeColumnsToFit(Table_daftarGrade);
        count();
    }//GEN-LAST:event_button_hapusActionPerformed

    private void button_countActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_countActionPerformed
        // TODO add your handling code here:
        count();
    }//GEN-LAST:event_button_countActionPerformed

    private void txt_catatanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_catatanKeyPressed
        // TODO add your handling code here:
        int x = txt_catatan.getText().length();
        label_catatan_counter.setText("(" + (x + 1) + "/250)");
    }//GEN-LAST:event_txt_catatanKeyPressed

    private void button_tambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_tambahActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_daftarGrade.getModel();
        model.addRow(new Object[]{
            0,
            null,
            null,
            null,
            null,
            null,
            0,
            "-"
        });
        ColumnsAutoSizer.sizeColumnsToFit(Table_daftarGrade);
    }//GEN-LAST:event_button_tambahActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser Date_awb;
    private com.toedter.calendar.JDateChooser Date_keluar;
    private com.toedter.calendar.JDateChooser Date_spk;
    private com.toedter.calendar.JDateChooser Date_tk;
    private javax.swing.JTable Table_daftarGrade;
    private javax.swing.JButton button_cancel;
    private javax.swing.JButton button_count;
    private javax.swing.JButton button_hapus;
    private javax.swing.JButton button_pilih_pembeli;
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
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel label_catatan_counter;
    private javax.swing.JLabel label_no_revisi;
    private javax.swing.JLabel label_total_grade;
    private javax.swing.JLabel label_total_gram;
    private javax.swing.JLabel label_total_harga;
    private javax.swing.JLabel label_total_kemasan;
    private javax.swing.JTextField txt_buyer_code;
    private javax.swing.JTextField txt_buyer_name;
    private javax.swing.JTextArea txt_catatan;
    private javax.swing.JTextField txt_kode_spk;
    // End of variables declaration//GEN-END:variables
}
