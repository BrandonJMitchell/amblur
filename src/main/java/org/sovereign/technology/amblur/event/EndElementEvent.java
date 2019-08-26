package org.sovereign.technology.amblur.event;

import javax.xml.stream.events.XMLEvent;

import org.sovereign.technology.amblur.parser.ParserManager;

public class EndElementEvent extends AbstractParserEvent {

	public EndElementEvent() {}
	
	public EndElementEvent(ParserManager manager) {
		this.manager = manager;
	}

	@Override
	public int getType() {
		return XMLEvent.END_ELEMENT;
	}

}
