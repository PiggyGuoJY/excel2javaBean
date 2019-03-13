package com.github.piggyguojy;

import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AssertTest {

    @Test
    public void notNull() {
        assertTrue(Assert.notNull(new Object()));
        assertFalse(Assert.notNull(null));
    }

    @Test
    public void isNull() {
        assertTrue(Assert.isNull(null));
        assertFalse(Assert.isNull(new Object()));
    }

    @Test
    public void isNul() {
        assertTrue(Assert.isNul((byte[])null));
        assertTrue(Assert.isNul(new byte[]{}));
        assertFalse(Assert.isNul(new byte[]{1}));
    }

    @Test
    public void notNul() {
        assertFalse(Assert.notNul((byte[])null));
        assertFalse(Assert.notNul(new byte[]{}));
        assertTrue(Assert.notNul(new byte[]{1}));
    }

    @Test
    public void isNul1() {
        assertTrue(Assert.isNul((short[])null));
        assertTrue(Assert.isNul(new short[]{}));
        assertFalse(Assert.isNul(new short[]{1}));
    }

    @Test
    public void notNul1() {
        assertFalse(Assert.notNul((short[])null));
        assertFalse(Assert.notNul(new short[]{}));
        assertTrue(Assert.notNul(new short[]{1}));
    }

    @Test
    public void isNul2() {
        assertTrue(Assert.isNul((int[])null));
        assertTrue(Assert.isNul(new int[]{}));
        assertFalse(Assert.isNul(new int[]{1}));
    }

    @Test
    public void notNul2() {
        assertFalse(Assert.notNul((int[])null));
        assertFalse(Assert.notNul(new int[]{}));
        assertTrue(Assert.notNul(new int[]{1}));
    }

    @Test
    public void isNul3() {
        assertTrue(Assert.isNul((long[])null));
        assertTrue(Assert.isNul(new long[]{}));
        assertFalse(Assert.isNul(new long[]{1}));
    }

    @Test
    public void notNul3() {
        assertFalse(Assert.notNul((long[])null));
        assertFalse(Assert.notNul(new long[]{}));
        assertTrue(Assert.notNul(new long[]{1}));
    }

    @Test
    public void isNul4() {
        assertTrue(Assert.isNul((float[])null));
        assertTrue(Assert.isNul(new float[]{}));
        assertFalse(Assert.isNul(new float[]{1F}));
    }

    @Test
    public void notNul4() {
        assertFalse(Assert.notNul((float[])null));
        assertFalse(Assert.notNul(new float[]{}));
        assertTrue(Assert.notNul(new float[]{1F}));
    }

    @Test
    public void isNul5() {
        assertTrue(Assert.isNul((double[])null));
        assertTrue(Assert.isNul(new double[]{}));
        assertFalse(Assert.isNul(new double[]{1F}));
    }

    @Test
    public void notNul5() {
        assertFalse(Assert.notNul((double[])null));
        assertFalse(Assert.notNul(new double[]{}));
        assertTrue(Assert.notNul(new double[]{1F}));
    }

    @Test
    public void isNul6() {
        assertTrue(Assert.isNul((char[])null));
        assertTrue(Assert.isNul(new char[]{}));
        assertFalse(Assert.isNul(new char[]{'\u0000'}));
    }

    @Test
    public void notNul6() {
        assertFalse(Assert.notNul((char[])null));
        assertFalse(Assert.notNul(new char[]{}));
        assertTrue(Assert.notNul(new char[]{'\u0000'}));
    }

    @Test
    public void isNul7() {
        assertTrue(Assert.isNul((boolean[])null));
        assertTrue(Assert.isNul(new boolean[]{}));
        assertFalse(Assert.isNul(new boolean[]{true}));
    }

    @Test
    public void notNul7() {
        assertFalse(Assert.notNul((boolean[])null));
        assertFalse(Assert.notNul(new boolean[]{}));
        assertTrue(Assert.notNul(new boolean[]{true}));
    }

    @Test
    public void isNul8() {
        assertTrue(Assert.isNul((Object[])null));
        assertTrue(Assert.isNul(new Object[]{}));
        assertFalse(Assert.isNul(new Object[]{true}));
    }

    @Test
    public void notNul8() {
        assertFalse(Assert.notNul((Object[])null));
        assertFalse(Assert.notNul(new Object[]{}));
        assertTrue(Assert.notNul(new Object[]{true}));
    }

    @Test
    public void isNul9() {
        assertTrue(Assert.isNul((String)null));
        assertTrue(Assert.isNul(""));
        assertFalse(Assert.isNul("hello"));
    }

    @Test
    public void notNul9() {
        assertFalse(Assert.notNul((String)null));
        assertFalse(Assert.notNul(""));
        assertTrue(Assert.notNul("hello"));
    }

    @Test
    public void notNul10() {
        assertFalse(Assert.notNul((Collection)null));
        assertFalse(Assert.notNul(Collections.EMPTY_LIST));
        assertTrue(Assert.notNul(Arrays.asList("1","2","3")));
    }

    @Test
    public void notNul11() {
        assertFalse(Assert.notNul((File)null));
        assertFalse(Assert.notNul(new File("/home/fileNoExist")));
        assertFalse(Assert.notNul(new File(AssertTest.class.getResource("/simple/").getFile())));
        assertFalse(Assert.notNul(new File(AssertTest.class.getResource("/simple/nothingFile.txt").getFile())));
        assertTrue(Assert.notNul(new File(AssertTest.class.getResource("/simple/TestExcelFile.xlsx").getFile())));

    }
}