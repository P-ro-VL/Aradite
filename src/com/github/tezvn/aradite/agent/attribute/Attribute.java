package com.github.tezvn.aradite.agent.attribute;

import java.util.Map;

/**
 * The stats of an agent.
 * 
 * @author phongphong28
 */
public interface Attribute {

	/**
	 * Return the attribute data.
	 */
	public Map<AttributeType, Double> getAttributeMap();
	
	/**
	 * Change the attribute value of a specific attribute type.
	 * 
	 * @param type
	 *            Attribute Type
	 * @param value
	 *            New value
	 */
	public Attribute set(AttributeType type, double value);
	
}
