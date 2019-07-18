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
import org.sovereign.technology.amblur.rules.ParserRules;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ExecutiveParser {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExecutiveParser.class);

	public <T> List<T> parse(String xml, ParserRules parserRules) throws ParserException {

		return parse(xml, parserRules, true);

	}

	public <T> List<T> parse(String xml, ParserRules parserRules, boolean keepObj)

			throws ParserException {

		 long start = System.currentTimeMillis();

		// LOGGER.debug("**** parse this xml => " + xml);

		// LOGGER.debug("**** parseXML CURRENT TIME => " + start);

		List<T> result = null;

		if (!StringUtils.isEmpty(xml) && parserRules != null) {

			try {

				List<ParserRule> rules = parserRules.retrieveRules();

				// String strRules = objectMapper.writeValueAsString(rules);

				// LOGGER.debug("**** RULES => " + strRules);

				// LOGGER.debug("**** xml => " + xml);

				StringReader reader = new StringReader(xml);

				XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();

				// disable external entities

				xmlInputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);

				xmlInputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);

				XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(reader);

				result = traverseXmlElements(xmlEventReader, rules, keepObj);

				xmlEventReader.close();

			} catch (SecurityException | IllegalArgumentException | XMLStreamException

					| NoSuchMethodException | IllegalAccessException | InvocationTargetException

					| InstantiationException e) {

				throw new ParserException(e.getMessage(), e);

			}

			// LOGGER.debug(result == null ? "NO RESULT(S) FOR: " +
			// parserRules.retrieveClass() : "**** result LIST size => " + result.size());

			 long end = System.currentTimeMillis();

			// LOGGER.debug("**** parseXML END TIME => " + end);

			 LOGGER.debug("PARSED XML TOTAL TIME (Milliseconds) => " + (end - start));

		}

		return result;

	}

	private ParserRule findRuleByName(String localPart, List<ParserRule> rules,

			boolean isInstance) {

		ParserRule result = null;

		if (!StringUtils.isEmpty(localPart)) {

			for (ParserRule rule : rules) {

				if (rule != null) {

					if (rule.getElementName().equals(localPart)) {

						if (isInstance) {

							if (rule.isFound() && rule.isInstanceRule()) {

								result = rule;

								break;

							}

						} else {

							if (!rule.isFound()) {

								result = rule;

								break;

							} else {

								if (rule.getParserRules() != null && !rule.getParserRules().isEmpty()) {

									result = findRuleByName(localPart, rule.getParserRules(), isInstance);

								}

							}

						}

					} else {

						if (rule.isFound() && rule.getParserRules() != null

								&& !rule.getParserRules().isEmpty()) {

							result = findRuleByName(localPart, rule.getParserRules(), isInstance);

						}

					}

				}

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

			if (rule.getParserRules() != null && !rule.getParserRules().isEmpty()) {

				resetRules(rule.getParserRules());

			}

		});

	}

	private String retrieveAttribute(StartElement startElement, String attributeName) {

		String result = null;

		// LOGGER.debug("retrieveAttribute looking attributeName: " +
		// attributeName);

		Attribute attr = startElement.getAttributeByName(new QName(attributeName));

		if (attr != null) {

			result = attr.getValue();

		}

		// LOGGER.debug("retrieveAttribute result => " + result);

		return result;

	}

	private void setSubObject(Object obj, Object subObj, ParserRule rule)

			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

		if (rule != null && obj != null && subObj != null) {

			Method method = obj.getClass().getMethod(rule.getMapper(), subObj.getClass());

			method.invoke(obj, subObj);

		} else {

			// LOGGER.writeError("***ERROR****** setSubObject obj => " + obj + " subObj => "
			// + subObj
			//
			// + " rule => " + rule);

		}

	}

	private void setSubList(Object obj, List<?> list, ParserRule rule)

			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

		if (rule != null && obj != null && list != null) {

			Method method = rule.getParentClazz().getMethod(rule.getMapper(), List.class);

			method.invoke(obj, list);

		} else {

			// LOGGER.writeError(
			//
			// "****ERROR***** setSubList obj => " + obj + " list => " + list + " rule => "
			// + rule);

		}

	}

	private <T> boolean setElementValue(XMLEventReader xmlEventReader, XMLEvent xmlEvent,

			ParserRule rule, T obj) throws NoSuchMethodException, IllegalAccessException,

			InvocationTargetException, XMLStreamException {

		// LOGGER.debug("setElementValue obj => " + obj);

		// LOGGER.debug("setElementValue rule => " + rule.getElementName() + " :::
		// " +

		// rule.getXpath());

		boolean result = false;

		rule.setFound(true);

		if (rule.getMapper() != null && obj != null && !rule.isCollect()

				&& rule.getParentClazz() == null) {

			Method method = rule.getClazz().getMethod(rule.getMapper(), String.class);

			if (rule.isAttribute()) {

				String data = retrieveAttribute(xmlEvent.asStartElement(), rule.getElementName());

				// LOGGER.debug("attr *** setElement-Value obj => " +
				// obj.getClass().getSimpleName());

				// LOGGER.debug("attr *** setElement-Value clazz => " +

				// rule.getClazz().getSimpleName());

				// LOGGER.debug("attr *** setElement-Value element => " +
				// rule.getElementName());

				// LOGGER.debug("attr *** setElement-Value mapper => " + rule.getMapper());

				// LOGGER.debug("attr *** setElement-Value => " + data);

				method.invoke(obj, data);

				result = !StringUtils.isEmpty(data);

			} else {

				if (xmlEventReader.hasNext()) {

					xmlEvent = xmlEventReader.nextEvent();

					if (xmlEvent.isCharacters()) {

						String data = xmlEvent.asCharacters().getData();

						method.invoke(obj, data);

						result = !StringUtils.isEmpty(data);

					}

					// LOGGER.debug("*** setElement-Value => " + data);

				}

			}

		}

		if (rule.getParserRules() != null && !rule.getParserRules().isEmpty() && rule.isFound()) {

			// LOGGER.debug("Searching Children of => " + rule.getElementName());

			List<ParserRule> attributeRules = rule.getParserRules().stream()

					.filter(r -> r.isAttribute() && !r.isFound()).collect(Collectors.toList());

			for (ParserRule attrRule : attributeRules) {

				setElementValue(xmlEventReader, xmlEvent, attrRule, obj);

			}

		}

		return result;

	}

	private <T> List<T> traverseXmlElements(XMLEventReader xmlEventReader, List<ParserRule> rules,

			boolean isXml) throws NoSuchMethodException, IllegalAccessException,

			InvocationTargetException, XMLStreamException, InstantiationException, ParserException {

		Class<T> parentClass = null;

		ParserRule parentRule = null;

		Map<Class<T>, T> objMap = new HashMap<>();

		Map<Class<T>, List<T>> objListMap = new HashMap<>();

		while (xmlEventReader.hasNext()) {

			String localPart = "";

			XMLEvent xmlEvent = xmlEventReader.nextEvent();

			// LOGGER.debug("-----1 JUST KEEP SWIMMING------ " + rules.size());

			if (xmlEvent.isStartElement()) {

				localPart = xmlEvent.asStartElement().getName().getLocalPart();

				// LOGGER.debug("**** xmlEvent start localData => " + localPart);

				ParserRule currentRule = findRuleByName(localPart, rules, false);

				if (currentRule != null) {

					// LOGGER.debug("-----currentRule not null------");

					if (currentRule.isInstanceRule()) {

						// LOGGER.debug("-----currentRule isInstanceRule------");

						if (currentRule.getParentClazz() != null) {

							// LOGGER.debug("-----create sub object------");

							T obj = objMap.get(currentRule.getParentClazz());

							if (obj == null) {

								throw new ParserException(

										"Object is null. Check parser rules and make sure it has an instance rule for parent class."

												+ currentRule);

							}

							T subObj = (T) generateObject(currentRule.getClazz());

							objMap.put((Class<T>) currentRule.getClazz(), subObj);

							if (!currentRule.isCollect()) {

								// LOGGER.debug("Is SubClass");

								setSubObject(obj, subObj, currentRule);

								setElementValue(xmlEventReader, xmlEvent, currentRule, subObj);

							} else if (currentRule.isCollect()) {

								// LOGGER.debug("Is SubClass stored in LIST");

								List<T> subObjList = objListMap.get(currentRule.getClazz());

								if (subObjList == null) {

									subObjList = (List<T>) genericList(currentRule.getClazz());

									setSubList(obj, subObjList, currentRule);

									objListMap.put((Class<T>) currentRule.getClazz(), subObjList);

								}

								setElementValue(xmlEventReader, xmlEvent, currentRule, subObj);

							}

						} else {

							// LOGGER.debug("-----currentRule => " + currentRule);

							// LOGGER.debug("-----create object => " + currentRule.getClazz());

							T obj = (T) generateObject(currentRule.getClazz());

							objMap.put((Class<T>) currentRule.getClazz(), obj);

							List<T> objList = objListMap.get(currentRule.getClazz());

							if (objListMap.isEmpty()) {

								parentClass = (Class<T>) currentRule.getClazz();

								parentRule = currentRule;

							}

							if (objList == null) {

								objList = (List<T>) genericList(currentRule.getClazz());

								objListMap.put((Class<T>) currentRule.getClazz(), objList);

							}

							setElementValue(xmlEventReader, xmlEvent, currentRule, obj);

						}

					} else {

						// LOGGER.debug("-----by the way rule => " + currentRule.getElementName() +
						// " ::: "

						// + currentRule.getXpath());

						T obj = objMap.get(currentRule.getClazz());

						setElementValue(xmlEventReader, xmlEvent, currentRule, obj);

					}

				} else {

					// LOGGER.debug("----- RULE NOT FOUND!!! ");

				}

			} else if (xmlEvent.isEndElement()) {

				localPart = xmlEvent.asEndElement().getName().getLocalPart();

				// LOGGER.debug("**** xmlEvent end localData => " + localPart);

				// LOGGER.debug("**** xmlEvent end rules => " + rules.size());

				ParserRule currentRule = findRuleByName(localPart, rules, true);

				if (currentRule != null && localPart.equals(currentRule.getElementName())

						&& currentRule.isInstanceRule()) {

					// LOGGER.debug(

					// "**** RESET RULES xmlEvent localData => "

					// + xmlEvent.asEndElement().getName().getLocalPart());

					// LOGGER.debug(

					// "**** xmlEvent END rule => "

					// + currentRule);

					resetRule(currentRule);

					T obj = objMap.get(currentRule.getClazz());

					List<T> objList = objListMap.get(currentRule.getClazz());

					if (objList != null && obj != null) {

//						  LOGGER.debug("**** ADDING OBJ to LIST => " + localPart + " ::: " + obj + " ::: " + currentRule);

						objList.add(obj);

						if (!isXml || currentRule.isRemoveObject()) {

							objMap.remove(currentRule.getClazz());

						}

						// objListMap.put((Class<T>) currentRule.getClazz(), objList);

					}

					if (parentClass != null && parentRule != null

							&& localPart.equals(parentRule.getElementName())) {

						// LOGGER.debug("**** parentClass => " + parentClass + "

						// parentRule.getElementName() => " + parentRule.getElementName());

						final Class<T> pc = parentClass;

						objListMap.keySet().removeIf(key -> !key.equals(pc));

					}

				}

			}

		}

		// LOGGER.debug("parentClass => " + parentClass);

		// objMap.forEach((key, value) -> LOGGER.debug(key + " : " + value));

		// objListMap.forEach((key, value) -> LOGGER.debug(key + " :: " + value));

		return objListMap.get(parentClass);

	}

	private <K> List<K> genericList(Class<K> type) {

		return new ArrayList<>();

	}

	private <K> K generateObject(Class<K> type)

			throws InstantiationException, IllegalAccessException {

		return type.newInstance();

	}

}
