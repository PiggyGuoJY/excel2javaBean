package com.tkp.tkpole.starter.utils.regex;

/**
 * 程序员（guojy24）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 *
 * <p> 创建时间：2018/7/31
 *
 * @author guojy24
 * @version 1.0
 * */
final public class PhoneNumberRegex extends AbstractRegex {

    @Override
    protected String setRegex4Match() {
        return REGEX_PHONENUMBER;
    }

    @Override
    protected String setRegex4Mask() {
        return REGEX_PHONENUMBER_MASK;
    }

    @Override
    protected String setSubstitute4Mask() {
        return REGEX_PHONENUMBER_MASK_R;
    }

    /**
     * 匹配手机号的正则表达式, 引用com.sinosoft.online.login.PatternUtil::isTelephone.regex1
     * */
    private static final String REGEX_PHONENUMBER = "^1\\d{10}$";
    private static final String REGEX_PHONENUMBER_MASK = "^(1\\d{2})\\d{4}(\\d{4})$";
    private static final String REGEX_PHONENUMBER_MASK_R = "$1****$2";
}
