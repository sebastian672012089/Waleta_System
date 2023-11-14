/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package waleta_system.Class;

/**
 *
 * @author PT. Waleta new PC 1
 */
public class Words {
    
    public enum Language{
        INDONESIAN,
        ENGLISH
    }

    public Words(String indonesia, String english) {
        this.indonesia = indonesia;
        this.english = english;
    }
    
    private String indonesia;
    private String english;

    public String getIndonesia() {
        return indonesia;
    }

    public void setIndonesia(String indonesia) {
        this.indonesia = indonesia;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }
}
