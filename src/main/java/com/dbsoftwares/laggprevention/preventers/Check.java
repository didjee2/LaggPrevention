package com.dbsoftwares.laggprevention.preventers;

/*
 * Created by DBSoftwares on 30 april 2017
 * Developer: Dieter Blancke
 * Project: LaggPrevention
 * May only be used for CentrixPVP
 */

import com.dbsoftwares.laggprevention.LaggPrevention;
import com.dbsoftwares.laggprevention.data.ConfigManager;
import com.dbsoftwares.laggprevention.enums.CheckType;
import lombok.Getter;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public abstract class Check {

    public LaggPrevention instance;
    @Getter public CheckType type;
    private BukkitTask task;

    public Check(LaggPrevention instance, CheckType type) {
        this.instance = instance;
        this.type = type;

        start();
    }

    public void start() {
        this.task = new BukkitRunnable() {

            @Override
            public void run() {
                tick();
            }

        }.runTaskTimer(instance, tickDelay(), tickDelay());
    }

    public void cancel() {
        if(task != null) {
            task.cancel();
        }
    }

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

        return true;
    }

    public abstract void tick();

    public abstract Integer tickDelay();

    public abstract void restartCheck();

    public void restart() {
        cancel();
        start();
    }
}