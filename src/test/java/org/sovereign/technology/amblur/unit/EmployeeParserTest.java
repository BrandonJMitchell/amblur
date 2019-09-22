package org.sovereign.technology.amblur.unit;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sovereign.technology.amblur.model.Employee;
import org.sovereign.technology.amblur.parser.AmblurParserImpl;
import org.sovereign.technology.amblur.rules.EmployeeRules;
import org.sovereign.technology.amblur.utils.AmblurTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles({"test"})
public class EmployeeParserTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeParserTest.class);
	@Autowired
	private ObjectMapper mapper;
	@Autowired
	private AmblurParserImpl parser;
	@Autowired
	private EmployeeRules employeeRules;
	
	private String xml;
	
	@Before
	public void setup() {
		final String fileName = "employee.xml";
		final String path = "src/test/resources/";
		
		xml = AmblurTestUtils.retrieveContent(path, fileName);
		assertThat(xml, notNullValue());
	}
	
	@Test
	public void employeeParserTest() throws Exception {
		List<Employee> employees = parser.parse(xml, employeeRules);	
		
		assertThat(employees, notNullValue());
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(mapper.writeValueAsString(employees));
		}
		assertThat(employees.size(), is(2));
		assertThat(employees.get(0), notNullValue());
		assertThat(employees.get(0).getHireDate(), is("02-13-2003"));
		assertThat(employees.get(0).getId(), is("xzn-4578"));
		assertThat(employees.get(0).getFirstName(), is("John"));
		assertThat(employees.get(0).getLastName(), is("Smith"));
		assertThat(employees.get(0).getAddressList(), notNullValue());
		assertThat(employees.get(0).getAddressList().size(), is(1));
		assertThat(employees.get(0).getAddressList().get(0), notNullValue());
		assertThat(employees.get(0).getAddressList().get(0).getStreet(), is("2278 Poplar Ave."));
		assertThat(employees.get(0).getAddressList().get(0).getCity(), is("Memphis"));
		assertThat(employees.get(0).getAddressList().get(0).getState(), is("TN"));
		assertThat(employees.get(0).getAddressList().get(0).getPostalCode(), is("38015"));
		assertThat(employees.get(0).getPhone(), notNullValue());
		assertThat(employees.get(0).getPhone().getMobile(), is("9013838721"));
		assertThat(employees.get(0).getPhone().getWork(), is("16158239743"));
		assertThat(employees.get(0).getEmails(), notNullValue());
		assertThat(employees.get(0).getEmails().size(), is(2));
		assertThat(employees.get(0).getEmails().get(0), notNullValue());
		assertThat(employees.get(0).getEmails().get(0).getValue(), is("john.smith@company.org"));
		assertThat(employees.get(0).getEmails().get(1), notNullValue());
		assertThat(employees.get(0).getEmails().get(1).getValue(), is("john.smith@business.com"));
		assertThat(employees.get(0).getExpertises(), notNullValue());
		assertThat(employees.get(0).getExpertises().get(0), notNullValue());
		assertThat(employees.get(0).getExpertises().get(0).getName(), is("IT Manager"));
		assertThat(employees.get(0).getExpertises().get(0).getSkills(), notNullValue());
		assertThat(employees.get(0).getExpertises().get(0).getSkills().size(), is(2));
		assertThat(employees.get(0).getExpertises().get(0).getSkills().get(0), notNullValue());
		assertThat(employees.get(0).getExpertises().get(0).getSkills().get(0).getName(), is("Communication"));
		assertThat(employees.get(0).getExpertises().get(0).getSkills().get(0).getExperience(), is("Senior"));
		assertThat(employees.get(0).getExpertises().get(0).getSkills().get(0).getYears(), is("25"));
		assertThat(employees.get(0).getExpertises().get(0).getSkills().get(1), notNullValue());
		assertThat(employees.get(0).getExpertises().get(0).getSkills().get(1).getName(), is("Excel"));
		assertThat(employees.get(0).getExpertises().get(0).getSkills().get(1).getExperience(), is("Sophmore"));
		assertThat(employees.get(0).getExpertises().get(0).getSkills().get(1).getYears(), is("4"));
		assertThat(employees.get(0).getExpertises().get(1), notNullValue());
		assertThat(employees.get(0).getExpertises().get(1).getName(), is("Budgeting"));
		assertThat(employees.get(0).getExpertises().get(1).getSkills(), notNullValue());
		assertThat(employees.get(0).getExpertises().get(1).getSkills().size(), is(2));
		assertThat(employees.get(0).getExpertises().get(1).getSkills().get(0), notNullValue());
		assertThat(employees.get(0).getExpertises().get(1).getSkills().get(0).getName(), is("Accounting"));
		assertThat(employees.get(0).getExpertises().get(1).getSkills().get(0).getExperience(), is("Senior"));
		assertThat(employees.get(0).getExpertises().get(1).getSkills().get(0).getYears(), is("15"));
		assertThat(employees.get(0).getContents(), notNullValue());
		assertThat(employees.get(0).getContents().size(), is(1));
		assertThat(employees.get(0).getContents().get(0), notNullValue());
		assertThat(employees.get(0).getContents().get(0).getValue(), is("Common Hobbies"));
		assertThat(employees.get(0).getTables(), notNullValue());
		assertThat(employees.get(0).getTables().size(), is(1));
		assertThat(employees.get(0).getTables().get(0), notNullValue());
		assertThat(employees.get(0).getTables().get(0).getContent(), is("Common Hobbies"));
		assertThat(employees.get(0).getTables().get(0).getTableHeader(), notNullValue());
		assertThat(employees.get(0).getTables().get(0).getTableHeader().getHeaders(), notNullValue());
		assertThat(employees.get(0).getTables().get(0).getTableHeader().getHeaders().size(), is(3));
		assertThat(employees.get(0).getTables().get(0).getTableHeader().getHeaders().get(0).getValue(), is("Hobbies"));
		assertThat(employees.get(0).getTables().get(0).getTableHeader().getHeaders().get(1).getValue(), is("Acquire Date"));
		assertThat(employees.get(0).getTables().get(0).getTableHeader().getHeaders().get(2).getValue(), is("Status"));
		assertThat(employees.get(0).getTables().get(0).getRows(), notNullValue());
		assertThat(employees.get(0).getTables().get(0).getRows().size(), is(3));
		assertThat(employees.get(0).getTables().get(0).getRows().get(0), notNullValue());
		assertThat(employees.get(0).getTables().get(0).getRows().get(0).getValues(), notNullValue());
		assertThat(employees.get(0).getTables().get(0).getRows().get(0).getValues().size(), is(3));
		assertThat(employees.get(0).getTables().get(0).getRows().get(0).getValues().get(0).getValue(), is("Juggling"));
		assertThat(employees.get(0).getTables().get(0).getRows().get(0).getValues().get(1).getValue(), is("July 1st, 2019"));
		assertThat(employees.get(0).getTables().get(0).getRows().get(0).getValues().get(2).getValue(), is("Intermediate"));		
		
		assertThat(employees.get(1), notNullValue());
		assertThat(employees.get(1).getHireDate(), is("02-13-2013"));
		assertThat(employees.get(1).getId(), is("agt-4158"));
		assertThat(employees.get(1).getFirstName(), is("John"));
		assertThat(employees.get(1).getLastName(), is("Taylor"));
		assertThat(employees.get(1).getAddressList(), notNullValue());
		assertThat(employees.get(1).getAddressList().size(), is(2));
		assertThat(employees.get(1).getAddressList().get(0), notNullValue());
		assertThat(employees.get(1).getAddressList().get(0).getStreet(), is("2278 Germantown Rd."));
		assertThat(employees.get(1).getAddressList().get(0).getCity(), is("Cordova"));
		assertThat(employees.get(1).getAddressList().get(0).getState(), is("TN"));
		assertThat(employees.get(1).getAddressList().get(0).getPostalCode(), is("38016"));
		assertThat(employees.get(1).getPhone(), notNullValue());
		assertThat(employees.get(1).getPhone().getMobile(), is("9013838791"));
		assertThat(employees.get(1).getPhone().getWork(), is("16158239983"));
		assertThat(employees.get(1).getEmails(), notNullValue());
		assertThat(employees.get(1).getEmails().size(), is(2));
		assertThat(employees.get(1).getEmails().get(0), notNullValue());
		assertThat(employees.get(1).getEmails().get(0).getValue(), is("john.taylor@company.org"));
		assertThat(employees.get(1).getEmails().get(1), notNullValue());
		assertThat(employees.get(1).getEmails().get(1).getValue(), is("john.taylor@business.com"));
		assertThat(employees.get(1).getExpertises(), notNullValue());
		assertThat(employees.get(1).getExpertises().get(0).getName(), is("Software Developer"));
		assertThat(employees.get(1).getExpertises().get(0).getSkills(), notNullValue());
		assertThat(employees.get(1).getExpertises().get(0).getSkills().size(), is(2));
		assertThat(employees.get(1).getExpertises().get(0).getSkills().get(0), notNullValue());
		assertThat(employees.get(1).getExpertises().get(0).getSkills().get(0).getName(), is("Java"));
		assertThat(employees.get(1).getExpertises().get(0).getSkills().get(0).getExperience(), is("Senior"));
		assertThat(employees.get(1).getExpertises().get(0).getSkills().get(0).getYears(), is("15"));
		assertThat(employees.get(1).getExpertises().get(0).getSkills().get(1), notNullValue());
		assertThat(employees.get(1).getExpertises().get(0).getSkills().get(1).getName(), is("SQL"));
		assertThat(employees.get(1).getExpertises().get(0).getSkills().get(1).getExperience(), is("Sophmore"));
		assertThat(employees.get(1).getExpertises().get(0).getSkills().get(1).getYears(), is("4"));
		assertThat(employees.get(1).getTables(), notNullValue());
		assertThat(employees.get(1).getTables().size(), is(1));
		assertThat(employees.get(1).getTables().get(0), notNullValue());
		assertThat(employees.get(1).getTables().get(0).getContent(), is("Common Hobbies"));
		assertThat(employees.get(1).getTables().get(0).getTableHeader(), notNullValue());
		assertThat(employees.get(1).getTables().get(0).getTableHeader().getHeaders(), notNullValue());
		assertThat(employees.get(1).getTables().get(0).getTableHeader().getHeaders().size(), is(3));
		assertThat(employees.get(1).getTables().get(0).getTableHeader().getHeaders().get(0).getValue(), is("Hobbies"));
		assertThat(employees.get(1).getTables().get(0).getTableHeader().getHeaders().get(1).getValue(), is("Acquire Date"));
		assertThat(employees.get(1).getTables().get(0).getTableHeader().getHeaders().get(2).getValue(), is("Status"));
		assertThat(employees.get(1).getTables().get(0).getRows(), notNullValue());
		assertThat(employees.get(1).getTables().get(0).getRows().size(), is(1));
		assertThat(employees.get(1).getTables().get(0).getRows().get(0), notNullValue());
		assertThat(employees.get(1).getTables().get(0).getRows().get(0).getValues(), notNullValue());
		assertThat(employees.get(1).getTables().get(0).getRows().get(0).getValues().size(), is(3));
		assertThat(employees.get(1).getTables().get(0).getRows().get(0).getValues().get(0).getValue(), is("Sleeping"));
		assertThat(employees.get(1).getTables().get(0).getRows().get(0).getValues().get(1).getValue(), is("July 1st, 1971"));
		assertThat(employees.get(1).getTables().get(0).getRows().get(0).getValues().get(2).getValue(), is("Intermediate"));
	}
}
