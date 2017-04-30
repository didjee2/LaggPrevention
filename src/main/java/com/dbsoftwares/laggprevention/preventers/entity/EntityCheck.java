package com.dbsoftwares.laggprevention.preventers.entity;

/*
 * Created by DBSoftwares on 30 april 2017
 * Developer: Dieter Blancke
 * Project: LaggPrevention
 * May only be used for CentrixPVP
 */

import com.dbsoftwares.laggprevention.preventers.Check;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

public class EntityCheck extends Check {

    public void tick() {
        for(World world : Bukkit.getWorlds()) {
            if(!canTick(world)) continue;

            for(Chunk chunk : world.getLoadedChunks()) {

            }
        }
    }

}