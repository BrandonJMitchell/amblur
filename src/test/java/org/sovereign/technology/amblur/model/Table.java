package org.sovereign.technology.amblur.model;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Table implements Serializable {

	private static final long serialVersionUID = 5401260848221839230L;
	
	private String content;
	private TableHeader tableHeader;
	private List<TableBodyRow> rows;
}
