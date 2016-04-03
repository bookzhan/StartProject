package com.zhanlibrary.utils;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * Created by zhandalin on 2015-12-15 10:43.
 * 说明:对称加密
 */
public class AESHelper {
    private static AESHelper aesHelper = null;
    private Cipher cipher = null;
    private SecretKeySpec key = null;
    private AlgorithmParameterSpec spec;
    public static final String SEED_16_CHARACTER = "ghs&^*M2^$H)5K$#";


    public static AESHelper getInstance() {
        if (null == aesHelper) {
            synchronized (AESHelper.class) {
                if (null == aesHelper) {
                    aesHelper = new AESHelper();
                }
            }
        }
        return aesHelper;
    }

    private AESHelper() {
        key = getKey();
        spec = getIV();
        cipher = getCipher();
    }

    public AlgorithmParameterSpec getIV() {
        byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,};
        IvParameterSpec ivParameterSpec;
        ivParameterSpec = new IvParameterSpec(iv);
        return ivParameterSpec;
    }

    /**
     * 加密
     *
     * @param plainText
     * @return
     * @throws Exception
     */
    public String encrypt(String plainText) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);
        byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));
        return new String(Base64.encode(encrypted,
                Base64.DEFAULT), "UTF-8");
    }

    /**
     * 解密
     *
     * @param cryptedText
     * @return
     * @throws Exception
     */
    public String decrypt(String cryptedText) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, key, spec);
        byte[] bytes = Base64.decode(cryptedText, Base64.DEFAULT);
        byte[] decrypted = cipher.doFinal(bytes);
        return new String(decrypted, "UTF-8");
    }

    private Cipher getCipher() {
        if (null == cipher)
            try {
                cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            } catch (Exception e) {
                e.printStackTrace();
            }
        return cipher;
    }

    private SecretKeySpec getKey() {
        if (null == key) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                digest.update(SEED_16_CHARACTER.getBytes("UTF-8"));
                byte[] keyBytes = new byte[32];
                System.arraycopy(digest.digest(), 0, keyBytes, 0, keyBytes.length);
                key = new SecretKeySpec(keyBytes, "AES");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return key;
    }

}
