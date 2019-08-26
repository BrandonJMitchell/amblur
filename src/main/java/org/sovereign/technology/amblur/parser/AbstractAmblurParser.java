package org.sovereign.technology.amblur.parser;

import org.sovereign.technology.amblur.dispatcher.AmblurEventDispatcher;
import org.sovereign.technology.amblur.factory.AmblurEventFactory;

public abstract class AbstractAmblurParser implements AmblurParser {

	protected AmblurEventFactory ableFactory;
	protected AmblurEventDispatcher dispatcher;
	
	protected ParserManager manager;
	
	public AbstractAmblurParser(AmblurEventFactory ableFactory, AmblurEventDispatcher dispatcher) {
		this.ableFactory = ableFactory;
		this.dispatcher = dispatcher;
	}
}
