package org.sovereign.technology.amblur.rules;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.sovereign.technology.amblur.exception.ParserException;
import org.sovereign.technology.amblur.model.ParserRule;
import org.sovereign.technology.amblur.model.RulePlan;
import org.sovereign.technology.amblur.parliament.Parliament;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class AbstractPaserRules implements ParserRules {

	protected Map<String, List<RulePlan>> rulesMap;

	@Override
	public List<ParserRule> retrieveRules(String className) throws ParserException {

		if (rulesMap == null) {
			getInstance();
		}

		return Parliament.decree(rulesMap.get(className));
	}

	@Override
	public List<ParserRule> retrieveRules() throws ParserException {

		if (rulesMap == null) {
			getInstance();
		}

		List<RulePlan> plans =

				rulesMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());

		return Parliament.decree(plans);
	}

}
