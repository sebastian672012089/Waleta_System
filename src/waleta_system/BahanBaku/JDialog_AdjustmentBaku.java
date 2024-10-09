package waleta_system.BahanBaku;

import java.awt.Color;
import java.awt.Component;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;

import waleta_system.Class.StockBahanBaku;

public class JDialog_AdjustmentBaku extends javax.swing.JDialog {

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String sql = null;
    ResultSet rs;
    String no_kartu, kode_grade;
    int min_kpg = 0, min_gram = 0;

    public JDialog_AdjustmentBaku(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setResizable(false);

        Table_stock_bahan_baku.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && Table_stock_bahan_baku.getSelectedRow() != -1) {
                    int i = Table_stock_bahan_baku.getSelectedRow();
                    if (i > -1) {
                        label_kartu.setText(Table_stock_bahan_baku.getValueAt(i, 0).toString());
                        label_grade.setText(Table_stock_bahan_baku.getValueAt(i, 1).toString());
                        txt_kpg_diambil.setText(Table_stock_bahan_baku.getValueAt(i, 6).toString());
                        txt_gram_diambil.setText(Table_stock_bahan_baku.getValueAt(i, 7).toString());
                    }
                }
            }
        });

        try {

            sql = "SELECT `kode_grade` FROM `tb_grade_bahan_baku` ORDER BY `kode_grade`";
            ResultSet rs1 = Utility.db.getStatement().executeQuery(sql);
            while (rs1.next()) {
                ComboBox_Search_grade.addItem(rs1.getString("kode_grade"));
            }
            ComboBox_Search_grade.setSelectedIndex(1);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(JDialog_AdjustmentBaku.this, e);
        } catch (Exception ex) {
            Logger.getLogger(JDialog_AdjustmentBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
        refreshTable();
        AutoCompleteDecorator.decorate(ComboBox_Search_grade);
    }

    public ArrayList<StockBahanBaku> StockList() {
        ArrayList<StockBahanBaku> StockList = new ArrayList<>();
        try {
            String kode = ComboBox_Search_grade.getSelectedItem().toString();
            if (ComboBox_Search_grade.getSelectedItem().equals("All")) {
                kode = "";
            }
            sql = "SELECT `no_grading`, `tb_grading_bahan_baku`.`no_kartu_waleta`, `tb_grading_bahan_baku`.`kode_grade`, `tb_grading_bahan_baku`.`jumlah_keping`, `total_berat`\n"
                    + "FROM `tb_grading_bahan_baku`\n"
                    + "LEFT JOIN `tb_bahan_baku_masuk` ON `tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`\n"
                    + "WHERE `tb_bahan_baku_masuk`.`tgl_timbang` IS NOT NULL AND `tb_grading_bahan_baku`.`kode_grade` LIKE '%" + kode + "%' AND `tb_grading_bahan_baku`.`no_kartu_waleta` LIKE '%" + txt_no_kartu.getText() + "%'\n";

            rs = Utility.db.getStatement().executeQuery(sql);
            StockBahanBaku Stock;
            while (rs.next()) {
                Stock = new StockBahanBaku(rs.getString("no_grading"), rs.getString("no_kartu_waleta"), null, rs.getString("kode_grade"), rs.getInt("jumlah_keping"), rs.getInt("total_berat"), 0);
                StockList.add(Stock);
            }
        } catch (SQLException ex) {
            Logger.getLogger(JDialog_AdjustmentBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
        return StockList;
    }

    public void show_data_Stock() {
        ResultSet rs_jual, rs_lp;
        int sisa_kpg = 0, sisa_gram = 0;
        int kpg_lp = 0, gram_lp = 0, kpg_jual = 0, gram_keluar = 0;

        ArrayList<StockBahanBaku> list = StockList();
        DefaultTableModel model = (DefaultTableModel) Table_stock_bahan_baku.getModel();

        Object[] row = new Object[8];
        for (int i = 0; i < list.size(); i++) {
            try {
                String sql_lp = "SELECT SUM(`jumlah_keping`) AS 'keping', SUM(`berat_basah`) AS 'berat' FROM `tb_laporan_produksi` WHERE `kode_grade` = '" + list.get(i).getKode_grade() + "'  AND `no_kartu_waleta` = '" + list.get(i).getNo_kartu_waleta() + "'";
                rs_lp = Utility.db.getStatement().executeQuery(sql_lp);
                if (rs_lp.next()) {
                    kpg_lp = rs_lp.getInt("keping");
                    gram_lp = rs_lp.getInt("berat");
                }
                String sql_jual = "SELECT SUM(`total_keping_keluar`) AS 'keping', SUM(`total_berat_keluar`) AS 'berat' FROM `tb_bahan_baku_keluar` WHERE `kode_grade` = '" + list.get(i).getKode_grade() + "'  AND `no_kartu_waleta` = '" + list.get(i).getNo_kartu_waleta() + "'";
                rs_jual = Utility.db.getStatement().executeQuery(sql_jual);
                if (rs_jual.next()) {
                    kpg_jual = rs_jual.getInt("keping");
                    gram_keluar = rs_jual.getInt("berat");
                }
            } catch (SQLException e) {
            }
            row[0] = list.get(i).getNo_kartu_waleta();
            row[1] = list.get(i).getKode_grade();
            row[2] = list.get(i).getJumlah_keping();
            row[3] = list.get(i).getTotal_berat();
            row[4] = kpg_lp + kpg_jual;
            row[5] = gram_lp + gram_keluar;

            sisa_kpg = list.get(i).getJumlah_keping() - (kpg_lp + kpg_jual);
            sisa_gram = list.get(i).getTotal_berat() - (gram_lp + gram_keluar);
            row[6] = sisa_kpg;
            row[7] = sisa_gram;

            model.addRow(row);
        }

        Table_stock_bahan_baku.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                switch (column) {
                    case 7:
                        if ((int) Table_stock_bahan_baku.getValueAt(row, column) > 0) {
                            if (isSelected) {
                                comp.setBackground(Table_stock_bahan_baku.getSelectionBackground());
                                comp.setForeground(Table_stock_bahan_baku.getSelectionForeground());
                            } else {
                                comp.setBackground(Table_stock_bahan_baku.getBackground());
                                comp.setForeground(Table_stock_bahan_baku.getForeground());
                            }
                        } else {
                            if (isSelected) {
                                comp.setBackground(Table_stock_bahan_baku.getSelectionBackground());
                                comp.setForeground(Color.red);
                            } else {
                                comp.setBackground(Color.PINK);
                                comp.setForeground(Color.red);
                            }
                        }
                        break;
                    default:
                        if (isSelected) {
                            comp.setBackground(Table_stock_bahan_baku.getSelectionBackground());
                            comp.setForeground(Table_stock_bahan_baku.getSelectionForeground());
                        } else {
                            comp.setBackground(Table_stock_bahan_baku.getBackground());
                            comp.setForeground(Table_stock_bahan_baku.getForeground());
                        }
                        break;
                }
                return comp;
            }
        });
        Table_stock_bahan_baku.repaint();
    }

    public void refreshTable() {
        DefaultTableModel model = (DefaultTableModel) Table_stock_bahan_baku.getModel();
        model.setRowCount(0);
        show_data_Stock();
        ColumnsAutoSizer.sizeColumnsToFit(Table_stock_bahan_baku);
    }

    public boolean akses() {
        try {
            JPasswordField pf = new JPasswordField();
            int okCxl = JOptionPane.showConfirmDialog(null, pf, "Password Required :", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (okCxl == JOptionPane.OK_OPTION) {
                String password = new String(pf.getPassword());
                sql = "SELECT * FROM `tb_login` WHERE (`user`='fani' OR `user`='priska07') AND `pass`='" + password + "'";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Access Granted !");
                    return true;
                } else {
                    JOptionPane.showMessageDialog(this, "Access Denied!");
                    return false;
                }
            } else {
                return false;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JDialog_AdjustmentBaku.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public void tambah() {
        int i = Table_stock_bahan_baku.getSelectedRow();
        boolean ok = true;
        int keping = 0, gram = 0, keping_sisa = 0, gram_sisa = 0;
        try {
            keping = Integer.valueOf(txt_kpg_diambil.getText());
            gram = Integer.valueOf(txt_gram_diambil.getText());
            keping_sisa = (int) Table_stock_bahan_baku.getValueAt(i, 6);
            gram_sisa = (int) Table_stock_bahan_baku.getValueAt(i, 7);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Inputan salah");
            ok = false;
        }
        if (ok && akses()) {
            try {
                int kpg_awal = Integer.valueOf(Table_stock_bahan_baku.getValueAt(i, 2).toString());
                int gram_awal = Integer.valueOf(Table_stock_bahan_baku.getValueAt(i, 3).toString());
                int kpg_akhir = kpg_awal + Integer.valueOf(txt_kpg_diambil.getText());
                int gram_akhir = gram_awal + Integer.valueOf(txt_gram_diambil.getText());
                String insert = "INSERT INTO `tb_adjustment_baku`(`jenis_adjustment`, `no_kartu_waleta`, `grade_bahan_baku`, `keping`, `gram`, `tgl_adjustment`, `tgl_stok_opname`, `kpg_awal`, `gram_awal`, `kpg_akhir`, `gram_akhir`) "
                        + "VALUES ('PENAMBAHAN','" + label_kartu.getText() + "','" + label_grade.getText() + "','" + txt_kpg_diambil.getText() + "','" + txt_gram_diambil.getText() + "','" + dateFormat.format(new Date()) + "','" + dateFormat.format(Date_stokOpname.getDate()) + "', '" + kpg_awal + "', '" + gram_awal + "', '" + kpg_akhir + "', '" + gram_akhir + "')";
                Utility.db.getConnection().createStatement();
                Utility.db.getStatement().executeUpdate(insert);

                String update1 = "UPDATE `tb_grading_bahan_baku` SET "
                        + "`jumlah_keping`=(`jumlah_keping`+" + keping + "),"
                        + "`total_berat`=(`total_berat`+" + gram + ") WHERE "
                        + "`no_kartu_waleta`='" + label_kartu.getText() + "' AND"
                        + "`kode_grade`='" + label_grade.getText() + "'";
                Utility.db.getConnection().createStatement();
                Utility.db.getStatement().executeUpdate(update1);

                String update2 = "UPDATE `tb_bahan_baku_masuk` SET "
                        + "`keping_real`=(`keping_real`+" + keping + "),"
                        + "`berat_real`=(`berat_real`+" + gram + ") WHERE "
                        + "`no_kartu_waleta`='" + label_kartu.getText() + "'";
                Utility.db.getConnection().createStatement();
                Utility.db.getStatement().executeUpdate(update2);

                JOptionPane.showMessageDialog(this, "Data Updated !");
                this.dispose();
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_AdjustmentBaku.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void kurang() {
        int i = Table_stock_bahan_baku.getSelectedRow();
        boolean ok = true;
        int keping = 0, gram = 0, keping_sisa = 0, gram_sisa = 0;
        try {
            keping = Integer.valueOf(txt_kpg_diambil.getText());
            gram = Integer.valueOf(txt_gram_diambil.getText());
            keping_sisa = (int) Table_stock_bahan_baku.getValueAt(i, 6);
            gram_sisa = (int) Table_stock_bahan_baku.getValueAt(i, 7);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Inputan salah");
            ok = false;
        }
        if (keping > keping_sisa && gram > gram_sisa) {
            JOptionPane.showMessageDialog(this, "Maaf jumlah keping dan gram diambil melebihi stok yang tersisa !");
        } else if (keping > keping_sisa) {
            JOptionPane.showMessageDialog(this, "Maaf jumlah keping diambil melebihi stok kpg yang tersisa !");
        } else if (gram > gram_sisa) {
            JOptionPane.showMessageDialog(this, "Maaf jumlah gram diambil melebihi stok gram yang tersisa !");
        } else if (ok && akses()) {
            try {
                int kpg_awal = Integer.valueOf(Table_stock_bahan_baku.getValueAt(i, 2).toString());
                int gram_awal = Integer.valueOf(Table_stock_bahan_baku.getValueAt(i, 3).toString());
                int kpg_akhir = kpg_awal - Integer.valueOf(txt_kpg_diambil.getText());
                int gram_akhir = gram_awal - Integer.valueOf(txt_gram_diambil.getText());
                String insert = "INSERT INTO `tb_adjustment_baku`(`jenis_adjustment`, `no_kartu_waleta`, `grade_bahan_baku`, `keping`, `gram`, `tgl_adjustment`, `tgl_stok_opname`, `kpg_awal`, `gram_awal`, `kpg_akhir`, `gram_akhir`) "
                        + "VALUES ('PENGURANGAN','" + label_kartu.getText() + "','" + label_grade.getText() + "','" + txt_kpg_diambil.getText() + "','" + txt_gram_diambil.getText() + "','" + dateFormat.format(new Date()) + "','" + dateFormat.format(Date_stokOpname.getDate()) + "', '" + kpg_awal + "', '" + gram_awal + "', '" + kpg_akhir + "', '" + gram_akhir + "')";
                Utility.db.getConnection().createStatement();
                Utility.db.getStatement().executeUpdate(insert);

                String update1 = "UPDATE `tb_grading_bahan_baku` SET "
                        + "`jumlah_keping`=(`jumlah_keping`-" + keping + "),"
                        + "`total_berat`=(`total_berat`-" + gram + ") WHERE "
                        + "`no_kartu_waleta`='" + label_kartu.getText() + "' AND "
                        + "`kode_grade`='" + label_grade.getText() + "'";
                Utility.db.getConnection().createStatement();
                Utility.db.getStatement().executeUpdate(update1);

                String update2 = "UPDATE `tb_bahan_baku_masuk` SET "
                        + "`keping_real`=(`keping_real`-" + keping + "),"
                        + "`berat_real`=(`berat_real`-" + gram + ") WHERE "
                        + "`no_kartu_waleta`='" + label_kartu.getText() + "'";
                Utility.db.getConnection().createStatement();
                Utility.db.getStatement().executeUpdate(update2);

                JOptionPane.showMessageDialog(this, "Data Updated !");
                this.dispose();
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_AdjustmentBaku.class.getName()).log(Level.SEVERE, null, ex);
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

        jScrollPane1 = new javax.swing.JScrollPane();
        Table_stock_bahan_baku = new javax.swing.JTable();
        ComboBox_Search_grade = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        button_ok = new javax.swing.JButton();
        button_cancel = new javax.swing.JButton();
        txt_no_kartu = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        button_refresh = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        txt_kpg_diambil = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txt_gram_diambil = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        ComboBox_jenisAdjustment = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        label_kartu = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        label_grade = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        Date_stokOpname = new com.toedter.calendar.JDateChooser();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        Table_stock_bahan_baku.setAutoCreateRowSorter(true);
        Table_stock_bahan_baku.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_stock_bahan_baku.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Kartu", "Grade", "Kpg Masuk", "Berat Masuk", "Kpg Keluar", "Berat Keluar", "Stok Kpg", "Stok Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_stock_bahan_baku.getTableHeader().setReorderingAllowed(false);
        Table_stock_bahan_baku.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Table_stock_bahan_bakuMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(Table_stock_bahan_baku);

        ComboBox_Search_grade.setEditable(true);
        ComboBox_Search_grade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_Search_grade.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Kode Grade :");

        button_ok.setBackground(new java.awt.Color(255, 255, 255));
        button_ok.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_ok.setText("OK");
        button_ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_okActionPerformed(evt);
            }
        });

        button_cancel.setBackground(new java.awt.Color(255, 255, 255));
        button_cancel.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_cancel.setText("Cancel");
        button_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_cancelActionPerformed(evt);
            }
        });

        txt_no_kartu.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_no_kartu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_no_kartuActionPerformed(evt);
            }
        });

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("No Kartu :");

        button_refresh.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh.setText("Refresh");
        button_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refreshActionPerformed(evt);
            }
        });

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Adjustment :");

        txt_kpg_diambil.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Kpg");

        txt_gram_diambil.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Gram");

        ComboBox_jenisAdjustment.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_jenisAdjustment.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Dikurangkan", "Ditambahkan" }));

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Ke Kartu");

        label_kartu.setBackground(new java.awt.Color(255, 255, 255));
        label_kartu.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_kartu.setText("-");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Grade");

        label_grade.setBackground(new java.awt.Color(255, 255, 255));
        label_grade.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_grade.setText("-");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("Tanggal Stok Opname :");

        Date_stokOpname.setBackground(new java.awt.Color(255, 255, 255));
        Date_stokOpname.setDateFormatString("dd MMMM yyyy");
        Date_stokOpname.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_stokOpname.setMaxSelectableDate(new Date());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txt_kpg_diambil, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_gram_diambil, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(ComboBox_jenisAdjustment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_kartu)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_grade)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 173, Short.MAX_VALUE)
                        .addComponent(button_cancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_ok))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_Search_grade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_no_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_refresh))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_stokOpname, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ComboBox_Search_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_no_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(button_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_gram_diambil, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_ok, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_jenisAdjustment, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_cancel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_kpg_diambil, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_stokOpname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void Table_stock_bahan_bakuMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Table_stock_bahan_bakuMousePressed
        int i = Table_stock_bahan_baku.getSelectedRow();
        if (i > -1) {
            txt_kpg_diambil.setText(Table_stock_bahan_baku.getValueAt(i, 6).toString());
            txt_gram_diambil.setText(Table_stock_bahan_baku.getValueAt(i, 7).toString());
        }
    }//GEN-LAST:event_Table_stock_bahan_bakuMousePressed

    private void button_okActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_okActionPerformed
        // TODO add your handling code here:
        int i = Table_stock_bahan_baku.getSelectedRow();
        if (i == -1) {
            JOptionPane.showMessageDialog(this, "anda belum memilih data");
        } else {
            if (ComboBox_jenisAdjustment.getSelectedItem().toString().equals("Dikurangkan")) {
                kurang();
            } else if (ComboBox_jenisAdjustment.getSelectedItem().toString().equals("Ditambahkan")) {
                tambah();
            }
        }
    }//GEN-LAST:event_button_okActionPerformed

    private void button_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_cancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_button_cancelActionPerformed

    private void txt_no_kartuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_no_kartuActionPerformed
        refreshTable();
    }//GEN-LAST:event_txt_no_kartuActionPerformed

    private void button_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refreshActionPerformed
        refreshTable();
    }//GEN-LAST:event_button_refreshActionPerformed

    /**
     * @param args the command line arguments
     */
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
            java.util.logging.Logger.getLogger(JDialog_AdjustmentBaku.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JDialog_AdjustmentBaku dialog = new JDialog_AdjustmentBaku(new javax.swing.JFrame(), true);
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
    private javax.swing.JComboBox<String> ComboBox_Search_grade;
    private javax.swing.JComboBox<String> ComboBox_jenisAdjustment;
    private com.toedter.calendar.JDateChooser Date_stokOpname;
    public static javax.swing.JTable Table_stock_bahan_baku;
    private javax.swing.JButton button_cancel;
    private javax.swing.JButton button_ok;
    private javax.swing.JButton button_refresh;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_grade;
    private javax.swing.JLabel label_kartu;
    private javax.swing.JTextField txt_gram_diambil;
    private javax.swing.JTextField txt_kpg_diambil;
    private javax.swing.JTextField txt_no_kartu;
    // End of variables declaration//GEN-END:variables
}
