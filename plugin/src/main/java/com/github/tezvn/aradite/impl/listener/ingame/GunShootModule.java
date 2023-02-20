package com.github.tezvn.aradite.impl.listener.ingame;

import com.github.tezvn.aradite.api.language.Language;
import com.github.tezvn.aradite.api.weapon.Weapon;
import com.github.tezvn.aradite.api.weapon.gun.Gun;
import com.github.tezvn.aradite.impl.AraditeImpl;
import com.github.tezvn.aradite.impl.match.MatchManager;
import com.github.tezvn.aradite.api.recoil.ZoomRatio;
import com.github.tezvn.aradite.impl.weapon.WeaponManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pdx.mantlecore.java.collection.Lists;

import java.util.List;
import java.util.UUID;

public class GunShootModule implements Listener {

    private Language lang =  AraditeImpl.getInstance().getLanguage();

    private List<UUID> scoping = Lists.newArrayList();

    @EventHandler
    public void shootAction(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        WeaponManager manager =  AraditeImpl.getInstance().getWeaponManager();
        Weapon holdingWeapon = manager.getWeaponByItemStack(item);
        if (holdingWeapon == null) return;
        if (!(holdingWeapon instanceof Gun)) return;

        Gun gun = (Gun) holdingWeapon;

        String action = event.getAction().toString();
        if (action.contains("LEFT") && gun.isScopable()) { // Right to aim, left to shoot
            boolean isScoping = isScoping(player);
            setScoping(player, isScoping = !isScoping);
            if (isScoping) {
                ZoomRatio zoomRatio = gun.getScopeMode();
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,
                        9999999, zoomRatio.getZoomRatio(), false, false, false));
                player.playSound(player.getEyeLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, 1);
            } else {
                player.removePotionEffect(PotionEffectType.SLOW);
                player.playSound(player.getEyeLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 1, 1);
            }
            return;
        }

        MatchManager matchManager =  AraditeImpl.getInstance().getMatchManager();
        gun.shoot(matchManager.getMatch(player), player, isScoping(player));
    }

    @EventHandler
    public void onBulletHit(ProjectileHitEvent e) {
        Projectile projectile = e.getEntity();
        if (projectile.hasMetadata("bullet")) e.setCancelled(true);
    }

    /**
     * Return {@code true} if player is in scoping mode, {@code false} otherwise.
     *
     * @param player The player
     */
    public boolean isScoping(Player player) {
        return this.scoping.contains(player.getUniqueId());
    }

    /**
     * Change the scoping status of the given {@code player}.
     *
     * @param player    The player
     * @param isScoping {@code true} if player is scoping, {@code false} otherwise.
     */
    public void setScoping(Player player, boolean isScoping) {
        if (!isScoping) {
            this.scoping.remove(player.getUniqueId());
            player.removePotionEffect(PotionEffectType.SLOW);
        } else {
            setScoping(player, false);
            this.scoping.add(player.getUniqueId());
        }

//        player.sendActionBar(lang.getString("match.ingame.set-scoping-" + isScoping));
    }

}
