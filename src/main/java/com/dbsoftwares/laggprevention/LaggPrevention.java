package com.dbsoftwares.laggprevention;

/*
 * Created by DBSoftwares on 30 april 2017
 * Developer: Dieter Blancke
 * Project: LaggPrevention
 * May only be used for CentrixPVP
 */

import com.dbsoftwares.laggprevention.commands.LaggPreventionCommand;
import com.dbsoftwares.laggprevention.data.ConfigManager;
import com.dbsoftwares.laggprevention.enums.CheckType;
import com.dbsoftwares.laggprevention.checks.Check;
import com.dbsoftwares.laggprevention.checks.entity.EntityCheck;
import com.dbsoftwares.laggprevention.checks.item.ItemCheck;
import com.dbsoftwares.laggprevention.checks.lagg.LaggCheck;
import com.dbsoftwares.laggprevention.preventions.LaggHalt;
import com.google.common.collect.Lists;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LaggPrevention extends JavaPlugin {

    @Getter private static final Logger Log = Logger.getLogger("LaggPrevention");
    @Getter private static LaggPrevention instance;
    @Getter private ConfigManager configManager;
    @Getter private List<Check> checks = Lists.newArrayList();
    @Getter private LaggHalt laggHalt;

    @Override
    public void onEnable(){
        Long begin = System.currentTimeMillis();
        instance = this;

        configManager = new ConfigManager(this);
        configManager.load();
        Log.log(Level.INFO, "[LaggPrevention] Loaded configuration");

        registerCommand(new LaggPreventionCommand(this));
        Log.log(Level.INFO, "[LaggPrevention] Loaded 1 command ");

        Log.log(Level.INFO, "[LaggPrevention] Loading checks ...");
        for(CheckType type : CheckType.values()) {
            if(configManager.isEnabled(type)) {
                try {
                    Class<?> clazz = Class.forName(type.getClazz());
                    
                    Check check = (Check) clazz.getConstructor(LaggPrevention.class).newInstance(this);
                    checks.add(check);
                    
                    Log.log(Level.INFO, "[LaggPrevention] Loaded " + type.toString().toLowerCase() + "check!");
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
        Log.log(Level.INFO, "[LaggPrevention] Loaded " + checks.size() + " checks");

        laggHalt = new LaggHalt(this);

        Log.log(Level.INFO, "[LaggPrevention] LaggPrevention has been enabled in " + (System.currentTimeMillis() - begin) + "ms!");
    }

    @Override
    public void onDisable() {
        if(laggHalt.isEnabled()) {
            laggHalt.disable();
        }
    }

    /*
     * Registers a listener into Bukkit.
     */
    public Listener registerListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this);
        return listener;
    }

    /*
     * Unregisters a listener from Bukkit.
     */
    public void unregisterListener(Listener listener) {
        HandlerList.unregisterAll(listener);
    }

    /*
     * Force registers a Command in Bukkit. (No need to write the command into plugin.yml)
     */
    public void registerCommand(Command command){
        try {
            Field commandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

            commandMap.setAccessible(true);
            CommandMap map = (CommandMap) commandMap.get(Bukkit.getServer());

            Command cmd = map.getCommand(command.getName());
            if(cmd != null) unregisterCommands(cmd.getName(), cmd.getAliases());

            map.register(command.getName(), command);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Force unregisters the given command with it aliases.
     */
    @SuppressWarnings("unchecked")
    public void unregisterCommands(String command, List<String> commands){
        try {
            Field commandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMap.setAccessible(true);

            SimpleCommandMap scm = (SimpleCommandMap)commandMap.get(Bukkit.getServer());

            Field field = scm.getClass().getDeclaredField("knownCommands");
            field.setAccessible(true);

            Map<String, Command> cmds = (Map<String, Command>) field.get(scm);

            if(cmds.containsKey(command)) cmds.remove(command);
            for(String s : commands){
                if(cmds.containsKey(s)) {
                    cmds.remove(s);
                }
            }
            List<String> remove = Lists.newArrayList();
            for (Map.Entry<String, Command> entry : cmds.entrySet()) {
                if (entry.getValue().getAliases() == null) {
                    continue;
                }
                for (String cmd : commands) {
                    for (String s : entry.getValue().getAliases()) {
                        if (s.equalsIgnoreCase(cmd)) {
                            remove.add(entry.getKey());
                        }
                    }
                }
            }
            for(String s : remove) {
                cmds.remove(s);
            }

            field.set(scm, cmds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
