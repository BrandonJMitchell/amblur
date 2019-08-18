package org.sovereign.technology.amblur.parser;

import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sovereign.technology.amblur.exception.ParserException;
import org.sovereign.technology.amblur.model.ParserRule;
import org.sovereign.technology.amblur.parliament.Parliament;
import org.sovereign.technology.amblur.rules.ParserRules;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ExecutiveParser {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExecutiveParser.class);

	public <T> List<T> parse(String xml, ParserRules parserRules)
			throws ParserException {

		return parse(xml, parserRules, true);

	}

	public <T> List<T> parse(String xml, ParserRules parserRules, boolean keepObj) throws ParserException {

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
				
				result = traverseXmlElements(xmlEventReader,
						parserRules.retrieveXpathMap(),
						parserRules.retrieveRoot(), keepObj);

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
				LOGGER.info("PARSED XML TOTAL TIME (Milliseconds) => "
						+ (end - start));
			}

		}

		return result;

	}

	private void resetRule(ParserRule rule) {

		rule.setFound(false);

		if (rule.getParserRules() != null && !rule.getParserRules().isEmpty()) {

			resetRules(rule.getParserRules());

		}

	}

	private void resetRules(List<ParserRule> rules) {

		rules.forEach(rule -> {

			rule.setFound(false);

			if (rule.getParserRules() != null
					&& !rule.getParserRules().isEmpty()) {

				resetRules(rule.getParserRules());

			}

		});

	}

	private String retrieveAttribute(StartElement startElement,
			String attributeName) {

		String result = null;
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("-----retrieveAttribute looking attributeName: "
					+ attributeName);
		}

		Attribute attr = startElement
				.getAttributeByName(new QName(attributeName));

		if (attr != null) {

			result = attr.getValue();

		}
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("-----retrieveAttribute result => " + result);
		}

		return result;

	}

	private void setSubObject(Object obj, Object subObj, ParserRule rule)

			throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException {

		if (rule != null && obj != null && subObj != null) {

			Method method = obj.getClass().getMethod(rule.getMapper(),
					subObj.getClass());

			method.invoke(obj, subObj);

		} else {
			if (LOGGER.isErrorEnabled()) {
				LOGGER.error("***ERROR****** setSubObject obj => " + obj
						+ " subObj => " + subObj + " rule => " + rule);
			}

		}

	}

	private void setSubList(Object obj, List<?> list, ParserRule rule)

			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {

		if (rule != null && obj != null && list != null) {

			Method method = rule.getParentClazz().getMethod(rule.getMapper(),
					List.class);

			method.invoke(obj, list);

		} else {
			if (LOGGER.isErrorEnabled()) {
				LOGGER.error("****ERROR***** setSubList obj => " + obj
						+ " list => " + list + " rule => " + rule);
			}

		}

	}

	private <T> boolean setElementValue(XMLEventReader xmlEventReader,
			XMLEvent xmlEvent,

			ParserRule rule, T obj)
			throws NoSuchMethodException, IllegalAccessException,

			InvocationTargetException, XMLStreamException {

		boolean result = false;

		rule.setFound(true);

		if (rule.getMapper() != null && obj != null && !rule.isCollect()

				&& rule.getParentClazz() == null) {

			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("-----setElement-Value rule => "
						+ rule.getElementName() + " :::" + rule.getXpath());
				LOGGER.trace("-----setElement-Value obj => "
						+ obj.getClass().getSimpleName());
			}

			Method method = rule.getClazz().getMethod(rule.getMapper(),
					String.class);

			if (rule.isAttribute()) {
				if (LOGGER.isTraceEnabled()) {
					LOGGER.trace("-----  Attribute");
				}
				String data = retrieveAttribute(xmlEvent.asStartElement(),
						rule.getElementName());

				method.invoke(obj, data);

				result = !StringUtils.isEmpty(data);

			} else {
				if (LOGGER.isTraceEnabled()) {
					LOGGER.trace("----- NOT attribute");
				}
				if (xmlEventReader.hasNext()) {
					XMLEvent peekEvent = xmlEventReader.peek();

					if (peekEvent.isCharacters()) {
						xmlEvent = xmlEventReader.nextEvent();

						String data = xmlEvent.asCharacters().getData();

						method.invoke(obj, data);

						result = !StringUtils.isEmpty(data);
						if (LOGGER.isTraceEnabled()) {
							LOGGER.trace("-----setElement-Value => " + data);
						}

					}

				}

			}

		}

		if (rule.getParserRules() != null && !rule.getParserRules().isEmpty()) {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace(
						"Searching Children of => " + rule.getElementName());
			}

			List<ParserRule> attributeRules = rule.getParserRules().stream()

					.filter(r -> r.isAttribute()).collect(Collectors.toList());

			for (ParserRule attrRule : attributeRules) {

				setElementValue(xmlEventReader, xmlEvent, attrRule, obj);

			}

		}

		return result;

	}

	private <T> List<T> traverseXmlElements(XMLEventReader xmlEventReader,
			Map<String, ParserRule> rules, String root,

			boolean isXml) throws NoSuchMethodException, IllegalAccessException,

			InvocationTargetException, XMLStreamException,
			InstantiationException, ParserException {

		Class<T> parentClass = null;

		ParserRule parentRule = null;

		Map<Class<T>, T> objMap = new HashMap<>();

		Map<Class<T>, List<T>> objListMap = new HashMap<>();

		StringBuilder xpathBuilder = new StringBuilder();

		String seperator = "";

		while (xmlEventReader.hasNext()) {

			String localPart = "";

			XMLEvent xmlEvent = xmlEventReader.nextEvent();

			if (xmlEvent.isStartElement()) {

				localPart = xmlEvent.asStartElement().getName().getLocalPart();

				xpathBuilder = xpathBuilder.append(seperator).append(localPart);

				seperator = "/";
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("-----xmlEvent start localData => " + localPart
							+ " ::: " + xpathBuilder.toString());
				}

				ParserRule currentRule = rules
						.get(Parliament.retrieveKey(xpathBuilder, root));

				if (currentRule != null) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("-----currentRule FOUND------");
					}

					if (currentRule.isInstanceRule()) {
						if (LOGGER.isTraceEnabled()) {
							LOGGER.trace(
									"-----currentRule isInstanceRule------");
						}

						if (currentRule.getParentClazz() != null) {
							if (LOGGER.isTraceEnabled()) {
								LOGGER.trace("-----create sub object------");
							}

							T obj = objMap.get(currentRule.getParentClazz());

							if (obj == null) {

								throw new ParserException(

										"Object is null. Check parser rules and make sure it has an instance rule for parent class."

												+ currentRule);

							}

							T subObj = (T) generateObject(
									currentRule.getClazz());

							objMap.put((Class<T>) currentRule.getClazz(),
									subObj);

							if (!currentRule.isCollect()) {
								if (LOGGER.isTraceEnabled()) {
									LOGGER.trace("-----Is SubClass-----");
								}

								setSubObject(obj, subObj, currentRule);

								setElementValue(xmlEventReader, xmlEvent,
										currentRule, subObj);

							} else if (currentRule.isCollect()) {
								if (LOGGER.isTraceEnabled()) {
									LOGGER.trace(
											"-----Is SubClass stored in LIST-----");
								}

								List<T> subObjList = objListMap
										.get(currentRule.getClazz());

								if (subObjList == null) {

									subObjList = (List<T>) genericList(
											currentRule.getClazz());

									setSubList(obj, subObjList, currentRule);

									objListMap.put(
											(Class<T>) currentRule.getClazz(),
											subObjList);

								}

								setElementValue(xmlEventReader, xmlEvent,
										currentRule, subObj);

							}

						} else {

							if (LOGGER.isTraceEnabled()) {
								LOGGER.trace("-----Parent Rule => "
										+ currentRule.getElementName());
							}

							T obj = (T) generateObject(currentRule.getClazz());

							objMap.put((Class<T>) currentRule.getClazz(), obj);

							List<T> objList = objListMap
									.get(currentRule.getClazz());

							if (objListMap.isEmpty()) {

								parentClass = (Class<T>) currentRule.getClazz();

								parentRule = currentRule;

							}

							if (objList == null) {

								objList = (List<T>) genericList(
										currentRule.getClazz());

								objListMap.put(
										(Class<T>) currentRule.getClazz(),
										objList);

							}

							setElementValue(xmlEventReader, xmlEvent,
									currentRule, obj);

						}

					} else {

						if (LOGGER.isTraceEnabled()) {
							LOGGER.trace(
									"----- currentRule is NOT InstanceRule");
						}

						T obj = objMap.get(currentRule.getClazz());

						setElementValue(xmlEventReader, xmlEvent, currentRule,
								obj);

					}

				}

			} else if (xmlEvent.isEndElement()) {

				localPart = xmlEvent.asEndElement().getName().getLocalPart();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("----xmlEvent end localPart => " + localPart);
				}

				ParserRule currentRule = rules
						.get(Parliament.retrieveKey(xpathBuilder, root));

				if (currentRule != null
						&& localPart.equals(currentRule.getElementName())

						&& currentRule.isInstanceRule()) {
					if (LOGGER.isTraceEnabled()) {
						LOGGER.trace("-----mlEvent END rule => "
								+ currentRule.getElementName());
					}

					resetRule(currentRule);

					T obj = null;
					List<T> objList = null;

					if (objMap != null) {
						obj = objMap.get(currentRule.getClazz());
					}

					if (objListMap != null) {
						objList = objListMap.get(currentRule.getClazz());
					}

					if (objList != null && obj != null) {
						if (LOGGER.isTraceEnabled()) {
							LOGGER.trace("-----ADDING OBJ to LIST => "
									+ localPart + " ::: "
									+ currentRule.getElementName());
						}
						objList.add(obj);

						if (!isXml || currentRule.isRemoveObject()) {
							if (LOGGER.isTraceEnabled()) {
								LOGGER.trace("-----REMOVING  OBJ from MAP => "
										+ localPart + " ::: "
										+ currentRule.getElementName());
							}
							objMap.remove(currentRule.getClazz());

						}
					}

					if (objList != null
							&& !currentRule.getRemoveLists().isEmpty()) {
						if (LOGGER.isTraceEnabled()) {
							LOGGER.trace("-----REMOVING  OBJ from LIST MAP => "
									+ localPart + " ::: "
									+ currentRule.getElementName());
						}
						currentRule.getRemoveLists().stream().forEach(clazz -> {
							objListMap.remove(clazz);
						});
					}

					if (parentClass != null && parentRule != null

							&& localPart.equals(parentRule.getElementName())) {
						if (LOGGER.isTraceEnabled()) {
							LOGGER.trace("-----parentClass => " + parentClass
									+ " parentRule.getElementName() => "
									+ parentRule.getElementName());
						}

						final Class<T> pc = parentClass;

						objListMap.keySet().removeIf(key -> !key.equals(pc));

					}

				}

				deleteLastPath(xpathBuilder, seperator);
			}

		}

		if (LOGGER.isTraceEnabled()) {
			objMap.forEach((key, value) -> LOGGER.trace(key + " : " + value));
			objListMap.forEach(
					(key, value) -> LOGGER.trace(key + " :: " + value));
		}

		return objListMap.get(parentClass);

	}

	private void deleteLastPath(StringBuilder xpathBuilder, String seperator) {
		int start = xpathBuilder.lastIndexOf(seperator);
		int end = xpathBuilder.length();
		if (start > 0 && start <= end) {
			xpathBuilder = xpathBuilder.delete(start, end);
		}
	}

	private <K> List<K> genericList(Class<K> type) {

		return new ArrayList<>();

	}

	private <K> K generateObject(Class<K> type)

			throws InstantiationException, IllegalAccessException {

		return type.newInstance();

	}

}
