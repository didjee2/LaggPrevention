package com.dbsoftwares.laggprevention.preventers.item;

/*
 * Created by DBSoftwares on 30 april 2017
 * Developer: Dieter Blancke
 * Project: LaggPrevention
 * May only be used for CentrixPVP
 */

import com.dbsoftwares.laggprevention.LaggPrevention;
import com.dbsoftwares.laggprevention.data.checks.EntityCheckData;
import com.dbsoftwares.laggprevention.data.checks.ItemCheckData;
import com.dbsoftwares.laggprevention.enums.CheckType;
import com.dbsoftwares.laggprevention.enums.LaggEntity;
import com.dbsoftwares.laggprevention.preventers.Check;
import com.dbsoftwares.laggprevention.utils.C;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;

import java.util.List;
import java.util.Map;

public class ItemCheck extends Check {

    private Integer delay = 0;

    public ItemCheck(LaggPrevention instance) {
        super(instance, CheckType.ITEM);
    }

    @Override
    public void tick() {
        ItemCheckData data = instance.getConfigManager().getItemCheckData();

        /*
         * Item removal runnable. Clears all items in enabled worlds.
         */
        if(data.getItemClearDelay() > 0) {
            Integer timeLeft = data.getItemClearDelay() - delay;
            if(timeLeft > 0 && data.getClearIncomingMessages().containsKey(timeLeft)) {
                Bukkit.broadcastMessage(C.c(data.getClearIncomingMessages().get(timeLeft).replace("%time%", timeLeft.toString())));
            }

            if(timeLeft <= 0) {
                delay = 0;
                Integer amount = 0;

                for(World world : Bukkit.getWorlds()) {
                    if(!canTick(world)) continue;

                    for(Entity entity : world.getEntities()) {
                        if(entity instanceof Item) {
                            amount++;
                            entity.remove();
                        }
                    }
                }
                Bukkit.broadcastMessage(C.c(data.getClearMessage().replace("%amount%", amount.toString())));
                return;
            }
            delay++;
        }

        /*
         * Checking if Items in chunk is higher then maximum allowed, if yes, removing every item above.
         */
        if(data.getMaxInChunk() > 0) {
            for(World world : Bukkit.getWorlds()) {
                if(!canTick(world)) continue;

                for(Chunk chunk : world.getLoadedChunks()) {
                    List<Entity> items = Lists.newArrayList();

                    for(Entity entity : chunk.getEntities()) {
                        if(entity instanceof Item) {
                            items.add(entity);
                        }
                    }

                    if(items.size() > data.getMaxInChunk()) {
                        for(Integer i = data.getMaxInChunk(); i < items.size(); i++) {
                            items.get(i).remove();
                        }
                    }

                    items.clear();
                }
            }
        }

        /*
         * Checking if Items in world is higher then maximum allowed, if yes, removing every item above.
         */
        if(data.getMaxInWorld() > 0) {
            for(World world : Bukkit.getWorlds()) {
                if(!canTick(world)) continue;

                List<Entity> items = Lists.newArrayList();

                for(Entity entity : world.getEntities()) {
                    if(entity instanceof Item) {
                        items.add(entity);
                    }
                }

                if(items.size() > data.getMaxInWorld()) {
                    for(Integer i = data.getMaxInWorld(); i < items.size(); i++) {
                        items.get(i).remove();
                    }
                }

                items.clear();
            }
        }
    }

    @Override
    public Integer tickDelay() {
        return 20;
    }

    @Override
    public void restartCheck() {
        super.restart();
        this.delay = 0;
    }
}