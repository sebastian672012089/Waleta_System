package waleta_system.BahanJadi;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;
import waleta_system.Packing.JPanel_DataPacking;

public class JDialog_Setor_Packing extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    String grade, kode_repacking;
    float total_keping_asal = 0, tot_keping = 0;
    float total_gram_asal = 0, tot_gram = 0;
    ArrayList<String> no_grade_spk = new ArrayList<>();

    public JDialog_Setor_Packing(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        button_hapus.setIcon(Utility.ResizeImageIcon(new javax.swing.ImageIcon(getClass().getResource("/waleta_system/Images/delete-icon.png")), button_hapus.getWidth(), button_hapus.getHeight()));
        try {
            this.setResizable(false);

            Load_KodeSPK();
            if (ComboBox_kodeSPK.getItemCount() > 0) {
                Load_GradeSPK(ComboBox_kodeSPK.getItemAt(0));
            }
            ComboBox_kodeSPK.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Load_GradeSPK(ComboBox_kodeSPK.getSelectedItem().toString());
                }
            });
            refresh_TabelDaftarBox();
        } catch (Exception ex) {
            Logger.getLogger(JDialog_Setor_Packing.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Load_KodeSPK() {
        try {
            ComboBox_kodeSPK.removeAllItems();
            String Query = "SELECT `tb_spk`.`kode_spk` FROM `tb_spk` LEFT JOIN `tb_pengiriman` ON `tb_spk`.`kode_spk` = `tb_pengiriman`.`kode_spk`"
                    + "WHERE `status` = 'PROSES' OR (`status` = 'SEND OUT' AND `tb_pengiriman`.`status_pengiriman` = 'PROSES')";
            ResultSet result = Utility.db.getStatement().executeQuery(Query);
            while (result.next()) {
                ComboBox_kodeSPK.addItem(result.getString("kode_spk"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(JDialog_Setor_Packing.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Load_GradeSPK(String kode_SPK) {
        try {
            no_grade_spk.clear();
            ComboBox_GradeSPK.removeAllItems();
            String Query = "SELECT `no`, `grade_waleta`, `grade_buyer`, `berat_kemasan`, `jumlah_kemasan`, `berat`, `kode_spk` "
                    + "FROM `tb_spk_detail` WHERE `kode_spk` = '" + kode_SPK + "'";
            ResultSet result = Utility.db.getStatement().executeQuery(Query);
            while (result.next()) {
                no_grade_spk.add(result.getString("no"));
                ComboBox_GradeSPK.addItem(result.getString("grade_waleta") + "-" + result.getString("grade_buyer") + "-" + decimalFormat.format(result.getFloat("berat_kemasan")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(JDialog_Setor_Packing.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refresh_TabelDaftarBox() {
        try {
            tot_keping = 0;
            tot_gram = 0;
            DefaultTableModel model = (DefaultTableModel) Table_daftarBox.getModel();
            model.setRowCount(0);
            sql = "SELECT * FROM `tb_box_bahan_jadi` LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`"
                    + "WHERE `no_box` LIKE '%" + txt_search_box.getText() + "%' "
                    + "AND `no_tutupan` LIKE '%" + txt_search_tutupan.getText() + "%' "
                    + "AND `lokasi_terakhir` = 'GRADING' "
                    + "AND `berat` > 0";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString("no_box"), rs.getString("kode_grade"), new SimpleDateFormat("dd MMM yyyy").format(rs.getDate("tanggal_box")), rs.getInt("keping"), rs.getFloat("berat"), rs.getString("status_terakhir"), rs.getString("no_tutupan"), false});
                tot_keping = tot_keping + rs.getInt("keping");
                tot_gram = tot_gram + rs.getFloat("berat");
            }
            label_total_daftarBox.setText(Integer.toString(Table_daftarBox.getRowCount()));
            label_total_keping_daftarBox.setText(decimalFormat.format(tot_keping));
            label_total_gram_daftarBox.setText(decimalFormat.format(tot_gram));
            ColumnsAutoSizer.sizeColumnsToFit(Table_daftarBox);
        } catch (SQLException ex) {
            Logger.getLogger(JDialog_Setor_Packing.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void count_asal() {
        tot_keping = 0;
        tot_gram = 0;
        int x = Table_setor.getRowCount();
        for (int i = 0; i < x; i++) {
            try {
                tot_keping = tot_keping + Integer.valueOf(Table_setor.getValueAt(i, 3).toString());
                tot_gram = tot_gram + Float.valueOf(Table_setor.getValueAt(i, 4).toString());
            } catch (NullPointerException e) {
                JOptionPane.showMessageDialog(this, "Tidak Boleh ada data yang kosong !");
            }
        }
        label_total_boxSetor.setText(Integer.toString(x));
        label_total_keping_setor.setText(decimalFormat.format(tot_keping));
        label_total_gram_setor.setText(decimalFormat.format(tot_gram));
        ColumnsAutoSizer.sizeColumnsToFit(Table_setor);
    }

    public boolean CheckDuplicateBoxAsal(String no_box) {
        int i = Table_setor.getRowCount();
        for (int j = 0; j < i; j++) {
            if (no_box.equals(Table_setor.getValueAt(j, 0).toString())) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        label_keterangan = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        Table_daftarBox = new javax.swing.JTable();
        jLabel6 = new javax.swing.JLabel();
        label_total_daftarBox = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        label_total_keping_daftarBox = new javax.swing.JLabel();
        label_total_keping_setor = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        label_total_gram_daftarBox = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_setor = new javax.swing.JTable();
        label_total_gram_setor = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        Date_setor = new com.toedter.calendar.JDateChooser();
        jLabel1 = new javax.swing.JLabel();
        label_total_boxSetor = new javax.swing.JLabel();
        txt_search_box = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        button_setor = new javax.swing.JButton();
        button_hapus = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        ComboBox_kodeSPK = new javax.swing.JComboBox<>();
        jLabel22 = new javax.swing.JLabel();
        ComboBox_GradeSPK = new javax.swing.JComboBox<>();
        CheckBox_all = new javax.swing.JCheckBox();
        Button_pindah = new javax.swing.JButton();
        jLabel23 = new javax.swing.JLabel();
        txt_search_tutupan = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Bagian Grading Bahan Jadi");
        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        label_keterangan.setBackground(new java.awt.Color(255, 255, 255));
        label_keterangan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_keterangan.setForeground(new java.awt.Color(102, 102, 102));
        label_keterangan.setText("*Press ENTER to insert");

        Table_daftarBox.setAutoCreateRowSorter(true);
        Table_daftarBox.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_daftarBox.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Box", "Grade", "Tgl Box", "Kpg", "Gram", "Status", "Tutupan", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_daftarBox.setFocusable(false);
        Table_daftarBox.getTableHeader().setReorderingAllowed(false);
        Table_daftarBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Table_daftarBoxMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(Table_daftarBox);
        if (Table_daftarBox.getColumnModel().getColumnCount() > 0) {
            Table_daftarBox.getColumnModel().getColumn(7).setMinWidth(30);
            Table_daftarBox.getColumnModel().getColumn(7).setMaxWidth(30);
        }

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Total :");

        label_total_daftarBox.setBackground(new java.awt.Color(255, 255, 255));
        label_total_daftarBox.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_daftarBox.setText("0");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Keping :");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("Keping :");

        label_total_keping_daftarBox.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_daftarBox.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_keping_daftarBox.setText("0");

        label_total_keping_setor.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_setor.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_keping_setor.setText("0");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Gram :");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Gram :");

        label_total_gram_daftarBox.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_daftarBox.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_gram_daftarBox.setText("0");

        Table_setor.setAutoCreateRowSorter(true);
        Table_setor.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_setor.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Box", "Grade", "Tgl Box", "Kpg", "Gram", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Float.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_setor.setFocusable(false);
        Table_setor.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Table_setor);

        label_total_gram_setor.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_setor.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_gram_setor.setText("0");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Daftar Box yang akan di setor");

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel19.setText("Setor Box ke Packing");

        Date_setor.setBackground(new java.awt.Color(255, 255, 255));
        Date_setor.setDate(new Date());
        Date_setor.setDateFormatString("dd MMMM yyyy");
        Date_setor.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_setor.setMaxSelectableDate(new Date());

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Total :");

        label_total_boxSetor.setBackground(new java.awt.Color(255, 255, 255));
        label_total_boxSetor.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_boxSetor.setText("0");

        txt_search_box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_box.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_boxKeyPressed(evt);
            }
        });

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel18.setText("No Box :");

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel20.setText("Tanggal Setor :");

        button_setor.setBackground(new java.awt.Color(255, 255, 255));
        button_setor.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_setor.setText("Setor");
        button_setor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_setorActionPerformed(evt);
            }
        });

        button_hapus.setBackground(new java.awt.Color(255, 255, 255));
        button_hapus.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_hapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_hapusActionPerformed(evt);
            }
        });

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel21.setText("Untuk kode SPK :");

        ComboBox_kodeSPK.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        ComboBox_kodeSPK.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Kode SPK" }));

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel22.setText("Grade :");

        ComboBox_GradeSPK.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        ComboBox_GradeSPK.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Grade SPK" }));

        CheckBox_all.setBackground(new java.awt.Color(255, 255, 255));
        CheckBox_all.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        CheckBox_all.setText("All");
        CheckBox_all.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CheckBox_allActionPerformed(evt);
            }
        });

        Button_pindah.setBackground(new java.awt.Color(255, 255, 255));
        Button_pindah.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Button_pindah.setText("Pindah");
        Button_pindah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_pindahActionPerformed(evt);
            }
        });

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel23.setText("Tutupan :");

        txt_search_tutupan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_tutupan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_tutupanKeyPressed(evt);
            }
        });

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
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_box, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_keterangan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel23)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_tutupan, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Button_pindah)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(CheckBox_all))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_daftarBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_keping_daftarBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_daftarBox)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(button_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_boxSetor)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_keping_setor)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_setor)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(button_setor))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel19)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_setor, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_kodeSPK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel22)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_GradeSPK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_setor, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ComboBox_GradeSPK, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ComboBox_kodeSPK, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txt_search_box, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_keterangan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(CheckBox_all, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Button_pindah, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_tutupan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(button_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 451, Short.MAX_VALUE)
                    .addComponent(jScrollPane3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_total_keping_daftarBox, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_keping_setor, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram_daftarBox, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_setor, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram_setor, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_daftarBox, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_boxSetor, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    private void Table_daftarBoxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Table_daftarBoxMouseClicked
        // TODO add your handling code here:
        int x = Table_daftarBox.getSelectedRow();
        if (evt.getClickCount() == 2) {
            if (CheckDuplicateBoxAsal(Table_daftarBox.getValueAt(x, 0).toString())) {
                JOptionPane.showMessageDialog(this, "Box " + Table_daftarBox.getValueAt(x, 0).toString() + " sudah masuk");
            } else {
                DefaultTableModel model = (DefaultTableModel) Table_setor.getModel();
                model.addRow(new Object[]{Table_daftarBox.getValueAt(x, 0),
                    Table_daftarBox.getValueAt(x, 1),
                    Table_daftarBox.getValueAt(x, 2),
                    Table_daftarBox.getValueAt(x, 3),
                    Table_daftarBox.getValueAt(x, 4),
                    Table_daftarBox.getValueAt(x, 5)});
                count_asal();
            }
        }
    }//GEN-LAST:event_Table_daftarBoxMouseClicked

    private void txt_search_boxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_boxKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refresh_TabelDaftarBox();
            if (Table_daftarBox.getRowCount() == 1) {
                if (CheckDuplicateBoxAsal(Table_daftarBox.getValueAt(0, 0).toString())) {
                    JOptionPane.showMessageDialog(this, "Box " + Table_daftarBox.getValueAt(0, 0).toString() + " sudah masuk");
                } else {
                    DefaultTableModel model = (DefaultTableModel) Table_setor.getModel();
                    model.addRow(new Object[]{Table_daftarBox.getValueAt(0, 0),
                        Table_daftarBox.getValueAt(0, 1),
                        Table_daftarBox.getValueAt(0, 2),
                        Table_daftarBox.getValueAt(0, 3),
                        Table_daftarBox.getValueAt(0, 4),
                        Table_daftarBox.getValueAt(0, 5)});
                    ColumnsAutoSizer.sizeColumnsToFit(Table_setor);
                    label_keterangan.setVisible(true);
                    label_keterangan.setForeground(Color.green);
                    label_keterangan.setText("Inserted !");
                    txt_search_box.setText("");
                    txt_search_box.requestFocus();
                    count_asal();
                }
            } else if (Table_daftarBox.getRowCount() > 0) {
                label_keterangan.setVisible(true);
                label_keterangan.setForeground(Color.red);
                label_keterangan.setText("Multiple data selected !");
            } else {
                label_keterangan.setVisible(true);
                label_keterangan.setForeground(Color.red);
                label_keterangan.setText("No Data !");
            }
        }
    }//GEN-LAST:event_txt_search_boxKeyPressed

    private void button_hapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_hapusActionPerformed
        // TODO add your handling code here:
        int i = Table_setor.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) Table_setor.getModel();
        if (i != -1) {
            model.removeRow(Table_setor.getRowSorter().convertRowIndexToModel(i));
        }
        ColumnsAutoSizer.sizeColumnsToFit(Table_setor);
        count_asal();
    }//GEN-LAST:event_button_hapusActionPerformed

    private void button_setorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_setorActionPerformed
        // TODO add your handling code here:
        try {
            Utility.db.getConnection().setAutoCommit(false);
            String no_box = "'" + Table_setor.getValueAt(0, 0).toString() + "'";
            for (int i = 0; i < Table_setor.getRowCount(); i++) {
                if (i > 0) {
                    no_box = no_box + ", '" + Table_setor.getValueAt(i, 0).toString() + "'";
                }
                String update = "UPDATE `tb_box_bahan_jadi` SET "
                        + "`status_terakhir`='Setor ke Packing',"
                        + "`lokasi_terakhir`='PACKING',"
                        + "`tgl_proses_terakhir`='" + dateFormat.format(Date_setor.getDate()) + "', "
                        + "`kode_rsb`=IF((SELECT SUBSTRING_INDEX(`kode_kh`, '-', 1) FROM `tb_spk_detail` WHERE `no` = " + no_grade_spk.get(ComboBox_GradeSPK.getSelectedIndex()) + ") IS NULL, "
                        + "`kode_rsb`, (SELECT SUBSTRING_INDEX(`kode_kh`, '-', 1) FROM `tb_spk_detail` WHERE `no` = " + no_grade_spk.get(ComboBox_GradeSPK.getSelectedIndex()) + ")), "
                        + "`kode_kh`=IF((SELECT `kode_kh` FROM `tb_spk_detail` WHERE `no` = " + no_grade_spk.get(ComboBox_GradeSPK.getSelectedIndex()) + ") IS NULL, "
                        + "`kode_kh`, (SELECT `kode_kh` FROM `tb_spk_detail` WHERE `no` = " + no_grade_spk.get(ComboBox_GradeSPK.getSelectedIndex()) + ")) "
                        + "WHERE `no_box` = '" + Table_setor.getValueAt(i, 0).toString() + "'";
                Utility.db.getConnection().createStatement();
                Utility.db.getStatement().executeUpdate(update);

                String insert = "INSERT INTO `tb_box_packing`(`no_box`, `tanggal_masuk`, `status`, `invoice_pengiriman`, `no_grade_spk`, `batch_number`) "
                        + "VALUES ('" + Table_setor.getValueAt(i, 0) + "',NULL, 'PENDING', NULL, '" + no_grade_spk.get(ComboBox_GradeSPK.getSelectedIndex()) + "',"
                        + "(SELECT CONCAT(SUBSTRING_INDEX(`kode_kh`, '-', 1), '-', DATE_FORMAT(`prod_date`, '%y%m%d')) AS 'batch_number' FROM `tb_spk_detail` WHERE `no` = '" + no_grade_spk.get(ComboBox_GradeSPK.getSelectedIndex()) + "'))";
                Utility.db.getConnection().createStatement();
                Utility.db.getStatement().executeUpdate(insert);
            }
            Utility.db.getConnection().commit();
            JOptionPane.showMessageDialog(this, "Setor ke Bagian Packing telah berhasil !");
            this.dispose();
            try {
                String query = "SELECT `tb_box_bahan_jadi`.`no_box`, `tanggal_box`, `keping`, `berat`, `kode_grade`, `tb_box_bahan_jadi`.`kode_rsb`, `batch_number`  "
                        + "FROM `tb_box_bahan_jadi`\n"
                        + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode` "
                        + "LEFT JOIN `tb_dokumen_kh` ON `tb_box_bahan_jadi`.`kode_kh` = `tb_dokumen_kh`.`kode_kh` \n"
                        + "LEFT JOIN `tb_box_packing` ON `tb_box_bahan_jadi`.`no_box` = `tb_box_packing`.`no_box` \n"
                        + "WHERE `tb_box_bahan_jadi`.`no_box` IN (" + no_box + ")";
                JRDesignQuery newQuery = new JRDesignQuery();
                newQuery.setText(query);
                JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Label_Box_QR_Packing.jrxml");
                JASP_DESIGN.setQuery(newQuery);
                JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                Map<String, Object> params = new HashMap<>();
                JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, params, Utility.db.getConnection());
                JasperViewer.viewReport(JASP_PRINT, false);
            } catch (JRException ex) {
                Logger.getLogger(JPanel_DataPacking.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException | NullPointerException e) {
            try {
                Utility.db.getConnection().rollback();
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_Setor_Packing.class.getName()).log(Level.SEVERE, null, ex);
            }
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JDialog_Setor_Packing.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                Utility.db.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_Setor_Packing.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_setorActionPerformed

    private void CheckBox_allActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CheckBox_allActionPerformed
        // TODO add your handling code here:
        if (CheckBox_all.isSelected()) {
            for (int i = 0; i < Table_daftarBox.getRowCount(); i++) {
                Table_daftarBox.setValueAt(true, i, 7);
            }
        } else {
            for (int i = 0; i < Table_daftarBox.getRowCount(); i++) {
                Table_daftarBox.setValueAt(false, i, 7);
            }
        }
    }//GEN-LAST:event_CheckBox_allActionPerformed

    private void txt_search_tutupanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_tutupanKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refresh_TabelDaftarBox();
        }
    }//GEN-LAST:event_txt_search_tutupanKeyPressed

    private void Button_pindahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_pindahActionPerformed
        // TODO add your handling code here:
        for (int i = 0; i < Table_daftarBox.getRowCount(); i++) {
            if (Table_daftarBox.getValueAt(i, 7).toString().equals("true")) {
                if (CheckDuplicateBoxAsal(Table_daftarBox.getValueAt(i, 0).toString())) {
                    JOptionPane.showMessageDialog(this, "Box " + Table_daftarBox.getValueAt(i, 0).toString() + " sudah masuk");
                } else {
                    DefaultTableModel model = (DefaultTableModel) Table_setor.getModel();
                    model.addRow(new Object[]{Table_daftarBox.getValueAt(i, 0),
                        Table_daftarBox.getValueAt(i, 1),
                        Table_daftarBox.getValueAt(i, 2),
                        Table_daftarBox.getValueAt(i, 3),
                        Table_daftarBox.getValueAt(i, 4),
                        Table_daftarBox.getValueAt(i, 5)});
                    ColumnsAutoSizer.sizeColumnsToFit(Table_setor);
                    count_asal();
                }
            }
        }

    }//GEN-LAST:event_Button_pindahActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JDialog_Setor_Packing.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JDialog_Setor_Packing dialog = new JDialog_Setor_Packing(new javax.swing.JFrame(), true);
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Button_pindah;
    private javax.swing.JCheckBox CheckBox_all;
    private javax.swing.JComboBox<String> ComboBox_GradeSPK;
    private javax.swing.JComboBox<String> ComboBox_kodeSPK;
    private com.toedter.calendar.JDateChooser Date_setor;
    private javax.swing.JTable Table_daftarBox;
    private javax.swing.JTable Table_setor;
    private javax.swing.JButton button_hapus;
    private javax.swing.JButton button_setor;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel label_keterangan;
    private javax.swing.JLabel label_total_boxSetor;
    private javax.swing.JLabel label_total_daftarBox;
    private javax.swing.JLabel label_total_gram_daftarBox;
    private javax.swing.JLabel label_total_gram_setor;
    private javax.swing.JLabel label_total_keping_daftarBox;
    private javax.swing.JLabel label_total_keping_setor;
    private javax.swing.JTextField txt_search_box;
    private javax.swing.JTextField txt_search_tutupan;
    // End of variables declaration//GEN-END:variables
}
