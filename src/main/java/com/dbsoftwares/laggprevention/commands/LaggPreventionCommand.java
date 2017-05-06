package com.dbsoftwares.laggprevention.commands;

/*
 * Created by DBSoftwares on 30 april 2017
 * Developer: Dieter Blancke
 * Project: LaggPrevention
 * May only be used for CentrixPVP
 */

import com.dbsoftwares.laggprevention.LaggPrevention;
import com.dbsoftwares.laggprevention.checks.Check;
import com.dbsoftwares.laggprevention.runnable.LaggRunnable;
import com.dbsoftwares.laggprevention.utils.C;
import com.dbsoftwares.laggprevention.utils.MathUtils;
import com.google.common.collect.Lists;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.concurrent.TimeUnit;

public class LaggPreventionCommand extends Command {

    LaggPrevention instance;

    public LaggPreventionCommand(LaggPrevention instance) {
        super("laggprevention");
        super.setAliases(Lists.newArrayList("laggprevent", "preventlagg", "lp"));
        this.instance = instance;
    }

    @Override
    public boolean execute(CommandSender sender, String cmd, String[] args) {
        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("reload")) {
                if(!sender.hasPermission("laggprevention.reload")) {
                    sender.sendMessage(C.c("&cYou don't have the permission to use this command!"));
                    return false;
                }
                if(instance.getLaggHalt().isEnabled()) {
                    instance.getLaggHalt().disable();
                    sender.sendMessage(C.c("&6Disabled &bLaggHalt&6!"));
                }
                instance.getConfigManager().reload();
                sender.sendMessage(C.c("&6Reloaded &bconfiguration&6!"));
                for(Check check : instance.getChecks()) {
                    check.restartCheck();
                    sender.sendMessage(C.c("&6Reloaded &b" + check.getType().toString().toLowerCase() + "check&6!"));
                }
                return true;
            } else if(args[0].equalsIgnoreCase("halt")) {
                if(instance.getLaggHalt().isEnabled()) {
                    instance.getLaggHalt().disable();
                    sender.sendMessage(C.c("&6LaggHalt has been &bdisabled&6!"));
                } else {
                    instance.getLaggHalt().enable();
                    sender.sendMessage(C.c("&6LaggHalt has been &benabled&6!"));
                }
                return true;
            }
        } else if(args.length == 2) {
            if(args[0].equalsIgnoreCase("halt")) {
                if(!MathUtils.isInteger(args[1])) {
                    sender.sendMessage(C.c("&6Please enter a valid number!"));
                    return false;
                }
                if(instance.getLaggHalt().isEnabled()) {
                    sender.sendMessage(C.c("&6LaggHalt is already enabled!"));
                    return false;
                }
                Integer time = Integer.parseInt(args[1]);
                if(time < 15) {
                    sender.sendMessage(C.c("&6Please enter a time above &b15 seconds&6!"));
                    return false;
                }

                instance.getLaggHalt().enable();
                sender.sendMessage(C.c("&6LaggHalt has been &benabled &6for the next &b" + time + " &6seconds!"));

                new LaggRunnable() {

                    @Override
                    public void run() {
                        instance.getLaggHalt().disable();
                    }

                }.start(TimeUnit.SECONDS, time);
                return true;
            }
        }
        help(sender);
        return false;
    }

    public void help(CommandSender sender) {
        sender.sendMessage(C.c("&bLaggPrevention &6help list:"));
        sender.sendMessage(C.c("&6- /lp reload &e| Reloads the config."));
        sender.sendMessage(C.c("&6- /lp halt [time] &e| Enables/disables lagg halt."));
    }
}