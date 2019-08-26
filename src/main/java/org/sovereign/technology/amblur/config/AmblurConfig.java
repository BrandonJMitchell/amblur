package org.sovereign.technology.amblur.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import javax.xml.stream.events.XMLEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sovereign.technology.amblur.dispatcher.AmblurEventDispatcher;
import org.sovereign.technology.amblur.event.EndElementEvent;
import org.sovereign.technology.amblur.event.ParserEvent;
import org.sovereign.technology.amblur.event.StartElementEvent;
import org.sovereign.technology.amblur.exception.DispatcherException;
import org.sovereign.technology.amblur.factory.AmblurEventFactory;
import org.sovereign.technology.amblur.handler.EndEventHandler;
import org.sovereign.technology.amblur.handler.StartEventHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class AmblurConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(AmblurConfig.class);
	
	@Bean
	public ObjectMapper getObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return objectMapper;
	}
	
	@Bean
	public AmblurEventDispatcher setupDispatchers() {
		AmblurEventDispatcher ambleDispatcher = new AmblurEventDispatcher();
		ambleDispatcher.setHandlerMap(new HashMap<>());
		try {
			ambleDispatcher.register(XMLEvent.START_ELEMENT, new StartEventHandler());
			ambleDispatcher.register(XMLEvent.END_ELEMENT, new EndEventHandler());
		} catch (DispatcherException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return ambleDispatcher;
	}
	
	@Bean
	public AmblurEventFactory setupEventfactory() {
		 Map<Integer, Supplier<ParserEvent>> map = new HashMap<>();
		 map.put(XMLEvent.START_ELEMENT, StartElementEvent::new);
		 map.put(XMLEvent.END_ELEMENT, EndElementEvent::new);
		 return new AmblurEventFactory(Collections.unmodifiableMap(map));
	}
	
}

