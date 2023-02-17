package com.github.tezvn.aradite.agent.attribute;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * Additional attribute stats that will be added by items or weapons.
 * 
 * @author phongphong28
 */
public class AdditionalAttribute implements Attribute {

	private Map<AttributeType, Double> attributes = Maps.newConcurrentMap();

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
