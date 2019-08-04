package org.sovereign.technology.amblur.rules;

import java.util.List;
import java.util.Map;

import org.sovereign.technology.amblur.exception.ParserException;
import org.sovereign.technology.amblur.model.ParserRule;
import org.sovereign.technology.amblur.model.RulePlan;

public interface ParserRules {

	public Class<?> retrieveClass();
	public Map<String, List<RulePlan>> getInstance();
	public List<ParserRule> retrieveRules(String className) throws ParserException;
	public List<ParserRule> retrieveRules() throws ParserException;
	public Map<String, ParserRule> retrieveXpathMap() throws ParserException;
	public void populateXpathMap(List<ParserRule> rules, Map<String, ParserRule> xpathMap);
	public String retrieveRoot();
}
