package org.sovereign.technology.amblur.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sovereign.technology.amblur.exception.ParserException;
import org.sovereign.technology.amblur.model.ParserRule;
import org.sovereign.technology.amblur.model.RulePlan;
import org.springframework.util.StringUtils;

public class AmblurUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(AmblurUtils.class);
	
	private static final List<String> SPECIAL_CHARACTERS = Arrays.asList("@","+");
	
	public static final String SEPARATOR = "/";
	
	private AmblurUtils() {}

	public static List<ParserRule> decree(List<RulePlan> plans) throws ParserException {

		List<ParserRule> result = null;

		if (plans != null && !plans.isEmpty()) {

			List<ParserRule> rules = new ArrayList<>();

			for (RulePlan rulePlan : plans) {

				if (LOGGER.isTraceEnabled()) {
					LOGGER.trace("decree current xpath => {}", rulePlan.getXpath());
				}

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

						if (LOGGER.isTraceEnabled()) {
							LOGGER.trace("element => {}", element);
						}

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

							if (LOGGER.isTraceEnabled()) {
								LOGGER.trace("NEW RULE => {} ::: {} ", currentRule.getElementName(), currentRule.getXpath());
							}

						}

						if (isInstance) {

							currentRule.setInstanceRule(true);

						}

						if (isAttribute) {

							currentRule.setAttribute(true);

						}

						if (i == elements.size() - 1) {

							if (LOGGER.isTraceEnabled()) {
								LOGGER.trace("Last element !!!");
							}

							currentRule.setMapper(rulePlan.getMapper());

							currentRule.setCollect(rulePlan.isCollect());

						}

						if (rules.isEmpty() && isNew) {

							rules.add(currentRule);
							if (LOGGER.isTraceEnabled()) {
								LOGGER.trace("first rule => {}", currentRule.getElementName());
							}

						} else if (i > 0 && isNew) {

							List<ParserRule> prevList = getPrevRuleList(i, elements, rules);

							if (prevList != null) {
								prevList.add(currentRule);
							} else {

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

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("prev name => {}", prevElementName);
			LOGGER.trace("prev xpath => {}", xpath);
		}
		 

		if (!prevElementName.isEmpty()) {

			List<ParserRule> results = new ArrayList<>();

			findRule(prevElementName, xpath, rules, results);

			ParserRule prevRule = null;

			if (!results.isEmpty()) {

				prevRule = results.get(0);

			}

			if (prevRule != null) {

				if (LOGGER.isTraceEnabled()) {
					LOGGER.trace("prevRule found => {}", prevRule.getElementName());
				}
				 

				List<ParserRule> prevList = prevRule.getParserRules();

				if (prevList == null) {

					if (LOGGER.isTraceEnabled()) {
						LOGGER.trace("NEW list");
					}

					prevList = new ArrayList<>();

				}

				result = new ArrayList<>(prevList);

				prevRule.setParserRules(result);

			} else {

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
	

}
