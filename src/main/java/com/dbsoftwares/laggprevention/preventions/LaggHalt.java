package com.dbsoftwares.laggprevention.preventions;

/*
 * Created by DBSoftwares on 06 mei 2017
 * Developer: Dieter Blancke
 * Project: LaggPrevention
 * May only be used for CentrixPVP
 */

import com.dbsoftwares.laggprevention.LaggPrevention;
import com.dbsoftwares.laggprevention.data.ConfigManager;
import com.dbsoftwares.laggprevention.events.LaggHaltEvent;
import com.dbsoftwares.laggprevention.utils.Events;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import java.util.Map;

public class LaggHalt implements Listener {

    private LaggPrevention instance;
    private Map<World, Integer[]> limits = Maps.newHashMap();
    private Boolean enabled = false;

    public LaggHalt(LaggPrevention instance) {
        this.instance = instance;
    }

    public void enable() {
        if (enabled) return;

        for (World world : Bukkit.getWorlds()) {
            if (!canExecute(world)) continue;

            Integer[] limits = new Integer[6];
            limits[0] = world.getAmbientSpawnLimit();
            world.setAmbientSpawnLimit(0);
            limits[1] = world.getAnimalSpawnLimit();
            world.setAnimalSpawnLimit(0);
            limits[2] = world.getMonsterSpawnLimit();
            world.setMonsterSpawnLimit(0);
            limits[3] = (int) world.getTicksPerAnimalSpawns();
            world.setTicksPerAnimalSpawns(0);
            limits[4] = (int) world.getTicksPerMonsterSpawns();
            world.setTicksPerMonsterSpawns(0);
            limits[5] = world.getWaterAnimalSpawnLimit();
            world.setWaterAnimalSpawnLimit(0);

            this.limits.put(world, limits);

            for(Entity entity : world.getEntities()) {
                if(entity instanceof Item
                        || entity instanceof TNTPrimed
                        || entity instanceof ExperienceOrb
                        || entity instanceof FallingBlock
                        || (entity instanceof LivingEntity
                        && !(entity instanceof Tameable)
                        && !(entity instanceof Player)
                        && !(entity instanceof ArmorStand))) {
                    entity.remove();
                }
            }
        }

        instance.registerListener(this);

        Events.call(Events.EventMode.SYNC, new LaggHaltEvent(false, true));
    }

    public void disable() {
        if (!enabled) return;

        for (Map.Entry<World, Integer[]> entry : limits.entrySet()) {
            World world = entry.getKey();
            Integer[] limits = entry.getValue();

            world.setAmbientSpawnLimit(limits[0]);
            world.setAnimalSpawnLimit(limits[1]);
            world.setMonsterSpawnLimit(limits[2]);
            world.setTicksPerAnimalSpawns(limits[3]);
            world.setTicksPerMonsterSpawns(limits[4]);
            world.setWaterAnimalSpawnLimit(limits[5]);
        }

        instance.unregisterListener(this);
        Events.call(Events.EventMode.SYNC, new LaggHaltEvent(true, false));
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public Boolean isDisabled() {
        return !enabled;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if(!canExecute(event.getBlock().getWorld())) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBurn(BlockBurnEvent event) {
        if(!canExecute(event.getBlock().getWorld())) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityExplode(EntityExplodeEvent event) {
        if(!canExecute(event.getLocation().getWorld())) return;
        event.setCancelled(true);
        event.blockList().clear();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLeaveDecay(LeavesDecayEvent event) {
        if(!canExecute(event.getBlock().getWorld())) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockForm(BlockFormEvent event) {
        if(!canExecute(event.getBlock().getWorld())) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockSpread(BlockSpreadEvent event) {
        if(!canExecute(event.getBlock().getWorld())) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockFade(BlockFadeEvent event) {
        if(!canExecute(event.getBlock().getWorld())) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockFromTo(BlockFromToEvent event) {
        if(!canExecute(event.getBlock().getWorld())) return;
        event.setCancelled(true);
    }

    private Boolean canExecute(World world) {
        ConfigManager configManager = LaggPrevention.getInstance().getConfigManager();

        if (configManager.getDisabledWorlds() != null) {
            for (String w : configManager.getDisabledWorlds()) {
                if (world.getName().equalsIgnoreCase(w)) {
                    return false;
                }
            }
        }

        if (configManager.getEnabledWorlds() != null) {
            for (String w : configManager.getEnabledWorlds()) {
                if (world.getName().equalsIgnoreCase(w)) {
                    return true;
                }
            }
        }

        return true;
    }
}