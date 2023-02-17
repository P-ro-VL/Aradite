package com.github.tezvn.aradite.api.weapon;

import com.github.tezvn.aradite.weapon.gun.Gun;
import com.github.tezvn.aradite.weapon.gun.type.Bronco;
import com.github.tezvn.aradite.weapon.gun.type.Bucky;
import com.github.tezvn.aradite.weapon.knife.Knife;
import com.github.tezvn.aradite.weapon.knife.type.Dagger;

public final class AvailableWeapons {

    /*
     * GUNS
     */
    public static Gun STINGER = new Bronco();

    public static Gun BUCKY = new Bucky();

    /*
       KNIVES
     */
    public static Knife DAGGER = new Dagger();
}
