
package com.github.piggyguojy.model.test;

import com.github.piggyguojy.parser.excel.rule.structure.annotation.ExcelBean;
import com.github.piggyguojy.parser.excel.rule.structure.annotation.ExcelCell;
import com.github.piggyguojy.parser.excel.rule.structure.annotation.ExcelColumn;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@ExcelBean(sheet = 2)
public class StudentRecordTable2 {

    @ExcelCell(columnName = "B", row = 3)
    private String headTeacher;

    @ExcelCell(address = "E3")
    private LocalDate recordDate;

    @ExcelColumn(columnNameBegin = "B", columnNameEnd = "E",
            map = "4->name;5->idType;6->idNo;7->birthDate;8->gender;9->phoneNo;10->address;11->education;12->marriage;13->page;14->remark;15->remark2")
    private List<StudentWithMoreInfo> studentWithMoreInfos;
}
