package com.dbsoftwares.laggprevention.preventers;

/*
 * Created by DBSoftwares on 30 april 2017
 * Developer: Dieter Blancke
 * Project: LaggPrevention
 * May only be used for CentrixPVP
 */

import com.dbsoftwares.laggprevention.LaggPrevention;
import com.dbsoftwares.laggprevention.data.ConfigManager;
import org.bukkit.World;

public abstract class Check {

    public Boolean canTick(World world) {
        ConfigManager configManager = LaggPrevention.getInstance().getConfigManager();

        if(configManager.getDisabledWorlds() != null) {
            for(String w : configManager.getDisabledWorlds()) {
                if(world.getName().equalsIgnoreCase(w)) {
                    return false;
                }
            }
        }

        if(configManager.getEnabledWorlds() != null) {
            for(String w : configManager.getEnabledWorlds()) {
                if(world.getName().equalsIgnoreCase(w)) {
                    return true;
                }
            }
        }
        return false;
    }

    public abstract void tick();
}