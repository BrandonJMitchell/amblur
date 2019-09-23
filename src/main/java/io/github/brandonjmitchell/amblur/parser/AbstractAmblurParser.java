package io.github.brandonjmitchell.amblur.parser;

import io.github.brandonjmitchell.amblur.dispatcher.AmblurEventDispatcher;
import io.github.brandonjmitchell.amblur.factory.AmblurEventFactory;

public abstract class AbstractAmblurParser implements AmblurParser {

	protected AmblurEventFactory amblurFactory;
	protected AmblurEventDispatcher dispatcher;
	
	protected ParserManager manager;
	
	public AbstractAmblurParser(AmblurEventFactory amblurFactory, AmblurEventDispatcher dispatcher) {
		this.amblurFactory = amblurFactory;
		this.dispatcher = dispatcher;
	}
}
