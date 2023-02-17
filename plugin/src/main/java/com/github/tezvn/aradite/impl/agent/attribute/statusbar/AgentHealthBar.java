package com.github.tezvn.aradite.impl.agent.attribute.statusbar;

import com.github.tezvn.aradite.api.Aradite;
import com.github.tezvn.aradite.api.agent.attribute.AttributeType;
import com.github.tezvn.aradite.api.agent.attribute.statusbar.StatusBarType;
import com.github.tezvn.aradite.api.language.Language;
import com.github.tezvn.aradite.api.match.Match;
import com.github.tezvn.aradite.api.weapon.Weapon;
import com.github.tezvn.aradite.impl.AraditeImpl;
import com.github.tezvn.aradite.impl.data.packet.PacketType;
import com.github.tezvn.aradite.impl.data.packet.type.PlayerInGameAttributePacket;
import com.github.tezvn.aradite.impl.data.packet.type.PlayerInGameLastDamagePacket;
import com.github.tezvn.aradite.impl.event.AgentDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import pdx.mantlecore.math.PrimaryMath;

public class AgentHealthBar extends AbstractStatusBar {

    private final Match match;
    private final Language lang = AraditeImpl.getInstance().getLanguage();

    public AgentHealthBar(Player owner, Match match) {
        super(owner);
        this.match = match;
    }

    @Override
    public StatusBarType getBarType() {
        return StatusBarType.HEALTH_BAR;
    }

    @Override
    public BarColor getBarColor() {
        return BarColor.RED;
    }

    @Override
    public BarStyle getBarStyle() {
        return BarStyle.SOLID;
    }

    @Override
    public void update() {
        Player player = getOwner();

        PlayerInGameAttributePacket attributePacket = (PlayerInGameAttributePacket) match.retrieveProtocol(player)
                .getPacket(PacketType.INGAME_PLAYER_ATTRIBUTE);
        PlayerInGameLastDamagePacket lastDamagePacket = (PlayerInGameLastDamagePacket) match.retrieveProtocol(player)
                .getPacket(PacketType.INGAME_PLAYER_LAST_DAMAGE);

        double currentHealth = attributePacket.getAttribute(AttributeType.CURRENT_HEALTH);
        if (currentHealth <= 0 && !lastDamagePacket.isDead()) {
            PlayerInGameLastDamagePacket.DeathReason deathReason = lastDamagePacket.getLastDeathReason();
            Player killer = lastDamagePacket.getLastKiller();
            Weapon weapon = lastDamagePacket.getLastKilledWeapon();
            String skill = lastDamagePacket.getLastKilledSkill();

            AgentDeathEvent event = new AgentDeathEvent(match, killer, player, deathReason, weapon, skill);
            Bukkit.getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                updatePercentage(0d);
                updateMessage(lang.getString("status-bar.health-bar-dead"));

                lastDamagePacket.setDead(true);
            }
            return;
        }

        double maxHealth = attributePacket.getAttribute(AttributeType.MAX_HEALTH);

        double progress = PrimaryMath.percentage(currentHealth, maxHealth, PrimaryMath.PercentageMode.ONE_DIGIT) / 100;

        updatePercentage(progress);
        updateMessage(lang.getString("status-bar.health-bar")
                .replaceAll("%current_health%", "" + currentHealth)
                .replaceAll("%max_health%", "" + maxHealth));
    }
}
