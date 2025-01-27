package me.illusion.skyblockcore.bungee.comparison;

import me.illusion.skyblockcore.shared.data.ServerInfo;

import java.util.Comparator;

public class EmptyFitComparator implements Comparator<ServerInfo> {

    @Override
    public int compare(ServerInfo serverInfo, ServerInfo t1) {
        return serverInfo.getIslandCount() - t1.getIslandCount();
    }
}
