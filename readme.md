# com.guojy.excel2javaBean

> excel2javaBean是一个便捷的Excel数据读取和处理工具. 工具的目标是让开发者能更加专注于业务逻辑和数据处理本身.

## 1. 快速入门
### 1.1 准备Excel文件
![Excel数据素材](https://github.com/PiggyGuoJY/execl2javaBean/blob/master/readme/CapTestFile.JPG?raw=true)
### 1.2 定义实体
```java
// 定义行数据的实体
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
// 定义复杂数据实体
@ExcelBean(sheetName = "First")
public class StudentRecordTable {
    @ExcelCell(columnName = "B", row = 3)
    private String headTeacher;
    @ExcelCell(address = "E3")
    private LocalDate recordDate;
    @ExcelRow(rowBegin = 5, rowEnd = 8,
            map = "A->name;B->idType;C->idNo;D->birthDate;E->gender;F->phoneNo;G->address")
    private List<Student> students;
}
```
### 1.3 解析和结果查看
```java

```

## 2. 高级功能


## 3. 未来规划


## 4. 附