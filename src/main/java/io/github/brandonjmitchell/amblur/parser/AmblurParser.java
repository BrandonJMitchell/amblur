package io.github.brandonjmitchell.amblur.parser;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import javax.xml.stream.XMLStreamException;

import io.github.brandonjmitchell.amblur.exception.DispatcherException;
import io.github.brandonjmitchell.amblur.exception.FactoryException;
import io.github.brandonjmitchell.amblur.exception.ParserException;
import io.github.brandonjmitchell.amblur.rules.ParserRules;

public interface AmblurParser {

	public <T> List<T> parse(String xml, ParserRules parserRules) throws ParserException, DispatcherException, FactoryException, XMLStreamException;
	public <T> List<T> traverseXmlElements(ParserManager manager) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, XMLStreamException,InstantiationException, ParserException, DispatcherException, FactoryException;
}
