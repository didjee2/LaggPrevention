package com.dbsoftwares.laggprevention.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TPSTickEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
	private Double TPS;

	public TPSTickEvent(Double TPS){
		this.TPS = TPS;
	}

	public Double getTPS() {
	    return TPS;
    }

	@Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
