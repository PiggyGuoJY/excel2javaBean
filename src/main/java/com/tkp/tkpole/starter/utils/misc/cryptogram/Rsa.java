package com.tkp.tkpole.starter.utils.misc.cryptogram;

import com.tkp.tkpole.starter.utils.EncryptDecryptUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Slf4j
public class Rsa {

    private static SecureRandom SECURE_RANDOM;

    static {
        try {
            SECURE_RANDOM = SecureRandom.getInstance("SHA1PRNG","SUN");
            SECURE_RANDOM.setSeed(System.currentTimeMillis());
        } catch ( NoSuchAlgorithmException|NoSuchProviderException e) {
            log.error(e.getMessage(),e);
            SECURE_RANDOM = new SecureRandom(StringUtils.getBytesUtf8(""+System.currentTimeMillis()));
        }

    }

    public Rsa() {
        Security.addProvider( new BouncyCastleProvider());
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM,PROVIDER);
            keyPairGenerator.initialize(KEY_SIZE,SECURE_RANDOM);
            KeyPair keyPair = keyPairGenerator.genKeyPair();
            this.rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
            this.rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
        } catch ( NoSuchAlgorithmException|NoSuchProviderException e) {
            log.error(e.getMessage(), e);
            this.rsaPublicKey = null;
            this.rsaPrivateKey = null;
        }
    }

    public Rsa(int keySize) {
        Security.addProvider( new BouncyCastleProvider());
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM,PROVIDER);
            keyPairGenerator.initialize(keySize,SECURE_RANDOM);
            KeyPair keyPair = keyPairGenerator.genKeyPair();
            this.rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
            this.rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
        } catch ( NoSuchAlgorithmException|NoSuchProviderException e) {
            log.error(e.getMessage(), e);
            this.rsaPublicKey = null;
            this.rsaPrivateKey = null;
        }
    }

    public String getEncodedPublicKey() {
        return EncryptDecryptUtil.getEncodedPublicKey(this.rsaPublicKey);
    }
    public String getEncodedPrivateKey() { return EncryptDecryptUtil.getEncodedPrivateKey(this.rsaPrivateKey); }
    public byte[] encryptOrDencrypt(byte[] src, int emode) {
        return EncryptDecryptUtil.RsaUtil.encryptOrDencrypt(src,emode,this.rsaPublicKey,this.rsaPrivateKey);
    }

    /**
     * 公钥
     * */
    @Getter
    private RSAPublicKey rsaPublicKey;
    /**
     * 私钥
     * */
    @Getter
    private RSAPrivateKey rsaPrivateKey;

    /**
     * 秘钥长度, 这个地方的变动可能需要修改政策文件来支持
     * */
    private static final int KEY_SIZE = 1024;
    /**
     * 加密算法
     * */
    private static final String ALGORITHM = "RSA";
    /**
     * 算法提供者
     * */
    private static final String PROVIDER = "BC";
}
