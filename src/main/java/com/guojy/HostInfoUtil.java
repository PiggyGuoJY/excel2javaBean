package com.guojy;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 宿主信息采集工具类
 * <p> 应该保证这些信息在项目启动时得以确定, 并在未来项目运行中不会发生改变
 *
 * <p> 创建时间：2018/5/2
 *
 * @author guojy24
 * @version 1.0
 * */
@Slf4j @NoArgsConstructor( access = AccessLevel.PRIVATE)
public final class HostInfoUtil {

//    /**
//     * Sigar实例, 用于获取宿主机的各种配置信息
//     * */
//    private static final Sigar SIGAR = new Sigar();

    public static class Net {
        public static final String NAME = "网络配置信息";
        public static final String HOST_IP = getHostIP();
    }

    public static class Project {
        public static final String NAME = "项目信息";
        public static final String BASE_PATH = "com.tkp.tkpole";
        public static final String OWNER = "泰康养老客服平台";
        public static final String SYSTEM_NAME = "TKPCS";
        public static final String SYSTEM_NICK = "tkpole";
        public static final String DEFAULT_OPERATOR = "system";
        public static final String RUNTIME_DIR = ResourceUtil.getRuntimePath();
    }

    /**
     * 获取宿主主机的IP地址
     *
     * @return 本地IP
     * */
    private static String getHostIP() {
        String hostIP = "127.0.0.1";
        try {
            hostIP = InetAddress.getLocalHost().getHostAddress();
        } catch ( UnknownHostException e) {
            log.error( e.getMessage(), e);
            return hostIP;
        }
        return hostIP;
    }

    //todo 在这里添加其他信息
}
