package gr1mly4memes.papercut.commands.subcommands;

import gr1mly4memes.papercut.PapercutConfig;
import gr1mly4memes.papercut.commands.PapercutSubcommand;
import net.minecraft.server.MinecraftServer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.File;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;

public class ReloadCommand implements PapercutSubcommand {
    @Override
    public boolean execute(CommandSender sender, String subCommand, String[] args) {
        MinecraftServer server = MinecraftServer.getServer();
        PapercutConfig.init((File) server.options.valueOf("papercut-settings"));
        Command.broadcastCommandMessage(sender, text("Papercut config reload complete.", GREEN));
        return false;
    }
}
