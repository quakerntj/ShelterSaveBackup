package com.ntj.sheltersavebackup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;

public class Decrypter {
    Cipher dcipher;
    public static final String CMD_ENC = "enc";
    public static final String CMD_DEC = "dec";

    public static final String HEX_KEY = "A7CA9F3366D892C2F0BEF417341CA971B69AE9F7BACCCFFCF43C62D1D7D021F9";
    public static final String HEX_IV = "7475383967656a693334307438397532";

    private SecretKey mKey;
    private byte [] mIV;

    protected static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public Decrypter(byte [] keyBytes, byte [] ivBytes) throws Exception {
        mKey = new SecretKeySpec(keyBytes, "AES");
        mIV = ivBytes;

        dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    }

    public String decrypt(File in, File out) throws Exception {
        InputStream cis = null;
        OutputStream os = null;
        dcipher.init(Cipher.DECRYPT_MODE, mKey, new IvParameterSpec(mIV));
        StringBuilder sb = new StringBuilder(50000);
        final String charset = "UTF-8";
        
        try {
            cis = new CipherInputStream(new Base64InputStream(new FileInputStream(in), Base64.DEFAULT), dcipher);
            os = new FileOutputStream(out);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
        try {
            final byte[] buffer = new byte[8192];
            int read = cis.read(buffer);

            while (read > -1) {
                os.write(buffer, 0, read);
                sb.append(new String(buffer, 0, read, charset));
                read = cis.read(buffer);
            }

            cis.close();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return sb.toString();
    }

    public void encrypt(File in, File out) throws Exception {
        InputStream is = null;
        OutputStream cos = null;
        dcipher.init(Cipher.ENCRYPT_MODE, mKey, new IvParameterSpec(mIV));

        try {
            is = new FileInputStream(in);
            cos = new CipherOutputStream(new Base64OutputStream(new FileOutputStream(out), Base64.DEFAULT), dcipher);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        try {
            final byte[] buffer = new byte[8192];
            int read = is.read(buffer, 0, 8192);

            while (read > -1) {
                cos.write(buffer, 0, read);
                read = is.read(buffer, 0, 8192);
            }

            is.close();
            cos.close();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public static void usage() {
        System.out.println("Decrypter <[enc|dec]> <SourceFile> <DestinationFile>");
    }

    public static void main(String args[]) throws Exception {
        if (args.length != 0) {
            Decrypter decrypter = new Decrypter(new byte [] {}, new byte [] {});
            if (CMD_ENC.equals(args[0])) {
                if (args.length != 3) {
                    usage();
                    return;
                }
                File src = new File(args[1]);
                if (!src.exists()) {
                    usage();
                    return;
                }
                File dst = new File(args[2]);
//                if (dst.exists()) {
//                    System.out.println("Overwrite file \"" + dst.getName() + "\" [y|N]?");
//                    BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
//	                String s = bufferRead.readLine();
//	                if (!(s.equals("Y") || s.equals("y")))
//	                    return;
//                }
                decrypter.encrypt(src, dst);
            } else if (CMD_DEC.equals(args[0])) {
                if (args.length != 3) {
                    usage();
                    return;
                }
                File src = new File(args[1]);
                if (!src.exists()) {
                    usage();
                    return;
                }
                File dst = new File(args[2]);
//                if (dst.exists()) {
//                    System.out.println("Overwrite file \"" + dst.getName() + "\" [y|N]?");
//                    BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
//	                String s = bufferRead.readLine();
//	                if (!(s.equals("Y") || s.equals("y")))
//	                    return;
//                }
                decrypter.decrypt(src, dst);
            } else {
                return;
            }
        }
    }
}