package io.github.brandonjmitchell.amblur.factory;

import java.util.Map;
import java.util.function.Supplier;

import io.github.brandonjmitchell.amblur.event.ParserEvent;
import io.github.brandonjmitchell.amblur.exception.FactoryException;
import lombok.Getter;

@Getter
public class AmblurEventFactory {


  private final Map<Integer, Supplier<ParserEvent>> map;
  
  public AmblurEventFactory(Map<Integer, Supplier<ParserEvent>> map) {
	  this.map = map;
  }
  
  public ParserEvent getEvent(Integer type) throws FactoryException {
	  if (map == null) {
		  throw new FactoryException("Initialize AmblurEventFactory map.");
	  }
	  Supplier<ParserEvent> supplier = map.get(type);
	  if (supplier != null) {
		  return supplier.get();
	  }
	  return null;
  }
  
}
