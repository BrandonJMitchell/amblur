package io.github.brandonjmitchell.amblur.event;

import io.github.brandonjmitchell.amblur.parser.ParserManager;

public abstract class AbstractParserEvent implements ParserEvent {

	protected ParserManager manager;

	public ParserManager getManager() {
		return manager;
	}

	public void setManager(ParserManager manager) {
		this.manager = manager;
	}
}
