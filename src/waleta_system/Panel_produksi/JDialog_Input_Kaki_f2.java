package waleta_system.Panel_produksi;

import waleta_system.Class.Utility;

import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


public class JDialog_Input_Kaki_f2 extends javax.swing.JDialog {

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    Date date = new Date();
    PreparedStatement pst;
    String sql = null;
    ResultSet rs;
    float total_gram = 0;

    public JDialog_Input_Kaki_f2(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        try {
            
            
            initComponents();
//            label_title_terima_lp.setText("Input By Product F2");
            this.setResizable(false);
        } catch (Exception ex) {
            Logger.getLogger(JDialog_Input_Kaki_f2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void input_kaki() {
        int total_baris = Tabel_data.getRowCount();
        Boolean Check = true;
        
        float stok = Float.valueOf(label_stok.getText());
        total_gram = 0;
        for (int i = 0; i < total_baris; i++) {
            total_gram = total_gram + Float.valueOf(Tabel_data.getValueAt(i, 3).toString());
        }
        
        if (total_gram > stok) {
            JOptionPane.showMessageDialog(this, "Maaf, Gram diambil melebihi stok tersisa");
            Check = false;
        }
        
        if (txt_lp_kaki.getText() == null || txt_lp_kaki.getText().equals("-")) {
            JOptionPane.showMessageDialog(this, "No LP Suwir tidak boleh kosong");
            Check = false;
        }

        if (Check) {
            try {
                Utility.db.getConnection().setAutoCommit(false);
                for (int i = 0; i < total_baris; i++) {
                    Connection con = Utility.db.getConnection();
                    String Query = "";
                    String a = "SELECT `tambahan_kaki1`, `lp_kaki1`, `tambahan_kaki2`, `lp_kaki2` FROM `tb_finishing_2` WHERE `no_laporan_produksi` = '" + Tabel_data.getValueAt(i, 1) + "'";
                    pst = con.prepareStatement(a);
                    rs = pst.executeQuery();
                    if (rs.next()) {
                        if (rs.getInt("tambahan_kaki1") > 0) {
                            if (rs.getString("lp_kaki1").equals(txt_lp_kaki.getText())) {
                                JOptionPane.showMessageDialog(this, "No LP " + Tabel_data.getValueAt(i, 1).toString() + " sudah menggunakan LP kaki " + txt_lp_kaki.getText() + ", data akan di tambahkan ke LP kaki 1");
                                int kaki = rs.getInt("tambahan_kaki1") + Integer.valueOf(Tabel_data.getValueAt(i, 3).toString());
                                Query = "UPDATE `tb_finishing_2` SET `tambahan_kaki1`='" + kaki + "'"
                                    + "WHERE `no_laporan_produksi`='" + Tabel_data.getValueAt(i, 1) + "'";
                            } else {
                                Query = "UPDATE `tb_finishing_2` SET "
                                    + "`tambahan_kaki2`='" + Tabel_data.getValueAt(i, 3) + "',"
                                    + "`lp_kaki2`='" + txt_lp_kaki.getText() + "'"
                                    + "WHERE `no_laporan_produksi`='" + Tabel_data.getValueAt(i, 1) + "'";
                            }
                        } else {
                            Query = "UPDATE `tb_finishing_2` SET "
                                    + "`tambahan_kaki1`='" + Tabel_data.getValueAt(i, 3) + "',"
                                    + "`lp_kaki1`='" + txt_lp_kaki.getText() + "'"
                                    + "WHERE `no_laporan_produksi`='" + Tabel_data.getValueAt(i, 1) + "'";
                        }
                    }
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(Query);
                }
                Utility.db.getConnection().commit();
                JOptionPane.showMessageDialog(this, "Input Data Sukses");
                this.dispose();
                JPanel_Finishing2.button_search_f2.doClick();
            } catch (SQLException e) {
                try {
                    Utility.db.getConnection().rollback();
                    JOptionPane.showMessageDialog(this, "Input Data Gagal");
                } catch (SQLException ex) {
                    Logger.getLogger(JDialog_Input_Kaki_f2.class.getName()).log(Level.SEVERE, null, ex);
                }
                JOptionPane.showMessageDialog(this, e);
                Logger.getLogger(JDialog_Input_Kaki_f2.class.getName()).log(Level.SEVERE, null, e);
            } finally {
                try {
                    Utility.db.getConnection().setAutoCommit(true);
                } catch (SQLException ex) {
                    Logger.getLogger(JDialog_Input_Kaki_f2.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public boolean CheckDuplicateLP(String no_box) {
        int i = Tabel_data.getRowCount();
        for (int j = 0; j < i; j++) {
            if (no_box.equals(Tabel_data.getValueAt(j, 1).toString())) {
                return true;
            }
        }
        return false;
    }

    public void count() {
        int a = Tabel_data.getRowCount();
        label_jumlahLP.setText(Integer.toString(a));
        total_gram = 0;
        for (int i = 0; i < Tabel_data.getRowCount(); i++) {
            total_gram = total_gram + Float.valueOf(Tabel_data.getValueAt(i, 3).toString());
        }
        label_total_gram.setText(Float.toString(total_gram));
    }
    
    public void insert(){
        try {
            boolean CHECK_F2 = true, checkKaki = true, checkSetor = true;
            String sql1 = "SELECT `tambahan_kaki1`, `tambahan_kaki2`, `tgl_setor_f2` FROM `tb_finishing_2` WHERE `no_laporan_produksi` = '" + txt_no_lp.getText() + "'";
            ResultSet rs1 = Utility.db.getStatement().executeQuery(sql1);
            if (rs1.next()) {
                CHECK_F2 = false;
                checkKaki = (rs1.getInt("tambahan_kaki1") > 0 && rs1.getInt("tambahan_kaki2") > 0);
                checkSetor = rs1.getDate("tgl_setor_f2") != null;
            } else {
                CHECK_F2 = true;
            }

            if (CHECK_F2) {
                JOptionPane.showMessageDialog(this, "No LP (" + txt_no_lp.getText() + ") belum masuk F2 !");
            } else if (checkKaki) {
                JOptionPane.showMessageDialog(this, "No LP " + txt_no_lp.getText() + " sudah menggunakan 2 LP Kaki");
            } else if (checkSetor) {
                JOptionPane.showMessageDialog(this, "No LP " + txt_no_lp.getText() + " sudah di setorkan");
            } else if (CheckDuplicateLP(txt_no_lp.getText())) {
                JOptionPane.showMessageDialog(this, "No LP " + txt_no_lp.getText() + " sudah Masuk di Tabel");
            } else {
                DefaultTableModel model = (DefaultTableModel) Tabel_data.getModel();
                model.addRow(new Object[]{Tabel_data.getRowCount() + 1,txt_no_lp.getText(), dateFormat.format(new Date()), null, null});
//                    ColumnsAutoSizer.sizeColumnsToFit(table_reproses);
                txt_no_lp.setText("");
                txt_no_lp.requestFocus();
                count();
            }
        } catch (SQLException ex) {
            Logger.getLogger(JDialog_Input_Kaki_f2.class.getName()).log(Level.SEVERE, null, ex);
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
        jLabel3 = new javax.swing.JLabel();
        button_cancel = new javax.swing.JButton();
        label_total_gram = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        button_insert = new javax.swing.JButton();
        button_count = new javax.swing.JButton();
        label_title_terima_lp = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        txt_lp_kaki = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        Tabel_data = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        label_stok = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        button_delete = new javax.swing.JButton();
        Date_input = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        button_LPkaki1 = new javax.swing.JButton();
        button_save = new javax.swing.JButton();
        txt_no_lp = new javax.swing.JTextField();
        label_jumlahLP = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 1, 10)); // NOI18N
        jLabel3.setText("Jumlah LP :");

        button_cancel.setBackground(new java.awt.Color(255, 255, 255));
        button_cancel.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_cancel.setText("Cancel");
        button_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_cancelActionPerformed(evt);
            }
        });

        label_total_gram.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram.setFont(new java.awt.Font("Arial", 1, 10)); // NOI18N
        label_total_gram.setText("0");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 1, 10)); // NOI18N
        jLabel4.setText("TOTAL GRAM :");

        button_insert.setBackground(new java.awt.Color(255, 255, 255));
        button_insert.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_insert.setText("Insert");
        button_insert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_insertActionPerformed(evt);
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

        label_title_terima_lp.setFont(new java.awt.Font("Candara", 1, 18)); // NOI18N
        label_title_terima_lp.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_title_terima_lp.setText("Input Kaki Finishing 2");

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel28.setText("No LP Kaki :");
        jLabel28.setFocusable(false);

        txt_lp_kaki.setEditable(false);
        txt_lp_kaki.setBackground(new java.awt.Color(255, 255, 255));
        txt_lp_kaki.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_lp_kaki.setText("-");
        txt_lp_kaki.setFocusable(false);

        Tabel_data.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Tabel_data.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "No LP", "Tanggal", "Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Object.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Tabel_data.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Tabel_data);
        if (Tabel_data.getColumnModel().getColumnCount() > 0) {
            Tabel_data.getColumnModel().getColumn(0).setMaxWidth(30);
        }

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("Tanggal Input :");
        jLabel9.setEnabled(false);

        label_stok.setBackground(new java.awt.Color(255, 255, 255));
        label_stok.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_stok.setText("0");
        label_stok.setFocusable(false);

        jLabel31.setBackground(new java.awt.Color(255, 255, 255));
        jLabel31.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel31.setText("Gram");
        jLabel31.setFocusable(false);

        button_delete.setBackground(new java.awt.Color(255, 255, 255));
        button_delete.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete.setText("Delete");
        button_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_deleteActionPerformed(evt);
            }
        });

        Date_input.setBackground(new java.awt.Color(255, 255, 255));
        Date_input.setDate(new Date());
        Date_input.setDateFormatString("dd MMMM yyyy");
        Date_input.setEnabled(false);
        Date_input.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_input.setMaxSelectableDate(new Date());

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("No. Laporan Produksi :");

        jLabel29.setBackground(new java.awt.Color(255, 255, 255));
        jLabel29.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel29.setText("Sisa Stok :");
        jLabel29.setFocusable(false);

        button_LPkaki1.setBackground(new java.awt.Color(255, 255, 255));
        button_LPkaki1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_LPkaki1.setText("LP kaki");
        button_LPkaki1.setFocusable(false);
        button_LPkaki1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_LPkaki1ActionPerformed(evt);
            }
        });

        button_save.setBackground(new java.awt.Color(255, 255, 255));
        button_save.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_save.setText("Save");
        button_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_saveActionPerformed(evt);
            }
        });

        txt_no_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_no_lp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_no_lpKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_no_lpKeyTyped(evt);
            }
        });

        label_jumlahLP.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlahLP.setFont(new java.awt.Font("Arial", 1, 10)); // NOI18N
        label_jumlahLP.setText("0");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_jumlahLP))
                    .addComponent(label_title_terima_lp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(4, 4, 4)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(label_stok, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel31)
                                .addGap(81, 81, 81))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(Date_input, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txt_no_lp, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txt_lp_kaki, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(button_save)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_cancel))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(button_insert, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(button_count, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_LPkaki1, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_title_terima_lp)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_gram, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_jumlahLP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_no_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_insert, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_count, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_lp_kaki, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_LPkaki1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_cancel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_save, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_stok, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    private void button_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_cancelActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_button_cancelActionPerformed

    private void button_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_saveActionPerformed
        count();
        input_kaki();
    }//GEN-LAST:event_button_saveActionPerformed

    private void txt_no_lpKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_no_lpKeyTyped
        // TODO add your handling code here:
        if (Character.isAlphabetic(evt.getKeyChar())) {
            char e = evt.getKeyChar();
            String c = String.valueOf(e);
            evt.consume();
            txt_no_lp.setText(txt_no_lp.getText() + c.toUpperCase());
        }
    }//GEN-LAST:event_txt_no_lpKeyTyped

    private void button_insertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_insertActionPerformed
        // TODO add your handling code here:
        insert();
    }//GEN-LAST:event_button_insertActionPerformed

    private void txt_no_lpKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_no_lpKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            insert();
        }
    }//GEN-LAST:event_txt_no_lpKeyPressed

    private void button_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_deleteActionPerformed
        // TODO add your handling code here:
        int i = Tabel_data.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) Tabel_data.getModel();
        if (i != -1) {
            model.removeRow(i);
        }
        count();
    }//GEN-LAST:event_button_deleteActionPerformed

    private void button_LPkaki1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_LPkaki1ActionPerformed
        // TODO add your handling code here:
        JDialog_Choose_LPkaki dialog = new JDialog_Choose_LPkaki(new javax.swing.JFrame(), true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        txt_lp_kaki.setText(dialog.get_lpKaki());
        label_stok.setText(Float.toString(dialog.get_stok()));
    }//GEN-LAST:event_button_LPkaki1ActionPerformed

    private void button_countActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_countActionPerformed
        // TODO add your handling code here:
        count();
    }//GEN-LAST:event_button_countActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JDialog_Choose_LPkaki.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JDialog_Input_Kaki_f2 dialog = new JDialog_Input_Kaki_f2(new javax.swing.JFrame(), true);
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
    private com.toedter.calendar.JDateChooser Date_input;
    private javax.swing.JTable Tabel_data;
    private javax.swing.JButton button_LPkaki1;
    private javax.swing.JButton button_cancel;
    private javax.swing.JButton button_count;
    private javax.swing.JButton button_delete;
    private javax.swing.JButton button_insert;
    private javax.swing.JButton button_save;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_jumlahLP;
    private javax.swing.JLabel label_stok;
    private javax.swing.JLabel label_title_terima_lp;
    private javax.swing.JLabel label_total_gram;
    private javax.swing.JTextField txt_lp_kaki;
    private javax.swing.JTextField txt_no_lp;
    // End of variables declaration//GEN-END:variables
}
