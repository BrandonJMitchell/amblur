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
	 * the name of the setMethod the parser should use to set value of the sub object
	 * 
	 */
	
	private String subMapper;
	
	/**
	 * calls method on end event
	 */
	
	private String action;

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
	 * When end tag is found should related object be removed from map
	 * 
	 */

	private boolean removeObject;
	
	/**
	 * List of class names that will be removed when the parent object is removed from map
	 */
	@Singular
	private List<Class<?>> removeLists;

	@Singular
	private List<ParserRule> parserRules;

}
