package com.tkp.tkpole.starter.utils.regex;

/**
 * 身份证号匹配类
 *
 * <p> 创建时间：2018/7/31
 *
 * @author guojy24
 * @version 1.0
 * */
final public class IdNumberRegex extends AbstractRegex {

    @Override
    protected String setRegex4Match() {
        return REGEX_IDNUMBER;
    }

    @Override
    protected String setRegex4Mask() {
        return REGEX_IDNUMBER_MASK;
    }

    @Override
    protected String setSubstitute4Mask() {
        return REGEX_IDNUMBER_MASK_R;
    }

    /**
     * 匹配身份证号的正则表达式, 引用com.sinosoft.online.login.PatternUtil::isIdentityCard.regex1
     * */
    private static final String REGEX_IDNUMBER = "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)";
    private static final String REGEX_IDNUMBER_MASK = "^\\d{3}(\\d+)\\w{4}$";
    private static final String REGEX_IDNUMBER_MASK_R = "***$1****";
}
