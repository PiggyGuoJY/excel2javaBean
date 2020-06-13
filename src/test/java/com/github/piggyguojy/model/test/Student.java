package com.github.piggyguojy.model.test;

import com.github.piggyguojy.parser.excel.structure.annotation.ExcelRow;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ExcelRow
public class Student {

  private String name;
  private String idType;
  private String idNo;
  private LocalDate birthDate;
  private String gender;
  private String phoneNo;
  private String address;
}
