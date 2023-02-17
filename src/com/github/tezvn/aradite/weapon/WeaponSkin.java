package com.github.tezvn.aradite.weapon;

/**
 * Another appearance of the weapon.
 */
public interface WeaponSkin {

    /**
     * Return the ID of the skin.
     */
    public String getSkinID();

    /**
     * Return the price of the skin.
     */
    public int getPrice();

    /**
     * The model data number of the skin.
     * The number must be a 7-digit number and different from others' ones.
     */
    public int getCustomModelData();

    /**
     * Return the edition of the skin.
     */
    public WeaponSkinEdition getEdition();

    /**
     * Return {@code true} if the skin is a limited skin. {@code false} otherwise.
     */
    public boolean isLimited();

    /**
     * Return the display name of the skin.
     */
    public String getDisplayName();

}
