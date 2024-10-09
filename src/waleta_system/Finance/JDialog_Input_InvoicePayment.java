package waleta_system.Finance;

import java.awt.event.KeyEvent;
import waleta_system.Class.Utility;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import waleta_system.Packing.JDialog_ChooseBuyer;

public class JDialog_Input_InvoicePayment extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DecimalFormat decimalFormat = new DecimalFormat();
    double waleta_gr = 0, esta_gr = 0, jastip_gr = 0;
    double waleta = 0, esta = 0, jastip = 0, margin_jastip = 0;

    public JDialog_Input_InvoicePayment(java.awt.Frame parent, boolean modal, String invoice) {
        super(parent, modal);
        initComponents();
        this.setResizable(false);
        decimalFormat.setGroupingUsed(true);
        try {

            if (invoice != null) {
                txt_waleta_value.setEditable(true);
                txt_esta_value.setEditable(true);
                txt_jastip_value.setEditable(true);
                txt_margin_value.setEditable(true);
                txt_invoice.setEditable(false);
                txt_invoice.setText(invoice);
                sql = "SELECT `invoice`, `tb_buyer`.`nama`, `tb_buyer`.`kode_buyer`, `tb_buyer`.`kategori`, `tgl_invoice`, `awb_date`, `no_peb`,"
                        + " `term_payment`, `berat_waleta`, `berat_esta`, `berat_jastip`, "
                        + "`currency`, `value_waleta`, `value_esta`, `value_from_jtp`, `value_to_jtp`, `date_payment2`, `payment2`, `date_payment1`, `payment1`, `beneficiary_name_jastip`, `date_transfer_jastip1`, `transfer_jastip1`, `date_transfer_jastip2`, `transfer_jastip2` "
                        + "FROM `tb_payment_report` LEFT JOIN `tb_buyer` ON `tb_payment_report`.`buyer` = `tb_buyer`.`kode_buyer`\n"
                        + "WHERE `invoice` LIKE '%" + invoice + "%'";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    txt_buyer_code.setText(rs.getString("kode_buyer"));
                    txt_buyer_name.setText(rs.getString("nama"));
                    Date_invoice.setDate(rs.getDate("tgl_invoice"));
                    Date_AWB.setDate(rs.getDate("awb_date"));
                    txt_no_peb.setText(rs.getString("no_peb"));
                    ComboBox_TermPayment.setSelectedItem(rs.getString("term_payment"));
                    txt_waleta_gr.setText(rs.getString("berat_waleta"));
                    txt_esta_gr.setText(rs.getString("berat_esta"));
                    txt_jastip_gr.setText(rs.getString("berat_jastip"));
                    txt_total_gr.setText(Double.toString(rs.getDouble("berat_waleta") + rs.getDouble("berat_esta") + rs.getDouble("berat_jastip")));
                    ComboBox_Currency.setSelectedItem(rs.getString("currency"));
                    txt_waleta_value.setText(rs.getString("value_waleta"));
                    txt_esta_value.setText(rs.getString("value_esta"));
                    txt_jastip_value.setText(rs.getString("value_to_jtp"));
                    txt_margin_value.setText(rs.getString("value_from_jtp"));
                    txt_total_amount.setText(Double.toString(rs.getDouble("value_waleta") + rs.getDouble("value_esta") + rs.getDouble("value_to_jtp") + rs.getDouble("value_from_jtp")));
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(JDialog_InputBTK.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public double count_total_gram() {
//        try {
//            waleta_gr = Double.valueOf(txt_waleta_gr.getText());
//        } catch (NumberFormatException e) {
////            Logger.getLogger(JDialog_InputBTK.class.getName()).log(Level.SEVERE, null, e);
//            waleta_gr = 0;
//        }
//        try {
//            esta_gr = Double.valueOf(txt_esta_gr.getText());
//        } catch (NumberFormatException e) {
////            Logger.getLogger(JDialog_InputBTK.class.getName()).log(Level.SEVERE, null, e);
//            esta_gr = 0;
//        }
//        try {
//            jastip_gr = Double.valueOf(txt_jastip_gr.getText());
//        } catch (NumberFormatException e) {
////            Logger.getLogger(JDialog_InputBTK.class.getName()).log(Level.SEVERE, null, e);
//            esta_gr = 0;
//        }
        return waleta_gr + esta_gr + jastip_gr;
    }

    public double count_total_amount() {
        try {
            waleta = Double.valueOf(txt_waleta_value.getText());
        } catch (NumberFormatException e) {
            waleta = 0;
//            Logger.getLogger(JDialog_InputBTK.class.getName()).log(Level.SEVERE, null, e);
        }
        try {
            esta = Double.valueOf(txt_esta_value.getText());
        } catch (NumberFormatException e) {
            esta = 0;
//            Logger.getLogger(JDialog_InputBTK.class.getName()).log(Level.SEVERE, null, e);
        }
        try {
            jastip = Double.valueOf(txt_jastip_value.getText());
        } catch (NumberFormatException e) {
            jastip = 0;
//            Logger.getLogger(JDialog_InputBTK.class.getName()).log(Level.SEVERE, null, e);
        }
        try {
            margin_jastip = Double.valueOf(txt_margin_value.getText());
        } catch (NumberFormatException e) {
            margin_jastip = 0;
//            Logger.getLogger(JDialog_InputBTK.class.getName()).log(Level.SEVERE, null, e);
        }
        return waleta + esta + jastip + margin_jastip;
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
        label_title = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txt_invoice = new javax.swing.JTextField();
        txt_waleta_gr = new javax.swing.JTextField();
        txt_esta_gr = new javax.swing.JTextField();
        button_save = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txt_buyer_name = new javax.swing.JTextField();
        txt_buyer_code = new javax.swing.JTextField();
        button_choose = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        Date_invoice = new com.toedter.calendar.JDateChooser();
        jLabel17 = new javax.swing.JLabel();
        Date_AWB = new com.toedter.calendar.JDateChooser();
        jLabel7 = new javax.swing.JLabel();
        txt_no_peb = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        ComboBox_TermPayment = new javax.swing.JComboBox<>();
        txt_jastip_gr = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txt_jastip_value = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        txt_waleta_value = new javax.swing.JTextField();
        txt_esta_value = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        ComboBox_Currency = new javax.swing.JComboBox<>();
        txt_margin_value = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        txt_total_gr = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        txt_total_amount = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        label_title.setBackground(new java.awt.Color(255, 255, 255));
        label_title.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_title.setText("Input Invoice Report");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel4.setText("Net Weight (Gram)");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("Invoice :");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Waleta :");

        txt_invoice.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        txt_waleta_gr.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_waleta_gr.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_waleta_grFocusLost(evt);
            }
        });
        txt_waleta_gr.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_waleta_grKeyTyped(evt);
            }
        });

        txt_esta_gr.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_esta_gr.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_esta_grFocusLost(evt);
            }
        });
        txt_esta_gr.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_esta_grKeyTyped(evt);
            }
        });

        button_save.setBackground(new java.awt.Color(255, 255, 255));
        button_save.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_save.setText("Save");
        button_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_saveActionPerformed(evt);
            }
        });

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel9.setText("Kode Buyer :");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel10.setText("Nama Buyer :");

        txt_buyer_name.setEditable(false);
        txt_buyer_name.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_buyer_name.setFocusable(false);

        txt_buyer_code.setEditable(false);
        txt_buyer_code.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_buyer_code.setFocusable(false);

        button_choose.setBackground(new java.awt.Color(255, 255, 255));
        button_choose.setFont(new java.awt.Font("SansSerif", 0, 11)); // NOI18N
        button_choose.setText("Choose Buyer");
        button_choose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_chooseActionPerformed(evt);
            }
        });

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel16.setText("Tanggal Invoice :");

        Date_invoice.setBackground(new java.awt.Color(255, 255, 255));
        Date_invoice.setDateFormatString("dd MMMM yyyy");
        Date_invoice.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel17.setText("Tanggal AWB :");

        Date_AWB.setBackground(new java.awt.Color(255, 255, 255));
        Date_AWB.setDateFormatString("dd MMMM yyyy");
        Date_AWB.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("No. PEB :");

        txt_no_peb.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText("Term of Payment :");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel11.setText("Esta :");

        ComboBox_TermPayment.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        ComboBox_TermPayment.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "T/T 50/50", "T/T 30/70", "-" }));

        txt_jastip_gr.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_jastip_gr.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_jastip_grFocusLost(evt);
            }
        });
        txt_jastip_gr.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_jastip_grKeyTyped(evt);
            }
        });

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel12.setText("Jasa Titip :");

        txt_jastip_value.setEditable(false);
        txt_jastip_value.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_jastip_value.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_jastip_valueFocusLost(evt);
            }
        });

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel13.setText("Jasa Titip :");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel14.setText("Invoice Amount");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel15.setText("Waleta :");

        txt_waleta_value.setEditable(false);
        txt_waleta_value.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_waleta_value.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_waleta_valueFocusLost(evt);
            }
        });

        txt_esta_value.setEditable(false);
        txt_esta_value.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_esta_value.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_esta_valueFocusLost(evt);
            }
        });

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel18.setText("Esta :");

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel19.setText("Currency :");

        ComboBox_Currency.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        ComboBox_Currency.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "CNY", "IDR", "USD" }));

        txt_margin_value.setEditable(false);
        txt_margin_value.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_margin_value.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_margin_valueFocusLost(evt);
            }
        });

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel20.setText("Margin Jastip :");

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel21.setText("Total :");

        txt_total_gr.setEditable(false);
        txt_total_gr.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_total_gr.setFocusable(false);

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel22.setText("Total :");

        txt_total_amount.setEditable(false);
        txt_total_amount.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_total_amount.setFocusable(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(button_save))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_invoice))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_no_peb))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_waleta_gr))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_esta_gr))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_jastip_gr))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_jastip_value))
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_waleta_value))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_esta_value))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_margin_value))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_total_gr))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_total_amount))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_AWB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_invoice, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(label_title)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txt_buyer_code, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_choose))
                            .addComponent(txt_buyer_name)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_TermPayment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_Currency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_title, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_invoice, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_buyer_code, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_choose, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_buyer_name, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(Date_invoice, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(Date_AWB, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_no_peb, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_TermPayment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_Currency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_waleta_gr, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_esta_gr, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_jastip_gr, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_total_gr, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_waleta_value, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_esta_value, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_jastip_value, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_margin_value, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_total_amount, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_save, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
        if (txt_invoice.getText() == null || txt_invoice.getText().equals("")
                || txt_buyer_code.getText() == null || txt_buyer_code.getText().equals("")
                || Date_invoice.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Silahkan lengkapi data diatas");
        } else {
            txt_total_gr.setText(decimalFormat.format(count_total_gram()));
            txt_total_amount.setText(decimalFormat.format(count_total_amount()));
            try {
                Utility.db.getConnection().setAutoCommit(false);
                if (txt_invoice.isEditable()) {
                    sql = "INSERT INTO `tb_payment_report`(`invoice`, `buyer`, `tgl_invoice`, `awb_date`, `no_peb`, `term_payment`, `berat_waleta`, `berat_esta`, `berat_jastip`, `currency`, `value_waleta`, `value_esta`, `value_from_jtp`, `value_to_jtp`) "
                            + "VALUES ("
                            + "'" + txt_invoice.getText() + "',"
                            + "'" + txt_buyer_code.getText() + "',"
                            + "'" + new SimpleDateFormat("yyyy-MM-dd").format(Date_invoice.getDate()) + "',"
                            + "'" + new SimpleDateFormat("yyyy-MM-dd").format(Date_AWB.getDate()) + "',"
                            + "'" + txt_no_peb.getText() + "',"
                            + "'" + ComboBox_TermPayment.getSelectedItem().toString() + "',"
                            + "'" + waleta_gr + "',"
                            + "'" + esta_gr + "',"
                            + "'" + jastip_gr + "',"
                            + "'" + ComboBox_Currency.getSelectedItem().toString() + "',"
                            + "'" + waleta + "',"
                            + "'" + esta + "',"
                            + "'" + margin_jastip + "',"
                            + "'" + jastip + "')";
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(sql);

                    String input = "INSERT INTO `tb_biaya_expor`(`invoice_no`) VALUES ('" + txt_invoice.getText() + "')";
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(input);
                } else {
                    sql = "UPDATE `tb_payment_report` SET "
                            + "`buyer`='" + txt_buyer_code.getText() + "',"
                            + "`tgl_invoice`='" + new SimpleDateFormat("yyyy-MM-dd").format(Date_invoice.getDate()) + "',"
                            + "`awb_date`='" + new SimpleDateFormat("yyyy-MM-dd").format(Date_AWB.getDate()) + "',"
                            + "`no_peb`='" + txt_no_peb.getText() + "',"
                            + "`term_payment`='" + ComboBox_TermPayment.getSelectedItem().toString() + "',"
                            + "`berat_waleta`='" + waleta_gr + "',"
                            + "`berat_esta`='" + esta_gr + "',"
                            + "`berat_jastip`='" + jastip_gr + "',"
                            + "`currency`='" + ComboBox_Currency.getSelectedItem().toString() + "',"
                            + "`value_waleta`='" + waleta + "',"
                            + "`value_esta`='" + esta + "',"
                            + "`value_from_jtp`='" + margin_jastip + "',"
                            + "`value_to_jtp`='" + jastip + "' "
                            + "WHERE `invoice`='" + txt_invoice.getText() + "'";
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(sql);
                }
                Utility.db.getConnection().commit();
                this.dispose();
                JOptionPane.showMessageDialog(this, "Data Berhasil simpan");
            } catch (SQLException ex) {
                try {
                    Utility.db.getConnection().rollback();
                } catch (SQLException ex1) {
                    Logger.getLogger(JDialog_Input_InvoicePayment.class.getName()).log(Level.SEVERE, null, ex1);
                }
                JOptionPane.showMessageDialog(this, ex.getMessage());
                Logger.getLogger(JDialog_Input_InvoicePayment.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    Utility.db.getConnection().setAutoCommit(true);
                } catch (SQLException ex) {
                    Logger.getLogger(JDialog_Input_InvoicePayment.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_button_saveActionPerformed

    private void button_chooseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_chooseActionPerformed
        // TODO add your handling code here:
        JDialog_ChooseBuyer dialog = new JDialog_ChooseBuyer(new javax.swing.JFrame(), true, "All");
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);

        int a = JDialog_ChooseBuyer.table_list_buyer.getSelectedRow();
        if (a >= 0) {
            String nama_buyer = JDialog_ChooseBuyer.table_list_buyer.getValueAt(a, 1).toString();
            String kode_buyer = JDialog_ChooseBuyer.table_list_buyer.getValueAt(a, 0).toString();
            txt_buyer_code.setText(kode_buyer);
            txt_buyer_name.setText(nama_buyer);
        }
    }//GEN-LAST:event_button_chooseActionPerformed

    private void txt_waleta_grFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_waleta_grFocusLost
        // TODO add your handling code here:
        try {
            waleta_gr = Double.valueOf(txt_waleta_gr.getText());
            if (waleta_gr < 1) {
                txt_waleta_value.setText(null);
                txt_waleta_value.setEditable(false);
                txt_waleta_value.setFocusable(false);
            } else {
                txt_waleta_value.setEditable(true);
                txt_waleta_value.setFocusable(true);
            }
            txt_total_gr.setText(decimalFormat.format(count_total_gram()));
        } catch (NumberFormatException e) {
            waleta_gr = 0;
        }
    }//GEN-LAST:event_txt_waleta_grFocusLost

    private void txt_esta_grFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_esta_grFocusLost
        // TODO add your handling code here:
        try {
            esta_gr = Double.valueOf(txt_esta_gr.getText());
            if (esta_gr < 1) {
                txt_esta_value.setText(null);
                txt_esta_value.setEditable(false);
                txt_esta_value.setFocusable(false);
            } else {
                txt_esta_value.setEditable(true);
                txt_esta_value.setFocusable(true);
            }
            txt_total_gr.setText(decimalFormat.format(count_total_gram()));
        } catch (NumberFormatException e) {
            esta_gr = 0;
        }
    }//GEN-LAST:event_txt_esta_grFocusLost

    private void txt_jastip_grFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_jastip_grFocusLost
        // TODO add your handling code here:
        try {
            jastip_gr = Double.valueOf(txt_jastip_gr.getText());
            if (jastip_gr < 1) {
                txt_jastip_value.setText(null);
                txt_jastip_value.setEditable(false);
                txt_jastip_value.setFocusable(false);
                txt_margin_value.setText(null);
                txt_margin_value.setEditable(false);
                txt_margin_value.setFocusable(false);
            } else {
                txt_jastip_value.setEditable(true);
                txt_margin_value.setEditable(true);
                txt_jastip_value.setFocusable(true);
                txt_margin_value.setFocusable(true);
            }
            txt_total_gr.setText(decimalFormat.format(count_total_gram()));
        } catch (NumberFormatException e) {
            jastip_gr = 0;
        }
    }//GEN-LAST:event_txt_jastip_grFocusLost

    private void txt_waleta_valueFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_waleta_valueFocusLost
        // TODO add your handling code here:
        txt_total_amount.setText(decimalFormat.format(count_total_amount()));
    }//GEN-LAST:event_txt_waleta_valueFocusLost

    private void txt_esta_valueFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_esta_valueFocusLost
        // TODO add your handling code here:
        txt_total_amount.setText(decimalFormat.format(count_total_amount()));
    }//GEN-LAST:event_txt_esta_valueFocusLost

    private void txt_jastip_valueFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_jastip_valueFocusLost
        // TODO add your handling code here:
        txt_total_amount.setText(decimalFormat.format(count_total_amount()));
    }//GEN-LAST:event_txt_jastip_valueFocusLost

    private void txt_margin_valueFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_margin_valueFocusLost
        // TODO add your handling code here:
        txt_total_amount.setText(decimalFormat.format(count_total_amount()));
    }//GEN-LAST:event_txt_margin_valueFocusLost

    private void txt_waleta_grKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_waleta_grKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_waleta_grKeyTyped

    private void txt_esta_grKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_esta_grKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_esta_grKeyTyped

    private void txt_jastip_grKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_jastip_grKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_jastip_grKeyTyped


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_Currency;
    private javax.swing.JComboBox<String> ComboBox_TermPayment;
    private com.toedter.calendar.JDateChooser Date_AWB;
    private com.toedter.calendar.JDateChooser Date_invoice;
    private javax.swing.JButton button_choose;
    private javax.swing.JButton button_save;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
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
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel label_title;
    private javax.swing.JTextField txt_buyer_code;
    private javax.swing.JTextField txt_buyer_name;
    private javax.swing.JTextField txt_esta_gr;
    private javax.swing.JTextField txt_esta_value;
    private javax.swing.JTextField txt_invoice;
    private javax.swing.JTextField txt_jastip_gr;
    private javax.swing.JTextField txt_jastip_value;
    private javax.swing.JTextField txt_margin_value;
    private javax.swing.JTextField txt_no_peb;
    private javax.swing.JTextField txt_total_amount;
    private javax.swing.JTextField txt_total_gr;
    private javax.swing.JTextField txt_waleta_gr;
    private javax.swing.JTextField txt_waleta_value;
    // End of variables declaration//GEN-END:variables
}
