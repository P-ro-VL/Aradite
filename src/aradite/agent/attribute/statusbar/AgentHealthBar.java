package aradite.agent.attribute.statusbar;

import aradite.Aradite;
import aradite.agent.attribute.AttributeType;
import aradite.agent.skill.Skill;
import aradite.data.packet.PacketType;
import aradite.data.packet.type.PlayerInGameAttributePacket;
import aradite.data.packet.type.PlayerInGameLastDamagePacket;
import aradite.event.AgentDeathEvent;
import aradite.language.Language;
import aradite.match.Match;
import aradite.weapon.Weapon;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import pdx.mantlecore.math.PrimaryMath;

public class AgentHealthBar extends AbstractStatusBar {

    private final Match match;
    private final Language lang = Aradite.getInstance().getLanguage();

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
