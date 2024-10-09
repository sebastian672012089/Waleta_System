package waleta_system.Packing;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.BahanJadi.JDialog_Setor_Packing;
import waleta_system.Class.ColumnsAutoSizer;

import waleta_system.Class.Utility;

public class JDialog_pengiriman_SI extends javax.swing.JDialog {

     
    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    float total_keping_kirim = 0, tot_keping_daftarBox = 0;
    float total_gram_kirim = 0, tot_gram_daftarBox = 0;
    String status = null, invoice = null;
    ArrayList<String> DataBoxAwal = new ArrayList<>();

    public JDialog_pengiriman_SI(java.awt.Frame parent, boolean modal, String status, String invoice) {
        super(parent, modal);
        this.status = status;
        this.invoice = invoice;
        try {
            initComponents();
            button_hapus.setIcon(Utility.ResizeImageIcon(new javax.swing.ImageIcon(getClass().getResource("/waleta_system/Images/delete-icon.png")), button_hapus.getWidth(), button_hapus.getHeight()));
            
            refresh_TabelDaftarBox();
            if (invoice != null) {
                getData();
            }
        } catch (Exception ex) {
            Logger.getLogger(JDialog_pengiriman.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void getData() {
        try {
            sql = "SELECT `invoice_no`, `tanggal_invoice`, `tanggal_pengiriman`, `tb_pengiriman`.`kode_buyer`, `no_dokumen`, `jenis_pengiriman`, `status_pengiriman`, `keterangan`, `tb_pengiriman`.`kode_spk`, `tb_spk`.`tanggal_tk`, `tb_buyer`.`nama` "
                    + "FROM `tb_pengiriman` "
                    + "LEFT JOIN `tb_buyer` ON `tb_pengiriman`.`kode_buyer` = `tb_buyer`.`kode_buyer` "
                    + "LEFT JOIN `tb_spk` ON `tb_pengiriman`.`kode_spk` = `tb_spk`.`kode_spk` "
                    + "WHERE `invoice_no` = '" + invoice + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                txt_no_dokumen.setText(invoice);
                Date_dokumen.setDate(rs.getDate("tanggal_invoice"));
                ComboBox_jenisPengiriman.setSelectedItem(rs.getString("jenis_pengiriman"));
                Date_pengiriman.setDate(rs.getDate("tanggal_pengiriman"));
                Date_dokumen.setDate(rs.getDate("tanggal_invoice"));
                txt_buyer_code.setText(rs.getString("kode_buyer"));
                txt_buyer_name.setText(rs.getString("nama"));
                txt_keterangan.setText(rs.getString("keterangan"));
            }

            DefaultTableModel model = (DefaultTableModel) table_pengiriman.getModel();
            model.setRowCount(0);
            sql = "SELECT `tb_box_packing`.`no_box`, `tb_grade_bahan_jadi`.`kode_grade`, `tb_box_packing`.`tanggal_masuk`, `batch_number`, `keping`, `berat` FROM `tb_box_packing` "
                    + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_box_packing`.`no_box` = `tb_box_bahan_jadi`.`no_box`"
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`"
                    + "WHERE `invoice_pengiriman` = '" + invoice + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("no_box"),
                    rs.getString("kode_grade"),
                    rs.getInt("keping"),
                    rs.getFloat("berat"),
                    rs.getString("batch_number")});
                DataBoxAwal.add(rs.getString("no_box"));
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_pengiriman);
            count_pengiriman();

        } catch (SQLException ex) {
            Logger.getLogger(JDialog_Setor_Packing.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refresh_TabelDaftarBox() {
        try {
            tot_keping_daftarBox = 0;
            tot_gram_daftarBox = 0;
            DefaultTableModel model = (DefaultTableModel) Table_daftarBox.getModel();
            model.setRowCount(0);
            sql = "SELECT `tb_box_packing`.`no_box`, `tb_grade_bahan_jadi`.`kode_grade`, `tb_box_packing`.`tanggal_masuk`, `keping`, `berat` "
                    + "FROM `tb_box_packing` "
                    + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_box_packing`.`no_box` = `tb_box_bahan_jadi`.`no_box`"
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`"
                    + "WHERE `tb_box_packing`.`no_box` LIKE '%" + txt_search_box.getText() + "%' "
                    + "AND `status` = 'STOK' "
                    + "AND `invoice_pengiriman` IS NULL "
                    + "AND `tb_box_packing`.`no_grade_spk` = '601'";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString("no_box"), rs.getString("kode_grade"), new SimpleDateFormat("dd MMMM yyyy").format(rs.getDate("tanggal_masuk")), rs.getInt("keping"), rs.getFloat("berat")});
                tot_keping_daftarBox = tot_keping_daftarBox + rs.getInt("keping");
                tot_gram_daftarBox = tot_gram_daftarBox + rs.getFloat("berat");
            }
            label_total_daftarBox.setText(Integer.toString(Table_daftarBox.getRowCount()));
            label_total_keping_daftarBox.setText(decimalFormat.format(tot_keping_daftarBox));
            label_total_gram_daftarBox.setText(decimalFormat.format(tot_gram_daftarBox));
            ColumnsAutoSizer.sizeColumnsToFit(Table_daftarBox);
        } catch (SQLException ex) {
            Logger.getLogger(JDialog_Setor_Packing.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void count_pengiriman() {
        total_keping_kirim = 0;
        total_gram_kirim = 0;
        int x = table_pengiriman.getRowCount();
        for (int i = 0; i < x; i++) {
            try {
                total_keping_kirim = total_keping_kirim + Integer.valueOf(table_pengiriman.getValueAt(i, 2).toString());
                total_gram_kirim = total_gram_kirim + Float.valueOf(table_pengiriman.getValueAt(i, 3).toString());
            } catch (NullPointerException e) {
                JOptionPane.showMessageDialog(this, "Tidak Boleh ada data yang kosong !");
            }
        }
        label_total_box.setText(Integer.toString(x));
        label_total_keping.setText(decimalFormat.format(total_keping_kirim));
        label_total_gram.setText(decimalFormat.format(total_gram_kirim));
        ColumnsAutoSizer.sizeColumnsToFit(table_pengiriman);
    }

    public boolean CheckDuplicateBoxAsal(String no_box) {
        int i = table_pengiriman.getRowCount();
        for (int j = 0; j < i; j++) {
            if (no_box.equals(table_pengiriman.getValueAt(j, 0).toString())) {
                return true;
            }
        }
        return false;
    }

    public void New() {
        try {
            Utility.db.getConnection().setAutoCommit(false);
            sql = "INSERT INTO `tb_pengiriman`(`invoice_no`, `tanggal_invoice`, `tanggal_pengiriman`, `jenis_pengiriman`, `kode_buyer`, `no_dokumen`, `status_pengiriman`, `keterangan`) "
                    + "VALUES ('" + txt_no_dokumen.getText() + "','" + dateFormat.format(Date_dokumen.getDate()) + "','" + dateFormat.format(Date_pengiriman.getDate()) + "','" + ComboBox_jenisPengiriman.getSelectedItem() + "','" + txt_buyer_code.getText() + "','" + txt_no_dokumen.getText() + "', 'PROSES', '" + txt_keterangan.getText() + "')";
            Utility.db.getConnection().createStatement();
            if ((Utility.db.getStatement().executeUpdate(sql)) > 0) {
                for (int i = 0; i < table_pengiriman.getRowCount(); i++) {
                    sql = "UPDATE `tb_box_packing` SET `status`='PROSES',"
                            + "`batch_number`='" + table_pengiriman.getValueAt(i, 4).toString() + "', "
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
                    + "`tanggal_invoice`='" + dateFormat.format(Date_dokumen.getDate()) + "',"
                    + "`tanggal_pengiriman`='" + dateFormat.format(Date_pengiriman.getDate()) + "',"
                    + "`kode_buyer`='" + txt_buyer_code.getText() + "',"
                    + "`no_dokumen`='" + txt_no_dokumen.getText() + "',"
                    + "`jenis_pengiriman`='" + ComboBox_jenisPengiriman.getSelectedItem() + "',"
                    + "`status_pengiriman`='PROSES', "
                    + "`keterangan`='" + txt_keterangan.getText() + "'"
                    + "WHERE `invoice_no`='" + invoice + "'";
            System.out.println(sql);
            Utility.db.getConnection().createStatement();
            if ((Utility.db.getStatement().executeUpdate(sql)) > 0) {
                for (int i = 0; i < table_pengiriman.getRowCount(); i++) {
                    sql = "UPDATE `tb_box_packing` SET `status`='PROSES',"
                            + "`batch_number`='" + table_pengiriman.getValueAt(i, 4).toString() + "',"
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
        jPanel2 = new javax.swing.JPanel();
        label_total_keping_daftarBox = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        label_total_gram_daftarBox = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        label_total_daftarBox = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        Table_daftarBox = new javax.swing.JTable();
        label_keterangan = new javax.swing.JLabel();
        txt_search_box = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
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
        button_cancel = new javax.swing.JButton();
        button_save = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        ComboBox_jenisPengiriman = new javax.swing.JComboBox<>();
        txt_buyer_name = new javax.swing.JTextField();
        txt_keterangan = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        Date_dokumen = new com.toedter.calendar.JDateChooser();
        txt_no_dokumen = new javax.swing.JTextField();
        Date_pengiriman = new com.toedter.calendar.JDateChooser();
        jLabel5 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        txt_buyer_code = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();

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

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Daftar Box Packing", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 11))); // NOI18N

        label_total_keping_daftarBox.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_daftarBox.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_keping_daftarBox.setText("0");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Gram :");

        label_total_gram_daftarBox.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_daftarBox.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_gram_daftarBox.setText("0");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Total :");

        label_total_daftarBox.setBackground(new java.awt.Color(255, 255, 255));
        label_total_daftarBox.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_daftarBox.setText("0");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Keping :");

        Table_daftarBox.setAutoCreateRowSorter(true);
        Table_daftarBox.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_daftarBox.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Box", "Grade", "Tgl Masuk", "Kpg", "Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
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

        label_keterangan.setBackground(new java.awt.Color(255, 255, 255));
        label_keterangan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_keterangan.setForeground(new java.awt.Color(102, 102, 102));
        label_keterangan.setText("*Press ENTER to insert");

        txt_search_box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_box.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_boxKeyPressed(evt);
            }
        });

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel18.setText("No Box :");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_box, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_keterangan, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel11)
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
                    .addComponent(jScrollPane3))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_search_box, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_keterangan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_total_keping_daftarBox)
                    .addComponent(jLabel14)
                    .addComponent(label_total_gram_daftarBox)
                    .addComponent(jLabel11)
                    .addComponent(label_total_daftarBox)
                    .addComponent(jLabel13))
                .addContainerGap())
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Daftar Box Pengiriman", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 11))); // NOI18N

        table_pengiriman.setAutoCreateRowSorter(true);
        table_pengiriman.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_pengiriman.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Box", "Grade", "Keping", "Gram", "Batch Numb"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true
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
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel3Layout.createSequentialGroup()
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
                        .addGap(0, 225, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(button_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(button_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_total_keping)
                    .addComponent(jLabel8)
                    .addComponent(label_total_gram)
                    .addComponent(label_total_box)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addContainerGap())
        );

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

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        ComboBox_jenisPengiriman.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_jenisPengiriman.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Sampel Internal" }));

        txt_buyer_name.setEditable(false);
        txt_buyer_name.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_buyer_name.setText("Sample Internal");

        txt_keterangan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel4.setText("No Dokumen :");

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel12.setText("Jenis Pengiriman :");

        Date_dokumen.setBackground(new java.awt.Color(255, 255, 255));
        Date_dokumen.setDate(new Date());
        Date_dokumen.setDateFormatString("dd MMM yyyy");
        Date_dokumen.setFont(new java.awt.Font("Schadow BT", 0, 12)); // NOI18N

        txt_no_dokumen.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        Date_pengiriman.setBackground(new java.awt.Color(255, 255, 255));
        Date_pengiriman.setDate(new Date());
        Date_pengiriman.setDateFormatString("dd MMM yyyy");
        Date_pengiriman.setFont(new java.awt.Font("Schadow BT", 0, 12)); // NOI18N

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel5.setText("Tanggal Keluar :");

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel16.setText("Tanggal Dokumen :");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel9.setText("Buyer Code :");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel15.setText("Keterangan :");

        txt_buyer_code.setEditable(false);
        txt_buyer_code.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_buyer_code.setText("SI");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel10.setText("Buyer Name :");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_keterangan)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(txt_buyer_name, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txt_buyer_code, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(ComboBox_jenisPengiriman, javax.swing.GroupLayout.Alignment.LEADING, 0, 180, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Date_dokumen, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date_pengiriman, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_no_dokumen))
                        .addGap(0, 170, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_no_dokumen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_pengiriman, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(Date_dokumen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_jenisPengiriman, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_buyer_code, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_buyer_name, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_keterangan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel1))
                                .addGap(290, 290, 290)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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

    private void button_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_saveActionPerformed
        // TODO add your handling code here:
        boolean check = true;
        if (txt_no_dokumen.getText() == null
                || txt_no_dokumen.getText() == null
                || txt_buyer_code.getText() == null
                || Date_pengiriman.getDate() == null
                || Date_dokumen.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Silahkan Lengkapi Data diatas !!");
            check = false;
        }
        try {
            for (int i = 0; i < table_pengiriman.getRowCount(); i++) {
                String batch_number = table_pengiriman.getValueAt(i, 4).toString();
            }
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(this, "Silahkan isi kolom Batch number !!");
            check = false;
        }

        if (check) {
            if ("new".equals(status)) {
                New();
            } else if ("edit".equals(status)) {
                Edit();
            }
        }
    }//GEN-LAST:event_button_saveActionPerformed

    private void Table_daftarBoxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Table_daftarBoxMouseClicked
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_pengiriman.getModel();
        int x = Table_daftarBox.getSelectedRow();
        if (evt.getClickCount() == 2) {
            if (CheckDuplicateBoxAsal(Table_daftarBox.getValueAt(x, 0).toString())) {
                JOptionPane.showMessageDialog(this, "Box " + Table_daftarBox.getValueAt(x, 0).toString() + " sudah masuk");
            } else {
                model.addRow(new Object[]{
                    Table_daftarBox.getValueAt(x, 0),
                    Table_daftarBox.getValueAt(x, 1),
                    Table_daftarBox.getValueAt(x, 3),
                    Table_daftarBox.getValueAt(x, 4)});
                ColumnsAutoSizer.sizeColumnsToFit(table_pengiriman);
                count_pengiriman();
            }
        }
    }//GEN-LAST:event_Table_daftarBoxMouseClicked

    private void txt_search_boxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_boxKeyPressed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_pengiriman.getModel();
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refresh_TabelDaftarBox();
            if (Table_daftarBox.getRowCount() == 1) {
                if (CheckDuplicateBoxAsal(Table_daftarBox.getValueAt(0, 0).toString())) {
                    JOptionPane.showMessageDialog(this, "Box " + Table_daftarBox.getValueAt(0, 0).toString() + " sudah masuk");
                } else {
                    model.addRow(new Object[]{
                        Table_daftarBox.getValueAt(0, 0),
                        Table_daftarBox.getValueAt(0, 1),
                        Table_daftarBox.getValueAt(0, 3),
                        Table_daftarBox.getValueAt(0, 4)});
                    ColumnsAutoSizer.sizeColumnsToFit(table_pengiriman);
                    label_keterangan.setVisible(true);
                    label_keterangan.setForeground(Color.green);
                    label_keterangan.setText("Inserted !");
                    txt_search_box.setText("");
                    txt_search_box.requestFocus();
                    count_pengiriman();
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
                JDialog_pengiriman_SI dialog = new JDialog_pengiriman_SI(new javax.swing.JFrame(), true, "new", null);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_jenisPengiriman;
    private com.toedter.calendar.JDateChooser Date_dokumen;
    private com.toedter.calendar.JDateChooser Date_pengiriman;
    private javax.swing.JTable Table_daftarBox;
    private javax.swing.JButton button_cancel;
    private javax.swing.JButton button_hapus;
    private javax.swing.JButton button_save;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel label_keterangan;
    private javax.swing.JLabel label_total_box;
    private javax.swing.JLabel label_total_daftarBox;
    private javax.swing.JLabel label_total_gram;
    private javax.swing.JLabel label_total_gram_daftarBox;
    private javax.swing.JLabel label_total_keping;
    private javax.swing.JLabel label_total_keping_daftarBox;
    private javax.swing.JTable table_pengiriman;
    private javax.swing.JTextField txt_buyer_code;
    private javax.swing.JTextField txt_buyer_name;
    private javax.swing.JTextField txt_keterangan;
    private javax.swing.JTextField txt_no_dokumen;
    private javax.swing.JTextField txt_search_box;
    // End of variables declaration//GEN-END:variables
}
