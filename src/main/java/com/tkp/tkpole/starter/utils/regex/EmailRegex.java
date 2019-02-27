package com.tkp.tkpole.starter.utils.regex;

/**
 * 程序员（guojy24）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 *
 * <p> 创建时间：2018/7/31
 *
 * @author guojy24
 * @version 1.0
 * */
final public class EmailRegex extends AbstractRegex {

    @Override
    protected String setRegex4Match() {
        return REGEX_EMAIL;
    }

    /**
     * 匹配电子邮箱的正则表达式, 引用com.sinosoft.online.login.PatternUtil::isEmail.regex1
     * */
    private static final String REGEX_EMAIL = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
}
