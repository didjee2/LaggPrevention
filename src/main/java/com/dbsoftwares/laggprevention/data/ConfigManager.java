package com.dbsoftwares.laggprevention.data;

/*
 * Created by DBSoftwares on 30 april 2017
 * Developer: Dieter Blancke
 * Project: LaggPrevention
 * May only be used for CentrixPVP
 */

import com.dbsoftwares.laggprevention.LaggPrevention;
import com.google.common.collect.Maps;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    private LaggPrevention instance;
    private File file;
    private FileConfiguration config;
    private Map<String, Object> data;

    public ConfigManager(LaggPrevention instance) {
        this.instance = instance;
        file = new File(instance.getDataFolder(), "config.yml");
        data = Maps.newHashMap();
    }

    public void load() {
        if(!file.exists()) {
            instance.saveResource("config.yml", true);
        }
        config = YamlConfiguration.loadConfiguration(file);
        reload();
    }

    public void reload() {
        data.put("enabledworlds", config.getBoolean("enabledworlds.use") ? config.getStringList("enabledworlds.worlds") : null);
        data.put("disabledworlds", config.getBoolean("disabledworlds.use") ? config.getStringList("disabledworlds.worlds") : null);
    }

    @SuppressWarnings("unchecked")
    public List<String> getEnabledWorlds() {
        return (List<String>)data.get("enabledworlds");
    }

    @SuppressWarnings("unchecked")
    public List<String> getDisabledWorlds() {
        return (List<String>)data.get("disabledworlds");
    }
}