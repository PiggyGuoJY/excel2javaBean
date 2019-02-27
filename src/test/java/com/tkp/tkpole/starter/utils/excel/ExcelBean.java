package com.tkp.tkpole.starter.utils.excel;

import com.tkp.tkpole.starter.utils.gson.TkpoleGsonBean;
import com.tkp.tkpole.starter.utils.parser.excel.rule.structure.annotation.ExcelCell;
import com.tkp.tkpole.starter.utils.parser.excel.rule.structure.annotation.ExcelRow;
import com.tkp.tkpole.starter.utils.parser.rule.structure.OverrideRule;
import lombok.Data;

import java.util.List;

@TkpoleGsonBean
@com.tkp.tkpole.starter.utils.parser.excel.rule.structure.annotation.ExcelBean
        (sheet = 1, isNestable = true)
@Data
public class ExcelBean {

    @ExcelCell( sheet = 2, address = "A1")
    private String author;

    @ExcelCell( row = 2, column = 2)
    private String title;

    @ExcelRow(
            map = "J->name; K->birth", overideRule = OverrideRule.SON_FORCE)
    private List<com.tkp.tkpole.starter.utils.excel.ExcelRow> excelRowList;

    @com.tkp.tkpole.starter.utils.parser.excel.rule.structure.annotation.ExcelColumn( columnNameBegin = "C", columnNameEnd = "G",
            map = "8->name; 9->birth")
    private List<com.tkp.tkpole.starter.utils.excel.ExcelColumn> excelColumnList;

    @com.tkp.tkpole.starter.utils.parser.excel.rule.structure.annotation.ExcelBean.Nested(stepBy = 1)
    private ExcelBean excelBean;
}