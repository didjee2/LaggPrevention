package com.dbsoftwares.laggprevention.data.checks;

/*
 * Created by DBSoftwares on 30 april 2017
 * Developer: Dieter Blancke
 * Project: LaggPrevention
 * May only be used for CentrixPVP
 */

import com.dbsoftwares.laggprevention.enums.LaggEntity;
import com.google.common.collect.Maps;
import lombok.Data;

import java.util.Map;

@Data
public class TPSCheckData {

    Integer tpsAverage;
    Map<String, Integer> tpsTriggers;
    String clearMessage;
    Map<LaggEntity, Integer> amountToKill;
    Map<String, Integer> cooldowns;
    String mobRemoveMessage;
    Integer laggHaltDuration;
    String laggHaltEnabled;
    String laggHaltDisabled;

    public TPSCheckData(Integer tpsAverage, Map<String, Integer> tpsTriggers, String clearMessage, Map<LaggEntity, Integer> amountToKill,
                        Map<String, Integer> cooldowns, String mobRemoveMessage, Integer laggHaltDuration, String laggHaltEnabled, String laggHaltDisabled) {
        this.tpsAverage = tpsAverage;
        this.tpsTriggers = tpsTriggers;
        this.clearMessage = clearMessage;
        this.amountToKill = amountToKill;
        this.cooldowns = cooldowns;
        this.mobRemoveMessage = mobRemoveMessage;
        this.laggHaltDuration = laggHaltDuration;
        this.laggHaltEnabled = laggHaltEnabled;
        this.laggHaltDisabled = laggHaltDisabled;
    }

    public Integer getMobKillTrigger(LaggEntity entity) {
        if(!tpsTriggers.containsKey(entity.toString().toLowerCase())) return 0;
        return tpsTriggers.get(entity.toString().toLowerCase());
    }

    public Integer getKillAmount(LaggEntity entity) {
        if(!amountToKill.containsKey(entity)) return -1;
        return amountToKill.get(entity);
    }

    public Integer getCooldown(LaggEntity entity) {
        if(!cooldowns.containsKey(entity.toString().toLowerCase())) return 0;
        return cooldowns.get(entity.toString().toLowerCase());
    }

    public Integer getItemClearTrigger() {
        return tpsTriggers.get("item-clear");
    }

    public Integer getItemClearCooldown() {
        return cooldowns.get("item-clear");
    }

    public Integer getLaggHaltTrigger() {
        return tpsTriggers.get("lagg-halt");
    }

    public Integer getLaggHaltCooldown() {
        return cooldowns.get("lagg-halt");
    }
}