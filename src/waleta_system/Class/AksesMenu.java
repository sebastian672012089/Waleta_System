package waleta_system.Class;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;

public class AksesMenu {
    public static final String MENU_ITEM_ISU_PRODUKSI = "MENU_ITEM_ISU_PRODUKSI";
    public static final String MENU_ITEM_ABSEN_PRODUKSI = "MENU_ITEM_ABSEN_PRODUKSI";
    
    public static final String MENU_BAHAN_BAKU = "MENU_BAHAN_BAKU";
    public static final String MENU_ITEM_SUPPLIER = "MENU_ITEM_SUPPLIER";
    public static final String MENU_ITEM_CUSTOMER = "MENU_ITEM_CUSTOMER";
    public static final String MENU_ITEM_RUMAH_BURUNG = "MENU_ITEM_RUMAH_BURUNG";
    public static final String MENU_ITEM_GRADE_BAKU = "MENU_ITEM_GRADE_BAKU";
    public static final String MENU_ITEM_BAHAN_BAKU_MASUK = "MENU_ITEM_BAHAN_BAKU_MASUK";
    public static final String MENU_ITEM_LAPORAN_PRODUKSI = "MENU_ITEM_LAPORAN_PRODUKSI";
    public static final String MENU_ITEM_LAPORAN_PRODUKSI_SESEKAN = "MENU_ITEM_LAPORAN_PRODUKSI_SESEKAN";
    public static final String MENU_ITEM_LAPORAN_PRODUKSI_SAPON = "MENU_ITEM_LAPORAN_PRODUKSI_SAPON";
    public static final String MENU_ITEM_BAKU_KELUAR = "MENU_ITEM_BAKU_KELUAR";
    public static final String MENU_ITEM_REKAPITULASI = "MENU_ITEM_REKAPITULASI";
    public static final String MENU_ITEM_ADJUSTMENT_BAHAN_BAKU = "MENU_ITEM_ADJUSTMENT_BAHAN_BAKU";
    public static final String MENU_ITEM_PEMBELIAN_BAHAN_BAKU = "MENU_ITEM_PEMBELIAN_BAHAN_BAKU";
    public static final String MENU_ITEM_KARTU_CAMPURAN = "MENU_ITEM_KARTU_CAMPURAN";
    public static final String MENU_ITEM_BONUS_PANEN = "MENU_ITEM_BONUS_PANEN";
    public static final String MENU_ITEM_MASTER_BULU_UPAH = "MENU_ITEM_MASTER_BULU_UPAH";
    
    public static final String MENU_PRODUKSI = "MENU_PRODUKSI";
    public static final String MENU_ITEM_RENDAM = "MENU_ITEM_RENDAM";
    public static final String MENU_ITEM_CUCI = "MENU_ITEM_CUCI";
    public static final String MENU_ITEM_CABUT = "MENU_ITEM_CABUT";
    public static final String MENU_ITEM_CETAK = "MENU_ITEM_CETAK";
    public static final String MENU_ITEM_F2 = "MENU_ITEM_F2";
    public static final String MENU_ITEM_PROGRESS_LP = "MENU_ITEM_PROGRESS_LP";
    
    public static final String MENU_ATB = "MENU_ATB";
    public static final String MENU_ATB_ITEM_KARYAWAN_ATB = "MENU_ATB_ITEM_KARYAWAN_ATB";
    public static final String MENU_ATB_ITEM_PRODUKSI_ATB = "MENU_ATB_ITEM_PRODUKSI_ATB";
    
    public static final String MENU_MANAJEMEN_SUB = "MENU_MANAJEMEN_SUB";
    public static final String MENU_ITEM_DATA_SUB_WALETA = "MENU_ITEM_DATA_SUB_WALETA";
    public static final String MENU_ITEM_CABUT_SUB = "MENU_ITEM_CABUT_SUB";
    public static final String MENU_ITEM_DATA_KARYAWAN_SUB = "MENU_ITEM_DATA_KARYAWAN_SUB";
    public static final String MENU_ITEM_ABSEN_SUB = "MENU_ITEM_ABSEN_SUB";
    public static final String MENU_ITEM_PENGGAJIAN_SUB = "MENU_ITEM_PENGGAJIAN_SUB";
    public static final String MENU_ITEM_PIUTANG_KARYAWAN_SUB = "MENU_ITEM_PIUTANG_KARYAWAN_SUB";
    public static final String MENU_ITEM_TARIF_UPAH_BOBOT_LP_SUB = "MENU_ITEM_TARIF_UPAH_BOBOT_LP_SUB";
    
    public static final String MENU_BAHAN_JADI = "MENU_BAHAN_JADI";
    public static final String MENU_ITEM_BAHAN_JADI_MASUK = "MENU_ITEM_BAHAN_JADI_MASUK";
    public static final String MENU_ITEM_TUTUPAN_GRADING = "MENU_ITEM_TUTUPAN_GRADING";
    public static final String MENU_ITEM_DATA_BOX_BAHAN_JADI = "MENU_ITEM_DATA_BOX_BAHAN_JADI";
    public static final String MENU_ITEM_STOCK_BAHAN_JADI = "MENU_ITEM_STOCK_BAHAN_JADI";
    public static final String MENU_ITEM_MASTER_GRADE_BAHAN_JADI = "MENU_ITEM_MASTER_GRADE_BAHAN_JADI";
    public static final String MENU_ITEM_BONUS_GRADING = "MENU_ITEM_BONUS_GRADING";
    public static final String MENU_ITEM_LAPORAN_PRODUKSI_BAHAN_JADI = "MENU_ITEM_LAPORAN_PRODUKSI_BAHAN_JADI";
    public static final String MENU_ITEM_PEMBELIAN_BAHAN_JADI = "MENU_ITEM_PEMBELIAN_BAHAN_JADI";
    public static final String MENU_ITEM_PRICE_LIST_BJ = "MENU_ITEM_PRICE_LIST_BJ";
    
    public static final String MENU_PACKING = "MENU_PACKING";
    public static final String MENU_ITEM_DATA_PACKING = "MENU_ITEM_DATA_PACKING";
    public static final String MENU_ITEM_DATA_PENGIRIMAN = "MENU_ITEM_DATA_PENGIRIMAN";
    public static final String MENU_ITEM_LIST_BUYER = "MENU_ITEM_LIST_BUYER";
    public static final String MENU_ITEM_DATA_SPK = "MENU_ITEM_DATA_SPK";
    public static final String MENU_ITEM_DATA_PAYMENT_REPORT = "MENU_ITEM_DATA_PAYMENT_REPORT";
    
    public static final String MENU_QC = "MENU_QC";
    public static final String MENU_ITEM_LAB_BAHAN_BAKU = "MENU_ITEM_LAB_BAHAN_BAKU";
    public static final String MENU_ITEM_LAB_LP = "MENU_ITEM_LAB_LP";
    public static final String MENU_ITEM_TREATMENT_LP = "MENU_ITEM_TREATMENT_LP";
    public static final String MENU_ITEM_PEMANASAN_BAHAN_JADI = "MENU_ITEM_PEMANASAN_BAHAN_JADI";
    
    public static final String MENU_HRD = "MENU_HRD";
    public static final String MENU_ITEM_DATA_KARYAWAN = "MENU_ITEM_DATA_KARYAWAN";
    public static final String MENU_ITEM_DATA_IJIN_KELUAR = "MENU_ITEM_DATA_IJIN_KELUAR";
    public static final String MENU_ITEM_DATA_IJIN_ABSEN_CUTI = "MENU_ITEM_DATA_IJIN_ABSEN_CUTI";
    public static final String MENU_ITEM_DATA_LEMBUR = "MENU_ITEM_DATA_LEMBUR";
    public static final String MENU_ITEM_DATA_PINDAH_BAGIAN = "MENU_ITEM_DATA_PINDAH_BAGIAN";
    public static final String MENU_ITEM_DEPT_BAG = "MENU_ITEM_DEPT_BAG";
    public static final String MENU_ITEM_TANGGAL_LIBUR = "MENU_ITEM_TANGGAL_LIBUR";
    public static final String MENU_ITEM_TBT = "MENU_ITEM_TBT";
    public static final String MENU_ITEM_ABSEN = "MENU_ITEM_ABSEN";
    public static final String MENU_ITEM_JAM_KERJA = "MENU_ITEM_JAM_KERJA";
    public static final String MENU_ITEM_JEMPUTAN = "MENU_ITEM_JEMPUTAN";
    
    public static final String MENU_KEUANGAN = "MENU_KEUANGAN";
    public static final String MENU_ITEM_BAHAN_BAKU = "MENU_ITEM_BAHAN_BAKU";
    public static final String MENU_ITEM_PENGGAJIAN_PRODUKSI = "MENU_ITEM_PENGGAJIAN_PRODUKSI";
    public static final String MENU_ITEM_BARANG_JADI = "MENU_ITEM_BARANG_JADI";
    public static final String MENU_ITEM_DATA_KARYAWAN_KEUANGAN = "MENU_ITEM_DATA_KARYAWAN_KEUANGAN";
    public static final String MENU_ITEM_PURCHASING = "MENU_ITEM_PURCHASING";
    public static final String MENU_ITEM_LEMBUR_STAFF = "MENU_ITEM_LEMBUR_STAFF";
    public static final String MENU_ITEM_DATA_PENGIRIMAN_KEUANGAN = "MENU_ITEM_DATA_PENGIRIMAN_KEUANGAN";
    public static final String MENU_ITEM_SALES_AND_PAYMENT_REPORT = "MENU_ITEM_SALES_AND_PAYMENT_REPORT";
    public static final String MENU_ITEM_DATA_BIAYA = "MENU_ITEM_DATA_BIAYA";
    public static final String MENU_ITEM_CASH_ON_BANK = "MENU_ITEM_CASH_ON_BANK";
    public static final String MENU_ITEM_NERACA = "MENU_ITEM_NERACA";
    public static final String MENU_ITEM_AR_AP_ESTA = "MENU_ITEM_AR_AP_ESTA";
    public static final String MENU_ITEM_LAPORAN_KEUANGAN = "MENU_ITEM_LAPORAN_KEUANGAN";
    
    public static final String MENU_MANAJEMEN = "MENU_MANAJEMEN";
    
    public static final String MENU_ITEM_DATA_USER = "MENU_ITEM_DATA_USER";
    public static final String MENU_ITEM_CREATE_NEW_USER = "MENU_ITEM_CREATE_NEW_USER";
    
    public static List<Akses> getNewAksesMenu(){
        List<Akses> dataMenu = new ArrayList<>();
        //MAIN MENU
        dataMenu.add(new Akses(MENU_ITEM_ISU_PRODUKSI));
        dataMenu.add(new Akses(MENU_ITEM_ABSEN_PRODUKSI));
        //BAGIAN BAHAN BAKU 11
        dataMenu.add(new Akses(MENU_BAHAN_BAKU));
        dataMenu.add(new Akses(MENU_ITEM_SUPPLIER));
        dataMenu.add(new Akses(MENU_ITEM_CUSTOMER));
        dataMenu.add(new Akses(MENU_ITEM_RUMAH_BURUNG));
        dataMenu.add(new Akses(MENU_ITEM_GRADE_BAKU));
        dataMenu.add(new Akses(MENU_ITEM_BAHAN_BAKU_MASUK));
        dataMenu.add(new Akses(MENU_ITEM_LAPORAN_PRODUKSI));
        dataMenu.add(new Akses(MENU_ITEM_LAPORAN_PRODUKSI_SESEKAN));
        dataMenu.add(new Akses(MENU_ITEM_LAPORAN_PRODUKSI_SAPON));
        dataMenu.add(new Akses(MENU_ITEM_BAKU_KELUAR));
        dataMenu.add(new Akses(MENU_ITEM_REKAPITULASI));
        dataMenu.add(new Akses(MENU_ITEM_ADJUSTMENT_BAHAN_BAKU));
        dataMenu.add(new Akses(MENU_ITEM_KARTU_CAMPURAN));
        dataMenu.add(new Akses(MENU_ITEM_BONUS_PANEN));
        
        //PRODUKSI 7
        dataMenu.add(new Akses(MENU_PRODUKSI));
        dataMenu.add(new Akses(MENU_ITEM_RENDAM));
        dataMenu.add(new Akses(MENU_ITEM_CUCI));
        dataMenu.add(new Akses(MENU_ITEM_CABUT));
        dataMenu.add(new Akses(MENU_ITEM_CETAK));
        dataMenu.add(new Akses(MENU_ITEM_F2));
        dataMenu.add(new Akses(MENU_ITEM_PROGRESS_LP));
        
        //MANAJEMEN SUB
        dataMenu.add(new Akses(MENU_MANAJEMEN_SUB));
        dataMenu.add(new Akses(MENU_ITEM_DATA_SUB_WALETA));
        dataMenu.add(new Akses(MENU_ITEM_CABUT_SUB));
        dataMenu.add(new Akses(MENU_ITEM_DATA_KARYAWAN_SUB));
        dataMenu.add(new Akses(MENU_ITEM_ABSEN_SUB));
        dataMenu.add(new Akses(MENU_ITEM_PENGGAJIAN_SUB));
        
        //BAGIAN BAHAN JADI 6
        dataMenu.add(new Akses(MENU_BAHAN_JADI));
        dataMenu.add(new Akses(MENU_ITEM_BAHAN_JADI_MASUK));
        dataMenu.add(new Akses(MENU_ITEM_TUTUPAN_GRADING));
        dataMenu.add(new Akses(MENU_ITEM_DATA_BOX_BAHAN_JADI));
        dataMenu.add(new Akses(MENU_ITEM_STOCK_BAHAN_JADI));
        dataMenu.add(new Akses(MENU_ITEM_MASTER_GRADE_BAHAN_JADI));
        dataMenu.add(new Akses(MENU_ITEM_BONUS_GRADING));
        dataMenu.add(new Akses(MENU_ITEM_LAPORAN_PRODUKSI_BAHAN_JADI));
        dataMenu.add(new Akses(MENU_ITEM_PEMBELIAN_BAHAN_JADI));
        dataMenu.add(new Akses(MENU_ITEM_PRICE_LIST_BJ));
        
        //MENU_PACKING 5
        dataMenu.add(new Akses(MENU_PACKING));
        dataMenu.add(new Akses(MENU_ITEM_DATA_PACKING));
        dataMenu.add(new Akses(MENU_ITEM_DATA_PENGIRIMAN));
        dataMenu.add(new Akses(MENU_ITEM_LIST_BUYER));
        dataMenu.add(new Akses(MENU_ITEM_DATA_SPK));
        dataMenu.add(new Akses(MENU_ITEM_DATA_PAYMENT_REPORT));
        //MENU_QC 5
        dataMenu.add(new Akses(MENU_QC));
        dataMenu.add(new Akses(MENU_ITEM_LAB_BAHAN_BAKU));
        dataMenu.add(new Akses(MENU_ITEM_LAB_LP));
        dataMenu.add(new Akses(MENU_ITEM_TREATMENT_LP));
        dataMenu.add(new Akses(MENU_ITEM_PEMANASAN_BAHAN_JADI));
        //MENU_HRD 7
        dataMenu.add(new Akses(MENU_HRD));
        dataMenu.add(new Akses(MENU_ITEM_DATA_KARYAWAN));
        dataMenu.add(new Akses(MENU_ITEM_DATA_IJIN_KELUAR));
        dataMenu.add(new Akses(MENU_ITEM_DATA_IJIN_ABSEN_CUTI));
        dataMenu.add(new Akses(MENU_ITEM_DATA_LEMBUR));
        dataMenu.add(new Akses(MENU_ITEM_DATA_PINDAH_BAGIAN));
        dataMenu.add(new Akses(MENU_ITEM_DEPT_BAG));
        dataMenu.add(new Akses(MENU_ITEM_TANGGAL_LIBUR));
        dataMenu.add(new Akses(MENU_ITEM_TBT));
        dataMenu.add(new Akses(MENU_ITEM_ABSEN));
        dataMenu.add(new Akses(MENU_ITEM_JAM_KERJA));
        dataMenu.add(new Akses(MENU_ITEM_JEMPUTAN));
        //MENU_KEUANGAN 2 
        dataMenu.add(new Akses(MENU_KEUANGAN));
        dataMenu.add(new Akses(MENU_ITEM_BAHAN_BAKU));
        dataMenu.add(new Akses(MENU_ITEM_PENGGAJIAN_PRODUKSI));
        dataMenu.add(new Akses(MENU_ITEM_BARANG_JADI));
        dataMenu.add(new Akses(MENU_ITEM_DATA_KARYAWAN_KEUANGAN));
        dataMenu.add(new Akses(MENU_ITEM_PURCHASING));
        dataMenu.add(new Akses(MENU_ITEM_LEMBUR_STAFF));
        dataMenu.add(new Akses(MENU_ITEM_DATA_PENGIRIMAN_KEUANGAN));
        dataMenu.add(new Akses(MENU_ITEM_SALES_AND_PAYMENT_REPORT));
        dataMenu.add(new Akses(MENU_ITEM_DATA_BIAYA));
        dataMenu.add(new Akses(MENU_ITEM_CASH_ON_BANK));
        dataMenu.add(new Akses(MENU_ITEM_NERACA));
        dataMenu.add(new Akses(MENU_ITEM_AR_AP_ESTA));
        dataMenu.add(new Akses(MENU_ITEM_LAPORAN_KEUANGAN));
        //MENU_MANAJEMEN
        dataMenu.add(new Akses(MENU_MANAJEMEN));
        //USER 2
        dataMenu.add(new Akses(MENU_ITEM_DATA_USER));
        dataMenu.add(new Akses(MENU_ITEM_CREATE_NEW_USER));
        return dataMenu;
    }
    
    public static int searchMenuByName(List<Akses> dataMenu, String nama){
        for (int i = 0; i < dataMenu.size(); i++) {
            if(dataMenu.get(i).nama.equalsIgnoreCase(nama)){
                return i;
            }
        }
        return -1;
    }
    
    public static class Akses{
        public String nama;
        public String akses;
        public Akses(String nama){
            this.nama = nama;
            this.akses = "0000";
        }
        public Akses(String nama,String akses){
            this.nama = nama;
            this.akses = akses;
        }
    }
    
    
    /*public static int searchComboByName(List<ComboMenu> dataMenu, String nama){
        for (int i = 0; i < dataMenu.size(); i++) {
            if(dataMenu.get(i).nama.equalsIgnoreCase(nama)){
                return i;
            }
        }
        return -1;
    }*/
    
    public static class ComboMenu{
        public String nama;
        public JCheckBox[] data;
        public ComboMenu(String nama, JCheckBox[] data){
            this.nama = nama;
            this.data = data;
        }
    }
}
