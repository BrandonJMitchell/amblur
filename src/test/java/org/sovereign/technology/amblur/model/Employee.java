package org.sovereign.technology.amblur.model;

import java.io.Serializable;
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
}
