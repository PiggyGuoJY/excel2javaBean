package com.tkp.tkpole.starter.utils;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AssertTest {

    @Test
    public void test() {
        assertTrue( Assert.notNull( new Object()));
        assertFalse( Assert.notNull( null));
    }

    @Test
    public void test2() {
        assertTrue( Assert.isEmaillAddr( "15135558980@163.com"));
        assertFalse( Assert.isEmaillAddr( "15135558980!163.com"));
    }


}