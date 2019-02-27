package com.tkp.tkpole.starter.utils.gson;

import com.google.common.collect.ImmutableMap;
import com.google.gson.reflect.TypeToken;
import com.tkp.tkpole.starter.utils.JsonXmlUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

@Slf4j
public class GsonBuilderStrategyTest {

    @Before
    public void before() {
        jsonBean = new JsonBean( "attr", "", null);
    }

    @Test
    public void testREMOVE_NULLS() {
        Assert.assertEquals(
    "{" +
                    "\"attr\":\"attr\"," +
                    "\"attr2\":\"\"" +
                "}",
                JsonXmlUtil.javaBean2Json( jsonBean, GsonBuilderStrategy.REMOVE_NULLS));
    }

    @Test
    public void testNULLS_TO_NULL() {
        Assert.assertEquals(
    "{" +
                    "\"attr\":\"attr\"," +
                    "\"attr2\":\"\"," +
                    "\"attr3\":null" +
                "}",
                JsonXmlUtil.javaBean2Json( jsonBean, GsonBuilderStrategy.NULLS_TO_NULL));
    }

    @Test
    public void testNULLS_STRING_TO_EMPTY() {
        Assert.assertEquals(
    "{" +
                    "\"attr\":\"attr\"," +
                    "\"attr2\":\"\"," +
                    "\"attr3\":\"\"" +
                "}",
                JsonXmlUtil.javaBean2Json( jsonBean, GsonBuilderStrategy.NULLS_STRING_TO_EMPTY));
    }

    @Test
    public void test() {

        String jsonString = "[\n" +
                "    {\n" +
                "        \"beg_date\": \"2012-08-30\",\n" +
                "        \"bnsyl\": \"0.02\",\n" +
                "        \"clylsyl\": \"26.23\",\n" +
                "        \"dwjj\": \"/\"\n" +
                "    }, {\n" +
                "        \"beg_date\": \"2012-08-31\",\n" +
                "        \"bnsyl\": \"1.02\",\n" +
                "        \"clylsyl\": \"-26.23\",\n" +
                "        \"dwjj\": \"/\"\n" +
                "    }\n" +
                "]";

        List<TJBean> tjBeans = JsonXmlUtil.json2JavaBean( new TypeToken<List<TJBean>>(){}.getType(), jsonString, GsonBuilderStrategy.NULLS_TO_NULL);
        tjBeans.forEach(System.out::println);

    }

    @Test
    public void test2() {
        Map<String,String> map = ImmutableMap.<String,String>builder().put("1","one").put("2","two").build();
        String str = JsonXmlUtil.javaBean2Json(map);
        Map<String,String> map2 = JsonXmlUtil.json2JavaBean(Map.class,str);
        log.info(map2.toString());
    }



    private JsonBean jsonBean;

    @Data @AllArgsConstructor @NoArgsConstructor
    private static class JsonBean {
        private String attr;
        private String attr2;
        private String attr3;
    }

    @Data @AllArgsConstructor @NoArgsConstructor
    private static class TJBean {
        private String beg_date;
        private String bnsyl;
        private String clylsyl;
        private String dwjj;
    }

}