package io.github.brandonjmitchell.amblur.event;

import io.github.brandonjmitchell.amblur.parser.ParserManager;

public interface ParserEvent {

	public int getType();
	public ParserManager getManager();
	public void setManager(ParserManager manager);
}
