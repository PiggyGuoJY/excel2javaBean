
package com.github.piggyguojy.parser.rule.parse;

import com.github.piggyguojy.parser.rule.structure.StructureHandler;
import com.github.piggyguojy.parser.rule.type.TransformableAndRuleAddable;
import com.github.piggyguojy.util.Msg;
import com.github.piggyguojy.util.model.Params;
import com.github.piggyguojy.util.model.Processor;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.Set;
import java.util.function.Function;

import static com.github.piggyguojy.util.Msg.msg;

/**
 * 程序员（guojy）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 * 
 *
 * @author <a href="https://github.com/PiggyGuoJY" target="_blank">PiggyGuoJY</a>
 * @version 1.0
 * */
@Slf4j @NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractParser<P extends AbstractParser>
        implements TransformableAndRuleAddable<AbstractParser> , Parseable {

    /**
     * {@inheritDoc}
     */
    @Override @SuppressWarnings("unchecked")
    public <G> Msg<G> parse(
            Class<G> gClass,
            Object ... args
    ) {
        Msg<?> msg = msg(Msg.MsgError.ILLEGAL_STATE_INIT.getE());
        Params params = new Params(gClass,this,args,msg);


        for( Processor processor : processors) {
            log.debug("执行过程 {}", processor.getName());
            msg = processor.getProcessor().apply(params);
            if (msg.isException()) {
                log.warn("执行过程 {} 出错, 中断并退出流程", processor.getName());
                break;
            } else { params = new Params(gClass,this,args,msg); }
        }
        return (Msg<G>)msg;
    }
    /**
     * {@inheritDoc}
     */
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
    /**
     * {@inheritDoc}
     */
    @Override @SuppressWarnings("unchecked")
    public <G> Msg<G> transform(Object object,  Class<G> gClass) {
        return abstractDataTypeTransformerRule.transform(object, gClass);
    }

    protected StructureHandler<P> structureHandler;
    protected TransformableAndRuleAddable abstractDataTypeTransformerRule;
    protected AbstractParser(
            StructureHandler<P> structureHandler,
            TransformableAndRuleAddable abstractDataTypeTransformerRule
    ) {
        this.structureHandler = structureHandler;
        this.abstractDataTypeTransformerRule = abstractDataTypeTransformerRule;
    }


    /**
     * 解析前置处理
     *
     * @param <T> 消息泛型
     * @param params 参数
     * @return 消息
     * */
    protected abstract <T> Msg<T> beforeParse(Params params);
    /**
     * 解析处理
     *
     * @param <T> 消息泛型
     * @param params 参数
     * @return 消息
     * */
    protected abstract  <T> Msg<T> doParse(Params params);
    /**
     * 解析后置处理
     *
     * @param <T> 消息泛型
     * @param params 参数
     * @return 消息
     * */
    protected abstract <T> Msg<T> afterParse(Params params);
    @SuppressWarnings("unchecked")
    protected P addProcessorBefore(
            String processorName,
            Function<Params,Msg<?>> processor,
            String beforeProcessorName,
            boolean force
    ) {
        if ( processorName.equals(beforeProcessorName)) {
            log.warn("processorName {} 不能和 beforeProcessorName {} 相同", processorName, beforeProcessorName);
            return (P)this;
        }
        Processor beforeProcessor = new Processor(beforeProcessorName,null);
        Processor process = new Processor(processorName, processor);
        if (!processors.contains(beforeProcessor)) {
            log.warn("处理器 {} 不存在, 无法进行操作", beforeProcessorName);
            return (P)this;
        }
        boolean exist = processors.contains(process);
        processors.add(processors.indexOf(beforeProcessor), process);
        if ( exist && force) {
            processors.removeFirstOccurrence( process);
        }
        return (P)this;
    }
    protected P addProcessor(
            String processorName,
            Function<Params,Msg<?>> processor
    ) {
        return addProcessorBefore(processorName, processor, "afterParse", true);
    }
    @SuppressWarnings("unchecked")
    protected P removeProcess( String processorName) {
        if (PROCESS_CANT_BE_REMORED.contains(processorName)) {
            log.warn("处理器 {} 只能被替换,不能被移除", processorName);
            return (P)this;
        }
        if ( !processors.remove( new Processor( processorName, null))) {
            log.warn("处理器 {} 不存在, 移除失败", processorName);
        }
        return (P)this;
    }
    private final Processor beforeParse
            = new Processor("beforeParse", this::beforeParse);
    private final Processor doParse
            = new Processor("doParse", this::doParse);
    private final Processor afterParse
            = new Processor("afterParse", this::afterParse);
    private LinkedList<Processor> processors
            = new LinkedList<>(ImmutableList.of(beforeParse, doParse, afterParse));
    private static final Set<String> PROCESS_CANT_BE_REMORED
            = ImmutableSet.of("beforeParse","doParse","afterParse");
}
