package waleta_system;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.font.TextAttribute;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import waleta_system.Class.AksesMenu;
import waleta_system.Class.Utility;

public class Login extends javax.swing.JFrame {

    private Font ori;
    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date today = new Date();
    JFileChooser chooser = new JFileChooser();

    public enum CharAt {
        VIEW(0), INSERT(1), UPDATE(2), DELETE(3);
        private final int value;

        private CharAt(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public static boolean checkAccess(List<AksesMenu.Akses> dataMenu, String menu, CharAt charAt) {
        int id = AksesMenu.searchMenuByName(dataMenu, menu);
        if (id >= 0) {
            if ('0' == dataMenu.get(id).akses.charAt(charAt.getValue())) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public void getAccess(List<AksesMenu.Akses> dataMenu) {
        //MAIN MENU
        if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_ISU_PRODUKSI, Login.CharAt.VIEW)) {
            MainForm.jMenu_produksi_isu.setEnabled(false);
            MainForm.jMenu_produksi_isu.setVisible(false);
        } else {
            MainForm.jMenu_produksi_isu.setEnabled(true);
            MainForm.jMenu_produksi_isu.setVisible(true);
        }
        if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_ABSEN_PRODUKSI, Login.CharAt.VIEW)) {
            MainForm.jMenu_TV_belumAbsen.setEnabled(false);
            MainForm.jMenu_TV_belumAbsen.setVisible(false);
        } else {
            MainForm.jMenu_TV_belumAbsen.setEnabled(true);
            MainForm.jMenu_TV_belumAbsen.setVisible(true);
        }
        //MENU BAHAN BAKU
        if (checkAccess(dataMenu, AksesMenu.MENU_BAHAN_BAKU, Login.CharAt.VIEW)) {
            MainForm.jMenu_bahan_baku.setEnabled(false);
            MainForm.jMenu_bahan_baku.setVisible(false);
        } else {
            MainForm.jMenu_bahan_baku.setEnabled(true);
            MainForm.jMenu_bahan_baku.setVisible(true);

            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_SUPPLIER, Login.CharAt.VIEW)) {
                MainForm.jMenu_baku_supplier.setEnabled(false);
                MainForm.jMenu_baku_supplier.setVisible(false);
            } else {
                MainForm.jMenu_baku_supplier.setEnabled(true);
                MainForm.jMenu_baku_supplier.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_CUSTOMER, Login.CharAt.VIEW)) {
                MainForm.jMenu_baku_Customer.setEnabled(false);
                MainForm.jMenu_baku_Customer.setVisible(false);
            } else {
                MainForm.jMenu_baku_Customer.setEnabled(true);
                MainForm.jMenu_baku_Customer.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_RUMAH_BURUNG, Login.CharAt.VIEW)) {
                MainForm.jMenu_baku_rumahburung.setEnabled(false);
                MainForm.jMenu_baku_rumahburung.setVisible(false);
            } else {
                MainForm.jMenu_baku_rumahburung.setEnabled(true);
                MainForm.jMenu_baku_rumahburung.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_GRADE_BAKU, Login.CharAt.VIEW)) {
                MainForm.jMenu_baku_grade.setEnabled(false);
                MainForm.jMenu_baku_grade.setVisible(false);
            } else {
                MainForm.jMenu_baku_grade.setEnabled(true);
                MainForm.jMenu_baku_grade.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_BAHAN_BAKU_MASUK, Login.CharAt.VIEW)) {
                MainForm.jMenu_baku_masuk.setEnabled(false);
                MainForm.jMenu_baku_masuk.setVisible(false);
            } else {
                MainForm.jMenu_baku_masuk.setEnabled(true);
                MainForm.jMenu_baku_masuk.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_LAPORAN_PRODUKSI, Login.CharAt.VIEW)) {
                MainForm.jMenu_baku_lp.setEnabled(false);
                MainForm.jMenu_baku_lp.setVisible(false);
                MainForm.jMenu_baku_lp_cheat.setEnabled(false);
                MainForm.jMenu_baku_lp_cheat.setVisible(false);
            } else {
                MainForm.jMenu_baku_lp.setEnabled(true);
                MainForm.jMenu_baku_lp.setVisible(true);
                MainForm.jMenu_baku_lp_cheat.setEnabled(true);
                MainForm.jMenu_baku_lp_cheat.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_LAPORAN_PRODUKSI_SESEKAN, Login.CharAt.VIEW)) {
                MainForm.jMenu_baku_lp_sesekan.setEnabled(false);
                MainForm.jMenu_baku_lp_sesekan.setVisible(false);
            } else {
                MainForm.jMenu_baku_lp_sesekan.setEnabled(true);
                MainForm.jMenu_baku_lp_sesekan.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_LAPORAN_PRODUKSI_SAPON, Login.CharAt.VIEW)) {
                MainForm.jMenu_baku_lp_sapon.setEnabled(false);
                MainForm.jMenu_baku_lp_sapon.setVisible(false);
            } else {
                MainForm.jMenu_baku_lp_sapon.setEnabled(true);
                MainForm.jMenu_baku_lp_sapon.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_BAKU_KELUAR, Login.CharAt.VIEW)) {
                MainForm.jMenu_baku_keluar.setEnabled(false);
                MainForm.jMenu_baku_keluar.setVisible(false);
            } else {
                MainForm.jMenu_baku_keluar.setEnabled(true);
                MainForm.jMenu_baku_keluar.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_REKAPITULASI, Login.CharAt.VIEW)) {
                MainForm.jMenu_baku_DataBahanBaku.setEnabled(false);
                MainForm.jMenu_baku_DataBahanBaku.setVisible(false);
            } else {
                MainForm.jMenu_baku_DataBahanBaku.setEnabled(true);
                MainForm.jMenu_baku_DataBahanBaku.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_ADJUSTMENT_BAHAN_BAKU, Login.CharAt.VIEW)) {
                MainForm.jMenu_baku_Adjustment.setEnabled(false);
                MainForm.jMenu_baku_Adjustment.setVisible(false);
            } else {
                MainForm.jMenu_baku_Adjustment.setEnabled(true);
                MainForm.jMenu_baku_Adjustment.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_PEMBELIAN_BAHAN_BAKU, Login.CharAt.VIEW)) {
                MainForm.jMenu_baku_pembelian_baku.setEnabled(false);
                MainForm.jMenu_baku_pembelian_baku.setVisible(false);
            } else {
                MainForm.jMenu_baku_pembelian_baku.setEnabled(true);
                MainForm.jMenu_baku_pembelian_baku.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_KARTU_CAMPURAN, Login.CharAt.VIEW)) {
                MainForm.jMenu_baku_KartuCampuran.setEnabled(false);
                MainForm.jMenu_baku_KartuCampuran.setVisible(false);
            } else {
                MainForm.jMenu_baku_KartuCampuran.setEnabled(true);
                MainForm.jMenu_baku_KartuCampuran.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_BONUS_PANEN, Login.CharAt.VIEW)) {
                MainForm.jMenu_baku_Bonus_Panen.setEnabled(false);
                MainForm.jMenu_baku_Bonus_Panen.setVisible(false);
            } else {
                MainForm.jMenu_baku_Bonus_Panen.setEnabled(true);
                MainForm.jMenu_baku_Bonus_Panen.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_MASTER_BULU_UPAH, Login.CharAt.VIEW)) {
                MainForm.jMenu_baku_master_upah.setEnabled(false);
                MainForm.jMenu_baku_master_upah.setVisible(false);
            } else {
                MainForm.jMenu_baku_master_upah.setEnabled(true);
                MainForm.jMenu_baku_master_upah.setVisible(true);
            }
        }

        //MENU PRODUKSI
        if (checkAccess(dataMenu, AksesMenu.MENU_PRODUKSI, Login.CharAt.VIEW)) {
            MainForm.jMenu_produksi.setEnabled(false);
            MainForm.jMenu_produksi.setVisible(false);
        } else {
            MainForm.jMenu_produksi.setEnabled(true);
            MainForm.jMenu_produksi.setVisible(true);
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_RENDAM, Login.CharAt.VIEW)) {
                MainForm.jMenu_produksi_rendam.setEnabled(false);
                MainForm.jMenu_produksi_rendam.setVisible(false);
            } else {
                MainForm.jMenu_produksi_rendam.setEnabled(true);
                MainForm.jMenu_produksi_rendam.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_CUCI, Login.CharAt.VIEW)) {
                MainForm.jMenu_produksi_cuci.setEnabled(false);
                MainForm.jMenu_produksi_cuci.setVisible(false);
            } else {
                MainForm.jMenu_produksi_cuci.setEnabled(true);
                MainForm.jMenu_produksi_cuci.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_CABUT, Login.CharAt.VIEW)) {
                MainForm.jMenu_produksi_cabut.setEnabled(false);
                MainForm.jMenu_produksi_cabut.setVisible(false);
            } else {
                MainForm.jMenu_produksi_cabut.setEnabled(true);
                MainForm.jMenu_produksi_cabut.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_CETAK, Login.CharAt.VIEW)) {
                MainForm.jMenu_produksi_cetak.setEnabled(false);
                MainForm.jMenu_produksi_cetak.setVisible(false);
            } else {
                MainForm.jMenu_produksi_cetak.setEnabled(true);
                MainForm.jMenu_produksi_cetak.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_F2, Login.CharAt.VIEW)) {
                MainForm.jMenu_produksi_f2.setEnabled(false);
                MainForm.jMenu_produksi_f2.setVisible(false);
            } else {
                MainForm.jMenu_produksi_f2.setEnabled(true);
                MainForm.jMenu_produksi_f2.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_PROGRESS_LP, Login.CharAt.VIEW)) {
                MainForm.jMenu_produksi_progress.setEnabled(false);
                MainForm.jMenu_produksi_progress.setVisible(false);
            } else {
                MainForm.jMenu_produksi_progress.setEnabled(true);
                MainForm.jMenu_produksi_progress.setVisible(true);
            }
        }

        //MENU ATB
        if (checkAccess(dataMenu, AksesMenu.MENU_ATB, Login.CharAt.VIEW)) {
            MainForm.jMenu_atb.setEnabled(false);
            MainForm.jMenu_atb.setVisible(false);
        } else {
            MainForm.jMenu_atb.setEnabled(true);
            MainForm.jMenu_atb.setVisible(true);
            if (checkAccess(dataMenu, AksesMenu.MENU_ATB_ITEM_KARYAWAN_ATB, Login.CharAt.VIEW)) {
                MainForm.jMenuItem_atb_dataKaryawan.setEnabled(false);
                MainForm.jMenuItem_atb_dataKaryawan.setVisible(false);
            } else {
                MainForm.jMenuItem_atb_dataKaryawan.setEnabled(true);
                MainForm.jMenuItem_atb_dataKaryawan.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ATB_ITEM_PRODUKSI_ATB, Login.CharAt.VIEW)) {
                MainForm.jMenuItem_atb_dataProduksi.setEnabled(false);
                MainForm.jMenuItem_atb_dataProduksi.setVisible(false);
            } else {
                MainForm.jMenuItem_atb_dataProduksi.setEnabled(true);
                MainForm.jMenuItem_atb_dataProduksi.setVisible(true);
            }
        }

        //MENU SUB
        if (checkAccess(dataMenu, AksesMenu.MENU_MANAJEMEN_SUB, Login.CharAt.VIEW)) {
            MainForm.jMenu_sub.setEnabled(false);
            MainForm.jMenu_sub.setVisible(false);
        } else {
            MainForm.jMenu_sub.setEnabled(true);
            MainForm.jMenu_sub.setVisible(true);
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_LAPORAN_PRODUKSI_SESEKAN, Login.CharAt.VIEW)) {
                MainForm.jMenu_baku_lp_sesekan.setEnabled(false);
                MainForm.jMenu_baku_lp_sesekan.setVisible(false);
            } else {
                MainForm.jMenu_baku_lp_sesekan.setEnabled(true);
                MainForm.jMenu_baku_lp_sesekan.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_LAPORAN_PRODUKSI_SAPON, Login.CharAt.VIEW)) {
                MainForm.jMenu_baku_lp_sapon.setEnabled(false);
                MainForm.jMenu_baku_lp_sapon.setVisible(false);
            } else {
                MainForm.jMenu_baku_lp_sapon.setEnabled(true);
                MainForm.jMenu_baku_lp_sapon.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_DATA_SUB_WALETA, Login.CharAt.VIEW)) {
                MainForm.jMenu_Sub_dataSub.setEnabled(false);
                MainForm.jMenu_Sub_dataSub.setVisible(false);
            } else {
                MainForm.jMenu_Sub_dataSub.setEnabled(true);
                MainForm.jMenu_Sub_dataSub.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_CABUT_SUB, Login.CharAt.VIEW)) {
                MainForm.jMenu_produksi_cabut_sub.setEnabled(false);
                MainForm.jMenu_produksi_cabut_sub.setVisible(false);
            } else {
                MainForm.jMenu_produksi_cabut_sub.setEnabled(true);
                MainForm.jMenu_produksi_cabut_sub.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_DATA_KARYAWAN_SUB, Login.CharAt.VIEW)) {
                MainForm.jMenu_hrd_karyawan_sub.setEnabled(false);
                MainForm.jMenu_hrd_karyawan_sub.setVisible(false);
            } else {
                MainForm.jMenu_hrd_karyawan_sub.setEnabled(true);
                MainForm.jMenu_hrd_karyawan_sub.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_ABSEN_SUB, Login.CharAt.VIEW)) {
                MainForm.jMenu_hrd_dataFinger_Sub.setEnabled(false);
                MainForm.jMenu_hrd_dataFinger_Sub.setVisible(false);
            } else {
                MainForm.jMenu_hrd_dataFinger_Sub.setEnabled(true);
                MainForm.jMenu_hrd_dataFinger_Sub.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_PENGGAJIAN_SUB, Login.CharAt.VIEW)) {
                MainForm.jMenu_Sub_penggajian.setEnabled(false);
                MainForm.jMenu_Sub_penggajian.setVisible(false);
            } else {
                MainForm.jMenu_Sub_penggajian.setEnabled(true);
                MainForm.jMenu_Sub_penggajian.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_PIUTANG_KARYAWAN_SUB, Login.CharAt.VIEW)) {
                MainForm.jMenu_Sub_Piutang_sub.setEnabled(false);
                MainForm.jMenu_Sub_Piutang_sub.setVisible(false);
            } else {
                MainForm.jMenu_Sub_Piutang_sub.setEnabled(true);
                MainForm.jMenu_Sub_Piutang_sub.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_TARIF_UPAH_BOBOT_LP_SUB, Login.CharAt.VIEW)) {
                MainForm.jMenu_Sub_tarif_upah.setEnabled(false);
                MainForm.jMenu_Sub_tarif_upah.setVisible(false);
            } else {
                MainForm.jMenu_Sub_tarif_upah.setEnabled(true);
                MainForm.jMenu_Sub_tarif_upah.setVisible(true);
            }
        }

        //MENU BAHAN JADI
        if (checkAccess(dataMenu, AksesMenu.MENU_BAHAN_JADI, Login.CharAt.VIEW)) {
            MainForm.jMenu_BahanJadi.setEnabled(false);
            MainForm.jMenu_BahanJadi.setVisible(false);
        } else {
            MainForm.jMenu_BahanJadi.setEnabled(true);
            MainForm.jMenu_BahanJadi.setVisible(true);
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_BAHAN_JADI_MASUK, Login.CharAt.VIEW)) {
                MainForm.jMenuItem_gudang_bahan_jadi.setEnabled(false);
                MainForm.jMenuItem_gudang_bahan_jadi.setVisible(false);
            } else {
                MainForm.jMenuItem_gudang_bahan_jadi.setEnabled(true);
                MainForm.jMenuItem_gudang_bahan_jadi.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_TUTUPAN_GRADING, Login.CharAt.VIEW)) {
                MainForm.jMenuItem_tutupan_grading.setEnabled(false);
                MainForm.jMenuItem_tutupan_grading.setVisible(false);
            } else {
                MainForm.jMenuItem_tutupan_grading.setEnabled(true);
                MainForm.jMenuItem_tutupan_grading.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_DATA_BOX_BAHAN_JADI, Login.CharAt.VIEW)) {
                MainForm.jMenuItem_Data_box_bahan_jadi.setEnabled(false);
                MainForm.jMenuItem_Data_box_bahan_jadi.setVisible(false);
            } else {
                MainForm.jMenuItem_Data_box_bahan_jadi.setEnabled(true);
                MainForm.jMenuItem_Data_box_bahan_jadi.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_STOCK_BAHAN_JADI, Login.CharAt.VIEW)) {
                MainForm.jMenuItem_stock_bahan_jadi.setEnabled(false);
                MainForm.jMenuItem_stock_bahan_jadi.setVisible(false);
            } else {
                MainForm.jMenuItem_stock_bahan_jadi.setEnabled(true);
                MainForm.jMenuItem_stock_bahan_jadi.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_MASTER_GRADE_BAHAN_JADI, Login.CharAt.VIEW)) {
                MainForm.jMenuItem_GradeBahanJadi.setEnabled(false);
                MainForm.jMenuItem_GradeBahanJadi.setVisible(false);
            } else {
                MainForm.jMenuItem_GradeBahanJadi.setEnabled(true);
                MainForm.jMenuItem_GradeBahanJadi.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_BONUS_GRADING, Login.CharAt.VIEW)) {
                MainForm.jMenuItem_Bonus_Grading.setEnabled(false);
                MainForm.jMenuItem_Bonus_Grading.setVisible(false);
            } else {
                MainForm.jMenuItem_Bonus_Grading.setEnabled(true);
                MainForm.jMenuItem_Bonus_Grading.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_LAPORAN_PRODUKSI_BAHAN_JADI, Login.CharAt.VIEW)) {
                MainForm.jMenuItem_LaporanProduksi_BJ.setEnabled(false);
                MainForm.jMenuItem_LaporanProduksi_BJ.setVisible(false);
            } else {
                MainForm.jMenuItem_LaporanProduksi_BJ.setEnabled(true);
                MainForm.jMenuItem_LaporanProduksi_BJ.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_PEMBELIAN_BAHAN_JADI, Login.CharAt.VIEW)) {
                MainForm.jMenuItem_PembelianBJ.setEnabled(false);
                MainForm.jMenuItem_PembelianBJ.setVisible(false);
            } else {
                MainForm.jMenuItem_PembelianBJ.setEnabled(true);
                MainForm.jMenuItem_PembelianBJ.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_PRICE_LIST_BJ, Login.CharAt.VIEW)) {
                MainForm.jMenuItem_PriceList.setEnabled(false);
                MainForm.jMenuItem_PriceList.setVisible(false);
            } else {
                MainForm.jMenuItem_PriceList.setEnabled(true);
                MainForm.jMenuItem_PriceList.setVisible(true);
            }
        }

        //MENU PACKING
        if (checkAccess(dataMenu, AksesMenu.MENU_PACKING, Login.CharAt.VIEW)) {
            MainForm.jMenu_packing.setEnabled(false);
            MainForm.jMenu_packing.setVisible(false);
        } else {
            MainForm.jMenu_packing.setEnabled(true);
            MainForm.jMenu_packing.setVisible(true);
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_DATA_PACKING, Login.CharAt.VIEW)) {
                MainForm.JMenuItem_DataPacking.setEnabled(false);
                MainForm.JMenuItem_DataPacking.setVisible(false);
            } else {
                MainForm.JMenuItem_DataPacking.setEnabled(true);
                MainForm.JMenuItem_DataPacking.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_DATA_PENGIRIMAN, Login.CharAt.VIEW)) {
                MainForm.jMenuItem_DataPengiriman.setEnabled(false);
                MainForm.jMenuItem_DataPengiriman.setVisible(false);
            } else {
                MainForm.jMenuItem_DataPengiriman.setEnabled(true);
                MainForm.jMenuItem_DataPengiriman.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_LIST_BUYER, Login.CharAt.VIEW)) {
                MainForm.jMenuItem_buyer.setEnabled(false);
                MainForm.jMenuItem_buyer.setVisible(false);
            } else {
                MainForm.jMenuItem_buyer.setEnabled(true);
                MainForm.jMenuItem_buyer.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_DATA_SPK, Login.CharAt.VIEW)) {
                MainForm.jMenuItem_SPK.setEnabled(false);
                MainForm.jMenuItem_SPK.setVisible(false);
            } else {
                MainForm.jMenuItem_SPK.setEnabled(true);
                MainForm.jMenuItem_SPK.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_DATA_PAYMENT_REPORT, Login.CharAt.VIEW)) {
                MainForm.jMenuItem_PaymentReport.setEnabled(false);
                MainForm.jMenuItem_PaymentReport.setVisible(false);
            } else {
                MainForm.jMenuItem_PaymentReport.setEnabled(true);
                MainForm.jMenuItem_PaymentReport.setVisible(true);
            }
        }

        //MENU QC
        if (checkAccess(dataMenu, AksesMenu.MENU_QC, Login.CharAt.VIEW)) {
            MainForm.jMenu_QC.setEnabled(false);
            MainForm.jMenu_QC.setVisible(false);
        } else {
            MainForm.jMenu_QC.setEnabled(true);
            MainForm.jMenu_QC.setVisible(true);
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_LAB_BAHAN_BAKU, Login.CharAt.VIEW)) {
                MainForm.jMenu_qc_bahanbaku.setEnabled(false);
                MainForm.jMenu_qc_bahanbaku.setVisible(false);
            } else {
                MainForm.jMenu_qc_bahanbaku.setEnabled(true);
                MainForm.jMenu_qc_bahanbaku.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_LAB_LP, Login.CharAt.VIEW)) {
                MainForm.jMenu_qc_lp.setEnabled(false);
                MainForm.jMenu_qc_lp.setVisible(false);
            } else {
                MainForm.jMenu_qc_lp.setEnabled(true);
                MainForm.jMenu_qc_lp.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_TREATMENT_LP, Login.CharAt.VIEW)) {
                MainForm.jMenu_qc_treatment.setEnabled(false);
                MainForm.jMenu_qc_treatment.setVisible(false);
            } else {
                MainForm.jMenu_qc_treatment.setEnabled(true);
                MainForm.jMenu_qc_treatment.setVisible(true);
            }
//            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_PEMANASAN_BAHAN_JADI, Login.CharAt.VIEW)) {
//                MainForm.jMenu_qc_pemanasan_jadi.setEnabled(false);
//                MainForm.jMenu_qc_pemanasan_jadi.setVisible(false);
//            } else {
//                MainForm.jMenu_qc_pemanasan_jadi.setEnabled(true);
//                MainForm.jMenu_qc_pemanasan_jadi.setVisible(true);
//            }
        }

        //MENU HRD
        if (checkAccess(dataMenu, AksesMenu.MENU_HRD, Login.CharAt.VIEW)) {
            MainForm.jMenu_HRD.setEnabled(false);
            MainForm.jMenu_HRD.setVisible(false);
        } else {
            MainForm.jMenu_HRD.setEnabled(true);
            MainForm.jMenu_HRD.setVisible(true);
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_DATA_KARYAWAN, Login.CharAt.VIEW)) {
                MainForm.jMenu_hrd_karyawan.setEnabled(false);
                MainForm.jMenu_hrd_karyawan.setVisible(false);
                MainForm.jMenu_hrd_karyawan_wltsub.setEnabled(false);
                MainForm.jMenu_hrd_karyawan_wltsub.setVisible(false);
            } else {
                MainForm.jMenu_hrd_karyawan.setEnabled(true);
                MainForm.jMenu_hrd_karyawan.setVisible(true);
                MainForm.jMenu_hrd_karyawan_wltsub.setEnabled(true);
                MainForm.jMenu_hrd_karyawan_wltsub.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_DATA_IJIN_KELUAR, Login.CharAt.VIEW)) {
                MainForm.jMenu_hrd_ijinKeluar.setEnabled(false);
                MainForm.jMenu_hrd_ijinKeluar.setVisible(false);
            } else {
                MainForm.jMenu_hrd_ijinKeluar.setEnabled(true);
                MainForm.jMenu_hrd_ijinKeluar.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_DATA_IJIN_ABSEN_CUTI, Login.CharAt.VIEW)) {
                MainForm.jMenu_hrd_cuti.setEnabled(false);
                MainForm.jMenu_hrd_cuti.setVisible(false);
            } else {
                MainForm.jMenu_hrd_cuti.setEnabled(true);
                MainForm.jMenu_hrd_cuti.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_DATA_LEMBUR, Login.CharAt.VIEW)) {
                MainForm.jMenu_hrd_lembur.setEnabled(false);
                MainForm.jMenu_hrd_lembur.setVisible(false);
            } else {
                MainForm.jMenu_hrd_lembur.setEnabled(true);
                MainForm.jMenu_hrd_lembur.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_DATA_PINDAH_BAGIAN, Login.CharAt.VIEW)) {
                MainForm.jMenu_hrd_grupCabut.setEnabled(false);
                MainForm.jMenu_hrd_grupCabut.setVisible(false);
            } else {
                MainForm.jMenu_hrd_grupCabut.setEnabled(true);
                MainForm.jMenu_hrd_grupCabut.setVisible(true);
            }

            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_DEPT_BAG, Login.CharAt.VIEW)) {
                MainForm.jMenu_hrd_departemen.setEnabled(false);
                MainForm.jMenu_hrd_departemen.setVisible(false);
            } else {
                MainForm.jMenu_hrd_departemen.setEnabled(true);
                MainForm.jMenu_hrd_departemen.setVisible(true);
            }

            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_TANGGAL_LIBUR, Login.CharAt.VIEW)) {
                MainForm.jMenu_hrd_dataLibur.setEnabled(false);
                MainForm.jMenu_hrd_dataLibur.setVisible(false);
            } else {
                MainForm.jMenu_hrd_dataLibur.setEnabled(true);
                MainForm.jMenu_hrd_dataLibur.setVisible(true);
            }

            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_TBT, Login.CharAt.VIEW)) {
                MainForm.jMenu_hrd_dataTBT.setEnabled(false);
                MainForm.jMenu_hrd_dataTBT.setVisible(false);
            } else {
                MainForm.jMenu_hrd_dataTBT.setEnabled(true);
                MainForm.jMenu_hrd_dataTBT.setVisible(true);
            }

            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_ABSEN, Login.CharAt.VIEW)) {
                MainForm.jMenu_hrd_dataFinger.setEnabled(false);
                MainForm.jMenu_hrd_dataFinger.setVisible(false);
            } else {
                MainForm.jMenu_hrd_dataFinger.setEnabled(true);
                MainForm.jMenu_hrd_dataFinger.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_JAM_KERJA, Login.CharAt.VIEW)) {
                MainForm.jMenu_hrd_dataJamKerja.setEnabled(false);
                MainForm.jMenu_hrd_dataJamKerja.setVisible(false);
            } else {
                MainForm.jMenu_hrd_dataJamKerja.setEnabled(true);
                MainForm.jMenu_hrd_dataJamKerja.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_JEMPUTAN, Login.CharAt.VIEW)) {
                MainForm.jMenu_hrd_dataJalurJemputan.setEnabled(false);
                MainForm.jMenu_hrd_dataJalurJemputan.setVisible(false);
            } else {
                MainForm.jMenu_hrd_dataJalurJemputan.setEnabled(true);
                MainForm.jMenu_hrd_dataJalurJemputan.setVisible(true);
            }
        }

        //MENU KEUANGAN
        if (checkAccess(dataMenu, AksesMenu.MENU_KEUANGAN, Login.CharAt.VIEW)) {
            MainForm.jMenu_Keuangan.setEnabled(false);
            MainForm.jMenu_Keuangan.setVisible(false);
        } else {
            MainForm.jMenu_Keuangan.setEnabled(true);
            MainForm.jMenu_Keuangan.setVisible(true);
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_BAHAN_BAKU, Login.CharAt.VIEW)) {
                MainForm.jMenu_Keu_Baku.setEnabled(false);
                MainForm.jMenu_Keu_Baku.setVisible(false);
            } else {
                MainForm.jMenu_Keu_Baku.setEnabled(true);
                MainForm.jMenu_Keu_Baku.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_PENGGAJIAN_PRODUKSI, Login.CharAt.VIEW)) {
                MainForm.jMenu_Keu_Produksi.setEnabled(false);
                MainForm.jMenu_Keu_Produksi.setVisible(false);
            } else {
                MainForm.jMenu_Keu_Produksi.setEnabled(true);
                MainForm.jMenu_Keu_Produksi.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_BARANG_JADI, Login.CharAt.VIEW)) {
                MainForm.jMenu_Keu_BarangJadi.setEnabled(false);
                MainForm.jMenu_Keu_BarangJadi.setVisible(false);
            } else {
                MainForm.jMenu_Keu_BarangJadi.setEnabled(true);
                MainForm.jMenu_Keu_BarangJadi.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_DATA_KARYAWAN_KEUANGAN, Login.CharAt.VIEW)) {
                MainForm.jMenu_Keu_HR.setEnabled(false);
                MainForm.jMenu_Keu_HR.setVisible(false);
            } else {
                MainForm.jMenu_Keu_HR.setEnabled(true);
                MainForm.jMenu_Keu_HR.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_PURCHASING, Login.CharAt.VIEW)) {
                MainForm.jMenu_Keu_purchasing.setEnabled(false);
                MainForm.jMenu_Keu_purchasing.setVisible(false);
            } else {
                MainForm.jMenu_Keu_purchasing.setEnabled(true);
                MainForm.jMenu_Keu_purchasing.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_LEMBUR_STAFF, Login.CharAt.VIEW)) {
                MainForm.jMenuItem_Keu_LemburStaff.setEnabled(false);
                MainForm.jMenuItem_Keu_LemburStaff.setVisible(false);
                MainForm.jMenuItem_Keu_LemburStaff_baru.setEnabled(false);
                MainForm.jMenuItem_Keu_LemburStaff_baru.setVisible(false);
            } else {
                MainForm.jMenuItem_Keu_LemburStaff.setEnabled(true);
                MainForm.jMenuItem_Keu_LemburStaff.setVisible(true);
                MainForm.jMenuItem_Keu_LemburStaff_baru.setEnabled(true);
                MainForm.jMenuItem_Keu_LemburStaff_baru.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_DATA_PENGIRIMAN_KEUANGAN, Login.CharAt.VIEW)) {
                MainForm.jMenuItem_Keu_DataEkspor.setEnabled(false);
                MainForm.jMenuItem_Keu_DataEkspor.setVisible(false);
            } else {
                MainForm.jMenuItem_Keu_DataEkspor.setEnabled(true);
                MainForm.jMenuItem_Keu_DataEkspor.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_SALES_AND_PAYMENT_REPORT, Login.CharAt.VIEW)) {
                MainForm.jMenuItem_Keu_Payment.setEnabled(false);
                MainForm.jMenuItem_Keu_Payment.setVisible(false);
            } else {
                MainForm.jMenuItem_Keu_Payment.setEnabled(true);
                MainForm.jMenuItem_Keu_Payment.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_DATA_BIAYA, Login.CharAt.VIEW)) {
                MainForm.jMenu_Keu_Rekap.setEnabled(false);
                MainForm.jMenu_Keu_Rekap.setVisible(false);
            } else {
                MainForm.jMenu_Keu_Rekap.setEnabled(true);
                MainForm.jMenu_Keu_Rekap.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_CASH_ON_BANK, Login.CharAt.VIEW)) {
                MainForm.jMenuItem_Keu_CashOnBank.setEnabled(false);
                MainForm.jMenuItem_Keu_CashOnBank.setVisible(false);
            } else {
                MainForm.jMenuItem_Keu_CashOnBank.setEnabled(true);
                MainForm.jMenuItem_Keu_CashOnBank.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_NERACA, Login.CharAt.VIEW)) {
                MainForm.jMenuItem_Keu_Neraca.setEnabled(false);
                MainForm.jMenuItem_Keu_Neraca.setVisible(false);
            } else {
                MainForm.jMenuItem_Keu_Neraca.setEnabled(true);
                MainForm.jMenuItem_Keu_Neraca.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_AR_AP_ESTA, Login.CharAt.VIEW)) {
                MainForm.jMenuItem_Keu_ARAP_Esta.setEnabled(false);
                MainForm.jMenuItem_Keu_ARAP_Esta.setVisible(false);
            } else {
                MainForm.jMenuItem_Keu_ARAP_Esta.setEnabled(true);
                MainForm.jMenuItem_Keu_ARAP_Esta.setVisible(true);
            }
            if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_LAPORAN_KEUANGAN, Login.CharAt.VIEW)) {
                MainForm.jMenuItem_Keu_Laporan.setEnabled(false);
                MainForm.jMenuItem_Keu_Laporan.setVisible(false);
            } else {
                MainForm.jMenuItem_Keu_Laporan.setEnabled(true);
                MainForm.jMenuItem_Keu_Laporan.setVisible(true);
            }
        }

        //MENU MANAJEMEN
        if (checkAccess(dataMenu, AksesMenu.MENU_MANAJEMEN, Login.CharAt.VIEW)) {
            MainForm.jMenu_manajemen.setEnabled(false);
            MainForm.jMenu_manajemen.setVisible(false);
        } else {
            MainForm.jMenu_manajemen.setEnabled(true);
            MainForm.jMenu_manajemen.setVisible(true);
        }

        if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_CREATE_NEW_USER, Login.CharAt.VIEW)) {
            MainForm.jMenuItem_user_new.setEnabled(false);
            MainForm.jMenuItem_user_new.setVisible(false);
        } else {
            MainForm.jMenuItem_user_new.setEnabled(true);
            MainForm.jMenuItem_user_new.setVisible(true);
        }
        if (checkAccess(dataMenu, AksesMenu.MENU_ITEM_DATA_USER, Login.CharAt.VIEW)) {
            MainForm.jMenuItem_user_view.setEnabled(false);
            MainForm.jMenuItem_user_view.setVisible(false);
        } else {
            MainForm.jMenuItem_user_view.setEnabled(true);
            MainForm.jMenuItem_user_view.setVisible(true);
        }
    }

    public void Submit() {
        try {
            Connection con = Utility.db.getConnection();
            sql = "SELECT `tb_login`.`id_pegawai`, `nama_pegawai`, `tb_karyawan`.`kode_bagian`, `nama_bagian`, `kode_departemen`, `status`, `posisi`, `user` "
                    + "FROM `tb_login` "
                    + "LEFT JOIN `tb_karyawan` ON `tb_login`.`id_pegawai` = `tb_karyawan`.`id_pegawai` "
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian` "
                    + "WHERE `user`=? AND `pass`=?";
            pst = con.prepareStatement(sql);
            pst.setString(1, txt_username.getText());
            pst.setString(2, txt_password.getText());
            rs = pst.executeQuery();
            if (rs.next()) {
                if ("IN".equals(rs.getString("status"))) {
                    String user = rs.getString("user");
                    String idPegawai = rs.getString("id_pegawai");
                    String nama = rs.getString("nama_pegawai");
                    int kode_bagian = rs.getInt("kode_bagian");
                    String nama_bagian = rs.getString("nama_bagian");
                    String departemen = rs.getString("kode_departemen");
                    String posisi = rs.getString("posisi");
                    List<AksesMenu.Akses> dataMenu = new ArrayList<>();
                    sql = "SELECT * FROM `tb_hak_akses` WHERE `user`=?";
                    pst = con.prepareStatement(sql);
                    pst.setString(1, txt_username.getText());
                    rs = pst.executeQuery();
                    while (rs.next()) {
                        dataMenu.add(new AksesMenu.Akses(rs.getString("menu"), rs.getString("hak_akses")));
                    }
                    MainForm main = new MainForm(user, idPegawai, nama, kode_bagian, nama_bagian, departemen, posisi, dataMenu);
                    getAccess(dataMenu);
                    if (!nama_bagian.toUpperCase().contains("KADEP-" + departemen)
                            && kode_bagian != 244
                            && !idPegawai.equals("20180102221")
                            && !idPegawai.equals("20240107935")
                            ) {
                        MainForm.jMenu_Cabuto.setVisible(false);
                    } else {
                        MainForm.jMenu_Cabuto.setVisible(true);
                    }
                    main.pack();
                    main.setLocationRelativeTo(this);
                    main.setEnabled(true);
//                    main.setExtendedState(main.getExtendedState() | MainForm.MAXIMIZED_BOTH);
                    main.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    /*try {
                        UIManager.setLookAndFeel(new DarculaLaf());
                    } catch (UnsupportedLookAndFeelException ex) {
                        Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                    }*/
                    main.setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Pegawai untuk Username " + txt_username.getText() + " sudah keluar!\nharap menghubungi administrator untuk Login!", "Access Denied !", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Username or Password !", "Access Denied !", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void edit() {
        try {
            Connection con = Utility.db.getConnection();
            String a = "SELECT `id_pegawai`, `nama_pegawai`, `kode_bagian`, (SELECT `tb_bagian`.`kode_bagian` FROM `tb_karyawan` LEFT JOIN `tb_grup_cabut` ON `tb_karyawan`.`id_pegawai` = `tb_grup_cabut`.`id_pegawai` LEFT JOIN `tb_bagian` ON `tb_grup_cabut`.`kode_grup` = `tb_bagian`.`nama_bagian` WHERE `tb_karyawan`.`id_pegawai` = A.`id_pegawai`) AS 'kode_baru' FROM `tb_karyawan` A WHERE `status` = 'IN' AND `posisi` = 'PEJUANG' AND `kode_bagian` IN (SELECT `kode_bagian` FROM `tb_bagian` WHERE `kode_departemen` = 'PRODUKSI') HAVING `kode_bagian` <> `kode_baru`";
            pst = con.prepareStatement(a);
            rs = pst.executeQuery();
            int edited = 0, failed = 0;
            while (rs.next()) {
                String c = "UPDATE `tb_karyawan` SET `kode_bagian`='" + rs.getString("kode_baru") + "' WHERE `id_pegawai`='" + rs.getString("id_pegawai") + "'";
                Utility.db.getConnection().createStatement();
                if ((Utility.db.getStatement().executeUpdate(c)) > 0) {
                    System.out.println("OKE");
                    edited++;
                } else {
                    System.out.println("FAILED");
                    failed++;
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void checkVersion() {
        try {
            sql = "SELECT `version`, `harus` FROM `tb_version`";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                if (!rs.getString("version").equals(label_version.getText())) {
                    if (rs.getBoolean("harus")) {
                        JOptionPane.showMessageDialog(this, "your system version is out of date", "Please install a newer version !", JOptionPane.ERROR_MESSAGE);
//                        JOptionPane.showMessageDialog(this, "Sistem harus di update untuk menghindari kesalahan inputan data");
                        try {
                            Runtime.getRuntime().exec("updater.exe");
                            System.exit(0);
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(this, ex.getMessage());
                            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                            System.exit(0);
                        }
                    } else {
                        int dialogResult = JOptionPane.showConfirmDialog(this, "your system version is out of date, Do you want to update?", "Please install a newer version !", JOptionPane.ERROR_MESSAGE);
                        if (dialogResult == JOptionPane.YES_OPTION) {
                            try {
                                Runtime.getRuntime().exec("updater.exe");
                                System.exit(0);
                            } catch (IOException ex) {
                                JOptionPane.showMessageDialog(this, ex.getMessage());
                                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                                System.exit(0);
                            }
                        }
                    }
                } else {
                    //versi sistem dengan database udah sama, udah versi terbaru
                }
            } else {
                JOptionPane.showMessageDialog(this, "versi tidak di temukan, harap menghubungi tim IT");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void ImportDataEdit() {
        try {
            int n = 0;
            chooser.setDialogTitle("Select CSV file to import!");
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                Utility.db.getConnection();
                File file = chooser.getSelectedFile();
                String filename1 = file.getAbsolutePath();
                try ( BufferedReader br = new BufferedReader(new FileReader(filename1))) {
                    String line;
                    String Query = null;
                    try {
                        Utility.db.getConnection().setAutoCommit(false);
                        while ((line = br.readLine()) != null) {
                            String[] value = line.split(";");
//                            Query = "INSERT INTO `tb_grading_bahan_baku` (`no_kartu_waleta`, `kode_grade`, `jumlah_keping`, `total_berat`, `harga_bahanbaku`)\n"
//                                    + "SELECT * FROM (SELECT '" + value[0] + "', '" + value[1] + "', '" + value[2] + "', '" + value[3] + "', '" + value[4] + "') AS tmp\n"
//                                    + "ON DUPLICATE KEY UPDATE\n"
//                                    + "`jumlah_keping` = '" + value[2] + "',\n"
//                                    + "`total_berat` = '" + value[3] + "',\n"
//                                    + "`harga_bahanbaku` = '" + value[4] + "';";
//                            Query = "UPDATE `tb_laporan_produksi` SET \n"
//                                    + "`berat_kering` = '" + value[1] + "'\n"
//                                    + "WHERE `no_laporan_produksi` = '" + value[0] + "'";
//                            Query = "UPDATE `tb_rendam` SET \n"
//                                    + "`waktu_mulai_pengeringan` = '" + value[1] + "',\n"
//                                    + "`waktu_selesai_pengeringan` = '" + value[2] + "',\n"
//                                    + "`waktu_mulai_rendam` = '" + value[3] + "',\n"
//                                    + "`waktu_selesai_rendam` = '" + value[4] + "'\n"
//                                    + "WHERE `no_laporan_produksi` = '" + value[0] + "'";
                            Query = "UPDATE `tb_rendam` SET \n"
                                    + "`waktu_mulai_rendam` = '" + value[1] + "',\n"
                                    + "`waktu_selesai_rendam` = '" + value[2] + "'\n"
                                    + "WHERE `no_laporan_produksi` = '" + value[0] + "'";
//                            Query = "UPDATE `tb_cetak` SET \n"
//                                    + "`waktu_mulai_pengeringan` = '" + value[1] + "',\n"
//                                    + "`waktu_selesai_pengeringan` = '" + value[2] + "'\n"
//                                    + "WHERE `no_laporan_produksi` = '" + value[0] + "'";
//                            Query = "UPDATE `tb_laporan_produksi_sesekan` SET \n"
//                                    + "`rendemen_bersih` = '" + value[1] + "', \n"
//                                    + "`hancuran` = '" + value[2] + "', \n"
//                                    + "`rontokan_kotor` = '" + value[3] + "', \n"
//                                    + "`rontokan_kuning` = '" + value[4] + "', \n"
//                                    + "`tanggal_timbang` = '" + value[5] + "'\n"
//                                    + "WHERE `no_lp_sesekan`='" + value[0] + "';";
//                            for (int i = 0; i < Integer.valueOf(value[2]); i++) {
//                                Query = "INSERT INTO `tb_aset_unit`(`kode_unit`, `kode_nota_detail`)\n"
//                                        + "SELECT CONCAT('23-" + value[1] + "-', LPAD(COUNT(`kode_unit`)+1, 5, '0')) AS 'kode_unit', '" + value[0] + "' AS 'kode_nota_detail'\n"
//                                        + "FROM `tb_aset_unit`\n"
//                                        + "WHERE `kode_unit` LIKE '23-" + value[1] + "-%'";
//                                System.out.println(Query);
//                                Utility.db.getStatement().executeUpdate(Query);
//                                n++;
//                            }
                            Utility.db.getStatement().executeUpdate(Query);
                            n++;
                            System.out.println(Query);
                        }
                        Utility.db.getConnection().commit();
                        JOptionPane.showMessageDialog(this, "Data Berhasil Masuk : " + n);
                    } catch (Exception ex) {
                        Utility.db.getConnection().rollback();
                        JOptionPane.showMessageDialog(this, ex + "\n" + Query);
                        Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        Utility.db.getConnection().setAutoCommit(true);
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void ImportDataEdit_sub() {
        try {
            Utility.db_sub.connect();
            int n = 0;
            chooser.setDialogTitle("Select CSV file to import!");
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                Utility.db_sub.getConnection();
                File file = chooser.getSelectedFile();
                String filename1 = file.getAbsolutePath();
                try ( BufferedReader br = new BufferedReader(new FileReader(filename1))) {
                    String line;
                    String Query = null;
                    try {
                        Utility.db_sub.getConnection().setAutoCommit(false);
                        while ((line = br.readLine()) != null) {
                            String[] value = line.split(";");
                            Query = "UPDATE `tb_karyawan` SET \n"
                                    + "`id_pegawai` = '" + value[1] + "' \n"
                                    + "WHERE `id_pegawai` = '" + value[0] + "' ";
                            Utility.db_sub.getStatement().executeUpdate(Query);
                            System.out.println(Query);
                            n++;
                        }
                        Utility.db_sub.getConnection().commit();
                        JOptionPane.showMessageDialog(this, "Data Berhasil Masuk : " + n);
                    } catch (Exception ex) {
                        Utility.db_sub.getConnection().rollback();
                        JOptionPane.showMessageDialog(this, ex + "\n" + Query);
                        Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        Utility.db_sub.getConnection().setAutoCommit(true);
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void ImportDataEdit_cabuto() {
        try {
            Utility.db_cabuto.connect();
            int n = 0;
            chooser.setDialogTitle("Select CSV file to import!");
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                Utility.db_cabuto.getConnection();
                File file = chooser.getSelectedFile();
                String filename1 = file.getAbsolutePath();
                try ( BufferedReader br = new BufferedReader(new FileReader(filename1))) {
                    String line;
                    String Query = null;
                    try {
                        Utility.db_cabuto.getConnection().setAutoCommit(false);
                        while ((line = br.readLine()) != null) {
                            String[] value = line.split(";");
                            Query = "UPDATE `tb_lp` SET `harga_baku`='" + value[1] + "' "
                                    + "WHERE `no_laporan_produksi` = '" + value[0] + "' ";
                            Utility.db_cabuto.getStatement().executeUpdate(Query);
                            System.out.println(Query);
                            n++;
                        }
                        Utility.db_cabuto.getConnection().commit();
                        JOptionPane.showMessageDialog(this, "Data Berhasil Masuk : " + n);
                    } catch (Exception ex) {
                        Utility.db_cabuto.getConnection().rollback();
                        JOptionPane.showMessageDialog(this, ex + "\n" + Query);
                        Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        Utility.db_cabuto.getConnection().setAutoCommit(true);
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void getcnyidr() {
        try {
            URL url = new URL("http://api.exchangeratesapi.io/v1/latest?access_key=622f667f4d350e4a7fff78f47a68fcbe");
            URLConnection conn = url.openConnection();
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String json = rd.readLine();
            JSONObject obj = (JSONObject) JSONValue.parse(json);
            JSONObject rates = (JSONObject) obj.get("rates");
            double cny = (double) rates.get("CNY");
            double idr = (double) rates.get("IDR");
            System.out.println(cny);
            System.out.println(idr);
            System.out.println(idr / cny);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Login() {
//        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//            System.out.println("Name: " + info.getName());
//            System.out.println("Class: " + info.getClassName());
//            System.out.println();
//        }
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        this.setResizable(false);
        this.setUndecorated(true);
//        String a = new MD5().encodeString("abc", "1");
//        LocalDate dt = LocalDate.of(2021, 8, 19);
//        System.out.println("hari kamis terakhir " + dt.with(TemporalAdjusters.previous(DayOfWeek.THURSDAY)));
        initComponents();
        label_forgot_password.setText("Forgot password?");
        user_image.setIcon(Utility.ResizeImageIcon(new javax.swing.ImageIcon(getClass().getResource("/waleta_system/Images/user.png")), user_image.getWidth(), user_image.getHeight()));
        lock_image.setIcon(Utility.ResizeImageIcon(new javax.swing.ImageIcon(getClass().getResource("/waleta_system/Images/Lock.png")), lock_image.getWidth(), lock_image.getHeight()));
        waleta_image.setIcon(Utility.ResizeImageIcon(new javax.swing.ImageIcon(getClass().getResource("/waleta_system/Images/logo_waleta.jpg")), waleta_image.getWidth(), waleta_image.getHeight()));

        Utility.db.connect();
        checkVersion();
//        ImportDataEdit();
//        ImportDataEdit_sub();
//        ImportDataEdit_cabuto();
        //        try {
//            BufferedImage img = com.google.zxing.client.j2se.MatrixToImageWriter.toBufferedImage(
//                    new com.google.zxing.qrcode.QRCodeWriter().encode(
//                            "qwe",
//                            com.google.zxing.BarcodeFormat.QR_CODE,
//                            300, 300
//                    )
//            );
//            waleta_image.setIcon(new ImageIcon(img));
//            waleta_image.setIcon(Utility.ResizeImageIcon(new javax.swing.ImageIcon(img), waleta_image.getWidth(), waleta_image.getHeight()));
//        } catch (Exception e) {
//        }
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
        lock_image = new javax.swing.JLabel();
        waleta_image = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txt_password = new javax.swing.JPasswordField();
        txt_username = new javax.swing.JTextField();
        user_image = new javax.swing.JLabel();
        button_login = new javax.swing.JButton();
        button_cancel = new javax.swing.JButton();
        label_forgot_password = new javax.swing.JLabel();
        check_password = new javax.swing.JCheckBox();
        label_version = new javax.swing.JLabel();
        label1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 255, 204)));

        lock_image.setBackground(new java.awt.Color(255, 255, 255));

        waleta_image.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText(waleta_system.Class.Vocabulary.getWord(waleta_system.Class.Vocabulary.NAMA_PENGGUNA));

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText(waleta_system.Class.Vocabulary.getWord(waleta_system.Class.Vocabulary.KATA_SANDI));

        txt_password.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_password.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_password.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_passwordKeyPressed(evt);
            }
        });

        txt_username.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_username.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_username.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_usernameKeyPressed(evt);
            }
        });

        user_image.setBackground(new java.awt.Color(255, 255, 255));

        button_login.setBackground(new java.awt.Color(153, 255, 204));
        button_login.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_login.setText("Log In");
        button_login.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_loginActionPerformed(evt);
            }
        });

        button_cancel.setBackground(new java.awt.Color(153, 255, 204));
        button_cancel.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_cancel.setText("Cancel");
        button_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_cancelActionPerformed(evt);
            }
        });

        label_forgot_password.setBackground(new java.awt.Color(255, 255, 255));
        label_forgot_password.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_forgot_password.setText("Forgot password?");
        label_forgot_password.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                label_forgot_passwordMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                label_forgot_passwordMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                label_forgot_passwordMouseExited(evt);
            }
        });

        check_password.setBackground(new java.awt.Color(255, 255, 255));
        check_password.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        check_password.setText("See Password");
        check_password.setFocusable(false);
        check_password.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                check_passwordActionPerformed(evt);
            }
        });

        label_version.setBackground(new java.awt.Color(255, 255, 255));
        label_version.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        label_version.setForeground(new java.awt.Color(153, 153, 153));
        label_version.setText("2.2.409");

        label1.setBackground(new java.awt.Color(255, 255, 255));
        label1.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        label1.setForeground(new java.awt.Color(153, 153, 153));
        label1.setText("Ver. ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_password, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txt_username)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(user_image, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(label_forgot_password)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_cancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_login))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lock_image, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(check_password))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(waleta_image, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(label1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_version)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(waleta_image, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(user_image, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_username, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lock_image, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(check_password, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_password, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_login)
                    .addComponent(button_cancel)
                    .addComponent(label_forgot_password))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_version, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label1))
                .addGap(5, 5, 5))
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
        System.exit(0);
    }//GEN-LAST:event_button_cancelActionPerformed

    private void label_forgot_passwordMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_forgot_passwordMouseEntered
        ori = evt.getComponent().getFont();
        Map attributes = ori.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        evt.getComponent().setFont(ori.deriveFont(attributes));

    }//GEN-LAST:event_label_forgot_passwordMouseEntered

    private void label_forgot_passwordMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_forgot_passwordMouseExited
        evt.getComponent().setFont(ori);
    }//GEN-LAST:event_label_forgot_passwordMouseExited

    private void label_forgot_passwordMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_forgot_passwordMouseClicked
        JOptionPane.showMessageDialog(this, "This Function is Under Construction!");
    }//GEN-LAST:event_label_forgot_passwordMouseClicked

    private void button_loginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_loginActionPerformed
        Submit();
    }//GEN-LAST:event_button_loginActionPerformed

    private void txt_passwordKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_passwordKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            Submit();
        }
    }//GEN-LAST:event_txt_passwordKeyPressed

    private void txt_usernameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_usernameKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            Submit();
        }
    }//GEN-LAST:event_txt_usernameKeyPressed

    private void check_passwordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_check_passwordActionPerformed
        if (check_password.isSelected()) {
            txt_password.setEchoChar((char) 0);
        } else {
            txt_password.setEchoChar('\u25cf');
        }
    }//GEN-LAST:event_check_passwordActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Login().setVisible(true);
            }
        });

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton button_cancel;
    private javax.swing.JButton button_login;
    private javax.swing.JCheckBox check_password;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel label1;
    private javax.swing.JLabel label_forgot_password;
    private javax.swing.JLabel label_version;
    private javax.swing.JLabel lock_image;
    private javax.swing.JPasswordField txt_password;
    private javax.swing.JTextField txt_username;
    private javax.swing.JLabel user_image;
    private javax.swing.JLabel waleta_image;
    // End of variables declaration//GEN-END:variables
}
