package com.tkp.tkpole.starter.utils.soap;

/**
 * 程序员（guojy24）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 *
 * <p> 创建时间：2018/7/10
 *
 * @author guojy24
 * @version 1.0
 * */
@FunctionalInterface
public interface TkpoleFunction<T,R> {

    /**
     * <p> 允许抛异常的Function接口
     *
     * @param t 描述此参数的作用
     * @return 描述返回值
     * @throws Exception Exception
     * */
    R apply(T t) throws Exception;
}
