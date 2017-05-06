package com.dbsoftwares.laggprevention.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LaggHaltEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
	private Boolean from;
	private Boolean to;

	public LaggHaltEvent(Boolean from, Boolean to){
		this.from = from;
		this.to = to;
	}

	public Boolean getTo() {
	    return to;
    }

    public Boolean getFrom() {
	    return from;
    }

	@Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
