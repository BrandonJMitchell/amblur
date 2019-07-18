package org.sovereign.technology.amblur.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Builder
@Data
public class ParserRule {

	/**
	 * 
	 * the name of this class's parent
	 * 
	 */

	private Class<?> parentClazz;

	/**
	 * 
	 * the name of the class to be instantiated
	 * 
	 */

	private Class<?> clazz;

	/**
	 * 
	 * the name of the setMethod the parser should use to set value
	 * 
	 */

	private String mapper;

	/**
	 * 
	 * Should the object be collected in a list
	 * 
	 */

	private boolean collect;

	/**
	 * 
	 * xml element that is being searched for
	 * 
	 */

	private String elementName;

	/**
	 * 
	 * path taken to find this element in xml document
	 * 
	 */

	private String xpath;

	/**
	 * 
	 * If true then create new instance of object
	 * 
	 */

	private boolean instanceRule;

	/**
	 * 
	 * Is the element value an attribute?
	 * 
	 */

	private boolean attribute;

	/**
	 * 
	 * If this element is found should the parser search through its children
	 * 
	 */

	private boolean endPoint;

	/**
	 * 
	 * True if this rule has been used before
	 * 
	 */

	private boolean found;

	/**
	 * 
	 * Sibling (based off priority)/Child rules (based if parent was found)
	 * 
	 */

	/**
	 * 
	 * When end tag is found should related object be removed from map
	 * 
	 */

	private boolean removeObject;

	@Singular
	private List<ParserRule> parserRules;

}
