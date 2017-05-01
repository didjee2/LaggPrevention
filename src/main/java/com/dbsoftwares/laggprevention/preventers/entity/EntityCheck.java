package com.dbsoftwares.laggprevention.preventers.entity;

/*
 * Created by DBSoftwares on 30 april 2017
 * Developer: Dieter Blancke
 * Project: LaggPrevention
 * May only be used for CentrixPVP
 */

import com.dbsoftwares.laggprevention.LaggPrevention;
import com.dbsoftwares.laggprevention.data.checks.EntityCheckData;
import com.dbsoftwares.laggprevention.enums.CheckType;
import com.dbsoftwares.laggprevention.enums.LaggEntity;
import com.dbsoftwares.laggprevention.preventers.Check;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.List;
import java.util.Map;

public class EntityCheck extends Check implements Listener {

    public EntityCheck(LaggPrevention instance) {
        super(instance, CheckType.ENTITY);

        instance.registerListener(this);
    }

    @Override
    public void tick() {
        EntityCheckData data = instance.getConfigManager().getEntityCheckData();
        if(!data.getKill()) return;
        for(World world : Bukkit.getWorlds()) {
            if(!canTick(world)) continue;

            /*
             * Removing entities above max limit if kill is enabled.
             */
            for(Chunk chunk : world.getLoadedChunks()) {
                Integer amount = LaggEntity.getKnownEntities(chunk).size();

                if(amount > data.getTotal()) {
                    for(Integer i = data.getTotal(); i < amount; i++) {
                        chunk.getEntities()[i].remove();
                    }
                }
            }

            /*
             * Removing entities (per type) above limit if kill is enabled.
             */
            Map<LaggEntity, List<Entity>> mobs = Maps.newHashMap();
            for(Chunk chunk : world.getLoadedChunks()) {
                for(Entity entity : chunk.getEntities()) {
                    LaggEntity laggEntity = LaggEntity.getByEntity(entity);
                    if(laggEntity == null) continue;

                    if(mobs.containsKey(laggEntity)) {
                        List<Entity> entities = mobs.get(laggEntity);
                        entities.add(entity);
                        mobs.put(laggEntity, entities);
                    } else {
                        mobs.put(laggEntity, Lists.newArrayList(entity));
                    }
                }
            }

            for(LaggEntity entity : mobs.keySet()) {
                if(data.getMobLimits().containsKey(entity)) {
                    Integer limit = data.getMobLimits().get(entity);
                    Integer amount = mobs.get(entity).size();

                    if(amount > limit) {
                        for(Integer i = limit; i < amount; i++) {
                            mobs.get(entity).get(i).remove();
                        }
                    }
                }
            }

            /*
             * Clearing maps & lists.
             */
            for(List<Entity> list : mobs.values()) {
                list.clear();
            }
            mobs.clear();
        }
    }

    @Override
    public Integer tickDelay() {
        return 40;
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        EntityCheckData data = instance.getConfigManager().getEntityCheckData();
        Entity entity = event.getEntity();

        LaggEntity laggEntity = LaggEntity.getByEntity(entity);
        if(laggEntity == null) return;

        Integer totalAmount = LaggEntity.getKnownEntities(entity.getLocation().getChunk()).size();
        if(totalAmount > data.getTotal()) {
            event.setCancelled(true);
        } else if(data.getMobLimits().containsKey(laggEntity)) {
            Integer entityAmount = LaggEntity.getKnownEntities(laggEntity, entity.getLocation().getChunk()).size();

            if(entityAmount > data.getMobLimits().get(laggEntity)) {
                event.setCancelled(true);
            }
        }
    }

    @Override
    public void restartCheck() {
        super.restart();
    }
}