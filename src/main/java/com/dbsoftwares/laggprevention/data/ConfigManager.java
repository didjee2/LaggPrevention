package com.dbsoftwares.laggprevention.data;

/*
 * Created by DBSoftwares on 30 april 2017
 * Developer: Dieter Blancke
 * Project: LaggPrevention
 * May only be used for CentrixPVP
 */

import com.dbsoftwares.laggprevention.LaggPrevention;
import com.dbsoftwares.laggprevention.data.checks.EntityCheckData;
import com.dbsoftwares.laggprevention.data.checks.ItemCheckData;
import com.dbsoftwares.laggprevention.data.checks.TPSCheckData;
import com.dbsoftwares.laggprevention.enums.LaggEntity;
import com.dbsoftwares.laggprevention.enums.CheckType;
import com.dbsoftwares.laggprevention.utils.MathUtils;
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
        if (!file.exists()) {
            instance.saveResource("config.yml", true);
        }
        config = YamlConfiguration.loadConfiguration(file);
        reload();
    }

    public void reload() {
        data.put("enabledworlds", config.getBoolean("enabledworlds.use") ? config.getStringList("enabledworlds.worlds") : null);
        data.put("disabledworlds", config.getBoolean("disabledworlds.use") ? config.getStringList("disabledworlds.worlds") : null);

        if (config.contains("checks.entities")) {
            Map<LaggEntity, Integer> maxEntities = Maps.newHashMap();

            if (config.contains("checks.entities.max-per-chunk.mobs")) {
                ConfigurationSection section = config.getConfigurationSection("checks.entities.max-per-chunk.mobs");

                for (String key : config.getKeys(false)) {
                    try {
                        LaggEntity entity = LaggEntity.valueOf(key.toUpperCase());
                        Integer max = section.getInt(key);

                        maxEntities.put(entity, max);
                    } catch (Exception ignored) {
                    }
                }
            }
            data.put("entitycheck", new EntityCheckData(config.getInt("checks.entities.max-per-chunk"), maxEntities, config.getBoolean("checks.entities.kill-if-above")));
        } else {
            data.put("entitycheck", null);
        }

        if (config.contains("checks.items")) {
            Map<Integer, String> clearIncomingMessages = Maps.newHashMap();

            if(config.contains("checks.items.item-clear.clear-incoming")) {
                ConfigurationSection section = config.getConfigurationSection("checks.items.item-clear.clear-incoming");

                for(String key : section.getKeys(false)) {
                    if(!MathUtils.isInteger(key)) continue;

                    Integer time = Integer.parseInt(key);
                    String message = section.getString(key);
                    clearIncomingMessages.put(time, message);
                }
            }
            data.put("itemcheck", new ItemCheckData(config.getInt("checks.items.max-per-chunk"), config.getInt("checks.items.max-in-world"), config.getInt("checks.items.item-clear.delay"),
                    config.getString("checks.items.item-clear.clear-message"), clearIncomingMessages));
        } else {
            data.put("itemcheck", null);
        }

        if(config.contains("checks.tps")) {
            Map<String, Integer> tpsTriggers = Maps.newHashMap();
            Map<String, Integer> cooldowns = Maps.newHashMap();
            Map<LaggEntity, Integer> amountToKill = Maps.newHashMap();
            Integer averageTPS = config.getInt("checks.tps.use-average-tps");

            String clearMessage = "";
            String mobRemoveMessage = "";
            if(config.contains("checks.tps.item-clear")) {
                tpsTriggers.put("item-clear", config.getInt("checks.tps.item-clear.execute-if-below"));
                cooldowns.put("item-clear", config.getInt("checks.tps.item-clear.cooldown"));
                clearMessage = config.getString("checks.tps.item-clear.clear-message");
            } else {
                tpsTriggers.put("item-clear", 0);
                cooldowns.put("item-clear", 0);
            }
            if(config.contains("checks.tps.mob-kill")) {
                ConfigurationSection section = config.getConfigurationSection("checks.tps.mob-kill");
                mobRemoveMessage = section.getString("remove-message");
                for(String key : section.getKeys(false)) {
                    try {
                        LaggEntity entity = LaggEntity.valueOf(key.toUpperCase());

                        Integer tps = section.getInt(key + ".kill-below-tps");
                        Integer amount = section.getInt(key + ".amount-to-kill");
                        Integer cooldown = section.getInt(key + ".cooldown");

                        tpsTriggers.put(entity.toString().toLowerCase(), tps);
                        amountToKill.put(entity, amount);
                        cooldowns.put(entity.toString().toLowerCase(), cooldown);
                    } catch (Exception ignored) {}
                }
            }
            Integer laggHaltDuration = 0;
            String laggHaltEnabled = "";
            String laggHaltDisabled = "";
            if(config.contains("checks.tps.lagg-halt")) {
                ConfigurationSection section = config.getConfigurationSection("checks.tps.lagg-halt");

                tpsTriggers.put("lagg-halt", section.getInt("execute-if-below"));
                cooldowns.put("lagg-halt", section.getInt("cooldown"));
                laggHaltDuration = section.getInt("duration");
                laggHaltEnabled = section.getString("enabled");
                laggHaltDisabled = section.getString("disabled");
            } else {
                tpsTriggers.put("lagg-halt", 0);
                cooldowns.put("lagg-halt", 0);
            }
            data.put("laggcheck", new TPSCheckData(averageTPS, tpsTriggers, clearMessage, amountToKill, cooldowns,
                    mobRemoveMessage, laggHaltDuration, laggHaltEnabled, laggHaltDisabled));
        } else {
            data.put("laggcheck", null);
        }
    }

    @SuppressWarnings("unchecked")
    public List<String> getEnabledWorlds() {
        return (List<String>) data.get("enabledworlds");
    }

    @SuppressWarnings("unchecked")
    public List<String> getDisabledWorlds() {
        return (List<String>) data.get("disabledworlds");
    }

    public EntityCheckData getEntityCheckData() {
        return (EntityCheckData) data.get("entitycheck");
    }

    public ItemCheckData getItemCheckData() {
        return (ItemCheckData) data.get("itemcheck");
    }

    public TPSCheckData getTPSCheckData() {
        return (TPSCheckData) data.get("laggcheck");
    }
    
    public Boolean isEnabled(CheckType type) {
        return data.get(type.toString().toLowerCase() + "check") != null;
    }
}
