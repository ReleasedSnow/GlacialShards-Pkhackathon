package me.releasedsnow.com.hackathon;

import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.CoreAbility;
import org.bukkit.plugin.java.JavaPlugin;


public class Plugin extends JavaPlugin {

    public static Plugin plugin;

    @Override
    public void onEnable() {
        plugin = this;
        new ConfigManager();
        CoreAbility.registerPluginAbilities(plugin, "me.releasedsnow.com.hackathon.ability");
        ProjectKorra.plugin.getServer().getPluginManager().registerEvents(new AbilityListener(), plugin);
        System.out.println("GlacialShards plugin has started");

    }

    public static Plugin getPlugin() {
        return plugin;
    }
}
