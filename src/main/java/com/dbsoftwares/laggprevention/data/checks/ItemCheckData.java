package com.dbsoftwares.laggprevention.data.checks;

/*
 * Created by DBSoftwares on 30 april 2017
 * Developer: Dieter Blancke
 * Project: LaggPrevention
 * May only be used for CentrixPVP
 */

import lombok.Data;

import java.util.Map;

@Data
public class ItemCheckData {

    Integer maxInChunk;
    Integer maxInWorld;
    Integer itemClearDelay;
    String clearMessage;
    Map<Integer, String> clearIncomingMessages;

    public ItemCheckData(Integer maxInChunk, Integer maxInWorld, Integer itemClearDelay, String clearMessage, Map<Integer, String> clearIncomingMessages) {
        this.maxInChunk = maxInChunk;
        this.maxInWorld = maxInWorld;
        this.itemClearDelay = itemClearDelay;
        this.clearMessage = clearMessage;
        this.clearIncomingMessages = clearIncomingMessages;
    }
}