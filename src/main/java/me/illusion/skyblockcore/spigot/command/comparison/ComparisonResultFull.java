package me.illusion.skyblockcore.spigot.command.comparison;

import me.illusion.skyblockcore.spigot.command.SkyblockCommand;

import java.util.Map;

public class ComparisonResultFull {
    private final Map<String, SkyblockCommand> commands;

    public ComparisonResultFull(Map<String, SkyblockCommand> commands) {
        this.commands = commands;
    }

    public SkyblockCommand match(String input) {
        String[] inputs = input.split("\\.");

        //in case input is empty
        if (inputs.length == 0)
            return null;

        //case alias.arg
        //first take out the alias
        for (SkyblockCommand command : commands.values()) {
            if (inputs.length == 1) {
                if (command.getIdentifier().equals(input) || searchAliases(input, command))
                    return command;
                return null;
            }
            //case for alias.command
            //if it's an alias of another command
            if (findAliasingCommand(inputs[0], command) && searchArgs(inputs, command))
                return command;


            //look for identifiers
            if (searchIds(inputs, command) && searchArgs(inputs, command))
                return command;

        }
        return null;
    }

    private boolean searchArgs(String[] inputs, SkyblockCommand command) {
        String[] args = command.getIdentifier().split("\\.");

        if (inputs.length > args.length) return false;
        //inputs has to be smaller than the command for autocomplete to work
        for (int index = 1; index < inputs.length; index++) {
            if (args[index].equals("*")) {
                continue;
            }
            if (!args[index].equals(inputs[index])) {
                return false;
            }
        }
        return true;
    }

    private boolean searchAliases(String input, SkyblockCommand command) {
        String[] aliases = command.getAliases();
        for (String alias : aliases)
            if (alias.equals(input))
                return true;


        return false;
    }

    private boolean searchIds(String[] inputs, SkyblockCommand command) {
        String[] ids = command.getIdentifier().split("\\.");
        String id = ids[0];
        return id.equals(inputs[0]);
    }

    private boolean findAliasingCommand(String input, SkyblockCommand command) {
        String id = command.getIdentifier().split("\\.")[0];

        SkyblockCommand it = commands.get(id);
        if (it.getIdentifier().equals(id))
            for (String alias : it.getAliases())
                if (alias.equals(input))
                    return true;

        return false;

    }


}
