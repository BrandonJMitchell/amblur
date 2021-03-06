package io.github.brandonjmitchell.amblur.event;

import javax.xml.stream.events.XMLEvent;

import io.github.brandonjmitchell.amblur.parser.ParserManager;


public class StartElementEvent extends AbstractParserEvent {

	public StartElementEvent() {}
	
	public StartElementEvent(ParserManager manager) {
		this.manager = manager;
	}

	@Override
	public int getType() {
		return XMLEvent.START_ELEMENT;
	}

}
