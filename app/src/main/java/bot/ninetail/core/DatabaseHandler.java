package bot.ninetail.core;

import java.sql.SQLException;

import javax.sql.DataSource;

import bot.ninetail.social.CoinsRegistry;
import bot.ninetail.structures.InteractionHandler;
import bot.ninetail.utilities.database.DatabaseManager;

/**
 * Class to handle operations with databases.
 * This class is used to handle operations with databases used by the bot.
 * 
 * @extends InteractionHandler
 */
public final class DatabaseHandler extends InteractionHandler {
    /**
     * Private constructor to prevent instantiation.
     */
    private DatabaseHandler() {}

    /**
     * Loads the database for all resources used by the bot.
     */
    public static void loadDatabase() {
        try {
            Logger.log(LogLevel.INFO, "Initialising database connection...");
            DataSource dataSource = DatabaseManager.loadCoinsRegistry();
            CoinsRegistry.initDatabase(dataSource);
            CoinsRegistry.init(dataSource);
            Logger.log(LogLevel.INFO, "Database connection successful");
        } catch (SQLException e) {
            Logger.log(LogLevel.ERROR, "Failed to initialise database: " + e.getMessage());
            Logger.log(LogLevel.WARN, "Social commands that require the database will be unavailable!");
        }
    }
}
