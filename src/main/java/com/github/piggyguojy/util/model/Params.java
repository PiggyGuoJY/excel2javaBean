package com.github.piggyguojy.util.model;

import com.github.piggyguojy.parser.rule.parse.AbstractParser;
import com.github.piggyguojy.util.Msg;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class Params {

  private Class<?> zlass;
  private AbstractParser parser;
  private Object[] args;
  private Msg<?> returnMsg;

  @SuppressWarnings("unchecked")
  public <P extends AbstractParser> P getParser() {
    return (P) parser;
  }
}