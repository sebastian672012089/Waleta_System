package waleta_system.Packing;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;

public class JDialog_pengiriman extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    float total_keping_kirim = 0, tot_keping_daftarBox = 0;
    float total_gram_kirim = 0, tot_gram_daftarBox = 0;
    String status = null, invoice = null;
    String tgl_kirim = null;

    public JDialog_pengiriman(java.awt.Frame parent, boolean modal, String status, String invoice) {
        super(parent, modal);
        this.status = status;
        this.invoice = invoice;
        try {
            initComponents();
            button_hapus.setIcon(Utility.ResizeImageIcon(new javax.swing.ImageIcon(getClass().getResource("/waleta_system/Images/delete-icon.png")), button_hapus.getWidth(), button_hapus.getHeight()));
            
            ComboBox_spk.removeAllItems();
            sql = "SELECT `tb_spk`.`kode_spk` FROM `tb_spk` "
                    + "LEFT JOIN `tb_pengiriman` ON `tb_spk`.`kode_spk` = `tb_pengiriman`.`kode_spk` "
                    + "WHERE `tb_pengiriman`.`invoice_no` IS NULL AND `status` = 'SEND OUT' "
                    + "AND `tb_spk`.`kode_spk` <> 'Sample Internal' AND `tanggal_spk` > '2021-01-01'";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_spk.addItem(rs.getString("kode_spk"));
            }

            if ("edit".equals(status)) {
                getData_Edit();
            }
        } catch (Exception ex) {
            Logger.getLogger(JDialog_pengiriman.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void getData_Edit() {
        try {
            sql = "SELECT `invoice_no`, `tanggal_invoice`, `tanggal_pengiriman`, `tb_pengiriman`.`kode_buyer`, `no_dokumen`, `jenis_pengiriman`, `status_pengiriman`, `keterangan`, `tb_pengiriman`.`kode_spk`, `tb_spk`.`tanggal_awb`, `tb_buyer`.`nama` "
                    + "FROM `tb_pengiriman` "
                    + "LEFT JOIN `tb_buyer` ON `tb_pengiriman`.`kode_buyer` = `tb_buyer`.`kode_buyer` "
                    + "LEFT JOIN `tb_spk` ON `tb_pengiriman`.`kode_spk` = `tb_spk`.`kode_spk` "
                    + "WHERE `invoice_no` = '" + invoice + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                txt_no_dokumen.setText(invoice);
                Date_dokumen.setDate(rs.getDate("tanggal_invoice"));
                ComboBox_jenisPengiriman.setSelectedItem(rs.getString("jenis_pengiriman"));
                if (rs.getString("kode_spk") != null) {
                    ComboBox_spk.addItem(rs.getString("kode_spk"));
                    ComboBox_spk.setSelectedItem(rs.getString("kode_spk"));
                    label_kode_spk.setText(rs.getString("kode_spk"));
                    label_tanggal_kirim.setText(new SimpleDateFormat("dd MMMM yyyy").format(rs.getDate("tanggal_pengiriman")));
                    label_tanggal_awb.setText(new SimpleDateFormat("dd MMMM yyyy").format(rs.getDate("tanggal_awb")));
                    txt_buyer_code.setText(rs.getString("kode_buyer"));
                    txt_buyer_name.setText(rs.getString("nama"));
                    txt_keterangan.setText(rs.getString("keterangan"));
                    tgl_kirim = rs.getString("tanggal_pengiriman");
                }
            }

            DefaultTableModel model = (DefaultTableModel) table_pengiriman.getModel();
            model.setRowCount(0);
            sql = "SELECT `tb_box_packing`.`no_box`, `tb_grade_bahan_jadi`.`kode_grade`, `tb_spk_detail`.`grade_buyer`, `tb_box_packing`.`tanggal_masuk`, `batch_number`, `tb_box_bahan_jadi`.`keping`, `tb_box_bahan_jadi`.`berat` "
                    + "FROM `tb_box_packing` "
                    + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_box_packing`.`no_box` = `tb_box_bahan_jadi`.`no_box` "
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode` "
                    + "LEFT JOIN `tb_spk_detail` ON `tb_box_packing`.`no_grade_spk` = `tb_spk_detail`.`no` "
                    + "WHERE `invoice_pengiriman` = '" + invoice + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("no_box"),
                    rs.getString("kode_grade"),
                    rs.getString("grade_buyer"),
                    rs.getInt("keping"),
                    rs.getFloat("berat"),
                    rs.getString("batch_number")});
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_pengiriman);
            count_pengiriman();

        } catch (SQLException ex) {
            Logger.getLogger(JDialog_pengiriman.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void count_pengiriman() {
        total_keping_kirim = 0;
        total_gram_kirim = 0;
        int x = table_pengiriman.getRowCount();
        for (int i = 0; i < x; i++) {
            try {
                total_keping_kirim = total_keping_kirim + Integer.valueOf(table_pengiriman.getValueAt(i, 3).toString());
                total_gram_kirim = total_gram_kirim + Float.valueOf(table_pengiriman.getValueAt(i, 4).toString());
            } catch (NullPointerException e) {
                JOptionPane.showMessageDialog(this, "Tidak Boleh ada data yang kosong !");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Format Angka salah !");
            }
        }
        label_total_box.setText(Integer.toString(x));
        label_total_keping.setText(decimalFormat.format(total_keping_kirim));
        label_total_gram.setText(decimalFormat.format(total_gram_kirim));
        ColumnsAutoSizer.sizeColumnsToFit(table_pengiriman);
    }

    public void New() {
        try {
            Utility.db.getConnection().setAutoCommit(false);
            sql = "INSERT INTO `tb_pengiriman`(`invoice_no`, `tanggal_invoice`, `tanggal_pengiriman`, `jenis_pengiriman`, `kode_buyer`, `no_dokumen`, `status_pengiriman`, `keterangan`, `kode_spk`) "
                    + "VALUES ('" + txt_no_dokumen.getText() + "','" + dateFormat.format(Date_dokumen.getDate()) + "','" + tgl_kirim + "','" + ComboBox_jenisPengiriman.getSelectedItem() + "','" + txt_buyer_code.getText() + "','" + txt_no_dokumen.getText() + "', 'PROSES', '" + txt_keterangan.getText() + "', '" + label_kode_spk.getText() + "')";
            Utility.db.getConnection().createStatement();
            if ((Utility.db.getStatement().executeUpdate(sql)) > 0) {
                for (int i = 0; i < table_pengiriman.getRowCount(); i++) {
                    sql = "UPDATE `tb_box_packing` SET `status`='PROSES',"
                            + "`batch_number`='" + table_pengiriman.getValueAt(i, 5).toString() + "', "
                            + "`invoice_pengiriman`='" + txt_no_dokumen.getText() + "' "
                            + "WHERE `no_box` = '" + table_pengiriman.getValueAt(i, 0) + "'";
//                            System.out.println(sql);
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(sql);
                }
                Utility.db.getConnection().commit();
                this.dispose();
                JOptionPane.showMessageDialog(this, "Data Saved !");
            }
        } catch (SQLException | NullPointerException ex) {
            try {
                Utility.db.getConnection().rollback();
            } catch (SQLException e) {
                Logger.getLogger(JDialog_pengiriman.class.getName()).log(Level.SEVERE, null, e);
            }
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JDialog_pengiriman.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                Utility.db.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_pengiriman.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void Edit() {
        try {
            Utility.db.getConnection().setAutoCommit(false);
            sql = "UPDATE `tb_box_packing` SET `status`='STOK', `batch_number`=NULL, `invoice_pengiriman`=NULL "
                    + "WHERE `invoice_pengiriman` = '" + txt_no_dokumen.getText() + "'";
            Utility.db.getConnection().createStatement();
            Utility.db.getStatement().executeUpdate(sql);

            sql = "UPDATE `tb_pengiriman` SET "
                    + "`invoice_no`='" + txt_no_dokumen.getText() + "',"
                    + "`no_dokumen`='" + txt_no_dokumen.getText() + "',"
                    + "`tanggal_invoice`='" + dateFormat.format(Date_dokumen.getDate()) + "',"
                    + "`tanggal_pengiriman`='" + tgl_kirim + "',"
                    + "`kode_buyer`='" + txt_buyer_code.getText() + "',"
                    + "`jenis_pengiriman`='" + ComboBox_jenisPengiriman.getSelectedItem() + "',"
                    + "`status_pengiriman`='PROSES', "
                    + "`keterangan`='" + txt_keterangan.getText() + "', "
                    + "`kode_spk`='" + label_kode_spk.getText() + "' "
                    + "WHERE `invoice_no`='" + invoice + "'";
            Utility.db.getConnection().createStatement();
            if ((Utility.db.getStatement().executeUpdate(sql)) > 0) {
                for (int i = 0; i < table_pengiriman.getRowCount(); i++) {
                    sql = "UPDATE `tb_box_packing` SET `status`='PROSES',"
                            + "`batch_number`='" + table_pengiriman.getValueAt(i, 5).toString() + "',"
                            + "`invoice_pengiriman`='" + txt_no_dokumen.getText() + "' "
                            + "WHERE `no_box` = '" + table_pengiriman.getValueAt(i, 0) + "'";
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(sql);
                }
                Utility.db.getConnection().commit();
                this.dispose();
                JOptionPane.showMessageDialog(this, "Data Saved !");
            }
        } catch (SQLException | NullPointerException ex) {
            try {
                Utility.db.getConnection().rollback();
            } catch (SQLException e) {
                Logger.getLogger(JDialog_pengiriman.class.getName()).log(Level.SEVERE, null, e);
            }
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JDialog_pengiriman.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                Utility.db.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_pengiriman.class.getName()).log(Level.SEVERE, null, ex);
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
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txt_no_dokumen = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txt_buyer_name = new javax.swing.JTextField();
        txt_buyer_code = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_pengiriman = new javax.swing.JTable();
        label_total_box = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        label_total_keping = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        label_total_gram = new javax.swing.JLabel();
        button_hapus = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        ComboBox_jenisPengiriman = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        Date_dokumen = new com.toedter.calendar.JDateChooser();
        button_cancel = new javax.swing.JButton();
        button_save = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        txt_keterangan = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        ComboBox_spk = new javax.swing.JComboBox<>();
        button_proses_pemindahanBox = new javax.swing.JButton();
        label_tanggal_kirim = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        label_tanggal_awb = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        label_kode_spk = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("PENGIRIMAN");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        jLabel1.setText("PENGIRIMAN BAHAN JADI");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel2.setText("DEPARTEMEN BAHAN JADI");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("No Dokumen :");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("Tanggal Kirim / Pick Up :");

        txt_no_dokumen.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("Buyer Code :");

        txt_buyer_name.setEditable(false);
        txt_buyer_name.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        txt_buyer_code.setEditable(false);
        txt_buyer_code.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel10.setText("Buyer Name :");

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Daftar Box Pengiriman", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 11))); // NOI18N

        table_pengiriman.setAutoCreateRowSorter(true);
        table_pengiriman.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_pengiriman.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Box", "Grade", "Grade Buyer", "Keping", "Gram", "Batch Numb"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_pengiriman.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table_pengiriman);

        label_total_box.setBackground(new java.awt.Color(255, 255, 255));
        label_total_box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_box.setText("0");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Total Box :");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Total Kpg :");

        label_total_keping.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_keping.setText("0");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Total Gram :");

        label_total_gram.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_gram.setText("0");

        button_hapus.setBackground(new java.awt.Color(255, 255, 255));
        button_hapus.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_hapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_hapusActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 595, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_box)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_keping)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(button_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(label_total_keping)
                        .addComponent(jLabel8)
                        .addComponent(label_total_gram)
                        .addComponent(label_total_box)
                        .addComponent(jLabel6)
                        .addComponent(jLabel7)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel12.setText("Jenis Pengiriman :");

        ComboBox_jenisPengiriman.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_jenisPengiriman.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Sampel Eksternal", "Ekspor", "Ekspor Esta", "Ekspor Sub", "Ekspor JT", "Lokal" }));

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel15.setText("Keterangan :");

        Date_dokumen.setBackground(new java.awt.Color(255, 255, 255));
        Date_dokumen.setDateFormatString("dd MMM yyyy");
        Date_dokumen.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

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
        jLabel16.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel16.setText("Tanggal Dokumen :");

        txt_keterangan.setEditable(false);
        txt_keterangan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel17.setText("SPK :");

        ComboBox_spk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_proses_pemindahanBox.setBackground(new java.awt.Color(255, 255, 255));
        button_proses_pemindahanBox.setFont(new java.awt.Font("SansSerif", 0, 11)); // NOI18N
        button_proses_pemindahanBox.setText("GET DATA");
        button_proses_pemindahanBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_proses_pemindahanBoxActionPerformed(evt);
            }
        });

        label_tanggal_kirim.setBackground(new java.awt.Color(255, 255, 255));
        label_tanggal_kirim.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_tanggal_kirim.setText("dd mmmm yyyy");

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel20.setText("Tanggal AWB :");

        label_tanggal_awb.setBackground(new java.awt.Color(255, 255, 255));
        label_tanggal_awb.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_tanggal_awb.setText("dd mmmm yyyy");

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel18.setText("SPK :");

        label_kode_spk.setBackground(new java.awt.Color(255, 255, 255));
        label_kode_spk.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_kode_spk.setText("<kode spk>");

        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(3);
        jTextArea1.setText("Notes : batch number akan otomatis terbuat dari data RSB dari KH dan tanggal produksi di data SPK per grade. \nMaka, harap melengkapi data KH dan tanggal produksi di data SPK terlebih dahulu. untuk tahun 2022 akan diberlakukan proteksinya.");
        jScrollPane2.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(button_save)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_cancel))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(ComboBox_jenisPengiriman, 0, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txt_keterangan, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txt_buyer_name, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txt_buyer_code, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txt_no_dokumen, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(Date_dokumen, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(ComboBox_spk, 0, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(button_proses_pemindahanBox))
                                        .addComponent(label_tanggal_kirim, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label_tanggal_awb, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label_kode_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_no_dokumen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(Date_dokumen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_jenisPengiriman, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                .addComponent(button_proses_pemindahanBox, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(ComboBox_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_kode_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_tanggal_kirim, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_tanggal_awb, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_buyer_code, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_buyer_name, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_keterangan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(button_save, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_cancel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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
        boolean check = true;
        if (txt_no_dokumen.getText() == null
                || txt_buyer_code.getText() == null
                || Date_dokumen.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Silahkan Lengkapi Data diatas !!");
            check = false;
        }

        if (!label_kode_spk.getText().equals(ComboBox_spk.getSelectedItem().toString())) {
            JOptionPane.showMessageDialog(this, "Setelah memilih SPK, silahkan get Data SPK terlebih dahulu");
            check = false;
        }
        if (table_pengiriman.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "tabel data box terkirim tidak boleh kosong!");
            check = false;
        }

        for (int i = 0; i < table_pengiriman.getRowCount(); i++) {
            if (table_pengiriman.getValueAt(i, 5) == null || table_pengiriman.getValueAt(i, 5).toString().equals("")) {
                JOptionPane.showMessageDialog(this, "Maaf Batch Number tidak boleh kosong !");
                check = false;
            }
        }

        if (check) {
            if ("new".equals(status)) {
                New();
            } else if ("edit".equals(status)) {
                Edit();
            }
        }
    }//GEN-LAST:event_button_saveActionPerformed

    private void button_hapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_hapusActionPerformed
        // TODO add your handling code here:
        int i = table_pengiriman.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) table_pengiriman.getModel();
        if (i != -1) {
            model.removeRow(table_pengiriman.getRowSorter().convertRowIndexToModel(i));
            ColumnsAutoSizer.sizeColumnsToFit(table_pengiriman);
            count_pengiriman();
        }
    }//GEN-LAST:event_button_hapusActionPerformed

    private void button_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_cancelActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_button_cancelActionPerformed

    private void button_proses_pemindahanBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_proses_pemindahanBoxActionPerformed
        // TODO add your handling code here:
        try {
            boolean check = true;
            String kode_spk = ComboBox_spk.getSelectedItem().toString();
            if (kode_spk == null || kode_spk.equals("")) {
                JOptionPane.showMessageDialog(this, "Kode SPK tidak bisa kosong");
                check = false;
            } else if (kode_spk.substring(0, 3).equals("SPK")) {
                sql = "SELECT `kode_spk`, `prod_date`, `kode_kh` "
                        + "FROM `tb_spk_detail` WHERE `kode_spk` = '" + kode_spk + "'";
                rs = Utility.db.getStatement().executeQuery(sql);
                while (rs.next()) {
                    if (rs.getDate("prod_date") == null || rs.getObject("kode_kh") == null) {
                        JOptionPane.showMessageDialog(this, "Harap melengkapi data Kode KH dan tanggal produksi untuk " + kode_spk + " terlebih dahulu !");
                        check = false;
                        break;
                    }
                }
            }
            if (check) {
                sql = "SELECT `tb_spk`.`kode_spk`, `tb_spk`.`buyer`, `tb_buyer`.`nama`, `no_revisi`, `tanggal_spk`, `tanggal_awb`, `tanggal_keluar`, `tanggal_fix`, `detail`, `fix`, `status`, `approved`, `tb_pengiriman`.`invoice_no`\n"
                        + "FROM `tb_spk` "
                        + "LEFT JOIN `tb_pengiriman` ON `tb_spk`.`kode_spk` = `tb_pengiriman`.`kode_spk`\n"
                        + "LEFT JOIN `tb_buyer` ON `tb_spk`.`buyer` = `tb_buyer`.`kode_buyer` "
                        + "WHERE `tb_spk`.`kode_spk` = '" + kode_spk + "'";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    if (rs.getDate("tanggal_keluar") != null) {
                        label_tanggal_kirim.setText(new SimpleDateFormat("dd MMMM yyyy").format(rs.getDate("tanggal_keluar")));
                    }
                    if (rs.getDate("tanggal_awb") != null) {
                        label_tanggal_awb.setText(new SimpleDateFormat("dd MMMM yyyy").format(rs.getDate("tanggal_awb")));
                    }
                    label_kode_spk.setText(rs.getString("kode_spk"));
                    txt_buyer_code.setText(rs.getString("buyer"));
                    txt_buyer_name.setText(rs.getString("nama"));
                    txt_keterangan.setText(rs.getString("detail"));
                    tgl_kirim = rs.getString("tanggal_keluar");
                }

                DefaultTableModel model = (DefaultTableModel) table_pengiriman.getModel();
                model.setRowCount(0);
                int total_keping = 0;
                float total_gram = 0;
                sql = "SELECT `tb_box_packing`.`no_box`, `tb_grade_bahan_jadi`.`kode_grade`, `tb_spk_detail`.`grade_buyer`, `tb_box_bahan_jadi`.`keping`, `tb_box_bahan_jadi`.`berat`, "
                        + "`tb_spk_detail`.`kode_kh`, `tb_spk_detail`.`prod_date` "
                        + "FROM `tb_box_packing` "
                        + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_box_packing`.`no_box` = `tb_box_bahan_jadi`.`no_box`"
                        + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode` "
                        + "LEFT JOIN `tb_spk_detail` ON `tb_box_packing`.`no_grade_spk` = `tb_spk_detail`.`no`"
                        + "WHERE `tb_spk_detail`.`kode_spk` = '" + kode_spk + "'";
                rs = Utility.db.getStatement().executeQuery(sql);
                while (rs.next()) {
                    String batch_number = "-";
                    if (rs.getDate("prod_date") != null && rs.getObject("kode_kh") != null) {
                        batch_number = rs.getString("kode_kh").split("-")[0] + "-" + new SimpleDateFormat("yyMMdd").format(rs.getDate("prod_date"));
                    }
                    model.addRow(new Object[]{
                        rs.getString("no_box"),
                        rs.getString("kode_grade"),
                        rs.getString("grade_buyer"),
                        rs.getInt("keping"),
                        rs.getFloat("berat"),
                        batch_number});
                    total_keping = total_keping + rs.getInt("keping");
                    total_gram = total_gram + rs.getFloat("berat");
                }
                ColumnsAutoSizer.sizeColumnsToFit(table_pengiriman);
                label_total_box.setText(Integer.toString(table_pengiriman.getRowCount()));
                label_total_keping.setText(decimalFormat.format(total_keping));
                label_total_gram.setText(decimalFormat.format(total_gram));
            }
        } catch (SQLException ex) {
            Logger.getLogger(JDialog_pengiriman.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_proses_pemindahanBoxActionPerformed

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JDialog_ChooseBuyer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JDialog_pengiriman dialog = new JDialog_pengiriman(new javax.swing.JFrame(), true, "new", null);
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_jenisPengiriman;
    private javax.swing.JComboBox<String> ComboBox_spk;
    private com.toedter.calendar.JDateChooser Date_dokumen;
    private javax.swing.JButton button_cancel;
    private javax.swing.JButton button_hapus;
    private javax.swing.JButton button_proses_pemindahanBox;
    private javax.swing.JButton button_save;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel label_kode_spk;
    private javax.swing.JLabel label_tanggal_awb;
    private javax.swing.JLabel label_tanggal_kirim;
    private javax.swing.JLabel label_total_box;
    private javax.swing.JLabel label_total_gram;
    private javax.swing.JLabel label_total_keping;
    private javax.swing.JTable table_pengiriman;
    private javax.swing.JTextField txt_buyer_code;
    private javax.swing.JTextField txt_buyer_name;
    private javax.swing.JTextField txt_keterangan;
    private javax.swing.JTextField txt_no_dokumen;
    // End of variables declaration//GEN-END:variables
}
