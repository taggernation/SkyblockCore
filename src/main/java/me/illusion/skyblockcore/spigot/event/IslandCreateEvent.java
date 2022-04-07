package me.illusion.skyblockcore.spigot.event;

import lombok.Getter;
import me.illusion.skyblockcore.spigot.island.Island;
import org.bukkit.event.HandlerList;

@Getter
public class IslandCreateEvent extends IslandEvent {

    private static final HandlerList handlers = new HandlerList();

    private final Island island;

    public IslandCreateEvent(Island island) {
        super();
        this.island = island;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
