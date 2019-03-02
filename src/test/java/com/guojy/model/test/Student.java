package com.guojy.model.test;

import com.google.gson.annotations.Expose;
import com.guojy.gson.GsonBean;
import com.guojy.parser.excel.rule.structure.annotation.ExcelRow;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
@GsonBean
@ExcelRow
public class Student {

    @Expose private String name;

    @Expose private String idType;

    @Expose private String idNo;

    @Expose private LocalDate birthDate;

    @Expose private String gender;

    @Expose private String phoneNo;

    @Expose private String address;
}
