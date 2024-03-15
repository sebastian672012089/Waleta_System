package waleta_system.BahanJadi;

import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
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
import waleta_system.Class.Utility;

public class JDialog_AddBox extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    String kode_tutupan, grade, kode_grade;
    int keping = 0, tot_keping = 0;
    float tot_gram = 0;
    float gram = 0;

    public JDialog_AddBox(java.awt.Frame parent, boolean modal, String kode_tutupan, String grade, int keping, float gram) {
        super(parent, modal);
        initComponents();
        this.kode_tutupan = kode_tutupan;
        this.grade = grade;
        this.keping = keping;
        this.gram = gram;
        try {
            button_tambah.setIcon(Utility.ResizeImageIcon(new javax.swing.ImageIcon(getClass().getResource("/waleta_system/Images/add_green.png")), button_tambah.getWidth(), button_tambah.getHeight()));
            button_kurang.setIcon(Utility.ResizeImageIcon(new javax.swing.ImageIcon(getClass().getResource("/waleta_system/Images/delete-icon.png")), button_kurang.getWidth(), button_kurang.getHeight()));
            label_no_tutupan.setText(kode_tutupan);
            label_grade.setText(grade);
            label_keping.setText(Integer.toString(keping));
            label_gram.setText(Float.toString(gram));

            sql = "SELECT `kode_rumah_burung` FROM `tb_tutupan_grading` WHERE `kode_tutupan` = '" + kode_tutupan + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                label_kode_rsb.setText(rs.getString("kode_rumah_burung"));
            }

            sql = "SELECT `kode` FROM `tb_grade_bahan_jadi` WHERE `kode_grade` = '" + grade + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                kode_grade = rs.getString("kode");
            }
            label_kode_grade.setText(kode_grade);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_AddBox.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void count() {
        tot_keping = 0;
        tot_gram = 0;
        int x = table_daftar_box.getRowCount();
        for (int i = 0; i < x; i++) {
            try {
                tot_keping = tot_keping + Integer.valueOf(table_daftar_box.getValueAt(i, 1).toString());
                tot_gram = tot_gram + Float.valueOf(table_daftar_box.getValueAt(i, 2).toString());
            } catch (NullPointerException e) {
                JOptionPane.showMessageDialog(this, "Tidak Boleh ada data yang kosong !");
            }
        }
        label_total.setText(Integer.toString(x));
        label_total_kpg.setText(Integer.toString(tot_keping));
        label_total_gram.setText(decimalFormat.format(tot_gram));
    }

    public String newBox(String grade) throws SQLException {
        String no_box_baru = null;
        String kode = null;
        int total_box = 0;
        sql = "SELECT MAX(RIGHT(`no_box`, 5)+0) AS 'total_box', `tb_grade_bahan_jadi`.`kode` "
                + "FROM `tb_box_bahan_jadi` "
                + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`"
                + "WHERE "
                + "YEAR(`tanggal_box`) = '" + new SimpleDateFormat("yyyy").format(new Date()) + "' "
                + "AND `tb_grade_bahan_jadi`.`kode_grade` = '" + grade + "'";
        rs = Utility.db.getStatement().executeQuery(sql);
        if (rs.next()) {
            total_box = rs.getInt("total_box") + 1;
            kode = rs.getString("kode");
        }

        no_box_baru = "BOX" + kode + "-" + new SimpleDateFormat("yyMM").format(new Date()) + String.format("%05d", total_box);
        return no_box_baru;
    }

    public void insert() {
        try {
            Utility.db.getConnection().setAutoCommit(false);
            for (int i = 0; i < table_daftar_box.getRowCount(); i++) {
                String no_box_baru = newBox(grade);
                if (no_box_baru != null && !no_box_baru.equals("") && !no_box_baru.equals("null")) {
                    sql = "INSERT INTO `tb_box_bahan_jadi`(`no_box`, `tanggal_box`, `kode_grade_bahan_jadi`, `keping`, `berat`, `no_tutupan`, `status_terakhir`, `lokasi_terakhir`, `tgl_proses_terakhir`, `kode_rsb`) "
                            + "VALUES ("
                            + "'" + no_box_baru + "',"
                            + "'" + dateFormat.format(date) + "',"
                            + "'" + kode_grade + "',"
                            + "'" + table_daftar_box.getValueAt(i, 1) + "',"
                            + "'" + table_daftar_box.getValueAt(i, 2) + "',"
                            + "'" + kode_tutupan + "', "
                            + "'NEW BOX', "
                            + "'GRADING', "
                            + "'" + dateFormat.format(date) + "', "
                            + "'" + label_kode_rsb.getText() + "' "
                            + ")";
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(sql);
                } else {
                    throw new Exception("No box baru gagal dibuat, silahkan coba lagi");
                }
            }
            Utility.db.getConnection().commit();
            JOptionPane.showMessageDialog(this, "Data BOX berhasil Ditambahkan");
            this.dispose();
        } catch (Exception e) {
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
        label_no_tutupan = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        label_grade = new javax.swing.JLabel();
        label_status = new javax.swing.JLabel();
        label_kode_grade = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        label_total_gram = new javax.swing.JLabel();
        button_count = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        button_save = new javax.swing.JButton();
        button_kurang = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        label_total_kpg = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        label_gram = new javax.swing.JLabel();
        txt_kpg = new javax.swing.JTextField();
        button_tambah = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        label_keping = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_daftar_box = new javax.swing.JTable();
        label_total = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txt_gram = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        label_kode_rsb = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Add Box");
        setBackground(new java.awt.Color(255, 255, 255));
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        label_no_tutupan.setBackground(new java.awt.Color(255, 255, 255));
        label_no_tutupan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_no_tutupan.setText("-");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("Kode Grade :");

        label_grade.setBackground(new java.awt.Color(255, 255, 255));
        label_grade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_grade.setText("-");

        label_status.setBackground(new java.awt.Color(255, 255, 255));
        label_status.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_status.setText("Add Box");

        label_kode_grade.setBackground(new java.awt.Color(255, 255, 255));
        label_kode_grade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_kode_grade.setText("-");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Gram Asal");

        label_total_gram.setText("0");

        button_count.setBackground(new java.awt.Color(255, 255, 255));
        button_count.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_count.setText("Count");
        button_count.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_countActionPerformed(evt);
            }
        });

        jLabel8.setText("Gram :");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Grade Barang Jadi :");

        button_save.setBackground(new java.awt.Color(255, 255, 255));
        button_save.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_save.setText("Save");
        button_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_saveActionPerformed(evt);
            }
        });

        button_kurang.setBackground(new java.awt.Color(255, 255, 255));
        button_kurang.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_kurang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_kurangActionPerformed(evt);
            }
        });

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Kpg Asal");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("Gram :");

        label_total_kpg.setText("0");

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("No. Tutupan :");

        label_gram.setBackground(new java.awt.Color(255, 255, 255));
        label_gram.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_gram.setText("0");

        txt_kpg.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_kpg.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_kpgKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_kpgKeyTyped(evt);
            }
        });

        button_tambah.setBackground(new java.awt.Color(255, 255, 255));
        button_tambah.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_tambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_tambahActionPerformed(evt);
            }
        });

        jLabel5.setText("Keping :");

        label_keping.setBackground(new java.awt.Color(255, 255, 255));
        label_keping.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_keping.setText("0");

        jLabel3.setText("Total :");

        table_daftar_box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_daftar_box.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Keping", "Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class
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
        table_daftar_box.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table_daftar_box);
        if (table_daftar_box.getColumnModel().getColumnCount() > 0) {
            table_daftar_box.getColumnModel().getColumn(0).setResizable(false);
            table_daftar_box.getColumnModel().getColumn(0).setPreferredWidth(10);
        }

        label_total.setText("0");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Keping :");

        txt_gram.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_gram.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_gramKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_gramKeyTyped(evt);
            }
        });

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Kode RSB :");

        label_kode_rsb.setBackground(new java.awt.Color(255, 255, 255));
        label_kode_rsb.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_kode_rsb.setText("-");

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
                        .addComponent(label_no_tutupan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_grade, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(label_keping)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_gram)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_kpg)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_save))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_kpg, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_gram, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_kurang, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_count))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(label_status)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_kode_grade, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_kode_rsb, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_status)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(label_no_tutupan))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(label_kode_rsb))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(label_grade))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(label_kode_grade))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(label_gram))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(label_keping)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_kpg, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_gram, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_count, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_kurang, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_kpg, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_save, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void button_tambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_tambahActionPerformed
        try {
            int a = Integer.valueOf(txt_kpg.getText());
            float b = Float.valueOf(txt_gram.getText());
            DefaultTableModel model = (DefaultTableModel) table_daftar_box.getModel();
            if ("".equals(txt_kpg.getText()) || txt_kpg.getText() == null || "0".equals(txt_gram.getText()) || txt_gram.getText() == null || "".equals(txt_gram.getText())) {
                JOptionPane.showMessageDialog(this, "Maaf Keping/gram tidak boleh kosong, dan gram tidak boleh 0");
            } else {
                model.addRow(new Object[]{table_daftar_box.getRowCount() + 1, txt_kpg.getText(), txt_gram.getText()});
                txt_kpg.setText(null);
                txt_gram.setText(null);
                txt_kpg.requestFocus();
            }
        } catch (HeadlessException | NumberFormatException e) {
            Logger.getLogger(JDialog_AddBox.class.getName()).log(Level.SEVERE, null, e);
            JOptionPane.showMessageDialog(this, "Invalid number !");
        }
    }//GEN-LAST:event_button_tambahActionPerformed

    private void button_kurangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_kurangActionPerformed
        int i = table_daftar_box.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) table_daftar_box.getModel();
        if (i != -1) {
            model.removeRow(i);
            for (int j = 0; j < table_daftar_box.getRowCount(); j++) {
                table_daftar_box.setValueAt(j + 1, j, 0);
            }
        }
        count();
    }//GEN-LAST:event_button_kurangActionPerformed

    private void button_countActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_countActionPerformed
        // TODO add your handling code here:
        count();
    }//GEN-LAST:event_button_countActionPerformed

    private void button_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_saveActionPerformed
        // TODO add your handling code here:
        count();
        decimalFormat.setMaximumFractionDigits(2);
//        float gram_akhir = Float.valueOf(label_total_gram.getText());
        float susut = ((gram - tot_gram) / gram) * 100.f;
        if (tot_keping != keping) {
            JOptionPane.showMessageDialog(this, "Maaf Data Keping dan Gram Belum Cocok !\nSilahkan Semuanya dibagi kedalam box");
        } else if (susut > 2 || susut < -2) {
            int dialogResult = JOptionPane.showConfirmDialog(this, "SH : " + decimalFormat.format(susut) + "% melebihi +-2%, apakah ingin melanjutkan??", "Warning", 0);
            if (dialogResult == JOptionPane.YES_OPTION) {
                JDialog_otorisasi_gradingBJ dialog = new JDialog_otorisasi_gradingBJ(new javax.swing.JFrame(), true);
                dialog.pack();
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
                dialog.setEnabled(true);
                if (dialog.akses()) {
                    insert();
                }
            }
        } else {
            insert();
        }
    }//GEN-LAST:event_button_saveActionPerformed

    private void txt_gramKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_gramKeyPressed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_daftar_box.getModel();
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            try {
                int a = Integer.valueOf(txt_kpg.getText());
                float b = Float.valueOf(txt_gram.getText());
                if ("".equals(txt_kpg.getText()) || txt_kpg.getText() == null || "0".equals(txt_gram.getText()) || txt_gram.getText() == null || "".equals(txt_gram.getText())) {
                    JOptionPane.showMessageDialog(this, "Maaf Keping/gram tidak boleh kosong, dan gram tidak boleh 0");
                } else {
                    model.addRow(new Object[]{table_daftar_box.getRowCount() + 1, txt_kpg.getText(), txt_gram.getText()});
                    txt_kpg.setText(null);
                    txt_gram.setText(null);
                    txt_kpg.requestFocus();
                }
            } catch (HeadlessException | NumberFormatException e) {
                Logger.getLogger(JDialog_AddBox.class.getName()).log(Level.SEVERE, null, e);
                JOptionPane.showMessageDialog(this, "Invalid number !");
            }
        }
    }//GEN-LAST:event_txt_gramKeyPressed

    private void txt_kpgKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_kpgKeyPressed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_daftar_box.getModel();
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            try {
                int a = Integer.valueOf(txt_kpg.getText());
                float b = Float.valueOf(txt_gram.getText());
                if ("".equals(txt_kpg.getText()) || txt_kpg.getText() == null || "0".equals(txt_gram.getText()) || txt_gram.getText() == null || "".equals(txt_gram.getText())) {
                    JOptionPane.showMessageDialog(this, "Maaf Keping/gram tidak boleh kosong, dan gram tidak boleh 0");
                } else {
                    model.addRow(new Object[]{table_daftar_box.getRowCount() + 1, txt_kpg.getText(), txt_gram.getText()});
                    txt_kpg.setText(null);
                    txt_gram.setText(null);
                    txt_kpg.requestFocus();
                }
            } catch (HeadlessException | NumberFormatException e) {
                Logger.getLogger(JDialog_AddBox.class.getName()).log(Level.SEVERE, null, e);
                JOptionPane.showMessageDialog(this, "Invalid number !");
            }
        }
    }//GEN-LAST:event_txt_kpgKeyPressed

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

    private void txt_kpgKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_kpgKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_kpgKeyTyped


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton button_count;
    private javax.swing.JButton button_kurang;
    private javax.swing.JButton button_save;
    private javax.swing.JButton button_tambah;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_grade;
    private javax.swing.JLabel label_gram;
    private javax.swing.JLabel label_keping;
    private javax.swing.JLabel label_kode_grade;
    private javax.swing.JLabel label_kode_rsb;
    private javax.swing.JLabel label_no_tutupan;
    private javax.swing.JLabel label_status;
    private javax.swing.JLabel label_total;
    private javax.swing.JLabel label_total_gram;
    private javax.swing.JLabel label_total_kpg;
    private javax.swing.JTable table_daftar_box;
    private javax.swing.JTextField txt_gram;
    private javax.swing.JTextField txt_kpg;
    // End of variables declaration//GEN-END:variables
}
