package aisk.saltpass;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {
    
    public static String shaConvert(String s, String type) {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance(type);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();
            
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }

    public static String limitCut(String s, int count, int offset) {
        if (s.length() > (offset + count)) {
            return s.substring(offset, offset + count);
        }
        else if (s.length() > offset) {
            return s.substring(offset, s.length());
        }
        else {
            return("");
        }
    }

}
