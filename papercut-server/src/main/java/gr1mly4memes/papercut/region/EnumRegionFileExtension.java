package gr1mly4memes.papercut.region;

import gr1mly4memes.papercut.PapercutConfig;
import net.minecraft.world.level.chunk.storage.RegionFile;
import org.jetbrains.annotations.Nullable;

public enum EnumRegionFileExtension {
    MCA("mca", "mca" , (info) -> new RegionFile(info.info(), info.filePath(), info.folder(), info.sync())),
    LINEAR("linear_v2", "linear" ,(info) -> new PapercutRegionFile(info.info(), info.filePath(), info.folder(), info.sync(), PapercutConfig.linearCompressionLevel));

    private final String name;
    private final String argument;
    private final IRegionCreateFunction creator;

    EnumRegionFileExtension(String name, String argument, IRegionCreateFunction creator) {
        this.name = name;
        this.argument = argument;
        this.creator = creator;
    }

    @Nullable
    public static EnumRegionFileExtension fromString(String string) {
        for (EnumRegionFileExtension format : values()) {
            if (format.name.equalsIgnoreCase(string)) {
                return format;
            }
        }

        return null;
    }

    public IRegionCreateFunction getCreator() {
        return this.creator;
    }

    public String getArgument() {
        return this.argument;
    }
}