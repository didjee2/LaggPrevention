package com.dbsoftwares.laggprevention.checks.lagg;

/*
 * Created by DBSoftwares on 01 mei 2017
 * Developer: Dieter Blancke
 * Project: LaggPrevention
 * May only be used for CentrixPVP
 */

import com.dbsoftwares.laggprevention.LaggPrevention;
import com.dbsoftwares.laggprevention.data.checks.TPSCheckData;
import com.dbsoftwares.laggprevention.enums.CheckType;
import com.dbsoftwares.laggprevention.enums.LaggEntity;
import com.dbsoftwares.laggprevention.checks.Check;
import com.dbsoftwares.laggprevention.events.LaggHaltEvent;
import com.dbsoftwares.laggprevention.events.TPSTickEvent;
import com.dbsoftwares.laggprevention.runnable.LaggRunnable;
import com.dbsoftwares.laggprevention.utils.C;
import com.dbsoftwares.laggprevention.utils.Events;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LaggCheck extends Check implements Listener {

    private static Integer TICK_COUNT = 0;
    private static Long[] TICKS = new Long[600];
    private Integer count;
    private Map<String, Long> cooldown = Maps.newHashMap();

    public LaggCheck(LaggPrevention instance) {
        super(instance, CheckType.LAGG);

        instance.registerListener(this);
    }

    @Override
    public void tick() {
        TPSCheckData data = instance.getConfigManager().getTPSCheckData();
        count++;
        tpsRun();

        if(count >= 100) {
            count = 0;

            Double tps = getTPS(data.getTpsAverage() * 20);
            if(data.getItemClearTrigger() > 0 && tps <= data.getItemClearTrigger() && getCooldown("item-clear") <= System.currentTimeMillis()) {
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
                cooldown.put("item-clear", System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(data.getItemClearCooldown()));
            }

            Integer totalAmountKilled = 0;
            for(LaggEntity entity : LaggEntity.values()) {
                Integer killTriggerTPS = data.getMobKillTrigger(entity);
                Integer amountToKill = data.getKillAmount(entity);
                Integer cooldown = data.getCooldown(entity);

                if(killTriggerTPS <= 0 || amountToKill < 0 || cooldown <= 0) {
                    continue;
                }

                if(tps <= killTriggerTPS && getCooldown(entity.toString().toLowerCase()) <= System.currentTimeMillis()) {
                    Integer amountKilled = 0;
                    for(World world : Bukkit.getWorlds()) {
                        if(!canTick(world)) continue;

                        for(Entity e : world.getEntities()) {
                            if(amountKilled >= amountToKill) continue;
                            if(LaggEntity.getByEntity(e) == entity) {
                                amountKilled++;
                                totalAmountKilled++;
                                e.remove();
                            }
                        }
                    }
                    this.cooldown.put(entity.toString().toLowerCase(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(cooldown));
                }
            }
            if(totalAmountKilled > 0) {
                Bukkit.broadcastMessage(C.c(data.getMobRemoveMessage().replace("%amount%", totalAmountKilled.toString())));
            }

            if(data.getLaggHaltTrigger() > 0 && tps <= data.getLaggHaltTrigger() && instance.getLaggHalt().isDisabled()
                    && getCooldown("lagg-halt") <= System.currentTimeMillis() && data.getLaggHaltDuration() > 0) {

                instance.getLaggHalt().enable();
                Bukkit.broadcastMessage(C.c(data.getLaggHaltEnabled()));

                new LaggRunnable() {

                    @Override
                    public void run() {
                        instance.getLaggHalt().disable();
                        Bukkit.broadcastMessage(C.c(data.getLaggHaltDisabled()));
                    }

                }.start(TimeUnit.SECONDS, data.getLaggHaltDuration());
            }
        }
    }

    @EventHandler
    public void onLaggHalt(LaggHaltEvent event) {
        if(!event.getTo()) {
            cooldown.put("lagg-halt", System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(instance.getConfigManager().getTPSCheckData().getLaggHaltCooldown()));
        }
    }

    private Long getCooldown(String key) {
        if(!cooldown.containsKey(key)) {
            return cooldown.put(key, 0L);
        }
        return cooldown.get(key);
    }

    @Override
    public Integer tickDelay() {
        return 1;
    }

    @Override
    public void restartCheck() {
        super.restart();

        cooldown.clear();
        count = 0;
        TICK_COUNT = 0;
        TICKS = new Long[600];
    }

    private Double getTPS(Integer ticks){
        if (TICK_COUNT < ticks) {
            return 20.0D;
        }
        int target = (TICK_COUNT - 1 - ticks) % TICKS.length;
        long elapsed = System.currentTimeMillis() - TICKS[target];
        return ticks / (elapsed / 1000.0D);
    }

    private void tpsRun(){
        TICKS[(TICK_COUNT % TICKS.length)] = System.currentTimeMillis();
        TICK_COUNT += 1;

        Events.call(Events.EventMode.NONE, new TPSTickEvent(getTPS(100)));
    }
}