package com.tkp.tkpole.starter.utils.excel;

import lombok.Data;

import java.time.LocalDate;

@Data @com.tkp.tkpole.starter.utils.parser.excel.rule.structure.annotation.ExcelRow(
        sheet = 1,
        rowBegin = 3, rowEnd = 5,
        map = "J->name; K->birth")
public class ExcelRow {

    private String name;
    private LocalDate birth;
}
