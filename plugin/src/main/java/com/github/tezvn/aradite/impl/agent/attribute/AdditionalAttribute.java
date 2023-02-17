package com.github.tezvn.aradite.impl.agent.attribute;

import com.github.tezvn.aradite.api.agent.attribute.Attribute;
import com.github.tezvn.aradite.api.agent.attribute.AttributeType;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Additional attribute stats that will be added by items or weapons.
 * 
 * @author phongphong28
 */
public class AdditionalAttribute implements Attribute {

	private final Map<AttributeType, Double> attributes = Maps.newConcurrentMap();

	@Override
	public Map<AttributeType, Double> getAttributeMap() {
		return this.attributes;
	}

	@Override
	public AdditionalAttribute set(AttributeType type, double value) {
		this.attributes.put(type, value);
		return this;
	}
	
}
