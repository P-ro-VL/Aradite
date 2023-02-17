package com.github.tezvn.aradite.impl.weapon;

import com.github.tezvn.aradite.api.Aradite;
import com.github.tezvn.aradite.api.weapon.AvailableWeapons;
import com.github.tezvn.aradite.api.weapon.Weapon;
import com.github.tezvn.aradite.api.weapon.WeaponCategory;
import com.github.tezvn.aradite.api.weapon.WeaponType;
import com.github.tezvn.aradite.impl.AraditeImpl;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A class that manages all weapons.
 */
public class WeaponManager {

    private final Map<String, Weapon> availableWeapon = Maps.newHashMap();

    /**
     * Return the weapon whose ID is {@code id}
     *
     * @param id      The weapon's id.
     * @param wrapper The wrapper class of the weapon (usually interface).
     * @return The weapon
     */
    public <T extends Weapon> T getWeaponByID(String id, Class<T> wrapper) {
        Weapon weapon = this.availableWeapon.get(id);
        return wrapper.cast(weapon);
    }

    /**
     * Return all weapons whose type is {@code weaponType}
     * @param weaponType The type of weapon
     */
    public List<Weapon> getWeaponsByType(WeaponType weaponType){
        return this.availableWeapon.values().stream().filter(weapon -> weapon.getWeaponType() == weaponType)
                .collect(Collectors.toList());
    }

    /**
     * Return the weapon whose ID is {@code id}
     *
     * @param id The weapon's id.
     * @return The weapon
     */
    public Weapon getWeaponByID(String id) {
        return this.availableWeapon.get(id);
    }

    /**
     * Return all weapon whose category is {@code category}
     *
     * @param category The category
     * @return All items belonging to that category.
     */
    public List<Weapon> getWeaponsByCategory(WeaponCategory category) {
        return this.availableWeapon.values().stream().filter(weapon -> weapon.getCategory() == category)
                .collect(Collectors.toList());
    }

    /**
     * Return the weapon whose {@link ItemStack} form is {@code itemStack}.
     *
     * @param itemStack The item stack.
     * @return The weapon
     */
    public Weapon getWeaponByItemStack(ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasItemMeta()) return null;
        ItemMeta meta = itemStack.getItemMeta();
        NamespacedKey idKey = new NamespacedKey(AraditeImpl.getInstance(), "weapon-id");
        assert meta != null;
        meta.getPersistentDataContainer();
        if (!meta.getPersistentDataContainer().has(idKey, PersistentDataType.STRING)) return null;
        String id = meta.getPersistentDataContainer().get(idKey, PersistentDataType.STRING);
        return this.availableWeapon.values().stream().filter(weapon -> weapon.getID().equals(id))
                .findAny().orElse(null);
    }

    /**
     * Register all available weapons in {@link AvailableWeapons}.
     */
    public void register() {
        try {
            Class<AvailableWeapons> clazz = AvailableWeapons.class;
            for (Field field : clazz.getDeclaredFields()) {
                Weapon weapon = (Weapon) field.get(clazz);
                register(weapon);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Bukkit.getLogger().info("[ARADITE WEAPON SETUP] Registered " + this.availableWeapon.size() + " weapons !");
    }

    /**
     * Register the given weapon.
     *
     * @param weapon The weapon need registering.
     */
    public void register(Weapon weapon) {
        this.availableWeapon.put(weapon.getID(), weapon);
    }
}
