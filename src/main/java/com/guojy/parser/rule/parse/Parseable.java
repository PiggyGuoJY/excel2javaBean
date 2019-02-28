package com.guojy.parser.rule.parse;

import com.guojy.model.Msg;

public interface Parseable {

    <G> Msg<G> parse(Class<G> gClass, Object ... args);
}
