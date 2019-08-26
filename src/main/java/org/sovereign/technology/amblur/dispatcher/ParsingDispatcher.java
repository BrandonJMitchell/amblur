package org.sovereign.technology.amblur.dispatcher;

import java.lang.reflect.InvocationTargetException;

import javax.xml.stream.XMLStreamException;

import org.sovereign.technology.amblur.event.ParserEvent;
import org.sovereign.technology.amblur.exception.DispatcherException;
import org.sovereign.technology.amblur.exception.ParserException;
import org.sovereign.technology.amblur.handler.ParsingHandler;

public interface ParsingDispatcher {

	public <E extends ParserEvent> void register(int eventType, ParsingHandler<E> handler) throws DispatcherException;
	public <E extends ParserEvent> void dispatch(E event) throws DispatcherException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, ParserException, XMLStreamException;
}
