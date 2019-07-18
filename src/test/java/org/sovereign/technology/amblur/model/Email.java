package org.sovereign.technology.amblur.model;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Email implements Serializable {

	private static final long serialVersionUID = 872081791407710088L;
	
	private String value;
}
