package io.github.brandonjmitchell.amblur.parser;

import java.util.List;
import java.util.Map;

import javax.xml.stream.events.XMLEvent;

import io.github.brandonjmitchell.amblur.model.ParserRule;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ParserContext {

	
	private ParserRule parentRule;
	private Map<Class<?>, ?> objMap;
	private Map<Class<?>, List<?>> objListMap;
	private XMLEvent xmlEvent;
	private StringBuilder xpathBuilder;
	

}
