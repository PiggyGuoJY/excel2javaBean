package com.guojy.regex;

/**
 * 程序员（guojy24）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 *
 * <p> 创建时间：2018/7/31
 *
 * @author guojy24
 * @version 1.0
 * */
final public class IpAddrsRegex extends AbstractRegex{

    @Override
    protected String setRegex4Match() {
        return REGEX_IPADDRS;
    }

    private static final String REGEX_IPADDRS = "^(((25[0-5]|2[0-4]\\d|[1]{1}\\d{1}\\d{1}|[1-9]{1}\\d{1}|\\d{1})\\.){3}(25[0-5]|2[0-4]\\d|[1]{1}\\d{1}\\d{1}|[1-9]{1}\\d{1}|\\d{1});?)+$";
}
