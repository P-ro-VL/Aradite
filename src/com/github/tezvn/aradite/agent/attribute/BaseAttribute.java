package com.github.tezvn.aradite.agent.attribute;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * The default attributes of an agent.
 * 
 * @author phongphong28
 */
public class BaseAttribute implements Attribute {

	private Map<AttributeType, Double> attributes = Maps.newConcurrentMap();

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
