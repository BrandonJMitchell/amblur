package org.sovereign.technology.amblur.parser;

import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLEventReader;

import org.sovereign.technology.amblur.model.ParserRule;
import org.sovereign.technology.amblur.parliament.Parliament;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ParserManager {

	private XMLEventReader xmlEventReader;
	private Map<String, ParserRule> rules;
	private ParserContext context;
	private String root;
	private String separator;
	private boolean useSeparator;
	
	public String retrieveSeparator() {
		String result = "";
		if (useSeparator) {
			return separator;
		}
		return result;
	}
	
	public List<?> retrieveParentList() {
		if (context != null && context.getParentRule() != null) {
			return context.getObjListMap().get(context.getParentRule().getClazz());
		}
		return null;
	}
}
