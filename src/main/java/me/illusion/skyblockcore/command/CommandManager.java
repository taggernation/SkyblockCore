package me.illusion.skyblockcore.command;

import me.illusion.skyblockcore.CorePlugin;
import me.illusion.utilities.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class CommandManager {

    private static CommandMap commandMap;

    static {
        try {
            Server server = Bukkit.getServer();

            Field commandMapField = server.getClass().getDeclaredField("commandMap");

            commandMapField.setAccessible(true);

            commandMap = (CommandMap) commandMapField.get(server);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private final Map<String, SkyblockCommand> commands = new HashMap<>();
    private final CorePlugin main;

    public CommandManager(CorePlugin main) {
        this.main = main;
    }

    private void makeCommand(SkyblockCommand command) {
        String[] split = StringUtil.split(command.getIdentifier(), '.');
        String name = split[0];

        commandMap.register(name, new BaseCommand(name, main, command));
    }

    public void register(SkyblockCommand command) {
        commands.put(command.getIdentifier(), command);
    }

    public SkyblockCommand get(String identifier) {
        return commands.get(identifier);
    }

    public SkyblockCommand get(String name, String... args) {
        String identifier = String.join(".", name, String.join(".", args));

        return get(identifier);
    }
}