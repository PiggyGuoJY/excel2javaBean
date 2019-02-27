package com.guojy.parser.rule.parse;

import com.guojy.model.Msg;
import com.tkp.tkpole.starter.utils.model.Msg;

public interface Parseable {

    <G> Msg<G> parse(Class<G> gClass, Object ... args);
}
