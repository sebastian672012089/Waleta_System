package waleta_system.Class;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class Utility {

    public static DBConnect db = new DBConnect();
    public static DBConnect_sub db_sub = new DBConnect_sub();
    public static DBConnect_maklun db_maklun = new DBConnect_maklun();
    public static DBConnect_cabuto db_cabuto = new DBConnect_cabuto();
    public static final int ROTATE_LEFT = 1;
    public static final int ROTATE_RIGHT = -1;
    public static int ROTATE = 1;

    public static DecimalFormat DecimalFormatUS(DecimalFormat decimalFormat) {
        Locale currentLocale = Locale.getDefault();

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(currentLocale);
        otherSymbols.setDecimalSeparator('.');
//        otherSymbols.setGroupingSeparator(' ');

//        Locale currentLocale = Locale.US;
//        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(currentLocale);
        decimalFormat.setDecimalFormatSymbols(otherSymbols);
        return decimalFormat;
    }

    public static ImageIcon ResizeImageIcon(ImageIcon MyImage, int widthTo, int heightTo) {
        int imgHeight = MyImage.getIconHeight();
        int imgWidth = MyImage.getIconWidth();
        Image img = MyImage.getImage();
        if (imgHeight > heightTo) {
            imgHeight = heightTo;
        }
        if (imgWidth > widthTo) {
            imgWidth = widthTo;
        }

        Image newImg = img.getScaledInstance(imgWidth - 2, imgHeight - 2, Image.SCALE_SMOOTH);
        ImageIcon image = new ImageIcon(newImg);
        return image;
    }

    public static Date addDaysSkippingSundays(Date date, int days) {
        Date result_date = date;
        int addedDays = 0;
        while (addedDays < days) {
            result_date = new Date(result_date.getTime() + (1000 * 60 * 60 * 24));
            if (!new SimpleDateFormat("EEEE", Locale.ENGLISH).format(result_date).equals("Sunday")) {
                ++addedDays;
            }
        }
        return result_date;
    }

    public static Date addDaysSkippingFreeDays(Date date, int days) throws Exception {
        Date result_date = date;
        int addedDays = 0;
        while (addedDays < days) {
            result_date = new Date(result_date.getTime() + (1000 * 60 * 60 * 24));
            String query = "SELECT `tanggal_libur` FROM `tb_libur` WHERE `tanggal_libur` = '" + new SimpleDateFormat("yyyy-MM-dd").format(result_date) + "'";
            ResultSet rs = db.getStatement().executeQuery(query);
            if (!rs.next() && !new SimpleDateFormat("EEEE", Locale.ENGLISH).format(result_date).equals("Sunday")) {
                ++addedDays;
            }
        }
        return result_date;
    }

    public static Date addDays(Date date, int days) {
        Date result = date;
        int addedDays = 0;
        while (addedDays < days) {
            result = new Date(result.getTime() + (1000 * 60 * 60 * 24));
            ++addedDays;
        }
        return result;
    }

    public static LocalDate addDaysSkippingWeekends(LocalDate date, int days) {
        LocalDate result = date;
        int addedDays = 0;
        while (addedDays < days) {
            result = result.plusDays(1);
            if (result.getDayOfWeek() != DayOfWeek.SUNDAY) {
                ++addedDays;
            }
        }
        return result;
    }

    public static int countDaysWithoutFreeDays(Date date1, Date date2) {
        long diff, jumlah_hari = 0;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            int jumlah_libur = 0;
            String sql = "SELECT COUNT(`tanggal_libur`) AS 'hari_libur'"
                    + "FROM `tb_libur` "
                    + "WHERE `tanggal_libur` BETWEEN '" + dateFormat.format(date1) + "' AND '" + dateFormat.format(date2) + "'"
                    + "AND DAYNAME(`tanggal_libur`) <> 'Sunday'";
            ResultSet rst = db.getStatement().executeQuery(sql);
            if (rst.next()) {
                jumlah_libur = rst.getInt("hari_libur");
            }
            diff = date2.getTime() - date1.getTime();
            jumlah_hari = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
//            System.out.println(jumlah_hari);
            int jumlah_minggu = 0;
            for (int i = 0; i < jumlah_hari; i++) {
                if (new SimpleDateFormat("EEE").format(date1).equals("Sun")) {
                    jumlah_minggu++;
                }
                date1 = new Date(date1.getTime() + (1 * 24 * 60 * 60 * 1000));
            }
            jumlah_hari = jumlah_hari - (jumlah_libur + jumlah_minggu);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
            Logger.getLogger(Utility.class.getName()).log(Level.SEVERE, null, ex);
        }
        return (int) jumlah_hari;
    }

    public static boolean ApakahIniRuangSub(String ruang) throws Exception {
        Utility.db_sub.connect();
        String query = "SELECT `kode_sub` FROM `tb_sub_waleta` WHERE `kode_sub` = '" + ruang + "'";
        ResultSet rs = Utility.db_sub.getStatement().executeQuery(query);
        return rs.next();
    }

    public static ImageIcon ResizeImage(String ImagePath, byte[] pic, int widthTo, int heightTo) {
        if (ImagePath != null || pic != null) {
            ImageIcon MyImage = null;
            if (ImagePath != null) {
                MyImage = new ImageIcon(ImagePath);
            } else if (pic != null) {
                MyImage = new ImageIcon(pic);
            }
            int imgHeight = MyImage.getIconHeight();
            int imgWidth = MyImage.getIconWidth();
            Image img = MyImage.getImage();
            if (imgWidth > widthTo) {
                imgWidth = widthTo - 1;
                imgHeight = (MyImage.getIconHeight() / MyImage.getIconWidth()) * widthTo;
                if (imgHeight > heightTo) {
                    imgHeight = heightTo - 1;
                    imgWidth = (MyImage.getIconWidth() / MyImage.getIconHeight()) * heightTo;
                }
            } else if (imgHeight > heightTo) {
                imgHeight = heightTo - 1;
                imgWidth = (MyImage.getIconWidth() / MyImage.getIconHeight()) * heightTo;
                if (imgWidth > widthTo) {
                    imgWidth = widthTo - 1;
                    imgHeight = (MyImage.getIconHeight() / MyImage.getIconWidth()) * widthTo;
                }
            }
            Image newImg = img.getScaledInstance(imgWidth - 2, imgHeight - 2, Image.SCALE_SMOOTH);
            MyImage = new ImageIcon(newImg);
            return MyImage;
        } else {
            return null;
        }
    }

    public static void rotate90(File input, File output, int direction) {
        try {
            ImageInputStream iis = ImageIO.createImageInputStream(input);
            Iterator<ImageReader> iterator = ImageIO.getImageReaders(iis);
            ImageReader reader = iterator.next();
            String format = reader.getFormatName();

            BufferedImage image = ImageIO.read(iis);
            int width = image.getWidth();
            int height = image.getHeight();

            BufferedImage rotated = new BufferedImage(height, width, image.getType());
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    switch (direction) {
                        case ROTATE_LEFT:
                            rotated.setRGB(y, (width - 1) - x, image.getRGB(x, y));
                            break;
                        case ROTATE_RIGHT:
                            rotated.setRGB((height - 1) - y, x, image.getRGB(x, y));
                            break;
                    }
                }
            }
            ImageIO.write(rotated, format, output);
        } catch (Exception e) {
            Logger.getLogger(Utility.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public static void createImage(File input, File output) {
        try {
            ImageInputStream iis = ImageIO.createImageInputStream(input);
            Iterator<ImageReader> iterator = ImageIO.getImageReaders(iis);
            ImageReader reader = iterator.next();
            String format = reader.getFormatName();

            BufferedImage image = ImageIO.read(iis);
            int width = image.getWidth();
            int height = image.getHeight();

            BufferedImage rotated = new BufferedImage(width, height, image.getType());
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    rotated.setRGB(x, y, image.getRGB(x, y));
                }
            }
            ImageIO.write(rotated, format, output);
        } catch (Exception e) {
            Logger.getLogger(Utility.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public static BufferedImage imageIconToBufferedImage(ImageIcon icon) {
        BufferedImage bufferedImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(),
                BufferedImage.TYPE_BYTE_GRAY);
        Graphics graphics = bufferedImage.createGraphics();
        icon.paintIcon(null, graphics, 0, 0);
        graphics.dispose();//from   w  ww.j a  va  2  s.  co m
        return bufferedImage;
    }

    public static String bytesToString(final byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (byte bt : bytes) {
            sb.append(String.format("%02x", bt));
        }
        return sb.toString();
    }

    public static String ASCIItoHEX(String text) {
        // Initialize final String
        String hex = "";

        // Make a loop to iterate through
        // every character of ascii string
        for (int i = 0; i < text.length(); i++) {

            // take a char from
            // position i of string
            char ch = text.charAt(i);

            // cast char to integer and
            // find its ascii value
            int in = (int) ch;

            // change this ascii value
            // integer to hexadecimal value
            String part = Integer.toHexString(in);

            // add this hexadecimal value
            // to final string.
            hex += part;
        }

        // return the final string hex
        return hex;
    }

    public static String ubah_ke_INDONESIA(String EnglishDayName) {
        String IndonesianDayName = "";

        switch (EnglishDayName.toLowerCase()) {
            case "sunday":
                IndonesianDayName = "Minggu";
                break;
            case "monday":
                IndonesianDayName = "Senin";
                break;
            case "tuesday":
                IndonesianDayName = "Selasa";
                break;
            case "wednesday":
                IndonesianDayName = "Rabu";
                break;
            case "thursday":
                IndonesianDayName = "Kamis";
                break;
            case "friday":
                IndonesianDayName = "Jumat";
                break;
            case "saturday":
                IndonesianDayName = "Sabtu";
                break;
            default:
                IndonesianDayName = "Hari tidak valid"; // Hari tidak dikenali
        }

        return IndonesianDayName;
    }

    public static List<Component> getAllComponents(final Container c) {
        Component[] comps = c.getComponents();
        List<Component> compList = new ArrayList<Component>();
        for (Component comp : comps) {
            compList.add(comp);
            if (comp instanceof Container) {
                compList.addAll(getAllComponents((Container) comp));
            }
        }
        return compList;
    }

    public static void main(String[] args) {
//        File input = new File("\\\\192.168.10.2\\Shared Folder\\DOKUMEN SISTEM\\TEST.jpg");
//        File outputfile = new File("\\\\192.168.10.2\\Shared Folder\\DOKUMEN SISTEM\\TEST2.jpg");
//        createImage(input, outputfile);
    }
}
