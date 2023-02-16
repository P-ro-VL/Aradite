package aradite.weapon.gun.meta;

import java.util.Map;

import aradite.weapon.BodyPart;

import com.google.common.collect.Table;

import aradite.weapon.WeaponMeta;
import pdx.mantlecore.java.IntRange;

/**
 * An interface defines the attributes and stats of the gun.
 *
 * @author phongphong28
 */
public interface GunMeta extends WeaponMeta {

    /**
     * Return the number of bullets this gun need to shot in the specific body part to totally
     * kill an enemy.<br>
     */
    public Table<IntRange, BodyPart, Integer> getDamageTable();

    /**
     * Set the damage table.
     * @param damageTable The damage table.
     */
    public void setDamageTable(Table<IntRange, BodyPart, Integer> damageTable);

    /**
     * Return the number of bullets that the gun need to shoot to totally kill an enemy basing on the given
     * hit body part.
     *
     * @param distance The distance
     * @param hitPart  The hit part
     * @return The number of bullets
     */
    default int getBulletsToKill(int distance, BodyPart hitPart) {
        if(hitPart == null) return 0;
        for (IntRange range : getDamageTable().rowKeySet()) {
            if (!range.isInRange(distance))
                continue;
            return getDamageTable().get(range, hitPart);
        }
        return 0;
    }

    /**
     * Return the attribute map of the gun.
     */
    public Map<GunMetaType, Double> getAttribute();

    /**
     * Set the attribute value.
     *
     * @param attribute Attribute
     * @param value     Value
     */
    public void setAttribute(GunMetaType attribute, double value);

    /**
     * Set the attribute values by the given map.
     *
     * @param attributeMap The attribute map
     */
    public void setAttribute(Map<GunMetaType, Double> attributeMap);

}
