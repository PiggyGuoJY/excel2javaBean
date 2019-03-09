# excel2javaBean

![](https://img.shields.io/badge/language-java-red.svg)
![](https://img.shields.io/maven-central/v/com.github.piggyguojy/excel2javaBean.svg)
![](https://img.shields.io/github/license/PiggyGuoJY/excel2javaBean.svg)
> excel2javaBean是一个便捷的Excel数据读取和处理工具. 工具的目标是让开发者能更加专注于业务逻辑和数据处理本身.

## 目录
- [1. 快速入门](#1-快速入门)
    - [1.1 准备Excel文件](#1.1-准备excel文件)
    - [1.2 定义实体](#1.2-定义实体)
    - [1.3 定义实体](#1.3-解析和结果查看)
    - [1.4 另一种实体定义对应的文件,-实体定义和解析结果](#1.4-另一种实体定义对应的文件,-实体定义和解析结果)
- [2. 更多功能](#2-更多功能)
    - [2.1 注解和类的简单组合使用](#2.1-注解和类的简单组合使用)
    - [2.2 更加方便的地址属性绑定方法](#2.2-更加方便的地址属性绑定方法)
    - [2.3 自定义类型转换规则](#2.3-自定义类型转换规则)
    - [2.4 使用嵌套注解](#2.4-使用嵌套注解)
    - [2.5 使用其他继承规则](#2.5-使用其他继承规则)
- [3. 附](#3-附)

---
## 1. 快速入门 
> 这部分我们主要简单了解@ExcelBean,@ExcelCell, @ExcelColumn和@ExcelRow注解的使用
### 1.1 准备Excel文件
![Excel数据素材](https://github.com/PiggyGuoJY/excel2javaBean/blob/master/readme/CapTestFile.JPG?raw=true)
### 1.2 定义实体
<details>
<summary>代码较多, 点击查看</summary>

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
</details>

### 1.3 解析和结果查看
<details>
<summary>代码较多, 点击查看</summary>

```java
@Slf4j
public class ExcelParserFactoryTest {
    private static Path TEST_FILE;
    @BeforeClass @SneakyThrows
    public static void beforeOnce() {
        TEST_FILE = Paths.get(ExcelParserFactoryTest.class.getResource("/simple/TestExcelFile.xlsx").toURI());
    }
    @Test
    public void test() {
        Msg<ExcelParser> excelParserMsg = ExcelParserFactory.createParser(TEST_FILE);
        assertFalse(excelParserMsg.isException());
        ExcelParser excelParser = excelParserMsg.getT();
        Msg<StudentRecordTable> studentRecordTableMsg = excelParser.parse(StudentRecordTable.class);
        assertFalse(studentRecordTableMsg.isException());
        log.info(JsonXmlUtil.javaBean2Json(studentRecordTableMsg.getT()));
    }

}
```

```json
{
	"headTeacher":"多馨兰",
	"recordDate":{"year":2019,"month":1,"day":1},
	"students":[
		{
			"name":"富和美",
			"idType":"身份证",
			"idNo":"1.1010119900307E17",
			"birthDate":{"year":1990,"month":3,"day":7},
			"gender":"男",
			"phoneNo":"1.2345678901E10",
			"address":"北京市东城区"
		},{
			"name":"倪千凡",
			"idType":"身份证",
			"idNo":"3.3060420040601498E17",
			"birthDate":{"year":2004,"month":6,"day":1},
			"gender":"男",
			"phoneNo":"1.2345678902E10",
			"address":"浙江省绍兴市"
		},{
			"name":"逢向露",
			"idType":"身份证",
			"idNo":"6.2010219940512704E17",
			"birthDate":{"year":1994,"month":5,"day":12},
			"gender":"女",
			"phoneNo":"1.2345678903E10",
			"address":"甘肃省兰州市"
		},{
			"name":"刁颖初",
			"idType":"身份证",
			"idNo":"1.20101201601014E17",
			"birthDate":{"year":2016,"month":1,"day":1},
			"gender":"女",
			"phoneNo":"1.2345678904E10",
			"address":"天津市和平区"
		}
	]
}
```
</details>

### 1.4 另一种实体定义对应的文件, 实体定义和解析结果
![Excel数据素材](https://github.com/PiggyGuoJY/excel2javaBean/blob/master/readme/CapTestFile2.JPG?raw=true)
<details>
<summary>代码较多, 点击查看</summary>

```java
// 定义列数据的实体
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
// 定义复杂数据实体
@Data
@ExcelBean(sheet = 2)
public class StudentRecordTable2 {
    @ExcelCell(columnName = "B", row = 3)
    private String headTeacher;
    @ExcelCell(address = "E3")
    private LocalDate recordDate;
    @ExcelColumn(columnNameBegin = "B", columnNameEnd = "E",
            map = "4->name;5->idType;6->idNo;7->birthDate;8->gender;9->phoneNo;10->address;11->education;12->marriage;13->page;14->remark;15->remark2")
    private List<StudentWithMoreInfo> studentWithMoreInfos;
}
```
```java
// 测试代码
@Slf4j
public class ExcelParserFactoryTest {
    private static Path TEST_FILE;
    @BeforeClass @SneakyThrows
    public static void beforeOnce() {
        TEST_FILE = Paths.get(ExcelParserFactoryTest.class.getResource("/simple/TestExcelFile.xlsx").toURI());
    }
    @Test
    public void test2() {
        Msg<ExcelParser> excelParserMsg = ExcelParserFactory.createParser(TEST_FILE);
        assertFalse(excelParserMsg.isException());
        ExcelParser excelParser = excelParserMsg.getT();
        Msg<StudentRecordTable2> studentRecordTable2Msg = excelParser.parse(StudentRecordTable2.class);
        assertFalse(studentRecordTable2Msg.isException());
        log.info(JsonUtil.javaBean2Json(studentRecordTable2Msg.getT()));
    }
}
```

```json
{
	"headTeacher":"多馨兰",
	"recordDate":{"year":2019,"month":1,"day":1},
	"studentWithMoreInfos":[
		{
			"name":"富和美",
			"idType":"身份证",
			"idNo":"1.1010119900307E17",
			"birthDate":{"year":1990,"month":3,"day":7},
			"gender":"男",
			"phoneNo":"1.2345678901E10",
			"address":"北京市东城区",
			"education":"本科",
			"marriage":"是",
			"page":"https://github.com/PiggyGuoJY/FuHM",
			"remark":"南国有佳人",
			"remark2":"家山何处"
		},{
			"name":"倪千凡",
			"idType":"身份证",
			"idNo":"3.3060420040601498E17",
			"birthDate":{"year":2004,"month":6,"day":1},
			"gender":"男",
			"phoneNo":"1.2345678902E10",
			"address":"浙江省绍兴市",
			"education":"高中",
			"marriage":"否",
			"page":"https://github.com/PiggyGuoJY/NiQF",
			"remark":"香雾冷风残",
			"remark2":"闻君有两意"
		},{
			"name":"逢向露",
			"idType":"身份证",
			"idNo":"6.2010219940512704E17",
			"birthDate":{"year":1994,"month":5,"day":12},
			"gender":"女",
			"phoneNo":"1.2345678903E10",
			"address":"甘肃省兰州市",
			"education":"硕士",
			"marriage":"是",
			"page":"https://github.com/PiggyGuoJY/FengXL",
			"remark":"云山乱,晓山青",
			"remark2":"白首卧松云"
		},{
			"name":"刁颖初",
			"idType":"身份证",
			"idNo":"1.20101201601014E17",
			"birthDate":{"year":2016,"month":1,"day":1},
			"gender":"女",
			"phoneNo":"1.2345678904E10",
			"address":"天津市和平区",
			"education":"研究生",
			"marriage":"否",
			"page":"https://github.com/PiggyGuoJY/DiaoYC",
			"remark":"荷花羞玉颜",
			"remark2":"香非在蕊"
		}
	]
}
```
</details>

- [回到目录](#目录)
## 2. 更多功能 
### 2.1 注解和类的简单组合使用
`建设中...`
### 2.2 更加方便的地址属性绑定方法
`建设中...`
### 2.3 自定义类型转换规则
`建设中...`
### 2.4 使用嵌套注解
`建设中...`
### 2.5 使用其他继承规则
`建设中...`
- [回到目录](#目录)
## 3. 附
`建设中...` 
- [回到目录](#目录)