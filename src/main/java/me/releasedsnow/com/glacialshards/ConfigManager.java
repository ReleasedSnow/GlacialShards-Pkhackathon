package me.releasedsnow.com.glacialshards;





import org.bukkit.configuration.file.FileConfiguration;


public class ConfigManager  {




    public static FileConfiguration config = Plugin.getPlugin().getConfig();
    public ConfigManager() {
        this.defaults();


    }

    public static FileConfiguration getConfig(){
        return config;
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


        config.options().copyDefaults(true);
        Plugin.getPlugin().saveConfig();

    }


}

