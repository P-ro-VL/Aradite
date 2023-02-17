package com.github.tezvn.aradite.api.weapon;

import java.util.List;

public interface WeaponMeta {

    /**
     * Return all available skins of the weapon.
     */
    public List<WeaponSkin> getSkins();

    /**
     * Register a new skin for the weapon.
     * @param id The skin id
     * @param modelData The model data of the skin. Must be a 7-digit number
     * @param skinEdition The edition of the skin
     * @param price The skin price. If you want to use the default price, set this parameter to -1.
     * @param isLimited {@code true} if the skin is limited version
     */
    default void registerSkin(String id, String displayname, int modelData, WeaponSkinEdition skinEdition,
                              int price, boolean isLimited) {
        WeaponSkin skin = new WeaponSkin() {
            @Override
            public String getSkinID() {
                return id;
            }

            @Override
            public int getPrice() {
                return price > 0 ? price : getEdition().getDefaultPrice();
            }

            @Override
            public int getCustomModelData() {
                return modelData;
            }

            @Override
            public WeaponSkinEdition getEdition() {
                return skinEdition;
            }

            @Override
            public boolean isLimited() {
                return isLimited;
            }

            @Override
            public String getDisplayName() {
                return displayname;
            }
        };
        getSkins().add(skin);
    }
}
