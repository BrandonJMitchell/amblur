package org.sovereign.technology.amblur.dispatcher;

import java.util.Map;

import org.sovereign.technology.amblur.event.ParserEvent;
import org.sovereign.technology.amblur.handler.ParsingHandler;

import lombok.Data;

@Data
public abstract class AbstractParsingDispatcher implements ParsingDispatcher {

	protected Map<Integer, ParsingHandler<ParserEvent>> handlerMap;


}
