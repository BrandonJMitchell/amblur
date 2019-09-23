package io.github.brandonjmitchell.amblur.rules;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.brandonjmitchell.amblur.exception.ParserException;
import io.github.brandonjmitchell.amblur.model.ParserRule;
import io.github.brandonjmitchell.amblur.model.RulePlan;
import io.github.brandonjmitchell.amblur.utils.AmblurUtils;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class AbstractPaserRules implements ParserRules {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPaserRules.class);
	
	protected Map<String, List<RulePlan>> rulesMap;
	protected String root;

	@Override
	public List<ParserRule> retrieveRules(String className) throws ParserException {

		if (rulesMap == null) {
			getInstance();
		}

		return AmblurUtils.createParserRules(rulesMap.get(className));
	}

	@Override
	public List<ParserRule> retrieveRules() throws ParserException {

		if (rulesMap == null) {
			getInstance();
		}

		List<RulePlan> plans =

				rulesMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());

		return AmblurUtils.createParserRules(plans);
	}
	
	@Override
	public Map<String, ParserRule> retrieveXpathMap() throws ParserException {
		Map<String, ParserRule> xpathMap = new HashMap<>();
		populateXpathMap(this.retrieveRules(), xpathMap);
		if(LOGGER.isTraceEnabled()) {
			xpathMap.entrySet().forEach(entry -> 
				LOGGER.trace("{} ::: {}", entry.getKey(), entry.getValue().getXpath())
			);
		}
		return Collections.unmodifiableMap(xpathMap);
	}

	@Override
	public void populateXpathMap(List<ParserRule> rules, Map<String, ParserRule> xpathMap) {
		if (rules != null && !rules.isEmpty() && xpathMap != null) {
			for(ParserRule rule : rules) {
				if (rule != null) {
					String xpath = AmblurUtils.cleanXpath(rule.getXpath());
					xpathMap.put(xpath, rule);
					if (rule.getParserRules() != null && !rule.getParserRules().isEmpty()) {
						populateXpathMap(rule.getParserRules(), xpathMap);
					}
				}
			}
		}
	}

	@Override
	public String retrieveRoot() {
		return this.root;
	}

}
