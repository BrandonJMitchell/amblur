package org.sovereign.technology.amblur.parliament;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.sovereign.technology.amblur.exception.ParserException;
import org.sovereign.technology.amblur.model.ParserRule;
import org.sovereign.technology.amblur.model.RulePlan;
import org.sovereign.technology.amblur.parser.ParserManager;
import org.springframework.util.StringUtils;

public class Parliament {

	private static final Logger LOGGER = LoggerFactory.getLogger(Parliament.class);
	
	private static final List<String> SPECIAL_CHARACTERS = Arrays.asList("@","+");
	
	public static String SEPARATOR = "/";
	
	private Parliament() {}

	public static List<ParserRule> decree(List<RulePlan> plans) throws ParserException {

		List<ParserRule> result = null;

		if (plans != null && !plans.isEmpty()) {

			List<ParserRule> rules = new ArrayList<>();

			for (RulePlan rulePlan : plans) {

				// LOGGER.writeDebug("decree => " + rulePlan.getXpath());

				List<String> elements = Arrays.asList(rulePlan.getXpath().split("\\/"));

				StringBuilder xpaths = new StringBuilder();

				if (elements != null && !elements.isEmpty()) {

					for (int i = 0; i < elements.size(); i++) {

						String element = elements.get(i);

						if (i == 0) {

							xpaths.append(element);

						} else {

							xpaths.append("/").append(element);

						}

						// LOGGER.writeDebug("element => " + element);

						boolean isAttribute = element.startsWith(SPECIAL_CHARACTERS.get(0));

						boolean isInstance = element.startsWith(SPECIAL_CHARACTERS.get(1));

						boolean isNew = false;

						if (isAttribute || isInstance) {

							element = element.substring(1);

						}

						ParserRule currentRule = null;

						List<ParserRule> results = new ArrayList<>();

						findRule(element, xpaths.toString(), rules, results);

						if (!results.isEmpty()) {

							currentRule = results.get(0);

						}

						if (currentRule == null) {

							isNew = true;

							currentRule = ParserRule.builder()

									.xpath(xpaths.toString())

									.elementName(element)

									.clazz(rulePlan.getClazz())

									.parentClazz(rulePlan.getParentClazz())

									.removeObject(rulePlan.isRemoveObject())
									
									.removeLists(rulePlan.getRemoveLists())

									.build();

							// LOGGER.writeDebug("NEW RULE => " + currentRule.getElementName() + " ::: " +
							// currentRule.getXpath());

						}

						if (isInstance) {

							currentRule.setInstanceRule(true);

						}

						if (isAttribute) {

							currentRule.setAttribute(true);

						}

						if (i == elements.size() - 1) {

							// LOGGER.writeDebug("Last element !!!");

							currentRule.setMapper(rulePlan.getMapper());

							currentRule.setCollect(rulePlan.isCollect());

						}

						if (rules.isEmpty() && isNew) {

							rules.add(currentRule);

							// LOGGER.writeDebug("first rule => " + currentRule.getElementName());

						} else if (i > 0 && isNew) {

							List<ParserRule> prevList = getPrevRuleList(i, elements, rules);

							if (prevList != null) {

								// LOGGER.writeDebug("prevList SIZE => " + prevList.size());

								// LOGGER.writeDebug("list SIZE => " + prevList.size());

								prevList.add(currentRule);

								// LOGGER.writeDebug(" Add " + currentRule.getElementName() + " ::: " + "list
								// SIZE

								// => " + prevList.size());

							} else {

								// LOGGER.writeDebug("WHAAAAAAAAT NO PREV LIST");

								throw new ParserException(

										"Parent list is null. Every Rule should have a parent other than the root."
												+ currentRule);

							}

						}

					}

				}

			}

			result = rules;

		}

		return result;

	}

	private static List<ParserRule> getPrevRuleList(int i, List<String> elements,

			List<ParserRule> rules) throws ParserException {

		List<ParserRule> result = null;

		String prevElementName = elements.get(i - 1);

		String xpath = elements.subList(0, i).stream().collect(Collectors.joining("/"));

		boolean isAttribute = prevElementName.startsWith("@");

		boolean isInstance = prevElementName.startsWith("+");

		if (isAttribute || isInstance) {

			prevElementName = prevElementName.substring(1);

		}

		// LOGGER.writeDebug("prev name => " + prevElementName);

		// LOGGER.writeDebug("prev xpath => " + xpath);

		if (!prevElementName.isEmpty()) {

			List<ParserRule> results = new ArrayList<>();

			findRule(prevElementName, xpath, rules, results);

			ParserRule prevRule = null;

			if (!results.isEmpty()) {

				prevRule = results.get(0);

			}

			if (prevRule != null) {

				// LOGGER.writeDebug("prevRule found => " + prevRule.getElementName());

				List<ParserRule> prevList = prevRule.getParserRules();

				if (prevList == null) {

					// LOGGER.writeDebug("NEW list");

					prevList = new ArrayList<>();

				}

				result = new ArrayList<>(prevList);

				prevRule.setParserRules(result);

			} else {

				// LOGGER.writeDebug("WHAAAAT prev RULE NULL");

				throw new ParserException(

						"Parent Rule is null. Every Rule should have a parent other than the root.");

			}

		}

		return result;

	}

	private static void findRule(String name, String xpath, List<ParserRule> rules,

			List<ParserRule> result) {

		for (ParserRule rule : rules) {

			if (rule.getElementName().equals(name) && rule.getXpath().equals(xpath)) {

				result.add(rule);

				break;

			} else {

				findRule(name, xpath, rule.getParserRules(), result);

			}

		}

	}
	
	public static String cleanXpath(String xpath) {
		String result = xpath;
		for(String sChar : SPECIAL_CHARACTERS) {
			result = StringUtils.delete(result, sChar);
		}
		
		return result;
	}
	
	public static String retrieveKey(StringBuilder xpathBuilder, String root) {
		String result = xpathBuilder.toString();
		if (!StringUtils.isEmpty(root)) {
			int startIdx = xpathBuilder.indexOf(cleanXpath(root));
			if (startIdx >= 0) {
				result = xpathBuilder.substring(startIdx);
			}
		}
		return result;
	}

	public static void resetRule(ParserRule rule) {

		rule.setFound(false);

		if (rule.getParserRules() != null && !rule.getParserRules().isEmpty()) {

			resetRules(rule.getParserRules());

		}

	}

	public static void resetRules(List<ParserRule> rules) {

		rules.forEach(rule -> {

			rule.setFound(false);

			if (rule.getParserRules() != null
					&& !rule.getParserRules().isEmpty()) {

				resetRules(rule.getParserRules());

			}

		});

	}
	
	public static String retrieveAttribute(StartElement startElement, String attributeName) {

		String result = null;
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("-----retrieveAttribute looking attributeName: "
					+ attributeName);
		}

		Attribute attr = startElement.getAttributeByName(new QName(attributeName));

		if (attr != null) {

			result = attr.getValue();

		}
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("-----retrieveAttribute result => " + result);
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
				LOGGER.error("***ERROR****** setSubObject obj => " + obj
						+ " subObj => " + subObj + " rule => " + rule);
			}

		}

	}
	
	
	public static <K> List<K> genericList(Class<K> type) {

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
	
	public static <T> void setElementValue(ParserManager manager, ParserRule rule, T obj)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, XMLStreamException {

		XMLEventReader xmlEventReader = manager.getXmlEventReader();
		XMLEvent xmlEvent = manager.getContext().getXmlEvent();
		
		rule.setFound(true);

		if (rule.getMapper() != null && obj != null && !rule.isCollect()

				&& rule.getParentClazz() == null) {

			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("-----setElement-Value rule => "
						+ rule.getElementName() + " :::" + rule.getXpath());
				LOGGER.trace("-----setElement-Value obj => "
						+ obj.getClass().getSimpleName());
			}

			Method method = rule.getClazz().getMethod(rule.getMapper(), String.class);

			if (rule.isAttribute()) {
				if (LOGGER.isTraceEnabled()) {
					LOGGER.trace("-----  Attribute");
				}
				String data = Parliament.retrieveAttribute(xmlEvent.asStartElement(), rule.getElementName());

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
							LOGGER.trace("-----setElement-Value => " + data);
						}

					}

				}

			}

		}

		if (rule.getParserRules() != null && !rule.getParserRules().isEmpty()) {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("Searching Children of => " + rule.getElementName());
			}

			List<ParserRule> attributeRules = rule.getParserRules().stream()
																   .filter(r -> r.isAttribute())
																   .collect(Collectors.toList());
			if (attributeRules != null && !attributeRules.isEmpty()) {
				for (ParserRule attrRule : attributeRules) {
					setElementValue(manager, attrRule, obj);
				}
			}
		}

	}
	
}
