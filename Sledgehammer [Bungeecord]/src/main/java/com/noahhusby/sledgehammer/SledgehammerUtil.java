/*
 * Copyright (c) 2020 Noah Husby
 * Sledgehammer [Bungeecord] - SledgehammerUtil.java
 * All rights reserved.
 */

package com.noahhusby.sledgehammer;

import com.noahhusby.sledgehammer.config.ConfigHandler;
import com.noahhusby.sledgehammer.config.ServerConfig;
import com.noahhusby.sledgehammer.config.types.Server;
import com.noahhusby.sledgehammer.projection.GeographicProjection;
import com.noahhusby.sledgehammer.projection.ModifiedAirocean;
import com.noahhusby.sledgehammer.projection.ScaleProjection;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

public class SledgehammerUtil {

    private static GeographicProjection projection = new ModifiedAirocean();
    private static GeographicProjection uprightProj = GeographicProjection.orientProjection(projection, GeographicProjection.Orientation.upright);
    private static ScaleProjection scaleProj = new ScaleProjection(uprightProj, Constants.SCALE, Constants.SCALE);

    public static double[] toGeo(double x, double z) {
        return scaleProj.toGeo(x, z);
    }

    public static double[] fromGeo(double lon, double lat) {
        return scaleProj.fromGeo(lon, lat);
    }

    public static ServerInfo getServerFromName(String name) {
        return ProxyServer.getInstance().getServerInfo(name);
    }

    public static ServerInfo getServerFromPlayerName(String name) {
        return ProxyServer.getInstance().getPlayer(name).getServer().getInfo();
    }

    public static String getServerNameByPlayer(CommandSender sender) {
        return ProxyServer.getInstance().getPlayer(sender.getName()).getServer().getInfo().getName();
    }

    public static boolean isServerRegional(ServerInfo server) {
        return isServerRegional(server.getName());
    }

    public static boolean isServerRegional(String name) {
        for(Server s : ServerConfig.getInstance().getServers()) {
            if(s.name.equals(name)) return true;
        }
        return false;
    }

    public static boolean isGenuineRequest(String u) {
        try {
            return u.equals(ConfigHandler.authenticationCode);
        } catch (Exception e) {
            Sledgehammer.logger.info("Error occurred while parsing incoming authentication command!");
            return false;
        }
    }

    public static String getRawArguments(String[] args) {
        if(args.length == 0) {
            return "";
        } else if(args.length == 1) {
            return args[0];
        }

        String arguments = args[0];

        for(int x = 1; x < args.length; x++) {
            arguments+=" "+args[x];
        }

        return arguments;
    }
}
