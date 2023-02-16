package aradite;

import aradite.data.DataController;
import aradite.language.Language;
import aradite.language.LanguageManager;
import aradite.listener.ingame.AgentDeathModule;
import aradite.listener.ingame.AgentSkillModule;
import aradite.listener.ingame.GunShootModule;
import aradite.listener.ingame.KnifeDamageModule;
import aradite.listener.setting.MatchLocationSettingModule;
import aradite.match.Match;
import aradite.match.MatchManager;
import aradite.match.MatchType;
import aradite.ui.endmatch.MatchSumUpUI;
import aradite.ui.matchsetup.SelectMatchToEditUI;
import aradite.weapon.AvailableWeapons;
import aradite.weapon.WeaponManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.spigotmc.AsyncCatcher;
import pdx.mantlecore.menu.Menu;
import pdx.mantlecore.message.GradientText;

public class Aradite extends JavaPlugin {

    private static Aradite instance = null;

    private Language language = null;
    private WeaponManager weaponManager;
    private DataController dataController;
    private MatchManager matchManager;

    @Override
    public void onEnable() {
        instance = this;

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        LanguageManager languageManager = new LanguageManager(this);
        this.language = languageManager.getCurrentLanguage();

        AsyncCatcher.enabled = false;
        Bukkit.getPluginManager().registerEvents(new GunShootModule(), this);
        Bukkit.getPluginManager().registerEvents(new KnifeDamageModule(), this);
        Bukkit.getPluginManager().registerEvents(new AgentDeathModule(), this);
        Bukkit.getPluginManager().registerEvents(new AgentSkillModule(), this);

        Bukkit.getPluginManager().registerEvents(new MatchLocationSettingModule(), this);

        weaponManager = new WeaponManager();
        weaponManager.register();

        dataController = new DataController();

        matchManager = new MatchManager();

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

    public static Aradite getInstance() {
        return instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0) {
            Player player = (Player) sender;

            if (args[0].equalsIgnoreCase("edit")) {
                Menu.open(player, new SelectMatchToEditUI(0));
                return true;
            }

            if (args[0].equalsIgnoreCase("list")) {
                player.sendMessage("Available matches :");
                matchManager.getAllAvailableMatches().forEach(match ->
                        player.sendMessage("- " + match.getUniqueID()));
                return true;
            }

            if(args[0].equalsIgnoreCase("join")){
                Match match = matchManager.getMatch(args[1]);
                if(match != null) match.join(player);
                return true;
            }

            if(args[0].equals("test")){
            }
        }
        return true;
    }
}
