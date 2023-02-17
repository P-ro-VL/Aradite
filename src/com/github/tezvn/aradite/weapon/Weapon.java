package com.github.tezvn.aradite.weapon;

import com.github.tezvn.aradite.Aradite;
import com.github.tezvn.aradite.match.Match;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The weapon that player uses in a match.
 *
 * @author phongphong28
 */
public interface Weapon {

    /**
     * Return the ID of the weapon.
     */
    public String getID();

    /**
     * Return the category of the weapon.
     */
    public WeaponCategory getCategory();

    /**
     * Return the display name of the weapon.
     */
    public String getDisplayName();

    /**
     * Return the weapon's material.
     */
    public Material getMaterial();

    /**
     * Return {@code true} if the weapon is melee, {@code false} if the weapon is
     * gun.
     */
    public boolean isMelee();

    /**
     * Return {@code true} if the weapon can be sub weapon.<br>
     * Default value will be {@code true}
     */
    default boolean canBeSubWeapon() {
        return true;
    }

    /**
     * Return the meta of the weapon.
     */
    public WeaponMeta getMeta();

    /**
     * Return the type of the weapon.
     */
    public WeaponType getWeaponType();

    /**
     * The model data number of the default skin weapon.
     * The number must be a 7-digit number and different from others' ones.
     */
    public int getCustomModelData();

    /**
     * Get the {@link ItemStack} form of the weapon with the given skin.
     *
     * @param skin The skin ID
     */
    default ItemStack toItemStack(String skin) {
        boolean isDefault = skin.equals("default");

        Optional<WeaponSkin> skinOptional = null;
        if (!isDefault) {
            skinOptional = getMeta().getSkins().stream().filter(skinObj -> skinObj.getSkinID().equalsIgnoreCase(skin))
                    .findAny();
            if(!skinOptional.isPresent()) throw new NullPointerException("Cannot find skin '" + skin + "' for weapon " +
                    "whose id is " + getID());
        }

        boolean hasSkin = skinOptional != null && skinOptional.isPresent();
        WeaponSkinEdition skinEdition = WeaponSkinEdition.DEFAULT;
        boolean isLimited = false;
        String displayName = getDisplayName();
        if(hasSkin){
            WeaponSkin skinInstance = skinOptional.get();
            skinEdition = skinInstance.getEdition();
            isLimited = skinInstance.isLimited();
            displayName = skinInstance.getDisplayName();
        }

        String category = getCategory().toString().toLowerCase();

        NamespacedKey key = new NamespacedKey(Aradite.getInstance(), category + "-item");
        NamespacedKey idKey = new NamespacedKey(Aradite.getInstance(), "weapon-id");

        ItemStack itemStack = new ItemStack(getMaterial());

        ItemMeta meta = itemStack.getItemMeta();
        meta.setCustomModelData(isDefault ? getCustomModelData() : skinOptional.get().getCustomModelData());
        meta.setDisplayName(displayName);
        meta.getPersistentDataContainer().set(idKey, PersistentDataType.STRING, getID());
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING,
                isDefault ? getID() : getID() + "-" + skin.toUpperCase());

        List<String> lore = new ArrayList<>();
        lore.add(skinEdition.parseDisplayName(skinEdition.toString(), isLimited));
        lore.add("");
        lore.addAll(getLore());
        meta.setLore(lore);

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    /**
     * Return the lore of the weapon. The lore will contain all its statistics and attributes.
     */
    public List<String> getLore();

    /**
     * Get the {@link ItemStack} form of the weapon with the default skin.
     */
    default ItemStack toItemStack() {
        return toItemStack("default");
    }

    /**
     * Actions that will be performed when the weapon deals damage to an enemy.
     *
     * @param match  The match player is in
     * @param dmger  Player who dealt damage.
     * @param target Entity
     * @param e      The damage event.
     */
    public void onDamage(Match match, Player dmger, LivingEntity target, Event e);
}
