package com.guojy;

import com.guojy.model.test.Student;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

@Slf4j
public class JsonXmlUtilTest {

    @Test
    public void test() {
        Student student = Student.builder()
                .name("张三")
                .idType("护照").idNo("12345678")
                .birthDate(LocalDate.now()).gender("男")
                .phoneNo("12345678901")
                .address("北京市海淀区")
                .build();
        log.info(JsonXmlUtil.javaBean2Json(student));
    }

}