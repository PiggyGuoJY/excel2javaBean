package com.guojy.model.test;

import com.guojy.parser.excel.rule.structure.annotation.ExcelBean;
import com.guojy.parser.excel.rule.structure.annotation.ExcelCell;
import com.guojy.parser.excel.rule.structure.annotation.ExcelRow;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@ExcelBean(sheetName = "First")
public class StudentRecordTable {

    @ExcelCell(columnName = "B", row = 3)
    private String headTeacher;

    @ExcelCell(address = "E3")
    private LocalDate recordDate;

    @ExcelRow(rowBegin = 5, rowEnd = 8,
            map = "A->name;B->idType;C->idNo;D->birthDate;E->gender;F->phoneNo;G->address")
    private List<Student> students;
}
