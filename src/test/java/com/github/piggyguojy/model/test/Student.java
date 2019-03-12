
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
    private String name;
    private String idType;
    private String idNo;
    private LocalDate birthDate;
    private String gender;
    private String phoneNo;
    private String address;
}
