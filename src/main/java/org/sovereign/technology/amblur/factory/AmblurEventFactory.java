package org.sovereign.technology.amblur.factory;

import java.util.Map;
import java.util.function.Supplier;

import org.sovereign.technology.amblur.event.ParserEvent;
import org.sovereign.technology.amblur.exception.FactoryException;
import lombok.Getter;

@Getter
public class AmblurEventFactory {


  private final Map<Integer, Supplier<ParserEvent>> map;
  
  public AmblurEventFactory(Map<Integer, Supplier<ParserEvent>> map) {
	  this.map = map;
  }
  
  public ParserEvent getEvent(Integer type) throws FactoryException {
	  if (map == null) {
		  throw new FactoryException("Initialize AbleFactory map.");
	  }
	  Supplier<ParserEvent> supplier = map.get(type);
	  if (supplier != null) {
		  return supplier.get();
	  }
	  return null;
  }
  
}
