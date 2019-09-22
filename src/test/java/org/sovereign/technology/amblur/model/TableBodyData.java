package org.sovereign.technology.amblur.model;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TableBodyData implements Serializable {

	private static final long serialVersionUID = -5774250231681628931L;
	private String value;
}
