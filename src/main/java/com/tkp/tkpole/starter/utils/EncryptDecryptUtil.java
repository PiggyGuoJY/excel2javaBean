package com.tkp.tkpole.starter.utils;

import com.google.common.base.Charsets;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.axis.encoding.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import static com.tkp.tkpole.starter.utils.Assert.isNull;
import static com.tkp.tkpole.starter.utils.Assert.notNull;

/**
 * 加解密工具类
 *
 * <p>快速入门
 * <blockquote><pre>
 *  //对称加密
 *  //1. 获取秘钥
 *      SecretKey key = null;
 *     //直接使用随机秘钥
 *     //SecretKey key = getSecretKey("3DES", 112);//使用3DES算法生产长度为112bits的随机秘钥
 *     //使用存储在文件中的秘钥
 *     //saveSecrtKey("AES", 128, new File("sk.bin"));//使用AES算法生产长度128bits的秘钥并保存在sk.bin中
 *     //SecretKey key = getSecretKey("AES", new File("sk.bin"));//从文件sk.bin中按AES算法生成秘钥
 *     //使用明文秘钥
 *     String sk = "TKPOLEHealth_YTK";
 *     SecretKey key = getSecretKeyUseRaw("AES",sk);//按指定的内容生成AES秘钥
 * //2. 加密
 *      String src = "预祝接口对接任务圆满完成";//明文
 *      byte[] encrypt = encryptOrDencrypt(src.getBytes("UTF-8"), "AES/CTR/PKCS7Padding", key, Cipher.ENCRYPT_MODE);//对src按指定的模式和秘钥加密
 * //3. 解密
 *      byte[] dencrypt = encryptOrDencrypt(r,"AES/CTR/PKCS7Padding", key, Cipher.DECRYPT_MODE);
 *
 * </pre></blockquote>
 *
 * 这个等用到的时候再完善完善
 *
 * <p> 创建时间：2018/3/20
 *
 * @author guojy24
 * @version 1.0
 * */
@Slf4j @NoArgsConstructor( access = AccessLevel.PRIVATE)
public final class EncryptDecryptUtil {

    static { Security.addProvider( new BouncyCastleProvider()); }

    public static final String ALGORITHMS_DES = "DES";
    public static final String ALGORITHMS_3DES = "3DES";
    public static final String ALGORITHMS_DESEDE = "DESede";
    public static final String ALGORITHMS_AES = "AES";

    /**
     * 使用指定的对称加密算法和秘钥长度来生产随机秘钥
     *
     * @param algorithms 加密算法(DES, 3DES, AES)
     * @param secretKeyLength 秘钥长度( DES(56, 64), 3DES(112,168), AES(128, 192, 256, 或更大(可能需要获得无政策限制文件)))
     * @return 当一切正常时返回秘钥;当算法不支持时返回null
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     * @throws NoSuchProviderException NoSuchProviderException
     * @throws InvalidKeyException InvalidKeyException
     * @throws InvalidKeySpecException InvalidKeySpecException
     * */
    public static SecretKey getSecretKey(String algorithms, int secretKeyLength)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, InvalidKeySpecException {
        KeyGenerator keyGenerator = null;
        switch ( algorithms) {
            case EncryptDecryptUtil.ALGORITHMS_DES:
                keyGenerator = KeyGenerator.getInstance(ALGORITHMS_DES,"BC");
                keyGenerator.getProvider();
                keyGenerator.init(secretKeyLength);
                return SecretKeyFactory.getInstance(ALGORITHMS_DES).generateSecret(new DESKeySpec(keyGenerator.generateKey().getEncoded()));
            case EncryptDecryptUtil.ALGORITHMS_3DES:
                keyGenerator = KeyGenerator.getInstance(ALGORITHMS_DESEDE);
                keyGenerator.init(secretKeyLength);
                return SecretKeyFactory.getInstance(ALGORITHMS_DESEDE).generateSecret(new DESedeKeySpec(keyGenerator.generateKey().getEncoded()));
            case EncryptDecryptUtil.ALGORITHMS_AES:
                keyGenerator = KeyGenerator.getInstance(ALGORITHMS_AES, "BC");
                keyGenerator.getProvider();
                keyGenerator.init(secretKeyLength);
                return new SecretKeySpec(keyGenerator.generateKey().getEncoded(), ALGORITHMS_AES);
            default:
                return null;
        }
    }
    /**
     * 根据秘钥文件获取秘钥
     *
     * @param algorithms 生成秘钥的算法
     * @param file 保存秘钥的文件
     * @return 当一切正常时返回秘钥;当算法不支持时返回null
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     * @throws InvalidKeyException InvalidKeyException
     * @throws InvalidKeySpecException InvalidKeySpecException
     * */
    public static SecretKey getSecretKey( String algorithms, File file)
            throws  NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
        byte[] sk = new byte[2048];
        int length = -1;
        try ( FileInputStream fileInputStream = new FileInputStream( file)) {
             length = fileInputStream.read( sk);
        } catch ( IOException e) {
            log.error( e.getMessage(), e);
        }
        byte[] sk2 = new byte[length];
        System.arraycopy( sk, 0, sk2, 0, length);
        Security.addProvider( new BouncyCastleProvider());
        switch ( algorithms) {
            case EncryptDecryptUtil.ALGORITHMS_DES:
                return SecretKeyFactory.getInstance( EncryptDecryptUtil.ALGORITHMS_DES).generateSecret( new DESKeySpec( sk2));
            case EncryptDecryptUtil.ALGORITHMS_3DES:
                return SecretKeyFactory.getInstance( EncryptDecryptUtil.ALGORITHMS_DESEDE).generateSecret( new DESedeKeySpec( sk2));
            case EncryptDecryptUtil.ALGORITHMS_AES:
                return new SecretKeySpec( sk2, EncryptDecryptUtil.ALGORITHMS_AES);
            default:
                return null;
        }
    }
    /**
     * 根据字符串生成秘钥
     *
     * @param algorithms 加密算法
     * @param skString 秘钥明文
     * @return 当一切正常时返回秘钥;当算法不支持时返回null
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     * @throws InvalidKeyException InvalidKeyException
     * @throws InvalidKeySpecException InvalidKeySpecException
     * */
    public static SecretKey getSecretKeyUseRaw(String algorithms, String skString)
            throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
        Security.addProvider(new BouncyCastleProvider());
        switch ( algorithms) {
            case EncryptDecryptUtil.ALGORITHMS_DES:
                return SecretKeyFactory.getInstance( ALGORITHMS_DES).generateSecret( new DESKeySpec(skString.getBytes( Charsets.UTF_8)));
            case EncryptDecryptUtil.ALGORITHMS_3DES:
                return SecretKeyFactory.getInstance( ALGORITHMS_DESEDE).generateSecret( new DESedeKeySpec(skString.getBytes( Charsets.UTF_8)));
            case EncryptDecryptUtil.ALGORITHMS_AES:
                return new SecretKeySpec( skString.getBytes( Charsets.UTF_8), ALGORITHMS_AES);
            default:
                return null;
        }
    }
    /**
     * <p> 生成随机秘钥并保存到文件
     *
     * @param algorithms 生成秘钥的算法
     * @param secretKeyLength 秘钥长度
     * @param file 保存秘钥的文件
     * @return 秘钥
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     * @throws NoSuchProviderException NoSuchProviderException
     * @throws InvalidKeyException InvalidKeyException
     * @throws InvalidKeySpecException InvalidKeySpecException
     * */
    public static SecretKey saveSecretKey(String algorithms, int secretKeyLength, File file)
            throws InvalidKeySpecException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException {
        SecretKey secretKey = getSecretKey(algorithms, secretKeyLength);
        if ( !notNull( secretKey)) { return null;}
        byte[] sk = secretKey.getEncoded();
        try ( FileOutputStream fileOutputStream = new FileOutputStream( file)) {
            fileOutputStream.write( sk, 0, sk.length);
            fileOutputStream.flush();
        } catch ( IOException e) {
            log.error( e.getMessage(), e);
            return null;
        }
        return secretKey;
    }

    private static final String IV_DES_AND_DESEDE = "TAIKANG.";
    private static final String IV_AES = "TAIKANG.PENSION.";

    private static String getIv( String mode) {
        String algorithms = mode.split("/")[0];
        switch ( algorithms) {
            case EncryptDecryptUtil.ALGORITHMS_DES:
            case EncryptDecryptUtil.ALGORITHMS_DESEDE:
                return EncryptDecryptUtil.IV_DES_AND_DESEDE;
            case EncryptDecryptUtil.ALGORITHMS_AES:
                return EncryptDecryptUtil.IV_AES;
            default:
                return "";
        }
    }
    /**
     * 对称加解密
     *
     * mode的取值范围:
     *  DES/ ECB,CBC,PCBC,CTR,CTS,CFB,CFB8~128,OFB,OFB8~128/ PKCS7Padding,ISO10126d2Padding,X932Padding,ISO7816d4Padding,ZeroBytePadding
     *  DESede/ ECB,CBC,PCBC,CTR,CTS,CFB,CFB8~128,OFB,OFB8~128/ PKCS7Padding,ISO10126d2Padding,X932Padding,ISO7816d4Padding,ZeroBytePadding
     *  AES/ ECB,CBC,PCBC,CTR,CTS,CFB,CFB8~128,OFB,OFB8~128/ PKCS7Padding,ZeroBytePadding
     * @param src 要加/解密密的数据
     * @param mode 加解密模式
     * @param secretKey 秘钥
     * @param emode 模式(Cipher.ENCRYPT_MODE, Cipher.DENCRYPT_MODE)
     * @return 密文/明文
     * @throws NoSuchPaddingException NoSuchPaddingException
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     * @throws InvalidKeyException InvalidKeyException
     * @throws BadPaddingException BadPaddingException
     * @throws IllegalBlockSizeException IllegalBlockSizeException
     * @throws InvalidAlgorithmParameterException InvalidAlgorithmParameterException
     * */
    public static byte[] encryptOrDencrypt( byte[] src, String mode, SecretKey secretKey, int emode) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        IvParameterSpec ivSpec = new IvParameterSpec( getIv( mode).getBytes( Charsets.UTF_8));
        Cipher cipher = Cipher.getInstance(mode);
        cipher.init(emode, secretKey, ivSpec);
        return cipher.doFinal(src);
    }

    private static final byte[] EMPTY_BYTES = new byte[]{};

    private static byte[] encryptOrDencrypt(
            String algorithm, String provider,
            byte[] src, int emode, PublicKey publicKey, PrivateKey privateKey
    ) {
        if ( isNull(src) || isNull(src)) {
            log.warn("没有要加密或解密的内容");
            return EMPTY_BYTES;
        }
        try {
            Cipher cipher = Cipher.getInstance(algorithm,provider);
            switch ( emode) {
                case Cipher.ENCRYPT_MODE:
                    if ( isNull(publicKey)) {
                        log.warn("没有找到公钥, 无法加密");
                        return EMPTY_BYTES;
                    }
                    cipher.init(Cipher.ENCRYPT_MODE, publicKey);
                    break;
                case Cipher.DECRYPT_MODE:
                    if ( isNull(privateKey)) {
                        log.warn("没有找到私钥, 无法解密");
                        return EMPTY_BYTES;
                    }
                    cipher.init(Cipher.DECRYPT_MODE, privateKey);
                    break;
                default:
                    log.warn("没有找到对应的处理方式, 无法进行操作");
                    return EMPTY_BYTES;
            }
            return cipher.doFinal(src);
        } catch ( NoSuchAlgorithmException|
                NoSuchProviderException|
                NoSuchPaddingException|
                InvalidKeyException|
                IllegalBlockSizeException|
                BadPaddingException e) {
            log.error("加解密过程发生异常, 请参考错误信息");
            log.error(e.getMessage(), e);
            return EMPTY_BYTES;
        }
    }
    public static String getEncodedPublicKey( PublicKey publicKey) {
        if ( isNull( publicKey)) {
            log.warn("没有找到公钥");
            return StringUtils.newStringUtf8(EMPTY_BYTES);
        }
        return StringUtils.newStringUtf8(Base64.encode(publicKey.getEncoded()).getBytes(Charsets.UTF_8));
    }
    public static String getEncodedPrivateKey( PrivateKey privateKey) {
        if ( isNull( privateKey)) {
            log.warn("没有找到私钥");
            return StringUtils.newStringUtf8(EMPTY_BYTES);
        }
        return StringUtils.newStringUtf8(Base64.encode(privateKey.getEncoded()).getBytes(Charsets.UTF_8));
    }
    public static byte[] signature(String signatureAlgorithm , byte[] src, PrivateKey privateKey) {
        try {
            Signature signature = Signature.getInstance(signatureAlgorithm);
            signature.initSign(privateKey);
            signature.update(src);
            return signature.sign();
        } catch ( NoSuchAlgorithmException|InvalidKeyException|SignatureException e) {
            log.error(e.getMessage(), e);
            return EMPTY_BYTES;
        }
    }
    public static boolean verify(String signatureAlgorithm, byte[] src, byte[] sign, PublicKey publicKey) {
        try {
            Signature signature = Signature.getInstance(signatureAlgorithm);
            signature.initVerify(publicKey);
            signature.update(src);
            return signature.verify(sign);
        } catch ( NoSuchAlgorithmException|InvalidKeyException|SignatureException e) {
            log.error(e.getMessage(),e);
            return false;
        }
    }

    //非对称加解密

    /**
     * RSA加解密工具类
     *
     * <p> 创建时间：2019/1/29
     *
     * @author guojy24
     * @version 1.0
     * */
    @Slf4j @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RsaUtil {

        public static PublicKey getPublicKey(String encodedPublicKey) {
            byte[] keyBytes = Base64.decode(encodedPublicKey);
            try {
                KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
                return keyFactory.generatePublic(new X509EncodedKeySpec(keyBytes));
            } catch ( NoSuchAlgorithmException|InvalidKeySpecException e) {
                log.error(e.getMessage(),e);
                return null;
            }
        }
        public static PrivateKey getPrivateKey(String encodedPrivateKey) {
            byte[] keyBytes = Base64.decode(encodedPrivateKey);
            try {
                KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
                PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
                return privateKey;
            } catch (NoSuchAlgorithmException|InvalidKeySpecException e) {
                log.error(e.getMessage(),e);
                return null;
            }
        }
        public static byte[] encryptOrDencrypt(byte[] src, int emode, PublicKey publicKey, PrivateKey privateKey) {
            return EncryptDecryptUtil.encryptOrDencrypt(ALGORITHM,PROVIDER,src,emode,publicKey,privateKey);
        }
        public static byte[] signature(byte[] src, PrivateKey privateKey) {
            return EncryptDecryptUtil.signature(SIGNATURE_ALGORITHM, src, privateKey);
        }
        public static boolean verify(byte[] src, byte[] sign, PublicKey publicKey) {
            return EncryptDecryptUtil.verify(SIGNATURE_ALGORITHM, src, sign, publicKey);
        }

        /**
         * 签名算法
         * */
        private static final String SIGNATURE_ALGORITHM = "SHA512withRSA";
        /**
         * 加密算法
         * */
        private static final String ALGORITHM = "RSA";
        /**
         * 算法提供者
         * */
        private static final String PROVIDER = "BC";
    }

    /**
     * ECC加解密工具类
     * 参考内容 https://www.cnblogs.com/xinzhao/p/8963724.html
     *
     * <p> 创建时间：2019/1/29
     *
     * @author guojy24
     * @version 1.0
     * */
    @Slf4j @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class EccUtil {

        /**
         * 根据编码的公钥生成ECC公钥
         *
         * @param encodedPublicKey 编码过的ECC公钥
         * @param publicKeyTL 公钥构件
         * @return ECC公钥
         * */
        public static ECPublicKey getPublicKey(String encodedPublicKey, byte[] publicKeyTL) {
            byte[] keyBytes = Base64.decode(encodedPublicKey);
            if(keyBytes.length == 65) {
                byte[] tlv = new byte[91];
                System.arraycopy(publicKeyTL, 0, tlv, 0, 26);
                System.arraycopy(keyBytes, 0, tlv, 26, 65);
                keyBytes = tlv;
            }
            try {
                KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM,PROVIDER);
                return (ECPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(keyBytes));
            } catch ( NoSuchAlgorithmException|NoSuchProviderException|InvalidKeySpecException e) {
                log.error( e.getMessage(), e);
                return null;
            }
        }
        /**
         * 根据编码的私钥生成ECC私钥
         *
         * @param encodedPrivateKey 编码过的ECC私钥
         * @return ECC私钥
         * */
        public static ECPrivateKey getPrivateKey( String encodedPrivateKey) {
            byte[] keyBytes = Base64.decode(encodedPrivateKey);
            try {
                KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM, PROVIDER);
                return (ECPrivateKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
            } catch (NoSuchAlgorithmException|NoSuchProviderException|InvalidKeySpecException e) {
                log.error( e.getMessage(), e);
                return null;
            }
        }
        public static byte[] encryptOrDencrypt(byte[] src, int emode, PublicKey publicKey, PrivateKey privateKey) {
            return EncryptDecryptUtil.encryptOrDencrypt(ALGORITHM,PROVIDER,src,emode,publicKey,privateKey);
        }
        public static byte[] signature(byte[] src, PrivateKey privateKey) {
            return EncryptDecryptUtil.signature(SIGNATURE_ALGORITHM, src, privateKey);
        }
        public static boolean verify(byte[] src, byte[] sign, PublicKey publicKey) {
            return EncryptDecryptUtil.verify(SIGNATURE_ALGORITHM, src, sign, publicKey);
        }

        /**
         * 签名算法
         * */
        private static final String SIGNATURE_ALGORITHM = "SHA512withECDSA";
        /**
         * 加密算法
         * */
        private static final String ALGORITHM = "ECIES";
        /**
         * 算法提供者
         * */
        private static final String PROVIDER = "BC";
    }


    //DH
//    public final static KeyPair getSenderPublicKeyEnc_DH(int keySize) throws NoSuchAlgorithmException {
//        KeyPairGenerator senderKeyPairGenerator = KeyPairGenerator.getInstance("DH");
//        senderKeyPairGenerator.initialize(keySize);
//        return  senderKeyPairGenerator.generateKeyPair();
//    }
//    public final static SecretKey[] getPublicAndSecretKey_DH(KeyPair keyPair) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, InvalidKeyException {
//        KeyFactory factory = KeyFactory.getInstance("DH");
//        PublicKey receiverPublicKey = factory.generatePublic(new X509EncodedKeySpec(keyPair.getPublic().getEncoded()));
//
//        KeyPairGenerator receiverKeyPairGenerator = KeyPairGenerator.getInstance("DH");
//        receiverKeyPairGenerator.initialize(((DHPublicKey)receiverPublicKey).getParams());
//        KeyPair receiverKeyPair = receiverKeyPairGenerator.generateKeyPair();
//        PrivateKey receiverPrivateKey = receiverKeyPair.getPrivate();
//        byte[] receiverPublicKeyEnc = receiverKeyPair.getPublic().getEncoded();
//
//        KeyAgreement receiverKeyAgreement = KeyAgreement.getInstance("DH");
//        receiverKeyAgreement.init(receiverPrivateKey);
//        receiverKeyAgreement.doPhase(receiverPublicKey, true);
//        SecretKey receiverKey = receiverKeyAgreement.generateSecret("DES");
//        KeyFactory senderKeyFactory = KeyFactory.getInstance("DH");
//        PublicKey senderPublicKey = senderKeyFactory.generatePublic(new X509EncodedKeySpec(receiverPublicKeyEnc));
//        KeyAgreement senderKeyAgreement=KeyAgreement.getInstance("DH");
//        senderKeyAgreement.init(keyPair.getPrivate());
//        senderKeyAgreement.doPhase(senderPublicKey, true);
//        SecretKey senderKey = senderKeyAgreement.generateSecret("DES");
//        return new SecretKey[]{senderKey, receiverKey};
//    }
//
//    public final static byte[] encryptOrDencrypt_DH(byte[] src, SecretKey secretKey, int mode) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
//        Cipher cipher = Cipher.getInstance("DES");
//        cipher.init(mode, secretKey);
//        return cipher.doFinal(src);
//    }

    //RSA
//    public final static Key[] getPublicAndSecretKey_RSA(int keySize) throws NoSuchAlgorithmException {
//        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
//        keyPairGenerator.initialize(keySize);
//        KeyPair keyPair = keyPairGenerator.generateKeyPair();
//        return new Key[]{(RSAPublicKey) keyPair.getPublic(), (RSAPrivateKey) keyPair.getPrivate()};
//    }
//
//    public final static byte[] encryptOrDencrypt_RSA(byte[] src, Key key, int mode) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
//        return null;
//    }

    //ELGamal
}
