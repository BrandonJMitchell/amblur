package io.github.brandonjmitchell.amblur.handler;

import java.lang.reflect.InvocationTargetException;

import javax.xml.stream.XMLStreamException;

import io.github.brandonjmitchell.amblur.event.ParserEvent;
import io.github.brandonjmitchell.amblur.exception.ParserException;

public interface ParsingHandler <E extends ParserEvent> {
	public <T> void onEvent(E event) throws ParserException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, XMLStreamException;
}
