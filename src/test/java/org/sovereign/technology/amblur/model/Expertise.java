package org.sovereign.technology.amblur.model;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Expertise implements Serializable {

	private static final long serialVersionUID = -6714007427607315412L;
	
	private String name;
	private List<Skill> skills;
}
