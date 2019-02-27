package com.tkp.tkpole.starter.utils.misc.cryptogram;

import com.tkp.tkpole.starter.utils.EncryptDecryptUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.math.ec.ECCurve;

import java.security.*;

import static com.tkp.tkpole.starter.utils.Assert.notNull;

@Slf4j
public class Ecc {

    private static SecureRandom SECURE_RANDOM;

    static {
        try {
            SECURE_RANDOM = SecureRandom.getInstance("PKCS11","SUN");
            SECURE_RANDOM.setSeed(System.currentTimeMillis());
        } catch ( NoSuchAlgorithmException|NoSuchProviderException e) {
            log.error(e.getMessage(),e);
            SECURE_RANDOM = new SecureRandom(StringUtils.getBytesUtf8(""+System.currentTimeMillis()));
        }

    }


    public Ecc() {
        Security.addProvider( new BouncyCastleProvider());
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM,PROVIDER);
            keyPairGenerator.initialize(KEY_SIZE,SECURE_RANDOM);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            this.ecPublicKey = (ECPublicKey) keyPair.getPublic();
            System.arraycopy(this.ecPublicKey.getEncoded(),0,publicKeyTL,0,26);
            this.ecPrivateKey = (ECPrivateKey) keyPair.getPrivate();
            this.ecCurve = this.ecPublicKey.getParameters().getCurve();
        } catch ( NoSuchProviderException |NoSuchAlgorithmException e) {
            log.error(e.getMessage(), e);
            this.ecPublicKey = null;
            this.ecPrivateKey = null;
            this.ecCurve = null;
        }
        showCurveParams(); // todo 调试使用, 上线时记得注释掉
    }

    public Ecc(int keySize) {
        Security.addProvider( new BouncyCastleProvider());
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM,PROVIDER);
            keyPairGenerator.initialize(keySize,SECURE_RANDOM);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            this.ecPublicKey = (ECPublicKey) keyPair.getPublic();
            System.arraycopy(this.ecPublicKey.getEncoded(),0,publicKeyTL,0,26);
            this.ecPrivateKey = (ECPrivateKey) keyPair.getPrivate();
            this.ecCurve = this.ecPublicKey.getParameters().getCurve();
        } catch ( NoSuchProviderException |NoSuchAlgorithmException e) {
            log.error(e.getMessage(), e);
            this.ecPublicKey = null;
            this.ecPrivateKey = null;
            this.ecCurve = null;
        }
        showCurveParams(); // todo 调试使用, 上线时记得注释掉
    }

    @Getter
    private byte[] publicKeyTL = new byte[26];

    /**
     * 打印参数信息
     * */
    private void showCurveParams() {
        if ( notNull(ecPublicKey) && notNull(ecPrivateKey) && notNull(ecCurve)) {
            log.info(
                    "椭圆曲线的参数:\na: {}\nb: {}\nq: {}\n" +
                            "基点坐标: {}, {}\n" +
                            "公钥坐标: {}, {}\n" +
                            "私钥: {}",
                    this.ecCurve.getA().toBigInteger().toString(10),
                    this.ecCurve.getB().toBigInteger().toString(10),
                    this.ecCurve.getField().getCharacteristic().toString(10),
                    this.ecPublicKey.getParameters().getG().getAffineXCoord().toBigInteger().toString(10),
                    this.ecPublicKey.getParameters().getG().getAffineYCoord().toBigInteger().toString(10),
                    this.ecPublicKey.getQ().getAffineXCoord().toBigInteger().toString(10),
                    this.ecPublicKey.getQ().getAffineYCoord().toBigInteger().toString(10),
                    this.ecPrivateKey.getD().toString(10)
            );
        } else {
            log.error("秘钥生成失败, 不能获取到相关参数");
        }
    }

    public String getEncodedPublicKey() {
        return EncryptDecryptUtil.getEncodedPublicKey(this.ecPublicKey);
    }
    public String getEncodedPrivateKey() { return EncryptDecryptUtil.getEncodedPrivateKey(this.ecPrivateKey); }
    /**
     * 加解密
     *
     * @param src 加密或解密的数据
     * @param emode 加解密模式
     * @return 加解密内容吧
     * */
    public byte[] encryptOrDencrypt(byte[] src, int emode) {
        return EncryptDecryptUtil.EccUtil.encryptOrDencrypt(src,emode,ecPublicKey,ecPrivateKey);
    }

    /**
     * 公钥
     * */
    @Getter
    private ECPublicKey ecPublicKey;
    /**
     * 私钥
     * */
    @Getter
    private ECPrivateKey ecPrivateKey;
    /**
     * 椭圆曲线
     * */
    private ECCurve ecCurve;

    /**
     * 秘钥长度, 这个地方的变动可能需要修改政策文件来支持
     * */
    private static final int KEY_SIZE = 256;
    /**
     * 加密算法
     * */
    private static final String ALGORITHM = "ECIES";
    /**
     * 算法提供者
     * */
    private static final String PROVIDER = "BC";
}
