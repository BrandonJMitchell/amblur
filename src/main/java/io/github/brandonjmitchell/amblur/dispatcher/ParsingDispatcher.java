package io.github.brandonjmitchell.amblur.dispatcher;

import java.lang.reflect.InvocationTargetException;

import javax.xml.stream.XMLStreamException;

import io.github.brandonjmitchell.amblur.event.ParserEvent;
import io.github.brandonjmitchell.amblur.exception.DispatcherException;
import io.github.brandonjmitchell.amblur.exception.ParserException;
import io.github.brandonjmitchell.amblur.handler.ParsingHandler;

public interface ParsingDispatcher {

	public <E extends ParserEvent> void register(int eventType, ParsingHandler<E> handler) throws DispatcherException;
	public <E extends ParserEvent> void dispatch(E event) throws DispatcherException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, ParserException, XMLStreamException;
}
