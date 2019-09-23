package io.github.brandonjmitchell.amblur.model;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Skill implements Serializable {
	private static final long serialVersionUID = -3676795395516255101L;

	private String name;
	private String experience;
	private String years;
}
