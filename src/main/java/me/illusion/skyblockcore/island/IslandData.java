package me.illusion.skyblockcore.island;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.illusion.skyblockcore.island.generator.OreGenerator;
import me.illusion.utilities.StringUtil;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class IslandData implements Serializable {

    private final UUID id;
    private final File islandSchematic;
    private transient final List<UUID> users = new ArrayList<>();
    private final UUID owner;
    private final List<OreGenerator> oreGenerators;

    @Setter
    private transient Island island;
    private String serialized;


    /**
     * Registers an OreGenerator
     *
     * @param generator - The ore generator
     */
    public void addGenerator(OreGenerator generator) {
        oreGenerators.add(generator);
    }

    /**
     * Adds a user to the island
     *
     * @param uuid - The user's UUID
     */
    public void addUser(UUID uuid) {
        users.add(uuid);
        serialized = serialized + " " + uuid.toString();
    }

    /**
     * Gets its users, as a UUID list
     * Ensures a singleton-style
     *
     * @return the list of UUIDs
     */
    public List<UUID> getUsers() {
        if (!users.isEmpty())
            return users;

        List<UUID> list = new ArrayList<>();

        String[] split = StringUtil.split(serialized, ' ');

        for (String str : split)
            list.add(UUID.fromString(str));

        users.addAll(list);
        return list;
    }
}
