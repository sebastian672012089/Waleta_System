package waleta_system.Manajemen;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;
import waleta_system.Finance.JPanel_Payment_Report;

public class JFrame_ARAP_Report extends javax.swing.JFrame {

    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    Date today = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public JFrame_ARAP_Report() {
        initComponents();
    }

    public void init() {
        try {
            jTable_ARAP.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
            Tabel_ARAP_Esta.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
            tabel_hutangExim.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
            refreshTable_ekspor();
            refresh_JAM();
            refreshTable_ARAP_esta();
            refreshTable_hutangExim();
        } catch (Exception ex) {
            Logger.getLogger(JFrame_ARAP_Report.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refresh_JAM() {
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss");

        String formattedDate = myDateObj.format(myFormatObj);
        label_waktu.setText(formattedDate);
    }

    public void refreshTable_ekspor() {
        try {
            double total_CNY = 0, total_USD = 0, total_ARDP = 0;
            DefaultTableModel model = (DefaultTableModel) jTable_ARAP.getModel();
            model.setRowCount(0);
            Object[] row = new Object[10];

            sql = "SELECT `invoice`, `awb_date`, `tb_buyer`.`nama`, `currency`, `value_waleta`, `value_esta`, `value_from_jtp`, `value_to_jtp`, `payment1`, `payment2`, `transfer_jastip1`, `transfer_jastip2`, DATEDIFF(CURRENT_DATE(), `awb_date`) AS 'Hari' "
                    + "FROM `tb_payment_report` "
                    + "LEFT JOIN `tb_buyer` ON `tb_payment_report`.`buyer` = `tb_buyer`.`kode_buyer`"
                    + "WHERE `tb_buyer`.`kategori` = 'Ekspor'";
            pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                row[0] = rs.getString("invoice");
                row[1] = new SimpleDateFormat("dd MMM yyyy").format(rs.getDate("awb_date"));
                row[2] = rs.getString("nama");
                row[3] = rs.getString("currency");
                double AR_DP = (rs.getDouble("value_waleta") + rs.getDouble("value_from_jtp") + rs.getDouble("value_to_jtp")) / 2.f;
                double AR_waleta = (rs.getDouble("value_waleta") + rs.getDouble("value_from_jtp") + rs.getDouble("value_to_jtp")) - (rs.getDouble("payment1") + rs.getDouble("payment2"));
                if ((rs.getDouble("payment1") + rs.getDouble("payment2")) == 0 && rs.getString("currency").equals("CNY")) {
                    row[4] = AR_DP;
                    total_ARDP = total_ARDP + AR_DP;
                    row[5] = AR_DP;
                } else {
                    row[4] = 0;
                    row[5] = AR_waleta;
                }
                double AP_JTP = rs.getDouble("value_to_jtp") - (rs.getDouble("transfer_jastip1") + rs.getDouble("transfer_jastip2"));
                row[6] = AP_JTP;
                row[7] = AR_waleta - AP_JTP;
                row[8] = new SimpleDateFormat("dd MMM yyyy").format(Utility.addDays(rs.getDate("awb_date"), 35));
                row[9] = rs.getInt("hari");

                if ((AR_waleta - AP_JTP) != 0 && AR_waleta > -1) {
                    model.addRow(row);
                    if (rs.getString("currency").equals("CNY")) {
                        total_CNY = total_CNY + (AR_waleta - AP_JTP);
                    } else if (rs.getString("currency").equals("USD")) {
                        total_USD = total_USD + (AR_waleta - AP_JTP);
                    }
                }
            }
            label_total_ARDP.setText("RMB ¥" + decimalFormat.format(total_ARDP));
            label_total_cny.setText("RMB ¥" + decimalFormat.format(total_CNY));
            label_total_usd.setText("USD $" + decimalFormat.format(total_USD));

            ColumnsAutoSizer.sizeColumnsToFit(jTable_ARAP);

            jTable_ARAP.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if ((int) jTable_ARAP.getValueAt(row, 9) > 42) {
                        if (isSelected) {
                            comp.setBackground(jTable_ARAP.getSelectionBackground());
                            comp.setForeground(jTable_ARAP.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.red);
                            comp.setForeground(Color.WHITE);
                        }
                    } else if ((int) jTable_ARAP.getValueAt(row, 9) > 35) {
                        if (isSelected) {
                            comp.setBackground(jTable_ARAP.getSelectionBackground());
                            comp.setForeground(jTable_ARAP.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.YELLOW);
                            comp.setForeground(jTable_ARAP.getForeground());
                        }
                    } else if ((int) jTable_ARAP.getValueAt(row, 9) <= 35) {
                        if (isSelected) {
                            comp.setBackground(jTable_ARAP.getSelectionBackground());
                            comp.setForeground(jTable_ARAP.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.green);
                            comp.setForeground(jTable_ARAP.getForeground());
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(jTable_ARAP.getSelectionBackground());
                            comp.setForeground(jTable_ARAP.getSelectionForeground());
                        } else {
                            comp.setBackground(jTable_ARAP.getBackground());
                            comp.setForeground(jTable_ARAP.getForeground());
                        }
                    }
                    return comp;
                }
            });
            jTable_ARAP.repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JFrame_ARAP_Report.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_other() {
        try {
            double total_AR = 0, total_AP = 0;
//            DefaultTableModel model = (DefaultTableModel) jTable_ARAP_lokal.getModel();
//            model.setRowCount(0);
            Object[] row = new Object[8];

            sql = "SELECT `invoice`, `tgl_invoice`, `tb_buyer`.`nama`, `currency`, `value_waleta`, `value_esta`, `value_from_jtp`, `value_to_jtp`, `payment1`, `payment2`, `transfer_jastip1`, `transfer_jastip2`, DATEDIFF(CURRENT_DATE(), `awb_date`) AS 'Hari' "
                    + "FROM `tb_payment_report` LEFT JOIN `tb_buyer` ON `tb_payment_report`.`buyer` = `tb_buyer`.`kode_buyer`"
                    + "WHERE `tb_buyer`.`kategori` = 'Lokal'";
            pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                row[0] = rs.getString("invoice");
                row[1] = new SimpleDateFormat("dd MMM yyyy").format(rs.getDate("tgl_invoice"));
                row[2] = rs.getString("nama");
                row[3] = rs.getString("currency");
                double AR_waleta = (rs.getDouble("value_waleta") + rs.getDouble("value_esta") + rs.getDouble("value_from_jtp") + rs.getDouble("value_to_jtp")) - (rs.getDouble("payment1") + rs.getDouble("payment2"));
                row[4] = AR_waleta;
                double AP_JTP = rs.getDouble("value_to_jtp") - (rs.getDouble("transfer_jastip1") + rs.getDouble("transfer_jastip2"));
                row[5] = AP_JTP;
                row[6] = AR_waleta - AP_JTP;
                row[7] = rs.getInt("hari");

                if ((AR_waleta - AP_JTP) != 0) {
//                    model.addRow(row);
                    total_AR = total_AR + AR_waleta;
                    total_AP = total_AP + AP_JTP;
                }
            }

//            ColumnsAutoSizer.sizeColumnsToFit(jTable_ARAP_lokal);
//            jTable_ARAP.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
//                @Override
//                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//                    if (row < 2) {
//                        comp.setFont(new Font("Arial", Font.BOLD, 40));
//                    }
//                    if ((int) jTable_ARAP.getValueAt(row, 1) > 100) {
//                        if (isSelected) {
//                            comp.setBackground(jTable_ARAP.getSelectionBackground());
//                            comp.setForeground(jTable_ARAP.getSelectionForeground());
//                        } else {
//                            comp.setBackground(Color.red);
//                            comp.setForeground(Color.WHITE);
//                        }
//                    } else if ((int) jTable_ARAP.getValueAt(row, 1) > 50) {
//                        if (isSelected) {
//                            comp.setBackground(jTable_ARAP.getSelectionBackground());
//                            comp.setForeground(jTable_ARAP.getSelectionForeground());
//                        } else {
//                            comp.setBackground(Color.YELLOW);
//                            comp.setForeground(jTable_ARAP.getForeground());
//                        }
//                    } else {
//                        if (isSelected) {
//                            comp.setBackground(jTable_ARAP.getSelectionBackground());
//                            comp.setForeground(jTable_ARAP.getSelectionForeground());
//                        } else {
//                            comp.setBackground(jTable_ARAP.getBackground());
//                            comp.setForeground(jTable_ARAP.getForeground());
//                        }
//                    }
//                    return comp;
//                }
//            });
//            jTable_ARAP.repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JFrame_ARAP_Report.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_ARAP_esta() {
        try {
            double total_AR = 0, total_AP = 0;
            int jumlah_tahun = Integer.valueOf(new SimpleDateFormat("yyyy").format(today)) - 2015;
            String[] deskripsi = new String[jumlah_tahun];
            int[] tahun = new int[jumlah_tahun];
            long[] debit = new long[jumlah_tahun];
            long[] kredit = new long[jumlah_tahun];
            long[] lain2 = new long[jumlah_tahun];
            long[] Saldo_awal = new long[jumlah_tahun];
            long[] Saldo_akhir = new long[jumlah_tahun];

            DefaultTableModel model = (DefaultTableModel) Tabel_ARAP_Esta.getModel();
            model.setColumnCount(0);
            model.addColumn("Deskripsi");
            for (int i = 0; i < jumlah_tahun; i++) {
                tahun[i] = Integer.valueOf(new SimpleDateFormat("yyyy").format(today)) - i;
                model.addColumn(Integer.toString(tahun[i]));
            }
            sql = "SELECT YEAR(`tanggal`) AS 'tahun', SUM(`nilai_debit`) AS 'debit', SUM(`nilai_kredit`) AS 'kredit', `jenis_hutang` "
                    + "FROM `tb_arap_esta` WHERE 1 GROUP BY YEAR(`tanggal`), `jenis_hutang` ORDER BY `tanggal` DESC";
            pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                for (int i = 0; i < jumlah_tahun; i++) {
                    if (rs.getInt("tahun") == tahun[i]) {
                        if (rs.getString("jenis_hutang").equals("JB-BJD")) {
                            debit[i] = rs.getLong("debit");
                            kredit[i] = rs.getLong("kredit");
                        } else {
                            lain2[i] = rs.getLong("debit") - rs.getLong("kredit");
                        }
                    }
                }
            }

            model.addRow(new Object[]{"Saldo Awal"});
            model.addRow(new Object[]{"Expor Produk Waleta Via 002"});
            model.addRow(new Object[]{"Expor 002 Via 019"});
            model.addRow(new Object[]{"Selisih Expor 019-002"});
            model.addRow(new Object[]{"Hutang Piutang Lain thd 002"});
            model.addRow(new Object[]{"AR AP thd 002"});
            model.addRow(new Object[]{"Saldo Akhir"});
            decimalFormat.setMaximumFractionDigits(0);
            for (int i = jumlah_tahun - 1; i > -1; i--) {
                if (i < 4) {
                    Saldo_awal[i] = Saldo_akhir[i + 1];
                } else {
                    Saldo_awal[i] = 0;
                }
                Tabel_ARAP_Esta.setValueAt(decimalFormat.format(Saldo_awal[i] / 1000000.f), 0, i + 1);
                Tabel_ARAP_Esta.setValueAt(decimalFormat.format(debit[i] / 1000000.f), 1, i + 1);
                Tabel_ARAP_Esta.setValueAt(decimalFormat.format(kredit[i] / 1000000.f), 2, i + 1);
                Tabel_ARAP_Esta.setValueAt(decimalFormat.format((kredit[i] - debit[i]) / 1000000.f), 3, i + 1);
                Tabel_ARAP_Esta.setValueAt(decimalFormat.format(lain2[i] / 1000000.f), 4, i + 1);
                Tabel_ARAP_Esta.setValueAt(decimalFormat.format((lain2[i] + (kredit[i] - debit[i])) / 1000000.f), 5, i + 1);
                Saldo_akhir[i] = Saldo_awal[i] + lain2[i] + (debit[i] - kredit[i]);
                Tabel_ARAP_Esta.setValueAt(decimalFormat.format((Saldo_akhir[i]) / 1000000.f), 6, i + 1);
            }
            if (Saldo_akhir[0] > 0) {
                label_ARAP_Esta.setText("Receiveable : Rp. " + decimalFormat.format(Saldo_akhir[0]));
            } else {
                label_ARAP_Esta.setText("Payable : Rp. " + decimalFormat.format(Saldo_akhir[0]));
            }
            ColumnsAutoSizer.sizeColumnsToFit(Tabel_ARAP_Esta);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JFrame_ARAP_Report.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void refreshTable_hutangExim() {
        try {
            double total_dollar = 0, total_rupiah = 0;
            DefaultTableModel model = (DefaultTableModel) tabel_hutangExim.getModel();
            model.setRowCount(0);
            sql = "SELECT `tb_hutang_exim`.`invoice`, `nama`, `jumlah_hutang`, (SELECT `nilai` FROM `tb_kurs_usd` WHERE 1 ORDER BY `tanggal` DESC LIMIT 1) AS 'kurs_usd' "
                    + "FROM `tb_hutang_exim` "
                    + "LEFT JOIN `tb_payment_report` ON `tb_hutang_exim`.`invoice` = `tb_payment_report`.`invoice`\n"
                    + "LEFT JOIN `tb_buyer` ON `tb_payment_report`.`buyer` = `tb_buyer`.`kode_buyer`\n"
                    + "WHERE `tanggal_pelunasan` IS NULL "
                    + "ORDER BY `tanggal_hutang` DESC";
//            System.out.println(sql);
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                Object[] row = new Object[5];
                row[0] = rs.getString("invoice");
                row[1] = rs.getString("nama");
                row[2] = rs.getDouble("jumlah_hutang");
                row[3] = rs.getDouble("jumlah_hutang") * rs.getDouble("kurs_usd");
                model.addRow(row);
                total_dollar = total_dollar + rs.getDouble("jumlah_hutang");
                total_rupiah = total_rupiah + (rs.getDouble("jumlah_hutang") * rs.getDouble("kurs_usd"));
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_hutangExim, 20);
            label_total_hutang_exim.setText("USD $" + decimalFormat.format(total_dollar) + " - IDR Rp." + decimalFormat.format(total_rupiah));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_Payment_Report.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_ARAP = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        label_waktu = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        label_total_cny = new javax.swing.JLabel();
        label_total_usd = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        label_total_ARDP = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        Tabel_ARAP_Esta = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        label_ARAP_Esta = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tabel_hutangExim = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        label_total_hutang_exim = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel1.setText("AR AP Report");

        jTable_ARAP.setAutoCreateRowSorter(true);
        jTable_ARAP.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jTable_ARAP.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Invoice No.", "AWB Date", "Buyer", "Curr", "AR DP", "AR Pelunasan", "AP JTP", "AR - AP", "AR Due Date", "Days*"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_ARAP.setRowHeight(20);
        jTable_ARAP.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable_ARAP);

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel2.setText("Export (in CNY)");

        label_waktu.setBackground(new java.awt.Color(255, 255, 255));
        label_waktu.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_waktu.setText("(TIME)");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel4.setText("Total AR-AP (USD) = ");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel5.setText("Total AR DP (CNY) = ");

        label_total_cny.setBackground(new java.awt.Color(255, 255, 255));
        label_total_cny.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        label_total_cny.setForeground(new java.awt.Color(255, 0, 0));
        label_total_cny.setText("RMB ¥");

        label_total_usd.setBackground(new java.awt.Color(255, 255, 255));
        label_total_usd.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        label_total_usd.setForeground(new java.awt.Color(255, 0, 0));
        label_total_usd.setText("USD $");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jLabel8.setText("*) Hari di hitung dari AWB Date sampai hari ini, jumlah hari diatas 6 Minggu (Merah), diatas 5 Minggu (Kuning), dibawah 5 Minggu (Hijau)");

        label_total_ARDP.setBackground(new java.awt.Color(255, 255, 255));
        label_total_ARDP.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        label_total_ARDP.setForeground(new java.awt.Color(255, 0, 0));
        label_total_ARDP.setText("RMB ¥");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel6.setText("Total AR-AP (CNY) = ");

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        Tabel_ARAP_Esta.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        Tabel_ARAP_Esta.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        Tabel_ARAP_Esta.setRowHeight(22);
        Tabel_ARAP_Esta.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(Tabel_ARAP_Esta);

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel3.setText("AR AP Waleta Esta (Satuan dalam Juta Rupiah)");

        label_ARAP_Esta.setBackground(new java.awt.Color(255, 255, 255));
        label_ARAP_Esta.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_ARAP_Esta.setForeground(new java.awt.Color(255, 0, 0));
        label_ARAP_Esta.setText("LAST POSITION");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 692, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(label_ARAP_Esta)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(label_ARAP_Esta))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        tabel_hutangExim.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        tabel_hutangExim.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Invoice", "Buyer", "$", "Rp"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel_hutangExim.setRowHeight(22);
        tabel_hutangExim.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(tabel_hutangExim);

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel7.setText("AP Hutang Exim");

        label_total_hutang_exim.setBackground(new java.awt.Color(255, 255, 255));
        label_total_hutang_exim.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_total_hutang_exim.setForeground(new java.awt.Color(255, 0, 0));
        label_total_hutang_exim.setText("TOTAL");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(label_total_hutang_exim)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(label_total_hutang_exim))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1142, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(label_total_usd, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(label_total_ARDP, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(label_total_cny, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(label_waktu))
                            .addComponent(jLabel8))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(label_waktu))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_ARDP)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_cny)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_usd))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JFrame_ARAP_Report.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFrame_ARAP_Report.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFrame_ARAP_Report.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrame_ARAP_Report.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame_ARAP_Report chart = new JFrame_ARAP_Report();
                chart.pack();
                chart.setResizable(true);
                chart.setLocationRelativeTo(null);
                chart.setVisible(true);
                chart.setEnabled(true);
                chart.setExtendedState(JFrame.MAXIMIZED_BOTH);
                chart.init();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable Tabel_ARAP_Esta;
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
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable jTable_ARAP;
    private javax.swing.JLabel label_ARAP_Esta;
    private javax.swing.JLabel label_total_ARDP;
    private javax.swing.JLabel label_total_cny;
    private javax.swing.JLabel label_total_hutang_exim;
    private javax.swing.JLabel label_total_usd;
    private javax.swing.JLabel label_waktu;
    private javax.swing.JTable tabel_hutangExim;
    // End of variables declaration//GEN-END:variables
}
