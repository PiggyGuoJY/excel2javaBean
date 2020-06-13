package com.github.piggyguojy.model.test;


import com.github.piggyguojy.parser.excel.structure.annotation.ExcelColumn;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;

@ExcelColumn(sheetName = "常用数据类型转换测试", columnNameBegin = "B", columnNameEnd = "I", rowBegin = 2)
public class BasicTypeClass {

  private boolean bool;
  private byte b;
  private short s;
  private char c;
  private int i;
  private long l;
  private float f;
  private double d;

  private Boolean aBoolean;
  private Byte aByte;
  private Short aShort;
  private Character character;
  private Integer integer;
  private Long aLong;
  private BigInteger bigInteger;
  private Float aFloat;
  private Double aDouble;
  private BigDecimal bigDecimal;

  private Void aVoid;
  private Object object;
  private Date date;
  private LocalDate localDate;
  private Class aClass;

  private boolean[] booleans;
  private byte[] bytes;
  private short[] shorts;
  private char[] chars;
  private int[] ints;
  private long[] longs;
  private float[] floats;
  private double[] doubles;
}
