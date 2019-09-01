package org.sovereign.technology.amblur.handler;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sovereign.technology.amblur.event.StartElementEvent;
import org.sovereign.technology.amblur.exception.ParserException;
import org.sovereign.technology.amblur.model.ParserRule;
import org.sovereign.technology.amblur.parser.ParserContext;
import org.sovereign.technology.amblur.parser.ParserManager;
import org.sovereign.technology.amblur.utils.AmblurUtils;
import org.sovereign.technology.amblur.utils.EventUtils;

public class StartEventHandler implements ParsingHandler<StartElementEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(StartEventHandler.class);

	@Override
	public <T> void onEvent(StartElementEvent event)
			throws ParserException, InstantiationException,
			IllegalAccessException, NoSuchMethodException,
			InvocationTargetException, XMLStreamException {
		if (event != null) {
			ParserManager manager = event.getManager();
			if (manager != null) {
				ParserContext context = manager.getContext();
				if (context != null) {

					String localPart = context.getXmlEvent().asStartElement().getName().getLocalPart();
					StringBuilder xpathBuilder = context.getXpathBuilder().append(manager.retrieveSeparator()).append(localPart);
					context.setXpathBuilder(xpathBuilder);

					Map<Class<?>, T> objMap = (Map<Class<?>, T>) context.getObjMap();
					Map<Class<?>, List<?>> objListMap = context.getObjListMap();

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("-----xmlEvent start localData => {} ::: {}", localPart, xpathBuilder);
					}

					ParserRule currentRule = manager.getRules().get(AmblurUtils.retrieveKey(xpathBuilder, manager.getRoot()));

					if (currentRule != null) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("-----currentRule FOUND------");
						}

						if (currentRule.isInstanceRule()) {
							if (LOGGER.isTraceEnabled()) {
								LOGGER.trace("-----currentRule isInstanceRule------");
							}
							if (currentRule.getParentClazz() != null) {
								handleSubObject(manager, currentRule, objMap, objListMap);
							} else {
								handleParentRule(manager, context, currentRule, objMap, objListMap);
							}
						} else {
							handleNonInstanceRule(manager, objMap, currentRule);
						}
					}
				}
			}
		}

	}

	private <T> void handleNonInstanceRule(ParserManager manager,
			Map<Class<?>, T> objMap, ParserRule currentRule)
			throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, XMLStreamException {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("----- currentRule is NOT InstanceRule");
		}

		T obj = objMap.get(currentRule.getClazz());

		EventUtils.setElementValue(manager, currentRule, obj);

	}

	private <T> void handleParentRule(ParserManager manager,
			ParserContext context, ParserRule currentRule,
			Map<Class<?>, T> objMap, Map<Class<?>, List<?>> objListMap)
			throws InstantiationException, IllegalAccessException,
			NoSuchMethodException, InvocationTargetException,
			XMLStreamException {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("-----Parent Rule => {}", currentRule.getElementName());
		}

		T obj = (T) EventUtils.generateObject(currentRule.getClazz());

		objMap.put((Class<T>) currentRule.getClazz(), obj);

		List<T> objList = (List<T>) objListMap.get(currentRule.getClazz());

		if (objListMap.isEmpty()) {
			context.setParentRule(currentRule);
		}

		if (objList == null) {

			objList = (List<T>) EventUtils.genericList();

			objListMap.put((Class<T>) currentRule.getClazz(), objList);

		}

		EventUtils.setElementValue(manager, currentRule, obj);
	}

	private <T> void handleSubClassList(ParserManager manager,
			ParserRule currentRule, Map<Class<?>, List<?>> objListMap, T obj,
			T subObj) throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, XMLStreamException {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("-----Is SubClass stored in LIST-----");
		}

		List<T> subObjList = (List<T>) objListMap.get(currentRule.getClazz());

		if (subObjList == null) {

			subObjList = (List<T>) EventUtils
					.genericList();

			EventUtils.setSubList(obj, subObjList, currentRule);

			objListMap.put((Class<T>) currentRule.getClazz(), subObjList);

		}

		EventUtils.setElementValue(manager, currentRule, subObj);
	}

	private <T> void handleSubClass(ParserManager manager,
			ParserRule currentRule, T obj, T subObj)
			throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, XMLStreamException {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("-----Is SubClass-----");
		}

		EventUtils.setSubObject(obj, subObj, currentRule);
		EventUtils.setElementValue(manager, currentRule, subObj);
	}

	private <T> void handleSubObject(ParserManager manager,
			ParserRule currentRule, Map<Class<?>, T> objMap,
			Map<Class<?>, List<?>> objListMap)
			throws ParserException, InstantiationException,
			IllegalAccessException, NoSuchMethodException,
			InvocationTargetException, XMLStreamException {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("-----create sub object------");
		}

		T obj = (T) objMap.get(currentRule.getParentClazz());

		if (obj == null) {
			throw new ParserException(
					"Object is null. Check parser rules and make sure it has an instance rule for parent class."
							+ currentRule);
		}

		T subObj = (T) EventUtils.generateObject(currentRule.getClazz());

		objMap.put(currentRule.getClazz(), subObj);

		if (!currentRule.isCollect()) {
			handleSubClass(manager, currentRule, obj, subObj);

		} else if (currentRule.isCollect()) {
			handleSubClassList(manager, currentRule, objListMap, obj, subObj);

		}
	}

}
