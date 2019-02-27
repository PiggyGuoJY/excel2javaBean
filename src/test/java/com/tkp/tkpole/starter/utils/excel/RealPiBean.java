package com.tkp.tkpole.starter.utils.excel;

import com.tkp.tkpole.starter.utils.parser.excel.rule.structure.annotation.ExcelRow;
import lombok.Data;

//@ExcelRow( rowBegin = 2, columnBegin = 1, columnNameBegin = "D")
@Data
public class RealPiBean {

    private String order;
    private String state;
    private String name;
    private String $Null;
    private String relation;
    private String idType;
    private String idNo;
    private String birth;
    private String sex;
    private String plan;
    private String job;
    private String job2;
    private String jobDesc;
    private String tax;
}
