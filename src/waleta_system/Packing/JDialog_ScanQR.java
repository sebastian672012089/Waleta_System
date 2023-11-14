package waleta_system.Packing;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;

public class JDialog_ScanQR extends javax.swing.JFrame {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    ArrayList<String> no_grade_spk = new ArrayList<>();
    int current_no_box = 0;
    int total_kpg;
    float total_gram;

    public JDialog_ScanQR(String no_grade_spk, int no_urut_barcode, String kode_packing) {
        try {
            initComponents();

            this.current_no_box = no_urut_barcode;
            label_no_urut_barcode.setText(Integer.toString(current_no_box));
            txt_kode_packing.setText(kode_packing);
            sql = "SELECT `tb_spk_detail`.`kode_spk`, `buyer`, `tb_buyer`.`nama`, `no`, `grade_buyer`, `berat_kemasan`, `jumlah_kemasan`, `berat` "
                    + "FROM `tb_spk_detail` "
                    + "LEFT JOIN `tb_spk` ON `tb_spk_detail`.`kode_spk` = `tb_spk`.`kode_spk` "
                    + "LEFT JOIN `tb_buyer` ON `tb_spk`.`buyer` = `tb_buyer`.`kode_buyer` "
                    + "WHERE `no` = '" + no_grade_spk + "' ";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                label_kode_spk.setText(rs.getString("kode_spk"));
                label_buyer.setText(rs.getString("nama") + " " + " (" + rs.getString("buyer") + ")");
                label_no_grade_spk.setText(rs.getString("no"));
                label_grade_buyer.setText(rs.getString("grade_buyer"));
                label_berat_kemasan.setText(decimalFormat.format(rs.getInt("berat_kemasan")));
                label_jumlah_kemasan.setText(decimalFormat.format(rs.getInt("jumlah_kemasan")));
                label_total_berat.setText(decimalFormat.format(rs.getInt("berat")));
            }
            refreshTabel();
        } catch (Exception ex) {
            Logger.getLogger(JDialog_ScanQR.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTabel() {
        try {
            DefaultTableModel model = (DefaultTableModel) tabel_data_scan.getModel();
            model.setRowCount(0);
            total_gram = 0;
            sql = "SELECT `no_urut_pcs`, `qrcode`, `gram`, LENGTH(`qrcode`) AS 'length', `kode_packing` FROM `tb_scan_qr_packing` "
                    + "WHERE `no_grade_spk` = '" + label_no_grade_spk.getText() + "' AND  `no_urut_barcode` = '" + current_no_box + "'"
                    + "ORDER BY `no_urut_pcs` ";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("no_urut_pcs"),
                    rs.getDouble("gram"),
                    rs.getString("qrcode"),
                    rs.getInt("length"),
                    rs.getString("kode_packing")});
                total_gram = total_gram + rs.getFloat("gram");

                int lastRow = tabel_data_scan.getRowCount() - 1;
                tabel_data_scan.setRowSelectionInterval(lastRow, lastRow);
            }

            if (tabel_data_scan.getRowCount() > 0) {
                int lastRow = tabel_data_scan.getRowCount() - 1;
                tabel_data_scan.setRowSelectionInterval(lastRow, lastRow);

                // Scroll the last row into view
                Rectangle lastRowRect = tabel_data_scan.getCellRect(lastRow, 0, true);
                tabel_data_scan.scrollRectToVisible(lastRowRect);
            }

            ColumnsAutoSizer.sizeColumnsToFit(tabel_data_scan);
            total_kpg = tabel_data_scan.getRowCount();
            label_total.setText("Total Data Scan = " + decimalFormat.format(tabel_data_scan.getRowCount()) + " Total Gram = " + decimalFormat.format(total_gram));
        } catch (SQLException ex) {
            Logger.getLogger(JDialog_ScanQR.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void input() {
        try {
            boolean check = true;
            float gram = 0;
            try {
                gram = Float.valueOf(txt_gram.getText());
                if (txt_scan_qr.getText() == null || txt_scan_qr.getText().equals("")) {
                    check = false;
                    JOptionPane.showMessageDialog(this, "silahkan scan QR !");
                }
            } catch (NumberFormatException e) {
                check = false;
                JOptionPane.showMessageDialog(this, "Maaf gram yang di masukkan salah !");
            }
            if (check) {
                int no_urut_qr = 0;
                sql = "SELECT MAX(`no_urut_pcs`) AS 'max' FROM `tb_scan_qr_packing` WHERE `no_grade_spk` = '" + label_no_grade_spk.getText() + "' AND `no_urut_barcode` = '" + label_no_urut_barcode.getText() + "'";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    no_urut_qr = rs.getInt("max") + 1;
                }
                String update = "INSERT INTO `tb_scan_qr_packing`(`no_urut_pcs`, `qrcode`, `gram`, `no_grade_spk`, `no_urut_barcode`, `scan_time`) "
                        + "VALUES ("
                        + "'" + no_urut_qr + "', "
                        + "'" + txt_scan_qr.getText().trim() + "',"
                        + "'" + gram + "',"
                        + "'" + label_no_grade_spk.getText() + "',"
                        + "'" + label_no_urut_barcode.getText() + "',"
                        + "NOW())";
                Utility.db.getConnection().createStatement();
                if (Utility.db.getStatement().executeUpdate(update) == 1) {
                    String update_kode_packing = "UPDATE `tb_scan_qr_packing` SET `kode_packing`='" + txt_kode_packing.getText() + "' "
                            + "WHERE `no_grade_spk`='" + label_no_grade_spk.getText() + "' AND `no_urut_barcode`='" + label_no_urut_barcode.getText() + "'";
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(update_kode_packing);

                    if (!CheckBox_bp.isSelected()) {
                        txt_gram.setText(null);
                    }
                    txt_scan_qr.setText(null);
                    txt_scan_qr.requestFocus();
                    refreshTabel();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JDialog_ScanQR.class.getName()).log(Level.SEVERE, null, e);
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
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_data_scan = new javax.swing.JTable();
        jLabel25 = new javax.swing.JLabel();
        txt_scan_qr = new javax.swing.JTextField();
        txt_gram = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        label_total = new javax.swing.JLabel();
        button_delete = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        label_grade_buyer = new javax.swing.JLabel();
        label_no_grade_spk = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        label_jumlah_kemasan = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        label_total_berat = new javax.swing.JLabel();
        label_berat_kemasan = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        label_buyer = new javax.swing.JTextArea();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        label_kode_spk = new javax.swing.JLabel();
        CheckBox_bp = new javax.swing.JCheckBox();
        label_no_urut_barcode = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txt_kode_packing = new javax.swing.JTextField();
        button_next_box = new javax.swing.JButton();
        txt_max_no_box = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        txt_search_qr = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        button_search_qr = new javax.swing.JButton();
        button_delete_semua = new javax.swing.JButton();
        button_set_kode_packing = new javax.swing.JButton();
        button_print_label = new javax.swing.JButton();
        CheckBox_print = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("PENGIRIMAN");
        setBackground(new java.awt.Color(255, 255, 255));
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel2.setText("Scan QR");
        jLabel2.setFocusable(false);

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("No Urut Barcode / No Box :");
        jLabel5.setFocusable(false);

        tabel_data_scan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_data_scan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Gram", "QR", "Lenght", "Kode Packing"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Float.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class
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
        jScrollPane1.setViewportView(tabel_data_scan);

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel25.setText("Scan QR :");
        jLabel25.setFocusable(false);

        txt_scan_qr.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_scan_qr.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_scan_qrKeyPressed(evt);
            }
        });

        txt_gram.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_gram.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_gramKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_gramKeyTyped(evt);
            }
        });

        jLabel32.setBackground(new java.awt.Color(255, 255, 255));
        jLabel32.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel32.setText("Gram :");
        jLabel32.setFocusable(false);

        label_total.setBackground(new java.awt.Color(255, 255, 255));
        label_total.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total.setText("Total Data, Total Gram");
        label_total.setFocusable(false);

        button_delete.setBackground(new java.awt.Color(255, 255, 255));
        button_delete.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete.setText("DELETE 1");
        button_delete.setFocusable(false);
        button_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_deleteActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.setFocusable(false);

        label_grade_buyer.setBackground(new java.awt.Color(255, 255, 255));
        label_grade_buyer.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_grade_buyer.setText("-");
        label_grade_buyer.setFocusable(false);

        label_no_grade_spk.setBackground(new java.awt.Color(255, 255, 255));
        label_no_grade_spk.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_no_grade_spk.setText("-");
        label_no_grade_spk.setFocusable(false);

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel19.setText("No Grade :");
        jLabel19.setFocusable(false);

        label_jumlah_kemasan.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_kemasan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_jumlah_kemasan.setText("-");
        label_jumlah_kemasan.setFocusable(false);

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel18.setText("Buyer :");
        jLabel18.setFocusable(false);

        label_total_berat.setBackground(new java.awt.Color(255, 255, 255));
        label_total_berat.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_berat.setText("-");
        label_total_berat.setFocusable(false);

        label_berat_kemasan.setBackground(new java.awt.Color(255, 255, 255));
        label_berat_kemasan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_berat_kemasan.setText("-");
        label_berat_kemasan.setFocusable(false);

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel17.setText("Kode SPK :");
        jLabel17.setFocusable(false);

        jScrollPane2.setFocusable(false);

        label_buyer.setEditable(false);
        label_buyer.setColumns(20);
        label_buyer.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_buyer.setLineWrap(true);
        label_buyer.setRows(2);
        label_buyer.setFocusable(false);
        jScrollPane2.setViewportView(label_buyer);

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel20.setText("Grade Buyer :");
        jLabel20.setFocusable(false);

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel21.setText("Berat Kemasan :");
        jLabel21.setFocusable(false);

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel24.setText("Total Berat :");
        jLabel24.setFocusable(false);

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel23.setText("Jumlah Kemasan :");
        jLabel23.setFocusable(false);

        label_kode_spk.setBackground(new java.awt.Color(255, 255, 255));
        label_kode_spk.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_kode_spk.setText("-");
        label_kode_spk.setFocusable(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_no_grade_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kode_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_grade_buyer, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_berat_kemasan, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_jumlah_kemasan, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_berat, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kode_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_no_grade_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_grade_buyer, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_berat_kemasan, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_jumlah_kemasan, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_berat, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        CheckBox_bp.setBackground(new java.awt.Color(255, 255, 255));
        CheckBox_bp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        CheckBox_bp.setText("BP");

        label_no_urut_barcode.setBackground(new java.awt.Color(255, 255, 255));
        label_no_urut_barcode.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_no_urut_barcode.setText("00");
        label_no_urut_barcode.setFocusable(false);

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Kode Packing :");
        jLabel6.setFocusable(false);

        txt_kode_packing.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_next_box.setBackground(new java.awt.Color(255, 255, 255));
        button_next_box.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        button_next_box.setText("NEXT BOX");
        button_next_box.setFocusable(false);
        button_next_box.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_next_boxActionPerformed(evt);
            }
        });

        txt_max_no_box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel33.setBackground(new java.awt.Color(255, 255, 255));
        jLabel33.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel33.setText("Sampai");
        jLabel33.setFocusable(false);

        txt_search_qr.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_qr.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_qrKeyPressed(evt);
            }
        });

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel26.setText("Search QR :");
        jLabel26.setFocusable(false);

        button_search_qr.setBackground(new java.awt.Color(255, 255, 255));
        button_search_qr.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_qr.setText("SEARCH");
        button_search_qr.setFocusable(false);
        button_search_qr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_qrActionPerformed(evt);
            }
        });

        button_delete_semua.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_semua.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete_semua.setText("DELETE SEMUA");
        button_delete_semua.setFocusable(false);
        button_delete_semua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_semuaActionPerformed(evt);
            }
        });

        button_set_kode_packing.setBackground(new java.awt.Color(255, 255, 255));
        button_set_kode_packing.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_set_kode_packing.setText("SET KODE PACKING");
        button_set_kode_packing.setFocusable(false);
        button_set_kode_packing.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_set_kode_packingActionPerformed(evt);
            }
        });

        button_print_label.setBackground(new java.awt.Color(255, 255, 255));
        button_print_label.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_print_label.setText("Print Label");
        button_print_label.setFocusable(false);
        button_print_label.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_print_labelActionPerformed(evt);
            }
        });

        CheckBox_print.setBackground(new java.awt.Color(255, 255, 255));
        CheckBox_print.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        CheckBox_print.setSelected(true);
        CheckBox_print.setText("Langsung Print");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 588, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(label_total)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_delete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_delete_semua))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel25)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_scan_qr, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel32)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_gram, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(CheckBox_bp))
                            .addComponent(jLabel2)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_kode_packing, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_set_kode_packing))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_no_urut_barcode)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel33)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_max_no_box, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_next_box)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_print_label)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(CheckBox_print)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_qr, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search_qr)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_print_label, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CheckBox_print, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_no_urut_barcode, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_next_box, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_max_no_box, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_kode_packing, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_set_kode_packing, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_scan_qr, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_gram, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CheckBox_bp))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_search_qr, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_search_qr, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_total, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete_semua, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    private void button_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_deleteActionPerformed
        // TODO add your handling code here:
        try {
            int i = tabel_data_scan.getSelectedRow();
            if (i > -1) {
                String delete = "DELETE FROM `tb_scan_qr_packing` WHERE `qrcode` = '" + tabel_data_scan.getValueAt(i, 2).toString() + "'";
                Utility.db.getConnection().createStatement();
                if (Utility.db.getStatement().executeUpdate(delete) == 1) {
                    String update = "UPDATE `tb_scan_qr_packing` SET `no_urut_pcs`=`no_urut_pcs`-1 "
                            + "WHERE `no_grade_spk`='" + label_no_grade_spk.getText() + "' "
                            + "AND `no_urut_barcode`='" + label_no_urut_barcode.getText() + "' "
                            + "AND `no_urut_pcs` > " + tabel_data_scan.getValueAt(i, 0).toString() + " ";
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(update);
                    txt_scan_qr.setText(null);
                    txt_gram.setText(null);
                    txt_scan_qr.requestFocus();
                    refreshTabel();
                    JOptionPane.showMessageDialog(this, "Data berhasil dihapus !");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Silahkan Klik data yang akan di hapus pada tabel !");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JDialog_ScanQR.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_deleteActionPerformed

    private void txt_scan_qrKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_scan_qrKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (CheckBox_bp.isSelected()) {
                input();
            } else {
                txt_gram.requestFocus();
            }
        }
    }//GEN-LAST:event_txt_scan_qrKeyPressed

    private void txt_gramKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_gramKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            input();
        }
    }//GEN-LAST:event_txt_gramKeyPressed

    private void txt_gramKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_gramKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_gramKeyTyped

    private void button_next_boxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_next_boxActionPerformed
        // TODO add your handling code here:
        try {
            int max_no_box = Integer.valueOf(txt_max_no_box.getText());
//        int no_urut_qr = 0;
//        sql = "SELECT MAX(`no_urut_pcs`) AS 'max' FROM `tb_scan_qr_packing` WHERE `no_grade_spk` = '" + label_no_grade_spk.getText() + "' AND `no_urut_barcode` = '" + label_no_urut_barcode.getText() + "'";
//        rs = Utility.db.getStatement().executeQuery(sql);
//        if (rs.next()) {
//            no_urut_qr = rs.getInt("max") + 1;
//        }
            if (current_no_box >= max_no_box) {
                JOptionPane.showMessageDialog(this, "Maaf, anda sudah melebihi batas max yang telah ditetapkan !");
            } else {
                current_no_box = current_no_box + 1;
                label_no_urut_barcode.setText(Integer.toString(current_no_box));
                refreshTabel();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Maaf nomor maksimal box yang dimasukkan salah!");
            Logger.getLogger(JDialog_ScanQR.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_next_boxActionPerformed

    private void button_search_qrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_qrActionPerformed
        // TODO add your handling code here:
        int row_index_found = -1;
        for (int i = 0; i < tabel_data_scan.getRowCount(); i++) {
            String qr = tabel_data_scan.getValueAt(i, 2).toString();
            if (qr.equals(txt_search_qr.getText())) {
                row_index_found = i;
            }
        }
        if (row_index_found > -1) {
            tabel_data_scan.setRowSelectionInterval(row_index_found, row_index_found);
        } else {
            tabel_data_scan.clearSelection();
        }
    }//GEN-LAST:event_button_search_qrActionPerformed

    private void txt_search_qrKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_qrKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            int row_index_found = -1;
            for (int i = 0; i < tabel_data_scan.getRowCount(); i++) {
                String qr = tabel_data_scan.getValueAt(i, 2).toString();
                if (qr.equals(txt_search_qr.getText())) {
                    row_index_found = i;
                }
            }
            if (row_index_found > -1) {
                tabel_data_scan.setRowSelectionInterval(row_index_found, row_index_found);
            } else {
                tabel_data_scan.clearSelection();
            }
        }
    }//GEN-LAST:event_txt_search_qrKeyPressed

    private void button_delete_semuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_semuaActionPerformed
        // TODO add your handling code here:
        try {
            Utility.db.getConnection().setAutoCommit(false);
            String delete = "DELETE FROM `tb_scan_qr_packing` WHERE `no_grade_spk`='" + label_no_grade_spk.getText() + "' "
                    + "AND `no_urut_barcode`='" + label_no_urut_barcode.getText() + "'";
            Utility.db.getConnection().createStatement();
            Utility.db.getStatement().executeUpdate(delete);
            Utility.db.getConnection().commit();
            refreshTabel();
            JOptionPane.showMessageDialog(this, "Data berhasil dihapus !");
        } catch (SQLException e) {
            try {
                Utility.db.getConnection().rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(JDialog_terima_box.class.getName()).log(Level.SEVERE, null, ex1);
            }
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JDialog_ScanQR.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                Utility.db.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_terima_box.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_delete_semuaActionPerformed

    private void button_set_kode_packingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_set_kode_packingActionPerformed
        // TODO add your handling code here:
        try {
            Utility.db.getConnection().setAutoCommit(false);
            String update_kode_packing = "UPDATE `tb_scan_qr_packing` SET `kode_packing`='" + txt_kode_packing.getText() + "' "
                    + "WHERE `no_grade_spk`='" + label_no_grade_spk.getText() + "' AND `no_urut_barcode`='" + label_no_urut_barcode.getText() + "'";
            Utility.db.getConnection().createStatement();
            Utility.db.getStatement().executeUpdate(update_kode_packing);
            Utility.db.getConnection().commit();
            refreshTabel();
            JOptionPane.showMessageDialog(this, "data saved !");
        } catch (SQLException e) {
            try {
                Utility.db.getConnection().rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(JDialog_terima_box.class.getName()).log(Level.SEVERE, null, ex1);
            }
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JDialog_ScanQR.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                Utility.db.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_terima_box.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_set_kode_packingActionPerformed

    private void button_print_labelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_print_labelActionPerformed
        // TODO add your handling code here:
        try {
            String nama_buyer = "", grade = "";
            sql = "SELECT `tb_spk_detail`.`kode_spk`, `buyer`, `tb_buyer`.`nama`, `no`, `grade_waleta`, `grade_buyer`, `berat_kemasan`, `jumlah_kemasan`, `berat` "
                    + "FROM `tb_spk_detail` "
                    + "LEFT JOIN `tb_spk` ON `tb_spk_detail`.`kode_spk` = `tb_spk`.`kode_spk` "
                    + "LEFT JOIN `tb_buyer` ON `tb_spk`.`buyer` = `tb_buyer`.`kode_buyer` "
                    + "WHERE `no` = '" + label_no_grade_spk.getText() + "' ";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                String lbl_buyer[] = rs.getString("nama").split(" ");
                nama_buyer = lbl_buyer[0];
                if (lbl_buyer.length > 1) {
                    nama_buyer = lbl_buyer[0] + " " + lbl_buyer[1];
                }
                grade = rs.getString("grade_waleta");
            }
            String no_urut_barcode = label_no_urut_barcode.getText();
            String kode_packing = txt_kode_packing.getText();
            if (kode_packing != null && !kode_packing.equals("")) {
                sql = "SELECT `kode_rsb` FROM `tb_box_bahan_jadi` WHERE `no_box` = '" + kode_packing + "' ";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    kode_packing = kode_packing + "-" + rs.getString("kode_rsb");
                }
            }

            //            JRDesignQuery newQuery = new JRDesignQuery();
            //            newQuery.setText(sql);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Label_PecahBox_Packing.jrxml");
            //            JASP_DESIGN.setQuery(newQuery);
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("SUBREPORT_DIR", "Report\\");
            params.put("NO_GRADE_SPK", label_no_grade_spk.getText());
            params.put("NO_URUT_BARCODE", no_urut_barcode);
            params.put("BUYER", nama_buyer);
            params.put("KODE_PACKING", kode_packing);
            params.put("GRADE", grade);
            params.put("KEPING", total_kpg);
            params.put("GRAM", total_gram);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, params, Utility.db.getConnection());
            if (CheckBox_print.isSelected()) {
                JasperPrintManager.printReport(JASP_PRINT, false);//langsung print
            } else {
                JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataPacking.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_print_labelActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox CheckBox_bp;
    private javax.swing.JCheckBox CheckBox_print;
    private javax.swing.JButton button_delete;
    private javax.swing.JButton button_delete_semua;
    public javax.swing.JButton button_next_box;
    public javax.swing.JButton button_print_label;
    private javax.swing.JButton button_search_qr;
    private javax.swing.JButton button_set_kode_packing;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel32;
    public javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel label_berat_kemasan;
    private javax.swing.JTextArea label_buyer;
    private javax.swing.JLabel label_grade_buyer;
    private javax.swing.JLabel label_jumlah_kemasan;
    private javax.swing.JLabel label_kode_spk;
    private javax.swing.JLabel label_no_grade_spk;
    private javax.swing.JLabel label_no_urut_barcode;
    private javax.swing.JLabel label_total;
    private javax.swing.JLabel label_total_berat;
    private javax.swing.JTable tabel_data_scan;
    private javax.swing.JTextField txt_gram;
    private javax.swing.JTextField txt_kode_packing;
    public javax.swing.JTextField txt_max_no_box;
    private javax.swing.JTextField txt_scan_qr;
    private javax.swing.JTextField txt_search_qr;
    // End of variables declaration//GEN-END:variables
}
