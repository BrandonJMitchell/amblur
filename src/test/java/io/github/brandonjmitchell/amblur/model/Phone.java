package io.github.brandonjmitchell.amblur.model;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Phone implements Serializable {

	private static final long serialVersionUID = -693535858189875482L;

	private String mobile;
	private String work;
}
