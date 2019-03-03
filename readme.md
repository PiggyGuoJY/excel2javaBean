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

## 2. 高级功能


## 3. 未来规划


## 4. 附