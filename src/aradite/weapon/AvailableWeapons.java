package aradite.weapon;

import aradite.weapon.gun.Gun;
import aradite.weapon.gun.type.Bronco;
import aradite.weapon.gun.type.Bucky;
import aradite.weapon.knife.Knife;
import aradite.weapon.knife.type.Dagger;

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
