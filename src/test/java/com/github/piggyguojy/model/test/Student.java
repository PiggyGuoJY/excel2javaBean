/* Copyright (c) 2019, Guo Jinyang. All rights reserved. */
package com.github.piggyguojy.model.test;

import com.github.piggyguojy.parser.excel.rule.structure.annotation.ExcelRow;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlElement;
import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
@ExcelRow
public class Student {
    @XmlElement
    private String name;
    @XmlElement
    private String idType;
    @XmlElement
    private String idNo;
    @XmlElement
    private LocalDate birthDate;
    @XmlElement
    private String gender;
    @XmlElement
    private String phoneNo;
    @XmlElement
    private String address;
}
