/* Copyright (c) 2019, Guo Jinyang. All rights reserved. */
package com.github.piggyguojy.parser.excel.rule.parse;

import com.github.piggyguojy.parser.rule.structure.StructureHandler;
import com.github.piggyguojy.parser.rule.type.TransformableAndRuleAddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Path;

/**
 * 程序员（guojy）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 *
 * <p> 创建时间：2018/11/9
 *
 * @author <a href="https://github.com/PiggyGuoJY" target="_blank">PiggyGuoJY</a>
 * @version 1.0
 * */
@Slf4j @NoArgsConstructor( access = AccessLevel.PRIVATE)
public class XlsxExcelParser extends ExcelParser {

    public XlsxExcelParser(
            Path path,
            StructureHandler<ExcelParser> structureHandler,
            TransformableAndRuleAddable abstractDataTypeTransformerRule
    ) {
        super(path, structureHandler, abstractDataTypeTransformerRule);
    }
    public XlsxExcelParser(
            File file,
            StructureHandler<ExcelParser> structureHandler,
            TransformableAndRuleAddable abstractDataTypeTransformerRule
    ) {
        super(file, structureHandler, abstractDataTypeTransformerRule);
    }
}