package org.sovereign.technology.amblur.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Builder
@Data
public class RulePlan {
	private String xpath;
	private String mapper;
	private String subMapper;
	private String action;
	private Class<?> clazz;
	private Class<?> parentClazz;
	private boolean collect;
	private boolean removeObject;
	@Singular
	private List<Class<?>> removeLists;
}
