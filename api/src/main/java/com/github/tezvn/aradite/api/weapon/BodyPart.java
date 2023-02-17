package com.github.tezvn.aradite.api.weapon;

import org.bukkit.inventory.EquipmentSlot;

import java.util.Arrays;

public enum BodyPart {

    HEAD(EquipmentSlot.HEAD),

    BODY(EquipmentSlot.CHEST),

    LEGS(EquipmentSlot.LEGS, EquipmentSlot.FEET),

    HANDS(EquipmentSlot.HAND, EquipmentSlot.OFF_HAND);

    private final EquipmentSlot[] slots;

    private BodyPart(EquipmentSlot... slots) {
        this.slots = slots;
    }

    /**
     * Return the {@link EquipmentSlot} this part represents.
     */
    public EquipmentSlot[] getSlots() {
        return slots;
    }

    /**
     * Return the body part representing the given {@link EquipmentSlot}
     * @param slot The equipment slot
     * @return The body part.
     */
    public static final BodyPart getBodyPart(EquipmentSlot slot){
        for(BodyPart value : values())
            if(Arrays.asList(value.getSlots()).contains(slot)) return value;
        return null;
    }
}
