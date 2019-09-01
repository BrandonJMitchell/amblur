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
import org.sovereign.technology.amblur.rules.ParserRules;
import org.sovereign.technology.amblur.utils.AmblurUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class AmblurParserImpl extends AbstractAmblurParser {

private static final Logger LOGGER = LoggerFactory.getLogger(AmblurParserImpl.class);
	
	@Autowired
	public AmblurParserImpl(AmblurEventFactory amblurFactory, AmblurEventDispatcher dispatcher) {
		super(amblurFactory, dispatcher);
	}

	public <T> List<T> parse(String xml, ParserRules parserRules) throws ParserException, DispatcherException, FactoryException, XMLStreamException {

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
				ParserManager manager = ParserManager.builder()
									   .xmlEventReader(xmlEventReader)
									   .rules(parserRules.retrieveXpathMap())
									   .root(parserRules.retrieveRoot())
									   .context(context)
									   .separator(AmblurUtils.SEPARATOR)
									   .useSeparator(false)
									   .build();
					   
				
				result = traverseXmlElements(manager);

			} catch (SecurityException | IllegalArgumentException
					| XMLStreamException | NoSuchMethodException 
					| IllegalAccessException | InvocationTargetException 
					| InstantiationException e) {

				if (xmlEventReader != null) {
					xmlEventReader.close();
				}
				throw new ParserException(e.getMessage(), e);

			}

			long end = System.currentTimeMillis();

			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("PARSED XML TOTAL TIME (Milliseconds) => {}", (end - start));
			}

		}

		return result;

	}

	public <T> List<T> traverseXmlElements(ParserManager manager) throws NoSuchMethodException, IllegalAccessException,

			InvocationTargetException, XMLStreamException,
			InstantiationException, ParserException, DispatcherException, FactoryException {

		if (manager == null || (manager != null && manager.getContext() == null)) {
			return null;
		}
		
		XMLEventReader xmlEventReader = manager.getXmlEventReader();
		if (xmlEventReader != null) {
			while (xmlEventReader.hasNext()) {
	
				XMLEvent xmlEvent = xmlEventReader.nextEvent();
				manager.getContext().setXmlEvent(xmlEvent);
				
				ParserEvent event = amblurFactory.getEvent(xmlEvent.getEventType());
				if (event != null) {
					event.setManager(manager);
					dispatcher.dispatch(event);
					if (!manager.isUseSeparator()) {
						manager.setUseSeparator(true);
					}
				}
			}
	
			if (LOGGER.isTraceEnabled()) {
				Map<Class<?>, List<?>> objListMap = manager.getContext().getObjListMap();
				Map<Class<?>, ?> objMap = manager.getContext().getObjMap();
				objMap.forEach((key, value) -> LOGGER.trace(" {} : {} ", key, value));
				objListMap.forEach((key, value) -> LOGGER.trace(" {} : {} ", key, value));
	
			}
		}

		return (List<T>) manager.retrieveParentList();

	}

}
