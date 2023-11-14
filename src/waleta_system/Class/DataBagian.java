package waleta_system.Class;

import java.util.ArrayList;
import java.util.List;

public class DataBagian {
    
    private int kodeBagian;
    private String namaBagian;
    private String posisiBagian;
    private String kodeDepartemen;
    private String divisiBagian;
    private String bagianBagian;
    private String ruangBagian;
    private String grup;
    private int statusBagian;
    private int kepalaBagian;

    public DataBagian(int kodeBagian, String namaBagian, String posisiBagian, String kodeDepartemen, String divisiBagian, String bagianBagian, String ruangBagian, String grup, int statusBagian, int kepalaBagian) {
        this.kodeBagian = kodeBagian;
        this.namaBagian = namaBagian;
        this.posisiBagian = posisiBagian;
        this.kodeDepartemen = kodeDepartemen;
        this.divisiBagian = divisiBagian;
        this.bagianBagian = bagianBagian;
        this.ruangBagian = ruangBagian;
        this.grup = grup;
        this.statusBagian = statusBagian;
        this.kepalaBagian = kepalaBagian;
    }
    
    public static List<Integer> getSubordinates(List<DataBagian> data, int kodeBagian) {
        List<Integer> subordinates = new ArrayList<>();
        for (DataBagian bagian : data) {
            if (bagian.getKepalaBagian() == kodeBagian) {
                subordinates.add(bagian.getKodeBagian());
                subordinates.addAll(getSubordinates(data, bagian.getKodeBagian()));
            }
        }
        return subordinates;
    }

    public int getKodeBagian() {
        return kodeBagian;
    }

    public void setKodeBagian(int kodeBagian) {
        this.kodeBagian = kodeBagian;
    }

    public String getNamaBagian() {
        return namaBagian;
    }

    public void setNamaBagian(String namaBagian) {
        this.namaBagian = namaBagian;
    }

    public String getPosisiBagian() {
        return posisiBagian;
    }

    public void setPosisiBagian(String posisiBagian) {
        this.posisiBagian = posisiBagian;
    }

    public String getKodeDepartemen() {
        return kodeDepartemen;
    }

    public void setKodeDepartemen(String kodeDepartemen) {
        this.kodeDepartemen = kodeDepartemen;
    }

    public String getDivisiBagian() {
        return divisiBagian;
    }

    public void setDivisiBagian(String divisiBagian) {
        this.divisiBagian = divisiBagian;
    }

    public String getBagianBagian() {
        return bagianBagian;
    }

    public void setBagianBagian(String bagianBagian) {
        this.bagianBagian = bagianBagian;
    }

    public String getRuangBagian() {
        return ruangBagian;
    }

    public void setRuangBagian(String ruangBagian) {
        this.ruangBagian = ruangBagian;
    }

    public String getGrup() {
        return grup;
    }

    public void setGrup(String grup) {
        this.grup = grup;
    }

    public int getStatusBagian() {
        return statusBagian;
    }

    public void setStatusBagian(int statusBagian) {
        this.statusBagian = statusBagian;
    }

    public int getKepalaBagian() {
        return kepalaBagian;
    }

    public void setKepalaBagian(int kepalaBagian) {
        this.kepalaBagian = kepalaBagian;
    }
}