# amblur
Amblur is a Java library that uses a StAX parser and XPaths to extract data.

# Introduction

In Java there are three types of parsers: DOM, SAX, and StAX. DOM parsers work on the entire XML document by loading it into memory and contructing a tree representation.  SAX parsers are event-based and parses the document using callbacks without loading the entire document into memory. A StAX parser is a median between the two.  DOM parsers are very convenient if you are not restricted by memory limitations. So, if you are memory limited StAX is a good solution.  However, if the xml documents you are working with are huge with several diffent types of data models then the coding can become intense with nested IF ELSE statements, FOR loops, and repetitive code.  Amblur reduces that code complexity while using low memory strategies and parsing at an accelerated pace.

# Steps for using amblur
1. Set amblur as a dependency in your project from [Maven Central](https://mvnrepository.com/artifact/io.github.brandonjmitchell/amblur)
2. Create a model(s) that represents the data stored in the xml.  You can create several models but they must all be wrapped by one parent model.
3. Create rules for data extraction into the models. Make sure to extend your rules class with AbstractPaserRules.  This will allow you to use convenient methods. You are creating a map of lists that contain RulePlan models. Use RulePlan builder methods to set the parentClass, class, mapper, and xpath variables. The xpath will need to be inserted with special characters so amblur will know how to deal with them. A plus sign '+' is used to specify the class and the at sign '@' is used when the data you want is an attribute.
4. Pass the xml string and the rules to the ExecutiveParser to return a list of your parent model.
