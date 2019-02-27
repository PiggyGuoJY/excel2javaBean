package com.tkp.tkpole.starter.utils.templates;

import com.tkp.tkpole.starter.utils.ClassUtil;
import com.tkp.tkpole.starter.utils.ExceptionUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * 切面工具基类(用于提供基础的切面特性)
 *
 * <p> 创建时间：2017/10/13
 *
 * @author guojy24
 * @version 1.0
 * */
@Slf4j @NoArgsConstructor( access = AccessLevel.PROTECTED)
public abstract class BaseAop {

    /** <p> 默认的前置通知方法(默认行为是打印调用方法时传入的参数)
     *
     * @param joinPoint 目标连接点
     * */
    protected void doBeforeAdvice( JoinPoint joinPoint) {
        this.printParametersByNameAndValue( joinPoint);
    }
    /** <p> 默认的后置通知方法(什么也不做)
     *
     * @param joinPoint 目标连接点
     * */
    protected void doAfterAdvice( JoinPoint joinPoint) { }
    /**
     * <p> 默认的返回后通知方法(打印返回值, 格式:_&acute;方法名&acute;_==&gt;_&acute;返回对象的toString()&acute;)(_表示空格)
     *
     * @param joinPoint 目标连接点
     * @param returnObj 返回的对象
     * */
    protected void doAfterReturningAdvice( JoinPoint joinPoint, Object returnObj) {
        log.info( " {} =>> {}", ClassUtil.getClassAndMethodName( joinPoint), ( returnObj==null ? "null" : returnObj.toString()));
    }
    /**
     * <p> 默认的抛出后通知方法(打印错误信息, 格式:_&acute;方法名&acute;_~~&gt;_&acute;异常对象.getMessage()&acute;)(_表示空格)
     *
     * @param joinPoint 目标连接点
     * @param t 抛出的异常
     * */
    protected void doAfterThrowingAdvice( JoinPoint joinPoint, Throwable t) {
        log.error(t.getMessage(), t);
        ExceptionUtil.printSimpleStackTrace( t, log);
    }
    /** <p> 默认的环绕方法(打印执行时间)
     *
     * @param proceedingJoinPoint 目标连接点
     * @return 描述返回值
     * @throws Throwable 当调用被代理方法发生异常事抛出
     * */
    protected Object doAroundAdvice( ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long time = System.currentTimeMillis();
        Object obj = proceedingJoinPoint.proceed();
        time = System.currentTimeMillis() - time;
        log.info( " {} 调用时长: {} ms", ClassUtil.getClassAndMethodName( proceedingJoinPoint), time);
        return obj;
    }

    //==== 华丽的分割线 === 私有资源

    /**
     * <p> 以指定格式打印调用方法时传入的参数
     * <p> 格式:_&acute;方法名&acute;_&acute;prefix&acute;&acute;参数1名称&acute;&acute;link&acute;&acute;参数1&acute;&acute;sep&acute;&acute;参数2名称&acute;&acute;link&acute;&acute;参数2&acute;&acute;sep&acute;...&acute;&acute;参数n名称&acute;&acute;link&acute;&acute;参数n&acute;&acute;suffix&acute;(_表示空格)
     *
     * @param joinPoint 目标连接点
     * */
    private void printParametersByNameAndValue( JoinPoint joinPoint) {
        Class<?> clazz = joinPoint.getTarget().getClass();
        String methodName =joinPoint.getSignature().getName();
        List<Method> methods = new LinkedList<>();
        Collections.addAll( methods , clazz.getDeclaredMethods());
        Optional<Method> methodOptional = methods.parallelStream().filter( method -> methodName.equals( method.getName())).findFirst();
        if ( methodOptional.isPresent()) {
            log.info( ClassUtil.printCallParams( methodOptional.get(), joinPoint.getArgs()));
        } else { log.warn( " 未能在类 {} 中找到方法 {}", clazz.getSimpleName(), methodName); }
    }

}
