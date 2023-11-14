package waleta_system.SubWaleta;

import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;

public class JPanel_Tarif_upah_sub extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;

    public JPanel_Tarif_upah_sub() {
        initComponents();
    }

    public void init() {
        try {
            Utility.db_sub.connect();
        } catch (Exception ex) {
            Logger.getLogger(JPanel_Tarif_upah_sub.class.getName()).log(Level.SEVERE, null, ex);
        }
        refresh_bulu_upah_online();
        refresh_bulu_upah_local_waleta();
        tabel_data_bulu_upah.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && tabel_data_bulu_upah.getSelectedRow() != -1) {
                    int x = tabel_data_bulu_upah.getSelectedRow();
                    txt_bulu_upah.setText(tabel_data_bulu_upah.getValueAt(x, 0).toString());
                    txt_kpg_1_lp.setText(tabel_data_bulu_upah.getValueAt(x, 1).toString());
                    txt_upah_cabut.setText(tabel_data_bulu_upah.getValueAt(x, 2).toString());
                    txt_bonus_cabut.setText(tabel_data_bulu_upah.getValueAt(x, 3).toString());
                    txt_upah_cuci.setText(tabel_data_bulu_upah.getValueAt(x, 4).toString());
                    txt_bonus_cuci.setText(tabel_data_bulu_upah.getValueAt(x, 5).toString());
                    txt_bobot_gram_lp.setText(tabel_data_bulu_upah.getValueAt(x, 6).toString());
                    txt_bonus_sesekan.setText(tabel_data_bulu_upah.getValueAt(x, 7).toString());
                }
            }
        });
        tabel_data_bulu_upah_local_waleta.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && tabel_data_bulu_upah_local_waleta.getSelectedRow() != -1) {
                    int x = tabel_data_bulu_upah_local_waleta.getSelectedRow();
                    txt_bulu_upah_local_waleta.setText(tabel_data_bulu_upah_local_waleta.getValueAt(x, 0).toString());
                    txt_upah_sub.setText(tabel_data_bulu_upah_local_waleta.getValueAt(x, 1).toString());
                    txt_tarif_lp_sesekan.setText(tabel_data_bulu_upah_local_waleta.getValueAt(x, 2).toString());
                    txt_tarif_lp_sapon.setText(tabel_data_bulu_upah_local_waleta.getValueAt(x, 3).toString());
                    txt_bonus_mk_utuh_cbt_sub.setText(tabel_data_bulu_upah_local_waleta.getValueAt(x, 4).toString());
                    txt_bonus_mk_utuh_ctk_sub.setText(tabel_data_bulu_upah_local_waleta.getValueAt(x, 5).toString());
                }
            }
        });
    }

    public void refresh_bulu_upah_online() {
        try {
            Utility.db_sub.connect();
            DefaultTableModel model = (DefaultTableModel) tabel_data_bulu_upah.getModel();
            model.setRowCount(0);
            sql = "SELECT `bulu_upah`, `bobot_1_lp`, `upah_cabut`, `bonus_cabut`, `upah_cuci`, `bonus_cuci`, `bobot_gram_lp`, `bonus_sesekan` FROM `tb_tarif_upah` WHERE 1";
            rs = Utility.db_sub.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getString("bulu_upah");
                row[1] = rs.getInt("bobot_1_lp");
                row[2] = rs.getInt("upah_cabut");
                row[3] = rs.getInt("bonus_cabut");
                row[4] = rs.getInt("upah_cuci");
                row[5] = rs.getInt("bonus_cuci");
                row[6] = rs.getInt("bobot_gram_lp");
                row[7] = rs.getInt("bonus_sesekan");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_data_bulu_upah);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Tarif_upah_sub.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void refresh_bulu_upah_local_waleta() {
        try {
            DefaultTableModel model = (DefaultTableModel) tabel_data_bulu_upah_local_waleta.getModel();
            model.setRowCount(0);
            sql = "SELECT `bulu_upah`, `tarif_gram`, `tarif_sub`, `tarif_sesekan_sub`, `tarif_sapon_sub`, `kpg_lp`, `kpg_lp_flat`, `kpg_lp_jidun`, `gram_lp_bp_sub`, "
                    + "`ctk_mku`, `ctk_mpch`, `ctk_flat`, `ctk_jdn`, "
                    + "`bonus_mk_utuh_cabut`, `bonus_mk_utuh_cetak`, `bonus_mk_utuh_cabut_sub`, `bonus_mk_utuh_cetak_sub`, "
                    + "`status` "
                    + "FROM `tb_tarif_cabut` WHERE 1";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getString("bulu_upah");
                row[1] = rs.getInt("tarif_sub");
                row[2] = rs.getInt("tarif_sesekan_sub");
                row[3] = rs.getInt("tarif_sapon_sub");
                row[4] = rs.getInt("bonus_mk_utuh_cabut_sub");
                row[5] = rs.getInt("bonus_mk_utuh_cetak_sub");
                row[6] = rs.getString("status");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_data_bulu_upah_local_waleta);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Tarif_upah_sub.class.getName()).log(Level.SEVERE, null, ex);
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
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabel_data_bulu_upah = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        button_edit = new javax.swing.JButton();
        button_new = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        button_clear = new javax.swing.JButton();
        txt_upah_cabut = new javax.swing.JTextField();
        txt_bulu_upah = new javax.swing.JTextField();
        txt_kpg_1_lp = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txt_bonus_cuci = new javax.swing.JTextField();
        txt_upah_cuci = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        button_delete = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        txt_bonus_cabut = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        txt_bobot_gram_lp = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        txt_bonus_sesekan = new javax.swing.JTextField();
        button_refresh = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tabel_data_bulu_upah_local_waleta = new javax.swing.JTable();
        button_refresh1 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        button_edit_data_local_waleta = new javax.swing.JButton();
        txt_upah_sub = new javax.swing.JTextField();
        txt_bulu_upah_local_waleta = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        txt_tarif_lp_sesekan = new javax.swing.JTextField();
        txt_bonus_mk_utuh_cbt_sub = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        txt_tarif_lp_sapon = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        txt_bonus_mk_utuh_ctk_sub = new javax.swing.JTextField();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Tarif Upah Sub", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        tabel_data_bulu_upah.setAutoCreateRowSorter(true);
        tabel_data_bulu_upah.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        tabel_data_bulu_upah.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Bulu Upah", "Kpg 1 LP", "Upah Cabut/Gr", "Bonus Cabut/Gr", "Upah Cuci/Gr", "Bonus Cuci/Gr", "Bobot Gram LP", "Bonus Sesekan / Gr"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        tabel_data_bulu_upah.setRowHeight(20);
        tabel_data_bulu_upah.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(tabel_data_bulu_upah);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.setPreferredSize(new java.awt.Dimension(400, 255));

        button_edit.setBackground(new java.awt.Color(255, 255, 255));
        button_edit.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_edit.setText("Edit");
        button_edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_editActionPerformed(evt);
            }
        });

        button_new.setBackground(new java.awt.Color(255, 255, 255));
        button_new.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_new.setText("Baru");
        button_new.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_newActionPerformed(evt);
            }
        });

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel13.setText("Upah Cuci / Gr :");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel15.setText("Bonus Cabut / Gr :");

        button_clear.setBackground(new java.awt.Color(255, 255, 255));
        button_clear.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_clear.setText("Clear");
        button_clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_clearActionPerformed(evt);
            }
        });

        txt_upah_cabut.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_upah_cabut.setText("0");
        txt_upah_cabut.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_upah_cabutKeyTyped(evt);
            }
        });

        txt_bulu_upah.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        txt_kpg_1_lp.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_kpg_1_lp.setText("0");
        txt_kpg_1_lp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_kpg_1_lpKeyTyped(evt);
            }
        });

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Kategori Bulu Upah :");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel14.setText("Upah Cabut / Gr :");

        txt_bonus_cuci.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_bonus_cuci.setText("0");
        txt_bonus_cuci.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_bonus_cuciKeyTyped(evt);
            }
        });

        txt_upah_cuci.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_upah_cuci.setText("0");
        txt_upah_cuci.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_upah_cuciKeyTyped(evt);
            }
        });

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel12.setText("Kpg 1 LP");

        button_delete.setBackground(new java.awt.Color(255, 255, 255));
        button_delete.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_delete.setText("Detele");
        button_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_deleteActionPerformed(evt);
            }
        });

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel21.setText("Bonus Cabut / Gr :");

        txt_bonus_cabut.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_bonus_cabut.setText("0");
        txt_bonus_cabut.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_bonus_cabutKeyTyped(evt);
            }
        });

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel16.setText("Bobot Gram LP :");

        txt_bobot_gram_lp.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_bobot_gram_lp.setText("0");
        txt_bobot_gram_lp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_bobot_gram_lpKeyTyped(evt);
            }
        });

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel17.setText("Bonus KK KLT, BP,SP / Gr :");

        txt_bonus_sesekan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_bonus_sesekan.setText("0");
        txt_bonus_sesekan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_bonus_sesekanKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_bulu_upah))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_kpg_1_lp))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_upah_cuci))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_upah_cabut))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_bonus_cuci))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_bonus_cabut))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(button_new)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_edit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_delete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_clear)
                        .addGap(0, 116, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_bobot_gram_lp))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_bonus_sesekan)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_bulu_upah, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_kpg_1_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_upah_cabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_bonus_cabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_upah_cuci, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_bonus_cuci, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_bobot_gram_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_bonus_sesekan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_clear)
                    .addComponent(button_delete)
                    .addComponent(button_edit)
                    .addComponent(button_new))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        button_refresh.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_refresh.setText("Refresh");
        button_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refreshActionPerformed(evt);
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
                        .addComponent(button_refresh)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 925, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(button_refresh)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 607, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 607, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Online", jPanel3);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        tabel_data_bulu_upah_local_waleta.setAutoCreateRowSorter(true);
        tabel_data_bulu_upah_local_waleta.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        tabel_data_bulu_upah_local_waleta.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Bulu Upah", "Upah Sub/gr", "Tarif LP sesekan Sub/Gr", "Tarif LP Sapon Sub/Gr", "Bonus MKU CBT SUB", "Bonus MKU CTK SUB", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel_data_bulu_upah_local_waleta.setRowHeight(20);
        tabel_data_bulu_upah_local_waleta.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(tabel_data_bulu_upah_local_waleta);

        button_refresh1.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_refresh1.setText("Refresh");
        button_refresh1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh1ActionPerformed(evt);
            }
        });

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel5.setPreferredSize(new java.awt.Dimension(400, 203));

        button_edit_data_local_waleta.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_data_local_waleta.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_edit_data_local_waleta.setText("Edit");
        button_edit_data_local_waleta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_data_local_waletaActionPerformed(evt);
            }
        });

        txt_upah_sub.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_upah_sub.setText("0");
        txt_upah_sub.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_upah_subKeyTyped(evt);
            }
        });

        txt_bulu_upah_local_waleta.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText("Kategori Bulu Upah :");

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel18.setText("Upah Sub / Gr :");

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel22.setText("Tarif LP Sesekan SUB/ Gr :");

        txt_tarif_lp_sesekan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_tarif_lp_sesekan.setText("0");
        txt_tarif_lp_sesekan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_tarif_lp_sesekanKeyTyped(evt);
            }
        });

        txt_bonus_mk_utuh_cbt_sub.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_bonus_mk_utuh_cbt_sub.setText("0");
        txt_bonus_mk_utuh_cbt_sub.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_bonus_mk_utuh_cbt_subKeyTyped(evt);
            }
        });

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel25.setText("Bonus MK Utuh CBT SUB :");

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel26.setText("Tarif LP Sapon SUB/ Gr :");

        txt_tarif_lp_sapon.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_tarif_lp_sapon.setText("0");
        txt_tarif_lp_sapon.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_tarif_lp_saponKeyTyped(evt);
            }
        });

        jLabel27.setBackground(new java.awt.Color(255, 255, 255));
        jLabel27.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel27.setText("Bonus MK Utuh CTK SUB :");

        txt_bonus_mk_utuh_ctk_sub.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_bonus_mk_utuh_ctk_sub.setText("0");
        txt_bonus_mk_utuh_ctk_sub.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_bonus_mk_utuh_ctk_subKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_bulu_upah_local_waleta)
                    .addComponent(txt_upah_sub, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
                    .addComponent(txt_tarif_lp_sesekan)
                    .addComponent(txt_bonus_mk_utuh_cbt_sub)
                    .addComponent(txt_tarif_lp_sapon)
                    .addComponent(txt_bonus_mk_utuh_ctk_sub))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(335, Short.MAX_VALUE)
                .addComponent(button_edit_data_local_waleta)
                .addGap(10, 10, 10))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_bulu_upah_local_waleta, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_upah_sub, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_tarif_lp_sesekan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_tarif_lp_sapon, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_bonus_mk_utuh_cbt_sub, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_bonus_mk_utuh_ctk_sub, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_edit_data_local_waleta)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(button_refresh1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 925, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(button_refresh1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, 607, Short.MAX_VALUE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 607, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Local Waleta", jPanel4);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void button_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_clearActionPerformed
        // TODO add your handling code here:
        txt_bulu_upah.setText(null);
        txt_kpg_1_lp.setText(null);
        txt_upah_cabut.setText(null);
        txt_bonus_cabut.setText(null);
        txt_upah_cuci.setText(null);
        txt_bonus_cuci.setText(null);
        txt_bobot_gram_lp.setText(null);
        txt_bonus_sesekan.setText(null);
    }//GEN-LAST:event_button_clearActionPerformed

    private void button_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_deleteActionPerformed
        // TODO add your handling code here:
        try {
            int j = tabel_data_bulu_upah.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Pilih data yang akan di hapus !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin akan menghapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    Utility.db_sub.connect();
                    sql = "DELETE FROM `tb_tarif_upah` WHERE `bulu_upah`='" + tabel_data_bulu_upah.getValueAt(j, 0).toString() + "'";
                    if ((Utility.db_sub.getStatement().executeUpdate(sql)) == 1) {
                        JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
                        refresh_bulu_upah_online();
                    } else {
                        JOptionPane.showMessageDialog(this, "DELETE FAILED!");
                    }
                    button_clear.doClick();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Tarif_upah_sub.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_deleteActionPerformed

    private void button_editActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_editActionPerformed
        // TODO add your handling code here:
        int j = tabel_data_bulu_upah.getSelectedRow();
        Boolean Check = true;
        try {
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Pilih data yang akan di edit !");
            } else {
                if (Check) {
                    Utility.db_sub.connect();
                    sql = "UPDATE `tb_tarif_upah` SET "
                            + "`bulu_upah`='" + txt_bulu_upah.getText() + "',"
                            + "`bobot_1_lp`='" + txt_kpg_1_lp.getText() + "',"
                            + "`upah_cabut`='" + txt_upah_cabut.getText() + "',"
                            + "`bonus_cabut`='" + txt_bonus_cabut.getText() + "',"
                            + "`upah_cuci`='" + txt_upah_cuci.getText() + "',"
                            + "`bonus_cuci`='" + txt_bonus_cuci.getText() + "', "
                            + "`bobot_gram_lp`='" + txt_bobot_gram_lp.getText() + "', "
                            + "`bonus_sesekan`='" + txt_bonus_sesekan.getText() + "' "
                            + "WHERE `bulu_upah`='" + tabel_data_bulu_upah.getValueAt(j, 0).toString() + "'";
                    if ((Utility.db_sub.getStatement().executeUpdate(sql)) == 1) {
                        JOptionPane.showMessageDialog(this, "DATA SAVED !");
                        refresh_bulu_upah_online();
                        button_clear.doClick();
                    } else {
                        JOptionPane.showMessageDialog(this, "update failed !");
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Tarif_upah_sub.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_editActionPerformed

    private void button_newActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_newActionPerformed
        // TODO add your handling code here:
        try {
            Utility.db_sub.connect();
            sql = "INSERT INTO `tb_tarif_upah`(`bulu_upah`, `bobot_1_lp`, `upah_cabut`, `bonus_cabut`, `upah_cuci`, `bonus_cuci`, `bobot_gram_lp`, `bonus_sesekan`) "
                    + "VALUES ("
                    + "'" + txt_bulu_upah.getText() + "',"
                    + "'" + txt_kpg_1_lp.getText() + "',"
                    + "'" + txt_upah_cabut.getText() + "',"
                    + "'" + txt_bonus_cabut.getText() + "',"
                    + "'" + txt_upah_cuci.getText() + "',"
                    + "'" + txt_bonus_cuci.getText() + "',"
                    + "'" + txt_bobot_gram_lp.getText() + "',"
                    + "'" + txt_bonus_sesekan.getText() + "') ";
            Utility.db_sub.getConnection().createStatement();
            if ((Utility.db_sub.getStatement().executeUpdate(sql)) == 1) {
                JOptionPane.showMessageDialog(this, "data insert Successfully");
                refresh_bulu_upah_online();
            } else {
                JOptionPane.showMessageDialog(this, "insert failed");
            }
            button_clear.doClick();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Tarif_upah_sub.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_newActionPerformed

    private void txt_kpg_1_lpKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_kpg_1_lpKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_kpg_1_lpKeyTyped

    private void txt_upah_cuciKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_upah_cuciKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_upah_cuciKeyTyped

    private void button_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refreshActionPerformed
        // TODO add your handling code here:
        refresh_bulu_upah_online();
    }//GEN-LAST:event_button_refreshActionPerformed

    private void txt_upah_cabutKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_upah_cabutKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_upah_cabutKeyTyped

    private void txt_bonus_cuciKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_bonus_cuciKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_bonus_cuciKeyTyped

    private void txt_bonus_cabutKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_bonus_cabutKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_bonus_cabutKeyTyped

    private void txt_bobot_gram_lpKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_bobot_gram_lpKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_bobot_gram_lpKeyTyped

    private void txt_bonus_sesekanKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_bonus_sesekanKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_bonus_sesekanKeyTyped

    private void button_refresh1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh1ActionPerformed
        // TODO add your handling code here:
        refresh_bulu_upah_local_waleta();
    }//GEN-LAST:event_button_refresh1ActionPerformed

    private void button_edit_data_local_waletaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_data_local_waletaActionPerformed
        // TODO add your handling code here:
        int j = tabel_data_bulu_upah.getSelectedRow();
        Boolean Check = true;
        try {
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Pilih data yang akan di edit !");
            } else {
                if (Check) {
                    sql = "UPDATE `tb_tarif_cabut` SET "
                    + "`tarif_sub`='" + txt_upah_sub.getText() + "',"
                    + "`tarif_sesekan_sub`='" + txt_tarif_lp_sesekan.getText() + "',"
                    + "`tarif_sapon_sub`='" + txt_tarif_lp_sapon.getText() + "',"
                    + "`bonus_mk_utuh_cabut_sub`='" + txt_bonus_mk_utuh_cbt_sub.getText() + "', "
                    + "`bonus_mk_utuh_cetak_sub`='" + txt_bonus_mk_utuh_ctk_sub.getText() + "' "
                    + "WHERE `bulu_upah`='" + tabel_data_bulu_upah.getValueAt(j, 0).toString() + "'";
                    if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                        JOptionPane.showMessageDialog(this, "data updated Successfully");
                        refresh_bulu_upah_local_waleta();
                        txt_bulu_upah_local_waleta.setText("");
                        txt_upah_sub.setText("");
                        txt_tarif_lp_sesekan.setText("");
                        txt_tarif_lp_sapon.setText("");
                        txt_bonus_mk_utuh_cbt_sub.setText("");
                        txt_bonus_mk_utuh_ctk_sub.setText("");
                    } else {
                        JOptionPane.showMessageDialog(this, "update failed");
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Tarif_upah_sub.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_edit_data_local_waletaActionPerformed

    private void txt_upah_subKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_upah_subKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
            && evt.getKeyChar() != '.'
            && evt.getKeyCode() != KeyEvent.VK_ENTER
            && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
            && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_upah_subKeyTyped

    private void txt_tarif_lp_sesekanKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_tarif_lp_sesekanKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
            && evt.getKeyChar() != '.'
            && evt.getKeyCode() != KeyEvent.VK_ENTER
            && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
            && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_tarif_lp_sesekanKeyTyped

    private void txt_bonus_mk_utuh_cbt_subKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_bonus_mk_utuh_cbt_subKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
            && evt.getKeyChar() != '.'
            && evt.getKeyCode() != KeyEvent.VK_ENTER
            && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
            && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_bonus_mk_utuh_cbt_subKeyTyped

    private void txt_tarif_lp_saponKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_tarif_lp_saponKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
            && evt.getKeyChar() != '.'
            && evt.getKeyCode() != KeyEvent.VK_ENTER
            && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
            && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_tarif_lp_saponKeyTyped

    private void txt_bonus_mk_utuh_ctk_subKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_bonus_mk_utuh_ctk_subKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
            && evt.getKeyChar() != '.'
            && evt.getKeyCode() != KeyEvent.VK_ENTER
            && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
            && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_bonus_mk_utuh_ctk_subKeyTyped


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton button_clear;
    private javax.swing.JButton button_delete;
    private javax.swing.JButton button_edit;
    private javax.swing.JButton button_edit_data_local_waleta;
    private javax.swing.JButton button_new;
    public static javax.swing.JButton button_refresh;
    public static javax.swing.JButton button_refresh1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable tabel_data_bulu_upah;
    private javax.swing.JTable tabel_data_bulu_upah_local_waleta;
    private javax.swing.JTextField txt_bobot_gram_lp;
    private javax.swing.JTextField txt_bonus_cabut;
    private javax.swing.JTextField txt_bonus_cuci;
    private javax.swing.JTextField txt_bonus_mk_utuh_cbt_sub;
    private javax.swing.JTextField txt_bonus_mk_utuh_ctk_sub;
    private javax.swing.JTextField txt_bonus_sesekan;
    private javax.swing.JTextField txt_bulu_upah;
    private javax.swing.JTextField txt_bulu_upah_local_waleta;
    private javax.swing.JTextField txt_kpg_1_lp;
    private javax.swing.JTextField txt_tarif_lp_sapon;
    private javax.swing.JTextField txt_tarif_lp_sesekan;
    private javax.swing.JTextField txt_upah_cabut;
    private javax.swing.JTextField txt_upah_cuci;
    private javax.swing.JTextField txt_upah_sub;
    // End of variables declaration//GEN-END:variables
}
