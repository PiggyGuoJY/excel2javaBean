package com.tkp.tkpole.starter.utils.templates;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.tkp.tkpole.starter.utils.Assert.notNul;
import static com.tkp.tkpole.starter.utils.Assert.notNull;

/**
 * 程序员（guojy24）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 *
 * <p> 创建时间：2019/2/15
 *
 * @author guojy24
 * @version 1.0
 * */
@Component
@Slf4j
public class BeanPostProcessorImpl implements BeanPostProcessor, Ordered {

    @Override
    @SuppressWarnings("unchecked")
    public Object postProcessBeforeInitialization( Object bean, String beanName) {
        if ( !quickFailure4PostProcessBeforeInitialization) {
            Class clazz = PROCESSOR_MAP_BEFORE_INITIALIZATION.get( bean.getClass());
            if ( notNull( clazz)) {
                log.info( "开始使用自定义配置处理Bean {}, 当前配置 {}", beanName, bean.toString());
                Object postBean = ( ( ConfigDataBeanPostProcessor) BeanUtils.instantiate( clazz)).process( bean);
                log.info( "Bean {}已使用自定义配置处理完成, 当前配置", beanName, postBean.toString());
                return postBean;
            } else {
                log.warn( " 关于bean {}, 没有找到关于Bean类型 {} 的自定义配置", beanName, clazz.getName());
                return bean;
            }
        } else {
            log.warn( " 没有关于Bean的自定义配置");
            return bean;
        }
    }
    @Override
    @SuppressWarnings("unchecked")
    public Object postProcessAfterInitialization( Object bean, String beanName) {
        if ( !quickFailure4postProcessAfterInitialization) {
            Class clazz = PROCESSOR_MAP_AFTER_INITIALIZATION.get( bean.getClass());
            if ( notNull( clazz)) {
                log.info( "Bean({})处理 - Bean初始化后 ......... 开始", beanName);
                log.debug( "Bean({})数据: {}", beanName, bean.toString());
                Object postBean = ( ( ConfigDataBeanPostProcessor)BeanUtils.instantiate( clazz)).process( bean);
                log.info( "Bean({})处理 - Bean初始化后 ......... 结束", beanName);
                log.debug( "Bean({})数据: {}", beanName, bean.toString());
                return postBean;
            } else { return bean; }
        } else { return bean; }
    }
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    //==== 华丽的分割线 ==== 私有资源

    /**
     * 程序员（guojy24）很懒，关于这个属性，ta什么也没写╮(╯▽╰)╭
     * */
    private static final Map<Class<?>, Class<? extends ConfigDataBeanPostProcessor>> PROCESSOR_MAP_BEFORE_INITIALIZATION = new HashMap<>();
    /**
     * 程序员（guojy24）很懒，关于这个属性，ta什么也没写╮(╯▽╰)╭
     * */
    private static final Map<Class<?>, Class<? extends ConfigDataBeanPostProcessor>> PROCESSOR_MAP_AFTER_INITIALIZATION = new HashMap<>();
    private static boolean quickFailure4PostProcessBeforeInitialization = true;
    private static boolean quickFailure4postProcessAfterInitialization = true;

    static {

        //todo... 这里按需添加

        quickFailure4PostProcessBeforeInitialization = !notNul( PROCESSOR_MAP_BEFORE_INITIALIZATION);
        quickFailure4postProcessAfterInitialization = !notNul( PROCESSOR_MAP_AFTER_INITIALIZATION);
    }

    /**
     * 程序员（guojy24）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
     *
     * <p> 创建时间：2018/12/17
     *
     * @author guojy24
     * @version 1.0
     * */
    public static interface ConfigDataBeanPostProcessor<T> {
        /**
         * <p> 程序员（guojy24）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
         *
         * @param t 描述此参数的作用
         * @return 描述返回值
         * */
        T process(T t);
    }

}
