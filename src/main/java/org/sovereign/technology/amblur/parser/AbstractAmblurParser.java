package org.sovereign.technology.amblur.parser;

import org.sovereign.technology.amblur.dispatcher.AmblurEventDispatcher;
import org.sovereign.technology.amblur.factory.AmblurEventFactory;

public abstract class AbstractAmblurParser implements AmblurParser {

	protected AmblurEventFactory amblurFactory;
	protected AmblurEventDispatcher dispatcher;
	
	protected ParserManager manager;
	
	public AbstractAmblurParser(AmblurEventFactory amblurFactory, AmblurEventDispatcher dispatcher) {
		this.amblurFactory = amblurFactory;
		this.dispatcher = dispatcher;
	}
}
