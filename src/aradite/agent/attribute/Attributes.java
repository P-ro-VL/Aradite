package aradite.agent.attribute;

public class Attributes {

    public static final BaseAttribute DEFAULT_BASE_ATTRIBUTE = new BaseAttribute();

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
