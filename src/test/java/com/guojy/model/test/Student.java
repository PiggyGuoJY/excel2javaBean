package com.guojy.model.test;

import com.google.gson.annotations.Expose;
import com.guojy.gson.GsonBean;
import com.guojy.parser.excel.rule.structure.annotation.ExcelRow;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
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
