package waleta_system.BahanBaku;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;

public class JDialog_Edit_Insert_LP_Sesekan extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String no_lp_sesekan_lama = null;

    public JDialog_Edit_Insert_LP_Sesekan(java.awt.Frame parent, boolean modal, String no_lp_sesekan) {
        super(parent, modal);
        try {
            initComponents();
            button_hapus_boxAsal.setIcon(Utility.ResizeImageIcon(new javax.swing.ImageIcon(getClass().getResource("/waleta_system/Images/delete-icon.png")), button_hapus_boxAsal.getWidth(), button_hapus_boxAsal.getHeight()));
            this.setResizable(false);
            Utility.db_sub.connect();
            sql = "SELECT `kode_sub` FROM `tb_sub_waleta` WHERE `tanggal_tutup` IS NULL";
            rs = Utility.db_sub.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_SUB_tujuan.addItem(rs.getString("kode_sub"));
            }
            sql = "SELECT `bulu_upah` FROM `tb_tarif_cabut` WHERE 1";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_bulu_upah.addItem(rs.getString("bulu_upah"));
            }

            if (no_lp_sesekan != null) {
                this.no_lp_sesekan_lama = no_lp_sesekan;
                load_data_edit(no_lp_sesekan);
                ComboBox_SUB_tujuan.setEnabled(false);
            }

            refresh_TabelDaftarLP_asal();
        } catch (Exception ex) {
            Logger.getLogger(JDialog_Edit_Insert_LP_Sesekan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refresh_TabelDaftarLP_asal() {
        try {
            float tot_gram = 0;
            DefaultTableModel model = (DefaultTableModel) Table_daftarLP.getModel();
            model.setRowCount(0);
            sql = "SELECT `no_laporan_produksi`, `kode_grade`, `jenis_bulu_lp`, `jumlah_keping`, `gram_sesekan_lp` "
                    + "FROM `tb_laporan_produksi` "
                    + "WHERE `no_lp_sesekan` IS NULL "
                    + "AND `gram_sesekan_lp` > 0 "
                    + "AND `tanggal_lp` >= '2024-01-01' "
                    + "AND `no_laporan_produksi` LIKE '%" + txt_search_no_lp.getText() + "%' "
                    + "ORDER BY `no_laporan_produksi` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("no_laporan_produksi"), 
                    rs.getString("kode_grade"), 
                    rs.getString("jenis_bulu_lp"), 
                    rs.getFloat("gram_sesekan_lp")
                });
                tot_gram = tot_gram + rs.getFloat("gram_sesekan_lp");
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_daftarLP);
            label_total_daftarLP.setText(decimalFormat.format(Table_daftarLP.getRowCount()));
            label_total_gram_daftarLP.setText(decimalFormat.format(tot_gram));
        } catch (SQLException ex) {
            Logger.getLogger(JDialog_Edit_Insert_LP_Sesekan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void load_data_edit(String no_lp_sesekan) {
        try {
            sql = "SELECT `no_lp_sesekan`, `sub`, `tanggal_lp`, `kode_grade`, `bulu_upah`, `memo`, `keping`, `sesekan_bersih`, `sesekan_kuning`, `sesekan_pasir`, `berat_setelah_cuci` "
                    + "FROM `tb_laporan_produksi_sesekan` WHERE `no_lp_sesekan` = '" + no_lp_sesekan + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                ComboBox_SUB_tujuan.setSelectedItem(rs.getString("sub"));
                ComboBox_bulu_upah.setSelectedItem(rs.getString("bulu_upah"));
                Date_LP.setDate(rs.getDate("tanggal_lp"));
                txt_memo_lp.setText(rs.getString("memo"));
                txt_grade_lp_sesekan.setText(rs.getString("kode_grade"));
                txt_gram_sesekan_bersih.setText(rs.getString("sesekan_bersih"));
                txt_gram_sesekan_kuning.setText(rs.getString("sesekan_kuning"));
                txt_gram_sesekan_pasir.setText(rs.getString("sesekan_pasir"));
                txt_gram_setelah_cuci.setText(rs.getString("berat_setelah_cuci"));
            }

            float tot_gram = 0;
            DefaultTableModel model = (DefaultTableModel) Table_daftarLP_terpilih.getModel();
            model.setRowCount(0);
            sql = "SELECT `no_laporan_produksi`, `kode_grade`, `jenis_bulu_lp`, `jumlah_keping`, `gram_sesekan_lp` "
                    + "FROM `tb_laporan_produksi` "
                    + "WHERE `no_lp_sesekan` = '" + no_lp_sesekan + "' "
                    + "ORDER BY `no_laporan_produksi` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString("no_laporan_produksi"), rs.getString("kode_grade"), rs.getString("jenis_bulu_lp"), rs.getFloat("gram_sesekan_lp")});
                tot_gram = tot_gram + rs.getFloat("gram_sesekan_lp");
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_daftarLP_terpilih);
            label_total_lp_terpilih.setText(Integer.toString(Table_daftarLP_terpilih.getRowCount()));
            label_total_gram_lp_terpilih.setText(decimalFormat.format(tot_gram));
        } catch (SQLException ex) {
            Logger.getLogger(JDialog_Edit_Insert_LP_Sesekan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean CheckDuplicateLPAsal(String no_lp) {
        int i = Table_daftarLP_terpilih.getRowCount();
        for (int j = 0; j < i; j++) {
            if (no_lp.equals(Table_daftarLP_terpilih.getValueAt(j, 0).toString())) {
                return true;
            }
        }
        return false;
    }

    public void count() {
        float tot_gram = 0;
        int x = Table_daftarLP_terpilih.getRowCount();
        for (int i = 0; i < x; i++) {
            tot_gram = tot_gram + Float.valueOf(Table_daftarLP_terpilih.getValueAt(i, 3).toString());
        }
        label_total_lp_terpilih.setText(Integer.toString(x));
        label_total_gram_lp_terpilih.setText(Float.toString(tot_gram));
        ColumnsAutoSizer.sizeColumnsToFit(Table_daftarLP_terpilih);
    }

    public void lp_baru() {
        try {
            Utility.db_sub.connect();
            Utility.db.getConnection().setAutoCommit(false);
            Utility.db_sub.getConnection().setAutoCommit(false);
            String no_lp_sesekan = "";
            sql = "SELECT MAX(SUBSTRING(`no_lp_sesekan`, 14, 5)+0) AS 'last_num' FROM `tb_laporan_produksi_sesekan` "
                    + "WHERE `no_lp_sesekan` LIKE 'BP.%' "
                    + "AND YEAR(`tanggal_lp`) = YEAR('" + dateFormat.format(Date_LP.getDate()) + "') "
                    + "AND `sub` = '" + ComboBox_SUB_tujuan.getSelectedItem().toString() + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                int last_num = rs.getInt("last_num");
                no_lp_sesekan = "BP." + ComboBox_SUB_tujuan.getSelectedItem().toString() + "-" + new SimpleDateFormat("YYMM").format(Date_LP.getDate()) + String.format("%05d", last_num + 1);
            }

            float total_gram_berat_asal = Float.valueOf(label_total_gram_lp_terpilih.getText());
            float total_gram_sesekan_bersih = Float.valueOf(txt_gram_sesekan_bersih.getText());
            float total_gram_sesekan_kuning = Float.valueOf(txt_gram_sesekan_kuning.getText());
            float total_gram_sesekan_pasir = Float.valueOf(txt_gram_sesekan_pasir.getText());
            float total_setelah_cuci = Float.valueOf(txt_gram_setelah_cuci.getText());
            float nilai_lp = 0;
            sql = "SELECT `tarif_sesekan_sub` FROM `tb_tarif_cabut` WHERE `bulu_upah` = '" + ComboBox_bulu_upah.getSelectedItem().toString() + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                nilai_lp = total_gram_sesekan_bersih * rs.getFloat("tarif_sesekan_sub");
            }
            String insert_lp_baru = "INSERT INTO `tb_laporan_produksi_sesekan`(`no_lp_sesekan`, `sub`, `tanggal_lp`, `kode_grade`, `bulu_upah`, `keping`, `sesekan_bersih`, `sesekan_kuning`, `sesekan_pasir`, `berat_setelah_cuci`, `nilai_lp`) "
                    + "VALUES ('" + no_lp_sesekan + "','" + ComboBox_SUB_tujuan.getSelectedItem().toString() + "',CURRENT_DATE,'" + txt_grade_lp_sesekan.getText() + "','" + ComboBox_bulu_upah.getSelectedItem().toString() + "','0', '" + total_gram_sesekan_bersih + "', '" + total_gram_sesekan_kuning + "', '" + total_gram_sesekan_pasir + "', '" + total_setelah_cuci + "', '" + nilai_lp + "')";
            Utility.db.getConnection().createStatement();
            Utility.db.getStatement().executeUpdate(insert_lp_baru);
            Utility.db_sub.getConnection().createStatement();
            Utility.db_sub.getStatement().executeUpdate(insert_lp_baru);
            for (int i = 0; i < Table_daftarLP_terpilih.getRowCount(); i++) {
                String update = "UPDATE `tb_laporan_produksi` SET `no_lp_sesekan`='" + no_lp_sesekan + "' "
                        + "WHERE `no_laporan_produksi` = '" + Table_daftarLP_terpilih.getValueAt(i, 0).toString() + "'";
                Utility.db.getConnection().createStatement();
                Utility.db.getStatement().executeUpdate(update);
            }
            Utility.db.getConnection().commit();
            Utility.db_sub.getConnection().commit();
            JOptionPane.showMessageDialog(this, "LP Sesekan SUB " + ComboBox_SUB_tujuan.getSelectedItem() + " telah berhasil dibuat!");
            this.dispose();
        } catch (Exception e) {
            try {
                Utility.db.getConnection().rollback();
                Utility.db_sub.getConnection().rollback();
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_Edit_Insert_LP_Sesekan.class.getName()).log(Level.SEVERE, null, ex);
            }
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JDialog_Edit_Insert_LP_Sesekan.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                Utility.db.getConnection().setAutoCommit(true);
                Utility.db_sub.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_Edit_Insert_LP_Sesekan.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void lp_edit() {
        try {
            Utility.db_sub.connect();
            Utility.db.getConnection().setAutoCommit(false);
            Utility.db_sub.getConnection().setAutoCommit(false);
            String update_lp = "UPDATE `tb_laporan_produksi` SET `no_lp_sesekan`=NULL WHERE `no_lp_sesekan`='" + no_lp_sesekan_lama + "'";
            Utility.db.getConnection().createStatement();
            Utility.db.getStatement().executeUpdate(update_lp);
            String delete_lp_sesekan = "DELETE FROM `tb_laporan_produksi_sesekan` WHERE `no_lp_sesekan`='" + no_lp_sesekan_lama + "'";
            Utility.db.getConnection().createStatement();
            Utility.db.getStatement().executeUpdate(delete_lp_sesekan);
            Utility.db_sub.getConnection().createStatement();
            Utility.db_sub.getStatement().executeUpdate(delete_lp_sesekan);

            float total_gram_berat_asal = Float.valueOf(label_total_gram_lp_terpilih.getText());
            float total_gram_sesekan_bersih = Float.valueOf(txt_gram_sesekan_bersih.getText());
            float total_gram_sesekan_kuning = Float.valueOf(txt_gram_sesekan_kuning.getText());
            float total_gram_sesekan_pasir = Float.valueOf(txt_gram_sesekan_pasir.getText());
            float total_setelah_cuci = Float.valueOf(txt_gram_setelah_cuci.getText());
            float nilai_lp = 0;
            sql = "SELECT `tarif_sesekan_sub` FROM `tb_tarif_cabut` WHERE `bulu_upah` = '" + ComboBox_bulu_upah.getSelectedItem().toString() + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                nilai_lp = total_gram_sesekan_bersih * rs.getFloat("tarif_sesekan_sub");
            }
            String insert_lp_baru = "INSERT INTO `tb_laporan_produksi_sesekan`(`no_lp_sesekan`, `sub`, `tanggal_lp`, `kode_grade`, `bulu_upah`, `keping`, `sesekan_bersih`, `sesekan_kuning`, `sesekan_pasir`, `berat_setelah_cuci`, `nilai_lp`) "
                    + "VALUES ('" + no_lp_sesekan_lama + "','" + ComboBox_SUB_tujuan.getSelectedItem().toString() + "','" + dateFormat.format(Date_LP.getDate()) + "','" + txt_grade_lp_sesekan.getText() + "','" + ComboBox_bulu_upah.getSelectedItem().toString() + "','0', '" + total_gram_sesekan_bersih + "', '" + total_gram_sesekan_kuning + "', '" + total_gram_sesekan_pasir + "', '" + total_setelah_cuci + "', '" + nilai_lp + "')";
            Utility.db.getConnection().createStatement();
            Utility.db.getStatement().executeUpdate(insert_lp_baru);
            Utility.db_sub.getConnection().createStatement();
            Utility.db_sub.getStatement().executeUpdate(insert_lp_baru);
            for (int i = 0; i < Table_daftarLP_terpilih.getRowCount(); i++) {
                sql = "UPDATE `tb_laporan_produksi` SET `no_lp_sesekan`='" + no_lp_sesekan_lama + "' "
                        + "WHERE `no_laporan_produksi` = '" + Table_daftarLP_terpilih.getValueAt(i, 0).toString() + "'";
                Utility.db.getConnection().createStatement();
                Utility.db.getStatement().executeUpdate(sql);
            }
            Utility.db.getConnection().commit();
            Utility.db_sub.getConnection().commit();
            JOptionPane.showMessageDialog(this, "LP Sesekan SUB " + ComboBox_SUB_tujuan.getSelectedItem() + " telah berhasil dibuat!");
            this.dispose();
        } catch (Exception e) {
            try {
                Utility.db.getConnection().rollback();
                Utility.db_sub.getConnection().rollback();
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_Edit_Insert_LP_Sesekan.class.getName()).log(Level.SEVERE, null, ex);
            }
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JDialog_Edit_Insert_LP_Sesekan.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                Utility.db.getConnection().setAutoCommit(true);
                Utility.db_sub.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_Edit_Insert_LP_Sesekan.class.getName()).log(Level.SEVERE, null, ex);
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
        jScrollPane2 = new javax.swing.JScrollPane();
        Table_daftarLP_terpilih = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        label_total_lp_terpilih = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        label_total_gram_lp_terpilih = new javax.swing.JLabel();
        button_save = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        ComboBox_SUB_tujuan = new javax.swing.JComboBox<>();
        txt_search_no_lp = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        label_keterangan = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        Table_daftarLP = new javax.swing.JTable();
        jLabel6 = new javax.swing.JLabel();
        label_total_daftarLP = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        label_total_gram_daftarLP = new javax.swing.JLabel();
        button_hapus_boxAsal = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        txt_gram_sesekan_bersih = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        txt_grade_lp_sesekan = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        ComboBox_bulu_upah = new javax.swing.JComboBox<>();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        txt_gram_setelah_cuci = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txt_memo_lp = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        txt_gram_sesekan_kuning = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        txt_gram_sesekan_pasir = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        Date_LP = new com.toedter.calendar.JDateChooser();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Pengeluaran Bahan Jadi");
        setBackground(new java.awt.Color(255, 255, 255));
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        Table_daftarLP_terpilih.setAutoCreateRowSorter(true);
        Table_daftarLP_terpilih.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_daftarLP_terpilih.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP", "Grade", "Bulu Upah", "Gram Ssk"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class
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
        Table_daftarLP_terpilih.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(Table_daftarLP_terpilih);

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Tabel Data LP terpilih");

        label_total_lp_terpilih.setText("0");

        jLabel7.setText("Total :");

        jLabel10.setText("Gram Sesekan :");

        label_total_gram_lp_terpilih.setText("0");

        button_save.setBackground(new java.awt.Color(255, 255, 255));
        button_save.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_save.setText("Save");
        button_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_saveActionPerformed(evt);
            }
        });

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel5.setText("Buat LP Sesekan");

        jLabel1.setBackground(java.awt.Color.white);
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("SUB Tujuan :");

        ComboBox_SUB_tujuan.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N

        txt_search_no_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_no_lp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_no_lpKeyPressed(evt);
            }
        });

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel18.setText("No LP :");

        label_keterangan.setBackground(new java.awt.Color(255, 255, 255));
        label_keterangan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_keterangan.setForeground(new java.awt.Color(102, 102, 102));
        label_keterangan.setText("*Press ENTER to insert");

        Table_daftarLP.setAutoCreateRowSorter(true);
        Table_daftarLP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_daftarLP.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP", "Grade", "Bulu Upah", "Gram Ssk"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class
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
        Table_daftarLP.setFocusable(false);
        Table_daftarLP.getTableHeader().setReorderingAllowed(false);
        Table_daftarLP.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Table_daftarLPMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(Table_daftarLP);

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Total :");

        label_total_daftarLP.setBackground(new java.awt.Color(255, 255, 255));
        label_total_daftarLP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_daftarLP.setText("0");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Gram Sesekan :");

        label_total_gram_daftarLP.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_daftarLP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_gram_daftarLP.setText("0");

        button_hapus_boxAsal.setBackground(new java.awt.Color(255, 255, 255));
        button_hapus_boxAsal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_hapus_boxAsal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_hapus_boxAsalActionPerformed(evt);
            }
        });

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel19.setText("Gram setelah Cuci :");

        txt_gram_sesekan_bersih.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel20.setText("Grade :");

        txt_grade_lp_sesekan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_grade_lp_sesekan.setText("-");

        jLabel3.setBackground(java.awt.Color.white);
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Bulu Upah :");

        ComboBox_bulu_upah.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel21.setText("Note : Nilai LP = berat sesekan bersih x tarif sesuai bulu upah (data di master grade baku)");

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel22.setText("Berat Sesekan Bersih (Gram) :");

        txt_gram_setelah_cuci.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel8.setBackground(java.awt.Color.white);
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Memo LP :");

        txt_memo_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_memo_lp.setText("HR");

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel23.setText("Berat Sesekan Kuning (Gram) :");

        txt_gram_sesekan_kuning.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel24.setText("Berat Sesekan Pasir (Gram) :");

        txt_gram_sesekan_pasir.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel2.setBackground(java.awt.Color.white);
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Tanggal LP :");

        Date_LP.setDate(new Date());
        Date_LP.setDateFormatString("dd MMM yyyy");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jLabel18)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(txt_search_no_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(label_keterangan, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jLabel6)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(label_total_daftarLP)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel14)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(label_total_gram_daftarLP))
                                            .addComponent(jLabel5))
                                        .addGap(0, 0, Short.MAX_VALUE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel7)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_lp_terpilih)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel10)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_gram_lp_terpilih)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(button_save))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(button_hapus_boxAsal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 451, Short.MAX_VALUE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_SUB_tujuan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_bulu_upah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_grade_lp_sesekan, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_memo_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_gram_setelah_cuci, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 212, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(txt_gram_sesekan_bersih, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel21))
                                    .addComponent(txt_gram_sesekan_kuning, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_gram_sesekan_pasir, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_LP, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_LP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ComboBox_SUB_tujuan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ComboBox_bulu_upah, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(txt_grade_lp_sesekan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_memo_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_gram_setelah_cuci, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_gram_sesekan_bersih, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_gram_sesekan_kuning, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_gram_sesekan_pasir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_search_no_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_keterangan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_hapus_boxAsal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram_daftarLP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(label_total_gram_lp_terpilih)
                    .addComponent(button_save, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_daftarLP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_lp_terpilih)
                    .addComponent(jLabel7))
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
        count();
        boolean cek = true;
        if (txt_gram_setelah_cuci.getText() == null || txt_gram_setelah_cuci.getText().equals("") || txt_gram_setelah_cuci.getText().equals("0")) {
            JOptionPane.showMessageDialog(this, "Silahkan masukkan jumlah gram setelah cuci !");
            cek = false;
        } else if (txt_gram_sesekan_bersih.getText() == null || txt_gram_sesekan_bersih.getText().equals("") || txt_gram_sesekan_bersih.getText().equals("0")) {
            JOptionPane.showMessageDialog(this, "Silahkan masukkan jumlah gram sesekan bersih !");
            cek = false;
        } else if (txt_gram_sesekan_kuning.getText() == null || txt_gram_sesekan_kuning.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Silahkan masukkan jumlah gram sesekan kuning !");
            cek = false;
        } else if (txt_gram_sesekan_pasir.getText() == null || txt_gram_sesekan_pasir.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Silahkan masukkan jumlah gram sesekan pasir !");
            cek = false;
        } else if (Table_daftarLP_terpilih.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Anda belum memilih LP yang akan diproduksi !");
            cek = false;
        } else if (txt_grade_lp_sesekan.getText() == null || txt_grade_lp_sesekan.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Silahkan masukkan jenis grade yang akan diproses!");
            cek = false;
        }
        if (cek) {
            if (no_lp_sesekan_lama == null) {
                lp_baru();
            } else {
                lp_edit();
            }
        }


    }//GEN-LAST:event_button_saveActionPerformed

    private void txt_search_no_lpKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_no_lpKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refresh_TabelDaftarLP_asal();
            if (Table_daftarLP.getRowCount() == 1) {
                if (CheckDuplicateLPAsal(Table_daftarLP.getValueAt(0, 0).toString())) {
                    JOptionPane.showMessageDialog(this, "No LP " + Table_daftarLP.getValueAt(0, 0).toString() + " sudah Masuk");
                } else {
                    DefaultTableModel model = (DefaultTableModel) Table_daftarLP_terpilih.getModel();
                    model.addRow(new Object[]{Table_daftarLP.getValueAt(0, 0),
                        Table_daftarLP.getValueAt(0, 1),
                        Table_daftarLP.getValueAt(0, 2),
                        Table_daftarLP.getValueAt(0, 3)});
//                    ColumnsAutoSizer.sizeColumnsToFit(table_reproses);
                    label_keterangan.setVisible(true);
                    label_keterangan.setForeground(Color.green);
                    label_keterangan.setText("Inserted !");
                    txt_search_no_lp.setText("");
                    txt_search_no_lp.requestFocus();
                    count();
                }
            } else if (Table_daftarLP.getRowCount() > 0) {
                label_keterangan.setVisible(true);
                label_keterangan.setForeground(Color.red);
                label_keterangan.setText("Multiple data selected !");
            } else {
                label_keterangan.setVisible(true);
                label_keterangan.setForeground(Color.red);
                label_keterangan.setText("No Data !");
            }
        }
    }//GEN-LAST:event_txt_search_no_lpKeyPressed

    private void Table_daftarLPMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Table_daftarLPMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            DefaultTableModel model = (DefaultTableModel) Table_daftarLP_terpilih.getModel();
            int x = Table_daftarLP.getSelectedRow();
            if (CheckDuplicateLPAsal(Table_daftarLP.getValueAt(x, 0).toString())) {
                JOptionPane.showMessageDialog(this, "Box " + Table_daftarLP.getValueAt(0, 0).toString() + " sudah Masuk");
            } else {
                model.addRow(new Object[]{Table_daftarLP.getValueAt(x, 0),
                    Table_daftarLP.getValueAt(x, 1),
                    Table_daftarLP.getValueAt(x, 2),
                    Table_daftarLP.getValueAt(x, 3)});
                count();
            }
        }
    }//GEN-LAST:event_Table_daftarLPMouseClicked

    private void button_hapus_boxAsalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_hapus_boxAsalActionPerformed
        // TODO add your handling code here:
        int i = Table_daftarLP_terpilih.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) Table_daftarLP_terpilih.getModel();
        if (i != -1) {
            model.removeRow(Table_daftarLP_terpilih.getRowSorter().convertRowIndexToModel(i));
        }
//        ColumnsAutoSizer.sizeColumnsToFit(table_reproses);
        count();
    }//GEN-LAST:event_button_hapus_boxAsalActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_SUB_tujuan;
    private javax.swing.JComboBox<String> ComboBox_bulu_upah;
    private com.toedter.calendar.JDateChooser Date_LP;
    private javax.swing.JTable Table_daftarLP;
    private javax.swing.JTable Table_daftarLP_terpilih;
    private javax.swing.JButton button_hapus_boxAsal;
    private javax.swing.JButton button_save;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel label_keterangan;
    private javax.swing.JLabel label_total_daftarLP;
    private javax.swing.JLabel label_total_gram_daftarLP;
    private javax.swing.JLabel label_total_gram_lp_terpilih;
    private javax.swing.JLabel label_total_lp_terpilih;
    private javax.swing.JTextField txt_grade_lp_sesekan;
    private javax.swing.JTextField txt_gram_sesekan_bersih;
    private javax.swing.JTextField txt_gram_sesekan_kuning;
    private javax.swing.JTextField txt_gram_sesekan_pasir;
    private javax.swing.JTextField txt_gram_setelah_cuci;
    private javax.swing.JTextField txt_memo_lp;
    private javax.swing.JTextField txt_search_no_lp;
    // End of variables declaration//GEN-END:variables
}
