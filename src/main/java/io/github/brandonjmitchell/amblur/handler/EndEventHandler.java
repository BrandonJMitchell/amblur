package io.github.brandonjmitchell.amblur.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import io.github.brandonjmitchell.amblur.event.EndElementEvent;
import io.github.brandonjmitchell.amblur.model.ParserRule;
import io.github.brandonjmitchell.amblur.parser.ParserContext;
import io.github.brandonjmitchell.amblur.parser.ParserManager;
import io.github.brandonjmitchell.amblur.utils.AmblurUtils;
import io.github.brandonjmitchell.amblur.utils.EventUtils;

public class EndEventHandler implements ParsingHandler<EndElementEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(EndEventHandler.class);
	@Override
	public <T> void onEvent(EndElementEvent event) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
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
					handleRule(manager, context, localPart, currentRule);
					EventUtils.deleteLastPath(manager);

				}
			}
		}
		
	}
	
	private <T> void handleRule(ParserManager manager, ParserContext context, String localPart, ParserRule currentRule) throws NoSuchMethodException, SecurityException, 
	IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (currentRule != null && localPart != null
				&& localPart.equals(currentRule.getElementName())
				&& currentRule.isInstanceRule()) {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("-----xmlEvent END rule => {}", currentRule.getElementName());
			}

			AmblurUtils.resetRule(currentRule);
			
			Map<Class<?>, T> objMap = (Map<Class<?>, T>) context.getObjMap();
			Map<Class<?>, List<?>> objListMap = context.getObjListMap();
			
			T parentObj = retrieveParentObj(objMap, currentRule);
			T obj = retrieveObj(objMap, currentRule);
			List<T> objList = retrieveObjList(objListMap, currentRule);

			adjustMaps(objListMap, objMap, objList, obj, currentRule, localPart);
			
			ParserRule parentRule = context.getParentRule();
			
			handleParentRule(parentObj, parentRule, currentRule);
			cleanUpObjListMap(objListMap, parentRule, localPart);

		}
	}
	
	private <T> void cleanUpObjListMap(Map<Class<?>, List<?>> objListMap, ParserRule parentRule, String localPart) {
		if (parentRule  != null && parentRule.getClazz() != null
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
	
	private <T> void handleParentRule(T parentObj, ParserRule parentRule, ParserRule currentRule) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if(parentObj != null && !StringUtils.isEmpty(currentRule.getParentClazz()) && !StringUtils.isEmpty(currentRule.getAction())) {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("-----parentClass => {} ::: currentRule.getAction() => {}", parentRule.getClazz(), currentRule.getAction());
			}
			Method actionMethod = currentRule.getParentClazz().getMethod(currentRule.getAction());
			actionMethod.invoke(parentObj);
		}
	}
	
	private <T> void adjustMaps(Map<Class<?>, List<?>> objListMap, Map<Class<?>, T> objMap, List<T> objList, T obj, ParserRule currentRule, String localPart) {
		if (objList != null && obj != null) {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("-----ADDING OBJ to LIST => {} ::: {}", obj, objList.size());
			}
			objList.add(obj);
			removeFromObjMap(objMap, currentRule, localPart);
		}

		removeFromObjListMap(objListMap, currentRule, localPart);
	}
	
	private <T> void removeFromObjMap(Map<Class<?>, T> objMap ,ParserRule currentRule, String localPart) {
		if (currentRule.isRemoveObject()) {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("-----REMOVING  OBJ from MAP => {} ::: {}", localPart, currentRule.getElementName());
			}
			objMap.remove(currentRule.getClazz());
		}
	}
	
	private <T> void removeFromObjListMap(Map<Class<?>, List<?>> objListMap ,ParserRule currentRule, String localPart) {
		if (objListMap != null
				&& !currentRule.getRemoveLists().isEmpty()) {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("-----REMOVING  OBJ from LIST MAP => {} ::: {}", localPart, currentRule.getElementName());
			}
			currentRule.getRemoveLists().stream().forEach(clazz -> {
				if (clazz != null) {
					objListMap.remove(clazz);
				}
			});
		}
	}
	
	private <T> T retrieveParentObj(Map<Class<?>, T> objMap, ParserRule currentRule) {
		T parentObj = null;
		if(objMap != null && !StringUtils.isEmpty(currentRule.getParentClazz())) {
			parentObj = objMap.get(currentRule.getParentClazz());
		}
		
		return parentObj;
	}
	
	private <T> T retrieveObj(Map<Class<?>, T> objMap, ParserRule currentRule) {
		T obj = null;
		if(objMap != null && !StringUtils.isEmpty(currentRule.getClazz())) {
			obj = objMap.get(currentRule.getClazz());
		}
				
		return obj;
	}

	private <T> List<T> retrieveObjList(Map<Class<?>, List<?>> objListMap,  ParserRule currentRule) {
		List<T> objList = null;

		if (objListMap != null) {
			objList = (List<T>) objListMap.get(currentRule.getClazz());
		}
		return objList;
	}
}
