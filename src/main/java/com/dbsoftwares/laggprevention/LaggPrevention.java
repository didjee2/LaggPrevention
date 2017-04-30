package com.dbsoftwares.laggprevention;

/*
 * Created by DBSoftwares on 30 april 2017
 * Developer: Dieter Blancke
 * Project: LaggPrevention
 * May only be used for CentrixPVP
 */

import com.dbsoftwares.laggprevention.data.ConfigManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class LaggPrevention extends JavaPlugin {

    @Getter private static LaggPrevention instance;
    @Getter private ConfigManager configManager;

    public void onEnable(){
        instance = this;

        configManager = new ConfigManager(this);
        configManager.load();


    }
}