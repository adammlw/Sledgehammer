/*
 * Copyright (c) 2020 Noah Husby
 * Sledgehammer [Bungeecord] - ServerSHSelectFragment.java
 *
 * Sledgehammer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sledgehammer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Sledgehammer.  If not, see <https://github.com/noahhusby/Sledgehammer/blob/master/LICENSE/>.
 */

package com.noahhusby.sledgehammer.commands.fragments.admin.server;

import com.noahhusby.sledgehammer.chat.ChatConstants;
import com.noahhusby.sledgehammer.chat.ChatHelper;
import com.noahhusby.sledgehammer.chat.TextElement;
import com.noahhusby.sledgehammer.commands.fragments.ICommandFragment;
import com.noahhusby.sledgehammer.config.ServerConfig;
import com.noahhusby.sledgehammer.config.SledgehammerServer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

public class ServerSHSelectFragment implements ICommandFragment {

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length < 3) {
            sender.sendMessage(ChatHelper.makeAdminTextComponent(new TextElement("Usage: /sha server <server name> setsledgehammer <true/false>", ChatColor.RED)));
        } else {
            String arg = args[2].toLowerCase();
            if(arg.equals("true") || arg.equals("false")) {
                SledgehammerServer s = ServerConfig.getInstance().getServer(args[0]);

                if(!Boolean.parseBoolean(arg)) {
                    ServerConfig.getInstance().removeServer(s);
                } else {
                    if(s == null) s = new SledgehammerServer(args[0]);
                    ServerConfig.getInstance().pushServer(s);
                }

                sender.sendMessage(ChatConstants.getValueMessage("runs_sledgehammer", arg, s.getName()));
            } else {
                sender.sendMessage(ChatHelper.makeAdminTextComponent(new TextElement("Usage: /sha server <server name> setsledgehammer <true/false>", ChatColor.RED)));
            }
        }
    }

    @Override
    public String getName() {
        return "setsledgehammer";
    }

    @Override
    public String getPurpose() {
        return "Set whether the server is running sledgehammer or not";
    }

    @Override
    public String[] getArguments() {
        return new String[]{"<true/false>"};
    }
}
