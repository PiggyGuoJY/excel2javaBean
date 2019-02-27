package com.tkp.tkpole.starter.utils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import oracle.sql.BLOB;
import org.apache.commons.io.IOUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;

/**
 * <p> 数据库工具类
 * <p> 创建时间：2018/2/11
 *
 * @author guojy24
 * @version 1.0
 * */
@Slf4j @NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DataBaseUtil {

    /**
     * <p> 用于转化Oracle的BLOB到Java的String类型
     *
     * @param blob 描述此参数的作用
     * @return 描述返回值
     * */
    public static String blob2String4Oracle( BLOB blob) {
        try {
            boolean isUat = ResourceUtil.EnvironmentType.UAT.equals(ResourceUtil.getEnvironmentType());
            long length = blob.length();
            if ( length <= (long)Integer.MAX_VALUE) {
                byte[] bytes = new byte[( int)length];
                IOUtils.readFully( blob.getBinaryStream(), bytes);
                // 额, 现在Oracle数据库里BLOB字段使用zhs16gbk编码, 这里使用gb2312; 生产上还得看下
                return new String( bytes, isUat ? Charset.forName("gb2312") : Charset.forName("utf-8"));
            } else {
                //todo 内容过程, 得分段拼接
                return null;
            }
        } catch ( SQLException | IOException e) {
            log.error( e.getMessage(), e);
        }
        return null;
    }

    @ToString
    public enum DatabaseType {
        /**
         * Oracle数据库
         * */
        Oracle("SELECT 1 FROM dual", Integer.class, 1),
        /**
         * MySql数据库
         * */
        MySql("SELECT 1", Integer.class, 1),
        /**
         * Postgresql数据库
         * */
        Postgresql("SELECT DISTINCT 1 FROM pg_tables", Integer.class, 1);

        @Getter
        private String query;
        @Getter
        private Class<?> type;
        @Getter
        private Object object;
        <T> DatabaseType(String query, Class<T> type, T t) {
            this.query = query;
            this.type = type;
            this.object = t;
        }
    }

    /**
     * <p> 数据库连接测试程序
     *
     * @param databaseType 描述此参数的作用
     * @param jdbcTemplate 描述此参数的作用
     * @return 是否连接成功
     * */
    public static boolean attach(DatabaseType databaseType, JdbcTemplate jdbcTemplate) {
        return databaseType.getObject().equals(jdbcTemplate.queryForObject(databaseType.getQuery(), databaseType.getType()));
    }
}