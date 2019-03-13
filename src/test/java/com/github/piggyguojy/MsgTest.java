package com.github.piggyguojy;

import org.junit.Test;

import java.util.Date;

import static com.github.piggyguojy.Msg.msg;
import static org.junit.Assert.*;

public class MsgTest {

    private static final Msg<Date> DATE_MSG = new Msg<>("0","正常访问2","无详细信息", new Date());
    private static final Msg<Date> DATE_MSG_ERR = new Msg<>(new IllegalStateException("异常状态2"));


    @Test
    public void test() {

        Msg<Date> dateMsg;

        dateMsg = new Msg<>("0","正常访问","无详细信息", new Date());
        assertFalse(dateMsg.isException());
        assertTrue(Assert.notNull(dateMsg.getT()));

        dateMsg = new Msg<>();
        assertFalse(dateMsg.isException());
        assertTrue(Assert.isNull(dateMsg.getT()));

        dateMsg = new Msg<>(DATE_MSG);
        assertFalse(dateMsg.isException());
        assertTrue(Assert.notNull(dateMsg.getT()));

        dateMsg = new Msg<>((DATE_MSG_ERR));
        assertTrue(dateMsg.isException());
        assertTrue(Assert.isNull(dateMsg.getT()));

        dateMsg = new Msg<>(new Date());
        assertFalse(dateMsg.isException());
        assertTrue(Assert.notNull(dateMsg.getT()));

        dateMsg = new Msg<>(new IllegalStateException("异常状态"));
        assertTrue(dateMsg.isException());
        assertTrue(Assert.isNull(dateMsg.getT()));

        dateMsg = new Msg<>(new Date(),"正常访问");
        assertEquals("正常访问",dateMsg.getMsg());
        assertFalse(dateMsg.isException());
        assertTrue(Assert.notNull(dateMsg.getT()));

        dateMsg = new Msg<>(new IllegalStateException("异常状态"));
        assertEquals("异常状态",dateMsg.getMsg());
        assertTrue(dateMsg.isException());
        assertTrue(Assert.isNull(dateMsg.getT()));

    }

    @Test
    public void test2() {

        Msg<Date> dateMsg;

        dateMsg = msg("0","正常访问","无详细信息", new Date());
        assertFalse(dateMsg.isException());
        assertTrue(Assert.notNull(dateMsg.getT()));

        dateMsg = msg();
        assertFalse(dateMsg.isException());
        assertTrue(Assert.isNull(dateMsg.getT()));

        dateMsg = msg(DATE_MSG);
        assertFalse(dateMsg.isException());
        assertTrue(Assert.notNull(dateMsg.getT()));

        dateMsg = msg((DATE_MSG_ERR));
        assertTrue(dateMsg.isException());
        assertTrue(Assert.isNull(dateMsg.getT()));

        dateMsg = msg(new Date());
        assertFalse(dateMsg.isException());
        assertTrue(Assert.notNull(dateMsg.getT()));

        dateMsg = msg(new IllegalStateException("异常状态"));
        assertTrue(dateMsg.isException());
        assertTrue(Assert.isNull(dateMsg.getT()));

        dateMsg = msg(new Date(),"正常访问");
        assertEquals("正常访问",dateMsg.getMsg());
        assertFalse(dateMsg.isException());
        assertTrue(Assert.notNull(dateMsg.getT()));

        dateMsg = msg(new IllegalStateException("异常状态"));
        assertEquals("异常状态",dateMsg.getMsg());
        assertTrue(dateMsg.isException());
        assertTrue(Assert.isNull(dateMsg.getT()));
    }

}