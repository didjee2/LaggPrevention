package com.dbsoftwares.laggprevention.enums;

/*
 * Created by DBSoftwares on 30 april 2017
 * Developer: Dieter Blancke
 * Project: LaggPrevention
 * May only be used for CentrixPVP
 */

import com.google.common.collect.Lists;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.List;

public enum LaggEntity {

    ELDER_GUARDIAN(EntityType.ELDER_GUARDIAN),
    SKELETON(EntityType.SKELETON, EntityType.WITHER_SKELETON),
    VILLAGER(EntityType.VILLAGER, EntityType.ZOMBIE_VILLAGER),
    HORSE(EntityType.HORSE, EntityType.SKELETON_HORSE, EntityType.ZOMBIE_HORSE, EntityType.MULE, EntityType.DONKEY),
    STRAY(EntityType.STRAY),
    VEX(EntityType.VEX),
    VINDATOR(EntityType.VINDICATOR),
    CREEPER(EntityType.CREEPER),
    SPIDER(EntityType.SPIDER, EntityType.CAVE_SPIDER),
    GIANT(EntityType.GIANT),
    ZOMBIE(EntityType.ZOMBIE),
    SLIME(EntityType.SLIME),
    GHAST(EntityType.GHAST),
    PIG_ZOMBIE(EntityType.PIG_ZOMBIE),
    ENDERMAN(EntityType.ENDERMAN),
    SILVERFISH(EntityType.SILVERFISH),
    BLAZE(EntityType.BLAZE),
    MAGMA_CUBE(EntityType.MAGMA_CUBE),
    ENDER_DRAGON(EntityType.ENDER_DRAGON),
    WITHER(EntityType.WITHER),
    ENDERMITE(EntityType.ENDERMITE),
    SHULKER(EntityType.SHULKER),
    GUARDIAN(EntityType.GUARDIAN),
    PIG(EntityType.PIG),
    SHEEP(EntityType.SHEEP),
    COW(EntityType.COW, EntityType.MUSHROOM_COW),
    CHICKEN(EntityType.CHICKEN),
    SQUID(EntityType.SQUID),
    WOLF(EntityType.WOLF),
    SHOWMAN(EntityType.SNOWMAN),
    OCELOT(EntityType.OCELOT),
    IRON_GOLEM(EntityType.IRON_GOLEM),
    RABBIT(EntityType.RABBIT),
    POLAR_BEAR(EntityType.POLAR_BEAR),
    LLAMA(EntityType.LLAMA);

    EntityType[] entityTypes;

    LaggEntity(EntityType... entities) {
        entityTypes = entities;
    }

    public EntityType[] getEntityTypes() {
        return entityTypes;
    }

    public static LaggEntity getByEntity(EntityType type) {
        for(LaggEntity entity : LaggEntity.values()) {
            for(EntityType et : entity.getEntityTypes()) {
                if(et.equals(type)) {
                    return entity;
                }
            }
        }
        return null;
    }

    public static LaggEntity getByEntity(Entity entity) {
        return getByEntity(entity.getType());
    }

    public static Boolean isKnown(EntityType type) {
        return getByEntity(type) != null;
    }

    public static Boolean isKnown(Entity entity) {
        return isKnown(entity.getType());
    }

    public static List<Entity> getKnownEntities(Chunk chunk) {
        List<Entity> entities = Lists.newArrayList();

        for(Entity entity : chunk.getEntities()) {
            if(isKnown(entity)) {
                entities.add(entity);
            }
        }

        return entities;
    }

    public static List<Entity> getKnownEntities(LaggEntity laggEntity, Chunk chunk) {
        List<Entity> entities = Lists.newArrayList();

        for(Entity entity : chunk.getEntities()) {
            LaggEntity le = getByEntity(entity);
            if(le == null) continue;

            if(laggEntity.equals(le)) {
                entities.add(entity);
            }
        }

        return entities;
    }
}