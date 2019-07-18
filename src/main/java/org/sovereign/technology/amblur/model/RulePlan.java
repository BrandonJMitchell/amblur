package org.sovereign.technology.amblur.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RulePlan {
	private String xpath;
	private String mapper;
	private Class<?> clazz;
	private Class<?> parentClazz;
	private boolean collect;
	private boolean removeObject;
}
