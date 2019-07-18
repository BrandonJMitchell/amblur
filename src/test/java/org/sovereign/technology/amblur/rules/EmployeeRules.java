package org.sovereign.technology.amblur.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.sovereign.technology.amblur.model.Address;
import org.sovereign.technology.amblur.model.Email;
import org.sovereign.technology.amblur.model.Employee;
import org.sovereign.technology.amblur.model.Phone;
import org.sovereign.technology.amblur.model.RulePlan;

public class EmployeeRules extends AbstractPaserRules {

	private static final String PREFACE = "employees/";
	private static final String ADDRESS_PREFACE = PREFACE + "+employee/addresses";
	private static final String PHONE_PREFACE = PREFACE + "+employee/+phone";
	private static final String EMAIL_PREFACE = PREFACE + "+employee/+emails";
	
	@Override
	public Map<String, List<RulePlan>> getInstance() {
	    if (rulesMap == null) {
	        rulesMap = new LinkedHashMap<>();
	        rulesMap.put(Employee.class.getSimpleName(), createEmployeeRules());
	        rulesMap.put(Address.class.getSimpleName(), createAddressRules());
	        rulesMap.put(Phone.class.getSimpleName(), createPhoneRules());
	        rulesMap.put(Email.class.getSimpleName(), createEmailsRules());
	      }

	      return rulesMap;
	}

	@Override
	public Class<Employee> retrieveClass() {
		return Employee.class;
	}
	
	private List<RulePlan> createEmployeeRules() {
		return new ArrayList<>(Arrays.asList(
				
				 RulePlan.builder()
				 		 .clazz(Employee.class)
				 		 .mapper("setHireDate")
				 		 .xpath(PREFACE + "+employee/@hireDate").build(),
				 		 
				 RulePlan.builder()
				 		 .clazz(Employee.class)
				 		 .mapper("setId")
				 		 .xpath(PREFACE + "+employee/id").build(),
				 		 
				 RulePlan.builder()
				 		 .clazz(Employee.class)
				 		 .mapper("setFirstName")
				 		 .xpath(PREFACE + "+employee/firstName").build(),
				 		 
				 RulePlan.builder()
				 		 .clazz(Employee.class)
				 		 .mapper("setLastName")
				 		 .xpath(PREFACE + "+employee/lastName").build(),
				 		 
				 RulePlan.builder()
				 		 .parentClazz(Employee.class)
				 		 .clazz(Address.class)
				 		 .collect(true)
				 		 .mapper("setAddressList")
				 		 .xpath(ADDRESS_PREFACE + "/+address").build(),
				 		 
		 		RulePlan.builder()
				 		 .parentClazz(Employee.class)
				 		 .clazz(Phone.class)
				 		 .mapper("setPhone")
				 		 .xpath(PHONE_PREFACE).build(),
				 		 
				RulePlan.builder()
				 		 .parentClazz(Employee.class)
				 		 .clazz(Email.class)
				 		 .collect(true)
				 		 .mapper("setEmails")
				 		 .xpath(EMAIL_PREFACE).build()
				
			));
	}
	
	private List<RulePlan> createAddressRules() {
		return new ArrayList<>(Arrays.asList(

			 RulePlan.builder()
			 		 .clazz(Address.class)
			 		 .mapper("setStreet")
			 		 .xpath(ADDRESS_PREFACE + "/+address/street").build(),
			 		 
			 RulePlan.builder()
			 		 .clazz(Address.class)
			 		 .mapper("setCity")
			 		 .xpath(ADDRESS_PREFACE + "/+address/city").build(),
			 		 
			 RulePlan.builder()
			 		 .clazz(Address.class)
			 		 .mapper("setState")
			 		 .xpath(ADDRESS_PREFACE + "/+address/state").build(),
			 		 
			 RulePlan.builder()
			 		 .clazz(Address.class)
			 		 .mapper("setPostalCode")
			 		 .xpath(ADDRESS_PREFACE + "/+address/postalCode").build()
			 		 
		));
	}
	
	private List<RulePlan> createPhoneRules() {
		return new ArrayList<>(Arrays.asList(

				RulePlan.builder()
				 		.clazz(Phone.class)
				 		.mapper("setMobile")
				 		.xpath(PHONE_PREFACE + "/mobile").build(),
				 		
				 RulePlan.builder()
				 		.clazz(Phone.class)
				 		.mapper("setWork")
				 		.xpath(PHONE_PREFACE + "/work").build()
		));
	}
	
	private List<RulePlan> createEmailsRules() {
		return new ArrayList<>(Arrays.asList(

				RulePlan.builder()
				 		.clazz(Email.class)
				 		.mapper("setValue")
				 		.removeObject(true)
				 		.xpath(EMAIL_PREFACE + "/+email").build()
		));
	}
}
