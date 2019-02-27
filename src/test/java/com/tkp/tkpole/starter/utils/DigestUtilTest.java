package com.tkp.tkpole.starter.utils;

import com.google.common.base.Charsets;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

@Slf4j
public class DigestUtilTest {

    private static final byte[] src = "大学之道在明明德".getBytes(Charsets.UTF_8);
    private static final byte[] salt ="花动一山春色".getBytes(Charsets.UTF_8);

    @Test
    public void test() {
        assertEquals(
              "2e8fc94c4e13cf22a88c9d3c8aee169dd2eda1b3",
              DigestUtil.getDigest(src,DigestUtil.SHA));
        assertEquals(
              "bf8cfc0bbafc02f40de7745a2efb3c82722fe1de68398c33d48653ac",
              DigestUtil.getDigest(src,DigestUtil.SHA_224));
        assertEquals(
                "9562388aa4aca457717d55c4a37c4b67c187dde8f8e0025c999c7f48a27c0e0f",
                DigestUtil.getDigest(src,DigestUtil.SHA_256));
        assertEquals(
                "4368cac484e9ba17b061a22982869284d18eb0d72014cb9e42bdde62bdd6064139ce7bdd328bfb8ba0a96ac6633c1fb6",
                DigestUtil.getDigest(src,DigestUtil.SHA_384));
        assertEquals(
                "823f1d17f6bbf550b4a5251d6d2040766a63997102fe62dc9f7c37cd02279bcb2046b39a7e642115ff6d061fcab6dd7702b6476bd1f7990208b1eea23904afba",
                DigestUtil.getDigest(src,DigestUtil.SHA_512));
    }

    @Test
    public void test2() {
        assertEquals(
                "79fd7dfa298b288ed3727542ccc0559266cde008",
                DigestUtil.getDigest(src, salt, DigestUtil.SHA));
        assertEquals(
                "e3e2de069299d8e774ea94d969cfb8510ff048e09adfba8e84979be3",
                DigestUtil.getDigest(src, salt, DigestUtil.SHA_224));
        assertEquals(
                "657ad6bcbfd1bc485cfcdbaea38345fef44571be6d910db6a7cb14b009280a0e",
                DigestUtil.getDigest(src, salt, DigestUtil.SHA_256));
        assertEquals(
                "921f83e4ee168502d3bd73f849f916e94be0917e857ed8b8ac29a573dff28204f8a82833d1aa96a42d3813ad72e03db3",
                DigestUtil.getDigest(src, salt, DigestUtil.SHA_384));
        assertEquals(
                "9a1828ac75765d861371f40aca8d2fbade0a2f905eacb719e7ccfaa9b43ec132fa3ad4bfb74607587c97e9d787640fa88509b73e832a4c02b4e690bf8b2c0dc7",
                DigestUtil.getDigest(src, salt, DigestUtil.SHA_512));
    }

}