package com.tkp.tkpole.starter.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.crypto.digests.*;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;

import static com.tkp.tkpole.starter.utils.Assert.notNull;

/**
 * 摘要算法工具类
 *
 * <p>快速入门
 * <blockquote><pre>
 *     byte[] src = "测试语句".getByte("UTF-8");
 *     String digest = DigestUtil.getDigest(src, "SHA-256");//使用SHA-256获取摘要
 *     byte[] sk = new byte[6]{0x00, 0x02, 0x45, 0xAB, 0xA3, 0x78};
 *     digest = DigestUtil.getDigest(src, sk, "MD5")//使用HMAC+MD5获取摘要
 * </pre></blockquote>
 *
 * <p>已不再支持MD2, MD4和MD5摘要算法
 *
 * <p> 创建时间：2018/3/20
 *
 * @author guojy24
 * @version 1.0
 * */
@Slf4j @NoArgsConstructor( access = AccessLevel.PRIVATE)
public final class DigestUtil {

    public static final String SHA = "SHA";
    public static final String SHA_224 = "SHA-224";
    public static final String SHA_256 = "SHA-256";
    public static final String SHA_384 = "SHA-384";
    public static final String SHA_512 = "SHA-512";

    /**
     * 获取摘要
     *
     * @param src 数据对象
     * @param algorithms 摘要算法(SHA, SHA-224, SHA-256, SHA-384, SHA-512)
     * @return 摘要(十六进制的字符串)或者{@code null}
     * */
    public static String getDigest(@NonNull byte[] src, @NonNull String algorithms) {
        MessageDigest messageDigest = MESSAGE_DIGEST.get( algorithms);
        if ( notNull( messageDigest)) {
            return Hex.encodeHexString( messageDigest.digest(src));
        } else {
            return null;
        }

    }

    /**
     * 获取摘要(HMAC)
     *
     * @param src 数据对象
     * @param secretKey 秘钥(长度至少为6bytes)
     * @param algorithms 摘要算法(SHA, SHA-224, SHA-256, SHA-384, SHA-512)
     * @return 摘要(十六进制的字符串)或null(当算法不存在时)
     * */
    public static String getDigest( @NonNull byte[] src, @NonNull byte[] secretKey, @NonNull String algorithms) { //Bouncy Castle
        HMac hMac = null;
        switch ( algorithms) {
            case SHA: hMac = new HMac(new SHA1Digest()); break;
            case SHA_224: hMac = new HMac(new SHA224Digest()); break;
            case SHA_256: hMac = new HMac(new SHA256Digest()); break;
            case SHA_384: hMac = new HMac(new SHA384Digest()); break;
            case SHA_512: hMac = new HMac(new SHA512Digest()); break;
            default: return null;
        }
        hMac.init(new KeyParameter(secretKey));
        hMac.update(src,0,src.length);
        byte[] result = new byte[hMac.getMacSize()];
        hMac.doFinal(result, 0);
        return Hex.encodeHexString(result);
    }

    private static final Map<String, MessageDigest> MESSAGE_DIGEST = new HashMap<>(10);
    static {
        try {
            Security.addProvider( new BouncyCastleProvider());
            MESSAGE_DIGEST.put(SHA, MessageDigest.getInstance(SHA));
            MESSAGE_DIGEST.put(SHA_224, MessageDigest.getInstance(SHA_224));
            MESSAGE_DIGEST.put(SHA_256, MessageDigest.getInstance(SHA_256));
            MESSAGE_DIGEST.put(SHA_384, MessageDigest.getInstance(SHA_384));
            MESSAGE_DIGEST.put(SHA_512, MessageDigest.getInstance(SHA_512));
        } catch ( NoSuchAlgorithmException e) {
            log.error( e.getMessage(), e);
        }
    }
}