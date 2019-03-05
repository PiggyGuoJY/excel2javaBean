package com.guojy;

import com.google.gson.reflect.TypeToken;
import com.guojy.gson.GsonBuilderStrategy;
import com.guojy.model.Msg;
import com.guojy.model.test.Student;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.time.LocalDate;

import static com.guojy.JsonUtil.javaBean2Json;
import static com.guojy.JsonUtil.json2JavaBean;
import static com.guojy.model.Msg.msg;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@Slf4j
public class JsonUtilTest {

    @Test
    public void test() {
        Msg<Student> studentMsg
                = msg(Student.builder()
                        .name("张三").idType("护照").idNo("123456")
                        .birthDate(LocalDate.now()).gender("男")
                        .phoneNo("12345678901")
                        .address("北京市东城区")
                        .build());

        String json = javaBean2Json(studentMsg);
        log.info("javaBean=>json: " + json);

        @SuppressWarnings("unchecked")
        Msg<Student> studentMsg2 = (Msg<Student>)json2JavaBean(Msg.class,json);
        log.info("json=>javaBean: " + studentMsg2.toString());
        assertNotEquals(studentMsg,studentMsg2);

        studentMsg2 = json2JavaBean(new TypeToken<Msg<Student>>(){}.getType(),json);
        log.info("json=>javaBean: " + studentMsg2.toString());
        assertEquals(studentMsg,studentMsg2);
    }

    @Test @SuppressWarnings("unchecked")
    public void test2() {
        Msg<Student> studentMsg
                = msg(Student.builder()
                .name("张三").idNo("123456")
                .gender("男")
                .phoneNo("12345678901")
                .address("北京市东城区")
                .build());
        String json = javaBean2Json(studentMsg, GsonBuilderStrategy.NULLS_TO_NULL);
        log.info("javaBean=>json: " + json);

        Msg<Student> studentMsg2 = (Msg<Student>)json2JavaBean(Msg.class,json,GsonBuilderStrategy.NULLS_TO_NULL);
        log.info("json=>javaBean: " + studentMsg2.toString());
        assertNotEquals(studentMsg,studentMsg2);

        studentMsg2 = json2JavaBean(new TypeToken<Msg<Student>>(){}.getType(),json,GsonBuilderStrategy.NULLS_TO_NULL);
        log.info("json=>javaBean: " + studentMsg2.toString());
        assertEquals(studentMsg,studentMsg2);


        json = javaBean2Json(studentMsg, GsonBuilderStrategy.NULLS_STRING_TO_EMPTY);
        log.info("javaBean=>json: " + json);

        studentMsg2 = (Msg<Student>)json2JavaBean(Msg.class,json,GsonBuilderStrategy.NULLS_STRING_TO_EMPTY);
        log.info("json=>javaBean: " + studentMsg2.toString());
        assertNotEquals(studentMsg,studentMsg2);

        studentMsg2 = json2JavaBean(new TypeToken<Msg<Student>>(){}.getType(),json,GsonBuilderStrategy.NULLS_STRING_TO_EMPTY);
        log.info("json=>javaBean: " + studentMsg2.toString());
        assertNotEquals(studentMsg,studentMsg2); //注意这里的使用


        json = javaBean2Json(studentMsg, GsonBuilderStrategy.REMOVE_NULLS);
        log.info("javaBean=>json: " + json);

        studentMsg2 = (Msg<Student>)json2JavaBean(Msg.class,json,GsonBuilderStrategy.REMOVE_NULLS);
        log.info("json=>javaBean: " + studentMsg2.toString());
        assertNotEquals(studentMsg,studentMsg2);

        studentMsg2 = json2JavaBean(new TypeToken<Msg<Student>>(){}.getType(),json,GsonBuilderStrategy.REMOVE_NULLS);
        log.info("json=>javaBean: " + studentMsg2.toString());
        assertEquals(studentMsg,studentMsg2);
    }
}