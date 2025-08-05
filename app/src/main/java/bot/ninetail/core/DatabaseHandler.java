package bot.ninetail.core;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.sql.SQLException;

import javax.sql.DataSource;

import jakarta.annotation.Nonnull;

import bot.ninetail.system.BotDatabaseManager;
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
    private static final Logger LOGGER = System.getLogger(DatabaseHandler.class.getName());

    /**
     * Loads the database for all resources used by the bot.
     */
    public static void loadDatabase() {
        try {
            LOGGER.log(Level.INFO, "Initialising database connection...");
            DataSource dataSource = DatabaseManager.loadDatabase();
            BotDatabaseManager.initDatabase(dataSource);
            BotDatabaseManager.init(dataSource);
            LOGGER.log(Level.INFO, "Database connection successful.");
        } catch (SQLException e) {
            LOGGER.log(Level.ERROR, "Failed to initialise database due to SQL exception: {0}", e.getMessage());
            LOGGER.log(Level.WARNING, "Social commands that require the database will be unavailable!");
        } catch (PoolInitializationException e) {
            LOGGER.log(Level.ERROR, "Failed to initialise database due to database handling exception: {0}", e.getMessage());
            LOGGER.log(Level.WARNING, "Social commands that require the database will be unavailable!");
        } catch (Exception e) {
            LOGGER.log(Level.ERROR, "Failed to initialise database: {0}", e.getMessage());
            LOGGER.log(Level.WARNING, "Social commands that require the database will be unavailable!");
        }
    }
}
