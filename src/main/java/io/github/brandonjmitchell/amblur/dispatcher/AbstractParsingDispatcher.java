package io.github.brandonjmitchell.amblur.dispatcher;

import java.util.Map;

import io.github.brandonjmitchell.amblur.event.ParserEvent;
import io.github.brandonjmitchell.amblur.handler.ParsingHandler;
import lombok.Data;

@Data
public abstract class AbstractParsingDispatcher implements ParsingDispatcher {

	protected Map<Integer, ParsingHandler<ParserEvent>> handlerMap;


}
