package tr.edu.metu.ceng.absa.aspectextraction.util;

public class StringUtil {
    private static StringUtil instance = null;


    public static StringUtil getInstance() {
        if(instance == null) {
            instance = new StringUtil();
        }
        return instance;
    }


    public String shorten(final String str, final int maxChar) {
        return shorten(str, maxChar, "...");
    }

    public String shorten(final String str, final int maxChar, final String delimiter) {
        if (str == null) {
            return null;
        }
        if (str.length() <= maxChar) {
            return str;
        }
        return str.substring(0, maxChar - delimiter.length()) + delimiter;
    }
}
