package com.github.tezvn.aradite.agent.type.winnin.skill;

import com.github.tezvn.aradite.agent.skill.Skill;
import com.github.tezvn.aradite.agent.skill.SkillType;
import com.github.tezvn.aradite.match.Match;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.github.tezvn.aradite.Aradite;
import com.github.tezvn.aradite.agent.Agents;
import pdx.mantlecore.math.Shapes;

public class WinninActivateC extends Skill {

    private final int POTION_DURATION = 3;

    public WinninActivateC() {
        super("winnin-activate-c", Aradite.getInstance().getLanguage().getString("agents.winnin.skill.c.name"),
                Agents.WINNIN);

        registerListener(new Listener() {
            @EventHandler
            public void onCast(ProjectileHitEvent e) {
                if (!(e.getEntity() instanceof ThrownPotion)) return;
                ThrownPotion projectile = (ThrownPotion) e.getEntity();
                if (projectile == null) return;
                if (!projectile.hasMetadata("winnin-activate-c")) return;

                Location loc = projectile.getLocation().clone();
                loc.setY(loc.getY() - 1);

                if (loc.getY() < ((Player) projectile.getShooter()).getLocation().getY())
                    Shapes.filledCircle(loc, 3).forEach(location -> {
                        Block block = location.getBlock();
                        if (!block.getType().isSolid() || block.getType().toString().contains("STAIRS")
                                || block.getType().toString().contains("SLAB")) return;

                        final BlockData oldBlockData = block.getBlockData();

                        block.setBlockData(Material.SLIME_BLOCK.createBlockData());
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                location.getBlock().setBlockData(oldBlockData);
                            }
                        }.runTaskLater(Aradite.getInstance(), 20 * POTION_DURATION);
                    });

                projectile.getLocation().getNearbyPlayers(3).forEach(pl -> {
                    pl.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, POTION_DURATION * 20,
                            255, false, false, false));
                });
            }

        });
    }

    @Override
    public void onActivate(int level, Match match, Player agent, LivingEntity targetEntity, Block targetBlock) {
        ItemStack potion = new ItemStack(Material.LINGERING_POTION);
        PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();
        potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.SLOW, POTION_DURATION * 20, 255,
                false, false, false), true);
        potionMeta.setColor(Color.LIME);
        potion.setItemMeta(potionMeta);

        ThrownPotion thrownPotion = agent.launchProjectile(LingeringPotion.class);
        thrownPotion.setItem(potion);
        thrownPotion.setShooter(agent);
        thrownPotion.setMetadata(getID(), new FixedMetadataValue(Aradite.getInstance(), getID()));

        agent.getWorld().playSound(agent.getEyeLocation(), Sound.ENTITY_BLAZE_SHOOT, 3, 1);

        Vector currentLocation = agent.getLocation().getDirection();
        agent.setVelocity(currentLocation.normalize().multiply(-2));
    }

    @Override
    public String getDescription() {
        return Aradite.getInstance().getLanguage().getString("agents.winnin.skill.c.description")
                .replaceAll("%duration%", "" + POTION_DURATION);
    }

    @Override
    public SkillType getType() {
        return SkillType.ACTIVE_C;
    }

}
