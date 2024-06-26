package waleta_system.BahanBaku;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.Utility;

public class JDialog_PecahLP_InputPecah extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String no_grading;
    int keping_grading = 0, gram_grading = 0;
    double gram_per_keping = 8d;
    int total_keping_riil = 0;
    int total_keping_upah = 0;
    int total_gram_riil = 0;
    int total_gram_sistem = 0;

    public JDialog_PecahLP_InputPecah(java.awt.Frame parent, boolean modal, String no_grading, String no_kartu, String grade, int keping_grading, int gram_grading) {
        super(parent, modal);
        initComponents();
        decimalFormat.setMaximumFractionDigits(5);

        this.no_grading = no_grading;
        this.keping_grading = keping_grading;
        this.gram_grading = gram_grading;

        if (keping_grading > 0) {
            gram_per_keping = (double) gram_grading / (double) keping_grading;
            txt_keping_riil.setEnabled(true);
        } else {
            txt_keping_riil.setEnabled(false);
            txt_keping_riil.setText("0");
        }

        label_no_grading.setText(no_grading);
        label_no_kartu.setText(no_kartu);
        label_grade.setText(grade);
        label_keping_grading.setText(decimalFormat.format(keping_grading));
        label_gram_grading.setText(decimalFormat.format(gram_grading));
        label_gram_per_keping.setText(decimalFormat.format(gram_per_keping));

        try {
            ComboBox_buluUpah.removeAllItems();
            sql = "SELECT `bulu_upah` FROM `tb_tarif_cabut` WHERE `status` = 'AKTIF'";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_buluUpah.addItem(rs.getString("bulu_upah"));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_Edit_PecahLP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void hitung_berat_sistem() {
        int nomor_baris = 0;
        total_keping_riil = 0;
        total_keping_upah = 0;
        total_gram_riil = 0;
        total_gram_sistem = 0;
        int selisih_gram_1 = 0;
        int jumlah_pecahan = Table_pecah_lp.getRowCount();

        for (int i = 0; i < jumlah_pecahan; i++) {
            nomor_baris++;
            Table_pecah_lp.setValueAt(nomor_baris, i, 0);
            total_keping_riil += (int) Table_pecah_lp.getValueAt(i, 1);
            total_keping_upah += (int) Table_pecah_lp.getValueAt(i, 2);
            total_gram_riil += (int) Table_pecah_lp.getValueAt(i, 3);
            total_gram_sistem += (int) Table_pecah_lp.getValueAt(i, 4);

            int gram_riil = (int) Table_pecah_lp.getValueAt(i, 3);
            Table_pecah_lp.setValueAt(gram_riil, i, 4);
        }

        selisih_gram_1 = gram_grading - total_gram_riil;
        int selisih_gram_1_abs = Math.abs(selisih_gram_1);
        System.out.println("selisih_gram_1 : " + selisih_gram_1);

        if (selisih_gram_1 > jumlah_pecahan) {
            int selisih_gram_2 = 0;
            int total_gram_sistem_2 = 0;
            for (int i = 0; i < jumlah_pecahan; i++) {
                int gram_riil = (int) Table_pecah_lp.getValueAt(i, 3);
                double persentase_dari_gram_grading = (double) gram_riil / (double) total_gram_riil;
                int gram_sistem = gram_riil + (int) Math.floor((double) selisih_gram_1 * persentase_dari_gram_grading);
                Table_pecah_lp.setValueAt(gram_sistem, i, 4);
                total_gram_sistem_2 += gram_sistem;
            }
            selisih_gram_2 = gram_grading - total_gram_sistem_2;
            System.out.println("selisih_gram_2 : " + selisih_gram_2);
            if (jumlah_pecahan >= selisih_gram_2) {
                for (int i = 0; i < selisih_gram_2; i++) {
                    int gram_sistem = (int) Table_pecah_lp.getValueAt(i, 4);
                    Table_pecah_lp.setValueAt(gram_sistem + 1, i, 4);
                }
            } else {
                JOptionPane.showMessageDialog(this, "jumlah_pecahan " + jumlah_pecahan + " < selisih_gram_2 " + selisih_gram_2);
            }
        } else if (selisih_gram_1 > 0) {
            for (int i = 0; i < selisih_gram_1; i++) {
                int gram_riil = (int) Table_pecah_lp.getValueAt(i, 3);
                Table_pecah_lp.setValueAt(gram_riil + 1, i, 4);
            }
        } else if (selisih_gram_1 == 0) {
            //Tidak ada selisih artinya semua sudah pas
        } else if (selisih_gram_1_abs > jumlah_pecahan) {
            int selisih_gram_2 = 0;
            int total_gram_sistem_2 = 0;
            for (int i = 0; i < jumlah_pecahan; i++) {
                int gram_riil = (int) Table_pecah_lp.getValueAt(i, 3);
                double persentase_dari_gram_grading = (double) gram_riil / (double) total_gram_riil;
                int gram_sistem = gram_riil - (int) Math.floor((double) selisih_gram_1_abs * persentase_dari_gram_grading);
                Table_pecah_lp.setValueAt(gram_sistem, i, 4);
                total_gram_sistem_2 += gram_sistem;
            }
            selisih_gram_2 = gram_grading - total_gram_sistem_2;
            int selisih_gram_2_abs = Math.abs(selisih_gram_2);
            System.out.println("selisih_gram_2_abs : " + selisih_gram_2_abs);
            if (jumlah_pecahan >= selisih_gram_2_abs) {
                for (int i = 0; i < selisih_gram_2_abs; i++) {
                    int gram_sistem = (int) Table_pecah_lp.getValueAt(i, 4);
                    Table_pecah_lp.setValueAt(gram_sistem - 1, i, 4);
                }
            } else {
                JOptionPane.showMessageDialog(this, "jumlah_pecahan " + jumlah_pecahan + " < selisih_gram_2_abs " + selisih_gram_2_abs);
            }
        } else if (selisih_gram_1_abs > 0) {
            for (int i = 0; i < selisih_gram_1_abs; i++) {
                int gram_riil = (int) Table_pecah_lp.getValueAt(i, 3);
                Table_pecah_lp.setValueAt(gram_riil - 1, i, 4);
            }
        }

        total_gram_sistem = 0;
        for (int i = 0; i < jumlah_pecahan; i++) {
            total_gram_sistem += (int) Table_pecah_lp.getValueAt(i, 4);
        }

        label_total_keping_riil.setText(decimalFormat.format(total_keping_riil));
        label_total_keping_upah.setText(decimalFormat.format(total_keping_upah));
        label_total_gram_riil.setText(decimalFormat.format(total_gram_riil));
        label_total_gram_sistem.setText(decimalFormat.format(total_gram_sistem));

        Table_pecah_lp.setDefaultRenderer(Float.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                double selisih_berat_per_keping = (double) Table_pecah_lp.getValueAt(row, 6) - gram_per_keping;
                if (Math.abs(selisih_berat_per_keping) > 1) {
                    if (isSelected) {
                        comp.setBackground(Table_pecah_lp.getSelectionBackground());
                        comp.setForeground(Table_pecah_lp.getSelectionForeground());
                    } else {
                        comp.setBackground(Color.red);
                        comp.setForeground(Color.WHITE);
                    }
                } else {
                    if (isSelected) {
                        comp.setBackground(Table_pecah_lp.getSelectionBackground());
                        comp.setForeground(Table_pecah_lp.getSelectionForeground());
                    } else {
                        comp.setBackground(Color.cyan);
                        comp.setForeground(Color.BLACK);
                    }
                }
                return comp;
            }
        });
        Table_pecah_lp.repaint();
    }

    private void tambah_baris() {
        boolean check = true;
        if (txt_gram_riil.getText() == null || txt_gram_riil.getText().equals("") || txt_gram_riil.getText().equals("0")) {
            JOptionPane.showMessageDialog(this, "Gram Riil tidak boleh kosong / 0!");
            check = false;
        }

        if (txt_keping_riil.getText() == null || txt_keping_riil.getText().equals("")) {
            txt_keping_riil.setText("0");
        }

        if (check) {
            int keping_riil = Integer.valueOf(txt_keping_riil.getText());
            int gram_riil = Integer.valueOf(txt_gram_riil.getText());
            int keping_upah = 0;
            int gram_sistem = gram_riil;
            double gram_per_keping_riil = 0;
            if (keping_riil > 0) {
                keping_upah = keping_riil;
                gram_per_keping_riil = (double) gram_riil / (double) keping_riil;
            } else {
                keping_upah = (int) Math.floor((double) gram_riil / 8d);
                keping_upah = keping_upah == 0 ? 1 : keping_upah;
                gram_per_keping_riil = 8d;
            }
            String BuluUpah = ComboBox_buluUpah.getSelectedItem().toString();
            DefaultTableModel model = (DefaultTableModel) Table_pecah_lp.getModel();
            Object[] row = new Object[10];
            row[0] = 0;
            row[1] = keping_riil;
            row[2] = keping_upah;
            row[3] = gram_riil;
            row[4] = gram_sistem;
            row[5] = BuluUpah;
            row[6] = gram_per_keping_riil;
            model.addRow(row);
            hitung_berat_sistem();

            if (txt_keping_riil.isEnabled()) {
                txt_keping_riil.setText("");
                txt_gram_riil.setText("");
                txt_keping_riil.requestFocus();
            } else {
                txt_gram_riil.setText("");
                txt_gram_riil.requestFocus();
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
        button_simpan = new javax.swing.JButton();
        label_no_kartu = new javax.swing.JLabel();
        button_batal = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        label_grade = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        label_no_grading = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        label_keping_grading = new javax.swing.JLabel();
        label_gram_grading = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        label_total_keping_riil = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Table_pecah_lp = new javax.swing.JTable();
        label_total_keping_upah = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        label_total_gram_riil = new javax.swing.JLabel();
        label_total_gram_sistem = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        txt_keping_riil = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        txt_gram_riil = new javax.swing.JTextField();
        button_tambah = new javax.swing.JButton();
        button_hapus = new javax.swing.JButton();
        jLabel33 = new javax.swing.JLabel();
        ComboBox_buluUpah = new javax.swing.JComboBox<>();
        jLabel34 = new javax.swing.JLabel();
        label_gram_per_keping = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel35 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        button_simpan.setBackground(new java.awt.Color(255, 255, 255));
        button_simpan.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        button_simpan.setText("Simpan");
        button_simpan.setFocusable(false);
        button_simpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_simpanActionPerformed(evt);
            }
        });

        label_no_kartu.setBackground(new java.awt.Color(255, 255, 255));
        label_no_kartu.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_no_kartu.setForeground(new java.awt.Color(255, 0, 0));
        label_no_kartu.setText("No Kartu");
        label_no_kartu.setFocusable(false);

        button_batal.setBackground(new java.awt.Color(255, 255, 255));
        button_batal.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        button_batal.setText("Batal");
        button_batal.setFocusable(false);
        button_batal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_batalActionPerformed(evt);
            }
        });

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel20.setText("Pecah LP");
        jLabel20.setFocusable(false);

        label_grade.setBackground(new java.awt.Color(255, 255, 255));
        label_grade.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_grade.setForeground(new java.awt.Color(255, 0, 0));
        label_grade.setText("Grade");
        label_grade.setFocusable(false);

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel22.setText("No Grading :");
        jLabel22.setFocusable(false);

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel23.setText("No Kartu :");
        jLabel23.setFocusable(false);

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel24.setText("Grade :");
        jLabel24.setFocusable(false);

        label_no_grading.setBackground(new java.awt.Color(255, 255, 255));
        label_no_grading.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_no_grading.setForeground(new java.awt.Color(255, 0, 0));
        label_no_grading.setText("No Grading");
        label_no_grading.setFocusable(false);

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel25.setText("Keping :");
        jLabel25.setFocusable(false);

        label_keping_grading.setBackground(new java.awt.Color(255, 255, 255));
        label_keping_grading.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_keping_grading.setForeground(new java.awt.Color(255, 0, 0));
        label_keping_grading.setText("Keping");
        label_keping_grading.setFocusable(false);

        label_gram_grading.setBackground(new java.awt.Color(255, 255, 255));
        label_gram_grading.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_gram_grading.setForeground(new java.awt.Color(255, 0, 0));
        label_gram_grading.setText("Gram");
        label_gram_grading.setFocusable(false);

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel26.setText("Gram :");
        jLabel26.setFocusable(false);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setFocusable(false);

        jLabel27.setBackground(new java.awt.Color(255, 255, 255));
        jLabel27.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel27.setText("Total Keping Riil :");
        jLabel27.setFocusable(false);

        label_total_keping_riil.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_riil.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_keping_riil.setForeground(new java.awt.Color(255, 0, 0));
        label_total_keping_riil.setText("0000");
        label_total_keping_riil.setFocusable(false);

        Table_pecah_lp.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Table_pecah_lp.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Keping Riil", "Keping Upah", "Gram Riil", "Gram Sistem", "Jenis Bulu", "Gram/Kpg"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.Float.class
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
        Table_pecah_lp.setFocusable(false);
        Table_pecah_lp.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(Table_pecah_lp);

        label_total_keping_upah.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_upah.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_keping_upah.setForeground(new java.awt.Color(255, 0, 0));
        label_total_keping_upah.setText("0000");
        label_total_keping_upah.setFocusable(false);

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel28.setText("Total Keping Upah :");
        jLabel28.setFocusable(false);

        jLabel29.setBackground(new java.awt.Color(255, 255, 255));
        jLabel29.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel29.setText("Total Gram Riil :");
        jLabel29.setFocusable(false);

        label_total_gram_riil.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_riil.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_gram_riil.setForeground(new java.awt.Color(255, 0, 0));
        label_total_gram_riil.setText("0000");
        label_total_gram_riil.setFocusable(false);

        label_total_gram_sistem.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_sistem.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_gram_sistem.setForeground(new java.awt.Color(255, 0, 0));
        label_total_gram_sistem.setText("0000");
        label_total_gram_sistem.setFocusable(false);

        jLabel30.setBackground(new java.awt.Color(255, 255, 255));
        jLabel30.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel30.setText("Total Gram Sistem :");
        jLabel30.setFocusable(false);

        jLabel31.setBackground(new java.awt.Color(255, 255, 255));
        jLabel31.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel31.setText("Keping Riil :");
        jLabel31.setFocusable(false);

        txt_keping_riil.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_keping_riil.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_keping_riilKeyTyped(evt);
            }
        });

        jLabel32.setBackground(new java.awt.Color(255, 255, 255));
        jLabel32.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel32.setText("Gram Riil :");
        jLabel32.setFocusable(false);

        txt_gram_riil.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_gram_riil.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_gram_riilKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_gram_riilKeyTyped(evt);
            }
        });

        button_tambah.setBackground(new java.awt.Color(255, 255, 255));
        button_tambah.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_tambah.setText("+ Baris");
        button_tambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_tambahActionPerformed(evt);
            }
        });

        button_hapus.setBackground(new java.awt.Color(255, 255, 255));
        button_hapus.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_hapus.setText("Hapus");
        button_hapus.setFocusable(false);
        button_hapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_hapusActionPerformed(evt);
            }
        });

        jLabel33.setBackground(new java.awt.Color(255, 255, 255));
        jLabel33.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel33.setText("Bulu Upah :");
        jLabel33.setFocusable(false);

        ComboBox_buluUpah.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        ComboBox_buluUpah.setFocusable(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel27)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_keping_riil))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel28)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_keping_upah))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel29)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_riil))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel31)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_keping_riil, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel32)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_gram_riil, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_tambah)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_hapus)))
                        .addGap(0, 126, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel30)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_sistem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel33)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_buluUpah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_keping_riil, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_keping_upah, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram_riil, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_gram_sistem, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(txt_gram_riil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_keping_riil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_buluUpah, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jLabel34.setBackground(new java.awt.Color(255, 255, 255));
        jLabel34.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel34.setText("~Rata2 Gram / Keping :");
        jLabel34.setFocusable(false);

        label_gram_per_keping.setBackground(new java.awt.Color(255, 255, 255));
        label_gram_per_keping.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_gram_per_keping.setForeground(new java.awt.Color(255, 0, 0));
        label_gram_per_keping.setText("0");
        label_gram_per_keping.setFocusable(false);

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("1. Jika Selisih total gram riil dengan gram grading di sistem <= jumlah pecahan :\n- Selisihnya di bagikan 1 per 1 mulai dari pecahan paling atas.\n- Jika Selisih bernilai Positif, maka setiap pecahan ditambah +1 mulai dari atas sesuai jumlah selisihnya.\n- Jika Selisih bernilai Negatif, maka setiap pecahan dikurang -1 mulai dari atas sesuai jumlah selisihnya.\n2. Jika Selisih total gram riil dengan gram grading di sistem > jumlah pecahan :\n- Selisih total gram riil dengan gram grading di sistem dibagikan ke semua pecahan secara proporsional, \ndengan pembulatan kebawah.\n- Karena pembulatan kebawah, maka akan masih ada selisih yang tersisa, sisa selisih tersebut akan dibagikan 1 per 1 mulai dari pecahan paling atas. sama dengan aturan poin no 1.");
        jScrollPane1.setViewportView(jTextArea1);

        jLabel35.setBackground(new java.awt.Color(255, 255, 255));
        jLabel35.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel35.setText("Cara Hitung Gram Sistem :");
        jLabel35.setFocusable(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel22)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_no_grading))
                            .addComponent(jLabel20)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel23)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_no_kartu)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel24)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_grade))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel25)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_keping_grading)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel26)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_gram_grading)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel34)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_gram_per_keping)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel35)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_batal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_simpan)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_no_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_no_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_keping_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_gram_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_gram_per_keping, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_simpan)
                    .addComponent(button_batal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    private void button_simpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_simpanActionPerformed
        // TODO add your handling code here:
        boolean check = true;
        if (keping_grading != total_keping_riil) {
            JOptionPane.showMessageDialog(this, "Total jumlah keping pecahan harus sama dengan total keping grading!");
            check = false;
        }
        if (check) {
            try {
                Utility.db.getConnection().setAutoCommit(false);
                String INSERT_Query = "INSERT INTO `tb_bahan_baku_pecah_kartu`(`no_grading`, `jumlah_keping`, `keping_upah`, `berat_riil`, `berat_basah`, `jenis_bulu_lp`) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement pst2 = Utility.db.getConnection().prepareStatement(INSERT_Query);
                for (int i = 0; i < Table_pecah_lp.getRowCount(); i++) {
                    pst2.setString(1, no_grading);
                    pst2.setString(2, Table_pecah_lp.getValueAt(i, 1).toString());
                    pst2.setString(3, Table_pecah_lp.getValueAt(i, 2).toString());
                    pst2.setString(4, Table_pecah_lp.getValueAt(i, 3).toString());
                    pst2.setString(5, Table_pecah_lp.getValueAt(i, 4).toString());
                    pst2.setString(6, Table_pecah_lp.getValueAt(i, 5).toString());
                    pst2.executeUpdate();
                }

                Utility.db.getConnection().commit();
                JOptionPane.showMessageDialog(this, "Sukses membuat pecah LP!!");
                this.dispose();
            } catch (Exception ex) {
                try {
                    Utility.db.getConnection().rollback();
                } catch (SQLException ex1) {
                    Logger.getLogger(JDialog_PecahLP_InputPecah.class.getName()).log(Level.SEVERE, null, ex1);
                }
                JOptionPane.showMessageDialog(this, "Membuat pecah LP gagal : \n" + ex);
                Logger.getLogger(JDialog_PecahLP_InputPecah.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    Utility.db.getConnection().setAutoCommit(true);
                } catch (SQLException ex) {
                    Logger.getLogger(JDialog_PecahLP_InputPecah.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_button_simpanActionPerformed

    private void button_batalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_batalActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_button_batalActionPerformed

    private void button_tambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_tambahActionPerformed
        // TODO add your handling code here:
        tambah_baris();
    }//GEN-LAST:event_button_tambahActionPerformed

    private void button_hapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_hapusActionPerformed
        // TODO add your handling code here:
        int i = Table_pecah_lp.getSelectedRow();
        if (i > -1) {
            DefaultTableModel model = (DefaultTableModel) Table_pecah_lp.getModel();
            model.removeRow(i);
//            ColumnsAutoSizer.sizeColumnsToFit(Table_pecah_lp);
            hitung_berat_sistem();
        }
    }//GEN-LAST:event_button_hapusActionPerformed

    private void txt_gram_riilKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_gram_riilKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tambah_baris();
        }
    }//GEN-LAST:event_txt_gram_riilKeyPressed

    private void txt_keping_riilKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_keping_riilKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_keping_riilKeyTyped

    private void txt_gram_riilKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_gram_riilKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_gram_riilKeyTyped


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_buluUpah;
    private javax.swing.JTable Table_pecah_lp;
    public javax.swing.JButton button_batal;
    public javax.swing.JButton button_hapus;
    public javax.swing.JButton button_simpan;
    public javax.swing.JButton button_tambah;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel label_grade;
    private javax.swing.JLabel label_gram_grading;
    private javax.swing.JLabel label_gram_per_keping;
    private javax.swing.JLabel label_keping_grading;
    private javax.swing.JLabel label_no_grading;
    private javax.swing.JLabel label_no_kartu;
    private javax.swing.JLabel label_total_gram_riil;
    private javax.swing.JLabel label_total_gram_sistem;
    private javax.swing.JLabel label_total_keping_riil;
    private javax.swing.JLabel label_total_keping_upah;
    private javax.swing.JTextField txt_gram_riil;
    private javax.swing.JTextField txt_keping_riil;
    // End of variables declaration//GEN-END:variables
}
