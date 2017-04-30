package com.dbsoftwares.laggprevention.data;

/*
 * Created by DBSoftwares on 30 april 2017
 * Developer: Dieter Blancke
 * Project: LaggPrevention
 * May only be used for CentrixPVP
 */

import com.dbsoftwares.laggprevention.LaggPrevention;
import com.dbsoftwares.laggprevention.data.checks.EntityCheckData;
import com.dbsoftwares.laggprevention.enums.LaggEntity;
import com.google.common.collect.Maps;
import org.bukkit.configuration.ConfigurationSection;
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

        if(config.contains("checks.entities")) {
            Map<LaggEntity, Integer> maxEntities = Maps.newHashMap();

            if(config.contains("checks.entities.max-per-chunk.mobs")) {
                ConfigurationSection section = config.getConfigurationSection("checks.entities.max-per-chunk.mobs");

                for(String key : config.getKeys(false)) {
                    try {
                        LaggEntity entity = LaggEntity.valueOf(key.toUpperCase());
                        Integer max = section.getInt(key);

                        maxEntities.put(entity, max);
                    } catch (Exception ignored) {}
                }
            }
            data.put("entitycheck", new EntityCheckData(config.getInt("checks.entities.max-per-chunk"), maxEntities, config.getBoolean("checks.entities.kill-if-above")));
        } else {
            data.put("entitycheck", null);
        }
    }

    @SuppressWarnings("unchecked")
    public List<String> getEnabledWorlds() {
        return (List<String>)data.get("enabledworlds");
    }

    @SuppressWarnings("unchecked")
    public List<String> getDisabledWorlds() {
        return (List<String>)data.get("disabledworlds");
    }

    public EntityCheckData getEntityCheckData() {
        return (EntityCheckData)data.get("entitycheck");
    }
}