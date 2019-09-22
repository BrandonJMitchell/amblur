package org.sovereign.technology.amblur.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sovereign.technology.amblur.model.ParserRule;
import org.sovereign.technology.amblur.parser.ParserManager;
import org.springframework.util.StringUtils;

public class EventUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(EventUtils.class);
	
	private EventUtils() {}
	
	public static String retrieveAttribute(StartElement startElement, String attributeName) {

		String result = null;
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("-----retrieveAttribute looking attributeName: {}", attributeName);
		}

		Attribute attr = startElement.getAttributeByName(new QName(attributeName));

		if (attr != null) {

			result = attr.getValue();

		}
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("-----retrieveAttribute result => {}", result);
		}

		return result;

	}
	
	public static <K> K generateObject(Class<K> type)

			throws InstantiationException, IllegalAccessException {

		return type.newInstance();

	}
	
	public static void setSubObject(Object obj, Object subObj, ParserRule rule)

			throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException {

		if (rule != null && obj != null && subObj != null) {

			Method method = obj.getClass().getMethod(rule.getMapper(),
					subObj.getClass());

			method.invoke(obj, subObj);

		} else {
			if (LOGGER.isErrorEnabled()) {
				LOGGER.error("***ERROR****** setSubObject obj => {} subObj => {} rule => {}", obj, subObj, rule);
			}

		}

	}
	
	
	public static <K> List<K> genericList() {

		return new ArrayList<>();

	}
	
	public static void deleteLastPath(ParserManager manager) {
		StringBuilder xpathBuilder = manager.getContext().getXpathBuilder();
		String seperator = manager.retrieveSeparator();
		int start = xpathBuilder.lastIndexOf(seperator);
		int end = xpathBuilder.length();
		if (start > 0 && start <= end) {
			xpathBuilder = xpathBuilder.delete(start, end);
		}
		manager.getContext().setXpathBuilder(xpathBuilder);
	}
	
	public static void setSubList(Object obj, List<?> list, ParserRule rule)

			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {

		if (rule != null && rule.getMapper() != null && obj != null && list != null) {

			Method method = rule.getParentClazz().getMethod(rule.getMapper(),
					List.class);

			method.invoke(obj, list);

		} else if(rule != null && rule.getMapper() == null 
							   && rule.getSubMapper() == null
							   & LOGGER.isErrorEnabled()) {
			LOGGER.error("****ERROR***** setSubList obj => {} list => {} rule = >{} ", obj, list, rule);
		}

	}
	
	public static <T> void setSubValue(ParserManager manager, ParserRule rule, T obj) 
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, XMLStreamException {
		XMLEventReader xmlEventReader = manager.getXmlEventReader();
		XMLEvent xmlEvent = manager.getContext().getXmlEvent();
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("-----setSubValue-----");
		}
		if(obj != null && rule != null && rule.isCollect() 
					   && !StringUtils.isEmpty(rule.getParentClazz()) 
					   && !StringUtils.isEmpty(rule.getSubMapper())) {
			Method method = rule.getParentClazz().getMethod(rule.getSubMapper(), String.class);
			if (rule.isAttribute()) {
				String data = retrieveAttribute(xmlEvent.asStartElement(), rule.getElementName());
				method.invoke(obj, data);
			} else {
				if (xmlEventReader.hasNext()) {
					XMLEvent xmlPeekEvent = xmlEventReader.peek();
					if (xmlPeekEvent.isCharacters()) {
						xmlEvent = xmlEventReader.nextEvent();
						String data = xmlEvent.asCharacters().getData();
						method.invoke(obj, data);
						manager.getContext().setXmlEvent(xmlEvent);
					} else if (xmlPeekEvent.isEndElement()) {
						method.invoke(obj, "");
					}
				}
			}
		}
	}
	
	public static <T> void setElementValue(ParserManager manager, ParserRule rule, T obj)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, XMLStreamException {

		XMLEventReader xmlEventReader = manager.getXmlEventReader();
		XMLEvent xmlEvent = manager.getContext().getXmlEvent();
		
		rule.setFound(true);

		if (rule.getMapper() != null && obj != null 
				&& !rule.isCollect()
				&& rule.getParentClazz() == null) {

			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("-----setElement-Value rule => {} ::: {}" , rule.getElementName(),  rule.getXpath());
				LOGGER.trace("-----setElement-Value obj => {}", obj.getClass().getSimpleName());
			}

			Method method = rule.getClazz().getMethod(rule.getMapper(), String.class);

			if (rule.isAttribute()) {
				if (LOGGER.isTraceEnabled()) {
					LOGGER.trace("-----  Attribute");
				}
				String data = retrieveAttribute(xmlEvent.asStartElement(), rule.getElementName());

				method.invoke(obj, data);

			} else {
				if (LOGGER.isTraceEnabled()) {
					LOGGER.trace("----- NOT attribute");
				}
				if (xmlEventReader.hasNext()) {
					XMLEvent peekEvent = xmlEventReader.peek();

					if (peekEvent.isCharacters()) {
						xmlEvent = xmlEventReader.nextEvent();
						manager.getContext().setXmlEvent(xmlEvent);
						String data = xmlEvent.asCharacters().getData();

						method.invoke(obj, data);

						if (LOGGER.isTraceEnabled()) {
							LOGGER.trace("-----setElement-Value => {}", data);
						}

					}

				}

			}

		}

		if (rule.getParserRules() != null && !rule.getParserRules().isEmpty()) {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("Searching Children of => {}", rule.getElementName());
			}

			List<ParserRule> attributeRules = rule.getParserRules().stream()
																   .filter(ParserRule::isAttribute)
																   .collect(Collectors.toList());
			if (attributeRules != null && !attributeRules.isEmpty()) {
				for (ParserRule attrRule : attributeRules) {
					setElementValue(manager, attrRule, obj);
				}
			}
		}

	}
	
}
