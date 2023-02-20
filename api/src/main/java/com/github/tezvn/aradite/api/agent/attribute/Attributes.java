package com.github.tezvn.aradite.api.agent.attribute;

import com.google.common.collect.Maps;

import java.util.Map;

public class Attributes {

    public static final Attribute DEFAULT_BASE_ATTRIBUTE = new Attribute() {

        private final Map<AttributeType, Double> attributes = Maps.newConcurrentMap();

        @Override
        public Map<AttributeType, Double> getAttributeMap() {
            return this.attributes;
        }

        @Override
        public Attribute set(AttributeType type, double value) {
            this.attributes.put(type, value);
            return this;
        }

    };

    static {
        DEFAULT_BASE_ATTRIBUTE.set(AttributeType.MAX_HEALTH, 200d);
        DEFAULT_BASE_ATTRIBUTE.set(AttributeType.CURRENT_HEALTH, 200d);
        DEFAULT_BASE_ATTRIBUTE.set(AttributeType.ARMOR, 10d);
        DEFAULT_BASE_ATTRIBUTE.set(AttributeType.MOVEMENT_SPEED, 0.4D);
        DEFAULT_BASE_ATTRIBUTE.set(AttributeType.ABILITY_HASTE, 0d);
        DEFAULT_BASE_ATTRIBUTE.set(AttributeType.CRITICAL_CHANCE, 0D);
        DEFAULT_BASE_ATTRIBUTE.set(AttributeType.CRITICAL_MULTIPLY, 1d);
        DEFAULT_BASE_ATTRIBUTE.set(AttributeType.ARMOR_PENETRATION, 0d);
    }

}
