package me.releasedsnow.com.glacialshards;





import org.bukkit.configuration.file.FileConfiguration;


public class ConfigManager  {




    public static FileConfiguration config = Plugin.getPlugin().getConfig();
    public ConfigManager() {
        this.deathMessages();
        this.defaults();


    }

    public static FileConfiguration getConfig(){
        return config;
    }


    private void deathMessages() {
        com.projectkorra.projectkorra.configuration.ConfigManager.languageConfig.get().addDefault("Abilities.Ice.GlacialShards.DeathMessage", "{victim} was frozen to death by {attacker}'s {ability}");
        com.projectkorra.projectkorra.configuration.ConfigManager.languageConfig.get().addDefault("Abilities.Ice.IceDisc.DeathMessage", "{victim} was frozen to death by {attacker}'s {ability}");

    }


    public void defaults(){
        FileConfiguration config = getConfig();
        String path = "Abilities.Ice.GlacialShards.";
        config.addDefault(path + "Duration",8000);
        config.addDefault(path + "Range", 20);
        config.addDefault(path + "Cooldown", 7000);
        config.addDefault(path + "freezeTics", 40);
        config.addDefault(path + "plantBending", true);
        config.addDefault(path + "Damage", 1);
        config.addDefault(path + "Speed", 2.0);
        config.addDefault(path + "Color", "#f6f6db");
        config.addDefault(path + "sourceRange", 6);

        path = "Abilities.Ice.IceDisc.";
        config.addDefault(path + "Duration", 5000);
        config.addDefault(path + "Cooldown", 5000);
        config.addDefault(path + "Damage", 3);
        config.addDefault(path + "Speed", 0.8);
        config.addDefault(path + "Range", 20);
        config.addDefault(path + "freezeTics", 50);



        config.options().copyDefaults(true);
        Plugin.getPlugin().saveConfig();

    }


}

