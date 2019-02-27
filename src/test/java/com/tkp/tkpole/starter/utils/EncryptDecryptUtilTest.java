package com.tkp.tkpole.starter.utils;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.tkp.tkpole.starter.utils.misc.cryptogram.Ecc;
import com.tkp.tkpole.starter.utils.misc.cryptogram.Rsa;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Slf4j
public class EncryptDecryptUtilTest {

    private static final String SRC = "秋水共长天一色,落霞与孤鹜齐飞";
    private static final byte[] SRC_BYTES = StringUtils.getBytesUtf8(SRC);

    @Test @SneakyThrows
    public void test() {
        // 获取秘钥
        SecretKey secretKey = EncryptDecryptUtil.getSecretKey(EncryptDecryptUtil.ALGORITHMS_AES, 128);
        // 加密
        log.info("加密前: {}",Base64.encode(SRC_BYTES));
        byte[] tar = EncryptDecryptUtil.encryptOrDencrypt( SRC_BYTES, "AES/CBC/PKCS5Padding", secretKey, Cipher.ENCRYPT_MODE);
        log.info("加密后: {}",Base64.encode(tar));
        // 解密
        byte[] src = EncryptDecryptUtil.encryptOrDencrypt( tar, "AES/CBC/PKCS5Padding", secretKey, Cipher.DECRYPT_MODE);
        log.info("解密后: {}",Base64.encode(src));
        assertEquals(SRC, StringUtils.newStringUtf8(src));
    }

    @Test
    public void test2() {
        Rsa rsa = new Rsa();
        // 加密
        log.info("加密前: {}",Base64.encode(SRC_BYTES));
        byte[] tar = rsa.encryptOrDencrypt(SRC_BYTES, Cipher.ENCRYPT_MODE);
        log.info("加密后: {}",Base64.encode(tar));
        // 解密
        byte[] src = rsa.encryptOrDencrypt(tar, Cipher.DECRYPT_MODE);
        log.info("解密后: {}",Base64.encode(src));
        assertEquals(SRC, StringUtils.newStringUtf8(src));
    }

    @Test
    public void test21() {
        Rsa rsa = new Rsa();
        Rsa rsa2 = new Rsa();
        String pk = rsa.getEncodedPrivateKey();
        byte[] sign = EncryptDecryptUtil.RsaUtil.signature(SRC_BYTES, rsa.getRsaPrivateKey());
        byte[] sign2 = EncryptDecryptUtil.RsaUtil.signature(SRC_BYTES, rsa2.getRsaPrivateKey());
        assertTrue(EncryptDecryptUtil.RsaUtil.verify(SRC_BYTES,sign2,rsa.getRsaPublicKey()));
    }

    @Test
    public void test3() {
        Ecc ecc = new Ecc();
        // 加密
        log.info("加密前: {}",Base64.encode(SRC_BYTES));
        byte[] tar = ecc.encryptOrDencrypt(SRC_BYTES, Cipher.ENCRYPT_MODE);
        log.info("加密后: {}",Base64.encode(tar));
        // 解密
        byte[] src = ecc.encryptOrDencrypt(tar, Cipher.DECRYPT_MODE);
        log.info("解密后: {}",Base64.encode(src));
        assertEquals(SRC, StringUtils.newStringUtf8(src));
    }

    @Test
    public void test4() {
        Ecc ecc = new Ecc(521);
        String pk = ecc.getEncodedPrivateKey();
        byte[] sign = EncryptDecryptUtil.EccUtil.signature(SRC_BYTES, ecc.getEcPrivateKey());
        assertTrue(EncryptDecryptUtil.EccUtil.verify(SRC_BYTES,sign,ecc.getEcPublicKey()));
    }
}