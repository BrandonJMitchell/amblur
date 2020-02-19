# amblur
Amblur is a Java library that uses a StAX parser and custom XPaths to extract data.

# Introduction

In Java there are three types of parsers: DOM, SAX, and StAX. DOM parsers work on the entire XML document by loading it into memory and contructing a tree representation.  SAX parsers are event-based and parses the document using callbacks without loading the entire document into memory. A StAX parser is a median between the two.  DOM parsers are very convenient if you are not restricted by memory limitations. So, if you are memory limited StAX is a good solution.  However, if the xml documents you are working with are huge with several different types of data models then the coding can become intense with nested IF ELSE statements, FOR loops, and repetitive code.  Amblur reduces that code complexity while using low memory strategies and parsing at an accelerated pace.

# Steps for using amblur
1. Set amblur as a dependency in your project from [Maven Central](https://mvnrepository.com/artifact/io.github.brandonjmitchell/amblur)
2. Configure amblur components into your project by scanning packages or creating beans in your @Configuration class.

  Example:
    
    @SpringBootApplication(scanBasePackages = {"org.company.your.package", "org.sovereign.technology.amblur"})
    public class YourApplication {
	    public static void main(String[] args) {
        SpringApplication.run(YourApplication.class, args);
      }
    }
    
  Or:
    Deprecated.
    
    @Configuration
    public class YourAppConfig {

	    @Bean
	    public ExecutiveParser executiveParser() {
		    return new ExecutiveParser();
	    }
    }
    
  Or: Setup a simple configuration file and then autowire AmblurParser in your application
  
    @Configuration
    @ComponentScan("io.github.brandonjmitchell.amblur")
    public class ParserConfig {}
    
  Use the following on the lastest version if you want to add events and handlers.
   
	@Bean
	public AmblurParser amblurParser() {
		return new AmblurParserImpl(setupEventfactory(), setupDispatchers());
	}
	@Primary
	@Bean
	public AmblurEventDispatcher setupDispatchers() {
		AmblurEventDispatcher ambleDispatcher = new AmblurEventDispatcher();
		ambleDispatcher.setHandlerMap(new HashMap<>());
		try {
			ambleDispatcher.register(XMLEvent.START_ELEMENT, new StartEventHandler());
			ambleDispatcher.register(XMLEvent.END_ELEMENT, new EndEventHandler());
			ambleDispatcher.register(XMLEvent.CHARACTERS, new CharactersEventHandler());
		} catch (DispatcherException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return ambleDispatcher;
	}
	
	@Primary
	@Bean
	public AmblurEventFactory setupEventfactory() {
		 Map<Integer, Supplier<ParserEvent>> map = new HashMap<>();
		 map.put(XMLEvent.START_ELEMENT, StartElementEvent::new);
		 map.put(XMLEvent.END_ELEMENT, EndElementEvent::new);
		 map.put(XMLEvent.CHARACTERS, CharactersEvent::new);
		 return new AmblurEventFactory(Collections.unmodifiableMap(map));
	}
	
3. Create a model(s) that represents the data stored in the xml.  You can create several models but they must all be wrapped by one parent model.
4. Create rules for data extraction into the models. Make sure to extend your rules class with AbstractPaserRules.  This will allow you to use convenient methods. You are creating a map of lists that contain RulePlan models. Use RulePlan builder methods to set the parentClass, class, mapper, and xpath variables. The xpath will need to be inserted with special characters so amblur will know how to deal with them. A plus sign '+' is used to specify the class and the at sign '@' is used when the data you want is an attribute.
5. Pass the xml string and the rules to the AmblurParser to return a list of your parent model.

[Here](https://github.com/BrandonJMitchell/amblur/tree/master/src/test) is an example.

# Release Notes

## Version 1.0.0

I hope this code sparks joy.

## Version 1.0.1

- This release should improve parsing speed due to recursive search being removed.
- Optional "root" variable added to AbstractParserRule. This allows for shorter xpaths to be used in the rule compared to what could actually be in the xml document.

## Version 1.0.2

- Sub classes of sub classes' List values were all being collected in the same List. To fix this the method removeLists was added to RulePlan and ParserRule. This will remove the current rule's sublist allowing for another rule of the same type to be populated in a new collection.

## Version 1.1.0

- This release refactors the code into an event driven architecture. This makes the code less complicated and easier for other developers to customize for their needs.
- Methods are made smaller for readability and simplification.
- Classes were renamed: ExecutiveParser is now AmblurParserImpl. Parliament is now AmblurUtils and was split into EventUtils.
- Some XML may contain HTML so two new methods were added to RulePlan and ParserRule to help with this: subMapper and action. See tests for examples of use.

## Version 1.1.3

- Added configuration so this application can be used as a library.
