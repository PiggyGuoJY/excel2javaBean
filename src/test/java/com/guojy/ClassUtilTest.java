package com.guojy;

import com.guojy.parser.rule.type.Transformable;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Set;

@Slf4j
public class ClassUtilTest {

    @Test
    public void test(){
        Set<Class<?>> classSet  = ClassUtil.getClassesWithInterfaceImplemented(Transformable.class,"com.guojy",true);
        classSet.forEach(zlass -> {log.info(zlass.getSimpleName());});
    }

}