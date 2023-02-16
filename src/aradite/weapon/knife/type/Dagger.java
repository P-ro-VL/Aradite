package aradite.weapon.knife.type;

import aradite.Aradite;
import aradite.language.Language;
import aradite.weapon.knife.AbstractKnife;
import org.bukkit.Material;

public class Dagger extends AbstractKnife {

    private static Language lang = Aradite.getInstance().getLanguage();

    public Dagger() {
        super("DAGGER", lang.getString("weapon.displayname.knife.dagger"), Material.STONE_SWORD);
    }

    @Override
    public int getCustomModelData() {
        return 9999901;
    }
}
