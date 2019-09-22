package org.sovereign.technology.amblur.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Employee implements Serializable {

	private static final long serialVersionUID = 1893852486504053821L;

	private String id;
	private String hireDate;
	private String firstName;
	private String lastName;
	private List<Address> addressList;
	private Phone phone;
	private List<Email> emails;
	private List<Expertise> expertises;
	private List<Content> contents;
	private List<Table> tables;
	
	public void addToContents (String value) {
		if(contents == null) {
			contents = new ArrayList<>();
		}
		Content data = new Content();
		data.setValue(value);
		
		contents.add(data);
	}
	
	public void setTableContent() {
		if (contents != null && !contents.isEmpty() && tables != null && !tables.isEmpty()) {
			Table table = tables.get(tables.size() - 1);
			Content data = contents.get(contents.size() - 1);
			if(data != null) {
				table.setContent(data.getValue());
			}
		}
	}
}
