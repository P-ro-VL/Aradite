package com.github.tezvn.aradite.weapon.gun;

import com.github.tezvn.aradite.Aradite;
import com.github.tezvn.aradite.language.Language;
import com.github.tezvn.aradite.language.Placeholder;
import com.github.tezvn.aradite.nms.recoil.ZoomRatio;
import com.github.tezvn.aradite.weapon.BodyPart;
import com.github.tezvn.aradite.weapon.gun.meta.AbstractGunMeta;
import com.github.tezvn.aradite.weapon.gun.meta.GunMeta;
import com.github.tezvn.aradite.weapon.gun.meta.GunMetaType;
import org.bukkit.Material;
import pdx.mantlecore.java.IntRange;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractGun implements Gun {

    private String ID;
    private Material material;
    private GunType type;
    private GunMeta meta;
    private String displayName;
    private boolean scopable;
    private ZoomRatio scopingMode;
    private List<String> lore;

    public AbstractGun(String ID, String displayname, GunType type) {
        this.ID = ID;
        this.type = type;
        this.displayName = displayname;
        this.meta = new AbstractGunMeta();
        this.material = Material.IRON_HOE;
    }

    @Override
    public ZoomRatio getScopeMode() {
        return scopingMode;
    }

    @Override
    public boolean isScopable() {
        return scopable;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getID() {
        return this.ID;
    }

    @Override
    public Material getMaterial() {
        return this.material;
    }

    @Override
    public GunType getType() {
        return this.type;
    }

    @Override
    public GunMeta getMeta() {
        return this.meta;
    }

    public void setMeta(GunMeta meta) {
        this.meta = meta;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void setScopable(boolean scopable) {
        this.scopable = scopable;
    }

    public void setScopeMode(ZoomRatio scopingMode) {
        this.scopingMode = scopingMode;
    }

    @Override
    public List<String> getLore() {
        if (lore != null) return lore;

        Language lang = Aradite.getInstance().getLanguage();

        GunMeta meta = getMeta();

        List<String> damageLore = new ArrayList<>();
        for (IntRange range : meta.getDamageTable().rowMap().keySet()) {
            Map<BodyPart, Integer> bulletToKills = meta.getDamageTable().rowMap().get(range);

            List<String> rangeDmg = lang.getListWithPlaceholders("lore.damage_range",
                    Placeholder.of("min_range", "" + range.getMin()),
                    Placeholder.of("max_range", "" + range.getMax()),
                    Placeholder.of("bullet_to_kill_head", "" + bulletToKills.get(BodyPart.HEAD)),
                    Placeholder.of("bullet_to_kill_body", "" + bulletToKills.get(BodyPart.BODY)),
                    Placeholder.of("bullet_to_kill_feet", "" + bulletToKills.get(BodyPart.LEGS))
            );
            damageLore.addAll(rangeDmg);
            damageLore.add("");
        }

        Map<GunMetaType, Double> attrs = meta.getAttribute();

        List<String> lore = lang.getListWithPlaceholders("lore.gun",
                Placeholder.of("wall_penetration", lang.getString("gun-meta-type.WALL_PENETRATION")),
                Placeholder.of("fire_rate", lang.getString("gun-meta-type.FIRE_RATE")),
                Placeholder.of("reload_speed", lang.getString("gun-meta-type.RELOAD_SPEED")),
                Placeholder.of("magazine", lang.getString("gun-meta-type.MAGAZINE")),
                Placeholder.of("reserve", lang.getString("gun-meta-type.RESERVE")),

                Placeholder.of("wall_penetration_value", "" + attrs.get(GunMetaType.WALL_PENETRATION)),
                Placeholder.of("fire_rate_value", "" + attrs.get(GunMetaType.FIRE_RATE)),
                Placeholder.of("reload_speed_value", "" + attrs.get(GunMetaType.RELOAD_SPEED)),
                Placeholder.of("magazine_value", "" + attrs.get(GunMetaType.MAGAZINE)),
                Placeholder.of("reserve_value", "" + attrs.get(GunMetaType.RESERVE)),

                Placeholder.of("damage_range", damageLore)
        );
        this.lore = lore;
        return getLore();
    }
}
