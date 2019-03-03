package com.guojy.model.test;

import com.guojy.parser.excel.rule.structure.annotation.ExcelRow;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
@XmlRootElement @XmlAccessorType( XmlAccessType.FIELD)
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
