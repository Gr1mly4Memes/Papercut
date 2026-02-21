package gr1mly4memes.papercut;

import com.destroystokyo.paper.util.SneakyThrow;
import gr1mly4memes.papercut.commands.GlobalConfigManager;
import gr1mly4memes.papercut.commands.PapercutCommand;
import gr1mly4memes.papercut.config.ConfigVerify;
import gr1mly4memes.papercut.config.GlobalConfig;
import gr1mly4memes.papercut.region.EnumRegionFileExtension;
import gr1mly4memes.papercut.region.PapercutRegionFile;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

public final class PapercutConfig {
    public static final List<String> CONFIG_HEADER = List.of(
            "This is the main configuration file for Papercut.",
            "",
            "Created by Gr1mly4Memes"
    );
    public static final int CURRENT_CONFIG_VERSION = 6;

    private static File configFile;
    public static YamlConfiguration config;
    private static int configVersion;
    public static boolean createWorldSections = true;

    public static void init(final File file) {
        PapercutConfig.configFile = file;
        config = new YamlConfiguration();
        config.options().setHeader(CONFIG_HEADER);
        config.options().copyDefaults(true);

        if (!file.exists()) {
            try {
                boolean is = file.createNewFile();
                if (!is) {
                    throw new IOException("Can't create file");
                }
            } catch (final Exception ex) {
                Bukkit.getLogger().log(Level.SEVERE, "Failure to create papercut config", ex);
            }
        } else {
            try {
                config.load(file);
            } catch (final Exception ex) {
                Bukkit.getLogger().log(Level.SEVERE, "Failure to load papercut config", ex);
                SneakyThrow.sneaky(ex);
                throw new RuntimeException(ex);
            }
        }

        PapercutConfig.configVersion = PapercutConfig.config.getInt("config-version", CURRENT_CONFIG_VERSION);
        PapercutConfig.config.set("config-version", CURRENT_CONFIG_VERSION);

        GlobalConfigManager.init();

        registerCommand("papercut", new PapercutCommand("papercut"));
    }

    public static void save() {
        try {
            config.save(PapercutConfig.configFile);
        } catch (final Exception ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Unable to save papercut config", ex);
        }
    }

    public static void registerCommand(String name, Command command) {
        MinecraftServer.getServer().server.getCommandMap().register(name, "papercut", command);
        MinecraftServer.getServer().server.syncCommands();
    }

    public static void unregisterCommand(String name) {
        name = name.toLowerCase(Locale.ENGLISH).trim();
        MinecraftServer.getServer().server.getCommandMap().getKnownCommands().remove(name);
        MinecraftServer.getServer().server.getCommandMap().getKnownCommands().remove("papercut:" + name);
        MinecraftServer.getServer().server.syncCommands();
    }

    // Papercut start - region
    @GlobalConfig(name = "format", category = "region", lock = true, verify = RegionFormatVerify.class)
    public static EnumRegionFileExtension regionFormat = EnumRegionFileExtension.MCA;

    private static class RegionFormatVerify extends ConfigVerify.EnumConfigVerify<EnumRegionFileExtension> {
        @Override
        public String check(EnumRegionFileExtension old, EnumRegionFileExtension value) throws IllegalArgumentException {
            if (value == null) {
                throw new RuntimeException("Invalid region format: " + regionFormat);
            }
            if (regionFormat == EnumRegionFileExtension.LINEAR) {
                PapercutRegionFile.SAVE_DELAY_MS = linearIoFlushDelayMs;
                PapercutRegionFile.SAVE_THREAD_MAX_COUNT = linearIoThreadCount;
                PapercutRegionFile.USE_VIRTUAL_THREAD = linearUseVirtualThread;
            }
            return null;
        }
    }

    @GlobalConfig(name = "compression-level", category = {"region", "linear"}, lock = true, verify = LinearCompressVerify.class)
    public static int linearCompressionLevel = 1;

    private static class LinearCompressVerify extends ConfigVerify.IntConfigVerify {
        @Override
        public String check(Integer old, Integer value) throws IllegalArgumentException {
            if (value < 1 || value > 23) {
                MinecraftServer.LOGGER.error("Linear region compression level should be between 1 and 22 in config: {}", linearCompressionLevel);
                MinecraftServer.LOGGER.error("Falling back to compression level 1.");
                linearCompressionLevel = 1;
            }
            return null;
        }
    }

    @GlobalConfig(name = "io-thread-count", category = {"region", "linear"}, lock = true, verify = ConfigVerify.IntConfigVerify.class)
    public static int linearIoThreadCount = 6;

    @GlobalConfig(name = "io-flush-delay-ms", category = {"region", "linear"}, lock = true, verify = ConfigVerify.IntConfigVerify.class)
    public static int linearIoFlushDelayMs = 100;

    @GlobalConfig(name = "use-virtual-thread", category = {"region", "linear"})
    public static boolean linearUseVirtualThread = true;

    @GlobalConfig(name = "flush-max-threads", category = {"region", "linear"}, lock = true, verify = ConfigVerify.IntConfigVerify.class)
    public static int linearFlushThreads = 1;

    public static int getLinearFlushThreads() {
        if (linearFlushThreads < 0) {
            return Math.max(Runtime.getRuntime().availableProcessors() + linearFlushThreads, 1);
        } else {
            return Math.max(linearFlushThreads, 1);
        }
    }
    // Papercut end - region

    // Papercut start - compatibility
    @GlobalConfig(name = "legacy-scheduler", category = "compatibility", lock = true)
    public static boolean legacyScheduler = true;
}
