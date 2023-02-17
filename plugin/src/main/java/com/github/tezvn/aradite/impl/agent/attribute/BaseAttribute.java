package com.github.tezvn.aradite.impl.agent.attribute;

import com.github.tezvn.aradite.api.agent.attribute.Attribute;
import com.github.tezvn.aradite.api.agent.attribute.AttributeType;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * The default attributes of an agent.
 * 
 * @author phongphong28
 */
public class BaseAttribute implements Attribute {

	private final Map<AttributeType, Double> attributes = Maps.newConcurrentMap();

	@Override
	public Map<AttributeType, Double> getAttributeMap() {
		return this.attributes;
	}

	@Override
	public BaseAttribute set(AttributeType type, double value) {
		this.attributes.put(type, value);
		return this;
	}

}
