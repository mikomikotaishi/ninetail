package bot.ninetail.core;

import java.sql.SQLException;

import javax.sql.DataSource;

import jakarta.annotation.Nonnull;

import bot.ninetail.system.BannedUsersManager;
import bot.ninetail.system.BotDatabaseManager;
import bot.ninetail.system.DisabledGuildsManager;
import bot.ninetail.util.database.DatabaseManager;

import lombok.experimental.UtilityClass;

import com.zaxxer.hikari.pool.HikariPool.PoolInitializationException;

/**
 * Class to handle operations with databases.
 * This class is used to handle operations with databases used by the bot.
 */
@UtilityClass
public final class DatabaseHandler {
    @Nonnull
    private static final System.Logger LOGGER = System.getLogger(DatabaseHandler.class.getName());

    /**
     * Loads the database for all resources used by the bot.
     */
    public static void loadDatabase() {
        try {
            LOGGER.log(System.Logger.Level.INFO, "Initializing database connection...");
            DataSource dataSource = DatabaseManager.loadDatabase();
            BotDatabaseManager.initDatabase(dataSource);
            BotDatabaseManager.init(dataSource);
            LOGGER.log(System.Logger.Level.INFO, "Database connection successful.");
        } catch (SQLException e) {
            LOGGER.log(System.Logger.Level.ERROR, "Failed to initialize database due to SQL exception: {0}", e.getMessage());
            LOGGER.log(System.Logger.Level.WARNING, "Social commands that require the database will be unavailable!");
        } catch (PoolInitializationException e) {
            LOGGER.log(System.Logger.Level.ERROR, "Failed to initialize database due to database handling exception: {0}", e.getMessage());
            LOGGER.log(System.Logger.Level.WARNING, "Social commands that require the database will be unavailable!");
        } catch (Exception e) {
            LOGGER.log(System.Logger.Level.ERROR, "Failed to initialize database: {0}", e.getMessage());
            LOGGER.log(System.Logger.Level.WARNING, "Social commands that require the database will be unavailable!");
        }

        // Run whether or not the database came up: both handle its absence, and banned users are
        // also sourced from config.
        BannedUsersManager.loadBannedUsers();
        DisabledGuildsManager.loadDisabledGuilds();
    }
}
