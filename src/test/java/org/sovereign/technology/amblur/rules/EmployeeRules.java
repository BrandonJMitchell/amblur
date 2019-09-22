package org.sovereign.technology.amblur.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.sovereign.technology.amblur.model.Address;
import org.sovereign.technology.amblur.model.Content;
import org.sovereign.technology.amblur.model.Email;
import org.sovereign.technology.amblur.model.Employee;
import org.sovereign.technology.amblur.model.Expertise;
import org.sovereign.technology.amblur.model.Phone;
import org.sovereign.technology.amblur.model.RulePlan;
import org.sovereign.technology.amblur.model.Skill;
import org.sovereign.technology.amblur.model.Table;
import org.sovereign.technology.amblur.model.TableBodyData;
import org.sovereign.technology.amblur.model.TableBodyRow;
import org.sovereign.technology.amblur.model.TableHeader;
import org.sovereign.technology.amblur.model.TableHeaderData;
import org.springframework.stereotype.Component;

@Component
public class EmployeeRules extends AbstractPaserRules {

	private static final String PREFACE = "employees";
	private static final String ADDRESS_PREFACE = PREFACE + "/+employee/addresses";
	private static final String PHONE_PREFACE = PREFACE + "/+employee/+phone";
	private static final String EMAIL_PREFACE = PREFACE + "/+employee/+emails";
	private static final String EXPERTISE_PREFACE = PREFACE + "/+employee/+expertise";
	private static final String SKILL_PREFACE = PREFACE + "/+employee/+expertise/+skill";
	private static final String HTML_DOC_PREFACE = PREFACE + "/+employee/htmlDocument/component/section/text";
	public EmployeeRules() {
		this.root = PREFACE;
	}
	
	@Override
	public Map<String, List<RulePlan>> getInstance() {
	    if (rulesMap == null) {
	        rulesMap = new LinkedHashMap<>();
	        rulesMap.put(Employee.class.getSimpleName(), createEmployeeRules());
	        rulesMap.put(Address.class.getSimpleName(), createAddressRules());
	        rulesMap.put(Phone.class.getSimpleName(), createPhoneRules());
	        rulesMap.put(Email.class.getSimpleName(), createEmailsRules());
	        rulesMap.put(Expertise.class.getSimpleName(), createExpertiseRules());
	        rulesMap.put(Skill.class.getSimpleName(), createSkillRules());
	        rulesMap.put(Table.class.getSimpleName(), createTableRules());
	        rulesMap.put(TableHeader.class.getSimpleName(), createTableHeaderRules());
	        rulesMap.put(TableHeaderData.class.getSimpleName(), createTableHeaderDataRules());
	        rulesMap.put(TableBodyRow.class.getSimpleName(), createTableBodyRowsRules());
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
				 		 .xpath(PREFACE + "/+employee/@hireDate")
				 		 .build(),
				 		 
				 RulePlan.builder()
				 		 .clazz(Employee.class)
				 		 .mapper("setId")
				 		 .xpath(PREFACE + "/+employee/id")
				 		 .build(),
				 		 
				 RulePlan.builder()
				 		 .clazz(Employee.class)
				 		 .mapper("setFirstName")
				 		 .xpath(PREFACE + "/+employee/firstName")
				 		 .build(),
				 		 
				 RulePlan.builder()
				 		 .clazz(Employee.class)
				 		 .mapper("setLastName")
				 		 .xpath(PREFACE + "/+employee/lastName")
				 		 .build(),
				 		 
				 RulePlan.builder()
				 		 .parentClazz(Employee.class)
				 		 .clazz(Address.class)
				 		 .collect(true)
				 		 .mapper("setAddressList")
				 		 .xpath(ADDRESS_PREFACE + "/+address")
				 		 .build(),
				 		 
		 		RulePlan.builder()
				 		 .parentClazz(Employee.class)
				 		 .clazz(Phone.class)
				 		 .mapper("setPhone")
				 		 .xpath(PHONE_PREFACE)
				 		 .build(),
				 		 
				RulePlan.builder()
				 		 .parentClazz(Employee.class)
				 		 .clazz(Email.class)
				 		 .collect(true)
				 		 .mapper("setEmails")
				 		 .xpath(EMAIL_PREFACE)
				 		 .build(),
				 		 
		 		RulePlan.builder()
				 		 .parentClazz(Employee.class)
				 		 .clazz(Expertise.class)
				 		 .collect(true)
				 		 .removeObject(true)
				 		 .removeList(Skill.class)
				 		 .mapper("setExpertises")
				 		 .xpath(EXPERTISE_PREFACE)
				 		 .build(),
				 		 
		 		 RulePlan.builder()
				 		 .parentClazz(Employee.class)
				 		 .clazz(Content.class)
				 		 .collect(true)
				 		 .subMapper("addToContents")
				 		 .xpath(HTML_DOC_PREFACE + "/+content")
				 		 .build(),
				 		 
		 		RulePlan.builder()
				 		 .parentClazz(Employee.class)
				 		 .clazz(Table.class)
				 		 .collect(true)
				 		 .mapper("setTables")
				 		 .action("setTableContent")
				 		 .xpath(HTML_DOC_PREFACE + "/+table")
				 		 .removeList(TableHeaderData.class)
				 		 .removeList(TableBodyRow.class)
				 		 .build()
				 		 
				 		 
				
			));
	}
	
	private List<RulePlan> createAddressRules() {
		return new ArrayList<>(Arrays.asList(

			 RulePlan.builder()
			 		 .clazz(Address.class)
			 		 .mapper("setStreet")
			 		 .xpath(ADDRESS_PREFACE + "/+address/street")
			 		 .build(),
			 		 
			 RulePlan.builder()
			 		 .clazz(Address.class)
			 		 .mapper("setCity")
			 		 .xpath(ADDRESS_PREFACE + "/+address/city")
			 		 .build(),
			 		 
			 RulePlan.builder()
			 		 .clazz(Address.class)
			 		 .mapper("setState")
			 		 .xpath(ADDRESS_PREFACE + "/+address/state")
			 		 .build(),
			 		 
			 RulePlan.builder()
			 		 .clazz(Address.class)
			 		 .mapper("setPostalCode")
			 		 .xpath(ADDRESS_PREFACE + "/+address/postalCode")
			 		 .build()
			 		 
		));
	}
	
	private List<RulePlan> createPhoneRules() {
		return new ArrayList<>(Arrays.asList(

				RulePlan.builder()
				 		.clazz(Phone.class)
				 		.mapper("setMobile")
				 		.xpath(PHONE_PREFACE + "/mobile")
				 		.build(),
				 		
				 RulePlan.builder()
				 		.clazz(Phone.class)
				 		.mapper("setWork")
				 		.xpath(PHONE_PREFACE + "/work")
				 		.build()
		));
	}
	
	private List<RulePlan> createEmailsRules() {
		return new ArrayList<>(Arrays.asList(

				RulePlan.builder()
				 		.clazz(Email.class)
				 		.mapper("setValue")
				 		.removeObject(true)
				 		.xpath(EMAIL_PREFACE + "/+email")
				 		.build()
		));
	}
	
	private List<RulePlan> createExpertiseRules() {
		return new ArrayList<>(Arrays.asList(

				RulePlan.builder()
				 		.clazz(Expertise.class)
				 		.mapper("setName")
				 		.xpath(EXPERTISE_PREFACE + "/name")
				 		.build(),
				 
		 		RulePlan.builder()
		 				.parentClazz(Expertise.class)
				 		.clazz(Skill.class)
				 		.collect(true)
				 		.removeObject(true)
				 		.mapper("setSkills")
				 		.xpath(SKILL_PREFACE)
				 		.build()
				 		
		));
	}
	
	private List<RulePlan> createSkillRules() {
		return new ArrayList<>(Arrays.asList(

				RulePlan.builder()
				 		.clazz(Skill.class)
				 		.mapper("setName")
				 		.xpath(SKILL_PREFACE + "/name")
				 		.build(),
				 
		 		RulePlan.builder()
				 		.clazz(Skill.class)
				 		.mapper("setExperience")
				 		.xpath(SKILL_PREFACE + "/experience")
				 		.build(),
				 		
		 		RulePlan.builder()
				 		.clazz(Skill.class)
				 		.mapper("setYears")
				 		.xpath(SKILL_PREFACE + "/years")
				 		.build()
				 		
		));
	}
	
	private List<RulePlan> createTableRules() {
		return new ArrayList<>(Arrays.asList(

				RulePlan.builder()
						.parentClazz(Table.class)
				 		.clazz(TableHeader.class)
				 		.mapper("setTableHeader")
				 		.xpath(HTML_DOC_PREFACE + "/+table/+thead")
				 		.build(),
				 		
		 		RulePlan.builder()
						.parentClazz(Table.class)
				 		.clazz(TableBodyRow.class)
				 		.mapper("setRows")
				 		.collect(true)
				 		.xpath(HTML_DOC_PREFACE + "/+table/tbody/+tr")
				 		.build()
				 		
		));
	}
	
	private List<RulePlan> createTableHeaderRules() {
		return new ArrayList<>(Arrays.asList(

				RulePlan.builder()
						.parentClazz(TableHeader.class)
				 		.clazz(TableHeaderData.class)
				 		.collect(true)
				 		.mapper("setHeaders")
				 		.xpath(HTML_DOC_PREFACE + "/+table/+thead/+tr")
				 		.build()
				 		
		));
	}
	
	private List<RulePlan> createTableHeaderDataRules() {
		return new ArrayList<>(Arrays.asList(

				RulePlan.builder()
				 		.clazz(TableHeaderData.class)
				 		.mapper("setValue")
				 		.removeObject(true)
				 		.xpath(HTML_DOC_PREFACE + "/+table/+thead/+tr/+th")
				 		.build()
				 		
		));
	}
	
	private List<RulePlan> createTableBodyRowsRules() {
		return new ArrayList<>(Arrays.asList(

				RulePlan.builder()
						.parentClazz(TableBodyRow.class)
				 		.clazz(TableBodyData.class)
				 		.collect(true)
				 		.subMapper("addToValues")
				 		.removeObject(true)
				 		.xpath(HTML_DOC_PREFACE + "/+table/tbody/+tr/+td")
				 		.build()
				 		
		));
	}
}
