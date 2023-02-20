package com.github.tezvn.aradite.api.match.mechanic;

import com.github.tezvn.aradite.api.match.mechanic.ingame.BombCartMechanic;
import com.github.tezvn.aradite.api.match.mechanic.ingame.CaptureMechanic;

/**
 * Types of available mechanics.
 */
public enum MechanicType {
    CAPTURE(CaptureMechanic.class),
    BOMB_CART(BombCartMechanic.class);

    private final Class<? extends Mechanic> wrapper;

    MechanicType(Class<? extends Mechanic> wrapper){
        this.wrapper = wrapper;
    }

    /**
     * Return the wrapper class of the mechanic.
     */
    public Class<? extends Mechanic> getWrapper() {
        return wrapper;
    }
}
