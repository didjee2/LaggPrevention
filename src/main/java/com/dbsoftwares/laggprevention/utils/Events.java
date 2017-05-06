package com.dbsoftwares.laggprevention.utils;

/*
 * Created by DBSoftwares on 06 mei 2017
 * Developer: Dieter Blancke
 * Project: LaggPrevention
 * May only be used for CentrixPVP
 */

import com.dbsoftwares.laggprevention.LaggPrevention;
import com.dbsoftwares.laggprevention.runnable.LaggRunnable;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;

public class Events {

    public enum EventMode {NONE, SYNC, ASYNC}

    public static void call(EventMode mode, Event event) {
        PluginManager pluginManager = LaggPrevention.getInstance().getServer().getPluginManager();
        switch (mode) {
            case NONE: {
                pluginManager.callEvent(event);
                break;
            }
            case SYNC: {
                new LaggRunnable() {

                    @Override
                    public void run() {
                        pluginManager.callEvent(event);
                    }

                }.start();
                break;
            }
            case ASYNC: {
                new LaggRunnable() {

                    @Override
                    public void run() {
                        pluginManager.callEvent(event);
                    }

                }.startAsync();
                break;
            }
        }
    }

}