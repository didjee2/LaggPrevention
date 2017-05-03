package com.dbsoftwares.laggprevention.preventers.lagg;

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
import com.dbsoftwares.laggprevention.preventers.Check;
import com.dbsoftwares.laggprevention.utils.C;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LaggCheck extends Check {

    private static Integer TICK_COUNT = 0;
    private static Long[] TICKS = new Long[600];
    private Integer count;
    private Map<String, Long> cooldown = Maps.newHashMap();

    public LaggCheck(LaggPrevention instance) {
        super(instance, CheckType.LAGG);
    }

    @Override
    public void tick() {
        TPSCheckData data = instance.getConfigManager().getTPSCheckData();
        count++;
        tpsRun();

        if(count >= 100) {
            count = 0;

            Double tps = getTPS();
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
        count = 0;
        TICK_COUNT = 0;
        TICKS = new Long[600];
    }

    public Double getTPS(){
        return getTPS(100);
    }

    private Double getTPS(Integer ticks){
        if (TICK_COUNT < ticks) {
            return 20.0D;
        }
        int target = (TICK_COUNT - 1 - ticks) % TICKS.length;
        long elapsed = System.currentTimeMillis() - TICKS[target];
        return ticks / (elapsed / 1000.0D);
    }

    public Long getElapsed(Integer tickID){
        Long time = TICKS[(tickID % TICKS.length)];
        return System.currentTimeMillis() - time;
    }

    private void tpsRun(){
        TICKS[(TICK_COUNT % TICKS.length)] = System.currentTimeMillis();
        TICK_COUNT += 1;
    }
}