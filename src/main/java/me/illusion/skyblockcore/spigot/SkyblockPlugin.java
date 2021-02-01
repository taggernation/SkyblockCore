package me.illusion.skyblockcore.spigot;

import lombok.Getter;
import lombok.SneakyThrows;
import me.illusion.skyblockcore.shared.sql.SQLUtil;
import me.illusion.skyblockcore.spigot.command.CommandManager;
import me.illusion.skyblockcore.spigot.command.island.IslandCommand;
import me.illusion.skyblockcore.spigot.data.PlayerManager;
import me.illusion.skyblockcore.spigot.file.IslandConfig;
import me.illusion.skyblockcore.spigot.hook.VaultHook;
import me.illusion.skyblockcore.spigot.island.IslandManager;
import me.illusion.skyblockcore.spigot.island.world.EmptyWorldGenerator;
import me.illusion.skyblockcore.spigot.listener.JoinListener;
import me.illusion.skyblockcore.spigot.listener.LeaveListener;
import me.illusion.skyblockcore.spigot.pasting.PastingHandler;
import me.illusion.skyblockcore.spigot.pasting.PastingType;
import me.illusion.skyblockcore.spigot.sql.StorageType;
import me.illusion.skyblockcore.spigot.utilities.storage.MessagesFile;
import me.illusion.skyblockcore.spigot.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

@Getter
public class SkyblockPlugin extends JavaPlugin {

    private Connection mySQLConnection;
    private IslandConfig islandConfig;
    private IslandManager islandManager;
    private PlayerManager playerManager;
    private CommandManager commandManager;
    private WorldManager worldManager;
    private PastingHandler pastingHandler;
    private EmptyWorldGenerator emptyWorldGenerator;

    private MessagesFile messages;
    private File[] startSchematic;

    @Override
    public void onEnable() {
        setupSQL().whenComplete((val, throwable) -> {

            System.out.println("SQL has been set up");

            if (!val)
                return;

            try {
                sync(this::load);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    private void load() {
        System.out.println("Registering configuration files");

        messages = new MessagesFile(this);
        islandConfig = new IslandConfig(this);
        islandManager = new IslandManager(this);
        commandManager = new CommandManager(this);
        playerManager = new PlayerManager();
        emptyWorldGenerator = new EmptyWorldGenerator();

        System.out.println("Setting up pasting handler");
        pastingHandler = PastingType.enable(this, islandConfig.getPastingSelection());

        System.out.println("Registering start files");
        startSchematic = startFiles();

        System.out.println("Creating worlds");
        worldManager = new WorldManager(this);

        System.out.println("Registering listeners");
        Bukkit.getPluginManager().registerEvents(new JoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new LeaveListener(this), this);

        System.out.println("Registering default commands.");
        registerDefaultCommands();

        System.out.println("Registering possible hooks");
        if (Bukkit.getPluginManager().isPluginEnabled("Vault"))
            new VaultHook(this);
    }
    /**
     * Generates the empty island worlds
     */
    public void setupWorld(String name) {
        new WorldCreator(name)
                .generator(emptyWorldGenerator)
                .generateStructures(false)
                .seed(0)
                .createWorld();
    }

    /**
     * Opens the SQL connection async
     */
    private CompletableFuture<Boolean> setupSQL() {
        return CompletableFuture.supplyAsync(() -> {
            saveDefaultConfig();

            StorageType type = StorageType.valueOf(getConfig().getString("database.type").toUpperCase(Locale.ROOT));

            String host = getConfig().getString("database.host", "");
            String database = getConfig().getString("database.database", "");
            String username = getConfig().getString("database.username", "");
            String password = getConfig().getString("database.password", "");
            int port = getConfig().getInt("database.port");

            SQLUtil sql;

            if (type == StorageType.MYSQL)
                sql = new SQLUtil(host, database, username, password, port);
            else
                sql = new SQLUtil(createSQLiteFile());

            if (!sql.openConnection()) {
                getLogger().warning("Could not load SQL Database.");
                getLogger().warning("This plugin requires a valid SQL database to work.");
                setEnabled(false);
                return false;
            }

            sql.createTable();
            mySQLConnection = sql.getConnection();
            return true;
        });

    }

    @SneakyThrows
    private File createSQLiteFile() {
        File file = new File(getDataFolder(), "storage.db");

        if (!file.exists())
            file.createNewFile();

        return file;
    }

    private void registerDefaultCommands() {
        commandManager.register(new IslandCommand(this));
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers())
            playerManager.get(player).save();
    }

    private File[] startFiles() {
        File startSchematicFolder = new File(getDataFolder() + File.separator + "start-schematic");

        if (!startSchematicFolder.exists())
            startSchematicFolder.mkdir();

        File[] files = startSchematicFolder.listFiles();

        if (files == null || files.length == 0) {
            if (pastingHandler.getType() == PastingType.FAWE)
                saveResource("start-schematic" + File.separator + "skyblock-schematic.schematic", false);
            else
                saveResource("start-schematic" + File.separator + "skyblock-mca.mca", false);
        }

        return startSchematicFolder.listFiles();
    }

    private void sync(Runnable runnable) {
        Bukkit.getScheduler().runTask(this, runnable);
    }
}