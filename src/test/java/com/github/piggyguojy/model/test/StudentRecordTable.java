package com.github.piggyguojy.model.test;

import com.github.piggyguojy.parser.excel.structure.annotation.ExcelBean;
import com.github.piggyguojy.parser.excel.structure.annotation.ExcelCell;
import com.github.piggyguojy.parser.excel.structure.annotation.ExcelRow;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
@ExcelBean(sheetName = "First")
@Marked(ia = {1, 2, 3}, sa = {"a", "b", "c"})
public class StudentRecordTable {

  @ExcelCell(columnName = "B", row = 3)
  private String headTeacher;

  @ExcelCell(address = "E3")
  private LocalDate recordDate;

  @ExcelRow(rowBegin = 5, rowEnd = 8,
      map = "A->name;B->idType;C->idNo;D->birthDate;E->gender;F->phoneNo;G->address")
  private List<Student> students;
}
