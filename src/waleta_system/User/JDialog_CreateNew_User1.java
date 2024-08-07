package waleta_system.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import waleta_system.Browse_Karyawan;
import waleta_system.Class.AksesMenu;
import waleta_system.Class.Utility;

public class JDialog_CreateNew_User1 extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    List<AksesMenu.ComboMenu> listCB = new ArrayList<>();

    public String replaceAt(String text, int index, char replacement) {
        return text.substring(0, index) + replacement + text.substring(index + 1);
    }

    public List<AksesMenu.Akses> hakAkses() {
        List<AksesMenu.Akses> listHakAkses = AksesMenu.getNewAksesMenu();
        for (int j = 0; j < listCB.size(); j++) {
            for (int k = 0; k < 4; k++) {
                listCB.get(j).data[k].setEnabled(false);
                if (listCB.get(j).data[k].isSelected()) {
                    listHakAkses.get(AksesMenu.searchMenuByName(listHakAkses, listCB.get(j).nama)).akses
                            = replaceAt(listHakAkses.get(AksesMenu.searchMenuByName(listHakAkses, listCB.get(j).nama)).akses, k, '1');
                }
            }
        }
        return listHakAkses;
    }

    public JDialog_CreateNew_User1(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        try {
            initComponents();
            //MAIN MENU
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_ISU_PRODUKSI, new JCheckBox[]{cbView_MainMenu1, cbInsert_MainMenu1, cbUpdate_MainMenu1, cbDelete_MainMenu1}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_ABSEN_PRODUKSI, new JCheckBox[]{cbView_MainMenu2, cbInsert_MainMenu2, cbUpdate_MainMenu2, cbDelete_MainMenu2}));
            //BAHAN BAKU
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_BAHAN_BAKU, new JCheckBox[]{cbView0, cbInsert0, cbUpdate0, cbDelete0}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_SUPPLIER, new JCheckBox[]{cbView0_1, cbInsert0_1, cbUpdate0_1, cbDelete0_1}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_CUSTOMER, new JCheckBox[]{cbView0_2, cbInsert0_2, cbUpdate0_2, cbDelete0_2}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_RUMAH_BURUNG, new JCheckBox[]{cbView0_3, cbInsert0_3, cbUpdate0_3, cbDelete0_3}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_GRADE_BAKU, new JCheckBox[]{cbView0_4, cbInsert0_4, cbUpdate0_4, cbDelete0_4}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_BAHAN_BAKU_MASUK, new JCheckBox[]{cbView0_5, cbInsert0_5, cbUpdate0_5, cbDelete0_5}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_LAPORAN_PRODUKSI, new JCheckBox[]{cbView0_6, cbInsert0_6, cbUpdate0_6, cbDelete0_6}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_LAPORAN_PRODUKSI_SESEKAN, new JCheckBox[]{cbView0_7, cbInsert0_7, cbUpdate0_7, cbDelete0_7}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_LAPORAN_PRODUKSI_SAPON, new JCheckBox[]{cbView0_8, cbInsert0_8, cbUpdate0_8, cbDelete0_8}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_BAKU_KELUAR, new JCheckBox[]{cbView0_9, cbInsert0_9, cbUpdate0_9, cbDelete0_9}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_REKAPITULASI, new JCheckBox[]{cbView0_10, cbInsert0_10, cbUpdate0_10, cbDelete0_10}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_ADJUSTMENT_BAHAN_BAKU, new JCheckBox[]{cbView0_11, cbInsert0_11, cbUpdate0_11, cbDelete0_11}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_PEMBELIAN_BAHAN_BAKU, new JCheckBox[]{cbView0_12, cbInsert0_12, cbUpdate0_12, cbDelete0_12}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_KARTU_CAMPURAN, new JCheckBox[]{cbView0_13, cbInsert0_13, cbUpdate0_13, cbDelete0_13}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_BONUS_PANEN, new JCheckBox[]{cbView0_14, cbInsert0_14, cbUpdate0_14, cbDelete0_14}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_MASTER_BULU_UPAH, new JCheckBox[]{cbView0_15, cbInsert0_15, cbUpdate0_15, cbDelete0_15}));
            //PRODUKSI
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_PRODUKSI, new JCheckBox[]{cbView1, cbInsert1, cbUpdate1, cbDelete1}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_RENDAM, new JCheckBox[]{cbView1_1, cbInsert1_1, cbUpdate1_1, cbDelete1_1}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_CUCI, new JCheckBox[]{cbView1_2, cbInsert1_2, cbUpdate1_2, cbDelete1_2}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_CABUT, new JCheckBox[]{cbView1_3, cbInsert1_3, cbUpdate1_3, cbDelete1_3}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_CETAK, new JCheckBox[]{cbView1_4, cbInsert1_4, cbUpdate1_4, cbDelete1_4}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_F2, new JCheckBox[]{cbView1_5, cbInsert1_5, cbUpdate1_5, cbDelete1_5}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_PROGRESS_LP, new JCheckBox[]{cbView1_6, cbInsert1_6, cbUpdate1_6, cbDelete1_6}));
            //ATB
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ATB, new JCheckBox[]{cbViewATB, cbInsertATB, cbUpdateATB, cbDeleteATB}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ATB_ITEM_KARYAWAN_ATB, new JCheckBox[]{cbViewATB_1, cbInsertATB_1, cbUpdateATB_1, cbDeleteATB_1}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ATB_ITEM_PRODUKSI_ATB, new JCheckBox[]{cbViewATB_2, cbInsertATB_2, cbUpdateATB_2, cbDeleteATB_2}));
            //MANAJEMEN SUB
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_MANAJEMEN_SUB, new JCheckBox[]{cbViewSub, cbInsertSub, cbUpdateSub, cbDeleteSub}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_DATA_SUB_WALETA, new JCheckBox[]{cbViewSub_1, cbInsertSub_1, cbUpdateSub_1, cbDeleteSub_1}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_CABUT_SUB, new JCheckBox[]{cbViewSub_2, cbInsertSub_2, cbUpdateSub_2, cbDeleteSub_2}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_DATA_KARYAWAN_SUB, new JCheckBox[]{cbViewSub_3, cbInsertSub_3, cbUpdateSub_3, cbDeleteSub_3}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_ABSEN_SUB, new JCheckBox[]{cbViewSub_4, cbInsertSub_4, cbUpdateSub_4, cbDeleteSub_4}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_PENGGAJIAN_SUB, new JCheckBox[]{cbViewSub_5, cbInsertSub_5, cbUpdateSub_5, cbDeleteSub_5}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_PIUTANG_KARYAWAN_SUB, new JCheckBox[]{cbViewSub_6, cbInsertSub_6, cbUpdateSub_6, cbDeleteSub_6}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_TARIF_UPAH_BOBOT_LP_SUB, new JCheckBox[]{cbViewSub_7, cbInsertSub_7, cbUpdateSub_7, cbDeleteSub_7}));
            //BAHAN JADI
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_BAHAN_JADI, new JCheckBox[]{cbView2, cbInsert2, cbUpdate2, cbDelete2}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_BAHAN_JADI_MASUK, new JCheckBox[]{cbView2_1, cbInsert2_1, cbUpdate2_1, cbDelete2_1}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_TUTUPAN_GRADING, new JCheckBox[]{cbView2_2, cbInsert2_2, cbUpdate2_2, cbDelete2_2}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_DATA_BOX_BAHAN_JADI, new JCheckBox[]{cbView2_3, cbInsert2_3, cbUpdate2_3, cbDelete2_3}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_STOCK_BAHAN_JADI, new JCheckBox[]{cbView2_4, cbInsert2_4, cbUpdate2_4, cbDelete2_4}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_MASTER_GRADE_BAHAN_JADI, new JCheckBox[]{cbView2_5, cbInsert2_5, cbUpdate2_5, cbDelete2_5}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_BONUS_GRADING, new JCheckBox[]{cbView2_6, cbInsert2_6, cbUpdate2_6, cbDelete2_6}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_LAPORAN_PRODUKSI_BAHAN_JADI, new JCheckBox[]{cbView2_7, cbInsert2_7, cbUpdate2_7, cbDelete2_7}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_PEMBELIAN_BAHAN_JADI, new JCheckBox[]{cbView2_8, cbInsert2_8, cbUpdate2_8, cbDelete2_8}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_PRICE_LIST_BJ, new JCheckBox[]{cbView2_9, cbInsert2_9, cbUpdate2_9, cbDelete2_9}));
            //PACKING
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_PACKING, new JCheckBox[]{cbView3, cbInsert3, cbUpdate3, cbDelete3}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_DATA_PACKING, new JCheckBox[]{cbView3_1, cbInsert3_1, cbUpdate3_1, cbDelete3_1}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_DATA_PENGIRIMAN, new JCheckBox[]{cbView3_2, cbInsert3_2, cbUpdate3_2, cbDelete3_2}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_LIST_BUYER, new JCheckBox[]{cbView3_3, cbInsert3_3, cbUpdate3_3, cbDelete3_3}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_DATA_SPK, new JCheckBox[]{cbView3_4, cbInsert3_4, cbUpdate3_4, cbDelete3_4}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_DATA_PAYMENT_REPORT, new JCheckBox[]{cbView3_5, cbInsert3_5, cbUpdate3_5, cbDelete3_5}));
            //QUALITY CONTROL
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_QC, new JCheckBox[]{cbView4, cbInsert4, cbUpdate4, cbDelete4}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_LAB_BAHAN_BAKU, new JCheckBox[]{cbView4_1, cbInsert4_1, cbUpdate4_1, cbDelete4_1}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_LAB_LP, new JCheckBox[]{cbView4_2, cbInsert4_2, cbUpdate4_2, cbDelete4_2}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_TREATMENT_LP, new JCheckBox[]{cbView4_3, cbInsert4_3, cbUpdate4_3, cbDelete4_3}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_PEMANASAN_BAHAN_JADI, new JCheckBox[]{cbView4_4, cbInsert4_4, cbUpdate4_4, cbDelete4_4}));
            //HRD
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_HRD, new JCheckBox[]{cbView5, cbInsert5, cbUpdate5, cbDelete5}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_DATA_KARYAWAN, new JCheckBox[]{cbView5_1, cbInsert5_1, cbUpdate5_1, cbDelete5_1}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_DATA_IJIN_KELUAR, new JCheckBox[]{cbView5_2, cbInsert5_2, cbUpdate5_2, cbDelete5_2}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_DATA_IJIN_ABSEN_CUTI, new JCheckBox[]{cbView5_3, cbInsert5_3, cbUpdate5_3, cbDelete5_3}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_DATA_LEMBUR, new JCheckBox[]{cbView5_4, cbInsert5_4, cbUpdate5_4, cbDelete5_4}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_DATA_PINDAH_BAGIAN, new JCheckBox[]{cbView5_5, cbInsert5_5, cbUpdate5_5, cbDelete5_5}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_DEPT_BAG, new JCheckBox[]{cbView5_6, cbInsert5_6, cbUpdate5_6, cbDelete5_6}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_TANGGAL_LIBUR, new JCheckBox[]{cbView5_7, cbInsert5_7, cbUpdate5_7, cbDelete5_7}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_TBT, new JCheckBox[]{cbView5_8, cbInsert5_8, cbUpdate5_8, cbDelete5_8}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_ABSEN, new JCheckBox[]{cbView5_9, cbInsert5_9, cbUpdate5_9, cbDelete5_9}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_JAM_KERJA, new JCheckBox[]{cbView5_10, cbInsert5_10, cbUpdate5_10, cbDelete5_10}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_JEMPUTAN, new JCheckBox[]{cbView5_11, cbInsert5_11, cbUpdate5_11, cbDelete5_11}));
            //KEUANGAN
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_KEUANGAN, new JCheckBox[]{cbViewKeuangan, cbInsertKeuangan, cbUpdateKeuangan, cbDeleteKeuangan}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_BAHAN_BAKU, new JCheckBox[]{cbViewKeuangan_1, cbInsertKeuangan_1, cbUpdateKeuangan_1, cbDeleteKeuangan_1}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_PENGGAJIAN_PRODUKSI, new JCheckBox[]{cbViewKeuangan_2, cbInsertKeuangan_2, cbUpdateKeuangan_2, cbDeleteKeuangan_2}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_BARANG_JADI, new JCheckBox[]{cbViewKeuangan_3, cbInsertKeuangan_3, cbUpdateKeuangan_3, cbDeleteKeuangan_3}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_DATA_KARYAWAN_KEUANGAN, new JCheckBox[]{cbViewKeuangan_4, cbInsertKeuangan_4, cbUpdateKeuangan_4, cbDeleteKeuangan_4}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_PURCHASING, new JCheckBox[]{cbViewKeuangan_5, cbInsertKeuangan_5, cbUpdateKeuangan_5, cbDeleteKeuangan_5}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_LEMBUR_STAFF, new JCheckBox[]{cbViewKeuangan_6, cbInsertKeuangan_6, cbUpdateKeuangan_6, cbDeleteKeuangan_6}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_DATA_PENGIRIMAN_KEUANGAN, new JCheckBox[]{cbViewKeuangan_7, cbInsertKeuangan_7, cbUpdateKeuangan_7, cbDeleteKeuangan_7}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_SALES_AND_PAYMENT_REPORT, new JCheckBox[]{cbViewKeuangan_8, cbInsertKeuangan_8, cbUpdateKeuangan_8, cbDeleteKeuangan_8}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_DATA_BIAYA, new JCheckBox[]{cbViewKeuangan_9, cbInsertKeuangan_9, cbUpdateKeuangan_9, cbDeleteKeuangan_9}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_CASH_ON_BANK, new JCheckBox[]{cbViewKeuangan_10, cbInsertKeuangan_10, cbUpdateKeuangan_10, cbDeleteKeuangan_10}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_NERACA, new JCheckBox[]{cbViewKeuangan_11, cbInsertKeuangan_11, cbUpdateKeuangan_11, cbDeleteKeuangan_11}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_AR_AP_ESTA, new JCheckBox[]{cbViewKeuangan_12, cbInsertKeuangan_12, cbUpdateKeuangan_12, cbDeleteKeuangan_12}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_LAPORAN_KEUANGAN, new JCheckBox[]{cbViewKeuangan_13, cbInsertKeuangan_13, cbUpdateKeuangan_13, cbDeleteKeuangan_13}));
            //MANAJEMEN
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_MANAJEMEN, new JCheckBox[]{cbView7, cbInsert7, cbUpdate7, cbDelete7}));
            //USER
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_CREATE_NEW_USER, new JCheckBox[]{cbView7_1, cbInsert7_1, cbUpdate7_1, cbDelete7_1}));
            listCB.add(new AksesMenu.ComboMenu(AksesMenu.MENU_ITEM_DATA_USER, new JCheckBox[]{cbView7_2, cbInsert7_2, cbUpdate7_2, cbDelete7_2}));
        } catch (Exception ex) {
            Logger.getLogger(JDialog_CreateNew_User1.class.getName()).log(Level.SEVERE, null, ex);
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

        jPanel_create_user = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txt_username = new javax.swing.JTextField();
        txt_pass_1 = new javax.swing.JPasswordField();
        txt_pass_2 = new javax.swing.JPasswordField();
        button_pilih_pegawai = new javax.swing.JButton();
        button_simpan = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        label_id = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        label_name = new javax.swing.JLabel();
        label_position = new javax.swing.JLabel();
        label_department = new javax.swing.JLabel();
        label_division = new javax.swing.JLabel();
        label_status = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        panel_main_menu = new javax.swing.JPanel();
        cbDelete_MainMenu1 = new javax.swing.JCheckBox();
        cbDelete_MainMenu2 = new javax.swing.JCheckBox();
        cbUpdate_MainMenu2 = new javax.swing.JCheckBox();
        cbUpdate_MainMenu1 = new javax.swing.JCheckBox();
        cbInsert_MainMenu1 = new javax.swing.JCheckBox();
        cbInsert_MainMenu2 = new javax.swing.JCheckBox();
        cbView_MainMenu2 = new javax.swing.JCheckBox();
        jLabel65 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        cbView_MainMenu1 = new javax.swing.JCheckBox();
        panel_bahanBaku = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        cbView0 = new javax.swing.JCheckBox();
        cbInsert0 = new javax.swing.JCheckBox();
        cbDelete0 = new javax.swing.JCheckBox();
        cbUpdate0 = new javax.swing.JCheckBox();
        cbView0_1 = new javax.swing.JCheckBox();
        cbInsert0_1 = new javax.swing.JCheckBox();
        cbUpdate0_1 = new javax.swing.JCheckBox();
        cbDelete0_1 = new javax.swing.JCheckBox();
        cbView0_2 = new javax.swing.JCheckBox();
        cbInsert0_2 = new javax.swing.JCheckBox();
        cbUpdate0_2 = new javax.swing.JCheckBox();
        cbDelete0_2 = new javax.swing.JCheckBox();
        cbView0_3 = new javax.swing.JCheckBox();
        cbInsert0_3 = new javax.swing.JCheckBox();
        cbUpdate0_3 = new javax.swing.JCheckBox();
        cbDelete0_3 = new javax.swing.JCheckBox();
        cbView0_4 = new javax.swing.JCheckBox();
        cbInsert0_4 = new javax.swing.JCheckBox();
        cbUpdate0_4 = new javax.swing.JCheckBox();
        cbDelete0_4 = new javax.swing.JCheckBox();
        cbView0_5 = new javax.swing.JCheckBox();
        cbInsert0_5 = new javax.swing.JCheckBox();
        cbUpdate0_5 = new javax.swing.JCheckBox();
        cbDelete0_5 = new javax.swing.JCheckBox();
        cbView0_6 = new javax.swing.JCheckBox();
        cbInsert0_6 = new javax.swing.JCheckBox();
        cbUpdate0_6 = new javax.swing.JCheckBox();
        cbDelete0_6 = new javax.swing.JCheckBox();
        jLabel56 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        cbDelete0_13 = new javax.swing.JCheckBox();
        cbView0_11 = new javax.swing.JCheckBox();
        cbInsert0_11 = new javax.swing.JCheckBox();
        cbUpdate0_11 = new javax.swing.JCheckBox();
        cbDelete0_11 = new javax.swing.JCheckBox();
        cbView0_13 = new javax.swing.JCheckBox();
        cbInsert0_13 = new javax.swing.JCheckBox();
        cbUpdate0_13 = new javax.swing.JCheckBox();
        cbDelete0_14 = new javax.swing.JCheckBox();
        cbView0_7 = new javax.swing.JCheckBox();
        cbView0_14 = new javax.swing.JCheckBox();
        cbInsert0_7 = new javax.swing.JCheckBox();
        cbInsert0_14 = new javax.swing.JCheckBox();
        cbUpdate0_7 = new javax.swing.JCheckBox();
        cbUpdate0_14 = new javax.swing.JCheckBox();
        cbDelete0_7 = new javax.swing.JCheckBox();
        cbInsert0_8 = new javax.swing.JCheckBox();
        cbDelete0_8 = new javax.swing.JCheckBox();
        cbView0_8 = new javax.swing.JCheckBox();
        cbUpdate0_8 = new javax.swing.JCheckBox();
        cbView0_9 = new javax.swing.JCheckBox();
        cbInsert0_9 = new javax.swing.JCheckBox();
        cbUpdate0_9 = new javax.swing.JCheckBox();
        cbDelete0_9 = new javax.swing.JCheckBox();
        cbView0_10 = new javax.swing.JCheckBox();
        cbInsert0_10 = new javax.swing.JCheckBox();
        cbUpdate0_10 = new javax.swing.JCheckBox();
        cbDelete0_10 = new javax.swing.JCheckBox();
        jLabel58 = new javax.swing.JLabel();
        cbView0_12 = new javax.swing.JCheckBox();
        cbDelete0_12 = new javax.swing.JCheckBox();
        cbUpdate0_12 = new javax.swing.JCheckBox();
        cbInsert0_12 = new javax.swing.JCheckBox();
        jLabel88 = new javax.swing.JLabel();
        cbInsert0_15 = new javax.swing.JCheckBox();
        cbView0_15 = new javax.swing.JCheckBox();
        cbDelete0_15 = new javax.swing.JCheckBox();
        cbUpdate0_15 = new javax.swing.JCheckBox();
        panel_Produksi = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        cbView1 = new javax.swing.JCheckBox();
        cbDelete1 = new javax.swing.JCheckBox();
        cbInsert1 = new javax.swing.JCheckBox();
        cbUpdate1 = new javax.swing.JCheckBox();
        cbView1_5 = new javax.swing.JCheckBox();
        cbInsert1_5 = new javax.swing.JCheckBox();
        cbUpdate1_5 = new javax.swing.JCheckBox();
        cbDelete1_5 = new javax.swing.JCheckBox();
        cbView1_3 = new javax.swing.JCheckBox();
        cbView1_4 = new javax.swing.JCheckBox();
        cbInsert1_4 = new javax.swing.JCheckBox();
        cbInsert1_3 = new javax.swing.JCheckBox();
        cbUpdate1_3 = new javax.swing.JCheckBox();
        cbUpdate1_4 = new javax.swing.JCheckBox();
        cbDelete1_4 = new javax.swing.JCheckBox();
        cbDelete1_3 = new javax.swing.JCheckBox();
        cbView1_6 = new javax.swing.JCheckBox();
        cbInsert1_6 = new javax.swing.JCheckBox();
        cbView1_1 = new javax.swing.JCheckBox();
        cbUpdate1_6 = new javax.swing.JCheckBox();
        cbInsert1_1 = new javax.swing.JCheckBox();
        cbUpdate1_1 = new javax.swing.JCheckBox();
        cbDelete1_6 = new javax.swing.JCheckBox();
        cbDelete1_1 = new javax.swing.JCheckBox();
        cbView1_2 = new javax.swing.JCheckBox();
        cbInsert1_2 = new javax.swing.JCheckBox();
        cbUpdate1_2 = new javax.swing.JCheckBox();
        cbDelete1_2 = new javax.swing.JCheckBox();
        panel_atb = new javax.swing.JPanel();
        cbDeleteATB_1 = new javax.swing.JCheckBox();
        cbDeleteATB_2 = new javax.swing.JCheckBox();
        cbUpdateATB_2 = new javax.swing.JCheckBox();
        cbUpdateATB_1 = new javax.swing.JCheckBox();
        cbInsertATB_1 = new javax.swing.JCheckBox();
        cbInsertATB_2 = new javax.swing.JCheckBox();
        cbViewATB_2 = new javax.swing.JCheckBox();
        jLabel73 = new javax.swing.JLabel();
        jLabel74 = new javax.swing.JLabel();
        cbViewATB_1 = new javax.swing.JCheckBox();
        cbDeleteATB = new javax.swing.JCheckBox();
        cbViewATB = new javax.swing.JCheckBox();
        cbUpdateATB = new javax.swing.JCheckBox();
        cbInsertATB = new javax.swing.JCheckBox();
        panel_manajemen_sub = new javax.swing.JPanel();
        cbDeleteSub_1 = new javax.swing.JCheckBox();
        cbViewSub_1 = new javax.swing.JCheckBox();
        cbInsertSub_1 = new javax.swing.JCheckBox();
        cbViewSub = new javax.swing.JCheckBox();
        cbDeleteSub = new javax.swing.JCheckBox();
        cbInsertSub = new javax.swing.JCheckBox();
        cbUpdateSub = new javax.swing.JCheckBox();
        jLabel54 = new javax.swing.JLabel();
        cbUpdateSub_1 = new javax.swing.JCheckBox();
        jLabel55 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        jLabel70 = new javax.swing.JLabel();
        jLabel71 = new javax.swing.JLabel();
        cbDeleteSub_2 = new javax.swing.JCheckBox();
        cbViewSub_2 = new javax.swing.JCheckBox();
        cbInsertSub_2 = new javax.swing.JCheckBox();
        cbUpdateSub_2 = new javax.swing.JCheckBox();
        cbDeleteSub_3 = new javax.swing.JCheckBox();
        cbViewSub_3 = new javax.swing.JCheckBox();
        cbInsertSub_3 = new javax.swing.JCheckBox();
        cbUpdateSub_3 = new javax.swing.JCheckBox();
        cbDeleteSub_4 = new javax.swing.JCheckBox();
        cbViewSub_4 = new javax.swing.JCheckBox();
        cbInsertSub_4 = new javax.swing.JCheckBox();
        cbUpdateSub_4 = new javax.swing.JCheckBox();
        cbDeleteSub_5 = new javax.swing.JCheckBox();
        cbViewSub_5 = new javax.swing.JCheckBox();
        cbInsertSub_5 = new javax.swing.JCheckBox();
        cbUpdateSub_5 = new javax.swing.JCheckBox();
        cbUpdateSub_6 = new javax.swing.JCheckBox();
        cbInsertSub_6 = new javax.swing.JCheckBox();
        cbViewSub_6 = new javax.swing.JCheckBox();
        cbDeleteSub_6 = new javax.swing.JCheckBox();
        jLabel86 = new javax.swing.JLabel();
        cbViewSub_7 = new javax.swing.JCheckBox();
        jLabel87 = new javax.swing.JLabel();
        cbUpdateSub_7 = new javax.swing.JCheckBox();
        cbInsertSub_7 = new javax.swing.JCheckBox();
        cbDeleteSub_7 = new javax.swing.JCheckBox();
        panel_bahanJadi = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        cbUpdate2_1 = new javax.swing.JCheckBox();
        cbDelete2_1 = new javax.swing.JCheckBox();
        cbView2 = new javax.swing.JCheckBox();
        cbDelete2 = new javax.swing.JCheckBox();
        cbInsert2 = new javax.swing.JCheckBox();
        cbUpdate2 = new javax.swing.JCheckBox();
        jLabel31 = new javax.swing.JLabel();
        cbView2_1 = new javax.swing.JCheckBox();
        cbInsert2_1 = new javax.swing.JCheckBox();
        cbView2_2 = new javax.swing.JCheckBox();
        cbInsert2_2 = new javax.swing.JCheckBox();
        cbUpdate2_2 = new javax.swing.JCheckBox();
        cbDelete2_2 = new javax.swing.JCheckBox();
        cbUpdate2_3 = new javax.swing.JCheckBox();
        cbInsert2_3 = new javax.swing.JCheckBox();
        cbDelete2_3 = new javax.swing.JCheckBox();
        cbView2_3 = new javax.swing.JCheckBox();
        cbDelete2_4 = new javax.swing.JCheckBox();
        cbView2_4 = new javax.swing.JCheckBox();
        cbUpdate2_4 = new javax.swing.JCheckBox();
        cbInsert2_4 = new javax.swing.JCheckBox();
        jLabel32 = new javax.swing.JLabel();
        cbView2_5 = new javax.swing.JCheckBox();
        cbInsert2_5 = new javax.swing.JCheckBox();
        cbUpdate2_5 = new javax.swing.JCheckBox();
        cbDelete2_5 = new javax.swing.JCheckBox();
        jLabel33 = new javax.swing.JLabel();
        cbView2_6 = new javax.swing.JCheckBox();
        cbInsert2_6 = new javax.swing.JCheckBox();
        cbUpdate2_6 = new javax.swing.JCheckBox();
        cbDelete2_6 = new javax.swing.JCheckBox();
        jLabel34 = new javax.swing.JLabel();
        cbInsert2_7 = new javax.swing.JCheckBox();
        cbView2_7 = new javax.swing.JCheckBox();
        cbUpdate2_7 = new javax.swing.JCheckBox();
        cbDelete2_7 = new javax.swing.JCheckBox();
        cbUpdate2_8 = new javax.swing.JCheckBox();
        cbView2_8 = new javax.swing.JCheckBox();
        jLabel51 = new javax.swing.JLabel();
        cbDelete2_8 = new javax.swing.JCheckBox();
        cbInsert2_8 = new javax.swing.JCheckBox();
        cbInsert2_9 = new javax.swing.JCheckBox();
        cbDelete2_9 = new javax.swing.JCheckBox();
        jLabel52 = new javax.swing.JLabel();
        cbView2_9 = new javax.swing.JCheckBox();
        cbUpdate2_9 = new javax.swing.JCheckBox();
        panel_Packing = new javax.swing.JPanel();
        cbDelete3_1 = new javax.swing.JCheckBox();
        cbView3_1 = new javax.swing.JCheckBox();
        cbInsert3_1 = new javax.swing.JCheckBox();
        cbView3 = new javax.swing.JCheckBox();
        cbDelete3 = new javax.swing.JCheckBox();
        cbInsert3 = new javax.swing.JCheckBox();
        cbUpdate3 = new javax.swing.JCheckBox();
        jLabel35 = new javax.swing.JLabel();
        cbUpdate3_1 = new javax.swing.JCheckBox();
        jLabel49 = new javax.swing.JLabel();
        cbView3_2 = new javax.swing.JCheckBox();
        cbInsert3_2 = new javax.swing.JCheckBox();
        cbUpdate3_2 = new javax.swing.JCheckBox();
        cbDelete3_2 = new javax.swing.JCheckBox();
        jLabel50 = new javax.swing.JLabel();
        cbView3_3 = new javax.swing.JCheckBox();
        cbInsert3_3 = new javax.swing.JCheckBox();
        cbUpdate3_3 = new javax.swing.JCheckBox();
        cbDelete3_3 = new javax.swing.JCheckBox();
        cbDelete3_4 = new javax.swing.JCheckBox();
        cbUpdate3_4 = new javax.swing.JCheckBox();
        cbInsert3_4 = new javax.swing.JCheckBox();
        cbView3_4 = new javax.swing.JCheckBox();
        jLabel68 = new javax.swing.JLabel();
        cbDelete3_5 = new javax.swing.JCheckBox();
        cbUpdate3_5 = new javax.swing.JCheckBox();
        cbInsert3_5 = new javax.swing.JCheckBox();
        cbView3_5 = new javax.swing.JCheckBox();
        jLabel69 = new javax.swing.JLabel();
        panel_QC = new javax.swing.JPanel();
        cbInsert4 = new javax.swing.JCheckBox();
        cbInsert4_1 = new javax.swing.JCheckBox();
        cbView4 = new javax.swing.JCheckBox();
        cbUpdate4 = new javax.swing.JCheckBox();
        jLabel36 = new javax.swing.JLabel();
        cbUpdate4_1 = new javax.swing.JCheckBox();
        cbDelete4 = new javax.swing.JCheckBox();
        cbDelete4_1 = new javax.swing.JCheckBox();
        cbView4_1 = new javax.swing.JCheckBox();
        jLabel37 = new javax.swing.JLabel();
        cbView4_2 = new javax.swing.JCheckBox();
        cbInsert4_2 = new javax.swing.JCheckBox();
        cbUpdate4_2 = new javax.swing.JCheckBox();
        cbDelete4_2 = new javax.swing.JCheckBox();
        jLabel38 = new javax.swing.JLabel();
        cbView4_3 = new javax.swing.JCheckBox();
        cbInsert4_3 = new javax.swing.JCheckBox();
        cbUpdate4_3 = new javax.swing.JCheckBox();
        cbDelete4_3 = new javax.swing.JCheckBox();
        cbView4_4 = new javax.swing.JCheckBox();
        cbInsert4_4 = new javax.swing.JCheckBox();
        cbDelete4_4 = new javax.swing.JCheckBox();
        jLabel39 = new javax.swing.JLabel();
        cbUpdate4_4 = new javax.swing.JCheckBox();
        panel_HRD = new javax.swing.JPanel();
        cbInsert5 = new javax.swing.JCheckBox();
        cbInsert5_1 = new javax.swing.JCheckBox();
        cbView5 = new javax.swing.JCheckBox();
        cbUpdate5 = new javax.swing.JCheckBox();
        jLabel40 = new javax.swing.JLabel();
        cbUpdate5_1 = new javax.swing.JCheckBox();
        cbDelete5 = new javax.swing.JCheckBox();
        cbDelete5_1 = new javax.swing.JCheckBox();
        cbView5_1 = new javax.swing.JCheckBox();
        jLabel41 = new javax.swing.JLabel();
        cbUpdate5_2 = new javax.swing.JCheckBox();
        cbDelete5_2 = new javax.swing.JCheckBox();
        cbView5_2 = new javax.swing.JCheckBox();
        cbInsert5_2 = new javax.swing.JCheckBox();
        cbInsert5_3 = new javax.swing.JCheckBox();
        jLabel42 = new javax.swing.JLabel();
        cbUpdate5_3 = new javax.swing.JCheckBox();
        cbDelete5_3 = new javax.swing.JCheckBox();
        cbView5_3 = new javax.swing.JCheckBox();
        cbUpdate5_4 = new javax.swing.JCheckBox();
        cbView5_4 = new javax.swing.JCheckBox();
        jLabel43 = new javax.swing.JLabel();
        cbDelete5_4 = new javax.swing.JCheckBox();
        cbInsert5_4 = new javax.swing.JCheckBox();
        cbInsert5_5 = new javax.swing.JCheckBox();
        jLabel44 = new javax.swing.JLabel();
        cbView5_5 = new javax.swing.JCheckBox();
        cbDelete5_5 = new javax.swing.JCheckBox();
        cbUpdate5_5 = new javax.swing.JCheckBox();
        cbView5_6 = new javax.swing.JCheckBox();
        jLabel48 = new javax.swing.JLabel();
        cbInsert5_6 = new javax.swing.JCheckBox();
        cbUpdate5_6 = new javax.swing.JCheckBox();
        cbDelete5_6 = new javax.swing.JCheckBox();
        cbInsert5_7 = new javax.swing.JCheckBox();
        jLabel60 = new javax.swing.JLabel();
        cbView5_7 = new javax.swing.JCheckBox();
        cbDelete5_7 = new javax.swing.JCheckBox();
        cbUpdate5_7 = new javax.swing.JCheckBox();
        cbInsert5_8 = new javax.swing.JCheckBox();
        jLabel61 = new javax.swing.JLabel();
        cbView5_8 = new javax.swing.JCheckBox();
        cbDelete5_8 = new javax.swing.JCheckBox();
        cbUpdate5_8 = new javax.swing.JCheckBox();
        cbInsert5_9 = new javax.swing.JCheckBox();
        jLabel62 = new javax.swing.JLabel();
        cbView5_9 = new javax.swing.JCheckBox();
        cbDelete5_9 = new javax.swing.JCheckBox();
        cbUpdate5_9 = new javax.swing.JCheckBox();
        cbUpdate5_10 = new javax.swing.JCheckBox();
        cbDelete5_10 = new javax.swing.JCheckBox();
        cbView5_10 = new javax.swing.JCheckBox();
        jLabel67 = new javax.swing.JLabel();
        cbInsert5_10 = new javax.swing.JCheckBox();
        cbUpdate5_11 = new javax.swing.JCheckBox();
        cbDelete5_11 = new javax.swing.JCheckBox();
        jLabel72 = new javax.swing.JLabel();
        cbInsert5_11 = new javax.swing.JCheckBox();
        cbView5_11 = new javax.swing.JCheckBox();
        panel_Keuangan = new javax.swing.JPanel();
        cbInsertKeuangan = new javax.swing.JCheckBox();
        cbViewKeuangan = new javax.swing.JCheckBox();
        cbUpdateKeuangan = new javax.swing.JCheckBox();
        cbDeleteKeuangan = new javax.swing.JCheckBox();
        cbInsertKeuangan_2 = new javax.swing.JCheckBox();
        cbUpdateKeuangan_2 = new javax.swing.JCheckBox();
        cbDeleteKeuangan_2 = new javax.swing.JCheckBox();
        jLabel45 = new javax.swing.JLabel();
        cbViewKeuangan_2 = new javax.swing.JCheckBox();
        cbViewKeuangan_1 = new javax.swing.JCheckBox();
        jLabel53 = new javax.swing.JLabel();
        cbDeleteKeuangan_1 = new javax.swing.JCheckBox();
        cbUpdateKeuangan_1 = new javax.swing.JCheckBox();
        cbInsertKeuangan_1 = new javax.swing.JCheckBox();
        cbInsertKeuangan_5 = new javax.swing.JCheckBox();
        jLabel75 = new javax.swing.JLabel();
        cbUpdateKeuangan_4 = new javax.swing.JCheckBox();
        cbViewKeuangan_4 = new javax.swing.JCheckBox();
        cbInsertKeuangan_4 = new javax.swing.JCheckBox();
        cbViewKeuangan_11 = new javax.swing.JCheckBox();
        cbDeleteKeuangan_11 = new javax.swing.JCheckBox();
        cbDeleteKeuangan_4 = new javax.swing.JCheckBox();
        cbViewKeuangan_10 = new javax.swing.JCheckBox();
        jLabel76 = new javax.swing.JLabel();
        cbInsertKeuangan_10 = new javax.swing.JCheckBox();
        cbViewKeuangan_3 = new javax.swing.JCheckBox();
        cbUpdateKeuangan_10 = new javax.swing.JCheckBox();
        cbInsertKeuangan_3 = new javax.swing.JCheckBox();
        cbUpdateKeuangan_3 = new javax.swing.JCheckBox();
        cbDeleteKeuangan_3 = new javax.swing.JCheckBox();
        cbViewKeuangan_12 = new javax.swing.JCheckBox();
        cbInsertKeuangan_12 = new javax.swing.JCheckBox();
        cbUpdateKeuangan_11 = new javax.swing.JCheckBox();
        jLabel81 = new javax.swing.JLabel();
        cbInsertKeuangan_11 = new javax.swing.JCheckBox();
        cbUpdateKeuangan_7 = new javax.swing.JCheckBox();
        jLabel77 = new javax.swing.JLabel();
        cbDeleteKeuangan_7 = new javax.swing.JCheckBox();
        cbUpdateKeuangan_6 = new javax.swing.JCheckBox();
        cbViewKeuangan_6 = new javax.swing.JCheckBox();
        jLabel78 = new javax.swing.JLabel();
        jLabel83 = new javax.swing.JLabel();
        cbUpdateKeuangan_13 = new javax.swing.JCheckBox();
        cbInsertKeuangan_13 = new javax.swing.JCheckBox();
        cbDeleteKeuangan_6 = new javax.swing.JCheckBox();
        jLabel82 = new javax.swing.JLabel();
        cbInsertKeuangan_6 = new javax.swing.JCheckBox();
        cbUpdateKeuangan_12 = new javax.swing.JCheckBox();
        cbUpdateKeuangan_5 = new javax.swing.JCheckBox();
        cbDeleteKeuangan_12 = new javax.swing.JCheckBox();
        cbViewKeuangan_5 = new javax.swing.JCheckBox();
        cbDeleteKeuangan_5 = new javax.swing.JCheckBox();
        cbViewKeuangan_13 = new javax.swing.JCheckBox();
        cbDeleteKeuangan_13 = new javax.swing.JCheckBox();
        cbInsertKeuangan_9 = new javax.swing.JCheckBox();
        cbDeleteKeuangan_9 = new javax.swing.JCheckBox();
        jLabel79 = new javax.swing.JLabel();
        cbUpdateKeuangan_9 = new javax.swing.JCheckBox();
        cbViewKeuangan_8 = new javax.swing.JCheckBox();
        jLabel80 = new javax.swing.JLabel();
        cbDeleteKeuangan_8 = new javax.swing.JCheckBox();
        cbUpdateKeuangan_8 = new javax.swing.JCheckBox();
        cbInsertKeuangan_8 = new javax.swing.JCheckBox();
        cbViewKeuangan_7 = new javax.swing.JCheckBox();
        cbInsertKeuangan_7 = new javax.swing.JCheckBox();
        jLabel84 = new javax.swing.JLabel();
        jLabel85 = new javax.swing.JLabel();
        cbDeleteKeuangan_10 = new javax.swing.JCheckBox();
        cbViewKeuangan_9 = new javax.swing.JCheckBox();
        panel_Keuangan1 = new javax.swing.JPanel();
        cbInsert7 = new javax.swing.JCheckBox();
        cbView7 = new javax.swing.JCheckBox();
        cbUpdate7 = new javax.swing.JCheckBox();
        cbDelete7 = new javax.swing.JCheckBox();
        panel_USER = new javax.swing.JPanel();
        cbView7_1 = new javax.swing.JCheckBox();
        cbDelete7_1 = new javax.swing.JCheckBox();
        cbInsert7_1 = new javax.swing.JCheckBox();
        cbUpdate7_1 = new javax.swing.JCheckBox();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        cbView7_2 = new javax.swing.JCheckBox();
        cbInsert7_2 = new javax.swing.JCheckBox();
        cbUpdate7_2 = new javax.swing.JCheckBox();
        cbDelete7_2 = new javax.swing.JCheckBox();
        jLabel6 = new javax.swing.JLabel();
        cbInsertAll = new javax.swing.JCheckBox();
        cbUpdateAll = new javax.swing.JCheckBox();
        cbViewAll = new javax.swing.JCheckBox();
        cbDeleteAll = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel_create_user.setBackground(new java.awt.Color(204, 204, 255));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Calibri", 1, 24)); // NOI18N
        jLabel1.setText("Create New User");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("Enter Your Username :");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("Enter Your Password :");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("Re-enter Password :");

        txt_username.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        txt_pass_1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        txt_pass_2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        button_pilih_pegawai.setBackground(new java.awt.Color(255, 255, 255));
        button_pilih_pegawai.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_pilih_pegawai.setText("Pilih Pegawai");
        button_pilih_pegawai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_pilih_pegawaiActionPerformed(evt);
            }
        });

        button_simpan.setBackground(new java.awt.Color(255, 255, 255));
        button_simpan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_simpan.setText("Simpan");
        button_simpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_simpanActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(204, 204, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Employee Identity", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Empleyee ID :");

        label_id.setBackground(new java.awt.Color(255, 255, 255));
        label_id.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_id.setText("ID");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Full Name :");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Position :");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("Department :");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("Division :");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Status :");

        label_name.setBackground(new java.awt.Color(255, 255, 255));
        label_name.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_name.setText("Name");

        label_position.setBackground(new java.awt.Color(255, 255, 255));
        label_position.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_position.setText("Positon");

        label_department.setBackground(new java.awt.Color(255, 255, 255));
        label_department.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_department.setText("Dept.");

        label_division.setBackground(new java.awt.Color(255, 255, 255));
        label_division.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_division.setText("Div");

        label_status.setBackground(new java.awt.Color(255, 255, 255));
        label_status.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_status.setText("IN / OUT");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_id, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_name, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_position, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_division, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_status, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_department, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_id, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_name, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_position, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_department, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_division, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_status, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        panel_main_menu.setBackground(new java.awt.Color(255, 255, 255));

        cbDelete_MainMenu1.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete_MainMenu1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete_MainMenu1.setText("Delete");

        cbDelete_MainMenu2.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete_MainMenu2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete_MainMenu2.setText("Delete");

        cbUpdate_MainMenu2.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate_MainMenu2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate_MainMenu2.setText("Update");

        cbUpdate_MainMenu1.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate_MainMenu1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate_MainMenu1.setText("Update");

        cbInsert_MainMenu1.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert_MainMenu1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert_MainMenu1.setText("Insert");

        cbInsert_MainMenu2.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert_MainMenu2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert_MainMenu2.setText("Insert");

        cbView_MainMenu2.setBackground(new java.awt.Color(255, 255, 255));
        cbView_MainMenu2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView_MainMenu2.setText("View");

        jLabel65.setBackground(new java.awt.Color(255, 255, 255));
        jLabel65.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel65.setText("DATA TERLAMBAT / BELUM ABSEN");

        jLabel66.setBackground(new java.awt.Color(255, 255, 255));
        jLabel66.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel66.setText("ISU PRODUKSI");

        cbView_MainMenu1.setBackground(new java.awt.Color(255, 255, 255));
        cbView_MainMenu1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView_MainMenu1.setText("View");

        javax.swing.GroupLayout panel_main_menuLayout = new javax.swing.GroupLayout(panel_main_menu);
        panel_main_menu.setLayout(panel_main_menuLayout);
        panel_main_menuLayout.setHorizontalGroup(
            panel_main_menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_main_menuLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_main_menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel65, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel66, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_main_menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_main_menuLayout.createSequentialGroup()
                        .addComponent(cbView_MainMenu2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInsert_MainMenu2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUpdate_MainMenu2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDelete_MainMenu2))
                    .addGroup(panel_main_menuLayout.createSequentialGroup()
                        .addComponent(cbView_MainMenu1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInsert_MainMenu1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUpdate_MainMenu1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDelete_MainMenu1)))
                .addContainerGap(133, Short.MAX_VALUE))
        );
        panel_main_menuLayout.setVerticalGroup(
            panel_main_menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_main_menuLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_main_menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel66, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView_MainMenu1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDelete_MainMenu1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsert_MainMenu1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdate_MainMenu1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_main_menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel65, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView_MainMenu2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDelete_MainMenu2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsert_MainMenu2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdate_MainMenu2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(391, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Main Menu", panel_main_menu);

        panel_bahanBaku.setBackground(new java.awt.Color(255, 255, 255));

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("DATA SUPPLIER");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("DATA CUSTOMER");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("DATA RUMAH BURUNG");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel15.setText("MASTER GRADE BAKU");

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel16.setText("BAHAN BAKU MASUK");

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel17.setText("LAPORAN PRODUKSI");

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel18.setText("BAHAN BAKU KELUAR");

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel19.setText("REKAP BAHAN BAKU");

        cbView0.setBackground(new java.awt.Color(255, 255, 255));
        cbView0.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView0.setText("MENU BAHAN BAKU");

        cbInsert0.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert0.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert0.setText("Insert");
        cbInsert0.setEnabled(false);
        cbInsert0.setVisible(false);

        cbDelete0.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete0.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete0.setText("Delete");
        cbDelete0.setEnabled(false);
        cbDelete0.setVisible(false);

        cbUpdate0.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate0.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate0.setText("Update");
        cbUpdate0.setEnabled(false);
        cbUpdate0.setVisible(false);

        cbView0_1.setBackground(new java.awt.Color(255, 255, 255));
        cbView0_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView0_1.setText("View");

        cbInsert0_1.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert0_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert0_1.setText("Insert");

        cbUpdate0_1.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate0_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate0_1.setText("Update");

        cbDelete0_1.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete0_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete0_1.setText("Delete");

        cbView0_2.setBackground(new java.awt.Color(255, 255, 255));
        cbView0_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView0_2.setText("View");

        cbInsert0_2.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert0_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert0_2.setText("Insert");

        cbUpdate0_2.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate0_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate0_2.setText("Update");

        cbDelete0_2.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete0_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete0_2.setText("Delete");

        cbView0_3.setBackground(new java.awt.Color(255, 255, 255));
        cbView0_3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView0_3.setText("View");

        cbInsert0_3.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert0_3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert0_3.setText("Insert");

        cbUpdate0_3.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate0_3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate0_3.setText("Update");

        cbDelete0_3.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete0_3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete0_3.setText("Delete");

        cbView0_4.setBackground(new java.awt.Color(255, 255, 255));
        cbView0_4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView0_4.setText("View");

        cbInsert0_4.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert0_4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert0_4.setText("Insert");

        cbUpdate0_4.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate0_4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate0_4.setText("Update");

        cbDelete0_4.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete0_4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete0_4.setText("Delete");

        cbView0_5.setBackground(new java.awt.Color(255, 255, 255));
        cbView0_5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView0_5.setText("View");

        cbInsert0_5.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert0_5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert0_5.setText("Insert");

        cbUpdate0_5.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate0_5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate0_5.setText("Update");

        cbDelete0_5.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete0_5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete0_5.setText("Delete");

        cbView0_6.setBackground(new java.awt.Color(255, 255, 255));
        cbView0_6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView0_6.setText("View");

        cbInsert0_6.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert0_6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert0_6.setText("Insert");

        cbUpdate0_6.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate0_6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate0_6.setText("Update");

        cbDelete0_6.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete0_6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete0_6.setText("Delete");

        jLabel56.setBackground(new java.awt.Color(255, 255, 255));
        jLabel56.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel56.setText("ADJUSTMENT BAHAN BAKU");

        jLabel57.setBackground(new java.awt.Color(255, 255, 255));
        jLabel57.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel57.setText("KARTU CAMPURAN");

        jLabel63.setBackground(new java.awt.Color(255, 255, 255));
        jLabel63.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel63.setText("BONUS PANEN RSB");

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel25.setText("LAPORAN PRODUKSI SESEKAN");

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel26.setText("LAPORAN PRODUKSI SAPON");

        cbDelete0_13.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete0_13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete0_13.setText("Delete");

        cbView0_11.setBackground(new java.awt.Color(255, 255, 255));
        cbView0_11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView0_11.setText("View");

        cbInsert0_11.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert0_11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert0_11.setText("Insert");

        cbUpdate0_11.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate0_11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate0_11.setText("Update");

        cbDelete0_11.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete0_11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete0_11.setText("Delete");

        cbView0_13.setBackground(new java.awt.Color(255, 255, 255));
        cbView0_13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView0_13.setText("View");

        cbInsert0_13.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert0_13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert0_13.setText("Insert");

        cbUpdate0_13.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate0_13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate0_13.setText("Update");

        cbDelete0_14.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete0_14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete0_14.setText("Delete");

        cbView0_7.setBackground(new java.awt.Color(255, 255, 255));
        cbView0_7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView0_7.setText("View");

        cbView0_14.setBackground(new java.awt.Color(255, 255, 255));
        cbView0_14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView0_14.setText("View");

        cbInsert0_7.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert0_7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert0_7.setText("Insert");

        cbInsert0_14.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert0_14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert0_14.setText("Insert");

        cbUpdate0_7.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate0_7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate0_7.setText("Update");

        cbUpdate0_14.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate0_14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate0_14.setText("Update");

        cbDelete0_7.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete0_7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete0_7.setText("Delete");

        cbInsert0_8.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert0_8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert0_8.setText("Insert");

        cbDelete0_8.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete0_8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete0_8.setText("Delete");

        cbView0_8.setBackground(new java.awt.Color(255, 255, 255));
        cbView0_8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView0_8.setText("View");

        cbUpdate0_8.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate0_8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate0_8.setText("Update");

        cbView0_9.setBackground(new java.awt.Color(255, 255, 255));
        cbView0_9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView0_9.setText("View");

        cbInsert0_9.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert0_9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert0_9.setText("Insert");

        cbUpdate0_9.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate0_9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate0_9.setText("Update");

        cbDelete0_9.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete0_9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete0_9.setText("Delete");

        cbView0_10.setBackground(new java.awt.Color(255, 255, 255));
        cbView0_10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView0_10.setText("View");

        cbInsert0_10.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert0_10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert0_10.setText("Insert");

        cbUpdate0_10.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate0_10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate0_10.setText("Update");

        cbDelete0_10.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete0_10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete0_10.setText("Delete");

        jLabel58.setBackground(new java.awt.Color(255, 255, 255));
        jLabel58.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel58.setText("PEMBELIAN BAHAN BAKU");

        cbView0_12.setBackground(new java.awt.Color(255, 255, 255));
        cbView0_12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView0_12.setText("View");

        cbDelete0_12.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete0_12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete0_12.setText("Delete");

        cbUpdate0_12.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate0_12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate0_12.setText("Update");

        cbInsert0_12.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert0_12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert0_12.setText("Insert");

        jLabel88.setBackground(new java.awt.Color(255, 255, 255));
        jLabel88.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel88.setText("MASTER BULU UPAH");

        cbInsert0_15.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert0_15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert0_15.setText("Insert");

        cbView0_15.setBackground(new java.awt.Color(255, 255, 255));
        cbView0_15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView0_15.setText("View");

        cbDelete0_15.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete0_15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete0_15.setText("Delete");

        cbUpdate0_15.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate0_15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate0_15.setText("Update");

        javax.swing.GroupLayout panel_bahanBakuLayout = new javax.swing.GroupLayout(panel_bahanBaku);
        panel_bahanBaku.setLayout(panel_bahanBakuLayout);
        panel_bahanBakuLayout.setHorizontalGroup(
            panel_bahanBakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_bahanBakuLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_bahanBakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_bahanBakuLayout.createSequentialGroup()
                        .addComponent(cbView0)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInsert0)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUpdate0)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDelete0))
                    .addGroup(panel_bahanBakuLayout.createSequentialGroup()
                        .addGroup(panel_bahanBakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel63, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel58, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel88, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel_bahanBakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panel_bahanBakuLayout.createSequentialGroup()
                                .addComponent(cbView0_1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbInsert0_1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbUpdate0_1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbDelete0_1))
                            .addGroup(panel_bahanBakuLayout.createSequentialGroup()
                                .addComponent(cbView0_2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbInsert0_2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbUpdate0_2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbDelete0_2))
                            .addGroup(panel_bahanBakuLayout.createSequentialGroup()
                                .addComponent(cbView0_3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbInsert0_3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbUpdate0_3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbDelete0_3))
                            .addGroup(panel_bahanBakuLayout.createSequentialGroup()
                                .addComponent(cbView0_4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbInsert0_4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbUpdate0_4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbDelete0_4))
                            .addGroup(panel_bahanBakuLayout.createSequentialGroup()
                                .addComponent(cbView0_5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbInsert0_5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbUpdate0_5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbDelete0_5))
                            .addGroup(panel_bahanBakuLayout.createSequentialGroup()
                                .addComponent(cbView0_6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbInsert0_6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbUpdate0_6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbDelete0_6))
                            .addGroup(panel_bahanBakuLayout.createSequentialGroup()
                                .addComponent(cbView0_9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbInsert0_9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbUpdate0_9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbDelete0_9))
                            .addGroup(panel_bahanBakuLayout.createSequentialGroup()
                                .addComponent(cbView0_10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbInsert0_10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbUpdate0_10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbDelete0_10))
                            .addGroup(panel_bahanBakuLayout.createSequentialGroup()
                                .addComponent(cbView0_11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbInsert0_11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbUpdate0_11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbDelete0_11))
                            .addGroup(panel_bahanBakuLayout.createSequentialGroup()
                                .addComponent(cbView0_13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbInsert0_13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbUpdate0_13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbDelete0_13))
                            .addGroup(panel_bahanBakuLayout.createSequentialGroup()
                                .addComponent(cbView0_14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbInsert0_14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbUpdate0_14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbDelete0_14))
                            .addGroup(panel_bahanBakuLayout.createSequentialGroup()
                                .addComponent(cbView0_7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbInsert0_7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbUpdate0_7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbDelete0_7))
                            .addGroup(panel_bahanBakuLayout.createSequentialGroup()
                                .addComponent(cbView0_8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbInsert0_8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbUpdate0_8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbDelete0_8))
                            .addGroup(panel_bahanBakuLayout.createSequentialGroup()
                                .addComponent(cbView0_12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbInsert0_12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbUpdate0_12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbDelete0_12))
                            .addGroup(panel_bahanBakuLayout.createSequentialGroup()
                                .addComponent(cbView0_15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbInsert0_15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbUpdate0_15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbDelete0_15)))))
                .addContainerGap(133, Short.MAX_VALUE))
        );
        panel_bahanBakuLayout.setVerticalGroup(
            panel_bahanBakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_bahanBakuLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_bahanBakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbDelete0)
                    .addComponent(cbInsert0)
                    .addComponent(cbUpdate0)
                    .addComponent(cbView0))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panel_bahanBakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView0_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDelete0_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsert0_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdate0_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_bahanBakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView0_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDelete0_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsert0_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdate0_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_bahanBakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView0_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDelete0_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsert0_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdate0_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_bahanBakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView0_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDelete0_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsert0_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdate0_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_bahanBakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView0_5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDelete0_5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsert0_5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdate0_5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_bahanBakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView0_6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDelete0_6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsert0_6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdate0_6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_bahanBakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_bahanBakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cbView0_7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbDelete0_7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbInsert0_7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbUpdate0_7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_bahanBakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_bahanBakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cbView0_8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbDelete0_8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbInsert0_8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbUpdate0_8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_bahanBakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_bahanBakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cbView0_9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbDelete0_9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbInsert0_9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbUpdate0_9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_bahanBakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_bahanBakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cbView0_10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbDelete0_10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbInsert0_10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbUpdate0_10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_bahanBakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_bahanBakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cbView0_11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbDelete0_11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbInsert0_11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbUpdate0_11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_bahanBakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_bahanBakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cbView0_12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbDelete0_12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbInsert0_12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbUpdate0_12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel58, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_bahanBakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_bahanBakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cbView0_13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbDelete0_13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbInsert0_13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbUpdate0_13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_bahanBakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel63, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView0_14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDelete0_14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsert0_14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdate0_14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_bahanBakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_bahanBakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cbView0_15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbDelete0_15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbInsert0_15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbUpdate0_15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel88, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(27, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Baku", panel_bahanBaku);

        panel_Produksi.setBackground(new java.awt.Color(255, 255, 255));

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel20.setText("DATA RENDAM");

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel21.setText("DATA CUCI");

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel22.setText("DATA CABUT");

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel23.setText("DATA CETAK");

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel24.setText("DATA FINISHING 2");

        jLabel27.setBackground(new java.awt.Color(255, 255, 255));
        jLabel27.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel27.setText("PROGRESS LP");

        cbView1.setBackground(new java.awt.Color(255, 255, 255));
        cbView1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView1.setText("MENU PRODUKSI");

        cbDelete1.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete1.setText("Delete");
        cbDelete1.setEnabled(false);
        cbDelete1.setVisible(false);

        cbInsert1.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert1.setText("Insert");
        cbInsert1.setEnabled(false);
        cbInsert1.setVisible(false);

        cbUpdate1.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate1.setText("Update");
        cbUpdate1.setEnabled(false);
        cbUpdate1.setVisible(false);

        cbView1_5.setBackground(new java.awt.Color(255, 255, 255));
        cbView1_5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView1_5.setText("View");

        cbInsert1_5.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert1_5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert1_5.setText("Insert");

        cbUpdate1_5.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate1_5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate1_5.setText("Update");

        cbDelete1_5.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete1_5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete1_5.setText("Delete");

        cbView1_3.setBackground(new java.awt.Color(255, 255, 255));
        cbView1_3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView1_3.setText("View");

        cbView1_4.setBackground(new java.awt.Color(255, 255, 255));
        cbView1_4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView1_4.setText("View");

        cbInsert1_4.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert1_4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert1_4.setText("Insert");

        cbInsert1_3.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert1_3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert1_3.setText("Insert");

        cbUpdate1_3.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate1_3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate1_3.setText("Update");

        cbUpdate1_4.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate1_4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate1_4.setText("Update");

        cbDelete1_4.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete1_4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete1_4.setText("Delete");

        cbDelete1_3.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete1_3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete1_3.setText("Delete");

        cbView1_6.setBackground(new java.awt.Color(255, 255, 255));
        cbView1_6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView1_6.setText("View");

        cbInsert1_6.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert1_6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert1_6.setText("Insert");

        cbView1_1.setBackground(new java.awt.Color(255, 255, 255));
        cbView1_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView1_1.setText("View");

        cbUpdate1_6.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate1_6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate1_6.setText("Update");

        cbInsert1_1.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert1_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert1_1.setText("Insert");

        cbUpdate1_1.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate1_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate1_1.setText("Update");

        cbDelete1_6.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete1_6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete1_6.setText("Delete");

        cbDelete1_1.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete1_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete1_1.setText("Delete");

        cbView1_2.setBackground(new java.awt.Color(255, 255, 255));
        cbView1_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView1_2.setText("View");

        cbInsert1_2.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert1_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert1_2.setText("Insert");

        cbUpdate1_2.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate1_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate1_2.setText("Update");

        cbDelete1_2.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete1_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete1_2.setText("Delete");

        javax.swing.GroupLayout panel_ProduksiLayout = new javax.swing.GroupLayout(panel_Produksi);
        panel_Produksi.setLayout(panel_ProduksiLayout);
        panel_ProduksiLayout.setHorizontalGroup(
            panel_ProduksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_ProduksiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_ProduksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_ProduksiLayout.createSequentialGroup()
                        .addComponent(cbView1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInsert1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUpdate1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDelete1))
                    .addGroup(panel_ProduksiLayout.createSequentialGroup()
                        .addGroup(panel_ProduksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel_ProduksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panel_ProduksiLayout.createSequentialGroup()
                                .addComponent(cbView1_1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbInsert1_1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbUpdate1_1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbDelete1_1))
                            .addGroup(panel_ProduksiLayout.createSequentialGroup()
                                .addComponent(cbView1_2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbInsert1_2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbUpdate1_2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbDelete1_2))
                            .addGroup(panel_ProduksiLayout.createSequentialGroup()
                                .addComponent(cbView1_3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbInsert1_3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbUpdate1_3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbDelete1_3))
                            .addGroup(panel_ProduksiLayout.createSequentialGroup()
                                .addComponent(cbView1_4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbInsert1_4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbUpdate1_4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbDelete1_4))
                            .addGroup(panel_ProduksiLayout.createSequentialGroup()
                                .addComponent(cbView1_6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbInsert1_6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbUpdate1_6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbDelete1_6))
                            .addGroup(panel_ProduksiLayout.createSequentialGroup()
                                .addComponent(cbView1_5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbInsert1_5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbUpdate1_5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbDelete1_5)))))
                .addGap(0, 133, Short.MAX_VALUE))
        );
        panel_ProduksiLayout.setVerticalGroup(
            panel_ProduksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_ProduksiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_ProduksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbView1)
                    .addComponent(cbDelete1)
                    .addComponent(cbInsert1)
                    .addComponent(cbUpdate1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panel_ProduksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView1_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDelete1_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsert1_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdate1_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_ProduksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView1_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDelete1_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsert1_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdate1_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_ProduksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView1_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDelete1_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsert1_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdate1_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_ProduksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView1_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDelete1_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsert1_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdate1_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_ProduksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView1_5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDelete1_5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsert1_5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdate1_5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_ProduksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView1_6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDelete1_6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsert1_6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdate1_6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(261, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Produksi", panel_Produksi);

        panel_atb.setBackground(new java.awt.Color(255, 255, 255));

        cbDeleteATB_1.setBackground(new java.awt.Color(255, 255, 255));
        cbDeleteATB_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDeleteATB_1.setText("Delete");

        cbDeleteATB_2.setBackground(new java.awt.Color(255, 255, 255));
        cbDeleteATB_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDeleteATB_2.setText("Delete");

        cbUpdateATB_2.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdateATB_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdateATB_2.setText("Update");

        cbUpdateATB_1.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdateATB_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdateATB_1.setText("Update");

        cbInsertATB_1.setBackground(new java.awt.Color(255, 255, 255));
        cbInsertATB_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsertATB_1.setText("Insert");

        cbInsertATB_2.setBackground(new java.awt.Color(255, 255, 255));
        cbInsertATB_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsertATB_2.setText("Insert");

        cbViewATB_2.setBackground(new java.awt.Color(255, 255, 255));
        cbViewATB_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbViewATB_2.setText("View");

        jLabel73.setBackground(new java.awt.Color(255, 255, 255));
        jLabel73.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel73.setText("DATA PRODUKSI ATB");

        jLabel74.setBackground(new java.awt.Color(255, 255, 255));
        jLabel74.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel74.setText("DATA KARYAWAN ATB");

        cbViewATB_1.setBackground(new java.awt.Color(255, 255, 255));
        cbViewATB_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbViewATB_1.setText("View");

        cbDeleteATB.setBackground(new java.awt.Color(255, 255, 255));
        cbDeleteATB.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDeleteATB.setText("Delete");
        cbDeleteATB.setEnabled(false);
        cbDelete1.setVisible(false);

        cbViewATB.setBackground(new java.awt.Color(255, 255, 255));
        cbViewATB.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbViewATB.setText("MENU ATB");

        cbUpdateATB.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdateATB.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdateATB.setText("Update");
        cbUpdateATB.setEnabled(false);
        cbUpdate1.setVisible(false);

        cbInsertATB.setBackground(new java.awt.Color(255, 255, 255));
        cbInsertATB.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsertATB.setText("Insert");
        cbInsertATB.setEnabled(false);
        cbInsert1.setVisible(false);

        javax.swing.GroupLayout panel_atbLayout = new javax.swing.GroupLayout(panel_atb);
        panel_atb.setLayout(panel_atbLayout);
        panel_atbLayout.setHorizontalGroup(
            panel_atbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_atbLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_atbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_atbLayout.createSequentialGroup()
                        .addComponent(cbViewATB)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInsertATB)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUpdateATB)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDeleteATB))
                    .addGroup(panel_atbLayout.createSequentialGroup()
                        .addGroup(panel_atbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel73, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel74, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel_atbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panel_atbLayout.createSequentialGroup()
                                .addComponent(cbViewATB_2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbInsertATB_2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbUpdateATB_2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbDeleteATB_2))
                            .addGroup(panel_atbLayout.createSequentialGroup()
                                .addComponent(cbViewATB_1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbInsertATB_1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbUpdateATB_1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbDeleteATB_1)))))
                .addContainerGap(133, Short.MAX_VALUE))
        );
        panel_atbLayout.setVerticalGroup(
            panel_atbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_atbLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_atbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbViewATB)
                    .addComponent(cbDeleteATB)
                    .addComponent(cbInsertATB)
                    .addComponent(cbUpdateATB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panel_atbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel74, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbViewATB_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDeleteATB_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsertATB_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdateATB_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_atbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel73, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbViewATB_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDeleteATB_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsertATB_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdateATB_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(365, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("ATB", panel_atb);

        panel_manajemen_sub.setBackground(new java.awt.Color(255, 255, 255));

        cbDeleteSub_1.setBackground(new java.awt.Color(255, 255, 255));
        cbDeleteSub_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDeleteSub_1.setText("Delete");

        cbViewSub_1.setBackground(new java.awt.Color(255, 255, 255));
        cbViewSub_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbViewSub_1.setText("View");

        cbInsertSub_1.setBackground(new java.awt.Color(255, 255, 255));
        cbInsertSub_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsertSub_1.setText("Insert");

        cbViewSub.setBackground(new java.awt.Color(255, 255, 255));
        cbViewSub.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbViewSub.setText("MENU MANAJEMEN SUB");

        cbDeleteSub.setBackground(new java.awt.Color(255, 255, 255));
        cbDeleteSub.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDeleteSub.setText("Delete");
        cbDeleteSub.setEnabled(false);
        cbDelete3.setVisible(false);

        cbInsertSub.setBackground(new java.awt.Color(255, 255, 255));
        cbInsertSub.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsertSub.setText("Insert");
        cbInsertSub.setEnabled(false);
        cbInsert3.setVisible(false);

        cbUpdateSub.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdateSub.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdateSub.setText("Update");
        cbUpdateSub.setEnabled(false);
        cbUpdate3.setVisible(false);

        jLabel54.setBackground(new java.awt.Color(255, 255, 255));
        jLabel54.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel54.setText("DATA SUB WALETA");

        cbUpdateSub_1.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdateSub_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdateSub_1.setText("Update");

        jLabel55.setBackground(new java.awt.Color(255, 255, 255));
        jLabel55.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel55.setText("DATA CABUT SUB ONLINE");

        jLabel64.setBackground(new java.awt.Color(255, 255, 255));
        jLabel64.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel64.setText("DATA KARYAWAN SUB");

        jLabel70.setBackground(new java.awt.Color(255, 255, 255));
        jLabel70.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel70.setText("DATA ABSEN SUB");

        jLabel71.setBackground(new java.awt.Color(255, 255, 255));
        jLabel71.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel71.setText("DATA PENGGAJIAN SUB");

        cbDeleteSub_2.setBackground(new java.awt.Color(255, 255, 255));
        cbDeleteSub_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDeleteSub_2.setText("Delete");

        cbViewSub_2.setBackground(new java.awt.Color(255, 255, 255));
        cbViewSub_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbViewSub_2.setText("View");

        cbInsertSub_2.setBackground(new java.awt.Color(255, 255, 255));
        cbInsertSub_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsertSub_2.setText("Insert");

        cbUpdateSub_2.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdateSub_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdateSub_2.setText("Update");

        cbDeleteSub_3.setBackground(new java.awt.Color(255, 255, 255));
        cbDeleteSub_3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDeleteSub_3.setText("Delete");

        cbViewSub_3.setBackground(new java.awt.Color(255, 255, 255));
        cbViewSub_3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbViewSub_3.setText("View");

        cbInsertSub_3.setBackground(new java.awt.Color(255, 255, 255));
        cbInsertSub_3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsertSub_3.setText("Insert");

        cbUpdateSub_3.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdateSub_3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdateSub_3.setText("Update");

        cbDeleteSub_4.setBackground(new java.awt.Color(255, 255, 255));
        cbDeleteSub_4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDeleteSub_4.setText("Delete");

        cbViewSub_4.setBackground(new java.awt.Color(255, 255, 255));
        cbViewSub_4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbViewSub_4.setText("View");

        cbInsertSub_4.setBackground(new java.awt.Color(255, 255, 255));
        cbInsertSub_4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsertSub_4.setText("Insert");

        cbUpdateSub_4.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdateSub_4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdateSub_4.setText("Update");

        cbDeleteSub_5.setBackground(new java.awt.Color(255, 255, 255));
        cbDeleteSub_5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDeleteSub_5.setText("Delete");

        cbViewSub_5.setBackground(new java.awt.Color(255, 255, 255));
        cbViewSub_5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbViewSub_5.setText("View");

        cbInsertSub_5.setBackground(new java.awt.Color(255, 255, 255));
        cbInsertSub_5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsertSub_5.setText("Insert");

        cbUpdateSub_5.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdateSub_5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdateSub_5.setText("Update");

        cbUpdateSub_6.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdateSub_6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdateSub_6.setText("Update");

        cbInsertSub_6.setBackground(new java.awt.Color(255, 255, 255));
        cbInsertSub_6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsertSub_6.setText("Insert");

        cbViewSub_6.setBackground(new java.awt.Color(255, 255, 255));
        cbViewSub_6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbViewSub_6.setText("View");

        cbDeleteSub_6.setBackground(new java.awt.Color(255, 255, 255));
        cbDeleteSub_6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDeleteSub_6.setText("Delete");

        jLabel86.setBackground(new java.awt.Color(255, 255, 255));
        jLabel86.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel86.setText("DATA PIUTANG KARYAWAN SUB");

        cbViewSub_7.setBackground(new java.awt.Color(255, 255, 255));
        cbViewSub_7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbViewSub_7.setText("View");

        jLabel87.setBackground(new java.awt.Color(255, 255, 255));
        jLabel87.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel87.setText("TARIF UPAH & BOBOT LP SUB");

        cbUpdateSub_7.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdateSub_7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdateSub_7.setText("Update");

        cbInsertSub_7.setBackground(new java.awt.Color(255, 255, 255));
        cbInsertSub_7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsertSub_7.setText("Insert");

        cbDeleteSub_7.setBackground(new java.awt.Color(255, 255, 255));
        cbDeleteSub_7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDeleteSub_7.setText("Delete");

        javax.swing.GroupLayout panel_manajemen_subLayout = new javax.swing.GroupLayout(panel_manajemen_sub);
        panel_manajemen_sub.setLayout(panel_manajemen_subLayout);
        panel_manajemen_subLayout.setHorizontalGroup(
            panel_manajemen_subLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_manajemen_subLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_manajemen_subLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_manajemen_subLayout.createSequentialGroup()
                        .addComponent(jLabel71, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbViewSub_5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInsertSub_5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUpdateSub_5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDeleteSub_5))
                    .addGroup(panel_manajemen_subLayout.createSequentialGroup()
                        .addComponent(cbViewSub)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInsertSub)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUpdateSub)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDeleteSub))
                    .addGroup(panel_manajemen_subLayout.createSequentialGroup()
                        .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbViewSub_1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInsertSub_1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUpdateSub_1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDeleteSub_1))
                    .addGroup(panel_manajemen_subLayout.createSequentialGroup()
                        .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbViewSub_2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInsertSub_2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUpdateSub_2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDeleteSub_2))
                    .addGroup(panel_manajemen_subLayout.createSequentialGroup()
                        .addComponent(jLabel64, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbViewSub_3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInsertSub_3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUpdateSub_3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDeleteSub_3))
                    .addGroup(panel_manajemen_subLayout.createSequentialGroup()
                        .addComponent(jLabel70, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbViewSub_4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInsertSub_4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUpdateSub_4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDeleteSub_4))
                    .addGroup(panel_manajemen_subLayout.createSequentialGroup()
                        .addComponent(jLabel86, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbViewSub_6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInsertSub_6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUpdateSub_6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDeleteSub_6))
                    .addGroup(panel_manajemen_subLayout.createSequentialGroup()
                        .addComponent(jLabel87, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbViewSub_7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInsertSub_7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUpdateSub_7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDeleteSub_7)))
                .addContainerGap(133, Short.MAX_VALUE))
        );
        panel_manajemen_subLayout.setVerticalGroup(
            panel_manajemen_subLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_manajemen_subLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_manajemen_subLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbViewSub)
                    .addComponent(cbDeleteSub)
                    .addComponent(cbInsertSub)
                    .addComponent(cbUpdateSub))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panel_manajemen_subLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbViewSub_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDeleteSub_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsertSub_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdateSub_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_manajemen_subLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_manajemen_subLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cbViewSub_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbDeleteSub_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbInsertSub_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbUpdateSub_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_manajemen_subLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_manajemen_subLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cbViewSub_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbDeleteSub_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbInsertSub_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbUpdateSub_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel64, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_manajemen_subLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_manajemen_subLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cbViewSub_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbDeleteSub_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbInsertSub_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbUpdateSub_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel70, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_manajemen_subLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_manajemen_subLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cbViewSub_5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbDeleteSub_5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbInsertSub_5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbUpdateSub_5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel71, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_manajemen_subLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_manajemen_subLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cbViewSub_6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbDeleteSub_6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbInsertSub_6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbUpdateSub_6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel86, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_manajemen_subLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_manajemen_subLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cbViewSub_7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbDeleteSub_7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbInsertSub_7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbUpdateSub_7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel87, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(235, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Sub", panel_manajemen_sub);

        panel_bahanJadi.setBackground(new java.awt.Color(255, 255, 255));

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel28.setText("TUTUPAN GRADING");

        jLabel29.setBackground(new java.awt.Color(255, 255, 255));
        jLabel29.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel29.setText("DATA BOX BAHAN JADI");

        jLabel30.setBackground(new java.awt.Color(255, 255, 255));
        jLabel30.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel30.setText("STOCK BAHAN JADI");

        cbUpdate2_1.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate2_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate2_1.setText("Update");

        cbDelete2_1.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete2_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete2_1.setText("Delete");

        cbView2.setBackground(new java.awt.Color(255, 255, 255));
        cbView2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView2.setText("MENU BARANG JADI");

        cbDelete2.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete2.setText("Delete");
        cbDelete2.setEnabled(false);
        cbDelete2.setVisible(false);

        cbInsert2.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert2.setText("Insert");
        cbInsert2.setEnabled(false);
        cbInsert2.setVisible(false);

        cbUpdate2.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate2.setText("Update");
        cbUpdate2.setEnabled(false);
        cbUpdate2.setVisible(false);

        jLabel31.setBackground(new java.awt.Color(255, 255, 255));
        jLabel31.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel31.setText("GUDANG BAHAN JADI");

        cbView2_1.setBackground(new java.awt.Color(255, 255, 255));
        cbView2_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView2_1.setText("View");

        cbInsert2_1.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert2_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert2_1.setText("Insert");

        cbView2_2.setBackground(new java.awt.Color(255, 255, 255));
        cbView2_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView2_2.setText("View");

        cbInsert2_2.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert2_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert2_2.setText("Insert");

        cbUpdate2_2.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate2_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate2_2.setText("Update");

        cbDelete2_2.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete2_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete2_2.setText("Delete");

        cbUpdate2_3.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate2_3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate2_3.setText("Update");

        cbInsert2_3.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert2_3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert2_3.setText("Insert");

        cbDelete2_3.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete2_3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete2_3.setText("Delete");

        cbView2_3.setBackground(new java.awt.Color(255, 255, 255));
        cbView2_3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView2_3.setText("View");

        cbDelete2_4.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete2_4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete2_4.setText("Delete");

        cbView2_4.setBackground(new java.awt.Color(255, 255, 255));
        cbView2_4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView2_4.setText("View");

        cbUpdate2_4.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate2_4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate2_4.setText("Update");

        cbInsert2_4.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert2_4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert2_4.setText("Insert");

        jLabel32.setBackground(new java.awt.Color(255, 255, 255));
        jLabel32.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel32.setText("MASTER GRADE BAHAN JADI");

        cbView2_5.setBackground(new java.awt.Color(255, 255, 255));
        cbView2_5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView2_5.setText("View");

        cbInsert2_5.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert2_5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert2_5.setText("Insert");

        cbUpdate2_5.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate2_5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate2_5.setText("Update");

        cbDelete2_5.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete2_5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete2_5.setText("Delete");

        jLabel33.setBackground(new java.awt.Color(255, 255, 255));
        jLabel33.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel33.setText("BONUS GRADING");

        cbView2_6.setBackground(new java.awt.Color(255, 255, 255));
        cbView2_6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView2_6.setText("View");

        cbInsert2_6.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert2_6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert2_6.setText("Insert");

        cbUpdate2_6.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate2_6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate2_6.setText("Update");

        cbDelete2_6.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete2_6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete2_6.setText("Delete");

        jLabel34.setBackground(new java.awt.Color(255, 255, 255));
        jLabel34.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel34.setText("LAPORAN PRODUKSI BJ");

        cbInsert2_7.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert2_7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert2_7.setText("Insert");

        cbView2_7.setBackground(new java.awt.Color(255, 255, 255));
        cbView2_7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView2_7.setText("View");

        cbUpdate2_7.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate2_7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate2_7.setText("Update");

        cbDelete2_7.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete2_7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete2_7.setText("Delete");

        cbUpdate2_8.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate2_8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate2_8.setText("Update");

        cbView2_8.setBackground(new java.awt.Color(255, 255, 255));
        cbView2_8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView2_8.setText("View");

        jLabel51.setBackground(new java.awt.Color(255, 255, 255));
        jLabel51.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel51.setText("PEMBELIAN BAHAN JADI");

        cbDelete2_8.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete2_8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete2_8.setText("Delete");

        cbInsert2_8.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert2_8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert2_8.setText("Insert");

        cbInsert2_9.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert2_9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert2_9.setText("Insert");

        cbDelete2_9.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete2_9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete2_9.setText("Delete");

        jLabel52.setBackground(new java.awt.Color(255, 255, 255));
        jLabel52.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel52.setText("PRICE LIST GRADE BJ");

        cbView2_9.setBackground(new java.awt.Color(255, 255, 255));
        cbView2_9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView2_9.setText("View");

        cbUpdate2_9.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate2_9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate2_9.setText("Update");

        javax.swing.GroupLayout panel_bahanJadiLayout = new javax.swing.GroupLayout(panel_bahanJadi);
        panel_bahanJadi.setLayout(panel_bahanJadiLayout);
        panel_bahanJadiLayout.setHorizontalGroup(
            panel_bahanJadiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_bahanJadiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_bahanJadiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_bahanJadiLayout.createSequentialGroup()
                        .addComponent(cbView2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInsert2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUpdate2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDelete2))
                    .addGroup(panel_bahanJadiLayout.createSequentialGroup()
                        .addGroup(panel_bahanJadiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel_bahanJadiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panel_bahanJadiLayout.createSequentialGroup()
                                .addComponent(cbView2_1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbInsert2_1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbUpdate2_1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbDelete2_1))
                            .addGroup(panel_bahanJadiLayout.createSequentialGroup()
                                .addComponent(cbView2_2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbInsert2_2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbUpdate2_2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbDelete2_2))
                            .addGroup(panel_bahanJadiLayout.createSequentialGroup()
                                .addComponent(cbView2_3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbInsert2_3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbUpdate2_3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbDelete2_3))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_bahanJadiLayout.createSequentialGroup()
                                .addComponent(cbView2_4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbInsert2_4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbUpdate2_4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbDelete2_4))
                            .addGroup(panel_bahanJadiLayout.createSequentialGroup()
                                .addComponent(cbView2_5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbInsert2_5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbUpdate2_5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbDelete2_5))
                            .addGroup(panel_bahanJadiLayout.createSequentialGroup()
                                .addComponent(cbView2_6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbInsert2_6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbUpdate2_6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbDelete2_6))
                            .addGroup(panel_bahanJadiLayout.createSequentialGroup()
                                .addComponent(cbView2_7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbInsert2_7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbUpdate2_7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbDelete2_7))
                            .addGroup(panel_bahanJadiLayout.createSequentialGroup()
                                .addComponent(cbView2_8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbInsert2_8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbUpdate2_8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbDelete2_8))
                            .addGroup(panel_bahanJadiLayout.createSequentialGroup()
                                .addComponent(cbView2_9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbInsert2_9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbUpdate2_9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbDelete2_9)))))
                .addContainerGap(133, Short.MAX_VALUE))
        );
        panel_bahanJadiLayout.setVerticalGroup(
            panel_bahanJadiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_bahanJadiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_bahanJadiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbView2)
                    .addComponent(cbDelete2)
                    .addComponent(cbInsert2)
                    .addComponent(cbUpdate2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panel_bahanJadiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView2_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDelete2_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsert2_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdate2_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_bahanJadiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView2_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDelete2_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsert2_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdate2_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_bahanJadiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView2_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDelete2_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsert2_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdate2_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_bahanJadiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView2_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDelete2_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsert2_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdate2_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_bahanJadiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView2_5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDelete2_5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsert2_5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdate2_5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_bahanJadiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView2_6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDelete2_6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsert2_6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdate2_6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_bahanJadiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView2_7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDelete2_7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsert2_7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdate2_7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_bahanJadiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView2_8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDelete2_8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsert2_8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdate2_8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_bahanJadiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView2_9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDelete2_9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsert2_9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdate2_9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Barang Jadi", panel_bahanJadi);

        panel_Packing.setBackground(new java.awt.Color(255, 255, 255));

        cbDelete3_1.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete3_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete3_1.setText("Delete");

        cbView3_1.setBackground(new java.awt.Color(255, 255, 255));
        cbView3_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView3_1.setText("View");

        cbInsert3_1.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert3_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert3_1.setText("Insert");

        cbView3.setBackground(new java.awt.Color(255, 255, 255));
        cbView3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView3.setText("MENU PACKING");

        cbDelete3.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete3.setText("Delete");
        cbDelete3.setEnabled(false);
        cbDelete3.setVisible(false);

        cbInsert3.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert3.setText("Insert");
        cbInsert3.setEnabled(false);
        cbInsert3.setVisible(false);

        cbUpdate3.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate3.setText("Update");
        cbUpdate3.setEnabled(false);
        cbUpdate3.setVisible(false);

        jLabel35.setBackground(new java.awt.Color(255, 255, 255));
        jLabel35.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel35.setText("DATA PACKING");

        cbUpdate3_1.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate3_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate3_1.setText("Update");

        jLabel49.setBackground(new java.awt.Color(255, 255, 255));
        jLabel49.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel49.setText("DATA PENGIRIMAN");

        cbView3_2.setBackground(new java.awt.Color(255, 255, 255));
        cbView3_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView3_2.setText("View");

        cbInsert3_2.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert3_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert3_2.setText("Insert");

        cbUpdate3_2.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate3_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate3_2.setText("Update");

        cbDelete3_2.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete3_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete3_2.setText("Delete");

        jLabel50.setBackground(new java.awt.Color(255, 255, 255));
        jLabel50.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel50.setText("DATA BUYER");

        cbView3_3.setBackground(new java.awt.Color(255, 255, 255));
        cbView3_3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView3_3.setText("View");

        cbInsert3_3.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert3_3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert3_3.setText("Insert");

        cbUpdate3_3.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate3_3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate3_3.setText("Update");

        cbDelete3_3.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete3_3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete3_3.setText("Delete");

        cbDelete3_4.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete3_4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete3_4.setText("Delete");

        cbUpdate3_4.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate3_4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate3_4.setText("Update");

        cbInsert3_4.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert3_4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert3_4.setText("Insert");

        cbView3_4.setBackground(new java.awt.Color(255, 255, 255));
        cbView3_4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView3_4.setText("View");

        jLabel68.setBackground(new java.awt.Color(255, 255, 255));
        jLabel68.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel68.setText("DATA SPK");

        cbDelete3_5.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete3_5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete3_5.setText("Delete");

        cbUpdate3_5.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate3_5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate3_5.setText("Update");

        cbInsert3_5.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert3_5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert3_5.setText("Insert");

        cbView3_5.setBackground(new java.awt.Color(255, 255, 255));
        cbView3_5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView3_5.setText("View");

        jLabel69.setBackground(new java.awt.Color(255, 255, 255));
        jLabel69.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel69.setText("DATA PAYMENT REPORT");

        javax.swing.GroupLayout panel_PackingLayout = new javax.swing.GroupLayout(panel_Packing);
        panel_Packing.setLayout(panel_PackingLayout);
        panel_PackingLayout.setHorizontalGroup(
            panel_PackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_PackingLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_PackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_PackingLayout.createSequentialGroup()
                        .addComponent(jLabel69, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbView3_5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInsert3_5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUpdate3_5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDelete3_5))
                    .addGroup(panel_PackingLayout.createSequentialGroup()
                        .addComponent(cbView3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInsert3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUpdate3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDelete3))
                    .addGroup(panel_PackingLayout.createSequentialGroup()
                        .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbView3_1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInsert3_1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUpdate3_1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDelete3_1))
                    .addGroup(panel_PackingLayout.createSequentialGroup()
                        .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbView3_2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInsert3_2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUpdate3_2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDelete3_2))
                    .addGroup(panel_PackingLayout.createSequentialGroup()
                        .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbView3_3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInsert3_3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUpdate3_3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDelete3_3))
                    .addGroup(panel_PackingLayout.createSequentialGroup()
                        .addComponent(jLabel68, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbView3_4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInsert3_4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUpdate3_4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDelete3_4)))
                .addContainerGap(133, Short.MAX_VALUE))
        );
        panel_PackingLayout.setVerticalGroup(
            panel_PackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_PackingLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_PackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbView3)
                    .addComponent(cbDelete3)
                    .addComponent(cbInsert3)
                    .addComponent(cbUpdate3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panel_PackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView3_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDelete3_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsert3_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdate3_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_PackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView3_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDelete3_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsert3_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdate3_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_PackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView3_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDelete3_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsert3_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdate3_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_PackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel68, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView3_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDelete3_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsert3_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdate3_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_PackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel69, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView3_5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDelete3_5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsert3_5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdate3_5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(287, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Packing", panel_Packing);

        panel_QC.setBackground(new java.awt.Color(255, 255, 255));

        cbInsert4.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert4.setText("Insert");
        cbInsert4.setEnabled(false);
        cbInsert4.setVisible(false);

        cbInsert4_1.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert4_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert4_1.setText("Insert");

        cbView4.setBackground(new java.awt.Color(255, 255, 255));
        cbView4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView4.setText("MENU QC");

        cbUpdate4.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate4.setText("Update");
        cbUpdate4.setEnabled(false);
        cbUpdate4.setVisible(false);

        jLabel36.setBackground(new java.awt.Color(255, 255, 255));
        jLabel36.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel36.setText("DATA LAB BAHAN BAKU");

        cbUpdate4_1.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate4_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate4_1.setText("Update");

        cbDelete4.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete4.setText("Delete");
        cbDelete4.setEnabled(false);
        cbDelete4.setVisible(false);

        cbDelete4_1.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete4_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete4_1.setText("Delete");

        cbView4_1.setBackground(new java.awt.Color(255, 255, 255));
        cbView4_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView4_1.setText("View");

        jLabel37.setBackground(new java.awt.Color(255, 255, 255));
        jLabel37.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel37.setText("DATA LAB LP");

        cbView4_2.setBackground(new java.awt.Color(255, 255, 255));
        cbView4_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView4_2.setText("View");

        cbInsert4_2.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert4_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert4_2.setText("Insert");

        cbUpdate4_2.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate4_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate4_2.setText("Update");

        cbDelete4_2.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete4_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete4_2.setText("Delete");

        jLabel38.setBackground(new java.awt.Color(255, 255, 255));
        jLabel38.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel38.setText("DATA TREATMENT LP");

        cbView4_3.setBackground(new java.awt.Color(255, 255, 255));
        cbView4_3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView4_3.setText("View");

        cbInsert4_3.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert4_3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert4_3.setText("Insert");

        cbUpdate4_3.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate4_3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate4_3.setText("Update");

        cbDelete4_3.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete4_3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete4_3.setText("Delete");

        cbView4_4.setBackground(new java.awt.Color(255, 255, 255));
        cbView4_4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView4_4.setText("View");

        cbInsert4_4.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert4_4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert4_4.setText("Insert");

        cbDelete4_4.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete4_4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete4_4.setText("Delete");

        jLabel39.setBackground(new java.awt.Color(255, 255, 255));
        jLabel39.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel39.setText("DATA PEMANASAN BJ");

        cbUpdate4_4.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate4_4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate4_4.setText("Update");

        javax.swing.GroupLayout panel_QCLayout = new javax.swing.GroupLayout(panel_QC);
        panel_QC.setLayout(panel_QCLayout);
        panel_QCLayout.setHorizontalGroup(
            panel_QCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_QCLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_QCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panel_QCLayout.createSequentialGroup()
                        .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbView4_2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInsert4_2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUpdate4_2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDelete4_2))
                    .addGroup(panel_QCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panel_QCLayout.createSequentialGroup()
                            .addComponent(cbView4)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cbInsert4)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cbUpdate4)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cbDelete4))
                        .addGroup(panel_QCLayout.createSequentialGroup()
                            .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cbView4_1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cbInsert4_1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cbUpdate4_1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cbDelete4_1)))
                    .addGroup(panel_QCLayout.createSequentialGroup()
                        .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbView4_3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInsert4_3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUpdate4_3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDelete4_3))
                    .addGroup(panel_QCLayout.createSequentialGroup()
                        .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbView4_4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInsert4_4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUpdate4_4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDelete4_4)))
                .addContainerGap(133, Short.MAX_VALUE))
        );
        panel_QCLayout.setVerticalGroup(
            panel_QCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_QCLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_QCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbView4)
                    .addComponent(cbDelete4)
                    .addComponent(cbInsert4)
                    .addComponent(cbUpdate4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panel_QCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView4_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDelete4_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsert4_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdate4_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_QCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView4_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDelete4_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsert4_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdate4_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_QCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView4_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDelete4_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsert4_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdate4_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_QCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView4_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDelete4_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsert4_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdate4_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(313, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("QC", panel_QC);

        panel_HRD.setBackground(new java.awt.Color(255, 255, 255));

        cbInsert5.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert5.setText("Insert");
        cbInsert5.setEnabled(false);
        cbInsert5.setVisible(false);

        cbInsert5_1.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert5_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert5_1.setText("Insert");

        cbView5.setBackground(new java.awt.Color(255, 255, 255));
        cbView5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView5.setText("MENU HRD");

        cbUpdate5.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate5.setText("Update");
        cbUpdate5.setEnabled(false);
        cbUpdate5.setVisible(false);

        jLabel40.setBackground(new java.awt.Color(255, 255, 255));
        jLabel40.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel40.setText("DATA KARYAWAN");

        cbUpdate5_1.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate5_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate5_1.setText("Update");

        cbDelete5.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete5.setText("Delete");
        cbDelete5.setEnabled(false);
        cbDelete5.setVisible(false);

        cbDelete5_1.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete5_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete5_1.setText("Delete");

        cbView5_1.setBackground(new java.awt.Color(255, 255, 255));
        cbView5_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView5_1.setText("View");

        jLabel41.setBackground(new java.awt.Color(255, 255, 255));
        jLabel41.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel41.setText("DATA IJIN KELUAR");

        cbUpdate5_2.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate5_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate5_2.setText("Update");

        cbDelete5_2.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete5_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete5_2.setText("Delete");

        cbView5_2.setBackground(new java.awt.Color(255, 255, 255));
        cbView5_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView5_2.setText("View");

        cbInsert5_2.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert5_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert5_2.setText("Insert");

        cbInsert5_3.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert5_3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert5_3.setText("Insert");

        jLabel42.setBackground(new java.awt.Color(255, 255, 255));
        jLabel42.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel42.setText("DATA IJIN ABSEN / CUTI");

        cbUpdate5_3.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate5_3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate5_3.setText("Update");

        cbDelete5_3.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete5_3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete5_3.setText("Delete");

        cbView5_3.setBackground(new java.awt.Color(255, 255, 255));
        cbView5_3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView5_3.setText("View");

        cbUpdate5_4.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate5_4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate5_4.setText("Update");

        cbView5_4.setBackground(new java.awt.Color(255, 255, 255));
        cbView5_4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView5_4.setText("View");

        jLabel43.setBackground(new java.awt.Color(255, 255, 255));
        jLabel43.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel43.setText("DATA LEMBUR");

        cbDelete5_4.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete5_4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete5_4.setText("Delete");

        cbInsert5_4.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert5_4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert5_4.setText("Insert");

        cbInsert5_5.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert5_5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert5_5.setText("Insert");

        jLabel44.setBackground(new java.awt.Color(255, 255, 255));
        jLabel44.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel44.setText("DAFTAR DEPT. & BAGIAN");

        cbView5_5.setBackground(new java.awt.Color(255, 255, 255));
        cbView5_5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView5_5.setText("View");

        cbDelete5_5.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete5_5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete5_5.setText("Delete");

        cbUpdate5_5.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate5_5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate5_5.setText("Update");

        cbView5_6.setBackground(new java.awt.Color(255, 255, 255));
        cbView5_6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView5_6.setText("View");

        jLabel48.setBackground(new java.awt.Color(255, 255, 255));
        jLabel48.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel48.setText("DATA PINDAH BAGIAN");

        cbInsert5_6.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert5_6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert5_6.setText("Insert");

        cbUpdate5_6.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate5_6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate5_6.setText("Update");

        cbDelete5_6.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete5_6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete5_6.setText("Delete");

        cbInsert5_7.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert5_7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert5_7.setText("Insert");

        jLabel60.setBackground(new java.awt.Color(255, 255, 255));
        jLabel60.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel60.setText("DATA TANGGAL LIBUR");

        cbView5_7.setBackground(new java.awt.Color(255, 255, 255));
        cbView5_7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView5_7.setText("View");

        cbDelete5_7.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete5_7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete5_7.setText("Delete");

        cbUpdate5_7.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate5_7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate5_7.setText("Update");

        cbInsert5_8.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert5_8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert5_8.setText("Insert");

        jLabel61.setBackground(new java.awt.Color(255, 255, 255));
        jLabel61.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel61.setText("DATA TEMAN BAWA TEMAN");

        cbView5_8.setBackground(new java.awt.Color(255, 255, 255));
        cbView5_8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView5_8.setText("View");

        cbDelete5_8.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete5_8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete5_8.setText("Delete");

        cbUpdate5_8.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate5_8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate5_8.setText("Update");

        cbInsert5_9.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert5_9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert5_9.setText("Insert");

        jLabel62.setBackground(new java.awt.Color(255, 255, 255));
        jLabel62.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel62.setText("DATA ABSEN");

        cbView5_9.setBackground(new java.awt.Color(255, 255, 255));
        cbView5_9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView5_9.setText("View");

        cbDelete5_9.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete5_9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete5_9.setText("Delete");

        cbUpdate5_9.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate5_9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate5_9.setText("Update");

        cbUpdate5_10.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate5_10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate5_10.setText("Update");

        cbDelete5_10.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete5_10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete5_10.setText("Delete");

        cbView5_10.setBackground(new java.awt.Color(255, 255, 255));
        cbView5_10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView5_10.setText("View");

        jLabel67.setBackground(new java.awt.Color(255, 255, 255));
        jLabel67.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel67.setText("DATA JAM KERJA");

        cbInsert5_10.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert5_10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert5_10.setText("Insert");

        cbUpdate5_11.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate5_11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate5_11.setText("Update");

        cbDelete5_11.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete5_11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete5_11.setText("Delete");

        jLabel72.setBackground(new java.awt.Color(255, 255, 255));
        jLabel72.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel72.setText("DATA JALUR JEMPUTAN");

        cbInsert5_11.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert5_11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert5_11.setText("Insert");

        cbView5_11.setBackground(new java.awt.Color(255, 255, 255));
        cbView5_11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView5_11.setText("View");

        javax.swing.GroupLayout panel_HRDLayout = new javax.swing.GroupLayout(panel_HRD);
        panel_HRD.setLayout(panel_HRDLayout);
        panel_HRDLayout.setHorizontalGroup(
            panel_HRDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_HRDLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_HRDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panel_HRDLayout.createSequentialGroup()
                        .addComponent(jLabel62, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbView5_9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInsert5_9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUpdate5_9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDelete5_9))
                    .addGroup(panel_HRDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panel_HRDLayout.createSequentialGroup()
                            .addComponent(jLabel61, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cbView5_8)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cbInsert5_8)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cbUpdate5_8)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cbDelete5_8))
                        .addGroup(panel_HRDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(panel_HRDLayout.createSequentialGroup()
                                .addComponent(jLabel60, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbView5_7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbInsert5_7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbUpdate5_7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbDelete5_7))
                            .addGroup(panel_HRDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(panel_HRDLayout.createSequentialGroup()
                                    .addComponent(cbView5)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cbInsert5)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cbUpdate5)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cbDelete5))
                                .addGroup(panel_HRDLayout.createSequentialGroup()
                                    .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cbView5_1)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cbInsert5_1)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cbUpdate5_1)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cbDelete5_1))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_HRDLayout.createSequentialGroup()
                                    .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cbView5_2)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cbInsert5_2)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cbUpdate5_2)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cbDelete5_2))
                                .addGroup(panel_HRDLayout.createSequentialGroup()
                                    .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cbView5_3)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cbInsert5_3)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cbUpdate5_3)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cbDelete5_3))
                                .addGroup(panel_HRDLayout.createSequentialGroup()
                                    .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cbView5_4)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cbInsert5_4)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cbUpdate5_4)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cbDelete5_4))
                                .addGroup(panel_HRDLayout.createSequentialGroup()
                                    .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cbView5_6)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cbInsert5_6)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cbUpdate5_6)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cbDelete5_6))
                                .addGroup(panel_HRDLayout.createSequentialGroup()
                                    .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cbView5_5)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cbInsert5_5)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cbUpdate5_5)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cbDelete5_5)))))
                    .addGroup(panel_HRDLayout.createSequentialGroup()
                        .addComponent(jLabel67, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbView5_10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInsert5_10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUpdate5_10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDelete5_10))
                    .addGroup(panel_HRDLayout.createSequentialGroup()
                        .addComponent(jLabel72, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbView5_11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInsert5_11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUpdate5_11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDelete5_11)))
                .addContainerGap(133, Short.MAX_VALUE))
        );
        panel_HRDLayout.setVerticalGroup(
            panel_HRDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_HRDLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_HRDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbView5)
                    .addComponent(cbDelete5)
                    .addComponent(cbInsert5)
                    .addComponent(cbUpdate5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panel_HRDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView5_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDelete5_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsert5_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdate5_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_HRDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView5_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDelete5_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsert5_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdate5_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_HRDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView5_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDelete5_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsert5_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdate5_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_HRDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView5_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDelete5_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsert5_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdate5_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_HRDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_HRDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cbView5_5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbDelete5_5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbInsert5_5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbUpdate5_5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_HRDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView5_6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDelete5_6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsert5_6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdate5_6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_HRDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel60, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView5_7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDelete5_7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsert5_7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdate5_7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_HRDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel61, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView5_8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDelete5_8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsert5_8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdate5_8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_HRDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel62, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView5_9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDelete5_9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsert5_9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdate5_9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_HRDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel67, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView5_10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDelete5_10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsert5_10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdate5_10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_HRDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel72, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView5_11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDelete5_11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsert5_11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdate5_11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(131, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("HRD", panel_HRD);

        panel_Keuangan.setBackground(new java.awt.Color(255, 255, 255));

        cbInsertKeuangan.setBackground(new java.awt.Color(255, 255, 255));
        cbInsertKeuangan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsertKeuangan.setText("Insert");
        cbInsertKeuangan.setEnabled(false);
        cbInsertKeuangan.setVisible(false);

        cbViewKeuangan.setBackground(new java.awt.Color(255, 255, 255));
        cbViewKeuangan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbViewKeuangan.setText("MENU KEUANGAN");

        cbUpdateKeuangan.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdateKeuangan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdateKeuangan.setText("Update");
        cbUpdateKeuangan.setEnabled(false);
        cbUpdateKeuangan.setVisible(false);

        cbDeleteKeuangan.setBackground(new java.awt.Color(255, 255, 255));
        cbDeleteKeuangan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDeleteKeuangan.setText("Delete");
        cbDeleteKeuangan.setEnabled(false);
        cbDeleteKeuangan.setVisible(false);

        cbInsertKeuangan_2.setBackground(new java.awt.Color(255, 255, 255));
        cbInsertKeuangan_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsertKeuangan_2.setText("Insert");

        cbUpdateKeuangan_2.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdateKeuangan_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdateKeuangan_2.setText("Update");

        cbDeleteKeuangan_2.setBackground(new java.awt.Color(255, 255, 255));
        cbDeleteKeuangan_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDeleteKeuangan_2.setText("Delete");

        jLabel45.setBackground(new java.awt.Color(255, 255, 255));
        jLabel45.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel45.setText("PRODUKSI");

        cbViewKeuangan_2.setBackground(new java.awt.Color(255, 255, 255));
        cbViewKeuangan_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbViewKeuangan_2.setText("View");

        cbViewKeuangan_1.setBackground(new java.awt.Color(255, 255, 255));
        cbViewKeuangan_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbViewKeuangan_1.setText("View");

        jLabel53.setBackground(new java.awt.Color(255, 255, 255));
        jLabel53.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel53.setText("BAHAN BAKU");

        cbDeleteKeuangan_1.setBackground(new java.awt.Color(255, 255, 255));
        cbDeleteKeuangan_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDeleteKeuangan_1.setText("Delete");

        cbUpdateKeuangan_1.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdateKeuangan_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdateKeuangan_1.setText("Update");

        cbInsertKeuangan_1.setBackground(new java.awt.Color(255, 255, 255));
        cbInsertKeuangan_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsertKeuangan_1.setText("Insert");

        cbInsertKeuangan_5.setBackground(new java.awt.Color(255, 255, 255));
        cbInsertKeuangan_5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsertKeuangan_5.setText("Insert");

        jLabel75.setBackground(new java.awt.Color(255, 255, 255));
        jLabel75.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel75.setText("PURCHASING");

        cbUpdateKeuangan_4.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdateKeuangan_4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdateKeuangan_4.setText("Update");

        cbViewKeuangan_4.setBackground(new java.awt.Color(255, 255, 255));
        cbViewKeuangan_4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbViewKeuangan_4.setText("View");

        cbInsertKeuangan_4.setBackground(new java.awt.Color(255, 255, 255));
        cbInsertKeuangan_4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsertKeuangan_4.setText("Insert");

        cbViewKeuangan_11.setBackground(new java.awt.Color(255, 255, 255));
        cbViewKeuangan_11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbViewKeuangan_11.setText("View");

        cbDeleteKeuangan_11.setBackground(new java.awt.Color(255, 255, 255));
        cbDeleteKeuangan_11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDeleteKeuangan_11.setText("Delete");

        cbDeleteKeuangan_4.setBackground(new java.awt.Color(255, 255, 255));
        cbDeleteKeuangan_4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDeleteKeuangan_4.setText("Delete");

        cbViewKeuangan_10.setBackground(new java.awt.Color(255, 255, 255));
        cbViewKeuangan_10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbViewKeuangan_10.setText("View");

        jLabel76.setBackground(new java.awt.Color(255, 255, 255));
        jLabel76.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel76.setText("DATA KARYAWAN");

        cbInsertKeuangan_10.setBackground(new java.awt.Color(255, 255, 255));
        cbInsertKeuangan_10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsertKeuangan_10.setText("Insert");

        cbViewKeuangan_3.setBackground(new java.awt.Color(255, 255, 255));
        cbViewKeuangan_3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbViewKeuangan_3.setText("View");

        cbUpdateKeuangan_10.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdateKeuangan_10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdateKeuangan_10.setText("Update");

        cbInsertKeuangan_3.setBackground(new java.awt.Color(255, 255, 255));
        cbInsertKeuangan_3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsertKeuangan_3.setText("Insert");

        cbUpdateKeuangan_3.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdateKeuangan_3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdateKeuangan_3.setText("Update");

        cbDeleteKeuangan_3.setBackground(new java.awt.Color(255, 255, 255));
        cbDeleteKeuangan_3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDeleteKeuangan_3.setText("Delete");

        cbViewKeuangan_12.setBackground(new java.awt.Color(255, 255, 255));
        cbViewKeuangan_12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbViewKeuangan_12.setText("View");

        cbInsertKeuangan_12.setBackground(new java.awt.Color(255, 255, 255));
        cbInsertKeuangan_12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsertKeuangan_12.setText("Insert");

        cbUpdateKeuangan_11.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdateKeuangan_11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdateKeuangan_11.setText("Update");

        jLabel81.setBackground(new java.awt.Color(255, 255, 255));
        jLabel81.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel81.setText("NERACA");

        cbInsertKeuangan_11.setBackground(new java.awt.Color(255, 255, 255));
        cbInsertKeuangan_11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsertKeuangan_11.setText("Insert");

        cbUpdateKeuangan_7.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdateKeuangan_7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdateKeuangan_7.setText("Update");

        jLabel77.setBackground(new java.awt.Color(255, 255, 255));
        jLabel77.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel77.setText("DATA PENGIRIMAN");

        cbDeleteKeuangan_7.setBackground(new java.awt.Color(255, 255, 255));
        cbDeleteKeuangan_7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDeleteKeuangan_7.setText("Delete");

        cbUpdateKeuangan_6.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdateKeuangan_6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdateKeuangan_6.setText("Update");

        cbViewKeuangan_6.setBackground(new java.awt.Color(255, 255, 255));
        cbViewKeuangan_6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbViewKeuangan_6.setText("View");

        jLabel78.setBackground(new java.awt.Color(255, 255, 255));
        jLabel78.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel78.setText("LEMBUR STAFF");

        jLabel83.setBackground(new java.awt.Color(255, 255, 255));
        jLabel83.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel83.setText("LAPORAN KEUANGAN");

        cbUpdateKeuangan_13.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdateKeuangan_13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdateKeuangan_13.setText("Update");

        cbInsertKeuangan_13.setBackground(new java.awt.Color(255, 255, 255));
        cbInsertKeuangan_13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsertKeuangan_13.setText("Insert");

        cbDeleteKeuangan_6.setBackground(new java.awt.Color(255, 255, 255));
        cbDeleteKeuangan_6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDeleteKeuangan_6.setText("Delete");

        jLabel82.setBackground(new java.awt.Color(255, 255, 255));
        jLabel82.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel82.setText("DATA AR AP ESTA");

        cbInsertKeuangan_6.setBackground(new java.awt.Color(255, 255, 255));
        cbInsertKeuangan_6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsertKeuangan_6.setText("Insert");

        cbUpdateKeuangan_12.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdateKeuangan_12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdateKeuangan_12.setText("Update");

        cbUpdateKeuangan_5.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdateKeuangan_5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdateKeuangan_5.setText("Update");

        cbDeleteKeuangan_12.setBackground(new java.awt.Color(255, 255, 255));
        cbDeleteKeuangan_12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDeleteKeuangan_12.setText("Delete");

        cbViewKeuangan_5.setBackground(new java.awt.Color(255, 255, 255));
        cbViewKeuangan_5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbViewKeuangan_5.setText("View");

        cbDeleteKeuangan_5.setBackground(new java.awt.Color(255, 255, 255));
        cbDeleteKeuangan_5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDeleteKeuangan_5.setText("Delete");

        cbViewKeuangan_13.setBackground(new java.awt.Color(255, 255, 255));
        cbViewKeuangan_13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbViewKeuangan_13.setText("View");

        cbDeleteKeuangan_13.setBackground(new java.awt.Color(255, 255, 255));
        cbDeleteKeuangan_13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDeleteKeuangan_13.setText("Delete");

        cbInsertKeuangan_9.setBackground(new java.awt.Color(255, 255, 255));
        cbInsertKeuangan_9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsertKeuangan_9.setText("Insert");

        cbDeleteKeuangan_9.setBackground(new java.awt.Color(255, 255, 255));
        cbDeleteKeuangan_9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDeleteKeuangan_9.setText("Delete");

        jLabel79.setBackground(new java.awt.Color(255, 255, 255));
        jLabel79.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel79.setText("DATA BIAYA");

        cbUpdateKeuangan_9.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdateKeuangan_9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdateKeuangan_9.setText("Update");

        cbViewKeuangan_8.setBackground(new java.awt.Color(255, 255, 255));
        cbViewKeuangan_8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbViewKeuangan_8.setText("View");

        jLabel80.setBackground(new java.awt.Color(255, 255, 255));
        jLabel80.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel80.setText("SALES & PAYMENT REPORT");

        cbDeleteKeuangan_8.setBackground(new java.awt.Color(255, 255, 255));
        cbDeleteKeuangan_8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDeleteKeuangan_8.setText("Delete");

        cbUpdateKeuangan_8.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdateKeuangan_8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdateKeuangan_8.setText("Update");

        cbInsertKeuangan_8.setBackground(new java.awt.Color(255, 255, 255));
        cbInsertKeuangan_8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsertKeuangan_8.setText("Insert");

        cbViewKeuangan_7.setBackground(new java.awt.Color(255, 255, 255));
        cbViewKeuangan_7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbViewKeuangan_7.setText("View");

        cbInsertKeuangan_7.setBackground(new java.awt.Color(255, 255, 255));
        cbInsertKeuangan_7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsertKeuangan_7.setText("Insert");

        jLabel84.setBackground(new java.awt.Color(255, 255, 255));
        jLabel84.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel84.setText("BARANG JADI");

        jLabel85.setBackground(new java.awt.Color(255, 255, 255));
        jLabel85.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel85.setText("CASH ON BANK");

        cbDeleteKeuangan_10.setBackground(new java.awt.Color(255, 255, 255));
        cbDeleteKeuangan_10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDeleteKeuangan_10.setText("Delete");

        cbViewKeuangan_9.setBackground(new java.awt.Color(255, 255, 255));
        cbViewKeuangan_9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbViewKeuangan_9.setText("View");

        javax.swing.GroupLayout panel_KeuanganLayout = new javax.swing.GroupLayout(panel_Keuangan);
        panel_Keuangan.setLayout(panel_KeuanganLayout);
        panel_KeuanganLayout.setHorizontalGroup(
            panel_KeuanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_KeuanganLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_KeuanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_KeuanganLayout.createSequentialGroup()
                        .addComponent(jLabel84, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbViewKeuangan_3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInsertKeuangan_3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUpdateKeuangan_3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDeleteKeuangan_3))
                    .addGroup(panel_KeuanganLayout.createSequentialGroup()
                        .addComponent(jLabel76, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbViewKeuangan_4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInsertKeuangan_4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUpdateKeuangan_4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDeleteKeuangan_4))
                    .addGroup(panel_KeuanganLayout.createSequentialGroup()
                        .addComponent(jLabel75, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbViewKeuangan_5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInsertKeuangan_5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUpdateKeuangan_5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDeleteKeuangan_5))
                    .addGroup(panel_KeuanganLayout.createSequentialGroup()
                        .addComponent(jLabel78, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbViewKeuangan_6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInsertKeuangan_6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUpdateKeuangan_6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDeleteKeuangan_6))
                    .addGroup(panel_KeuanganLayout.createSequentialGroup()
                        .addComponent(jLabel77, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbViewKeuangan_7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInsertKeuangan_7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUpdateKeuangan_7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDeleteKeuangan_7))
                    .addGroup(panel_KeuanganLayout.createSequentialGroup()
                        .addComponent(jLabel80, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbViewKeuangan_8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInsertKeuangan_8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUpdateKeuangan_8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDeleteKeuangan_8))
                    .addGroup(panel_KeuanganLayout.createSequentialGroup()
                        .addComponent(jLabel79, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbViewKeuangan_9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInsertKeuangan_9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUpdateKeuangan_9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDeleteKeuangan_9))
                    .addGroup(panel_KeuanganLayout.createSequentialGroup()
                        .addComponent(jLabel85, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbViewKeuangan_10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInsertKeuangan_10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUpdateKeuangan_10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDeleteKeuangan_10))
                    .addGroup(panel_KeuanganLayout.createSequentialGroup()
                        .addComponent(jLabel81, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbViewKeuangan_11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInsertKeuangan_11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUpdateKeuangan_11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDeleteKeuangan_11))
                    .addGroup(panel_KeuanganLayout.createSequentialGroup()
                        .addComponent(jLabel82, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbViewKeuangan_12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInsertKeuangan_12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUpdateKeuangan_12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDeleteKeuangan_12))
                    .addGroup(panel_KeuanganLayout.createSequentialGroup()
                        .addComponent(jLabel83, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbViewKeuangan_13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInsertKeuangan_13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUpdateKeuangan_13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDeleteKeuangan_13))
                    .addGroup(panel_KeuanganLayout.createSequentialGroup()
                        .addComponent(cbViewKeuangan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInsertKeuangan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUpdateKeuangan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDeleteKeuangan))
                    .addGroup(panel_KeuanganLayout.createSequentialGroup()
                        .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbViewKeuangan_2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInsertKeuangan_2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUpdateKeuangan_2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDeleteKeuangan_2))
                    .addGroup(panel_KeuanganLayout.createSequentialGroup()
                        .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbViewKeuangan_1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInsertKeuangan_1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUpdateKeuangan_1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDeleteKeuangan_1)))
                .addContainerGap(133, Short.MAX_VALUE))
        );
        panel_KeuanganLayout.setVerticalGroup(
            panel_KeuanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_KeuanganLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_KeuanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbViewKeuangan)
                    .addComponent(cbDeleteKeuangan)
                    .addComponent(cbInsertKeuangan)
                    .addComponent(cbUpdateKeuangan))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_KeuanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbViewKeuangan_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDeleteKeuangan_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsertKeuangan_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdateKeuangan_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_KeuanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbViewKeuangan_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDeleteKeuangan_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsertKeuangan_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdateKeuangan_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_KeuanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel84, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbViewKeuangan_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDeleteKeuangan_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsertKeuangan_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdateKeuangan_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_KeuanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel76, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbViewKeuangan_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDeleteKeuangan_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsertKeuangan_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdateKeuangan_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_KeuanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel75, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbViewKeuangan_5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDeleteKeuangan_5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsertKeuangan_5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdateKeuangan_5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_KeuanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel78, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbViewKeuangan_6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDeleteKeuangan_6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsertKeuangan_6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdateKeuangan_6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_KeuanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel77, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbViewKeuangan_7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDeleteKeuangan_7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsertKeuangan_7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdateKeuangan_7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_KeuanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel80, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbViewKeuangan_8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDeleteKeuangan_8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsertKeuangan_8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdateKeuangan_8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_KeuanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel79, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbViewKeuangan_9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDeleteKeuangan_9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsertKeuangan_9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdateKeuangan_9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_KeuanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel85, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbViewKeuangan_10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDeleteKeuangan_10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsertKeuangan_10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdateKeuangan_10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_KeuanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel81, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbViewKeuangan_11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDeleteKeuangan_11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsertKeuangan_11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdateKeuangan_11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_KeuanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel82, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbViewKeuangan_12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDeleteKeuangan_12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsertKeuangan_12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdateKeuangan_12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_KeuanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel83, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbViewKeuangan_13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDeleteKeuangan_13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbInsertKeuangan_13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUpdateKeuangan_13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(84, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Keuangan", panel_Keuangan);

        panel_Keuangan1.setBackground(new java.awt.Color(255, 255, 255));

        cbInsert7.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert7.setText("Insert");
        cbInsert7.setEnabled(false);
        cbInsertKeuangan.setVisible(false);

        cbView7.setBackground(new java.awt.Color(255, 255, 255));
        cbView7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView7.setText("MENU MANAJEMEN");

        cbUpdate7.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate7.setText("Update");
        cbUpdate7.setEnabled(false);
        cbUpdateKeuangan.setVisible(false);

        cbDelete7.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete7.setText("Delete");
        cbDelete7.setEnabled(false);
        cbDeleteKeuangan.setVisible(false);

        javax.swing.GroupLayout panel_Keuangan1Layout = new javax.swing.GroupLayout(panel_Keuangan1);
        panel_Keuangan1.setLayout(panel_Keuangan1Layout);
        panel_Keuangan1Layout.setHorizontalGroup(
            panel_Keuangan1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_Keuangan1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cbView7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbInsert7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbUpdate7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbDelete7)
                .addContainerGap(269, Short.MAX_VALUE))
        );
        panel_Keuangan1Layout.setVerticalGroup(
            panel_Keuangan1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_Keuangan1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_Keuangan1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbView7)
                    .addComponent(cbDelete7)
                    .addComponent(cbInsert7)
                    .addComponent(cbUpdate7))
                .addContainerGap(418, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Manajemen", panel_Keuangan1);

        panel_USER.setBackground(new java.awt.Color(255, 255, 255));

        cbView7_1.setBackground(new java.awt.Color(255, 255, 255));
        cbView7_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView7_1.setText("View");

        cbDelete7_1.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete7_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete7_1.setText("Delete");

        cbInsert7_1.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert7_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert7_1.setText("Insert");

        cbUpdate7_1.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate7_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate7_1.setText("Update");

        jLabel46.setBackground(new java.awt.Color(255, 255, 255));
        jLabel46.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel46.setText("CREATE NEW USER");

        jLabel47.setBackground(new java.awt.Color(255, 255, 255));
        jLabel47.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel47.setText("DATA LOGIN");

        cbView7_2.setBackground(new java.awt.Color(255, 255, 255));
        cbView7_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbView7_2.setText("View");

        cbInsert7_2.setBackground(new java.awt.Color(255, 255, 255));
        cbInsert7_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsert7_2.setText("Insert");

        cbUpdate7_2.setBackground(new java.awt.Color(255, 255, 255));
        cbUpdate7_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdate7_2.setText("Update");

        cbDelete7_2.setBackground(new java.awt.Color(255, 255, 255));
        cbDelete7_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDelete7_2.setText("Delete");

        javax.swing.GroupLayout panel_USERLayout = new javax.swing.GroupLayout(panel_USER);
        panel_USER.setLayout(panel_USERLayout);
        panel_USERLayout.setHorizontalGroup(
            panel_USERLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_USERLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_USERLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_USERLayout.createSequentialGroup()
                        .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbView7_1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInsert7_1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUpdate7_1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDelete7_1))
                    .addGroup(panel_USERLayout.createSequentialGroup()
                        .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbView7_2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInsert7_2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUpdate7_2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDelete7_2)))
                .addContainerGap(183, Short.MAX_VALUE))
        );
        panel_USERLayout.setVerticalGroup(
            panel_USERLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_USERLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_USERLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView7_1)
                    .addComponent(cbDelete7_1)
                    .addComponent(cbInsert7_1)
                    .addComponent(cbUpdate7_1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_USERLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbView7_2)
                    .addComponent(cbDelete7_2)
                    .addComponent(cbInsert7_2)
                    .addComponent(cbUpdate7_2))
                .addContainerGap(389, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("User", panel_USER);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel6.setText("Hak Akses User");

        cbInsertAll.setBackground(new java.awt.Color(204, 204, 255));
        cbInsertAll.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbInsertAll.setText("Insert All");
        cbInsertAll.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbInsertAllItemStateChanged(evt);
            }
        });

        cbUpdateAll.setBackground(new java.awt.Color(204, 204, 255));
        cbUpdateAll.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbUpdateAll.setText("Update All");
        cbUpdateAll.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbUpdateAllItemStateChanged(evt);
            }
        });

        cbViewAll.setBackground(new java.awt.Color(204, 204, 255));
        cbViewAll.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbViewAll.setText("View All");
        cbViewAll.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbViewAllItemStateChanged(evt);
            }
        });

        cbDeleteAll.setBackground(new java.awt.Color(204, 204, 255));
        cbDeleteAll.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbDeleteAll.setText("Delete All");
        cbDeleteAll.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbDeleteAllItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel_create_userLayout = new javax.swing.GroupLayout(jPanel_create_user);
        jPanel_create_user.setLayout(jPanel_create_userLayout);
        jPanel_create_userLayout.setHorizontalGroup(
            jPanel_create_userLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_create_userLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_create_userLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_create_userLayout.createSequentialGroup()
                        .addGroup(jPanel_create_userLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_create_userLayout.createSequentialGroup()
                                .addGroup(jPanel_create_userLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel_create_userLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(txt_username)
                                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(txt_pass_1)
                                        .addComponent(txt_pass_2, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(button_pilih_pegawai))
                                .addGap(62, 62, 62))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_create_userLayout.createSequentialGroup()
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(jPanel_create_userLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addGroup(jPanel_create_userLayout.createSequentialGroup()
                                .addComponent(cbViewAll)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbInsertAll)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbUpdateAll)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbDeleteAll))
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_create_userLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(button_simpan)))
                .addContainerGap())
        );
        jPanel_create_userLayout.setVerticalGroup(
            jPanel_create_userLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_create_userLayout.createSequentialGroup()
                .addGroup(jPanel_create_userLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_create_userLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addGroup(jPanel_create_userLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_create_userLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(cbViewAll)
                                .addComponent(cbDeleteAll)
                                .addComponent(cbInsertAll)
                                .addComponent(cbUpdateAll))
                            .addComponent(txt_username, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel_create_userLayout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addComponent(jLabel6)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_create_userLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_create_userLayout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_pass_1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_pass_2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_pilih_pegawai, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_simpan, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_create_user, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_create_user, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void button_pilih_pegawaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_pilih_pegawaiActionPerformed
        // TODO add your handling code here:
        Browse_Karyawan dialog = new Browse_Karyawan(new javax.swing.JFrame(), true, null);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);

        int x = Browse_Karyawan.table_list_karyawan.getSelectedRow();
        if (x >= 0) {
            label_id.setText(Browse_Karyawan.table_list_karyawan.getValueAt(x, 0).toString());
            label_name.setText(Browse_Karyawan.table_list_karyawan.getValueAt(x, 1).toString());
        }
        try {
            sql = "SELECT * "
                    + "FROM `tb_karyawan` JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`"
                    + "WHERE `id_pegawai` LIKE '%" + label_id.getText() + "%'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                label_position.setText(rs.getString("posisi"));
                label_department.setText(rs.getString("kode_departemen"));
                label_division.setText(rs.getString("nama_bagian"));
                label_status.setText(rs.getString("status"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JDialog_CreateNew_User1.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_pilih_pegawaiActionPerformed

    private void button_simpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_simpanActionPerformed
        // TODO add your handling code here:
        try {
            Boolean Check = true;
            if (!txt_pass_1.getText().equals(txt_pass_2.getText())) {
                JOptionPane.showMessageDialog(this, "Enter the Same Password on Both Password Field !!");
                Check = false;
            }
            if ("ID".equals(label_id.getText())) {
                JOptionPane.showMessageDialog(this, "Please Select the employee identity !!");
                Check = false;
            }
            if (Check) {
                Utility.db.getConnection().setAutoCommit(false);

                String Query = "INSERT INTO `tb_login`(`user`, `pass`, `id_pegawai`) VALUES ('" + txt_username.getText() + "','" + txt_pass_2.getText() + "','" + label_id.getText() + "')";
                Utility.db.getConnection().createStatement();
                Utility.db.getStatement().executeUpdate(Query);
                for (int j = 0; j < listCB.size(); j++) {
                    String akses = "";
                    for (int k = 0; k < listCB.get(j).data.length; k++) {
                        if (listCB.get(j).data[k].isSelected()) {
                            akses += "1";
                        } else {
                            akses += "0";
                        }
                    }

                    String Query2 = "INSERT INTO `tb_hak_akses` (`user`, `menu`, `hak_akses`) VALUES('" + txt_username.getText() + "','" + listCB.get(j).nama + "','" + akses + "')";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(Query2)) <= 0) {
                        throw new Exception();
                    }
                }
                Utility.db.getConnection().commit();
                JOptionPane.showMessageDialog(this, "User baru berhasil dibuat");
                this.dispose();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JDialog_CreateNew_User1.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                Utility.db.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_CreateNew_User1.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_simpanActionPerformed

    private void cbViewAllItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbViewAllItemStateChanged
        // TODO add your handling code here:
        if (cbViewAll.isSelected()) {
            for (int i = 0; i < listCB.size(); i++) {
                listCB.get(i).data[0].setSelected(true);
            }
        } else {
            for (int i = 0; i < listCB.size(); i++) {
                listCB.get(i).data[0].setSelected(false);
            }
        }
    }//GEN-LAST:event_cbViewAllItemStateChanged

    private void cbInsertAllItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbInsertAllItemStateChanged
        if (cbInsertAll.isSelected()) {
            for (int i = 0; i < listCB.size(); i++) {
                listCB.get(i).data[1].setSelected(true);
            }
        } else {
            for (int i = 0; i < listCB.size(); i++) {
                listCB.get(i).data[1].setSelected(false);
            }
        }
    }//GEN-LAST:event_cbInsertAllItemStateChanged

    private void cbUpdateAllItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbUpdateAllItemStateChanged
        if (cbUpdateAll.isSelected()) {
            for (int i = 0; i < listCB.size(); i++) {
                listCB.get(i).data[2].setSelected(true);
            }
        } else {
            for (int i = 0; i < listCB.size(); i++) {
                listCB.get(i).data[2].setSelected(false);
            }
        }
    }//GEN-LAST:event_cbUpdateAllItemStateChanged

    private void cbDeleteAllItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbDeleteAllItemStateChanged
        if (cbDeleteAll.isSelected()) {
            for (int i = 0; i < listCB.size(); i++) {
                listCB.get(i).data[3].setSelected(true);
            }
        } else {
            for (int i = 0; i < listCB.size(); i++) {
                listCB.get(i).data[3].setSelected(false);
            }
        }
    }//GEN-LAST:event_cbDeleteAllItemStateChanged

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
            java.util.logging.Logger.getLogger(JDialog_CreateNew_User1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JDialog_CreateNew_User1 dialog = new JDialog_CreateNew_User1(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
                dialog.setResizable(false);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton button_pilih_pegawai;
    private javax.swing.JButton button_simpan;
    private javax.swing.JCheckBox cbDelete0;
    private javax.swing.JCheckBox cbDelete0_1;
    private javax.swing.JCheckBox cbDelete0_10;
    private javax.swing.JCheckBox cbDelete0_11;
    private javax.swing.JCheckBox cbDelete0_12;
    private javax.swing.JCheckBox cbDelete0_13;
    private javax.swing.JCheckBox cbDelete0_14;
    private javax.swing.JCheckBox cbDelete0_15;
    private javax.swing.JCheckBox cbDelete0_2;
    private javax.swing.JCheckBox cbDelete0_3;
    private javax.swing.JCheckBox cbDelete0_4;
    private javax.swing.JCheckBox cbDelete0_5;
    private javax.swing.JCheckBox cbDelete0_6;
    private javax.swing.JCheckBox cbDelete0_7;
    private javax.swing.JCheckBox cbDelete0_8;
    private javax.swing.JCheckBox cbDelete0_9;
    private javax.swing.JCheckBox cbDelete1;
    private javax.swing.JCheckBox cbDelete1_1;
    private javax.swing.JCheckBox cbDelete1_2;
    private javax.swing.JCheckBox cbDelete1_3;
    private javax.swing.JCheckBox cbDelete1_4;
    private javax.swing.JCheckBox cbDelete1_5;
    private javax.swing.JCheckBox cbDelete1_6;
    private javax.swing.JCheckBox cbDelete2;
    private javax.swing.JCheckBox cbDelete2_1;
    private javax.swing.JCheckBox cbDelete2_2;
    private javax.swing.JCheckBox cbDelete2_3;
    private javax.swing.JCheckBox cbDelete2_4;
    private javax.swing.JCheckBox cbDelete2_5;
    private javax.swing.JCheckBox cbDelete2_6;
    private javax.swing.JCheckBox cbDelete2_7;
    private javax.swing.JCheckBox cbDelete2_8;
    private javax.swing.JCheckBox cbDelete2_9;
    private javax.swing.JCheckBox cbDelete3;
    private javax.swing.JCheckBox cbDelete3_1;
    private javax.swing.JCheckBox cbDelete3_2;
    private javax.swing.JCheckBox cbDelete3_3;
    private javax.swing.JCheckBox cbDelete3_4;
    private javax.swing.JCheckBox cbDelete3_5;
    private javax.swing.JCheckBox cbDelete4;
    private javax.swing.JCheckBox cbDelete4_1;
    private javax.swing.JCheckBox cbDelete4_2;
    private javax.swing.JCheckBox cbDelete4_3;
    private javax.swing.JCheckBox cbDelete4_4;
    private javax.swing.JCheckBox cbDelete5;
    private javax.swing.JCheckBox cbDelete5_1;
    private javax.swing.JCheckBox cbDelete5_10;
    private javax.swing.JCheckBox cbDelete5_11;
    private javax.swing.JCheckBox cbDelete5_2;
    private javax.swing.JCheckBox cbDelete5_3;
    private javax.swing.JCheckBox cbDelete5_4;
    private javax.swing.JCheckBox cbDelete5_5;
    private javax.swing.JCheckBox cbDelete5_6;
    private javax.swing.JCheckBox cbDelete5_7;
    private javax.swing.JCheckBox cbDelete5_8;
    private javax.swing.JCheckBox cbDelete5_9;
    private javax.swing.JCheckBox cbDelete7;
    private javax.swing.JCheckBox cbDelete7_1;
    private javax.swing.JCheckBox cbDelete7_2;
    private javax.swing.JCheckBox cbDeleteATB;
    private javax.swing.JCheckBox cbDeleteATB_1;
    private javax.swing.JCheckBox cbDeleteATB_2;
    private javax.swing.JCheckBox cbDeleteAll;
    private javax.swing.JCheckBox cbDeleteKeuangan;
    private javax.swing.JCheckBox cbDeleteKeuangan_1;
    private javax.swing.JCheckBox cbDeleteKeuangan_10;
    private javax.swing.JCheckBox cbDeleteKeuangan_11;
    private javax.swing.JCheckBox cbDeleteKeuangan_12;
    private javax.swing.JCheckBox cbDeleteKeuangan_13;
    private javax.swing.JCheckBox cbDeleteKeuangan_2;
    private javax.swing.JCheckBox cbDeleteKeuangan_3;
    private javax.swing.JCheckBox cbDeleteKeuangan_4;
    private javax.swing.JCheckBox cbDeleteKeuangan_5;
    private javax.swing.JCheckBox cbDeleteKeuangan_6;
    private javax.swing.JCheckBox cbDeleteKeuangan_7;
    private javax.swing.JCheckBox cbDeleteKeuangan_8;
    private javax.swing.JCheckBox cbDeleteKeuangan_9;
    private javax.swing.JCheckBox cbDeleteSub;
    private javax.swing.JCheckBox cbDeleteSub_1;
    private javax.swing.JCheckBox cbDeleteSub_2;
    private javax.swing.JCheckBox cbDeleteSub_3;
    private javax.swing.JCheckBox cbDeleteSub_4;
    private javax.swing.JCheckBox cbDeleteSub_5;
    private javax.swing.JCheckBox cbDeleteSub_6;
    private javax.swing.JCheckBox cbDeleteSub_7;
    private javax.swing.JCheckBox cbDelete_MainMenu1;
    private javax.swing.JCheckBox cbDelete_MainMenu2;
    private javax.swing.JCheckBox cbInsert0;
    private javax.swing.JCheckBox cbInsert0_1;
    private javax.swing.JCheckBox cbInsert0_10;
    private javax.swing.JCheckBox cbInsert0_11;
    private javax.swing.JCheckBox cbInsert0_12;
    private javax.swing.JCheckBox cbInsert0_13;
    private javax.swing.JCheckBox cbInsert0_14;
    private javax.swing.JCheckBox cbInsert0_15;
    private javax.swing.JCheckBox cbInsert0_2;
    private javax.swing.JCheckBox cbInsert0_3;
    private javax.swing.JCheckBox cbInsert0_4;
    private javax.swing.JCheckBox cbInsert0_5;
    private javax.swing.JCheckBox cbInsert0_6;
    private javax.swing.JCheckBox cbInsert0_7;
    private javax.swing.JCheckBox cbInsert0_8;
    private javax.swing.JCheckBox cbInsert0_9;
    private javax.swing.JCheckBox cbInsert1;
    private javax.swing.JCheckBox cbInsert1_1;
    private javax.swing.JCheckBox cbInsert1_2;
    private javax.swing.JCheckBox cbInsert1_3;
    private javax.swing.JCheckBox cbInsert1_4;
    private javax.swing.JCheckBox cbInsert1_5;
    private javax.swing.JCheckBox cbInsert1_6;
    private javax.swing.JCheckBox cbInsert2;
    private javax.swing.JCheckBox cbInsert2_1;
    private javax.swing.JCheckBox cbInsert2_2;
    private javax.swing.JCheckBox cbInsert2_3;
    private javax.swing.JCheckBox cbInsert2_4;
    private javax.swing.JCheckBox cbInsert2_5;
    private javax.swing.JCheckBox cbInsert2_6;
    private javax.swing.JCheckBox cbInsert2_7;
    private javax.swing.JCheckBox cbInsert2_8;
    private javax.swing.JCheckBox cbInsert2_9;
    private javax.swing.JCheckBox cbInsert3;
    private javax.swing.JCheckBox cbInsert3_1;
    private javax.swing.JCheckBox cbInsert3_2;
    private javax.swing.JCheckBox cbInsert3_3;
    private javax.swing.JCheckBox cbInsert3_4;
    private javax.swing.JCheckBox cbInsert3_5;
    private javax.swing.JCheckBox cbInsert4;
    private javax.swing.JCheckBox cbInsert4_1;
    private javax.swing.JCheckBox cbInsert4_2;
    private javax.swing.JCheckBox cbInsert4_3;
    private javax.swing.JCheckBox cbInsert4_4;
    private javax.swing.JCheckBox cbInsert5;
    private javax.swing.JCheckBox cbInsert5_1;
    private javax.swing.JCheckBox cbInsert5_10;
    private javax.swing.JCheckBox cbInsert5_11;
    private javax.swing.JCheckBox cbInsert5_2;
    private javax.swing.JCheckBox cbInsert5_3;
    private javax.swing.JCheckBox cbInsert5_4;
    private javax.swing.JCheckBox cbInsert5_5;
    private javax.swing.JCheckBox cbInsert5_6;
    private javax.swing.JCheckBox cbInsert5_7;
    private javax.swing.JCheckBox cbInsert5_8;
    private javax.swing.JCheckBox cbInsert5_9;
    private javax.swing.JCheckBox cbInsert7;
    private javax.swing.JCheckBox cbInsert7_1;
    private javax.swing.JCheckBox cbInsert7_2;
    private javax.swing.JCheckBox cbInsertATB;
    private javax.swing.JCheckBox cbInsertATB_1;
    private javax.swing.JCheckBox cbInsertATB_2;
    private javax.swing.JCheckBox cbInsertAll;
    private javax.swing.JCheckBox cbInsertKeuangan;
    private javax.swing.JCheckBox cbInsertKeuangan_1;
    private javax.swing.JCheckBox cbInsertKeuangan_10;
    private javax.swing.JCheckBox cbInsertKeuangan_11;
    private javax.swing.JCheckBox cbInsertKeuangan_12;
    private javax.swing.JCheckBox cbInsertKeuangan_13;
    private javax.swing.JCheckBox cbInsertKeuangan_2;
    private javax.swing.JCheckBox cbInsertKeuangan_3;
    private javax.swing.JCheckBox cbInsertKeuangan_4;
    private javax.swing.JCheckBox cbInsertKeuangan_5;
    private javax.swing.JCheckBox cbInsertKeuangan_6;
    private javax.swing.JCheckBox cbInsertKeuangan_7;
    private javax.swing.JCheckBox cbInsertKeuangan_8;
    private javax.swing.JCheckBox cbInsertKeuangan_9;
    private javax.swing.JCheckBox cbInsertSub;
    private javax.swing.JCheckBox cbInsertSub_1;
    private javax.swing.JCheckBox cbInsertSub_2;
    private javax.swing.JCheckBox cbInsertSub_3;
    private javax.swing.JCheckBox cbInsertSub_4;
    private javax.swing.JCheckBox cbInsertSub_5;
    private javax.swing.JCheckBox cbInsertSub_6;
    private javax.swing.JCheckBox cbInsertSub_7;
    private javax.swing.JCheckBox cbInsert_MainMenu1;
    private javax.swing.JCheckBox cbInsert_MainMenu2;
    private javax.swing.JCheckBox cbUpdate0;
    private javax.swing.JCheckBox cbUpdate0_1;
    private javax.swing.JCheckBox cbUpdate0_10;
    private javax.swing.JCheckBox cbUpdate0_11;
    private javax.swing.JCheckBox cbUpdate0_12;
    private javax.swing.JCheckBox cbUpdate0_13;
    private javax.swing.JCheckBox cbUpdate0_14;
    private javax.swing.JCheckBox cbUpdate0_15;
    private javax.swing.JCheckBox cbUpdate0_2;
    private javax.swing.JCheckBox cbUpdate0_3;
    private javax.swing.JCheckBox cbUpdate0_4;
    private javax.swing.JCheckBox cbUpdate0_5;
    private javax.swing.JCheckBox cbUpdate0_6;
    private javax.swing.JCheckBox cbUpdate0_7;
    private javax.swing.JCheckBox cbUpdate0_8;
    private javax.swing.JCheckBox cbUpdate0_9;
    private javax.swing.JCheckBox cbUpdate1;
    private javax.swing.JCheckBox cbUpdate1_1;
    private javax.swing.JCheckBox cbUpdate1_2;
    private javax.swing.JCheckBox cbUpdate1_3;
    private javax.swing.JCheckBox cbUpdate1_4;
    private javax.swing.JCheckBox cbUpdate1_5;
    private javax.swing.JCheckBox cbUpdate1_6;
    private javax.swing.JCheckBox cbUpdate2;
    private javax.swing.JCheckBox cbUpdate2_1;
    private javax.swing.JCheckBox cbUpdate2_2;
    private javax.swing.JCheckBox cbUpdate2_3;
    private javax.swing.JCheckBox cbUpdate2_4;
    private javax.swing.JCheckBox cbUpdate2_5;
    private javax.swing.JCheckBox cbUpdate2_6;
    private javax.swing.JCheckBox cbUpdate2_7;
    private javax.swing.JCheckBox cbUpdate2_8;
    private javax.swing.JCheckBox cbUpdate2_9;
    private javax.swing.JCheckBox cbUpdate3;
    private javax.swing.JCheckBox cbUpdate3_1;
    private javax.swing.JCheckBox cbUpdate3_2;
    private javax.swing.JCheckBox cbUpdate3_3;
    private javax.swing.JCheckBox cbUpdate3_4;
    private javax.swing.JCheckBox cbUpdate3_5;
    private javax.swing.JCheckBox cbUpdate4;
    private javax.swing.JCheckBox cbUpdate4_1;
    private javax.swing.JCheckBox cbUpdate4_2;
    private javax.swing.JCheckBox cbUpdate4_3;
    private javax.swing.JCheckBox cbUpdate4_4;
    private javax.swing.JCheckBox cbUpdate5;
    private javax.swing.JCheckBox cbUpdate5_1;
    private javax.swing.JCheckBox cbUpdate5_10;
    private javax.swing.JCheckBox cbUpdate5_11;
    private javax.swing.JCheckBox cbUpdate5_2;
    private javax.swing.JCheckBox cbUpdate5_3;
    private javax.swing.JCheckBox cbUpdate5_4;
    private javax.swing.JCheckBox cbUpdate5_5;
    private javax.swing.JCheckBox cbUpdate5_6;
    private javax.swing.JCheckBox cbUpdate5_7;
    private javax.swing.JCheckBox cbUpdate5_8;
    private javax.swing.JCheckBox cbUpdate5_9;
    private javax.swing.JCheckBox cbUpdate7;
    private javax.swing.JCheckBox cbUpdate7_1;
    private javax.swing.JCheckBox cbUpdate7_2;
    private javax.swing.JCheckBox cbUpdateATB;
    private javax.swing.JCheckBox cbUpdateATB_1;
    private javax.swing.JCheckBox cbUpdateATB_2;
    private javax.swing.JCheckBox cbUpdateAll;
    private javax.swing.JCheckBox cbUpdateKeuangan;
    private javax.swing.JCheckBox cbUpdateKeuangan_1;
    private javax.swing.JCheckBox cbUpdateKeuangan_10;
    private javax.swing.JCheckBox cbUpdateKeuangan_11;
    private javax.swing.JCheckBox cbUpdateKeuangan_12;
    private javax.swing.JCheckBox cbUpdateKeuangan_13;
    private javax.swing.JCheckBox cbUpdateKeuangan_2;
    private javax.swing.JCheckBox cbUpdateKeuangan_3;
    private javax.swing.JCheckBox cbUpdateKeuangan_4;
    private javax.swing.JCheckBox cbUpdateKeuangan_5;
    private javax.swing.JCheckBox cbUpdateKeuangan_6;
    private javax.swing.JCheckBox cbUpdateKeuangan_7;
    private javax.swing.JCheckBox cbUpdateKeuangan_8;
    private javax.swing.JCheckBox cbUpdateKeuangan_9;
    private javax.swing.JCheckBox cbUpdateSub;
    private javax.swing.JCheckBox cbUpdateSub_1;
    private javax.swing.JCheckBox cbUpdateSub_2;
    private javax.swing.JCheckBox cbUpdateSub_3;
    private javax.swing.JCheckBox cbUpdateSub_4;
    private javax.swing.JCheckBox cbUpdateSub_5;
    private javax.swing.JCheckBox cbUpdateSub_6;
    private javax.swing.JCheckBox cbUpdateSub_7;
    private javax.swing.JCheckBox cbUpdate_MainMenu1;
    private javax.swing.JCheckBox cbUpdate_MainMenu2;
    private javax.swing.JCheckBox cbView0;
    private javax.swing.JCheckBox cbView0_1;
    private javax.swing.JCheckBox cbView0_10;
    private javax.swing.JCheckBox cbView0_11;
    private javax.swing.JCheckBox cbView0_12;
    private javax.swing.JCheckBox cbView0_13;
    private javax.swing.JCheckBox cbView0_14;
    private javax.swing.JCheckBox cbView0_15;
    private javax.swing.JCheckBox cbView0_2;
    private javax.swing.JCheckBox cbView0_3;
    private javax.swing.JCheckBox cbView0_4;
    private javax.swing.JCheckBox cbView0_5;
    private javax.swing.JCheckBox cbView0_6;
    private javax.swing.JCheckBox cbView0_7;
    private javax.swing.JCheckBox cbView0_8;
    private javax.swing.JCheckBox cbView0_9;
    private javax.swing.JCheckBox cbView1;
    private javax.swing.JCheckBox cbView1_1;
    private javax.swing.JCheckBox cbView1_2;
    private javax.swing.JCheckBox cbView1_3;
    private javax.swing.JCheckBox cbView1_4;
    private javax.swing.JCheckBox cbView1_5;
    private javax.swing.JCheckBox cbView1_6;
    private javax.swing.JCheckBox cbView2;
    private javax.swing.JCheckBox cbView2_1;
    private javax.swing.JCheckBox cbView2_2;
    private javax.swing.JCheckBox cbView2_3;
    private javax.swing.JCheckBox cbView2_4;
    private javax.swing.JCheckBox cbView2_5;
    private javax.swing.JCheckBox cbView2_6;
    private javax.swing.JCheckBox cbView2_7;
    private javax.swing.JCheckBox cbView2_8;
    private javax.swing.JCheckBox cbView2_9;
    private javax.swing.JCheckBox cbView3;
    private javax.swing.JCheckBox cbView3_1;
    private javax.swing.JCheckBox cbView3_2;
    private javax.swing.JCheckBox cbView3_3;
    private javax.swing.JCheckBox cbView3_4;
    private javax.swing.JCheckBox cbView3_5;
    private javax.swing.JCheckBox cbView4;
    private javax.swing.JCheckBox cbView4_1;
    private javax.swing.JCheckBox cbView4_2;
    private javax.swing.JCheckBox cbView4_3;
    private javax.swing.JCheckBox cbView4_4;
    private javax.swing.JCheckBox cbView5;
    private javax.swing.JCheckBox cbView5_1;
    private javax.swing.JCheckBox cbView5_10;
    private javax.swing.JCheckBox cbView5_11;
    private javax.swing.JCheckBox cbView5_2;
    private javax.swing.JCheckBox cbView5_3;
    private javax.swing.JCheckBox cbView5_4;
    private javax.swing.JCheckBox cbView5_5;
    private javax.swing.JCheckBox cbView5_6;
    private javax.swing.JCheckBox cbView5_7;
    private javax.swing.JCheckBox cbView5_8;
    private javax.swing.JCheckBox cbView5_9;
    private javax.swing.JCheckBox cbView7;
    private javax.swing.JCheckBox cbView7_1;
    private javax.swing.JCheckBox cbView7_2;
    private javax.swing.JCheckBox cbViewATB;
    private javax.swing.JCheckBox cbViewATB_1;
    private javax.swing.JCheckBox cbViewATB_2;
    private javax.swing.JCheckBox cbViewAll;
    private javax.swing.JCheckBox cbViewKeuangan;
    private javax.swing.JCheckBox cbViewKeuangan_1;
    private javax.swing.JCheckBox cbViewKeuangan_10;
    private javax.swing.JCheckBox cbViewKeuangan_11;
    private javax.swing.JCheckBox cbViewKeuangan_12;
    private javax.swing.JCheckBox cbViewKeuangan_13;
    private javax.swing.JCheckBox cbViewKeuangan_2;
    private javax.swing.JCheckBox cbViewKeuangan_3;
    private javax.swing.JCheckBox cbViewKeuangan_4;
    private javax.swing.JCheckBox cbViewKeuangan_5;
    private javax.swing.JCheckBox cbViewKeuangan_6;
    private javax.swing.JCheckBox cbViewKeuangan_7;
    private javax.swing.JCheckBox cbViewKeuangan_8;
    private javax.swing.JCheckBox cbViewKeuangan_9;
    private javax.swing.JCheckBox cbViewSub;
    private javax.swing.JCheckBox cbViewSub_1;
    private javax.swing.JCheckBox cbViewSub_2;
    private javax.swing.JCheckBox cbViewSub_3;
    private javax.swing.JCheckBox cbViewSub_4;
    private javax.swing.JCheckBox cbViewSub_5;
    private javax.swing.JCheckBox cbViewSub_6;
    private javax.swing.JCheckBox cbViewSub_7;
    private javax.swing.JCheckBox cbView_MainMenu1;
    private javax.swing.JCheckBox cbView_MainMenu2;
    private javax.swing.JLabel jLabel1;
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
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel_create_user;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label_department;
    private javax.swing.JLabel label_division;
    private javax.swing.JLabel label_id;
    private javax.swing.JLabel label_name;
    private javax.swing.JLabel label_position;
    private javax.swing.JLabel label_status;
    private javax.swing.JPanel panel_HRD;
    private javax.swing.JPanel panel_Keuangan;
    private javax.swing.JPanel panel_Keuangan1;
    private javax.swing.JPanel panel_Packing;
    private javax.swing.JPanel panel_Produksi;
    private javax.swing.JPanel panel_QC;
    private javax.swing.JPanel panel_USER;
    private javax.swing.JPanel panel_atb;
    private javax.swing.JPanel panel_bahanBaku;
    private javax.swing.JPanel panel_bahanJadi;
    private javax.swing.JPanel panel_main_menu;
    private javax.swing.JPanel panel_manajemen_sub;
    private javax.swing.JPasswordField txt_pass_1;
    private javax.swing.JPasswordField txt_pass_2;
    private javax.swing.JTextField txt_username;
    // End of variables declaration//GEN-END:variables
}
