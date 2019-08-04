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
import org.sovereign.technology.amblur.exception.ParserException;
import org.sovereign.technology.amblur.model.Employee;
import org.sovereign.technology.amblur.parser.ExecutiveParser;
import org.sovereign.technology.amblur.rules.EmployeeRules;
import org.sovereign.technology.amblur.utils.AmblurTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EmployeeParserTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeParserTest.class);
	
	@Autowired
	private ExecutiveParser parser;
	
	private String xml;
	
	@Before
	public void setup() {
		String fileName = "employee.xml";
		String path = "src/test/resources/";
		
		xml = AmblurTestUtils.retrieveContent(path, fileName);
		assertThat(xml, notNullValue());
	}
	
	@Test
	public void employeeParserTest() throws ParserException {
		List<Employee> employees = parser.parse(xml, new EmployeeRules());
		
		
		assertThat(employees, notNullValue());
		employees.forEach(e -> {LOGGER.info("employee => " + e);});
		assertThat(employees.size(), is(1));
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
	}
}
