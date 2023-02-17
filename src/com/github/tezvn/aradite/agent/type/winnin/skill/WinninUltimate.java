package com.github.tezvn.aradite.agent.type.winnin.skill;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.github.tezvn.aradite.agent.skill.SkillType;
import com.github.tezvn.aradite.agent.skill.UltimateSkill;
import com.github.tezvn.aradite.language.Language;
import com.github.tezvn.aradite.match.Match;
import com.github.tezvn.aradite.match.MatchManager;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.collect.Maps;

import com.github.tezvn.aradite.Aradite;
import com.github.tezvn.aradite.agent.Agents;
import com.github.tezvn.aradite.data.packet.PacketType;
import com.github.tezvn.aradite.data.packet.type.PlayerInGameAttributePacket;
import com.github.tezvn.aradite.data.packet.type.PlayerInGameLastDamagePacket;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import pdx.mantlecore.block.LocationFactory;
import pdx.mantlecore.math.PrimaryMath;
import pdx.mantlecore.math.Shapes;

public class WinninUltimate extends UltimateSkill {

	private static Map<UUID, WinninUltimateLocateTask> locatingTaskMaps = Maps.newHashMap();
	private final int DAMAGE_RANGE = 5;

	public WinninUltimate() {
		super("winnin-ultimate", Aradite.getInstance().getLanguage().getString("agents.winnin.skill.ultimate.name"),
				Agents.WINNIN);

		setEvalExpression("damage", "level^2+2*level+26");
		registerListener(new Listener() {
			@EventHandler
			public void winnin_ultimate_locate_listener(PlayerInteractEvent e) {
				Player player = e.getPlayer();
				Block block = e.getClickedBlock();

				if (block == null || e.getHand() != EquipmentSlot.HAND
						|| !e.getAction().toString().contains("LEFT"))
					return;

				MatchManager manager = Aradite.getInstance().getMatchManager();
				Match match = manager.getMatch(player);

				if (match == null)
					return;

				if (locatingTaskMaps.containsKey(player.getUniqueId())) {
					WinninUltimateLocateTask task = locatingTaskMaps.get(player.getUniqueId());

					if (task.isRunning) {
						Location virtualLocation = task.getVirtualBlockLocation();
						Location ultimateLocation = virtualLocation == null ? task.defaultBombLocation : virtualLocation;
						callBomb(player, match, ultimateLocation, task.level);
					}

					task.isRunning = false;
					task.cancel();
					locatingTaskMaps.remove(player.getUniqueId());
				}
			}

		});
	}

	@Override
	public void onActivate(int level, Match match, Player agent, LivingEntity targetEntity, Block targetBlock) {
		if(locatingTaskMaps.containsKey(agent.getUniqueId())){
			WinninUltimateLocateTask task = locatingTaskMaps.get(agent.getUniqueId());
			task.isRunning = false;
			Language lang = Aradite.getInstance().getLanguage();
			agent.sendActionBar(lang.getString("agents.winnin.skill.ultimate.status.deactivate"));
			task.cancel();
			locatingTaskMaps.remove(agent.getUniqueId());
			return;
		}

		if(targetBlock == null){
			agent.sendMessage(Aradite.getInstance().getLanguage()
					.getString("agents.winnin.skill.ultimate.target-block-invalid"));
			return;
		}

		locatingTaskMaps.remove(agent.getUniqueId());
		WinninUltimateLocateTask locatingTask = new WinninUltimateLocateTask(match, agent, level);
		locatingTask.defaultBombLocation = targetBlock.getLocation();
		BukkitTask task = locatingTask.runTaskTimerAsynchronously(Aradite.getInstance(), 10, 10);

		locatingTaskMaps.put(agent.getUniqueId(), locatingTask);

		agent.playSound(agent.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2, 4);

		agent.sendMessage("");
		agent.sendMessage(Aradite.getInstance().getLanguage().getString("agents.winnin.skill.ultimate.place-notify"));
		agent.sendMessage("");
	}

	@Override
	public String getDescription() {
		return Aradite.getInstance().getLanguage().getString("agents.winnin.skill.ultimate.description")
				.replaceAll("%range%", "" + DAMAGE_RANGE);
	}

	@Override
	public SkillType getType() {
		return SkillType.ULTIMATE;
	}

	/**
	 * Call the ultimate at the given location.
	 * 
	 * @param location
	 *            The location
	 */
	public void callBomb(Player agent, Match match, Location location, int level) {
		double dmg = PrimaryMath.eval(getSkillEvalExpressions().get("damage").replaceAll("level", "" + level));

		new BukkitRunnable() {
			int y = 50;

			@Override
			public void run() {
				y -= 5;

				Location loc = location.clone();
				loc.add(0, y, 0);
				loc.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, loc, 0, 0, 0, 0, 1);
				loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 10f, 1f);

				Shapes.circle(location, DAMAGE_RANGE, 30).forEach(location -> {
					loc.getWorld().spawnParticle(Particle.REDSTONE, location, 1,
							new Particle.DustOptions(Color.ORANGE, 2));
				});

				if (y == 0) {
					loc.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, loc, 0, 0, 0, 0, 52);
					this.cancel();
					location.getNearbyPlayers(DAMAGE_RANGE, player -> {
						return !match.getMatchTeam().isOnSameTeam(player, agent);
					}).forEach(player -> {
						PlayerInGameAttributePacket targetData = (PlayerInGameAttributePacket) match
								.retrieveProtocol(player).getPacket(PacketType.INGAME_PLAYER_ATTRIBUTE);
						PlayerInGameLastDamagePacket targetLastDmgData = (PlayerInGameLastDamagePacket) match
								.retrieveProtocol(player).getPacket(PacketType.INGAME_PLAYER_LAST_DAMAGE);

						targetData.damage("SKILL:" + agent.getName() + "•Winnin•ULTIMATE", dmg, false,
								targetLastDmgData);
					});
				}
			};
		}.runTaskTimer(Aradite.getInstance(), 5, 5);
	}

	public static class WinninUltimateLocateTask extends BukkitRunnable {
		private boolean isRunning;

		private Player p;
		private Location virtualBlockLocation, defaultBombLocation;
		private Block oldTargetBlock;

		private Match currentMatch;
		private Language lang;

		private int level;

		public WinninUltimateLocateTask(Match match, Player player, int level) {
			isRunning = true;
			this.p = player;
			this.currentMatch = match;
			this.level = level;
			this.lang = Aradite.getInstance().getLanguage();
		}

		public Location getVirtualBlockLocation() {
			return virtualBlockLocation;
		}

		@Override
		public void run() {
			if (!isRunning) {
				this.cancel();
				locateModeStatus(this.p, this.isRunning);
				return;
			}

			locateModeStatus(this.p, this.isRunning);

			virtualLocation(this.p, 5);
		}

		public boolean isRunning() {
			return isRunning;
		}

		public void setRunning(boolean isRunning) {
			this.isRunning = isRunning;
		}

		private void locateModeStatus(Player p, boolean isRunning) {
			if (!isRunning) {
				message(p, "ACTION_BAR", lang.getString("agents.winnin.skill.ultimate.status.deactivate"));
				return;
			}
			message(p, "ACTION_BAR", lang.getString("agents.winnin.skill.ultimate.status.activate"));
		}

		private void message(Player p, String type, String msg) {
			p.spigot().sendMessage(ChatMessageType.valueOf(type), TextComponent.fromLegacyText(msg));
		}

		private Optional<Block> targetBlock(Player p, int distance) {
			BlockFace face = LocationFactory.getBlockFace(p);
			if (face == null)
				return Optional.empty();
			Block targetBlock = p.getTargetBlockExact(distance);
			if (targetBlock == null || targetBlock.getType() == Material.AIR || targetBlock.getType().isEdible())
				return Optional.empty();
			return !targetBlock.getType().isSolid() ? Optional.ofNullable(targetBlock)
					: Optional.ofNullable(targetBlock.getRelative(face));
		}

		private void virtualLocation(Player p, int distance) {
			if (!p.isOnline() || currentMatch.getMatchTeam().getTeamOf(p) == null) {
				this.cancel();
				return;
			}

			Optional<Block> optTarget = targetBlock(p, distance);
			if (!optTarget.isPresent())
				return;

			Block b = optTarget.get();
			this.oldTargetBlock = b;

			Location bLoc = b.getLocation();

			p.sendBlockChange(bLoc, Material.END_ROD.createBlockData());
			this.virtualBlockLocation = new Location(bLoc.getWorld(), bLoc.getBlockX(), bLoc.getBlockY(),
					bLoc.getBlockZ());

			runSyncLater(() -> {
				p.sendBlockChange(bLoc, oldTargetBlock.getBlockData());
			}, 5);
		}

		private void runSyncLater(Runnable runnable, long tick) {
			new BukkitRunnable() {
				@Override
				public void run() {
					runnable.run();
				}
			}.runTaskLater(Aradite.getInstance(), tick);
		}
	}
}
