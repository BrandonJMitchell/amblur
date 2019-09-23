package io.github.brandonjmitchell.amblur.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TableBodyRow implements Serializable {

	private static final long serialVersionUID = -918261198194379158L;
	
	private List<TableBodyData> values;
	
	public void addToValues(String value) {
		if (values == null) {
			values = new ArrayList<>();
		}
		TableBodyData data = new TableBodyData();
		data.setValue(value);
		
		values.add(data);
	}
}
