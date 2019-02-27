package com.tkp.tkpole.starter.utils.preset;

import com.tkp.tkpole.starter.utils.model.Msg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.tkp.tkpole.starter.utils.model.Msg.msg;

/**
 * 程序员（guojy24）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 * 
 * <p> 创建时间：2019/1/22
 * 
 * @author guojy24
 * @version 1.0
 * */
@ControllerAdvice
@Slf4j
public class DefaultExceptionHandler {

    /**
     * <p> 返回业务错误信息
     *
     * @param e 描述此参数的作用
     * @return 描述返回值
     * */
    @ExceptionHandler
    @ResponseBody
    public Msg<String> handleCustomerError(Exception e) { return msg(e); }
}
