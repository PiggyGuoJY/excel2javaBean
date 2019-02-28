package com.guojy.parser.rule.parse;

import com.guojy.model.Msg;
import com.guojy.parser.rule.structure.StructureHandler;
import com.guojy.parser.rule.type.AbstractDataTypeTransformerRule;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.function.Function;

import static com.guojy.Assert.notNul;

/**
 * 程序员（guojy24）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 * 
 * <p> 创建时间：2019/2/18
 * 
 * @author guojy24
 * @version 1.0
 * */
@Slf4j @NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractParser<P extends AbstractParser> extends AbstractDataTypeTransformerRule<AbstractParser> implements Parseable {

    // 1. 组合公共资源

    protected AbstractParser(
            StructureHandler<P> structureHandler,
            AbstractDataTypeTransformerRule abstractDataTypeTransformerRule) {
        this.structureHandler = structureHandler;
        this.abstractDataTypeTransformerRule = abstractDataTypeTransformerRule;
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE) @EqualsAndHashCode(exclude = {"processor"}) @ToString(of = {"name"})
    private static class Process {
        private String name;
        private Function<Object[],Msg<?>> processor;
    }

    protected StructureHandler<P> structureHandler;
    protected AbstractDataTypeTransformerRule abstractDataTypeTransformerRule;

    private LinkedList<Process> processes = new LinkedList<>();
    {
        processes.addLast(new Process("beforeParse", this::beforeParse));
        processes.addLast(new Process("doParse", this::doParse));
        processes.addLast(new Process("afterParse", this::afterParse));
    }

    // 2. 暴露API

    /**
     * 解析入口
     *
     * @param <G> 目标类型
     * @param gClass 目标类类型
     * @param args 附加参数
     * @return 含有目标实例的消息
     * */
    @Override @SuppressWarnings("unchecked")
    public <G> Msg<G> parse(Class<G> gClass, Object ... args) {
        Msg<?> msg = Msg.MsgError.IllegalState_INIT.getMsg();
        Object[] params = new Object[]{gClass,this,args,msg};
        for( Process process : processes) {
            log.info("执行过程 {}", process.name);
            msg = process.processor.apply(params);
            if (msg.isException()) {
                log.warn("执行过程 {} 出错, 中断流程", process.name);
                break;
            } else { params = new Object[]{gClass,this,args,msg}; }
        }
        return (Msg<G>)msg;
    }

    @Override @SuppressWarnings("unchecked")
    public <G> AbstractParser addCustomerDataTypeTransformRule(
            @NonNull Class<G> gClass, @NonNull Class zlass, @NonNull Function<?, G> ogFunction) {
        abstractDataTypeTransformerRule = abstractDataTypeTransformerRule.addCustomerDataTypeTransformRule(gClass, zlass, ogFunction);
        return this;
    }

    @Override @SuppressWarnings("unchecked")
    public <G> Msg<G> transform(Object object, @NonNull Class<G> gClass) {
        return abstractDataTypeTransformerRule.transform(object,gClass);
    }

    @SuppressWarnings("unchecked")
    public P addProcessorBefore(
            @NonNull String processorName,
            @NonNull Function<Object[],Msg<?>> processor,
            String beforeProcessorName,
            boolean force) {
        if ( processorName.equals(beforeProcessorName)) {
            log.warn("processorName {} 不能和 beforeProcessorName {}", processorName, beforeProcessorName);
            return (P)this;
        }
        Process beforeProcess = new Process(beforeProcessorName,null);
        Process process = new Process(processorName, processor);
        boolean forceFlag = processes.contains(process) && force;
        if ( notNul(beforeProcessorName) && processes.contains(beforeProcess)) {
            processes.add(processes.indexOf(beforeProcess), process);
            if (forceFlag) {
                processes.removeLastOccurrence( process);
            } else {
                log.warn("未指定强制替换, 不予添加和替换处理器");
                return (P)this;
            }
        } else {
            processes.addLast(process);
            if ( forceFlag) {
                processes.removeFirstOccurrence( process);
            } else {
                log.warn("未指定强制替换, 不予添加和替换处理器");
                return (P)this;
            }
        }
        return (P)this;
    }

    public P addProcessor(@NonNull String processorName, @NonNull Function<Object[],Msg<?>> processor) {
        return addProcessorBefore(processorName, processor, null, false);
    }

    @SuppressWarnings("unchecked")
    public P removeProcess(@NonNull String processorName) {
        if ( !processes.remove( new Process( processorName, null))) { log.warn("处理器 {} 不存在", processorName);}
        return (P)this;
    }

    // 3. 提供公共工具

    protected static final int GOAL_CLASS = 0;
    protected static final int PARSER_SELF = 1;
    protected static final int ARGS_INIT = 2;
    protected static final int VALUE_RETURNED = 3;

    /**
     * 解析前置处理
     *
     * */
    protected abstract <T> Msg<T> beforeParse(Object... args);
    /**
     * 解析处理
     *
     * */
    protected abstract  <T> Msg<T> doParse(Object ... args);
    /**
     * 解析后置处理
     *
     * */
    protected abstract <T> Msg<T> afterParse(Object... args);
}
