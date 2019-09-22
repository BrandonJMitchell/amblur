package org.sovereign.technology.amblur.model;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TableHeader implements Serializable {

	private static final long serialVersionUID = -7651042742706806037L;
	
	private List<TableHeaderData> headers; 
}
