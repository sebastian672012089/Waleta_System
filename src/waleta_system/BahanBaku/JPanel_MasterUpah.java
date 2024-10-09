package waleta_system.BahanBaku;

import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;
import waleta_system.MainForm;

public class JPanel_MasterUpah extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_MasterUpah() {
        initComponents();
    }

    public void init() {
        refresh_tarif_cabut();
        tabel_data_bulu_upah.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && tabel_data_bulu_upah.getSelectedRow() != -1) {
                    int x = tabel_data_bulu_upah.getSelectedRow();
                    txt_bulu_upah.setText(tabel_data_bulu_upah.getValueAt(x, 0).toString());
                    txt_upah_cabut.setText(tabel_data_bulu_upah.getValueAt(x, 1).toString());
                    txt_upah_sub.setText(tabel_data_bulu_upah.getValueAt(x, 2).toString());
                    txt_tarif_lp_sesekan.setText(tabel_data_bulu_upah.getValueAt(x, 3).toString());
                    txt_tarif_lp_sapon.setText(tabel_data_bulu_upah.getValueAt(x, 4).toString());
                    txt_kpg_lp.setText(tabel_data_bulu_upah.getValueAt(x, 5).toString());
                    txt_kpg_lp_flat.setText(tabel_data_bulu_upah.getValueAt(x, 6).toString());
                    txt_kpg_lp_jidun.setText(tabel_data_bulu_upah.getValueAt(x, 7).toString());
                    txt_gram_lp_bp_sub.setText(tabel_data_bulu_upah.getValueAt(x, 8).toString());
                    txt_ctk_mk.setText(tabel_data_bulu_upah.getValueAt(x, 9).toString());
                    txt_ctk_pch.setText(tabel_data_bulu_upah.getValueAt(x, 10).toString());
                    txt_ctk_flat.setText(tabel_data_bulu_upah.getValueAt(x, 11).toString());
                    txt_ctk_jdn.setText(tabel_data_bulu_upah.getValueAt(x, 12).toString());
                    txt_bonus_mk_utuh_cbt.setText(tabel_data_bulu_upah.getValueAt(x, 13).toString());
                    txt_bonus_mk_utuh_ctk.setText(tabel_data_bulu_upah.getValueAt(x, 14).toString());
                    txt_bonus_mk_utuh_cbt_sub.setText(tabel_data_bulu_upah.getValueAt(x, 15).toString());
                    txt_bonus_mk_utuh_ctk_sub.setText(tabel_data_bulu_upah.getValueAt(x, 16).toString());
                    if (tabel_data_bulu_upah.getValueAt(x, 17).toString().equals("AKTIF")) {
                        button_aktif.setEnabled(false);
                        button_non_aktif.setEnabled(true);
                    } else if (tabel_data_bulu_upah.getValueAt(x, 16).toString().equals("NON-AKTIF")) {
                        button_aktif.setEnabled(true);
                        button_non_aktif.setEnabled(false);
                    }
                }
            }
        });
    }

    public void refresh_tarif_cabut() {
        try {
            DefaultTableModel model = (DefaultTableModel) tabel_data_bulu_upah.getModel();
            model.setRowCount(0);
            sql = "SELECT `bulu_upah`, `tarif_gram`, `tarif_sub`, `tarif_sesekan_sub`, `tarif_sapon_sub`, `kpg_lp`, `kpg_lp_flat`, `kpg_lp_jidun`, `gram_lp_bp_sub`, "
                    + "`ctk_mku`, `ctk_mpch`, `ctk_flat`, `ctk_jdn`, "
                    + "`bonus_mk_utuh_cabut`, `bonus_mk_utuh_cetak`, `bonus_mk_utuh_cabut_sub`, `bonus_mk_utuh_cetak_sub`, "
                    + "`status` "
                    + "FROM `tb_tarif_cabut` WHERE 1";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[20];
            while (rs.next()) {
                row[0] = rs.getString("bulu_upah");
                row[1] = rs.getInt("tarif_gram");
                row[2] = rs.getInt("tarif_sub");
                row[3] = rs.getInt("tarif_sesekan_sub");
                row[4] = rs.getInt("tarif_sapon_sub");
                row[5] = rs.getInt("kpg_lp");
                row[6] = rs.getInt("kpg_lp_flat");
                row[7] = rs.getInt("kpg_lp_jidun");
                row[8] = rs.getInt("gram_lp_bp_sub");
                row[9] = rs.getInt("ctk_mku");
                row[10] = rs.getInt("ctk_mpch");
                row[11] = rs.getInt("ctk_flat");
                row[12] = rs.getInt("ctk_jdn");
                row[13] = rs.getInt("bonus_mk_utuh_cabut");
                row[14] = rs.getInt("bonus_mk_utuh_cetak");
                row[15] = rs.getInt("bonus_mk_utuh_cabut_sub");
                row[16] = rs.getInt("bonus_mk_utuh_cetak_sub");
                row[17] = rs.getString("status");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_data_bulu_upah);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_GradeBahanBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void executeSQLQuery(String query, String message) {
        Utility.db.getConnection();
        try {
            Utility.db.getConnection().createStatement();
            if ((Utility.db.getStatement().executeUpdate(query)) == 1) {
                JOptionPane.showMessageDialog(this, "data " + message + " Successfully");
                refresh_tarif_cabut();
            } else {
                JOptionPane.showMessageDialog(this, "data not " + message);
            }
        } catch (SQLException | HeadlessException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, e);
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
        jScrollPane3 = new javax.swing.JScrollPane();
        tabel_data_bulu_upah = new javax.swing.JTable();
        button_aktif = new javax.swing.JButton();
        button_non_aktif = new javax.swing.JButton();
        button_refresh = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        button_edit = new javax.swing.JButton();
        button_new = new javax.swing.JButton();
        txt_ctk_pch = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        button_clear = new javax.swing.JButton();
        txt_upah_sub = new javax.swing.JTextField();
        txt_bulu_upah = new javax.swing.JTextField();
        txt_kpg_lp_jidun = new javax.swing.JTextField();
        txt_upah_cabut = new javax.swing.JTextField();
        txt_ctk_flat = new javax.swing.JTextField();
        txt_ctk_jdn = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txt_ctk_mk = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        txt_kpg_lp_flat = new javax.swing.JTextField();
        txt_kpg_lp = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        button_delete = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        txt_tarif_lp_sesekan = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        txt_gram_lp_bp_sub = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        txt_bonus_mk_utuh_cbt = new javax.swing.JTextField();
        txt_bonus_mk_utuh_ctk = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        txt_bonus_mk_utuh_cbt_sub = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        txt_tarif_lp_sapon = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        txt_bonus_mk_utuh_ctk_sub = new javax.swing.JTextField();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Tarif Upah Borong & Upah Sub", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        tabel_data_bulu_upah.setAutoCreateRowSorter(true);
        tabel_data_bulu_upah.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        tabel_data_bulu_upah.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Bulu Upah", "Upah Cabut/gr", "Upah Sub/gr", "Tarif LP sesekan Sub/Gr", "Tarif LP Sapon Sub/Gr", "Kpg LP Bsr", "Kpg LP Flat", "Kpg LP Jdn", "Gram LP BP SUB", "Ctk MK", "Ctk Pch", "Ctk Flat", "Ctk JDN", "Bonus MKU CBT", "Bonus MKU CTK", "Bonus MKU CBT SUB", "Bonus MKU CTK SUB", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
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

        button_aktif.setBackground(new java.awt.Color(255, 255, 255));
        button_aktif.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_aktif.setText("AKTIF");
        button_aktif.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_aktifActionPerformed(evt);
            }
        });

        button_non_aktif.setBackground(new java.awt.Color(255, 255, 255));
        button_non_aktif.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_non_aktif.setText("NON-AKTIF");
        button_non_aktif.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_non_aktifActionPerformed(evt);
            }
        });

        button_refresh.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_refresh.setText("Refresh");
        button_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refreshActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

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
        button_new.setText("New");
        button_new.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_newActionPerformed(evt);
            }
        });

        txt_ctk_pch.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_ctk_pch.setText("0");
        txt_ctk_pch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_ctk_pchKeyTyped(evt);
            }
        });

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel13.setText("Jmlh Kpg LP Besar :");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel15.setText("Jmlh Kpg LP Flat :");

        button_clear.setBackground(new java.awt.Color(255, 255, 255));
        button_clear.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_clear.setText("Clear");
        button_clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_clearActionPerformed(evt);
            }
        });

        txt_upah_sub.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_upah_sub.setText("0");
        txt_upah_sub.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_upah_subKeyTyped(evt);
            }
        });

        txt_bulu_upah.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        txt_kpg_lp_jidun.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_kpg_lp_jidun.setText("0");
        txt_kpg_lp_jidun.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_kpg_lp_jidunKeyTyped(evt);
            }
        });

        txt_upah_cabut.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_upah_cabut.setText("0");
        txt_upah_cabut.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_upah_cabutKeyTyped(evt);
            }
        });

        txt_ctk_flat.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_ctk_flat.setText("0");
        txt_ctk_flat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_ctk_flatKeyTyped(evt);
            }
        });

        txt_ctk_jdn.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_ctk_jdn.setText("0");
        txt_ctk_jdn.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_ctk_jdnKeyTyped(evt);
            }
        });

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel18.setText("Upah Cetak Flat :");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Kategori Bulu Upah :");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel14.setText("Upah Sub / Gr :");

        txt_ctk_mk.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_ctk_mk.setText("0");
        txt_ctk_mk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_ctk_mkKeyTyped(evt);
            }
        });

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel19.setText("Upah Cetak Jdn :");

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel16.setText("Upah Cetak MK :");

        txt_kpg_lp_flat.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_kpg_lp_flat.setText("0");
        txt_kpg_lp_flat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_kpg_lp_flatKeyTyped(evt);
            }
        });

        txt_kpg_lp.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_kpg_lp.setText("0");
        txt_kpg_lp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_kpg_lpKeyTyped(evt);
            }
        });

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel12.setText("Upah Cabut / Gr :");

        button_delete.setBackground(new java.awt.Color(255, 255, 255));
        button_delete.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_delete.setText("Detele");
        button_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_deleteActionPerformed(evt);
            }
        });

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel20.setText("Jmlh Kpg LP JDN :");

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel17.setText("Upah Cetak Pch :");

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel21.setText("Tarif LP Sesekan SUB/ Gr :");

        txt_tarif_lp_sesekan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_tarif_lp_sesekan.setText("0");
        txt_tarif_lp_sesekan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_tarif_lp_sesekanKeyTyped(evt);
            }
        });

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel22.setText("Jmlh Gram LP BP SUB :");

        txt_gram_lp_bp_sub.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_gram_lp_bp_sub.setText("0");
        txt_gram_lp_bp_sub.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_gram_lp_bp_subKeyTyped(evt);
            }
        });

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel23.setText("Bonus MK Utuh CBT :");

        txt_bonus_mk_utuh_cbt.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_bonus_mk_utuh_cbt.setText("0");
        txt_bonus_mk_utuh_cbt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_bonus_mk_utuh_cbtKeyTyped(evt);
            }
        });

        txt_bonus_mk_utuh_ctk.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_bonus_mk_utuh_ctk.setText("0");
        txt_bonus_mk_utuh_ctk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_bonus_mk_utuh_ctkKeyTyped(evt);
            }
        });

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel24.setText("Bonus MK Utuh CTK :");

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
                        .addComponent(txt_upah_cabut, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_kpg_lp))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_ctk_mk))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_ctk_pch))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_ctk_flat))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_ctk_jdn))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(button_new)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_edit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_delete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_clear))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_upah_sub))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_kpg_lp_flat))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_kpg_lp_jidun))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_tarif_lp_sesekan))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_gram_lp_bp_sub))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_bonus_mk_utuh_cbt))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_bonus_mk_utuh_ctk))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_bonus_mk_utuh_cbt_sub))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_tarif_lp_sapon))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_bonus_mk_utuh_ctk_sub)))
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
                    .addComponent(txt_upah_cabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_upah_sub, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_tarif_lp_sesekan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_tarif_lp_sapon, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_kpg_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_kpg_lp_flat, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_kpg_lp_jidun, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_gram_lp_bp_sub, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_ctk_mk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_ctk_pch, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_ctk_flat, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_ctk_jdn, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_bonus_mk_utuh_cbt, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_bonus_mk_utuh_ctk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_bonus_mk_utuh_cbt_sub, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_bonus_mk_utuh_ctk_sub, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_clear)
                    .addComponent(button_delete)
                    .addComponent(button_edit)
                    .addComponent(button_new))
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
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 942, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(button_refresh)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_aktif)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_non_aktif)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_aktif, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_non_aktif, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 639, Short.MAX_VALUE))
                .addContainerGap())
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

    private void txt_ctk_flatKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_ctk_flatKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_ctk_flatKeyTyped

    private void txt_ctk_jdnKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_ctk_jdnKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_ctk_jdnKeyTyped

    private void button_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_clearActionPerformed
        // TODO add your handling code here:
        txt_bulu_upah.setText(null);
        txt_upah_cabut.setText(null);
        txt_upah_sub.setText(null);
        txt_tarif_lp_sesekan.setText(null);
        txt_tarif_lp_sapon.setText(null);
        txt_kpg_lp.setText(null);
        txt_kpg_lp_flat.setText(null);
        txt_kpg_lp_jidun.setText(null);
        txt_gram_lp_bp_sub.setText(null);
        txt_ctk_mk.setText(null);
        txt_ctk_pch.setText(null);
        txt_ctk_flat.setText(null);
        txt_ctk_jdn.setText(null);
        txt_bonus_mk_utuh_cbt.setText(null);
        txt_bonus_mk_utuh_ctk.setText(null);
        txt_bonus_mk_utuh_cbt_sub.setText(null);
        txt_bonus_mk_utuh_ctk_sub.setText(null);
    }//GEN-LAST:event_button_clearActionPerformed

    private void button_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_deleteActionPerformed
        // TODO add your handling code here:
        try {
            int j = tabel_data_bulu_upah.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Pilih data yang akan di hapus !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    sql = "DELETE FROM `tb_tarif_cabut` WHERE `bulu_upah`='" + tabel_data_bulu_upah.getValueAt(j, 0).toString() + "'";
                    if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                        JOptionPane.showMessageDialog(this, "data deleted Successfully");
                        refresh_tarif_cabut();
                    } else {
                        JOptionPane.showMessageDialog(this, "delete failed");
                    }
                    button_clear.doClick();
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JPanel_GradeBahanBaku.class.getName()).log(Level.SEVERE, null, e);
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
                    sql = "UPDATE `tb_tarif_cabut` SET "
                            + "`bulu_upah`='" + txt_bulu_upah.getText() + "',"
                            + "`tarif_gram`='" + txt_upah_cabut.getText() + "',"
                            + "`tarif_sub`='" + txt_upah_sub.getText() + "',"
                            + "`tarif_sesekan_sub`='" + txt_tarif_lp_sesekan.getText() + "',"
                            + "`tarif_sapon_sub`='" + txt_tarif_lp_sapon.getText() + "',"
                            + "`kpg_lp`='" + txt_kpg_lp.getText() + "',"
                            + "`kpg_lp_flat`='" + txt_kpg_lp_flat.getText() + "',"
                            + "`kpg_lp_jidun`='" + txt_kpg_lp_jidun.getText() + "',"
                            + "`gram_lp_bp_sub`='" + txt_gram_lp_bp_sub.getText() + "',"
                            + "`ctk_mku`='" + txt_ctk_mk.getText() + "',"
                            + "`ctk_mpch`='" + txt_ctk_pch.getText() + "',"
                            + "`ctk_flat`='" + txt_ctk_flat.getText() + "',"
                            + "`ctk_jdn`='" + txt_ctk_jdn.getText() + "', "
                            + "`bonus_mk_utuh_cabut`='" + txt_bonus_mk_utuh_cbt.getText() + "', "
                            + "`bonus_mk_utuh_cetak`='" + txt_bonus_mk_utuh_ctk.getText() + "', "
                            + "`bonus_mk_utuh_cabut_sub`='" + txt_bonus_mk_utuh_cbt_sub.getText() + "', "
                            + "`bonus_mk_utuh_cetak_sub`='" + txt_bonus_mk_utuh_ctk_sub.getText() + "' "
                            + "WHERE `bulu_upah`='" + tabel_data_bulu_upah.getValueAt(j, 0).toString() + "'";
                    if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                        JOptionPane.showMessageDialog(this, "Perubahan data berhasil disimpan");
                        refresh_tarif_cabut();
                    } else {
                        JOptionPane.showMessageDialog(this, "update failed");
                    }
                    button_clear.doClick();
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JPanel_GradeBahanBaku.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_editActionPerformed

    private void button_newActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_newActionPerformed
        // TODO add your handling code here:
        try {
            sql = "INSERT INTO `tb_tarif_cabut`(`bulu_upah`, `tarif_gram`, `tarif_sub`, `tarif_sesekan_sub`, `tarif_sapon_sub`, `kpg_lp`, `kpg_lp_flat`, `kpg_lp_jidun`, `gram_lp_bp_sub`, `ctk_mku`, `ctk_mpch`, `ctk_flat`, `ctk_jdn`, `bonus_mk_utuh_cabut`, `bonus_mk_utuh_cetak`, `bonus_mk_utuh_cabut_sub`, `bonus_mk_utuh_cetak_sub`, `status`) "
                    + "VALUES ("
                    + "'" + txt_bulu_upah.getText() + "',"
                    + "'" + txt_upah_cabut.getText() + "',"
                    + "'" + txt_upah_sub.getText() + "',"
                    + "'" + txt_tarif_lp_sesekan.getText() + "',"
                    + "'" + txt_tarif_lp_sapon.getText() + "',"
                    + "'" + txt_kpg_lp.getText() + "',"
                    + "'" + txt_kpg_lp_flat.getText() + "',"
                    + "'" + txt_kpg_lp_jidun.getText() + "',"
                    + "'" + txt_gram_lp_bp_sub.getText() + "',"
                    + "'" + txt_ctk_mk.getText() + "',"
                    + "'" + txt_ctk_pch.getText() + "',"
                    + "'" + txt_ctk_flat.getText() + "',"
                    + "'" + txt_ctk_jdn.getText() + "',"
                    + "'" + txt_bonus_mk_utuh_cbt.getText() + "',"
                    + "'" + txt_bonus_mk_utuh_ctk.getText() + "',"
                    + "'" + txt_bonus_mk_utuh_cbt_sub.getText() + "',"
                    + "'" + txt_bonus_mk_utuh_ctk_sub.getText() + "',"
                    + "'AKTIF')";
            Utility.db.getConnection().createStatement();
            if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                JOptionPane.showMessageDialog(this, "data insert Successfully");
                refresh_tarif_cabut();
            } else {
                JOptionPane.showMessageDialog(this, "insert failed");
            }
            button_clear.doClick();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_GradeBahanBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_newActionPerformed

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

    private void txt_kpg_lpKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_kpg_lpKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_kpg_lpKeyTyped

    private void txt_ctk_mkKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_ctk_mkKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_ctk_mkKeyTyped

    private void txt_ctk_pchKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_ctk_pchKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_ctk_pchKeyTyped

    private void button_aktifActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_aktifActionPerformed
        // TODO add your handling code here:
        try {
            int j = tabel_data_bulu_upah.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Pilih data yang akan di edit !");
            } else {
                sql = "UPDATE `tb_tarif_cabut` SET "
                        + "`status`='AKTIF'"
                        + "WHERE `bulu_upah`='" + tabel_data_bulu_upah.getValueAt(j, 0).toString() + "'";
                if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                    JOptionPane.showMessageDialog(this, "Set Aktif Successfully");
                    refresh_tarif_cabut();
                } else {
                    JOptionPane.showMessageDialog(this, "update failed");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JPanel_GradeBahanBaku.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_aktifActionPerformed

    private void button_non_aktifActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_non_aktifActionPerformed
        // TODO add your handling code here:
        try {
            int j = tabel_data_bulu_upah.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Pilih data yang akan di edit !");
            } else {
                sql = "UPDATE `tb_tarif_cabut` SET "
                        + "`status`='NON-AKTIF'"
                        + "WHERE `bulu_upah`='" + tabel_data_bulu_upah.getValueAt(j, 0).toString() + "'";
                if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                    JOptionPane.showMessageDialog(this, "Set Non-Aktif Successfully");
                    refresh_tarif_cabut();
                } else {
                    JOptionPane.showMessageDialog(this, "update failed");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JPanel_GradeBahanBaku.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_non_aktifActionPerformed

    private void button_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refreshActionPerformed
        // TODO add your handling code here:
        refresh_tarif_cabut();
    }//GEN-LAST:event_button_refreshActionPerformed

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

    private void txt_kpg_lp_flatKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_kpg_lp_flatKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_kpg_lp_flatKeyTyped

    private void txt_kpg_lp_jidunKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_kpg_lp_jidunKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_kpg_lp_jidunKeyTyped

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

    private void txt_gram_lp_bp_subKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_gram_lp_bp_subKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_gram_lp_bp_subKeyTyped

    private void txt_bonus_mk_utuh_cbtKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_bonus_mk_utuh_cbtKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_bonus_mk_utuh_cbtKeyTyped

    private void txt_bonus_mk_utuh_ctkKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_bonus_mk_utuh_ctkKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_bonus_mk_utuh_ctkKeyTyped

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
    public static javax.swing.JButton button_aktif;
    private javax.swing.JButton button_clear;
    private javax.swing.JButton button_delete;
    private javax.swing.JButton button_edit;
    private javax.swing.JButton button_new;
    public static javax.swing.JButton button_non_aktif;
    public static javax.swing.JButton button_refresh;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable tabel_data_bulu_upah;
    private javax.swing.JTextField txt_bonus_mk_utuh_cbt;
    private javax.swing.JTextField txt_bonus_mk_utuh_cbt_sub;
    private javax.swing.JTextField txt_bonus_mk_utuh_ctk;
    private javax.swing.JTextField txt_bonus_mk_utuh_ctk_sub;
    private javax.swing.JTextField txt_bulu_upah;
    private javax.swing.JTextField txt_ctk_flat;
    private javax.swing.JTextField txt_ctk_jdn;
    private javax.swing.JTextField txt_ctk_mk;
    private javax.swing.JTextField txt_ctk_pch;
    private javax.swing.JTextField txt_gram_lp_bp_sub;
    private javax.swing.JTextField txt_kpg_lp;
    private javax.swing.JTextField txt_kpg_lp_flat;
    private javax.swing.JTextField txt_kpg_lp_jidun;
    private javax.swing.JTextField txt_tarif_lp_sapon;
    private javax.swing.JTextField txt_tarif_lp_sesekan;
    private javax.swing.JTextField txt_upah_cabut;
    private javax.swing.JTextField txt_upah_sub;
    // End of variables declaration//GEN-END:variables
}
