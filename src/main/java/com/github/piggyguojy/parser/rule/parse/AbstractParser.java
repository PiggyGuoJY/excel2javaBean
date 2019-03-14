
package com.github.piggyguojy.parser.rule.parse;

import com.github.piggyguojy.Msg;
import com.github.piggyguojy.parser.rule.structure.StructureHandler;
import com.github.piggyguojy.parser.rule.type.TransformableAndRuleAddable;
import com.google.common.collect.ImmutableSet;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.Set;
import java.util.function.Function;

import static com.github.piggyguojy.Msg.msg;

/**
 * 程序员（guojy）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 * 
 * <p> 创建时间：2019/2/18
 * 
 * @author <a href="https://github.com/PiggyGuoJY" target="_blank">PiggyGuoJY</a>
 * @version 1.0
 * */
@Slf4j @NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractParser<P extends AbstractParser>
        implements TransformableAndRuleAddable<AbstractParser> , Parseable {

    /**
     * 解析入口
     *
     * @param <G> 目标类型
     * @param gClass 目标类类型
     * @param args 附加参数
     * @return 含有目标实例的消息
     * */
    @Override @SuppressWarnings("unchecked")
    public <G> Msg<G> parse(
            Class<G> gClass,
            Object ... args
    ) {
        Msg<?> msg = msg(Msg.MsgError.ILLEGAL_STATE_INIT.getE());
        Object[] params = new Object[]{gClass,this,args,msg};
        for( Process process : processes) {
            log.debug("执行过程 {}", process.name);
            msg = process.processor.apply(params);
            if (msg.isException()) {
                log.warn("执行过程 {} 出错, 中断流程", process.name);
                break;
            } else { params = new Object[]{gClass,this,args,msg}; }
        }
        return (Msg<G>)msg;
    }
    @Override @SuppressWarnings("unchecked")
    public <G> AbstractParser addRule4Transformer(
            Class<G> gClass,
            Class zlass,
            Function<?, G> ogFunction
    ) {
        abstractDataTypeTransformerRule =
                abstractDataTypeTransformerRule.addRule4Transformer(gClass, zlass, ogFunction);
        return this;
    }
    @Override @SuppressWarnings("unchecked")
    public <G> Msg<G> transform(Object object,  Class<G> gClass) {
        return abstractDataTypeTransformerRule.transform(object,gClass);
    }
    @SuppressWarnings("unchecked")
    public P addProcessorBefore(
            String processorName,
            Function<Object[],Msg<?>> processor,
            String beforeProcessorName,
            boolean force
    ) {
        if ( processorName.equals(beforeProcessorName)) {
            log.warn("processorName {} 不能和 beforeProcessorName {} 相同", processorName, beforeProcessorName);
            return (P)this;
        }
        Process beforeProcess = new Process(beforeProcessorName,null);
        Process process = new Process(processorName, processor);
        if (!processes.contains(beforeProcess)) {
            log.warn("处理器 {} 不存在", beforeProcessorName);
            return (P)this;
        }
        boolean exist = processes.contains(process);
        processes.add(processes.indexOf(beforeProcess), process);
        if ( exist && force) {
            processes.removeFirstOccurrence( process);
        }
        return (P)this;
    }
    public P addProcessor(
            String processorName,
            Function<Object[],Msg<?>> processor
    ) {
        return addProcessorBefore(processorName, processor, null, false);
    }
    @SuppressWarnings("unchecked")
    public P removeProcess( String processorName) {
        if (PROCESS_CANT_BE_REMORED.contains(processorName)) {
            log.error("处理器 {} 只能被替换,不能被移除", processorName);
            return (P)this;
        }
        if ( !processes.remove( new Process( processorName, null))) {
            log.warn("处理器 {} 不存在", processorName);
        }
        return (P)this;
    }



    protected static final int GOAL_CLASS = 0;
    protected static final int PARSER_SELF = 1;
    protected static final int ARGS_INIT = 2;
    protected static final int VALUE_RETURNED = 3;
    /**
     * 解析前置处理
     *
     * @param <T> 消息泛型
     * @param args 参数
     * @return 消息
     * */
    protected abstract <T> Msg<T> beforeParse(Object... args);
    /**
     * 解析处理
     *
     * @param <T> 消息泛型
     * @param args 参数
     * @return 消息
     * */
    protected abstract  <T> Msg<T> doParse(Object ... args);
    /**
     * 解析后置处理
     *
     * @param <T> 消息泛型
     * @param args 参数
     * @return 消息
     * */
    protected abstract <T> Msg<T> afterParse(Object... args);
    protected StructureHandler<P> structureHandler;
    protected TransformableAndRuleAddable abstractDataTypeTransformerRule;
    protected AbstractParser(
            StructureHandler<P> structureHandler,
            TransformableAndRuleAddable abstractDataTypeTransformerRule
    ) {
        this.structureHandler = structureHandler;
        this.abstractDataTypeTransformerRule = abstractDataTypeTransformerRule;
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode(exclude = {"processor"}) @ToString(of = {"name"})
    private static class Process {
        private String name;
        private Function<Object[],Msg<?>> processor;
    }
    private LinkedList<Process> processes = new LinkedList<>();
    private static final Set<String> PROCESS_CANT_BE_REMORED
            = ImmutableSet.<String>builder().add("beforeParse").add("doParse").add("afterParse").build();
    {
        processes.addLast(new Process("beforeParse", this::beforeParse));
        processes.addLast(new Process("doParse", this::doParse));
        processes.addLast(new Process("afterParse", this::afterParse));
    }
}
