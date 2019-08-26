package org.sovereign.technology.amblur.handler;

import java.lang.reflect.InvocationTargetException;

import javax.xml.stream.XMLStreamException;

import org.sovereign.technology.amblur.event.ParserEvent;
import org.sovereign.technology.amblur.exception.ParserException;

public interface ParsingHandler <E extends ParserEvent> {
	public <T> void onEvent(E event) throws ParserException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, XMLStreamException;
}
