package waleta_system.BahanBaku;

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
import waleta_system.Packing.JDialog_pengiriman;

public class JDialog_PengeluaranBahanBaku extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    float total_keping_keluar = 0, tot_keping_daftarBox = 0;
    float total_gram_keluar = 0, tot_gram_daftarBox = 0;
    String status = null, kode_keluar = null;
    ArrayList<String> DataBoxAwal = new ArrayList<>();

    public JDialog_PengeluaranBahanBaku(java.awt.Frame parent, boolean modal, String status, String kode_keluar) {
        super(parent, modal);
        this.status = status;
        this.kode_keluar = kode_keluar;
        initComponents();
        if (kode_keluar != null) {
            getData();
        }
    }

    public void getData() {
//        int i = JPanel_pengiriman.tabel_data_pengiriman.getSelectedRow();
//        txt_kode_keluar.setText(kode_keluar);
//        txt_no_dokumen.setText(JPanel_pengiriman.tabel_data_pengiriman.getValueAt(i, 2).toString());
//        try {
//            Date tgl_invoice = dateFormat.parse(JPanel_pengiriman.tabel_data_pengiriman.getValueAt(i, 3).toString());
//            Date tgl_kirim = dateFormat.parse(JPanel_pengiriman.tabel_data_pengiriman.getValueAt(i, 4).toString());
//            Date_invoice.setDate(tgl_invoice);
//            Date_pengiriman.setDate(tgl_kirim);
//        } catch (ParseException ex) {
//            Logger.getLogger(JDialog_pengiriman.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        txt_buyer_code.setText(JPanel_pengiriman.tabel_data_pengiriman.getValueAt(i, 5).toString());
//        txt_buyer_name.setText(JPanel_pengiriman.tabel_data_pengiriman.getValueAt(i, 6).toString());
//        ComboBox_jenisPengiriman.setSelectedItem(JPanel_pengiriman.tabel_data_pengiriman.getValueAt(i, 7).toString());
//        txt_keterangan.setText(JDialog_PengeluaranBahanBaku.tabel_data_pengiriman.getValueAt(i, 9).toString());
//
//        try {
//            DefaultTableModel model = (DefaultTableModel) table_pengiriman.getModel();
//            model.setRowCount(0);
//            sql = "SELECT `tb_box_packing`.`no_box`, `tb_grade_bahan_jadi`.`kode_grade`, `tb_box_packing`.`tanggal_masuk`, `batch_number`, `keping`, `berat` FROM `tb_box_packing` "
//                    + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_box_packing`.`no_box` = `tb_box_bahan_jadi`.`no_box`"
//                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`"
//                    + "WHERE `invoice_pengiriman` = '" + invoice + "'";
//            rs = Utility.db.getStatement().executeQuery(sql);
//            while (rs.next()) {
//                model.addRow(new Object[]{
//                    rs.getString("no_box"),
//                    rs.getString("kode_grade"),
//                    rs.getInt("keping"),
//                    rs.getFloat("berat"),
//                    rs.getString("batch_number")});
//                DataBoxAwal.add(rs.getString("no_box"));
//            }
//            ColumnsAutoSizer.sizeColumnsToFit(table_pengiriman);
//            count_pengiriman();
//
//        } catch (SQLException ex) {
//            Logger.getLogger(JDialog_PengeluaranBahanBaku.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    public void count_pengiriman() {
        total_keping_keluar = 0;
        total_gram_keluar = 0;
        int x = table_pengeluaran.getRowCount();
        for (int i = 0; i < x; i++) {
            try {
                total_keping_keluar = total_keping_keluar + Integer.valueOf(table_pengeluaran.getValueAt(i, 2).toString());
                total_gram_keluar = total_gram_keluar + Float.valueOf(table_pengeluaran.getValueAt(i, 3).toString());
            } catch (NullPointerException e) {
                JOptionPane.showMessageDialog(this, "Tidak Boleh ada data yang kosong !");
            }
        }
        label_total_data.setText(Integer.toString(x));
        label_total_keping.setText(decimalFormat.format(total_keping_keluar));
        label_total_gram.setText(decimalFormat.format(total_gram_keluar));
        ColumnsAutoSizer.sizeColumnsToFit(table_pengeluaran);
    }

    public void New() {
        try {
            Utility.db.getConnection().setAutoCommit(false);
            sql = "INSERT INTO `tb_bahan_baku_keluar1`(`kode_pengeluaran`, `jenis_pengeluaran`, `tgl_keluar`, `customer_baku`, `keterangan`) "
                    + "VALUES ('" + txt_kode_keluar.getText() + "','" + ComboBox_jenisPengeluaran.getSelectedItem().toString() + "','" + dateFormat.format(Date_keluar.getDate()) + "','" + txt_kode_cust.getText() + "','" + txt_keterangan.getText() + "')";
            Utility.db.getConnection().createStatement();
            if ((Utility.db.getStatement().executeUpdate(sql)) > 0) {
                for (int i = 0; i < table_pengeluaran.getRowCount(); i++) {
                    sql = "INSERT INTO `tb_bahan_baku_keluar`(`no_kartu_waleta`, `kode_grade`, `total_keping_keluar`, `total_berat_keluar`, `kode_pengeluaran`) "
                            + "VALUES ('" + table_pengeluaran.getValueAt(i, 0) + "','" + table_pengeluaran.getValueAt(i, 1) + "','" + table_pengeluaran.getValueAt(i, 2) + "','" + table_pengeluaran.getValueAt(i, 3) + "','" + txt_kode_keluar.getText() + "')";
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
        jLabel12 = new javax.swing.JLabel();
        txt_kode_cust = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txt_kode_keluar = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_pengeluaran = new javax.swing.JTable();
        label_total_data = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        label_total_keping = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        label_total_gram = new javax.swing.JLabel();
        button_delete = new javax.swing.JButton();
        Label_kode_grade = new javax.swing.JLabel();
        label_no_kartu = new javax.swing.JLabel();
        button_Keluar_select_kartu = new javax.swing.JButton();
        label_keping = new javax.swing.JLabel();
        label_berat_keluar = new javax.swing.JLabel();
        txt_berat = new javax.swing.JTextField();
        txt_keping = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        label_kartu_waleta_keluar = new javax.swing.JLabel();
        label_kartu_waleta_keluar1 = new javax.swing.JLabel();
        button_insert = new javax.swing.JButton();
        button_cancel = new javax.swing.JButton();
        ComboBox_jenisPengeluaran = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        Date_keluar = new com.toedter.calendar.JDateChooser();
        jLabel5 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        button_save = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txt_nama_cust = new javax.swing.JTextField();
        txt_keterangan = new javax.swing.JTextField();
        button_choose = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Bahan Baku Keluar");
        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel12.setText("Jenis Pengeluaran :");

        txt_kode_cust.setEditable(false);
        txt_kode_cust.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel9.setText("Customer :");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel3.setText("Kode Keluar :");

        txt_kode_keluar.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Detail Pengeluaran", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 11))); // NOI18N

        table_pengeluaran.setAutoCreateRowSorter(true);
        table_pengeluaran.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_pengeluaran.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Kartu", "Grade", "Keping", "Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
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
        table_pengeluaran.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table_pengeluaran);

        label_total_data.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_data.setText("0");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Total Data :");

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

        button_delete.setBackground(new java.awt.Color(255, 255, 255));
        button_delete.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete.setText("Delete");
        button_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_deleteActionPerformed(evt);
            }
        });

        Label_kode_grade.setBackground(new java.awt.Color(255, 255, 255));
        Label_kode_grade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Label_kode_grade.setText("Kode Grade");
        Label_kode_grade.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        label_no_kartu.setBackground(new java.awt.Color(255, 255, 255));
        label_no_kartu.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_no_kartu.setText("No. Kartu Waleta");
        label_no_kartu.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        button_Keluar_select_kartu.setBackground(new java.awt.Color(255, 255, 255));
        button_Keluar_select_kartu.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_Keluar_select_kartu.setText("Select From Data");
        button_Keluar_select_kartu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_Keluar_select_kartuActionPerformed(evt);
            }
        });

        label_keping.setBackground(new java.awt.Color(255, 255, 255));
        label_keping.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_keping.setText("Jumlah Keping :");

        label_berat_keluar.setBackground(new java.awt.Color(255, 255, 255));
        label_berat_keluar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_berat_keluar.setText("Berat :");

        txt_berat.setEditable(false);
        txt_berat.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        txt_keping.setEditable(false);
        txt_keping.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel22.setText("Gram");

        label_kartu_waleta_keluar.setBackground(new java.awt.Color(255, 255, 255));
        label_kartu_waleta_keluar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_kartu_waleta_keluar.setText("No. Kartu Waleta :");

        label_kartu_waleta_keluar1.setBackground(new java.awt.Color(255, 255, 255));
        label_kartu_waleta_keluar1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_kartu_waleta_keluar1.setText("Kode Grade :");

        button_insert.setBackground(new java.awt.Color(255, 255, 255));
        button_insert.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_insert.setText("insert");
        button_insert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_insertActionPerformed(evt);
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
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_keping)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_berat_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_keping, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_kartu_waleta_keluar1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_kartu_waleta_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(button_Keluar_select_kartu)
                            .addComponent(Label_kode_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_no_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addComponent(button_delete)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(button_insert))
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(txt_berat)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel22))
                                    .addComponent(txt_keping, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_kartu_waleta_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_no_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_kartu_waleta_keluar1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Label_kode_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_Keluar_select_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_keping, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_keping, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_berat, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_berat_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(button_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_insert, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 177, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_total_keping)
                    .addComponent(jLabel8)
                    .addComponent(label_total_gram)
                    .addComponent(label_total_data)
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

        ComboBox_jenisPengeluaran.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        ComboBox_jenisPengeluaran.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Penjualan", "Sampel" }));

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel10.setText("Nama :");

        Date_keluar.setBackground(new java.awt.Color(255, 255, 255));
        Date_keluar.setDateFormatString("dd MMMM yyyy");
        Date_keluar.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel5.setText("Tanggal Keluar :");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel15.setText("Keterangan :");

        button_save.setBackground(new java.awt.Color(255, 255, 255));
        button_save.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_save.setText("SAVE");
        button_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_saveActionPerformed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel2.setText("DEPARTEMEN BAHAN BAKU");

        txt_nama_cust.setEditable(false);
        txt_nama_cust.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N

        txt_keterangan.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N

        button_choose.setBackground(new java.awt.Color(255, 255, 255));
        button_choose.setFont(new java.awt.Font("SansSerif", 0, 11)); // NOI18N
        button_choose.setText("Choose Buyer");
        button_choose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_chooseActionPerformed(evt);
            }
        });

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        jLabel1.setText("PENGELUARAN BAHAN BAKU");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(571, 571, 571)
                        .addComponent(button_cancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_save))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(txt_kode_keluar)
                                            .addComponent(ComboBox_jenisPengeluaran, 0, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txt_kode_cust)
                                            .addComponent(txt_nama_cust, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button_choose))
                                    .addComponent(txt_keterangan, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGap(13, 13, 13)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_kode_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_jenisPengeluaran, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_kode_cust, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_choose, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_nama_cust, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_keterangan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void button_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_deleteActionPerformed
        // TODO add your handling code here:
        int i = table_pengeluaran.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) table_pengeluaran.getModel();
        if (i != -1) {
            model.removeRow(table_pengeluaran.getRowSorter().convertRowIndexToModel(i));
            ColumnsAutoSizer.sizeColumnsToFit(table_pengeluaran);
            count_pengiriman();
        }
    }//GEN-LAST:event_button_deleteActionPerformed

    private void button_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_cancelActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_button_cancelActionPerformed

    private void button_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_saveActionPerformed
        // TODO add your handling code here:
        boolean check = true;
        if (txt_kode_keluar.getText() == null
                || txt_kode_cust.getText() == null
                || Date_keluar.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Silahkan Lengkapi Data diatas !!");
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

    private void button_Keluar_select_kartuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_Keluar_select_kartuActionPerformed
        // TODO add your handling code here:
        Stock_Bahan_Baku dialog = new Stock_Bahan_Baku(new javax.swing.JFrame(), true, "keluar");
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
    }//GEN-LAST:event_button_Keluar_select_kartuActionPerformed

    private void button_insertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_insertActionPerformed
        // TODO add your handling code here:
        boolean check_duplicates = false;
        for (int i = 0; i < table_pengeluaran.getRowCount(); i++) {
            if (table_pengeluaran.getValueAt(i, 0).toString().equals(label_no_kartu.getText()) && table_pengeluaran.getValueAt(i, 1).toString().equals(Label_kode_grade.getText())) {
                check_duplicates = true;
            }
        }
        if (check_duplicates) {
            JOptionPane.showMessageDialog(this, "No kartu " + label_no_kartu.getText() + " grade " + Label_kode_grade.getText() + " sudah masuk!");
        } else {
            DefaultTableModel model = (DefaultTableModel) table_pengeluaran.getModel();
            model.addRow(new Object[]{
                label_no_kartu.getText(),
                Label_kode_grade.getText(),
                txt_keping.getText(),
                txt_berat.getText()});
            ColumnsAutoSizer.sizeColumnsToFit(table_pengeluaran);
            count_pengiriman();
        }
    }//GEN-LAST:event_button_insertActionPerformed

    private void button_chooseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_chooseActionPerformed
        // TODO add your handling code here:
        JDialog_ChooseCust dialog = new JDialog_ChooseCust(new javax.swing.JFrame(), true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);

        int a = JDialog_ChooseCust.table_list_customer.getSelectedRow();
        if (a >= 0) {
            String kode = JDialog_ChooseCust.table_list_customer.getValueAt(a, 0).toString();
            String nama = JDialog_ChooseCust.table_list_customer.getValueAt(a, 1).toString();
            txt_kode_cust.setText(kode);
            txt_nama_cust.setText(nama);
        }
    }//GEN-LAST:event_button_chooseActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_jenisPengeluaran;
    private com.toedter.calendar.JDateChooser Date_keluar;
    public static javax.swing.JLabel Label_kode_grade;
    private javax.swing.JButton button_Keluar_select_kartu;
    private javax.swing.JButton button_cancel;
    private javax.swing.JButton button_choose;
    private javax.swing.JButton button_delete;
    public static javax.swing.JButton button_insert;
    private javax.swing.JButton button_save;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_berat_keluar;
    private javax.swing.JLabel label_kartu_waleta_keluar;
    private javax.swing.JLabel label_kartu_waleta_keluar1;
    private javax.swing.JLabel label_keping;
    public static javax.swing.JLabel label_no_kartu;
    private javax.swing.JLabel label_total_data;
    private javax.swing.JLabel label_total_gram;
    private javax.swing.JLabel label_total_keping;
    private javax.swing.JTable table_pengeluaran;
    public static javax.swing.JTextField txt_berat;
    public static javax.swing.JTextField txt_keping;
    private javax.swing.JTextField txt_keterangan;
    private javax.swing.JTextField txt_kode_cust;
    private javax.swing.JTextField txt_kode_keluar;
    private javax.swing.JTextField txt_nama_cust;
    // End of variables declaration//GEN-END:variables
}
