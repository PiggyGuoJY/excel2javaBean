package com.github.piggyguojy.parser.excel;

import com.github.piggyguojy.util.Msg;
import com.github.piggyguojy.parser.excel.parse.ExcelParser;
import com.github.piggyguojy.parser.excel.parse.XlsExcelParser;
import com.github.piggyguojy.parser.excel.parse.XlsxExcelParser;
import com.github.piggyguojy.parser.excel.structure.annotation.handler.ExcelAnnotationHandler;
import com.github.piggyguojy.parser.excel.type.ExcelTransformerRule;
import com.github.piggyguojy.parser.excel.type.ExcelTransformerRuleTypeAdvanced;
import com.github.piggyguojy.parser.rule.structure.StructureHandler;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.github.piggyguojy.util.Assert.*;
import static com.github.piggyguojy.util.Msg.msg;
import static java.lang.String.format;

/**
 * Excel解析器构造工厂
 * <p>
 *      该工厂主要用于根据指定的参数生成合适的excel解析器, 出错时给出错误原因
 *
 * @author <a href="https://github.com/PiggyGuoJY" target="_blank">PiggyGuoJY</a>
 * @version 1.0
 *
 * */
@Slf4j @NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExcelParserFactory {

    /**
     * <p> 程序员（guojy）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
     *
     * @param path 文件路径
     * @return 描述返回值
     * */
    public static Msg<ExcelParser> createParser(Path path) {
        return builder().setPath(path).build();
    }

    /**
     * 使用工厂自定义Excel解析器
     * @return Excel解析器构建器
     */
    public static ExcelParserBuilder builder() {
        return new ExcelParserBuilder();
    }

    /**
     * Excel解析器构建器
     *
     * @author <a href="https://github.com/PiggyGuoJY" target="_blank">PiggyGuoJY</a>
     * @version 1.0
     *
     * */
    public static class ExcelParserBuilder {

        /**
         * 指定路径
         *
         * @param path 路径
         * @return Excel解析器构建器
         */
        public ExcelParserBuilder setPath(String path) {
            if (excelParserMsg.isException()) { return this; }
            if (isNul(path)) {
                excelParserMsg = msg( new IllegalStateException("参数 path 不能为null或长度为0."));
                return this;
            }
            File file4path = new File(path);
            if (!(file4path.exists()&&
                    file4path.isFile()&&
                    file4path.canRead()&&
                    FilenameUtils.getExtension(file4path.getName()).matches("xlsx?"))) {
                excelParserMsg = msg( new IllegalStateException(format("指定的文件[文件名:%s]不存在,不是文件,不可读或不是xlsx?格式",path)));
                return this;
            }
            this.file = file4path; this.path = null;
            return this;
        }

        /**
         * 指定路径
         *
         * @param path 路径
         * @return Excel解析器构建器
         */
        public ExcelParserBuilder setPath(Path path) {
            if (excelParserMsg.isException()) { return this; }
            if (isNull(path)) {
                excelParserMsg = msg(Msg.MsgError.ILLEGAL_ARGS_NULL.getE());
                return this;
            }
            if (!(Files.exists(path)&&
                    Files.isRegularFile(path)&&
                    Files.isRegularFile(path))) {
                excelParserMsg = msg( new IllegalStateException(format("指定的文件[文件名:%s]不存在,不是文件,不可读或不是xlsx?格式",path)));
                return this;
            }
            this.path = path; this.file = null;
            return this;
        }

        /**
         * 使用自定义处理器
         *
         * @param <T> StructureHandler&lt;ExcelParser&gt;子类的泛型
         * @param t 自定义处理器
         * @return Excel解析器构建器
         */
        public <T extends StructureHandler<ExcelParser>> ExcelParserBuilder setHandler(T t) {
            if (excelParserMsg.isException()) { return this; }
            if(isNull(excelParserStructureHandler)) {
                excelParserMsg = msg(Msg.MsgError.ILLEGAL_ARGS_NULL.getE());
            }
            excelParserStructureHandler = t;
            return this;
        }

        /**
         * 使用自定义的转换器
         *
         * @param <T> ExcelTransformerRule子类的泛型
         * @param t 自定义转换器
         * @return Excel解析器构建器
         */
        public <T extends ExcelTransformerRule> ExcelParserBuilder setExcelTransformerRule(T t) {
            if (excelParserMsg.isException()) { return this; }
            if(isNull(excelTransformerRule)) {
                excelParserMsg = msg(Msg.MsgError.ILLEGAL_ARGS_NULL.getE());
            }
            excelTransformerRule = t;
            return this;
        }

        /**
         * 构建解析器
         * @return 解析器消息
         */
        public Msg<ExcelParser> build() {
            if (excelParserMsg.isException()) { return excelParserMsg; }
            excelParserMsg = msg(Msg.MsgError.ILLEGAL_STATE_INIT.getE());
            // 1. 判断文件大小
            if ( notNull(file)) {
                long size = this.file.length();
                switch (FilenameUtils.getExtension(file.getName()).toLowerCase()) {
                    case "xls":
                        excelParserMsg = msg(new XlsExcelParser(
                                file,
                                decideExcelParserStructureHandler(size),
                                decideExcelTransformerRule()));
                        break;
                    case "xlsx":
                        excelParserMsg = msg(new XlsxExcelParser(
                                file,
                                decideExcelParserStructureHandler(size),
                                decideExcelTransformerRule()));
                        break;
                    default:
                        excelParserMsg = msg(Msg.MsgError.ILLEGAL_STATE_SEGMENT_SHOULD_NOT_BE_ACCESSED.getE());
                }
            }

            if (notNull(path)){
                long size;
                try {
                    size = Files.size(this.path);
                } catch (IOException e) {
                    log.error(e.getMessage(),e);
                    return msg(new IOException("获取文件大小时发生异常"));
                }
                // 未来这里可能会根据文件大小更换处理器
                log.debug("文件[文件名:{}]大小:{}",path.getFileName(),size);
                switch (FilenameUtils.getExtension(path.toString()).toLowerCase()) {
                    case "xls":
                        this.excelParserMsg = msg(new XlsExcelParser(
                                path,
                                decideExcelParserStructureHandler(size),
                                decideExcelTransformerRule()));
                        break;
                    case "xlsx":
                        excelParserMsg = msg(new XlsxExcelParser(
                                path,
                                decideExcelParserStructureHandler(size),
                                decideExcelTransformerRule()));
                        break;
                    default:
                        excelParserMsg = msg(Msg.MsgError.ILLEGAL_STATE_SEGMENT_SHOULD_NOT_BE_ACCESSED.getE());
                }
            }
            return excelParserMsg;
        }



        /**
         * 解析器消息
         */
        @Getter(AccessLevel.PROTECTED)
        private Msg<ExcelParser> excelParserMsg = msg();
        /**
         * 路径
         */
        @Getter(AccessLevel.PROTECTED)
        private Path path;
        /**
         * 文件
         */
        @Getter(AccessLevel.PROTECTED)
        private File file;
        /**
         * 指令处理器(默认使用注解指令处理器)
         */
        @Getter(AccessLevel.PROTECTED)
        private StructureHandler<ExcelParser> excelParserStructureHandler;
        /**
         * 转换规则(默认使用内建规则)
         */
        @Getter(AccessLevel.PROTECTED)
        private ExcelTransformerRule excelTransformerRule;

        /**
         * 确定要使用的处理器
         * @param fileSize 文件大小
         * @return 处理器
         */
        private StructureHandler<ExcelParser> decideExcelParserStructureHandler(long fileSize) {
            //todo 未来这里可能会根据文件大小更换处理器
            log.debug("文件大小:{}",fileSize);
            return notNull(excelParserStructureHandler)?excelParserStructureHandler:ExcelAnnotationHandler.of();
        }

        /**
         * 确定要使用的类型转换器
         * @return 类型转换器
         */
        private ExcelTransformerRule decideExcelTransformerRule() {
            return notNull(excelTransformerRule)?excelTransformerRule:ExcelTransformerRuleTypeAdvanced.of();
        }
    }
}