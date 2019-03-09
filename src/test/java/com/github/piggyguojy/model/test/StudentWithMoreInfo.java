package com.github.piggyguojy.model.test;

import com.github.piggyguojy.parser.excel.rule.structure.annotation.ExcelColumn;

import java.time.LocalDate;

@ExcelColumn
public class StudentWithMoreInfo {
    private String name;
    private String idType;
    private String idNo;
    private LocalDate birthDate;
    private String gender;
    private String phoneNo;
    private String address;
    private String education;
    private String marriage;
    private String page;
    private String remark;
    private String remark2;
}
