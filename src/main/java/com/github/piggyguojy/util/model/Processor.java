package com.github.piggyguojy.util.model;

import com.github.piggyguojy.util.Msg;
import lombok.*;

import java.util.function.Function;

@AllArgsConstructor
@Getter
@EqualsAndHashCode(exclude = {"processor"}) @ToString(of = {"name"})
public class Processor {
    private String name;
    private Function<Params,Msg<?>> processor;
}
