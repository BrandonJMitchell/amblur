package org.sovereign.technology.amblur.event;

import org.sovereign.technology.amblur.parser.ParserManager;

public interface ParserEvent {

	public int getType();
	public ParserManager getManager();
	public void setManager(ParserManager manager);
}
