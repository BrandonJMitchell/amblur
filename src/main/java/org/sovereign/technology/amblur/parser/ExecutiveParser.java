package org.sovereign.technology.amblur.parser;

import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sovereign.technology.amblur.dispatcher.AmblurEventDispatcher;
import org.sovereign.technology.amblur.event.ParserEvent;
import org.sovereign.technology.amblur.exception.DispatcherException;
import org.sovereign.technology.amblur.exception.FactoryException;
import org.sovereign.technology.amblur.exception.ParserException;
import org.sovereign.technology.amblur.factory.AmblurEventFactory;
import org.sovereign.technology.amblur.parliament.Parliament;
import org.sovereign.technology.amblur.rules.ParserRules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ExecutiveParser extends AbstractAmblurParser {

private static final Logger LOGGER = LoggerFactory.getLogger(ExecutiveParser.class);
	
	private ParserManager manager;
	
	@Autowired
	public ExecutiveParser(AmblurEventFactory ableFactory, AmblurEventDispatcher dispatcher) {
		super(ableFactory, dispatcher);

	}


	public <T> List<T> parse(String xml, ParserRules parserRules) throws ParserException, DispatcherException, FactoryException {

		long start = System.currentTimeMillis();
		List<T> result = null;
		XMLEventReader xmlEventReader = null;
		if (!StringUtils.isEmpty(xml) && parserRules != null) {
			try {

				StringReader reader = new StringReader(xml);
				XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
				// disable external entities
				xmlInputFactory.setProperty(
						XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES,
						Boolean.FALSE);
				
				xmlInputFactory.setProperty(XMLInputFactory.SUPPORT_DTD,
						Boolean.FALSE); 
				
				xmlEventReader = xmlInputFactory.createXMLEventReader(reader);
				ParserContext context = ParserContext.builder()
													 .objMap( new HashMap<>())
													 .objListMap( new HashMap<>())
													 .xpathBuilder(new StringBuilder())
													 .build();
				manager = ParserManager.builder()
									   .xmlEventReader(xmlEventReader)
									   .rules(parserRules.retrieveXpathMap())
									   .root(parserRules.retrieveRoot())
									   .context(context)
									   .separator(Parliament.SEPARATOR)
									   .useSeparator(false)
									   .build();
					   
				
				result = traverseXmlElements(manager);

			} catch (SecurityException | IllegalArgumentException
					| XMLStreamException | NoSuchMethodException 
					| IllegalAccessException | InvocationTargetException 
					| InstantiationException e) {

				throw new ParserException(e.getMessage(), e);

			} finally {
				if (xmlEventReader != null) {
					try {
						xmlEventReader.close();
					} catch (XMLStreamException e) {
						throw new ParserException(e.getMessage(), e);
					}
				}
			}

			long end = System.currentTimeMillis();

			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("PARSED XML TOTAL TIME (Milliseconds) => " + (end - start));
			}

		}

		return result;

	}



	public <T> List<T> traverseXmlElements(ParserManager manager) throws NoSuchMethodException, IllegalAccessException,

			InvocationTargetException, XMLStreamException,
			InstantiationException, ParserException, DispatcherException, FactoryException {

		XMLEventReader xmlEventReader = manager.getXmlEventReader();
		
		while (xmlEventReader.hasNext()) {

			XMLEvent xmlEvent = xmlEventReader.nextEvent();
			manager.getContext().setXmlEvent(xmlEvent);
			
			ParserEvent event = ableFactory.getEvent(xmlEvent.getEventType());
			if (event != null) {
				event.setManager(manager);
				dispatcher.dispatch(event);
				manager.setUseSeparator(true);
			}
		}

		if (LOGGER.isTraceEnabled()) {
			Map<Class<?>, List<?>> objListMap = manager.getContext().getObjListMap();
			Map<Class<?>, ?> objMap = manager.getContext().getObjMap();
			objMap.forEach((key, value) -> LOGGER.trace(key + " : " + value));
			objListMap.forEach((key, value) -> LOGGER.trace(key + " :: " + value));

		}

		return (List<T>) manager.retrieveParentList();

	}

}
