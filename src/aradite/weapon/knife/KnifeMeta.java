package aradite.weapon.knife;

import aradite.weapon.WeaponMeta;

public interface KnifeMeta extends WeaponMeta  {

    public static final int DEFAULT_DAMAGE = 5;

    /**
     * Return the damage the knife deals.
     */
    public double getDamage();

    /**
     * Change the damage the knife deals.
     * @param damage The damage
     */
    public void setDamage(double damage);

}
