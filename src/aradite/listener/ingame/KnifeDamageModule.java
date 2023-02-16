package aradite.listener.ingame;

import aradite.Aradite;
import aradite.match.Match;
import aradite.match.MatchManager;
import aradite.weapon.Weapon;
import aradite.weapon.WeaponManager;
import aradite.weapon.knife.Knife;
import aradite.weapon.knife.KnifeMeta;
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

        MatchManager matchManager = Aradite.getInstance().getMatchManager();
        Match match = matchManager.getMatch(dmger);

        WeaponManager weaponManager = Aradite.getInstance().getWeaponManager();
        Weapon weapon = weaponManager.getWeaponByItemStack(item);
        if(weapon == null || !(weapon instanceof Knife)) return;

        Knife knife = (Knife) weapon;
        KnifeMeta meta = (KnifeMeta) knife.getMeta();

        e.setCancelled(true);
        e.setDamage(meta.getDamage());
        knife.onDamage(match, dmger, en, e);
    }

}
