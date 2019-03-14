# excel2javaBean

[![travis](https://img.shields.io/travis/PiggyGuoJY/excel2javaBean.svg)](https://www.travis-ci.org/)
[![codecov](https://img.shields.io/codecov/c/github/PiggyGuoJY/excel2javaBean.svg)](https://codecov.io/gh/PiggyGuoJY)
[![codebeat badge](https://codebeat.co/badges/c53eacdc-12cd-4081-8853-208c7f08fc39)](https://codebeat.co/projects/github-com-piggyguojy-excel2javabean-master)
[![maven](https://img.shields.io/maven-central/v/com.github.piggyguojy/excel2javaBean.svg)](https://repo1.maven.org/maven2/com/github/piggyguojy/excel2javaBean/)
[![license](https://img.shields.io/github/license/PiggyGuoJY/excel2javaBean.svg)](https://www.apache.org/licenses/LICENSE-2.0)
> excel2javaBean是一个便捷的Excel数据读取和处理工具. 工具的目标是让开发者能更加专注于业务逻辑和数据处理本身.

## 目录
- [1. 快速入门](#1-快速入门-回到目录)
    - [1.1 准备Excel文件](#11-准备excel文件)
    - [1.2 定义实体](#12-定义实体)
    - [1.3 解析和结果查看](#13-解析和结果查看)
    - [1.4 另一种实体定义对应的文件,-实体定义和解析结果](#14-另一种实体定义对应的文件-实体定义和解析结果)
- [2. 更多功能](#2-更多功能-回到目录)
    - [2.1 注解和类的简单组合使用](#21-注解和类的简单组合使用)
    - [2.2 更加方便的地址属性绑定方法](#22-更加方便的地址属性绑定方法)
    - [2.3 自定义类型转换规则](#23-自定义类型转换规则)
    - [2.4 使用嵌套注解](#24-使用嵌套注解)
    - [2.5 使用其他继承规则](#25-使用其他继承规则)
- [3. 附](#3-附-回到目录)
    - [3.1 未来建设计划](#31-未来建设计划)

## 引用方式
<details>
<summary>代码较多, 点击查看</summary>

- maven
```xml
<dependency>
  <groupId>com.github.piggyguojy</groupId>
  <artifactId>excel2javaBean</artifactId>
  <version>1.0.2</version>
</dependency>
```
- Gradle
```gradle
implementation 'com.github.piggyguojy:excel2javaBean:1.0.2'
```
	
</details>

---
## 1. 快速入门 <sup>[回到目录](#目录)</sup>
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
</details>

<details>
<summary>解析结果较多, 点击查看</summary>

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

***

## 2. 更多功能 <sup>[回到目录](#目录)</sup>
### 2.1 注解和类的简单组合使用
`建设中...`
### 2.2 更加方便的地址属性绑定方法
#### 2.2.1 准备Excel文件
![](https://github.com/PiggyGuoJY/excel2javaBean/blob/master/readme/CapTestFile3.JPG?raw=true)
#### 2.2.2 定义实体
<details>
<summary>代码较多, 点击查看</summary>

```java
// 定义列数据的实体
@ExcelRow(sheet = 3, columnBegin = 1, rowBegin = 8)
public class CensusMetaData {
    
    private String local;

    private Integer totalRegistered;
    private Integer totalFamilyRegistered;
    private Integer totalCollectiveRegistered;

    private Integer totalPopulation;
    private Integer totalMalePopulation;
    private Integer totalFeMalePopulation;
    private Double populationGenderRatio;

    private Integer totalFamilyPopulation;
    private Integer totalFamilyMalePopulation;
    private Integer totalFamilyFemalePopulation;
    private Double familyPopulationGenderRatio;

    private Integer totalCollectivePopulation;
    private Integer totalCollectiveMalePopulation;
    private Integer totalCollectiveFemalePopulation;
    private Double familyCollectiveGenderRatio;

    private Double averagePopulationPerRegistered;
}
```
</details>

#### 2.2.3 解析和结果查看
<details>
<summary>代码较多, 点击查看</summary>

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
    public void test3() {
        Msg<ExcelParser> excelParserMsg = ExcelParserFactory.createParser(TEST_FILE);
        assertFalse(excelParserMsg.isException());
        ExcelParser excelParser = excelParserMsg.getT();
        Msg<CensusMetaData> censusMetaDataMsg = excelParser.parse(CensusMetaData.class);
        assertFalse(censusMetaDataMsg.isException());
        log.info(JsonUtil.javaBean2Json(censusMetaDataMsg.getT()));
    }
}
```
</details>

> 结果请到这里查看 <a href="https://www.jianshu.com/p/4f49a81f5a57" target="_blank">解析结果</a>
### 2.3 自定义类型转换规则
`建设中...`
### 2.4 使用嵌套注解
`建设中...`
### 2.5 使用其他继承规则
`建设中...`
***

## 3. 附 <sup>[回到目录](#目录)</sup>
### 3.1 未来建设计划
- 进一步完善readme和测试案例 (进行中...)
- 实现用户自定义数据处理脚本化
- 完善对基本数据类型的自动处理 (进行中...)
- 实现xml式配置
- 实现对csv格式文件的转化与解析
- 利用ByteBuf优化底层实现
- 攥写Wiki文档
- 攥写github上的其他模板

`建设中...` 

***
[![](https://img.shields.io/badge/Javadoc-%20excel2javaBean-brightgreen.svg)](https://piggyguojy.github.io/excel2javaBean-java-api/)