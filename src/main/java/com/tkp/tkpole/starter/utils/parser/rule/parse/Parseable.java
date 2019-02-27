package com.tkp.tkpole.starter.utils.parser.rule.parse;

import com.tkp.tkpole.starter.utils.model.Msg;

public interface Parseable {

    <G> Msg<G> parse(Class<G> gClass, Object ... args);
}
