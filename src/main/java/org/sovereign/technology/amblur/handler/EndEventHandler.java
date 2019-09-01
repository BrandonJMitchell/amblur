package org.sovereign.technology.amblur.handler;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sovereign.technology.amblur.event.EndElementEvent;
import org.sovereign.technology.amblur.model.ParserRule;
import org.sovereign.technology.amblur.parser.ParserContext;
import org.sovereign.technology.amblur.parser.ParserManager;
import org.sovereign.technology.amblur.utils.AmblurUtils;
import org.sovereign.technology.amblur.utils.EventUtils;

public class EndEventHandler implements ParsingHandler<EndElementEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(EndEventHandler.class);
	@Override
	public <T> void onEvent(EndElementEvent event) {
		if (event != null) {
			ParserManager manager = event.getManager();
			if(manager != null) {
				ParserContext context = manager.getContext();
				if (context != null) {
					String localPart = context.getXmlEvent().asEndElement().getName().getLocalPart();
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("----xmlEvent end localPart => {}", localPart);
					}

					ParserRule currentRule = manager.getRules().get(AmblurUtils.retrieveKey(context.getXpathBuilder(), manager.getRoot()));

					if (currentRule != null && localPart != null
							&& localPart.equals(currentRule.getElementName())
							&& currentRule.isInstanceRule()) {
						if (LOGGER.isTraceEnabled()) {
							LOGGER.trace("-----xmlEvent END rule => {}", currentRule.getElementName());
						}

						AmblurUtils.resetRule(currentRule);
						
						Map<Class<?>, T> objMap = (Map<Class<?>, T>) context.getObjMap();
						Map<Class<?>, List<?>> objListMap = context.getObjListMap();
						
						T obj = null;
						List<T> objList = null;

						if (objMap != null) {
							obj = objMap.get(currentRule.getClazz());
						}

						if (objListMap != null) {
							objList = (List<T>) objListMap.get(currentRule.getClazz());
						}

						if (objList != null && obj != null) {
							if (LOGGER.isTraceEnabled()) {
								LOGGER.trace("-----ADDING OBJ to LIST => {} ::: {}"
										, localPart
										, currentRule.getElementName());
							}
							objList.add(obj);

							if (currentRule.isRemoveObject()) {
								if (LOGGER.isTraceEnabled()) {
									LOGGER.trace("-----REMOVING  OBJ from MAP => {} ::: {}"
											, localPart
											, currentRule.getElementName());
								}
								objMap.remove(currentRule.getClazz());

							}
						}

						if (objList != null
								&& !currentRule.getRemoveLists().isEmpty()) {
							if (LOGGER.isTraceEnabled()) {
								LOGGER.trace("-----REMOVING  OBJ from LIST MAP => {} ::: {}"
										, localPart
										, currentRule.getElementName());
							}
							currentRule.getRemoveLists().stream().forEach(clazz -> {
								if (clazz != null) {
									objListMap.remove(clazz);
								}
							});
						}
						
						ParserRule parentRule = context.getParentRule();
						if (parentRule  != null && localPart != null
								&& parentRule.getClazz() != null
								&& localPart.equals(parentRule.getElementName())) {
							if (LOGGER.isTraceEnabled()) {
								LOGGER.trace("-----parentClass => {} parentRule.getElementName() => {}", parentRule.getClazz(), parentRule.getElementName());
							}

							final Class<T> pc = (Class<T>) parentRule.getClazz();
							if (objListMap != null) {
								objListMap.keySet().removeIf(key -> !key.equals(pc));
							}

						}

					}

					EventUtils.deleteLastPath(manager);

				}
			}
		}
		
	}

}
