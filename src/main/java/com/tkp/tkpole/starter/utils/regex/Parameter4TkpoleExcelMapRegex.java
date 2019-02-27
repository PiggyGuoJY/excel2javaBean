package com.tkp.tkpole.starter.utils.regex;

/**
 * 程序员（guojy24）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 *
 * <p> 创建时间：2019/2/15
 *
 * @author guojy24
 * @version 1.0
 * */
public class Parameter4TkpoleExcelMapRegex extends AbstractRegex {

    @Override
    protected String setRegex4Match() {
        return REGEX_Parameter4TkpoleExcelMap;
    }

    private static final String REGEX_Parameter4TkpoleExcelMap = "(^(?!\\d)(([A-Z]+)->[_$a-zA-Z0-9]+;)*(([A-Z]+)->[_$a-zA-Z0-9]+(?=$))$)|(^(?=\\d)(([0-9]+)->[_$a-zA-Z0-9]+;)*(([0-9]+)->[_$a-zA-Z0-9]+(?=$))$)";

}
