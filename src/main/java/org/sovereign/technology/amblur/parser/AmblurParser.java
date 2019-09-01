package org.sovereign.technology.amblur.parser;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import org.sovereign.technology.amblur.exception.DispatcherException;
import org.sovereign.technology.amblur.exception.FactoryException;
import org.sovereign.technology.amblur.exception.ParserException;
import org.sovereign.technology.amblur.rules.ParserRules;

public interface AmblurParser {

	public <T> List<T> parse(String xml, ParserRules parserRules) throws ParserException, DispatcherException, FactoryException, XMLStreamException;
	public <T> List<T> traverseXmlElements(ParserManager manager) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, XMLStreamException,InstantiationException, ParserException, DispatcherException, FactoryException;
}
