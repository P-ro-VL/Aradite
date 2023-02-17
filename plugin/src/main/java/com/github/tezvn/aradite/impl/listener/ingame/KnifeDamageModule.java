package com.github.tezvn.aradite.impl.listener.ingame;

import com.github.tezvn.aradite.api.Aradite;
import com.github.tezvn.aradite.api.match.Match;
import com.github.tezvn.aradite.api.weapon.Weapon;
import com.github.tezvn.aradite.api.weapon.knife.Knife;
import com.github.tezvn.aradite.api.weapon.knife.KnifeMeta;
import com.github.tezvn.aradite.impl.AraditeImpl;
import com.github.tezvn.aradite.impl.match.MatchManager;
import com.github.tezvn.aradite.impl.weapon.WeaponManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class KnifeDamageModule implements Listener  {

    @EventHandler
    public void onKnifeDamage(EntityDamageByEntityEvent e){
        if(!(e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity)) return;

        Player dmger = (Player) e.getDamager();
        LivingEntity en = (LivingEntity) e.getEntity();

        ItemStack item = dmger.getInventory().getItemInMainHand();
        if(item == null) return;

        MatchManager matchManager =  AraditeImpl.getInstance().getMatchManager();
        Match match = matchManager.getMatch(dmger);

        WeaponManager weaponManager =  AraditeImpl.getInstance().getWeaponManager();
        Weapon weapon = weaponManager.getWeaponByItemStack(item);
        if(weapon == null || !(weapon instanceof Knife)) return;

        Knife knife = (Knife) weapon;
        KnifeMeta meta = (KnifeMeta) knife.getMeta();

        e.setCancelled(true);
        e.setDamage(meta.getDamage());
        knife.onDamage(match, dmger, en, e);
    }

}
