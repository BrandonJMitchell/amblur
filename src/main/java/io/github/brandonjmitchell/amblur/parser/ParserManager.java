package io.github.brandonjmitchell.amblur.parser;

import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLEventReader;

import io.github.brandonjmitchell.amblur.model.ParserRule;
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
		if (context != null &&  context.getObjListMap() != null && context.getParentRule() != null) {
			return context.getObjListMap().get(context.getParentRule().getClazz());
		}
		return null;
	}
}
