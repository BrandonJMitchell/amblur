package org.sovereign.technology.amblur.model;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TableHeaderData implements Serializable {

	private static final long serialVersionUID = 4952309958686916125L;
	private String value;
}
