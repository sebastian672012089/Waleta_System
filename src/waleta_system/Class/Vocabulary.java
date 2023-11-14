/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package waleta_system.Class;

import waleta_system.Class.Words.Language;

/**
 *
 * @author PT. Waleta new PC 1
 */
public class Vocabulary {
    
    public static final Words SIMPAN = new Words("Simpan", "Save");
    public static final Words BUKA = new Words("Buka", "Open");
    public static final Words TUTUP = new Words("Tutup", "Close");
    public static final Words MASUK = new Words("Masuk", "Log In");
    public static final Words NAMA_PENGGUNA = new Words("Nama Pengguna", "Username");
    public static final Words KATA_SANDI = new Words("Kata Sandi", "Password");
    public static final Words KELUAR = new Words("Keluar", "Log Out");
    public static final Words KELUAR_APLIKASI = new Words("Keluar Aplikasi", "Exit");
    
    public static Language languageConfig = Words.Language.ENGLISH;
    
    public static String getWord(Words words){
        switch (languageConfig) {
            case ENGLISH:
                return words.getEnglish();
            case INDONESIAN:
                return words.getIndonesia();
            default:
                return words.getIndonesia();
        }
    }
}
