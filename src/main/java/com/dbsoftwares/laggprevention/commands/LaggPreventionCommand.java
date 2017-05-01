package com.dbsoftwares.laggprevention.commands;

/*
 * Created by DBSoftwares on 30 april 2017
 * Developer: Dieter Blancke
 * Project: LaggPrevention
 * May only be used for CentrixPVP
 */

import com.dbsoftwares.laggprevention.LaggPrevention;
import com.dbsoftwares.laggprevention.preventers.Check;
import com.dbsoftwares.laggprevention.utils.C;
import com.google.common.collect.Lists;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

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
                sender.sendMessage(C.c("&6Reloading &bconfiguration &6...!"));
                instance.getConfigManager().reload();
                sender.sendMessage(C.c("&6Reloaded &bconfiguration&6!"));
                sender.sendMessage(C.c("&6Reloading &bchecks&6!"));
                for(Check check : instance.getChecks()) {
                    check.restartCheck();
                    sender.sendMessage(C.c("&6- Reloaded &b" + check.getType().toString().toLowerCase() + "check&6!"));
                }
                sender.sendMessage(C.c("&6Reloaded &bchecks&6!"));
                return true;
            }
        }
        help(sender);
        return false;
    }

    public void help(CommandSender sender) {
        sender.sendMessage(C.c("&bLaggPrevention &6help list:"));
        sender.sendMessage(C.c("&6- /laggprevention reload &e| Reloads the config."));
    }
}