package com.github.tezvn.aradite.api.weapon;

import com.github.tezvn.aradite.api.Aradite;
import com.github.tezvn.aradite.api.AraditeAPIProvider;
import com.github.tezvn.aradite.api.language.Language;
import pdx.mantlecore.message.GradientText;
import pdx.mantlecore.message.TextStyle;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public enum WeaponSkinEdition {

    DEFAULT, POPULAR, DELUXE, PREMIUM(true), EXCLUSIVE(true), ULTRA(true);

    public static final int DEFAULT_POPULAR_SKIN_PRICE = 20,
            DEFAULT_DELUXE_SKIN_PRICE = 35,
            DEFAULT_PREMIUM_SKIN_PRICE = 50,
            DEFAULT_EXCLUSIVE_SKIN_PRICE = 75,
            DEFAULT_ULTRA_SKIN_PRICE = 100,
            DEFAULT_DEFAULT_SKIN_PRICE = 0;

    private final boolean isBold;

    private WeaponSkinEdition() {
        this.isBold = false;
    }

    private WeaponSkinEdition(boolean bold) {
        this.isBold = bold;
    }

    private Language lang = AraditeAPIProvider.get().getLanguage();

    /**
     * Apply the gradient color of the skin edition to the given weapon name.
     *
     * @param weaponName The weapon name
     * @return The gradient weapon name
     */
    public String parseDisplayName(String weaponName, boolean isLimited) {
        String gradientColor = lang.getString("weapon.skin-editions." + this.toString() + ".color");
        List<TextStyle> styles = new ArrayList<>();
        if (isBold) styles.add(TextStyle.BOLD);
        if (isLimited) styles.add(TextStyle.ITALIC);
        return this == DEFAULT ? "§7" + weaponName :
                GradientText.toGradient(weaponName, gradientColor, "&f", styles.toArray(new TextStyle[0]));
    }

    public String getIcon() {
        return lang.getString("weapon.skin-editions." + toString() + ".icon");
    }

    public int getDefaultPrice() {
        try {
            Class<WeaponSkinEdition> clazz = WeaponSkinEdition.class;
            Field field = clazz.getDeclaredField("DEFAULT_" + toString() + "_SKIN_PRICE");
            int price = field.getInt(clazz);
            return price;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return -1;
    }

}
