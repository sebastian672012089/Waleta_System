package waleta_system.BahanJadi;

import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.Utility;

public class JDialog_TambahDataTutupan extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    String status, kode_tutupan;
    Date date_min = null, date_max = null;

    public JDialog_TambahDataTutupan(java.awt.Frame parent, boolean modal, String status, String kode_tutupan, Date mulai, Date selesai, String memo_tutupan) {
        super(parent, modal);
        initComponents();
        this.status = status;
        this.kode_tutupan = kode_tutupan;
        try {
            //give icon for button
            button_tambah.setIcon(Utility.ResizeImageIcon(new javax.swing.ImageIcon(getClass().getResource("/waleta_system/Images/arrow_right.png")), button_tambah.getWidth(), button_tambah.getHeight()));
            button_kurang.setIcon(Utility.ResizeImageIcon(new javax.swing.ImageIcon(getClass().getResource("/waleta_system/Images/arrow_left.png")), button_kurang.getWidth(), button_kurang.getHeight()));

            ComboBox_kodeRSB.removeAllItems();
            sql = "SELECT `no_registrasi` FROM `tb_rumah_burung` WHERE LENGTH(`no_registrasi`) IN (3, 4)";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_kodeRSB.addItem(rs.getString("no_registrasi"));
            }

            if ("Edit".equals(status)) {
                DefaultTableModel model = (DefaultTableModel) tabel_LP_tutupan.getModel();
                label_judul.setText("Edit Data Tutupan Grading");
                txt_kode_tutupan.setText(kode_tutupan);
                Date_Mulai_tutupan.setDate(mulai);
                Date_selesai_tutupan.setDate(selesai);
                txt_memo_tutupan.setText(memo_tutupan);
                sql = "SELECT `kode_asal`, `tanggal_grading`, `tb_bahan_baku_masuk_cheat`.`no_registrasi` "
                        + "FROM `tb_bahan_jadi_masuk` "
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_bahan_jadi_masuk`.`kode_asal` = `tb_laporan_produksi`.`no_laporan_produksi` "
                        + "LEFT JOIN `tb_bahan_baku_masuk_cheat` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta` "
                        + "WHERE `kode_tutupan` = '" + kode_tutupan + "'";
                rs = Utility.db.getStatement().executeQuery(sql);
                while (rs.next()) {
                    model.addRow(new Object[]{rs.getString("kode_asal"), rs.getDate("tanggal_grading"), rs.getString("no_registrasi")});
                }
                label_total_lp_tutupan.setText(Integer.toString(tabel_LP_tutupan.getRowCount()));
                refreshTable();
            } else if ("Tambah".equals(status)) {
                refreshTable();
                label_judul.setText("Tambah Data Tutupan Grading");
                txt_kode_tutupan.setVisible(false);
                jLabel2.setVisible(false);
//                txt_kode_tutupan.setText(null);
//                txt_kode_tutupan.setEditable(true);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_TambahDataTutupan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getNewkodeTutupan() {
        String newCode = null;
        try {
            int number = 0;
            String new_number = null;
            sql = "SELECT COUNT(`kode_tutupan`) AS 'last' FROM `tb_tutupan_grading` "
                    + "WHERE YEAR(`tgl_mulai_tutupan`)='" + new SimpleDateFormat("yyyy").format(Date_Mulai_tutupan.getDate()) + "'";
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
            newCode = kode_tutupan + "-" + new SimpleDateFormat("yyMM").format(Date_Mulai_tutupan.getDate()) + new_number;
        } catch (SQLException | NullPointerException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_TambahDataTutupan.class.getName()).log(Level.SEVERE, null, ex);
        }
        return newCode;
    }

    public void refreshTable() {
        try {
            DefaultTableModel model = (DefaultTableModel) table_daftar_lp.getModel();
            model.setRowCount(0);
            if (kode_tutupan.substring(0, 3).equals("GRD")) {
                sql = "SELECT `kode_asal`, `tanggal_masuk`, `tanggal_grading`, `tb_bahan_baku_masuk_cheat`.`no_registrasi` "
                        + "FROM `tb_bahan_jadi_masuk` "
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_bahan_jadi_masuk`.`kode_asal` = `tb_laporan_produksi`.`no_laporan_produksi` "
                        + "LEFT JOIN `tb_bahan_baku_masuk_cheat` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta` "
                        + "WHERE `kode_asal` LIKE '%" + txt_search_kodeAsal.getText() + "%' "
                        + "AND `kode_asal` LIKE 'WL%' "
                        + "AND `tanggal_grading` IS NOT NULL AND `kode_tutupan` IS NULL";
            } else {
                sql = "SELECT `kode_asal`, `tanggal_masuk`, `tanggal_grading`, `tb_bahan_baku_masuk_cheat`.`no_registrasi` "
                        + "FROM `tb_bahan_jadi_masuk` "
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_bahan_jadi_masuk`.`kode_asal` = `tb_laporan_produksi`.`no_laporan_produksi` "
                        + "LEFT JOIN `tb_bahan_baku_masuk_cheat` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta` "
                        + "WHERE `kode_asal` LIKE '%" + txt_search_kodeAsal.getText() + "%' "
                        + "AND `kode_asal` NOT LIKE 'WL%' "
                        + "AND `tanggal_grading` IS NOT NULL AND `kode_tutupan` IS NULL";
            }
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[4];
            while (rs.next()) {
                row[0] = rs.getString("kode_asal");
                row[1] = rs.getDate("tanggal_masuk");
                row[2] = rs.getDate("tanggal_grading");
                row[3] = rs.getString("no_registrasi");
                model.addRow(row);
            }
            label_total_daftar_lp.setText(Integer.toString(table_daftar_lp.getRowCount()));
//            ColumnsAutoSizer.sizeColumnsToFit(table_daftar_lp, 10);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_TambahDataTutupan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean checkDouble(String lp) {
        int i = tabel_LP_tutupan.getRowCount();
        for (int j = 0; j < i; j++) {
            if (lp.equals(tabel_LP_tutupan.getValueAt(j, 0).toString())) {
                return true;
            }
        }
        return false;
    }

    public void refreshMinMaxDate() {
        int total_data = tabel_LP_tutupan.getRowCount();
        date_min = null;
        date_max = null;
        try {
            for (int i = 0; i < total_data; i++) {
                Date tgl = new SimpleDateFormat("yyyy-MM-dd").parse(tabel_LP_tutupan.getValueAt(i, 1).toString());
                if (date_min == null && date_max == null) {
                    date_min = tgl;
                    date_max = tgl;
                    Date_Mulai_tutupan.setDate(tgl);
                    Date_selesai_tutupan.setDate(tgl);
                } else if (tgl.after(date_max)) {
                    Date_selesai_tutupan.setDate(tgl);
                    date_max = tgl;
                } else if (tgl.before(date_min)) {
                    Date_Mulai_tutupan.setDate(tgl);
                    date_min = tgl;
                }
            }
        } catch (ParseException ex) {
            Logger.getLogger(JDialog_TambahDataTutupan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean addDataToTable(String lp, String tanggal, String rsb) {
        DefaultTableModel model = (DefaultTableModel) tabel_LP_tutupan.getModel();
        if (checkDouble(lp)) {
            JOptionPane.showMessageDialog(this, lp + " Sudah masuk");
            return false;
        } else {
            model.insertRow(0, new Object[]{lp, tanggal, rsb});
            label_total_lp_tutupan.setText(Integer.toString(tabel_LP_tutupan.getRowCount()));
            try {
                Date tgl = new SimpleDateFormat("yyyy-MM-dd").parse(tanggal);
                if (date_min == null && date_max == null) {
                    date_min = tgl;
                    date_max = tgl;
                    Date_Mulai_tutupan.setDate(tgl);
                } else if (tgl.after(date_max)) {
                    Date_selesai_tutupan.setDate(tgl);
                    date_max = tgl;
                } else if (tgl.before(date_min)) {
                    Date_Mulai_tutupan.setDate(tgl);
                    date_min = tgl;
                }
            } catch (ParseException ex) {
                Logger.getLogger(JDialog_TambahDataTutupan.class.getName()).log(Level.SEVERE, null, ex);
            }
            return true;
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

        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        label_total_lp_tutupan = new javax.swing.JLabel();
        button_tambah = new javax.swing.JButton();
        txt_kode_tutupan = new javax.swing.JTextField();
        Date_selesai_tutupan = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        button_ok = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        Date_Mulai_tutupan = new com.toedter.calendar.JDateChooser();
        ComboBox_kodeRSB = new javax.swing.JComboBox<>();
        button_kurang = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txt_search_kodeAsal = new javax.swing.JTextField();
        button_search = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_daftar_lp = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        label_total_daftar_lp = new javax.swing.JLabel();
        label_judul = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabel_LP_tutupan = new javax.swing.JTable();
        button_cancel = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txt_memo_tutupan = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Tutupan Grading Bahan Jadi");
        setMaximumSize(new java.awt.Dimension(1366, 700));
        setMinimumSize(new java.awt.Dimension(250, 260));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Total :");

        label_total_lp_tutupan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_lp_tutupan.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_lp_tutupan.setText("0");

        button_tambah.setBackground(new java.awt.Color(255, 255, 255));
        button_tambah.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_tambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_tambahActionPerformed(evt);
            }
        });

        txt_kode_tutupan.setEditable(false);
        txt_kode_tutupan.setBackground(new java.awt.Color(255, 255, 255));
        txt_kode_tutupan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_selesai_tutupan.setBackground(new java.awt.Color(255, 255, 255));
        Date_selesai_tutupan.setDateFormatString("dd MMMM yyyy");
        Date_selesai_tutupan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_selesai_tutupan.setMaxSelectableDate(new Date());

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Tanggal Selesai Tutupan :");

        button_ok.setBackground(new java.awt.Color(255, 255, 255));
        button_ok.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_ok.setText("SAVE");
        button_ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_okActionPerformed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Kode Tutupan :");

        Date_Mulai_tutupan.setBackground(new java.awt.Color(255, 255, 255));
        Date_Mulai_tutupan.setDateFormatString("dd MMMM yyyy");
        Date_Mulai_tutupan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_Mulai_tutupan.setMaxSelectableDate(new Date());

        ComboBox_kodeRSB.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_kurang.setBackground(new java.awt.Color(255, 255, 255));
        button_kurang.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_kurang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_kurangActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Kode Pembelian / No. LP :");

        txt_search_kodeAsal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_kodeAsal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_kodeAsalKeyPressed(evt);
            }
        });

        button_search.setBackground(new java.awt.Color(255, 255, 255));
        button_search.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search.setText("Search");
        button_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_searchActionPerformed(evt);
            }
        });

        table_daftar_lp.setAutoCreateRowSorter(true);
        table_daftar_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_daftar_lp.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Asal / LP", "Tgl Masuk", "Tgl Grading", "RSB CT1", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.Boolean.class
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
        table_daftar_lp.getTableHeader().setReorderingAllowed(false);
        table_daftar_lp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table_daftar_lpMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(table_daftar_lp);
        if (table_daftar_lp.getColumnModel().getColumnCount() > 0) {
            table_daftar_lp.getColumnModel().getColumn(4).setResizable(false);
            table_daftar_lp.getColumnModel().getColumn(4).setPreferredWidth(30);
        }

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Total :");

        label_total_daftar_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_total_daftar_lp.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_daftar_lp.setText("0");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_kodeAsal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_daftar_lp)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_kodeAsal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_daftar_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        label_judul.setBackground(new java.awt.Color(255, 255, 255));
        label_judul.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_judul.setText("Tutupan Grading Barang Jadi");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Tanggal Mulai Tutupan :");

        tabel_LP_tutupan.setAutoCreateRowSorter(true);
        tabel_LP_tutupan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_LP_tutupan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No. LP / Kode", "Tgl Grading", "RSB CT1"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tabel_LP_tutupan);

        button_cancel.setBackground(new java.awt.Color(255, 255, 255));
        button_cancel.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_cancel.setText("CANCEL");
        button_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_cancelActionPerformed(evt);
            }
        });

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Kode RSB :");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Memo Tutupan :");

        txt_memo_tutupan.setBackground(new java.awt.Color(255, 255, 255));
        txt_memo_tutupan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(label_judul)
                        .addGap(703, 703, 703))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                            .addComponent(txt_kode_tutupan, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(Date_Mulai_tutupan, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(Date_selesai_tutupan, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(ComboBox_kodeRSB, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel5)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txt_memo_tutupan))))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(button_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(button_kurang, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel6)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_lp_tutupan)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(button_cancel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button_ok)))))
                        .addContainerGap())))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_judul)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_kode_tutupan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Mulai_tutupan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_selesai_tutupan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_memo_tutupan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ComboBox_kodeRSB, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(button_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_kurang, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 422, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(button_ok, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_cancel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_lp_tutupan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void button_okActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_okActionPerformed
        try {
            boolean check = true;
            String new_kode = getNewkodeTutupan();
            Utility.db.getConnection().setAutoCommit(false);
            if (txt_kode_tutupan.getText() == null
                    || Date_Mulai_tutupan.getDate() == null
                    || Date_selesai_tutupan.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Silahkan Lengkapi Data diatas !!");
                check = false;
            }

            String[] kode_rsb = ComboBox_kodeRSB.getSelectedItem().toString().split("-");
            if (check && ("Tambah".equals(status))) {
                sql = "INSERT INTO `tb_tutupan_grading`(`kode_tutupan`, `tgl_mulai_tutupan`, `tgl_selesai_tutupan`, `kode_rumah_burung`, `memo_tutupan`) "
                        + "VALUES ('" + new_kode + "','" + dateFormat.format(Date_Mulai_tutupan.getDate()) + "','" + dateFormat.format(Date_selesai_tutupan.getDate()) + "', '" + kode_rsb[0] + "', '" + txt_memo_tutupan.getText() + "')";
                Utility.db.getConnection().createStatement();
                if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                    for (int i = 0; i < tabel_LP_tutupan.getRowCount(); i++) {
                        sql = "UPDATE `tb_bahan_jadi_masuk` SET `kode_tutupan`='" + new_kode + "' WHERE `kode_asal` = '" + tabel_LP_tutupan.getValueAt(i, 0) + "'";
                        Utility.db.getConnection().createStatement();
                        Utility.db.getStatement().executeUpdate(sql);
                    }
                    Utility.db.getConnection().commit();
                    this.dispose();
                    JPanel_TutupanGradingBahanJadi.button_search.doClick();
                    JOptionPane.showMessageDialog(this, "Data Berhasil Ditambahkan");
                } else {
                    JOptionPane.showMessageDialog(this, "insert tutupan error");
                }
            } else if (check && ("Edit".equals(status))) {
                sql = "UPDATE `tb_bahan_jadi_masuk` SET `kode_tutupan` = NULL WHERE `kode_tutupan` = '" + kode_tutupan + "'";
                Utility.db.getConnection().createStatement();
                Utility.db.getStatement().executeUpdate(sql);

                for (int i = 0; i < tabel_LP_tutupan.getRowCount(); i++) {
                    sql = "UPDATE `tb_bahan_jadi_masuk` SET "
                            + "`kode_tutupan`='" + kode_tutupan + "' "
                            + "WHERE `kode_asal` = '" + tabel_LP_tutupan.getValueAt(i, 0).toString() + "'";
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(sql);
                }

                sql = "UPDATE `tb_tutupan_grading` SET "
                        + "`tgl_mulai_tutupan`='" + dateFormat.format(Date_Mulai_tutupan.getDate()) + "',"
                        + "`tgl_selesai_tutupan`='" + dateFormat.format(Date_selesai_tutupan.getDate()) + "',"
                        + "`memo_tutupan`='" + txt_memo_tutupan.getText() + "',"
                        + "`kode_rumah_burung` = '" + kode_rsb[0] + "' "
                        + "WHERE `kode_tutupan`='" + kode_tutupan + "'";
                Utility.db.getConnection().createStatement();
                Utility.db.getStatement().executeUpdate(sql);

                Utility.db.getConnection().commit();
                this.dispose();
                JPanel_TutupanGradingBahanJadi.button_search.doClick();
                JOptionPane.showMessageDialog(this, "Data Berhasil Diubah");
            }
        } catch (SQLException e) {
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
    }//GEN-LAST:event_button_okActionPerformed

    private void button_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_cancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_button_cancelActionPerformed

    private void button_tambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_tambahActionPerformed
        Boolean isChecked = null;
        int total_row = table_daftar_lp.getRowCount();
        for (int i = 0; i < total_row; i++) {
            if (table_daftar_lp.getValueAt(i, 4) != null) {
                isChecked = Boolean.valueOf(table_daftar_lp.getValueAt(i, 4).toString());
            } else {
                isChecked = false;
            }
            if (isChecked) {
                String lp = table_daftar_lp.getValueAt(i, 0).toString();
                String tanggal = table_daftar_lp.getValueAt(i, 2).toString();
                String rsb = "";
                if (table_daftar_lp.getValueAt(i, 3) != null) {
                    rsb = table_daftar_lp.getValueAt(i, 3).toString();
                }
                addDataToTable(lp, tanggal, rsb);
            }
        }
    }//GEN-LAST:event_button_tambahActionPerformed

    private void button_kurangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_kurangActionPerformed
        DefaultTableModel model = (DefaultTableModel) tabel_LP_tutupan.getModel();
        int x = tabel_LP_tutupan.getSelectedRow();
        System.out.println("Kurang : " + x);
        if (x > -1) {
            model.removeRow(tabel_LP_tutupan.getRowSorter().convertRowIndexToModel(x));
            label_total_lp_tutupan.setText(Integer.toString(tabel_LP_tutupan.getRowCount()));
            refreshMinMaxDate();
        }
    }//GEN-LAST:event_button_kurangActionPerformed

    private void table_daftar_lpMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_daftar_lpMouseClicked
        // TODO add your handling code here:
        int x = table_daftar_lp.getSelectedRow();
        String lp = table_daftar_lp.getValueAt(x, 0).toString();
        String tanggal = table_daftar_lp.getValueAt(x, 2).toString();
        String rsb = "";
        if (table_daftar_lp.getValueAt(x, 3) != null) {
            rsb = table_daftar_lp.getValueAt(x, 3).toString();
        }
        if (evt.getClickCount() == 2) {
            if (x > -1) {
                addDataToTable(lp, tanggal, rsb);
            }
        }
    }//GEN-LAST:event_table_daftar_lpMouseClicked

    private void txt_search_kodeAsalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_kodeAsalKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
            if (table_daftar_lp.getRowCount() == 1) {
                String lp = table_daftar_lp.getValueAt(0, 0).toString();
                String tanggal = table_daftar_lp.getValueAt(0, 2).toString();
                String rsb = "";
                if (table_daftar_lp.getValueAt(0, 3) != null) {
                    rsb = table_daftar_lp.getValueAt(0, 3).toString();
                }
                if (addDataToTable(lp, tanggal, rsb)) {
                    txt_search_kodeAsal.setText("");
                    refreshTable();
                }
            }
        }
    }//GEN-LAST:event_txt_search_kodeAsalKeyPressed

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_button_searchActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_kodeRSB;
    private com.toedter.calendar.JDateChooser Date_Mulai_tutupan;
    private com.toedter.calendar.JDateChooser Date_selesai_tutupan;
    private javax.swing.JButton button_cancel;
    private javax.swing.JButton button_kurang;
    private javax.swing.JButton button_ok;
    private javax.swing.JButton button_search;
    private javax.swing.JButton button_tambah;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel label_judul;
    private javax.swing.JLabel label_total_daftar_lp;
    private javax.swing.JLabel label_total_lp_tutupan;
    private javax.swing.JTable tabel_LP_tutupan;
    public static javax.swing.JTable table_daftar_lp;
    private javax.swing.JTextField txt_kode_tutupan;
    private javax.swing.JTextField txt_memo_tutupan;
    private javax.swing.JTextField txt_search_kodeAsal;
    // End of variables declaration//GEN-END:variables
}
