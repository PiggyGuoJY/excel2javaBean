package com.github.piggyguojy.util.model;

import com.github.piggyguojy.util.Msg;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@EqualsAndHashCode(exclude = {"processor"})
@ToString(of = {"name"})
public class Processor {

  private String name;
  private Function<Params, Msg<?>> processor;
}
