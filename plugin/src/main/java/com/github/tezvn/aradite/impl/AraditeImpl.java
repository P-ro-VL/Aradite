package com.github.tezvn.aradite.impl;

import com.github.tezvn.aradite.api.Aradite;
import com.github.tezvn.aradite.api.AraditeAPIProvider;
import com.github.tezvn.aradite.api.agent.AgentManager;
import com.github.tezvn.aradite.api.data.DataController;
import com.github.tezvn.aradite.api.match.Match;
import com.github.tezvn.aradite.impl.agent.AgentManagerImpl;
import com.github.tezvn.aradite.impl.agent.type.innova.InnovaCharacter;
import com.github.tezvn.aradite.impl.agent.type.moroe.MoroeCharacter;
import com.github.tezvn.aradite.impl.agent.type.winnin.WinninCharacter;
import com.github.tezvn.aradite.impl.data.DataControllerImpl;
import com.github.tezvn.aradite.api.language.Language;
import com.github.tezvn.aradite.api.language.LanguageManager;
import com.github.tezvn.aradite.impl.listener.ingame.AgentDeathModule;
import com.github.tezvn.aradite.impl.listener.ingame.AgentSkillModule;
import com.github.tezvn.aradite.impl.listener.ingame.GunShootModule;
import com.github.tezvn.aradite.impl.listener.ingame.KnifeDamageModule;
import com.github.tezvn.aradite.impl.listener.setting.MatchLocationSettingModule;
import com.github.tezvn.aradite.impl.match.MatchManager;
import com.github.tezvn.aradite.impl.ui.matchsetup.SelectMatchToEditUI;
import com.github.tezvn.aradite.impl.weapon.WeaponManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pdx.mantlecore.menu.Menu;

import java.lang.reflect.Field;

public class AraditeImpl extends JavaPlugin implements Aradite {

    private static AraditeImpl instance = null;

    private Language language = null;
    private WeaponManager weaponManager;
    private DataController dataController;
    private MatchManager matchManager;
    private AgentManager agentManager;

    @Override
    public void onEnable() {
        instance = this;

        AraditeAPIProvider.register(new DefaultAraditeAPI(this));
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        LanguageManager languageManager = new LanguageManager(this);
        this.language = languageManager.getCurrentLanguage();

        turnOffAsyncCatcher();
        /*Objects.requireNonNull(Bukkit.getPluginCommand("edit")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("list")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("join")).setExecutor(this);*/
        Bukkit.getPluginManager().registerEvents(new GunShootModule(), this);
        Bukkit.getPluginManager().registerEvents(new KnifeDamageModule(), this);
        Bukkit.getPluginManager().registerEvents(new AgentDeathModule(), this);
        Bukkit.getPluginManager().registerEvents(new AgentSkillModule(), this);

        Bukkit.getPluginManager().registerEvents(new MatchLocationSettingModule(), this);

        weaponManager = new WeaponManager();
        weaponManager.register();

        dataController = new DataControllerImpl();

        matchManager = new MatchManager();

        agentManager = new AgentManagerImpl();
        registerAgents();

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            matchManager.load();
        });
    }

    @Override
    public void onDisable() {
        matchManager.save();
    }

    public MatchManager getMatchManager() {
        return matchManager;
    }

    public WeaponManager getWeaponManager() {
        return weaponManager;
    }

    public DataController getDataController() {
        return dataController;
    }

    public Language getLanguage() {
        return language;
    }

    public AgentManager getAgentManager() {
        return agentManager;
    }

    public static AraditeImpl getInstance() {
        return instance;
    }

    public void registerAgents(){
        getAgentManager().registerAgentClass(WinninCharacter.class);
        getAgentManager().registerAgentClass(MoroeCharacter.class);
        getAgentManager().registerAgentClass(InnovaCharacter.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0) {
            Player player = (Player) sender;

            if (cmd.getName().equalsIgnoreCase("edit")) {
                Menu.open(player, new SelectMatchToEditUI(0));
                return true;
            }

            if (cmd.getName().equalsIgnoreCase("list")) {
                player.sendMessage("Available matches :");
                matchManager.getAllAvailableMatches().forEach(match ->
                        player.sendMessage("- " + match.getUniqueID()));
                return true;
            }

            if(cmd.getName().equalsIgnoreCase("join")){
                Match match = matchManager.getMatch(args[0]);
                if(match != null) match.join(player);
                return true;
            }

            if(args[0].equals("test")){
            }
        }
        return true;
    }

    private void turnOffAsyncCatcher() {
        try {
            Class<?> clazz = Class.forName("org.spigotmc.AsyncCatcher");
            Field field = clazz.getDeclaredField("enabled");
            field.setAccessible(true);
            field.set(null, false);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
