package gr1mly4memes.papercut;

import org.bukkit.Bukkit;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PapercutLogger extends Logger {
    public static final PapercutLogger LOGGER = new PapercutLogger();

    private PapercutLogger() {
        super("Papercut", null);
        setParent(Bukkit.getLogger());
        setLevel(Level.ALL);
    }

    public void severe(String msg, Exception exception) {
        this.log(Level.SEVERE, msg, exception);
    }

    public void warning(String msg, Exception exception) {
        this.log(Level.WARNING, msg, exception);
    }

}