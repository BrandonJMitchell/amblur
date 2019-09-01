package org.sovereign.technology.amblur.dispatcher;

import java.lang.reflect.InvocationTargetException;

import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sovereign.technology.amblur.event.ParserEvent;
import org.sovereign.technology.amblur.exception.DispatcherException;
import org.sovereign.technology.amblur.exception.ParserException;
import org.sovereign.technology.amblur.handler.ParsingHandler;

public class AmblurEventDispatcher extends AbstractParsingDispatcher {

	private static final Logger LOGGER = LoggerFactory.getLogger(AmblurEventDispatcher.class);

	@Override
	public <E extends ParserEvent> void register(int eventType,
			ParsingHandler<E> handler) throws DispatcherException {
		if (this.handlerMap == null) {
			throw new DispatcherException("Unable to register handler because handlerMap is null.");
		}
		
		this.handlerMap.put(Integer.valueOf(eventType), (ParsingHandler<ParserEvent>) handler);
	}

	@Override
	public <E extends ParserEvent> void dispatch(E event)
			throws DispatcherException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, ParserException, XMLStreamException {
		if (this.handlerMap == null) {
			throw new DispatcherException("Unable to dispatch event because handlerMap is null.");
		}
		ParsingHandler<ParserEvent> handler = this.handlerMap.get(Integer.valueOf(event.getType()));
		if (handler != null) {
			handler.onEvent(event);
		} else {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("A handler for event type {} has not been registered." , event.getType());
			}
		}
	}

}
